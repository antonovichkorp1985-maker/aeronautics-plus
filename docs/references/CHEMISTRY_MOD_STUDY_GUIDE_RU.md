# Учебно-проектный справочник по химическому и материаловедческому моду

Статус: рабочая база для проектирования отдельного химико-технологического мода и его
интеграций со сборкой. Документ хранится в этом репозитории как исследовательская база,
но сам по себе не требует встраивать всю химию в авиационный аддон Aeronautics Plus.

Документ рассчитан на разработчика, который начинает почти с нуля, но хочет прийти не к
набору «магических рецептов», а к проверяемой модели вещества, энергии, оборудования,
риска и качества продукта.

> Ядерная часть этого справочника посвящена фундаментальной физике, измерениям,
> радиохимии, радиационной защите, гражданской энергетике, топливному циклу и отходам.
> Проектирование оружия, получение оружейных материалов и соответствующие практические
> инструкции сюда не входят.

---

## 0. Как пользоваться справочником

### 0.1 Обозначения уровня

- **Б** — база: пройти обязательно до реализации механики;
- **С** — средний уровень: нужен для правдоподобной игровой модели;
- **П** — продвинутый уровень: нужен при глубоком моделировании;
- **R** — reference: справочник или база данных, а не линейный учебник.

### 0.2 Обозначения русскоязычных книг

- **[М]** — у оригинальной русской/советской работы есть проверяемое международное
  влияние: зарубежные переводы, издания или признанная научная школа;
- **[В]** — сильный отечественный вузовский источник; полезен, но не выдаётся за
  автоматически «признанный всем миром»;
- **[ПР]** — качественный русский перевод международного учебника; сверять год и
  редакцию с текущим оригиналом;
- **[И]** — историческая работа: важна для идей, но современные данные, терминологию и
  требования безопасности нужно брать из новых источников.

### 0.3 Правило трёх опор

Для каждой реализуемой величины нужны по возможности три независимые опоры:

1. **теория** — учебник или рецензируемый обзор;
2. **численные данные** — NIST, IUPAC, PubChem, ECHA, IAEA, NNDC или другая профильная
   база с происхождением данных;
3. **границы и безопасность** — стандарт, руководство регулятора или профессионального
   общества.

Wikipedia, игровые wiki и ответы форумов допустимы только как указатель на первоисточник.
Случайный PDF без издателя, ISBN, DOI или библиотечной карточки — не источник для кода.

---

## 1. Какой результат должен дать этот маршрут

После обязательного ядра разработчик должен уметь:

1. различать элемент, нуклид, атом, ион, молекулу, фазу, вещество, смесь, материал и
   игровую партию продукта;
2. проверять сохранение химических элементов, массы, электрического заряда и энергии;
3. считать количества вещества, состав смеси, лимитирующий реагент, конверсию,
   селективность и выход;
4. отличать термодинамическую возможность процесса от его реальной скорости;
5. связывать температуру и давление с фазовым состоянием, равновесием и безопасностью;
6. строить материальный и энергетический баланс аппарата;
7. понимать назначение реактора, теплообменника, колонны, фильтра, сушилки, электролизёра,
   гальванической ячейки и металлургической печи;
8. моделировать не только рецепт, но также чистоту, катализатор, побочные продукты,
   отходы, коррозию, износ и контроль качества;
9. читать SDS, пиктограммы GHS и базовые диаграммы технологического процесса;
10. связывать электрическую мощность, ток, напряжение, сопротивление и тепловыделение с
    электролизом, батареями, нагревом и приводами;
11. читать простую фазовую диаграмму и объяснять роль состава, охлаждения и термообработки;
12. объяснять распад, активность, дозу, экранирование, критичность и отвод остаточного
    тепла без сведения ядерной ветви к «радиоактивному топливу»;
13. указывать источник, единицы, диапазон применимости и неопределённость каждого
    инженерного коэффициента;
14. проверить, не дублирует ли новая машина уже существующую механику Mekanism,
    NuclearCraft Neohaul, TFMG, Create New Age или Immersive Engineering.

---

## 2. Порядок обучения с нуля

Не нужно читать все книги подряд. Каждая ступень должна завершаться маленькой расчётной
программой или прототипом механики.

| Этап | Что изучить | Практический результат | Уровень |
|---|---|---|---:|
| 0 | Единицы SI, размерности, пропорции, графики, погрешности | конвертер единиц и тесты размерности | Б |
| 1 | Атомы, периодичность, связь, формулы, моль, стехиометрия | валидатор формул и реакций | Б |
| 2 | Газы, растворы, фазы, теплота, первый и второй законы | сосуд и простой калориметр | Б |
| 3 | Равновесие, кислоты/основания, растворимость, электрохимия | равновесная ячейка и электролизёр | Б–С |
| 4 | Кинетика, катализ, тепло- и массоперенос | batch/CSTR-модель с нагревом | С |
| 5 | Аналитика и спектроскопия | пробоотбор, чистота, калибровка | С |
| 6 | Органика, полимеры и нефтехимическая логика | цепочка сырьё → мономер → материал | С |
| 7 | Кристаллы, сплавы, металлургия, фазовые диаграммы | состав + режим → структура → свойство | С–П |
| 8 | Процессы и аппараты, управление, безопасность | непрерывная линия с recycle и interlock | С–П |
| 9 | Электромагнетизм, машины, энергосистема | правдоподобный электрический баланс | С |
| 10 | Атомная/ядерная физика, радиохимия, защита | детектор и гражданский тепловой цикл | С–П |
| 11 | Валидация и интеграция с модпаком | вертикальный игровой срез | П |

### 2.1 Минимум до первой версии мода

Обязательно пройти этапы 0–5 и основы этапов 7–9. Ядерную ветвь не следует добавлять,
пока в модели нет единиц, энергии, теплопередачи, измерений, отказов и защитных систем.

### 2.2 Что можно отложить

- ab initio-квантовую химию;
- неидеальные многокомпонентные модели высокого порядка;
- подробную турбулентность;
- полную CALPHAD-оптимизацию;
- пространственную нейтронную кинетику;
- молекулярную динамику;
- полный plant-wide control.

Это ветви углубления, а не оправдание неверной базовой стехиометрии.

---

## 3. Обязательное ядро и углублённые ветви

### 3.1 Обязательное ядро

- размерности, SI и оценка порядка величины;
- строение атома, периодическая система и типы химической связи;
- формула, молярная масса, моль, концентрация и состав;
- баланс атомов, массы, заряда и энергии;
- идеальный газ как первая модель и её ограничения;
- фазы, фазовые переходы и простые смеси;
- энтальпия, энтропия, энергия Гиббса, химический потенциал;
- равновесие и принцип Ле Шателье без его абсолютизации;
- скорость реакции, порядок, закон Аррениуса и катализ;
- pH, окислительно-восстановительные процессы и растворимость;
- напряжение, ток, мощность, сопротивление, заряд;
- теплоёмкость, теплопроводность, диффузия и конвекция;
- чистота, погрешность, предел обнаружения и калибровка;
- GHS/SDS, RAMP, совместимость веществ и иерархия мер контроля.

### 3.2 Ветви углубления

1. **Молекулярная и квантовая химия:** орбитали, симметрия, электронная структура,
   спектры, вычислительные методы.
2. **Органический синтез:** механизмы, стереохимия, ретросинтез, хемоселективность.
3. **Катализ:** гомогенный, гетерогенный, ферментативный; поверхности и деактивация.
4. **Электрохимическая энергетика:** батареи, топливные элементы, суперконденсаторы,
   деградация и transport-limited режимы.
5. **Полимеры:** распределение молекулярных масс, реология, морфология, старение,
   композиты и переработка.
6. **Металлургия:** термодинамика шлаков, экстракция, рафинирование, CALPHAD,
   затвердевание и термообработка.
7. **Химическая технология:** реальные свойства, многофазные реакторы, разделение,
   process control, pinch analysis и HAZOP.
8. **Ядерная инженерия:** взаимодействие излучения, transport, reactor kinetics,
   топливо, теплоотвод, защита, safeguards и обращение с отходами.

---

## 4. Проверенная русскоязычная полка

### 4.1 Оригинальные работы с международным признанием

- **Д. И. Менделеев, «Основы химии»** — исторический источник по периодическому закону
  и развитию общей химии. Книга переводилась на английский, немецкий и французский;
  метаданные раннего английского издания:
  https://openlibrary.org/books/OL14035579M/The_principles_of_chemistry. Для современных
  атомных данных и номенклатуры применять IUPAC/NIST, а не старые таблицы. **[М, И]**
- **Н. Н. Семёнов, «Цепные реакции» и «О некоторых проблемах химической кинетики и
  реакционной способности»** — основа теории цепных процессов; Семёнов получил
  Нобелевскую премию по химии 1956 года совместно с Сирилом Хиншелвудом:
  https://www.nobelprize.org/prizes/chemistry/1956/summary/ **[М, П]**
- **А. Н. Фрумкин, избранные труды по электрохимической кинетике** — международно
  признанная школа двойного электрического слоя и электродных процессов; обзор школы
  и переводов: https://link.springer.com/article/10.1007/s10008-014-2480-5
  **[М, П]**
- **В. С. Баготский, «Основы электрохимии»** — имеет международное английское издание
  *Fundamentals of Electrochemistry* в серии Electrochemical Society/Wiley:
  https://iopscience.iop.org/article/10.1149/2.F01061IF **[М, С–П]**
