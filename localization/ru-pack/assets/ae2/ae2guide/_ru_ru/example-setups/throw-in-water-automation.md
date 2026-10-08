---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация в воде
  icon: fluix_crystal
---

Автоматизация выбрасывания в воду рецептов

Обратите внимание, что, поскольку он использует<ItemLink id="pattern_provider" />Он предназначен для интеграции в ваш [автопрокат]../ae2-mechanics/autocrafting.md)
подстава.

Некоторые рецепты требуют, чтобы предметы были брошены в воду (хотя аналогичная установка может быть использована для бросания предметов в другие места).
Это можно автоматизировать с помощью<ItemLink id="formation_plane" />, <ItemLink id="annihilation_plane" />и некоторые поддерживающие
инфраструктура (это, по существу, 2 модифицированные [подсети трубопровода])pipe-subnet.md)).

Эта установка предназначена для использования в сочетании с [автоматизацией зарядного устройства]charger-automation.md) для обеспечения<ItemLink id="charged_certus_quartz_crystal" />s.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/throw_in_water.snbt" />

<BoxAnnotation color="#dddddd" min="2 0 1" max="3 1 2">
(1) Поставщик шаблонов: В своей конфигурации по умолчанию, с соответствующими шаблонами обработки.

![Fluix Pattern](../assets/diagrams/fluix_pattern_small.png) ![Flawed Budding Pattern](../assets/diagrams/flawed_budding_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1.7 0 1" max="2 1 2">
(2) Интерфейс: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 .7 1" max="2 1 2">
(3) План формирования: Установка для сброса входов в качестве элементов.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 2 1" max="2 2.3 2">
(4) Аннигиляционный самолет: нет графического интерфейса для настройки.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 1 1" max="3 1.3 2">
(5) Складская шина: фильтруется на выходе шаблонов
        <Row><ItemImage id="fluix_crystal" scale="2" /><BlockImage id="flawless_budding_quartz" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="3.9 0.5 1.5" color="#00ff00">
Основная сеть и автоматизация заряда
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/charger_automation.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
    </DiamondAnnotation>

  <IsometricCamera yaw="180" pitch="0" />
</GameScene>

## Конфигурации и шаблоны

* The<ItemLink id="pattern_provider" />(1) находится в своей конфигурации по умолчанию, с соответствующей<ItemLink id="processing_pattern" />s
*для<ItemLink id="fluix_crystal" />Рецепт по умолчанию от JEI/REI работает отлично:

[Fluix Pattern] (../assets/diagrams/fluix_pattern.png)

*для<ItemLink id="flawed_budding_quartz" />Лучше всего сделать это непосредственно из<ItemLink id="quartz_block" />,
который позволяет избежать проблем, когда вход одного рецепта является выходом другого, в результате чего шина хранения не может фильтровать:

![Паттер неисправности](../assets/diagrams/flawed_budding_pattern.png)

* The<ItemLink id="interface" />(2) находится в своей конфигурации по умолчанию.
* The<ItemLink id="formation_plane" />(3) настроен на снижение входных данных в качестве элементов.
* The<ItemLink id="annihilation_plane" />(4) не имеет графического интерфейса и не может быть настроен.
* The<ItemLink id="storage_bus" />(5) фильтруется на выходы шаблонов.

## Как это работает

1. The<ItemLink id="pattern_provider" />Выталкивает ингредиенты в<ItemLink id="interface" />На его стороне, в зеленой подсети
2. Интерфейс (настроенный на то, чтобы ничего не хранить по умолчанию) пытается втиснуть свое содержимое в сетевое хранилище.../ae2-mechanics/import-export-storage.md)
3. Единственным хранилищем в зеленой подсети является<ItemLink id="formation_plane" />, который бросает предметы, которые он получает в воду
4. The<ItemLink id="annihilation_plane" />в оранжевой подсети пытается подобрать предметы, которые только что были выброшены, но не может, потому что
тот<ItemLink id="storage_bus" />поверх поставщика шаблонов (единственное хранилище в оранжевой подсети) фильтруется только для принятия результатов возможных ремесел.
5. Предметы выполняют свою трансформацию в мире.
6. Самолет аннигиляции теперь может собирать предметы перед собой, так как шина хранения позволяет хранить их.
7. Шина хранения хранит полученные элементы в шаблоне провайдера, возвращая их в сеть.
