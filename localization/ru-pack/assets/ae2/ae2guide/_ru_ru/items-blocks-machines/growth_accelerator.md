---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Ускоритель роста
  icon: growth_accelerator
  position: 310
categories:
- machines
item_ids:
- ae2:growth_accelerator
---

#Ускоритель роста

<BlockImage id="growth_accelerator" p:powered="true" scale="8"/>

Ускоритель роста значительно ускоряет [рост]../ae2-mechanics/certus-growth.md) кертус или аметист, когда они расположены рядом с блоком почкования.

Любопытно, что он также может ускорить рост различных растений.

Это делается путем нанесения «случайных клещей» на соседние блоки, в дополнение к случайным клещам, которые происходят естественным образом.
Теоретически это означает, что 1 ускоритель должен заставить вещи расти в 90 раз быстрее, чем обычно.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/growth_accelerator.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Мощность может быть обеспечена через верхнюю или нижнюю часть, через кабели AE2.cables.md), или другие силовые кабели мода. Он может
Принять энергию AE2 (AE) или Forge Energy (FE).

Чтобы питать его вручную, поместите<ItemLink id="crank" />сверху или снизу и щелкните правой кнопкой мыши.

Верхнюю и нижнюю части можно определить по розовому потоку греблей на них.

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/accelerator_connections.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Рецепт

<RecipeFor id="growth_accelerator" />