- **Л. Д. Ландау, Е. М. Лифшиц**, тома 3 «Квантовая механика», 5 «Статистическая
  физика», 8 «Электродинамика сплошных сред» и 10 «Физическая кинетика» — мировая
  теоретическая база для физической химии и материалов. **[М, П]**
- **В. А. Фок, «Начала квантовой механики»** — исторически важная основа квантовой
  теории; имя Фока закреплено в международной терминологии Hartree–Fock и Fock space.
  **[М, И–П]**
- **А. Н. и Н. А. Несмеяновы, «Начала органической химии»** — вышли на английском в
  четырёх томах как *Fundamentals of Organic Chemistry* (Mir Publishers); метаданные
  искать через WorldCat:
  https://search.worldcat.org/search?q=ti%3AFundamentals%20of%20Organic%20Chemistry%20au%3ANesmeyanov
  Учебные данные сверять с современными Clayden, March и IUPAC. **[М, И–С]**
- **В. А. Каргин, избранные труды по высокомолекулярным соединениям** — основа
  отечественной физико-химической школы полимеров и созданной при его участии
  русско-/англоязычной журнальной традиции *Высокомолекулярные соединения / Polymer
  Science*. Это признание школы, а не утверждение, что конкретный старый учебник сегодня
  заменяет Odian или IUPAC. **[М, И–П]**
- **А. П. Гуляев, «Металловедение»** — выходил в английском переводе как *Physical
  Metallurgy* (Mir Publishers) и присутствует в зарубежных университетских каталогах:
  http://opac.duls.du.ac.in/bib/87777. Хорошая связь состава, структуры, обработки и
  свойств. **[М, С–П]**
- **А. Н. Климов, «Ядерная физика и ядерные реакторы»** — переводился Mir Publishers
  как *Nuclear Physics and Nuclear Reactors*; библиографическая запись OSTI:
  https://www.osti.gov/biblio/4285248. Полезен как исторический системный курс, но
  данные и безопасность должны идти из IAEA/NNDC и современных изданий. **[М, И–С]**

Статус **[М]** не означает, что старое издание является лучшим современным справочником.
Он означает международную проверяемость влияния. Числа всё равно нужно обновлять.

### 4.2 Сильные современные и классические русские учебники

- **Н. С. Ахметов, «Общая и неорганическая химия»** — цельный базовый курс.
  Карточка РГБ: https://search.rsl.ru/ru/record/01007551840 **[В, Б–С]**
- **Ю. Д. Третьяков и др., «Неорганическая химия» в 3 томах** — современная школа МГУ:
  https://www.chem.msu.ru/rus/books/2001-2010/inorg-book-vol1/welcome.html
  **[В, С–П]**
- **О. А. Реутов, А. Л. Курц, К. П. Бутин, «Органическая химия» в 4 частях** —
  механизмы и логика органических превращений. Карточка РГБ:
  https://search.rsl.ru/ru/record/01003082497 **[В, С–П]**
- **Е. Н. Ерёмин, «Основы химической термодинамики»**, а затем современный курс
  **В. В. Ерёмина, С. И. Каргова, И. А. Успенской и др., «Основы физической химии»** —
  хорошая русская траектория к физической химии; карточка нового издания МФТИ:
  https://books.mipt.ru/book/307198 **[В, С]**
- **«Краткий справочник физико-химических величин» под ред. К. П. Мищенко и
  А. А. Равделя** (в более новых редакциях — под ред. А. А. Равделя и
  А. М. Пономарёвой) — полезен для обучения, но устаревающие численные данные нужно
  проверять по NIST. **[В, R]**
- **Б. Б. Дамаскин, О. А. Петрий, Г. А. Цирлина, «Электрохимия»** — современный
  университетский курс. Карточка РГБ:
  https://search.rsl.ru/ru/record/01007886855 **[В, С–П]**
- **В. Г. Воскобойников, В. А. Кудрин, А. М. Якушев, «Общая металлургия»**, а также
  профильные учебники МИСИС — база рудоподготовки и основных металлургических
  производств; библиотечная карточка СФУ:
  https://bik.sfu-kras.ru/elib/view?id=BOOK1-669%2F%D0%9276-747321 **[В, С]**
- **А. Г. Касаткин, «Основные процессы и аппараты химической технологии»** —
  классический русский курс. Уравнения полезны, но свойства, нормы и коэффициенты
  старых изданий требуют обновления. Карточка РГБ:
  https://search.rsl.ru/ru/record/01002569897 **[В, С–П]**
- **П. Г. Романков и др., учебники по гидромеханическим, тепло- и массообменным
  процессам химической технологии** — профильное углубление. **[В, П]**
- **В. И. Бекман, «Радиохимия», т. 1–2** — радиоактивность, радионуклиды,
  дозиметрия, защита и прикладная радиохимия. Официальная страница химфака МГУ:
  https://www.chem.msu.ru/rus/books/2014/bekman/welcome.html **[В, С–П]**
- **Д. В. Сивухин, «Общий курс физики»**, особенно тома 3 и 5 — электричество,
  атомная и ядерная физика. **[В, Б–С]**
- **И. Е. Тамм, «Основы теории электричества»** — сильный теоретический курс с
  международными переводами, но не первое введение. **[М, П]**

### 4.3 Качественные русские переводы мировых учебников

Искать именно официальное издание в библиотеке или у издателя, а не безымянный перевод.
Русский перевод полезен для первого чтения, но обозначения и номер издания нужно сверять
с оригиналом.

- **П. Эткинс, Х. де Паула, «Физическая химия»** — термодинамика, квантовая химия,
  спектроскопия и кинетика. **[ПР, Б–П]**
- **Дж. Клейден, Н. Гривз, С. Уоррен, «Органическая химия»** — механизмы вместо
  заучивания перечня реакций. **[ПР, Б–П]**
- **Ф. Кэри, Р. Сандберг, «Углублённый курс органической химии»** или **Дж. Марч,
  «Органическая химия. Реакции, механизмы и структура»** — продвинутая ветвь.
  **[ПР, П]**
- **Н. Гринвуд, А. Эрншо, «Химия элементов»** — систематическая неорганика.
  **[ПР, С–П]**
- **Д. Шрайвер, П. Эткинс и др., «Неорганическая химия»** — современная структура,
  связь и реакционная способность. **[ПР, С–П]**
- **Д. Харрис, «Количественный химический анализ»** и **Д. Скуг и др.,
  «Основы аналитической химии» / «Принципы инструментального анализа»**.
  **[ПР, Б–П]**
- **Д. Мак-Куорри, «Квантовая химия»** — классический русский перевод для перехода
  к молекулярным расчётам; новые методы сверять с актуальным англоязычным изданием и
  современной вычислительной литературой. **[ПР, С–П]**
- **Ф. Дэниелс, Р. Олберти, физическая химия**, **Г. Кастеллан, «Физическая химия»** —
  классические альтернативы, если доступны в библиотеке. **[ПР, С]**
- **О. Левеншпиль, «Инженерное оформление химических процессов»**, **Х. Фоглер,
  «Расчёты химических реакторов»**, **У. Маккейб, Дж. Смит, П. Харриотт,
  «Процессы разделения»** — химическая инженерия. **[ПР, С–П]**
- **У. Каллистер, Д. Ретвиш, «Материаловедение: от технологии к применению»** —
  доступная связь структуры и свойств материалов. **[ПР, Б–С]**
- **Д. Портер, К. Истерлинг, М. Шериф, «Фазовые превращения в металлах и сплавах»** —
  продвинутая металлургическая ветвь. **[ПР, П]**
- **Ф. Родригес и др., «Принципы полимерных систем»**, **Л. Сперлинг,
  «Введение в физику полимеров»** — структура, свойства и переработка полимеров.
  **[ПР, С–П]**
- **Д. Гриффитс, «Введение в электродинамику»**, **Э. Парселл, Д. Морин,
  «Электричество и магнетизм»** — полевая основа. **[ПР, С–П]**
- **Дж. Ламарш, «Введение в теорию ядерных реакторов»** — классический русский
  перевод более раннего международного курса; использовать как теорию, а не источник
  действующих эксплуатационных норм. Современные англоязычные Lamarsh & Baratta и
  Shultis & Faw читать вместе с IAEA, NNDC и актуальными нормами безопасности.
  **[ПР, И–П]**

### 4.4 Где искать легально

- Российская государственная библиотека: https://search.rsl.ru/
- Национальная электронная библиотека: https://rusneb.ru/
- каталог химического факультета МГУ: https://www.chem.msu.ru/rus/books/
- каталоги библиотек МГУ, МИСИС, МИФИ, СПбГУ и профильных вузов;
- WorldCat: https://search.worldcat.org/
- Google Books — для метаданных и доступного издателем preview:
  https://books.google.com/
- сайты авторов, издательств, DOI и открытые университетские репозитории.

Не использовать пиратские агрегаторы как ссылку в документации проекта.

---

## 5. Математика, вычисления и качество данных

### 5.1 Математический минимум

- алгебра, степени, логарифмы и экспоненты;
- функции нескольких переменных и частные производные;
- интегралы как накопление количества и площади;
- векторы, матрицы и системы линейных уравнений;
- обыкновенные дифференциальные уравнения;
- основы вероятности, среднего, дисперсии и доверительного интервала;
- интерполяция, регрессия и анализ остатков;
- численное интегрирование и поиск корней;
- оптимизация с ограничениями;
- размерностный анализ.

