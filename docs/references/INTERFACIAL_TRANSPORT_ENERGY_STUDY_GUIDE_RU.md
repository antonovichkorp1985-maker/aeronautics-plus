# Поверхности, сложные потоки, трибология и энергетическая физика

Статус: углублённый физико-химический маршрут по интерфейсам, коллоидам, rheology,
multiphase/granular/porous media, tribology, combustion, acoustics, plasma, vacuum и
cryogenics. Дата аудита источников: **1 октября 2026 года**.

Связанные документы:

- [математическое ядро](MATHEMATICS_STUDY_GUIDE_RU.md);
- [инженерная физика](REALISM_STUDY_GUIDE_RU.md);
- [химия и материалы](CHEMISTRY_MOD_STUDY_GUIDE_RU.md);
- [метрология, системы и надёжность](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md);
- [карта наук](SCIENCE_COVERAGE_MAP_RU.md).

> Разделы combustion, plasma, pressure/vacuum и cryogenics предназначены для научного
> понимания и безопасной игровой модели. Здесь нет рецептур горючих/взрывчатых смесей,
> operating setpoints, схем обхода защит или инструкций для опасного оборудования.

---

## 0. Почему эти области связаны

Поверхность catalyst, oil film подшипника, пузырь в колонне, flame front и plasma sheath
различны, но для них повторяются одни и те же вопросы:

1. какие phases и interfaces существуют;
2. какие forces/fluxes действуют;
3. какой характерный scale/time;
4. что находится в equilibrium, а что transport-limited;
5. где continuum approximation перестаёт работать;
6. как измерены properties;
7. какие dimensionless groups определяют regime;
8. какие instability/failure возможны.

---

## 1. Результат и порядок обучения

После маршрута разработчик должен уметь:

- вычислять wetting/capillary pressure;
- различать adsorption, absorption и reaction;
- объяснять stability эмульсии/суспензии;
- выбирать rheological constitutive law;
- оценивать particle/bubble regime;
- моделировать porous flow и filtration;
- связывать friction/wear с lubrication regime;
- отличать combustion equilibrium от finite-rate flame;
- считать acoustic frequency/level и propagation limits;
- отличать hot gas от plasma;
- выбирать vacuum flow regime;
- оценивать cryogenic heat leak/boil-off;
- документировать hazards и valid range.

Порядок:

| Этап | Тема | Prerequisites |
|---|---|---|
| 1 | Interfaces, wetting, capillarity | thermo + calculus |
| 2 | Adsorption/colloids/surfactants | physical chemistry |
| 3 | Rheology/viscoelasticity | continuum mechanics |
| 4 | Multiphase/granular/porous flow | fluid mechanics |
| 5 | Tribology | mechanics + materials + rheology |
| 6 | Combustion/reactive flow | thermo + kinetics + transport |
| 7 | Acoustics | waves + fluids/structures |
| 8 | Plasma/high-temperature gas | EM + kinetic/statistical physics |
| 9 | Vacuum/cryogenics | thermo + molecular transport |
| 10 | Coupled validation | metrology + numerical V&V |

---

## 2. Surface thermodynamics и wetting

### 2.1 Interface quantities

Нужны:

- surface/interfacial tension `γ`;
- surface free energy;
- surface excess;
- work of adhesion/cohesion;
- contact angle;
- roughness/heterogeneity;
- dynamic contact angle;
- line pinning/hysteresis;
- temperature/composition dependence.

Surface tension — force per length и одновременно reversible work per area только при
подходящей thermodynamic formulation.

### 2.2 Young equation

Для ideal smooth chemically homogeneous solid:

`γ_SV = γ_SL + γ_LV cos θ`.

Measured contact angle зависит от preparation, contamination, roughness, advancing/
receding protocol. Одно число `θ` не всегда является equilibrium material constant.

### 2.3 Young–Laplace pressure

`Δp = γ(1/R₁ + 1/R₂)`.

Для spherical interface:

`Δp = 2γ/R`.

Sign зависит от chosen normal/pressure convention. Curvature создаёт pressure difference,
поэтому маленькие bubbles/drops ведут себя иначе крупных.

### 2.4 Capillary rise

Для ideal cylindrical tube:

`h = 2γ cos θ / (ρ g r)`.

Limits: static equilibrium, simple geometry, negligible inertia/evaporation, known angle.
В porous material заменять всё одним fictitious radius можно только как calibrated model.

### 2.5 Rough wetting

Изучить модели Wenzel и Cassie–Baxter как limiting idealizations, а также:

