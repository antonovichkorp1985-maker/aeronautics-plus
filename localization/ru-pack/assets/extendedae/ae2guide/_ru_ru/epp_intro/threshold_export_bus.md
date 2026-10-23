---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Экспортный автобус Threshold
    icon: extendedae:threshold_export_bus
categories:
- extended devices
item_ids:
- extendedae:threshold_export_bus
---

Экспортный автобус Threshold

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_export_bus.snbt"></ImportStructure>
</GameScene>

ME Порог экспорта Автобус работает, когда количество элемента, хранящегося в сети ME, выше или ниже порога.

## Пример

> Иллюстрация находится в оригинальном руководстве мода.

Порог меди установлен на уровне 128, поэтому она экспортирует медь, когда количество меди, хранящейся в сети, превышает 128.

> Иллюстрация находится в оригинальном руководстве мода.

Порог тот же, что и выше, но режим установлен на BELOW. Он экспортирует медь, когда количество хранящейся меди ниже 128.
