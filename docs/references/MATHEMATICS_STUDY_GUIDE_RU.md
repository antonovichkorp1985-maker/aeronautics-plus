# Сильная математика для физики, химии и инженерных модов

Статус: обязательное математическое ядро для инженерного справочника, химического мода
и дальнейших физических механик Aeronautics Plus.

Этот документ вынесен отдельно намеренно. Математика не должна быть коротким
«подготовительным параграфом» перед физикой. Без уверенной алгебры, анализа, линейной
алгебры, дифференциальных уравнений, вероятности и численных методов невозможно ни
вывести модель, ни понять её границы, ни проверить код.

> Цель — не формально прочитать как можно больше книг, а научиться ставить задачу,
> выводить уравнения, оценивать масштаб, выбирать численный метод, доказывать хотя бы
> ключевые свойства и проверять результат независимым способом.

Зависимости между этим ядром и физическими, химическими, геологическими,
вычислительными и инженерными ветвями перечислены в общей
[карте научного покрытия](SCIENCE_COVERAGE_MAP_RU.md).

---

## 0. Как пользоваться маршрутом

### 0.1 Уровни

- **Б** — базовый уровень, необходимый до серьёзной физики;
- **С** — инженерный уровень, необходимый для моделей аддона;
- **П** — продвинутый уровень для CFD, FEM, управления, квантовой и ядерной ветвей;
- **R** — справочник, который не требуется читать подряд.

### 0.2 Статус русскоязычных книг

- **[М]** — русская/советская работа с зарубежными переводами, изданиями или
  устойчивым международным научным влиянием;
- **[В]** — сильный отечественный вузовский курс без утверждения, что именно он является
  мировым стандартом;
- **[ПР]** — официальный или профессионально отредактированный русский перевод
  международного учебника;
- **[И]** — исторический источник: идеи важны, но обозначения и вычислительную практику
  следует сверять с современными изданиями.

### 0.3 Правило обучения

Для каждой темы нужны четыре действия:

1. понять определения и геометрический смысл;
2. самостоятельно решить задачи без копирования решения;
3. вывести хотя бы одну используемую в проекте формулу;
4. реализовать вычисление и сравнить с аналитическим ответом или эталонным benchmark.

Видео без задач создаёт узнавание, но не умение. Библиотека, CAS или численный solver
без понимания assumptions создаёт правдоподобное число, но не доказательство.

---

## 1. Какой математический уровень нужен проекту

Разработчик физической механики должен уметь:

- свободно преобразовывать дроби, степени, логарифмы, тригонометрические выражения;
- проверять единицы и порядок величины до подстановки чисел;
- читать и строить графики функций нескольких переменных;
- отличать определение, теорему, модель, аппроксимацию и численный результат;
- работать с векторами, базисами, матрицами, собственными значениями и тензорами;
- дифференцировать и интегрировать функции одной и нескольких переменных;
- применять градиент, дивергенцию, ротор и интегральные теоремы;
- составлять ОДУ из законов сохранения и исследовать устойчивость;
- понимать смысл начальных и граничных условий для PDE;
- применять Fourier/Laplace methods и комплексные числа;
- считать вероятность, математическое ожидание, дисперсию и доверительный интервал;
- понимать conditioning, truncation error, floating-point error и convergence;
- решать нелинейные уравнения и задачи оптимизации с ограничениями;
- отличать correlation от causal model;
- документировать assumptions, valid range и uncertainty.

### 1.1 Что считается недостаточным

- знать формулу, но не уметь проверить её размерность;
- уметь вызвать `solve()` или `odeint()`, но не понимать, что решается;
- подгонять коэффициенты без train/validation split и анализа остатков;
- считать совпадение одного числа в одной точке верификацией модели;
- использовать матрицу вращения без проверки ортогональности и handedness;
- уменьшать timestep до исчезновения видимого бага без convergence study;
- считать, что больше знаков после запятой означает большую физическую точность.

---

## 2. Полная лестница обучения

| Ступень | Темы | Практический результат | Уровень |
|---|---|---|---:|
| 0 | арифметика, дроби, проценты, степени, единицы | надёжный калькулятор SI | Б |
| 1 | алгебра, функции, геометрия, тригонометрия | графики и координатная геометрия | Б |
| 2 | логика, множества, доказательства | корректные определения и инварианты | Б |
| 3 | предел, производная, интеграл, ряды | движение и накопление | Б |
| 4 | многомерный и векторный анализ | поля, потоки, моменты | Б–С |
| 5 | линейная алгебра и аналитическая геометрия | состояния, вращения, моды | Б–С |
| 6 | ОДУ и динамические системы | нестационарная физика | С |
| 7 | комплексные числа, Fourier/Laplace | колебания, цепи, спектры | С |
| 8 | вероятность, статистика, погрешность | измерения и случайные процессы | С |
| 9 | PDE и математическая физика | тепло, поток, волны, диффузия | С–П |
| 10 | численные методы | устойчивый solver и convergence | С–П |
| 11 | оптимизация и управление | калибровка, PID/state-space | С–П |
| 12 | тензоры, вариационные методы, функциональный анализ | сплошные среды, FEM, quantum | П |
| 13 | специализированные ветви | CFD, kinetics, reactor physics | П |

