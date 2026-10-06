# Aeronautics Plus RU Pack v0.27-test

## Create addons exact-JAR pass and previous batches

The exact Create addon batch is now fully covered: **1,755/1,755** effective language keys across Create Connected, Create Optical, Create Ratatouille, Create Big Cannons and Create Diesel Generators, with zero missing keys and zero placeholder mismatches. This includes the animation-adjacent captions, GUI labels, configuration text and tooltips found in these JARs.

The AE2 ecosystem batch, Chisels & Bits, Power Grid, Ok Zoomer, Xaero, PneumaticCraft, LDLib2, Immersive Engineering and Mekanism exact-JAR passes remain complete.

## AE2 ecosystem exact-JAR pass and previous batches

The exact AE2 ecosystem batch is now fully covered: **2,311/2,311** effective language keys across AE2, ExtendedAE, AE2WTLib, AE2WTLib API and AE Additions, with zero missing keys and zero placeholder mismatches. This closes the remaining ordinary GUI/configuration labels found in the downloaded batch; GuideME resources were already integrated separately.

Chisels & Bits, Power Grid, Ok Zoomer, Xaero, PneumaticCraft, LDLib2, Immersive Engineering and Mekanism exact-JAR passes remain complete.

## Chisels & Bits, Power Grid, Ok Zoomer and previous exact-JAR passes

The exact current JAR batch is now fully covered: **1,525/1,525** effective language keys across Chisels & Bits, Power Grid and Ok Zoomer, with zero missing keys and zero placeholder mismatches. This closes 250 Chisels & Bits keys, 164 Power Grid keys and 159 Ok Zoomer keys. Technical IDs and official mod names remain unchanged.

Xaero, PneumaticCraft, LDLib2, Immersive Engineering and Mekanism exact-JAR passes remain complete.

## Xaero maps, PneumaticCraft and previous exact-JAR passes

The exact current Xaero/PneumaticCraft JAR batch is now fully covered: **3,363/3,363** effective language keys across `xaeroworldmap`, `xaerominimap`, `xaerolib`, `xaerobetterpvp` and `pneumaticcraft`, with zero missing keys and zero placeholder mismatches. This closes 277 PneumaticCraft keys and 436 Xaero-related keys in the audited batch. GUI terminology, map controls, tooltips and configuration labels were translated with the technical glossary; IDs and official mod names remain unchanged.

LDLib2, Immersive Engineering and Mekanism exact-JAR passes remain complete.

## LDLib2 and previous exact-JAR passes

The exact `ldlib2-neoforge-1.21.1-2.2.41-all.jar` is now fully covered: **703/703** effective language keys, zero missing keys and zero placeholder mismatches. The 696 missing editor/configuration strings were translated with the machine draft plus a dedicated GUI terminology glossary; technical IDs remain unchanged and the result is marked as a test draft for editorial review.

Immersive Engineering and Mekanism exact-JAR passes remain complete.

## Immersive Engineering and Mekanism exact-JAR passes

The exact `ImmersiveEngineering-1.21.1-12.4.2-194.jar` is now fully covered: **1,952/1,952** effective language keys, zero missing keys, zero placeholder mismatches and zero catalogue conflicts. This includes the GUI, tooltips, machines, tools, manual-related labels and integrations. The draft uses the exact translation memory plus the available Russian translation source and a conservative technical glossary; official mod names and IDs remain unchanged, so it still needs editorial review before being called final.

Mekanism, Mekanism Generators and Mekanism Tools remain at **4,403/4,403** effective keys.

## Mekanism configuration pass and exact non-standard resources

The exact Drive JARs `Mekanism`, `MekanismGenerators` and `MekanismTools` 10.7.19.85 were audited separately. Their **4,403/4,403** effective language keys now have Russian values, including the remaining gear, general, world-generation, tier and storage configuration labels. Placeholders remain valid and the exact three-JAR audit reports zero missing keys and zero placeholder mismatches.

