# Учебный гайд по литературе СПбГУ для физики, ChemMod и космоса

Версия: 2026-10-08

Это самостоятельный список книг, которые следует искать через каталог и электронные ресурсы СПбГУ вместо общего списка литературы проекта. ChemMod, Aeronautics Plus и COMSOL — разные системы; COMSOL здесь только отдельный инженерный инструмент для проверки физики.

## Как искать через СПбГУ

Искать по полному названию, автору, ISBN и DOI. Для каждой записи проверить: печатный или электронный фонд, конкретное издание, язык, полный текст и возможность доступа через университетскую авторизацию.

- Электронная библиотека СПбГУ: https://library.spbu.ru/
- Каталог СПбГУ: https://catalog.library.spbu.ru/
- Библиотека физического факультета: https://phys.spbu.ru/library/
- Библиотека матмеха: https://math.spbu.ru/rus/lib.html
- eLIBRARY: https://www.elibrary.ru/
- SpringerLink: https://link.springer.com/
- ScienceDirect: https://www.sciencedirect.com/
- Wiley: https://onlinelibrary.wiley.com/
- Cambridge Core: https://www.cambridge.org/core/
- AIAA: https://arc.aiaa.org/
- NASA NTRS: https://ntrs.nasa.gov/

Наличие издательской страницы само по себе не доказывает, что книга входит в подписку СПбГУ: доступ нужно подтверждать через университетский каталог/discovery-поиск.

## Короткий маршрут

1. Kreyszig — математика.
2. White — механика жидкости.
3. Anderson — аэродинамика.
4. Moran & Shapiro — термодинамика.
5. Incropera — тепло- и массоперенос.
6. Sutton–Biblarz — ракетные двигатели.
7. Curtis — орбитальная механика.
8. Vallado — практическая астронавтика.
9. Bathe — метод конечных элементов.
10. NASA Systems Engineering Handbook — системная инженерия.
11. Apollo 13 Mission Report и Apollo Experience Reports — аварии и эксплуатация.

## Математика

### Erwin Kreyszig — Advanced Engineering Mathematics

Искать: `Kreyszig Advanced Engineering Mathematics`, Wiley. Заменяет базовый математический study guide: векторный анализ, матрицы, собственные значения, дифференциальные уравнения, Fourier/Laplace, численные методы и вероятность.

### Gilbert Strang — Introduction to Linear Algebra

Искать: `Gilbert Strang Introduction to Linear Algebra`, MIT Press/Wellesley-Cambridge. Для матриц вращения, линейзации динамики, FEM и переходов координат.

### Richard L. Burden, J. Douglas Faires — Numerical Analysis

Искать: `Burden Faires Numerical Analysis`. Для численного интегрирования, ошибок, интерполяции и проверки орбитального интегратора.

## Механика и FEM

### K. J. Bathe — Finite Element Procedures

Искать: `Bathe Finite Element Procedures`. Основной источник по weak form, shape functions, stiffness matrix, динамике, контакту, нелинейности и сходимости FEM.

### O. C. Zienkiewicz, R. L. Taylor, J. Z. Zhu — The Finite Element Method

Искать полное название. Углублённая теория FEM, сеточная ошибка, порядок аппроксимации и сопряжённые поля.

### J. E. Shigley — Mechanical Engineering Design

Для валов, креплений, усталости, концентрации напряжений, подшипников, силовых элементов и коэффициентов запаса.

### S. S. Rao — Mechanical Vibrations

Для собственных частот, форм колебаний, резонанса, демпфирования и динамических нагрузок двигателя.

## Жидкости и аэродинамика

### F. M. White — Fluid Mechanics

Базовый и продвинутый курс: контрольный объём, Navier–Stokes, пограничный слой, отрыв, drag, сжимаемое течение, Reynolds и Mach.

### J. D. Anderson — Fundamentals of Aerodynamics

Для lift, drag, профилей, угла атаки, устойчивости, compressible flow, shock waves, гиперзвукового входа и коэффициентов аппарата.

### G. K. Batchelor — An Introduction to Fluid Dynamics

Теоретическая гидродинамика университетского уровня. Читать после White.

### H. Schlichting, K. Gersten — Boundary-Layer Theory

Пограничный слой, переход, отрыв и зависимость сопротивления от Reynolds, Mach и состояния поверхности.

## Термодинамика, тепло и перенос

### M. J. Moran, H. N. Shapiro — Fundamentals of Engineering Thermodynamics

Первое и второе начало, контрольные объёмы, энтропия, газовые смеси, сопла, турбины и нестационарные процессы.

### F. P. Incropera et al. — Fundamentals of Heat and Mass Transfer

Теплопроводность, конвекция, излучение, переходный нагрев, аэродинамический нагрев, охлаждение камеры и сопла.

### R. B. Bird, W. E. Stewart, E. N. Lightfoot — Transport Phenomena

Продвинутый источник по переносу импульса, тепла и массы: вязкость, диффузия, тепловой поток, массообмен и масштабный анализ. Читать после White и Moran–Shapiro.

