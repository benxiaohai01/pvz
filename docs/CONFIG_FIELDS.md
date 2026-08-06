# 配置文件字段说明

本文档逐字段说明 `src/main/resources/config/` 下三个 JSON 配置文件的含义：

- `plants.json`：植物静态配置；
- `zombies.json`：僵尸静态配置；
- `levels.json`：关卡与波次配置。

配置由 `ConfigLoader` 使用 Jackson 反序列化为 Record，加载时进行字段校验。
注意 JSON 本身不支持注释，本文档是配置字段的权威说明。

## 通用约定

- `type` 等枚举字段必须与 Java 枚举名完全一致，例如 `SUNFLOWER`、`BASIC`、`PEA`；
- 颜色字段统一使用 `"#RRGGBB"` 十六进制格式，例如 `"#FFD700"`；
- 时间字段单位是秒，速度字段单位是像素/秒；
- 图片素材路径由 `spriteKey` 等字段按约定推导，详见各字段说明；
- 未配置素材字段的植物或僵尸，渲染层会退回颜色方块，不影响游戏逻辑。

## plants.json

顶层是数组，每个元素代表一种植物，元素必须包含以下字段。

| 字段 | 类型 | 必填 | 说明 | 校验规则 / 示例 |
| --- | --- | --- | --- | --- |
| `type` | 枚举字符串 | 是 | 植物唯一键，必须与 `PlantType` 枚举一致；种植、渲染、关卡开放都以此字段为准 | `SUNFLOWER` / `PEASHOOTER` / `WALLNUT` |
| `displayName` | 字符串 | 是 | 植物中文显示名，界面卡片、选植物界面和击杀提示使用 | 非空，例如 `"向日葵"` |
| `cost` | 整数 | 是 | 种植一次消耗的阳光数量 | `>= 0`，例如 `50` |
| `cooldown` | 数字 | 是 | 种植成功后卡片的冷却时间，单位秒 | `>= 0`，例如 `7.5` |
| `maxHp` | 整数 | 是 | 植物生命值上限，生命归零后植物死亡 | `> 0`，例如 `100` |
| `attackInterval` | 数字 | 是 | 攻击行为的触发间隔，单位秒；`0` 表示不攻击 | `>= 0`，例如 `2` |
| `damage` | 整数 | 是 | 单次攻击造成的伤害，目前用于豌豆子弹 | `>= 0`，例如 `20` |
| `projectileSpeed` | 数字 | 是 | 子弹沿行飞行的速度，单位像素/秒 | `>= 0`，例如 `260` |
| `sunInterval` | 数字 | 是 | 产阳光行为的触发间隔，单位秒；`0` 表示不产阳光 | `>= 0`，例如 `5` |
| `sunAmount` | 整数 | 是 | 每次产出的阳光数量 | `>= 0`，例如 `25` |
| `color` | 字符串 | 是 | 兜底渲染颜色；没有图片素材时植物用该颜色绘制 | `"#RRGGBB"`，例如 `"#FFD700"` |
| `attackBehavior` | 枚举字符串 | 是 | 攻击行为键，决定攻击策略 | `NONE` 不攻击；`PEA` 发射豌豆 |
| `sunBehavior` | 枚举字符串 | 是 | 产阳光行为键，决定产阳光策略 | `NONE` 不产阳光；`PRODUCE_SUN` 定时产阳光 |
| `spriteKey` | 字符串 | 否 | 植物素材目录名；配置后按 `assets/plants/{spriteKey}/1.png ... {frameCount}.png` 加载动画帧 | 配置后不能为空；缺失资源启动时报错 |
| `frameCount` | 整数 | 配置 `spriteKey` 后必填 | 动画帧数量，从 `1.png` 开始连续编号 | `> 0`，例如 `18` |
| `animationFps` | 数字 | 配置 `spriteKey` 后必填 | 动画每秒播放帧数，控制动画快慢 | `> 0`，例如 `6` |
| `cardImage` | 字符串 | 配置 `spriteKey` 后必填 | 物品栏和选植物界面的卡片图片文件名，位于 `assets/cards/` | 非空，例如 `"sunflowerCard.png"` |

字段组合建议：

- `attackBehavior = "PEA"` 时，应同时配置 `attackInterval > 0`、`damage > 0`、`projectileSpeed > 0`；
- `sunBehavior = "PRODUCE_SUN"` 时，应同时配置 `sunInterval > 0`、`sunAmount > 0`；
- 当前校验只保证数值非负，数值与行为的一致性由配置者保证。