- contact-angle hysteresis;
- pinning;
- impregnation transitions;
- dynamic wetting;
- Marangoni stresses;
- evaporation/contact-line effects.

Не определять «гидрофобность» только по названию материала.

---

## 3. Adsorption, surfaces и heterogeneous catalysis

### 3.1 Adsorption

Различать:

- physisorption/chemisorption;
- monolayer/multilayer;
- adsorption/desorption;
- coverage;
- site heterogeneity;
- competitive adsorption;
- pore diffusion;
- irreversible poisoning.

Langmuir first model:

`θ = Kp / (1 + Kp)`

или concentration form при соответствующих assumptions: equivalent independent sites,
monolayer, no lateral interaction. Это не универсальная isotherm.

Другие модели:

- Henry low-coverage;
- Freundlich empirical;
- BET для определённых multilayer surface-area measurements;
- Temkin/competitive models;
- pore-size models для porous solids.

### 3.2 Surface reaction

Общий observable rate может ограничиваться:

- external mass transfer;
- pore diffusion;
- adsorption;
- surface reaction;
- desorption;
- heat removal.

Apparent activation energy и reaction order могут не совпадать с elementary step.

### 3.3 Catalyst state

Хранить:

- active area/site density proxy;
- oxidation/state/phase;
- temperature history;
- poison loading;
- coke/fouling;
- pore blockage/sintering;
- regeneration cycles;
- selectivity uncertainty.

---

## 4. Colloid science

### 4.1 Dispersed systems

- suspension: solid in liquid;
- emulsion: liquid in liquid;
- foam: gas in liquid/solid;
- aerosol: liquid/solid in gas;
- sol/gel;
- micellar и lyotropic systems.

Всегда указывать continuous/dispersed phase, size distribution, volume fraction и
interface chemistry.

### 4.2 Brownian motion и transport

Stokes–Einstein first model:

`D = k_B T / (6πμa)`.

Limits: isolated spherical particle, continuum fluid, no-slip, low Reynolds, dilute
system. At nanoscale/interface-specific conditions corrections may be required.

Particle Péclet number сравнивает advection с diffusion:

`Pe = UL/D`.

### 4.3 Electric double layer и DLVO

Нужны:

- surface charge;
- counterions/co-ions;
- ionic strength;
- Debye screening;
- zeta potential as operational electrokinetic quantity;
- van der Waals attraction;
- electrostatic repulsion;
- primary/secondary minima;
- non-DLVO steric/hydration effects.

Zeta potential не равен surface potential и сам по себе не гарантирует stability.

### 4.4 Aggregation и breakage

Модели должны учитывать:

- perikinetic/orthokinetic collision;
- attachment efficiency;
- floc strength;
- shear breakage;
- reversible/irreversible aggregation;
- ageing;
- population balance.

Mean particle size недостаточен при broad/multimodal distribution.

### 4.5 Surfactants

- amphiphilic structure;
- adsorption at interfaces;
- critical micelle concentration;
- ionic/nonionic/zwitterionic classes;
- hydrophile–lipophile balance as empirical guide;
- cloud/Krafft behavior;
- salinity/temperature effects;
- foam/emulsion stabilization;
- environmental fate.

Не применять одну CMC независимо от temperature, electrolyte и impurities.

### 4.6 Emulsions и foams

Failure modes:

- creaming/sedimentation;
- flocculation;
- coalescence;
- Ostwald ripening;
- drainage;
- film rupture;
- phase inversion.

Игровой separator должен зависеть от droplet size, density contrast, viscosity,
surfactant/interface и residence time.

Источники:

- Israelachvili, *Intermolecular and Surface Forces*;
- Adamson & Gast, *Physical Chemistry of Surfaces*;
- Hunter, *Foundations of Colloid Science*;
- Hiemenz & Rajagopalan, *Principles of Colloid and Surface Chemistry*;
- Russel, Saville & Schowalter, *Colloidal Dispersions*;
- IUPAC Gold Book: https://goldbook.iupac.org/

---

## 5. Rheology

### 5.1 Newtonian baseline

Simple shear:

`τ = μ γ̇`.

Viscosity depends on temperature, pressure и composition; «fluid viscosity» без state и
method incomplete.

### 5.2 Generalized Newtonian models

Power law:

`τ = K |γ̇|ⁿ⁻¹ γ̇`.

- `n < 1`: shear thinning;
- `n = 1`: Newtonian;
- `n > 1`: shear thickening.

`K` имеет model-dependent units. Power law не описывает zero/infinite-shear plateaus.

Bingham idealization:

`τ = τ_y sign(γ̇) + μ_p γ̇` после yield.

