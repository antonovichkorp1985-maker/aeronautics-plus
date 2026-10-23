---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматическая руда Fortuner
  icon: minecraft:raw_iron
---

Автоматизация Ore Fortuning

The<ItemLink id="annihilation_plane" />может быть очарован любым очарованием кирки, включая удачу, поэтому очевидным вариантом использования является
Применять богатство к нескольким и иметь<ItemLink id="formation_plane" />s и<ItemLink id="annihilation_plane" />быстрое место и
ломать руды.

Отметим, что с тех пор<ItemLink id="import_bus" />SES «вращение до скорости», установка начнет медленно, а затем достигнет полной скорости через несколько секунд.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/ore_fortuner.snbt" />

  <BoxAnnotation color="#dddddd" min="2.7 0 2" max="3 1 3">
(1) Импортный автобус: имеет несколько карт ускорения.
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 2" max="2 1 2.3">
(2) Планы формирования: В их конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 0.7" max="2 1 1">
(3) Планы уничтожения: нет графического интерфейса для настройки, но очарованы Fortune.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 0 0" max="3 1 1">
(4) Складской автобус: В своей конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="3.5 0.5 2.5" color="#00ff00">
Ввод
    </DiamondAnnotation>

<DiamondAnnotation pos="3.5 0.5 0.5" color="#00ff00">
выход
    </DiamondAnnotation>

<DiamondAnnotation pos="4 0.5 1.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* The<ItemLink id="import_bus" />1 имеет несколько<ItemLink id="speed_card" />В нем. Требуется больше плоскостей формирования
Они находятся в массиве, так как они заставляют импортный автобус тянуть больше предметов одновременно.
* The<ItemLink id="formation_plane" />s (2) находятся в своих конфигурациях по умолчанию.
* The<ItemLink id="annihilation_plane" />s (3) не имеют графического интерфейса и не могут быть настроены, но очарованы богатством.
* The<ItemLink id="storage_bus" />(4) находится в конфигурации по умолчанию.

## Как это работает

1. The<ItemLink id="import_bus" />на зеленую подсеть импортируют блоки из первой бочки в [сетевое хранилище]../ae2-mechanics/import-export-storage.md)
2. Единственным хранилищем в зеленой подсети является<ItemLink id="formation_plane" />которые помещают блоки.
3. The<ItemLink id="annihilation_plane" />оранжевая подсеть разбивает блоки, прикладывая к ним удачу.
4. The<ItemLink id="storage_bus" />на оранжевой подсети хранит результаты пробоя во второй стволе.
