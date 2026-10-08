---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Пространственные клетки
  icon: spatial_storage_cell_128
  position: 410
categories:
- tools
item_ids:
- ae2:spatial_storage_cell_2
- ae2:spatial_storage_cell_16
- ae2:spatial_storage_cell_128
- ae2:spatial_cell_component_2
- ae2:spatial_cell_component_16
- ae2:spatial_cell_component_128
---

# Пространственные камеры хранения

  <Row>
    <ItemImage id="spatial_storage_cell_2" scale="4" />

    <ItemImage id="spatial_storage_cell_16" scale="4" />

    <ItemImage id="spatial_storage_cell_128" scale="4" />
  </Row>

Пространственные камеры хранения используются для хранения физических объемов пространства.../ae2-mechanics/spatial-io.md). 
Они используются в<ItemLink id="spatial_io_port" />.

В отличие от [хранилища]../items-blocks-machines/storage_cells.mdПространственные клетки не могут быть переформатированы.

Опять же, ** Вы не можете восстановить, переформатировать или восстановить испарительный сигнал после того, как он был использован.** Создайте новую ячейку, если хотите использовать разные размеры.


## Рецепты

  <Row>
    <Recipe id="network/cells/spatial_storage_cell_2_cubed_storage" />

    <Recipe id="network/cells/spatial_storage_cell_16_cubed_storage" />

    <Recipe id="network/cells/spatial_storage_cell_128_cubed_storage" />
  </Row>

#Жилье

Клетки могут быть изготовлены с пространственным компонентом и корпусом или с рецептом корпуса вокруг пространственного компонента:

<Row>
  <Recipe id="network/cells/spatial_storage_cell_2_cubed" />

  <Recipe id="network/cells/spatial_storage_cell_2_cubed_storage" />
</Row>

Сами по себе дома построены так:

  <RecipeFor id="item_cell_housing" />

# Пространственные компоненты

Пространственные компоненты являются ядром пространственных ячеек хранения. Каждый уровень увеличивает размеры объема, который может быть
Сохраняется в 8 раз.

  <Row>
    <RecipeFor id="spatial_cell_component_2" />

    <RecipeFor id="spatial_cell_component_16" />

    <RecipeFor id="spatial_cell_component_128" />
  </Row>