The configuration batch combines the official current Mekanism Russian catalogue with a conservative Russian technical glossary for the configuration-only strings that are not present in the upstream catalogue. Technical identifiers, Forge energy names and official mod names are preserved.

## Exact active-set recheck, Ponder and non-standard resources

This build integrates the offline translation pass for **560 non-standard resources** from the exact active JAR set: AE2/ExtendedAE/AE2WTLib GuideME, Immersive Engineering manual, PneumaticCraft Patchouli, Ad Astra, Productive Bees, Alex's Caves mini-game text, AE2 Additions, Ender IO, Citadel, Ksyxis and TIS-3D documentation. Structured JSON, GuideME front matter, resource IDs, links, code fences and placeholders were preserved and all 364 JSON resources parse successfully.

The machine-assisted prose is a first draft and should receive editorial review before being called final. Official mod names remain unchanged.

## Exact active-set recheck and Create: Deep Seas 3.3.0

The current Drive `mods` folder was rechecked as the source of truth: **205 active JARs**. This pack does not modify any installed JAR, including PMWeather.

This maintenance build adds and verifies:

- all **40** previously missing Create: Deep Seas 3.3.0 Ponder scene strings;
- the missing `key.categories.ponder` catalogue entry, while keeping the official name `Ponder` unchanged;
- the current submarine tutorials for arresting hooks, ballast vents, decompression chambers, the shaft-driven electrolyzer, onboard computer and sonar;
- the one TFC hydration overlay with its two `%s` placeholders preserved;
- a valid Russian overlay for the malformed upstream Locometal Armor development catalogue.

The exact Ponder audit now reports **0 missing**, **0 placeholder mismatches** and **0 catalogue conflicts** across 3,313 add-on Ponder keys. The sole remaining audit error is the malformed upstream English `locometal_armor` catalogue; the RU overlay itself is valid and the original JAR remains untouched.

The full exact-set and non-lang audit scope is documented in `docs/localization-audit/FULL_EXACT_MODSET_2026-10-05.md`. The integrated 560-resource draft closes the largest non-lang queue; remaining hard-coded Java strings and any residual non-standard files stay in the review audit until verified against the exact runtime.

## Mekanism 10.7.19.85 — первая партия подробных настроек

После полного закрытия Mekanism: Generators и Mekanism: Tools в v0.16-test
переведена первая отдельная партия подробных параметров базового Mekanism:

- **73/73** оставшихся клиентских параметра: положения окон, частицы, QIO,
  отрисовка, звуки и непрозрачные передатчики;
- **10/10** общих клиент-серверных параметров: единицы энергии и температуры,
  радиационные таймеры, праздники и копирование данных блока;
- **3/3** параметра запуска для экипировки;
- **45/45** параметров интеграции ProjectE: обработчик содержимого и 14 типов
  сопоставления рецептов для вычисления EMC.

Всего в этой версии добавлен перевод **131** конфигурационной строки. Полностью
закрыты группы `client`, `common`, `startup` и `projecte`. Итоговое покрытие
базового Mekanism выросло до **2 232/3 239**. Оставшиеся **1 007** строк относятся
только к шести серверным/игровым группам: `world`, `gear`, `tier`, `general`,
`usage` и `storage`.

Суммарно три модуля Mekanism теперь покрыты на **3 396/4 403** ключа;
несовпадений placeholders — **0**. В исходном дереве v0.17-test находится
**36 775** языковых значений в **89** пространствах имён плюс 21 исправленный
файл TFC Field Guide.

Подробный воспроизводимый отчёт:
`docs/localization-audit/MEKANISM_10_7_19_85.md`.

---

# Aeronautics Plus RU Pack v0.16-test

## Mekanism 10.7.19.85

