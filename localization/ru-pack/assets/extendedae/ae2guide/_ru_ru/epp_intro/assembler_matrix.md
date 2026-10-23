---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Матрица сборщика
    icon: extendedae:assembler_matrix_frame
categories:
- extended devices
item_ids:
- extendedae:assembler_matrix_frame
- extendedae:assembler_matrix_wall
- extendedae:assembler_matrix_glass
- extendedae:assembler_matrix_pattern
- extendedae:assembler_matrix_crafter
- extendedae:assembler_matrix_speed
---

Матрица сборщиков

<Row>
<BlockImage id="extendedae:assembler_matrix_frame" p:formed="true" p:powered="true" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_wall" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_glass" scale="5"></BlockImage>
</Row>
<Row>
<BlockImage id="extendedae:assembler_matrix_pattern" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_crafter" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_speed" scale="5"></BlockImage>
</Row>

Assembler Matrix — многоблочная структура. Это сочетание<ItemLink id="ae2:molecular_assembler" />и<ItemLink id="ae2:pattern_provider" />.
Он может выполнять много крафтовых работ одновременно (с достаточным количеством рабочих мест).<ItemLink id="ae2:crafting_accelerator" />s в вашей сети ME) и сохранять каналы для вас.

## Структура

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/assembler_matrix.snbt"></ImportStructure>
</GameScene>

Это прямоугольная призма, с длиной края от 3 до 7.
- Эджес состоит из матрицы Ассамблера.
Лица состоят из Assembler Matrix Wall/Glass.
Интерьер состоит из Assembler Matrix Pattern/Craft/Speed Core.

Действительная матрица сборщика должна содержать по меньшей мере одно ядро шаблона и одно ядро ремесла.
Она должна быть полностью заполнена и не может быть полой.
Когда матрица сборщика правильно сформирована и приведена в действие, линии на матрице сборщика станут синими.

##Матрица сборщика

Существует 3 различных матрицы сборки.

Assembler Matrix Pattern Core

Assembler Matrix берет шаблоны из своего ядра шаблонов. Каждый шаблон Core предоставляет 36 слотов для матрицы сборщиков.

Сборщик Matrix Craft Core

Assembler Matrix присваивает полученные крафтовые работы своим Craft Cores. Каждое ремесло Core может одновременно выполнять 8 крафтовых работ.

- Assembler Matrix Speed Core

Это<ItemLink id="ae2:speed_card" />Для матрицы сборщиков. 5 ядер скорости позволяют матрице сборщика работать на полной скорости.
Установка более 5 ядер скорости не даст дополнительного увеличения скорости.

## GUI

Прямой щелчок по сформированной и онлайн-матрице сборщика открывает свой графический интерфейс.

![GUI](../pic/assembler_matrix.png)

Вы можете добавить или искать шаблоны в нем и посмотреть, сколько рабочих мест он выполняет.
