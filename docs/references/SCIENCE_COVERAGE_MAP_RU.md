# Карта научных областей монументального проекта

<!-- TOC:START -->
## Оглавление

- [0. Обозначения аудита](#coverage-0)
- [1. Что уже покрыто](#coverage-1)
- [2. Важные пробелы первого аудита и их текущее покрытие](#coverage-2)
- [3. Сводная матрица областей](#coverage-3)
- [3A. Карта зависимостей](#coverage-3a)
- [4. Физические области, которые можно отложить](#coverage-4)
- [5. Области инженерии, которые нельзя заменить физикой и химией](#coverage-5)
- [6. Приоритетный порядок дальнейшего углубления](#coverage-6)
- [7. Как закрыты важные пробелы](#coverage-7)
- [8. Как решать проблему огромного объёма знаний](#coverage-8)
- [9. Что значит «крайне хорошо знать физику и химию»](#coverage-9)
- [Итог](#coverage-summary)
- [Реестр полных списков источников](#coverage-sources)

> [Единая программа](INTEGRATED_SCIENCE_CURRICULUM_RU.md) · [Реестр источников](#coverage-sources)
<!-- TOC:END -->


Статус: аудит уже созданных учебных справочников и карта областей, которые ещё нужно
углубить до реализации соответствующих игровых ветвей.

Дата аудита: **1 октября 2026 года**. Навигация и порядок изучения актуализированы
**2 октября 2026 года**.

Линейный маршрут находится в
[единой программе изучения наук](INTEGRATED_SCIENCE_CURRICULUM_RU.md). Начинать следует
с неё: программа отвечает на вопрос «что учить дальше», а эта карта — «какие области уже
покрыты, кому они принадлежат и где остаётся риск».

Проект уже охватывает большую часть классической механики, аэрогидродинамики,
термодинамики, общей/органической/физической химии, материаловедения, электрохимии и
безопасной ядерной энергетики. Но физика и химия не существуют отдельно от математики,
метрологии, геологии, вычислительной науки, управления, надёжности и экологии.

> «Знать крайне хорошо» не означает одновременно стать специалистом мирового уровня в
> сорока дисциплинах. Для проекта это означает: иметь сильное общее ядро, углублять
> область до проверяемой инженерной модели перед её реализацией и привлекать профильную
> экспертную проверку для высокорисковых ветвей.

---

<a id="coverage-0"></a>
## 0. Обозначения аудита

### 0.1 Текущее покрытие

Статус оценивает подробные учебные маршруты, существовавшие к моменту аудита. Простое
упоминание области в этой карте не повышает её статус.

- **A — сильное ядро:** есть отдельный маршрут, книги, формулы, данные и критерии;
- **B — покрыто частично:** основы есть, но нет самостоятельного глубокого раздела;
- **C — обозначено:** область упоминается, но учебного маршрута пока нет;
- **D — пробел:** область практически не рассмотрена.

### 0.2 Приоритет

- **P0** — общий фундамент, обязательный для всех ветвей;
- **P1** — нужен текущим авиационным, механическим и химическим задачам;
- **P2** — нужен перед расширением индустриального мода;
- **P3** — углубление для специализированной поздней ветви;
- **P4** — контекст/reference; не требуется для первой реализации.

### 0.3 Уровень знания области

1. **Грамотность:** термины, единицы, основные явления.
2. **Расчёт:** решение учебных задач и оценка порядка величины.
3. **Модель:** самостоятельный вывод assumptions, equations и limits.
4. **Верификация:** benchmark, convergence, uncertainty и независимые данные.
5. **Экспертный уровень:** литература, конкурирующие модели, failure cases и review
   профильного специалиста.

В production-механику нельзя переносить область ниже уровня 4. Для ядерной, токсичной,
высоконапорной и иной опасной тематики дополнительно нужен уровень 5 или внешний review.

---

<a id="coverage-1"></a>
## 1. Что уже покрыто

### 1.1 Математика — A/P0

Отдельный маршрут:
[`MATHEMATICS_STUDY_GUIDE_RU.md`](MATHEMATICS_STUDY_GUIDE_RU.md)

- алгебра, геометрия, тригонометрия и доказательства;
- анализ одной и нескольких переменных;
- векторный анализ и линейная алгебра;
- ОДУ/PDE и динамические системы;
- complex/Fourier/Laplace;
- вероятность, статистика и uncertainty;
- численные методы, оптимизация и управление;
- тензоры, вариационные методы и функциональный анализ;
- размерностный и асимптотический анализ.

### 1.2 Инженерная физика — A/B, P0–P1

Маршрут:
[`REALISM_STUDY_GUIDE_RU.md`](REALISM_STUDY_GUIDE_RU.md)

Сильнее всего покрыты:

- классическая и инженерная механика;
- вращение, передачи, вибрации;
- сопротивление материалов и конструкции;
- механика жидкости и газа;
- термодинамика и теплопередача;
- управление и устойчивость;
- самолёты, вертолёты и роторы;
- суда, подлодки и гребные винты;
- атмосфера, океан и волны;
- инженерные данные, CFD/FEA и верификация.

### 1.3 Химия и материалы — A/B, P0–P2

Маршрут:
[`CHEMISTRY_MOD_STUDY_GUIDE_RU.md`](CHEMISTRY_MOD_STUDY_GUIDE_RU.md)

Покрыты:

- общая и неорганическая химия;
- органика и механизмы;
- физическая, молекулярная и квантовая химия;
- coordination/organometallic, photo/supramolecular, nano и computational chemistry;
- термодинамика, равновесие, кинетика и катализ;
- аналитика и спектроскопия;
- электрохимия, батареи и коррозия;
- полимеры;
- кристаллохимия и материаловедение;
- металлургия и фазовые диаграммы;
- процессы и аппараты;
- электричество и электромагнетизм;
- атомная/ядерная физика и радиохимия;
- экология, токсикология, GHS/SDS и process safety;
- нефтехимия, природный газ, топлива и переработка нефти.

### 1.4 Метрология, systems и надёжность — A/B, P0–P2

Маршрут:
[`MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md`](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md)

Покрыты measurement uncertainty, sensors/DSP, experiment design, numerical V&V/UQ,
systems engineering, plantwide control/process safety, fracture/fatigue/reliability,
manufacturing/NDT, human factors и lifecycle decisions.

### 1.5 Интерфейсы и сложная энергетическая физика — A/B, P1–P3

Маршрут:
[`INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md`](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md)

Покрыты surfaces/wetting/adsorption, colloids, rheology, multiphase/granular/porous flow,
tribology, combustion/fire science, acoustics, plasma, vacuum и cryogenics.

### 1.6 Земля, специальные материалы и environment — A/B, P1–P3

Маршрут:
[`EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md`](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md)

Покрыты geology/mineralogy/geochemistry/geophysics, resource estimation, mining и
mineral processing, ceramics/glass/cement/refractories, condensed/electronic materials,
optics/photonics, water/atmosphere/environment/LCA и безопасные conditional bio/agro
branches.

---

<a id="coverage-2"></a>
## 2. Важные пробелы первого аудита и их текущее покрытие

Ниже сохранён полный перечень обнаруженных пробелов. После второго этапа для них уже
созданы связанные учебные маршруты, формулы, источники, data models и acceptance
criteria. Это **не** означает автоматическую готовность всех игровых механик: перед кодом
всё равно нужны domain-specific data, benchmark и review.

### 2.1 Поверхностные явления, коллоидная химия и интерфейсы — A/B, P1

Почему важны:

- катализ идёт на поверхности;
- коррозия и passivation происходят на границе фаз;
- пены, эмульсии и суспензии определяют разделение;
- смачивание влияет на фильтрацию, флотацию и porous media;
- adhesion/coatings зависят от surface energy;
- батареи работают через electrochemical interfaces;
- наноматериалы имеют большую удельную поверхность.

Нужно изучить:

- surface tension/energy;
- wetting и contact angle;
- adsorption isotherms;
- electric double layer;
- colloidal stability и DLVO как первая модель;
- emulsions, foams, aerosols и suspensions;
- surfactants, micelles и critical micelle concentration;
- nucleation;
- heterogeneous catalysis;
- membranes и porous interfaces.

Будущая игровая механика: стабилизированная эмульсия не должна разделяться так же, как
идеальная смесь; filter cake, froth flotation, catalyst area и coating adhesion должны
иметь физическую причину.

### 2.2 Реология, многофазные потоки и гранулированные среды — A/B, P1

Обычная Newtonian liquid model недостаточна для:

- нефти и тяжёлых остатков;
- polymer melts;
- slurries и pulp;
- mud, cement и suspensions;
- powders, ores и catalyst pellets;
- gas–liquid/solid–liquid reactors;
- boiling/condensation;
- cavitation.

Нужны:

- Newtonian/non-Newtonian constitutive laws;
- shear thinning/thickening, yield stress и thixotropy;
- viscoelasticity;
- particle-size distributions;
- settling, fluidization и pneumatic transport;
- multiphase pressure drop;
- phase holdup;
- bubble/drop breakup/coalescence;
- granular friction, segregation и arching.

Это отдельный мост между fluid mechanics, polymer physics, metallurgy и process design.

### 2.3 Наука о горении и реакционноспособные потоки — A/B, P1

На первом аудите были термодинамика, кинетика, топливо и двигатели, но не было цельного
курса combustion science. Теперь он добавлен в межфазно-энергетический маршрут.

Обязательное содержание:

- stoichiometry и equivalence ratio;
- adiabatic flame temperature;
- ignition/extinction;
- laminar/turbulent flames;
- premixed/diffusion flames;
- chemical timescale против mixing timescale;
- Damköhler и Karlovitz ideas;
- pollutant formation;
- soot и particulate;
- detonation/explosion phenomena только для safety analysis;
- fire dynamics, ventilation и suppression;
- combustion instability.

Граница безопасности: моделировать энергию, emissions, ignition risk и защиту; не
публиковать рецептуры или практические инструкции по изготовлению взрывчатых составов.

### 2.4 Трибология, смазки, контакт и износ — A/B, P1

Для валов, подшипников, редукторов, уплотнений, насосов и роторов критичны:

- friction regimes;
- Hertz contact;
- boundary/mixed/hydrodynamic lubrication;
- Stribeck curve;
- viscosity–temperature/pressure behavior;
- wear: adhesive, abrasive, fatigue, erosive;
- fretting;
- seal leakage;
- lubricant oxidation/contamination;
- thermal balance контакта;
- material/coating compatibility.

Без трибологии «подшипник» остаётся вечным магическим соединителем. Для микромодульного
ротора эта область имеет высокий приоритет.

### 2.5 Разрушение, усталость, ползучесть и инженерная надёжность — A/B, P1

К сопротивлению материалов теперь добавлена самостоятельная ветвь:

- stress concentration;
- fracture mechanics и crack growth;
- low/high-cycle fatigue;
- creep и stress rupture;
- thermal fatigue;
- corrosion fatigue;
- Miner rule как грубая модель и её ограничения;
- probabilistic strength;
- reliability block diagrams;
- FMEA/FMECA, fault tree и event tree;
- maintainability, inspection и remaining life;
- common-cause failures;
- graceful degradation.

Это связывает материал, нагрузочную историю и обслуживание, а не только мгновенный
предел прочности.

### 2.6 Метрология, приборостроение и обработка сигналов — A/B, P0

К аналитической химии и uncertainty теперь добавлен общий project-wide measurement layer:

- SI realization и traceability;
- calibration и reference standards;
- accuracy, precision, resolution, sensitivity и bandwidth;
- static/dynamic sensor response;
- noise, drift, bias, hysteresis и saturation;
- sampling, aliasing и filtering;
- sensor fusion;
- observability;
- diagnostics и fault detection;
- data acquisition;
- calibration interval и uncertainty budget.

Без этого UI показывает «истинное число мира», а приборы не являются частью инженерной
системы.

### 2.7 Геология, минералогия, геохимия и месторождения — A/B, P2

Металлургия начинается не с универсального блока «руда».

Нужны:

- minerals против chemical compounds;
- crystal habit, hardness, cleavage и density;
- igneous/sedimentary/metamorphic processes;
- ore genesis;
- grade, gangue, texture и liberation;
- representative sampling;
- mineral identification;
- geochemical cycles;
- weathering и oxidation zones;
- fluid inclusions/hydrothermal systems на обзорном уровне;
- resource/reserve uncertainty;
- environmental geochemistry и acid mine drainage.

Эта ветвь должна предшествовать глубокой добыче, обогащению и реалистичным рудам.

### 2.8 Горное дело и обогащение полезных ископаемых — A/B, P2

Отдельно от металлургии:

- drilling/blasting только на безопасном обзорном уровне;
- excavation и ground support;
- ventilation и dust;
- haulage;
- crushing/grinding;
- screening/classification;
- gravity/magnetic/electrostatic separation;
- flotation;
- dewatering;
- tailings;
- mass recovery против grade;
- energy cost of comminution;
- geotechnical risk.

Игровая ценность: качество руды, liberation size, recovery и tailings становятся важнее
умножения руды одной машиной.

### 2.9 Керамика, стекло, цемент и огнеупоры — A/B, P2

Материаловедение не ограничивается металлами и полимерами.

Нужны:

- silicate chemistry;
- glass transition и glass-forming systems;
- ceramic phase diagrams;
- powder preparation;
- pressing, drying и sintering;
- porosity;
- brittle fracture;
- thermal shock;
- refractories и slag compatibility;
- cement hydration;
- concrete microstructure и durability;
- advanced ceramics.

Это необходимо для печей, реакторов, изоляции, строительства и высоких температур.

### 2.10 Физика конденсированного состояния и электронные материалы — A/B, P2

Кристаллохимия и основы твёрдого тела уже были; теперь цельная физическая и
технологическая ветвь добавлена:

- symmetry, lattices и reciprocal space;
- bonding и electronic structure;
- phonons, thermal capacity/conductivity;
- defects, diffusion и dislocations;
- phase transitions и collective phenomena;
- band structure;
- electrical/thermal/magnetic transport;
- intrinsic/doped semiconductors;
- carriers, mobility и recombination;
- p–n junction;
- dielectric и ferroelectric materials;
- magnetic domains, hysteresis и losses;
- superconductivity на обзорном уровне;
- crystal growth;
- purification и zone refining;
- thin films, deposition, diffusion/implantation на обзорном уровне;
- lithography and contamination control;
- thermal management;
- electronic waste/recycling.

Не обязательно превращать мод в fabrication simulator, но свойства твёрдых тел и
электрических компонентов должны зависеть от структуры, дефектов, материала и
температуры.

### 2.11 Оптика, фотоника и физика излучения — A/B, P2

К аналитической спектроскопии теперь добавлена самостоятельная физическая база:

- geometrical optics;
- interference, diffraction и polarization;
- absorption/emission/scattering;
- lasers на принципиальном уровне;
- detectors;
- radiometry/photometry;
- fiber optics;
- optical materials и coatings;
- thermal radiation;
- remote sensing.

Применения: аналитические приборы, navigation sensors, thermal imaging, communication,
solar energy и atmospheric measurements.

### 2.12 Акустика, виброакустика и шум — A/B, P1

К vibrations теперь добавлен полный путь от источника до sound field:

- wave equation;
- impedance;
- resonance и modes;
- radiation from structures;
- aeroacoustics/hydroacoustics;
- cavitation noise;
- propeller/rotor tonal и broadband noise;
- underwater acoustics;
- measurement, weighting и spectra;
- damping/isolation;
- human/environmental effects.

Для вертолётной и подводной тем эта область принципиальна.

### 2.13 Plasma physics, high-temperature и ionized-gas chemistry — B, P3

Нужны перед плазменной обработкой, дугами, высокотемпературной металлургией или
экзотическими силовыми установками:

- ionization и recombination;
- quasi-neutrality и Debye length;
- collisions;
- conductivity;
- sheaths;
- magnetized plasma;
- MHD basics;
- nonequilibrium temperature;
- plasma chemistry;
- arcs и electrode erosion;
- diagnostics;
- radiation и heat loads.

Не смешивать plasma с обычным горячим газом и не считать любой разряд бесплатным
источником энергии.

### 2.14 Вакуумная и криогенная техника — A/B, P2

Это инженерная область на стыке термодинамики, surfaces и materials:

- vacuum regimes и mean free path;
- outgassing, leaks и pumping;
- conductance;
- cryogenic properties;
- liquefaction cycles;
- multilayer insulation;
- boil-off;
- cryopumping;
- embrittlement и seals;
- oxygen-deficiency hazard;
- instrumentation.

Нужна для LNG, industrial gases, space/vacuum equipment, superconductivity и части
ядерных технологий.

### 2.15 Водная химия и очистка воды — A/B, P1

К основам растворов и экологии теперь добавлена самостоятельная industrial-water system:

- alkalinity/hardness;
- carbonate system;
- salinity и ionic strength;
- speciation;
- scale и corrosion;
- coagulation/flocculation;
- sedimentation/filtration;
- ion exchange;
- membrane processes;
- adsorption;
- disinfection;
- biological treatment;
- boiler/cooling water;
- wastewater mass balance;
- sludge и concentrate.

Практически каждая химическая, нефтяная, металлургическая и энергетическая линия зависит
от воды и создаёт отдельные water loops.

### 2.16 Атмосферная и экологическая химия, LCA — A/B, P1–P2

Нужны:

- atmospheric reactions и photochemistry;
- aerosol formation;
- dispersion/deposition;
- greenhouse gases без подмены climate science одной цифрой;
- acidification, eutrophication и ecotoxicity;
- soil/water partition;
- persistence и bioaccumulation;
- material/energy inventory;
- lifecycle boundaries;
- direct против indirect emissions;
- allocation и recycling credits;
- uncertainty и scenario analysis.

Экология — не универсальный «штраф загрязнения», а материальные потоки и последствия во
времени и пространстве.

### 2.17 Биохимия, микробиология и биотехнология — B/C, P3

Нужны только если проект пойдёт в fermentation, bioleaching, wastewater, agriculture,
biofuels или biomaterials:

- proteins/enzymes;
- metabolism и energy coupling;
- cell growth kinetics;
- mass transfer to cells;
- fermentation;
- microbial communities;
- sterilization/contamination control;
- bioreactors;
- bioseparation;
- bioleaching и biocorrosion;
- biosafety и ethics.

Это самостоятельный большой маршрут, а не подраздел органической химии.

### 2.18 Агрохимия, почвоведение и растениеводство — B/C, P3

Для интеграции с AgriCraft потенциально нужны:

- soil texture/structure;
- cation exchange;
- pH/buffering;
- water retention;
- macro/micronutrients;
- nitrogen/phosphorus cycles;
- salinity;
- deficiency/toxicity;
- fertilizers and controlled release;
- runoff/leaching;
- plant uptake;
- soil microbiome;
- sustainable nutrient balance.

Не добавлять эту ветвь только ради ещё одного удобрения с multiplier.

### 2.19 Вычислительная химия и molecular simulation — A/B, P3

К квантовым основам и списку программ теперь добавлен полноценный workflow:

- electronic-structure approximations;
- basis sets/pseudopotentials;
- geometry optimization;
- frequencies и transition states;
- molecular mechanics/force fields;
- molecular dynamics;
- Monte Carlo;
- solvation;
- periodic solids;
- validation against experiment;
- uncertainty/model discrepancy;
- reproducible workflows и licensing of datasets.

Realtime DFT в игре не нужен. Эти методы полезны для offline-данных и понимания limits.

### 2.20 Системная инженерия — A/B, P0

Физика и химия сами по себе не удержат большой проект.

Нужны:

- requirements и traceability;
- interfaces;
- architecture и ownership механик;
- verification против validation;
- configuration/version management;
- model/data provenance;
- change control;
- hazard analysis;
- trade studies;
- test pyramids;
- acceptance criteria;
- technical debt;
- documentation lifecycle.

Это защита от ситуации, когда каждая отдельная машина «правильная», но вся система
создаёт вещество или энергию из ничего.

### 2.21 Вычислительная наука, software engineering и information layer — A/B, P0

К математическим algorithms теперь добавлена самостоятельная дисциплина вычислительной
достоверности:

- algorithms, data structures и computational complexity;
- floating-point arithmetic;
- deterministic/reproducible simulation;
- discretization, convergence и stability;
- optimization и inverse problems;
- stochastic simulation;
- sensitivity analysis и uncertainty propagation;
- surrogate/reduced-order models;
- performance profiling и parallelism;
- data schemas, units и provenance;
- versioning/migration of worlds and datasets;
- property-based, regression и differential testing;
- software architecture для связанных solvers;
- information theory и coding basics для sensors/communication;
- cybersecurity для networked control как поздняя инженерная ветвь.

Более сложный solver не обязательно точнее. Он полезен только при контролируемых input,
сеточной/временной сходимости, benchmark и измеримой цене вычисления.

### 2.22 Process systems engineering, управление и безопасность — A/B, P1

Отдельные аппараты и PID не создают согласованный завод. Нужны:

- flowsheet synthesis;
- degrees-of-freedom analysis;
- recycle/purge и convergence;
- heat/material integration;
- pinch analysis;
- steady-state и dynamic simulation;
- controllability/observability;
- plantwide control;
- scheduling и optimization;
- state estimation;
- alarm/interlock/SIS layers;
- inherently safer design;
- HAZID/HAZOP/LOPA на концептуальном уровне;
- startup/shutdown/off-design как состояния модели, без operational recipes;
- resilience to feed variability и equipment failure.

Эта дисциплина связывает химию, термодинамику, transport, control, economics,
reliability и process safety.

### 2.23 Специализированные химические ветви — A/B для project-relevant, C/P4 conditional

Project-relevant branches теперь добавлены в раздел 8A химического маршрута; P4
branches сохранены на карте как условные:

- coordination и organometallic chemistry — catalysts, extraction, advanced materials;
- photochemistry — atmosphere, coatings, degradation, sensors;
- supramolecular и host–guest chemistry — separations/sensors;
- solid-state synthesis — ceramics, batteries, electronic materials;
- geochemistry — ores, water/rock interaction, waste;
- atmospheric chemistry — emissions and climate branch;
- medicinal/pharmaceutical chemistry — отдельная P4-область, только если появится
  соответствующий безопасный модуль;
- food chemistry — P4, если появится processing/nutrition branch.

Упоминание в карте не означает готовность добавлять recipes. Каждая такая ветвь требует
своего маршрута, данных, этических и safety-границ.

### 2.24 Физиология среды, occupational health и cognitive ergonomics — B, P2

Для авиации, подлодок, шума, вибрации и опасной промышленной среды нужны хотя бы:

- hypoxia и oxygen-deficiency hazards;
- pressure change, barotrauma и decompression risk;
- acceleration/G-load и motion sickness;
- heat/cold stress;
- noise/vibration exposure;
- visibility, workload и situational awareness;
- exposure routes и dose–response вместе с toxicology;
- fatigue, alarm perception и human error;
- accessibility и ergonomic maintenance.

Это не медицинский симулятор и не руководство по лечению. Задача ветви — корректно
моделировать environmental limits, предупреждения и protective layers.

---

<a id="coverage-3"></a>
## 3. Сводная матрица областей

| Область | Покрытие | Приоритет | Нужна для |
|---|---:|---:|---|
| Математика | A | P0 | всего проекта |
| Вычислительные методы | A/B | P0 | все динамические модели |
| Computer/data/software science | A/B | P0 | solvers, schemas, reproducibility |
| Метрология и uncertainty | A/B | P0 | все данные и приборы |
| Системная инженерия/V&V | A/B | P0 | архитектура проекта |
| Классическая механика | A | P0 | аппараты, транспорт, роторы |
| Сплошные среды | A/B | P1 | aero/hydro/process |
| Термодинамика/теплоперенос | A | P0 | химия, двигатели, энергетика |
| Statistical physics | B | P2 | thermo/materials/quantum |
| Condensed matter physics | A/B | P2 | materials/electronics/high-T |
| Электромагнетизм | A/B | P1 | сети, машины, electrochem |
| Optics/photonics | A/B | P2 | sensors, spectroscopy, solar |
| Acoustics | A/B | P1 | rotors, submarines, machinery |
| Quantum/atomic physics | B | P2 | chemistry, materials, nuclear |
| Nuclear/radiation | A/B | P3 | peaceful energy/detection |
| Plasma/MHD | B | P3 | arcs, high-temperature branches |
| Meteorology/oceanography | A/B | P2 | flight, ships, environment |
| Geophysics/geodesy | B | P3 | resources/navigation/Earth model |
| Relativity | C | P4 | precision/context, не первая механика |
| General/inorganic chemistry | A | P0 | reactions/materials |
| Organic chemistry | A | P1 | fuels, polymers, synthesis |
| Physical/quantum chemistry | A/B | P1 | thermo, kinetics, spectra |
| Analytical chemistry | A/B | P0 | quality and measurement |
| Electrochemistry | A | P1 | batteries, corrosion, refining |
| Polymers | A/B | P2 | structural/functional materials |
| Petroleum/refining/petrochemistry | A/B | P2 | TFMG, fuels, polymers |
| Surface/colloid chemistry | A/B | P1 | catalysis, separation, coatings |
| Combustion | A/B | P1 | engines, furnaces, emissions |
| Water chemistry | A/B | P1 | utilities, environment, reactors |
| Atmospheric/environmental chemistry | A/B | P2 | emissions/ecology |
| Crystal/solid-state chemistry | A/B | P1 | materials/electronics |
| Metallurgy | A/B | P1 | alloys/process equipment |
| Ceramics/glass/cement | A/B | P2 | refractory/building/high-T |
| Semiconductor/electronic materials | A/B | P2 | electronics/power |
| Nanoscience | A/B | P3 | catalysts/coatings/electronics |
| Tribology/lubrication | A/B | P1 | shafts, bearings, seals |
| Fracture/fatigue/reliability | A/B | P1 | safe machinery/structures |
| Geology/mineralogy/geochemistry | A/B | P2 | believable resources |
| Mining/mineral processing | A/B | P2 | ore-to-concentrate chain |
| Vacuum/cryogenics | A/B | P2 | LNG, gases, advanced systems |
| Chemical/process engineering | A/B | P1 | reactors/separations/utilities |
| Process systems/control/safety | A/B | P1 | integrated factories |
| Manufacturing science | A/B | P2 | turning materials into parts |
| Coordination/organometallic chemistry | A/B | P2–P3 | catalysis/extraction/materials |
| Photochemistry/supramolecular chemistry | A/B | P3 | atmosphere/sensors/separation |
| Biochemistry/biotechnology | B/C | P3 | optional bio-branch |
| Soil/agrochemistry | B/C | P3 | optional AgriCraft branch |
| Environmental/occupational physiology | B | P2 | flight, diving, industrial hazards |
| Human factors/ergonomics | B | P2 | alarms, controls, maintenance |
| Operations research/logistics | B | P2 | planning, scheduling, supply chains |
| Engineering economics/LCA | A/B | P2 | trade-offs and sustainability |

---

<a id="coverage-3a"></a>
## 3A. Карта зависимостей

Стрелка означает prerequisite или сильную зависимость, а не запрет изучать темы
параллельно.

```text
математика + логика
    ├─> theoretical physics ─> mechanics / fields / quantum / statistical physics
    ├─> numerical methods ─> CFD / FEA / process simulation / optimization
    ├─> probability ─> uncertainty / reliability / experimental design
    └─> control theory ─> automation / navigation / plantwide control

метрология + приборы + signal processing
    ├─> достоверные physical/chemical properties
    ├─> calibration and feedback control
    └─> validation / diagnostics / quality certification

atomic + quantum + statistical physics
    ├─> physical chemistry ─> equilibrium / kinetics / spectroscopy
    └─> condensed matter ─> metals / ceramics / polymers / semiconductors

mechanics + thermodynamics + transport + chemistry
    └─> chemical/process engineering
          ├─> reactors / separations / heat exchange
          ├─> refining / petrochemistry / metallurgy
          └─> process systems / control / safety / economics

Earth science
    ├─> geology / mineralogy / geochemistry ─> mining / beneficiation ─> metallurgy
    └─> basin/reservoir knowledge ─> petroleum upstream ─> refining / petrochemistry

surface science + materials + fluid mechanics
    ├─> tribology / lubrication / seals
    ├─> corrosion / coatings / catalysis
    └─> colloids / flotation / filtration / membranes

materials + manufacturing + load/environment history
    └─> fracture / fatigue / creep / reliability / maintenance

combustion + reactive flow + acoustics + control
    └─> engines / furnaces / fire safety / emissions

water + atmospheric chemistry + ecology + geoscience
    └─> waste treatment / dispersion / remediation / lifecycle assessment
```

Сквозные дисциплины — **computation, metrology, uncertainty, safety и systems
engineering**. Они не находятся в конце дерева: каждая новая ветвь должна использовать
их с первого prototype.

### 3A.1 Зависимости текущего ротора

```text
математика
  └─> rigid-body rotation + aerodynamics
       ├─> rotor interference / induced flow / RPM-dependent thrust
       ├─> structures + fatigue
       ├─> tribology + bearings + lubrication
       ├─> vibration + acoustics
       ├─> sensors + control
       └─> numerical validation + in-game balance
```

### 3A.2 Зависимости химического вертикального среза

```text
общая + физическая + органическая chemistry
  ├─> thermo / phase equilibrium / kinetics
  ├─> surfaces / catalysis / corrosion
  ├─> transport / rheology / multiphase flow
  └─> analytical chemistry / metrology
       └─> reactors + separation + recycle + utilities
            └─> process control + safety + environment + economics
```

---

<a id="coverage-4"></a>
## 4. Физические области, которые можно отложить

### 4.1 Относительность

Special relativity полезна для общей физической грамотности, nuclear/particle context и
точного electromagnetism, но обычным роторам, химическим аппаратам и судам не нужен
relativistic tick solver.

General relativity и cosmology относятся к **P4**, пока проект не включает space/astronomy
branch. Их следует изучать как фундаментальную физику, но не выдавать за текущий blocker.

### 4.2 Физика элементарных частиц

Нужна для цельной картины nuclear physics и detectors, но не для первой модели реактора,
материалов или химии. Достаточен университетский обзор до появления отдельной механики.

### 4.3 Astrophysics и planetary science

Станут важны, если появятся:

- другие атмосферы;
- orbital mechanics;
- re-entry;
- space radiation;
- extraterrestrial resources;
- vacuum/cryogenic propulsion.

До этого — reference branch.

---

<a id="coverage-5"></a>
## 5. Области инженерии, которые нельзя заменить физикой и химией

### 5.1 Manufacturing science

Материал с правильными свойствами ещё не является деталью. Нужны:

- casting, forming, machining и joining;
- welding/brazing/adhesive bonding;
- additive manufacturing;
- tolerances, fits и surface finish;
- residual stress;
- heat treatment after processing;
- nondestructive evaluation;
- quality planning;
- repairability и recycling.

### 5.2 Human factors

Аварии происходят не только из-за плохой формулы:

- alarm overload;
- ambiguous controls;
- hidden state;
- плохая цветовая/звуковая кодировка;
- неудобное обслуживание;
- ошибочная последовательность действий;
- усталость оператора;
- automation surprise.

UI реалистичного мода должен обучать причинности, а не скрывать её.

### 5.3 Engineering economics

Реальная технология выбирается не только по максимальному yield:

- capital/operating cost;
- energy и utilities;
- catalyst/material replacement;
- uptime/maintenance;
- feed variability;
- product value;
- waste treatment;
- safety/environmental cost;
- lifecycle и recycling;
- uncertainty/risk.

Экономика не должна подменять физику, но без неё нет инженерного trade-off.

---

<a id="coverage-6"></a>
## 6. Приоритетный порядок дальнейшего углубления

### Волна 0 — фундамент всего проекта

1. математика;
2. единицы и метрология;
3. conservation и balances;
4. numerical methods/UQ;
5. data provenance;
6. systems engineering и V&V.

### Волна 1 — текущие механические системы

1. tribology;
2. fatigue/fracture/reliability;
3. acoustics;
4. rotor/propeller coupled flow;
5. sensors/control;
6. manufacturing/tolerances.

### Волна 2 — химико-индустриальный вертикальный срез

1. surface/colloid chemistry;
2. water chemistry;
3. multiphase flow/rheology;
4. instrumentation/process control;
5. corrosion/material compatibility;
6. separation and waste;
7. petroleum/refining integration with TFMG.

### Волна 3 — сырьё и материалы

1. geology/mineralogy;
2. mining/mineral processing;
3. metallurgy;
4. ceramics/glass/cement/refractories;
5. polymers/composites;
6. manufacturing и inspection.

### Волна 4 — энергетика

1. combustion/reactive flow;
2. electrochemical energy;
3. electrical machines/grid;
4. high-temperature materials;
5. environmental/LCA;
6. только затем — nuclear energy с defense in depth.

### Волна 5 — advanced branches

- semiconductors/photonics;
- plasma/MHD;
- vacuum/cryogenics;
- computational chemistry;
- biotechnology;
- space/relativity/planetary science.

---

<a id="coverage-7"></a>
## 7. Как закрыты важные пробелы

### 7.1 Сквозной фундамент P0–P2

[`MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md`](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md)
закрывает единым маршрутом:

1. метрологию, sensors, signal processing и uncertainty;
2. experiment design и system identification;
3. computational/software credibility, V&V и UQ;
4. systems engineering и configuration/traceability;
5. process systems, plantwide control и process safety;
6. fracture, fatigue, creep, reliability и maintenance;
7. manufacturing, tolerances, quality и NDT;
8. human factors/environmental physiology;
9. operations research, economics и lifecycle decisions.

### 7.2 Межфазная и энергетическая физика P1–P3

[`INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md`](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md)
закрывает:

1. surfaces, wetting, adsorption, colloids и surfactants;
2. rheology/viscoelasticity;
3. multiphase, porous и granular media;
4. tribology, lubrication, wear и seals;
5. combustion/reactive flows и fire-safety boundary;
6. acoustics/vibroacoustics;
7. plasma/MHD;
8. vacuum и cryogenics.

### 7.3 Земля, материалы и environment P1–P3

[`EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md`](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md)
закрывает:

1. geology, petrology, mineralogy, geochemistry и geophysics;
2. sampling, resources/reserves и spatial uncertainty;
3. mining, geotechnics и mineral processing;
4. ceramics, glass, cement/concrete и refractories;
5. condensed matter, semiconductors, dielectrics и magnetics;
6. optics/photonics;
7. industrial water, wastewater и environmental fate;
8. atmospheric chemistry/dispersion и LCA/circularity;
9. безопасные conditional routes по biotechnology и soil/agrochemistry.

### 7.4 Специализированная химия

В [`CHEMISTRY_MOD_STUDY_GUIDE_RU.md`](CHEMISTRY_MOD_STUDY_GUIDE_RU.md) добавлен раздел
8A: coordination/organometallic chemistry, photochemistry, supramolecular chemistry,
nanoscience и computational chemistry/molecular simulation с method hierarchy,
verification workflow, data model и safety boundary.

### 7.5 Что остаётся условным, а не забытым

Отдельный глубокий справочник пока не нужен для branches, которых нет в scope:

- medicinal/pharmaceutical и food science;
- full biotechnology/genetics;
- relativistic/particle/astrophysics;
- planetary/space systems;
- специализированная nanofabrication;
- отраслевое legal/regulatory compliance конкретной страны.

Они явно отмечены как P3/P4 и получают самостоятельный маршрут только перед реальной
механикой. Нефтехимия уже включена большим разделом в химический справочник; отдельный
файл понадобится, если она станет самостоятельным модулем разработки.

---

<a id="coverage-8"></a>
## 8. Как решать проблему огромного объёма знаний

### 8.1 T-shaped модель команды

Каждому участнику нужны:

- широкая грамотность по всей карте;
- глубокая специализация в 1–2 областях;
- общий язык единиц, balances, uncertainty и V&V;
- способность читать соседнюю модель и задавать правильные вопросы.

Один человек не обязан одинаково глубоко знать quantum chemistry, rotorcraft,
geotechnics и reactor physics. Но проект обязан иметь проверяемый источник и owner для
каждой механики.

### 8.2 Паспорт научной механики

Перед coding каждая механика получает:

- научный вопрос;
- owner;
- необходимые prerequisites;
- glossary;
- state variables и units;
- governing laws;
- assumptions;
- valid range;
- source/data provenance;
- benchmark;
- numerical method;
- uncertainty;
- safety boundary;
- integration owner;
- acceptance tests;
- список известных упрощений.

### 8.3 Definition of Ready

Механика готова к реализации, если:

1. есть минимум один основной учебник и один независимый источник;
2. данные происходят из авторитетной базы;
3. выписаны conservation laws;
4. выполнен расчёт вручную;
5. есть offline prototype;
6. пройдены limiting cases;
7. задан benchmark/tolerance;
8. проверено отсутствие дублирования другого мода;
9. определены hazards;
10. понятно, что увидит и чему научится игрок.

### 8.4 Definition of Done

- schema и units валидируются;
- solver сходится или честно сообщает failure;
- balance residual находится в tolerance;
- результаты воспроизводимы;
- uncertainty не скрыта;
- multiplayer/save-load не меняют физику;
- есть tests и documentation;
- локализация полна;
- специалист может проследить формулу до источника;
- игровое упрощение объяснено.

---

<a id="coverage-9"></a>
## 9. Что значит «крайне хорошо знать физику и химию»

Для нашего проекта это не запоминание всех формул. Это способность:

1. начинать с определения системы и границ;
2. выбирать state variables;
3. применять conservation;
4. выводить модель и безразмерные группы;
5. отличать equilibrium от kinetics и transport;
6. знать properties материалов и phases;
7. понимать измерение и uncertainty;
8. выбирать численный метод;
9. проверять stability/convergence;
10. сопоставлять с экспериментом;
11. анализировать отказ и hazard;
12. честно обозначать упрощение;
13. не дублировать уже готовую механику;
14. обновлять модель при новых данных.

Именно глубина этой цепочки важнее количества названий наук в README.

---

<a id="coverage-summary"></a>
## Итог

Все важные пробелы первого аудита теперь получили самостоятельное глубокое покрытие:
**поверхности и коллоиды, реология/многофазные среды, горение, трибология, разрушение и
надёжность, метрология/приборы, geology/mining, керамика/стекло/цемент, condensed и
электронные материалы, coordination/organometallic/photo/supramolecular/nanochemistry,
computational chemistry, оптика, акустика, plasma, vacuum/cryogenics, water/environment,
conditional biotechnology/agro, вычислительная достоверность, process systems и systems
engineering**.

Это закрывает пробел в **учебной архитектуре**, но не объявляет все mechanics
верифицированными. Следующий рациональный шаг — применять P0/P1-ядро к одному
вертикальному срезу: измерения и V&V → rotor interaction/RPM thrust → tribology/fatigue/
acoustics → verified gameplay simplification.

---

<!-- SOURCES:START -->
<a id="coverage-sources"></a>
## Реестр полных списков источников

Карта не повторяет тысячи библиографических строк внутри тематических разделов. Полные списки, уже продублированные в конце каждого маршрута:

- [Математика](MATHEMATICS_STUDY_GUIDE_RU.md#math-sources)
- [Физика и инженерная механика](REALISM_STUDY_GUIDE_RU.md#physics-sources)
- [Химия и материаловедение](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-sources)
- [Метрология, systems и надёжность](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-sources)
- [Поверхности, сложные потоки и энергетическая физика](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-sources)
- [Земля, специальные материалы и окружающая среда](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-sources)
- [Единый каталог всех дисциплин](INTEGRATED_SCIENCE_CURRICULUM_RU.md#curriculum-sources)
<!-- SOURCES:END -->
