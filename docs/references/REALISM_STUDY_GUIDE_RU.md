# Инженерная база реализма для Aeronautics Plus

Актуальность ссылок проверена: **1 октября 2026 года**.

Этот список — учебный маршрут «с нуля» до уровня, на котором можно осмысленно
проектировать игровую физику самолётов, вертолётов, надводных судов и подлодок.
Он не заменяет профильное образование, расчёт сертифицированной техники или натурные
испытания. Его цель — дать аддону правдоподобные зависимости, ограничения, режимы
работы и понятные игроку причины отказов.

## Как пользоваться списком

Обозначения:

- **[Б]** — начальный уровень;
- **[С]** — средний инженерный уровень;
- **[П]** — продвинутый материал;
- **[свободно]** — легальный открытый доступ;
- **[книга]** — обычно библиотека или покупка; искать следует у издателя, в вузовской
  библиотеке, НЭБ или РГБ, а не на пиратских сайтах.

Порядок доверия к данным для аддона:

1. международный стандарт, государственное руководство или официальный отчёт;
2. университетский курс и профильный учебник;
3. рецензируемая статья с описанной методикой и диапазоном применимости;
4. документация производителя;
5. популярная статья или видео — только как объяснение, но не источник коэффициентов.

Не нужно сначала изучить все книги, а потом писать код. Для каждой игровой механики
достаточно пройти путь: **базовая теория → простая формула → эталонный расчёт → тест в
игре → уточнённая таблица коэффициентов**.

---

## 1. Короткий обязательный набор

Если нужен минимальный маршрут, начать следует с этих источников в указанном порядке.

1. **OpenStax University Physics, Vol. 1–2** — механика, вращение, колебания, жидкости,
   термодинамика. [Б, свободно]
   - https://openstax.org/details/books/university-physics-volume-1
   - https://openstax.org/details/books/university-physics-volume-2
2. **MIT OCW 18.01SC, 18.02SC, 18.03SC, 18.06** — анализ, многомерный анализ,
   дифференциальные уравнения и линейная алгебра. [Б–С, свободно]
   - https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/
   - https://ocw.mit.edu/courses/18-02sc-multivariable-calculus-fall-2010/
   - https://ocw.mit.edu/courses/18-03sc-differential-equations-fall-2011/
   - https://ocw.mit.edu/courses/18-06-linear-algebra-spring-2010/
3. **«Инженерная механика», УрФУ** — русскоязычный мост от школьной физики к
   расчётным схемам машин. [Б–С]
   - https://openedu.ru/course/urfu/ENGM/
4. **MIT OCW 2.003SC Engineering Dynamics** — движение твёрдого тела, моменты,
   вращение, колебания. [С, свободно]
   - https://ocw.mit.edu/courses/2-003sc-engineering-dynamics-fall-2011/
5. **MIT OCW 2.06 Fluid Dynamics** — гидростатика, контрольный объём, Бернулли,
   вязкость, пограничный слой и сопротивление. [С, свободно]
   - https://ocw.mit.edu/courses/2-06-fluid-dynamics-spring-2013/
6. **MIT Unified Engineering 16.01–16.04** — связанный курс по аэродинамике,
   термодинамике, двигателям, конструкциям и системам. [С, свободно]
   - https://ocw.mit.edu/courses/16-01-unified-engineering-i-ii-iii-iv-fall-2005-spring-2006/
7. **J. Gordon Leishman, Introduction to Aerospace Flight Vehicles** — открытый
   современный учебник: самолёты, вертолёты, винты, дирижабли, конструкции,
   устойчивость и управление. [Б–С, свободно]
   - https://eaglepubs.erau.edu/introductiontoaerospaceflightvehicles/
8. **NASA Beginner’s Guide to Aeronautics and Propulsion** — быстрый справочник по
   подъёмной силе, сопротивлению, числам Рейнольдса и Маха, винтам и двигателям.
   [Б, свободно]
   - https://www1.grc.nasa.gov/beginners-guide-to-aeronautics/
   - https://www.grc.nasa.gov/WWW/K-12/airplane/bgp.html
9. **FAA Pilot’s Handbook of Aeronautical Knowledge** — устройство самолёта,
   аэродинамика, органы управления, системы, масса, центровка и характеристики.
   [Б, свободно]
   - https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/phak
10. **FAA Helicopter Flying Handbook** — аэродинамика винта, автомат перекоса,
    управление, компоненты, режимы и опасные состояния. [Б–С, свободно]
    - https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/helicopter_flying_handbook
11. **NPTEL Introduction to Helicopter Aerodynamics and Dynamics** — переход от
    описательного уровня FAA к расчётам несущего винта. [С, свободно]
    - https://nptel.ac.in/courses/101104017
12. **NASA NDARC Theory** — единая расчётная архитектура обычных, тандемных,
    соосных вертолётов и конвертопланов. [П, свободно]
    - https://ntrs.nasa.gov/citations/20220000355
13. **MIT OCW 2.700 Principles of Naval Architecture** — геометрия корпуса,
    плавучесть, остойчивость, повреждения, сопротивление и прочность. [С, свободно]
    - https://ocw.mit.edu/courses/2-700-principles-of-naval-architecture-fall-2014/
14. **USNA EN400 Principles of Ship Performance** — цельный открытый конспект по
    судам и подлодкам от закона Архимеда до движителей. [Б–С, свободно]
    - https://www.usna.edu/NAOE/academics/en400_no_vids.php
15. **MIT OCW 2.20 Marine Hydrodynamics** — волны, присоединённая масса,
    сопротивление, подобие, испытания, винты и морская гидродинамика. [П, свободно]
    - https://ocw.mit.edu/courses/2-20-marine-hydrodynamics-13-021-spring-2005/
16. **MIT 2.154 Maneuvering and Control of Surface and Underwater Vehicles** —
    шестистепенная динамика и управление судами и подводными аппаратами.
    [П, свободно]
    - https://ocw.mit.edu/courses/2-154-maneuvering-and-control-of-surface-and-underwater-vehicles-13-49-fall-2004/
17. **Åström & Murray, Feedback Systems** — бесплатный фундамент по обратной связи,
    устойчивости и регуляторам. [С, свободно]
    - https://fbsbook.org/