Перевод сверён с точными установленными JAR `Mekanism`, `Mekanism: Generators`
и `Mekanism: Tools` версии 10.7.19.85. Все три JAR полностью проверены как ZIP.
Подходящие обновления синхронизированы из официальной ветки `l10n_1.21.x`,
commit `c7f6a2b2af787bbfc65ab8f680dd3f67044d1978`, но сомнительные значения не
переносились вслепую.

В v0.16-test:

- добавлено покрытие **1 271** ранее отсутствовавшего ключа;
- редакторски заменены **108** существовавших значений;
- переведены все 156 отсутствовавших названий item tags;
- закрыты игровые сообщения, описания машин, QIO, телепортер, словарь,
  мультиблоки и химические операции;
- полностью переведены 128 отсутствовавших подробных параметров
  `Mekanism: Generators`;
- полностью переведены 419 отсутствовавших подробных параметров материалов
  `Mekanism: Tools`;
- исправлены ошибки Crowdin о топоре/мотыге в параметрах мультиинструмента,
  посторонние мемные и сленговые фразы, терминология QIO/Jade и научные термины
  `хольраум`/`DT-топливо`;
- официальные названия всех трёх модулей оставлены без перевода;
- несовпадений `%1$s`, printf-параметров и `{...}` — **0**.

Итоговое effective-покрытие точных JAR:

- Mekanism: **2 101/3 239**; оставшиеся 1 138 строк — только подробные параметры
  `configuration.mekanism.*`, которые будут переводиться следующими партиями;
- Mekanism: Generators: **364/364**;
- Mekanism: Tools: **800/800**.

В исходном дереве v0.16-test находится **36 644** языковых значения в **89**
пространствах имён плюс 21 исправленный файл TFC Field Guide.

Подробный воспроизводимый отчёт:
`docs/localization-audit/MEKANISM_10_7_19_85.md`.

---

# Aeronautics Plus RU Pack v0.15-test

## TerraFirmaCraft 4.2.11 и сообщения запуска

Проверен точный `TerraFirmaCraft-NeoForge-1.21.1-4.2.11.jar` из актуальной
Drive-папки `mods` (SHA-256
`1565efa96f090bec8e8c12f54ae222d3f9321ef7fad2ea3af752fb9198e803df`). JAR
исправен как ZIP. Встроенный русский каталог покрывает все **10 047/10 047**
игровых ключей, однако первичная вычитка обнаружила видимые английские остатки.

В v0.15-test добавлено **112** точечных исправлений `tfc`:

- вкладки творческого режима, подсказки, Jade, JEI и EMI;
- верблюды, броненосцы, яйца призыва и серверные параметры животных;
- обсидиановые инструменты и рабочие части;
- ящики и листья всех 20 пород древесины;
- корзина, цветочный черенок, ламповое топливо и соседние редакторские ошибки.

После наложения pack осталось 18 совпадающих значений `EN=RU`; это только
TerraFirmaCraft, авторы, числовые форматы, обозначения температур и служебные
шаблоны. Несовпадений placeholders — **0**.

В русской Field Guide присутствуют все **88/88** содержательных файлов. В 21 из
них переведены **60** английских названий и абзацев, а также исправлены **15**
русских опечаток и редакторских ошибок. Сохранены все динамические ссылки,
клавиши и параметры Patchouli. Английские 13 файлов `templates/*` являются
общими техническими шаблонами, а не отсутствующими русскими статьями.

По присланному скриншоту дополнительно переведены предупреждение о сломанных
тегах рецептов Mekanism и сообщение загрузки Hbm's Nuclear Tech Mod.
Предупреждение PMWeather об Iris уже было переведено. Сообщения Ender IO об
alpha-версии и Distant Horizons о Chunky/G1 GC зашиты в Java-код и не могут быть
заменены resource pack.

В исходном дереве v0.15-test находится **35 267** языковых значений в **87**
пространствах имён плюс 21 исправленный файл Field Guide.

Подробный воспроизводимый отчёт: `docs/localization-audit/TFC_4_2_11.md`.

