# Полный аудит русификации сборки

> Отчёт сгенерирован сравнением реальных `en_us`/`ru_ru` из JAR, включая вложенные Jar-in-Jar, с наложением текущего RU-pack.

## Объём проверки

- Проверено активных JAR: **207**.
- Все JAR корректны как ZIP: **207/207**.
- Найдено пространств имён с `en_us`: **196**.
- Английских ключей: **77,122**.
- Ключей в RU-pack: **34,135**; совпадают с текущими `en_us`: **33,797**; устаревших/лишних: **338**.
- Покрыто встроенным `ru_ru` или `AeronauticsPlus-RU-Pack-v0.8-test.zip`: **68,242** (**88.49%**).
- Отсутствует переводов: **8,880**.
- Ключей, где effective RU дословно совпадает с English: **7,043**; значений без кириллицы: **7,105** (это очередь ручной проверки, а не автоматический приговор для брендов и аббревиатур).
- Несовпадений format placeholders (`%s`, `%1$s`, `{0}`): **0**.
- Дублирующихся ключей внутри исходных lang-файлов: **157** (сохранено последнее значение, как у обычного JSON-парсера).
- Полностью покрытых пространств: **108**; частичных: **66**; без единого русского ключа: **22**.
- Пространств с английскими/неизменёнными значениями, требующими ручной проверки: **116**.
- Статических англоязычных названий модов в metadata: **208**. Обычный resource pack их не переопределяет.

## Покрытие по типам ключей

| Тип | Покрыто | Всего | Доля |
|---|---:|---:|---:|
| Блоки | 26,541 | 27,856 | 95.28% |
| Предметы | 8,543 | 8,783 | 97.27% |
| Подсказки/описания | 6,101 | 7,495 | 81.40% |
| Интерфейсы/config/messages | 5,765 | 7,465 | 77.23% |
| Сущности | 832 | 911 | 91.33% |
| Творческие вкладки | 135 | 139 | 97.12% |
| Прочие строки | 20,325 | 24,473 | 83.05% |

## Показанные пользователем проблемы

### `chiselsandbits`

Покрытие ключей: **163/413 (39.47%)**, отсутствует **250**.

| Ключ | English | Текущее effective RU | Источник |
|---|---|---|---|
| `item.chiselsandbits.chisel_stone` | Stone Chisel | Каменное долото | RU-pack |
| `mod.chiselsandbits.chiselmode.mode_grouped` | Chisel Mode: %1$s (%2$s) | Режим долота: %1$s (%2$s) | RU-pack |
| `mod.chiselsandbits.chiselmode.cubed` | Cube | Куб | RU-pack |
| `mod.chiselsandbits.chiselmode.single` | Single Bit | Один бит | RU-pack |

### `aeronauticswinds`

Покрытие ключей: **297/297 (100.00%)**, отсутствует **0**.

| Ключ | English | Текущее effective RU | Источник |
|---|---|---|---|
| `item.aeronauticswinds.cotton_seeds` | Cotton Seeds | Семена хлопка | RU-pack |

### `create_no_touching`

Покрытие ключей: **17/17 (100.00%)**, отсутствует **0**.

| Ключ | English | Текущее effective RU | Источник |
|---|---|---|---|
| `item_group.create_no_touching.create_no_touching` | Create No Touching | Create: Без контакта | RU-pack |

Примечание к `Monook Seeds`/`Moonoak Seeds`: такой строки, ключа или ресурса нет в проверенном `aeronauticswinds-1.3.1.jar`; единственные семена в нём — `item.aeronauticswinds.cotton_seeds` (`Cotton Seeds`). Значит, показанный экземпляр игры использует другой JAR либо динамическую строку, которой нет в папке сборки. В v0.8-test исправлено доступное `Cotton Seeds` → `Семена хлопка`.

Название **Create: No Touching** в строке принадлежности моду берётся из `META-INF/neoforge.mods.toml`, а не из lang-файла. Его нельзя корректно заменить обычным resource pack; для этого нужен отдельный кодовый патч/аддон или изменение исходного стороннего мода.

## Главная очередь по числу отсутствующих ключей

