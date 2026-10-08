---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация печи
  icon: minecraft:furnace
---

# Автоматизация печи

Обратите внимание, что, поскольку он использует<ItemLink id="pattern_provider" />Он предназначен для интеграции в ваш [автопрокат]../ae2-mechanics/autocrafting.md)
подстава. Если вы просто хотите автоматизировать печь самостоятельно, используйте хопперы, сундуки и прочее.

Автоматизация a<ItemLink id="minecraft:furnace" />Это немного сложнее, чем автоматизация более простых машин, таких как зарядное устройство.../example-setups/charger-automation.md).
Печь требует ввода с двух отдельных сторон и извлечения с третьей. Предмет, подлежащий плавке, должен толкаться в верхнюю часть лица,
топливо должно толкаться в боковую сторону, а результат должен вытягиваться из дна.

Это можно сделать с помощью a<ItemLink id="pattern_provider" />
сверху, а<ItemLink id="export_bus" />на стороне, чтобы постоянно толкать топливо, и<ItemLink id="import_bus" />на
Внизу для импорта результатов в сеть. При этом используется 3 [канала].../ae2-mechanics/channels.md).

Вот как это сделать с помощью 1 канала:

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/furnace_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
(1) Поставщик шаблонов: Направленный вариант, с использованием кварцевого ключа certus, с соответствующими схемами обработки.

![Железный шаблон](../активы/диаграммы/печь_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
(2) Интерфейс: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="1.3 2 1">
(3) Складской автобус No1: Отфильтрованный на уголь.
        <ItemImage id="minecraft:coal" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 2 0" max="1 2.3 1">
(4) Автобус для хранения No2: Фильтруется в черный список угля, используя инверторную карту.
        <Row><ItemImage id="minecraft:coal" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* The<ItemLink id="pattern_provider" />(1) находится в своей конфигурации по умолчанию, с соответствующей<ItemLink id="processing_pattern" />s.
Он становится направленным, используя<ItemLink id="certus_quartz_wrench" />На нем.

[Iron Pattern] (../assets/diagrams/furnace_pattern.png)

* The<ItemLink id="interface" />(2) находится в своей конфигурации по умолчанию.
* Первый<ItemLink id="storage_bus" />(3) фильтруется на уголь или любое топливо, которое вы хотите использовать.
* Второй<ItemLink id="storage_bus" />(4) фильтруется для внесения в черный список топлива, которое вы используете, используя<ItemLink id="inverter_card" />.

## Как это работает

1. The<ItemLink id="pattern_provider" />Выталкивает ингредиенты в<ItemLink id="interface" />.
(На самом деле, в качестве оптимизации, он проталкивает непосредственно через шины хранения, как если бы они были расширениями лиц провайдера. Элементы никогда не входят в интерфейс.
2. Интерфейс настроен на то, чтобы ничего не хранить, поэтому он пытается подтолкнуть ингредиенты в сетевое хранилище.../ae2-mechanics/import-export-storage.md).
3. Единственным хранилищем в зеленой подсети является<ItemLink id="storage_bus" />Сес. Автобус, отфильтрованный на уголь, помещает уголь в топливный прорезь печи через боковую поверхность.
Автобус, отфильтрованный на НЕ уголь, помещает предметы, которые будут плавиться в верхнем слоте, через верхнюю поверхность.
4. Печь делает свое печь вещь
5. Хоппер вытаскивает результаты из дна печи и помещает их в слоты возврата провайдера, возвращая их в основную сеть.