---

# Aeronautics Plus RU Pack v0.14-test

## Повторный аудит всех Create-аддонов

Повторно проверены все **2 160** Ponder-строк 46 установленных JAR Create-аддонов.
В сравнении с v0.13-test:

- закрыты все **169** отсутствовавших строк;
- переведены ещё **112** полностью английских значений;
- исправлены **80** машинно-смешанных фраз;
- вручную вычитаны `powergrid`, `tfmg`, `create_optical`, `create_new_age`,
  `create_central_kitchen`, `create_dragons_plus`, `create_connected`,
  `create_radar`, `morepropulsion`, `ratatouille` и меньшие интеграции;
- названия самих модов оставлены в оригинальном написании.

Итог Ponder: **2 160/2 160** без пропусков. Оставшиеся консервативные срабатывания
аудита относятся только к названиям модов, клавишам, формулам, координатам,
единицам измерения, техническим форматам и служебным числовым строкам.

В исходном дереве v0.14-test находится **35 154** русских значения в **86**
пространствах имён.

## Create: Fire Fighting Additions

Полностью вручную вычитаны **306/306** строк `createfirefightingadd-0.2.3-beta`:

- названия блоков, предметов и механизмов;
- интерфейсы конфигуратора насадок и модификатора жидкостей;
- пожарные рукава, соединители, кронштейны и выдвижная лестница;
- справочник пожарного, приглашения в команду и учёт тушений;
- подсказки предметов, сообщения об ошибках и субтитры;
- все **62/62** строки Ponder вместо английских и машинно-смешанных фраз.

Сохранены 306 ключей, все `%s` и форматирование Create. Девять Ponder-значений с
латиницей проверены вручную: в них остались только имена Create, точный ID настройки
и обозначения точек A/B/C. Единицы нагрузки переведены официальным сокращением `ЕН`.

## Create: Radiologistics

Полностью вручную вычитаны **338/338** строк `CreateRadiologistics-1.1.2`:

- радиопередатчики, антенны, генератор помех, память и звуковой модуль;
- редактор компьютерной сети и единая русская терминология всех узлов;
- прозрачный экран, шлем пилота, кассеты и экранная индикация;
- сервомотор, сервоподшипник и умный оптический датчик;
- интеграции Create Radars и Create Big Cannons;
- все **283/283** строки Ponder, включая 53 английских и 114 машинно-смешанных.

С точным JAR совпадают все ключи, `%d`, `%s`, подчёркивания и управляющие коды `§`.
Оставшиеся 42 Ponder-значения с латиницей вручную проверены: в них находятся только
имена модов, обозначения координат и входов, клавиши, форматы файлов и технические
сокращения; непереведённых английских фраз нет.

---

# Aeronautics Plus RU Pack v0.13-test

## Полное покрытие Create 6.0.10

Проверен точный каталог установленного `create-1.21.1-6.0.10.jar`, включая
анимации-инструкции Ponder и описания предметов/механизмов.

- Ponder уже имеет полный встроенный русский каталог: **1 053/1 053** ключей;
  английских предложений среди effective-значений не осталось;
- RU-pack дополнен всеми 20 отсутствовавшими ключами Create: 13 видимыми
  строками и 7 пустыми служебными продолжениями инструкций схематики;
- переведены настройки синхронизации кладовщика с просмотрщиком рецептов,
  клавиши-модификаторы и тег пшеничного теста;
- итоговое effective-покрытие Create: **3 639/3 639**;
- описания `.description`/`.desc`: **168/168**;
- несовпадений форматных параметров: **0**.

Проверка закреплена скриптом `scripts/validate_create_localization.py` и CI.
RU-pack содержит **34 912** записей. Опубликованный ZIP: Drive ID
`1E813rFsIw6MbPaTgviJLVDKaT7gPBEfu`, SHA-256
`773eebe4775770c38dd2a69e8ecd79667100531dfa3afb6f72ce45a08cc4f56f`.
Подробный отчёт: `docs/localization-audit/CREATE_6_0_10.md`.

