---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Я Каннер
    icon: extendedae:caner
categories:
- extended devices
item_ids:
- extendedae:caner
---

#Меня Каннер

<BlockImage id="extendedae:caner" scale="8"></BlockImage>

ME Canner - это машина для «сканирования» материалов, включая жидкости, газ меканизма, ману Ботании и даже энергию!

Первый слот удерживает заполняющий материал, а второй слот удерживает предмет для заполнения.

Он нуждается в энергии для работы, и каждая операция стоит 80 АЕ.

[GUI] (../pic/caner_gui.png)

По умолчанию он заполняет только жидкости; установите соответствующий аддон, чтобы он заполнял другие материалы.

#### Поддерживаемые дополнения:
Прикладной поток
Прикладная меканистика
Прикладной ботанический аддон

Оригинальное название: Autocrafting with ME Caner

Только верхняя и нижняя стороны могут принимать энергию и подключаться к сети.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/caner_example.snbt"></ImportStructure>
</GameScene>

Простая настройка для ME Canner. ME Canner автоматически выбрасывает заполненный элемент, когда он получает ингредиенты.<ItemLink id="ae2:pattern_provider" />.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/caner_auto.snbt"></ImportStructure>
</GameScene>

Узор должен содержать только заполняющий материал и контейнер, подлежащий заполнению. Вот несколько примеров:

Заполните ведро с водой:

[P1] (../pic/fill_water.png)

Планшет Empower Energy Tablet (требует прикладного потока):

![P1](../pic/fill_energy.png)


##Сканирование

ME Canner также может сливать материалы из контейнера в режиме Empty. Вам нужно переключать входы и выходы в шаблоне.