18. **ITTC Recommended Procedures** — эталон того, как проверять корабельные модели,
    сопротивление, движители, кавитацию и неопределённость. [П, свободно]
    - https://www.ittc.info/recommended-procedures/

---

## 2. Математика и вычисления

### 2.1 Обязательная математика

- **MIT 18.01SC Single Variable Calculus** — производные, интегралы, ряды. [Б]
- **MIT 18.02SC Multivariable Calculus** — векторы, градиент, дивергенция, ротор,
  поверхностные и объёмные интегралы. [Б–С]
- **MIT 18.03SC Differential Equations** — ОДУ, колебания, устойчивость, отклик
  динамической системы. [С]
- **MIT 18.06 Linear Algebra, Gilbert Strang** — матрицы, собственные значения,
  системы координат и состояния. [Б–С]
- **3Blue1Brown: Essence of Calculus / Linear Algebra** — наглядная интуиция перед
  университетскими курсами. [Б, свободно]
  - https://www.3blue1brown.com/topics/calculus
  - https://www.3blue1brown.com/topics/linear-algebra
- **Мещерский, «Сборник задач по теоретической механике»** — классическая русская
  практика после основ анализа и векторов. [С, книга]

### 2.2 Численные методы и программирование

- **MIT 2.086 Numerical Computation for Mechanical Engineers** — ошибки,
  интерполяция, интегрирование, ОДУ, оптимизация и проверка расчётов. [С, свободно]
  - https://ocw.mit.edu/courses/2-086-numerical-computation-for-mechanical-engineers-fall-2014/
- **NumPy / SciPy / SymPy** — массивы, интеграторы, интерполяция, оптимизация и
  символьная проверка формул. [Б–С, свободно]
  - https://numpy.org/learn/
  - https://docs.scipy.org/doc/scipy/tutorial/
  - https://docs.sympy.org/latest/tutorials/intro-tutorial/
- **Chapra & Canale, Numerical Methods for Engineers** — практический основной
  учебник. [С, книга]
- **Trefethen & Bau, Numerical Linear Algebra** — устойчивые матричные вычисления.
  [П, книга]

Что нужно уметь до серьёзной физики аддона: переводить RPM в обороты в секунду и
угловую скорость, работать с векторами и матрицами вращения, интегрировать ОДУ,
интерполировать таблицы коэффициентов и оценивать численную ошибку.

---

## 3. Общая и инженерная механика

### 3.1 База

- **OpenStax University Physics Vol. 1** — силы, энергия, импульс, вращение,
  гравитация, колебания, волны и основы жидкостей. [Б, свободно]
- **MIT 8.01SC Classical Mechanics** — строгая университетская механика с задачами.
  [Б–С, свободно]
  - https://ocw.mit.edu/courses/8-01sc-classical-mechanics-fall-2016/
- **OpenEdu: Теоретическая механика, СПбПУ** — статика, кинематика, динамика и
  аналитическая механика на русском. [С]
  - https://openedu.ru/course/spbstu/TMECH/
- **Лойцянский, Лурье, «Курс теоретической механики»**. [С, книга]
- **Никитин, «Курс теоретической механики»**. [С, книга]

### 3.2 Твёрдое тело, машины и вибрации

- **MIT 2.003SC Engineering Dynamics** — кинематика и динамика твёрдых тел. [С]
- **MIT 8.09 Classical Mechanics III, главы Rigid Body и Fluid Mechanics** — тензор
  инерции, уравнения Эйлера и механика сплошной среды. [П, свободно]
  - https://ocw.mit.edu/courses/8-09-classical-mechanics-iii-fall-2014/pages/lecture-notes/
- **NPTEL Engineering Mechanics: Statics and Dynamics**. [Б–С, свободно]
  - https://nptel.ac.in/courses/112106180
- **NPTEL Dynamics of Machines / Mechanical Vibrations** — валы, дисбаланс,
  резонансы, виброизоляция. [С]
  - https://nptel.ac.in/courses
- **Shigley’s Mechanical Engineering Design** — валы, шпонки, зубчатые передачи,
  подшипники, пружины, усталость и коэффициенты запаса. [С–П, книга]
- **Norton, Design of Machinery** — кинематика механизмов, кулачки, зубчатые передачи
  и балансировка. [С, книга]
- **Machinery’s Handbook** — справочник по деталям машин, допускам и резьбам.
  [справочник, книга]

Для микромодульного ротора особенно важны: передача момента по валу, передаточное
отношение, направление вращения, жёсткость на кручение, люфт, шарнирные степени
свободы, дисбаланс и резонанс.

---

## 4. Сопротивление материалов, конструкции и материалы

- **MIT 2.001 Mechanics & Materials I** — напряжения, деформации, балки, кручение.
  [С, свободно]
  - https://ocw.mit.edu/courses/2-001-mechanics-materials-i-fall-2006/
- **MIT 2.002 Mechanics & Materials II** — устойчивость, пластичность, разрушение и
  усталость. [С, свободно]
  - https://ocw.mit.edu/courses/2-002-mechanics-and-materials-ii-spring-2004/
- **MIT 16.20 Structural Mechanics** — тонкостенные авиационные конструкции.
  [С–П, свободно]
  - https://ocw.mit.edu/courses/16-20-structural-mechanics-fall-2002/
- **MIT 2.092 Finite Element Analysis of Solids and Fluids I** — основы метода
  конечных элементов. [П, свободно]
  - https://ocw.mit.edu/courses/2-092-finite-element-analysis-of-solids-and-fluids-i-fall-2009/
- **DoITPoMS, University of Cambridge** — открытые модули по металлам, полимерам,
  композитам, усталости и разрушению. [Б–С, свободно]
  - https://www.doitpoms.ac.uk/
- **Gere / Goodno, Mechanics of Materials** или **Hibbeler, Mechanics of Materials** —
  основной задачник. [С, книга]
- **Megson, Aircraft Structures for Engineering Students** — самолётные балки,
  тонкостенные сечения, кручение, сдвиг и фермы. [С–П, книга]
- **Bruhn, Analysis and Design of Flight Vehicle Structures** — профессиональный
  справочник по авиационной прочности. [П, книга]
- **Barbero, Introduction to Composite Materials Design** — композитные лопасти и
  оболочки. [П, книга]

Игровая модель не обязана решать поле напряжений каждый тик. Достаточно заранее
рассчитать допустимые RPM, момент, изгибающую нагрузку и ресурс, а в игре сравнивать
текущие нагрузки с таблицей пределов.

