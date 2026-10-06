---
navigation:
  parent: example-setups/example-setups-index.md
  title: Ведро пустеет
  icon: minecraft:bucket
---

Ведро пустеет

Смотрите также [Bucket Filler]bucket-filler.md).

Обратите внимание, что, поскольку он использует<ItemLink id="pattern_provider" />Он предназначен для интеграции в ваш [автопрокат]../ae2-mechanics/autocrafting.md)
подстава.

Иногда жизнь неудобна, и вам нужна сама жидкость, но вы можете сделать жидкость только в ведре. Иногда машина может сделать это за вас.
(например, переносчик жидкости из теплового расширения), но вы не всегда можете иметь мод, который делает это удобно для вас. К счастью
ваниль Minecraft имеет немного менее удобный способ.<ItemLink id="minecraft:dispenser" />.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/bucket_emptier.snbt" />

<BoxAnnotation color="#dddddd" min="2 1 0" max="3 2 1">
(1) Поставщик шаблонов: Настроен на блокировку крафта «С сигналом Redstone» и включен режим блокировки, с соответствующими шаблонами обработки.

        <Row>
![Fill Pattern](../assets/diagrams/water_empty_pattern_small.png)
![Fill Pattern](../assets/diagrams/lava_empty_pattern_small.png)
        </Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2.1 2 0.1" max="2.9 2.2 0.9">
(2) Интерфейс: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.1 2 1.1" max="3.9 2.2 1.9">
(3) Складская шина No1: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="4.05 1.05 0.8" max="4.95 1.95 1">
(4) Аннигиляционный самолет: нет графического интерфейса для настройки.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.2 1.2 0.8" max="3.8 1.8 1">
(5) Импортный автобус: отфильтрованный до ведер.
        <ItemImage id="minecraft:bucket" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1.1 0.1" max="3.2 1.9 0.9">
(6) Складская шина No2: в конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="0 1.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="225" pitch="45" />
</GameScene>

## Конфигурации

* The<ItemLink id="pattern_provider" />(1) Настроен на блокировку крафта «С сигналом красного камня» и включен режим блокировки,
с соответствующим<ItemLink id="processing_pattern" />s.
  
! [Charger Pattern] (../assets/diagrams/water_empty_pattern.png)
[Charger Pattern] (../assets/diagrams/lava_empty_pattern.png)

* The<ItemLink id="interface" />(2) находится в своей конфигурации по умолчанию.
* Первый<ItemLink id="storage_bus" />(3) находится в своей конфигурации по умолчанию.
* The<ItemLink id="annihilation_plane" />(4) не имеет графического интерфейса и не может быть настроен.
* The<ItemLink id="import_bus" />(5) фильтруется в ведра.
  <ItemImage id="minecraft:bucket" scale="2" />
* Второй<ItemLink id="storage_bus" />(6) находится в конфигурации по умолчанию.

## Как это работает

1. The<ItemLink id="pattern_provider" />Выталкивает ингредиенты в<ItemLink id="interface" />.
(На самом деле, в качестве оптимизации, он проталкивает непосредственно через шину хранения, аси фит был расширением лиц провайдера. Элементы никогда не входят в интерфейс.
2. С помощью механизмов, описанных в [трубопроводных подсетях]pipe-subnet.md#providing-to-multiple-places),
Ведро заканчивается в<ItemLink id="minecraft:dispenser" />.
3. The<ItemLink id="minecraft:comparator" />обнаруживает ведро в дозаторе и, таким образом, одновременно питает дозатор и замки
тот<ItemLink id="pattern_provider" />.
4. Дозатор выбрасывает жидкость из ведра, теперь в нем есть пустое ведро.
5. The<ItemLink id="import_bus" />вытаскивает пустое ведро из дозатора и хранит его через
   <ItemLink id="storage_bus" />Поставщик шаблонов возвращает его в основную сеть.
6. Компаратор видит, что диспенсер пуст, разблокируя провайдера.