Переходить дальше можно параллельно, но нельзя пропускать алгебру, функции, анализ,
линейную алгебру и ОДУ. Именно эти пробелы чаще всего маскируются библиотечным кодом.

---

## 3. Ступень 0–1: арифметика, алгебра, геометрия и тригонометрия

### 3.1 Обязательные темы

- целые, рациональные, действительные и комплексные числа;
- дроби, отношения, пропорции и проценты;
- степени, корни, экспоненты и логарифмы;
- раскрытие скобок, факторизация и преобразование выражений;
- линейные, квадратные и простые нелинейные уравнения;
- неравенства и интервалы;
- функции, domain/range, inverse и composition;
- координатная геометрия;
- подобие, площади и объёмы;
- радианная мера;
- `sin`, `cos`, `tan`, identities и inverse trigonometric functions;
- закон синусов и закон косинусов;
- графики и asymptotic behavior;
- scientific notation и оценка порядка величины;
- единицы и размерности.

### 3.2 Открытые источники

- OpenStax Algebra and Trigonometry 2e:
  https://openstax.org/details/books/algebra-and-trigonometry-2e
- OpenStax Precalculus 2e:
  https://openstax.org/details/books/precalculus-2e
- Khan Academy Math: https://www.khanacademy.org/math
- 3Blue1Brown: https://www.3blue1brown.com/

### 3.3 Контрольные умения

- выразить любую переменную из инженерной формулы;
- перевести RPM в `rad/s` и обратно;
- восстановить катеты и угол по неполному набору данных;
- построить log-log график и определить показатель степени;
- оценить ответ до калькулятора;
- обнаружить невозможный результат по знаку, диапазону или единице.

---

## 4. Ступень 2: логика, множества и доказательства

Сильная физика требует не только счёта. Нужно понимать, какие утверждения следуют из
модели, а какие были незаметно добавлены.

### 4.1 Темы

- proposition, implication, equivalence и quantifiers;
- necessary и sufficient conditions;
- множества, отображения, отношения и equivalence classes;
- injective/surjective/bijective mappings;
- direct proof, contradiction, contrapositive и induction;
- counterexample;
- определение и теорема;
- existence, uniqueness и stability как разные вопросы;
- invariant;
- dimensional и limiting arguments как инженерные доказательства.

### 4.2 Книги

- Д. Веллеман, «Как доказывать теоремы» / Daniel Velleman, *How to Prove It*;
- Р. Хэммэк, *Book of Proof* — легально открытый вводный текст:
  https://richardhammack.github.io/BookOfProof/
- Р. Курант, Г. Роббинс, «Что такое математика?» **[ПР, Б–С]**
- И. М. Гельфанд и соавторы, школьно-университетская серия по алгебре, функциям и
  координатам — для содержательного перехода от вычислений к структуре. **[М/В, Б]**

### 4.3 Для кода

Каждый физический модуль должен явно иметь:

- входной domain;
- инварианты;
- preconditions/postconditions;
- единицы;
- обработку вырожденного случая;
- тест контрпримера;
- тест предельного перехода.

---

## 5. Ступень 3: математический анализ одной переменной

### 5.1 Темы

- предел и непрерывность;
- производная как локальная линейная аппроксимация;
- product/chain/implicit differentiation;
- extrema и convexity;
- Taylor series и remainder;
- определённый интеграл и Fundamental Theorem of Calculus;
- improper integrals;
- sequences и series;
- convergence tests;
- parametric curves;
- простые differential models.

### 5.2 Источники

- MIT 18.01SC Single Variable Calculus:
  https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/
- OpenStax Calculus, volumes 1–3:
  https://openstax.org/details/books/calculus-volume-1
- 3Blue1Brown, Essence of Calculus:
  https://www.3blue1brown.com/topics/calculus
- Томас, Вейр, Хасс, «Математический анализ» **[ПР, Б–С]**;
- Т. Апостол, «Математический анализ», т. 1–2 **[ПР, С]**;
- В. А. Зорич, «Математический анализ», т. 1–2 **[М, С–П]**;
- Г. М. Фихтенгольц, «Курс дифференциального и интегрального исчисления», т. 1–3
  **[В, С–П]**;
- Б. П. Демидович, «Сборник задач и упражнений по математическому анализу» — практика,
  не замена теории. **[М/В, Б–С]**

Международное издание Зорича:
https://link.springer.com/book/10.1007/978-3-662-48792-1

### 5.3 Формулы, которые нужно уметь вывести

Локальная линейная аппроксимация:

`f(x + Δx) = f(x) + f'(x)Δx + O(Δx²)`

Taylor:

`f(x) = Σ_(k=0)^n f^(k)(a)(x-a)^k/k! + R_n`

Среднее значение функции:

`f_avg = (1/(b-a)) ∫_a^b f(x) dx`

Работа переменной силы:

`W = ∫ F(x) dx`

Нужно понимать не только запись, но и regularity assumptions, units и remainder.

---

## 6. Ступень 4: многомерный и векторный анализ

### 6.1 Темы

- функции нескольких переменных;
- partial и directional derivatives;
- gradient, Jacobian и Hessian;
- multiple integrals и change of variables;
- line/surface/volume integrals;
- vector fields;
- divergence и curl;
- Green, Gauss и Stokes theorems;
- curvilinear coordinates;
- differential forms как продвинутая ветвь;
- constrained extrema.