### 5.2 Источники

- MIT OCW 18.01 Single Variable Calculus:
  https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/
- MIT OCW 18.02 Multivariable Calculus:
  https://ocw.mit.edu/courses/18-02sc-multivariable-calculus-fall-2010/
- MIT OCW 18.03 Differential Equations:
  https://ocw.mit.edu/courses/18-03sc-differential-equations-fall-2011/
- MIT OCW 18.06 Linear Algebra:
  https://ocw.mit.edu/courses/18-06sc-linear-algebra-fall-2011/
- NIST/SEMATECH e-Handbook of Statistical Methods:
  https://www.itl.nist.gov/div898/handbook/
- NIST CODATA constants: https://physics.nist.gov/cuu/Constants/

### 5.3 Правила реализации

- хранить значения в SI внутри модели;
- единицу делать частью типа или валидируемого поля, а не комментария;
- не смешивать °C и K в термодинамических формулах;
- хранить источник, редакцию, диапазон температуры/давления и неопределённость;
- покрывать баланс и преобразование единиц property-based тестами;
- не экстраполировать полиномы свойств за их диапазон;
- не округлять промежуточные результаты до интерфейсной точности.

---

## 6. Общая и неорганическая химия

### 6.1 Что знать

- атомные номера, изотопы и электронные конфигурации;
- периодические тренды;
- ионная, ковалентная и металлическая связь;
- Lewis/VSEPR как первая модель геометрии;
- твёрдые тела, координационная химия и комплексы;
- типы реакций и окислительные числа;
- растворы, электролиты, активность и ионная сила;
- кислоты/основания, буферы, гидролиз;
- растворимость, комплексообразование и осаждение;
- redox-баланс;
- химия главных подгрупп и переходных металлов.

### 6.2 Курсы и справочники

- MIT 5.111 Principles of Chemical Science:
  https://ocw.mit.edu/courses/5-111sc-principles-of-chemical-science-fall-2014/
- OpenStax Chemistry 2e:
  https://openstax.org/details/books/chemistry-2e
- Chemistry LibreTexts: https://chem.libretexts.org/
- IUPAC Periodic Table:
  https://iupac.org/what-we-do/periodic-table-of-elements/
- IUPAC Gold Book: https://goldbook.iupac.org/
- IUPAC Colour Books and nomenclature: https://iupac.org/what-we-do/books/

### 6.3 Игровой смысл

Неорганическая химия должна создавать систему взаимосвязанных условий, а не таблицу
`A + B = C`:

- степень окисления задаёт допустимые redox-переходы;
- pH и лиганд влияют на растворимость и селективность;
- температура меняет растворимость и скорость;
- примеси переходят между продуктом, раствором, газом и отходами;
- осадок требует фильтрации, промывки и сушки;
- газ требует герметичности, отвода, анализа и очистки.

---

## 7. Органическая химия и механизмы

### 7.1 Порядок изучения

1. функциональные группы и номенклатура;
2. кислотность/основность и электронные эффекты;
3. стереохимия и конформации;
4. нуклеофилы, электрофилы и leaving groups;
5. замещение, элиминирование и присоединение;
6. карбонильная химия;
7. ароматичность и электрофильное/нуклеофильное замещение;
8. radical и pericyclic основы;
9. защита групп, селективность и ретросинтез;
10. industrial feedstocks, катализ и разделение продукта.

### 7.2 Источники

- MIT OCW search: Organic Chemistry:
  https://ocw.mit.edu/search/?q=organic%20chemistry
- OpenStax Organic Chemistry:
  https://openstax.org/details/books/organic-chemistry
- IUPAC Blue Book resources: https://iupac.org/what-we-do/books/bluebook/
- PubChem: https://pubchem.ncbi.nlm.nih.gov/
- Clayden, Greeves, Warren, *Organic Chemistry*;
- Carey & Sundberg, *Advanced Organic Chemistry*;
- March, *Advanced Organic Chemistry*;
- Smith, *Organic Chemistry*;
- Реутов, Курц, Бутин, «Органическая химия».

### 7.3 Что переносить в механику

- не «названия сотен реакций», а общие классы механизмов;
- атомный mapping и сохранение заряда;
- роль растворителя, температуры, катализатора и порядка добавления только на уровне,
  безопасном и необходимом для промышленной модели;
- конкурирующие пути, селективность и побочный продукт;
- разделение и очистку после реакции;
- повторное использование растворителя и стоимость отходов;
- стабильность при хранении.

Мод не должен становиться лабораторным сборником пошаговых синтезов опасных веществ.

---

## 8. Физическая, молекулярная и квантовая химия

### 8.1 Термодинамика

- состояние и уравнение состояния;
- внутренняя энергия, энтальпия, энтропия;
- энергия Гельмгольца и Гиббса;
- химический потенциал и парциальные молярные величины;
- fugacity/activity как поправки к идеальности;
- фазовое и химическое равновесие;
- реальные смеси и excess functions;
- статистический смысл температуры и энтропии.

### 8.2 Молекулярные основы

- волновая функция, оператор и измеряемая величина;
- частица в ящике, осциллятор, ротатор и атом водорода;
- spin и принцип Паули;
- приближение Борна—Оппенгеймера;
- molecular orbitals и valence bond;
- Hartree–Fock, DFT и basis set на концептуальном уровне;
- вращательные, колебательные, электронные и магнитные спектры;
- связь симметрии и selection rules.

### 8.3 Источники

- MIT OCW search: Physical Chemistry:
  https://ocw.mit.edu/search/?q=physical%20chemistry
- MIT OCW search: Quantum Mechanics:
  https://ocw.mit.edu/search/?q=quantum%20chemistry
- Atkins & de Paula, *Physical Chemistry*;
- McQuarrie & Simon, *Physical Chemistry: A Molecular Approach*;
- McQuarrie, *Quantum Chemistry*;
- Levine, *Quantum Chemistry*;
- NIST Chemistry WebBook: https://webbook.nist.gov/chemistry/
- NIST-JANAF Thermochemical Tables: https://janaf.nist.gov/
- NIST ThermoML: https://trc.nist.gov/ThermoML.html

### 8.4 Граница игрового упрощения

Первой версии не нужен realtime DFT. Квантовая химия нужна, чтобы:

- не путать типы связи и электронные состояния;
- понимать происхождение спектров и цвет материалов;
- связывать структуру с реакционной способностью;
- заранее знать, где эмпирическая игровая модель перестаёт работать;
- строить offline-таблицы из проверенных расчётов или экспериментальных данных.

---

## 9. Равновесие, кинетика и катализ

### 9.1 Не смешивать три разных вопроса

1. **Возможен ли процесс?** — знак и величина `ΔG`.
2. **Где установится равновесие?** — `K`, activities, фазы.
3. **Как быстро туда придёт система?** — механизм, `k`, transport и катализ.

Катализатор меняет путь и скорость, но не превращает произвольный термодинамически
невыгодный процесс в источник бесплатной энергии.

### 9.2 Что изучить

- rate law и порядок реакции;
- elementary step против overall equation;
- Arrhenius и transition-state picture;
- последовательные, параллельные и обратимые реакции;
- chain reaction и inhibition;
- residence time distribution;
- homogeneous/heterogeneous catalysis;
- adsorption, Langmuir-type модели;
- diffusion limitation, Thiele modulus как углубление;
- catalyst poisoning, fouling, sintering и regeneration;
- runaway и тепловая устойчивость реактора.

### 9.3 Источники

- MIT OCW search: Chemical Kinetics:
  https://ocw.mit.edu/search/?q=chemical%20kinetics
- Cantera documentation: https://cantera.org/documentation/
- International Association for Chemical Kinetics:
  https://www.iack.net/
- IUPAC Gold Book: https://goldbook.iupac.org/
- Semenov, chain reactions;
- Espenson, *Chemical Kinetics and Reaction Mechanisms*;
- Fogler, *Elements of Chemical Reaction Engineering*;
- Masel, *Chemical Kinetics and Catalysis*.

---

## 10. Аналитическая химия и спектроскопия

### 10.1 Зачем это моду

Без измерения неизвестно, получен ли требуемый материал. Аналитика превращает «машина
закончила рецепт» в систему качества:

- пробоотбор может быть нерепрезентативным;
- прибор имеет калибровку, blank, drift и предел обнаружения;
- результат содержит неопределённость;
- разные методы видят разные компоненты;
- разрушительный анализ расходует пробу;
- online-анализ ускоряет управление, но требует обслуживания.

### 10.2 Минимум методов

- гравиметрия и титрование;
- pH, conductometry и potentiometry;
- UV–Vis, IR/Raman, fluorescence;
- atomic absorption/emission, ICP как продвинутый tier;
- chromatography и mass spectrometry;
- NMR как исследовательский инструмент;
- XRD/XRF для фаз и элементов;
- microscopy и thermal analysis для материалов;
- Geiger/scintillation/semiconductor detection для излучения.

### 10.3 Источники и данные

- NIST Chemistry WebBook spectra: https://webbook.nist.gov/chemistry/
- NIST Atomic Spectra Database: https://physics.nist.gov/PhysRefData/ASD/
- SDBS, AIST spectral database: https://sdbs.db.aist.go.jp/
- PubChem: https://pubchem.ncbi.nlm.nih.gov/
- NIST mass spectral library information:
  https://chemdata.nist.gov/
