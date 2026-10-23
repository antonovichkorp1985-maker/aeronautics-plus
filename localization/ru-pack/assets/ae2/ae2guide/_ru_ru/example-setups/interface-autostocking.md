---
navigation:
  parent: example-setups/example-setups-index.md
  title: Интерфейс Autostocking
  icon: interface
---

# Интерфейс Автостокинг

Можно спросить: «Как мне хранить определенное количество различных предметов на складе, создавая больше по мере необходимости?»

Одним из решений является использование<ItemLink id="interface" />и<ItemLink id="crafting_card" />автоматически запрашивать новые предметы
(перенаправлено с «Autocrafting»)../ae2-mechanics/autocrafting.md). Эта установка больше подходит для поддержания небольшого количества широкого
разнообразие предметов.

Эта демонстрационная установка сокращена, поэтому она не слишком широка, вероятно, наиболее оптимально использовать 4.<ItemLink id="interface" />s и 4<ItemLink id="storage_bus" />Сес,
Для использования всех 8 каналов../ae2-mechanics/channels.md) в обычном [кабеле]../items-blocks-machines/cables.md).

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_autostocking.snbt" />

<BoxAnnotation color="#dddddd" min="0 0 0" max="2 1 1">
(1) Интерфейсы: Набор для хранения желаемых элементов в себе. У них есть крафтовые карты.
        <ItemImage id="crafting_card" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 0" max="2 1.3 1">
(2) Автобусы для хранения: «Режим ввода/вывода» настроен на «Только извлечение».
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* The<ItemLink id="interface" />s (1) настроены на то, чтобы сохранить желаемые элементы в себе, нажав желаемый элемент в их
верхние слоты или перетаскивание в верхние слоты из JEI, затем нажатие на значок гаечного ключа над слотами, чтобы установить сумму. Они имеют<ItemLink id="crafting_card" />s.
* The<ItemLink id="storage_bus" />ses (2) устанавливаются таким образом, что "Режим ввода/вывода" устанавливается на "Только извлечение".

## Как это работает

1. Если<ItemLink id="interface" />не может получить достаточно сконфигурированного элемента из [сетевого хранилища]../ae2-mechanics/import-export-storage.md),
(и имеет свою<ItemLink id="crafting_card" />), он будет требовать, чтобы сеть [автопроизводство](../ae2-mechanics/autocrafting.md) создать больше этого элемента.
2. The<ItemLink id="storage_bus" />ses позволяют сети получать доступ к содержимому интерфейсов.