### 6.2 Источники

- MIT 18.02SC Multivariable Calculus:
  https://ocw.mit.edu/courses/18-02sc-multivariable-calculus-fall-2010/
- OpenStax Calculus, volume 3:
  https://openstax.org/details/books/calculus-volume-3
- Marsden & Tromba, *Vector Calculus*;
- Schey, *Div, Grad, Curl, and All That*;
- Зорич, т. 2; Ильин и Позняк, «Основы математического анализа» **[В, С]**.

### 6.3 Физический смысл

`∇f` — направление наибольшего роста scalar field.

`∇·F` — локальный source/sink balance.

`∇×F` — локальная циркуляция.

Gauss:

`∫_V ∇·F dV = ∮_∂V F·n dS`

Stokes:

`∫_S (∇×F)·n dS = ∮_∂S F·dl`

Это язык сохранения массы, импульса, энергии, электрического заряда и magnetic flux.

---

## 7. Ступень 5: линейная алгебра и геометрия

### 7.1 Темы

- vectors, vector spaces и subspaces;
- basis, coordinates, dimension и rank;
- linear maps и matrices;
- Gaussian elimination;
- determinant как oriented volume scale, а не только формула;
- eigenvalues/eigenvectors;
- diagonalization и Jordan form как углубление;
- inner products, orthogonality и projections;
- least squares;
- symmetric/Hermitian и positive-definite matrices;
- singular value decomposition;
- condition number;
- rotations, homogeneous transforms и quaternions;
- sparse matrices.

### 7.2 Источники

- MIT 18.06SC Linear Algebra, Gilbert Strang:
  https://ocw.mit.edu/courses/18-06sc-linear-algebra-fall-2011/
- 3Blue1Brown, Essence of Linear Algebra:
  https://www.3blue1brown.com/topics/linear-algebra
- Strang, *Introduction to Linear Algebra*;
- Axler, *Linear Algebra Done Right*;
- Trefethen & Bau, *Numerical Linear Algebra*;
- И. Р. Шафаревич, А. О. Ремизов, «Линейная алгебра и геометрия» **[М/В, С]**;
- Д. В. Беклемишев, «Курс аналитической геометрии и линейной алгебры» **[В, Б–С]**;
- Ф. Р. Гантмахер, «Теория матриц», т. 1–2 **[М, П/R]**.

AMS продолжает международное издание Гантмахера:
https://bookstore.ams.org/chel-131/

### 7.3 Для роторов и 3D-механики

Матрица вращения должна удовлетворять:

`RᵀR = I`, `det(R) = +1`

Преобразование вектора:

`v_world = R v_local`

Угловая динамика твёрдого тела в body-fixed frame, где `I` постоянно:

`τ = I ωdot + ω × (Iω)`

Здесь `I` — тензор инерции в согласованном базисе. В inertial frame исходной записью
служит `τ = dL/dt`. Ошибка оси вращения — обычно ошибка
базиса, порядка преобразований или local/world frame, а не «неправильная анимация».

---

## 8. Ступень 6: ОДУ и динамические системы

### 8.1 Темы

- first-order ODE;
- separable и linear equations;
- systems of ODE;
- existence/uniqueness;
- initial-value problem;
- phase portrait;
- equilibria и linearization;
- eigenvalue stability;
- forced/damped oscillations;
- resonance;
- nonlinear dynamics и bifurcations;
- stiff systems;
- conservation laws;
- state-space representation.

### 8.2 Источники

- MIT 18.03SC Differential Equations:
  https://ocw.mit.edu/courses/18-03sc-differential-equations-fall-2011/
- MIT OCW search, Dynamical Systems:
  https://ocw.mit.edu/search/?q=dynamical%20systems
- Strogatz, *Nonlinear Dynamics and Chaos*;
- Boyce, DiPrima & Meade, *Elementary Differential Equations*;
- В. И. Арнольд, «Обыкновенные дифференциальные уравнения» **[М, С–П]**;
- Л. С. Понтрягин, «Обыкновенные дифференциальные уравнения» **[М, П]**;
- Л. Э. Эльсгольц, «Дифференциальные уравнения и вариационное исчисление»
  **[М/В, С–П]**.

### 8.3 Базовая запись

`xdot = f(t, x, u, p)`

Linearization около равновесия:

`δxdot = A δx + B δu`

где `A = ∂f/∂x`, `B = ∂f/∂u` в рабочей точке.

Для continuous linear system асимптотическая устойчивость требует отрицательных real
parts всех собственных значений `A`. Для discrete map соответствующий критерий —
собственные значения внутри unit circle. Для нелинейной системы вывод по linearization
локален и не является глобальной гарантией.

---

## 9. Ступень 7: комплексные числа, Fourier и Laplace

### 9.1 Комплексные числа

- algebraic/polar/exponential forms;
- Euler identity;
- roots и branches;
- analytic functions;
- Cauchy–Riemann conditions;
- contour integrals и residues как углубление;
- conformal mapping для potential flow.

`e^(iθ) = cos θ + i sin θ`

