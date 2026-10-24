# Покрытие RU-пака по модам — 25.10.2026

> Метод: **не по паку, а по модам**. Список мод-пака взят из живой папки Drive
> `minecraft/mods` (ID `1ycFsvYr_O8WXedIz6G-GOU2sgT4q6HaR`, 210 элементов,
> 209 активных JAR, последняя запись 09.10.2026). Источник правды — сами JAR и их
> `assets/<namespace>/lang/en_us.json`; пак лишь накладывается сверху.

## Почему прошлый ответ был методически неверным

Я утверждал «в CA нет крыльев», посмотрев в `localization/ru-pack/assets/aeronautics`.
Это ошибка: отсутствие ключа в паке означает «мы это не переводили», а не
«этого нет в моде». Правильный порядок:

1. взять JAR из инстанса;
2. прочитать `assets/<ns>/lang/en_us.json` — это полный список видимых строк,
   включая `block.*`, то есть весь перечень блоков;
3. вычесть из него `ru_ru` самого JAR и `ru_ru` пака;
4. остаток — реальная дырка.

Проверено этим способом (исходники мода на GitHub, `MaxCreateMC/Create-Deep-Seas`):

- `create_submarine` — **521** английский ключ. В JAR свой `ru_ru` на 188,
  в паке было 87, эффективно 222 → **302 ключа без перевода (42 %)**. Это и было
  главной дыркой; теперь закрыто (см. ниже).
- `create_abyss` (идёт внутри Deep Seas) — 34 ключа; было эффективно 16
  (**18 без перевода**). Теперь в паке 34/34.
- `create_high_seas` (тоже внутри Deep Seas) — 175 ключей; было **0 %**. Теперь
  175/175.

Причина: пак собирался под Deep Seas 2.2.4 (там было 191 ключ). Сейчас в
инстансе `create_submarine-3.3.0.jar`, и мод вырос втрое — парусники, водолазный
костюм, сонар, декомпрессия, три неймспейса.

В самом моде крыльев **нет** — проверено по дереву исходников: единственные
совпадения на `wing` — это `FlowingFluidMixin.java` и текстуры `*_glowing.png`.
Крыло «шаттла» в паке — это Create Propulsion (`wing`, `tempered_wing`,
`copycat_wing`), как и было сказано в `docs/space/WINGS_RU.md`.

## Что сейчас в паке

- 682 файла, **106 неймспейсов**, **43 281** ключ `ru_ru`;
- `pack.mcmeta` = `Aeronautics Plus RU Pack v0.29-test`;
- собран `AeronauticsPlus-RU-Pack-v0.29-test.zip`, 1 429 828 Б,
  SHA-256 `20c73336bb30aade78aeafec289a17d71530a22a3bf6277e28ed38f12bacb7a5`.

До этого в корне репозитория и в Drive лежал ZIP **v0.26**, внутри которого
`pack.mcmeta` всё ещё говорил `v0.17-test — Mekanism config batch 1`: контент
совпадал с исходником, но описание и версия в опубликованном архиве отставали.
Теперь пересобрано из исходника, старые ZIP удалены.

## Моды без неймспейса в паке

Полный список 209 JAR разложен на три группы. Первые две — работа, третья — шум.

### A. Контентные моды без покрытия (кандидаты на перевод)

