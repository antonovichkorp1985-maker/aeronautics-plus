---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Контроллер
  icon: controller
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:controller
---

# Контролер

<BlockImage id="controller" p:state="online" scale="8" />

Контроллер — это концентратор маршрутизации [ME-сети].../ae2-mechanics/me-network-connections.md).
Без него сеть является «специальной» и может иметь только максимум 8 каналов.../ae2-mechanics/devices.mdВсего.

Невозможно иметь 2 контроллера в одной сети.../ae2-mechanics/me-network-connections.md).

Контроллер предоставляет 32 [канала]../ae2-mechanics/channels.md) на лицо.

Контроллер требует 6 АЕ/т на блок контроллера для
функции. Каждый блок контроллера может хранить 8000 AE, поэтому для больших сетей может потребоваться дополнительное оборудование.
хранение энергии. См. [энергия]../ae2-mechanics/energy.md) для подробностей.

Многоблоковые контроллеры могут быть построены в довольно свободной форме.

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../assets/assemblies/controllers.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Однако есть несколько правил, которые необходимо соблюдать:

1. Все блоки контроллеров в [ME-сети]../ae2-mechanics/me-network-connections.md) должны быть соединены; в противном случае блоки станут красными.
2. Размер контроллера должен быть в пределах 7x7x7, иначе он станет красным.
3. Контроллер может иметь 2 соседних блока не более чем в 1 оси; если блок нарушает это правило, он отключится и покраснеет.

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../assets/assemblies/controller_rules.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Пока все правила соблюдены и приведены в действие, контроллер должен светиться и
Цвета цикла.

Вы можете щелкнуть правой кнопкой мыши на контроллере, чтобы получить тот же графический интерфейс, что и<ItemLink id="network_tool" />

## Рецепт

<RecipeFor id="controller" />