Комплексное представление не делает физическую величину «мнимой»: оно компактно хранит
амплитуду и фазу.

### 9.2 Fourier/Laplace

- orthogonal functions;
- Fourier series и transform;
- convolution;
- Parseval identity;
- sampling и aliasing;
- discrete Fourier transform;
- Laplace transform;
- poles, zeros и transfer functions;
- frequency response.

Fourier transform convention должна быть зафиксирована: разные библиотеки по-разному
располагают `2π` и знаки.

### 9.3 Источники

- MIT 18.03 и MIT OCW search по Fourier:
  https://ocw.mit.edu/search/?q=Fourier
- MIT 18.04 Complex Variables with Applications:
  https://ocw.mit.edu/courses/18-04-complex-variables-with-applications-fall-1999/
- Ahlfors, *Complex Analysis* / «Комплексный анализ» **[ПР, С–П]**;
- Churchill & Brown, *Complex Variables and Applications*;
- Bracewell, *The Fourier Transform and Its Applications*;
- R. N. Bracewell или Oppenheim & Willsky для signals;
- В. И. Смирнов, «Курс высшей математики», соответствующие тома **[М/В, С–П]**.

---

## 10. Ступень 8: вероятность, статистика и неопределённость

### 10.1 Темы

- sample space, events и probability axioms;
- conditional probability и Bayes theorem;
- random variables;
- common distributions;
- expectation, variance, covariance и correlation;
- law of large numbers и central limit theorem;
- estimators, bias и variance;
- confidence intervals;
- hypothesis tests и multiple comparisons;
- linear/nonlinear regression;
- residual analysis;
- Bayesian update;
- stochastic processes, Markov chains и Poisson process;
- Monte Carlo;
- measurement uncertainty и error propagation.

### 10.2 Источники

- Harvard Stat 110: https://stat110.hsites.harvard.edu/
- MIT 6.041SC Probabilistic Systems Analysis:
  https://ocw.mit.edu/courses/6-041sc-probabilistic-systems-analysis-and-applied-probability-fall-2013/
- Seeing Theory, Brown University: https://seeing-theory.brown.edu/
- NIST/SEMATECH e-Handbook:
  https://www.itl.nist.gov/div898/handbook/
- JCGM GUM и VIM:
  https://www.bipm.org/en/committees/jc/jcgm/publications
- Blitzstein & Hwang, *Introduction to Probability*;
- Casella & Berger, *Statistical Inference*;
- W. Feller, «Введение в теорию вероятностей и её приложения» **[ПР, С–П]**;
- А. Н. Колмогоров, «Основные понятия теории вероятностей» **[М, П/И]**;
- Б. В. Гнеденко, «Курс теории вероятностей» **[М, С–П]**;
- А. Н. Ширяев, «Вероятность» **[М, П]**.

### 10.3 Формулы

Bayes:

`P(A|B) = P(B|A)P(A)/P(B)`

Expectation и variance:

`E[X] = ∫x p(x)dx`

`Var(X) = E[(X-E[X])²]`

First-order propagation при covariance matrix `Σ_x`:

`Σ_y ≈ J Σ_x Jᵀ`

где `J` — Jacobian функции измерения. Складывать все проценты линейно обычно неверно.

---

## 11. Ступень 9: PDE и математическая физика

### 11.1 Темы

- formulation: domain, unknown field, PDE, initial/boundary conditions;
- conservation form;
- first/second-order classification;
- elliptic, parabolic и hyperbolic behavior;
- separation of variables;
- eigenfunction expansions;
- Green functions;
- characteristics;
- diffusion/heat equation;
- wave equation;
- Laplace/Poisson equation;
- advection и advection–diffusion;
- Navier–Stokes structure;
- Maxwell equations structure;
- transport и reaction–diffusion;
- weak solution и distributions;
- well-posedness.

### 11.2 Канонические уравнения

Diffusion/heat:

`∂u/∂t = α∇²u`

Wave:

`∂²u/∂t² = c²∇²u`

Poisson:

`∇²u = f`

Advection–diffusion–reaction:

`∂c/∂t + v·∇c = ∇·(D∇c) + R(c)`

Уравнение без граничных и начальных условий ещё не является полной задачей.

### 11.3 Источники

- MIT OCW search, Partial Differential Equations:
  https://ocw.mit.edu/search/?q=partial%20differential%20equations
- Strauss, *Partial Differential Equations: An Introduction*;
- Haberman, *Applied Partial Differential Equations*;
- Evans, *Partial Differential Equations* — продвинутый строгий курс;
- Courant & Hilbert, «Методы математической физики» **[ПР, П]**;
- А. Н. Тихонов, А. А. Самарский, «Уравнения математической физики» **[М, П]**;
- В. С. Владимиров, «Уравнения математической физики» **[М, П]**.

Международное издание Тихонова—Самарского:
https://books.google.com/books/about/Equations_of_Mathematical_Physics.html?id=PTmoAAAAQBAJ

---

## 12. Ступень 10: численные методы

### 12.1 Что обязательно понимать