Herschel–Bulkley сочетает yield и power-law behavior. Yield stress может быть
measurement/time-scale dependent; report protocol.

### 5.3 Time dependence

- thixotropy;
- rheopexy;
- ageing;
- structural rebuilding;
- shear history;
- startup/transient stress.

Static curve недостаточна для startup pipeline, mixer или printable paste.

### 5.4 Viscoelasticity

Нужны:

- stress relaxation;
- creep;
- storage/loss moduli `G′`, `G″`;
- relaxation spectrum;
- normal stresses;
- extensional viscosity;
- Maxwell, Kelvin–Voigt и standard-linear-solid models;
- nonlinear polymer rheology.

Dimensionless:

- Deborah `De = relaxation time / observation time`;
- Weissenberg `Wi = relaxation time × characteristic strain rate`.

Они близки по смыслу, но не взаимозаменяемы без определения process/time scale.

### 5.5 Measurement traps

- wall slip;
- edge fracture;
- shear heating;
- sedimentation;
- evaporation;
- instrument inertia;
- sample loading history;
- geometry limits.

Источники:

- Bird, Armstrong & Hassager, *Dynamics of Polymeric Liquids*;
- Macosko, *Rheological Principles, Measurements, and Applications*;
- Barnes, Hutton & Walters, *An Introduction to Rheology*;
- Larson, *The Structure and Rheology of Complex Fluids*.

---

## 6. Multiphase flow

### 6.1 Regime before correlation

Для gas–liquid, liquid–liquid, particle–fluid сначала определить:

- phases/components;
- dispersed/continuous topology;
- volume fraction/holdup;
- size distribution;
- coalescence/breakup;
- slip velocity;
- wetting;
- compressibility;
- heat/mass transfer;
- geometry/orientation.

Одна pressure-drop correlation не покрывает все flow regimes.

### 6.2 Dimensionless groups

`Re = ρUL/μ` — inertia/viscous;

`We = ρU²L/γ` — inertia/surface tension;

`Ca = μU/γ` — viscous/surface tension;

`Bo = Δρ g L²/γ` — gravity/surface tension;

`Oh = μ/√(ρ γ L)` — viscous относительно inertia+surface tension.

Указать, velocity/length/property какой phase использованы.

### 6.3 Settling

Для isolated rigid sphere при creeping flow:

`u_t = (ρ_p - ρ_f) g d² / (18μ)`.

Stokes result нарушается при larger Re, non-spherical particles, wall effects,
concentrated suspension, non-Newtonian fluid и Brownian regime.

### 6.4 Bubbles/drops

Нужны:

- drag and terminal velocity;
- deformation;
- internal circulation;
- contamination/Marangoni immobilization;
- breakup/coalescence;
- mass transfer;
- swarm effects;
- bubble-column regimes.

### 6.5 Boiling и condensation

Различать:

- sensible heating;
- nucleate boiling;
- critical heat-flux transition;
- film boiling;
- dropwise/filmwise condensation;
- subcooled/two-phase flow;
- pressure dependence;
- noncondensable gases.

Не переносить single-phase heat-transfer coefficient в two-phase regime.

### 6.6 Modelling levels

- homogeneous equilibrium/mixture;
- drift-flux;
- separated two-fluid;
- Euler–Lagrange particle/bubble;
- Euler–Euler;
- interface-resolving VOF/level-set;
- population balance.

Выбор зависит от scale и question, а не от того, какой solver выглядит сложнее.

Книги:

- Brennen, *Fundamentals of Multiphase Flow*;
- Clift, Grace & Weber, *Bubbles, Drops, and Particles*;
- Crowe et al., *Multiphase Flows with Droplets and Particles*;
- Ishii & Hibiki, *Thermo-Fluid Dynamics of Two-Phase Flow*;
- Wallis, *One-Dimensional Two-Phase Flow*.

---

## 7. Porous media, filtration и membranes

### 7.1 Darcy law

`q = -(k/μ)(∇p - ρg)`

в выбранной sign/vector convention, где `k` — permeability, а не porosity.

Limits: continuum REV, low inertial effects, single-phase Newtonian baseline. At higher
velocity нужны non-Darcy corrections.

### 7.2 Porous descriptors

- porosity;
- tortuosity;
- permeability tensor;
- pore-size/throat distribution;
- connectivity;
- saturation;
- capillary pressure;
- relative permeability;
- wettability;
- compressibility.

Porosity alone does not determine flow.

### 7.3 Filtration

