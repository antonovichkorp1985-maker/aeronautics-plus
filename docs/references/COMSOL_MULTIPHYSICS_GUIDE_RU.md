# COMSOL Multiphysics: учебный гайд для ChemMod и Aeronautics Plus

**Статус:** учебный материал проекта
**Дата:** 2026-10-08
**Уровень:** университетский / исследовательский
**Назначение:** офлайн-верификация физических моделей; COMSOL не является runtime-зависимостью Minecraft-мода.

## 1. Что это за программа

COMSOL Multiphysics — среда моделирования, в которой геометрия, материалы, физические интерфейсы, сетка, решатели и постобработка собираются в воспроизводимую расчётную модель. Она позволяет решать связанные задачи механики, течений, теплопередачи, химической кинетики, электромагнетизма, акустики и динамики конструкций.

Для проекта COMSOL нужен не для замены физического ядра Aeronautics Plus, а для получения проверочных данных:

```text
COMSOL / аналитическое решение
        ↓
эталонные значения и диапазоны применимости
        ↓
быстрые модели Aeronautics Plus
        ↓
unit-тесты и проверка регрессий
```

Нельзя переносить в Minecraft произвольные коэффициенты из одного расчёта без указания геометрии, сетки, материала, режима течения и диапазона применимости.

## 2. Официальный маршрут изучения

1. **Introduction to COMSOL Multiphysics, документация и User's Guides**  
   https://www.comsol.com/documentation
2. **COMSOL Learning Center**  
   https://www.comsol.com/support/learning-center
3. **Getting Started with Modeling Structural Mechanics** — базовая статическая модель, напряжения, деформации, материалы, нагрузки, сетка и результаты.  
   https://www.comsol.com/support/learning-center/course/getting-started-with-modeling-structural-mechanics-382/getting-started-with-modeling-structural-mechanics-123502
4. **Physics Interfaces for Modeling Structures** — Solid, Shell, Beam/Edge и выбор структурного интерфейса.  
   https://www.comsol.com/support/learning-center/course/getting-started-with-modeling-structural-mechanics-382/physics-interfaces-for-modeling-structures-123682
5. **Basics of Eigenfrequency Analysis** — собственные частоты, формы колебаний и prestressed eigenfrequency.  
   https://www.comsol.com/support/learning-center/course/getting-started-with-modeling-structural-mechanics-382/basics-of-eigenfrequency-analysis-in-structural-mechanics-123812
6. **Defining Multiphysics Models Automatically with Multiphysics Interfaces** — сопряжение электричества, тепла, структуры, течения и химии.  
   https://www.comsol.com/support/learning-center/article/Defining-Multiphysics-Models-Automatically-with-Multiphysics-Interfaces-25931
7. **Application Libraries** внутри установленного COMSOL: Structural Mechanics, CFD, Heat Transfer, Chemical Reaction Engineering, Fluid-Structure Interaction, Rotordynamics и Multibody Dynamics.

## 3. Model Builder: правильный порядок работы

### 3.1 Parameters и Variables

Все размеры, скорости, давления и свойства, которые должны изменяться, задаются параметрами. Не следует зашивать числа в отдельных узлах модели.

Минимальный набор для корпуса ракеты:

```text
D       диаметр корпуса
L       длина секции
m_dry   сухая масса
p_tank  давление в баке
T_wall  температура стенки
v       скорость
rho     плотность среды
```

Переменные должны иметь единицы измерения и физический смысл. COMSOL должен самостоятельно проверять размерность выражений.

### 3.2 Geometry

Начинать следует с минимальной геометрии, которая отвечает исследуемому вопросу:

- 2D axisymmetric — бак, сопло, осесимметричный корпус;
- 2D — профиль и простое внешнее течение;
- 3D — боковые ускорители, решётчатые рули, асимметрия, крепления и FSI.

Union, Assembly и Contact Pairs нельзя выбирать формально: выбор зависит от того, должна ли геометрия быть единой областью или между частями должна существовать механическая/тепловая связь.

### 3.3 Materials

Для каждого материала фиксируются:

- плотность;
- модуль Юнга и коэффициент Пуассона;
- предел текучести и прочность;
- коэффициент теплового расширения;
- теплопроводность;
- теплоёмкость;
- температурная зависимость;
- вязкость и плотность среды, если материал участвует в течении.

Материальные данные должны быть совместимы с каноническими партиями и материалами ChemMod. COMSOL не должен создавать второй независимый справочник материалов внутри Aeronautics Plus.

## 4. Физические интерфейсы для проекта

### 4.1 Конструкция корабля

- **Solid Mechanics** — напряжения, деформации, нагрузки, давление и крепления.
- **Shells** — тонкая обшивка и панели.
- **Beams/Trusses** — фермы, силовой набор и межступенчатые элементы.
- **Contact** — разъёмные соединения, опоры и соприкосновение деталей.
- **Eigenfrequency** — собственные частоты и формы колебаний.
- **Time Dependent Structural** — переходные нагрузки, вибрации и ударные режимы.
- **Multibody Dynamics** — отделение ступеней, шарниры, посадочные опоры и раскрытие механизмов.
- **Rotordynamics** — валы, насосы, турбины и дисбаланс.

### 4.2 Аэродинамика

- **CFD Module** — внешнее и внутреннее течение.
- **Compressible Flow** — сжимаемое течение, число Маха, ударные волны и сопло.
- **Turbulent Flow** — RANS-модели, wall treatment и чувствительность к модели турбулентности.
- **Fluid-Structure Interaction** — деформация крыльев, рулей, решётчатых рулей и корпуса.
- **Rotating Machinery** — пропеллеры, турбины и вращающиеся элементы.

Для Minecraft не требуется переносить полное CFD-решение в runtime. Нужно получить проверенные зависимости:

```text
Cd(Mach, angle_of_attack, Reynolds)
Cl(Mach, angle_of_attack)
heat_flux(Mach, altitude, angle_of_attack)
```

Затем хранить аппроксимации и диапазоны применимости.

### 4.3 Тепло и химия

- **Heat Transfer in Solids/Fluids** — бак, сопло, стенки и теплозащита.
- **Nonisothermal Flow** — связь температуры, плотности, вязкости и течения.
- **Chemical Reaction Engineering** — Arrhenius, баланс массы, скорость и конверсия.
- **Transport of Diluted Species** — диффузия, смешение и перенос компонентов.

ChemMod остаётся каноническим источником химических веществ, структур молекул, партий и процессов. COMSOL используется для эталонной проверки отдельных моделей, а не для создания второго скрытого ChemMod.

## 5. Минимальный учебный набор расчётов

### Расчёт 1: бак под давлением

1. Построить тонкостенный цилиндр в 2D axisymmetric.
2. Назначить материал стенки.
3. Задать внутреннее давление.
4. Закрепить модель так, чтобы не создавать ложную жёсткость.
5. Рассчитать hoop stress и longitudinal stress.
6. Сравнить с аналитическими формулами тонкостенной оболочки.
7. Выполнить mesh convergence.

Результат для Aeronautics Plus: предел давления, запас прочности, масса бака и область применимости упрощённой модели.

### Расчёт 2: собственные частоты конструкции

1. Собрать бак, ферму и двигательную раму.
2. Выполнить stationary/preload при необходимости.
3. Запустить eigenfrequency.
4. Проверить формы колебаний.
5. Сравнить частоты с рабочими частотами двигателя и вращающихся агрегатов.

Результат: предупреждение о резонансе и риск повреждения при разгоне.

### Расчёт 3: внешнее сопротивление

1. Построить корпус и окружающую область.
2. Задать входную скорость, плотность, вязкость и температуру.
3. Уточнить сетку в пограничном слое.
4. Получить drag force и безразмерный `Cd`.
5. Повторить для разных чисел Маха и углов атаки.
6. Проверить результат аналитической корреляцией.

Результат: таблица `Cd` для runtime-модели и диапазон, где она достоверна.

### Расчёт 4: сопло и нагрев

1. Начать с 2D axisymmetric.
2. Задать состав и свойства рабочего тела из ChemMod-совместимого набора.
3. Задать давление и температуру камеры.
4. Решить сжимаемое течение.
5. Добавить теплопередачу к стенке.
6. Проверить массовый расход, тягу, температуру и тепловой поток.