---

## 5. Механика жидкости и газа

### 5.1 Первый курс

- **MIT 1.060 Engineering Mechanics II** — физически ориентированное введение в
  механику жидкости. [Б–С, свободно]
  - https://ocw.mit.edu/courses/1-060-engineering-mechanics-ii-spring-2006/
- **MIT 2.06 Fluid Dynamics** — основной расчётный курс. [С, свободно]
- **NPTEL Fluid Mechanics / Foundations of Fluid Mechanics** — полный видеокурс и
  дополнительные задачи. [С, свободно]
  - https://nptel.ac.in/courses
- **White, Fluid Mechanics** — сильный общий учебник. [С, книга]
- **Çengel & Cimbala, Fluid Mechanics: Fundamentals and Applications** — более
  плавное введение с большим числом задач. [Б–С, книга]
- **Munson et al., Fundamentals of Fluid Mechanics** — альтернативный основной
  учебник. [С, книга]
- **Лойцянский, «Механика жидкости и газа»** — классический русскоязычный курс.
  [С–П, книга]
- **Войткунский, Фаддеев, Федяевский, «Гидромеханика»** — общий и морской уклон.
  [С–П, книга]

### 5.2 Продвинутые темы

- **MIT 2.26 Compressible Fluid Dynamics** — сопла, скачки, течение Фанно и Релея.
  [П, свободно]
  - https://ocw.mit.edu/courses/2-26-compressible-fluid-dynamics-spring-2004/
- **MIT 2.27 Turbulent Flow and Transport**. [П, свободно]
  - https://ocw.mit.edu/courses/2-27-turbulent-flow-and-transport-spring-2002/
- **NASA Beginner’s Guide to Compressible Aerodynamics**. [С, свободно]
  - https://www.grc.nasa.gov/www/k-12/airplane/bgc.html
- **NACA Report 1135, Equations, Tables and Charts for Compressible Flow** —
  эталонные соотношения для проверки кода. [П, свободно]
  - https://ntrs.nasa.gov/citations/19930091059
- **Anderson, Fundamentals of Aerodynamics** — мост от общей гидрогазодинамики к
  самолётам и винтам. [С, книга]
- **Anderson, Modern Compressible Flow**. [П, книга]
- **Schlichting & Gersten, Boundary-Layer Theory**; русский перевод старого издания
  — **Шлихтинг, «Теория пограничного слоя»**. [П, книга]
- **Batchelor, An Introduction to Fluid Dynamics**. [П, книга]
- **Landau & Lifshitz, Fluid Mechanics / «Гидродинамика»**. [П, книга]

Ключевые безразмерные параметры: Reynolds, Mach, Froude, Strouhal, advance ratio,
коэффициенты тяги и момента, коэффициент кавитации. Без них нельзя корректно переносить
коэффициенты между маленькой моделью, блоковой конструкцией и полноразмерным аппаратом.

---

## 6. Термодинамика, теплопередача и энергетика

### 6.1 Основы

- **OpenStax University Physics Vol. 2** — температура, тепло, первое и второе начала,
  энтропия и тепловые машины. [Б, свободно]
- **МФТИ, «Термодинамика и молекулярная физика»** — русскоязычный курс с задачами.
  [Б–С]
  - https://courses.mipt.ru/course/view/71
- **OpenEdu / МИСИС, «Общая физика: механика, термодинамика и основы кинетической
  теории»**. [Б]
  - https://openedu.ru/course/misis/FIS/
- **MIT 5.60 Thermodynamics & Kinetics**. [С, свободно]
  - https://ocw.mit.edu/courses/5-60-thermodynamics-kinetics-spring-2008/
- **NPTEL Basic / Engineering Thermodynamics**. [С, свободно]
  - https://nptel.ac.in/courses
- **Çengel & Boles, Thermodynamics: An Engineering Approach** — удобный первый
  инженерный учебник. [Б–С, книга]
- **Moran et al., Fundamentals of Engineering Thermodynamics** — строгая
  альтернатива. [С, книга]

### 6.2 Теплообмен и двигатели

- **MIT 2.051 Introduction to Heat Transfer** — теплопроводность, конвекция,
  излучение и теплообменники. [С, свободно]
  - https://ocw.mit.edu/courses/2-051-introduction-to-heat-transfer-fall-2015/
- **Incropera et al., Fundamentals of Heat and Mass Transfer**. [С–П, книга]
- **MIT 16.50 Introduction to Propulsion Systems** — воздушно-реактивные и ракетные
  двигатели как системы. [С–П, свободно]
  - https://ocw.mit.edu/courses/16-50-introduction-to-propulsion-systems-spring-2012/
- **NASA Beginner’s Guide to Propulsion / EngineSim** — наглядное введение в винты,
  турбины и реактивную тягу. [Б–С, свободно]
- **Heywood, Internal Combustion Engine Fundamentals** — поршневые двигатели.
  [П, книга]
- **Saravanamuttoo et al., Gas Turbine Theory** — газовые турбины. [П, книга]
- **Rolls-Royce, The Jet Engine** — устройство авиационного ГТД. [С, книга]
- **Wärtsilä Encyclopedia of Marine and Energy Technology** — морские дизели,
  движители и судовые системы. [справочник, свободно]
  - https://www.wartsila.com/encyclopedia
- **Spirax Sarco Learn About Steam** — пар, котлы, конденсат и теплообмен.
  [Б–С, свободно]
  - https://www.spiraxsarco.com/learn-about-steam

Для игры особенно важны не детальные циклы, а баланс мощности: источник выдаёт
ограниченную механическую мощность, винт требует момент, потери превращаются в тепло,
а охлаждение и топливо ограничивают длительный максимальный режим.

---

## 7. Управление, устойчивость и динамические системы

- **Åström & Murray, Feedback Systems** — модели состояния, обратная связь,
  устойчивость, PID, частотные методы и робастность. [С, свободно]
  - https://fbsbook.org/
- **MIT 2.004 Systems, Modeling and Control II**. [С, свободно]
  - https://ocw.mit.edu/courses/2-004-systems-modeling-and-control-ii-fall-2007/
