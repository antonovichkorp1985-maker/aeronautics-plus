---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Самолет уничтожения
  icon: annihilation_plane
  position: 210
categories:
- devices
item_ids:
- ae2:annihilation_plane
---

#План уничтожения

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/annihilation_plane.snbt" />
</GameScene>

Самолет Аннигиляции разбивает блоки и подбирает предметы. Работает аналогично тому, как<ItemLink id="import_bus" />толкать вещи
в [сетевое хранилище]../ae2-mechanics/import-export-storage.md). Для того, чтобы предметы были подобраны, они должны столкнуться с
Лицо самолета, он не поднимается в районе.

Самолеты уничтожения могут быть очарованы любым очарованием кирки, так что да, вы можете положить сумасшедшие уровни состояния на несколько и даже больше.
[Автоматная обработка руды]../example-setups/ore-fortuner.md) если ваш модуль позволяет это. Шелковое прикосновение делает то, что
Как и следовало ожидать, эффективность снижает затраты на энергию при разрушении блока и дает шанс не использовать энергию.

Они [кабельные части]../ae2-mechanics/cable-subparts.md).

**Вспоминайте ненастоящих фальшивых игроков в вашем шуточном преступлении**

## Фильтрация

Плоскость аннигиляции сломает блок или подберет предмет, только если он может хранить полученные капли / предметы.
в своей сети. Это означает, что для фильтрации нужно ограничить то, что может храниться в его сети, скорее всего, поставив
в [подсети]../ae2-mechanics/subnetworks.md). A <ItemLink id="storage_bus" />или [ячейка]../items-blocks-machines/storage_cells.md)
(может быть разделена)cell_workbench.md) добиться этого.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/annihilation_filtering.snbt" />

  <DiamondAnnotation pos="1 0.5 0.5" color="#00ff00">
Фильтровать все, что падает с того, что вы хотите сломать.
  </DiamondAnnotation>

  <DiamondAnnotation pos=".5 0.5 2.5" color="#00ff00">
Разделен на все, что падает с того, что вы хотите сломать.
  </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Опять же, он фильтрует * по элементу падает * так, например, если вы хотите фильтровать взлом<ItemLink id="minecraft:amethyst_cluster" />s,
Вам нужен самолет, очарованный шелковым прикосновением, иначе каждая предыдущая стадия роста ничего не роняет, и поэтому самолет сломает их независимо от того, что вы делаете.
Что, как сеть всегда может хранить «ничего».

## Рецепт

<RecipeFor id="annihilation_plane" />
