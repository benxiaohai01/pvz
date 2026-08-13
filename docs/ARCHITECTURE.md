# 架构说明

本文档描述项目的分层、依赖方向、核心对象、事件流、状态机与扩展点，
是阅读代码和继续开发的第一份地图。

## 1. 设计目标

- 领域逻辑不依赖 JavaFX，任何规则都可以脱离 UI 测试；
- 视图只读世界状态并回应用户输入，不写游戏规则；
- 控制器只做编排，不做数值判断；
- 植物、僵尸、关卡数值进入 JSON，行为通过策略注入；
- 对象创建收敛到工厂，操作封装为命令，事件通过总线解耦。

## 2. 分层与职责

| 层 | 包 | 职责 | 依赖 |
| --- | --- | --- | --- |
| 启动 | `launcher` | JavaFX Application 入口 | 无业务依赖 |
| 核心 | `core` | 游戏循环、组合根 | controller / service / model / view |
| 状态 | `state` | 顶层流程状态机 | 无 |
| 控制 | `controller` | 输入编排、命令执行、撤销历史 | service / model / command |
| 服务 | `service` | 战斗、碰撞、生成、关卡查询 | model / event / factory |
| 领域 | `model` | 世界、实体、关卡、网格、规则 | config / strategy |
| 策略 | `strategy` | 攻击、产阳光、移动、索敌 | model |
| 命令 | `command` | 可执行/可撤销的玩家操作 | model / factory / event |
| 事件 | `event` | EventBus 与事件 Record | model |
| 配置 | `config` | 常量、Catalog、JSON 加载 | model（枚举键） |
| 渲染 | `renderer` | 只读 Canvas 绘制 | model / config |
| 视图 | `view` | JavaFX 控件与画布绑定 | controller / renderer / event |
| 工具 | `util` | 向量等值对象 | 无 |

## 3. 依赖方向

```
view / renderer
      |
      v
controller
      |
      v
service ----------> factory / command
      |
      v
   model
      ^
      |
strategy
```

关键约束：

- `model` 包内禁止出现 `javafx.*` import；
- `view` 包内禁止修改世界状态（收集阳光、种植等必须通过 `GameController`）；
- `controller` 内不写数值规则，规则查询 `GameWorld` / `Level` / 服务；
- 新增行为优先放在 `strategy`，新增操作优先放在 `command`，新增通知优先放在 `event`。

## 4. 组合根

`GameEngine` 是唯一的装配点：

- 创建 `EventBus`、`LevelService`、`PlantFactory`、`ZombieFactory`、战斗/碰撞/生成服务；
- 创建菜单、选关、胜负视图，并注册状态机与事件订阅；
- 开始游戏时创建 `GameWorld`、`GameController`、`MouseController`、`GameView`；
- 游戏结束或回到主菜单时统一 `disposeGameSession()` 释放控制器与视图的订阅。

业务类通过构造器接收依赖，不使用 Service Locator 或全局单例做装配。

## 5. 领域模型

### 实体层级

`GameObject` 是 sealed 基类，只允许四类实体与两类环境对象：

```
GameObject
├── Plant（配置驱动，攻击 + 产阳光策略）
├── Zombie（配置驱动，移动策略 + 配置能力）
├── Projectile（sealed）
│   └── PeaProjectile
├── Sun
└── LawnCar
```

`Plant.update` 统一调用 `AttackStrategy` 与 `SunProductionStrategy`；
`Zombie.update` 调用 `MoveStrategy`。实体身份、数值与行为键全部来自 JSON，
`BehaviorCatalog` 负责把 `attackBehavior` / `sunBehavior` / `moveBehavior`
映射为具体策略，因此新增数值变体不用再动工厂。

### 世界与网格

- `GameWorld` 持有草坪、关卡、僵尸、子弹、阳光、小推车与阳光数值；
- 实体列表对外返回 `Collections.unmodifiableList`，写入必须走
  `addZombie` / `addProjectile` / `addSun` / `collectSun` / `cleanup`；
- `Grid` 负责占位、查询、清理死亡植物；
- 胜负规则沉淀在 `GameWorld.isWinConditionMet()` 与 `markOver()`，
  `GameController` 只读取结果并发布事件。

### 关卡与波次