- Harris, *Quantitative Chemical Analysis*;
- Skoog, Holler, Crouch, *Principles of Instrumental Analysis*;
- IUPAC Orange Book resources: https://iupac.org/what-we-do/books/orangebook/

### 10.4 Игровые показатели

Хранить отдельно:

- accuracy;
- precision;
- limit of detection/quantification;
- selectivity;
- response time;
- sample consumption;
- calibration interval;
- uncertainty.

Нельзя свести анализатор к лампочке «правильно/неправильно».

---

## 11. Электрохимия, батареи, электролиз и коррозия

### 11.1 Обязательная теория

- электродный потенциал и cell voltage;
- электрохимический потенциал;
- Nernst equation;
- Faraday laws;
- ionic conductivity и transport numbers;
- overpotential и Butler–Volmer как углубление;
- mass-transfer limitation;
- double layer;
- primary/secondary batteries;
- fuel cells и electrolyzers;
- charge/discharge, C-rate, state of charge/health;
- corrosion cells, passivation, galvanic series;
- coatings, inhibitors и cathodic protection;
- heat generation и thermal runaway.

### 11.2 Источники

- MIT 10.626 Electrochemical Energy Systems:
  https://ocw.mit.edu/courses/10-626-electrochemical-energy-systems-spring-2014/
- Bard & Faulkner, *Electrochemical Methods*;
- Newman & Thomas-Alyea, *Electrochemical Systems*;
- Bagotsky, *Fundamentals of Electrochemistry*;
- Дамаскин, Петрий, Цирлина, «Электрохимия»;
- Electrochemical Society: https://www.electrochem.org/
- NIST Standard Reference Data: https://www.nist.gov/srd
- PyBaMM battery models: https://www.pybamm.org/

### 11.3 Игровая модель батареи

Минимум параметров:

- nominal/open-circuit voltage;
- capacity и usable state of charge;
- internal resistance;
- max continuous/peak current;
- charge/discharge efficiency;
- temperature;
- cycle/calendar degradation;
- chemistry/material compatibility;
- cooling and containment state.

Одна величина «RF capacity» не описывает батарею.

### 11.4 Коррозия

Коррозия должна зависеть от материала, электролита, температуры, потенциала, контакта
разнородных металлов и состояния покрытия. Результат — утонение, загрязнение продукта,
утечка или потеря проводимости, а не случайный универсальный damage tick.

---

## 12. Высокомолекулярные соединения и полимеры

### 12.1 Что изучить

- monomer, repeat unit, functionality;
- chain-growth и step-growth polymerization;
- radical, ionic, coordination и ring-opening механизмы;
- molecular-weight distribution, `M_n`, `M_w`, dispersity;
- degree of polymerization;
- glass transition `T_g`, melting `T_m`, crystallinity;
- tacticity, branching и crosslinking;
- elastomers, thermoplastics, thermosets;
- copolymers и blends;
- viscoelasticity и rheology;
- extrusion, injection molding, fiber spinning, curing;
- additives, fillers, plasticizers, stabilizers;
- oxidation, UV/thermal aging;
- recycling, downcycling и lifecycle trade-offs.

### 12.2 Источники

- IUPAC Purple Book: https://iupac.org/what-we-do/books/purplebook/
- IUPAC Polymer Division: https://iupac.org/who-we-are/divisions/division-details/?body_code=400
- Polymer Database: https://polymerdatabase.com/
- Young & Lovell, *Introduction to Polymers*;
- Odian, *Principles of Polymerization*;
- Sperling, *Introduction to Physical Polymer Science*;
- Flory, *Principles of Polymer Chemistry*;
- Каргин и Слонимский, труды по физической химии полимеров.

### 12.3 Игровая цепочка

`сырьё → очищенный мономер → инициирование/катализ → контроль тепла → полимер →
компаундирование → формование → отжиг/отверждение → испытание → изделие`

Качество определяется не только названием полимера, но и молекулярной массой,
распределением, кристалличностью, добавками, влагой и thermal history.

---

## 13. Кристаллохимия, твёрдое тело и материаловедение

### 13.1 Структура

- crystal lattice, unit cell и symmetry;
- Miller indices;
- bonding in solids;
- point, line, surface и volume defects;
- dislocations и strengthening;
- diffusion;
- nucleation и growth;
- grain size и texture;
- amorphous, crystalline и composite materials;
- electronic band structure на концептуальном уровне;
- ceramics, glasses, semiconductors и composites.

### 13.2 Свойства

- density;
- elastic modulus, yield, strength, toughness;
- hardness and wear;
- fatigue and creep;
- thermal conductivity/expansion;
- electrical conductivity and dielectric properties;
- magnetic response;
- optical response;
- oxidation and corrosion;
- irradiation damage.

### 13.3 Источники и базы

- MIT 3.091 Introduction to Solid-State Chemistry:
  https://ocw.mit.edu/courses/3-091-introduction-to-solid-state-chemistry-fall-2018/
- Materials Project: https://materialsproject.org/
- Crystallography Open Database: https://www.crystallography.net/cod/
- International Union of Crystallography: https://www.iucr.org/
- NIST Materials Data resources: https://www.nist.gov/materials-science
- Callister & Rethwisch, *Materials Science and Engineering*;
- Askeland & Wright, *The Science and Engineering of Materials*;
- Porter, Easterling & Sherif, *Phase Transformations in Metals and Alloys*.

### 13.4 Игровое правило

Свойство принадлежит не только `material_id`. Оно является функцией:

`состав + фазы + дефекты + размер зерна + обработка + температура + деградация`.

В первой версии допускается агрегированный `material_grade`, но его provenance должен
вести к этим физическим причинам.

---

## 14. Металлургия и фазовые диаграммы

### 14.1 Производственная цепочка

1. разведка и представительная проба;
2. дробление, измельчение, классификация;
3. физическое обогащение;
4. agglomeration/pelletizing при необходимости;
5. pyrometallurgy, hydrometallurgy или electrometallurgy;
6. separation/refining;
7. alloying;
8. casting или powder route;
9. forming;
10. heat treatment;
11. inspection и recycling.

### 14.2 Что изучить

- free-energy basis фазовой устойчивости;
- unary/binary/ternary phase diagrams;
- tie line и lever rule;
- eutectic, peritectic, solid solution;
- segregation и non-equilibrium solidification;
- TTT/CCT diagrams;
- annealing, quenching, tempering, aging;
- slag, matte, flux и refractory;
- Ellingham diagram;
- leaching, solvent extraction, ion exchange, electrowinning;
- powder metallurgy;
- oxidation, inclusions, gas pickup;
- scrap composition и circular metallurgy.

### 14.3 Источники

- A. P. Gуляев, «Металловедение»;
- David R. Gaskell & David E. Laughlin, *Introduction to the Thermodynamics of
  Materials*;
- Porter, Easterling & Sherif, *Phase Transformations in Metals and Alloys*;
- ASM International: https://www.asminternational.org/
- NIST phase-equilibria and materials programs: https://www.nist.gov/mml
- pycalphad: https://pycalphad.org/
- OpenCalphad: https://www.opencalphad.com/
- Materials Project: https://materialsproject.org/

Коммерческие ASM и CALPHAD-базы часто лицензируются отдельно. Нельзя копировать их
таблицы в репозиторий без разрешения. Для игрового набора использовать открытые данные,
собственные fit-файлы с provenance или явно лицензированный источник.

### 14.4 Модель качества металла

- range состава, а не единственная точка;
- impurity limits;
- phase fractions;
- grain/defect state;
- heat-treatment state;
- mechanical/thermal/electrical property ranges;
- corrosion state;
- inspection confidence.

---

## 15. Химическая технология и процессы аппаратов

### 15.1 Балансы

Каждый аппарат должен отвечать на вопросы:

- что входит и выходит;
- что накапливается;
- что превращается;
- куда уходит энергия;
- какая фаза присутствует;
- что ограничивает скорость;
- где возникает перепад давления;
- что произойдёт при отказе охлаждения, перемешивания, питания или отвода продукта.

### 15.2 Аппараты

- batch, CSTR, PFR;
- stirred/packed/fluidized reactors;
- heat exchangers, condensers, evaporators;
- distillation, absorption, stripping;
- extraction and leaching;
- filtration, centrifugation, sedimentation;
- drying and crystallization;
- adsorption, ion exchange, membranes;
- compressors, pumps, valves, vessels and piping;
- flare/scrubber/relief and waste treatment;
- sensors, control loops and emergency shutdown.

### 15.3 Источники

- MIT OpenCourseWare, Chemical Engineering:
  https://ocw.mit.edu/search/?d=Chemical%20Engineering
- LearnChemE simulations and screencasts: https://learncheme.com/
- NPTEL courses: https://nptel.ac.in/courses
- Felder, Rousseau & Bullard, *Elementary Principles of Chemical Processes*;
- Bird, Stewart & Lightfoot, *Transport Phenomena*;
- Incropera et al., *Fundamentals of Heat and Mass Transfer*;
- Fogler, *Elements of Chemical Reaction Engineering*;
- Levenspiel, *Chemical Reaction Engineering*;
- McCabe, Smith & Harriott, *Unit Operations of Chemical Engineering*;
- Seader, Henley & Roper, *Separation Process Principles*;
- Towler & Sinnott, *Chemical Engineering Design*;
- Касаткин, «Основные процессы и аппараты химической технологии».