- floating-point representation и catastrophic cancellation;
- absolute/relative error;
- truncation, discretization и roundoff errors;
- conditioning задачи против stability алгоритма;
- convergence order;
- root finding;
- interpolation и approximation;
- numerical differentiation/integration;
- direct/iterative linear solvers;
- eigenvalue algorithms;
- time integration;
- stiffness;
- finite difference, finite volume и finite element идеи;
- mesh/time-step refinement;
- conservation и monotonicity;
- verification против validation.

### 12.2 Источники

- MIT 2.086 Numerical Computation for Mechanical Engineers:
  https://ocw.mit.edu/courses/2-086-numerical-computation-for-mechanical-engineers-fall-2014/
- MIT 18.085 Computational Science and Engineering I:
  https://ocw.mit.edu/courses/18-085-computational-science-and-engineering-i-fall-2008/
- Trefethen & Bau, *Numerical Linear Algebra*;
- Higham, *Accuracy and Stability of Numerical Algorithms*;
- Sauer, *Numerical Analysis*;
- LeVeque, *Finite Difference Methods for ODE and PDE*;
- Ferziger, Perić & Street, *Computational Methods for Fluid Dynamics*;
- А. А. Самарский, «Теория разностных схем» **[М, П]**;
- С. К. Годунов, В. С. Рябенький, «Разностные схемы» **[М, П]**;
- Н. Н. Калиткин, «Численные методы» **[В, С–П]**;
- Н. С. Бахвалов, Н. П. Жидков, Г. М. Кобельков, «Численные методы» **[В, С–П]**.

### 12.3 Минимальные алгоритмы

Bisection — медленно, но надёжно при bracket и смене знака.

Newton:

`x_(k+1) = x_k - f(x_k)/f'(x_k)`

Euler:

`x_(n+1) = x_n + Δt f(t_n, x_n)`

Euler нужен для понимания, но редко является хорошим production integrator.

Для explicit diffusion scheme типичное ограничение timestep связано с mesh size как
`Δt = O(Δx²)`. Для advection появляется CFL condition. Точный коэффициент зависит от
схемы, размерности и уравнения; его нельзя копировать без вывода.

### 12.4 Обязательный convergence test

Рассчитать задачу при `h`, `h/2`, `h/4` и сравнить:

- solution norm;
- conservation residual;
- observed order;
- runtime/memory;
- изменение игрового решения, а не только последнего знака числа.

---

## 13. Ступень 11: оптимизация, идентификация и управление

### 13.1 Оптимизация

- objective, variables и constraints;
- local/global optimum;
- convex sets/functions;
- Lagrange multipliers и KKT conditions;
- gradient/Newton/quasi-Newton;
- least squares;
- regularization;
- linear/quadratic programming;
- constrained nonlinear optimization;
- sensitivity и identifiability;
- multi-objective Pareto trade-off.

### 13.2 Источники

- Boyd & Vandenberghe, *Convex Optimization* — официальный открытый текст:
  https://web.stanford.edu/~boyd/cvxbook/
- Nocedal & Wright, *Numerical Optimization*;
- Bertsekas, *Nonlinear Programming*;
- Л. В. Канторович, математические методы организации и планирования производства —
  историческая основа linear programming. **[М, И–П]**
- Б. Т. Поляк, «Введение в оптимизацию» **[М/В, С–П]**.

### 13.3 Управление

- transfer function и state space;
- controllability/observability;
- feedback/feedforward;
- PID;
- root locus, Bode и Nyquist;
- discrete-time models;
- state estimation/Kalman filter;
- nonlinear control как углубление;
- saturation, delay и noise.

Для tuning по данным сначала нужна идентификация модели и uncertainty. Нельзя объявлять
любой коэффициент PID «физической константой».

---

## 14. Ступень 12: тензоры, вариационные методы и функциональный анализ

### 14.1 Тензоры

- index notation и Einstein summation;
- coordinate transformation;
- metric и dual space;
- symmetric/antisymmetric parts;
- stress/strain tensors;
- inertia tensor;
- tensor invariants;
- principal values/directions;
- covariant derivative как продвинутая ветвь.

Transformation second-order tensor:

`T' = R T Rᵀ`

Компоненты зависят от базиса, физический тензор — нет.

### 14.2 Вариационное исчисление

Functional:

`J[y] = ∫ F(x, y, y') dx`

Euler–Lagrange:

`∂F/∂y - d/dx(∂F/∂y') = 0`

Применения:

- least action;
- geodesics;
- minimum potential energy;
- weak formulation PDE;
- finite element method;
- constrained fields.

### 14.3 Функциональный анализ

- metric, normed, Banach и Hilbert spaces;
- linear operators;
- orthogonality и projection;
- distributions;
- compactness и convergence notions;
- spectral ideas;
- weak derivatives и Sobolev spaces.

Не нужно проходить полный строгий курс до первого прототипа, но эти понятия нужны для
понимания FEM, quantum mechanics и корректности PDE.

### 14.4 Русские работы международного уровня

- И. М. Гельфанд, С. В. Фомин, «Вариационное исчисление» **[М, С–П]**;
- А. Н. Колмогоров, С. В. Фомин, «Элементы теории функций и функционального анализа»
  **[М, П]**;
- Л. В. Канторович, Г. П. Акилов, «Функциональный анализ» **[М, П]**;
- С. Л. Соболев, работы и курсы по функциональному анализу и уравнениям математической
  физики **[М, П]**;