- screen/depth/cake filtration;
- clean-medium resistance;
- cake buildup/compressibility;
- pressure/flux operation;
- breakthrough;
- backwash/regeneration as model state;
- particle capture/re-entrainment;
- fouling.

### 7.4 Membranes

- solution–diffusion;
- pore flow;
- permeability/selectivity trade-off;
- concentration polarization;
- osmotic pressure;
- scaling/fouling;
- compaction;
- module hydrodynamics;
- cleaning lifetime;
- integrity testing.

Игровая membrane не должна иметь постоянный purity/yield при любом feed и fouling.

Источники:

- Bear, *Dynamics of Fluids in Porous Media*;
- Dullien, *Porous Media*;
- Crittenden et al., *MWH’s Water Treatment*;
- Baker, *Membrane Technology and Applications*;
- Mulder, *Basic Principles of Membrane Technology*.

---

## 8. Granular materials и powders

### 8.1 Почему это не liquid

Granular medium может:

- выдерживать static shear;
- образовывать arches;
- dilate/compact;
- segregate;
- exhibit avalanches;
- transmit force chains;
- jam;
- fluidize.

### 8.2 Descriptors

- particle-size/shape distribution;
- true/bulk/tapped density;
- porosity;
- moisture/cohesion;
- angle of repose;
- internal/wall friction;
- flowability indices;
- attrition;
- electrostatic tendency.

Angle of repose не является универсальной material constant.

### 8.3 Operations

- crushing/grinding;
- screening/classification;
- conveying;
- hopper discharge;
- mixing/segregation;
- compaction/tableting;
- fluidization;
- dust collection.

### 8.4 Fluidization

Minimum fluidization определяется balance pressure drop и effective particle weight, но
реальный regime зависит от size distribution, cohesion, gas properties и geometry.
Изучить fixed, bubbling, turbulent и transport regimes.

### 8.5 Simulation

- bulk empirical correlations;
- continuum granular models;
- discrete element method;
- coupled CFD–DEM;
- reduced inventory/segregation model.

DEM contact parameters требуют calibration и timestep verification.

Источники:

- Nedderman, *Statics and Kinematics of Granular Materials*;
- Rhodes, *Introduction to Particle Technology*;
- de Gennes, *Granular Matter: A Tentative View*;
- Schulze, *Powders and Bulk Solids*.

---

## 9. Tribology

### 9.1 Contact and friction

Простая Coulomb model:

`F_f ≤ μ_s N` в stick,

`F_f = μ_k N` в ideal sliding.

В реальности friction зависит от surface, speed, temperature, lubrication, load history,
third body и vibration. Один `μ` для material pair недостаточен.

### 9.2 Contact mechanics

Изучить:

- Hertz elastic contact;
- contact pressure/area;
- subsurface stress;
- plasticity;
- rough-surface asperities;
- conformal/nonconformal contact;
- rolling/sliding;
- edge loading;
- thermal contact.

Nominal area не равна real contact area.

### 9.3 Lubrication regimes

- boundary;
- mixed;
- hydrodynamic;
- elastohydrodynamic;
- hydrostatic;
- solid lubrication;
- starvation.

Stribeck curve связывает friction с группой viscosity × speed / load при заданной
geometry, но не является одной universal curve.

### 9.4 Reynolds equation

Для simple steady one-dimensional incompressible thin film одна форма:

`d/dx(h³ dp/dx) = 6 μ U dh/dx`.

Assumptions: thin film, laminar, Newtonian, negligible inertia, chosen surface velocities.
Cavitation boundary и viscosity variation принципиальны.

### 9.5 Wear

Archard first model:

`V = k W L / H`,

где `k` empirical wear coefficient, `W` load, `L` sliding distance, `H` hardness.

Не переносить `k` между regimes/material states без data.

Wear modes:

- adhesive;
- abrasive two/three-body;
- surface fatigue/pitting;
- fretting;
- erosive;
- corrosive/tribochemical;
- electrical;
- cavitation erosion.

### 9.6 Lubricants

Нужны:

- base oil и viscosity grade;
- viscosity–temperature/pressure;
- additives;
- oxidation;
- contamination/water;
- aeration/foaming;
- compatibility with seals/metals;
- filterability;
- grease bleed/channeling;
- relubrication interval.

### 9.7 Bearings/gears/seals

Model state:

- load/speed/alignment;
- film regime;
- temperature;
- contamination;
- damage/vibration;
- lubricant state;
- clearance/preload;
- leakage;
- installation error.

Это напрямую нужно микромодульному ротору: bearing не является вечным frictionless pivot.

Источники:

- Stachowiak & Batchelor, *Engineering Tribology*;
- Hamrock, Schmid & Jacobson, *Fundamentals of Fluid Film Lubrication*;
- Hutchings & Shipway, *Tribology: Friction and Wear*;
- Bhushan, *Introduction to Tribology*;
- STLE: https://www.stle.org/
- ASTM G-series wear/erosion standards — проверять method scope.

---

## 10. Combustion и reactive flows

### 10.1 Three coupled layers

1. thermochemistry/equilibrium;
2. finite-rate chemical kinetics;
3. heat/mass/momentum transport.

Adiabatic equilibrium temperature не предсказывает ignition delay, flame speed,
stability или emissions.

### 10.2 Stoichiometry

Equivalence ratio:

`φ = (F/A) / (F/A)_st`.

- `φ < 1`: lean относительно chosen oxidizer/fuel definition;
- `φ = 1`: stoichiometric;
- `φ > 1`: rich.

Для oxygenated/multicomponent fuel сначала нужен elemental balance. Не хранить fuel
quality одним φ.

### 10.3 Energy balance

Adiabatic flame temperature получают из enthalpy balance с species/phase equilibrium:

`Σ_react n_i h_i(T_in) = Σ_prod n_j h_j(T_ad)`.

Dissociation, variable heat capacity, pressure и product set имеют значение.

### 10.4 Kinetics

Elementary Arrhenius form:

`k = A Tⁿ exp(-E_a/RT)`.

Detailed mechanism требует species, reactions, thermo/transport data и validation domain.
Global one-step model может передавать heat release, но обычно не pollutants/ignition.

### 10.5 Flames

Изучить:

- premixed/diffusion;
- laminar/turbulent;
- flame speed;
- flame thickness;
- stretch/curvature;
- ignition/extinction;
- flashback/blowoff concepts;
- wall quenching;
- droplet/spray combustion;
- solid-fuel devolatilization/char;
- catalytic combustion.

### 10.6 Timescales

Damköhler number сравнивает flow/mixing time с chemical time. Конкретное определение
зависит от problem.

Turbulent combustion также требует Reynolds, Karlovitz и scale definitions. Нельзя
определить regime только словом «турбулентный».

### 10.7 Pollutants

- CO/unburned species;
- thermal/prompt/fuel NO pathways;
- SOx from fuel sulfur;
- soot nucleation/growth/oxidation;
- particulate/ash;
- metal/halogen species для специальных fuels;
- greenhouse-gas accounting.

### 10.8 Engines/furnaces

Couple:

- fuel specification;
- mixing/atomization;
- chamber residence time;
- wall heat loss;
- pressure loss;
- cooling/material limit;
- instability;
- emissions;
- control/sensor dynamics.

### 10.9 Tools/data

- Cantera: https://cantera.org/
- NASA CEA:
  https://www1.grc.nasa.gov/research-and-engineering/ceaweb/
- NIST Chemistry WebBook: https://webbook.nist.gov/chemistry/
- Burcat thermochemical data: https://burcat.technion.ac.il/
- LLNL combustion mechanisms:
  https://combustion.llnl.gov/mechanisms

Mechanism license, version, species naming и validation range хранить рядом с data.

Книги:

- Turns, *An Introduction to Combustion*;
- Law, *Combustion Physics*;
- Glassman, Yetter & Glumac, *Combustion*;
- Williams, *Combustion Theory*;
- Poinsot & Veynante, *Theoretical and Numerical Combustion*;
- Heywood, *Internal Combustion Engine Fundamentals*.

---

## 11. Fire и explosion safety boundary

### 11.1 Concepts

- ignition source;
- flammability envelope;
- minimum ignition energy as method/condition-dependent;
- deflagration/detonation distinction;
- confinement/congestion;
- dust cloud/layer;
- pressure rise;
- heat release;
- smoke/toxic products;
- ventilation;
- detection/suppression;
- escalation/domino effects.

### 11.2 Safe game abstraction

Разрешено моделировать:

- hazard class;
- ignition probability proxy;
- oxygen/fuel/temperature compatibility;
- containment and ventilation;
- detection/isolation/suppression;
- consequence zones in abstract units;
- incident learning.

Не включать real preparation recipes, optimal destructive conditions, bypass procedures
или weaponization.

### 11.3 Sources

- SFPE, *Handbook of Fire Protection Engineering*;
- Drysdale, *An Introduction to Fire Dynamics*;
- CCPS combustible/reactivity resources: https://www.aiche.org/ccps
- U.S. CSB investigations: https://www.csb.gov/
- NFPA standards — current edition/scope через официальный каталог.

