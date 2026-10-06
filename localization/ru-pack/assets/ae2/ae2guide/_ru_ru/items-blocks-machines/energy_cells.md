---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Энергетические клетки
  icon: energy_cell
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:energy_cell
- ae2:dense_energy_cell
- ae2:creative_energy_cell
---

# Энергетические клетки

<Row gap="20">
  <BlockImage id="energy_cell" scale="8" p:fullness="4" />

  <BlockImage id="dense_energy_cell" scale="8" p:fullness="4" />

  <BlockImage id="creative_energy_cell" scale="8" />
</Row>

Энергетические ячейки дают сети больше энергии.../ae2-mechanics/energy.md) хранение. Некоторое количество энергетического буфера помогает сгладить
всплески потребления энергии, когда большие количества предметов вставляются или извлекаются, и большие объемы хранения энергии
Позволяет сети работать, пока энергия не генерируется (например, ночью с солнечными батареями) или обрабатывать массивные мгновенные сигналы.
энергозатраты [пространственного хранения]../ae2-mechanics/spatial-io.md).

## Заполните бары

<Row>
<BlockImage id="energy_cell" scale="4" p:fullness="0" />
<BlockImage id="energy_cell" scale="4" p:fullness="1" />
<BlockImage id="energy_cell" scale="4" p:fullness="2" />
<BlockImage id="energy_cell" scale="4" p:fullness="3" />
<BlockImage id="energy_cell" scale="4" p:fullness="4" />
</Row>

Бары на стороне клетки соответствуют количеству энергии, которое она имеет.

* 0 при цене ниже 25%
* 1 при зарядке от 25% до 50%
* 2 при зарядке от 50% до 75%
* 3 при зарядке от 75% до 99%
* 4, если цена выше 99%

## Типы клеток

* The<ItemLink id="energy_cell" />может хранить 200k AE, и только одного должно быть достаточно для большинства случаев использования, обрабатывая перепады мощности.
Нормальное использование сети с легкостью.
* The<ItemLink id="dense_energy_cell" />может хранить 1,6M AE и предназначен для тех случаев, когда вы хотите запустить сеть из сохраненной мощности;
обрабатывать массивный мгновенный поток энергии большого [пространственного хранилища]../ae2-mechanics/spatial-io.md) установки.
* The<ItemLink id="creative_energy_cell" />Это творческий элемент для тестирования, предоставляющий неограниченную воду или что-то еще.

## Рецепты

<Row>
  <RecipeFor id="energy_cell" />

  <RecipeFor id="dense_energy_cell" />
</Row>