| Namespace | Нет ключей | Покрытие | Block | Item | Tooltip | GUI/config |
|---|---:|---:|---:|---:|---:|---:|
| `mekanism` | 1630 | 1609/3239 (49.68%) | 187/200 | 166/168 | 136/683 | 146/157 |
| `immersiveengineering` | 894 | 1061/1955 (54.27%) | 266/846 | 264/314 | 2/2 | 107/226 |
| `ldlib2` | 696 | 7/703 (1.00%) | 0/2 | 0/2 | 0/14 | 2/193 |
| `mekanismtools` | 615 | 185/800 (23.12%) | 0/0 | 168/168 | 8/303 | 0/0 |
| `burnt` | 584 | 0/584 (0.00%) | 0/382 | 0/72 | 0/52 | 0/0 |
| `xaeroworldmap` | 322 | 0/322 (0.00%) | 0/0 | 0/0 | 0/2 | 0/313 |
| `powergrid` | 284 | 669/953 (70.20%) | 57/77 | 50/55 | 89/119 | 102/165 |
| `pneumaticcraft` | 277 | 1994/2271 (87.80%) | 166/167 | 164/166 | 366/382 | 895/1115 |
| `ldlib` | 256 | 7/263 (2.66%) | 0/0 | 0/0 | 0/5 | 2/225 |
| `chiselsandbits` | 250 | 163/413 (39.47%) | 19/19 | 29/29 | 0/63 | 0/60 |
| `emi` | 210 | 747/957 (78.06%) | 0/0 | 0/0 | 49/49 | 122/122 |
| `pamhc2crops` | 194 | 320/514 (62.26%) | 103/103 | 216/216 | 0/0 | 0/0 |
| `biomesoplenty` | 176 | 407/583 (69.81%) | 296/429 | 14/32 | 2/2 | 0/0 |
| `veil` | 174 | 0/174 (0.00%) | 0/0 | 0/0 | 0/0 | 0/3 |
| `mekanismgenerators` | 164 | 200/364 (54.95%) | 28/28 | 7/7 | 9/71 | 5/5 |
| `ok_zoomer` | 159 | 0/159 (0.00%) | 0/0 | 0/0 | 0/67 | 0/88 |
| `sophisticatedbackpacks` | 149 | 257/406 (63.30%) | 6/6 | 67/79 | 87/89 | 68/90 |
| `ad_astra` | 146 | 685/831 (82.43%) | 333/333 | 84/84 | 91/92 | 10/41 |
| `jade` | 129 | 281/410 (68.54%) | 0/0 | 0/0 | 19/19 | 177/220 |
| `sable` | 128 | 0/128 (0.00%) | 0/0 | 0/0 | 0/4 | 0/4 |
| `productivebees` | 120 | 998/1118 (89.27%) | 302/302 | 68/69 | 101/112 | 9/9 |
| `sodium` | 89 | 0/89 (0.00%) | 0/0 | 0/0 | 0/33 | 0/2 |
| `createdeco` | 81 | 330/411 (80.29%) | 310/389 | 20/20 | 0/0 | 0/0 |
| `xaerominimap` | 77 | 569/646 (88.08%) | 0/0 | 0/0 | 6/6 | 550/627 |
| `create_optical` | 73 | 96/169 (56.80%) | 9/13 | 12/14 | 5/8 | 7/22 |
| `rechiseledae2` | 73 | 0/73 (0.00%) | 0/72 | 0/0 | 0/0 | 0/0 |
| `ftbbackups3` | 69 | 0/69 (0.00%) | 0/0 | 0/0 | 0/18 | 0/9 |
| `ratatouille` | 58 | 75/133 (56.39%) | 9/17 | 20/42 | 1/4 | 0/2 |
| `mynethersdelight` | 55 | 132/187 (70.59%) | 44/48 | 65/71 | 6/27 | 0/0 |
| `ad_astra_giselle_addon` | 51 | 117/168 (69.64%) | 3/4 | 6/8 | 7/12 | 69/83 |

## Полная таблица по пространствам имён