- В. И. Арнольд, «Математические методы классической механики» **[М, П]**.

Проверяемые зарубежные издания:

- Колмогоров—Фомин, Dover:
  https://store.doverpublications.com/products/9780486406831
- Гельфанд—Фомин, Dover:
  https://books.google.com/books/about/Calculus_of_Variations.html?id=CeC7AQAAQBAJ
- Арнольд, Springer, DOI:
  https://doi.org/10.1007/978-1-4757-2063-1

---

## 15. Размерностный, асимптотический и perturbation-анализ

### 15.1 Размерностный анализ

- base dimensions;
- dimensionally homogeneous equations;
- Buckingham Pi theorem;
- similarity;
- scaling;
- nondimensionalization;
- dominant balance.

Если задача имеет `n` dimensional variables и `k` независимых основных размерностей,
обычно можно построить `n-k` независимых dimensionless groups.

Для потоков особенно важны:

- Reynolds;
- Mach;
- Froude;
- Strouhal;
- Prandtl;
- Nusselt;
- Peclet;
- Weber;
- advance ratio и thrust/power coefficients.

### 15.2 Асимптотика

- big-O/little-o;
- regular и singular perturbations;
- boundary layers;
- matched asymptotic expansions;
- multiple scales;
- linearization и small-parameter ordering.

### 15.3 Источники

- Л. И. Седов, «Методы подобия и размерности в механике» **[М, С–П]**;
- Barenblatt, *Scaling* и «Подобие, автомодельность, промежуточная асимптотика»
  **[М, П]**;
- Bender & Orszag, *Advanced Mathematical Methods for Scientists and Engineers*;
- Hinch, *Perturbation Methods*;
- Holmes, *Introduction to Perturbation Methods*.

Dimensionless formulation часто надёжнее абсолютной: она показывает режим задачи и
помогает переносить результат между Minecraft-моделью, экспериментом и реальным
аппаратом.

---

## 16. Вычислительные инструменты

### 16.1 Основной стек

| Инструмент | Роль | Ссылка |
|---|---|---|
| Python | воспроизводимые расчёты | https://www.python.org/ |
| NumPy | arrays и linear algebra | https://numpy.org/ |
| SciPy | solvers, ODE, optimization | https://scipy.org/ |
| SymPy | symbolic checks | https://www.sympy.org/ |
| Pint | units | https://pint.readthedocs.io/ |
| pandas | data/ETL | https://pandas.pydata.org/ |
| Matplotlib | plots | https://matplotlib.org/ |
| Jupyter | notebooks | https://jupyter.org/ |
| Julia | high-performance numerical work | https://julialang.org/ |
| GNU Octave | matrix-oriented calculations | https://octave.org/ |
| SageMath | open CAS | https://www.sagemath.org/ |
| NIST DLMF | special functions reference | https://dlmf.nist.gov/ |

### 16.2 Продвинутые инструменты

- PETSc: https://petsc.org/
- FEniCSx: https://fenicsproject.org/
- Gmsh: https://gmsh.info/
- SUNDIALS: https://computing.llnl.gov/projects/sundials
- JAX: https://docs.jax.dev/
- CVXPY: https://www.cvxpy.org/

### 16.3 Правила

- фиксировать версии;
- писать единицы рядом с типом данных;
- задавать random seed там, где нужна воспроизводимость;
- не принимать default tolerance без оценки масштаба;
- сохранять solver status и residual;
- проверять решение другим методом хотя бы на эталонных случаях;
- отделять notebook исследования от production implementation;
- хранить benchmark и expected tolerance в CI.

---

## 17. Сильная русскоязычная математическая полка

### 17.1 От основ к анализу

- **А. П. Киселёв, «Геометрия»** — строгая элементарная геометрия. **[В, И–Б]**
- **М. Я. Выгодский, «Справочник по элементарной математике»** — справочник, не
  линейный курс. **[В, R]**
- **И. Н. Бронштейн, К. А. Семендяев, «Справочник по математике для инженеров и
  учащихся втузов»** — многократно переводившийся международный справочник.
  **[М, R]**
- **Н. С. Пискунов, «Дифференциальное и интегральное исчисление», т. 1–2** —
  международно переводившийся инженерный курс. **[М/В, Б–С]**
- **Г. М. Фихтенгольц, «Курс дифференциального и интегрального исчисления», т. 1–3** —
  подробный строгий классический курс. **[В, С–П]**
- **В. А. Зорич, «Математический анализ», т. 1–2** — современный строгий курс,
  изданный Springer на английском. **[М, С–П]**
- **Б. П. Демидович, «Сборник задач и упражнений по математическому анализу»** —
  большой тренажёр после теории. **[М/В, Б–С]**

### 17.2 Алгебра, матрицы и геометрия

- **А. И. Кострикин, «Введение в алгебру»** — сильный университетский курс.
  **[М/В, С–П]**
- **А. Г. Курош, «Курс высшей алгебры»** — классика с международными переводами.
  **[М, С–П]**
- **Ф. Р. Гантмахер, «Теория матриц»** — матрицы, спектр, устойчивость и приложения;
  продолжает издаваться AMS. **[М, П/R]**
- **Д. В. Беклемишев, «Курс аналитической геометрии и линейной алгебры»**.
  **[В, Б–С]**