- **MIT 16.333 Aircraft Stability and Control** — устойчивость, производные,
  продольные и боковые режимы, V/STOL и управление. [П, свободно]
  - https://ocw.mit.edu/courses/16-333-aircraft-stability-and-control-fall-2004/
- **MIT Underactuated Robotics** — нелинейная динамика, состояния и управление
  недоприводными системами. [П, свободно]
  - https://underactuated.csail.mit.edu/
- **Brian Douglas, Control Systems Lectures** — наглядное дополнение, но не замена
  учебнику. [Б–С, свободно]
  - https://www.youtube.com/@BrianBDouglas
- **Stevens, Lewis & Johnson, Aircraft Control and Simulation** — шестистепенная
  модель, управление и симуляция. [П, книга]
- **Etkin & Reid, Dynamics of Flight**. [П, книга]
- **Nelson, Flight Stability and Automatic Control**. [С–П, книга]

В аддоне управление должно создавать силы и моменты, а не напрямую задавать ориентацию.
Автомат перекоса, рули, элероны, балластные цистерны и рули глубины должны менять
управляющее воздействие; интегратор физического движка уже изменяет движение аппарата.

---

## 8. Самолёты: аэродинамика, устройство и проектирование

### 8.1 Открытые курсы и руководства

- **ERAU Introduction to Aerospace Flight Vehicles** — лучший единый открытый старт.
- **NASA Beginner’s Guide to Aeronautics** — формулы, графики и интерактивные модели.
- **FAA Pilot’s Handbook of Aeronautical Knowledge** — главы Aircraft Construction,
  Principles of Flight, Aerodynamics, Controls, Systems, Weight and Balance, Performance.
- **FAA Aviation Maintenance Technician Handbook — General, Airframe, Powerplant** —
  реальное устройство силового набора, систем, двигателей, винтов и обслуживания.
  [Б–С, свободно]
  - https://www.faa.gov/regulations_policies/handbooks_manuals/aviation
- **FAA Airplane Flying Handbook** — связь конструкции и аэродинамики с режимами
  полёта. [Б, свободно]
  - https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/airplane_handbook
- **MIT 16.100 Aerodynamics**. [С, свободно]
  - https://ocw.mit.edu/courses/16-100-aerodynamics-fall-2005/
- **MIT 16.885J Aircraft Systems Engineering** — вес, безопасность, надёжность,
  подсистемы и компромиссы всего самолёта. [П, свободно]
  - https://ocw.mit.edu/courses/16-885j-aircraft-systems-engineering-fall-2005/
- **NPTEL Introduction to Aerospace Engineering** — атмосфера, устройство,
  аэродинамика, двигатели, характеристики и устойчивость. [Б–С, свободно]
  - https://onlinecourses.nptel.ac.in/noc21_ae11/preview

### 8.2 Основные книги

- **Anderson, Introduction to Flight** — первый цельный учебник. [Б–С]
- **Anderson, Aircraft Performance and Design**. [С]
- **Raymer, Aircraft Design: A Conceptual Approach** — компоновка и предварительный
  расчёт. [С–П]
- **Gudmundsson, General Aviation Aircraft Design** — практическое проектирование и
  большое число эмпирических зависимостей. [С–П]
- **Torenbeek, Synthesis of Subsonic Airplane Design**. [П]
- **Roskam, Airplane Design, Parts I–VIII** — подробная серия по проектированию. [П]
- **Megson, Aircraft Structures for Engineering Students** — конструкция и
  прочность. [С–П]
- **Житомирский, «Конструкция самолётов»** — русскоязычная конструктивная база.
  [С, книга]
- **Егер и др., «Проектирование самолётов»**. [П, книга]
- **Войт, Ендогур, Мелик-Саркисян, Алявдин, «Проектирование конструкций
  самолётов»**. [П, книга]
- **Остославский, «Динамика полёта. Траектории летательных аппаратов»**. [П, книга]

### 8.3 Что перенести в игру

- масса, центр масс и тензор инерции зависят от расположения блоков;
- подъёмная сила зависит от плотности, скорости, площади, угла атаки и конфигурации;
- сопротивление не равно постоянному штрафу и растёт с динамическим давлением;
- сваливание — потеря подъёмной силы и рост сопротивления после критического угла;
- закрылки увеличивают доступный коэффициент подъёмной силы, но добавляют сопротивление;
- элерон, руль высоты и направления создают моменты с учётом плеча до центра масс;
- винт должен учитывать скорость набегающего потока, а не только RPM;
- высота меняет плотность воздуха и доступную мощность некоторых двигателей.

---

## 9. Вертолёты, винты и соосные системы

### 9.1 От начального к продвинутому

1. **ERAU, Helicopters / Vertical Flight**. [Б, свободно]
   - https://eaglepubs.erau.edu/introductiontoaerospaceflightvehicles/chapter/helicopters/
2. **FAA Helicopter Flying Handbook**, главы 2–7. [Б, свободно]
3. **NPTEL Introduction to Helicopter Aerodynamics and Dynamics**. [С, свободно]
4. **Leishman, Principles of Helicopter Aerodynamics** — импульсная теория,
   blade-element theory, вихревой след, авторотация, шум и особые схемы. [С–П, книга]
5. **Bramwell’s Helicopter Dynamics** — динамика лопасти и аппарата. [П, книга]
6. **Wayne Johnson, Helicopter Theory** — фундаментальный расчётный труд. [П, книга]
7. **Wayne Johnson, Rotorcraft Aeromechanics** — современный полный курс. [П, книга]
8. **Prouty, Helicopter Performance, Stability, and Control** — практические
   приближения и инженерная интерпретация. [С–П, книга]
9. **NASA TP-2011-215971, Milestones in Rotorcraft Aeromechanics** — обзор развития
   теории, нагрузок, вибраций, устойчивости и CFD. [С–П, свободно]
   - https://rotorcraft.arc.nasa.gov/Johnson_TP-2011-215971_final.pdf
10. **NASA NDARC Theory** — расчёт конфигурации, массы, миссии, роторов, силовой
    установки и характеристик. [П, свободно]
    - https://rotorcraft.arc.nasa.gov/ndarc/index.php/reports-and-papers

### 9.2 Соосные роторы

- **Coleman, A Survey of Theoretical and Experimental Coaxial Rotor Aerodynamic
  Research, NASA TP-3675** — расстояние между роторами, распределение нагрузки,
  вихревой след, взаимное влияние, swirl recovery и эффективность. Это основной
  открытый источник для нашей соосной схемы. [П, свободно]
  - https://ntrs.nasa.gov/citations/19970015550
