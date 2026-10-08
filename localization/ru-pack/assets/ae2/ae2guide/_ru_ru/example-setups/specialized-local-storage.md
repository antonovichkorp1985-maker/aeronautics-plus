---
navigation:
  parent: example-setups/example-setups-index.md
  title: Специализированный локальный склад
  icon: drive
---

Специализированный локальный склад

Использование одного из [специальных поведений интерфейса]../items-blocks-machines/interface.md#special-interactions), a
[подсеть]../ae2-mechanics/subnetworks.md) может передавать содержимое своего хранилища в основную сеть без возможности
Для просмотра основного хранилища сети, занимающего всего 1 [канал]../ae2-mechanics/channels.md).

Это полезно для местного хранения на какой-либо ферме, чтобы предметы не перетекали в ваше основное хранилище.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/local_storage.snbt" />

<BoxAnnotation color="#dddddd" min="4 0 0" max="5 2 1">
(1) Некоторые способы импорта товаров (в данном случае интерфейс)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 0 0" max="4 1 1">
(2) Драйв: в нем есть клетки. Клетки должны быть отфильтрованы на любой выход фермы.
Клетки могут иметь карты равного распределения и карты разрушения переполнения.
        <Row><ItemImage id="item_storage_cell_4k" scale="2" /> <ItemImage id="equal_distribution_card" scale="2" /> <ItemImage id="void_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1 0" max="4 2 0.3">
(3) Крафтовый терминал: Это может видеть содержимое диска в подсети, но не содержимое хранилища вашей основной сети.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0 0" max="2.3 1 1">
(4) Интерфейс #2: В конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1.7 0 0" max="2 1 1">
(5) Складской автобус: Имеет приоритет, установленный выше, чем основной накопитель, может быть отфильтрован на любой выход фермы.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 2 0.3">
Крафтовый терминал: Это позволяет видеть как содержимое хранилища основной сети, так и подсеть.
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* Первый<ItemLink id="interface" />(1) просто принимает предметы с любой фермы и выталкивает их в подсеть.
* The<ItemLink id="drive" />2 имеет некоторые [ячейки]../items-blocks-machines/storage_cells.md) в нем. Клетки должны быть
[разделено]../items-blocks-machines/cell_workbench.md) независимо от результатов деятельности фермы.
Клетки могут иметь<ItemLink id="equal_distribution_card" />s и<ItemLink id="void_card" />s.
* Второй<ItemLink id="interface" />(4) находится в конфигурации по умолчанию.
* The<ItemLink id="storage_bus" />имеет свою первоочередность.../ae2-mechanics/import-export-storage.md#storage-priority) установлен
выше, чем основное хранилище. Он может быть отфильтрован на любой продукт фермы.

## Как это работает

* The<ItemLink id="interface" />в подсети показывает<ItemLink id="storage_bus" />В основной сети содержание
тот<ItemLink id="drive" />. Это означает, что шина хранения может непосредственно вытягивать предметы из и толкать предметы в ячейки на диске.
* Приоритетное значение имеет шина хранения.../ae2-mechanics/import-export-storage.md#storage-priority) таким образом, чтобы элементы были преимущественно
Поместите его обратно в подсеть вместо основного хранилища.
Важно отметить, что если ячейки в подсети заполняются, элементы не будут перетекать в основную сеть. Если ферма имеет вид
Если он сломается,<ItemLink id="void_card" />s может использоваться для удаления избыточных элементов.
* Если ферма производит несколько продуктов,<ItemLink id="equal_distribution_card" />s может остановить один элемент от заполнения всех ячеек.
Не позволяйте другим предметам храниться.