---

# Aeronautics Plus RU Pack v0.12-test

## Sable 2.0.5 и обновлённая сборка

Drive-папка актуальной сборки проверена 30.09.2026: в `mods` находится **209
активных JAR**. По сравнению с зафиксированным аудитом 207 JAR добавлены
`create_submarine-2.2.4.jar` и `FaradayEarsMod-1.21.1-1.3.0.jar`; Aeronautics Plus
заменён с 0.2.0 на `0.2.2-dev`. Временный файл `fzzy_config-...jar.ZBYHVE` не
считается активным модом.

В v0.12-test вручную переведены все **128/128** ключей namespace `sable`:

- команды создания, разделения, телепортации и диагностики подуровней;
- селекторы, модификаторы, ошибки аргументов и принудительная загрузка;
- настройки частоты физики, уведомления восстановления и камеры конструкций;
- группы сил: гравитация, сопротивление, подъёмная и движущая силы;
- сохранены все `%s`, `%d`, `%1$s` и числовые форматы; несовпадений placeholders — 0.

`FaradayEarsMod` не содержит `assets/*/lang/en_us.json`: его видимый GUI уже
зашит в код на русском языке, поэтому resource pack для него создать нельзя.
Вложенный редактор Veil (`veil`, 174 разработческих строки) остаётся отдельной
очередью. Полный namespace Create Deep Seas из v0.11-test сохранён без изменений.

RU-pack содержит **34 892** записи. Опубликованный ZIP: Drive ID
`1-_vzoEFbpNVUdtLDz7U_QkEdYafGYFkg`, SHA-256
`91009b1f191ead862858c03ac0329876874de8b685664813fa553405c9490a70`.
На каталоге 209 JAR расчётное строгое покрытие после добавления Deep Seas и Sable — **68 999/77 325 (89,23%)**;
полный повторный аудит будет пересобран при следующем полном зеркале JAR, а
дельта новых файлов уже проверена напрямую.

---

# Aeronautics Plus RU Pack v0.11-test

## Create Deep Seas 2.2.4

Добавлена полная поддержка установленного мода Create Deep Seas:

- `create_submarine`: **191/191** effective-ключей вместо 149/191 во встроенном переводе;
- `create_abyss`: **12/12**;
- переведены барометр, глубина и состояние корпуса, декомпрессионная камера,
  параметры кислорода, мощность гребного винта и группы сил Sable;
- исправлены оставшиеся английские строки Ponder и названия бразильской
  светящейся акулы;
- placeholders проверены, несовпадений нет.

RU-pack содержит 34 764 записи. Полный аудит сборки остаётся зафиксирован на 207 JAR
до появления `create_submarine-2.2.4.jar` в Drive-папке `mods`; предварительная
проверка официального каталога сохранена как исторический отчёт в
`docs/archive/localization-audit/DEEP_SEAS.md`.

---

# Aeronautics Plus RU Pack v0.10-test

## Основной Burnt завершён

Namespace `burnt` доведён до **584/584** ключей. После предметов, сущностей и
настроек из v0.9-test вручную добавлены все **382 названия блоков**:

- четыре стадии: тлеющие, раскалённые, пылающие и обгоревшие варианты;
- закопчённые породы древесины и строительные блоки;
- двери, люки, заборы, калитки, ступеньки, плиты и вертикальные плиты;
- посевы, листва, бамбук, мох, терракота, флаги и служебные эффекты.

Проверены русские род и число, в новых значениях нет английских остатков.
На неизменившемся каталоге 207 JAR теперь покрыто **68 826/77 122 ключа
(89,24%)**, отсутствует 8 296. RU-pack содержит 34 719 записей, из них 34 381
соответствуют текущим `en_us`, а 338 остаются устаревшими/лишними.

---