- **NASA NDARC Theory** — отдельные верхний и нижний роторы, направления вращения,
  мощность и конфигурация coaxial rotorcraft. [П, свободно]
- **Lim et al., Hover Performance Correlation for Full-Scale and Model-Scale Coaxial
  Rotors** — проверка расчётов по экспериментам. [П, статья]
  - https://rotorcraft.arc.nasa.gov/Publications/files/Lim_JAHS2009.pdf

### 9.3 Русскоязычная полка по вертолётам

- **Миль, Некрасов, Браверман и др., «Вертолёты. Расчёт и проектирование»**,
  книги 1–3. [С–П]
- **Володко, «Основы аэродинамики и динамики полёта вертолётов»**. [С]
- **Браверман, Вайнтруб, «Динамика вертолёта. Предельные режимы полёта»**. [П]
- **Богданов, Михеев, Скулков, «Конструкция вертолётов»**. [С]
- **Завалов, «Конструкция вертолётов»**. [С–П]
- **Тищенко, Некрасов, Радин, «Вертолёты. Выбор параметров при проектировании»**.
  [П]
- **Юрьев, «Аэродинамический расчёт вертолёта»** — исторический фундамент; сверять
  обозначения и эмпирику с современными источниками. [П]
- Библиография кафедры 102 МАИ:
  - https://institutes.mai.ru/avia/102_book/

### 9.4 Приоритеты для нашей механики

- направление RPM определяет знак вращения и аэродинамического момента;
- общий шаг меняет среднюю тягу, циклический шаг наклоняет результирующий вектор и
  создаёт управляющие моменты;
- тяга двух соосных роторов не должна просто удваиваться: нужны взаимное влияние,
  распределение тяги и баланс противоположных моментов;
- разные головки на 2/3/4/5 лопастей должны менять solidity, нагрузку на лопасть,
  допустимые RPM, момент и плавность;
- остановленный ротор даёт нулевую активную тягу, но движущийся аппарат может
  создавать авторотационные или сопротивляющие нагрузки;
- пределы по tip Mach, сваливанию отступающей лопасти, мощности и прочности важнее
  искусственного жёсткого потолка тяги.

---

## 10. Суда и корабли

### 10.1 Начальный и средний уровень

- **MIT 2.700 Principles of Naval Architecture** — обязательный основной курс.
- **USNA EN247 Principles of Naval Architecture and Marine Engineering** — обзор
  проектной спирали, геометрии, плавучести, остойчивости, сопротивления и энергетики.
  [Б–С, свободно]
  - https://www.usna.edu/NAOE/academics/en247.php
- **USNA EN400 Principles of Ship Performance** — открытые конспекты и лабораторные.
- **Tupper, Introduction to Naval Architecture** — лучший первый общий учебник.
  [Б–С, книга]
- **Rawson & Tupper, Basic Ship Theory, Vol. 1–2** — расчётная теория корабля.
  [С–П, книга]
- **Lewis, Principles of Naval Architecture** — фундаментальная серия SNAME:
  геометрия, остойчивость, прочность, сопротивление, движители и вибрации. [П, книга]

### 10.2 Гидродинамика, мореходность и движители

- **MIT 2.20 Marine Hydrodynamics**. [П, свободно]
- **Newman, Marine Hydrodynamics** — потенциальное течение, волны, присоединённая
  масса и гидродинамические силы. 40th Anniversary Edition доступно как официальный
  open-access e-book у MIT Press. [П]
  - https://direct.mit.edu/books/book/2693/Marine-Hydrodynamics
- **MIT 2.22 Design Principles for Ocean Vehicles** — нагрузки, волны,
  мореходность и маневренность. [П, свободно]
  - https://ocw.mit.edu/courses/2-22-design-principles-for-ocean-vehicles-13-42-spring-2005/
- **MIT 2.23 Hydrofoils and Propellers** — теория гребных винтов. [П, свободно]
  - https://ocw.mit.edu/courses/2-23-hydrofoils-and-propellers-spring-2007/
- **Molland, Turnock & Hudson, Ship Resistance and Propulsion**. [С–П, книга]
- **Carlton, Marine Propellers and Propulsion**. [П, книга]
- **Faltinsen, Sea Loads on Ships and Offshore Structures**. [П, книга]
- **Faltinsen, Hydrodynamics of High-Speed Marine Vehicles** — глиссеры,
  гидрокрылья и экранный эффект. [П, книга]

### 10.3 Конструкция и русскоязычные источники

- **Семёнов-Тян-Шанский, «Статика и динамика корабля»** — плавучесть,
  остойчивость и спуск. [С–П, книга]
- **«Справочник по теории корабля» под ред. Войткунского, 3 тома** — гидромеханика,
  сопротивление, движители, качка и управляемость. [П, книга]
  - https://rusneb.ru/catalog/000199_000009_001269973/
- **«Справочник по строительной механике корабля» под ред. Шиманского**. [П]
- **«Технология судостроения» под ред. Гармашева** — набор корпуса, сборка и
  испытания. [С]
- **Фукельман, «Основы теории корабля»** — более простой вход. [Б–С]

### 10.4 Что перенести в игру

- вытеснение вычисляется по реально погружённому объёму, а не площади контакта;
- сила плавучести приложена в центре величины, вес — в центре масс;
- остойчивость зависит от взаимного положения центров масс и плавучести и формы
  ватерлинии;
- свободная поверхность в неполной цистерне уменьшает остойчивость;
- затопление меняет массу, центр масс, запас плавучести и сопротивление;
- сопротивление включает как минимум вязкую и волновую части и связано с Reynolds и
  Froude;
- винт получает неоднородный поток за корпусом, может кавитировать и требует момент;
- волны вызывают качку, удары днищем, изменение погружения винта и вентиляцию.

---

## 11. Подлодки и подводные аппараты

Изучать после разделов о кораблях, механике жидкости, прочности и управлении.

- **USNA EN400** — базовые различия поверхностного и полностью погружённого тела,
  плавучесть, материалы, конструкция и движители. [Б–С, свободно]
- **MIT 2.154 Maneuvering and Control of Surface and Underwater Vehicles** —
  уравнения движения и управление. [П, свободно]
