---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Crafting CPU Multiblock (Storage, Coprocessor, Monitor, Unit)
  icon: 1k_crafting_storage
  position: 210
categories:
- devices
item_ids:
- ae2:1k_crafting_storage
- ae2:4k_crafting_storage
- ae2:16k_crafting_storage
- ae2:64k_crafting_storage
- ae2:256k_crafting_storage
- ae2:crafting_accelerator
- ae2:crafting_monitor
- ae2:crafting_unit
---

# Crafting CPU

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/crafting_cpus.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<Row>
  <BlockImage id="1k_crafting_storage" scale="4" />

  <BlockImage id="crafting_accelerator" scale="4" />

  <BlockImage id="crafting_monitor" scale="4" />

  <BlockImage id="crafting_unit" scale="4" />
</Row>

Crafting CPU управляют запросами / заданиями. Они хранят промежуточные ингредиенты, создавая рабочие места с несколькими шагами.
Это влияет на то, насколько большими могут быть рабочие места, и в какой-то степени на то, как быстро они выполняются. См. [автопрокат]../ae2-mechanics/autocrafting.md)
для более подробной информации.

Каждый процессор обработки обрабатывает 1 запрос или работу, поэтому, если вы хотите запросить как вычислительный процессор, так и 256 гладких камней одновременно, вам нужно 2 многоблока процессора.

Они могут быть настроены для обработки запросов от игроков, автоматизации (экспортные шины и интерфейсы) или обоих.

Нажатие правой кнопкой мыши поднимает UI статуса крафта, где вы можете проверить прогресс в крафтовой работе, которую обрабатывает процессор.

##Настройки

* Процессор может быть настроен на прием запросов только от игроков, только автоматизация (например,<ItemLink id="export_bus" />Сес с
    <ItemLink id="crafting_card" />или и то, и другое.

## Строительство

Crafting CPU являются многоблоковыми и должны быть сплошными прямоугольными призмами без зазоров. Они состоят из нескольких компонентов.

Каждый процессор должен содержать не менее 1 блока хранения крафта (и минимальный жизнеспособный процессор на самом деле является всего лишь одним хранилищем крафта 1k).

#Крафтовый блок

<BlockImage id="crafting_unit" scale="4" />

(Необязательно) Креативные блоки просто заполняют пространство в процессоре, чтобы сделать его сплошной прямоугольной призмой, если у вас недостаточно
других компонентов. Они также являются основным ингредиентом в других компонентах.

<RecipeFor id="crafting_unit" />

#Крафтовый склад

<Row>
  <BlockImage id="1k_crafting_storage" scale="4" />

  <BlockImage id="4k_crafting_storage" scale="4" />

  <BlockImage id="16k_crafting_storage" scale="4" />

  <BlockImage id="64k_crafting_storage" scale="4" />

  <BlockImage id="256k_crafting_storage" scale="4" />
</Row>

(Требуемые) Ремесленные хранилища доступны во всех стандартных размерах ячеек (1k, 4k, 16k, 64k, 256k). Они хранят ингредиенты и
промежуточные ингредиенты, участвующие в ремесле, поэтому для процессора требуется больше или больше хранилищ для обработки рабочих мест
Больше ингредиентов.

<Column>
  <Row>
    <RecipeFor id="1k_crafting_storage" />

    <RecipeFor id="4k_crafting_storage" />

    <RecipeFor id="16k_crafting_storage" />
  </Row>

  <Row>
    <RecipeFor id="64k_crafting_storage" />

    <RecipeFor id="256k_crafting_storage" />
  </Row>
</Column>

# Crafting Co-Processing Unit

<BlockImage id="crafting_accelerator" scale="4" />

(Необязательно) Совместные процессоры заставляют систему отправлять партии ингредиентов из<ItemLink id="pattern_provider" />более часто
Чтобы CPU работал быстрее.
Это позволяет им идти в ногу с машинами, которые быстро обрабатываются. Примером этого является поставщик шаблонов, окруженный
<ItemLink id="molecular_assembler" />s способность выталкивать ингредиенты быстрее, чем может обрабатывать один сборщик, и, таким образом,
распределение партий ингредиентов между окружающими сборщиками.

Некоторые сложные рецепты имеют несколько шагов, которые могут быть выполнены параллельно, например, изготовление досок и книг одновременно для изготовления книжных полок.
На экране состояния крафта (видимо, щелкнув правой кнопкой мыши CPU или с значком молотка в [терминале])terminals.md), эти
Все шаги будут отображаться как «запланированные». Каждый дополнительный сопроцессор позволяет выполнять еще один из этих шагов параллельно (и, таким образом, отображается как «создание»).
Тем не менее, это не так актуально, потому что обычно у вас будет больше сопроцессоров исключительно для скорости вставки, чем в рецепте есть шаги, которые могут быть выполнены параллельно.

<RecipeFor id="crafting_accelerator" />

# Монитор ремесла

<BlockImage id="crafting_monitor" scale="4" />

(необязательно) Монитор крафта отображает работу, которую процессор выполняет в данный момент.
Экран может быть окрашен в<ItemLink id="color_applicator" />.

<RecipeFor id="crafting_monitor" />
