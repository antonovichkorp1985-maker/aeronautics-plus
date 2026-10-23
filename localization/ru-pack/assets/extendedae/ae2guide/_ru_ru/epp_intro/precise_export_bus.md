---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Точный экспортный автобус
    icon: extendedae:precise_export_bus
categories:
- extended devices
item_ids:
- extendedae:precise_export_bus
---

Точный экспортный автобус

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_precise_export_bus.snbt"></ImportStructure>
</GameScene>

Точный экспорт Автобус экспортирует товары или жидкости в определенных количествах. Он экспортируется только в том случае, если контейнер может полностью принять весь объем производства.

## Пример

[GUI] (../pic/pre_bus_gui1.png)

Это экспортирует 3 булыжника за операцию. Он прекращает экспорт, когда количество булыжника в сети меньше 3.

[GUI] (../pic/pre_bus_gui2.png)

Он также прекращает экспорт, когда целевой контейнер не может удерживать всю продукцию. Грудь может вместить только 2 булыжника, поэтому автобус на экспорт останавливается.
