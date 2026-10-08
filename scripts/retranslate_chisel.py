#!/usr/bin/env python3
"""Build the curated Russian localization for Chisel Modern 1.4.1.

Block names in Chisel are assembled from a material and a finish.  Keeping the
Russian glossary here makes thousands of repetitive names consistent while the
UI, tooltips and items remain explicitly hand translated.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
from pathlib import Path
from zipfile import ZipFile

EXPECTED_SHA1 = "371ea3c218b6c218a2dd2aa641c26f964c26f533"
LANG_PATH = "assets/chisel/lang/en_us.json"

UI = {
    "block.chisel.auto_chisel": "Автоматическое долото",
    "chisel.button.chisel": "Высечь",
    "chisel.button.chisel_all": "Высечь всё",
    "chisel.button.preview": "Предпросмотр",
    "chisel.message.cooldown": "Долото перезаряжается: %s с",
    "chisel.message.fuzzy_disabled": "Нечёткий режим выключен — обрабатываются только точно совпадающие блоки",
    "chisel.message.fuzzy_enabled": "Нечёткий режим включён — обрабатываются все варианты из группы резьбы",
    "chisel.message.mode_changed": "Выбран режим: %s",
    "chisel.mode.column": "Колонна",
    "chisel.mode.column.desc": "Высекает колонну блоков размером 3×1.",
    "chisel.mode.contiguous": "Смежная область",
    "chisel.mode.contiguous.desc": "Высекает область одинаковых смежных блоков на расстоянии до 10 блоков в любом направлении.",
    "chisel.mode.contiguous_2d": "Смежная область (2D)",
    "chisel.mode.contiguous_2d.desc": "Высекает область одинаковых смежных блоков на расстоянии до 10 блоков в плоскости выбранной грани.",
    "chisel.mode.panel": "Панель",
    "chisel.mode.panel.desc": "Высекает квадрат блоков размером 3×3.",
    "chisel.mode.row": "Ряд",
    "chisel.mode.row.desc": "Высекает ряд блоков размером 1×3.",
    "chisel.mode.single": "Один блок",
    "chisel.mode.single.desc": "Высекает один блок.",
    "chisel.preview": "Предпросмотр",
    "chisel.preview.hollow": "Рамка",
    "chisel.preview.panel": "Панель",
    "chisel.preview.plus": "Крест",
    "chisel.preview.single": "Один блок",
    "chisel.tooltip.brownstone.speed": "Ходьба по этому блоку увеличивает скорость",
    "chisel.tooltip.ctm": "Поддерживает соединяемые текстуры (CTM)",
    "chisel.tooltip.fuzzy": "Нечёткий режим: %s",
    "chisel.tooltip.fuzzy.disabled": "выключен",
    "chisel.tooltip.fuzzy.enabled": "включён",
    "chisel.tooltip.fuzzy.hint": "Ctrl + Shift + ПКМ — переключить нечёткий режим",
    "chisel.tooltip.gui": "ПКМ — открыть интерфейс",
    "chisel.tooltip.holystone.glow": "Излучает святое сияние и искрится",
    "chisel.tooltip.leftclick.1": "ЛКМ по блоку — следующий вариант",
    "chisel.tooltip.leftclick.2": "Shift + ЛКМ — предыдущий вариант",
    "chisel.tooltip.modes": "Shift + ПКМ — сменить режим",
    "chisel.tooltip.offset_tool.1": "ПКМ по блоку — сместить текстуру",
    "chisel.tooltip.offset_tool.2": "Красться + ПКМ — сбросить смещение",
    "chisel.tooltip.power.pertick": "%s FE/т",
    "chisel.tooltip.power.stored": "%s/%s FE",
    "chisel.tooltip.selectedmode": "Выбранный режим: %s",
    "chisel.tooltip.target": "Выберите в интерфейсе целевой вариант резьбы",
    "container.chisel": "Долото",
    "container.chisel.hitech": "Электронное долото",
    "item.chisel.ball_o_moss": "Комок мха",
    "item.chisel.cloud_in_a_bottle": "Облако в бутылке",
    "item.chisel.diamond_chisel": "Алмазное долото",
    "item.chisel.hitech_chisel": "Электронное долото",
    "item.chisel.iron_chisel": "Железное долото",
    "item.chisel.offset_tool": "Эндер-жезл смещения",
    "itemGroup.chisel": "Chisel",
    "jei.chisel.chiseling": "Высекание",
    "jei.chisel.no_craftable": "Нет доступного для создания блока",
}

COLORS = {
    "Black": ("Чёрный", "Чёрное", "Чёрная"),
    "Blue": ("Синий", "Синее", "Синяя"),
    "Brown": ("Коричневый", "Коричневое", "Коричневая"),
    "Cyan": ("Бирюзовый", "Бирюзовое", "Бирюзовая"),
    "Gray": ("Серый", "Серое", "Серая"),
    "Green": ("Зелёный", "Зелёное", "Зелёная"),
    "Light Blue": ("Голубой", "Голубое", "Голубая"),
    "Light Gray": ("Светло-серый", "Светло-серое", "Светло-серая"),
    "Lime": ("Лаймовый", "Лаймовое", "Лаймовая"),
    "Magenta": ("Пурпурный", "Пурпурное", "Пурпурная"),
    "Orange": ("Оранжевый", "Оранжевое", "Оранжевая"),
    "Pink": ("Розовый", "Розовое", "Розовая"),
    "Purple": ("Фиолетовый", "Фиолетовое", "Фиолетовая"),
    "Red": ("Красный", "Красное", "Красная"),
    "White": ("Белый", "Белое", "Белая"),
    "Yellow": ("Жёлтый", "Жёлтое", "Жёлтая"),
}

WOOD = {
    "Acacia": ("Акациевый", "Акациевые"),
    "Bamboo": ("Бамбуковый", "Бамбуковые"),
    "Birch": ("Берёзовый", "Берёзовые"),
    "Cherry": ("Вишнёвый", "Вишнёвые"),
    "Crimson": ("Багровый", "Багровые"),
    "Dark Oak": ("Тёмно-дубовый", "Тёмно-дубовые"),
    "Jungle": ("Тропический", "Тропические"),
    "Mangrove": ("Мангровый", "Мангровые"),
    "Oak": ("Дубовый", "Дубовые"),
    "Spruce": ("Еловый", "Еловые"),
    "Warped": ("Искажённый", "Искажённые"),
}

METALS = {
    "Aluminum": "алюминия", "Bronze": "бронзы", "Cobalt": "кобальта",
    "Copper": "меди", "Electrum": "электрума", "Invar": "инвара",
    "Lead": "свинца", "Nickel": "никеля", "Platinum": "платины",
    "Silver": "серебра", "Steel": "стали", "Tin": "олова", "Uranium": "урана",
}

MATERIALS = {
    "Amber": "Янтарь",
    "Ancient Stone": "Древний камень",
    "Andesite Block": "Андезитовый блок",
    "Antiblock Block": "Антиблок",
    "Arcane Stone Block": "Блок магического камня",
    "Arcane Stone": "Магический камень",
    "Basalt Block": "Базальтовый блок",
    "Birdstone": "Птичий камень",
    "Blood Rune": "Кровавая руна",
    "Custom Brick": "Особый кирпич",
    "Bricks Block": "Кирпичный блок",
    "Brownstone Block": "Блок бурого камня",
    "Certus Quartz Block": "Блок истинного кварца",
    "Charcoal Block": "Блок древесного угля",
    "Cloud Block": "Облачный блок",
    "Coal Block": "Угольный блок",
    "Coal Coke Block": "Блок коксового угля",
    "Cobblestone Block": "Булыжник",
    "Colored Sand": "Цветной песок",
    "Cubits Block": "Блок кубитов",
    "Diabase Block": "Диабазовый блок",
    "Diamond Block": "Алмазный блок",
    "Diorite Block": "Диоритовый блок",
    "Dirt Block": "Земляной блок",
    "Emerald Block": "Изумрудный блок",
    "End Stone Block": "Блок эндерняка",
    "Factory Block": "Заводской блок",
    "Fantasy Block": "Фантазийный блок",
    "Fantasy Block 2": "Фантазийный блок 2",
    "Futura Block": "Блок «Футура»",
    "Futura Circuit": "Схема «Футура»",
    "Glass Block": "Стеклянный блок",
    "Glowstone Block": "Светокамень",
    "Gold Block": "Золотой блок",
    "Granite Block": "Гранитный блок",
    "Grimstone": "Мрачный камень",
    "Holystone": "Священный камень",
    "Ice Block": "Ледяной блок",
    "Icepillar Block": "Ледяная колонна",
    "Iron Block": "Железный блок",
    "Iron Bars": "Железные прутья",
    "Jack o'Lantern": "Светильник Джека",
    "Laboratory Block": "Лабораторный блок",
    "Lapis Block": "Лазуритовый блок",
    "Lavastone Block": "Лавовый камень",
    "Leaves": "Листва",
    "Limestone Block": "Известняковый блок",
    "Magma Block": "Магмовый блок",
    "Marble Block": "Мраморный блок",
    "Marble Pillar": "Мраморная колонна",
    "Mechanical Block": "Механический блок",
    "Imperialistic Block": "Имперский блок",
    "Rebellious Block": "Повстанческий блок",
    "Mossy Cobblestone Block": "Замшелый булыжник",
    "Netherbrick Block": "Блок незерского кирпича",
    "Netherrack Block": "Блок незерака",
    "Obsidian Block": "Обсидиановый блок",
    "Paper Block": "Бумажный блок",
    "Prismarine Block": "Призмариновый блок",
    "Pumpkin": "Тыква",
    "Purpur Block": "Пурпуровый блок",
    "Quartz Block": "Кварцевый блок",
    "Red Sandstone Block": "Блок красного песчаника",
    "Redstone Block": "Редстоуновый блок",
    "Road Line": "Дорожная разметка",
    "Sandstone Block": "Блок песчаника",
    "Sandstone Scribbles": "Узоры на песчанике",
    "Red Sandstone Scribbles": "Узоры на красном песчанике",
    "Shingles": "Черепица",
    "Stone Bricks Block": "Блок каменных кирпичей",
    "Tallow": "Жир",
    "Technical Block": "Технический блок",
    "Technical Block (Transparent)": "Прозрачный технический блок",
    "Temple Block": "Храмовый блок",
    "Temple Mossy": "Замшелый храмовый блок",
    "Terracotta Block": "Терракотовый блок",
    "Thaumium Block": "Блок таум-металла",
    "Tyrian Block": "Тирийский блок",
    "Valentines Block": "Блок ко Дню святого Валентина",
    "Voidstone Block": "Блок камня Пустоты",
    "Voidstone Pillar": "Колонна из камня Пустоты",
    "Energised Voidstone Pillar": "Заряженная колонна из камня Пустоты",
    "Runic Voidstone": "Рунический камень Пустоты",
    "Energised Voidstone": "Заряженный камень Пустоты",
    "Warning Sign": "Предупреждающий знак",
    "Waterstone Block": "Блок водного камня",
    "Woolen Clay": "Шерстяная глина",
    "Fuzzy mode disabled": "Нечёткий режим выключен",
    "Fuzzy mode enabled": "Нечёткий режим включён",
}

STYLES = {
    "Amber": "Янтарь", "Arrayed Bricks": "Узорчатые кирпичи", "Braid": "Плетение",
    "Trodden Bricks": "Истёртые кирпичи", "Disordered Tiles": "Беспорядочная плитка",
    "Small Disordered Tiles": "Мелкая беспорядочная плитка", "Circular": "Круговой узор",
    "Cracked": "Потрескавшийся", "Cracked Bricks": "Потрескавшиеся кирпичи", "Cuts": "Насечки",
    "Dent": "Вмятины", "Encased Bricks": "Обрамлённые кирпичи", "French 1": "Французская кладка 1",
    "French 2": "Французская кладка 2", "Jellybean": "Леденцы", "Layers": "Слои",
    "Mosaic": "Мозаика", "Ornate": "Украшенный", "Panel": "Панель", "Pillar": "Колонна",
    "Prism": "Призма", "Raw": "Необработанный", "Road": "Дорожный", "Slanted": "Наклонный",
    "Small Bricks": "Мелкие кирпичи", "Weathered Bricks": "Выветренные кирпичи", "Bricks": "Кирпичи",
    "Big Tile": "Крупная плитка", "Tiles": "Плитка", "Small Tiles": "Мелкая плитка",
    "Wide Bricks": "Широкие кирпичи", "Twisted": "Витой", "Celtic": "Кельтский узор", "Zag": "Зигзаг",
    "Infernally Arcane": "Инфернальная магия", "Cracked Rock leaking Eldritch Glow": "Треснувший камень с потусторонним сиянием",
    "Nasty Nazca Lines": "Зловещие линии Наски", "Beveled Tile": "Скошенная плитка", "Braaainz": "Мозги-и-и",
    "Temporal Conduit": "Временной проводник", "Engraved Moon": "Выгравированная луна", "Glowing Moon": "Светящаяся луна",
    "Engraved Symbols": "Выгравированные символы", "Glowing Symbols": "Светящиеся символы", "Single Rune": "Одиночная руна",
    "Fine Thaumaturge's Emblem": "Изысканная эмблема тауматурга", "Dark Panel": "Тёмная панель", "Emboss": "Тиснение",
    "Four Tile": "Четыре плитки", "French": "Французская кладка", "French Alt": "Альтернативная французская кладка",
    "French Creeper": "Французская кладка с крипером", "Marker": "Разметка", "Ornate 1": "Украшенный 1", "Ornate 2": "Украшенный 2",
    "Rough": "Грубый", "Small Brick": "Мелкий кирпич", "Small Broken": "Мелкий разбитый", "Small Tile": "Мелкая плитка",
    "Smooth": "Гладкий", "Smooth Creeper": "Гладкий с крипером", "Blank Rune": "Пустая руна", "Arranged Blood Rune": "Узор из кровавых рун",
    "Blood Rune Bricks": "Кирпичи с кровавыми рунами", "Carved Blood Rune": "Резная кровавая руна",
    "Radial Carved Blood Rune": "Радиальная резная кровавая руна", "Classic Panel Blood Rune": "Классическая панель с кровавой руной",
    "Blood Rune Tiles": "Плитка с кровавыми рунами", "Aged Bricks": "Состаренные кирпичи", "Large Bricks": "Крупные кирпичи",
    "Mortarless Bricks": "Кирпичи без раствора", "Varied Bricks": "Разнородные кирпичи", "Yellow Bricks": "Жёлтые кирпичи",
    "Big Tiled": "Крупная плитка", "Tiled": "Плиточный", "Bisected": "Разделённый", "Weathered": "Выветренный",
    "Weathered Big Tile": "Крупная выветренная плитка", "Half-Weathered Big Tile": "Наполовину выветренная крупная плитка",
    "Weathered Tiles": "Выветренная плитка", "Weathered Bisected": "Разделённый выветренный",
    "Half-Weathered": "Наполовину выветренный", "Legacy": "Классический", "Llama": "Лама", "Cloud": "Облако",
    "Vertical Bricks": "Вертикальные кирпичи", "Embossed": "Рельефный", "Indent": "Углубление",
    "Bismuth": "Висмут", "Cells": "Ячейки", "Crushed": "Дроблёный", "Ornate Tiles": "Украшенная плитка",
    "Gem": "Самоцвет", "Gold-Encrusted": "Инкрустированный золотом", "Simple": "Простой", "Purple Space": "Фиолетовый космос",
    "Black Space": "Чёрный космос", "Zelda": "Зельда", "Disordered Bricks": "Беспорядочные кирпичи",
    "Brick-Topped Dirt": "Земля с кирпичным верхом", "Chunky": "Крупнозернистый", "Cobble": "Булыжник", "Happy Souls": "Весёлые души",
    "Horizontal Streaks": "Горизонтальные полосы", "Horizontal": "Горизонтальный", "Nether Bricks": "Незерские кирпичи",
    "Plate": "Плита", "Reinforced Cobble": "Укреплённый булыжник", "Reinforced Dirt": "Укреплённая земля",
    "Vertical Streaks": "Вертикальные полосы", "Vertical Layers": "Вертикальные слои", "Cell": "Ячейка", "Cell Bismuth": "Висмутовая ячейка", "Red Tomes": "Красные фолианты",
    "Tiles Large Bismuth": "Крупная висмутовая плитка", "Tiles Medium Bismuth": "Средняя висмутовая плитка",
    "Masonry": "Каменная кладка", "Tile": "Плитка", "Circuit": "Схема", "Metal Column": "Металлическая колонна",
    "Dotted Rusty Plate": "Точечная ржавая плита", "Blue-Framed Circuit": "Схема в синей рамке",
    "Gold-Plated Circuit": "Позолоченная схема", "Gold-Framed Purple Plates": "Фиолетовые плиты в золотой рамке", "Grinder": "Измельчитель",
    "Yellow-Black Caution Stripes": "Жёлто-чёрные сигнальные полосы", "Orange-White Caution Stripes": "Оранжево-белые сигнальные полосы",
    "Ice Ice Ice": "Лёд, лёд, лёд", "Metal Box": "Металлический короб", "Slightly Rusty Plate": "Слегка ржавая плита",
    "Old Vents": "Старые вентиляционные решётки", "Rusty Plate": "Ржавая плита", "Segmented Rusty Plates": "Сегментированные ржавые плиты",
    "Blue Circuits": "Синие схемы", "Vents": "Вентиляционные решётки", "Purple Wireframe": "Фиолетовый проволочный каркас",
    "Blue Wireframe": "Синий проволочный каркас", "Wireframe": "Проволочный каркас", "Block": "Блок", "Brick": "Кирпич",
    "Brick Faded": "Выцветший кирпич", "Brick Wear": "Потёртый кирпич", "Bricks Chaotic": "Хаотичные кирпичи",
    "Bricks Wear": "Потёртые кирпичи", "Decor": "Декор", "Decor Block": "Декоративный блок",
    "Pillar Decorated": "Украшенная колонна", "AE2 Controller": "Контроллер AE2", "ME Controller": "МЭ-контроллер",
    "Purple ME Controller": "Фиолетовый МЭ-контроллер", "Mysterious Cube": "Таинственный куб", "Cyan Screen": "Бирюзовый экран",
    "Gray Screen": "Серый экран", "Fabulously Wavy": "Сказочно волнистый", "Rainbowliciously Wavy": "Радужно-волнистый",
    "Iron Fence": "Железная ограда", "Chinese": "Китайский", "Asymmetrical Leaded Glass": "Асимметричное витражное стекло",
    "Japanese": "Японский", "Japanese 2": "Японский 2", "Ornate Steel": "Украшенная сталь", "Screen": "Экран",
    "Steel-Framed": "В стальной рамке", "Thick Grid": "Толстая решётка", "Thin Grid": "Тонкая решётка",
    "Bubble": "Пузырьковый", "Dungeon": "Подземелье", "Light": "Светлый", "Gray-Bordered": "С серой каймой",
    "Shale": "Сланец", "Stone-Framed": "В каменной рамке", "Streaks": "Полосы", "Framed": "В рамке",
    "Forestry": "Лесной", "Fancy Panel": "Изысканная панель", "Transparent": "Прозрачный", "Quad": "Четверной",
    "Fancy Quad": "Изысканный четверной", "Neon": "Неоновый", "Neon Panel": "Неоновая панель",
    "Egregious": "Необычный", "Bolted": "С болтами", "Cart": "Тележка", "Caution": "Предупреждение",
    "Coin (Heads)": "Монета (орёл)", "Coin (Tails)": "Монета (решка)", "Shipping Crate": "Транспортный ящик",
    "Dark Crate": "Тёмный ящик", "Light Crate": "Светлый ящик", "Goldeye": "Золотой глаз", "Large Ingot": "Крупный слиток",
    "Machine": "Машинный", "Plates": "Плиты", "Riveted Plates": "Клёпаные плиты", "Scaffold": "Строительные леса",
    "Small Ingot": "Малый слиток", "Stars": "Звёзды", "Thermal": "Тепловой", "Blocks": "Блоки",
    "Rough Blocks": "Грубые блоки", "Chiseled": "Резной", "Construction": "Строительный", "Fancy Tiles": "Изысканная плитка",
    "Flaky": "Слоистый", "Hate": "Ненависть", "Rough Plate": "Грубая плита", "Platform": "Платформа",
    "Platform Tiles": "Плитка платформы", "Grimstone": "Мрачный камень", "Hex Base": "Шестигранная основа", "Hex New": "Новый шестиугольник",
    "Blocks Rough": "Грубые блоки", "Love": "Любовь", "Plate Rough": "Грубая плита", "Holystone": "Священный камень",
    "Moon": "Луна", "Barbed Wire": "Колючая проволока", "Straight": "Прямой", "Frameless": "Без рамки",
    "Frameless Topper": "Верх без рамки", "Thick Cage": "Толстая клетка", "Classic": "Классический", "Classic Thin": "Классический тонкий",
    "Fence": "Ограда", "Modern Fence": "Современная ограда", "Spikes": "Шипы", "Checker Tile": "Шахматная плитка",
    "Direction Left": "Указатель налево", "Direction Right": "Указатель направо", "Dotted Panel": "Точечная панель",
    "Floor Tile": "Напольная плитка", "Fuzz Screen": "Экран с помехами", "Info Console": "Информационная консоль",
    "Large Steel": "Крупная стальная плитка", "Large Tile": "Крупная плитка", "Roundel": "Круглая эмблема", "Small Steel": "Мелкая стальная плитка",
    "Wall Panel": "Стеновая панель", "Wall Vents": "Настенная вентиляция", "Shiny Panel": "Блестящая панель", "Wood-Framed": "В деревянной рамке",
    "Christmas Balls": "Ёлочные шары", "Christmas Lights": "Новогодние огни", "Dead Leaves": "Сухая листва", "Fancy Leaves": "Изысканная листва",
    "Pink Petals": "Розовые лепестки", "Red Rose": "Красная роза", "White Rose": "Белая роза", "Gear": "Шестерня",
    "Hex": "Шестиугольник", "Vent": "Вентиляция", "Glowing Vent": "Светящаяся вентиляция", "Camo": "Камуфляж",
    "Secluded Camo": "Скрытый камуфляж", "Orange Caution": "Оранжевое предупреждение", "White Caution": "Белое предупреждение",
    "Red Caution": "Красное предупреждение", "Blood-Spattered": "Забрызганный кровью", "Meat Bricks": "Мясные кирпичи",
    "Dark Meat Bricks": "Тёмные мясные кирпичи", "Small Meat Bricks": "Мелкие мясные кирпичи", "Blue Lava Brick": "Синий лавовый кирпич",
    "Brown Lava Brick": "Коричневый лавовый кирпич", "Dark Lava Brick": "Тёмный лавовый кирпич", "Stone Lava Brick": "Каменный лавовый кирпич",
    "Nether Brick made of Meat": "Незерский кирпич из мяса", "Red Nether Brick made of Meat": "Красный незерский кирпич из мяса",
    "Small Nether Brick made of Meat": "Мелкий незерский кирпич из мяса", "Small Red Nether Brick made of Meat": "Мелкий красный незерский кирпич из мяса",
    "Red Nether Brick": "Красный незерский кирпич", "Small Red Nether Brick": "Мелкий красный незерский кирпич",
    "Disordered Nether Bricks": "Беспорядочные незерские кирпичи", "Streaked": "Полосатый", "Red Rock": "Красный камень",
    "Gray Rock": "Серый камень", "Blue Streaked": "Синий полосатый", "Blue Shale": "Синий сланец", "Rocky": "Каменистый",
    "Blood-Splatted Rocky": "Каменистый, забрызганный кровью", "Raw Guts": "Сырые внутренности", "Guts": "Внутренности",
    "Meat": "Мясо", "Raw Meat": "Сырое мясо", "Bloody Rock": "Кровавый камень", "Dark Red": "Тёмно-красный",
    "Bloody Blue": "Кроваво-синий", "Medium Tiles": "Средняя плитка", "Chunks": "Обломки", "Crate": "Ящик", "Shiny": "Блестящий",
    "Greek": "Греческий", "Organic Chunks": "Органические куски", "Map (Eastern)": "Карта (восток)", "Map (Western)": "Карта (запад)",
    "Light Panel": "Светлая панель", "Box": "Короб", "Crossed": "Перекрещенный", "Door": "Дверь", "Floral": "Цветочный",
    "Plain": "Простой", "Six-Pack": "Шесть секций", "Horizontally Striked": "Горизонтальные штрихи", "Vertical": "Вертикальный",
    "Bevel Skeleton": "Скошенный скелет", "Glyphs": "Глифы", "Small": "Малый", "Double White Line": "Двойная белая линия",
    "Double Yellow Line": "Двойная жёлтая линия", "White Line": "Белая линия", "Yellow Line": "Жёлтая линия", "Bevel Creeper": "Скошенный крипер",
    "Skull Landscape": "Пейзаж с черепом", "Eye of Horus": "Глаз Гора", "Bird": "Птица", "Halo": "Ореол", "Man with Staff": "Человек с посохом",
    "Waves": "Волны", "Red Diagonal": "Красная диагональ", "Red Small Bricks": "Мелкие красные кирпичи",
    "Red Large Bricks": "Крупные красные кирпичи", "Black Small Bricks": "Мелкие чёрные кирпичи", "Black Diagonal": "Чёрная диагональ",
    "Black Large Bricks": "Крупные чёрные кирпичи", "Large Ornate": "Крупный украшенный", "Poison": "Яд", "Sunken": "Утопленный",
    "Ensouled Tallow": "Одухотворённый жир", "Tallow": "Жир", "Smooth Tallow": "Гладкий жир", "Cables": "Кабели",
    "Caution-Framed Plates": "Плиты в сигнальной рамке", "Engineer's Pipes": "Инженерные трубы", "Exhaust Plating": "Выхлопная обшивка",
    "Fan (Fast)": "Вентилятор (быстро)", "Fan (Off)": "Вентилятор (выключен)", "Grate": "Решётка", "Rusty Grate": "Ржавая решётка",
    "Industrial Relic": "Промышленная реликвия", "Insulation": "Изоляция", "Sloppy Plating": "Неаккуратная обшивка",
    "Fan (Malfunctioning)": "Вентилятор (неисправен)", "Massive Fan": "Огромный вентилятор", "Massive Hexagonal Plating": "Огромная шестигранная обшивка",
    "Megacell Battery": "Батарея мегаячейки", "Oldetimey Server": "Старинный сервер", "Large Pipes": "Крупные трубы", "Small Pipes": "Мелкие трубы",
    "Pipes": "Трубы", "Rusty Bolted Plates": "Ржавые плиты с болтами", "Rusty Scaffold": "Ржавые строительные леса",
    "Large Rusty Scaffold": "Крупные ржавые строительные леса", "Gears & Flywheels": "Шестерни и маховики", "Sturdy": "Прочный",
    "Tape Drive": "Ленточный накопитель", "Extremely Corroded Panels": "Сильно корродированные панели",
    "Extremely Rusted Panels": "Сильно проржавевшие панели", "Fan (Still)": "Вентилятор (неподвижен)", "Column": "Колонна",
    "Cracked Plate": "Треснувшая плита", "Light Small Tiles": "Светлая мелкая плитка", "Stand": "Подставка",
    "Creeper Stand": "Подставка с крипером", "Mosaic Stand": "Мозаичная подставка", "Light Medium Tiles": "Светлая средняя плитка",
    "Damaged Bricks": "Повреждённые кирпичи", "Bevel": "Скошенный", "Lattice": "Решётчатый", "Ornate Pattern": "Украшенный узор",
    "Bounded Planks": "Скреплённые доски", "Thaumium": "Таум-металл", "Totem Faces": "Лики тотема",
    "Black Scaled Plates": "Чёрные чешуйчатые плиты", "Black Strips": "Чёрные полосы", "Blue Plates": "Синие плиты",
    "Disordered Purple Bits": "Беспорядочные фиолетовые фрагменты", "Diagonal Plates": "Диагональные плиты", "Shiny Plates": "Блестящие плиты",
    "Opening": "Проём", "Shiny Plate": "Блестящая плита", "Small Uneven Tiles": "Мелкая неровная плитка", "Routes": "Маршруты",
    "Rust": "Ржавчина", "Disordered Metal Bits": "Беспорядочные металлические фрагменты", "Purple Plates": "Фиолетовые плиты",
    "Metal Plates": "Металлические плиты", "Pink Marker": "Розовая разметка", "Gray Rocky": "Серый каменистый",
    "Pink Heart": "Розовое сердце", "Pink Rocky": "Розовый каменистый", "Pink Tile": "Розовая плитка", "Pink Cracks": "Розовые трещины",
    "Pink Studded": "Розовый с заклёпками", "Flame": "Пламя", "Pink Steel": "Розовая сталь", "Companion Cube": "Куб-компаньон",
    "Eye": "Глаз", "Metallic": "Металлический", "Rune": "Руна", "Skulls": "Черепа", "Striped": "Полосатый",
    "Acid": "Кислота", "Biohazard": "Биологическая опасность", "Cryogenic": "Криогенная опасность", "Death": "Смертельная опасность",
    "Explosion": "Опасность взрыва", "Fall": "Опасность падения", "Falling": "Падающие предметы", "Fire": "Пожарная опасность",
    "Generic": "Общее предупреждение", "No Entry": "Вход запрещён", "Oxygen": "Кислород", "Radiation": "Радиация",
    "Sound": "Громкий звук", "Under Construction": "Ведутся работы", "Voltage": "Высокое напряжение",
    "chisels exact block matches only": "обрабатывает только точно совпадающие блоки",
    "chisels all variants in the carving group": "обрабатывает все варианты из группы резьбы",
}

# Repeated numbered/decorative labels that need no individual wording.
WORD_PARTS = {
    "Cubit": "Кубит", "Face": "Грань", "Gold Decor": "Золотой декор", "Hieroglyphs": "Иероглифы",
    "Landscape": "Пейзаж", "Pattern": "Узор", "Skull": "Череп", "Scribed Pillar": "Расписная колонна",
    "Convexed Pillar": "Выпуклая колонна", "Small-Concaved Pillar": "Малая вогнутая колонна",
    "Large Pillar": "Крупная колонна", "Simple Pillar": "Простая колонна",
    "Greek-Capped Greek Pillar": "Греческая колонна с греческой капителью",
    "Plain-Capped Greek Pillar": "Греческая колонна с простой капителью",
    "Convexed-Capped Plain Pillar": "Простая колонна с выпуклой капителью",
    "Ornamental Pillar": "Орнаментальная колонна",
    "Greek-Capped Plain Pillar": "Простая колонна с греческой капителью",
    "Plain-Capped Plain Pillar": "Простая колонна с простой капителью",
    "Decor-Capped Greek Pillar": "Греческая колонна с декоративной капителью",
    "Decor-Capped Plain Pillar": "Простая колонна с декоративной капителью",
    "Decor-Capped Wide Pillar": "Широкая колонна с декоративной капителью",
    "Greek-Capped Wide Pillar": "Широкая колонна с греческой капителью",
    "Plain-Capped Wide Pillar": "Широкая колонна с простой капителью",
    "Braced Planks": "Укреплённые доски", "Crude Horizontal Planks": "Грубые горизонтальные доски",
    "Crude Paneling": "Грубая обшивка", "Crude Vertical Planks": "Грубые вертикальные доски",
    "Encased Large Planks": "Обрамлённые крупные доски", "Encased Planks": "Обрамлённые доски",
    "Encased Smooth": "Обрамлённый гладкий", "Large Planks": "Крупные доски", "Log Cabin": "Сруб",
    "Paneling": "Обшивка", "Stacked": "Сложенный", "Vertical Planks": "Вертикальные доски",
}

TOKEN_FALLBACK = {
    "Abandoned": "Заброшенный", "Brim": "Кайма", "Cans": "Банки", "Historian": "Историк",
    "Hoarder": "Собиратель", "Necromancer": "Некромант", "Novice": "Начинающий", "Stacks": "Стопки",
    "Of": "из", "Papers": "бумаги", "Rainbow": "Радуга", "Tomes": "Фолианты", "Gold": "Золотой",
    "Decor": "декор", "Tiles": "плитка", "Large": "крупный", "Medium": "средний", "Rough": "грубый",
    "Plain": "простой", "Pillar": "колонна", "Greek": "греческий", "Capped": "с капителью",
}


def translate_material(text: str) -> str:
    if text in MATERIALS:
        return MATERIALS[text]
    for color, forms in COLORS.items():
        if text == f"{color} Carpet": return f"{forms[0]} ковёр"
        if text == f"{color} Concrete": return f"{forms[0]} бетон"
        if text == f"{color} Stained Glass": return f"{forms[1]} стекло"
        if text == f"{color} Dyed Glass": return f"{forms[1]} окрашенное стекло"
        if text == f"{color} Dyed Glass Pane": return f"{forms[2]} окрашенная стеклянная панель"
        if text == f"{color} Hex Plating": return f"{forms[2]} шестигранная обшивка"
        if text == f"{color} Wool": return f"{forms[2]} шерсть"
        if text == color: return forms[0]
    for wood, forms in WOOD.items():
        if text == f"{wood} Bookshelf": return f"{forms[0]} книжный шкаф"
        if text == f"{wood} Planks": return f"{forms[1]} доски"
    for metal, genitive in METALS.items():
        if text == f"{metal} Block": return f"Блок {genitive}"
    raise KeyError(f"No Russian material for {text!r}")


def translate_style(text: str) -> str:
    if text in STYLES:
        return STYLES[text]
    for color, forms in COLORS.items():
        if text == color:
            return forms[0]
    for prefix, translation in sorted(WORD_PARTS.items(), key=lambda item: -len(item[0])):
        if text == prefix:
            return translation
        if text.startswith(prefix + " ") and text[len(prefix) + 1:].isdigit():
            return f"{translation} {text[len(prefix) + 1:]}"
    words = text.split()
    if words and all(word.rstrip(".,:;()") in TOKEN_FALLBACK or word.isdigit() for word in words):
        return " ".join(TOKEN_FALLBACK.get(word.rstrip(".,:;()"), word) for word in words)
    raise KeyError(f"No Russian finish for {text!r}")


def load_english(jar: Path) -> dict[str, str]:
    digest = hashlib.sha1(jar.read_bytes()).hexdigest()
    if digest != EXPECTED_SHA1:
        raise SystemExit(f"Unexpected Chisel JAR SHA-1: {digest}; expected {EXPECTED_SHA1}")
    with ZipFile(jar) as archive:
        return json.loads(archive.read(LANG_PATH))


def build(english: dict[str, str]) -> dict[str, str]:
    result: dict[str, str] = {}
    errors: list[str] = []
    for key, value in english.items():
        if key in UI:
            result[key] = UI[key]
        elif " - " in value:
            material, style = value.split(" - ", 1)
            try:
                result[key] = f"{translate_material(material)} — {translate_style(style)}"
            except KeyError as exc:
                errors.append(f"{key}: {exc}")
        else:
            errors.append(f"{key}: no explicit UI translation")
    if errors:
        raise SystemExit("Incomplete curated Chisel glossary:\n" + "\n".join(errors))
    return result


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", type=Path, required=True)
    parser.add_argument("--output", type=Path, default=Path("localization/ru-pack/assets/chisel/lang/ru_ru.json"))
    args = parser.parse_args()
    translated = build(load_english(args.jar))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(translated, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {len(translated)} curated Chisel translations to {args.output}")


if __name__ == "__main__":
    main()
