---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация зарядного устройства
  icon: charger
---

# Автоматизация зарядки

Обратите внимание, что, поскольку он использует<ItemLink id="pattern_provider" />Он предназначен для интеграции в ваш [автопрокат]../ae2-mechanics/autocrafting.md)
подстава. Если вы хотите автоматизировать<ItemLink id="charger" />отдельно, используйте хопперы, сундуки и прочее.

Автоматизация a<ItemLink id="charger" />Это довольно просто. А.<ItemLink id="pattern_provider" />После этого в него вносятся ингредиенты, а затем [подсети трубопровода].pipe-subnet.md)
или другой элемент трубы подталкивает результат обратно к поставщику.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/charger_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
(1) Поставщик шаблонов: В своей конфигурации по умолчанию, с соответствующими шаблонами обработки. Также обеспечивает зарядное устройство мощностью.

[Charger Pattern] (../assets/diagrams/charger_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 0" max="1 1.3 1">
(2) Импортный автобус: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
(3) Складской автобус: В своей конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* The<ItemLink id="pattern_provider" />(1) находится в своей конфигурации по умолчанию, с соответствующей<ItemLink id="processing_pattern" />s.
Он также обеспечивает<ItemLink id="charger" />(с энергией)../ae2-mechanics/energy.mdПотому что он действует как [кабель].../items-blocks-machines/cables.md).
  
[Charger Pattern] (../assets/diagrams/charger_pattern.png)

* The<ItemLink id="import_bus" />(2) находится в своей конфигурации по умолчанию.
* The<ItemLink id="storage_bus" />(3) находится в своей конфигурации по умолчанию.

## Как это работает

1. The<ItemLink id="pattern_provider" />Выталкивает ингредиенты в<ItemLink id="charger" />.
2. Зарядное устройство делает свою зарядную вещь.
3. The<ItemLink id="import_bus" />зеленая подсеть извлекает результат из зарядного устройства и пытается сохранить его в
[хранилище сети]../ae2-mechanics/import-export-storage.md).
4. Единственным хранилищем в зеленой подсети является<ItemLink id="storage_bus" />, который хранит полученные элементы в шаблоне провайдера, возвращая их в основную сеть.