- **Burcher & Rydill, Concepts in Submarine Design** — гидростатика, прочность,
  мощность, динамика, компоновка и проектная спираль. [С–П, книга]
  - https://www.cambridge.org/core/books/concepts-in-submarine-design/868475E3EF0E09895E220BB02F21B715
- **Renilson, Submarine Hydrodynamics** — сопротивление, управление, маневрирование,
  винты и работа у поверхности. [П, книга]
- **Joubert, Some Aspects of Submarine Design, Part 1: Hydrodynamics** — открытый
  государственный обзор компромиссов формы и сопротивления. [П, свободно]
  - https://apps.dtic.mil/sti/tr/pdf/ADA428039.pdf
- **MIT 2.22 Design Principles for Ocean Vehicles** — внешние нагрузки и
  мореходность. [П, свободно]
- **MIT 2.080J Structural Mechanics** и курсы по plates/shells — оболочки под
  внешним давлением и потеря устойчивости. [П, свободно]
  - https://ocw.mit.edu/courses/2-080j-structural-mechanics-fall-2013/
- **Discovery of Sound in the Sea (DOSITS)** — физика распространения подводного
  звука, шум судов, природные источники и измерения. [Б–С, свободно]
  - https://dosits.org/

Минимальная игровая модель подлодки:

1. внешнее давление растёт с глубиной;
2. прочный корпус имеет предел по давлению и накоплению повреждений;
3. главные балластные цистерны меняют среднюю плотность для погружения и всплытия;
4. дифферентные цистерны перемещают центр масс;
5. на ходу горизонтальные рули создают подъёмную силу и момент;
6. полностью погружённое тело не имеет обычной метацентрической остойчивости
   надводного судна — важны взаимное положение центров тяжести и плавучести;
7. кавитация, шум и доступная тяга зависят от глубины, скорости и RPM.

Боевые системы в этот учебный план намеренно не входят: для реализма сборки достаточно
гидростатики, энергетики, конструкции, маневренности, акустики среды и жизнеобеспечения.

---

## 12. Воздушные и гребные винты, турбомашины

- **ERAU, Propellers** и главы о rotorcraft. [Б–С, свободно]
- **NASA Beginner’s Guide to Propulsion**. [Б]
- **MIT 2.23 Hydrofoils and Propellers**. [П]
- **MIT 16.540 Internal Flows in Turbomachines**. [П, свободно]
  - https://ocw.mit.edu/courses/16-540-internal-flows-in-turbomachines-spring-2006/
- **McCormick, Aerodynamics, Aeronautics, and Flight Mechanics** — теория
  воздушного винта и характеристики аппарата. [С–П, книга]
- **Glauert, The Elements of Aerofoil and Airscrew Theory** — классическая теория;
  использовать вместе с современными поправками. [П, книга]
- **Carlton, Marine Propellers and Propulsion** — гребные винты и кавитация. [П]
- **Dixon & Hall, Fluid Mechanics and Thermodynamics of Turbomachinery**. [П]
- **OpenProp** — открытый код и статьи по расчёту морских винтов и турбин.
  - http://openprop.mit.edu/

Для любого винта нужны не только тяга, но также реактивный момент, потребляемая
мощность, направление вращения, диаметр, шаг, число лопастей, коэффициенты в функции
advance ratio и пределы по прочности/кавитации/сжимаемости.

---

## 13. Атмосфера, океан, волны и погода

- **NOAA JetStream** — атмосфера, ветер, облака, верхние слои, грозы и океанские
  волны. [Б, свободно]
  - https://www.noaa.gov/jetstream
- **FAA Aviation Weather Handbook** — погода с точки зрения полёта. [Б–С, свободно]
  - https://www.faa.gov/regulations_policies/handbooks_manuals/aviation
- **MIT 12.003 Atmosphere, Ocean and Climate Dynamics**. [П, свободно]
  - https://ocw.mit.edu/courses/12-003-atmosphere-ocean-and-climate-dynamics-fall-2008/
- **NOAA Waves** — fetch, период, зыбь, значимая высота и прибой. [Б]
  - https://www.noaa.gov/jetstream/ocean/waves
- **U.S. Standard Atmosphere 1976** — эталон плотности, температуры и давления по
  высоте. [стандарт]
  - https://ntrs.nasa.gov/citations/19770009539
- **TEOS-10** — официальный стандарт свойств морской воды, льда и влажного воздуха.
  [стандарт, свободно]
  - https://www.teos-10.org/

Для Minecraft разумнее моделировать не полную метеорологию, а слои среды: плотность и
давление по высоте/глубине, постоянный и порывистый ветер, турбулентность у рельефа,
течение, простое спектральное волнение и локальные опасные погодные состояния.

---

## 14. Стандарты и проверочные данные

Эти документы нужны не для копирования реальных сертификационных требований, а для
единиц, определений, диапазонов и эталонных тестов.

### 14.1 Воздух и термодинамические свойства

- **U.S. Standard Atmosphere 1976** — атмосфера.
- **NIST Chemistry WebBook** — свойства газов и веществ.
  - https://webbook.nist.gov/chemistry/
- **IAPWS-95 / IAPWS-IF97** — свойства воды и пара.
  - https://iapws.org/
- **CoolProp** — открытая программная реализация свойств множества рабочих тел;
  проверять диапазон и backend. [свободно]
  - https://coolprop.org/
- **TEOS-10 / Gibbs SeaWater (GSW)** — морская вода.

### 14.2 Авиация

- **FAA handbooks** — терминология, системы и эксплуатационные режимы.
- **NASA Technical Reports Server (NTRS)** — NACA/NASA отчёты и экспериментальные
  данные.
  - https://ntrs.nasa.gov/
- **UIUC Airfoil Data Site** — координаты профилей и часть экспериментальных поляр;
  всегда сохранять происхождение конкретного набора.
  - https://m-selig.ae.illinois.edu/ads.html
- **NASA Turbulence Modeling Resource** — эталонные CFD cases. [П]
  - https://turbmodels.larc.nasa.gov/

### 14.3 Морская техника

- **ITTC Recommended Procedures and Guidelines** — сопротивление, self-propulsion,
  мореходность, маневренность, кавитация, шум, CFD verification and validation.
