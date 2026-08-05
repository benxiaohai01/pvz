# 扩展指南

本文档手把手说明如何在这个架构上添加新内容。
总原则：**改数据优先，改策略其次，最后才改渲染；领域模型不依赖 UI。**

## 0. 改前准备与回归

每次改动完成后运行：

```bash
mvn test
```

涉及界面时再运行：

```bash
mvn javafx:run
```

## 1. 新增一种植物

以“寒冰射手”为例。它和豌豆射手一样发射弹道，但希望子弹附带减速。

### 1.1 添加枚举

`src/main/java/com/pvz/model/entity/plant/PlantType.java`：

```java
public enum PlantType {
    SUNFLOWER,
    PEASHOOTER,
    WALLNUT,
    ICESHOOTER
}
```

### 1.2 添加配置

`src/main/resources/config/plants.json` 追加一个对象：

```json
{
  "type": "ICESHOOTER",
  "displayName": "寒冰射手",
  "cost": 175,
  "cooldown": 7.5,
  "maxHp": 100,
  "attackInterval": 1.8,
  "damage": 20,
  "projectileSpeed": 260,
  "sunInterval": 0,
  "sunAmount": 0,
  "color": "#4FC3F7"
}
```

`PlantConfig` 会自动反序列化并校验，无需改模型代码。

### 1.3 在工厂中装配

`src/main/java/com/pvz/factory/PlantFactory.java`：

```java
case ICESHOOTER -> new IceShooter(
        config, row, col,
        new PeaAttackStrategy(new SameRowTargetStrategy(), config),
        new NoSunProductionStrategy());
```

如果寒冰射手只是豌豆射手的数值变体，也可以直接复用 `PeaShooter`。
需要减速效果时才新增行为。

### 1.4 新增行为策略（可选）

减速一般由子弹命中触发，因此可以：

1. 新增 `IceProjectile extends Projectile`（记得更新 `Projectile` 的 `permits`）；
2. 新增 `FreezingPeaAttackStrategy` 或给 `PeaAttackStrategy` 增加弹道类型参数；
3. 新增 `SlowEffectStrategy` 或让 `Zombie` 增加可选的减速状态字段。

原则：行为变化放在 `strategy` 包，不要在 `Plant` 子类里手写 if/else。

### 1.5 更新渲染

`src/main/java/com/pvz/renderer/PlantRenderer.java` 的 switch 增加分支，
或直接依赖 `PlantConfig.color()` 走默认方块。想替换成图片见第 5 节。

### 1.6 在关卡中开放

在 `levels.json` 的 `availablePlants` 数组中加入 `"ICESHOOTER"`。

### 1.7 测试

参考 `src/test/java/com/pvz/strategy/PlantStrategyTest.java`，
新增“种植后按间隔发射”“不攻击时待发”等断言。

## 2. 新增一种僵尸

以“路障僵尸”为例，本项目已经用它演示了完整链路：

- `ZombieType.CONEHEAD`（枚举键）
- `zombies.json` 中 `CONEHEAD` 配置（更高生命、稍慢速度）
- `ConeheadZombie`（只保留身份的空实体）
- `ZombieFactory` 的 `case CONEHEAD` 分支

### 2.1 添加枚举

`src/main/java/com/pvz/model/entity/zombie/ZombieType.java`：

```java
public enum ZombieType {
    BASIC,
    CONEHEAD
}
```

### 2.2 添加配置

`src/main/resources/config/zombies.json`：

```json
{
  "type": "CONEHEAD",
  "displayName": "路障僵尸",
  "maxHp": 200,
  "speed": 18,
  "damage": 12,
  "biteInterval": 1,
  "color": "#D2A06D"
}
```

### 2.3 工厂分支与实体

`ZombieFactory` 中增加：

```java
case CONEHEAD -> new ConeheadZombie(config, row, moveStrategy);
```

实体类只做身份标识：

```java
public final class ConeheadZombie extends Zombie {
    public ConeheadZombie(ZombieConfig config, int row, MoveStrategy moveStrategy) {
        super(config, row, moveStrategy);
    }
}
```

移动策略由工厂统一注入，僵尸子类不需要自己 `new MoveLeftStrategy()`。

### 2.4 更新渲染

`ZombieRenderer` 使用 `zombie.config().color()` 作为主色，路障僵尸会自动变色。
想画“路障帽子”这类独特造型时，可以按类型判断或使用图片资源。

### 2.5 在关卡中使用

`levels.json` 的波次 `spawns` 数组可以混编多种僵尸：

```json
{
  "startTime": 25,
  "spawns": [
    { "type": "BASIC", "count": 4, "spawnInterval": 3 },
    { "type": "CONEHEAD", "count": 3, "spawnInterval": 3 }
  ]
}
```

