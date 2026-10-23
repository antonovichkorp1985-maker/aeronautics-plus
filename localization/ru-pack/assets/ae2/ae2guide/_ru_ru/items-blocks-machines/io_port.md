---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Мой порт
  icon: io_port
  position: 210
categories:
- devices
item_ids:
- ae2:io_port
---

ME I/O Порт

<BlockImage id="io_port" p:powered="true" scale="8" />

Порт IO позволяет быстро заполнять или опорожнять [камеры хранения]../items-blocks-machines/storage_cells.md) в или из
[хранилище сети]../ae2-mechanics/import-export-storage.md).

Его можно вращать с помощью<ItemLink id="certus_quartz_wrench" />.

##Настройки

* Порт ввода-вывода может быть настроен для перемещения ячейки к выходным слотам, когда ячейка пуста, полна или когда работа выполнена.
* Если<ItemLink id="redstone_card" />вставлено, появятся варианты различных краснокаменных условий
* В центре графического интерфейса есть стрелка, чтобы установить, в каком направлении передавать предметы, из ячейки в [сетевое хранилище]../ae2-mechanics/import-export-storage.md),
или из хранилища в камеру.

## Обновления

Порт IO поддерживает следующие обновления:upgrade_cards.md):

*   <ItemLink id="speed_card" />Увеличивает количество вещей, перемещаемых за операцию
*   <ItemLink id="redstone_card" />Добавляет управление красным камнем, позволяя активировать на высоком сигнале, низком сигнале или один раз на импульс

## Рецепт

<RecipeFor id="io_port" />