| Namespace | Статус | Покрытие | Нет ключей | Block | Item | Tooltip | GUI/config | JAR |
|---|---|---:|---:|---:|---:|---:|---:|---|
| `burnt` | none | 0/584 (0.00%) | 584 | 0/382 | 0/72 | 0/52 | 0/0 | burnt-1.10.5.1-neoforge-1.21.1.jar |
| `xaeroworldmap` | none | 0/322 (0.00%) | 322 | 0/0 | 0/0 | 0/2 | 0/313 | xaeroworldmap-neoforge-1.21.1-1.46.0.jar |
| `veil` | none | 0/174 (0.00%) | 174 | 0/0 | 0/0 | 0/0 | 0/3 | pmweather-1.21.1-0.17.14-alpha.jar; sable-neoforge-1.21.1-2.0.5.jar |
| `ok_zoomer` | none | 0/159 (0.00%) | 159 | 0/0 | 0/0 | 0/67 | 0/88 | ok_zoomer-neo-10.0.0-beta.13.jar |
| `sable` | none | 0/128 (0.00%) | 128 | 0/0 | 0/0 | 0/4 | 0/4 | sable-neoforge-1.21.1-2.0.5.jar |
| `sodium` | none | 0/89 (0.00%) | 89 | 0/0 | 0/0 | 0/33 | 0/2 | sodium-neoforge-0.8.13+mc1.21.1.jar |
| `rechiseledae2` | none | 0/73 (0.00%) | 73 | 0/72 | 0/0 | 0/0 | 0/0 | rechiseledae2-neoforge-1.21-1.21.1-1.1.0.jar |
| `ftbbackups3` | none | 0/69 (0.00%) | 69 | 0/0 | 0/0 | 0/18 | 0/9 | ftb-backups-3-21.1.5.jar |
| `balm` | none | 0/36 (0.00%) | 36 | 0/0 | 0/0 | 0/0 | 0/1 | balm-neoforge-1.21.1-21.0.66.jar |
| `productivelib` | none | 0/27 (0.00%) | 27 | 0/0 | 0/18 | 0/0 | 0/0 | productivebees-1.21.1-13.13.5.jar |
| `citadel` | none | 0/24 (0.00%) | 24 | 0/1 | 0/5 | 0/0 | 0/11 | citadel-1.21.1-2.7.6.jar |
| `create_sa_curios_jetpacks` | none | 0/19 (0.00%) | 19 | 0/0 | 0/0 | 0/2 | 0/15 | create_sa_curios_jetpacks-neoforge-1.21.1-1.2.4.jar |
| `flywheel` | none | 0/12 (0.00%) | 12 | 0/0 | 0/0 | 0/0 | 0/11 | create-1.21.1-6.0.10.jar |
| `flightlib` | none | 0/11 (0.00%) | 11 | 0/0 | 0/0 | 0/3 | 0/5 | create_jetpack-forge-5.2.1.jar |
| `trender` | none | 0/10 (0.00%) | 10 | 0/0 | 0/0 | 0/2 | 0/0 | entityculling-neoforge-1.11.2-mc1.21.1.jar; firstperson-neoforge-2.7.2-mc1.21.1.jar; notenoughanimations-neoforge-1.12.6-mc1.21.1.jar |
| `atmosphere` | none | 0/9 (0.00%) | 9 | 0/0 | 0/0 | 0/0 | 0/0 | sliceanddice-4.3.4-neoforge.jar |
| `fusion` | none | 0/8 (0.00%) | 8 | 0/0 | 0/0 | 0/0 | 0/0 | fusion-1.3.15b-neoforge-mc1.21.1.jar |
| `apollib` | none | 0/7 (0.00%) | 7 | 0/0 | 0/0 | 0/3 | 0/4 | lithostitched-1.8.0-neoforge-21.1.jar; tectonic-3.0.28-neoforge-21.1.jar |
| `ctm` | none | 0/7 (0.00%) | 7 | 0/0 | 0/0 | 0/2 | 0/0 | CTM-1.21-1.2.1+3.jar |
| `ae2wtlib_api` | none | 0/5 (0.00%) | 5 | 0/0 | 0/0 | 0/0 | 0/0 | ae2wtlib-19.5.1.jar; ExtendedAE-1.21-2.2.35-neoforge.jar |
| `spectrelib` | none | 0/1 (0.00%) | 1 | 0/0 | 0/0 | 0/0 | 0/0 | comforts-neoforge-9.0.5+1.21.1.jar |
| `supermartijn642corelib` | none | 0/1 (0.00%) | 1 | 0/0 | 0/0 | 0/0 | 0/0 | supermartijn642corelib-1.1.24a-neoforge-mc1.21.jar |
| `ldlib2` | partial | 7/703 (1.00%) | 696 | 0/2 | 0/2 | 0/14 | 2/193 | ldlib2-neoforge-1.21.1-2.2.41-all.jar |
| `ldlib` | partial | 7/263 (2.66%) | 256 | 0/0 | 0/0 | 0/5 | 2/225 | gtceu-1.21.1-7.0.2.jar |
| `xaerolib` | partial | 10/46 (21.74%) | 36 | 0/0 | 0/0 | 0/3 | 10/43 | xaerominimap-neoforge-1.21.1-26.5.0.jar |
| `mekanismtools` | partial | 185/800 (23.12%) | 615 | 0/0 | 168/168 | 8/303 | 0/0 | MekanismTools-1.21.1-10.7.19.85.jar |
| `yungsapi` | partial | 1/3 (33.33%) | 2 | 0/0 | 0/0 | 0/1 | 1/1 | YungsApi-1.21.1-NeoForge-5.1.9.jar |
| `betterendisland` | partial | 5/13 (38.46%) | 8 | 0/0 | 0/0 | 2/6 | 0/0 | YungsBetterEndIsland-1.21.1-NeoForge-3.1.2.jar |
| `chiselsandbits` | partial | 163/413 (39.47%) | 250 | 19/19 | 29/29 | 0/63 | 0/60 | chisels-and-bits-neoforge-21.1.32.jar |
| `mekanism` | partial | 1609/3239 (49.68%) | 1630 | 187/200 | 166/168 | 136/683 | 146/157 | Mekanism-1.21.1-10.7.19.85.jar |
| `vortylib` | partial | 3/6 (50.00%) | 3 | 0/0 | 0/0 | 0/0 | 0/0 | vortylib-1.2.5.2.jar |
| `immersiveengineering` | partial | 1061/1955 (54.27%) | 894 | 266/846 | 264/314 | 2/2 | 107/226 | AgriCraft-neoforge-1.21.1-4.0.17.jar; ImmersiveEngineering-1.21.1-12.4.2-194.jar |
| `appleskin` | partial | 12/22 (54.55%) | 10 | 0/0 | 0/0 | 3/13 | 0/0 | appleskin-neoforge-mc1.21-3.0.9.jar |
| `betterstrongholds` | partial | 6/11 (54.55%) | 5 | 0/0 | 0/0 | 1/4 | 0/0 | YungsBetterStrongholds-1.21.1-NeoForge-5.1.3.jar |
| `mekanismgenerators` | partial | 200/364 (54.95%) | 164 | 28/28 | 7/7 | 9/71 | 5/5 | MekanismGenerators-1.21.1-10.7.19.85.jar |
| `ratatouille` | partial | 75/133 (56.39%) | 58 | 9/17 | 20/42 | 1/4 | 0/2 | create_ratatouille-1.21.1-1.4.0.jar |
| `create_optical` | partial | 96/169 (56.80%) | 73 | 9/13 | 12/14 | 5/8 | 7/22 | create_optical-0.4.2.jar |
| `libipn` | partial | 21/34 (61.76%) | 13 | 0/0 | 0/0 | 0/2 | 13/24 | libIPN-neoforge-1.21.1-6.6.3.jar |
| `pamhc2crops` | partial | 320/514 (62.26%) | 194 | 103/103 | 216/216 | 0/0 | 0/0 | AgriCraft-neoforge-1.21.1-4.0.17.jar; pamhc2crops-NEOFORGE-1.21.1-1.0.9.jar |
| `ae2wtlib` | partial | 29/46 (63.04%) | 17 | 0/0 | 8/8 | 0/0 | 21/36 | ae2wtlib-19.5.1.jar |
| `emi_ores` | partial | 79/125 (63.20%) | 46 | 0/0 | 0/0 | 0/0 | 0/0 | emi_ores-1.3+1.21.1+neoforge.jar |
| `sophisticatedbackpacks` | partial | 257/406 (63.30%) | 149 | 6/6 | 67/79 | 87/89 | 68/90 | sophisticatedbackpacks-1.21.1-3.26.3.2158.jar |
| `lambdynlights` | partial | 41/63 (65.08%) | 22 | 0/0 | 0/0 | 16/24 | 0/0 | lambdynamiclights-4.8.11+1.21.1.jar |
| `jade` | partial | 281/410 (68.54%) | 129 | 0/0 | 0/0 | 19/19 | 177/220 | Jade-1.21.1-NeoForge-15.10.6.jar |
| `ad_astra_giselle_addon` | partial | 117/168 (69.64%) | 51 | 3/4 | 6/8 | 7/12 | 69/83 | Ad-Astra-Giselle-Addon-neoforge-1.21.1-8.1.jar |
| `biomesoplenty` | partial | 407/583 (69.81%) | 176 | 296/429 | 14/32 | 2/2 | 0/0 | AgriCraft-neoforge-1.21.1-4.0.17.jar; BiomesOPlenty-neoforge-1.21.1-21.1.0.14.jar |
| `powergrid` | partial | 669/953 (70.20%) | 284 | 57/77 | 50/55 | 89/119 | 102/165 | powergrid-mc1.21.1-0.6.0.1.jar |
| `tracks` | partial | 31/44 (70.45%) | 13 | 1/1 | 9/22 | 16/16 | 2/2 | tracks_plus-1.0.6b7.jar |
| `mynethersdelight` | partial | 132/187 (70.59%) | 55 | 44/48 | 65/71 | 6/27 | 0/0 | MyNethersDelight-1.21.1-1.10.4.1.jar |
| `entityculling` | partial | 20/27 (74.07%) | 7 | 0/0 | 0/0 | 8/11 | 2/3 | entityculling-neoforge-1.11.2-mc1.21.1.jar |
| `dynamic_fps` | partial | 63/85 (74.12%) | 22 | 0/0 | 0/0 | 10/16 | 49/61 | dynamic-fps-3.11.4+minecraft-1.21.0-neoforge.jar |
| `emi` | partial | 747/957 (78.06%) | 210 | 0/0 | 0/0 | 49/49 | 122/122 | emi-1.1.24+1.21.1+neoforge.jar; nuclearcraftneohaul-1.21.1-0.10.10-kjs181patch.jar |
| `modernfix` | partial | 121/155 (78.06%) | 34 | 0/0 | 0/0 | 4/4 | 6/6 | modernfix-neoforge-5.27.24+mc1.21.1.jar |
| `firstperson` | partial | 26/33 (78.79%) | 7 | 0/0 | 0/0 | 10/12 | 2/3 | firstperson-neoforge-2.7.2-mc1.21.1.jar |
| `create_new_age` | partial | 129/163 (79.14%) | 34 | 28/36 | 20/23 | 30/38 | 0/1 | create-new-age-1.2.0+neoforge-mc1.21.1.jar |
| `createdeco` | partial | 330/411 (80.29%) | 81 | 310/389 | 20/20 | 0/0 | 0/0 | createdeco-2.1.3.jar |
| `ftbteams` | partial | 90/112 (80.36%) | 22 | 0/0 | 0/0 | 1/3 | 41/55 | ftb-teams-neoforge-2101.1.11.jar |
| `ad_astra` | partial | 685/831 (82.43%) | 146 | 333/333 | 84/84 | 91/92 | 10/41 | adastra-1.21.1-1.16.26-neoforge.jar |
| `controlling` | partial | 10/12 (83.33%) | 2 | 0/0 | 0/0 | 0/0 | 10/12 | Controlling-neoforge-1.21.1-19.0.5.jar |
| `notenoughanimations` | partial | 92/107 (85.98%) | 15 | 0/0 | 0/0 | 36/42 | 0/0 | notenoughanimations-neoforge-1.12.6-mc1.21.1.jar |
| `pneumaticcraft` | partial | 1994/2271 (87.80%) | 277 | 166/167 | 164/166 | 366/382 | 895/1115 | pneumaticcraft-repressurized-8.2.23+mc1.21.1.jar |
| `xaerominimap` | partial | 569/646 (88.08%) | 77 | 0/0 | 0/0 | 6/6 | 550/627 | xaerominimap-neoforge-1.21.1-26.5.0.jar |
| `nutritionalbalance` | partial | 24/27 (88.89%) | 3 | 0/0 | 0/1 | 0/1 | 1/2 | nutritionalbalance-1.21.1-7.0.3.jar |
| `productivebees` | partial | 998/1118 (89.27%) | 120 | 302/302 | 68/69 | 101/112 | 9/9 | productivebees-1.21.1-13.13.5.jar |
| `extendedae` | partial | 227/253 (89.72%) | 26 | 34/35 | 45/45 | 17/25 | 111/124 | ExtendedAE-1.21-2.2.35-neoforge.jar |
| `sound_physics_remastered` | partial | 66/72 (91.67%) | 6 | 0/0 | 0/0 | 30/33 | 0/0 | sound-physics-remastered-neoforge-1.21.1-1.5.1.jar |
| `curios` | partial | 44/48 (91.67%) | 4 | 0/0 | 0/0 | 2/3 | 3/5 | curios-neoforge-9.5.1+1.21.1.jar |
| `iris` | partial | 67/73 (91.78%) | 6 | 0/0 | 0/0 | 7/8 | 31/32 | iris-neoforge-1.8.14-beta.1+mc1.21.1.jar |
| `inventoryprofilesnext` | partial | 516/557 (92.64%) | 41 | 0/0 | 0/0 | 222/240 | 250/273 | InventoryProfilesNext-neoforge-1.21.1-2.2.5.jar |
| `emi_enchanting` | partial | 13/14 (92.86%) | 1 | 0/0 | 0/0 | 1/1 | 0/0 | emi_enchanting-0.1.2+1.21+neoforge.jar |
| `zerocore` | partial | 48/50 (96.00%) | 2 | 0/0 | 1/1 | 2/4 | 29/29 | ZeroCore2-1.21.1-2.4.21.jar |
| `ftblibrary` | partial | 109/113 (96.46%) | 4 | 0/0 | 3/3 | 9/10 | 47/47 | ftb-library-neoforge-2101.1.36.jar |
| `createdieselgenerators` | partial | 286/293 (97.61%) | 7 | 64/65 | 37/37 | 56/58 | 15/15 | createdieselgenerators-1.21.1-1.3.15.jar |
| `patchouli` | partial | 90/92 (97.83%) | 2 | 0/0 | 16/16 | 4/4 | 68/68 | Patchouli-1.21.1-93-NEOFORGE.jar |
| `ponder` | partial | 61/62 (98.39%) | 1 | 0/0 | 0/0 | 6/6 | 1/2 | create-1.21.1-6.0.10.jar; sliceanddice-4.3.4-neoforge.jar |
| `takesapillage` | partial | 61/62 (98.39%) | 1 | 0/0 | 7/7 | 3/3 | 16/17 | takesapillage-neoforge-1.0.12+mc1.21.1.jar |
| `railways` | partial | 2501/2540 (98.46%) | 39 | 1437/1440 | 464/467 | 55/55 | 13/13 | railways-0.3.0-beta.2+neoforge-mc1.21.1.jar |
| `xaerobetterpvp` | partial | 77/78 (98.72%) | 1 | 0/0 | 0/0 | 4/4 | 73/74 | xaerominimap-neoforge-1.21.1-26.5.0.jar |
| `extradelight` | partial | 1780/1802 (98.78%) | 22 | 823/823 | 668/668 | 33/55 | 2/2 | extradelight-2.6.6.jar |
| `createaddition` | partial | 260/263 (98.86%) | 3 | 21/23 | 34/34 | 114/114 | 0/0 | createaddition-1.7.1.jar |
| `create_mechanical_spawner` | partial | 105/106 (99.06%) | 1 | 34/34 | 28/28 | 3/3 | 1/1 | create_mechanical_spawner-1.21.1-1.3.2-6.0.10-kjs181patch.jar |
| `weaversparadise` | partial | 322/324 (99.38%) | 2 | 17/17 | 128/128 | 9/9 | 1/1 | weaversparadise-1.6.3.jar |
| `create` | partial | 3619/3639 (99.45%) | 20 | 643/643 | 103/103 | 258/269 | 459/464 | create-1.21.1-6.0.10.jar |
| `tacz` | partial | 559/561 (99.64%) | 2 | 3/3 | 8/8 | 78/78 | 179/181 | tacz-neoforge-1.21.1-1.1.8-hotfix-r6.jar |
| `mcwdoors` | partial | 287/288 (99.65%) | 1 | 269/269 | 13/13 | 0/0 | 0/0 | mcw-doors-1.1.5-mc1.21.1neoforge.jar |
| `ae2` | partial | 1018/1021 (99.71%) | 3 | 102/102 | 262/262 | 179/179 | 254/256 | appliedenergistics2-19.2.17.jar |
| `createbigcannons` | partial | 736/738 (99.73%) | 2 | 142/142 | 64/64 | 198/198 | 7/7 | cbc_going_ballistic-0.3.1.jar; createbigcannons-5.11.7+mc.1.21.1.jar |
| `gtceu` | partial | 5544/5546 (99.96%) | 2 | 1583/1583 | 689/690 | 1143/1143 | 302/303 | gtceu-1.21.1-7.0.2.jar |
| `chipped` | complete | 7265/7265 (100.00%) | 0 | 6967/6967 | 6/6 | 0/0 | 0/0 | chipped-neoforge-1.21.1-4.0.2.jar |
| `rechiseled` | complete | 3656/3656 (100.00%) | 0 | 2418/2418 | 0/0 | 1/1 | 0/0 | rechiseled-1.2.6-neoforge-mc1.21.jar |
| `nuclearcraftneohaul` | complete | 3643/3643 (100.00%) | 0 | 887/887 | 1084/1084 | 418/418 | 87/87 | nuclearcraftneohaul-1.21.1-0.10.10-kjs181patch.jar |
| `chisel` | complete | 2932/2932 (100.00%) | 0 | 2880/2880 | 6/6 | 17/17 | 4/4 | chisel-1.21.1-NeoForge-1.4.1.jar |
| `alexscaves` | complete | 1725/1725 (100.00%) | 0 | 355/355 | 312/312 | 0/0 | 2/2 | alexscaves-1.1.1-neoforge+1.21.1.jar |
| `wariumce` | complete | 1281/1281 (100.00%) | 0 | 502/502 | 390/390 | 101/101 | 40/40 | tectonic-3.0.28-neoforge-21.1.jar; wariumce-1.0.3-kjs181patch.jar |
| `hbmsntm` | complete | 1228/1228 (100.00%) | 0 | 202/202 | 742/742 | 0/0 | 4/4 | hbmsntm-198A.jar |
| `tfmg` | complete | 1170/1170 (100.00%) | 0 | 433/433 | 176/176 | 211/211 | 0/0 | tfmg-1.2.0.jar |
| `alexsmobs` | complete | 1085/1085 (100.00%) | 0 | 118/118 | 336/336 | 19/19 | 1/1 | alexsmobs-2.2.2-neoforge+1.21.1.jar |
| `cannoncompressedarmor` | complete | 1052/1052 (100.00%) | 0 | 1032/1032 | 18/18 | 0/0 | 0/0 | cannoncompressedarmor-1.0.0.jar |
| `ftbquests` | complete | 772/772 (100.00%) | 0 | 42/42 | 12/12 | 91/91 | 163/163 | ftb-quests-neoforge-2101.1.36.jar |
| `mcwfurnitures` | complete | 661/661 (100.00%) | 0 | 652/652 | 2/2 | 0/0 | 0/0 | mcw-furniture-3.4.1-mc1.21.1neoforge.jar |
| `simulated` | complete | 650/650 (100.00%) | 0 | 98/98 | 10/10 | 80/80 | 76/76 | create-aeronautics-bundled-1.21.1-1.3.2.jar |
| `enderio` | complete | 619/619 (100.00%) | 0 | 212/212 | 128/128 | 69/69 | 83/83 | enderio-8.2.12-beta.jar |
| `mcwroofs` | complete | 610/610 (100.00%) | 0 | 605/605 | 2/2 | 0/0 | 0/0 | mcw-roofs-2.3.2-mc1.21.1neoforge.jar |
| `cbcmoreshells` | complete | 505/505 (100.00%) | 0 | 233/233 | 95/95 | 140/140 | 0/0 | CBC-Military-Supplement-1.21.1-2.1.4.jar |
| `farmersdelight` | complete | 487/487 (100.00%) | 0 | 139/139 | 97/97 | 60/60 | 0/0 | AgriCraft-neoforge-1.21.1-4.0.17.jar; FarmersDelight-1.21.1-1.3.4.jar |
| `agricraft` | complete | 449/449 (100.00%) | 0 | 8/8 | 20/20 | 86/86 | 73/73 | AgriCraft-neoforge-1.21.1-4.0.17.jar |
| `create_connected` | complete | 422/422 (100.00%) | 0 | 109/109 | 14/14 | 73/73 | 71/71 | create_connected-1.3.3-mc1.21.1.jar |
| `create_dragons_plus` | complete | 356/356 (100.00%) | 0 | 56/56 | 57/57 | 6/6 | 0/0 | CreateDragonsPlus-1.11.9.jar |
| `exnihilosequentia` | complete | 352/352 (100.00%) | 0 | 53/53 | 174/174 | 5/5 | 4/4 | ExNihiloSequentia-1.21.1-7.0.3.5-build.LOCAL.jar |
| `radiologistics` | complete | 338/338 (100.00%) | 0 | 14/14 | 4/4 | 27/27 | 12/12 | CreateRadiologistics-1.1.2.jar |
| `pipeorgans` | complete | 325/325 (100.00%) | 0 | 38/38 | 12/12 | 0/0 | 48/48 | pipeorgans-0.8.2+1.21.1.jar |
| `sophisticatedcore` | complete | 318/318 (100.00%) | 0 | 0/0 | 2/2 | 86/86 | 215/215 | sophisticatedcore-1.21.1-1.5.1.2341.jar |
| `createfirefightingadd` | complete | 306/306 (100.00%) | 0 | 19/19 | 16/16 | 66/66 | 9/9 | createfirefightingadd-0.2.3-beta.jar |
| `aeronauticswinds` | complete | 297/297 (100.00%) | 0 | 14/14 | 17/17 | 114/114 | 46/46 | aeronauticswinds-1.3.1.jar |
| `createpropulsion` | complete | 276/276 (100.00%) | 0 | 33/33 | 16/16 | 59/59 | 69/69 | createpropulsion-1.1.5.jar |
| `mysticalagriculture` | complete | 272/272 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | AgriCraft-neoforge-1.21.1-4.0.17.jar |
| `quarryplus` | complete | 241/241 (100.00%) | 0 | 33/33 | 22/22 | 26/26 | 39/39 | AdditionalEnchantedMiner-1.21.1-neoforge-21.1.164.jar |
| `morepropulsion` | complete | 227/227 (100.00%) | 0 | 39/39 | 1/1 | 57/57 | 0/0 | morepropulsion-1.4.0.jar |
| `excompressum` | complete | 225/225 (100.00%) | 0 | 61/61 | 14/14 | 88/88 | 2/2 | excompressum-neoforge-1.21.1-21.1.15.0.jar |
| `pmweather` | complete | 222/222 (100.00%) | 0 | 100/100 | 6/6 | 7/7 | 3/3 | pmweather-1.21.1-0.17.14-alpha.jar |
| `aeronautics` | complete | 219/219 (100.00%) | 0 | 43/43 | 4/4 | 8/8 | 20/20 | create-aeronautics-bundled-1.21.1-1.3.2.jar |
| `create_sa` | complete | 203/203 (100.00%) | 0 | 2/2 | 91/91 | 33/33 | 8/8 | create-stuff-additions1.21.1_v2.1.4b.jar |
| `pamhc2foodcore` | complete | 203/203 (100.00%) | 0 | 0/0 | 202/202 | 0/0 | 0/0 | pamhc2foodcore-NEOFORGE-1.21.1-1.0.4.jar |
| `mcwfences` | complete | 184/184 (100.00%) | 0 | 180/180 | 0/0 | 0/0 | 0/0 | mcw-mcwfences-1.2.1-mc1.21.1neoforge.jar |
| `burnt_additions` | complete | 180/180 (100.00%) | 0 | 77/77 | 21/21 | 34/34 | 0/0 | burnt-1.10.5.1-neoforge-1.21.1.jar |
| `keybindatlas` | complete | 180/180 (100.00%) | 0 | 0/0 | 0/0 | 46/46 | 44/44 | KeybindAtlas-v1.4.0-mc1.21.1-neoforge.jar |
| `create_radar` | complete | 174/174 (100.00%) | 0 | 17/17 | 6/6 | 17/17 | 0/0 | create_radar-0.4.9.4-1.21.1.jar |
| `pamhc2trees` | complete | 158/158 (100.00%) | 0 | 100/100 | 57/57 | 0/0 | 0/0 | pamhc2trees-NEOFORGE-1.21.1-1.0.9.jar |
| `create_confectionery` | complete | 150/150 (100.00%) | 0 | 27/27 | 60/60 | 45/45 | 0/0 | create-confectionery1.21.1_v1.1.3b.jar |
| `mcwlights` | complete | 146/146 (100.00%) | 0 | 140/140 | 0/0 | 0/0 | 0/0 | mcw-lights-1.1.5-mc1.21.1neoforge.jar |
| `immersive_aircraft` | complete | 133/133 (100.00%) | 0 | 0/0 | 29/29 | 14/14 | 20/20 | immersive_aircraft-1.5.2+1.21.1-neoforge.jar |
| `createmobfarming` | complete | 123/123 (100.00%) | 0 | 4/4 | 2/2 | 2/2 | 5/5 | createmobfarming-1.1.0.jar |
| `tectonic` | complete | 109/109 (100.00%) | 0 | 0/0 | 0/0 | 43/43 | 58/58 | tectonic-3.0.28-neoforge-21.1.jar |
| `createoreexcavation` | complete | 105/105 (100.00%) | 0 | 6/6 | 8/8 | 16/16 | 9/9 | createoreexcavation-1.21-1.6.8-kjs181patch.jar |
| `tab_organizer` | complete | 91/91 (100.00%) | 0 | 0/0 | 0/0 | 35/35 | 56/56 | tab_organizer-2.4.0-neoforge.jar |
| `betterdungeons` | complete | 86/86 (100.00%) | 0 | 0/0 | 0/0 | 38/38 | 0/0 | YungsBetterDungeons-1.21.1-NeoForge-5.1.4.jar |
| `betterdays` | complete | 83/83 (100.00%) | 0 | 0/0 | 0/0 | 41/41 | 2/2 | betterdays-1.21.1-3.3.6.3-NEOFORGE.jar |
| `comforts` | complete | 83/83 (100.00%) | 0 | 44/44 | 0/0 | 19/19 | 15/15 | comforts-neoforge-9.0.5+1.21.1.jar |
| `emotecraft` | complete | 73/73 (100.00%) | 0 | 0/0 | 0/0 | 15/15 | 4/4 | emotecraft-for-MC1.21.1-2.4.12-neoforge.jar |
| `ae2additions` | complete | 68/68 (100.00%) | 0 | 5/5 | 58/58 | 0/0 | 1/1 | AEAdditions-1.21.1-6.0.2.jar |
| `copycats` | complete | 67/67 (100.00%) | 0 | 46/46 | 2/2 | 16/16 | 0/0 | copycats-3.0.9+mc.1.21.1-neoforge.jar |
| `interiors` | complete | 65/65 (100.00%) | 0 | 53/53 | 0/0 | 10/10 | 0/0 | interiors-0.6.1 v3.jar |
| `create_hypertube` | complete | 64/64 (100.00%) | 0 | 9/9 | 4/4 | 3/3 | 2/2 | create_hypertube-0.6.0-NEOFORGE.jar |
| `create_central_kitchen` | complete | 63/63 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 0/0 | create-central-kitchen-2.6.2.jar |
| `bettermineshafts` | complete | 60/60 (100.00%) | 0 | 0/0 | 0/0 | 29/29 | 0/0 | YungsBetterMineshafts-1.21.1-NeoForge-5.1.1.jar |
| `amcdelight` | complete | 53/53 (100.00%) | 0 | 0/0 | 52/52 | 0/0 | 0/0 | amcdelight-1.0.1-neoforge+1.21.1.jar |
| `treephysics` | complete | 52/52 (100.00%) | 0 | 0/0 | 0/0 | 22/22 | 25/25 | treephysics-neoforge-1.21.1-2.4.jar |
| `cloth-config2` | complete | 49/49 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 2/2 | cloth-config-15.0.140-neoforge.jar |
| `offroad` | complete | 49/49 (100.00%) | 0 | 3/3 | 5/5 | 18/18 | 0/0 | create-aeronautics-bundled-1.21.1-1.3.2.jar |
| `whitenoise` | complete | 49/49 (100.00%) | 0 | 0/0 | 0/0 | 14/14 | 0/0 | betterdays-1.21.1-3.3.6.3-NEOFORGE.jar |
| `createtree` | complete | 48/48 (100.00%) | 0 | 0/0 | 0/0 | 2/2 | 27/27 | createtree-3.2.3.jar |
| `create_aero_radar` | complete | 46/46 (100.00%) | 0 | 4/4 | 40/40 | 0/0 | 0/0 | create_aero_radar-0.1.1-1.21.1.jar |
| `createtailwind` | complete | 44/44 (100.00%) | 0 | 1/1 | 0/0 | 12/12 | 27/27 | CreateTailwind-1.0.0+1.21.1-neoforge.jar |
| `naturescompass` | complete | 44/44 (100.00%) | 0 | 0/0 | 1/1 | 0/0 | 0/0 | NaturesCompass-1.21.1-3.4.0-neoforge.jar |
| `configuration` | complete | 40/40 (100.00%) | 0 | 0/0 | 0/0 | 8/8 | 8/8 | configuration-neoforge-1.21.1-3.1.1.jar; gtceu-1.21.1-7.0.2.jar |
| `bellsandwhistles` | complete | 39/39 (100.00%) | 0 | 36/36 | 0/0 | 0/0 | 0/0 | bellsandwhistles-0.4.7-1.21.1.jar |
| `accdelight` | complete | 37/37 (100.00%) | 0 | 0/0 | 36/36 | 0/0 | 0/0 | accdelight-1.0.0-neoforge+1.21.1.jar |
| `guideme` | complete | 36/36 (100.00%) | 0 | 0/0 | 1/1 | 2/2 | 2/2 | guideme-21.1.19.jar |
| `ironchest` | complete | 36/36 (100.00%) | 0 | 14/14 | 7/7 | 0/0 | 0/0 | ironchest-1.21-neoforge-16.0.7.jar |
| `sereneseasons` | complete | 36/36 (100.00%) | 0 | 1/1 | 2/2 | 0/0 | 0/0 | SereneSeasons-neoforge-1.21.1-10.1.0.3.jar |
| `gravestone` | complete | 33/33 (100.00%) | 0 | 1/1 | 1/1 | 2/2 | 28/28 | gravestone-neoforge-1.21.1-1.0.40.jar |
| `create_tsr` | complete | 32/32 (100.00%) | 0 | 0/0 | 1/1 | 7/7 | 5/5 | create_tsr-1.1.0.jar |
| `moderntrainparts` | complete | 32/32 (100.00%) | 0 | 7/7 | 20/20 | 0/0 | 0/0 | moderntrainparts-0.2.5-neoforge-mc1.21.1-cr6.0.10.jar |
| `create_radar_mobile_radars` | complete | 31/31 (100.00%) | 0 | 0/0 | 2/2 | 8/8 | 12/12 | create_radar_mobile_radars-1.0.0.jar |
| `resourcefulconfig` | complete | 28/28 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 0/0 | resourcefulconfig-neoforge-1.21-3.0.11.jar |
| `create_jetpack` | complete | 27/27 (100.00%) | 0 | 2/2 | 4/4 | 20/20 | 1/1 | create_jetpack-forge-5.2.1.jar |
| `mechanicals` | complete | 26/26 (100.00%) | 0 | 0/0 | 1/1 | 22/22 | 0/0 | mechanicals-1.21.1-1.1.6-kjs181patch.jar |
| `create_no_touching` | complete | 17/17 (100.00%) | 0 | 6/6 | 5/5 | 2/2 | 0/0 | create_no_touching-1.0.8Neo-Aeronautics.jar |
| `createendertransmission` | complete | 17/17 (100.00%) | 0 | 4/4 | 0/0 | 0/0 | 1/1 | createendertransmission-2.1.1-1.21.1.jar |
| `chestpreview` | complete | 16/16 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 16/16 | Chest-Preview-NeoForge-1.21.1-1.0.0.jar |
| `create_mechanical_extruder` | complete | 16/16 (100.00%) | 0 | 2/2 | 0/0 | 4/4 | 0/0 | create_mechanical_extruder-1.21.1-2.2.2-6.0.10-kjs181patch.jar |
| `sliceanddice` | complete | 16/16 (100.00%) | 0 | 3/3 | 1/1 | 1/1 | 1/1 | sliceanddice-4.3.4-neoforge.jar |
| `create_autonavigation` | complete | 14/14 (100.00%) | 0 | 1/1 | 0/0 | 0/0 | 12/12 | create-autonavigation-0.2.0.jar |
| `betterdeserttemples` | complete | 13/13 (100.00%) | 0 | 0/0 | 0/0 | 6/6 | 0/0 | YungsBetterDesertTemples-1.21.1-NeoForge-4.1.5.jar |
| `carryon` | complete | 12/12 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 2/2 | carryon-neoforge-1.21.1-2.2.6.13.jar |
| `aeronauticsplus` | complete | 11/11 (100.00%) | 0 | 10/10 | 0/0 | 0/0 | 0/0 | aeronauticsplus-0.2.0.jar |
| `betterjungletemples` | complete | 11/11 (100.00%) | 0 | 0/0 | 0/0 | 4/4 | 0/0 | YungsBetterJungleTemples-1.21.1-NeoForge-3.1.2.jar |
| `spruceui` | complete | 10/10 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 1/1 | lambdynamiclights-4.8.11+1.21.1.jar |
| `cbc_going_ballistic` | complete | 7/7 (100.00%) | 0 | 0/0 | 0/0 | 6/6 | 0/0 | cbc_going_ballistic-0.3.1.jar |
| `almostunified` | complete | 6/6 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 0/0 | almostunified-neoforge-1.21.1-1.4.2.jar |
| `custommusic` | complete | 6/6 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 6/6 | custommusic-1.21.1-1.0.1.jar |
| `locometal_armor` | complete | 6/6 (100.00%) | 0 | 1/1 | 1/1 | 0/0 | 0/0 | locometal_armor-1.1.0.jar |
| `betteradvancedtooltips` | complete | 5/5 (100.00%) | 0 | 0/0 | 0/0 | 5/5 | 0/0 | better-advanced-tooltips-2101.1.0-build.5.jar |
| `cosmeticarmorreworked` | complete | 5/5 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 2/2 | cosmeticarmorreworked-1.21.1-v1-neoforge.jar |
| `betterfortresses` | complete | 4/4 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 0/0 | YungsBetterNetherFortresses-1.21.1-NeoForge-3.1.5.jar |
| `betteroceanmonuments` | complete | 4/4 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 0/0 | YungsBetterOceanMonuments-1.21.1-NeoForge-4.1.2.jar |
| `betterwitchhuts` | complete | 4/4 (100.00%) | 0 | 0/0 | 0/0 | 1/1 | 0/0 | YungsBetterWitchHuts-1.21.1-NeoForge-4.1.1.jar |
| `ksyxis` | complete | 4/4 (100.00%) | 0 | 0/0 | 0/0 | 4/4 | 0/0 | Ksyxis-1.4.5.jar |
| `baguettelib` | complete | 3/3 (100.00%) | 0 | 1/1 | 1/1 | 0/0 | 0/0 | baguettelib-1.21.1-NeoForge-2.0.7.jar |
| `common_storage_lib` | complete | 3/3 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | common-storage-lib-neoforge-1.21.1-0.0.10.jar |
| `polymorph` | complete | 3/3 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | polymorph-neoforge-1.2.0+1.21.1.jar |
| `bigwater` | complete | 2/2 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | bigwater-1.2.0-neoforge+mc1.21.1.jar |
| `terrablender` | complete | 2/2 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | TerraBlender-neoforge-1.21.1-4.1.0.8.jar |
| `codxlib` | complete | 1/1 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | codxlib-1.6.1-neoforge+1.21.1.jar |
| `emiprofessions` | complete | 1/1 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 0/0 | EMIProfessions-neoforge-1.21.1-1.0.3.jar |
| `searchables` | complete | 1/1 (100.00%) | 0 | 0/0 | 0/0 | 0/0 | 1/1 | Searchables-neoforge-1.21.1-1.0.2.jar |