---

## 12. Acoustics и vibroacoustics

### 12.1 Wave equation

Для simple homogeneous lossless medium:

`∂²p/∂t² = c² ∇²p`.

Real propagation может включать viscosity, thermal loss, dispersion, mean flow,
inhomogeneity и boundaries.

### 12.2 Levels

Sound pressure level:

`L_p = 20 log₁₀(p_rms/p_ref)`.

В воздухе commonly `p_ref = 20 µPa`; underwater acoustics использует другое reference и
часто другую distance convention. Значения dB из этих областей напрямую не сравнивать.

### 12.3 Frequency и spectra

Для rotor с `B` blades и rotation frequency `f_rot`:

`BPF = B f_rot`.

Есть harmonics, loading/thickness noise, broadband turbulence, blade–vortex interaction,
gear/bearing tones и structural paths.

### 12.4 Structural acoustics

- modal shapes/frequencies;
- damping;
- mobility/impedance;
- airborne/structure-borne paths;
- radiation efficiency;
- acoustic–structure coupling;
- isolation;
- coincidence/resonance;
- enclosure modes.

### 12.5 Aero/hydroacoustics

Нужны:

- monopole/dipole/quadrupole source ideas;
- convective effects;
- turbulence/boundary-layer noise;
- propeller/rotor loading;
- cavitation noise;
- free-surface/bottom effects underwater;
- absorption/refraction;
- array/beamforming measurement.

### 12.6 Measurement

Document:

- microphone/hydrophone calibration;
- sampling/bandwidth;
- weighting;
- window/averaging;
- background noise;
- distance/environment;
- tonal/broadband metrics;
- uncertainty.

Источники:

- Kinsler et al., *Fundamentals of Acoustics*;
- Pierce, *Acoustics*;
- Fahy & Gardonio, *Sound and Structural Vibration*;
- Goldstein, *Aeroacoustics*;
- Leishman, rotorcraft noise chapters;
- NASA aeroacoustics research: https://www.nasa.gov/aeronautics/

---

## 13. High-temperature gas и plasma

### 13.1 Не всякий hot gas — plasma

Plasma requires significant charged-particle population and collective/electromagnetic
behavior. Нужны:

- ionization/recombination;
- electrons/ions/neutrals;
- quasi-neutral bulk;
- sheaths;
- collisions;
- conductivity;
- radiation;
- nonequilibrium temperatures;
- chemical reactions;
- magnetic-field interaction.

### 13.2 Debye scale

Одна electron Debye length form:

`λ_D = √(ε₀ k_B T_e / (n_e e²))`.

Plasma approximation требует достаточного числа particles в Debye sphere и appropriate
scale/time; formula меняется для multicomponent/nonideal cases.

### 13.3 Ionization equilibrium

Saha equation применима к thermal equilibrium idealized gas. Arc, discharge и low-
temperature plasma часто nonequilibrium, поэтому Saha не заменяет kinetics/transport.

### 13.4 Plasma regimes

- glow/corona/arc;
- capacitively/inductively coupled;
- thermal/nonthermal;
- magnetized/unmagnetized;
- atmospheric/low-pressure;
- plasma–surface interaction;
- MHD flow;
- dusty plasma as specialized branch.

### 13.5 MHD

Couple:

- Navier–Stokes;
- charge/current closure;
- Maxwell approximations;
- Lorentz force `J × B`;
- Joule heating `J·E`;
- induction/diffusion;
- Hall/ambipolar effects where relevant.

### 13.6 Engineering limits

- electrode erosion;
- sheath heat flux;
- arc attachment;
- insulation breakdown;
- electromagnetic force;
- radiation;
- cooling;
- contamination;
- diagnostics uncertainty.

Sources:

- Chen, *Introduction to Plasma Physics and Controlled Fusion*;
- Lieberman & Lichtenberg, *Principles of Plasma Discharges and Materials Processing*;
- Fridman, *Plasma Chemistry*;
- Bittencourt, *Fundamentals of Plasma Physics*;
- PlasmaPy: https://www.plasmapy.org/
- NIST Atomic Spectra Database: https://physics.nist.gov/asd

---

## 14. Vacuum science

### 14.1 Regimes

Knudsen number:

`Kn = λ/L`.

Он помогает отличать continuum, slip, transitional и free-molecular behavior. Exact
boundaries application-dependent.

Pressure alone не определяет regime без gas, temperature и characteristic length.

### 14.2 Gas load

Sources:

- initial gas removal;
- leaks;
- permeation;
- desorption/outgassing;
- vapor pressure;
- process gas;
- virtual leaks/trapped volumes.