### 17.3 Дифференциальные уравнения и математическая физика

- **В. И. Арнольд, «Обыкновенные дифференциальные уравнения»** **[М, С–П]**;
- **Л. С. Понтрягин, «Обыкновенные дифференциальные уравнения»** **[М, П]**;
- **А. Н. Тихонов, А. А. Самарский, «Уравнения математической физики»** **[М, П]**;
- **В. С. Владимиров, «Уравнения математической физики»** **[М, П]**;
- **И. Г. Петровский, «Лекции об уравнениях с частными производными»** **[М, П]**;
- **В. И. Смирнов, «Курс высшей математики», т. 1–5** — большой справочный мост к
  математической физике. **[М/В, С–П/R]**.

### 17.4 Вероятность, функции и вариационные методы

- **А. Н. Колмогоров, «Основные понятия теории вероятностей»** **[М, П/И]**;
- **Б. В. Гнеденко, «Курс теории вероятностей»** **[М, С–П]**;
- **А. Н. Ширяев, «Вероятность»** **[М, П]**;
- **Колмогоров—Фомин, «Элементы теории функций и функционального анализа»**
  **[М, П]**;
- **Гельфанд—Фомин, «Вариационное исчисление»** **[М, С–П]**;
- **Канторович—Акилов, «Функциональный анализ»** **[М, П]**.

### 17.5 Численные методы

- **Самарский, «Теория разностных схем»** **[М, П]**;
- **Годунов—Рябенький, «Разностные схемы»** **[М, П]**;
- **Калиткин, «Численные методы»** **[В, С–П]**;
- **Бахвалов—Жидков—Кобельков, «Численные методы»** **[В, С–П]**.

### 17.6 Русские переводы международной классики

- Курант—Роббинс, «Что такое математика?» **[ПР, Б]**;
- Томас, «Математический анализ» **[ПР, Б–С]**;
- Апостол, «Математический анализ», т. 1–2 **[ПР, С]**;
- Рудин, «Основы математического анализа» и «Функциональный анализ» **[ПР, С–П]**;
- Стрэнг, «Линейная алгебра и её применения» **[ПР, Б–С]**;
- Курант—Гильберт, «Методы математической физики» **[ПР, П]**;
- Альфорс, «Комплексный анализ» **[ПР, С–П]**;
- Феллер, «Введение в теорию вероятностей и её приложения» **[ПР, С–П]**;
- Пресс, Тьюкольски, Веттерлинг, Фланнери, «Численные рецепты» — исторически важный
  практический справочник; algorithms сверять с современными библиотеками. **[ПР, R]**.

Наличие русского перевода не отменяет проверки номера издания и errata оригинала.

---

## 18. Математика по физическим направлениям

### 18.1 Механика и роторы

Обязательны:

- векторы и системы координат;
- матрицы вращения/quaternions;
- eigenvalues и inertia tensor;
- ОДУ и устойчивость;
- Fourier analysis колебаний;
- numerical integration;
- optimization;
- uncertainty.

Для соосного ротора дополнительно:

- nonlinear coupled systems;
- dimensionless coefficients;
- interpolation response maps;
- induced-velocity iteration;
- Jacobian/sensitivity;
- phase и counter-rotation conventions;
- conservation of power/momentum;
- convergence по timestep и radial discretization.

### 18.2 Аэродинамика, гидродинамика и суда

- vector calculus;
- PDE и conservation form;
- complex variables для potential flow;
- tensors;
- asymptotics/boundary layers;
- numerical linear algebra;
- finite volume/FEM;
- probability для waves/turbulence;
- dimensional analysis.

### 18.3 Термодинамика и теплопередача

- multivariable calculus;
- total/exact differentials;
- Jacobians и Legendre transforms;
- constrained extrema;
- PDE diffusion;
- nonlinear equations;
- interpolation с valid range;
- uncertainty propagation.

### 18.4 Химия и химическая технология

- stoichiometric matrices и null spaces;
- nonlinear equilibrium;
- logarithms/activity scales;
- stiff ODE kinetics;
- Jacobians и sensitivity;
- transport PDE;
- regression и experimental design;
- constrained optimization;
- process control;
- graph/network models для reaction paths.

### 18.5 Материаловедение

- tensors stress/strain;
- eigenvalues principal stresses;
- diffusion PDE;
- phase-field ideas;
- probability of failure;
- interpolation/thermodynamic optimization;
- FEM;
- inverse problems.

### 18.6 Электричество и электромагнетизм

- complex numbers/phasors;
- vector calculus;
- linear systems;
- ODE circuits;
- Maxwell PDE;
- Fourier/Laplace;
- control and signal processing;
- sparse network matrices.

### 18.7 Квантовая, статистическая и ядерная физика

- linear algebra over complex field;
- probability;
- PDE/eigenvalue problems;
- operators и Hilbert spaces;
- Fourier transform;
- statistics/Monte Carlo;
- stiff decay networks;
- transport equations;
- numerical linear algebra;
- uncertainty и evaluated-data covariance.

---

## 19. Минимальные математические проверки игровых моделей

### 19.1 Размерность

Каждый additive term имеет одинаковую размерность. Например, в

`m dv/dt = ΣF`

