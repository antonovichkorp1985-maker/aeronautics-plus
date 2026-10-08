# Единый порядок изучения наук для проекта

<!-- TOC:START -->
## Оглавление

- [0. Как устроена вся база](#curriculum-0)
- [1. Карта prerequisites](#curriculum-1)
- [2. Правило прохождения каждой темы](#curriculum-2)
- [3. Входная диагностика](#curriculum-3)
- [4. Этап 0 — числа, алгебра, единицы и научная грамотность](#curriculum-4)
- [5. Этап 1 — геометрия, тригонометрия, векторы и эксперимент](#curriculum-5)
- [6. Этап 2 — calculus I, механика и общая химия параллельно](#curriculum-6)
- [7. Этап 3 — доказательства, linear algebra и multivariable calculus](#curriculum-7)
- [8. Этап 4 — ODE, dynamics, electromagnetism и waves](#curriculum-8)
- [9. Этап 5 — thermodynamics и physical chemistry как единая тема](#curriculum-9)
- [10. Этап 6 — probability, statistics, metrology и experiments](#curriculum-10)
- [11. Этап 7 — numerical methods, software credibility и UQ](#curriculum-11)
- [12. Этап 8 — continuum mechanics и transport](#curriculum-12)
- [13. Этап 9 — chemical structure, reactions и analytical chemistry](#curriculum-13)
- [14. Этап 10 — surfaces, rheology, multiphase и tribology](#curriculum-14)
- [15. Этап 11 — materials, manufacturing, damage и reliability](#curriculum-15)
- [16. Этап 12 — control, systems engineering и safety](#curriculum-16)
- [17. После общего ядра: как выбирать ветвь](#curriculum-17)
- [18. Что учить сейчас для Aeronautics Plus](#curriculum-18)
- [19. Weekly study loop](#curriculum-19)
- [20. Что изучать «по мере требования», а что нельзя откладывать](#curriculum-20)
- [21. Единые gates готовности](#curriculum-21)
- [22. Правило распределения новых материалов по файлам](#curriculum-22)
- [23. Итоговый порядок одной строкой](#curriculum-23)
- [Полный сводный каталог источников](#curriculum-sources)

> [Карта покрытия](SCIENCE_COVERAGE_MAP_RU.md) · [Общий каталог источников](#curriculum-sources)
<!-- TOC:END -->


Статус: главный навигационный документ. Если непонятно, что учить дальше, начинать
нужно отсюда, а не с отдельных списков книг.

Дата систематизации: **2 октября 2026 года**.

Этот файл не заменяет предметные справочники. Он задаёт **одну логически связанную
очередь**, а подробная теория, формулы, задачи и источники остаются в профильных файлах.
Так сохраняется единство физики и химии без превращения одного документа в неудобную
книгу на десятки тысяч строк.

---

<a id="curriculum-0"></a>
## 0. Как устроена вся база

### 0.1 Главный принцип

Учиться только «по мере требования» нельзя: так появляются пробелы в единицах,
математике, термодинамике и измерениях. Но и полностью заканчивать одну науку перед
началом другой неправильно: механика помогает понять анализ, физика — химическую
термодинамику, а химия — материалы и топливо.

Поэтому используется модель:

1. **общее обязательное ядро** — проходится последовательно;
2. **параллельные физико-химические связки** — изучаются вместе;
3. **отраслевые ветви** — выбираются после prerequisites;
4. **just-in-time углубление** — допускается внутри уже подготовленной ветви;
5. **верификация** — идёт на каждом этапе, а не в самом конце.

### 0.2 Роли файлов

| Файл | Что в нём является главным | Чего в нём не дублировать |
|---|---|---|
| [`MATHEMATICS_STUDY_GUIDE_RU.md`](MATHEMATICS_STUDY_GUIDE_RU.md) | математические понятия, доказательства, solvers | отраслевую физику и chemistry facts |
| [`REALISM_STUDY_GUIDE_RU.md`](REALISM_STUDY_GUIDE_RU.md) | механика, thermo, aero/hydro, transport vehicles | общую химию и полный reliability course |
| [`CHEMISTRY_MOD_STUDY_GUIDE_RU.md`](CHEMISTRY_MOD_STUDY_GUIDE_RU.md) | вещества, реакции, equilibrium, kinetics, materials, petroleum, nuclear chemistry | механику аппаратов и общий DSP |
| [`MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md`](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md) | measurement, computation, V&V, control, reliability, manufacturing | предметные property tables |
| [`INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md`](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md) | surfaces, complex flows, tribology, combustion, acoustics, plasma/vacuum/cryo | базовую fluid mechanics и общую chemistry |
| [`EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md`](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md) | Earth/resources, special materials, water/environment/LCA | общую metallurgy и базовую crystallography |
| [`SCIENCE_COVERAGE_MAP_RU.md`](SCIENCE_COVERAGE_MAP_RU.md) | аудит полноты, dependencies и приоритеты | линейный учебный курс |

### 0.3 Почему не один гигантский предметный файл

Физика и химия неразрывны, но у них разные reference data, задачи и методы проверки.
Один монолит быстро создаёт три проблемы:

- трудно понять, где источник конкретного коэффициента;
- невозможно независимо обновлять standards/data;
- порядок обучения смешивается со справочником реализации.

Поэтому **этот файл — единая программа**, а семь остальных — её учебники и reference
volumes.

---

<a id="curriculum-1"></a>
## 1. Карта prerequisites

```text
арифметика + алгебра + единицы
  ├─> геометрия + тригонометрия + векторы
  │    ├─> mechanics
  │    ├─> fields/waves
  │    └─> molecular geometry/crystals
  ├─> functions + calculus
  │    ├─> thermodynamics
  │    ├─> kinetics
  │    ├─> transport
  │    └─> optimization
  └─> logic + proof + dimensional analysis

calculus + linear algebra
  ├─> ODE/dynamics/control
  ├─> multivariable/vector calculus
  │    ├─> fluid mechanics/EM
  │    └─> PDE/continuum transport
  └─> quantum/statistical foundations

probability + statistics
  ├─> metrology/experiment
  ├─> uncertainty/UQ
  ├─> reliability
  └─> geostatistics/environmental data

general chemistry + thermodynamics
  ├─> equilibrium/kinetics/electrochemistry
  ├─> organic/inorganic/coordination chemistry
  ├─> materials and corrosion
  └─> process engineering

mechanics + transport + chemistry + measurement
  ├─> rotors/vehicles
  ├─> chemical plants/refining
  ├─> mineral processing/metallurgy
  ├─> environmental systems
  └─> energy systems
```

Стрелка означает prerequisite. Темы на одном уровне можно проходить параллельно, если
предыдущий уровень уже освоен.

---

<a id="curriculum-2"></a>
## 2. Правило прохождения каждой темы

Каждая тема проходит один и тот же цикл:

1. **Определения:** выписать quantities, units и coordinate/state conventions.
2. **Смысл:** объяснить явление словами и нарисовать схему.
3. **Вывод:** получить хотя бы одну основную формулу из определения или conservation.
4. **Задачи:** решить несколько задач без подглядывания.
5. **Расчёт:** реализовать маленький notebook/script.
6. **Пределы:** проверить ноль, знак, infinity/asymptotic и смену regime.
7. **Измерение:** понять, как quantity реально наблюдается.
8. **Uncertainty:** назвать основные источники разброса и model discrepancy.
9. **Benchmark:** сравнить с независимым ответом или dataset.
10. **Игровое упрощение:** только теперь выбрать reduced model.

Чтение без задач даёт узнавание. Код без вывода даёт случайно работающий calculator.
Формула без measurement и valid range не даёт инженерную модель.

---

<a id="curriculum-3"></a>
## 3. Входная диагностика

Перед стартом проверить, можете ли вы:

- свободно работать с дробями, степенями, scientific notation;
- преобразовывать простые algebraic equations;
- переводить единицы;
- строить график функции;
- применять sin/cos и векторы;
- объяснить force, energy, power, pressure, temperature;
- отличить element, compound, mixture и phase;
- записать простую программу с function/test;
- вычислить mean и оценить spread нескольких измерений.

Если не выполнены первые пять пунктов — начать с этапа 0. Если выполнены все — всё равно
пройти контрольные проекты этапов 0–2 и перейти к месту первого реального затруднения.

---

<a id="curriculum-4"></a>
## 4. Этап 0 — числа, алгебра, единицы и научная грамотность

### Prerequisites

Нет.

### Учить

- arithmetic, fractions, ratios, percentages;
- powers, roots, logarithms;
- equations/inequalities;
- functions and graphs;
- scientific notation/order of magnitude;
- SI base/derived units;
- significant digits without premature rounding;
- dimensional homogeneity;
- basic spreadsheet/Python;
- source reliability and citation.

### Одновременно из физики и химии

- length, mass, time, temperature, amount of substance;
- velocity/acceleration;
- density/pressure;
- atom/molecule/mole;
- mass fraction/molar concentration;
- energy/power distinction.

### Читать

- [математика: ступени 0–1](MATHEMATICS_STUDY_GUIDE_RU.md#math-3);
- [chemistry: обязательное ядро](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-3);
- [metrology: базовые различия](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-3).

### Контрольный результат

Unit converter с automated tests, который отвергает несовместимые dimensions и не
смешивает °C с K в формулах.

---

<a id="curriculum-5"></a>
## 5. Этап 1 — геометрия, тригонометрия, векторы и эксперимент

### Prerequisites

Этап 0.

### Учить

- plane/solid geometry;
- radians;
- sin/cos/tan;
- vectors, components, dot/cross product;
- coordinate frames;
- basic descriptive statistics;
- graph/table reading;
- measurement result, resolution, repeatability;
- uncertainty as interval/model, not «ошибка прибора».

### Физико-химическая связка

- vector displacement/force;
- torque lever arm;
- pressure force;
- molecular geometry;
- crystal directions as later motivation;
- calibration line and residuals.

### Читать

- [математика: полная лестница](MATHEMATICS_STUDY_GUIDE_RU.md#math-2);
- [physics: общая механика](REALISM_STUDY_GUIDE_RU.md#physics-3);
- [measurement route](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-3).

### Контрольный результат

Vector/coordinate-frame notebook: преобразовать force/torque между frames и проверить
инвариантность физического результата.

---

<a id="curriculum-6"></a>
## 6. Этап 2 — calculus I, механика и общая химия параллельно

### Prerequisites

Этапы 0–1.

### Математика

- limits;
- derivative;
- integral;
- fundamental theorem;
- Taylor approximation;
- simple separable ODE intuition.

### Физика

- kinematics;
- Newton laws;
- work/energy;
- momentum;
- rotation;
- oscillation baseline;
- conservation laws.

### Химия

- atoms/isotopes/electrons;
- periodic trends;
- bonding;
- mole/stoichiometry;
- gases/solutions;
- acid/base and redox basics;
- phase vocabulary.

### Почему вместе

Derivative становится velocity/rate; integral — displacement/work/amount; conservation
одинаково связывает momentum, energy и chemical elements.

### Читать

- [математика: анализ одной переменной](MATHEMATICS_STUDY_GUIDE_RU.md#math-5);
- [physics: механика](REALISM_STUDY_GUIDE_RU.md#physics-3);
- [chemistry: общая и неорганическая](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-6).

### Gate 2

Самостоятельно вывести движение под constant force, energy balance и balanced chemical
reaction; реализовать их с unit tests.

---

<a id="curriculum-7"></a>
## 7. Этап 3 — доказательства, linear algebra и multivariable calculus

### Prerequisites

Уверенный этап 2.

### Учить

- logic and proof;
- sequences/series/convergence;
- partial derivatives;
- multiple integrals;
- grad/div/curl;
- line/surface integrals;
- matrices/linear systems;
- basis/eigenvalues;
- least squares;
- conditioning.

### Применения

- inertia tensor;
- coupled modes;
- conservation in control volume;
- fields and fluxes;
- chemical balance matrix;
- spectroscopy/quantum state preparation;
- regression/calibration.

### Читать

- [proofs](MATHEMATICS_STUDY_GUIDE_RU.md#math-4);
- [multivariable calculus](MATHEMATICS_STUDY_GUIDE_RU.md#math-6);
- [linear algebra](MATHEMATICS_STUDY_GUIDE_RU.md#math-7).

### Gate 3

Решить coupled linear model, определить eigenmodes и проверить conservation через
surface/control-volume formulation.

---

<a id="curriculum-8"></a>
## 8. Этап 4 — ODE, dynamics, electromagnetism и waves

### Prerequisites

Этап 3; basic calculus можно продолжать параллельно.

### Учить

- first/second-order ODE;
- systems/state space;
- equilibrium/stability;
- harmonic response/resonance;
- Fourier/complex-number basics;
- electrostatics/current/circuits;
- magnetic fields/induction;
- mechanical/electromagnetic waves;
- boundary/initial conditions.

### Химические связи

- reaction-rate ODE;
- RC analogy with electrochemistry;
- spectroscopy and wave–matter interaction;
- diffusion as preparation for PDE;
- molecular vibration/rotation concepts.

### Читать

- [ODE/dynamical systems](MATHEMATICS_STUDY_GUIDE_RU.md#math-8);
- [complex/Fourier/Laplace](MATHEMATICS_STUDY_GUIDE_RU.md#math-9);
- [physics: control and dynamics](REALISM_STUDY_GUIDE_RU.md#physics-7);
- [chemistry: electricity and machines](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-16).

### Gate 4

Simulate damped oscillator и first-order sensor, compare analytic/numerical solutions и
показать resonance/phase lag.

---

<a id="curriculum-9"></a>
## 9. Этап 5 — thermodynamics и physical chemistry как единая тема

### Prerequisites

Calculus I, general chemistry, mechanics/energy.

### Сначала общая thermodynamics

- system/boundary/state;
- intensive/extensive quantities;
- equations of state;
- first/second laws;
- work/heat;
- internal energy/enthalpy/entropy;
- cycles and efficiency;
- phase transitions.

### Затем physical chemistry

- chemical potential;
- Gibbs/Helmholtz energies;
- activity/fugacity;
- phase/chemical equilibrium;
- mixtures;
- electrochemical potential;
- statistical interpretation;
- kinetic vs equilibrium distinction.

### Читать

- [physics: thermo/heat transfer](REALISM_STUDY_GUIDE_RU.md#physics-6);
- [chemistry: physical/molecular/quantum](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-8);
- [chemistry: equilibrium/kinetics/catalysis](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-9).

### Gate 5

Закрыть mass/element/energy balance vessel, найти equilibrium limit и отдельно объяснить,
почему реальный rate может быть мал.

---

<a id="curriculum-10"></a>
## 10. Этап 6 — probability, statistics, metrology и experiments

### Prerequisites

Algebra, calculus basics, linear algebra.

### Учить

- random variables/distributions;
- expectation/variance/covariance;
- estimation/confidence;
- regression;
- hypothesis/model checking;
- measurement model;
- Type A/B uncertainty;
- calibration/traceability;
- sampling/aliasing/filtering;
- experiment design;
- sensor dynamics.

### Почему до сложных solvers

Без measurement uncertainty невозможно понять, точнее ли CFD/DFT новой correlation и
прошла ли игровая mechanic validation.

### Читать

- [probability/statistics](MATHEMATICS_STUDY_GUIDE_RU.md#math-10);
- [metrology](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-3);
- [sensors](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-4);
- [signals](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-5);
- [experiment/system identification](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-6).

### Gate 6

Calibration experiment с held-out validation, residual diagnostics и complete uncertainty
budget.

---

<a id="curriculum-11"></a>
## 11. Этап 7 — numerical methods, software credibility и UQ

### Prerequisites

Calculus, linear algebra, ODE, probability.

### Учить

- floating-point behavior;
- root finding/linear/nonlinear systems;
- ODE integration;
- discretization;
- interpolation/approximation;
- optimization;
- PDE baseline;
- conditioning/stability/convergence;
- code vs solution verification;
- validation;
- sensitivity/UQ;
- reproducibility/provenance;
- automated/property/regression tests.

### Читать

- [numerical methods](MATHEMATICS_STUDY_GUIDE_RU.md#math-12);
- [optimization/control](MATHEMATICS_STUDY_GUIDE_RU.md#math-13);
- [numerical credibility](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-7);
- [uncertainty/sensitivity](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-8).

### Gate 7

One analytic benchmark + manufactured/independent test + timestep/grid refinement +
uncertainty propagation. Только после этого solver можно использовать для balance data.

---

<a id="curriculum-12"></a>
## 12. Этап 8 — continuum mechanics и transport

### Prerequisites

Vector calculus, ODE/PDE basics, thermo, numerical methods.

### Учить

- stress/strain and constitutive law;
- control-volume conservation;
- fluid statics/dynamics;
- viscosity/boundary layer;
- compressibility;
- heat conduction/convection/radiation;
- mass diffusion/convection;
- dimensionless groups;
- turbulence as modeled hierarchy;
- coupled heat/mass/momentum transport.

### Читать

- [physics: structures](REALISM_STUDY_GUIDE_RU.md#physics-4);
- [fluid mechanics](REALISM_STUDY_GUIDE_RU.md#physics-5);
- [thermo/heat transfer](REALISM_STUDY_GUIDE_RU.md#physics-6);
- [PDE](MATHEMATICS_STUDY_GUIDE_RU.md#math-11).

### Gate 8

Control-volume model with mass, momentum and energy residuals plus dimensionless regime
justification.

---

<a id="curriculum-13"></a>
## 13. Этап 9 — chemical structure, reactions и analytical chemistry

### Prerequisites

General chemistry, thermo, kinetics basics, measurement.

### Учить

- inorganic/coordination chemistry;
- organic mechanisms;
- stereochemistry/selectivity;
- organometallic catalysis;
- electrochemistry;
- polymers;
- photochemistry/supramolecular/nano;
- spectroscopy/chromatography/electroanalysis;
- computational chemistry hierarchy;
- sampling and quality decision.

Quantum/electronic structure углублять после linear algebra, ODE/waves и physical
chemistry; не начинать с запуска DFT package.

### Читать

- [general/inorganic](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-6);
- [organic](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-7);
- [specialized/computational](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-8a);
- [analytical chemistry](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-10);
- [electrochemistry/corrosion](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-11);
- [polymers](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-12).

### Gate 9

Для выбранной safe reaction/material property: atom/charge/energy balance, method of
measurement, uncertainty, kinetic/equilibrium distinction и source provenance.

---

<a id="curriculum-14"></a>
## 14. Этап 10 — surfaces, rheology, multiphase и tribology

### Prerequisites

Physical chemistry, fluid mechanics, materials basics, transport.

### Учить в этом порядке

1. surface thermodynamics/wetting;
2. adsorption and surface reaction;
3. colloids/surfactants;
4. rheology/viscoelasticity;
5. bubbles/drops/particles;
6. porous media/filtration/membranes;
7. granular media;
8. contact/lubrication/wear.

### Читать

- [surface/wetting](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-2);
- [adsorption/catalysis](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-3);
- [colloids](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-4);
- [rheology](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-5);
- [multiphase](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-6);
- [porous/membranes](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-7);
- [granular](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-8);
- [tribology](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-9).

### Gate 10

Один separator/contact module, где regime, distribution, fouling/lubrication и energy loss
изменяют result, а не являются декоративными тегами.

---

<a id="curriculum-15"></a>
## 15. Этап 11 — materials, manufacturing, damage и reliability

### Prerequisites

Mechanics, chemistry, thermo, probability, measurement.

### Последовательность

1. bonding/crystal/amorphous structure;
2. phase diagrams/transformations;
3. metals/polymers/ceramics/glass/cement;
4. process–structure–property;
5. manufacturing/tolerances/residual stress;
6. corrosion/environment;
7. fracture/fatigue/creep/wear;
8. inspection/NDT;
9. reliability/maintenance/common cause.

### Читать

- [chemistry: materials](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-13);
- [metallurgy](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-14);
- [ceramics/glass/cement/refractories](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-9);
- [condensed matter](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-13);
- [fracture/fatigue](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-11);
- [reliability](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-12);
- [manufacturing/NDT](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-13).

### Gate 11

Component model с material state, manufacturing variation, load history, damage,
inspection uncertainty и nonmagical failure/maintenance.

---

<a id="curriculum-16"></a>
## 16. Этап 12 — control, systems engineering и safety

### Prerequisites

ODE/state space, measurement, numerical credibility, domain model.

### Учить

- requirements/interfaces/traceability;
- static/dynamic degrees of freedom;
- stability/feedback;
- PID/actuator/sensor limitations;
- estimation/observability;
- recycle/purge and plantwide control;
- hazards and independent protection layers;
- FMEA/fault/event trees;
- configuration/change management;
- verification matrix;
- human factors/alarms;
- lifecycle/economic trade study.

### Читать

- [systems engineering](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-9);
- [process systems/control](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-10);
- [human factors](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-14);
- [operations/economics](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-15);
- [chemistry: safety/environment](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-18).

### Gate 12

Requirement → model → interface → hazard → test trace для одной complete mechanic.

---

<a id="curriculum-17"></a>
## 17. После общего ядра: как выбирать ветвь

Нельзя начинать все ветви одновременно. Выбирается один vertical slice, остальные темы
остаются reference до появления задачи.

### Ветка A — авиация, роторы и транспорт

Порядок:

1. rigid-body rotation;
2. fluid mechanics/dimensionless analysis;
3. aerofoil/propeller baseline;
4. power/torque/RPM;
5. rotor momentum/blade-element;
6. control/stability;
7. structures/fatigue;
8. bearings/tribology;
9. vibration/acoustics;
10. multi/coaxial rotor interference;
11. measurement/V&V/game balance.

Подробно:

- [aircraft](REALISM_STUDY_GUIDE_RU.md#physics-8);
- [helicopters/coaxial](REALISM_STUDY_GUIDE_RU.md#physics-9);
- [propellers/turbomachinery](REALISM_STUDY_GUIDE_RU.md#physics-12);
- [acoustics](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-12).

Первый production slice проекта идёт именно по этой ветви.

### Ветка B — химическая технология и нефтехимия

Порядок:

1. composition/phase/property data;
2. balances and thermo;
3. kinetics/catalysis;
4. analytical quality;
5. transport;
6. unit operations;
7. recycle/heat integration/control;
8. surfaces/rheology/multiphase;
9. corrosion/material selection;
10. process safety/waste/LCA;
11. petroleum/petrochemical specialization.

Подробно:

- [petroleum/refining](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-7a);
- [process engineering](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-15);
- [process systems/control](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-10);
- [combustion](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-10).

### Ветка C — Земля, руда, metallurgy и special materials

Порядок:

1. geology/petrology;
2. mineralogy/crystallography;
3. geochemistry;
4. sampling/geophysics/resource uncertainty;
5. mining/geotechnics;
6. mineral processing;
7. metallurgy/ceramics/glass/cement;
8. manufacturing/property;
9. water/tailings/environment;
10. LCA/closure.

Подробно:

- [geology](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-2);
- [mineral processing](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-8);
- [metallurgy](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-14);
- [water/environment](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-16).

### Ветка D — electricity, electronics и photonics

Порядок:

1. circuits/EM;
2. quantum/statistical basics;
3. condensed matter;
4. semiconductors/dielectrics/magnetics;
5. optical wave/light–matter interaction;
6. devices/sensors;
7. manufacturing/thermal/reliability;
8. control and grid integration.

Подробно:

- [electricity/machines](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-16);
- [condensed matter](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-13);
- [electronic materials](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-14);
- [optics/photonics](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-15).

### Ветка E — water, environment и climate context

Порядок:

1. aqueous equilibrium/speciation;
2. colloids/surfaces;
3. transport/porous media;
4. treatment unit operations;
5. biological treatment when needed;
6. atmospheric chemistry/dispersion;
7. exposure/ecotoxicology;
8. LCA/circularity;
9. measurement and monitoring.

Подробно:

- [water](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-16);
- [atmosphere](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-17);
- [environmental fate](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-18);
- [LCA](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-19).

### Ветка F — nuclear/radiation, только поздняя

До неё обязательны thermo/heat transfer, quantum/atomic, measurement, numerical V&V,
materials, control, reliability и defense in depth.

Порядок:

1. atomic/nuclear physics;
2. radiation interaction/detection;
3. decay/activation;
4. transport/criticality concepts;
5. fuel/materials/heat removal;
6. control/protection;
7. waste/safeguards/regulation;
8. independent expert review.

Подробно: [nuclear and radiochemistry](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-17).
Оружейные и enrichment-production инструкции исключены.

### Ветка G — biotechnology/agro, условная

Только если будет реальная integration need:

1. biochemistry/cell metabolism;
2. enzyme/growth kinetics;
3. transport/bioreactors;
4. biosafety/contamination;
5. downstream/waste;
6. soil/water/nutrients;
7. plant/environment balance.

Подробно:

- [biotechnology](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-20);
- [soil/agrochemistry](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-21).

---

<a id="curriculum-18"></a>
## 18. Что учить сейчас для Aeronautics Plus

Не нужно сначала завершать petroleum, geology или biotechnology. Текущий рациональный
порядок:

1. units/frames/vector rotation;
2. rotational mechanics and power/torque;
3. propeller/rotor dimensionless coefficients;
4. RPM-dependent thrust benchmark;
5. two-rotor induced-flow/interference model;
6. numerical convergence and limiting cases;
7. in-game sensor/readout and uncertainty;
8. bearing/tribology loss;
9. fatigue/load history;
10. vibration/acoustic signature;
11. control/interface contract;
12. multiplayer/save-load/regression validation.

Эта последовательность сохраняет уже исправленную ось вращения и не требует сразу
встраивать всю scientific base в один tick solver.

---

<a id="curriculum-19"></a>
## 19. Weekly study loop

Пример для 8–12 часов в неделю:

- 2–3 часа — теория/конспект;
- 3–4 часа — задачи вручную;
- 1–2 часа — numerical notebook;
- 1 час — source/data audit;
- 1–2 часа — benchmark или маленький project;
- 30 минут — обновить glossary/model passport.

Каждые 4–6 недель:

- один closed-book контроль;
- один independent benchmark;
- один review assumptions;
- решение: продолжить, повторить prerequisite или перейти к branch.

Календарь вторичен. Gate считается пройденным только по результату.

---

<a id="curriculum-20"></a>
## 20. Что изучать «по мере требования», а что нельзя откладывать

### Нельзя откладывать

- algebra/trigonometry/calculus basics;
- SI/dimensions;
- conservation;
- probability/measurement uncertainty;
- numerical verification;
- source provenance;
- safety boundary;
- requirements/interfaces/tests.

### Можно изучать just-in-time после core

- advanced PDE/tensors;
- turbulence closure;
- DFT/molecular dynamics;
- detailed plasma;
- cryogenics;
- advanced geostatistics;
- semiconductor fabrication;
- full LCA;
- biotechnology;
- relativity/astrophysics.

Just-in-time означает «после prerequisites и перед mechanic», а не «прочитать случайную
статью и сразу перенести коэффициент в код».

---

<a id="curriculum-21"></a>
## 21. Единые gates готовности

| Gate | Можно двигаться дальше, если… |
|---|---|
| G0 | units/dimensions проходят tests |
| G1 | vectors/frames/signs объясняются без guess |
| G2 | mechanics и stoichiometry выводятся из conservation |
| G3 | linear/multivariable model решается и проверяется |
| G4 | ODE/transient/stability воспроизводятся |
| G5 | thermo equilibrium отделён от kinetics |
| G6 | measurement result содержит uncertainty |
| G7 | numerical solution имеет convergence/benchmark |
| G8 | transport regime обоснован dimensionless groups |
| G9 | chemistry/material property traceable to method/data |
| G10 | interface/multiphase/contact state не магический |
| G11 | damage/reliability зависят от history/environment |
| G12 | requirement→test и hazard→protection trace закрыты |
| Branch | domain benchmark пройден до game balancing |

---

<a id="curriculum-22"></a>
## 22. Правило распределения новых материалов по файлам

Когда появляется новая тема:

1. prerequisites/order/link добавляются **сюда**;
2. новая область и status добавляются в **coverage map**;
3. mathematical method идёт в **mathematics guide**;
4. physical law/vehicle behavior — в **realism guide**;
5. substance/reaction/property — в **chemistry guide**;
6. measurement/V&V/control/reliability/manufacturing — в **measurement systems guide**;
7. surface/complex flow/tribology/combustion/plasma/vacuum/cryo — в **interfacial guide**;
8. natural resource/special material/water/environment — в **Earth/materials guide**;
9. implementation details — в `docs/architecture/`, а не в учебный справочник;
10. один primary owner хранит полное объяснение, остальные файлы дают link и context.

Это предотвращает conflicting copies формул.

---

<a id="curriculum-23"></a>
## 23. Итоговый порядок одной строкой

`алгебра/SI → геометрия/векторы → calculus + mechanics + general chemistry → linear/
multivariable math → ODE/fields/waves → thermodynamics + physical chemistry →
probability/metrology/experiments → numerical V&V/UQ → continuum/transport → chemical
structure/analysis → surfaces/multiphase/tribology → materials/manufacturing/reliability →
control/systems/safety → одна выбранная отраслевая ветвь → verified gameplay model`.

Это и есть основной учебный маршрут проекта. Всё остальное подключается к нему по
prerequisites, а не по случайному интересу или порядку появления модов.

---

<!-- SOURCES:START -->
<a id="curriculum-sources"></a>
## Полный сводный каталог источников

Ниже намеренно повторены источники всех предметных маршрутов. Для контекста, уровня сложности и области применимости следует переходить в соответствующий файл.

### Быстрые ссылки на предметные списки

- [Математика](MATHEMATICS_STUDY_GUIDE_RU.md#math-sources)
- [Физика и инженерная механика](REALISM_STUDY_GUIDE_RU.md#physics-sources)
- [Химия и материаловедение](CHEMISTRY_MOD_STUDY_GUIDE_RU.md#chem-sources)
- [Метрология, systems и надёжность](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md#measure-sources)
- [Поверхности, сложные потоки и энергетическая физика](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md#interface-sources)
- [Земля, специальные материалы и окружающая среда](EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md#earth-sources)

### Математика: книги, отчёты и стандарты

- Д. Веллеман, «Как доказывать теоремы» / Daniel Velleman, *How to Prove It*;
- Р. Курант, Г. Роббинс, «Что такое математика?» **[ПР, Б–С]**
- Томас, Вейр, Хасс, «Математический анализ» **[ПР, Б–С]**;
- Т. Апостол, «Математический анализ», т. 1–2 **[ПР, С]**;
- В. А. Зорич, «Математический анализ», т. 1–2 **[М, С–П]**;
- Г. М. Фихтенгольц, «Курс дифференциального и интегрального исчисления», т. 1–3 **[В, С–П]**;
- Б. П. Демидович, «Сборник задач и упражнений по математическому анализу» — практика, не замена
  теории. **[М/В, Б–С]**
- Marsden & Tromba, *Vector Calculus*;
- Schey, *Div, Grad, Curl, and All That*;
- Зорич, т. 2; Ильин и Позняк, «Основы математического анализа» **[В, С]**.
- Strang, *Introduction to Linear Algebra*;
- Axler, *Linear Algebra Done Right*;
- Trefethen & Bau, *Numerical Linear Algebra*;
- И. Р. Шафаревич, А. О. Ремизов, «Линейная алгебра и геометрия» **[М/В, С]**;
- Д. В. Беклемишев, «Курс аналитической геометрии и линейной алгебры» **[В, Б–С]**;
- Ф. Р. Гантмахер, «Теория матриц», т. 1–2 **[М, П/R]**.
- Strogatz, *Nonlinear Dynamics and Chaos*;
- Boyce, DiPrima & Meade, *Elementary Differential Equations*;
- В. И. Арнольд, «Обыкновенные дифференциальные уравнения» **[М, С–П]**;
- Л. С. Понтрягин, «Обыкновенные дифференциальные уравнения» **[М, П]**;
- Л. Э. Эльсгольц, «Дифференциальные уравнения и вариационное исчисление» **[М/В, С–П]**.
- Ahlfors, *Complex Analysis* / «Комплексный анализ» **[ПР, С–П]**;
- Churchill & Brown, *Complex Variables and Applications*;
- Bracewell, *The Fourier Transform and Its Applications*;
- В. И. Смирнов, «Курс высшей математики», соответствующие тома **[М/В, С–П]**.
- Blitzstein & Hwang, *Introduction to Probability*;
- Casella & Berger, *Statistical Inference*;
- W. Feller, «Введение в теорию вероятностей и её приложения» **[ПР, С–П]**;
- А. Н. Колмогоров, «Основные понятия теории вероятностей» **[М, П/И]**;
- Б. В. Гнеденко, «Курс теории вероятностей» **[М, С–П]**;
- А. Н. Ширяев, «Вероятность» **[М, П]**.
- Strauss, *Partial Differential Equations: An Introduction*;
- Haberman, *Applied Partial Differential Equations*;
- Evans, *Partial Differential Equations* — продвинутый строгий курс;
- Courant & Hilbert, «Методы математической физики» **[ПР, П]**;
- А. Н. Тихонов, А. А. Самарский, «Уравнения математической физики» **[М, П]**;
- В. С. Владимиров, «Уравнения математической физики» **[М, П]**.
- Higham, *Accuracy and Stability of Numerical Algorithms*;
- Sauer, *Numerical Analysis*;
- LeVeque, *Finite Difference Methods for ODE and PDE*;
- Ferziger, Perić & Street, *Computational Methods for Fluid Dynamics*;
- А. А. Самарский, «Теория разностных схем» **[М, П]**;
- С. К. Годунов, В. С. Рябенький, «Разностные схемы» **[М, П]**;
- Н. Н. Калиткин, «Численные методы» **[В, С–П]**;
- Н. С. Бахвалов, Н. П. Жидков, Г. М. Кобельков, «Численные методы» **[В, С–П]**.
- Nocedal & Wright, *Numerical Optimization*;
- Bertsekas, *Nonlinear Programming*;
- Б. Т. Поляк, «Введение в оптимизацию» **[М/В, С–П]**.
- И. М. Гельфанд, С. В. Фомин, «Вариационное исчисление» **[М, С–П]**;
- А. Н. Колмогоров, С. В. Фомин, «Элементы теории функций и функционального анализа» **[М, П]**;
- Л. В. Канторович, Г. П. Акилов, «Функциональный анализ» **[М, П]**;
- В. И. Арнольд, «Математические методы классической механики» **[М, П]**.
- Л. И. Седов, «Методы подобия и размерности в механике» **[М, С–П]**;
- Barenblatt, *Scaling* и «Подобие, автомодельность, промежуточная асимптотика» **[М, П]**;
- Bender & Orszag, *Advanced Mathematical Methods for Scientists and Engineers*;
- Hinch, *Perturbation Methods*;
- Holmes, *Introduction to Perturbation Methods*.
- **А. П. Киселёв, «Геометрия»** — строгая элементарная геометрия. **[В, И–Б]**
- **М. Я. Выгодский, «Справочник по элементарной математике»** — справочник, не линейный курс. **[В,
  R]**
- **И. Н. Бронштейн, К. А. Семендяев, «Справочник по математике для инженеров и учащихся втузов»** —
  многократно переводившийся международный справочник. **[М, R]**
- **Н. С. Пискунов, «Дифференциальное и интегральное исчисление», т. 1–2** — международно
  переводившийся инженерный курс. **[М/В, Б–С]**
- **Г. М. Фихтенгольц, «Курс дифференциального и интегрального исчисления», т. 1–3** — подробный
  строгий классический курс. **[В, С–П]**
- **В. А. Зорич, «Математический анализ», т. 1–2** — современный строгий курс, изданный Springer на
  английском. **[М, С–П]**
- **Б. П. Демидович, «Сборник задач и упражнений по математическому анализу»** — большой тренажёр
  после теории. **[М/В, Б–С]**
- **А. И. Кострикин, «Введение в алгебру»** — сильный университетский курс. **[М/В, С–П]**
- **А. Г. Курош, «Курс высшей алгебры»** — классика с международными переводами. **[М, С–П]**
- **Ф. Р. Гантмахер, «Теория матриц»** — матрицы, спектр, устойчивость и приложения; продолжает
  издаваться AMS. **[М, П/R]**
- **Д. В. Беклемишев, «Курс аналитической геометрии и линейной алгебры»**. **[В, Б–С]**
- **В. И. Арнольд, «Обыкновенные дифференциальные уравнения»** **[М, С–П]**;
- **Л. С. Понтрягин, «Обыкновенные дифференциальные уравнения»** **[М, П]**;
- **А. Н. Тихонов, А. А. Самарский, «Уравнения математической физики»** **[М, П]**;
- **В. С. Владимиров, «Уравнения математической физики»** **[М, П]**;
- **И. Г. Петровский, «Лекции об уравнениях с частными производными»** **[М, П]**;
- **В. И. Смирнов, «Курс высшей математики», т. 1–5** — большой справочный мост к математической
  физике. **[М/В, С–П/R]**.
- **А. Н. Колмогоров, «Основные понятия теории вероятностей»** **[М, П/И]**;
- **Б. В. Гнеденко, «Курс теории вероятностей»** **[М, С–П]**;
- **А. Н. Ширяев, «Вероятность»** **[М, П]**;
- **Колмогоров—Фомин, «Элементы теории функций и функционального анализа»** **[М, П]**;
- **Гельфанд—Фомин, «Вариационное исчисление»** **[М, С–П]**;
- **Канторович—Акилов, «Функциональный анализ»** **[М, П]**.
- **Самарский, «Теория разностных схем»** **[М, П]**;
- **Годунов—Рябенький, «Разностные схемы»** **[М, П]**;
- **Калиткин, «Численные методы»** **[В, С–П]**;
- **Бахвалов—Жидков—Кобельков, «Численные методы»** **[В, С–П]**.
- Курант—Роббинс, «Что такое математика?» **[ПР, Б]**;
- Томас, «Математический анализ» **[ПР, Б–С]**;
- Апостол, «Математический анализ», т. 1–2 **[ПР, С]**;
- Рудин, «Основы математического анализа» и «Функциональный анализ» **[ПР, С–П]**;
- Стрэнг, «Линейная алгебра и её применения» **[ПР, Б–С]**;
- Курант—Гильберт, «Методы математической физики» **[ПР, П]**;
- Альфорс, «Комплексный анализ» **[ПР, С–П]**;
- Феллер, «Введение в теорию вероятностей и её приложения» **[ПР, С–П]**;
- Пресс, Тьюкольски, Веттерлинг, Фланнери, «Численные рецепты» — исторически важный практический
  справочник; algorithms сверять с современными библиотеками. **[ПР, R]**.

### Математика: онлайн-источники и данные

- OpenStax Algebra and Trigonometry 2e:
  https://openstax.org/details/books/algebra-and-trigonometry-2e
- OpenStax Precalculus 2e: https://openstax.org/details/books/precalculus-2e
- Khan Academy Math: https://www.khanacademy.org/math
- 3Blue1Brown: https://www.3blue1brown.com/
- Р. Хэммэк, *Book of Proof* — легально открытый вводный текст:
  https://richardhammack.github.io/BookOfProof/
- MIT 18.01SC Single Variable Calculus:
  https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/
- OpenStax Calculus, volumes 1–3: https://openstax.org/details/books/calculus-volume-1
- 3Blue1Brown, Essence of Calculus: https://www.3blue1brown.com/topics/calculus
- MIT 18.02SC Multivariable Calculus:
  https://ocw.mit.edu/courses/18-02sc-multivariable-calculus-fall-2010/
- OpenStax Calculus, volume 3: https://openstax.org/details/books/calculus-volume-3
- MIT 18.06SC Linear Algebra, Gilbert Strang:
  https://ocw.mit.edu/courses/18-06sc-linear-algebra-fall-2011/
- 3Blue1Brown, Essence of Linear Algebra: https://www.3blue1brown.com/topics/linear-algebra
- MIT 18.03SC Differential Equations:
  https://ocw.mit.edu/courses/18-03sc-differential-equations-fall-2011/
- MIT OCW search, Dynamical Systems: https://ocw.mit.edu/search/?q=dynamical%20systems
- MIT 18.03 и MIT OCW search по Fourier: https://ocw.mit.edu/search/?q=Fourier
- MIT 18.04 Complex Variables with Applications:
  https://ocw.mit.edu/courses/18-04-complex-variables-with-applications-fall-1999/
- Harvard Stat 110: https://stat110.hsites.harvard.edu/
- MIT 6.041SC Probabilistic Systems Analysis:
  https://ocw.mit.edu/courses/6-041sc-probabilistic-systems-analysis-and-applied-probability-fall-2013/
- Seeing Theory, Brown University: https://seeing-theory.brown.edu/
- NIST/SEMATECH e-Handbook: https://www.itl.nist.gov/div898/handbook/
- JCGM GUM и VIM: https://www.bipm.org/en/committees/jc/jcgm/publications
- MIT OCW search, Partial Differential Equations:
  https://ocw.mit.edu/search/?q=partial%20differential%20equations
- MIT 2.086 Numerical Computation for Mechanical Engineers:
  https://ocw.mit.edu/courses/2-086-numerical-computation-for-mechanical-engineers-fall-2014/
- MIT 18.085 Computational Science and Engineering I:
  https://ocw.mit.edu/courses/18-085-computational-science-and-engineering-i-fall-2008/
- Boyd & Vandenberghe, *Convex Optimization* — официальный открытый текст:
  https://web.stanford.edu/~boyd/cvxbook/
- Колмогоров—Фомин, Dover: https://store.doverpublications.com/products/9780486406831
- Гельфанд—Фомин, Dover:
  https://books.google.com/books/about/Calculus_of_Variations.html?id=CeC7AQAAQBAJ
- Арнольд, Springer, DOI: https://doi.org/10.1007/978-1-4757-2063-1
- PETSc: https://petsc.org/
- FEniCSx: https://fenicsproject.org/
- Gmsh: https://gmsh.info/
- SUNDIALS: https://computing.llnl.gov/projects/sundials
- JAX: https://docs.jax.dev/
- CVXPY: https://www.cvxpy.org/
- https://link.springer.com/book/10.1007/978-3-662-48792-1
- https://bookstore.ams.org/chel-131/
- https://books.google.com/books/about/Equations_of_Mathematical_Physics.html?id=PTmoAAAAQBAJ
- https://www.python.org/
- https://numpy.org/
- https://scipy.org/
- https://www.sympy.org/
- https://pint.readthedocs.io/
- https://pandas.pydata.org/
- https://matplotlib.org/
- https://jupyter.org/
- https://julialang.org/
- https://octave.org/
- https://www.sagemath.org/
- https://dlmf.nist.gov/

### Физика и инженерная механика: книги, отчёты и стандарты

- **Л. Д. Ландау, Е. М. Лифшиц, «Курс теоретической физики»**. Для аддона особенно нужны тома: 1
  «Механика», 2 «Теория поля», 5 «Статистическая физика», 6 «Гидродинамика», 7 «Теория упругости», 8
  «Электродинамика сплошных сред» и 10 «Физическая кинетика». Серия полностью переведена и
  продолжает издаваться за пределами России. Это продвинутый курс, а не первый учебник. **[М, П]**
- **Л. И. Седов, «Методы подобия и размерности в механике»** — один из важнейших источников для
  переноса результатов между моделью и натурой; имеет многократные английские издания как
  *Similarity and Dimensional Methods in Mechanics*. **[М, С–П]**
- **Л. И. Седов, «Механика сплошной среды», т. 1–2** — движение и деформация сплошных сред, тензоры,
  идеальные и вязкие жидкости, газовая динамика. **[М, П]**
- **Д. В. Сивухин, «Общий курс физики», т. 1–5** — механика, термодинамика, электричество, оптика,
  атомная и ядерная физика. Хороший цельный русскоязычный фундамент перед специальными курсами.
  **[В, Б–С]**
- **И. Е. Иродов, «Задачи по общей физике»** и серия «Основные законы» — не замена теории, а
  международно издаваемый задачник для закрепления механики, термодинамики, электродинамики и
  атомной физики. **[М, С]**
- **Л. Г. Лойцянский, «Механика жидкости и газа»** — классический полный русский курс
  гидрогазодинамики. Сверять старые обозначения и свойства веществ с современными справочниками.
  **[В, С–П]**
- **Г. Н. Абрамович, «Прикладная газовая динамика»** — струи, сопла, сжимаемые течения и инженерные
  методы; используется как классический справочник школы ЦАГИ. **[В, П]**
- **Н. Ф. Краснов, «Аэродинамика», т. 1–2** — систематический русскоязычный курс по профилю, крылу,
  сжимаемому и сверхзвуковому течению; выходил также в английском переводе издательства «Мир».
  **[М/В, С–П]**
- **В. И. Феодосьев, «Сопротивление материалов»** — ясный инженерный курс с задачами; издавался на
  английском как *Strength of Materials*. **[М/В, Б–С]**
- **Ю. Н. Работнов, «Механика деформируемого твёрдого тела»** и работы по ползучести — основа для
  длительной прочности и нелинейных материалов; имеет зарубежные издания. **[М, П]**
- **В. В. Болотин, «Динамическая устойчивость упругих систем»** и «Случайные колебания упругих
  систем» — международно переведённые монографии по устойчивости, вибрациям и вероятностным
  нагрузкам. **[М, П]**
- **И. А. Биргер, Р. Р. Мавлютов, «Сопротивление материалов»** и справочники под редакцией Биргера
  по расчёту на прочность — сильная прикладная школа. **[В, С–П]**
- **Ю. М. Лахтин, «Металловедение и термическая обработка металлов»** — доступный русский вводный
  курс перед ASM и современными CALPHAD-источниками. **[В, С]**
- **Г. И. Житомирский, «Конструкция самолётов»** — силовые схемы крыла, фюзеляжа, оперения, шасси и
  соединений. **[В, С]**
- **С. М. Егер и др., «Проектирование самолётов»** — компоновка, масса, аэродинамика, силовая
  установка и проектные компромиссы. **[В, П]**
- **Е. С. Войт, А. И. Ендогур, З. А. Мелик-Саркисян, И. М. Алявдин, «Проектирование конструкций
  самолётов»** — расчёт и конструирование силовых элементов. **[В, П]**
- **И. В. Остославский, «Динамика полёта. Траектории летательных аппаратов»** — уравнения движения и
  характеристики полёта. **[В, П]**
- **И. А. Шаталов, С. М. Егер, А. М. Матвеенко и др., «Основы авиационной техники»** — более
  доступный связующий учебник по аппарату в целом. **[В, С]**
- **М. Л. Миль, А. В. Некрасов, А. С. Браверман и др., «Вертолёты. Расчёт и проектирование»** —
  аэродинамика, колебания, динамическая прочность и проектирование отечественной вертолётной школы.
  **[М/В, П]**
- **Б. Н. Юрьев, «Аэродинамический расчёт вертолёта»** — историческая база теории несущего винта;
  применять совместно с Leishman, Johnson и NASA NDARC. **[М, И]**
- **А. М. Володко, «Основы аэродинамики и динамики полёта вертолётов»** — хороший переходный
  русскоязычный курс. **[В, С]**
- **А. С. Браверман, А. П. Вайнтруб, «Динамика вертолёта. Предельные режимы полёта»**. **[В, П]**
- **Ю. С. Богданов, Р. А. Михеев, Д. Д. Скулков, «Конструкция вертолётов»** и **О. А. Завалов,
  «Конструкция вертолётов»** — втулки, лопасти, трансмиссия, управление и силовые схемы. **[В,
  С–П]**
- **В. В. Семёнов-Тян-Шанский, «Статика и динамика корабля»** — плавучесть, остойчивость, дифферент
  и спуск. **[В, С–П]**
- **Я. И. Войткунский, Ю. И. Фаддеев, К. К. Федяевский, «Гидромеханика»** — фундамент для теории
  корабля. **[В, С–П]**
- **В. Л. Фукельман, «Основы теории корабля»** — доступный первый русский учебник перед Войткунским
  и MIT 2.700. **[В, Б–С]**
- **Л. Прандтль, О. Титьенс, «Гидро- и аэромеханика»** — исторически важный перевод мировой школы;
  коэффициенты и обозначения обновлять. **[ПР, И]**
- **Г. Бэтчелор, «Введение в динамику жидкости»** — фундаментальное изложение вихря, вязкости, малых
  Reynolds и турбулентности. **[ПР, С–П]**
- **А. Шапиро, «Динамика и термодинамика сжимаемого газового потока», т. 1–2** — сопла, скачки,
  трение и теплоподвод. **[ПР, П]**
- **И. Эббот, А. фон Доенхофф, «Теория крыльевых сечений»** — геометрия и данные классических
  профилей NACA; пользоваться вместе с современными базами NASA. **[ПР, С–П]**
- **С. Хёрнер, «Аэродинамическое сопротивление»** — инженерные корреляции с явно указанными режимами
  применимости. **[ПР, R]**
- **Дж. Андерсон, «Основы аэродинамики»** — если доступно официальное русское издание, это хорошая
  базовая пара к англоязычному оригиналу. **[ПР, Б–С]**
- **С. Тимошенко, Дж. Гере, «Механика материалов»** и русские переводы классических трудов Тимошенко
  по устойчивости и колебаниям — международная база прочности. **[ПР, С–П]**
- **Дж. Ньюмен, «Морская гидродинамика»** и **Э. Роусон, Э. Таппер, «Основные положения теории
  корабля»** — полезные русские переводы, если есть в библиотеке; для норм и коэффициентов всё равно
  требуются актуальные IMO/ITTC. **[ПР, С–П]**
- **Мещерский, «Сборник задач по теоретической механике»** — классическая русская практика после
  основ анализа и векторов. [С, книга]
- **Лойцянский, Лурье, «Курс теоретической механики»**. [С, книга]
- **Никитин, «Курс теоретической механики»**. [С, книга]
- **Лойцянский, «Механика жидкости и газа»** — классический русскоязычный курс. [С–П, книга]
- **Войткунский, Фаддеев, Федяевский, «Гидромеханика»** — общий и морской уклон. [С–П, книга]
- **Schlichting & Gersten, Boundary-Layer Theory**; русский перевод старого издания — **Шлихтинг,
  «Теория пограничного слоя»**. [П, книга]
- **Landau & Lifshitz, Fluid Mechanics / «Гидродинамика»**. [П, книга]
- **Житомирский, «Конструкция самолётов»** — русскоязычная конструктивная база. [С, книга]
- **Егер и др., «Проектирование самолётов»**. [П, книга]
- **Войт, Ендогур, Мелик-Саркисян, Алявдин, «Проектирование конструкций самолётов»**. [П, книга]
- **Остославский, «Динамика полёта. Траектории летательных аппаратов»**. [П, книга]
- **Миль, Некрасов, Браверман и др., «Вертолёты. Расчёт и проектирование»**, книги 1–3. [С–П]
- **Володко, «Основы аэродинамики и динамики полёта вертолётов»**. [С]
- **Браверман, Вайнтруб, «Динамика вертолёта. Предельные режимы полёта»**. [П]
- **Богданов, Михеев, Скулков, «Конструкция вертолётов»**. [С]
- **Завалов, «Конструкция вертолётов»**. [С–П]
- **Тищенко, Некрасов, Радин, «Вертолёты. Выбор параметров при проектировании»**. [П]
- **Юрьев, «Аэродинамический расчёт вертолёта»** — исторический фундамент; сверять обозначения и
  эмпирику с современными источниками. [П]
- **Семёнов-Тян-Шанский, «Статика и динамика корабля»** — плавучесть, остойчивость и спуск. [С–П,
  книга]
- **Фукельман, «Основы теории корабля»** — более простой вход. [Б–С]

### Физика и инженерная механика: онлайн-источники и данные

- **OpenStax University Physics, Vol. 1–2** — механика, вращение, колебания, жидкости,
  термодинамика. [Б, свободно] - https://openstax.org/details/books/university-physics-volume-1 -
  https://openstax.org/details/books/university-physics-volume-2
- **MIT OCW 18.01SC, 18.02SC, 18.03SC, 18.06** — анализ, многомерный анализ, дифференциальные
  уравнения и линейная алгебра. [Б–С, свободно] -
  https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/ -
  https://ocw.mit.edu/courses/18-02sc-multivariable-calculus-fall-2010/ -
  https://ocw.mit.edu/courses/18-03sc-differential-equations-fall-2011/ -
  https://ocw.mit.edu/courses/18-06-linear-algebra-spring-2010/
- **«Инженерная механика», УрФУ** — русскоязычный мост от школьной физики к расчётным схемам машин.
  [Б–С] - https://openedu.ru/course/urfu/ENGM/
- **MIT OCW 2.003SC Engineering Dynamics** — движение твёрдого тела, моменты, вращение, колебания.
  [С, свободно] - https://ocw.mit.edu/courses/2-003sc-engineering-dynamics-fall-2011/
- **MIT OCW 2.06 Fluid Dynamics** — гидростатика, контрольный объём, Бернулли, вязкость, пограничный
  слой и сопротивление. [С, свободно] - https://ocw.mit.edu/courses/2-06-fluid-dynamics-spring-2013/
- **MIT Unified Engineering 16.01–16.04** — связанный курс по аэродинамике, термодинамике,
  двигателям, конструкциям и системам. [С, свободно] -
  https://ocw.mit.edu/courses/16-01-unified-engineering-i-ii-iii-iv-fall-2005-spring-2006/
- **J. Gordon Leishman, Introduction to Aerospace Flight Vehicles** — открытый современный учебник:
  самолёты, вертолёты, винты, дирижабли, конструкции, устойчивость и управление. [Б–С, свободно] -
  https://eaglepubs.erau.edu/introductiontoaerospaceflightvehicles/
- **NASA Beginner’s Guide to Aeronautics and Propulsion** — быстрый справочник по подъёмной силе,
  сопротивлению, числам Рейнольдса и Маха, винтам и двигателям. [Б, свободно] -
  https://www1.grc.nasa.gov/beginners-guide-to-aeronautics/ -
  https://www.grc.nasa.gov/WWW/K-12/airplane/bgp.html
- **FAA Pilot’s Handbook of Aeronautical Knowledge** — устройство самолёта, аэродинамика, органы
  управления, системы, масса, центровка и характеристики. [Б, свободно] -
  https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/phak
- **FAA Helicopter Flying Handbook** — аэродинамика винта, автомат перекоса, управление, компоненты,
  режимы и опасные состояния. [Б–С, свободно] -
  https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/helicopter_flying_handbook
- **NPTEL Introduction to Helicopter Aerodynamics and Dynamics** — переход от описательного уровня
  FAA к расчётам несущего винта. [С, свободно] - https://nptel.ac.in/courses/101104017
- **NASA NDARC Theory** — единая расчётная архитектура обычных, тандемных, соосных вертолётов и
  конвертопланов. [П, свободно] - https://ntrs.nasa.gov/citations/20220000355
- **MIT OCW 2.700 Principles of Naval Architecture** — геометрия корпуса, плавучесть, остойчивость,
  повреждения, сопротивление и прочность. [С, свободно] -
  https://ocw.mit.edu/courses/2-700-principles-of-naval-architecture-fall-2014/
- **USNA EN400 Principles of Ship Performance** — цельный открытый конспект по судам и подлодкам от
  закона Архимеда до движителей. [Б–С, свободно] -
  https://www.usna.edu/NAOE/academics/en400_no_vids.php
- **MIT OCW 2.20 Marine Hydrodynamics** — волны, присоединённая масса, сопротивление, подобие,
  испытания, винты и морская гидродинамика. [П, свободно] -
  https://ocw.mit.edu/courses/2-20-marine-hydrodynamics-13-021-spring-2005/
- **MIT 2.154 Maneuvering and Control of Surface and Underwater Vehicles** — шестистепенная динамика
  и управление судами и подводными аппаратами. [П, свободно] -
  https://ocw.mit.edu/courses/2-154-maneuvering-and-control-of-surface-and-underwater-vehicles-13-49-fall-2004/
- **Åström & Murray, Feedback Systems** — бесплатный фундамент по обратной связи, устойчивости и
  регуляторам. [С, свободно] - https://fbsbook.org/
- **ITTC Recommended Procedures** — эталон того, как проверять корабельные модели, сопротивление,
  движители, кавитацию и неопределённость. [П, свободно] -
  https://www.ittc.info/recommended-procedures/
- **Н. Е. Жуковский, «Теоретические основы воздухоплавания» и избранные работы** — происхождение
  теории подъёмной силы, вихрей, винтов и устойчивости; международный обзор наследия опубликован
  ЦАГИ: https://www.dl.begellhouse.com/download/article/7d9f22e81b01a8d9/TsAGI4904(2)-27990.pdf
  **[М, И]**
- **А. П. Гуляев, «Металловедение»** — состав, структура, термообработка и свойства; существует
  англоязычное издание *Physical Metallurgy* (Mir Publishers), которое присутствует в зарубежных
  университетских каталогах: http://opac.duls.du.ac.in/bib/87777. Старые марки сплавов сверять с
  действующими стандартами. **[М, С–П]**
- Официальная библиография кафедры проектирования вертолётов МАИ:
  https://institutes.mai.ru/avia/102_book/
- **3Blue1Brown: Essence of Calculus / Linear Algebra** — наглядная интуиция перед университетскими
  курсами. [Б, свободно] - https://www.3blue1brown.com/topics/calculus -
  https://www.3blue1brown.com/topics/linear-algebra
- **MIT 2.086 Numerical Computation for Mechanical Engineers** — ошибки, интерполяция,
  интегрирование, ОДУ, оптимизация и проверка расчётов. [С, свободно] -
  https://ocw.mit.edu/courses/2-086-numerical-computation-for-mechanical-engineers-fall-2014/
- **NumPy / SciPy / SymPy** — массивы, интеграторы, интерполяция, оптимизация и символьная проверка
  формул. [Б–С, свободно] - https://numpy.org/learn/ - https://docs.scipy.org/doc/scipy/tutorial/ -
  https://docs.sympy.org/latest/tutorials/intro-tutorial/
- **MIT 8.01SC Classical Mechanics** — строгая университетская механика с задачами. [Б–С, свободно]
  - https://ocw.mit.edu/courses/8-01sc-classical-mechanics-fall-2016/
- **OpenEdu: Теоретическая механика, СПбПУ** — статика, кинематика, динамика и аналитическая
  механика на русском. [С] - https://openedu.ru/course/spbstu/TMECH/
- **MIT 8.09 Classical Mechanics III, главы Rigid Body и Fluid Mechanics** — тензор инерции,
  уравнения Эйлера и механика сплошной среды. [П, свободно] -
  https://ocw.mit.edu/courses/8-09-classical-mechanics-iii-fall-2014/pages/lecture-notes/
- **NPTEL Engineering Mechanics: Statics and Dynamics**. [Б–С, свободно] -
  https://nptel.ac.in/courses/112106180
- **NPTEL Dynamics of Machines / Mechanical Vibrations** — валы, дисбаланс, резонансы,
  виброизоляция. [С] - https://nptel.ac.in/courses
- **MIT 2.001 Mechanics & Materials I** — напряжения, деформации, балки, кручение. [С, свободно] -
  https://ocw.mit.edu/courses/2-001-mechanics-materials-i-fall-2006/
- **MIT 2.002 Mechanics & Materials II** — устойчивость, пластичность, разрушение и усталость. [С,
  свободно] - https://ocw.mit.edu/courses/2-002-mechanics-and-materials-ii-spring-2004/
- **MIT 16.20 Structural Mechanics** — тонкостенные авиационные конструкции. [С–П, свободно] -
  https://ocw.mit.edu/courses/16-20-structural-mechanics-fall-2002/
- **MIT 2.092 Finite Element Analysis of Solids and Fluids I** — основы метода конечных элементов.
  [П, свободно] -
  https://ocw.mit.edu/courses/2-092-finite-element-analysis-of-solids-and-fluids-i-fall-2009/
- **DoITPoMS, University of Cambridge** — открытые модули по металлам, полимерам, композитам,
  усталости и разрушению. [Б–С, свободно] - https://www.doitpoms.ac.uk/
- **MIT 1.060 Engineering Mechanics II** — физически ориентированное введение в механику жидкости.
  [Б–С, свободно] - https://ocw.mit.edu/courses/1-060-engineering-mechanics-ii-spring-2006/
- **NPTEL Fluid Mechanics / Foundations of Fluid Mechanics** — полный видеокурс и дополнительные
  задачи. [С, свободно] - https://nptel.ac.in/courses
- **MIT 2.26 Compressible Fluid Dynamics** — сопла, скачки, течение Фанно и Релея. [П, свободно] -
  https://ocw.mit.edu/courses/2-26-compressible-fluid-dynamics-spring-2004/
- **MIT 2.27 Turbulent Flow and Transport**. [П, свободно] -
  https://ocw.mit.edu/courses/2-27-turbulent-flow-and-transport-spring-2002/
- **NASA Beginner’s Guide to Compressible Aerodynamics**. [С, свободно] -
  https://www.grc.nasa.gov/www/k-12/airplane/bgc.html
- **NACA Report 1135, Equations, Tables and Charts for Compressible Flow** — эталонные соотношения
  для проверки кода. [П, свободно] - https://ntrs.nasa.gov/citations/19930091059
- **МФТИ, «Термодинамика и молекулярная физика»** — русскоязычный курс с задачами. [Б–С] -
  https://courses.mipt.ru/course/view/71
- **OpenEdu / МИСИС, «Общая физика: механика, термодинамика и основы кинетической теории»**. [Б] -
  https://openedu.ru/course/misis/FIS/
- **MIT 5.60 Thermodynamics & Kinetics**. [С, свободно] -
  https://ocw.mit.edu/courses/5-60-thermodynamics-kinetics-spring-2008/
- **NPTEL Basic / Engineering Thermodynamics**. [С, свободно] - https://nptel.ac.in/courses
- **MIT 2.051 Introduction to Heat Transfer** — теплопроводность, конвекция, излучение и
  теплообменники. [С, свободно] -
  https://ocw.mit.edu/courses/2-051-introduction-to-heat-transfer-fall-2015/
- **MIT 16.50 Introduction to Propulsion Systems** — воздушно-реактивные и ракетные двигатели как
  системы. [С–П, свободно] -
  https://ocw.mit.edu/courses/16-50-introduction-to-propulsion-systems-spring-2012/
- **Wärtsilä Encyclopedia of Marine and Energy Technology** — морские дизели, движители и судовые
  системы. [справочник, свободно] - https://www.wartsila.com/encyclopedia
- **Spirax Sarco Learn About Steam** — пар, котлы, конденсат и теплообмен. [Б–С, свободно] -
  https://www.spiraxsarco.com/learn-about-steam
- **Åström & Murray, Feedback Systems** — модели состояния, обратная связь, устойчивость, PID,
  частотные методы и робастность. [С, свободно] - https://fbsbook.org/
- **MIT 2.004 Systems, Modeling and Control II**. [С, свободно] -
  https://ocw.mit.edu/courses/2-004-systems-modeling-and-control-ii-fall-2007/
- **MIT 16.333 Aircraft Stability and Control** — устойчивость, производные, продольные и боковые
  режимы, V/STOL и управление. [П, свободно] -
  https://ocw.mit.edu/courses/16-333-aircraft-stability-and-control-fall-2004/
- **MIT Underactuated Robotics** — нелинейная динамика, состояния и управление недоприводными
  системами. [П, свободно] - https://underactuated.csail.mit.edu/
- **Brian Douglas, Control Systems Lectures** — наглядное дополнение, но не замена учебнику. [Б–С,
  свободно] - https://www.youtube.com/@BrianBDouglas
- **FAA Aviation Maintenance Technician Handbook — General, Airframe, Powerplant** — реальное
  устройство силового набора, систем, двигателей, винтов и обслуживания. [Б–С, свободно] -
  https://www.faa.gov/regulations_policies/handbooks_manuals/aviation
- **FAA Airplane Flying Handbook** — связь конструкции и аэродинамики с режимами полёта. [Б,
  свободно] - https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/airplane_handbook
- **MIT 16.100 Aerodynamics**. [С, свободно] -
  https://ocw.mit.edu/courses/16-100-aerodynamics-fall-2005/
- **MIT 16.885J Aircraft Systems Engineering** — вес, безопасность, надёжность, подсистемы и
  компромиссы всего самолёта. [П, свободно] -
  https://ocw.mit.edu/courses/16-885j-aircraft-systems-engineering-fall-2005/
- **NPTEL Introduction to Aerospace Engineering** — атмосфера, устройство, аэродинамика, двигатели,
  характеристики и устойчивость. [Б–С, свободно] -
  https://onlinecourses.nptel.ac.in/noc21_ae11/preview
- **ERAU, Helicopters / Vertical Flight**. [Б, свободно] -
  https://eaglepubs.erau.edu/introductiontoaerospaceflightvehicles/chapter/helicopters/
- **NASA TP-2011-215971, Milestones in Rotorcraft Aeromechanics** — обзор развития теории, нагрузок,
  вибраций, устойчивости и CFD. [С–П, свободно] -
  https://rotorcraft.arc.nasa.gov/Johnson_TP-2011-215971_final.pdf
- **NASA NDARC Theory** — расчёт конфигурации, массы, миссии, роторов, силовой установки и
  характеристик. [П, свободно] - https://rotorcraft.arc.nasa.gov/ndarc/index.php/reports-and-papers
- **Coleman, A Survey of Theoretical and Experimental Coaxial Rotor Aerodynamic Research, NASA
  TP-3675** — расстояние между роторами, распределение нагрузки, вихревой след, взаимное влияние,
  swirl recovery и эффективность. Это основной открытый источник для нашей соосной схемы. [П,
  свободно] - https://ntrs.nasa.gov/citations/19970015550
- **Lim et al., Hover Performance Correlation for Full-Scale and Model-Scale Coaxial Rotors** —
  проверка расчётов по экспериментам. [П, статья] -
  https://rotorcraft.arc.nasa.gov/Publications/files/Lim_JAHS2009.pdf
- Библиография кафедры 102 МАИ: - https://institutes.mai.ru/avia/102_book/
- **USNA EN247 Principles of Naval Architecture and Marine Engineering** — обзор проектной спирали,
  геометрии, плавучести, остойчивости, сопротивления и энергетики. [Б–С, свободно] -
  https://www.usna.edu/NAOE/academics/en247.php
- **Newman, Marine Hydrodynamics** — потенциальное течение, волны, присоединённая масса и
  гидродинамические силы. 40th Anniversary Edition доступно как официальный open-access e-book у MIT
  Press. [П] - https://direct.mit.edu/books/book/2693/Marine-Hydrodynamics
- **MIT 2.22 Design Principles for Ocean Vehicles** — нагрузки, волны, мореходность и маневренность.
  [П, свободно] -
  https://ocw.mit.edu/courses/2-22-design-principles-for-ocean-vehicles-13-42-spring-2005/
- **MIT 2.23 Hydrofoils and Propellers** — теория гребных винтов. [П, свободно] -
  https://ocw.mit.edu/courses/2-23-hydrofoils-and-propellers-spring-2007/
- **«Справочник по теории корабля» под ред. Войткунского, 3 тома** — гидромеханика, сопротивление,
  движители, качка и управляемость. [П, книга] - https://rusneb.ru/catalog/000199_000009_001269973/
- **Burcher & Rydill, Concepts in Submarine Design** — гидростатика, прочность, мощность, динамика,
  компоновка и проектная спираль. [С–П, книга] -
  https://www.cambridge.org/core/books/concepts-in-submarine-design/868475E3EF0E09895E220BB02F21B715
- **Joubert, Some Aspects of Submarine Design, Part 1: Hydrodynamics** — открытый государственный
  обзор компромиссов формы и сопротивления. [П, свободно] -
  https://apps.dtic.mil/sti/tr/pdf/ADA428039.pdf
- **MIT 2.080J Structural Mechanics** и курсы по plates/shells — оболочки под внешним давлением и
  потеря устойчивости. [П, свободно] -
  https://ocw.mit.edu/courses/2-080j-structural-mechanics-fall-2013/
- **Discovery of Sound in the Sea (DOSITS)** — физика распространения подводного звука, шум судов,
  природные источники и измерения. [Б–С, свободно] - https://dosits.org/
- **MIT 16.540 Internal Flows in Turbomachines**. [П, свободно] -
  https://ocw.mit.edu/courses/16-540-internal-flows-in-turbomachines-spring-2006/
- **OpenProp** — открытый код и статьи по расчёту морских винтов и турбин. -
  http://openprop.mit.edu/
- **NOAA JetStream** — атмосфера, ветер, облака, верхние слои, грозы и океанские волны. [Б,
  свободно] - https://www.noaa.gov/jetstream
- **FAA Aviation Weather Handbook** — погода с точки зрения полёта. [Б–С, свободно] -
  https://www.faa.gov/regulations_policies/handbooks_manuals/aviation
- **MIT 12.003 Atmosphere, Ocean and Climate Dynamics**. [П, свободно] -
  https://ocw.mit.edu/courses/12-003-atmosphere-ocean-and-climate-dynamics-fall-2008/
- **NOAA Waves** — fetch, период, зыбь, значимая высота и прибой. [Б] -
  https://www.noaa.gov/jetstream/ocean/waves
- **U.S. Standard Atmosphere 1976** — эталон плотности, температуры и давления по высоте. [стандарт]
  - https://ntrs.nasa.gov/citations/19770009539
- **TEOS-10** — официальный стандарт свойств морской воды, льда и влажного воздуха. [стандарт,
  свободно] - https://www.teos-10.org/
- **NIST Chemistry WebBook** — свойства газов и веществ. - https://webbook.nist.gov/chemistry/
- **IAPWS-95 / IAPWS-IF97** — свойства воды и пара. - https://iapws.org/
- **CoolProp** — открытая программная реализация свойств множества рабочих тел; проверять диапазон и
  backend. [свободно] - https://coolprop.org/
- **NASA Technical Reports Server (NTRS)** — NACA/NASA отчёты и экспериментальные данные. -
  https://ntrs.nasa.gov/
- **UIUC Airfoil Data Site** — координаты профилей и часть экспериментальных поляр; всегда сохранять
  происхождение конкретного набора. - https://m-selig.ae.illinois.edu/ads.html
- **NASA Turbulence Modeling Resource** — эталонные CFD cases. [П] -
  https://turbmodels.larc.nasa.gov/
- **IMO 2008 Intact Stability Code (IS Code)** — определения и реальная логика проверки
  остойчивости. Актуальную редакцию брать у IMO. - https://www.imo.org/
- **DNV Rules and Standards** — корпуса, механизмы и морские операции; часть документов доступна
  после бесплатной регистрации. - https://www.dnv.com/rules-standards/
- **ABS Rules and Guides** — корабли, малые суда и подводные аппараты. -
  https://ww2.eagle.org/en/rules-and-resources/rules-and-guides.html
- **NIST Engineering Statistics Handbook** — погрешности, регрессия, планирование эксперимента.
  [свободно] - https://www.itl.nist.gov/div898/handbook/
- **JCGM 100: GUM, Guide to the Expression of Uncertainty in Measurement**. -
  https://www.bipm.org/en/committees/jc/jcgm/publications
- **XFOIL** — двумерные дозвуковые профили; не доверять около глубокого срыва и вне проверенного
  диапазона Reynolds/Mach. - https://web.mit.edu/drela/Public/web/xfoil/
- **XFLR5** — профили, крылья, VLM/панельные методы и устойчивость малых самолётов. -
  https://www.xflr5.tech/
- **OpenVSP + VSPAERO** — параметрическая геометрия самолёта, площади, аэродинамика концептуального
  уровня и простые модели винтов. [свободно] - https://openvsp.org/
- **JSBSim** — открытая нелинейная 6-DoF модель полёта, атмосфера, масса, двигатели, шасси и
  управление. Хороший архитектурный образец для нашей физики. -
  https://github.com/JSBSim-Team/jsbsim
- **QBlade** — blade-element/momentum расчёты роторов и турбин; результаты сверять с учебником и
  экспериментом. - https://qblade.org/
- **FreeShip / FreeShip Plus** — форма корпуса и учебные гидростатические расчёты. -
  https://github.com/markmal/freeship-plus-in-lazarus
- **DELFTship Free** — форма корпуса и базовая гидростатика; расширенные функции могут быть
  коммерческими. - https://www.delftship.net/
- **Capytaine** — линейная потенциальная гидродинамика плавающих тел. - https://capytaine.org/
- **OpenFOAM Foundation** — CFD, свободно; высокий порог входа. - https://openfoam.org/
- **SU2** — аэродинамический CFD и оптимизация, свободно. - https://su2code.github.io/
- **Gmsh** — сетки. https://gmsh.info/
- **ParaView** — визуализация. https://www.paraview.org/
- https://books.google.com/books/about/The_Classical_Theory_of_Fields.html?id=X18PF4oKyrUC
- https://books.google.com/books/about/Similarity_and_Dimensional_Methods_in_Me.html?id=HEsPEAAAQBAJ
- https://search.worldcat.org/search?q=ti%3AProblems%20in%20General%20Physics%20au%3AIrodov

### Химия и материаловедение: книги, отчёты и стандарты

- **Л. Д. Ландау, Е. М. Лифшиц**, тома 3 «Квантовая механика», 5 «Статистическая физика», 8
  «Электродинамика сплошных сред» и 10 «Физическая кинетика» — мировая теоретическая база для
  физической химии и материалов. **[М, П]**
- **В. А. Фок, «Начала квантовой механики»** — исторически важная основа квантовой теории; имя Фока
  закреплено в международной терминологии Hartree–Fock и Fock space. **[М, И–П]**
- **Д. В. Сивухин, «Общий курс физики»**, особенно тома 3 и 5 — электричество, атомная и ядерная
  физика. **[В, Б–С]**
- **И. Е. Тамм, «Основы теории электричества»** — сильный теоретический курс с международными
  переводами, но не первое введение. **[М, П]**
- **П. Эткинс, Х. де Паула, «Физическая химия»** — термодинамика, квантовая химия, спектроскопия и
  кинетика. **[ПР, Б–П]**
- **Дж. Клейден, Н. Гривз, С. Уоррен, «Органическая химия»** — механизмы вместо заучивания перечня
  реакций. **[ПР, Б–П]**
- **Ф. Кэри, Р. Сандберг, «Углублённый курс органической химии»** или **Дж. Марч, «Органическая
  химия. Реакции, механизмы и структура»** — продвинутая ветвь. **[ПР, П]**
- **Н. Гринвуд, А. Эрншо, «Химия элементов»** — систематическая неорганика. **[ПР, С–П]**
- **Д. Шрайвер, П. Эткинс и др., «Неорганическая химия»** — современная структура, связь и
  реакционная способность. **[ПР, С–П]**
- **Д. Харрис, «Количественный химический анализ»** и **Д. Скуг и др., «Основы аналитической химии»
  / «Принципы инструментального анализа»**. **[ПР, Б–П]**
- **Д. Мак-Куорри, «Квантовая химия»** — классический русский перевод для перехода к молекулярным
  расчётам; новые методы сверять с актуальным англоязычным изданием и современной вычислительной
  литературой. **[ПР, С–П]**
- **Ф. Дэниелс, Р. Олберти, физическая химия**, **Г. Кастеллан, «Физическая химия»** — классические
  альтернативы, если доступны в библиотеке. **[ПР, С]**
- **О. Левеншпиль, «Инженерное оформление химических процессов»**, **Х. Фоглер, «Расчёты химических
  реакторов»**, **У. Маккейб, Дж. Смит, П. Харриотт, «Процессы разделения»** — химическая инженерия.
  **[ПР, С–П]**
- **У. Каллистер, Д. Ретвиш, «Материаловедение: от технологии к применению»** — доступная связь
  структуры и свойств материалов. **[ПР, Б–С]**
- **Д. Портер, К. Истерлинг, М. Шериф, «Фазовые превращения в металлах и сплавах»** — продвинутая
  металлургическая ветвь. **[ПР, П]**
- **Ф. Родригес и др., «Принципы полимерных систем»**, **Л. Сперлинг, «Введение в физику
  полимеров»** — структура, свойства и переработка полимеров. **[ПР, С–П]**
- **Д. Гриффитс, «Введение в электродинамику»**, **Э. Парселл, Д. Морин, «Электричество и
  магнетизм»** — полевая основа. **[ПР, С–П]**
- **Дж. Ламарш, «Введение в теорию ядерных реакторов»** — классический русский перевод более раннего
  международного курса; использовать как теорию, а не источник действующих эксплуатационных норм.
  Современные англоязычные Lamarsh & Baratta и Shultis & Faw читать вместе с IAEA, NNDC и
  актуальными нормами безопасности. **[ПР, И–П]**
- Clayden, Greeves, Warren, *Organic Chemistry*;
- Carey & Sundberg, *Advanced Organic Chemistry*;
- March, *Advanced Organic Chemistry*;
- Smith, *Organic Chemistry*;
- Реутов, Курц, Бутин, «Органическая химия».
- Kaiser, de Klerk, Gary & Handwerk, *Petroleum Refining: Technology, Economics, and Markets*;
- Speight, *The Chemistry and Technology of Petroleum*;
- Speight, *Handbook of Petroleum Refining*;
- Fahim, Alsahhaf & Elkilani, *Fundamentals of Petroleum Refining*;
- Jones & Pujadó, *Handbook of Petroleum Processing*;
- Riazi, *Characterization and Properties of Petroleum Fractions*;
- Meyers, *Handbook of Petroleum Refining Processes*;
- Wauquier, *Petroleum Refining*;
- Weissermel & Arpe, *Industrial Organic Chemistry* — нефтехимические platform chains.
- **Е. В. Смидович, «Технология переработки нефти и газа»** — классический отраслевой курс; старые
  process data и нормы обязательно обновлять. **[В, И–П]**
- **Р. З. Магарил, «Теоретические основы химических процессов переработки нефти»** —
  kinetics/thermochemistry технологических превращений. **[В, С–П]**
- ASTM D86 — atmospheric distillation curve;
- ASTM D2887 — simulated distillation;
- ASTM D4052 — density/relative density/API gravity;
- ASTM D445 — kinematic viscosity;
- ASTM D93 — flash point;
- ASTM D4294 — sulfur by X-ray fluorescence;
- ASTM D2699/D2700 — research/motor octane;
- ASTM D613 — cetane number.
- Atkins & de Paula, *Physical Chemistry*;
- McQuarrie & Simon, *Physical Chemistry: A Molecular Approach*;
- McQuarrie, *Quantum Chemistry*;
- Levine, *Quantum Chemistry*;
- Miessler, Fischer & Tarr, *Inorganic Chemistry*;
- Housecroft & Sharpe, *Inorganic Chemistry*;
- Cotton, *Chemical Applications of Group Theory*;
- Crabtree, *The Organometallic Chemistry of the Transition Metals*;
- Elschenbroich, *Organometallics*;
- Hartwig, *Organotransition Metal Chemistry*;
- Turro, Ramamurthy & Scaiano, *Modern Molecular Photochemistry of Organic Molecules*;
- Steed & Atwood, *Supramolecular Chemistry*;
- Leach, *Molecular Modelling*;
- Jensen, *Introduction to Computational Chemistry*;
- Cramer, *Essentials of Computational Chemistry*;
- Frenkel & Smit, *Understanding Molecular Simulation*;
- Martin, *Electronic Structure*.
- Espenson, *Chemical Kinetics and Reaction Mechanisms*;
- Fogler, *Elements of Chemical Reaction Engineering*;
- Masel, *Chemical Kinetics and Catalysis*.
- Harris, *Quantitative Chemical Analysis*;
- Skoog, Holler, Crouch, *Principles of Instrumental Analysis*;
- Bard & Faulkner, *Electrochemical Methods*;
- Newman & Thomas-Alyea, *Electrochemical Systems*;
- Bagotsky, *Fundamentals of Electrochemistry*;
- Дамаскин, Петрий, Цирлина, «Электрохимия»;
- Young & Lovell, *Introduction to Polymers*;
- Odian, *Principles of Polymerization*;
- Sperling, *Introduction to Physical Polymer Science*;
- Flory, *Principles of Polymer Chemistry*;
- Callister & Rethwisch, *Materials Science and Engineering*;
- Askeland & Wright, *The Science and Engineering of Materials*;
- Porter, Easterling & Sherif, *Phase Transformations in Metals and Alloys*.
- A. P. Gуляев, «Металловедение»;
- David R. Gaskell & David E. Laughlin, *Introduction to the Thermodynamics of Materials*;
- Felder, Rousseau & Bullard, *Elementary Principles of Chemical Processes*;
- Bird, Stewart & Lightfoot, *Transport Phenomena*;
- Incropera et al., *Fundamentals of Heat and Mass Transfer*;
- Levenspiel, *Chemical Reaction Engineering*;
- McCabe, Smith & Harriott, *Unit Operations of Chemical Engineering*;
- Seader, Henley & Roper, *Separation Process Principles*;
- Towler & Sinnott, *Chemical Engineering Design*;
- Касаткин, «Основные процессы и аппараты химической технологии».
- Purcell & Morin, *Electricity and Magnetism*;
- Griffiths, *Introduction to Electrodynamics*;
- Chapman, *Electric Machinery Fundamentals*;
- Horowitz & Hill, *The Art of Electronics* — для электроники и измерительных трактов;
- Сивухин, том 3; Тамм, «Основы теории электричества»; Иродов, задачи по электромагнетизму.

### Химия и материаловедение: онлайн-источники и данные

- **Д. И. Менделеев, «Основы химии»** — исторический источник по периодическому закону и развитию
  общей химии. Книга переводилась на английский, немецкий и французский; метаданные раннего
  английского издания: https://openlibrary.org/books/OL14035579M/The_principles_of_chemistry. Для
  современных атомных данных и номенклатуры применять IUPAC/NIST, а не старые таблицы. **[М, И]**
- **Н. Н. Семёнов, «Цепные реакции» и «О некоторых проблемах химической кинетики и реакционной
  способности»** — основа теории цепных процессов; Семёнов получил Нобелевскую премию по химии 1956
  года совместно с Сирилом Хиншелвудом: https://www.nobelprize.org/prizes/chemistry/1956/summary/
  **[М, П]**
- **А. Н. Фрумкин, избранные труды по электрохимической кинетике** — международно признанная школа
  двойного электрического слоя и электродных процессов; обзор школы и переводов:
  https://link.springer.com/article/10.1007/s10008-014-2480-5 **[М, П]**
- **В. С. Баготский, «Основы электрохимии»** — имеет международное английское издание *Fundamentals
  of Electrochemistry* в серии Electrochemical Society/Wiley:
  https://iopscience.iop.org/article/10.1149/2.F01061IF **[М, С–П]**
- **А. Н. и Н. А. Несмеяновы, «Начала органической химии»** — вышли на английском в четырёх томах
  как *Fundamentals of Organic Chemistry* (Mir Publishers); метаданные искать через WorldCat:
  https://search.worldcat.org/search?q=ti%3AFundamentals%20of%20Organic%20Chemistry%20au%3ANesmeyanov
  Учебные данные сверять с современными Clayden, March и IUPAC. **[М, И–С]**
- **А. П. Гуляев, «Металловедение»** — выходил в английском переводе как *Physical Metallurgy* (Mir
  Publishers) и присутствует в зарубежных университетских каталогах:
  http://opac.duls.du.ac.in/bib/87777. Хорошая связь состава, структуры, обработки и свойств. **[М,
  С–П]**
- **А. Н. Климов, «Ядерная физика и ядерные реакторы»** — переводился Mir Publishers как *Nuclear
  Physics and Nuclear Reactors*; библиографическая запись OSTI: https://www.osti.gov/biblio/4285248.
  Полезен как исторический системный курс, но данные и безопасность должны идти из IAEA/NNDC и
  современных изданий. **[М, И–С]**
- **Н. С. Ахметов, «Общая и неорганическая химия»** — цельный базовый курс. Карточка РГБ:
  https://search.rsl.ru/ru/record/01007551840 **[В, Б–С]**
- **Ю. Д. Третьяков и др., «Неорганическая химия» в 3 томах** — современная школа МГУ:
  https://www.chem.msu.ru/rus/books/2001-2010/inorg-book-vol1/welcome.html **[В, С–П]**
- **О. А. Реутов, А. Л. Курц, К. П. Бутин, «Органическая химия» в 4 частях** — механизмы и логика
  органических превращений. Карточка РГБ: https://search.rsl.ru/ru/record/01003082497 **[В, С–П]**
- **Е. Н. Ерёмин, «Основы химической термодинамики»**, а затем современный курс **В. В. Ерёмина, С.
  И. Каргова, И. А. Успенской и др., «Основы физической химии»** — хорошая русская траектория к
  физической химии; карточка нового издания МФТИ: https://books.mipt.ru/book/307198 **[В, С]**
- **Б. Б. Дамаскин, О. А. Петрий, Г. А. Цирлина, «Электрохимия»** — современный университетский
  курс. Карточка РГБ: https://search.rsl.ru/ru/record/01007886855 **[В, С–П]**
- **В. Г. Воскобойников, В. А. Кудрин, А. М. Якушев, «Общая металлургия»**, а также профильные
  учебники МИСИС — база рудоподготовки и основных металлургических производств; библиотечная
  карточка СФУ: https://bik.sfu-kras.ru/elib/view?id=BOOK1-669%2F%D0%9276-747321 **[В, С]**
- **А. Г. Касаткин, «Основные процессы и аппараты химической технологии»** — классический русский
  курс. Уравнения полезны, но свойства, нормы и коэффициенты старых изданий требуют обновления.
  Карточка РГБ: https://search.rsl.ru/ru/record/01002569897 **[В, С–П]**
- **В. И. Бекман, «Радиохимия», т. 1–2** — радиоактивность, радионуклиды, дозиметрия, защита и
  прикладная радиохимия. Официальная страница химфака МГУ:
  https://www.chem.msu.ru/rus/books/2014/bekman/welcome.html **[В, С–П]**
- Российская государственная библиотека: https://search.rsl.ru/
- Национальная электронная библиотека: https://rusneb.ru/
- каталог химического факультета МГУ: https://www.chem.msu.ru/rus/books/
- WorldCat: https://search.worldcat.org/
- Google Books — для метаданных и доступного издателем preview: https://books.google.com/
- MIT OCW 18.01 Single Variable Calculus:
  https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/
- MIT OCW 18.02 Multivariable Calculus:
  https://ocw.mit.edu/courses/18-02sc-multivariable-calculus-fall-2010/
- MIT OCW 18.03 Differential Equations:
  https://ocw.mit.edu/courses/18-03sc-differential-equations-fall-2011/
- MIT OCW 18.06 Linear Algebra: https://ocw.mit.edu/courses/18-06sc-linear-algebra-fall-2011/
- NIST/SEMATECH e-Handbook of Statistical Methods: https://www.itl.nist.gov/div898/handbook/
- NIST CODATA constants: https://physics.nist.gov/cuu/Constants/
- MIT 5.111 Principles of Chemical Science:
  https://ocw.mit.edu/courses/5-111sc-principles-of-chemical-science-fall-2014/
- OpenStax Chemistry 2e: https://openstax.org/details/books/chemistry-2e
- Chemistry LibreTexts: https://chem.libretexts.org/
- IUPAC Periodic Table: https://iupac.org/what-we-do/periodic-table-of-elements/
- IUPAC Gold Book: https://goldbook.iupac.org/
- IUPAC Colour Books and nomenclature: https://iupac.org/what-we-do/books/
- MIT OCW search: Organic Chemistry: https://ocw.mit.edu/search/?q=organic%20chemistry
- OpenStax Organic Chemistry: https://openstax.org/details/books/organic-chemistry
- IUPAC Blue Book resources: https://iupac.org/what-we-do/books/bluebook/
- PubChem: https://pubchem.ncbi.nlm.nih.gov/
- Penn State FSC 432, Petroleum Refining: https://courses.ems.psu.edu/fsc432/
- NPTEL Petroleum Refinery Engineering, IIT Delhi: https://nptel.ac.in/courses/103102022
- NPTEL Petroleum Technology: https://onlinecourses.nptel.ac.in/noc23_ch64/preview
- **А. К. Мановян, «Технология первичной переработки нефти и природного газа»** — подготовка,
  атмосферно-вакуумное разделение и газовые процессы. Карточка РГБ:
  https://search.rsl.ru/ru/record/01000687885 **[В, С–П]**
- **С. А. Ахметов, «Физико-химическая технология глубокой переработки нефти и газа»** —
  конверсионные и очистные процессы. Карточка РГБ: https://search.rsl.ru/ru/record/01000841217 **[В,
  С–П]**
- ASTM Petroleum Standards:
  https://store.astm.org/products-services/standards-and-publications/standards/petroleum-standards.html
- NIST Chemistry WebBook: https://webbook.nist.gov/chemistry/
- NIST Thermophysical Properties of Hydrocarbon Mixtures Database:
  https://www.nist.gov/srd/nist-standard-reference-database-4
- NIST ThermoData Engine public dataset:
  https://catalog.data.gov/dataset/thermodata-engine-free-public-version
- API standards catalog: https://www.api.org/products-and-services/standards
- GPA Midstream standards: https://www.gpamidstream.org/publications/
- CCPS process safety: https://www.aiche.org/ccps
- MIT OCW search: Physical Chemistry: https://ocw.mit.edu/search/?q=physical%20chemistry
- MIT OCW search: Quantum Mechanics: https://ocw.mit.edu/search/?q=quantum%20chemistry
- NIST-JANAF Thermochemical Tables: https://janaf.nist.gov/
- NIST ThermoML: https://trc.nist.gov/ThermoML.html
- ORCA: https://www.faccts.de/orca/
- Psi4: https://psicode.org/
- CP2K: https://www.cp2k.org/
- Quantum ESPRESSO: https://www.quantum-espresso.org/
- Atomic Simulation Environment: https://ase-lib.org/
- NOMAD materials data/infrastructure: https://nomad-lab.eu/
- Materials Project: https://materialsproject.org/
- NIST Computational Chemistry Comparison and Benchmark Database: https://cccbdb.nist.gov/
- MIT OCW search: Chemical Kinetics: https://ocw.mit.edu/search/?q=chemical%20kinetics
- Cantera documentation: https://cantera.org/documentation/
- International Association for Chemical Kinetics: https://www.iack.net/
- NIST Chemistry WebBook spectra: https://webbook.nist.gov/chemistry/
- NIST Atomic Spectra Database: https://physics.nist.gov/PhysRefData/ASD/
- SDBS, AIST spectral database: https://sdbs.db.aist.go.jp/
- NIST mass spectral library information: https://chemdata.nist.gov/
- IUPAC Orange Book resources: https://iupac.org/what-we-do/books/orangebook/
- MIT 10.626 Electrochemical Energy Systems:
  https://ocw.mit.edu/courses/10-626-electrochemical-energy-systems-spring-2014/
- Electrochemical Society: https://www.electrochem.org/
- NIST Standard Reference Data: https://www.nist.gov/srd
- PyBaMM battery models: https://www.pybamm.org/
- IUPAC Purple Book: https://iupac.org/what-we-do/books/purplebook/
- IUPAC Polymer Division: https://iupac.org/who-we-are/divisions/division-details/?body_code=400
- Polymer Database: https://polymerdatabase.com/
- MIT 3.091 Introduction to Solid-State Chemistry:
  https://ocw.mit.edu/courses/3-091-introduction-to-solid-state-chemistry-fall-2018/
- Crystallography Open Database: https://www.crystallography.net/cod/
- International Union of Crystallography: https://www.iucr.org/
- NIST Materials Data resources: https://www.nist.gov/materials-science
- ASM International: https://www.asminternational.org/
- NIST phase-equilibria and materials programs: https://www.nist.gov/mml
- pycalphad: https://pycalphad.org/
- OpenCalphad: https://www.opencalphad.com/
- MIT OpenCourseWare, Chemical Engineering: https://ocw.mit.edu/search/?d=Chemical%20Engineering
- LearnChemE simulations and screencasts: https://learncheme.com/
- NPTEL courses: https://nptel.ac.in/courses
- MIT 8.02 Electricity and Magnetism: https://ocw.mit.edu/search/?q=electricity%20and%20magnetism
- MIT 6.002 Circuits and Electronics:
  https://ocw.mit.edu/courses/6-002-circuits-and-electronics-spring-2007/
- OpenStax University Physics, volume 2:
  https://openstax.org/details/books/university-physics-volume-2
- MIT 22.01 Introduction to Nuclear Engineering and Ionizing Radiation:
  https://ocw.mit.edu/courses/22-01-introduction-to-nuclear-engineering-and-ionizing-radiation-fall-2016/
- IAEA, Fundamentals of Reactor Physics with Python:
  https://www.iaea.org/online-learning/courses/1337/fundamentals-of-reactor-physics-with-python
- IAEA Nuclear Data Services: https://www-nds.iaea.org/
- IAEA LiveChart of Nuclides: https://www-nds.iaea.org/relnsd/vcharthtml/VChartHTML.html
- NNDC evaluated nuclear databases: https://www.nndc.bnl.gov/databases/
- NuDat 3: https://www.nndc.bnl.gov/nudat3/
- ENDF: https://www.nndc.bnl.gov/endf/
- IAEA Safety Standards: https://www.iaea.org/resources/safety-standards
- U.S. NRC student resources: https://www.nrc.gov/reading-rm/basic-ref/students
- UNSCEAR: https://www.unscear.org/
- ICRP: https://www.icrp.org/
- В. И. Бекман, «Радиохимия»: https://www.chem.msu.ru/rus/books/2014/bekman/welcome.html
- UNECE GHS Rev. 11 (2025): https://unece.org/sites/default/files/2025-09/GHS%20Rev11e.pdf
- OSHA Hazard Communication / SDS: https://www.osha.gov/hazcom
- NIOSH Pocket Guide: https://www.cdc.gov/niosh/npg/
- ECHA Information on Chemicals: https://echa.europa.eu/information-on-chemicals
- OECD eChemPortal: https://www.echemportal.org/echemportal/
- EPA CompTox Chemicals Dashboard: https://comptox.epa.gov/dashboard/
- ATSDR Toxicological Profiles: https://wwwn.cdc.gov/TSP/index.aspx
- AIChE Center for Chemical Process Safety: https://www.aiche.org/ccps
- GROMACS: https://www.gromacs.org/
- OpenFOAM: https://www.openfoam.com/
- OpenModelica: https://openmodelica.org/
- Reaktoro: https://reaktoro.org/
- RMG-Py: https://reactionmechanismgenerator.github.io/
- https://www.acs.org/content/dam/acsorg/about/governance/committees/chemicalsafety/publications/acs-safety-guidelines-academic.pdf
- https://www.bipm.org/en/publications/si-brochure
- https://www.bipm.org/en/committees/jc/jcgm/publications
- https://www.iso.org/standard/66912.html
- https://www.iso.org/standard/14001
- https://www.iso.org/standard/63787.html
- https://www.iso.org/standard/65694.html
- https://www.osha.gov/process-safety-management
- https://www.python.org/
- https://scipy.org/
- https://pint.readthedocs.io/
- https://pandas.pydata.org/
- https://jupyter.org/
- https://cantera.org/
- https://dwsim.org/
- https://www.rdkit.org/
- https://avogadro.cc/
- https://www.usgs.gov/software/phreeqc-version-3
- https://openmc.org/
- https://geant4.web.cern.ch/
- https://webbook.nist.gov/cgi/cbook.cgi?ID=C7732185&Mask=1"

### Метрология, systems и надёжность: книги, отчёты и стандарты

- ISO/IEC 17025 — competence of testing/calibration laboratories;
- ISO 10012 — measurement management systems;
- Doebelin & Manik, *Measurement Systems*;
- Bentley, *Principles of Measurement Systems*;
- Beckwith, Marangoni & Lienhard, *Mechanical Measurements*.
- Oppenheim & Schafer, *Discrete-Time Signal Processing*;
- Lyons, *Understanding Digital Signal Processing*;
- Bendat & Piersol, *Random Data*;
- Welch, Bishop, *An Introduction to the Kalman Filter*;
- Grewal & Andrews, *Kalman Filtering*.
- Montgomery, *Design and Analysis of Experiments*;
- Box, Hunter & Hunter, *Statistics for Experimenters*;
- Ljung, *System Identification*;
- Coleman & Steele, *Experimentation, Validation, and Uncertainty Analysis for Engineers*.
- Higham, *Accuracy and Stability of Numerical Algorithms*;
- Oberkampf & Roy, *Verification and Validation in Scientific Computing*;
- Roache, *Verification and Validation in Computational Science and Engineering*;
- NASA-HDBK-7009 series — implementation guidance;
- ASME V&V standards — выбирать документ по application domain.
- Saltelli et al., *Global Sensitivity Analysis*;
- Smith, *Uncertainty Quantification*;
- JCGM 101 — Monte Carlo propagation of distributions.
- INCOSE, *Systems Engineering Handbook*;
- Blanchard & Fabrycky, *Systems Engineering and Analysis*;
- Leveson, *Engineering a Safer World*;
- ISO/IEC/IEEE 15288 — system life-cycle processes.
- Seborg et al., *Process Dynamics and Control*;
- Skogestad & Postlethwaite, *Multivariable Feedback Control*;
- Biegler, Grossmann & Westerberg, *Systematic Methods of Chemical Process Design*;
- Towler & Sinnott, *Chemical Engineering Design*;
- IEC 61511 — safety instrumented systems for process industry.
- Anderson, *Fracture Mechanics*;
- Dowling, *Mechanical Behavior of Materials*;
- Suresh, *Fatigue of Materials*;
- Stephens et al., *Metal Fatigue in Engineering*;
- ASTM E399/E1820/E647/E466 families — проверять current scope/edition;
- ASM Handbooks, *Fatigue and Fracture*.
- Modarres, Kaminskiy & Krivtsov, *Reliability Engineering and Risk Analysis*;
- O’Connor & Kleyner, *Practical Reliability Engineering*;
- Rausand & Høyland, *System Reliability Theory*;
- Jardine & Tsang, *Maintenance, Replacement, and Reliability*;
- IEC 60812 — FMEA/FMECA;
- ISO 31000 — risk management principles.
- Groover, *Fundamentals of Modern Manufacturing*;
- Kalpakjian & Schmid, *Manufacturing Engineering and Technology*;
- Shigley, *Mechanical Engineering Design*;
- ASME Y14.5 / ISO GPS family — geometric tolerancing;
- Wickens et al., *Engineering Psychology and Human Performance*;
- Salvendy, *Handbook of Human Factors and Ergonomics*;
- ISO 11064 — control-centre ergonomics;
- IEC 62682 / ISA-18.2 — alarm management context.
- Hillier & Lieberman, *Introduction to Operations Research*;
- Winston, *Operations Research*;
- Turton et al., *Analysis, Synthesis, and Design of Chemical Processes*;
- Blank & Tarquin, *Engineering Economy*;
- ISO 14040/14044 — LCA framework, подробнее в Earth/environment guide.
- **П. В. Новицкий, И. А. Зограф, «Оценка погрешностей результатов измерений»** — классическая школа
  measurement-error analysis; терминологию приводить к VIM/GUM. **[В, И]**
- **В. А. Грановский, Т. Н. Сирая, «Методы обработки экспериментальных данных при измерениях»** —
  статистическая обработка и measurement models. **[В]**
- **П. П. Орнатский, «Теоретические основы информационно-измерительной техники»** — signals,
  преобразования и instrument chains; hardware examples обновлять. **[В, И]**
- **В. А. Бесекерский, Е. П. Попов, «Теория систем автоматического регулирования»** — классический
  курс feedback/control. **[В, И]**
- **Е. С. Вентцель, «Исследование операций: задачи, принципы, методология»** — русскоязычный вход в
  optimization и decision models. **[В]**
- **Б. В. Гнеденко, Ю. К. Беляев, А. Д. Соловьёв, «Математические методы в теории надёжности»** —
  фундаментальная probabilistic reliability school, издававшаяся за рубежом. **[М]**
- **В. В. Болотин, «Прогнозирование ресурса машин и конструкций»** — stochastic strength, fatigue и
  lifetime. **[М, В]**
- **Г. П. Черепанов, «Механика хрупкого разрушения»** — отечественная fracture-mechanics школа,
  работа известна в международной литературе. **[М]**
- **В. П. Когаев, Н. А. Махутов, А. П. Гусенков, «Расчёты деталей машин и конструкций на прочность и
  долговечность»** — fatigue и engineering life calculations. **[В]**
- **И. А. Биргер, Б. Ф. Шорр, Г. Б. Иосилевич, «Расчёт на прочность деталей машин»** — связка stress
  concentration, fatigue и machine elements. **[В]**
- **Б. М. Базров, «Основы технологии машиностроения»** — manufacturing route, dimensional chains и
  process accuracy. **[В]**

### Метрология, systems и надёжность: онлайн-источники и данные

- BIPM, *SI Brochure*: https://www.bipm.org/en/publications/si-brochure
- JCGM, GUM/VIM и supplements: https://www.bipm.org/en/committees/jc/jcgm/publications
- NIST/SEMATECH, *e-Handbook of Statistical Methods*: https://www.itl.nist.gov/div898/handbook/
- NIST Technical Note 1297: https://www.nist.gov/pml/nist-technical-note-1297
- NASA Models and Simulations standards: https://standards.nasa.gov/systems-and-subsystem-test
- NASA, *Systems Engineering Handbook*: https://www.nasa.gov/reference/systems-engineering-handbook/
- NASA Systems Engineering standards: https://standards.nasa.gov/
- CCPS: https://www.aiche.org/ccps
- OSHA PSM hazard resources: https://www.osha.gov/process-safety-management/hazards
- U.S. Chemical Safety Board incident investigations: https://www.csb.gov/
- ASNT resources: https://www.asnt.org/
- NIST Smart Manufacturing: https://www.nist.gov/topics/smart-manufacturing
- FAA Human Factors: https://www.faa.gov/about/initiatives/maintenance_hf
- NIOSH aviation safety: https://www.cdc.gov/niosh/aviation/
- NIOSH aircrew noise: https://www.cdc.gov/niosh/aviation/prevention/aircrew-noise.html

### Поверхности, сложные потоки и энергетическая физика: книги, отчёты и стандарты

- Israelachvili, *Intermolecular and Surface Forces*;
- Adamson & Gast, *Physical Chemistry of Surfaces*;
- Hunter, *Foundations of Colloid Science*;
- Hiemenz & Rajagopalan, *Principles of Colloid and Surface Chemistry*;
- Russel, Saville & Schowalter, *Colloidal Dispersions*;
- Bird, Armstrong & Hassager, *Dynamics of Polymeric Liquids*;
- Macosko, *Rheological Principles, Measurements, and Applications*;
- Barnes, Hutton & Walters, *An Introduction to Rheology*;
- Larson, *The Structure and Rheology of Complex Fluids*.
- Brennen, *Fundamentals of Multiphase Flow*;
- Clift, Grace & Weber, *Bubbles, Drops, and Particles*;
- Crowe et al., *Multiphase Flows with Droplets and Particles*;
- Ishii & Hibiki, *Thermo-Fluid Dynamics of Two-Phase Flow*;
- Wallis, *One-Dimensional Two-Phase Flow*.
- Bear, *Dynamics of Fluids in Porous Media*;
- Dullien, *Porous Media*;
- Crittenden et al., *MWH’s Water Treatment*;
- Baker, *Membrane Technology and Applications*;
- Mulder, *Basic Principles of Membrane Technology*.
- Nedderman, *Statics and Kinematics of Granular Materials*;
- Rhodes, *Introduction to Particle Technology*;
- de Gennes, *Granular Matter: A Tentative View*;
- Schulze, *Powders and Bulk Solids*.
- Stachowiak & Batchelor, *Engineering Tribology*;
- Hamrock, Schmid & Jacobson, *Fundamentals of Fluid Film Lubrication*;
- Hutchings & Shipway, *Tribology: Friction and Wear*;
- Bhushan, *Introduction to Tribology*;
- ASTM G-series wear/erosion standards — проверять method scope.
- Turns, *An Introduction to Combustion*;
- Law, *Combustion Physics*;
- Glassman, Yetter & Glumac, *Combustion*;
- Williams, *Combustion Theory*;
- Poinsot & Veynante, *Theoretical and Numerical Combustion*;
- Heywood, *Internal Combustion Engine Fundamentals*.
- SFPE, *Handbook of Fire Protection Engineering*;
- Drysdale, *An Introduction to Fire Dynamics*;
- NFPA standards — current edition/scope через официальный каталог.
- Kinsler et al., *Fundamentals of Acoustics*;
- Pierce, *Acoustics*;
- Fahy & Gardonio, *Sound and Structural Vibration*;
- Goldstein, *Aeroacoustics*;
- Chen, *Introduction to Plasma Physics and Controlled Fusion*;
- Lieberman & Lichtenberg, *Principles of Plasma Discharges and Materials Processing*;
- Fridman, *Plasma Chemistry*;
- Bittencourt, *Fundamentals of Plasma Physics*;
- O’Hanlon, *A User’s Guide to Vacuum Technology*;
- Roth, *Vacuum Technology*;
- Jousten, *Handbook of Vacuum Technology*;
- Barron, *Cryogenic Systems*;
- Flynn, *Cryogenic Engineering*;
- Weisend, *Handbook of Cryogenic Engineering*;
- **Б. В. Дерягин, Н. В. Чураев, В. М. Муллер, «Поверхностные силы»** — disjoining pressure, thin
  films и colloidal interactions; есть международная версия *Surface Forces*. **[М]**
- **Е. Д. Щукин, А. В. Перцов, Е. А. Амелина, «Коллоидная химия»** — surfaces, dispersions, wetting
  и stability. **[В]**
- **Ю. Г. Фролов, «Курс коллоидной химии»** — системный русскоязычный университетский курс. **[В]**
- **К. С. Урьев, «Физико-химическая динамика дисперсных систем»** — rheology и processing
  concentrated dispersions. **[В]**
- **Л. Д. Ландау, Е. М. Лифшиц, «Гидродинамика»** — continuum fluids, waves, instabilities и
  transport foundation; многочисленные зарубежные издания. **[М]**
- **С. С. Кутателадзе, «Основы теории теплообмена»** — heat transfer и boiling-school context;
  correlations сверять с current data. **[М, И]**
- **И. В. Крагельский, М. Н. Добычин, В. С. Комбалов, «Основы расчётов на трение и износ»** —
  mechanistic tribology; издавалась на английском. **[М]**
- **Д. Н. Гаркунов, «Триботехника»** — friction, wear и lubrication для машин. **[В]**
- **Я. Б. Зельдович, Г. И. Баренблатт, В. Б. Либрович, Г. М. Махвиладзе, «Математическая теория
  горения и взрыва»** — международно известная mathematical combustion school; использовать для
  theory/safety, не для operational recipes. **[М]**
- **Ю. П. Райзер, «Физика газового разряда»** — plasma/discharge physics, доступна в международных
  изданиях. **[М]**
- **Л. М. Бреховских, «Волны в слоистых средах»** — wave propagation и acoustics, переведена и
  широко цитируется. **[М]**
- **Л. Н. Розанов, «Вакуумная техника»** — сильный инженерный курс; components и нормы обновлять.
  **[В, И]**
- **А. М. Архаров и соавт., «Криогенные системы»** — thermodynamics, equipment и engineering context
  cryogenics. **[В]**

### Поверхности, сложные потоки и энергетическая физика: онлайн-источники и данные

- IUPAC Gold Book: https://goldbook.iupac.org/
- STLE: https://www.stle.org/
- Cantera: https://cantera.org/
- NASA CEA: https://www1.grc.nasa.gov/research-and-engineering/ceaweb/
- NIST Chemistry WebBook: https://webbook.nist.gov/chemistry/
- Burcat thermochemical data: https://burcat.technion.ac.il/
- LLNL combustion mechanisms: https://combustion.llnl.gov/mechanisms
- CCPS combustible/reactivity resources: https://www.aiche.org/ccps
- U.S. CSB investigations: https://www.csb.gov/
- NASA aeroacoustics research: https://www.nasa.gov/aeronautics/
- PlasmaPy: https://www.plasmapy.org/
- NIST Atomic Spectra Database: https://physics.nist.gov/asd
- NIST vacuum metrology: https://www.nist.gov/pml/sensor-science/fluid-metrology
- NIST REFPROP: https://www.nist.gov/srd/refprop
- NIST Cryogenic Materials Properties Reference List:
  https://www.nist.gov/mml/acmd/cryogenic-materials-properties-reference-list

### Земля, специальные материалы и окружающая среда: книги, отчёты и стандарты

- Tarbuck, Lutgens & Tasa, *Earth: An Introduction to Physical Geology*;
- Marshak, *Essentials of Geology* и *Earth: Portrait of a Planet*;
- Winter, *Principles of Igneous and Metamorphic Petrology*;
- Nichols, *Sedimentology and Stratigraphy*;
- Fossen, *Structural Geology*;
- Klein & Dutrow, *Manual of Mineral Science*;
- Nesse, *Introduction to Mineralogy*;
- Putnis, *Introduction to Mineral Sciences*;
- Giacovazzo et al., *Fundamentals of Crystallography*.
- White, *Geochemistry*;
- Faure, *Principles and Applications of Geochemistry*;
- Drever, *The Geochemistry of Natural Waters*;
- Appelo & Postma, *Geochemistry, Groundwater and Pollution*;
- Stumm & Morgan, *Aquatic Chemistry*.
- Lowrie, *Fundamentals of Geophysics*;
- Kearey, Brooks & Hill, *An Introduction to Geophysical Exploration*;
- Menke, *Geophysical Data Analysis: Discrete Inverse Theory*;
- Telford, Geldart & Sheriff, *Applied Geophysics*.
- Rossi & Deutsch, *Mineral Resource Estimation*;
- Journel & Huijbregts, *Mining Geostatistics*;
- Hoek & Bray, *Rock Slope Engineering*;
- Brady & Brown, *Rock Mechanics for Underground Mining*;
- Jaeger, Cook & Zimmerman, *Fundamentals of Rock Mechanics*;
- Das, *Principles of Geotechnical Engineering*;
- Wills & Finch, *Wills’ Mineral Processing Technology*;
- Fuerstenau & Han, *Principles of Mineral Processing*;
- SME, *Mineral Processing and Extractive Metallurgy Handbook*;
- Napier-Munn et al., *Mineral Comminution Circuits*;
- Kingery, Bowen & Uhlmann, *Introduction to Ceramics*;
- Barsoum, *Fundamentals of Ceramics*;
- Carter & Norton, *Ceramic Materials*;
- Rahaman, *Ceramic Processing and Sintering*;
- ASTM C-series standards for ceramic tests — current method/scope.
- Shelby, *Introduction to Glass Science and Technology*;
- Varshneya & Mauro, *Fundamentals of Inorganic Glasses*;
- Taylor, *Cement Chemistry*;
- Mehta & Monteiro, *Concrete: Microstructure, Properties, and Materials*;
- Hewlett & Liska, *Lea’s Chemistry of Cement and Concrete*;
- Neville, *Properties of Concrete*.
- Schacht, *Refractories Handbook*;
- Routschka & Wuthnow, *Pocket Manual of Refractory Materials*;
- Kittel, *Introduction to Solid State Physics*;
- Ashcroft & Mermin, *Solid State Physics*;
- Callister & Rethwisch, *Materials Science and Engineering*;
- Porter, Easterling & Sherif, *Phase Transformations in Metals and Alloys*;
- Sze & Ng, *Physics of Semiconductor Devices*;
- Pierret, *Semiconductor Device Fundamentals*;
- Streetman & Banerjee, *Solid State Electronic Devices*;
- Schroder, *Semiconductor Material and Device Characterization*;
- Cullity & Graham, *Introduction to Magnetic Materials*;
- Kasap, *Principles of Electronic Materials and Devices*;
- Hecht, *Optics*;
- Saleh & Teich, *Fundamentals of Photonics*;
- Goodman, *Introduction to Fourier Optics*;
- Boyd, *Nonlinear Optics*;
- Snoeyink & Jenkins, *Water Chemistry*;
- Crittenden et al., *MWH’s Water Treatment*;
- Metcalf & Eddy, *Wastewater Engineering*;
- Seinfeld & Pandis, *Atmospheric Chemistry and Physics*;
- Wallace & Hobbs, *Atmospheric Science*;
- Schwarzenbach et al., *Environmental Organic Chemistry*;
- Manahan, *Environmental Chemistry*;
- ISO 14040/14044 — framework/requirements;
- ISO 14067 — product carbon footprint context;
- Nelson & Cox, *Lehninger Principles of Biochemistry*;
- Shuler, Kargi & DeLisa, *Bioprocess Engineering*;
- Doran, *Bioprocess Engineering Principles*;
- Brady & Weil, *The Nature and Properties of Soils*;
- Hillel, *Introduction to Environmental Soil Physics*;
- Havlin et al., *Soil Fertility and Fertilizers*;
- **В. И. Вернадский, «Очерки геохимии» и «Биосфера»** — фундаментальная geochemical/ biosphere
  perspective, переводы и мировое влияние; quantitative data исторические. **[М, И]**
- **А. Е. Ферсман, «Геохимия»** — развитие geochemical classification и resource thinking; читать
  вместе с современной thermodynamics/speciation. **[М, И]**
- **Д. С. Коржинский, «Физико-химические основы анализа парагенезисов минералов»** — thermodynamic
  petrology и open-component systems; международно признанная школа. **[М]**
- **А. И. Перельман, Н. С. Касимов, «Геохимия ландшафта»** — migration/barriers и environmental
  geochemistry. **[В]**
- **В. А. Авдохин, «Основы обогащения полезных ископаемых»** — mineral preparation, separation и
  flowsheets. **[В]**
- **А. А. Абрамов, «Переработка, обогащение и комплексное использование твёрдых полезных
  ископаемых»** — системная отечественная школа mineral processing. **[В]**
- **П. П. Будников, Д. Н. Полубояринов, «Химическая технология керамики и огнеупоров»** и профильные
  продолжения школы — исторически сильная ceramic base; оборудование, energy и standards обновлять.
  **[В, И]**
- **Ю. М. Баженов, «Технология бетона»** — concrete composition, process и durability. **[В]**
- **А. Ф. Иоффе, «Физика полупроводников»** — исторически влиятельная semiconductor physics school с
  зарубежным изданием; device data устарели. **[М, И]**
- **Г. С. Ландсберг, «Оптика»** — сильный университетский курс wave/geometrical optics. **[В]**
- **О. А. Алекин, «Основы гидрохимии»** — natural-water composition/classification; speciation и
  environmental standards сверять с современными sources. **[В, И]**
- **Ю. А. Израэль, «Экология и контроль состояния природной среды»** — monitoring и
  environmental-system perspective; нормативные части исторические. **[В, И]**
- **Д. С. Орлов, «Химия почв»** — soil organic/mineral chemistry и sorption. **[В]**

### Земля, специальные материалы и окружающая среда: онлайн-источники и данные

- USGS education/data: https://www.usgs.gov/
- National Geologic Map Database: https://ngmdb.usgs.gov/ngmdb/ngmdb_home.html
- OneGeology: https://onegeology.org/
- RRUFF mineral spectra/XRD/chemistry: https://www.rruff.net/
- Crystallography Open Database: https://www.crystallography.net/cod/
- IMA mineral nomenclature/classification: https://www.ima-mineralogy.org/
- USGS PHREEQC: https://www.usgs.gov/software/phreeqc-version-3
- EarthChem: https://www.earthchem.org/
- USGS water data: https://waterdata.usgs.gov/
- current CRIRSCO-family reporting definitions for context: https://www.crirsco.com/
- NIOSH Mining: https://www.cdc.gov/niosh/mining/
- USGS minerals information/material flow:
  https://www.usgs.gov/programs/mineral-resources-program/science/minerals-information-and-material-flow
- NIST Materials Data Resources: https://www.nist.gov/mgi/materials-data-resources
- Materials Project: https://materialsproject.org/
- NIST materials repository: https://materialsdata.nist.gov/
- NIST Atomic Spectra Database: https://physics.nist.gov/asd
- NIST optical metrology: https://www.nist.gov/topics/optics-and-photonics
- WHO, *Guidelines for Drinking-water Quality*, fourth edition incorporating the first and second
  addenda: https://www.who.int/publications/i/item/9789240045064
- U.S. EPA Water Research: https://www.epa.gov/water-research
- Jacob, *Introduction to Atmospheric Chemistry*:
  https://acmg.seas.harvard.edu/education/intro-atmos-chem-book
- NOAA Global Monitoring Laboratory: https://gml.noaa.gov/
- NASA Earthdata: https://www.earthdata.nasa.gov/
- IPCC reports: https://www.ipcc.ch/reports/
- U.S. EPA CompTox: https://comptox.epa.gov/dashboard/
- ECHA chemicals information: https://echa.europa.eu/information-on-chemicals
- UNEP resources: https://www.unep.org/
- openLCA: https://www.openlca.org/
- Argonne GREET: https://greet.anl.gov/
- ecoinvent: https://ecoinvent.org/ — licensed inventory, respect terms;
- U.S. EPA sustainability/LCA tools: https://www.epa.gov/e3/e3-sustainability-tools
- IPCC assessment/GWP basis: https://www.ipcc.ch/reports/
- OpenStax Biology: https://openstax.org/details/books/biology-2e
- WHO Laboratory Biosafety Manual: https://www.who.int/publications/i/item/9789240011311
- USDA NRCS soils: https://www.nrcs.usda.gov/resources/data-and-reports/soils
- FAO Soils Portal: https://www.fao.org/soils-portal/

### Углублённая библиография

- M. Spivak, *Calculus*, Vols. I–II — строгий курс анализа с задачами.
- T. M. Apostol, *Calculus*, Vols. I–II — анализ, многомерное исчисление, линейная алгебра и приложения.
- W. Rudin, *Principles of Mathematical Analysis* — строгий honours/graduate bridge.
- R. Courant, F. John, *Introduction to Calculus and Analysis*, Vols. I–II.
- S. Axler, *Linear Algebra Done Right*.
- P. Lax, *Linear Algebra and Its Applications*.
- M. Artin, *Algebra* — группы, кольца, поля и линейная алгебра.
- J. M. Lee, *Introduction to Smooth Manifolds* — многообразия и дифференциальная геометрия.
- V. I. Arnold, *Ordinary Differential Equations*.
- L. Perko, *Differential Equations and Dynamical Systems*.
- L. C. Evans, *Partial Differential Equations*.
- M. E. Taylor, *Partial Differential Equations I–III*.
- R. Courant, D. Hilbert, *Methods of Mathematical Physics*, Vols. I–II.
- R. Durrett, *Probability: Theory and Examples*.
- A. W. van der Vaart, *Asymptotic Statistics*.
- E. Süli, D. F. Mayers, *An Introduction to Numerical Analysis*.
- E. Hairer, S. P. Nørsett, G. Wanner, *Solving Ordinary Differential Equations I–II*.
- R. J. LeVeque, *Finite Difference Methods for Ordinary and Partial Differential Equations*.
- J. Nocedal, S. Wright, *Numerical Optimization*.
- S. Boyd, L. Vandenberghe, *Convex Optimization*.
- H. Brezis, *Functional Analysis, Sobolev Spaces and Partial Differential Equations*.
- L. C. Evans, *Measure Theory and Fine Properties of Functions*.
- I. M. Gelfand, S. V. Fomin, *Calculus of Variations*.
- R. Temam, *Navier–Stokes Equations*.
- C. Canuto et al., *Spectral Methods: Fundamentals in Single Domains*.
- L. D. Landau, E. M. Lifshitz, *Mechanics*; *Fluid Mechanics*; *Theory of Elasticity*.
- G. K. Batchelor, *An Introduction to Fluid Dynamics*.
- C. Truesdell, W. Noll, *The Non-Linear Field Theories of Mechanics*.
- J. N. Reddy, *An Introduction to Continuum Mechanics*.
- S. P. Timoshenko, J. M. Gere, *Theory of Elastic Stability*.
- J. D. Anderson, *Fundamentals of Aerodynamics*; *Modern Compressible Flow*.
- J. Katz, A. Plotkin, *Low-Speed Aerodynamics*.
- J. D. Anderson, *Aircraft Performance and Design*.
- E. L. Houghton et al., *Aerodynamics for Engineering Students*.
- J. Seddon, E. L. Goldsmith, *Introduction to Aircraft Flight Mechanics*.
- M. J. Lighthill, *Waves in Fluids*.
- J. N. Newman, *Marine Hydrodynamics*.
- R. H. Faltinsen, *Sea Loads on Ships and Offshore Structures*.
- R. B. Munson et al., *Fundamentals of Fluid Mechanics*.
- H. B. Callen, *Thermodynamics and an Introduction to Thermostatistics*.
- E. M. Lifshitz, L. P. Pitaevskii, *Statistical Physics*.
- F. P. Incropera et al., *Fundamentals of Heat and Mass Transfer*.
- S. K. Kothandaraman, *Fundamentals of Heat and Mass Transfer*.
- S. Glasstone, R. H. Lovberg, *Controlled Thermonuclear Reactions*.
- K. Ogata, *Modern Control Engineering*.
- H. K. Khalil, *Nonlinear Systems*.
- K. J. Åström, R. M. Murray, *Feedback Systems*.
- P. J. Roache, *Verification and Validation in Computational Science and Engineering*.
- J. C. Blazek, *Computational Fluid Dynamics: Principles and Applications*.
- P. W. Atkins, J. de Paula, *Physical Chemistry*.
- I. N. Levine, *Physical Chemistry*.
- J. E. McMurry, *Organic Chemistry* — базовый университетский курс с механизмами.
- R. T. Morrison, R. N. Boyd, *Organic Chemistry*.
- F. A. Cotton, G. Wilkinson et al., *Advanced Inorganic Chemistry*.
- I. N. Levine, *Quantum Chemistry*.
- D. A. McQuarrie, J. D. Simon, *Physical Chemistry: A Molecular Approach*.
- R. A. Masel, *Chemical Kinetics and Catalysis*.
- J. M. Smith, H. C. Van Ness, M. M. Abbott, *Introduction to Chemical Engineering Thermodynamics*.
- J. M. Thomas, W. J. Thomas, *Principles and Practice of Heterogeneous Catalysis*.
- D. A. Skoog et al., *Principles of Instrumental Analysis*.
- A. J. Bard, L. R. Faulkner, *Electrochemical Methods*.
- C. M. A. Brett, A. M. O. Brett, *Electrochemistry: Principles, Methods, and Applications*.
- W. D. Callister, D. G. Rethwisch, *Materials Science and Engineering*.
- R. W. Cahn, P. Haasen, E. J. Kramer, *Materials Science and Technology*.
- O. Levenspiel, *Chemical Reaction Engineering*.
- J. M. Coulson, J. F. Richardson, *Chemical Engineering*, multi-volume set.
- R. K. Sinnott, G. Towler, *Chemical Engineering Design*.
- R. H. Perry, D. W. Green, *Perry's Chemical Engineers' Handbook*.
- JCGM 100:2008, *Evaluation of Measurement Data — Guide to the Expression of Uncertainty in Measurement (GUM)*.
- JCGM 101:2008, *Supplement 1 to the GUM — Monte Carlo Method*.
- B. N. Taylor, C. E. Kuyatt, *NIST Technical Note 1297*.
- D. C. Montgomery, *Design and Analysis of Experiments*.
- A. V. Oppenheim, A. S. Willsky, S. H. Nawab, *Signals and Systems*.
- S. M. Kay, *Fundamentals of Statistical Signal Processing*.
- L. Ljung, *System Identification: Theory for the User*.
- E. E. Lewis, *Introduction to Reliability Engineering*.
- M. Rausand, A. Høyland, *System Reliability Theory*.
- P. D. T. O'Connor, A. Kleyner, *Practical Reliability Engineering*.
- T. L. Anderson, *Fracture Mechanics: Fundamentals and Applications*.
- J. Schijve, *Fatigue of Structures and Materials*.
- INCOSE, *Systems Engineering Handbook*.
- E. Balagurusamy, *Reliability Engineering*.
- M. G. Xie et al., *Statistical Methods for Reliability Data*.
- J. N. Israelachvili, *Intermolecular and Surface Forces*.
- A. W. Adamson, A. P. Gast, *Physical Chemistry of Surfaces*.
- R. J. Hunter, *Foundations of Colloid Science*.
- J. C. Berg, *An Introduction to Interfaces and Colloids*.
- R. B. Bird, W. E. Stewart, E. N. Lightfoot, *Transport Phenomena*.
- R. B. Bird et al., *Dynamics of Polymeric Liquids*.
- M. C. Potter, D. C. Wiggert, *Mechanics of Fluids*.
- M. Kaviany, *Principles of Heat Transfer in Porous Media*.
- C. T. Crowe et al., *Multiphase Flows with Droplets and Particles*.
- C. W. Macosko, *Rheology: Principles, Measurements, and Applications*.
- N. P. Cheremisinoff, *An Introduction to Polymer Rheology*.
- R. A. Mashelkar, *Transport Phenomena in Multiphase Systems*.
- B. Bhushan, *Introduction to Tribology*.
- M. J. Adams, *Powder and Bulk Solids*.
- S. R. Turns, *An Introduction to Combustion*.
- C. K. Law, *Combustion Physics*.
- F. F. Chen, *Introduction to Plasma Physics and Controlled Fusion*.
- J. F. O'Hanlon, *A User's Guide to Vacuum Technology*.
- K. D. Timmerhaus, T. M. Flynn, *Cryogenic Process Engineering*.
- F. J. Pettijohn, *Sedimentary Rocks*.
- W. D. Nesse, *Introduction to Mineralogy*.
- C. Klein, B. Dutrow, *The Manual of Mineral Science*.
- H. Rollinson, *Using Geochemical Data*.
- W. M. White, *Geochemistry*.
- W. M. Telford et al., *Applied Geophysics*.
- J. M. Reynolds, *An Introduction to Applied and Environmental Geophysics*.
- H. K. W. Hoek, E. T. Brown, *Underground Excavations in Rock*.
- W. A. Hustrulid, *Underground Mining Methods*.
- B. A. Wills, J. Finch, *Wills' Mineral Processing Technology*.
- M. W. Barsoum, *Fundamentals of Ceramics*.
- J. S. Reed, *Principles of Ceramics Processing*.
- W. D. Kingery et al., *Introduction to Ceramics*.
- J. E. Shelby, *Introduction to Glass Science and Technology*.
- A. M. Neville, *Properties of Concrete*.
- C. N. Sawyer, P. L. McCarty, G. F. Parkin, *Chemistry for Environmental Engineering and Science*.
- W. J. Weber, *Environmental Systems and Processes*.
- M. A. Haith, *Environmental Systems Optimization*.
- J. B. Guinée et al., *Handbook on Life Cycle Assessment*.

<!-- SOURCES:END -->