## Химия

### P. Atkins, J. de Paula — Physical Chemistry

Термодинамика, равновесие, кинетика, электрохимия, растворы и перенос. Основная замена физико-химическому блоку ChemMod.

### J. M. Smith, H. C. Van Ness, M. M. Abbott, M. T. Swihart — Introduction to Chemical Engineering Thermodynamics

Давление, смеси, фазовое равновесие и инженерные химические процессы.

### R. C. Reid, J. M. Prausnitz, B. E. Poling — The Properties of Gases and Liquids

Справочник свойств газов и жидкостей: плотность, вязкость, теплопроводность, фазовое поведение. Использовать как reference, не читать подряд.

## Ракеты

### G. P. Sutton, O. Biblarz — Rocket Propulsion Elements

Основная книга: жидкие и твёрдые топлива, окислитель, камера, сопло, thrust, specific impulse, turbopumps, pressure-fed systems, ступени и испытания.

### D. K. Huzel, D. H. Huang — Design of Liquid-Propellant Rocket Engines

NASA/SP-125. Углублённая конструкция жидкостного двигателя: подача компонентов, охлаждение, камера, сопло и испытания. NASA NTRS: https://ntrs.nasa.gov/

### T. A. Heppenheimer — Countdown: A History of Space Flight

Исторический и инженерный контекст развития ракет, экспериментов и отказов. Не заменяет расчётный учебник.

## Орбитальная механика

### H. D. Curtis — Orbital Mechanics for Engineering Students

Элементы орбиты, vis-viva, импульсные манёвры, Hohmann transfer, смена плоскости, Lambert, escape и patched conics.

### D. A. Vallado — Fundamentals of Astrodynamics and Applications

Системы координат, время, эфемериды, преобразования состояний, ориентация аппарата и практические алгоритмы. Использовать вместе с Curtis.

### R. H. Battin — An Introduction to the Mathematics and Methods of Astrodynamics

Углублённая математика Lambert, переходов, оптимизации и орбитальных методов.

### H. Schaub, J. L. Junkins — Analytical Mechanics of Space Systems

Угловой момент, кватернионы, attitude dynamics, RCS и управление ориентацией.

## Системная инженерия и аварии

### NASA Systems Engineering Handbook, NASA/SP-2016-6105 Rev 2

https://www.nasa.gov/reference/systems-engineering-handbook/

Требования, decomposition, интерфейсы, verification/validation, управление рисками, trade studies и system safety.

### NASA Apollo Experience Reports

Искать через https://ntrs.nasa.gov/. Особое внимание: propulsion, guidance, environmental control, thermal control, crew systems и mission operations.

### Apollo 13 Mission Report

Case study для отказа кислородного бака, потери энергии, перехода на лунный модуль, ограничения ресурсов, CO2, коррекции траектории и возвращения.

### NASA Fault Tree Handbook with Aerospace Applications

Для деревьев отказов, причин, симптомов, диагностики и объяснимых аварий.

## Таблица замены нашего списка

| Раздел | Основная замена | Дополнение |
|---|---|---|
| Математика | Kreyszig | Strang; Burden & Faires |
| Механика | Shigley | Rao |
| FEM | Bathe | Zienkiewicz–Taylor–Zhu |
| Гидродинамика | White | Batchelor |
| Аэродинамика | Anderson | Schlichting |
| Термодинамика | Moran & Shapiro | Smith–Van Ness |
| Тепло и масса | Incropera | Bird–Stewart–Lightfoot |
| Физическая химия | Atkins | Reid–Prausnitz–Poling |
| Ракеты | Sutton–Biblarz | Huzel–Huang |
| Орбиты | Curtis | Vallado |
| Динамика аппарата | Schaub–Junkins | Battin |
| Системная инженерия | NASA SE Handbook | Wertz/SMAD |
| Отказы | Apollo Reports | Fault Tree Handbook |

## Как подтвердить книгу в СПбГУ

1. Открыть каталог СПбГУ.
2. Искать по автору и полному названию.
3. Повторить поиск по ISBN.
4. Проверить конкретное издание и год.
5. Для печатной книги записать отдел и шифр.
6. Для электронной книги открыть издательскую платформу через университетскую авторизацию.
7. Проверить, доступен ли полный текст, а не только библиографическая запись.
8. Если книга отсутствует, искать другое издание того же автора или университетский аналог.

## Итоговый порядок чтения

Kreyszig → White → Moran & Shapiro → Anderson → Incropera → Bird–Stewart–Lightfoot → Atkins → Sutton–Biblarz → Curtis → Vallado → Schaub–Junkins → Bathe → NASA Systems Engineering Handbook → Apollo reports.

Цель маршрута — уметь объяснить массу и центр масс ракеты, топливо и окислитель, тягу и delta-v, drag и нагрев, давление и температуру, FEM бака, численную ошибку и физический отказ, а также цепочку «симптом → причина → процедура → изменение миссии».
