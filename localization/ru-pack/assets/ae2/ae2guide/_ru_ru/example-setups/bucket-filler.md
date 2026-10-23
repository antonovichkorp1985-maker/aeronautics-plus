---
navigation:
  parent: example-setups/example-setups-index.md
  title: Букетный наполнитель
  icon: minecraft:water_bucket
---

# Букетный наполнитель

См. также [Bucket Emptier]bucket-emptier.md).

Обратите внимание, что, поскольку он использует<ItemLink id="pattern_provider" />Он предназначен для интеграции в ваш [автопрокат]../ae2-mechanics/autocrafting.md)
подстава.

Иногда жизнь неудобна, и вам нужны ведра жидкости вместо самой жидкости. Иногда машина может сделать это за вас.
(например, переносчик жидкости из теплового расширения), но вы не всегда можете иметь мод, который делает это удобно для вас. К счастью
ваниль Minecraft имеет немного менее удобный способ.<ItemLink id="minecraft:dispenser" />.

** Обратите внимание, что вам часто не нужно этого делать, потому что жидкие заменители
[Терминал кодирования шаблона]../items-blocks-machines/terminals.md#pattern-encoding-terminal) позволяет использовать саму жидкость в
Рецепт крафта вместо ведра. **

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/bucket_filler.snbt" />

<BoxAnnotation color="#dddddd" min="2 1 0" max="3 2 1">
(1) Поставщик шаблонов: Настроен на блокировку крафта «С сигналом Redstone», с соответствующими схемами обработки.

        <Row>
![Fill Pattern](../assets/diagrams/water_fill_pattern_small.png)
![Fill Pattern](../assets/diagrams/lava_fill_pattern_small.png)
        </Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1.1 0.1" max="3.2 1.9 0.9">
(2) Интерфейс: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.1 1.1 0.8" max="3.9 1.9 1">
(3) Складская шина No1: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="4.05 1.05 0.8" max="4.95 1.95 1">
(4) Самолет формирования: фильтруется для ведер из черного списка, используя инверторную карту.
        <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.2 2 1.2" max="3.8 2.2 1.8">
(5) Импортный автобус: фильтруется для ведер из черного списка, используя инверторную карту.
        <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2.1 2 0.1" max="2.9 2.2 0.9">
(6) Складская шина No2: в конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="0 1.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="225" pitch="45" />
</GameScene>

## Конфигурации

* The<ItemLink id="pattern_provider" />(1) Настроен на блокировку крафта «С сигналом красного камня», с соответствующим<ItemLink id="processing_pattern" />s.
  
[Charger Pattern] (../assets/diagrams/water_fill_pattern.png)
[Charger Pattern] (../assets/diagrams/lava_fill_pattern.png)

* The<ItemLink id="interface" />(2) находится в своей конфигурации по умолчанию.
* Первый<ItemLink id="storage_bus" />(3) находится в своей конфигурации по умолчанию.
* The<ItemLink id="formation_plane" />(4) фильтруется в ведра черного списка, используя инверторную карту.
  <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
* The<ItemLink id="import_bus" />(5) фильтруется в ведра черного списка, используя инверторную карту.
  <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
* Второй<ItemLink id="storage_bus" />(6) находится в конфигурации по умолчанию.

## Как это работает

1. The<ItemLink id="pattern_provider" />Выталкивает ингредиенты в<ItemLink id="interface" />.
(На самом деле, в качестве оптимизации, он проталкивается непосредственно через шину хранения и плоскость формирования, как если бы они были расширениями лиц провайдера. Элементы никогда не входят в интерфейс.
2. С помощью механизмов, описанных в [трубопроводных подсетях]pipe-subnet.md#providing-to-multiple-places) и<ItemLink id="formation_plane" />,
Ведро заканчивается в<ItemLink id="minecraft:dispenser" />и жидкость помещается плоскостью формирования.
3. The<ItemLink id="minecraft:comparator" />обнаруживает ведро в дозаторе и, таким образом, одновременно питает дозатор и замки
тот<ItemLink id="pattern_provider" />.
4. Дозатор набирает жидкость ведром, теперь он сам по себе имеет заполненное ведро.
5. The<ItemLink id="import_bus" />вытаскивает заполненное ведро из дозатора и хранит его через
   <ItemLink id="storage_bus" />Поставщик шаблонов возвращает его в основную сеть.
6. Компаратор видит, что диспенсер пуст, разблокируя провайдера.