- **IMO 2008 Intact Stability Code (IS Code)** — определения и реальная логика
  проверки остойчивости. Актуальную редакцию брать у IMO.
  - https://www.imo.org/
- **DNV Rules and Standards** — корпуса, механизмы и морские операции; часть
  документов доступна после бесплатной регистрации.
  - https://www.dnv.com/rules-standards/
- **ABS Rules and Guides** — корабли, малые суда и подводные аппараты.
  - https://ww2.eagle.org/en/rules-and-resources/rules-and-guides.html

### 14.4 Метрология и неопределённость

- **NIST Engineering Statistics Handbook** — погрешности, регрессия, планирование
  эксперимента. [свободно]
  - https://www.itl.nist.gov/div898/handbook/
- **JCGM 100: GUM, Guide to the Expression of Uncertainty in Measurement**.
  - https://www.bipm.org/en/committees/jc/jcgm/publications
- **ITTC uncertainty procedures** — практические примеры для сопротивления и CFD.

---

## 15. Инструменты для расчётов и проверки

### 15.1 Базовый набор

- **Python + Jupyter + NumPy + SciPy + SymPy + Matplotlib** — расчёты и графики;
- **Pint** — размерности и автоматическое преобразование единиц;
- **pandas** — таблицы поляр и коэффициентов;
- **python-control** — динамические системы и регуляторы;
- **CoolProp** — свойства рабочих тел;
- **GSW-Python** — TEOS-10.

### 15.2 Авиация и роторы

- **XFOIL** — двумерные дозвуковые профили; не доверять около глубокого срыва и вне
  проверенного диапазона Reynolds/Mach.
  - https://web.mit.edu/drela/Public/web/xfoil/
- **XFLR5** — профили, крылья, VLM/панельные методы и устойчивость малых самолётов.
  - https://www.xflr5.tech/
- **OpenVSP + VSPAERO** — параметрическая геометрия самолёта, площади,
  аэродинамика концептуального уровня и простые модели винтов. [свободно]
  - https://openvsp.org/
- **JSBSim** — открытая нелинейная 6-DoF модель полёта, атмосфера, масса,
  двигатели, шасси и управление. Хороший архитектурный образец для нашей физики.
  - https://github.com/JSBSim-Team/jsbsim
- **QBlade** — blade-element/momentum расчёты роторов и турбин; результаты сверять с
  учебником и экспериментом.
  - https://qblade.org/

### 15.3 Суда и подлодки

- **FreeShip / FreeShip Plus** — форма корпуса и учебные гидростатические расчёты.
  - https://github.com/markmal/freeship-plus-in-lazarus
- **DELFTship Free** — форма корпуса и базовая гидростатика; расширенные функции
  могут быть коммерческими.
  - https://www.delftship.net/
- **OpenProp** — предварительный расчёт гребных винтов и турбин.
- **Capytaine** — линейная потенциальная гидродинамика плавающих тел.
  - https://capytaine.org/

### 15.4 CFD, FEA и визуализация

- **OpenFOAM Foundation** — CFD, свободно; высокий порог входа.
  - https://openfoam.org/
- **SU2** — аэродинамический CFD и оптимизация, свободно.
  - https://su2code.github.io/
- **Gmsh** — сетки. https://gmsh.info/
- **ParaView** — визуализация. https://www.paraview.org/
- **CalculiX**, **Code_Aster**, **Elmer FEM** — открытые решатели прочности и
  мультифизики.
- **FreeCAD** — инженерная геометрия; **Blender** — игровая модель и визуальная
  проверка, но не источник прочностного или гидродинамического результата.

Правило: CFD/FEA нельзя считать «истиной» только потому, что получилась красивая цветная
картинка. Нужны исследование сходимости сетки, чувствительность к шагу времени, баланс
массы/энергии и сравнение с аналитикой или экспериментом.

---

## 16. Лестница реализма для аддона

### Уровень 0 — размерности и предельные случаи

- единицы SI внутри расчётного слоя;
- нулевая скорость даёт нулевую аэродинамическую силу;
- знак RPM сохраняется;
- масса и энергия не возникают из ничего;
- формулы не зависят от FPS и игрового tick rate.

### Уровень 1 — квазистационарные формулы

- `q = ρV²/2`;
- `L = q S C_L`, `D = q S C_D`;
- `B = ρ g V_displaced`;
- `p = p_surface + ρgh`;
- силы и моменты суммируются относительно центра масс;
- простой тепловой и механический баланс мощности.

### Уровень 2 — безразмерные таблицы

- поляры крыла `C_L(α, Re, Mach)` и `C_D(...)`;
- винтовые кривые `C_T(J)`, `C_Q(J)`;
- зависимости сопротивления корпуса от Reynolds и Froude;
- коэффициенты рулей, винтов и роторов по режиму;
- материал и геометрия задают допустимую нагрузку.

### Уровень 3 — нестационарность и взаимное влияние

- динамическое сваливание и запаздывание сил;
- вихревое кольцо, ground effect, translational lift;
- взаимодействие верхнего и нижнего соосных роторов;
- качка на волне, присоединённая масса и демпфирование;
- затопление отсеков, перетекание жидкости и свободные поверхности.

### Уровень 4 — офлайн-инженерный расчёт

CFD, FEA и оптимизация используются **вне игрового тика**, чтобы создать и проверить
таблицы параметров. Minecraft получает компактную устойчивую модель, а не решатель
Navier–Stokes на каждом корабле.

---

## 17. Формулы, с которых следует начать реализацию

### 17.1 Воздушный или гребной винт

Пусть `n` — обороты в секунду, не RPM, `D` — диаметр:

- `J = V_a / (nD)` — advance ratio;
- `T = C_T(J) ρ n² D⁴` — модуль тяги;
- `Q = C_Q(J) ρ n² D⁵` — момент;
- `P = 2πnQ` — потребляемая мощность.

Направление силы и момента задаётся осью и знаком вращения. Около `n = 0` нужен
численно устойчивый переход. Текущую линейную зависимость тяги от RPM в Aeronautics Plus
следует рассматривать как временную игровую аппроксимацию; реалистичная следующая
ступень — квадратичная зависимость с таблицей `C_T(J)` и ограничением по доступной
мощности.

### 17.2 Несущий винт в висении

Начальный ориентир импульсной теории:

- `T = 2ρA v_i²`;
- `P_induced = T v_i`;
- `A = πR²`.

