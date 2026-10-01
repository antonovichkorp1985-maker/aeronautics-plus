# Create add-on Ponder audit

Exact audit of installed Create add-on JAR catalogues, nested JARs and the
effective Russian values after applying `localization/ru-pack` overlays.
Base Create is excluded and audited separately in `CREATE_6_0_10.md`.

## Totals

- top-level JARs inspected: **46**
- add-on namespaces with Ponder keys: **29**
- Ponder keys: **2160**
- missing effective Russian values: **169**
- values identical to English: **127**
- other Latin-only values: **1**
- mixed Cyrillic/Latin values requiring editorial review: **174**
- placeholder mismatches: **0**
- conflicting duplicate catalogue keys: **0**
- archive/JSON errors: **0**

`Identical` and `Latin-only` are review candidates rather than an automatic
claim that every value is wrong: abbreviations and product names may be valid.
Mixed-script values are listed because earlier machine-assisted overlays can
contain untranslated fragments even when the key itself is present.

## Per namespace

| Namespace | Ponder | Missing | Identical EN | Latin only | Mixed | Cyrillic | Neutral |
|---|---:|---:|---:|---:|---:|---:|---:|
| `aeronautics` | 97 | 0 | 0 | 0 | 0 | 97 | 0 |
| `cbcmoreshells` | 33 | 0 | 0 | 0 | 0 | 33 | 0 |
| `create_central_kitchen` | 55 | 0 | 26 | 0 | 8 | 21 | 0 |
| `create_connected` | 123 | 0 | 6 | 0 | 5 | 112 | 0 |
| `create_dragons_plus` | 35 | 0 | 24 | 0 | 0 | 11 | 0 |
| `create_hypertube` | 27 | 0 | 0 | 0 | 0 | 27 | 0 |
| `create_mechanical_extruder` | 6 | 0 | 0 | 0 | 0 | 6 | 0 |
| `create_mechanical_spawner` | 6 | 0 | 0 | 0 | 0 | 6 | 0 |
| `create_new_age` | 63 | 9 | 9 | 0 | 0 | 45 | 0 |
| `create_optical` | 86 | 34 | 0 | 0 | 1 | 51 | 0 |
| `create_radar` | 34 | 0 | 4 | 0 | 11 | 19 | 0 |
| `create_submarine` | 37 | 0 | 0 | 1 | 4 | 32 | 0 |
| `createaddition` | 50 | 0 | 0 | 0 | 4 | 46 | 0 |
| `createbigcannons` | 185 | 0 | 0 | 0 | 3 | 182 | 0 |
| `createdieselgenerators` | 55 | 0 | 0 | 0 | 0 | 55 | 0 |
| `createfirefightingadd` | 62 | 0 | 0 | 0 | 9 | 53 | 0 |
| `createmobfarming` | 31 | 0 | 0 | 0 | 1 | 30 | 0 |
| `createoreexcavation` | 21 | 0 | 0 | 0 | 0 | 21 | 0 |
| `createpropulsion` | 79 | 0 | 0 | 0 | 11 | 68 | 0 |
| `morepropulsion` | 25 | 0 | 3 | 0 | 20 | 2 | 0 |
| `nuclearcraftneohaul` | 1 | 0 | 1 | 0 | 0 | 0 | 0 |
| `offroad` | 15 | 0 | 0 | 0 | 1 | 14 | 0 |
| `powergrid` | 321 | 120 | 0 | 0 | 3 | 198 | 0 |
| `radiologistics` | 283 | 0 | 0 | 0 | 42 | 241 | 0 |
| `railways` | 40 | 0 | 0 | 0 | 0 | 40 | 0 |
| `ratatouille` | 32 | 6 | 0 | 0 | 0 | 26 | 0 |
| `simulated` | 227 | 0 | 0 | 0 | 4 | 223 | 0 |
| `sliceanddice` | 7 | 0 | 0 | 0 | 0 | 7 | 0 |
| `tfmg` | 124 | 0 | 54 | 0 | 47 | 23 | 0 |

## Завершённый ручной проход `createfirefightingadd`

В рабочем RU-pack вручную вычитаны все **306/306** значений namespace, а не только
Ponder: названия, интерфейсы конфигуратора, сообщения рукавов и лестницы, справочник
пожарного, подсказки предметов, субтитры и 62 строки анимаций-инструкций.

Для Ponder закрыты все 34 английских и 28 смешанных значения. Оставшиеся девять
формально mixed-script строк проверены вручную: латиница в них относится только к
названиям `Create`/`Create: Dragons Plus`, точному имени параметра
`HighPressurePump.amplificationMultiplier` и обозначениям точек `A/B/C`.
Единицы нагрузки `SU` переведены официальным термином Create `ЕН`. Оставшаяся
латиница не является непереведёнными английскими фрагментами. Placeholders и
число ключей сохранены.

## Завершённый ручной проход `radiologistics`

В рабочем RU-pack вычитаны все **338/338** значений `CreateRadiologistics-1.1.2`:
названия и подсказки блоков, настройка компьютерной сети, редактор узлов,
совместимые порты, радиосвязь, память, звук, экраны, шлем пилота, сервомеханизмы,
оптический датчик и интеграция с большими орудиями.

В Ponder закрыты все **53** английских и **114** смешанных машинных значения;
итог — **283/283** строк без пропусков и совпадений с английскими предложениями.
Оставшиеся 42 формально mixed-script строки проверены вручную. Латиница в них —
это марки входов и координат `A/B/X/Y/Z`, клавиши `W/Esc`, технические форматы
`RGB/JSON/URL/MP3/WAV`, сокращение `CBC` и названия модов Create. Английских
фрагментов интерфейса или пояснений среди них нет. Placeholders и управляющие
коды `§` совпадают с точным каталогом JAR.

## Interpretation

Namespaces with missing or English-identical scene text are the first
translation queue. Mixed-script values are the second queue and must be
reviewed manually rather than accepted as translated merely because they
contain Cyrillic characters. Full row-level evidence is in
`CREATE_ADDON_PONDER.csv`.
