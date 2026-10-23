---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Молекулярный сборщик
  icon: molecular_assembler
  position: 310
categories:
- machines
item_ids:
- ae2:molecular_assembler
---

# Молекулярный сборщик

<BlockImage id="molecular_assembler" scale="8" />

Молекулярный ассемблер принимает элементы, введенные в него, и выполняет операцию, определенную соседним устройством.<ItemLink id="pattern_provider" />,
или вставленный<ItemLink id="crafting_pattern" />, <ItemLink id="smithing_table_pattern" />или<ItemLink id="stonecutting_pattern" />,
Затем подталкивает результат к смежным запасам.

Этот ассемблер имеет рисунок крафта, который определяет рецепт 1 дубового журнала = 4 дубовых досок. Когда дубовые бревна подаются в верхний бункер,
Сборщик ремесел и плюет дубовые доски в нижний бункер.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/standalone_assembler.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Основное использование молекулярного сборщика

Однако их основное использование находится рядом с<ItemLink id="pattern_provider" />. Поставщики шаблонов имеют особое поведение в этом случае.
и отправит информацию о соответствующем образце вместе с ингредиентами соседним сборщикам. Поскольку сборщики автоматически выводят результаты
(a) ремесла к смежным запасам (и, таким образом, в слоты возврата поставщика шаблонов), сборщик на поставщике шаблонов
Это все, что нужно для автоматизации крафта.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/assembler_tower.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Обновления

Молекулярный сборщик поддерживает следующие [обновления]upgrade_cards.md):

*   <ItemLink id="speed_card" />

## Рецепт

<RecipeFor id="molecular_assembler" />

## Примечание

Оптифин нарушает функцию «толкать к смежным запасам», поэтому большинство монтажных установок с сборщиками не будут работать.