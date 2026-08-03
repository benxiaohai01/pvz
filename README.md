# 植物大战僵尸（JavaFX 25 学习版）

一个使用 **Java 25 + JavaFX 25 + Maven** 从零实现的《植物大战僵尸》风格 2D 游戏。
项目目标是学习高级面向对象设计、游戏架构、设计模式在真实工程中的应用，以及 JavaFX 游戏开发。

## 技术栈

- Java 25（Record / Sealed Class / Pattern Matching）
- JavaFX 25（AnimationTimer 游戏循环、Canvas 渲染）
- Maven 构建
- JUnit 5 单元测试（领域模型与服务层）

## 运行

需要 JDK 25：

```bash
mvn javafx:run
```

运行测试：

```bash
mvn test
```

> 提示：编译阶段会把 JavaFX 依赖放到 module path（项目包含 `module-info.java`），
> 因此可以正常使用跨包的 Sealed 继承体系。

## 操作说明

1. 主菜单 → 开始游戏
2. 选择关卡（1-1 / 1-2 / 1-3）
3. 选择植物（点击卡片加入选择栏，最多 5 种）
4. 游戏中：
   - 点击顶部植物卡片，再点击草坪格子种植
   - 点击黄色阳光收集
   - 点击右上角「铲子」进入铲除模式，再点击植物移除
5. 胜利：所有波次完成且僵尸全部被消灭
6. 失败：僵尸突破防线（僵尸吃掉了你的脑子）

## 架构

采用 **MVC + Game Loop + OOP 领域模型 + 设计模式 + 数据驱动配置**：

```
launcher       启动入口（无游戏逻辑）
core           游戏循环、状态机、引擎装配
model          纯领域模型：世界 / 植物 / 僵尸 / 子弹 / 阳光 / 小推车 / 关卡（不依赖 JavaFX）
controller     输入控制：菜单、关卡、植物选择、游戏、鼠标
view           JavaFX 界面
renderer       只读渲染（未来可无缝替换为图片资源）
service        战斗、碰撞、生成、关卡
factory        植物 / 僵尸工厂
strategy       攻击 / 移动 / 索敌策略
command        种植 / 铲除命令（可撤销）
event          事件总线与游戏事件
config         数据驱动配置（GameConfig / PlantCatalog / ZombieCatalog / LevelCatalog）
util           向量值对象
```

## 设计模式（自然使用）

| 模式 | 位置 | 解决的实际问题 |
| --- | --- | --- |
| Factory | `PlantFactory` / `ZombieFactory` | 按类型创建对象并装配策略，避免控制器里写 `new` |
| Strategy | `AttackStrategy` / `MoveStrategy` / `TargetStrategy` | 植物攻击、僵尸移动、索敌行为可插拔 |
| State | `GameState` + `GameStateManager` | 游戏流程（菜单→选关→选植物→游戏中→胜/负）合法迁移 |
| Observer | `EventBus` + 事件 Record | 僵尸死亡、游戏结束、阳光收集等解耦通知（UI / 计数 / 音效） |
| Command | `PlantCommand` / `RemovePlantCommand` | 玩家操作封装为对象，支持校验与撤销 |

## Java 25 特性

- **Record**：`Vector2`、`LevelConfig`、`ZombieWave`、`PlantConfig`、事件等值对象
- **Sealed Class**：`GameObject`（Plant / Zombie / Projectile / Sun / LawnCar）、`GameEvent`、`Projectile`
- **Pattern Matching**：事件总线用 `switch` 类型模式分发，渲染器按类型绘制

## 数据驱动配置

植物、僵尸、关卡全部是数据而非硬编码逻辑：

- `PlantCatalog`：新增植物 = 添加一条 `PlantConfig`
- `ZombieCatalog`：新增僵尸 = 添加一条 `ZombieConfig`
- `LevelCatalog`：新增关卡 = 添加一条 `LevelConfig`（初始阳光、可用植物、波次）

## 扩展指南

- **新植物**：`PlantType` 加枚举 → `PlantCatalog` 加配置 → `PlantFactory` 加创建分支 → 可选 `Renderer` 调整造型
- **新僵尸**：`ZombieType` 加枚举 → `ZombieCatalog` 加配置 → `ZombieFactory` 加分支
- **新关卡**：`LevelCatalog` 加一条记录
- **图片资源**：只改 `renderer` 包，领域模型不动
- **音效 / 技能 / Buff**：订阅 `EventBus` 或在模型中新增行为即可，不影响现有结构

## 测试

`src/test/java` 覆盖纯模型与服务的核心逻辑：格子占用、种植/铲除命令与撤销、战斗伤害与死亡事件、
波次推进、僵尸生成、碰撞（豌豆命中 / 啃植物 / 防线失守）。