# Aeronautics Plus RU Pack v0.9-test

Продолжение ручного перевода по результатам полного аудита 207 активных JAR.

## Новый проход: основной мод Burnt

В namespace `burnt` вручную добавлены **202/584** отсутствовавших строки:

- 72/72 названия предметов;
- 4/4 названия сущностей;
- 52/52 подсказки настроек;
- 74 сообщения, субтитра, заголовка и параметра конфигурации;
- сохранены все `%1$s`, `%2$s` и `%3$s`, совпадений с английским и значений без
  кириллицы среди новых строк нет.

Остаются 382 названия блоков. Они выделены в следующую отдельную партию, потому
что повторяющиеся стадии горения и род русских названий требуют ручной проверки,
а не механической замены слов.

На неизменившемся английском каталоге сборки теперь покрыто **68 444/77 122
ключа (88,75%)**, отсутствует 8 678. RU-pack содержит 34 337 записей, из них
33 999 соответствуют текущим `en_us`, а 338 остаются устаревшими/лишними.

---

# Aeronautics Plus RU Pack v0.8-test

Это первый пакет исправлений после **полного воспроизводимого аудита 207 активных JAR**
сборки. Проверялись реальные каталоги `assets/*/lang/en_us.json` и встроенные
`ru_ru.json`, в том числе внутри вложенных Jar-in-Jar, а затем поверх них
накладывался этот resource pack.

## Итог аудита после исправлений v0.8-test

- 207/207 JAR корректны как ZIP;
- найдено 196 пространств имён и 77 122 английских ключа;
- ключами покрыто 68 242 (88,49%), отсутствует 8 880;
- из 34 135 ключей самого RU-pack с текущими JAR совпадают 33 797, ещё 338
  устарели или относятся к другой версии и вынесены в отдельную таблицу;
- полностью покрыто 108 пространств, частично — 66, без русского ключа — 22;
- в исходных JAR найдено 157 повторяющихся JSON-ключей (в основном TFMG);
- 7 043 значения совпадают с English и требуют ручной редакторской проверки
  (часть из них — допустимые бренды, аббревиатуры и технические обозначения);
- после отдельного прохода исправлено **61 несовпадение форматных параметров**
  `%s`, `%d`, `%1$s`; контрольный аудит показывает **0** таких ошибок.

Подробные таблицы этого исторического полного аудита сохранены в
`docs/archive/localization-audit/full-audit-v0.10/`.

## Исправления v0.8-test

### Chisels & Bits

- переведены все 19 названий блоков и все 29 названий предметов;
- переведены обычные и многострочные названия режимов долота;
- исправлены показанные строки: `Stone Chisel` → `Каменное долото`,
  `Chisel Mode: Cube (Single Bit)` → `Режим долота: Куб (Один бит)`;
- переведены сообщения совместимости блоков, измерения, отмена/повтор и основные
  действия кругового меню;
- текущее строгое покрытие: **163/413 ключей (39,47%)**. Остальные 250 ключей —
  в основном длинная справка, настройки и экраны — остаются в очереди ручного
  перевода и не скрываются за заявлением о «полной русификации».

### Aeronautics: Winds & Weather

- добавлены 10 отсутствовавших новых ключей;
- исправлены `Cotton Seeds` / смешанное `Хлопок Seeds` → `Семена хлопка`;
- вручную переведены оставшиеся английские условия и подсказки парусов, реи,
  руля, киля, анемометра, флюгера и планера;
- effective-покрытие текущего JAR `aeronauticswinds-1.3.1.jar`: **297/297**, без
  дословно оставшихся английских значений.

Строка `Monook Seeds`/`Moonoak Seeds` со скриншота **не существует** ни среди
ключей, ни среди ресурсов именно JAR 1.3.1 из папки сборки; в нём есть только
`Cotton Seeds`. Это означает расхождение фактически запущенной версии или
динамически созданную строку. Для точного исправления нужен повторный скриншот
с расширенной подсказкой/F3+H либо JAR из фактически запускаемого экземпляра.