Результат: эталонные значения тяги, `Isp`, расхода и предельного теплового режима.

### Расчёт 5: вход в атмосферу

1. Взять профиль плотности и температуры атмосферы.
2. Использовать внешнее сжимаемое течение.
3. Добавить тепловой поток и теплозащиту.
4. Исследовать угол входа, скорость и коэффициент сопротивления.
5. Построить envelope допустимых режимов.

Результат должен выражаться не случайным взрывом, а диагностикой:

```text
heat flux > shield limit
→ degradation rate увеличена
→ запас теплозащиты уменьшается
→ требуется изменить угол/скорость/траекторию
```

## 6. Сетка и верификация

Ни один расчёт не принимается только потому, что COMSOL выдал красивую картинку.

Обязательные проверки:

- размерностный анализ;
- mass balance;
- energy balance;
- mesh convergence;
- time-step convergence;
- чувствительность к граничным условиям;
- сравнение с аналитическим решением;
- сравнение с экспериментальными/справочными данными;
- указание диапазона применимости.

Каждый результат для мода должен храниться как контрольный набор:

```text
geometry_id
material_id
physics_model
mesh_level
solver_settings
input_parameters
output_values
validity_range
reference_source
```

## 7. Связь с архитектурой Aeronautics Plus

```text
ChemMod
  → вещества, партии, смеси, давление, температура, плотность и химия

Aeronautics Winds
  → ветер и погодные возмущения

Create Aeronautics / Sable
  → физическая конструкция и базовая кинематика

Aeronautics Plus
  → массы, аэродинамика, тяга, нагрев, разрушение,
    ракеты, ступени и орбитальная миссия

COMSOL
  → офлайн-эталоны, калибровка и verification datasets
```

COMSOL не должен становиться обязательной зависимостью сборки или запускаться во время игры. Runtime использует физически осмысленные формулы, таблицы интерполяции и модели пониженного порядка.

## 8. Литература для продолжения

- J. D. Anderson, *Fundamentals of Aerodynamics*.
- F. M. White, *Fluid Mechanics*.
- G. K. Batchelor, *An Introduction to Fluid Dynamics*.
- H. Schlichting, K. Gersten, *Boundary-Layer Theory*.
- K. J. Bathe, *Finite Element Procedures*.
- O. C. Zienkiewicz, R. L. Taylor, J. Z. Zhu, *The Finite Element Method*.
- G. P. Sutton, O. Biblarz, *Rocket Propulsion Elements*.
- D. K. Huzel, D. H. Huang, *Design of Liquid-Propellant Rocket Engines*, NASA/SP-125.
- H. D. Curtis, *Orbital Mechanics for Engineering Students*.
- D. A. Vallado, *Fundamentals of Astrodynamics and Applications*.
- R. B. Bird, W. E. Stewart, E. N. Lightfoot, *Transport Phenomena*.
- NASA, *Systems Engineering Handbook*, NASA/SP-2016-6105 Rev 2.
- NASA Apollo Experience Reports и Apollo 13 Mission Report.

## 9. Доступ через СПбГУ

Искать книги и статьи следует по ISBN/DOI через университетский каталог и открывать издательские версии через сеть или удалённый доступ СПбГУ:

- Электронная библиотека СПбГУ: https://library.spbu.ru/
- Каталог библиотеки СПбГУ: https://catalog.library.spbu.ru/
- eLIBRARY.RU: https://www.elibrary.ru/
- SpringerLink: https://link.springer.com/
- ScienceDirect: https://www.sciencedirect.com/
- Wiley Online Library: https://onlinelibrary.wiley.com/
- Cambridge Core: https://www.cambridge.org/core/
- AIAA Aerospace Research Central: https://arc.aiaa.org/
- NASA Technical Reports Server: https://ntrs.nasa.gov/
- Crossref: https://search.crossref.org/

Подписки СПбГУ и права на конкретные издания могут меняться, поэтому доступ нужно проверять по каждой записи. При отсутствии полной версии использовать официальный open access, NASA NTRS или авторский manuscript, а не случайную копию.