### 15.4 Управление процессом

Минимум:

- sensor → controller → actuator;
- setpoint, disturbance, lag, dead time;
- feedback/feedforward;
- PID на концептуальном и простом численном уровне;
- alarm отдельно от trip;
- fail-open/fail-closed положение;
- interlock и permissive;
- redundancy для критической защиты;
- ручной safe shutdown.

---

## 16. Электричество, электромагнетизм и электрические машины

### 16.1 Что нужно химику-разработчику

- charge, current, voltage and potential;
- resistance, conductivity and Joule heating;
- capacitance and inductance;
- electric and magnetic fields;
- DC/AC, RMS, phase and power factor;
- Kirchhoff laws;
- three-phase concept;
- transformer;
- generator and motor torque/speed basics;
- rectifier/inverter;
- grounding, insulation and short-circuit protection;
- energy storage and grid balance.

### 16.2 Источники

- MIT 8.02 Electricity and Magnetism:
  https://ocw.mit.edu/search/?q=electricity%20and%20magnetism
- MIT 6.002 Circuits and Electronics:
  https://ocw.mit.edu/courses/6-002-circuits-and-electronics-spring-2007/
- OpenStax University Physics, volume 2:
  https://openstax.org/details/books/university-physics-volume-2
- Purcell & Morin, *Electricity and Magnetism*;
- Griffiths, *Introduction to Electrodynamics*;
- Chapman, *Electric Machinery Fundamentals*;
- Horowitz & Hill, *The Art of Electronics* — для электроники и измерительных трактов;
- Сивухин, том 3; Тамм, «Основы теории электричества»; Иродов, задачи по
  электромагнетизму.

### 16.3 Интеграционное правило

Если Create New Age, Immersive Engineering или Power Grid уже считает сеть, химический
мод не должен создавать несовместимую вторую «электрическую физику». Нужен адаптер:

- единая внутренняя энергия;
- известная временная база power/energy;
- ограничение тока или мощности;
- потери с явной теплотой;
- таблица conversion и автоматические тесты conservation;
- отсутствие генерации энергии при округлении.

---

## 17. Атомная и ядерная физика, радиохимия и мирная энергетика

### 17.1 Порядок обучения

1. атомное ядро, `Z`, `A`, изотопы и binding energy;
2. стабильность и виды распада;
3. exponential decay, half-life и activity;
4. взаимодействие alpha, beta, gamma, neutron с веществом;
5. detector response и counting statistics;
6. absorbed dose, equivalent/effective dose и ограничения этих величин;
7. shielding, time–distance–shielding и ALARA;
8. fission, neutron balance, multiplication и criticality safety;
9. reactor kinetics на уровне point model;
10. heat generation, coolant loop и decay heat;
11. fuel cycle, safeguards, waste classification и repository logic;
12. environmental monitoring и decommissioning.

### 17.2 Официальные курсы и данные

- MIT 22.01 Introduction to Nuclear Engineering and Ionizing Radiation:
  https://ocw.mit.edu/courses/22-01-introduction-to-nuclear-engineering-and-ionizing-radiation-fall-2016/
- IAEA, Fundamentals of Reactor Physics with Python:
  https://www.iaea.org/online-learning/courses/1337/fundamentals-of-reactor-physics-with-python
- IAEA Nuclear Data Services: https://www-nds.iaea.org/
- IAEA LiveChart of Nuclides:
  https://www-nds.iaea.org/relnsd/vcharthtml/VChartHTML.html
- NNDC evaluated nuclear databases: https://www.nndc.bnl.gov/databases/
- NuDat 3: https://www.nndc.bnl.gov/nudat3/
- ENDF: https://www.nndc.bnl.gov/endf/
- IAEA Safety Standards: https://www.iaea.org/resources/safety-standards
- U.S. NRC student resources: https://www.nrc.gov/reading-rm/basic-ref/students
- UNSCEAR: https://www.unscear.org/
- ICRP: https://www.icrp.org/
- В. И. Бекман, «Радиохимия»:
  https://www.chem.msu.ru/rus/books/2014/bekman/welcome.html

### 17.3 Что хранить у нуклида

- `Z`, `A`, metastable state;
- atomic mass and natural abundance;
- half-life with uncertainty;
- decay modes and branching ratios;
- emitted radiation categories and energies;
- daughter nuclides;
- cross-section reference, energy grid and evaluated-library version;
- chemical element identity separately from nuclear identity;
- provenance and data release date.

Нельзя хранить одну абстрактную величину «радиоактивность элемента»: изотопный состав
принципиален.

### 17.4 Реакторная игровая ветвь

Правдоподобная гражданская модель должна связывать:

- neutron population/reaction rate;
- fuel and moderator state;
- temperature feedback;
- control and shutdown systems;
- coolant flow;
- heat exchanger/steam or other power cycle;
- decay heat after shutdown;
- containment;
- monitoring and waste streams.

Даже упрощённая модель должна демонстрировать defense in depth. «Нажали SCRAM — тепло
исчезло» неверно: распадные тепловыделения продолжаются.

### 17.5 Граница безопасности проекта

В документации и интерфейсе допустимы:

- учебные данные распада и детектирования;
- гражданские типы реакторов на обзорном уровне;
- теплогидравлика, защита, safeguards и обращение с отходами;
- симуляция отказов ради понимания безопасности.

Не включать:

- проектирование ядерного взрывного устройства;
- способы обхода safeguards;
- оптимизацию получения оружейного материала;
- эксплуатационные инструкции, превращающие игру в практическое руководство для
  опасной реальной деятельности.

---

## 18. Экология, токсикология и промышленная безопасность

### 18.1 Иерархия мер контроля

1. elimination;
2. substitution;
3. engineering controls;
4. administrative controls;
5. PPE как последняя линия, а не универсальное решение.

### 18.2 RAMP

- **Recognize** hazards;
- **Assess** risks;
- **Minimize** risks;
- **Prepare** for emergencies.

ACS safety guidelines:
https://www.acs.org/content/dam/acsorg/about/governance/committees/chemicalsafety/publications/acs-safety-guidelines-academic.pdf

### 18.3 Обязательные источники

- UNECE GHS Rev. 11 (2025):
  https://unece.org/sites/default/files/2025-09/GHS%20Rev11e.pdf
- OSHA Hazard Communication / SDS:
  https://www.osha.gov/hazcom
- NIOSH Pocket Guide: https://www.cdc.gov/niosh/npg/
- ECHA Information on Chemicals:
  https://echa.europa.eu/information-on-chemicals
- OECD eChemPortal: https://www.echemportal.org/echemportal/
- EPA CompTox Chemicals Dashboard: https://comptox.epa.gov/dashboard/
- ATSDR Toxicological Profiles: https://wwwn.cdc.gov/TSP/index.aspx
- AIChE Center for Chemical Process Safety: https://www.aiche.org/ccps
- IAEA Safety Standards: https://www.iaea.org/resources/safety-standards

### 18.4 Что должна учитывать механика риска

- hazard не равен risk;
- route of exposure: inhalation, ingestion, dermal, external radiation;
- dose/concentration, duration and frequency;
- flammability/explosion limits;
- oxidizer/fuel incompatibility;
- pressure and stored energy;
- dust explosion;
- thermal runaway;
- corrosion and material incompatibility;
- acute/chronic/ecotoxic effects;
- release pathway and environmental persistence;
- detection, alarm, containment and mitigation;
- uncertainty and missing data.

### 18.5 Отходы

Отход — материальный поток с составом, фазой, температурой, hazard class и возможностью
recovery. Он не должен исчезать в универсальном trash slot. Приоритет:

`предотвращение → уменьшение → повторное использование → переработка → обработка →
контролируемое размещение`.

### 18.6 Стандарты и нормативные ориентиры

Стандарт — не учебник и не автоматически применимый закон. Нужно фиксировать редакцию,
юрисдикцию и область применения. Многие полные тексты платные: в репозиторий записывать
библиографию и собственные требования, а не копировать закрытый документ.

| Область | Ориентир | Что брать в проект |
|---|---|---|
| Единицы | BIPM SI Brochure: https://www.bipm.org/en/publications/si-brochure | SI, symbols, prefixes |
| Метрология | JCGM GUM/VIM: https://www.bipm.org/en/committees/jc/jcgm/publications | uncertainty и термины |
| Испытательная лаборатория | ISO/IEC 17025:2017: https://www.iso.org/standard/66912.html | traceability, calibration, competence |
| Экологическое управление | ISO 14001:2026: https://www.iso.org/standard/14001 | аспект → воздействие → control |
| Охрана труда | ISO 45001:2018: https://www.iso.org/standard/63787.html | hazard/risk/control workflow |
| Управление риском | ISO 31000:2018: https://www.iso.org/standard/65694.html | единая risk vocabulary |
| Process safety | OSHA PSM: https://www.osha.gov/process-safety-management | process information, MOC, review |
| Process safety | AIChE CCPS: https://www.aiche.org/ccps | inherently safer design, HAZOP layers |
| Классификация химикатов | UNECE GHS Rev. 11 | labels, hazard statements, SDS structure |
| Свойства материалов | ASTM/ISO method named with each datum | specimen/method/temperature context |
| Радиационная защита | IAEA GSR Part 3 и ICRP | justification, optimization/ALARA, limits |
| Проект АЭС | IAEA SSR-2/1 и связанные guides | defense in depth и safety functions |
| Радиоактивные отходы | IAEA GSR Part 5 | characterization и predisposal lifecycle |

