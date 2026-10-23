---
navigation:
  parent: example-setups/example-setups-index.md
  title: Полуавто ферма Certus
  icon: certus_quartz_crystal
  position: 115
---

# Полуавто ферма Certus

К сожалению, [простая ферма Certus]simple-certus-farm.md) требует a<ItemLink id="flawless_budding_quartz" />работать полностью
автоматически. Для этого требуется либо [пространственный IO]../ae2-mechanics/spatial-io.md) или строительство фермы на [метеорите](../ae2-mechanics/meteorites.md).

Тем не менее, AE2 может размещать и разбивать блоки, поэтому он может просто разбивать блоки.
Вы можете сделать свою ферму *заменить начинающий кертус для вас *. (Вам придется периодически вставлять некоторые
<ItemLink id="flawed_budding_quartz" />на входную бочку и извлекать<ItemLink id="quartz_block" />от потраченного
бочка будднг кертус

Чтобы сделать это полностью автоматически, см. [Передовая ферма Certus]advanced-certus-farm.md).

Эта ферма немного сложнее, чем простая ферма Certus.simple-certus-farm.mdПотому что это действительно
3 отдельные установки, забитые вместе.

См. [Certus Growth]../ae2-mechanics/certus-growth.md) для расчетных скоростей.

**Это сложное сооружение со стюфами, спрятанными за другими стюфами, вокруг, чтобы увидеть его со всех ног* *

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/semiauto_certus_farm.snbt" />

  <BoxAnnotation color="#ddaaaa" min="3.7 2 1" max="4 3 2">
(1) План уничтожения #1: Нет графического интерфейса для настройки, но вы можете быть очарованы Fortune.
  </BoxAnnotation>

  <BoxAnnotation color="#ddaaaa" min="2 2 1" max="2.3 3 2">
(2) Хранилище No1: Фильтровать до Certus Quartz Crystal.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 2.5 1.5" color="#ff0000">
Кластер Breaker Subnet
  </DiamondAnnotation>

  <BoxAnnotation color="#aaddaa" min="3.7 1 1" max="4 2 2">
(3) Самолет аннигиляции No2: нет графического интерфейса для настройки, но очарован Silk Touch.
  </BoxAnnotation>

  <BoxAnnotation color="#aaddaa" min="2 1 1" max="2.3 2 2">
(4) Автобус для хранения No2: Отфильтрованный до блока Certus Quartz.
        <BlockImage id="quartz_block" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 1.5 1.5" color="#00ff00">
Подсеть Certus Block Breaker
  </DiamondAnnotation>

  <BoxAnnotation color="#ffddaa" min="4 0.7 1" max="5 1 2">
(5) План формирования: в конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#ffddaa" min="2 0 1" max="2.3 1 2">
(6) Импортная шина: в конфигурации по умолчанию.
  </BoxAnnotation>

  <DiamondAnnotation pos="3 0.5 1.5" color="#ddcc00">
Обсуждение Budding Block Placer Subnet
  </DiamondAnnotation>

  <BoxAnnotation color="#aaaadd" min="0.7 2 1" max="1 3 2">
(7) Хранилище No3: Фильтровать до Certus Quartz Crystal. Приоритет установлен выше, чем у основного хранилища.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

    <DiamondAnnotation pos="1.5 0.5 1.5" color="#00ff00">
Оригинальное название: Flawed Budding Certus Quartz
        <BlockImage id="flawed_budding_quartz" scale="2" />
    </DiamondAnnotation>

    <DiamondAnnotation pos="1.5 1.5 1.5" color="#00ff00">
Вручную извлечь блок Certus Quartz.
        <BlockImage id="quartz_block" scale="2" />
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="165" pitch="5" />
</GameScene>

## Конфигурации

### Кластерный разрушитель:

* Первый<ItemLink id="annihilation_plane" />(1) не имеет графического интерфейса и не может быть настроен, но может быть очарован Fortune.
* Первый<ItemLink id="storage_bus" />2 фильтруется для<ItemLink id="certus_quartz_crystal" />.

### Certus Block Breaker

* Второй<ItemLink id="annihilation_plane" />(3) не имеет графического интерфейса и не может быть настроен, но должен быть очарован Silk Touch.
* Второй<ItemLink id="storage_bus" />(4) фильтруется для<ItemLink id="quartz_block" />.

### Budding Block Placer:

* The<ItemLink id="formation_plane" />(5) находится в конфигурации по умолчанию.
* The<ItemLink id="import_bus" />(6) находится в конфигурации по умолчанию.

### в основной сети:

* Третий<ItemLink id="storage_bus" />(7) фильтруется для<ItemLink id="certus_quartz_crystal" />и имеет свою
[Приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) установить выше, чем ваше основное хранилище.

## Как это работает

### Кластерный разрушитель:

Подсеть выключателя кластера работает очень похоже на подсеть в [простой ферме кертуса]simple-certus-farm.md).

1. The<ItemLink id="annihilation_plane" />пытается сломать то, что перед ним, но может только сломать<ItemLink id="quartz_cluster" />
Единственным хранилищем в подсети является<ItemLink id="storage_bus" />Отфильтрованный на<ItemLink id="certus_quartz_crystal" />.
2. The<ItemLink id="storage_bus" />Хранит кристаллы кварца в бочке.

Certus Block Breaker скачать

Подсеть разрушителя блока Certus служит для разрушения обедненного блока почкования, как только он превращается в равнину.<ItemLink id="quartz_block" />.
Работает аналогично кластерному выключателю.

1. The<ItemLink id="annihilation_plane" />пытается сломать то, что перед ним, но может только сломать<ItemLink id="quartz_block" />
Единственным хранилищем в подсети является<ItemLink id="storage_bus" />Отфильтрованный на<ItemLink id="quartz_block" />.
Самолет должен иметь шелковое прикосновение, поэтому блок почкования не будет разрушаться при поломке, и, таким образом, самолет не сломает его преждевременно.
2. The<ItemLink id="storage_bus" />хранит кварцевый блок в отработанном
бочка будднг кертус, придется вручную бросать ее в воду с<ItemLink id="charged_certus_quartz_crystal" />Чтобы освежиться.

### Блок-плейсер Budding

Появляющаяся блок-пласерная подсеть служит для размещения новой<ItemLink id="flawed_budding_quartz" />Когда подсеть выключателя разрушает старую истощенную.

1. The<ItemLink id="import_bus" />Импортирует подающий блок из входной бочки.
2. Единственным хранилищем в подсети является<ItemLink id="formation_plane" />который помещает блокировку.

#### На главной сети

* The<ItemLink id="storage_bus" />Дает основную сеть (а также [Автоматизация заряда])charger-automation.md)) доступ ко всем кристаллам кварца в стволе. Он установлен для
высокий [приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) чтобы кристаллы кварца кертуса были предпочтительно
Положите обратно в бочку, а не в основное хранилище.