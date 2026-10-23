---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Складские клетки
  icon: item_storage_cell_1k
  position: 410
categories:
- tools
item_ids:
- ae2:item_cell_housing
- ae2:fluid_cell_housing
- ae2:cell_component_1k
- ae2:cell_component_4k
- ae2:cell_component_16k
- ae2:cell_component_64k
- ae2:cell_component_256k
- ae2:item_storage_cell_1k
- ae2:item_storage_cell_4k
- ae2:item_storage_cell_16k
- ae2:item_storage_cell_64k
- ae2:item_storage_cell_256k
- ae2:fluid_storage_cell_1k
- ae2:fluid_storage_cell_4k
- ae2:fluid_storage_cell_16k
- ae2:fluid_storage_cell_64k
- ae2:fluid_storage_cell_256k
---

# Складские клетки

<Column>
  <Row>
    <ItemImage id="item_storage_cell_1k" scale="4" />

    <ItemImage id="item_storage_cell_4k" scale="4" />

    <ItemImage id="item_storage_cell_16k" scale="4" />

    <ItemImage id="item_storage_cell_64k" scale="4" />

    <ItemImage id="item_storage_cell_256k" scale="4" />
  </Row>

  <Row>
    <ItemImage id="fluid_storage_cell_1k" scale="4" />

    <ItemImage id="fluid_storage_cell_4k" scale="4" />

    <ItemImage id="fluid_storage_cell_16k" scale="4" />

    <ItemImage id="fluid_storage_cell_64k" scale="4" />

    <ItemImage id="fluid_storage_cell_256k" scale="4" />
  </Row>
</Column>

Ячейки хранения являются одним из основных методов хранения в прикладной энергетике. Они входят в<ItemLink id="drive" />s
или<ItemLink id="chest" />s.

См. [Bytes and Types]../ae2-mechanics/bytes-and-types.md) для объяснения их возможностей в байтах и типах.

Компоненты хранения могут быть удалены из корпуса, если ячейка пуста, щелкнув правой рукой.

<Row>
    <Recipe id="upgrade/item_storage_cell_1k_to_4k" />

Вы можете обновить ячейки хранения до более высоких уровней, объединив их с компонентами хранения более высокого уровня в крафтовой сетке. Их содержание будет сохранено, а компонент нижнего уровня возвращен.
</Row>

## Вместимость хранилища с переменным счетом типов

[Авансовая стоимость типов]../ae2-mechanics/bytes-and-types.md) является такой, что ячейка, содержащая 1 тип, может содержать в 2 раза больше, чем ячейка со всеми 63 типами в использовании.

Общая емкость ячейки с 1 типом в использовании | Общая емкость ячейки с 63 типами в использовании
| ---------------------------------------- | ----------------------------------------: | ------------------------------------------: |
| <ItemLink id="item_storage_cell_1k" />   |                                     8,128 |                                       4,160 |
| <ItemLink id="item_storage_cell_4k" />   |                                    32,512 |                                      16,640 |
| <ItemLink id="item_storage_cell_16k" />  |                                   130,048 |                                      66,560 |
| <ItemLink id="item_storage_cell_64k" />  |                                   520,192 |                                     266,240 |
| <ItemLink id="item_storage_cell_256k" /> |                                 2,080,768 |                                   1,064,960 |


## Разделение

Клетки могут быть отфильтрованы, чтобы принимать только определенные элементы.<ItemLink id="storage_bus" />SES можно фильтровать. Это
выполненный в<ItemLink id="cell_workbench" />.

Элементы могут быть втянуты в слоты от JEI / REI, даже если у вас нет этого элемента.

## Обновления

Ячейки хранения поддерживают следующие [обновления]upgrade_cards.md), вставляется через а<ItemLink id="cell_workbench" />:

*   <ItemLink id="fuzzy_card" />(недоступно на жидких клетках) позволяет клетке быть разделенной по уровню повреждения и / или игнорировать элемент NBT.
*   <ItemLink id="inverter_card" />Переключает фильтр из белого списка в черный.
*   <ItemLink id="equal_distribution_card" />распределяет одинаковое количество байтового пространства для каждого типа, поэтому один тип не может заполнить всю ячейку.
*   <ItemLink id="void_card" />пустоты предметов, вставленных, если ячейка заполнена (или выделенное пространство этого конкретного типа в ячейке).
Случай с равной картой распределения, полезный для остановки ферм от резервного копирования. Будьте осторожны, чтобы разделить это!
Портативные ячейки могут принимать<ItemLink id="energy_card" />Чтобы увеличить емкость батареи