Официальный каталог IAEA Safety Standards:
https://www.iaea.org/resources/safety-standards

Для прочности и материалов минимум различать метод испытания: tensile, hardness,
Charpy impact, fatigue, creep, corrosion и grain-size measurement. Значения из разных
методов и температур нельзя складывать в один безусловный `strength`.

---

## 19. Официальные базы данных

| Данные | Основной источник | Для чего |
|---|---|---|
| Термины и номенклатура | IUPAC Gold/Colour Books | единые определения и имена |
| Формулы, структуры, свойства | PubChem | идентификаторы, cross-links, safety summaries |
| Термохимия и спектры | NIST Chemistry WebBook | `ΔHf°`, `S°`, `Cp`, phase/spectral data |
| Высокоточные константы | NIST CODATA | `R`, `F`, `N_A`, `e`, `k_B` |
| Токсикология/регуляторика | ECHA, EPA CompTox, OECD eChemPortal | hazards, endpoints, dossiers |
| Atomic spectra | NIST ASD | линии и уровни атомов/ионов |
| Кристаллы | COD, IUCr | структуры и стандарты кристаллографии |
| Расчётные материалы | Materials Project | DFT-derived structures/properties |
| Фазовые диаграммы | ASM/NIST/open CALPHAD data | фазы и диапазоны состава |
| Нуклиды и распады | IAEA LiveChart, NNDC NuDat | half-life, decay, branching |
| Ядерные реакции | ENDF/IAEA NDS | evaluated cross sections |
| Безопасность | UNECE GHS, SDS, NIOSH, IAEA | классификация и controls |

### 19.1 Правила импорта

Для каждой записи сохранять:

- точный URL или DOI;
- название набора;
- версию/release date;
- дату получения;
- лицензию;
- исходные единицы;
- условия и неопределённость;
- преобразование в внутренний формат;
- checksum исходного разрешённого файла;
- скрипт воспроизводимого импорта.

Не коммитить большой dataset или коммерческую таблицу только потому, что её удалось
скачать.

---

## 20. Расчётные программы

### 20.1 Обязательный минимальный набор

| Инструмент | Назначение | Ссылка |
|---|---|---|
| Python | воспроизводимые расчёты и тесты | https://www.python.org/ |
| NumPy/SciPy | массивы, ODE, оптимизация | https://scipy.org/ |
| Pint | единицы измерения | https://pint.readthedocs.io/ |
| pandas | табличный ETL и QA | https://pandas.pydata.org/ |
| Jupyter | объяснимые notebooks | https://jupyter.org/ |
| Cantera | термодинамика, кинетика, transport | https://cantera.org/ |
| DWSIM | открытый process simulator | https://dwsim.org/ |
| RDKit | cheminformatics | https://www.rdkit.org/ |
| Avogadro | просмотр и построение молекул | https://avogadro.cc/ |
| PHREEQC | aqueous speciation и geochemistry | https://www.usgs.gov/software/phreeqc-version-3 |
| pycalphad | CALPHAD-расчёты | https://pycalphad.org/ |
| PyBaMM | модели батарей | https://www.pybamm.org/ |
| OpenMC | открытый Monte Carlo neutron/photon transport | https://openmc.org/ |
| Geant4 | transport частиц и detector studies | https://geant4.web.cern.ch/ |

### 20.2 Продвинутые ветви

- Quantum ESPRESSO: https://www.quantum-espresso.org/
- CP2K: https://www.cp2k.org/
- GROMACS: https://www.gromacs.org/
- OpenFOAM: https://www.openfoam.com/
- OpenModelica: https://openmodelica.org/
- Reaktoro: https://reaktoro.org/
- RMG-Py: https://reactionmechanismgenerator.github.io/

### 20.3 Принцип применения

- программа не является источником истины сама по себе;
- model, database и solver должны быть указаны отдельно;
- результат проверять benchmark-задачей;
- notebook не должен быть единственной реализацией production-формулы;
- лицензия данных может отличаться от лицензии программы;
- версии solver и database фиксируются.

---

## 21. Формулы первого игрового этапа

Ниже — не полный курс, а проверяемое ядро для первой реализации.

### 21.1 Количество вещества и состав

Количество вещества:

`n = m / M`

Молярная концентрация:

`c_i = n_i / V`

Мольная и массовая доли:

`x_i = n_i / Σn_j`, `w_i = m_i / Σm_j`

Для реакции с extent `ξ`:

`n_i = n_i,0 + ν_i ξ`

где `ν_i < 0` для реагентов и `ν_i > 0` для продуктов.

Проверки:

- число атомов каждого элемента слева и справа совпадает;
- суммарный заряд совпадает;
- `Σw_i = 1` и `Σx_i = 1` с численной терпимостью.

### 21.2 Материальный баланс аппарата

Общий вид:

`накопление = вход − выход + образование − потребление`

Для компонента в хорошо перемешанном объёме:

`dN_i/dt = F_i,in − F_i,out + V Σ_r ν_i,r r_r`

Steady state не означает, что внутри ничего не происходит; он означает нулевое
макроскопическое накопление.

### 21.3 Газ

Первая модель:

`P V = n R T`

Плотность идеального газа:

`ρ = P M / (R T)`

Проверять `T` только в K. Для высокого давления, конденсации и сильно неидеальных смесей
нужна EOS реального газа.

### 21.4 Теплота и энергия

Без фазового перехода и реакции:

`Q = m c_p ΔT`

С фазовым переходом:

`Q_phase = m L`

Энтальпия реакции из энтальпий образования:

`Δ_r H° = Σ_products ν_i Δ_f H_i° − Σ_reactants |ν_i| Δ_f H_i°`

Энергетический баланс должен включать sensible heat, latent heat, reaction heat, shaft
work, electrical work и потери.

### 21.5 Термодинамика и равновесие

`ΔG = ΔH − TΔS`

`Δ_r G = Δ_r G° + R T ln Q`

При равновесии:

`Δ_r G = 0`, `K = exp(−Δ_r G° / RT)`

Использовать activities; концентрации и парциальные давления — приближения в указанных
standard states.

### 21.6 Кинетика

Простая rate law:

`r = k ∏ c_i^α_i`

Arrhenius:

`k = A exp(−E_a / RT)`

Первый порядок:

`c(t) = c_0 exp(−kt)`

Температурная зависимость скорости не должна автоматически использоваться за пределами
валидности механизма и фазового состояния.

### 21.7 Перенос

Fourier:

`q'' = −k_th ∇T`

Fick:

`J_i = −D_i ∇c_i`

Newton cooling как lumped-модель:

`Qdot = h A (T_surface − T_bulk)`

Pressure drop и mixing power добавляются отдельными моделями, а не прячутся в
эффективности рецепта.

### 21.8 Электричество и электрохимия

`P = U I`

`E = ∫P dt`, для постоянной мощности `E = Pt`

`U = I R`, `R = ρ_e l / A`

Заряд:

`Q_e = I t`

Закон Фарадея:

`n_product = η_F I t / (z F)`

Nernst:

`E = E° − (RT / zF) ln Q`

Связь химии и электричества:

`ΔG = −z F E`

### 21.9 Полимеры

Number-average molecular mass:

`M_n = ΣN_i M_i / ΣN_i`

Weight-average:

`M_w = ΣN_i M_i² / ΣN_i M_i`

Dispersity:

`Đ = M_w / M_n`

Degree of polymerization как первая оценка:

`DP_n ≈ M_n / M_repeat`

### 21.10 Фазовые диаграммы и диффузия

Lever rule в двухфазной области:

`f_α = (C_β − C_0)/(C_β − C_α)`

`f_β = (C_0 − C_α)/(C_β − C_α)`

Характерная диффузионная длина:

`L ~ sqrt(Dt)`

`D = D_0 exp(−Q_D / RT)`

### 21.11 Радиоактивность и защита

`N(t) = N_0 exp(−λt)`

`λ = ln 2 / t_1/2`

`A = λN`

Упрощённое ослабление узкого пучка:

`I = I_0 exp(−μx)`

Последняя формула не заменяет transport calculation: buildup, scattering, geometry и
вторичное излучение требуют более полной модели и официальных данных.

---

## 22. Предлагаемая модель данных

### 22.1 Разделять понятия

- `element` — химический элемент;
- `nuclide` — конкретное ядро;
- `species` — молекула/ион/частица;
- `phase` — gas/liquid/solid/plasma и более точная фаза;
- `substance` — химически определённое вещество;
- `mixture` — состав с долями;
- `material_grade` — допустимый состав и состояние материала;
- `batch` — фактическая игровая партия с provenance;
- `reaction_model` — стехиометрия + thermo + kinetics;
- `process_unit` — аппарат и его state;
- `measurement` — значение, uncertainty, method и sample.

### 22.2 Минимальная запись вещества

```json
{
  "id": "aeronautics_plus:water",
  "formula": "H2O",
  "molar_mass_kg_per_mol": 0.01801528,
  "reference_state": {"temperature_K": 298.15, "pressure_Pa": 100000},
  "phases": ["liquid", "gas", "solid"],
  "identifiers": {"pubchem_cid": 962},
  "hazards": [],
  "sources": [
    {
      "kind": "property_database",
      "url": "https://webbook.nist.gov/cgi/cbook.cgi?ID=C7732185&Mask=1",
      "retrieved": "2026-10-01"
    }
  ]
}
```

