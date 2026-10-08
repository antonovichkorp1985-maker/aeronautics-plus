---
navigation:
  parent: example-setups/example-setups-index.md
  title: Рекурсивное ремесло
  icon: minecraft:netherite_upgrade_smithing_template
---

# Рекурсивная настройка ремесла

Как указано в [автопрокат]../ae2-mechanics/autocrafting.md), алгоритм планирования автопроизводства не может обрабатывать рецепты, где
Первичный выход является одним из входов. Например, он не может справиться с клонированием.<ItemLink id="minecraft:netherite_upgrade_smithing_template" />s.

Одним из решений является использование<ItemLink id="level_emitter" />Способность притворяться образцом../items-blocks-machines/patterns.md).

Затем он будет использоваться для включения небольшой установки, которая постоянно выполняет ремесло. В этом случае мы рассмотрим установку
клонировать<ItemLink id="minecraft:netherite_upgrade_smithing_template" />s.

<RecipeFor id="minecraft:netherite_upgrade_smithing_template" />

***

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/recursive_recipe_setup.snbt" />

  <BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
(1) Интерфейс: устанавливается для запаса необходимых дополнительных ингредиентов: алмаза и нитрэка.
        <Row><ItemImage id="minecraft:diamond" scale="2" /> <ItemImage id="minecraft:netherrack" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 1 0.3" max="2.7 1.3 0.7">
2 Уровень эмиттер: Настроенный с «нетеритным кузнечным шаблоном», установленный на «Emit Redstone to craft item».
        <Row><ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 0 0" max="2.3 1 1">
(3) Импортный автобус No1: Отфильтрованный на предметах, которые заполняет интерфейс. Имеет Redstone Card. Режим Redstone установлен
"Активно с сигналом".
        <Row>
        <ItemImage id="minecraft:diamond" scale="2" />
        <ItemImage id="minecraft:netherrack" scale="2" />
        <ItemImage id="redstone_card" scale="2" />
        </Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="4 1.3 2">
(4) Автобус хранения No1: Настройка на более высокий приоритет, чем другой автобус хранения. Очень важно.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 0 1" max="4 1 2">
(5) Молекулярный сборщик: Имеет в ней рисунок для дублирования кузнечного шаблона.

![Паттерн](../активы/диаграммы/смайтинг_template_pattern_small.png)

Он также имеет один кузнечный шаблон, уже вставленный в него вручную, когда вы впервые создаете его.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
(6) Импортная шина No2: в конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 1" max="2 1 1.3">
(7) Автобус для хранения No2: Отфильтрован на «нетеритный кузнечный шаблон». Установите более низкий приоритет, чем у другой шины хранения.
        <ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="15" pitch="30" />
</GameScene>

## Конфигурации

* The<ItemLink id="interface" />(1) настроен на запас необходимых дополнительных ингредиентов: алмаза и нитрэка.
* The<ItemLink id="level_emitter" />(2) сконфигурирован с "нетеритным кузнечным шаблоном" и установлен на "Emit Redstone to craft item".
* Первый<ItemLink id="import_bus" />(3) фильтруется на элементы, которые заполняет интерфейс. Имеется карта Redstone. Режим Redstone настроен на «активность с сигналом».
* Первый<ItemLink id="storage_bus" />(4) устанавливается на *высший* [приоритет]()../ae2-mechanics/import-export-storage.md#storage-priority) чем второй автобус для хранения.
* The<ItemLink id="molecular_assembler" />(5) имеет шаблон для дублирования кузнечного шаблона, и один кузнечный шаблон уже вставлен вручную.

![Паттерн](../активы/диаграммы/смайтинг_template_pattern.png)

* Второй<ItemLink id="import_bus" />(6) находится в конфигурации по умолчанию.
* Второй<ItemLink id="storage_bus" />(7) отфильтровывается на "нетеритный кузнечный шаблон". Имеет *низкий* [приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) чем первый автобус для хранения.

## Как это работает

1. The<ItemLink id="level_emitter" />Притворяется, что является [образцом]../items-blocks-machines/patterns.md) вследствие вставки
   <ItemLink id="crafting_card" />Называется она «Emit Redstone to craft item». Таким образом, «нетеритный кузнечный шаблон» появляется в
[терминалы]../items-blocks-machines/terminals.md) в качестве действительной вещи для [автомобилестроения]../ae2-mechanics/autocrafting.md).
2. При получении запроса на создание этого элемента либо от игрока, либо от самой системы включается излучатель уровня.
3. Первый<ItemLink id="import_bus" />активируется эмиттером уровня и извлекает ингредиенты, запасенные в<ItemLink id="interface" />.
4. Единственный<ItemLink id="storage_bus" />В сети, которая может хранить эти ингредиенты, есть тот, который находится на ассемблере.
5. The<ItemLink id="molecular_assembler" />получает ингредиенты (уже имеющие 1 кузнечный рисунок внутри), и выполняет ремесло, производя 2 кузнечных узора.
6. Второй<ItemLink id="import_bus" />Извлекает 1 кузнечный рисунок.
7. Первая шина хранения имеет более высокий приоритет, так что кузнечный рисунок возвращается в сборщик.
8. Второй<ItemLink id="import_bus" />Извлекает 1 кузнечный рисунок.
9. Сборщик не может получить другой шаблон кузнечного дела, поэтому второй шаблон кузнечного дела переходит на шину хранения с более низким приоритетом, вставляя ее в интерфейс.
10. The<ItemLink id="interface" />Не будучи настроенным на шаблоны кузнечного дела, вставляет его в сеть.