## раскраска

Портативные элементы и жидкие ячейки могут быть окрашены подобно кожаной брони, создавая их вместе с красителями.

#Жилье

Клетки могут быть изготовлены с компонентом хранения и корпусом или с рецептом корпуса вокруг компонента хранения:

<Row>
  <Recipe id="network/cells/item_storage_cell_1k" />

  <Recipe id="network/cells/item_storage_cell_1k_storage" />
</Row>

Сами по себе дома построены так:

<Row>
  <RecipeFor id="item_cell_housing" />

  <RecipeFor id="fluid_cell_housing" />
</Row>

# Компоненты хранения

Компоненты хранения являются ядром всех клеток AE2, определяя емкость клеток. Каждый уровень увеличивает емкость
в 4 раза и стоит 3 предыдущего уровня.

<Column>
  <Row>
    <RecipeFor id="cell_component_1k" />

    <RecipeFor id="cell_component_4k" />

    <RecipeFor id="cell_component_16k" />
  </Row>

  <Row>
    <RecipeFor id="cell_component_64k" />

    <RecipeFor id="cell_component_256k" />
  </Row>
</Column>

# Элементы хранения клеток

Ячейки для хранения предметов могут содержать до 63 различных типов предметов и доступны во всех стандартных емкостях.

<Column>
  <Row>
    <Recipe id="network/cells/item_storage_cell_1k_storage" />

    <Recipe id="network/cells/item_storage_cell_4k_storage" />

    <Recipe id="network/cells/item_storage_cell_16k_storage" />
  </Row>

  <Row>
    <Recipe id="network/cells/item_storage_cell_64k_storage" />

    <Recipe id="network/cells/item_storage_cell_256k_storage" />
  </Row>
</Column>

### Портативное хранение предметов

Они действуют как крошечные<ItemLink id="chest" />в кармане, или в виде рюкзака. Их можно обвинить в<ItemLink id="charger" />

В отличие от стандартных ячеек хранения, они фактически уменьшают емкость по типу, поскольку их байтовая емкость увеличивается, и имеют половину емкости.
Общая емкость байта.

В дополнение к картам обновления, которые могут получить все ячейки, они также принимают<ItemLink id="energy_card" />Обновление внутренних батарей.

<Column>
  <Row>
    <RecipeFor id="portable_item_cell_1k" />

    <RecipeFor id="portable_item_cell_4k" />

    <RecipeFor id="portable_item_cell_16k" />
  </Row>

  <Row>
    <RecipeFor id="portable_item_cell_64k" />

    <RecipeFor id="portable_item_cell_256k" />
  </Row>
</Column>

# Жидкие камеры хранения

Жидкие ячейки хранения могут содержать до 5 различных типов жидкостей и доступны во всех стандартных емкостях.

<Column>
  <Row>
    <Recipe id="network/cells/fluid_storage_cell_1k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_4k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_16k_storage" />
  </Row>

  <Row>
    <Recipe id="network/cells/fluid_storage_cell_64k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_256k_storage" />
  </Row>
</Column>

### Портативное хранение жидкости

Они действуют как крошечные<ItemLink id="chest" />в кармане, или в виде рюкзака. Их можно обвинить в<ItemLink id="charger" />

В отличие от стандартных ячеек хранения, они фактически уменьшают емкость по типу, поскольку их байтовая емкость увеличивается, и имеют половину емкости.
Общая емкость байта.

В дополнение к картам обновления, которые могут получить все ячейки, они также принимают<ItemLink id="energy_card" />Обновление внутренних батарей.

<Column>
  <Row>
    <RecipeFor id="portable_fluid_cell_1k" />

    <RecipeFor id="portable_fluid_cell_4k" />

    <RecipeFor id="portable_fluid_cell_16k" />
  </Row>

  <Row>
    <RecipeFor id="portable_fluid_cell_64k" />

    <RecipeFor id="portable_fluid_cell_256k" />
  </Row>
</Column>

Креативная ячейка хранения

<Row>
  <ItemImage id="creative_storage_cell" scale="2" />
</Row>

Творческие ячейки не обеспечивают бесконечного хранения. Вместо этого они действуют как бесконечные источники и поглотители чего угодно.
Для вас или для вас (для вас)cell_workbench.md) им.