### 14.3 Throughput и pumping

In simple steady units:

`Q = pS`.

Effective speed with line conductance:

`1/S_eff = 1/S_pump + 1/C`.

Большой pump через узкий conductance path не обеспечивает nominal speed at chamber.

### 14.4 Pump/gauge selection

Различать operating principles и ranges:

- positive displacement;
- momentum transfer;
- capture pumps;
- rough/high/ultrahigh vacuum gauges;
- total vs partial pressure;
- gas-dependent sensitivity;
- contamination/backstreaming;
- calibration/location effects.

### 14.5 Materials

- outgassing;
- vapor pressure;
- permeation;
- cleaning/bake compatibility;
- seals;
- lubricants;
- virtual volumes;
- particle contamination;
- thermal radiation/contact.

### 14.6 Game model

State:

- chamber volume;
- gas species/inventory;
- wall load/outgassing;
- conductance network;
- pump curve/state;
- leak/permeation;
- gauge range/uncertainty;
- contamination;
- temperature.

Источники:

- O’Hanlon, *A User’s Guide to Vacuum Technology*;
- Roth, *Vacuum Technology*;
- Jousten, *Handbook of Vacuum Technology*;
- NIST vacuum metrology: https://www.nist.gov/pml/sensor-science/fluid-metrology

---

## 15. Cryogenics

### 15.1 Thermodynamic foundation

- real-gas properties;
- saturation/two-phase states;
- Joule–Thomson effect;
- isentropic expansion;
- recuperative heat exchange;
- liquefaction/refrigeration cycles;
- exergy;
- ortho/para and special-fluid effects where relevant.

### 15.2 Heat leaks

Components:

- solid conduction;
- gas conduction/convection;
- thermal radiation;
- penetrations/supports;
- piping;
- instrumentation wires;
- imperfect insulation/vacuum;
- transient cooldown.

First boil-off estimate:

`ṁ_boil ≈ Q̇_leak / h_fg`.

Only valid when heat primarily drives phase change at defined pressure and sensible/
subcooling effects are negligible.

### 15.3 Insulation

- vacuum jacket;
- multilayer insulation;
- foam/powder;
- radiation shields;
- intercepts;
- support optimization;
- moisture/permeation;
- degradation.

MLI performance strongly depends on vacuum, layer density, seams and penetrations; one
catalogue number is not universal.

### 15.4 Materials and components

- low-temperature strength/toughness;
- embrittlement;
- differential contraction;
- seal behavior;
- lubricant limits;
- thermal contact;
- superconducting transitions;
- sensor calibration;
- relief/boil-off architecture.

### 15.5 Hazards

- oxygen-deficiency atmosphere;
- oxygen enrichment/condensation;
- frostbite/cold contact;
- trapped-liquid expansion;
- pressure rise on warming;
- material incompatibility;
- visibility/ventilation;
- brittle failure.

No real operating instructions are included.

Источники:

- Barron, *Cryogenic Systems*;
- Flynn, *Cryogenic Engineering*;
- Weisend, *Handbook of Cryogenic Engineering*;
- NIST Chemistry WebBook: https://webbook.nist.gov/chemistry/
- NIST REFPROP: https://www.nist.gov/srd/refprop
- NIST Cryogenic Materials Properties Reference List:
  https://www.nist.gov/mml/acmd/cryogenic-materials-properties-reference-list

---

## 16. Coupled reduced-order architecture

### 16.1 Common state objects

```text
InterfaceState
  phases
  area_or_size_distribution
  surface_composition
  tension_or_surface_energy
  wetting_state
  adsorption_coverage
  contamination
  temperature
  uncertainty

ComplexFlowState
  phase_fractions
  rheology_model_and_parameters
  particle_drop_bubble_distribution
  flow_regime
  pressure_temperature
  fouling_or_deposit
  correlations_and_valid_ranges

ContactState
  bodies_materials_surfaces
  normal_tangential_load
  speed_and_slip
  lubrication_regime
  lubricant_condition
  temperature
  wear_damage
  alignment_clearance

EnergyProcessState
  species_and_phases
  thermo_model
  kinetics_model
  transport_model
  heat_release_or_heat_leak
  pressure_inventory
  emissions_hazards
  model_uncertainty
```

### 16.2 Timescale separation

Realtime tick model может использовать:

- precomputed validated property tables;
- regime map;
- reduced-order ODE;
- state machine;
- bounded interpolation;
- slower update cadence;
- offline high-fidelity calibration.