生成服务会按条目顺序生成，第一条目第一只立即生成，之后按 `spawnInterval` 间隔。

### 2.6 测试

参考 `src/test/java/com/pvz/factory/ZombieFactoryTest.java`
与 `src/test/java/com/pvz/service/SpawnServiceTest.java` 的混合波用例。

## 3. 新增关卡

关卡完全由数据驱动，不改 Java 代码即可新增。

`src/main/resources/config/levels.json` 追加：

```json
{
  "id": "1-4",
  "name": "关卡 1-4",
  "initialSun": 200,
  "availablePlants": ["SUNFLOWER", "PEASHOOTER", "WALLNUT"],
  "waves": [
    {
      "startTime": 10,
      "spawns": [
        { "type": "BASIC", "count": 6, "spawnInterval": 3 }
      ]
    },
    {
      "startTime": 35,
      "spawns": [
        { "type": "BASIC", "count": 6, "spawnInterval": 2.5 },
        { "type": "CONEHEAD", "count": 4, "spawnInterval": 2.5 }
      ]
    }
  ]
}
```

字段说明：

- `startTime`：该波次激活的关卡时间（秒），顺序波次要求上一波生成完才轮到下一波；
- `spawns`：该波次内的生成条目，可混编多种僵尸；
- `count`：该条目生成的僵尸总数；
- `spawnInterval`：条目内相邻两只僵尸的生成间隔。

`LevelCatalog.LEVELS` 自动加载全部关卡。如果代码里想用便捷常量，
可以仿照 `LEVEL_1_1` 添加 `public static final LevelConfig LEVEL_1_4 = find("1-4");`。

## 4. 替换为图片资源

当前渲染是 Canvas 程序化绘制（`PlantRenderer` / `ZombieRenderer` /
`AnimationRenderer`）。替换成图片时，**领域模型、服务、控制器都不需要改**，
只需要改 `renderer` 包。

### 4.1 建立资源目录

```
src/main/resources/assets/
├── plants/
│   ├── sunflower.png
│   ├── peashooter.png
│   └── wallnut.png
└── zombies/
    ├── basic.png
    └── conehead.png
```

### 4.2 在渲染器中加载图片

`PlantRenderer.java` 示例：

```java
import javafx.scene.image.Image;

public final class PlantRenderer {

    private static final Image SUNFLOWER_IMAGE = new Image(
            PlantRenderer.class.getResourceAsStream("/assets/plants/sunflower.png"));

    public void draw(GraphicsContext gc, Plant plant) {
        double width = 54;
        double height = 62;
        double x = plant.x() - width / 2;
        double y = plant.y() - height / 2;

        switch (plant) {
            case SunFlower ignored ->
                    gc.drawImage(SUNFLOWER_IMAGE, x, y, width, height);
            default -> {
                // 兜底绘制，未配置图片的植物仍然可见
            }
        }
    }
}
```

建议继续保留 `PlantConfig.color()` 作为兜底，避免漏配图片时白屏。
图片加载一次为 `static final`，不要每帧 `new Image(...)`。

### 4.3 替换僵尸

`ZombieRenderer` 同理，可按 `zombie.config().type()` 选择图片，
也可以直接维护一张 `EnumMap<ZombieType, Image>`。

### 4.4 动画与多帧

如果要做帧动画，可以引入 `SpriteCatalog`：按类型加载多帧 `Image`，
渲染时根据 `elapsed` 选取帧。渲染层内部演化，不改变模型接口。

## 5. 扩展行为 / 技能 / 新操作

- **新植物行为**：实现 `AttackStrategy` 或 `SunProductionStrategy`，在工厂装配；
- **新移动方式**：实现 `MoveStrategy`，注入 `ZombieFactory`；
- **新索敌方式**：实现 `TargetStrategy<Zombie>`，替换 `SameRowTargetStrategy`；
- **新玩家操作**：实现 `GameCommand`，`GameController` 的历史栈自动支持撤销；
- **新全局反馈**：新增 `GameEvent` Record，需要响应的订阅者注册到 `EventBus`。

保持依赖方向：策略可以依赖 `model`，但 `model` 不能反向依赖策略之外的 UI 层。

## 6. 改动检查清单

- [ ] 配置 JSON 字段完整、类型正确；
- [ ] 枚举键与 JSON `type` 一致；
- [ ] 工厂已为所有枚举分支返回对象（switch 无遗漏）；
- [ ] 新实体没有把行为写死在 `update` 里（优先策略）；
- [ ] 渲染器有兜底绘制，新资源缺图不会崩；
- [ ] `mvn test` 全绿；
- [ ] 必要时补充单元测试并同步更新本文档。