| JAR | Неймспейс | Комментарий |
|---|---|---|
| `create_submarine-3.3.0.jar` | `create_submarine`, `create_abyss`, `create_high_seas` | **730/730**, закрыт |
| `create_the_air_wars-5.00.jar` | `create_the_air_wars` | новый мод, в паке нет вообще |
| `fluxnautics-1.0.0.jar` | `fluxnautics` | нет |
| `railways-0.3.0-beta.2` | `railways` | нет |
| `createdeco-2.1.3.jar` | `createdeco` | нет |
| `createoreexcavation` | `createoreexcavation` | нет |
| `create_jetpack-forge` / `create_sa_curios_jetpacks` | `create_jetpack`, `create_sa` | нет |
| `create_mechanical_extruder` / `_spawner` | `create_mechanical_extruder`, `_spawner` | нет |
| `create-stuff-additions` | `create_stuff_additions` | нет |
| `createtree-3.2.3.jar` | `createtree` | нет |
| `mechanicals-1.21.1-1.1.6` | `mechanicals` | нет |
| `moderntrainparts-0.2.5` | `moderntrainparts` | нет |
| `simulatedcoasters-0.1.5` | `simulatedcoasters` | нет |
| `tracks_plus-1.0.6b7.jar` | `tracks_plus` | нет |
| `interiors-0.6.1 v3.jar` | `interiors` | нет |
| `pipeorgans-0.8.2` | `pipeorgans` | нет |
| `perspatium`, `weaversparadise`, `vortylib` | одноимённые | нет |
| `immersive_aircraft-1.5.2` | `immersive_aircraft` | нет |
| `extradelight`, `MyNethersDelight`, `sliceanddice` | одноимённые | нет |
| `tacz-...-r6.jar` | `tacz` | нет |
| `gravestone`, `ironchest`, `carryon`, `comforts`, `cosmeticarmorreworked`, `naturescompass`, `Chest-Preview`, `betterdays`, `easy_mob_spawn_control`, `custommusic`, `freecam`, `firstperson`, `notenoughanimations`, `sound-physics-remastered`, `dynamic-fps`, `KeybindAtlas`, `InventoryProfilesNext`, `Controlling`, `Searchables` | одноимённые | нет |
| `Yungs*` (10 JAR) | `yungsapi`, `better*` | нет |
| `mcw-*` (5 JAR) | `macaws_*` | нет |
| `AdditionalEnchantedMiner` | `veinmining` | нет |
| `chemmod-0.9.0-test.4.jar` | `chemmod` | в паке нет; полагается на `ru_ru` внутри JAR — требует проверки |

### B. Формально без неймспейса, но текст есть в JAR (библиотеки с GUI)

`cloth-config`, `balm`, `architectury`, `curios`, `geckolib`, `Patchouli`,
`resourcefullib`, `TerraBlender`, `supermartijn642*`, `fzzy_config`, `fusion`,
`Glodium`, `kotlinforforge`, `rhino`, `ScalableCatsForce`, `ZeroCore2`,
`potentials`, `polymorph`, `lithostitched`, `terrain_slabs`.
Часть строк видна игроку (экраны конфигов). Требуют отдельного exact-JAR прохода.

### C. Переводить нечего

`ferritecore`, `modernfix`, `noisium`, `entityculling`, `ImmediatelyFast`,
`lambdynamiclights`, `sodium`, `iris`, `spark`, `Chunky`, `Clumps`, `Ksyxis`,
`MouseTweaks`, `ritchiesprojectilelib`, `common-storage-lib`, `configuration`
(покрыт), `codxlib`, `gtbridge`, `baguettelib`, `gaboulibs`, `libIPN`.

## Что остаётся недоказанным

Полный ключевой проход по 209 JAR в этой песочнице невозможен: JAR лежат в Drive
и на Modrinth CDN, а из песочницы доступны только GitHub, npm и PyPI. Проверено
точно то, у чего есть публичные исходники на GitHub (Deep Seas — полностью).
Остальные цифры в таблицах — это **наличие/отсутствие неймспейса**, а не подсчёт
ключей.

Воспроизвести полный аудит на машине с инстансом:

```bash
python3 scripts/audit_ru_localization.py \
  --mods /path/to/instance/mods \
  --resource-pack AeronauticsPlus-RU-Pack-v0.29-test.zip \
  --output docs/localization-audit
```

## Приоритет следующего прохода

1. `create_submarine-3.3.0.jar` закрыт полностью: **730/730** ключей по трём
   неймспейсам (`create_submarine` 521, `create_high_seas` 175, `create_abyss`
   34), 0 пропусков, 0 расхождений плейсхолдеров.
2. `create_the_air_wars`, `fluxnautics`, `railways`, `createdeco` — новые моды,
   которых в паке нет вовсе.
3. Библиотеки из группы B — после контентных.
