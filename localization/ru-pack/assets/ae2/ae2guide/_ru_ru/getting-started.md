---
navigation:
  title: Начало работы (1.20+)
  position: 10
---

<div class="notification is-info">
Следующая информация относится только к прикладной энергетике 2 в Minecraft 1.20 и более новой.
</div>

# Начало работы

## Получение исходных материалов

<GameScene zoom="4" background="transparent">
  <ImportStructure src="assets/assemblies/meteor_interior.snbt" />
</GameScene>

Чтобы начать работу с Applied Energistics 2, сначала нужно найти [метеорит].ae2-mechanics/meteorites.md). Они довольно распространены и, как правило, оставляют массивные отверстия в местности, поэтому вы, вероятно, сталкивались с ними в своих путешествиях.
Если нет, вы можете создать<ItemLink id="meteorite_compass" />Который указывает на ближайший<ItemLink id="mysterious_cube" />.

Как только вы нашли метеорит, мой в его центр. Вы найдете кварцевые кластеры Certus, кварцевые почки Certus, [блоки Budding Certus]items-blocks-machines/budding_certus.md) различных типов и таинственного куба в центре.

Разминируйте кварцевые кластеры и любые кварцевые блоки, которые вы найдете. Вы также можете подобрать почковательные блоки церта, но без шелкового прикосновения они разлагаются на 1 ярус.

Не ломайте ни одного безупречного почковательного церта, так как даже при шелковом прикосновении они будут деградировать до дефектного почковательного церта, и невозможно восстановить их обратно до безупречного.

Также разминируйте Таинственный куб в центре метеорита, чтобы получить все 4 нажатия.

## Растущий Certus Quartz

<GameScene zoom="4" background="transparent">
<ImportStructure src="assets/assemblies/budding_certus_1.snbt" />
</GameScene>

Кварцевые почки Certus будут прорастать из [пузырчатых блоков Certus]items-blocks-machines/budding_certus.md), аналогично аметисту. Если вы сломаете бутон, который не закончен
Вырастая, выпадет один<ItemLink id="certus_quartz_dust" />Неизменное состояние. Если разбить полностью выросший кластер, он упадет на четыре
<ItemLink id="certus_quartz_crystal" />s, и состояние увеличит это число.

Существует 4 яруса почковательных блоков кертуса: безупречные, дефектные, измельченные и поврежденные.

<GameScene zoom="4" background="transparent">
<ImportStructure src="assets/assemblies/budding_blocks.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

Каждый раз, когда бутон растет на другой стадии, блок почкования имеет шанс деградировать на один уровень, в конечном итоге превращаясь в
Простой кварцевый блок. Их можно отремонтировать (и создать новые блоки), бросив блок (или блок).
кварцевый блок) в воде с одним или несколькими<ItemLink id="charged_certus_quartz_crystal" />.

<RecipeFor id="damaged_budding_quartz" />

Безупречные почковательные блоки кертуса не будут разрушаться и будут генерировать кертус бесконечно. Однако они не могут быть обработаны или перемещены.
с киркой, даже с шелковым прикосновением. (Они могут быть перемещены с [пространственным хранением])ae2-mechanics/spatial-io.mdХотя)

Сами по себе почки кварца растут очень медленно. К счастью,<ItemLink id="growth_accelerator" />массово
ускоряет этот процесс при размещении рядом с блоком почкования. Вы должны построить некоторые из них в качестве вашего первого приоритета.

<GameScene zoom="4" background="transparent">
<ImportStructure src="assets/assemblies/budding_certus_2.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

Если у вас недостаточно кварца, чтобы сделать<ItemLink id="energy_acceptor" />или<ItemLink id="vibration_chamber" />,
Вы можете сделать<ItemLink id="crank" />Прикрепите его к концу акселератора.

Сбор кертуса автоматически [описан здесь]example-setups/simple-certus-farm.md).

## Быстрая сторона Fluix

Другим материалом, который вам понадобится, является Fluix, с которым вы уже сталкивались при создании ускорителей роста. Он производится путем бросания заряженного кертуса, красного камня и чистого кварца в воду. Делать это автоматически «оставлено в качестве упражнения для читателя».

The<ItemLink id="charger" />Требуется производить<ItemLink id="charged_certus_quartz_crystal" />Если вы еще не сделали его.

## Назначение некоторых процессоров

В разграблении метеорита вы найдете четыре «пресса» от разрушения Таинственного куба. Они используются в<ItemLink id="inscriber" />Выберите три типа процессора.

<ItemGrid>
  <ItemIcon id="silicon_press" />

  <ItemIcon id="logic_processor_press" />

  <ItemIcon id="calculation_processor_press" />

  <ItemIcon id="engineering_processor_press" />
</ItemGrid>

Вписывающий — боковая машина, очень похожая на ванильную печь. Вставка сверху или снизу помещает предметы в верхние или нижние слоты и вставка из боковых или задних вставок в центральный слот. Результаты могут быть извлечены со стороны или сзади.

