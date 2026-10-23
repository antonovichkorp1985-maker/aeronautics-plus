---
navigation:
  parent: example-setups/example-setups-index.md
  title: Уровни Emitter Autostocking
  icon: level_emitter
---

Уровень эмиттер Автосток

Можно спросить: «Как мне сохранить определенное количество товара на складе, создавая больше по мере необходимости?»

Одним из решений является использование<ItemLink id="export_bus" />, <ItemLink id="level_emitter" />и<ItemLink id="crafting_card" />автоматически запрашивать новые предметы
(перенаправлено с «Autocrafting»)../ae2-mechanics/autocrafting.md). Эта установка предназначена для поддержания большого количества одного элемента.

Конечно, вы можете постоянно создавать свою сеть, опустив излучатель уровня и карту Redstone.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/level_emitter_autostocking.snbt" />

  <BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
(1) Экспортный автобус: фильтруется на нужный товар. Имеет Redstone Card и Crafting Card. Режим Redstone установлен
«Активный с сигналом», Crafting behavior set to «Не используйте запасные предметы».
        <Row><ItemImage id="redstone_card" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0.7 1 0" max="1 2 1">
2 Уровень эмиттер: Настроенный с желаемым элементом и количеством, установите «Выйти, когда уровни ниже предела».
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
(3) Интерфейс: в конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* The<ItemLink id="export_bus" />(1) фильтруется на желаемый элемент. Он имеет<ItemLink id="redstone_card" />и<ItemLink id="crafting_card" />.
Режим «Redstone» настроен на «Актив с сигналом», «Поведение при создании» настроен на «Не используйте запасные предметы».
* The<ItemLink id="level_emitter" />(2) сконфигурирован с желаемым элементом и количеством, и установлен на "Выйти, когда уровни ниже предела".
* The<ItemLink id="interface" />(3) находится в своей конфигурации по умолчанию.

## Как это работает

1. Если количество желаемого элемента в [сетевом хранилище]../ae2-mechanics/import-export-storage.md) ниже количества, указанного в
   <ItemLink id="level_emitter" />Он будет излучать сигнал красного камня.
2. При получении сигнала красного камня (и вследствие<ItemLink id="crafting_card" />и не использовать заготовленные предметы,
тот<ItemLink id="export_bus" />будет требовать, чтобы сеть [автопроизводство]../ae2-mechanics/autocrafting.md) ремесло
больше желаемого товара, а затем экспортировать его.
3. После того, как предмет втолкнули в него (и не были настроены, чтобы иметь что-либо во внутреннем инвентаре),<ItemLink id="interface" />Этот элемент будет перенесен в сетевое хранилище.