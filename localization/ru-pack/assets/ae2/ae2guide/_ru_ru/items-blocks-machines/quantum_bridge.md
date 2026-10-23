---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Квантовый мост
  icon: quantum_ring
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:quantum_link
- ae2:quantum_ring
---

Квантовый сетевой мост

Сформированный Квантовый сетевой мост (../assets/diagrams/quantum_bridge_demonstration.png)

Квантовые сетевые мосты могут расширять сеть../ae2-mechanics/me-network-connections.md) на бесконечные расстояния и даже между измерениями.
Они могут нести 32 канала в общей сложности (независимо от того, как кабели соединены с каждой лицевой стороной).
Действует как беспроводной [плотный кабель]cables.md#dense-cable).

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/quantum_bridge_internal_structure_1.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/quantum_bridge_internal_structure_2.snbt" />

  <BoxAnnotation color="#33dd33" min="1 1 1" max="6 2 3">
воображаемый кабель между двумя конечными точками
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Следует отметить, что ** обе стороны должны быть выгружены ** так, чтобы<ItemLink id="spatial_anchor" />или должен быть использован другой хлам
2 стороны находятся далеко друг от друга.

Квантовое кольцо

<BlockImage id="quantum_ring" scale="8" />

Восемь блоков, расположенных вокруг<ItemLink id="quantum_link" />будет создавать
Квантовый сетевой мост. Только 4<ItemLink id="quantum_ring" />блоки, прилегающие к
тот<ItemLink id="quantum_link" />будет принимать сетевые соединения,
4 угловых блока не могут подключаться к кабелям.

## Рецепт

<RecipeFor id="quantum_ring" />

Квантовая камера связи

<BlockImage id="quantum_link" scale="8" />

Один из таких блоков окружен<ItemLink id="quantum_ring" />
Это позволит создать квантовый сетевой мост. Этот блок не подключается ни к одному кабелю и только регистрирует
как часть сети с полным мостом.

Этот блок может содержать только один<ItemLink id="quantum_entangled_singularity" />и является
Доступна автоматизация.

## Рецепт

<RecipeFor id="quantum_link" />
