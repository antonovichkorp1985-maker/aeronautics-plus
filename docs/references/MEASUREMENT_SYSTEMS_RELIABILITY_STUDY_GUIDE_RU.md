# Метрология, вычислительная достоверность, системы и надёжность

<!-- TOC:START -->
## Оглавление

- [0. Уровни и правило допуска к реализации](#measure-0)
- [1. Какой результат должен дать маршрут](#measure-1)
- [2. Порядок обучения](#measure-2)
- [3. Метрология](#measure-3)
- [4. Sensors и instrumentation](#measure-4)
- [5. Sampling и signal processing](#measure-5)
- [6. Experiment design и system identification](#measure-6)
- [7. Numerical credibility и software engineering](#measure-7)
- [8. Uncertainty и sensitivity](#measure-8)
- [9. Systems engineering](#measure-9)
- [10. Process systems engineering и control](#measure-10)
- [11. Fracture, fatigue и creep](#measure-11)
- [12. Reliability, maintenance и diagnostics](#measure-12)
- [13. Manufacturing, tolerances и quality](#measure-13)
- [14. Human factors и environmental physiology](#measure-14)
- [15. Operations research, economics и lifecycle decisions](#measure-15)
- [16. Общая модель данных](#measure-16)
- [16A. Сильная русскоязычная полка](#measure-16a)
- [17. Практические проекты](#measure-17)
- [18. Критерии завершения](#measure-18)
- [Сводный список источников](#measure-sources)

> [Единая программа](INTEGRATED_SCIENCE_CURRICULUM_RU.md) · [Карта покрытия](SCIENCE_COVERAGE_MAP_RU.md) · [Источники этого файла](#measure-sources)
<!-- TOC:END -->


Статус: обязательный сквозной маршрут для всех физических, химических и инженерных
механик проекта. Дата аудита источников: **1 октября 2026 года**.

Связанные документы:

- [математическое ядро](MATHEMATICS_STUDY_GUIDE_RU.md);
- [инженерная физика](REALISM_STUDY_GUIDE_RU.md);
- [химия и материалы](CHEMISTRY_MOD_STUDY_GUIDE_RU.md);
- [общая карта научного покрытия](SCIENCE_COVERAGE_MAP_RU.md).

Этот справочник закрывает области, которые нельзя заменить ещё одной формулой тяги или
реакции: измерения, sensors, signal processing, numerical V&V, uncertainty, systems
engineering, process control/safety, fracture/fatigue/reliability, manufacturing,
человеческий фактор и engineering economics.

> Это учебно-проектный документ. Он не является руководством по эксплуатации самолёта,
> химического объекта, шахты, pressure equipment или иной опасной реальной системы.

---

<a id="measure-0"></a>
## 0. Уровни и правило допуска к реализации

- **Б** — термины, единицы и расчёты по одной формуле;
- **С** — связанная модель с данными, uncertainty и тестами;
- **П** — competing models, failure cases, V&V и system integration;
- **R** — стандарт, база или справочник.

Механика готова к production-коду только если одновременно известны:

1. measurand или intended use модели;
2. units и reference conditions;
3. source и версия данных;
4. measurement/model uncertainty;
5. valid range;
6. numerical error;
7. benchmark;
8. failure behavior;
9. владелец интерфейса;
10. критерий принятия решения.

---

<a id="measure-1"></a>
## 1. Какой результат должен дать маршрут

Разработчик должен уметь:

- отличать quantity, measurand, indication, measurement result и true value;
- строить calibration и uncertainty budget;
- не путать accuracy с precision;
- моделировать sensor dynamics, noise, bias, drift и saturation;
- выбирать sampling rate и anti-alias filtering;
- отличать code verification, solution verification и validation;
- проверять conservation, convergence и limiting cases;
- распространять parameter и model-form uncertainty;
- превращать stakeholder need в проверяемое requirement;
- вести interfaces, configuration и traceability;
- анализировать hazard, failure и common cause;
- связывать load history с fatigue/fracture/remaining life;
- учитывать tolerances, manufacturing route и inspection;
- проектировать UI/alarm так, чтобы он не создавал скрытое опасное состояние;
- сравнивать варианты по lifecycle, а не только по максимальной мощности.

---

<a id="measure-2"></a>
## 2. Порядок обучения

| Этап | Тема | Проектный результат | Уровень |
|---|---|---|---:|
| 1 | SI, VIM, GUM | quantity schema + uncertainty budget | Б |
| 2 | Sensors и calibration | модель датчика с drift/hysteresis | Б–С |
| 3 | Sampling/DSP | anti-alias + spectrum test | С |
| 4 | Experiment/identification | fitted model с residual diagnostics | С |
| 5 | Numerical V&V | convergence и benchmark report | С–П |
| 6 | UQ/sensitivity | uncertainty propagation + ranking | П |
| 7 | Systems engineering | requirement/interface/V&V matrix | С |
| 8 | Control/process systems | dynamic plant + safeguards | С–П |
| 9 | Fracture/fatigue/reliability | damage/failure model | С–П |
| 10 | Manufacturing/NDT | tolerance и inspection plan | С |
| 11 | Human factors | alarm/UI review | С |
| 12 | Economics/operations | lifecycle trade study | С |

Этапы не заменяют математику: вероятность, статистика, линейная алгебра, ОДУ,
оптимизация и numerical analysis проходят параллельно.

---

<a id="measure-3"></a>
## 3. Метрология

### 3.1 Базовые различия

- **Quantity** — свойство, которому можно приписать число и reference.
- **Measurand** — конкретно определённая величина, которую намерены измерить.
- **Indication** — показание прибора до полной интерпретации.
- **Measurement result** — значение вместе с uncertainty и необходимой metadata.
- **Reference quantity value** — значение, используемое для calibration/comparison.
- **Error** относится к разности с reference; **uncertainty** описывает сомнение в
  результате и не требует знания недоступного «истинного значения».

В игровом UI нельзя писать просто `температура = 900`. Нужны unit, location,
reference/state, resolution и хотя бы класс uncertainty.

### 3.2 Accuracy, precision и resolution

- accuracy — близость результата к reference в качественном смысле;
- precision — разброс при заданных условиях;
- repeatability — близкие условия;
- reproducibility — изменённые лаборатория/оператор/оборудование;
- resolution — минимальное различимое изменение indication;
- sensitivity — изменение output на изменение input;
- selectivity — способность отделить нужный measurand от interferences;
- trueness связано с bias большого набора результатов.

Высокая resolution не гарантирует ни accuracy, ни traceability.

### 3.3 Measurement model

Пусть результат:

`y = f(x₁, x₂, …, xₙ)`.

При linearized propagation combined standard uncertainty:

`u_c²(y) = Σᵢ cᵢ² u²(xᵢ) + 2 Σᵢ<ⱼ cᵢ cⱼ u(xᵢ, xⱼ)`,

где `cᵢ = ∂f/∂xᵢ` — sensitivity coefficient, а covariance term нельзя выбрасывать при
коррелированных inputs.

Expanded uncertainty:

`U = k u_c`.

Coverage factor `k` выбирается из требуемого coverage и distribution assumptions, а не
всегда автоматически равен 2.

### 3.4 Type A и Type B

- Type A — evaluation из statistical analysis наблюдений;
- Type B — evaluation из сертификатов, calibration history, resolution, literature,
  specifications и иных данных.

Это способы **оценивания**, а не синонимы random и systematic error.

### 3.5 Calibration chain

Различать:

- calibration;
- adjustment;
- verification;
- validation метода;
- metrological traceability;
- reference material;
- check standard;
- proficiency/interlaboratory comparison.

Calibration не делает прибор «точным навсегда». Между calibrations остаются drift,
transport damage, environment и use-dependent effects.

### 3.6 Conformity assessment

Если specification limit близок к measurement uncertainty, простое сравнение показания с
limit создаёт false accept/false reject. Нужны:

- decision rule;
- guard band;
- consumer/producer risk;
- declared coverage;
- treatment of values near boundary.

В механике качества предмет не должен автоматически считаться годным только потому,
что округлённое число совпало с пределом.

### 3.7 Основные источники

- BIPM, *SI Brochure*: https://www.bipm.org/en/publications/si-brochure
- JCGM, GUM/VIM и supplements:
  https://www.bipm.org/en/committees/jc/jcgm/publications
- NIST/SEMATECH, *e-Handbook of Statistical Methods*:
  https://www.itl.nist.gov/div898/handbook/
- NIST Technical Note 1297:
  https://www.nist.gov/pml/nist-technical-note-1297
- ISO/IEC 17025 — competence of testing/calibration laboratories;
- ISO 10012 — measurement management systems;
- Doebelin & Manik, *Measurement Systems*;
- Bentley, *Principles of Measurement Systems*;
- Beckwith, Marangoni & Lienhard, *Mechanical Measurements*.

ISO/IEC documents often платные; в репозиторий хранить только собственные summaries,
identifiers и допустимые metadata.

---

<a id="measure-4"></a>
## 4. Sensors и instrumentation

### 4.1 Static model

Минимальная calibration model:

`z = a₀ + a₁x + a₂x² + … + b_T(T - T_ref) + ε`.

Документировать:

- range/span;
- sensitivity;
- zero offset;
- nonlinearity;
- hysteresis;
- cross-sensitivity;
- saturation/dead zone;
- loading effect;
- environment;
- drift и ageing.

### 4.2 Dynamic response

Первая модель датчика первого порядка:

`τ dy/dt + y = Kx`.

Step response:

`y(t) = Kx₀(1 - exp(-t/τ))`.

Прибор с большой `τ` не видит быстрый transient, даже если static calibration идеальна.
Второй порядок нужен для resonance, overshoot и damping.

### 4.3 Sensor families

Нужно знать принцип и типичные failure modes:

- temperature: thermocouple, RTD, thermistor, radiation;
- pressure: strain/piezoresistive/capacitive;
- force/torque: strain gauge, piezoelectric;
- displacement/speed: encoder, Hall, inductive, optical;
- flow: differential pressure, turbine, Coriolis, magnetic, ultrasonic;
- composition: spectroscopy, chromatography, electrochemical cells;
- vibration/acoustics: accelerometer, microphone, hydrophone;
- radiation: ionization, scintillation, semiconductor detector.

Не выбирать sensor только по названию измеряемой величины: важны bandwidth, range,
material compatibility, response time и installation disturbance.

### 4.4 Instrument architecture

`physical quantity → transducer → analog conditioning → anti-alias filter → ADC →
calibration/correction → estimate → alarm/control → archive`.

На каждом переходе фиксировать unit, timestamp, uncertainty, missing-data semantics и
quality flag.

---

<a id="measure-5"></a>
## 5. Sampling и signal processing

### 5.1 Sampling

Для идеально band-limited signal теорема Nyquist требует:

`f_s > 2 f_max`.

В реальной системе нужны transition band, anti-alias filter и запас. Более высокая
sampling rate сама по себе не удаляет alias, возникший до ADC.

Quantization step для `N` bits и full-scale range `V_FS`:

`Δ = V_FS / 2ᴺ`.

При идеализированном равномерном quantization error:

`u_q = Δ / √12`.

Эта формула неверна при overload, deterministic locking и poorly dithered small signals.

### 5.2 Time и frequency domain

Изучить:

- mean, RMS, variance, autocorrelation;
- impulse/step response;
- convolution;
- Fourier transform и FFT;
- amplitude/phase spectrum;
- power spectral density;
- cross-spectrum/coherence;
- leakage и window functions;
- transient против stationary signal;
- time-frequency analysis.

Sound/vibration level и peak amplitude нельзя взаимозаменять.

### 5.3 Filters

Различать:

- analog/digital;
- FIR/IIR;
- low/high/band-pass/notch;
- causal/non-causal;
- phase delay/group delay;
- online filtering против offline smoothing.

Фильтр может скрыть опасный transient или создать phase lag в control loop.

### 5.4 Noise и SNR

Power ratio:

`SNR_dB = 10 log₁₀(P_signal / P_noise)`.

Для amplitude ratio при одинаковом impedance используется `20 log₁₀`. Всегда указывать
reference, bandwidth и weighting.

### 5.5 State estimation

Для linear discrete model:

`xₖ₊₁ = A xₖ + B uₖ + wₖ`,

`zₖ = H xₖ + vₖ`.

Kalman filter объединяет prediction и measurement через covariance assumptions. Он не
«исправляет любой шум»: нужны observability, reasonable model и проверка innovations.

Источники:

- Oppenheim & Schafer, *Discrete-Time Signal Processing*;
- Lyons, *Understanding Digital Signal Processing*;
- Bendat & Piersol, *Random Data*;
- Welch, Bishop, *An Introduction to the Kalman Filter*;
- Grewal & Andrews, *Kalman Filtering*.

---

<a id="measure-6"></a>
## 6. Experiment design и system identification

### 6.1 Планирование эксперимента

До сбора данных определить:

- question/hypothesis;
- response и factors;
- nuisance variables;
- randomization;
- blocking;
- replication;
- sample size/power;
- calibration/checks;
- exclusion rule до просмотра результата;
- data/version plan.

Повторное измерение одного неизменённого specimen оценивает repeatability, но не заменяет
независимые replicates процесса.

### 6.2 Regression

После fit проверять:

- residual pattern;
- heteroscedasticity;
- autocorrelation;
- leverage/influential points;
- parameter correlation;
- physical sign/units;
- out-of-sample prediction;
- extrapolation distance.

Высокий `R²` не доказывает causal mechanism и не гарантирует точность вне data range.

### 6.3 Identification

Различать:

- white/grey/black-box model;
- structural и practical identifiability;
- input excitation;
- parameter estimation;
- validation dataset;
- overfitting;
- time-varying system;
- closed-loop identification.

Для ротора input sweep должен разделять RPM, geometry, ambient density и interaction,
а не подгонять один coefficient по единственной точке.

Книги:

- Montgomery, *Design and Analysis of Experiments*;
- Box, Hunter & Hunter, *Statistics for Experimenters*;
- Ljung, *System Identification*;
- Coleman & Steele, *Experimentation, Validation, and Uncertainty Analysis for Engineers*.

---

<a id="measure-7"></a>
## 7. Numerical credibility и software engineering

### 7.1 Четыре разных вопроса

1. **Code verification:** правильно ли реализованы equations?
2. **Solution verification:** насколько discretization/iteration влияют на этот result?
3. **Validation:** насколько model соответствует реальности для intended use?
4. **Uncertainty quantification:** как input/model/numerical uncertainty влияют на output?

Validation не может доказать универсальную истинность модели.

### 7.2 Обязательные numerical tests

- unit/dimension tests;
- analytic solution;
- method of manufactured solutions;
- conservation residual;
- symmetry/invariance;
- limiting cases;
- timestep/grid refinement;
- independent implementation;
- regression test;
- adversarial/boundary inputs;
- deterministic replay;
- performance budget.

### 7.3 Convergence

При characteristic grid/time scale `h` и expected order `p`:

`E(h) ≈ C hᵖ`.

Для трёх систематически refined solutions оценивать observed order и asymptotic range.
Одно совпадение с benchmark может быть cancellation of errors.

### 7.4 Floating-point

Нужно знать:

- representation/rounding;
- machine epsilon;
- overflow/underflow;
- catastrophic cancellation;
- conditioning против algorithmic stability;
- summation order;
- deterministic parallel reduction;
- unit-aware tolerances.

Проверка `a == b` для computed physical values обычно заменяется documented absolute +
relative tolerance, но tolerance нельзя выбирать только чтобы test стал зелёным.

### 7.5 Reproducibility

Хранить:

- code commit;
- input data checksum/version/license;
- solver and dependency versions;
- configuration;
- random seed;
- hardware-sensitive settings;
- output schema;
- provenance graph;
- generated report.

### 7.6 Software architecture физики

Разделять:

- immutable source data;
- parsed/validated data;
- state;
- solver;
- integration adapter;
- rendering/UI;
- persistence/migration;
- tests/benchmarks.

Не смешивать visual RPM, kinetic network RPM и physical angular speed без явного
conversion contract.

Источники:

- Higham, *Accuracy and Stability of Numerical Algorithms*;
- Oberkampf & Roy, *Verification and Validation in Scientific Computing*;
- Roache, *Verification and Validation in Computational Science and Engineering*;
- NASA Models and Simulations standards:
  https://standards.nasa.gov/systems-and-subsystem-test
- NASA-HDBK-7009 series — implementation guidance;
- ASME V&V standards — выбирать документ по application domain.

---

<a id="measure-8"></a>
## 8. Uncertainty и sensitivity

### 8.1 Классы uncertainty

- aleatory variability;
- epistemic uncertainty;
- parameter uncertainty;
- initial/boundary condition uncertainty;
- measurement uncertainty;
- numerical uncertainty;
- model-form discrepancy;
- scenario uncertainty.

Нельзя автоматически складывать их как независимые normal variables.

### 8.2 Propagation

Методы:

- linearized propagation;
- interval/bounds для ограниченных случаев;
- Monte Carlo;
- Latin hypercube/quasi-random sampling;
- polynomial chaos/surrogates;
- Bayesian posterior predictive analysis;
- ensemble/scenario analysis.

Monte Carlo не исправляет biased input distributions или неверную model structure.

### 8.3 Sensitivity

Различать:

- local derivatives;
- screening;
- variance-based Sobol indices;
- Morris method;
- identifiability/profile likelihood;
- value-of-information analysis.

Sensitivity ranking зависит от диапазона и joint distribution inputs.

### 8.4 Calibration и validation data

Не использовать один dataset одновременно для unrestricted calibration и честной
validation. Если данных мало, применять cross-validation/hierarchical treatment и явно
показывать leakage risk.

Книги:

- Saltelli et al., *Global Sensitivity Analysis*;
- Smith, *Uncertainty Quantification*;
- Kennedy & O’Hagan — model calibration with discrepancy, читать вместе с критикой;
- JCGM 101 — Monte Carlo propagation of distributions.

---

<a id="measure-9"></a>
## 9. Systems engineering

### 9.1 Иерархия

`stakeholder need → mission/use cases → system requirements → architecture → allocated
requirements → implementation → verification → validation`.

Каждое requirement должно быть:

- necessary;
- unambiguous;
- singular насколько возможно;
- feasible;
- measurable/verifiable;
- traceable;
- versioned.

«Сделать реалистично» — не requirement. «При одинаковой геометрии в заданном диапазоне
тяга монотонно растёт с RPM и проходит benchmark с tolerance X» — проверяемое начало.

### 9.2 Interfaces

Interface Control Document фиксирует:

- owner;
- direction;
- data/type/unit;
- coordinate frame;
- sign convention;
- timing/rate;
- valid range;
- error/missing behavior;
- version compatibility;
- safety consequence.

### 9.3 Configuration management

Version control недостаточен без:

- baselines;
- change request/rationale;
- impact analysis;
- migration path;
- compatibility matrix;
- data/configuration version;
- rollback evidence;
- release acceptance record.

### 9.4 Verification matrix

Для каждого requirement выбрать method:

- analysis;
- inspection;
- demonstration;
- test;
- similarity/heritage с обоснованием.

Скриншот красивой модели не проверяет thrust axis, save/load consistency или multiplayer.

### 9.5 Источники

- NASA, *Systems Engineering Handbook*:
  https://www.nasa.gov/reference/systems-engineering-handbook/
- NASA Systems Engineering standards: https://standards.nasa.gov/
- INCOSE, *Systems Engineering Handbook*;
- Blanchard & Fabrycky, *Systems Engineering and Analysis*;
- Leveson, *Engineering a Safer World*;
- ISO/IEC/IEEE 15288 — system life-cycle processes.

---

<a id="measure-10"></a>
## 10. Process systems engineering и control

### 10.1 Flowsheet thinking

Для whole plant нужны:

- stream table;
- degrees-of-freedom analysis;
- material/energy closure;
- tear streams;
- recycle convergence;
- purge against inert buildup;
- utilities;
- bottlenecks/capacity;
- heat integration;
- inventory;
- dynamic transitions;
- emissions/waste.

Хорошо работающие unit operations могут образовать неработоспособную сеть.

### 10.2 Degrees of freedom

`DOF = number of unknown independent variables - number of independent equations`.

- `DOF > 0`: нужны specifications/controls;
- `DOF = 0`: система математически определена;
- `DOF < 0`: specifications могут конфликтовать.

Equation count не гарантирует solvability, independence или physical admissibility.

### 10.3 Heat integration

Изучить:

- hot/cold composite curves;
- minimum approach temperature `ΔT_min`;
- energy targets;
- exchanger network trade-offs;
- fouling/controllability;
- utility levels;
- heat pump/cogeneration context.

Минимальная utility target не всегда даёт лучшую безопасную/управляемую схему.

### 10.4 Dynamics и control

Нужны:

- inventories as states;
- first-principles dynamic model;
- transfer functions/state space;
- stability;
- PID и anti-windup;
- feedforward/cascade/ratio control;
- constraints/saturation;
- multivariable interaction;
- observability/controllability;
- state estimation;
- model predictive control как позднее углубление.

PID output:

`u(t) = K_p e(t) + K_i ∫e(t)dt + K_d de/dt`.

Derivative filtering, actuator limits и sample time являются частью модели.

### 10.5 Safety layers

Разделять:

1. inherently safer design;
2. basic process control;
3. alarms/operator response;
4. independent protection/interlock/SIS;
5. physical relief/containment;
6. emergency response.

Нельзя считать один software flag независимыми двумя protection layers.

### 10.6 Hazard methods

- What-if/checklist;
- HAZID;
- HAZOP;
- FMEA/FMECA;
- fault tree;
- event tree;
- bow-tie;
- LOPA;
- quantitative risk только при достаточных данных.

Этот справочник не содержит operating setpoints, bypass instructions или recipes для
опасных процессов.

Источники:

- Seborg et al., *Process Dynamics and Control*;
- Skogestad & Postlethwaite, *Multivariable Feedback Control*;
- Biegler, Grossmann & Westerberg, *Systematic Methods of Chemical Process Design*;
- Towler & Sinnott, *Chemical Engineering Design*;
- CCPS: https://www.aiche.org/ccps
- OSHA PSM hazard resources:
  https://www.osha.gov/process-safety-management/hazards
- U.S. Chemical Safety Board incident investigations: https://www.csb.gov/
- IEC 61511 — safety instrumented systems for process industry.

---

<a id="measure-11"></a>
## 11. Fracture, fatigue и creep

### 11.1 Stress concentration

Nominal stress недостаточен около notch, hole, thread, keyway, contact и defect.

`K_t = σ_max / σ_nom`.

Для fatigue material sensitivity и effective fatigue concentration отличаются от purely
elastic `K_t`.

### 11.2 Linear elastic fracture mechanics

Mode-I stress intensity:

`K_I = Y σ √(πa)`.

Сравнение с `K_IC` допустимо при подходящих geometry, crack definition, loading и
small-scale yielding conditions. Toughness не является одной универсальной константой
для любой thickness/temperature/rate.

Energy release rate и J-integral нужны за пределами простой LEFM.

### 11.3 Fatigue

Изучить:

- S–N и strain–life approaches;
- mean stress corrections;
- low/high-cycle regimes;
- variable-amplitude loading;
- rainflow counting;
- multiaxial fatigue;
- surface/size/environment effects;
- fatigue limit как material/model-specific concept.

Miner cumulative damage:

`D = Σᵢ nᵢ / Nᵢ`.

Rule `D ≈ 1` — грубое приближение: оно игнорирует sequence и interaction effects.

Crack growth first model:

`da/dN = C(ΔK)^m`.

Paris law действует только в своём regime и не описывает initiation, threshold и rapid
fracture целиком.

### 11.4 Creep и thermal fatigue

- primary/secondary/tertiary creep;
- stress rupture;
- creep-fatigue interaction;
- oxidation/environment;
- thermal gradients;
- ratcheting;
- relaxation;
- time–temperature parameters и их limits.

### 11.5 Игровая модель damage

Хранить:

- material/heat treatment;
- geometry factor;
- temperature/environment;
- load-cycle histogram;
- inspection state;
- crack/damage proxy;
- uncertainty;
- maintenance/repair history.

Один scalar durability допустим только как documented reduced-order model.

Источники:

- Anderson, *Fracture Mechanics*;
- Dowling, *Mechanical Behavior of Materials*;
- Suresh, *Fatigue of Materials*;
- Stephens et al., *Metal Fatigue in Engineering*;
- ASTM E399/E1820/E647/E466 families — проверять current scope/edition;
- ASM Handbooks, *Fatigue and Fracture*.

---

<a id="measure-12"></a>
## 12. Reliability, maintenance и diagnostics

### 12.1 Probability model

Reliability:

`R(t) = P(T > t)`.

Для hazard rate `h(t)`:

`R(t) = exp(-∫₀ᵗ h(τ)dτ)`.

Exponential model означает constant hazard и memoryless behavior; применять его ко всем
компонентам нельзя.

Weibull:

`R(t) = exp[-(t/η)^β]`.

`β < 1`, `β ≈ 1`, `β > 1` могут описывать разные shapes hazard, но physical mechanism
всё равно нужно обосновать.

### 12.2 System reliability

Для независимых элементов:

- series: `R_s = Π R_i`;
- active parallel: `R_p = 1 - Π(1 - R_i)`.

Independence часто нарушается общими environment, design error, power, maintenance и
software — это common-cause failure.

### 12.3 Reliability methods

- reliability block diagram;
- fault/event trees;
- FMEA/FMECA;
- Markov/state models;
- physics of failure;
- accelerated testing;
- Bayesian updating;
- availability/maintainability;
- spare/logistics model;
- reliability growth;
- prognostics/remaining useful life.

### 12.4 Condition monitoring

Signals:

- vibration/order tracking;
- temperature;
- oil/debris analysis;
- electrical signature;
- acoustic emission;
- performance residual;
- leak/pressure decay;
- visual/NDT findings.

Anomaly score не равен diagnosis. Нужны false-alarm/missed-detection rates и known
failure labels.

### 12.5 Maintenance

Различать:

- corrective;
- scheduled preventive;
- condition-based;
- predictive/prognostic;
- inspection-based;
- run-to-failure for justified noncritical items.

Оптимум зависит от consequence, detectability, downtime, spare и uncertainty.

Книги:

- Modarres, Kaminskiy & Krivtsov, *Reliability Engineering and Risk Analysis*;
- O’Connor & Kleyner, *Practical Reliability Engineering*;
- Rausand & Høyland, *System Reliability Theory*;
- Jardine & Tsang, *Maintenance, Replacement, and Reliability*;
- IEC 60812 — FMEA/FMECA;
- ISO 31000 — risk management principles.

---

<a id="measure-13"></a>
## 13. Manufacturing, tolerances и quality

### 13.1 Process–structure–property chain

`feedstock → manufacturing history → geometry/microstructure/residual stress → property
variation → service behavior`.

Одинаковый nominal material после casting, forging, machining, welding и additive route
не обязан иметь одинаковые свойства.

### 13.2 Обязательные процессы

- casting и solidification;
- bulk/sheet forming;
- machining и grinding;
- welding/brazing/soldering;
- adhesive/mechanical joining;
- powder processing/sintering;
- polymer/ceramic shaping;
- additive manufacturing;
- heat treatment;
- coating/surface treatment;
- cleaning и contamination control.

Для каждого: capability, defects, anisotropy, residual stress, surface, inspection,
repairability и waste.

### 13.3 Tolerances

Нужны:

- size/form/orientation/location/runout;
- datums;
- fits/clearances/interference;
- worst-case stack-up;
- statistical stack-up;
- thermal expansion;
- surface roughness/waviness;
- balance и concentricity для rotating parts.

Модель ротора не должна предполагать идеально совпадающие оси при любом assembly.

### 13.4 Process capability

При stable approximately normal process:

`C_p = (USL - LSL) / (6σ)`,

`C_pk = min[(USL - μ)/(3σ), (μ - LSL)/(3σ)]`.

Эти indices бессмысленны без process stability, adequate measurement system и проверки
distribution/time dependence.

### 13.5 Nondestructive testing

- visual;
- liquid penetrant;
- magnetic particle;
- ultrasonic;
- radiographic;
- eddy current;
- leak testing;
- thermography;
- acoustic emission.

У каждого method есть detectable defect class, orientation, depth, resolution,
false-call rate, calibration block и qualification requirement.

Источники:

- Groover, *Fundamentals of Modern Manufacturing*;
- Kalpakjian & Schmid, *Manufacturing Engineering and Technology*;
- Shigley, *Mechanical Engineering Design*;
- ASM Handbook series;
- ASME Y14.5 / ISO GPS family — geometric tolerancing;
- ASNT resources: https://www.asnt.org/
- NIST Smart Manufacturing: https://www.nist.gov/topics/smart-manufacturing

---

<a id="measure-14"></a>
## 14. Human factors и environmental physiology

### 14.1 Почему это engineering input

Operator error часто является следствием design:

- hidden state;
- mode confusion;
- ambiguous control;
- alarm flood;
- poor mapping;
- unreadable scale;
- delayed feedback;
- maintenance inaccessibility;
- procedure/interface mismatch;
- fatigue/workload.

### 14.2 UI и alarms

Проверять:

- priority и consequence;
- rate limits/shelving policy;
- acknowledgement против correction;
- latching;
- color + shape + text, а не только color;
- trend/context;
- stale/bad sensor indication;
- units/reference;
- accessible controls;
- recovery from error.

### 14.3 Physiology boundaries

Для aviation/submarine/industry нужны conceptual models:

- oxygen deficiency/hypoxia;
- pressure change/barotrauma/decompression risk;
- acceleration and motion sickness;
- heat/cold strain;
- noise/vibration;
- toxic exposure routes;
- visibility/lighting;
- fatigue/circadian disruption.

Не моделировать medical treatment и не давать diving/flight procedures. Использовать
регуляторные/medical sources для limits и явно отделять game abstraction.

Источники:

- Wickens et al., *Engineering Psychology and Human Performance*;
- Salvendy, *Handbook of Human Factors and Ergonomics*;
- FAA Human Factors: https://www.faa.gov/about/initiatives/maintenance_hf
- NIOSH aviation safety: https://www.cdc.gov/niosh/aviation/
- NIOSH aircrew noise:
  https://www.cdc.gov/niosh/aviation/prevention/aircrew-noise.html
- ISO 11064 — control-centre ergonomics;
- IEC 62682 / ISA-18.2 — alarm management context.

---

<a id="measure-15"></a>
## 15. Operations research, economics и lifecycle decisions

### 15.1 Optimization

Задача должна содержать:

- decision variables;
- objective(s);
- constraints;
- uncertainty/scenarios;
- integer/discrete choices;
- time horizon;
- infeasibility handling;
- sensitivity/shadow prices;
- robustness.

Максимизация production без constraints на energy, safety, reliability и emissions — не
engineering optimization.

### 15.2 Planning/scheduling

- network flow;
- assignment;
- inventory;
- batch/continuous scheduling;
- maintenance windows;
- spare parts;
- queuing;
- supply disruption;
- multiobjective/Pareto analysis.

### 15.3 Time value

Net present value:

`NPV = -C₀ + Σₜ CFₜ/(1+r)ᵗ`.

Показывать discount rate, real/nominal basis, inflation, project life, replacement,
salvage и uncertainty. NPV не оценивает автоматически safety, equity или irreversible
environmental damage.

### 15.4 Cost classes

- capital;
- installation/integration;
- utilities;
- consumables/catalyst;
- labor/maintenance;
- downtime;
- quality loss;
- waste/emissions;
- decommissioning;
- risk/contingency.

Книги:

- Hillier & Lieberman, *Introduction to Operations Research*;
- Winston, *Operations Research*;
- Turton et al., *Analysis, Synthesis, and Design of Chemical Processes*;
- Blank & Tarquin, *Engineering Economy*;
- ISO 14040/14044 — LCA framework, подробнее в Earth/environment guide.

---

<a id="measure-16"></a>
## 16. Общая модель данных

Минимальный scientific result:

```text
Result
  quantity_kind
  value
  unit
  reference_conditions
  coordinate_frame
  timestamp_or_state
  standard_uncertainty
  coverage_information
  quality_flags
  method_id
  source_id
  valid_range
  model_version
```

Минимальный model passport:

```text
ModelPassport
  purpose_and_decision
  owner
  equations
  assumptions
  inputs_outputs_units
  parameter_sources
  calibration_data
  validation_data
  numerical_method
  uncertainty_model
  benchmarks
  failure_modes
  interfaces
  safety_boundary
  version_and_changes
```

Не хранить uncertainty только строкой в документации: schema должна позволять её
передать, проверить или честно отметить unknown.

---

<a id="measure-16a"></a>
## 16A. Сильная русскоязычная полка

Обозначения: **[М]** — подтверждённое международное влияние/переводы; **[В]** — сильный
вузовский или отраслевой источник; **[И]** — исторический, современные нормы и data
обязательно сверять.

- **П. В. Новицкий, И. А. Зограф, «Оценка погрешностей результатов измерений»** —
  классическая школа measurement-error analysis; терминологию приводить к VIM/GUM.
  **[В, И]**
- **В. А. Грановский, Т. Н. Сирая, «Методы обработки экспериментальных данных при
  измерениях»** — статистическая обработка и measurement models. **[В]**
- **П. П. Орнатский, «Теоретические основы информационно-измерительной техники»** —
  signals, преобразования и instrument chains; hardware examples обновлять. **[В, И]**
- **В. А. Бесекерский, Е. П. Попов, «Теория систем автоматического регулирования»** —
  классический курс feedback/control. **[В, И]**
- **Е. С. Вентцель, «Исследование операций: задачи, принципы, методология»** —
  русскоязычный вход в optimization и decision models. **[В]**
- **Б. В. Гнеденко, Ю. К. Беляев, А. Д. Соловьёв, «Математические методы в теории
  надёжности»** — фундаментальная probabilistic reliability school, издававшаяся за
  рубежом. **[М]**
- **В. В. Болотин, «Прогнозирование ресурса машин и конструкций»** — stochastic
  strength, fatigue и lifetime. **[М, В]**
- **Г. П. Черепанов, «Механика хрупкого разрушения»** — отечественная fracture-mechanics
  школа, работа известна в международной литературе. **[М]**
- **В. П. Когаев, Н. А. Махутов, А. П. Гусенков, «Расчёты деталей машин и конструкций
  на прочность и долговечность»** — fatigue и engineering life calculations. **[В]**
- **И. А. Биргер, Б. Ф. Шорр, Г. Б. Иосилевич, «Расчёт на прочность деталей машин»** —
  связка stress concentration, fatigue и machine elements. **[В]**
- **Б. М. Базров, «Основы технологии машиностроения»** — manufacturing route,
  dimensional chains и process accuracy. **[В]**

Русские книги не заменяют актуальные BIPM/JCGM, ISO/IEC, NASA, ASTM/ASME и regulator
sources; они дают сильную теоретическую и терминологическую опору.

---

<a id="measure-17"></a>
## 17. Практические проекты

### Проект A — виртуальный стенд ротора

- encoder RPM с quantization/jitter;
- force sensor first-order response;
- zero/tare и calibration;
- ambient density;
- anti-alias sampling;
- uncertainty budget thrust coefficient;
- fit + held-out validation;
- abnormal sensor flags.

### Проект B — химический quality loop

- representative sample;
- composition measurement;
- calibration curve;
- blank/reference;
- uncertainty and detection limit;
- product decision rule;
- feedback to blending;
- audit trail.

### Проект C — fatigue/reliability

- RPM/load history;
- cycle counting;
- S–N or crack-growth reduced model;
- inspection probability;
- maintenance decision;
- common-cause scenario;
- uncertainty band.

### Проект D — plantwide dynamic model

- two unit operations + recycle;
- inventory states;
- sensor dynamics;
- actuator saturation;
- PID/interlock separation;
- utility loss and feed variation;
- safe-state abstraction;
- mass/energy residual report.

### Проект E — V&V dossier

Для одной mechanic собрать requirements, references, equations, analytic benchmark,
mesh/timestep study, experimental comparison, uncertainty, hazards и acceptance report.

---

<a id="measure-18"></a>
## 18. Критерии завершения

Область считается реально добавленной в проект, когда:

- SI/VIM terminology соблюдается;
- measurement result содержит uncertainty;
- sensor не читает hidden exact state без declared abstraction;
- numerical result прошёл convergence/benchmark;
- calibration и validation data разделены;
- model limitations видимы;
- requirement связан с test;
- interfaces содержат units/frames/timing;
- failure/common-cause учтены;
- manufacturing variation не равна нулю без причины;
- alarm не выдаётся за independent protection;
- trade study учитывает lifecycle;
- все внешние данные имеют provenance/license/version.

Главный принцип:

> **Измерить → оценить uncertainty → построить модель → проверить код и решение →
> сравнить с независимыми данными → принять решение с учётом отказов и последствий.**

---

<!-- SOURCES:START -->
<a id="measure-sources"></a>
## Сводный список источников

Этот раздел намеренно дублирует источники, приведённые рядом с темами. Список собран в одном месте для последовательного чтения и аудита ссылок.

### Книги, отчёты и стандарты

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

### Онлайн-курсы, базы данных и официальные страницы

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
<!-- SOURCES:END -->