Число без reference state и источника не должно считаться законченными данными.

### 22.3 Минимальная запись реакции

```json
{
  "id": "aeronautics_plus:water_electrolysis",
  "stoichiometry": {
    "aeronautics_plus:water": -2,
    "aeronautics_plus:hydrogen": 2,
    "aeronautics_plus:oxygen": 1
  },
  "atom_balance_required": true,
  "charge_balance_required": true,
  "reversible": true,
  "model": "faradaic_v1",
  "operating_window": {
    "temperature_K": [273.15, 373.15]
  },
  "requires": ["electrolyte", "electrodes", "gas_separation", "ventilation"],
  "sources": []
}
```

Это схема, а не готовый production-рецепт. Диапазоны, материалы, efficiency и hazards
должны быть заполнены из проверенных источников и покрыты тестами.

### 22.4 Материал и партия

`material_grade` задаёт допустимые диапазоны, а `batch` хранит фактические:

- composition;
- phase/microstructure state;
- temperature and pressure;
- mass/amount;
- purity and moisture;
- process history;
- contamination;
- age/degradation;
- measurements and uncertainty;
- source machines and operator events.

Это позволяет отличить две партии «одного предмета» без создания тысяч item ID.

### 22.5 Обязательные инварианты

- atom balance;
- electric charge balance;
- non-negative amount/mass;
- normalized fractions;
- energy conservation within declared numerical tolerance;
- no NaN/Infinity crossing save/network boundary;
- deterministic result for fixed state and seed;
- source and unit for every imported coefficient;
- schema version and migration test.

---

## 23. Уровни игрового реализма

### Tier 0 — учебная лаборатория

- единицы, весы, термометр, pH и простая идентификация;
- вещества имеют формулу и молярную массу;
- реакция проверяет баланс;
- UI объясняет ошибку.

### Tier 1 — стехиометрическое производство

- limiting reagent;
- conversion, selectivity, yield;
- phase and temperature window;
- energy requirement;
- byproducts and waste.

### Tier 2 — термодинамика и разделение

- reversible reaction/equilibrium;
- heating/cooling and phase change;
- gas/liquid/solid handling;
- distillation, extraction, filtration, crystallization;
- purity and recycle.

### Tier 3 — кинетика и непрерывные процессы

- batch/CSTR/PFR distinction;
- residence time;
- catalyst activity;
- heat/mass-transfer limits;
- sensors, PID, alarm, trip;
- pressure drop and equipment sizing class.

### Tier 4 — материалы

- alloy/polymer/ceramic composition;
- phase diagram and heat treatment;
- microstructure grade;
- corrosion, fatigue, creep and aging;
- quality inspection.

### Tier 5 — электрохимия и энергетика

- cell voltage, current and Faraday efficiency;
- resistance and waste heat;
- battery degradation;
- electrical network adapter;
- cooling and containment.

### Tier 6 — безопасная ядерная энергетика

- nuclide-specific decay;
- detector and shielding;
- neutron/thermal feedback abstraction;
- shutdown plus decay heat;
- multiple barriers;
- fuel lifecycle, safeguards and waste.

Каждый tier должен работать самостоятельно и не требовать realtime-решателя следующего
уровня.

---

## 24. Матрица «механика → источник → проверка»

| Механика | Теория/данные | Проверка |
|---|---|---|
| Молярная масса | IUPAC atomic weights | совпадение formula parser с эталонами |
| Баланс реакции | OpenStax/IUPAC | atoms и charge exactly conserved |
| Идеальный газ | OpenStax/MIT | benchmark `PV=nRT`, SI tests |
| `Cp`, `ΔHf°`, `S°` | NIST WebBook/JANAF | reproduce tabulated state point |
| Равновесие | Atkins/NIST | `ΔG° ↔ K` consistency |
| Кинетика | primary kinetic source/Cantera | regression across valid T range |
| Теплообмен | Incropera | energy closure and limiting cases |
| Реактор | Fogler/Levenspiel | batch/CSTR/PFR textbook benchmarks |
| Разделение | McCabe/Seader | binary mass balance and stage limits |
| pH/speciation | Harris/PHREEQC | buffer and electroneutrality cases |
| Спектр | NIST/SDBS | peak IDs include conditions/source |
| Электролиз | Bagotsky/MIT 10.626 | Faraday balance and energy lower bound |
| Батарея | Newman/PyBaMM | OCV, resistance and capacity cases |
| Полимеризация | Odian/IUPAC Purple Book | mass balance and molecular averages |
| Фаза сплава | CALPHAD/open phase data | lever-rule tie-line test |
| Термообработка | Porter/ASM-licensed or open data | state-transition test |
| Коррозия | ECS/authoritative materials source | compatible/incompatible benchmark |
| Электросеть | MIT 6.002/OpenStax | power conservation and no free energy |
| Распад | NNDC/IAEA | half-life and branching sum |
| Детектор | Knoll/IAEA | Poisson count statistics |
| Экранирование | IAEA/NIST data | monotonicity plus benchmark geometry |
| Реакторное тепло | MIT/IAEA | power-to-heat balance and decay heat |
| Hazard UI | GHS/SDS/ECHA | label/SDS field traceability |
| Выброс/отход | mass balance + regulatory source | no disappearing mass |

---

## 25. Интеграция с реальными модами сборки

В текущей сборке уже присутствуют связанные namespace: `tfmg`, `mekanism`,
`nuclearcraftneohaul`, `create_new_age`, `immersiveengineering`, `powergrid`, `agricraft`.
Перед добавлением блока необходимо проверить реальный JAR, recipes, tags, capabilities/API
и лицензию конкретной версии.

### 25.1 TFMG (`tfmg`)

Вероятные области пересечения:

- нефтехимия;
- жидкие топлива;
- дистилляция;
- металлургическое/машиностроительное производство;
- Create-совместимые аппараты.

Не делать вторую одинаковую колонну только ради нового texture. Возможное дополнение:
чистота, online-анализ, катализатор, материальный/тепловой баланс, corrosion и adapters.

### 25.2 Mekanism (`mekanism`)

Вероятные области пересечения:

- многоступенчатая переработка руд;
- газы/chemicals;
- электролиз;
- химические машины;
- энергетика и radiation-related mechanics.

Уникальная роль нового слоя — coherent provenance данных, equilibrium/kinetics,
материаловедение, контроль качества и межмодовый conservation, а не замена всей цепочки
Mekanism.

### 25.3 NuclearCraft Neohaul (`nuclearcraftneohaul`)

Вероятные области пересечения:

- fission fuel cycles;
- реакторные компоненты;
- тепло и охлаждение;
- изотопы/радиация.

Не создавать параллельный список условных изотопов. Сначала составить mapping реестров,
единиц тепла, radiation API и recipes. Дополнение должно усиливать детектирование,
радиохимию, materials degradation, safety barriers и waste provenance.

### 25.4 Create New Age (`create_new_age`), Immersive Engineering
(`immersiveengineering`) и Power Grid (`powergrid`)

Пересечение:

- генерация и передача энергии;
- электромагнитные компоненты;
- motors/generators;
- аккумуляторы;
- электрический нагрев.

Нужна единая таблица adapters. Нельзя независимо считать один и тот же ток в трёх сетях
или получать энергию на циклическом conversion.

### 25.5 AgriCraft (`agricraft`)

Если будет агрохимическая ветвь:

- nutrients и soil chemistry;
- удобрения и trace elements;
- вода, salinity и pH;
- безопасные limits и runoff.

Не сводить растения к «добавить NPK = ускорение». Избыток, дефицит, токсичность и
загрязнение должны быть различимы, но не перегружать базовую игру.

### 25.6 Проверка перед интеграцией

1. открыть JAR как ZIP;
2. выписать registry IDs, tags, recipes, fluids/gases/chemicals;
3. проверить официальную документацию/API конкретной версии;
4. построить таблицу `same substance → canonical identity → each mod identity`;
5. определить owner механики;
6. добавить adapter и round-trip tests;
7. проверить recipe conflicts и JEI/REI presentation;
8. проверить локализацию и официальное написание названий модов.

---

## 26. Практические проекты

### Проект 1. Проверяемый калькулятор единиц

- SI internal representation;
- mass, amount, energy, power, pressure, temperature;
- dimensional errors fail loudly;
- тесты °C/K и J/W·tick conversion.

**Готово, если:** 100 случайных round-trip преобразований не создают энергию и не меняют
размерность.

### Проект 2. Парсер формул и балансировщик

- elements, parentheses, hydrate notation, ionic charge;
- molar mass from versioned atomic weights;
- atom/charge matrix;
- human-readable diagnostics.

**Готово, если:** эталонные neutral/redox equations проходят, а намеренно неверные
отклоняются.

### Проект 3. Реестр вещества с provenance

- stable namespaced ID;
- IUPAC/PubChem/NIST identifiers;
- state-dependent property records;
- source, units, range and uncertainty.

**Готово, если:** ни одно production-свойство не появляется без источника.

### Проект 4. Калориметр и фазовый переход

- `mcΔT`;
- latent heat;
- finite heater power;
- environmental loss;
- overheat alarm.

**Готово, если:** energy closure укладывается в заявленный tolerance.

### Проект 5. Batch reactor

- limiting reagent;
- kinetic progression;
- heat of reaction;
- cooling and runaway state;
- conversion/selectivity/byproduct.