`Level` 是关卡的运行时状态机：

- `elapsed` 推进关卡时间；
- `activeWave()` / `isWaveActive()` 判断当前波是否到时间；
- `announceWave()` 标记波次公告；
- `currentSpawn()` 返回当前生成条目，`consumeSpawn()` 推进条目进度；
- `completeWave()` 在条目耗尽后切换到下一波。

生成状态（已公告、生成计时、条目索引、条目内数量）全部归属 `Level`，
`SpawnService` 只负责“读取状态 → 创建僵尸 → 推进状态”，不会自己保存波次进度。

## 6. 事件流

`EventBus` 是进程内同步观察者，事件全部是 Record：

- `ZombieDeathEvent`：击杀计数、渲染反馈；
- `PlantRemovedEvent`：铲除/死亡反馈；
- `SunCollectedEvent`：阳光 UI 更新；
- `WaveSpawnEvent`：波次公告 UI；
- `GameOverEvent`：引擎切换胜负状态。

示例：僵尸死亡

```
CollisionService/CombatService
  -> combat.kill(zombie, LAWN_CAR)
  -> eventBus.publish(new ZombieDeathEvent(...))
  -> GameController 击杀数 +1
  -> GameView 更新击杀标签
```

订阅者在构造时通过 `eventBus.subscribe(...)` 注册，在 `dispose()` 中退订，
避免跨会话的事件泄漏。

## 7. 状态机

`state` 包中的 `GameStateManager` 约束合法迁移：

```
MENU -> LEVEL_SELECT -> PLANT_SELECT -> PLAYING -> WIN / LOSE
  ^                                      |          |
  +--------------------------------------+----------+
```

非法迁移直接抛异常，例如从 `MENU` 跳 `PLAYING` 会被拒绝。

## 8. 数据驱动

`src/main/resources/config/` 下的 JSON 是唯一数值来源：

- `plants.json` -> `PlantCatalog`
- `zombies.json` -> `ZombieCatalog`
- `levels.json` -> `LevelCatalog`

`ConfigLoader` 使用 Jackson 反序列化并校验必填字段，Catalog 实例在组合根创建
并建立不可变索引。业务代码只通过注入的 `PlantCatalog.of(type)` 这类 API 查询。

配置与代码的边界：

- 数值、颜色、波次组合属于配置；
- 显示名（`displayName`）属于配置，枚举中的 JavaDoc 只是开发注释；
- “如何生成阳光”“如何索敌”“如何移动”属于策略代码；
- “什么时候算赢”“格子能否种植”属于领域规则。

## 9. 规则归属约定

| 规则 | 归属 |
| --- | --- |
| 阳光扣减、种植占位、清理 | `GameWorld` / `Grid` |
| 波次激活、生成进度 | `Level` |
| 胜负判定 | `GameWorld`（胜利）、`CollisionService` 防线失守 |
| 伤害、死亡、事件发布 | `CombatService` |
| 命中、啃咬、小推车 | `CollisionService` |
| 命令校验与撤销 | `PlantCommand` / `RemovePlantCommand` |
| 植物/僵尸行为 | `strategy` |
| 渲染 | `renderer` |

## 10. 生命周期管理

- 游戏会话 = `GameWorld + GameController + GameView + MouseController`；
- `startGame()` 创建会话，进入 `WIN` / `LOSE` / `MENU` 时销毁会话；
- `GameController.dispose()` 退订事件；
- `GameView.dispose()` 退订事件；
- `GameLoop` 只在 `PLAYING` 状态运行，进入胜负状态立即停止。

## 11. 扩展点

- 新植物：`PlantType` + `plants.json` + `PlantFactory` + 可选新策略 + 渲染；
- 新僵尸：`ZombieType` + `zombies.json` + `ZombieFactory` + 可选新实体 + 渲染；
- 新关卡：`levels.json` 添加条目，无需改代码；
- 新行为：实现 `AttackStrategy` / `SunProductionStrategy` / `MoveStrategy` / `TargetStrategy`；
- 新操作：实现 `GameCommand`，控制器历史栈自动支持撤销；
- 新通知：新增 `GameEvent` Record，订阅者按类型模式匹配处理。

具体步骤见 [EXTENSION_GUIDE.md](EXTENSION_GUIDE.md)。
