# 植物大战僵尸（JavaFX 25 工程实践版）

一个使用 **Java 25 + JavaFX 25 + Maven** 从零实现的《植物大战僵尸》风格 2D 游戏。
项目定位是“学习型准商业工程”：它不是只演示语法的玩具，而是按照可维护、可测试、可扩展、
配置与代码分离的方式组织的一个真实可玩的游戏。

## 项目定位

本项目在编写过程中有意练习以下商业工程能力：

- **分层与依赖方向**：`view -> controller -> service/model`，领域模型完全不依赖 JavaFX；
- **组合根装配**：`GameEngine` 统一创建工厂、服务、控制器，业务类不自己 new 全局依赖；
- **数据驱动**：植物、僵尸、关卡数值全部来自 `src/main/resources/config/*.json`，改平衡不动代码；
- **行为可插拔**：植物攻击、产阳光、僵尸移动全部通过 Strategy 装配，新玩法优先新增策略；
- **可测试**：纯领域模型与服务层有 JUnit 5 覆盖，规则可以脱离 UI 验证；
- **生命周期可管理**：游戏会话进入胜负或主菜单时统一释放控制器与视图订阅，避免事件泄漏；
- **可撤销操作**：种植/铲除通过 Command 封装，游戏内提供“撤销”入口；
- **防御性建模**：值对象构造时校验、只读集合视图、Sealed 继承体系约束类型边界。

## 技术栈

- Java 25：Record / Sealed Class / Pattern Matching / Switch 表达式
- JavaFX 25：AnimationTimer 游戏循环、Canvas 渲染
- Maven 构建
- Jackson 2.13：JSON 配置加载
- JUnit 5：单元测试

## 快速开始

需要 JDK 25：

```bash
mvn javafx:run
```

运行测试：

```bash
mvn test
```

> 项目包含 `module-info.java`，JavaFX 依赖在编译期放入 module path，
> 因此可以正常使用跨包的 Sealed 继承体系。

## 操作说明

1. 主菜单 → 开始游戏；
2. 选择关卡（1-1 / 1-2 / 1-3）；
3. 选择植物（点击卡片加入选择栏，最多 5 种）；
4. 游戏中：
   - 点击顶部植物卡片，再点击草坪格子种植；
   - 点击黄色阳光收集；
   - 点击「铲子」进入铲除模式，再点击植物移除；
   - 点击「撤销」回退最近一次种植/铲除；
5. 胜利：所有波次完成且僵尸全部被消灭；
6. 失败：僵尸突破防线。

## 架构总览

```
launcher       启动入口（无游戏逻辑）
core           游戏循环、状态机、引擎装配（组合根）
controller     输入控制：菜单、选关、选植物、游戏、鼠标
view           JavaFX 界面
renderer       只读渲染，未来可替换为图片资源
service        战斗、碰撞、生成、关卡编排
factory        植物 / 僵尸工厂（策略装配）
strategy       攻击 / 产阳光 / 移动 / 索敌策略
command        种植 / 铲除命令（可撤销）
event          事件总线与游戏事件
model          纯领域模型：世界 / 实体 / 关卡（不依赖 JavaFX）
config         数据驱动配置（常量 / Catalog / JSON 加载）
util           向量值对象
```

依赖方向是自上而下单向的：`view` 依赖 `controller`，`controller` 依赖 `service` 与 `model`，
`model` 不依赖任何 UI 层。具体分层、依赖与事件流见 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)。

## 设计模式

| 模式 | 位置 | 解决的实际问题 |
| --- | --- | --- |
| Factory | `PlantFactory` / `ZombieFactory` | 按类型查配置，再由 `BehaviorCatalog` 装配行为策略 |
| Strategy | `AttackStrategy` / `SunProductionStrategy` / `MoveStrategy` / `TargetStrategy` | 攻击、产阳光、移动、索敌行为可插拔 |
| State | `GameState` + `GameStateManager` | 菜单→选关→选植物→游戏中→胜/负的合法迁移约束 |
| Observer | `EventBus` + 事件 Record | 僵尸死亡、阳光收集、波次公告等通知解耦 |
| Command | `PlantCommand` / `RemovePlantCommand` + 历史栈 | 玩家操作封装为对象，支持校验与撤销 |

## 数据驱动配置

所有静态数值放在 `src/main/resources/config/`：

- `plants.json`：植物显示名、价格、冷却、生命、攻击、产阳光、行为键、颜色；
- `zombies.json`：僵尸显示名、生命、速度、攻击、啃咬间隔、移动行为键、颜色；
- `levels.json`：关卡初始阳光、可用植物、波次与生成条目。

`PlantCatalog` / `ZombieCatalog` / `LevelCatalog` 在启动时加载并建立索引，
业务代码通过 Catalog 查询配置，不直接读 JSON。新增内容的具体步骤见
[docs/EXTENSION_GUIDE.md](docs/EXTENSION_GUIDE.md)。

## 领域设计原则

- **植物**：基类只负责位置、生命、价格，行为全部由策略驱动；向日葵“产阳光”不再是子类手写逻辑；
- **僵尸**：数据决定能力与移动行为键，路障僵尸只是新配置；
- **关卡**：波次是一组有序生成条目，支持混合僵尸波；生成进度状态归属 `Level`，服务层只读取和推进；
- **规则归属**：胜负、占位、阳光扣减、波次推进都在领域模型；`GameController` 只做编排，不做数值判断；
- **只读视图**：世界对象列表对外部只读，写入必须走 `addZombie` / `addSun` 等显式方法。

## 测试策略

`src/test/java` 覆盖纯模型、命令、服务与策略：

- 格子占位与清理；
- 种植/铲除命令执行与撤销；
- 战斗伤害与死亡事件；
- 豌豆命中、僵尸啃植物、防线失守；
- 波次激活、混合波、生成数量；
- 只读集合视图、胜负条件；
- 植物策略（产阳光、发射豌豆）与僵尸工厂注入。

## 文档索引

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)：架构总览、分层依赖、事件流、状态机、扩展点；
- [docs/EXTENSION_GUIDE.md](docs/EXTENSION_GUIDE.md)：新增植物、僵尸、关卡、替换图片资源的手把手步骤。

## 与商业工程的差距（路线图）

本项目已经具备基本的商业工程结构，但距离可上线的商业产品仍有明确差距，
这些内容可作为继续学习的路线图：

- **日志与可观测性**：接入 SLF4J + Logback，关键流程（开局、波次、胜负、异常）输出结构化日志；
- **配置热重载与校验**：启动时 schema 校验、缺失字段 fail-fast、支持环境化配置；
- **存档与设置**：进度、植物解锁、音量等持久化（JSON/Properties/SQLite）；
- **国际化**：界面文案抽离为资源包，目前中文文案直接写在代码中；
- **CI/CD**：GitHub Actions 或 Jenkins 流水线：`mvn verify` + 打包 + 发布产物；
- **打包分发**：jlink / jpackage 生成可分发安装包，配置启动脚本；
- **性能剖析**：对象池、渲染批处理、GC 调优，针对低端机器做 Profiling；
- **自动化 UI 测试**：TestFX 覆盖交互链路，当前测试聚焦领域层与服务层；
- **存档/回放/Mod**：扩展配置格式与热插拔策略，形成内容创作者生态。