**Готово, если:** уменьшение timestep не меняет результат сверх tolerance.

### Проект 6. Равновесная ячейка

- reaction quotient;
- `ΔG` and equilibrium direction;
- simple non-ideal correction as optional model;
- phase presence.

**Готово, если:** equilibrium solution independently reproduces a textbook benchmark.

### Проект 7. Разделение смеси

Начать с binary flash/distillation или фильтрации:

- complete mass balance;
- purity/recovery trade-off;
- heat duty;
- recycle without duplication.

**Готово, если:** сумма feeds равна products + inventory + declared losses.

### Проект 8. Электролизёр

- Faraday law;
- voltage/current limits;
- efficiency and waste heat;
- electrode/electrolyte compatibility abstraction;
- separated product streams.

**Готово, если:** chemical free-energy gain не превышает electrical input после учёта
принятой модели.

### Проект 9. Батарейный блок

- OCV curve;
- state of charge;
- internal resistance;
- temperature;
- degradation and protection.

**Готово, если:** charge-discharge cycle не возвращает больше энергии, чем получил.

### Проект 10. Полимерная линия

- monomer purity;
- conversion and molecular-weight class;
- exotherm;
- additives;
- forming and curing/annealing.

**Готово, если:** свойства конечного grade меняются объяснимо от process history.

### Проект 11. Бинарный сплав

- composition range;
- phase diagram regions;
- lever rule;
- cooling rate abstraction;
- heat treatment and mechanical grade.

**Готово, если:** phase fractions непрерывны внутри областей и выполняют сумму 1.

### Проект 12. Гидро-/электрометаллургическая цепочка

- ore assay;
- selective leaching abstraction;
- purification;
- electrowinning;
- water/reagent recycle;
- residue composition.

**Готово, если:** каждый элемент трассируется от руды до металла, раствора или residue.

### Проект 13. Аналитическая лаборатория

- sampling;
- calibration curve;
- uncertainty/noise;
- detection limit;
- certificate of analysis.

**Готово, если:** игрок может принять плохую партию по слабому измерению, но видит причину
и способ улучшения.

### Проект 14. Коррозия и совместимость

- material + environment;
- temperature;
- galvanic contact;
- coating/passivation;
- inspection.

**Готово, если:** corrosion changes leak/contamination risk gradually and reproducibly.

### Проект 15. Радиационный детектор

- background;
- Poisson counting;
- dead time/efficiency abstraction;
- energy window for advanced detector;
- shielding geometry category.

**Готово, если:** статистика совпадает с ожидаемым mean/variance, а источник данных
нуклида ведёт к NNDC/IAEA.

### Проект 16. Мирный реакторный тепловой контур

- abstract neutron power;
- temperature feedback;
- control/shutdown;
- coolant flow and heat exchanger;
- decay heat;
- containment and monitoring.

**Готово, если:** shutdown прекращает цепную мощность, но не обнуляет decay heat; потеря
теплоотвода проходит через несколько защитных состояний, а не мгновенный scripted boom.

---

## 27. Предлагаемый 16-недельный маршрут

| Недели | Теория | Проект |
|---|---|---|
| 1–2 | SI, размерности, моль, формулы | 1–2 |
| 3–4 | общая химия, газы, растворы | 3–4 |
| 5–6 | термодинамика и равновесие | 5–6 |
| 7 | кинетика и катализ | улучшение 5 |
| 8 | аналитика и uncertainty | 13 |
| 9 | transport и аппараты | 7 |
| 10 | электрохимия и цепи | 8–9 |
| 11 | органика и полимеры | 10 |
| 12–13 | кристаллы, металлургия, фазы | 11–12 |
| 14 | corrosion, GHS, process safety | 14 |
| 15 | ядерные основы и детектирование | 15 |
| 16 | гражданская энергетика и интеграция | 16 + vertical slice |

После каждого блока:

1. одна страница конспекта;
2. пять задач вручную;
3. один воспроизводимый notebook;
4. один production-тест;
5. одна запись в матрице источников;
6. review игрового упрощения.

---

## 28. Критерии проверки каждой механики

### 28.1 Научная корректность

- определены system boundary и state variables;
- выписаны conservation laws;
- формула размерностно корректна;
- указаны assumptions;
- указан valid range;
- есть минимум один benchmark;
- при достаточных данных указана uncertainty.

### 28.2 Данные

- первичный или авторитетный агрегирующий источник;
- version/date;
- разрешённая лицензия;
- единицы и reference state;
- автоматический validation import;
- отсутствие ручного копирования неизвестного происхождения.

### 28.3 Численные методы

- timestep/refinement study;
- bounded state;
- convergence/failure behavior;
- deterministic save/load;
- no NaN/Infinity;
- стабильность на min/max игровых параметрах;
- conservation residual visible in tests.

### 28.4 Игровой дизайн

- решение игрока, а не одна обязательная кнопка;
- observable cause and effect;
- цена accuracy/throughput/safety;
- graceful simplification;
- понятные единицы и tooltip;
- ошибки объясняются;
- нет бессмысленного дублирования другого мода.

### 28.5 Безопасность

- hazard labels traceable to GHS/SDS;
- инженерные меры эффективнее одного PPE-бонуса;
- несовместимости и pressure/thermal risks учтены;
- emergency state восстанавливаем;
- ядерная ветвь отражает ALARA и defense in depth;
- документация не превращается в практическое руководство по опасному синтезу или
  оружейным технологиям.

### 28.6 Совместимость

- canonical tag/identity mapping;
- energy and substance round-trip tests;
- no dupe loop;
- recipe ownership documented;
- server/client deterministic;
- localization keys present;
- официальные названия модов не переводятся.

---

## 29. Рекомендуемая структура репозитория данных

```text
chemistry/
  schema/
    substance.schema.json
    reaction.schema.json
    material.schema.json
    nuclide.schema.json
    process_unit.schema.json
  data/
    elements/
    nuclides/
    species/
    substances/
    reactions/
    materials/
    hazards/
  sources/
    registry.json
    licenses.json
  importers/
  validators/
  benchmarks/
  notebooks/
  docs/
```

### 29.1 CI-проверки

- JSON Schema validation;
- duplicate IDs and aliases;
- atom/charge balance;
- fraction sum and non-negative bounds;
- dimension/unit validation;
- source URL and required metadata presence;
- license allowlist;
- equilibrium/thermo consistency where applicable;
- decay branching sum with documented exceptions;
- benchmark tolerances;
- adapter round trips;
- localization key coverage.

---

## 30. Первая вертикальная версия химического направления

Не начинать со ста веществ и двадцати машин. Первый убедительный срез:

1. вода и одна безопасная учебная salt system;
2. весы, thermometer, pH/conductivity measurement;
3. tank with temperature, pressure and composition;
4. heater/cooler with energy balance;
5. one balanced reversible or phase process;
6. filter/crystallizer or simple separator;
7. electrolyzer with Faraday and electrical balance;
8. one corrosion/material-compatibility choice;
9. waste stream and recovery option;
10. certificate of analysis;
11. adapter to one existing energy mod;
12. test suite and source registry.

Затем можно добавлять alloy/polymer vertical slice, и только после устойчивой thermal,
measurement и safety base — nuclear educational slice.

---

## 31. Краткая матрица книг по задачам

| Задача | Русская траектория | Международная траектория |
|---|---|---|
| Общая химия | Ахметов | OpenStax; MIT 5.111 |
| Неорганика | Третьяков; Ахметов | Shriver & Atkins; Greenwood & Earnshaw |
| Органика | Реутов—Курц—Бутин | Clayden; Carey & Sundberg; March |
| Физхимия | Эткинс в русском переводе; курсы МГУ | Atkins; McQuarrie & Simon |
| Квантовая база | Ландау—Лифшиц; Фок | McQuarrie; Levine |
| Кинетика | Семёнов | Espenson; Masel; Fogler |
| Аналитика | русские переводы Harris/Skoog | Harris; Skoog |
| Электрохимия | Дамаскин; Баготский; Фрумкин | Bard & Faulkner; Newman |
| Полимеры | Каргин; русские переводы | Odian; Sperling; Flory |
| Материаловедение | Гуляев; переводы Callister | Callister; Askeland; Porter |
| Металлургия | Гуляев; учебники МИСИС | Gaskell; ASM; CALPHAD |
| Процессы | Касаткин; Романков | Felder; Bird; Fogler; Seader |
| Электричество | Сивухин; Тамм; Иродов | Purcell; Griffiths; Chapman |
| Радиохимия | Бекман | Friedlander et al.; IAEA data |
| Ядерная инженерия | Сивухин; профильные курсы МИФИ | MIT 22.01; Lamarsh; Knoll |
| Безопасность | официальные переводы/версии GHS | UNECE GHS; ACS RAMP; CCPS; IAEA |

---

## 32. Финальный принцип

Хорошая химическая механика отвечает не только на вопрос «что положить в машину», но и
на вопросы:

- сколько и в каких единицах;
- почему реакция возможна;
- как быстро она идёт;
- куда ушли атомы, заряд и энергия;
- какая фаза и какие условия;
- как отделён и измерен продукт;
- что произошло с примесями;
- чем рискует установка;
- куда ушли отходы;
- как источник и допущения проверяются;
- не реализована ли эта функция уже другим модом.

Именно такая цепочка превращает набор рецептов в учебный инженерный мод.