### Create: No Touching

- вручную отредактированы все 17 lang-ключей: блоки, предметы, подсказки,
  субтитры и вкладка;
- исправлены смешанные фразы `железный Fist`, `Ceramic Пластина` и английские
  инструкции;
- надпись принадлежности моду **Create: No Touching** берётся напрямую из
  `META-INF/neoforge.mods.toml`. Обычный resource pack не может её заменить;
  для этого потребуется отдельный кодовый патч либо изменение самого мода.

### Безопасность форматирования

Исправлены несовпадающие параметры в AE2, Alex's Caves, Create, Create Optical,
Create Radar, Ex Compressum, ExtendedAE, FTB Quests/Teams, GTCEu, Immersive
Engineering, Mekanism, NuclearCraft Neohaul, PneumaticCraft, Simulated и
ZeroCore. Это предотвращает сырые `%с`, пропавшие значения и ошибки форматирования.

---

# Aeronautics Plus RU Pack v0.7-test

Изменения относительно v0.6:

- полностью вручную переведены 180 строк Burnt Additions;
- вручную исправлены названия, характеристики, компоненты и подсказки торпед CBC Military Supplement;
- удалены смешанные фразы наподобие `High-Explosive Short дальность Torpedo`;
- сохранены все ключи и форматные параметры `%s` исходных модов.

Остальная часть CBC Military Supplement ещё проходит ручной аудит.

---

# AeronauticsPlus-RU-Pack v0.5 — отчёт

Тип: resourcepack. Класть в `.minecraft/resourcepacks/` и включить поверх остальных.

## Что нового в v0.5
Полный **ручной** перевод «с нуля» (не автоглоссарий) для 13 модов, у которых не было встроенного ru_ru:
- **pmweather** (ProtoManly Weather): все 222 ключa — блоки (Радиокупол, Метеозонд, метеостанция WSR-88D, вся серия «Укреплённый … бетон/сухой бетон» ×32, «Тлеющий/Обугленный», «Опалённый»), предметы с §-подсказками, 12 месяцев, все настройки конфига. Попутно найден баг автора мода: `scalded_concrete_slab` в оригинале называется «Stairs» — в переводе исправлено как «Плита».
- **pamhc2crops**: все 320 ключей — 85+ культур по схеме «Агава» (предмет) / «Агава (культура)» (блок) / «Семена агавы»; грибы → «Споры …»; 6 садов («Засушливый/Морозный/Тенистый/Болотистый/Тропический/Ветреный сад»); печёное/жареное; горячие напитки.
- **pamhc2foodcore**: все 203 ключa — пончики/желе/тосты/соки/маффины/пироги/мороженое/смузи/йогурты ×8 вкусов, сэндвичи, бургеры, лапша-супы, закрытые пироги, вяленое мясо, инструменты (Форма для выпечки, Разделочная доска, Мясорубка, Соковыжималка, Скалка, Ковш, Сковорода…), базовые ингредиенты.
- **pamhc2trees**: все 158 ключей — 48 фруктовых деревьев (Кандийский орех, Пау-пау, Саусеп, Питахайя, Карамболь, Маракуйя…), саженцы («Саженец финиковой пальмы», «Саженец перечного дерева»…), плоды «X (плод)», жареные орехи, кленовый сироп.
- **ae2additions, accdelight, amcdelight, biomesoplenty, create_confectionery, createmobfarming, emi (весь UI), farmersdelight, immersiveengineering** — ранее в этой же серии правок.

