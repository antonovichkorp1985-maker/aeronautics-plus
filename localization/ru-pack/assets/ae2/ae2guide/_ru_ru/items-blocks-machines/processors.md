---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Процессоры
  icon: logic_processor
  position: 010
categories:
- misc ingredients blocks
item_ids:
- ae2:logic_processor
- ae2:calculation_processor
- ae2:engineering_processor
- ae2:printed_silicon
- ae2:printed_logic_processor
- ae2:printed_calculation_processor
- ae2:printed_engineering_processor
- ae2:silicon
---

#процессоры

<Row>
  <ItemImage id="logic_processor" scale="4" />

  <ItemImage id="calculation_processor" scale="4" />

  <ItemImage id="engineering_processor" scale="4" />
</Row>

Процессоры являются одним из основных ингредиентов в AE2.../ae2-mechanics/devices.md) и машин. Они также являются одними из первых
Большие проблемы автоматизации. Существует три типа процессоров, изготовленных из золота.<ItemLink id="certus_quartz_crystal" />,
алмазов соответственно. Они изготавливаются с использованием [прессов].presses.md) в одном<ItemLink id="inscriber" />В многошаговом
процесс (обычно достигается с помощью серии вписателей и фильтрованных трубопроводов).

##Производственные шаги

<Column gap="5">
1. Собрать/сделать необходимые ингредиенты: кремний, красный камень, золото,<ItemLink id="certus_quartz_crystal" />Бриллиант.

  <RecipeFor id="silicon" />

  <br />

2. Нажать необходимые компоненты печатной схемы

  <Row>
    <RecipeFor id="printed_silicon" />

    <RecipeFor id="printed_logic_processor" />
  </Row>

  <Row>
    <RecipeFor id="printed_calculation_processor" />

    <RecipeFor id="printed_engineering_processor" />
  </Row>

  <br />

3.Окончательное собрание

  <Row>
    <RecipeFor id="logic_processor" />

    <RecipeFor id="calculation_processor" />
  </Row>

  <RecipeFor id="engineering_processor" />
</Column>
