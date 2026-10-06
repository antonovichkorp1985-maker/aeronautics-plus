---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Автобус Toggle
  icon: toggle_bus
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:toggle_bus
- ae2:inverted_toggle_bus
---

# Автобус Toggle

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/assemblies/toggle_bus.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

Автобус, который работает аналогично<ItemLink id="fluix_glass_cable" />кабелей, но это
Это позволяет переключать состояние соединения через Redstone. Это позволяет сократить
Отдельный раздел [ME Network]../ae2-mechanics/me-network-connections.md).

Когда подается сигнал Redstone, деталь обеспечивает подключение,<ItemLink id="inverted_toggle_bus" />обеспечивает обратную
Вместо этого он отключает связь.

Следует отметить, что их переключение может привести к перезагрузке сети и пересчету подключенных устройств.

Они [кабельные части]../ae2-mechanics/cable-subparts.md).

## Рецепты

<RecipeFor id="toggle_bus" />

<RecipeFor id="inverted_toggle_bus" />