## zombies.json

顶层是数组，每个元素代表一种僵尸，元素必须包含以下字段。

| 字段 | 类型 | 必填 | 说明 | 校验规则 / 示例 |
| --- | --- | --- | --- | --- |
| `type` | 枚举字符串 | 是 | 僵尸唯一键，必须与 `ZombieType` 枚举一致；波次生成和渲染以此为准 | `BASIC` / `CONEHEAD` |
| `displayName` | 字符串 | 是 | 僵尸中文显示名，击杀提示使用 | 非空，例如 `"普通僵尸"` |
| `maxHp` | 整数 | 是 | 僵尸生命值上限 | `> 0`，例如 `100` |
| `speed` | 数字 | 是 | 僵尸向左移动速度，单位像素/秒 | `> 0`，例如 `20` |
| `damage` | 整数 | 是 | 僵尸每次啃咬植物造成的伤害 | `> 0`，例如 `10` |
| `biteInterval` | 数字 | 是 | 两次啃咬之间的间隔，单位秒 | `> 0`，例如 `1` |
| `color` | 字符串 | 是 | 兜底渲染颜色；没有图片素材时僵尸用该颜色绘制 | `"#RRGGBB"`，例如 `"#9E9E9E"` |
| `moveBehavior` | 枚举字符串 | 是 | 移动行为键，决定僵尸移动策略 | `MOVE_LEFT` 向左移动 |

## levels.json

顶层是数组，每个元素代表一个关卡。关卡包含三个层级：关卡、波次、生成条目。

### 关卡字段

| 字段 | 类型 | 必填 | 说明 | 校验规则 / 示例 |
| --- | --- | --- | --- | --- |
| `id` | 字符串 | 是 | 关卡唯一 ID，选关界面和 `LevelService` 按它查找关卡 | 建议格式 `"1-1"`、`"1-2"` |
| `name` | 字符串 | 是 | 关卡显示名 | 非空，例如 `"关卡 1-1"` |
| `initialSun` | 整数 | 是 | 开局阳光数量 | 建议 `>= 0`，例如 `150` |
| `availablePlants` | 枚举数组 | 是 | 本关可选的植物列表，必须是 `plants.json` 中存在的 `type` | 例如 `["SUNFLOWER", "PEASHOOTER"]` |
| `waves` | 对象数组 | 是 | 本关全部波次；建议至少配置一项，空数组会导致开局立即判定胜利 | 数组，例如 `[{ "startTime": 10, "spawns": [...] }]` |

### 波次字段

| 字段 | 类型 | 必填 | 说明 | 校验规则 / 示例 |
| --- | --- | --- | --- | --- |
| `startTime` | 数字 | 是 | 波次激活的关卡时间，单位秒；波次按顺序触发，上一波生成条目全部完成后才轮到下一波 | `>= 0`，例如 `10` |
| `spawns` | 对象数组 | 是 | 本波内的生成条目；支持混编多种僵尸，条目按数组顺序生成 | 至少 1 项，例如 `[{ "type": "BASIC", "count": 3, "spawnInterval": 4 }]` |

### 生成条目字段

| 字段 | 类型 | 必填 | 说明 | 校验规则 / 示例 |
| --- | --- | --- | --- | --- |
| `type` | 枚举字符串 | 是 | 本条目生成的僵尸类型，必须与 `ZombieType` 枚举一致 | `BASIC` / `CONEHEAD` |
| `count` | 整数 | 是 | 本条目生成的僵尸总数量 | `> 0`，例如 `3` |
| `spawnInterval` | 数字 | 是 | 条目内相邻两只僵尸的生成间隔，单位秒；条目第一只立即生成 | `> 0`，例如 `4` |

## 关联代码

- `PlantConfig`：`plants.json` 的 Record 与字段校验；
- `ZombieConfig`：`zombies.json` 的 Record 与字段校验；
- `LevelConfig` / `ZombieWave` / `ZombieSpawn`：`levels.json` 的 Record 与字段校验；
- `PlantCatalog` / `ZombieCatalog` / `LevelCatalog`：启动时加载并建立索引；
- `SpriteCatalog`：根据 `spriteKey`、`frameCount`、`cardImage` 加载图片素材。
