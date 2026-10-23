---
navigation:
  parent: example-setups/example-setups-index.md
  title: Элемент/текучая подсеть "Труба"
  icon: storage_bus
---

# Элемент/текучая подсеть "Труба"

Простой способ эмуляции элемента и/или жидкой трубы с помощью AE2 [устройств]../ae2-mechanics/devices.md), полезный для всего, для чего вы бы использовали предмет или жидкую трубу.
Это включает в себя возвращение результата ремесла в<ItemLink id="pattern_provider" />.

Как правило, существует два различных метода достижения этого:

## Импортный автобус -> Складской автобус

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_storage_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
(1) Импортный автобус: Можно фильтровать.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
(2) Складской автобус: Можно фильтровать. Это (и другие автобусы для хранения, которые вы хотите использовать)
Это должно быть единственное хранилище в сети.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#00ff00">
Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#00ff00">
Направление
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

The<ItemLink id="import_bus" />(1) на исходный инвентарь импортируются предметы или жидкость, и попытки хранить их в [сетевом хранилище]()../ae2-mechanics/import-export-storage.md).
Поскольку единственным хранилищем в сети является<ItemLink id="storage_bus" />(2) (поэтому это подсеть, а не основная сеть), элементы или жидкость
Они помещаются в инвентарь назначения, таким образом, переводятся. Энергия поступает через a<ItemLink id="quartz_fiber" />.
Как шина импорта, так и шина хранения могут быть отфильтрованы, но настройка передаст все, к чему может получить доступ, если не будут применены фильтры.
Эта установка также работает с несколькими автобусами импорта и несколькими автобусами хранения.

##Складской автобус -> Экспортные автобусы

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/storage_export_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
(1) Складской автобус: Можно фильтровать. Это (и другие автобусы для хранения, которые вы хотите использовать)
Это должно быть единственное хранилище в сети.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
2 Экспортные автобусы: Нужно фильтровать.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#00ff00">
Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#00ff00">
Направление
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

The<ItemLink id="export_bus" />на опись назначения пытается вытащить элементы в своем фильтре из [сетевого хранилища]../ae2-mechanics/import-export-storage.md).
Поскольку единственным хранилищем в сети является<ItemLink id="storage_bus" />(поэтому это подсеть, а не основная сеть), элементы или жидкость.
Извлекаются из инвентаря источника, таким образом переводятся. Энергия поступает через a<ItemLink id="quartz_fiber" />.
Поскольку экспортные шины должны быть отфильтрованы для работы, эта установка работает только в том случае, если вы фильтруете экспортную шину.
Эта установка также работает с несколькими автобусами хранения и несколькими экспортными автобусами.

Настройка, которая не работает (импортный автобус)

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_export_pipe.snbt" />

<BoxAnnotation color="#dd3333" min="3.7 0 0" max="4 1 1">
Импортный автобус: Поскольку сеть не имеет хранилища, ее некуда импортировать.
  </BoxAnnotation>

<BoxAnnotation color="#dd3333" min="1 0 0" max="1.3 1 1">
2 Экспортные автобусы: Поскольку сеть не имеет хранилища, ей нечего экспортировать.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#ff0000">
Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#ff0000">
Направление
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Установка только с импортным и экспортным автобусом не будет работать. Импортный автобус попытается вытащить из исходного инвентаря
и хранить элементы или жидкость в сетевом хранилище. Экспортный автобус попытается вытащить из сетевого хранилища и поставить
предметы или жидкость в инвентаре назначения. Однако, поскольку эта сеть не имеет хранилища, шина импорта не может импортировать.
Экспортный автобус не может экспортировать, поэтому ничего не происходит.

## Ввод и вывод через 1 лицо

Допустим, у вас есть машина, которая может принимать входные данные и выводить их через 1 лицо. (Как будто<ItemLink id="charger" />)
Вы можете как протолкнуть ингредиенты, так и извлечь результат, объединив 2 метода подсети труб:

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_storage_export_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="4 1 1" max="5 1.3 2">
(1) Импортный автобус: Можно фильтровать.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 1 1" max="3 1.3 2">
(2) Складской автобус: Можно фильтровать. Это (и другие шины хранения, которые вы хотите толкать и тянуть предметы)
Это должно быть единственное хранилище в сети.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0 1" max="3 1 2">
(3) То, что вы хотите подтолкнуть и вытащить из: В данном случае зарядка.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 1" max="1 1.3 2">
4 Экспортные автобусы: Нужно фильтровать.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 1.5" color="#00ff00">
Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 1.5" color="#00ff00">
Направление
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Интерфейсы

Оказывается, есть [устройства]../ae2-mechanics/devices.md) помимо импортных автобусов и экспортных автобусов, которые вталкивают товары в
и вытаскивать предметы из [сетевого хранилища]../ae2-mechanics/import-export-storage.md)!
Актуальность здесь заключается в<ItemLink id="interface" />. Если элемент вставлен, что интерфейс не настроен на запас, интерфейс будет
Подтолкните его к сетевому хранению, которое мы можем использовать аналогично шине импорта -> трубе шины хранения. Настройка интерфейса для
запас какой-то товар вытащит его из сетевого хранилища, аналогичного накопительному автобусу -> экспортная автобусная труба. Интерфейсы могут быть установлены
запасайте некоторые вещи, а не запасайте другие, что позволяет вам удаленно проталкивать и протягивать через шины хранения, если вы по какой-то причине хотите это сделать.

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/interface_pipes.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
Интерфейс
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
Складской автобус
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.7 0 2" max="4 1 3">
Складской автобус
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 2" max="1 1.3 3">
Интерфейс
  </BoxAnnotation>

<IsometricCamera yaw="195" pitch="30" />
</GameScene>

One-To-Many и Many-To One (и многие-ко-многим)

Конечно, вам не нужно использовать только один<ItemLink id="import_bus" />или<ItemLink id="export_bus" />или<ItemLink id="storage_bus" />

<GameScene zoom="3" background="transparent">
<ImportStructure src="../assets/assemblies/many_to_many_pipe.snbt" />

<IsometricCamera yaw="185" pitch="30" />
</GameScene>

## Предоставление нескольких мест

Из всего этого мы можем получить метод отправки ингредиентов из одного источника.<ItemLink id="pattern_provider" />лицом ко многим различным
Места, такие как множество машин или несколько разных лиц одной машины.

Мы не хотим импорта Хранилище или склад -> экспортные трубы, поскольку<ItemLink id="pattern_provider" />никогда
На самом деле содержит ингредиенты. Вместо этого поставщики * подталкивают * ингредиенты к смежным запасам, поэтому нам нужны некоторые из них.
смежный инвентарь, который также может импортировать товары.

Это звучит как...<ItemLink id="interface" />!
Убедитесь, что провайдер находится в режиме направленной или плоской подчасти, и / или интерфейс находится в режиме плоской подчасти, поэтому они не образуют сеть.
связь.

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
Интерфейс (должен быть плоским, а не полным блоком)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 4">
Складские автобусы
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 1 4">
Места, которые вы хотите предоставить шаблону (несколько машин или несколько лиц 1 машины)
  </BoxAnnotation>

<IsometricCamera yaw="185" pitch="30" />
</GameScene>