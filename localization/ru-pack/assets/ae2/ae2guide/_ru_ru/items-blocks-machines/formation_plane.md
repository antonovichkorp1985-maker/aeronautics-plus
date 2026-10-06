---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: План формирования
  icon: formation_plane
  position: 210
categories:
- devices
item_ids:
- ae2:formation_plane
---

#План формирования

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/formation_plane.snbt" />
</GameScene>

Самолет формации размещает блоки и сбрасывает предметы. Работает аналогично только вставке<ItemLink id="storage_bus" />,
когда в нем находится [внутреннее] устройство,../ae2-mechanics/devices.md) вставка в [сетевое хранилище]()../ae2-mechanics/import-export-storage.md),
как<ItemLink id="import_bus" />и<ItemLink id="interface" />s.

<GameScene zoom="8" interactive={true}>
  <ImportStructure src="../assets/assemblies/formation_plane_demonstration.snbt" />
  <IsometricCamera yaw="255" pitch="30" />
</GameScene>

Обратите внимание, что они похожи на импортный автобус. шина хранения и интерфейс -> трубы шины хранения в [подсети трубопровода]../example-setups/pipe-subnet.md).

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/import_storage_pipe.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_storage_pipe.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Это [устройство]../ae2-mechanics/devices.md) использует механику, используемую шинами хранения в таких вещах, как [подсети труб](../example-setups/pipe-subnet.md),
и может заменить шины хранения в этих настройках, если вы хотите сбросить элементы / разместить блоки вместо транспортных элементов.

Они [кабельные части]../ae2-mechanics/cable-subparts.md).

**Вспоминайте ненастоящих фальшивых игроков в вашем шуточном преступлении**

## Фильтрация

По умолчанию самолет будет размещать / сбрасывать что-либо. Элементы, вставленные в слоты фильтра, будут действовать как белый список.
Это позволяет размещать эти конкретные предметы.

Элементы и жидкости могут быть втянуты в слоты от JEI / REI, даже если у вас нет этого элемента.

Нажмите правой кнопкой мыши на контейнер с жидкостью (например, ведро или резервуар с жидкостью), чтобы установить эту жидкость в качестве фильтра вместо элемента ведра или резервуара.

## Приоритет

Приоритеты можно установить, щелкнув гаечный ключ в правом верхнем углу GUI.
Предметы, входящие в сеть, начинаются с наиболее приоритетного хранилища.

##Настройки

* Самолет может быть установлен для размещения блоков в мире или сбрасывания предметов.

## Обновления

Плоскость формирования поддерживает следующие [обновления]upgrade_cards.md):

*   <ItemLink id="capacity_card" />Увеличивает количество фильтрующих слотов
*   <ItemLink id="fuzzy_card" />позволяет фильтровать самолет по уровню повреждений и/или игнорировать элемент NBT
*   <ItemLink id="inverter_card" />Переключает фильтр из белого списка в черный.

## Рецепт

<RecipeFor id="formation_plane" />