Нельзя скрывать discontinuity/regime transition только ради smooth animation.

---

## 16A. Сильная русскоязычная полка

Обозначения: **[М]** — международное влияние/зарубежные издания; **[В]** — сильный
вузовский источник; **[И]** — исторический, данные и safety-нормы обновлять.

- **Б. В. Дерягин, Н. В. Чураев, В. М. Муллер, «Поверхностные силы»** —
  disjoining pressure, thin films и colloidal interactions; есть международная версия
  *Surface Forces*. **[М]**
- **Е. Д. Щукин, А. В. Перцов, Е. А. Амелина, «Коллоидная химия»** — surfaces,
  dispersions, wetting и stability. **[В]**
- **Ю. Г. Фролов, «Курс коллоидной химии»** — системный русскоязычный университетский
  курс. **[В]**
- **К. С. Урьев, «Физико-химическая динамика дисперсных систем»** — rheology и
  processing concentrated dispersions. **[В]**
- **П. А. Ребиндер, работы по физико-химической механике дисперсных систем** —
  adsorption–strength links и отечественная surface-science школа; читать исторически и
  сверять с modern surface mechanics. **[М, И]**
- **Л. Д. Ландау, Е. М. Лифшиц, «Гидродинамика»** — continuum fluids, waves,
  instabilities и transport foundation; многочисленные зарубежные издания. **[М]**
- **С. С. Кутателадзе, «Основы теории теплообмена»** — heat transfer и boiling-school
  context; correlations сверять с current data. **[М, И]**
- **И. В. Крагельский, М. Н. Добычин, В. С. Комбалов, «Основы расчётов на трение и
  износ»** — mechanistic tribology; издавалась на английском. **[М]**
- **Д. Н. Гаркунов, «Триботехника»** — friction, wear и lubrication для машин. **[В]**
- **Я. Б. Зельдович, Г. И. Баренблатт, В. Б. Либрович, Г. М. Махвиладзе,
  «Математическая теория горения и взрыва»** — международно известная mathematical
  combustion school; использовать для theory/safety, не для operational recipes. **[М]**
- **Ю. П. Райзер, «Физика газового разряда»** — plasma/discharge physics, доступна в
  международных изданиях. **[М]**
- **Л. М. Бреховских, «Волны в слоистых средах»** — wave propagation и acoustics,
  переведена и широко цитируется. **[М]**
- **Л. Н. Розанов, «Вакуумная техника»** — сильный инженерный курс; components и нормы
  обновлять. **[В, И]**
- **А. М. Архаров и соавт., «Криогенные системы»** — thermodynamics, equipment и
  engineering context cryogenics. **[В]**

---

## 17. Практические проекты

### A. Emulsion separator

- droplet distribution;
- density/viscosity;
- surfactant state;
- settling/coalescence;
- residence time;
- purity/recovery;
- uncertainty;
- off-design feed.

### B. Bearing–rotor module

- radial/axial load;
- RPM;
- film/temperature;
- friction loss;
- imbalance/misalignment;
- wear/fatigue;
- vibration/noise;
- lubricant contamination;
- failure response.

### C. Reactive heater

- elemental/energy balance;
- equilibrium limit;
- finite-rate reduced model;
- wall heat loss;
- residence/mixing;
- emissions proxy;
- ignition/extinction state;
- independent safety layer abstraction.

### D. Vacuum cryogenic vessel

- conductance/pump curve;
- wall outgassing;
- radiation/conduction heat leak;
- liquid inventory;
- boil-off;
- gauge dynamics;
- insulation degradation;
- pressure/hazard state.

### E. Rotor acoustics

- RPM/order tracking;
- blade-passing tones;
- broadband floor;
- structure path;
- microphone distance/bandwidth;
- ambient correction;
- comparison with thrust/efficiency.

---

## 18. Acceptance criteria

- interface sign/curvature convention documented;
- phase topology and size distribution explicit;
- rheology measured in relevant rate/time range;
- dimensionless regime checked before correlation;
- pressure/heat/mass balances close;
- tribology depends on load/speed/lubricant/temperature;
- combustion separates equilibrium, kinetics and transport;
- acoustics stores reference/bandwidth/distance;
- plasma model verifies plasma criteria;
- vacuum uses conductance/gas loads, not pressure magic;
- cryogenic model closes heat and inventory balance;
- transitions and hazards are visible;
- data source/version/uncertainty accompanies every coefficient.

Главный принцип:

> **Phase + interface + transport + timescale + regime + measurement define behavior;
> название жидкости, материала или машины само по себе ничего не рассчитывает.**
