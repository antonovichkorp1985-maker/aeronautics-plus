---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: Certus Рост
  icon: quartz_cluster
---

#Сертус Рост

### По сути, просто копировать-вставлено со страницы начала работы

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/budding_certus_1.snbt" />
</GameScene>

Кварцевые почки Certus будут прорастать из [пузырчатых блоков Certus]../items-blocks-machines/budding_certus.md), аналогично аметисту. Если вы сломаете бутон, который не закончен
Вырастая, выпадет один<ItemLink id="certus_quartz_dust" />Неизменное состояние. Если разбить полностью выросший кластер, он упадет на четыре
<ItemLink id="certus_quartz_crystal" />s, и состояние увеличит это число.

Есть 4 яруса почковательных блоков кертуса: безупречный, дефектный, прорезанный и поврежденный, и вы изначально
Найдите их в [метеоритах]../ae2-mechanics/meteorites.md).

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_blocks.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Каждый раз, когда бутон растет на другой стадии, блок почкования имеет шанс деградировать на один уровень, в конечном итоге превращаясь в
Простой кварцевый блок. Их можно отремонтировать (и создать новые блоки), бросив блок (или блок).
кварцевый блок) в воде с одним или несколькими<ItemLink id="charged_certus_quartz_crystal" />.

<RecipeFor id="damaged_budding_quartz" />

Безупречные почковательные блоки кертуса не будут разрушаться и будут генерировать кертус бесконечно. Однако они не могут быть обработаны или перемещены.
с киркой, даже с шелковым прикосновением. (Они могут быть перемещены с [пространственным хранением])../ae2-mechanics/spatial-io.mdХотя)

Сами по себе почки кварца растут очень медленно. К счастью,<ItemLink id="growth_accelerator" />массово
ускоряет этот процесс при размещении рядом с блоком почкования. Вы должны построить некоторые из них в качестве вашего первого приоритета.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_certus_2.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Сложные взаимодействия означают, что каждая сторона почковательного блока, которая покрыта, замедляет кумулятивный темп роста из почковательного блока.
Это в конечном итоге подавляет эффект большего количества ускорителей. Эмпирическое тестирование показывает:

! [Предметы/мин для соотношений](../активы/диаграммы/certus_farm_speed_chart_1.png)

![общие настройки](../активы/диаграммы/certus_farm_speed_chart_2.png)

Если у вас недостаточно кварца, чтобы сделать<ItemLink id="energy_acceptor" />или<ItemLink id="vibration_chamber" />,
Вы можете сделать<ItemLink id="crank" />Прикрепите его к концу акселератора.

Сбор кертуса автоматически [описан здесь]../example-setups/simple-certus-farm.md).