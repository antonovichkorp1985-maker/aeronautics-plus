---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Tag Складской автобус
    icon: extendedae:tag_storage_bus
categories:
- extended devices
item_ids:
- extendedae:tag_storage_bus
---

#ME Tag Складской автобус

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_tag_storage_bus.snbt"></ImportStructure>
</GameScene>

Складской автобус - это<ItemLink id="ae2:storage_bus" />Он может быть отфильтрован тегами элемента или жидкости и поддерживает некоторые основные логические операторы.

Вот несколько примеров:

- Принимать только сырую руду

c:raw_materials/*

- Принять все слитки и драгоценные камни

c:ingots/* | c:gems/*

