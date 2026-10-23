# Земля, ресурсы, специальные материалы и окружающая среда

<!-- TOC:START -->
## Оглавление

- [0. Уровни и результат](#earth-0)
- [1. Порядок обучения](#earth-1)
- [2. Earth system, geology и petrology](#earth-2)
- [3. Crystallography и mineralogy](#earth-3)
- [4. Geochemistry и hydrogeochemistry](#earth-4)
- [5. Geophysics, geodesy и inverse problems](#earth-5)
- [6. Sampling, resources и spatial uncertainty](#earth-6)
- [7. Mining и geotechnical engineering](#earth-7)
- [8. Mineral processing](#earth-8)
- [9. Ceramics](#earth-9)
- [10. Glass](#earth-10)
- [11. Cement, concrete и binders](#earth-11)
- [12. Refractories и high-temperature materials](#earth-12)
- [13. Condensed matter physics](#earth-13)
- [14. Electronic, dielectric и magnetic materials](#earth-14)
- [15. Optics и photonics](#earth-15)
- [16. Water chemistry](#earth-16)
- [17. Atmospheric chemistry, meteorology и dispersion](#earth-17)
- [18. Environmental fate, ecotoxicology и remediation](#earth-18)
- [19. Life-cycle assessment и circular materials](#earth-19)
- [20. Biotechnology — conditional branch](#earth-20)
- [21. Soil science и agrochemistry — conditional branch](#earth-21)
- [22. Cross-domain data model](#earth-22)
- [22A. Сильная русскоязычная полка](#earth-22a)
- [23. Практические проекты](#earth-23)
- [24. Acceptance criteria](#earth-24)
- [Углублённая университетская и исследовательская литература](#earth-deep)
- [Сводный список источников](#earth-sources)

> [Единая программа](INTEGRATED_SCIENCE_CURRICULUM_RU.md) · [Карта покрытия](SCIENCE_COVERAGE_MAP_RU.md) · [Источники этого файла](#earth-sources)
<!-- TOC:END -->


Статус: маршрут по geology/mineralogy/geochemistry, geophysics, mining/mineral
processing, ceramics/glass/cement/refractories, condensed matter, electronic materials,
optics/photonics, water/atmospheric chemistry, LCA и условным bio/agro branches.
Дата аудита источников: **1 октября 2026 года**.

Связанные документы:

- [математика](MATHEMATICS_STUDY_GUIDE_RU.md);
- [инженерная физика](REALISM_STUDY_GUIDE_RU.md);
- [химия и материаловедение](CHEMISTRY_MOD_STUDY_GUIDE_RU.md);
- [поверхности, сложные потоки и энергетическая физика](INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md);
- [метрология, системы и надёжность](MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md);
- [карта научного покрытия](SCIENCE_COVERAGE_MAP_RU.md).

Документ связывает природный ресурс с обработанным материалом и экологическим
последствием. Универсального блока «руда», одинакового yield любой шахты и вечного
материала с одной прочностью здесь быть не должно.

> Горные, высокотемпературные, электрические, токсикологические и биологические темы
> рассматриваются как science/modeling route. Здесь нет blasting recipes, инструкций по
> работе в шахте, синтезу опасных веществ, медицинских или pathogen protocols.

---

<a id="earth-0"></a>
## 0. Уровни и результат

- **Б** — определения, карты, phases, units и first-order balances;
- **С** — process/property models и measured data;
- **П** — inverse problems, coupled transport, uncertainty и lifecycle integration;
- **R** — официальный data/standard source.

После маршрута разработчик должен уметь:

- различать mineral, rock, ore, resource, reserve и product;
- читать geological/mineralogical evidence с uncertainty;
- строить grade–recovery и water/tailings balance;
- связывать ceramic/glass/cement process с microstructure/property;
- объяснять electronic/optical properties через structure;
- моделировать water treatment как speciation + transport + process;
- учитывать atmospheric fate и lifecycle boundary;
- не переносить laboratory property на real component без scale/manufacturing data;
- указывать provenance, spatial/temporal scale и detection limit environmental data.

---

<a id="earth-1"></a>
## 1. Порядок обучения

| Этап | Тема | Результат |
|---|---|---|
| 1 | Earth system, rocks/minerals | geological vocabulary + map |
| 2 | Crystallography/mineral identification | mineral property record |
| 3 | Geochemistry/hydrogeochemistry | speciation/mass balance |
| 4 | Geophysics/inverse problems | uncertain subsurface model |
| 5 | Resource estimation/sampling | grade distribution |
| 6 | Mining/geotechnics | safe conceptual extraction network |
| 7 | Mineral processing/tailings | grade–recovery–water flowsheet |
| 8 | Ceramics/glass/cement/refractories | process–structure–property model |
| 9 | Condensed/electronic materials | temperature/defect-aware properties |
| 10 | Optics/photonics | source–path–detector model |
| 11 | Water/environment/atmosphere | emissions/fate/treatment model |
| 12 | LCA/circularity | functional-unit trade study |
| 13 | Bio/agro conditional branch | bounded bioprocess/soil model |

---

<a id="earth-2"></a>
## 2. Earth system, geology и petrology

### 2.1 Не смешивать понятия

- **Mineral** — naturally occurring crystalline substance/species under mineralogical
  definitions, not any item with chemical formula.
- **Rock** — aggregate of one or more minerals/mineraloids/textures.
- **Ore** — material potentially workable for valuable component under defined technical,
  economic, legal and environmental conditions.
- **Deposit** — geological concentration with geometry/history.
- **Resource** — concentration assessed with stated confidence and assumptions.
- **Reserve** — economically mineable part under modifying factors; это не просто
  «resource с высоким grade».

Definitions of resource/reserve depend on reporting code and jurisdiction; never invent a
universal game/legal equivalence.

### 2.2 Rock cycle и tectonic setting

Изучить:

- plate tectonics;
- magmatism/crystallization;
- weathering/erosion/transport;
- sedimentation/diagenesis;
- metamorphism;
- deformation/faulting;
- uplift/burial;
- hydrothermal circulation;
- basin evolution.

Название rock без texture, mineralogy, alteration и geological setting даёт мало данных о
processing behavior.

### 2.3 Igneous petrology

- magma composition;
- partial melting/fractional crystallization;
- phase equilibria;
- cooling rate/texture;
- intrusive/extrusive setting;
- volatiles;
- differentiation;
- alteration.

Bowen reaction series — teaching framework, not complete simulator of every magma.

### 2.4 Sedimentary systems

- clastic grain size/sorting/roundness;
- chemical/biogenic sediments;
- porosity/permeability;
- compaction/cementation;
- facies;
- depositional environment;
- source–transport–sink;
- organic matter maturation as separate petroleum branch.

### 2.5 Metamorphism

- pressure–temperature–time path;
- index minerals/facies;
- deformation/fabric;
- fluid–rock interaction;
- equilibrium vs kinetic preservation;
- retrogression.

### 2.6 Structural geology

- stress/strain;
- folds/faults/joints;
- orientation data;
- stereographic projection;
- brittle/ductile regimes;
- fracture networks;
- scale dependence;
- uncertainty hidden by 2D map.

Источники:

- Tarbuck, Lutgens & Tasa, *Earth: An Introduction to Physical Geology*;
- Marshak, *Essentials of Geology* и *Earth: Portrait of a Planet*;
- Winter, *Principles of Igneous and Metamorphic Petrology*;
- Nichols, *Sedimentology and Stratigraphy*;
- Fossen, *Structural Geology*;
- USGS education/data: https://www.usgs.gov/
- National Geologic Map Database: https://ngmdb.usgs.gov/ngmdb/ngmdb_home.html
- OneGeology: https://onegeology.org/

---

<a id="earth-3"></a>
## 3. Crystallography и mineralogy

### 3.1 Crystal description

- lattice/basis;
- unit cell;
- symmetry/space group;
- Miller indices;
- polymorphism;
- solid solution;
- ordering;
- defects;
- cleavage/twinning;
- crystal habit.

Ideal formula and actual mineral composition can differ through substitution, vacancies,
water, oxidation state and zoning.

### 3.2 X-ray diffraction

Bragg law:

`nλ = 2d sin θ`.

Peak position, intensity и width depend on structure, texture, crystallite size, strain,
instrument and sample preparation. Matching one peak is not mineral identification.

### 3.3 Identification methods

- optical microscopy;
- XRD;
- Raman/IR;
- electron microscopy/microprobe;
- XRF;
- thermal analysis;
- density/magnetic properties;
- hardness/cleavage/streak as field observations.

Mohs hardness is ordinal scratch resistance, not linear strength or universal wear index.

### 3.4 Mineral property record

```text
MineralPhase
  accepted_name_and_identifier
  ideal_and_measured_composition
  crystal_structure
  oxidation_state
  density
  optical_magnetic_electrical_properties
  hardness_cleavage_fracture
  grain_size_shape_liberation
  alteration_weathering
  methods_and_uncertainty
  sources
```

### 3.5 Data

- RRUFF mineral spectra/XRD/chemistry: https://www.rruff.net/
- Crystallography Open Database: https://www.crystallography.net/cod/
- Mindat can be discovery/context, but critical values should return to primary literature;
- IMA mineral nomenclature/classification: https://www.ima-mineralogy.org/

Книги:

- Klein & Dutrow, *Manual of Mineral Science*;
- Nesse, *Introduction to Mineralogy*;
- Putnis, *Introduction to Mineral Sciences*;
- Giacovazzo et al., *Fundamentals of Crystallography*.

---

<a id="earth-4"></a>
## 4. Geochemistry и hydrogeochemistry

### 4.1 Foundation

- elemental abundance;
- reservoirs/fluxes;
- aqueous speciation;
- activity/ionic strength;
- acid–base;
- complexation;
- redox;
- mineral dissolution/precipitation;
- adsorption/ion exchange;
- gas exchange;
- isotopes;
- kinetics/transport;
- microbial mediation.

### 4.2 Saturation

Mineral saturation index:

`SI = log₁₀(IAP / K)`.

- `SI < 0`: undersaturated under model assumptions;
- `SI = 0`: equilibrium;
- `SI > 0`: supersaturated.

Supersaturation does not guarantee immediate precipitation: nucleation/kinetics and
inhibitors matter.

### 4.3 Redox

Eh–pH diagrams show thermodynamic predominance under specified activity, temperature,
pressure and chosen species. They do not prove kinetic rate or coexistence history.

Track electrons through actual half-reactions and charge balance; do not use one generic
«oxidation level» for a water/rock system.

### 4.4 Partitioning

Distribution coefficient:

`K_d = C_solid / C_solution`

is operational and condition-dependent. pH, mineral surface, competing ions, ionic
strength, organic matter and equilibration protocol affect it.

### 4.5 Isotopes

Delta notation:

`δ = (R_sample/R_standard - 1) × 1000 ‰`.

Always state isotope ratio, standard, fractionation process and measurement uncertainty.
Age dating additionally requires closed-system and decay-model assumptions.

### 4.6 Weathering and mine drainage

Couple:

- sulfide oxidation;
- acid generation/neutralization;
- metal release/sorption/precipitation;
- oxygen/water transport;
- microbial catalysis;
- flow paths;
- seasonal variation;
- treatment residuals.

Do not predict acid mine drainage only from total sulfur.

### 4.7 Tools/data

- USGS PHREEQC: https://www.usgs.gov/software/phreeqc-version-3
- EarthChem: https://www.earthchem.org/
- USGS water data: https://waterdata.usgs.gov/
- NIST thermochemical data through existing chemistry guide.

Книги:

- White, *Geochemistry*;
- Faure, *Principles and Applications of Geochemistry*;
- Drever, *The Geochemistry of Natural Waters*;
- Appelo & Postma, *Geochemistry, Groundwater and Pollution*;
- Stumm & Morgan, *Aquatic Chemistry*.

---

<a id="earth-5"></a>
## 5. Geophysics, geodesy и inverse problems

### 5.1 Methods

- seismic reflection/refraction;
- gravity;
- magnetic;
- electrical resistivity/IP;
- electromagnetic;
- ground-penetrating radar;
- well logging;
- remote sensing;
- geodetic/GNSS/deformation measurement;
- thermal/geothermal observations.

Each measures a physical response, not a direct label «ore here».

### 5.2 Forward/inverse distinction

Forward model:

`properties + geometry + physics → predicted observations`.

Inverse problem:

`observations + assumptions/regularization → plausible properties/geometry`.

Inverse solutions may be nonunique, ill-conditioned and resolution-limited.

### 5.3 Required metadata

- coordinate reference system;
- elevation/depth datum;
- acquisition geometry;
- instrument/calibration;
- processing steps;
- noise/covariance;
- resolution/sensitivity;
- prior/regularization;
- alternative models;
- blind validation where possible.

### 5.4 Game abstraction

Survey reveals probability/resolution volume, not exact ore blocks at unlimited depth.
Better instruments improve SNR/resolution but do not remove nonuniqueness.

Книги:

- Lowrie, *Fundamentals of Geophysics*;
- Kearey, Brooks & Hill, *An Introduction to Geophysical Exploration*;
- Menke, *Geophysical Data Analysis: Discrete Inverse Theory*;
- Telford, Geldart & Sheriff, *Applied Geophysics*.

---

<a id="earth-6"></a>
## 6. Sampling, resources и spatial uncertainty

### 6.1 Representative sample

Bias can enter through:

- delimitation;
- extraction;
- preparation;
- mass reduction;
- particle-size segregation;
- moisture;
- contamination/loss;
- assay;
- spatial clustering.

Analytical precision cannot repair biased sampling.

### 6.2 Grade and tonnage

Track:

- dry/wet basis;
- valuable element/mineral basis;
- cutoff assumption;
- density model;
- block/support volume;
- compositing;
- missing/below-detection data;
- spatial correlation;
- geological domains;
- uncertainty classification.

### 6.3 Geostatistics

Study:

- variogram;
- anisotropy;
- stationarity assumptions;
- kriging estimate/variance;
- conditional simulation;
- change of support;
- cross-validation;
- declustering;
- domain boundaries.

Smooth kriged map hides local variability; simulation ensembles better preserve risk for
some decisions.

### 6.4 Resource vs reserve

Reserve needs modifying factors:

- mining;
- processing/metallurgical;
- infrastructure;
- economic/market;
- legal;
- environmental;
- social/governance;
- confidence and reporting code.

Project must not label geological inventory as guaranteed recoverable material.

Источники:

- Rossi & Deutsch, *Mineral Resource Estimation*;
- Journel & Huijbregts, *Mining Geostatistics*;
- Pitard, sampling theory texts;
- current CRIRSCO-family reporting definitions for context: https://www.crirsco.com/

---

<a id="earth-7"></a>
## 7. Mining и geotechnical engineering

### 7.1 Scope

- surface/underground method selection;
- rock mass/soil characterization;
- excavation stability;
- support;
- groundwater;
- ventilation;
- dust/gas/heat;
- haulage;
- dewatering;
- backfill;
- closure;
- subsidence;
- monitoring/emergency architecture.

No blasting design or operational instructions are included.

### 7.2 Effective stress

For saturated soil baseline:

`σ′ = σ - u`,

где `u` — pore pressure. Strength/deformation respond to effective stress under model
assumptions; unsaturated/fractured media need extensions.

Mohr–Coulomb idealization:

`τ_f = c′ + σ′ tan φ′`.

`c′`, `φ′` depend on material, drainage, stress path, scale and test; discontinuous rock
mass needs joint/orientation models.

### 7.3 Rock mass

Distinguish:

- intact specimen;
- discontinuities;
- rock mass;
- weathering;
- in-situ stress;
- water pressure;
- excavation damage;
- time dependence;
- scale.

Laboratory uniaxial strength is not mine-wide stability.

### 7.4 Ventilation/environment

Conceptual model:

- airflow network;
- pressure losses;
- heat/moisture;
- contaminants/dust;
- fan/control state;
- sensor coverage;
- power loss;
- refuge/egress as safety architecture.

### 7.5 Geotechnical sources

- Hoek & Bray, *Rock Slope Engineering*;
- Brady & Brown, *Rock Mechanics for Underground Mining*;
- Jaeger, Cook & Zimmerman, *Fundamentals of Rock Mechanics*;
- Das, *Principles of Geotechnical Engineering*;
- NIOSH Mining: https://www.cdc.gov/niosh/mining/

---

<a id="earth-8"></a>
## 8. Mineral processing

### 8.1 Flowsheet

`run-of-mine → comminution → sizing/classification → concentration → dewatering →
concentrate + tailings + recycle water`.

Each stage has recovery, selectivity, energy, water, wear, reagent, dust и uncertainty.

### 8.2 Liberation

Separation targets mineral particles, not abstract element atoms. Need:

- mineral association;
- grain size;
- liberation distribution;
- overgrinding;
- locking;
- texture;
- density/magnetic/surface contrasts.

### 8.3 Comminution

Study crushing vs grinding, size distribution, circulating load, classification and wear.

Bond equation common form:

`W = 10 W_i (1/√P₈₀ - 1/√F₈₀)`

with standardized units/test definitions. It is empirical and must not be extrapolated to
all machines, particle sizes or ores.

### 8.4 Separation methods

- screening/classification;
- gravity;
- magnetic;
- electrostatic;
- dense-medium;
- flotation;
- sensor-based sorting;
- leaching only within chemical/process safety scope;
- solid–liquid separation.

### 8.5 Grade–recovery

For feed mass `m_f`, feed grade `f`, concentrate mass `m_c`, grade `c`:

`recovery = m_c c / (m_f f)`.

Mass yield:

`Y = m_c/m_f`.

High concentrate grade often trades against recovery. Both need mass/element balance and
sampling uncertainty.

### 8.6 Flotation

Couple:

- mineral surface chemistry;
- collector/depressant/activator/frother functions at conceptual level;
- particle/bubble size;
- attachment/detachment;
- hydrodynamics;
- entrainment;
- froth drainage;
- water chemistry;
- residence time;
- recycle.

No reagent recipes or operational dosages are provided.

### 8.7 Dewatering/tailings

- thickening;
- filtration;
- moisture;
- water recycle;
- rheology;
- segregation;
- geochemistry;
- seepage;
- dust;
- long-term stability;
- closure/monitoring.

Tailings are a designed long-lived system, not disposable item deletion.

Источники:

- Wills & Finch, *Wills’ Mineral Processing Technology*;
- Fuerstenau & Han, *Principles of Mineral Processing*;
- SME, *Mineral Processing and Extractive Metallurgy Handbook*;
- Napier-Munn et al., *Mineral Comminution Circuits*;
- USGS minerals information/material flow:
  https://www.usgs.gov/programs/mineral-resources-program/science/minerals-information-and-material-flow

---

<a id="earth-9"></a>
## 9. Ceramics

### 9.1 Families

- traditional silicates;
- oxides;
- carbides;
- nitrides;
- borides;
- piezo/ferroelectric ceramics;
- ionic conductors;
- ceramic matrix composites;
- bioceramics as conditional branch.

### 9.2 Processing

`powder synthesis → classification/mixing → binder/granulation → forming → drying/
debinding → sintering/reaction → machining/coating → inspection`.

Properties depend on:

- purity;
- particle/agglomerate distribution;
- green density;
- binder burnout;
- atmosphere;
- temperature/time;
- grain growth;
- porosity;
- residual stress;
- defects.

### 9.3 Sintering

Distinguish densification from grain growth. Driving force relates to surface energy;
mechanisms include diffusion paths, viscous flow and liquid-phase processes.

One `sintering temperature` does not determine final density/microstructure.

### 9.4 Brittle behavior

- flaw population;
- fracture toughness;
- slow crack growth;
- Weibull strength statistics;
- thermal shock;
- contact damage;
- environment;
- proof testing.

Mean strength without specimen size/surface/method is unsafe.

### 9.5 Thermal shock

Depends on expansion, modulus, strength/toughness, thermal conductivity/diffusivity,
geometry, heat-transfer coefficient and transient profile. A single `thermal resistance`
stat hides mechanism.

Книги:

- Kingery, Bowen & Uhlmann, *Introduction to Ceramics*;
- Barsoum, *Fundamentals of Ceramics*;
- Carter & Norton, *Ceramic Materials*;
- Rahaman, *Ceramic Processing and Sintering*;
- ASTM C-series standards for ceramic tests — current method/scope.

---

<a id="earth-10"></a>
## 10. Glass

### 10.1 Structure and transition

Glass is an amorphous solid with history-dependent structure. Need:

- network formers/modifiers/intermediates;
- fictive temperature;
- glass transition range;
- viscosity curve;
- crystallization/devitrification;
- phase separation;
- thermal expansion;
- optical absorption;
- chemical durability.

`T_g` is method/rate-dependent, not one equilibrium melting point.

### 10.2 Processing

- batch/raw-material chemistry;
- melting/refining/homogenization;
- forming;
- annealing;
- tempering;
- coating;
- contamination;
- cullet/recycling;
- furnace/refractory compatibility.

### 10.3 Failure

Glass strength is flaw/surface/history dependent. Model scratches, moisture-assisted crack
growth, residual stress, edge condition and thermal gradients.

Книги:

- Shelby, *Introduction to Glass Science and Technology*;
- Varshneya & Mauro, *Fundamentals of Inorganic Glasses*;
- Schott technical glass data can support a specific material, not universal glass.

---

<a id="earth-11"></a>
## 11. Cement, concrete и binders

### 11.1 Chemistry

- clinker phases;
- hydration reactions;
- setting vs hardening;
- supplementary cementitious materials;
- pore solution;
- heat evolution;
- carbonation;
- sulfate/chloride reactions;
- alkali–silica reaction;
- alternative binders with separate evidence.

### 11.2 Process–property

- raw feed/kiln/clinker/grinding at high level;
- water-to-binder ratio;
- aggregate grading;
- admixtures;
- mixing/placing/curing;
- porosity/permeability;
- shrinkage/creep;
- reinforced steel corrosion;
- temperature history;
- ageing/durability.

No real construction specification is provided.

### 11.3 Sustainability

Track:

- process CO₂;
- fuel/electricity;
- transport;
- clinker substitution;
- service life;
- maintenance;
- carbonation accounting assumptions;
- demolition/reuse/recycling;
- functional performance.

Книги:

- Taylor, *Cement Chemistry*;
- Mehta & Monteiro, *Concrete: Microstructure, Properties, and Materials*;
- Hewlett & Liska, *Lea’s Chemistry of Cement and Concrete*;
- Neville, *Properties of Concrete*.

---

<a id="earth-12"></a>
## 12. Refractories и high-temperature materials

### 12.1 Selection variables

- maximum/continuous temperature;
- load under temperature;
- thermal cycling/shock;
- slag/gas/metal chemistry;
- redox/oxygen potential;
- erosion;
- permeability;
- joints/anchors;
- installation/drying;
- expansion/creep;
- contamination of product.

Melting point alone does not select a lining.

### 12.2 Degradation

- dissolution/corrosion;
- penetration;
- reaction-layer formation;
- spalling;
- creep;
- abrasion/erosion;
- carbon oxidation;
- hydration;
- thermal fatigue.

### 12.3 Model

Store composition/mineral phases, porosity, thermal properties, hot strength, chemical
compatibility, wear rate range, joints and inspection history.

Источники:

- Schacht, *Refractories Handbook*;
- Routschka & Wuthnow, *Pocket Manual of Refractory Materials*;
- phase-equilibrium and vendor data with independent validation.

---

<a id="earth-13"></a>
## 13. Condensed matter physics

### 13.1 Foundation

- bonding and symmetry;
- reciprocal lattice;
- diffraction;
- electrons in periodic potential;
- Fermi statistics;
- phonons;
- defects/dislocations;
- diffusion;
- phase transitions;
- magnetism;
- dielectric response;
- superconductivity;
- low-dimensional/nanoscale effects.

### 13.2 Structure–property links

Mechanical, thermal, electrical and optical properties depend on:

- composition;
- phase fraction;
- grain/domain size;
- texture;
- point/line/planar defects;
- interfaces;
- residual stress;
- temperature;
- frequency;
- field;
- irradiation/ageing.

### 13.3 Heat capacity/conduction

Study:

- electronic/phonon contributions;
- Debye/Einstein models;
- scattering;
- anisotropy;
- boundary/defect effects;
- ballistic/diffusive limits;
- Wiedemann–Franz context and limits.

### 13.4 Diffusion

Arrhenius first model:

`D = D₀ exp(-Q/RT)`.

Different species/pathways/phases have different `D₀`, `Q`; phase changes, defects and
non-Arrhenius regimes matter.

### 13.5 Phase transitions

- first/continuous-order ideas;
- order parameter;
- nucleation/growth;
- critical behavior;
- hysteresis/metastability;
- martensitic/displacive;
- magnetic/ferroelectric transitions.

Equilibrium phase diagram does not predict transformation rate/microstructure alone.

Книги:

- Kittel, *Introduction to Solid State Physics*;
- Ashcroft & Mermin, *Solid State Physics*;
- Callister & Rethwisch, *Materials Science and Engineering*;
- Porter, Easterling & Sherif, *Phase Transformations in Metals and Alloys*;
- NIST Materials Data Resources:
  https://www.nist.gov/mgi/materials-data-resources
- Materials Project: https://materialsproject.org/

Computed database values require method/version/convergence and experimental comparison.

---

<a id="earth-14"></a>
## 14. Electronic, dielectric и magnetic materials

### 14.1 Semiconductors

- bands/band gap;
- intrinsic/extrinsic carriers;
- donors/acceptors;
- Fermi level;
- mobility/scattering;
- generation/recombination;
- drift/diffusion;
- contacts/junctions;
- traps/interfaces;
- dielectric breakdown;
- thermal coupling;
- radiation defects.

Conductivity:

`σ = q(nμ_n + pμ_p)`.

At nondegenerate thermal equilibrium baseline:

`np = n_i²`.

These relations need temperature, statistics and material regime.

### 14.2 Junction baseline

Ideal diode model:

`I = I_s[exp(qV/(n k_B T)) - 1]`.

Real behavior includes series/shunt resistance, recombination, capacitance, breakdown,
self-heating and parameter spread.

### 14.3 Manufacturing context

- purification/zone refining;
- crystal growth;
- epitaxy/thin films;
- oxidation/deposition;
- doping;
- lithography;
- etching;
- metallization;
- packaging;
- contamination/yield;
- testing/reliability.

Keep this conceptual until a dedicated fabrication module exists.

### 14.4 Dielectrics

- polarization mechanisms;
- complex permittivity;
- loss tangent;
- frequency/temperature dependence;
- ferroelectric hysteresis;
- piezoelectric coupling;
- insulation ageing;
- partial discharge/breakdown.

### 14.5 Magnetics

- ferro/ferri/antiferromagnetism;
- domains;
- B–H loop;
- coercivity/remanence;
- soft/hard magnets;
- eddy/hysteresis losses;
- saturation;
- magnetostriction;
- temperature/Curie behavior;
- geometry/demagnetizing field.

### 14.6 Sources

- Sze & Ng, *Physics of Semiconductor Devices*;
- Pierret, *Semiconductor Device Fundamentals*;
- Streetman & Banerjee, *Solid State Electronic Devices*;
- Schroder, *Semiconductor Material and Device Characterization*;
- Cullity & Graham, *Introduction to Magnetic Materials*;
- Kasap, *Principles of Electronic Materials and Devices*;
- NIST materials repository: https://materialsdata.nist.gov/

---

<a id="earth-15"></a>
## 15. Optics и photonics

### 15.1 Geometrical optics

Snell law:

`n₁ sin θ₁ = n₂ sin θ₂`.

Study reflection/refraction, lenses/mirrors, aberrations, numerical aperture, étendue and
imaging. Ray optics fails when wave/coherence/feature scale matters.

### 15.2 Wave optics

- phase/coherence;
- interference;
- diffraction;
- polarization;
- dispersion;
- scattering;
- near/far field;
- Fourier optics;
- guided modes.

Diffraction-limited angular scale for circular aperture first estimate:

`θ ≈ 1.22 λ/D`.

This does not include atmosphere, aberration, sampling, detector noise or processing.

### 15.3 Light–matter interaction

Photon energy:

`E = hν = hc/λ`.

Beer–Lambert baseline:

`A = εbc`,

under defined wavelength, homogeneous non-scattering sample and linear regime. At high
concentration, chemical equilibria/scattering/instrument limits cause deviations.

Study absorption, spontaneous/stimulated emission, fluorescence/phosphorescence, Raman,
nonlinear optics as advanced branch.

### 15.4 Sources and detectors

- thermal/blackbody;
- LED;
- laser principles;
- lamps;
- photodiodes/APD;
- photomultiplier;
- image sensors;
- bolometer/thermal detector;
- spectrometers;
- responsivity/noise/dynamic range;
- eye safety and laser classification through current standards.

### 15.5 Fibers/integrated photonics

- total internal reflection;
- modes;
- attenuation;
- dispersion;
- coupling;
- bending loss;
- nonlinearities;
- connectors/contamination;
- temperature/radiation.

### 15.6 Radiometry vs photometry

Radiometry measures physical radiant quantities; photometry weights by human visual
response. Do not mix W, lumen, lux, irradiance, radiance and exposure.

### 15.7 Applications

- spectroscopy/chemical quality;
- encoder/navigation;
- range/remote sensing;
- thermal imaging;
- optical communication;
- solar energy;
- process monitoring;
- mineral identification.

Источники:

- Hecht, *Optics*;
- Saleh & Teich, *Fundamentals of Photonics*;
- Goodman, *Introduction to Fourier Optics*;
- Boyd, *Nonlinear Optics*;
- NIST Atomic Spectra Database: https://physics.nist.gov/asd
- NIST optical metrology: https://www.nist.gov/topics/optics-and-photonics

---

<a id="earth-16"></a>
## 16. Water chemistry

### 16.1 Water is not inert utility

Track:

- temperature;
- pH;
- alkalinity/acidity;
- hardness;
- conductivity/TDS;
- ionic composition/activity;
- dissolved gases;
- suspended/colloidal solids;
- organic matter;
- nutrients;
- microbes where relevant;
- trace contaminants;
- redox;
- saturation indices;
- uncertainty/detection limits.

### 16.2 Carbonate system

Couple dissolved CO₂/H₂CO₃/HCO₃⁻/CO₃²⁻ with pH, alkalinity, gas exchange and mineral
saturation. Alkalinity is charge-balance capacity under defined titration/model, not simply
`pH high`.

### 16.3 Hardness, scale и corrosion

- Ca/Mg and other contributors;
- temperature/concentration cycles;
- carbonate/sulfate/silica scales;
- nucleation/kinetics;
- corrosion electrochemistry;
- oxygen/chloride/pH;
- galvanic/material effects;
- deposit-under-corrosion;
- inhibitors and environmental fate.

One saturation index cannot predict all deposition/corrosion rates.

### 16.4 Treatment train

- source protection/screening;
- aeration/gas transfer;
- coagulation/flocculation;
- sedimentation/flotation;
- granular filtration;
- adsorption;
- ion exchange;
- membranes;
- oxidation/disinfection;
- biological treatment;
- advanced oxidation as specialized branch;
- sludge/concentrate management;
- remineralization/stabilization;
- monitoring.

### 16.5 Wastewater

- flow/load variability;
- COD/BOD/TOC distinctions;
- solids;
- nitrogen/phosphorus;
- oxygen transfer;
- biomass kinetics;
- settling;
- industrial inhibitors/toxicity;
- anaerobic/aerobic routes;
- resource recovery;
- effluent/reuse requirements;
- sludge lifecycle.

### 16.6 Industrial loops

- boiler feed/steam condensate;
- cooling water;
- process wash;
- ultrapure water;
- mine water;
- refinery sour/process water at conceptual level;
- blowdown/recycle;
- water pinch/balance.

Источники:

- Stumm & Morgan, *Aquatic Chemistry*;
- Snoeyink & Jenkins, *Water Chemistry*;
- Crittenden et al., *MWH’s Water Treatment*;
- Metcalf & Eddy, *Wastewater Engineering*;
- WHO, *Guidelines for Drinking-water Quality*, fourth edition incorporating the first
  and second addenda: https://www.who.int/publications/i/item/9789240045064
- U.S. EPA Water Research: https://www.epa.gov/water-research
- USGS Water Data: https://waterdata.usgs.gov/

Standards/limits depend on jurisdiction and current edition; game values must not be
presented as public-health advice.

---

<a id="earth-17"></a>
## 17. Atmospheric chemistry, meteorology и dispersion

### 17.1 Atmospheric structure

- hydrostatic balance;
- pressure/temperature profiles;
- humidity/cloud microphysics;
- stability/stratification;
- boundary layer;
- turbulence;
- radiation;
- weather systems;
- terrain/urban effects.

Hydrostatic baseline:

`dp/dz = -ρg`.

It does not describe vertical acceleration, storms or small-scale turbulence alone.

### 17.2 Chemistry

- photolysis;
- OH and radical cycles;
- ozone chemistry;
- NOx/VOC interactions;
- sulfur/nitrogen oxidation;
- secondary aerosol;
- gas–particle partition;
- aqueous/cloud chemistry;
- deposition;
- lifetime/transport;
- greenhouse gases.

Emission mass is not ambient concentration without meteorology, chemistry and removal.

### 17.3 Aerosols

- number/mass/size distributions;
- nucleation/condensation/coagulation;
- hygroscopic growth;
- optical effects;
- dry/wet deposition;
- health/climate relevance;
- instrument cut size/response.

PM mass alone hides composition and ultrafine number.

### 17.4 Dispersion models

Levels:

- box model;
- Gaussian plume/puff under restricted assumptions;
- Lagrangian particle;
- Eulerian chemistry-transport;
- CFD local flow;
- ensemble meteorology.

Validate against concentration observations with background, averaging time and detection
limits. Do not use one steady plume for calm/complex terrain/transient release.

### 17.5 Climate scope

For lifecycle/project decisions use authoritative assessment rather than custom climate
model. Distinguish weather, climate, radiative forcing, emission metric, scenario and
impact. Avoid collapsing all effects into one context-free `pollution score`.

Sources:

- Jacob, *Introduction to Atmospheric Chemistry*:
  https://acmg.seas.harvard.edu/education/intro-atmos-chem-book
- Seinfeld & Pandis, *Atmospheric Chemistry and Physics*;
- Wallace & Hobbs, *Atmospheric Science*;
- NOAA Global Monitoring Laboratory: https://gml.noaa.gov/
- NASA Earthdata: https://www.earthdata.nasa.gov/
- IPCC reports: https://www.ipcc.ch/reports/

---

<a id="earth-18"></a>
## 18. Environmental fate, ecotoxicology и remediation

### 18.1 Source–pathway–receptor

Model:

`source → release → partition/transport/transformation → exposure → effect`.

Hazard alone is not risk; absence of immediate toxicity is not absence of persistence or
bioaccumulation.

### 18.2 Fate descriptors

- solubility;
- vapor pressure/Henry behavior;
- partition coefficients;
- pKa/speciation;
- degradation half-life;
- photolysis/hydrolysis;
- sorption;
- bioaccumulation;
- transformation products;
- sediment/soil/water/air transfer.

Use condition-specific measured data and uncertainty.

### 18.3 Exposure/effects

- acute/chronic;
- concentration–response;
- route/duration;
- species/life stage;
- mixture effects;
- bioavailability;
- benchmark/assessment factors;
- spatial/temporal variability.

Game mechanics should not present environmental limits as medical advice.

### 18.4 Remediation

Conceptual families:

- containment/isolation;
- excavation/removal;
- pump-and-treat;
- adsorption/precipitation;
- soil washing;
- thermal methods;
- bioremediation/phytoremediation;
- monitored natural attenuation;
- long-term stewardship.

Each transfers mass/energy/waste; «очистка» does not delete contaminants.

Sources:

- Schwarzenbach et al., *Environmental Organic Chemistry*;
- Manahan, *Environmental Chemistry*;
- U.S. EPA CompTox: https://comptox.epa.gov/dashboard/
- ECHA chemicals information: https://echa.europa.eu/information-on-chemicals
- UNEP resources: https://www.unep.org/

---

<a id="earth-19"></a>
## 19. Life-cycle assessment и circular materials

### 19.1 LCA phases

1. goal and scope;
2. functional unit/reference flow;
3. system boundary;
4. lifecycle inventory;
5. impact assessment;
6. interpretation/sensitivity;
7. reporting/review.

### 19.2 Functional unit

Сравнивать не `1 kg material` автоматически, а эквивалентную service:

- delivered mechanical function over life;
- energy supplied with reliability;
- water treated to specification;
- tonne-kilometre transport;
- product quality/time.

### 19.3 Boundary and allocation

Document:

- cradle/gate/use/end-of-life;
- geography/time/technology;
- co-products;
- recycling allocation;
- capital equipment;
- land/water;
- biogenic carbon assumptions;
- avoided products;
- cutoff;
- data quality.

Allocation choice can change answer; show alternative where decision-relevant.

### 19.4 Impact categories

- climate change;
- energy/resource use;
- water use/scarcity;
- acidification;
- eutrophication;
- ozone/photochemical effects;
- particulate;
- toxicity/ecotoxicity with larger uncertainty;
- land use;
- waste/circularity indicators.

Do not sum unlike categories into one score without explicit value choices.

### 19.5 Circularity

- reduce material/energy;
- durability/repair;
- reuse/remanufacture;
- recycling quality/yield;
- design for disassembly;
- contamination/downcycling;
- recovery energy;
- stock/delay;
- rebound/market assumptions.

Mass recycled is not equivalent to retained function/value.

### 19.6 Sources/tools

- ISO 14040/14044 — framework/requirements;
- ISO 14067 — product carbon footprint context;
- openLCA: https://www.openlca.org/
- Argonne GREET: https://greet.anl.gov/
- ecoinvent: https://ecoinvent.org/ — licensed inventory, respect terms;
- U.S. EPA sustainability/LCA tools:
  https://www.epa.gov/e3/e3-sustainability-tools
- IPCC assessment/GWP basis: https://www.ipcc.ch/reports/

---

<a id="earth-20"></a>
## 20. Biotechnology — conditional branch

Эта область нужна для wastewater biology, fermentation, bioleaching, biomaterials,
biofuels или agriculture. Без такой mechanic она остаётся P3.

### 20.1 Foundation

- cell structure;
- DNA/RNA/protein;
- enzymes;
- metabolism/ATP/redox;
- transport;
- growth/death;
- microbial communities;
- contamination;
- evolution/selection;
- biosafety/ethics.

### 20.2 Enzyme kinetics

Michaelis–Menten baseline:

`v = V_max S/(K_M + S)`.

Requires initial-rate/quasi-steady assumptions for a simple scheme. Inhibition,
allostery, transport, deactivation and multiple substrates need extensions.

### 20.3 Growth

Monod baseline:

`μ = μ_max S/(K_S + S)`.

Real growth can be limited by multiple nutrients/oxygen, inhibited, maintenance-dependent
and population-structured.

### 20.4 Bioprocess

- batch/fed-batch/continuous;
- mass/element/electron balance;
- yield coefficients;
- oxygen transfer;
- mixing/shear;
- heat;
- pH;
- contamination;
- sterilization assurance at conceptual level;
- downstream separation;
- waste/biosafety.

### 20.5 Safe boundary

No pathogen enhancement, genetic modification procedures, culture recipes or actionable
wet-lab protocols. Use harmless abstract organisms/process classes unless a separately
reviewed safe module is approved.

Books/sources:

- OpenStax Biology: https://openstax.org/details/books/biology-2e
- Nelson & Cox, *Lehninger Principles of Biochemistry*;
- Shuler, Kargi & DeLisa, *Bioprocess Engineering*;
- Doran, *Bioprocess Engineering Principles*;
- WHO Laboratory Biosafety Manual:
  https://www.who.int/publications/i/item/9789240011311

---

<a id="earth-21"></a>
## 21. Soil science и agrochemistry — conditional branch

### 21.1 Soil system

- mineral/organic fractions;
- texture/structure;
- porosity/bulk density;
- water retention/conductivity;
- cation exchange;
- pH/buffering;
- salinity/sodicity;
- redox;
- biology;
- erosion/compaction;
- spatial horizons.

Soil is not an inert inventory with one fertility scalar.

### 21.2 Water

Study:

- infiltration;
- field capacity/wilting concepts;
- unsaturated flow;
- evapotranspiration;
- drainage;
- preferential flow;
- root-zone balance;
- salinization.

### 21.3 Nutrients

- nitrogen fixation/mineralization/nitrification/denitrification;
- phosphorus sorption/precipitation;
- potassium and exchange;
- sulfur;
- micronutrients;
- plant uptake;
- leaching/runoff;
- deficiency/toxicity;
- organic amendments.

Fertilizer input is not equal to crop uptake; close nutrient and environmental balances.

### 21.4 Plant model

- light/temperature/water;
- phenology;
- biomass allocation;
- nutrient limitation;
- root access;
- disease/pest only as safe abstract pressure;
- genotype × environment interaction;
- harvest/residue.

### 21.5 Integration with AgriCraft

Before adding mechanics audit its actual soil/crop/nutrient model. New layer should add:

- measured soil properties;
- water/nutrient balance;
- salinity/pH;
- amendment quality;
- runoff/leaching;
- uncertainty/field sampling;
- compatibility adapters, not duplicate crops.

Sources:

- Brady & Weil, *The Nature and Properties of Soils*;
- Hillel, *Introduction to Environmental Soil Physics*;
- Havlin et al., *Soil Fertility and Fertilizers*;
- USDA NRCS soils: https://www.nrcs.usda.gov/resources/data-and-reports/soils
- FAO Soils Portal: https://www.fao.org/soils-portal/

---

<a id="earth-22"></a>
## 22. Cross-domain data model

```text
NaturalResourceBatch
  spatial_source_and_time
  geological_domain
  mineral_phase_distribution
  particle_size_texture_liberation
  grade_and_impurities
  moisture_density
  sampling_method
  uncertainty_and_detection_limits
  resource_classification

ProcessedMaterialBatch
  chemistry_and_phases
  manufacturing_thermal_history
  grain_pore_defect_distribution
  surface_and_residual_stress
  mechanical_thermal_electrical_optical_properties
  test_methods_reference_conditions
  variability_uncertainty

EnvironmentalFlow
  source_process
  medium_phase
  species_or_indicator
  mass_flow_and_concentration
  particle_size_speciation
  location_time_averaging
  transport_transformation
  measurement_uncertainty
  treatment_destination

LifecycleRecord
  functional_unit
  boundary
  geography_time_technology
  inventory_sources
  allocation_recycling_rules
  impact_method_version
  uncertainty_scenarios
```

---

<a id="earth-22a"></a>
## 22A. Сильная русскоязычная полка

Обозначения: **[М]** — международное влияние/переводы; **[В]** — сильный вузовский
источник; **[И]** — исторический, современные data, terminology и нормы обновлять.

- **В. И. Вернадский, «Очерки геохимии» и «Биосфера»** — фундаментальная geochemical/
  biosphere perspective, переводы и мировое влияние; quantitative data исторические.
  **[М, И]**
- **А. Е. Ферсман, «Геохимия»** — развитие geochemical classification и resource
  thinking; читать вместе с современной thermodynamics/speciation. **[М, И]**
- **Д. С. Коржинский, «Физико-химические основы анализа парагенезисов минералов»** —
  thermodynamic petrology и open-component systems; международно признанная школа.
  **[М]**
- **А. И. Перельман, Н. С. Касимов, «Геохимия ландшафта»** — migration/barriers и
  environmental geochemistry. **[В]**
- **В. А. Авдохин, «Основы обогащения полезных ископаемых»** — mineral preparation,
  separation и flowsheets. **[В]**
- **А. А. Абрамов, «Переработка, обогащение и комплексное использование твёрдых
  полезных ископаемых»** — системная отечественная школа mineral processing. **[В]**
- **П. П. Будников, Д. Н. Полубояринов, «Химическая технология керамики и огнеупоров»**
  и профильные продолжения школы — исторически сильная ceramic base; оборудование,
  energy и standards обновлять. **[В, И]**
- **И. И. Китайгородский, работы по технологии стекла** — glass-forming/process school;
  property data сверять с modern databases. **[В, И]**
- **Ю. М. Баженов, «Технология бетона»** — concrete composition, process и durability.
  **[В]**
- **А. Ф. Иоффе, «Физика полупроводников»** — исторически влиятельная semiconductor
  physics school с зарубежным изданием; device data устарели. **[М, И]**
- **Г. С. Ландсберг, «Оптика»** — сильный университетский курс wave/geometrical optics.
  **[В]**
- **О. А. Алекин, «Основы гидрохимии»** — natural-water composition/classification;
  speciation и environmental standards сверять с современными sources. **[В, И]**
- **Ю. А. Израэль, «Экология и контроль состояния природной среды»** — monitoring и
  environmental-system perspective; нормативные части исторические. **[В, И]**
- **Д. С. Орлов, «Химия почв»** — soil organic/mineral chemistry и sorption. **[В]**

---

<a id="earth-23"></a>
## 23. Практические проекты

### A. Ore-to-concentrate

- geological domains;
- sampling uncertainty;
- mineralogy/liberation;
- comminution/classification;
- two separation methods;
- grade/recovery;
- water/energy/wear;
- tailings chemistry;
- mass/element closure.

### B. Refractory-lined furnace

- process chemistry/temperature;
- lining phases/porosity;
- heat loss;
- slag/gas compatibility;
- penetration/wear;
- thermal cycling;
- inspection/remaining life;
- lifecycle replacement.

### C. Semiconductor sensor

- band/junction principle;
- temperature dependence;
- fabrication variation;
- calibration;
- noise/drift;
- package/thermal path;
- ageing;
- uncertainty.

### D. Industrial water loop

- source composition;
- speciation/saturation;
- treatment train;
- cooling/process demand;
- corrosion/scale;
- blowdown/recycle;
- wastewater/sludge;
- monitoring/decision rule;
- energy/LCA.

### E. Emission-to-impact chain

- source mass balance;
- stack/fugitive distinction;
- meteorology/dispersion;
- atmospheric transformation;
- deposition/exposure;
- measurement comparison;
- lifecycle boundary;
- uncertainty/scenarios.

### F. Soil nutrient loop

- soil properties;
- water balance;
- nutrient inputs/transformations;
- plant uptake;
- runoff/leaching;
- residue/recycling;
- sampling;
- integration with existing mod.

---

<a id="earth-24"></a>
## 24. Acceptance criteria

- rock/mineral/ore/resource/reserve terms not mixed;
- geological map stores scale/projection/uncertainty;
- mineral identification uses method evidence;
- geophysical result is not treated as exact block truth;
- sampling error separated from analytical error;
- ore processing closes mass and element balance;
- recovery never exceeds available feed component;
- tailings/water persist as streams;
- ceramic/glass/cement properties depend on process history;
- electronic/optical values include temperature/frequency/reference;
- water model includes speciation and solids;
- emission is separated from concentration/exposure/effect;
- LCA has functional unit, boundary and method version;
- bio/agro branch respects safety/ethics and existing mods;
- every dataset has provenance, license, version and uncertainty.

Главный принцип:

> **Геологическая история создаёт неоднородное сырьё; processing создаёт структуру;
> структура создаёт свойства; эксплуатация создаёт degradation; lifecycle определяет,
> куда перешли масса, энергия и последствия.**

---

<!-- SOURCES:START -->
<a id="earth-deep"></a>
## Углублённая университетская и исследовательская литература

Вводные источники не заменяют университетскую подготовку. Для глубокого маршрута нужны следующие дисциплины и монографии.

### Геология, минералогия и геохимия
- F. J. Pettijohn, *Sedimentary Rocks*.
- W. D. Nesse, *Introduction to Mineralogy*.
- C. Klein, B. Dutrow, *The Manual of Mineral Science*.
- H. Rollinson, *Using Geochemical Data*.
- W. M. White, *Geochemistry*.

### Геофизика, ресурсы и горное дело
- W. M. Telford et al., *Applied Geophysics*.
- J. M. Reynolds, *An Introduction to Applied and Environmental Geophysics*.
- H. K. W. Hoek, E. T. Brown, *Underground Excavations in Rock*.
- W. A. Hustrulid, *Underground Mining Methods*.
- B. A. Wills, J. Finch, *Wills' Mineral Processing Technology*.

### Материалы, керамика, стекло и цемент
- M. W. Barsoum, *Fundamentals of Ceramics*.
- J. S. Reed, *Principles of Ceramics Processing*.
- W. D. Kingery et al., *Introduction to Ceramics*.
- J. E. Shelby, *Introduction to Glass Science and Technology*.
- A. M. Neville, *Properties of Concrete*.

### Окружающая среда и жизненный цикл
- C. N. Sawyer, P. L. McCarty, G. F. Parkin, *Chemistry for Environmental Engineering and Science*.
- W. J. Weber, *Environmental Systems and Processes*.
- M. A. Haith, *Environmental Systems Optimization*.
- J. B. Guinée et al., *Handbook on Life Cycle Assessment*.

Критерий уровня: минералогическая идентификация, mass balance, uncertainty of reserves, transport/reaction model, exposure pathway, LCA inventory и sensitivity analysis.

<a id="earth-sources"></a>
## Сводный список источников

Этот раздел намеренно дублирует источники, приведённые рядом с темами. Список собран в одном месте для последовательного чтения и аудита ссылок.

### Книги, отчёты и стандарты

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

### Онлайн-курсы, базы данных и официальные страницы

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