обе стороны имеют единицу силы.

### 19.2 Знак и ориентация

- right-handed coordinate system зафиксирована;
- local/world transforms протестированы;
- positive RPM и thrust direction документированы;
- counter-rotation меняет torque sign, но не обязана менять thrust sign;
- normal vectors нормированы.

### 19.3 Предельные случаи

Проверить:

- zero input;
- малую скорость;
- симметрию;
- удвоение масштаба;
- очень большую массу/inertia;
- отсутствие drag/loss;
- steady state;
- выключение coupling;
- identical upper/lower rotors.

### 19.4 Сохранение

Контролировать residual:

- mass;
- atom/charge;
- linear/angular momentum;
- energy;
- probability normalization;
- phase/material fractions.

### 19.5 Численная сходимость

- timestep refinement;
- mesh/table refinement;
- tolerance sensitivity;
- comparison solver;
- analytical benchmark;
- long-run drift;
- save/load reproducibility.

---

## 20. Практические проекты

### Проект 1. SI-калькулятор с анализом размерностей

Поддержать base/derived units, affine temperature units и invalid-operation errors.

**Готово, если:** случайные round trips сохраняют значение и размерность.

### Проект 2. Графики и безразмерные группы

Построить linear, semilog и log-log графики; извлечь power-law exponent; собрать
Reynolds/Mach/Froude и объяснить режим.

**Готово, если:** slope и группы воспроизводятся на синтетическом эталоне.

### Проект 3. Вращения в 3D

Реализовать matrix/quaternion rotation, composition и inverse.

**Готово, если:** `RᵀR=I`, `det R=1`, длина вектора сохраняется, local shaft axis точно
совпадает с world transform.

### Проект 4. Линейная динамическая система

Пружина–масса–демпфер или rotor inertia + load.

**Готово, если:** numerical solution совпадает с analytical regime и показывает
правильные under/critical/over-damped limits.

### Проект 5. Нелинейная ОДУ

Реализовать ограниченный привод или batch kinetics с несколькими integrators.

**Готово, если:** выполнены timestep study, bounds и conservation.

### Проект 6. Fourier-анализ

Синтетический сигнал из известных частот, noise и sampling.

**Готово, если:** частоты/амплитуды найдены, а aliasing продемонстрирован и объяснён.

### Проект 7. Least squares и uncertainty

Fit response curve с train/validation split, residual plot и confidence interval.

**Готово, если:** код отличает measurement noise от model bias.

### Проект 8. Diffusion PDE

1D heat/diffusion equation analytic + finite difference.

**Готово, если:** наблюдается ожидаемый convergence order и energy/mass behavior.

### Проект 9. Constrained optimization

Подобрать размер/режим при ограничениях power, stress, temperature и noise.

**Готово, если:** constraints проверены независимо, sensitivity описана.

### Проект 10. Coupled coaxial rotor toy model

Не production-аэродинамика, а математический прототип:

- два rotor states;
- opposite angular velocities;
- shared induced-flow variable;
- torque/power balance;
- fixed-point или root solve;
- convergence diagnostics.

**Готово, если:** decoupled limit воспроизводит одиночные роторы, symmetric case проходит,
а solver честно сообщает о несходимости.

---

## 21. Рекомендуемый темп

### Фаза A — 12–16 недель

- алгебра, тригонометрия, функции;
- единицы и графики;
- single-variable calculus;
- elementary proofs;
- 150–250 задач.

### Фаза B — 12–16 недель

- multivariable/vector calculus;
- linear algebra;
- first ODE;
- Python/NumPy/SciPy;
- проекты 1–5.

### Фаза C — 12–20 недель

- Fourier/Laplace/complex;
- probability/statistics;
- numerical analysis;
- optimization/control;
- проекты 6–9.

### Фаза D — постоянно

- PDE;
- tensors;
- variational/functional analysis;
- domain-specific mathematics;
- project 10 и production benchmarks.

Лучше ежедневно решать 60–90 минут задач, чем раз в неделю смотреть много часов лекций.
Скорость прохождения не важнее способности самостоятельно решить незнакомую задачу.

---

## 22. Критерии готовности перед сильной физической моделью

Разработчик готов к серьёзной реализации, если он может без подсказки:

1. определить переменные, единицы и систему координат;
2. вывести хотя бы lumped-модель из conservation law;
3. получить безразмерную форму;
4. найти steady state и исследовать локальную устойчивость;
5. выбрать solver, объяснить tolerance и failure mode;
6. провести timestep/mesh refinement;
7. построить residual и uncertainty;
8. проверить минимум два предельных случая;
9. сравнить с независимым benchmark;
10. объяснить, какая математика в модели отброшена и почему это допустимо для игры.

Если любой из пунктов отсутствует, правильная реакция — упростить модель и доучить
математику, а не скрыть проблему дополнительным коэффициентом.

---

## Итоговый принцип

Сильная математика для этого проекта — не стремление превратить Minecraft в диссертацию.
Это способ делать осмысленные упрощения:

`определения → законы сохранения → уравнения → безразмерная форма → численный метод →
верификация → экспериментальные данные → игровое упрощение`.

Только после этой цепочки коэффициент в коде становится инженерной моделью, а не
случайно правдоподобным числом.