## Файлы детального аудита

- `artifacts.csv` — все проверенные JAR, SHA-256, mod IDs и найденные языки.
- `namespaces.csv` — точное покрытие каждого namespace и каждой категории.
- `missing_keys.csv` — каждый отсутствующий ключ с английским значением и JAR-источником.
- `orphan_pack_keys.csv` — ключи RU-pack, которых нет в текущих `en_us` (устаревшие или ошибочные).
- `suspicious_values.csv` — совпавшие с English, без кириллицы или с повреждёнными placeholders.
- `mod_metadata.csv` — статические названия модов, которые resource pack не локализует.
- `catalogue_conflicts.csv` — конфликтующие определения одного ключа между JAR.
- `duplicate_keys.csv` — повторяющиеся ключи внутри одного lang-файла.
- `parse_errors.csv` — ошибки чтения каталогов (должен быть пустым кроме заголовка).

## Методика и ограничения

- Покрытие считается строго по ключам: `pack ru_ru` → встроенный `ru_ru` → missing.
- Совпадение русского значения с английским помечается на ручную проверку, но не автоматически считается ошибкой: аббревиатуры, бренды и имена могут быть корректны.
- Наличие кириллицы — только индикатор качества; оно не заменяет редакторскую проверку.
- Динамически составляемые строки без lang-ключей и текст внутри изображений/книг нужно проверять отдельно в игре.