## Состав пакета (54 модида, ~33 300 ключей)
| modid | en_us | встроенный ru_ru | ru из v0.3 | итог ru_ru |
|---|---:|---:|---:|---:|
| accdelight | 37 | 0 | 0 | 37 |
| ae2additions | 68 | 0 | 0 | 68 |
| agricraft | 449 | 447 | 0 | 449 |
| alexscaves | 1725 | 1705 | 0 | 1725 |
| alexsmobs | 1085 | 1075 | 0 | 1086 |
| amcdelight | 53 | 0 | 0 | 53 |
| biomesoplenty | 33 | 0 | 0 | 33 |
| cannoncompressedarmor | 1052 | 0 | 1052 | 1052 |
| cbcmoreshells | 505 | 0 | 505 | 505 |
| chipped | 7265 | 0 | 0 | 7265 |
| chisel | 2932 | 0 | 0 | 2932 |
| create_aero_radar | 46 | 0 | 46 | 46 |
| create_confectionery | 150 | 0 | 0 | 150 |
| create_connected | 422 | 357 | 0 | 422 |
| create_hypertube | 64 | 30 | 0 | 64 |
| create_no_touching | 17 | 0 | 18 | 18 |
| create_radar | 174 | 73 | 178 | 178 |
| create_radar_mobile_radars | 31 | 0 | 31 | 33 |
| create_tsr | 32 | 0 | 32 | 32 |
| createendertransmission | 17 | 0 | 0 | 17 |
| createfirefightingadd | 306 | 0 | 306 | 306 |
| createmobfarming | 123 | 0 | 0 | 123 |
| emi | 210 | 0 | 0 | 210 |
| excompressum | 225 | 128 | 0 | 249 |
| exnihilosequentia | 352 | 132 | 0 | 450 |
| farmersdelight | 8 | 0 | 0 | 8 |
| ftbquests | 772 | 371 | 0 | 805 |
| hbmsntm | 1228 | 0 | 0 | 1228 |
| immersiveengineering | 3 | 0 | 0 | 3 |
| mysticalagriculture | 272 | 0 | 0 | 272 |
| nuclearcraftneohaul | 3643 | 2021 | 0 | 3661 |
| pamhc2crops | 514 | 0 | 0 | 514 |
| pamhc2foodcore | 203 | 0 | 0 | 203 |
| pamhc2trees | 158 | 0 | 0 | 158 |
| pmweather | 222 | 0 | 0 | 222 |
| rechiseled | 3656 | 0 | 0 | 3656 |
| sophisticatedcore | 318 | 288 | 0 | 318 |
| tfmg | 1170 | 402 | 0 | 1299 |
| wariumce | 1281 | 0 | 1281 | 1281 |

Остальные таблицы и примечания v0.4 сохранены.


## v0.6 — добавления (28.09)
Полный проход по всем 209 jar инстанса `1.21.1` напрямую с Диска. Проверены lang-файлы
каждого мода. Найдено и переведено вручную 12 модов без встроенного ru_ru:

| modid | ключей | что |
|---|---:|---|
| treephysics | 52 | весь конфиг физики деревьев + субтитры + смерть/команда |
| createtailwind | 45 | Create: Tailwind (реактивный ранец): сообщения, субтитры, весь конфиг |
| tab_organizer | 88 | Creative Tab Organizer: весь экран + настройки + справка |
| tectonic | 79 | Tectonic: весь конфиг генерации (ландшафт/океаны/пещеры/пресеты) |
| guideme | 34 | GuideME: UI + конфиг |
| configuration | 40 | библиотека конфигов: весь UI + ошибки + команды |
| resourcefulconfig | 36 | UI редактора конфигов + серверные свойства |
| common_storage_lib | 3 | энергия/жидкость/потребление |
| betteradvancedtooltips | 5 | настройки подсказок |
| baguettelib | 3 | dev-примеры |
| codxlib | 1 | таб генератора |
| bigwater | 2 | масштаб запасной текстуры |

Остальные 190+ jar: либо встроенный ru_ru (проверено распаковкой), либо вообще без
lang-файлов (утилитарные библиотеки: грузятся молча, текста игроку не показывают).
Всего в пакете: 66 модидов, ~33 770 ключей.