Чтобы облегчить автоматизацию с помощью хопперов (и, возможно, уменьшить спагетти труб), вписыватели могут вращаться с помощью<ItemLink id="certus_quartz_wrench" />.

Изготовьте несколько процессоров каждого типа для подготовки к следующему шагу, создав базовую систему ME. Автоматизация производства процессоров "[оставлена в качестве упражнения для читателя]example-setups/processor-automation.md)".

Материальная энергетика: сети и хранение

#### Что такое хранение?

Его произносится как Emm-Eee и обозначает энергию материи.

Matter Energy является основным компонентом Applied Energistics 2, она похожа на безумную научную версию сундука с несколькими блоками.
Это может революционизировать ситуацию с хранением. ME сильно отличается от других систем хранения в Minecraft.
Это может занять немного времени, чтобы привыкнуть; но как только вы начнете, огромное количество хранения в крошечных емкостях.
Пространство и терминалы с множественным доступом являются лишь верхушкой айсберга того, что становится возможным.

#### Что мне нужно знать, чтобы начать?

Сначала я Хранит предметы внутри других предметов, называемых [ячейки хранения]items-blocks-machines/storage_cells.mdЕсть 5 уровней с постоянно растущим количеством
хранилище. Для того, чтобы использовать ячейку для хранения, она должна быть размещена внутри любого из<ItemLink id="chest" />,
или<ItemLink id="drive" />.

The<ItemLink id="chest" />показывает вам содержимое ячейки, как только она помещается внутрь, и вы
Он может добавлять и удалять предметы из него, как если бы он был<ItemLink id="minecraft:chest" />За исключением того, что предметы
на самом деле хранится в камерах хранения, а не в<ItemLink id="chest" />себя.

The<ItemLink id="chest" />Она довольно ситуативна и ограничена в полезности. Действительно
Воспользуйтесь преимуществами AE2, вам нужно настроить [ME Network]ae2-mechanics/me-network-connections.md).

## Ваша первая система

Теперь, когда у вас есть все основные материалы и машины для прикладной энергетики 2, вы можете создать свою первую систему ME (Matter Energy). Это будет очень простой, без автопроизводства, без логистики, просто хорошее, простое, доступное для поиска хранилище.

<GameScene zoom="6" interactive={true}>
<ImportStructure src="assets/assemblies/tiny_me_system.snbt" />

</GameScene>

* Ваш список ингредиентов:
    * 1x <ItemLink id="drive" />
    * 1x <ItemLink id="terminal" />или<ItemLink id="crafting_terminal" />
    * 1x <ItemLink id="energy_acceptor" />
* Несколько [кабелей]items-blocks-machines/cables.md), либо стеклянный, покрытый, либо умный, но не плотный
* Несколько [камер хранения]items-blocks-machines/storage_cells.md), рекомендованный сорт 4k для хорошей смеси
вместимость и типы (было бы эффективнее [разделить])items-blocks-machines/cell_workbench.md) смесь 4k и 1k, но это сложность, в которую мы не будем вдаваться сейчас
---
1.  Place the drive down.
2.  The energy acceptor (and several other AE2 [devices](ae2-mechanics/devices.md)) comes in 2 modes, cube and flat. They can be switched between in a crafting grid. If your energy acceptor is a cube, place it down next to the drive. If it's a flat square, place a cable on the drive and place the acceptor on that.
3.  Run energy into the energy acceptor with a cable/pipe/conduit from your favorite energy-generation mod.
4.  Place a cable on top of the drive (or otherwise at eye level) and place your terminal or crafting terminal on it.
5.  Put your storage cells into the drive
6.  Profit
7.  Fiddle with the terminal's settings
8.  Bask in your ultimate power and ability
9.  Realize that this network is, in the grand scheme, rather small

### Expanding your Network

So you have some basic storage, and access to that storage, it's a good start, but you'll likely be looking to maybe
automate some processing.

A great example of this is to place a <ItemLink id="export_bus" /> on the top of a furnace to
dump in ores, and a <ItemLink id="import_bus" />
on the bottom of the furnace to extract furnaced ores.

The <ItemLink id="export_bus" /> lets you export items from the network, into the attached
inventory, while the <ItemLink id="import_bus" /> imports items from the attached inventory into
the network.

### Overcoming Limits

At this point you probably getting close to 8 or so [devices](ae2-mechanics/devices.md), once you hit 9 devices you'll have to start
managing [channels](ae2-mechanics/channels.md). Many devices but not all, require a channel to
function.

By default a network can support 8 channels, once you break this limit, you'll have to add
an <ItemLink id="controller" /> to your network. this allows you to expand your network greatly.
[Smart cables](items-blocks-machines/cables.md) will allow you to see how channels are routed through your network. Use them extensively when starting out to learn how channels act, or if you have a lot of redstone and glowstone.