Затем добавляются профильная мощность, потери, число лопастей, solidity, ground effect,
режим снижения и взаимное влияние роторов. Для соосной пары нельзя дважды вызвать
формулу одиночного ротора без interference model.

### 17.3 Крыло и органы управления

- локальная относительная скорость складывается из поступательной и угловой;
- сила рассчитывается в локальном потоке;
- момент `M = r × F` создаётся плечом от центра масс;
- коэффициенты зависят от угла атаки и отклонения поверхности;
- после критического угла нужен спад `C_L` и рост `C_D`.

### 17.4 Судно и подлодка

- интегрировать погружённый объём и центр плавучести;
- вычислять плавучесть отдельно от гидродинамической подъёмной силы;
- давление и повреждение корпуса зависят от глубины;
- сопротивление и управляющие силы зависят от относительного потока;
- вода в цистернах меняет массу и центр масс;
- частично заполненная цистерна создаёт штраф свободной поверхности.

### 17.5 Трансмиссия

- `P = τω`;
- идеальный редуктор меняет `τ` и `ω` обратно пропорционально, но не создаёт мощность;
- реальные подшипники, шестерни и шарниры добавляют потери и предел нагрузки;
- два встречно вращающихся ротора требуют раздельных ветвей момента и проверки
  направления каждой ветви.

---

## 18. План обучения и разработки

### Этап A — 8–12 недель: фундамент

Изучить OpenStax Vol. 1, основы анализа, векторы и инженерную механику. Параллельно
написать маленькие Python-проверки: падение, вращающийся диск, пружина, баланс сил и
моментов, преобразование координат.

### Этап B — 8–12 недель: жидкости и термодинамика

MIT 2.06, вводная термодинамика, размерный анализ. Реализовать отдельную библиотеку
эталонных расчётов: Bernoulli, drag, buoyancy, hydrostatic pressure, пропеллерные
коэффициенты, тепловой баланс.

### Этап C — 8–12 недель: самолёт и обычный винт

ERAU + FAA + NASA + MIT 16.100. Сделать офлайн-поляры, квазистационарные силы,
момент от органов управления, центровку, сваливание и мощностное ограничение винта.

### Этап D — 12–16 недель: вертолёт

FAA → NPTEL → Leishman → NASA NDARC. Ввести импульсную и blade-element модель,
общий/циклический шаг, реактивный момент, рулевой винт, ground effect и авторотационные
режимы в упрощённом виде.

### Этап E — 8–12 недель: соосная система

Coleman NASA TP-3675 + NDARC + экспериментальные статьи. Сначала две независимые
кинематические ветви и баланс момента, затем таблица взаимного влияния. Не обещать
точность, пока нет теста против опубликованных hover data.

### Этап F — 12–16 недель: суда и подлодки

MIT 2.700 + USNA EN400 → MIT 2.20/2.154. Реализовать объёмную плавучесть,
остойчивость, затопление, давление, сопротивление, винты, рули и балласт. Волны и
кавитацию добавлять после устойчивой базовой модели.

### Этап G — постоянно: верификация

Для каждого изменения сохранять эталонные cases, графики и диапазон применимости.
Коэффициенты из разных источников не смешивать без приведения единиц, геометрии и
безразмерных параметров.

---

## 19. Матрица источников для задач Aeronautics Plus

| Игровая задача | Первый источник | Уточняющий источник | Целевой уровень |
|---|---|---|---|
| Signed thrust от RPM | NASA Propulsion | ERAU + `C_T/C_Q` curves | 2 |
| Момент и мощность винта | MIT 16.50 | McCormick / OpenVSP | 2 |
| Несущий винт | FAA Helicopter Handbook | Leishman / NDARC | 2–3 |
| Соосный ротор | Coleman TP-3675 | NDARC + hover experiments | 3 |
| Мачта, редуктор, шарниры | Shigley | NPTEL Machines / vibrations | 2 |
| Крылья и рули | ERAU / FAA PHAK | MIT 16.100 / 16.333 | 2–3 |
| Центровка и инерция | OpenStax / MIT Dynamics | JSBSim formulation | 1–2 |
| Плавучесть судна | USNA EN400 | MIT 2.700 | 1–2 |
| Остойчивость и затопление | MIT 2.700 | Tupper / IMO IS Code | 2–3 |
| Сопротивление корпуса | MIT 2.20 | ITTC / Molland | 2–3 |
| Гребной винт | MIT 2.23 | Carlton / OpenProp / ITTC | 2–3 |
| Подлодка и балласт | USNA EN400 | Burcher / Renilson / MIT 2.154 | 2–3 |
| Давление и прочный корпус | hydrostatics + MIT structures | shell buckling references | 2 |
| Погода и ветер | NOAA JetStream | FAA Weather / MIT 12.003 | 1–3 |
| Вода/пар/морская вода | IAPWS / TEOS-10 | CoolProp / GSW | 2 |
| Регуляторы и автоматика | Åström & Murray | MIT 16.333 | 2–3 |

---

## 20. Чек-лист приёмки каждой физической механики

1. Записаны единицы всех входов и выходов.
2. Указаны источник формулы и диапазон применимости.
3. Проверены нулевой режим, смена знака и очень большие значения.
4. Сходятся баланс сил, моментов, мощности и энергии.
5. Результат не меняется при другом FPS; зависимость от tick rate контролируется.
6. Есть не менее одного ручного аналитического примера.
7. Есть не менее одного сравнения с опубликованной таблицей, графиком или опытом.
8. Таблица коэффициентов хранит происхождение и версию источника.
9. Искусственные caps помечены как gameplay limits, а не физические законы.
10. В CI есть regression test на знак, монотонность, сохранение и предельные случаи.
11. Высокоточная офлайн-модель не подменяется необоснованной CFD-картинкой.
12. В игре есть понятная обратная связь: очки/прибор показывают RPM, тягу, момент,
    мощность, перегрузку, глубину, давление или причину отказа.

## Итоговый принцип

Реализм — это не максимальное количество формул. Это правильные причинно-следственные
связи, сохранение величин, корректные масштабы и узнаваемые режимы. Лучше устойчивая
модель уровня 1–2, проверенная по учебнику и тесту, чем сложная модель уровня 4 без
верификации и с непредсказуемым поведением в игровом тике.
