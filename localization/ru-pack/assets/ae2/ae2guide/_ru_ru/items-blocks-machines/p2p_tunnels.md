---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Туннели P2P
  icon: me_p2p_tunnel
  position: 210
categories:
- devices
item_ids:
- ae2:me_p2p_tunnel
- ae2:redstone_p2p_tunnel
- ae2:item_p2p_tunnel
- ae2:fluid_p2p_tunnel
- ae2:fe_p2p_tunnel
- ae2:light_p2p_tunnel
---

Туннели Point To Point

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_tunnels.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

P2P-туннели - это способ перемещения предметов, жидкостей, сигналов красного камня, мощности, света и [каналов].../ae2-mechanics/channels.md)
вокруг сети без непосредственного взаимодействия с ней. Существует множество вариантов P2P-туннеля, но каждый из них имеет свои особенности.
Перевозит только свой специфический вид. Они действуют как порталы, которые напрямую соединяются.
На расстоянии два квартала. Они не являются двунаправленными, есть определенные входы и выходы.

[Портал](../активы/сборки/p2p_portal.png)

Например, бункер, обращенный к элементу P2P, будет действовать так, как если бы он был непосредственно подключен к стволу, и предметы будут течь.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_hopper_barrel.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Однако два бочка рядом друг с другом не будут передавать предметы между собой.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_barrel_barrel.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Есть и другие варианты, такие как Redstone P2P.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_redstone.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

И ME P2P, который перемещает каналы.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_channels.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Типы туннелей P2P и настройки

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_tunnels.snbt" />
  <IsometricCamera yaw="180" pitch="90" />
</GameScene>

Существует несколько типов туннелей P2P. Только туннель ME P2P является непосредственно обрабатываемым, остальные сделаны путем щелчка правой кнопкой мыши другим.
Туннели P2P с определенными элементами:
- туннели ME P2P выбираются щелчком правой кнопкой мыши с любым [кабелем]../items-blocks-machines/cables.md).
- туннели Redstone P2P выбираются путем щелчка правой кнопкой мыши с различными компонентами красного камня.
- Пункт P2P туннелей выбирают правым щелчком сундука или бункера.
Жидкие туннели P2P выбираются правым щелчком ковша или бутылки.
Энергетические туннели P2P выбираются путем щелчка правой кнопкой мыши практически любым энергосодержащим элементом.
- Световые туннели P2P выбираются правым щелчком факела или светящегося камня

Некоторые типы туннелей имеют причуды. Например, каналы туннелей ME P2P не могут проходить через другие туннели ME P2P.
Энергетические туннели P2P косвенно извлекают налог в размере 2,5% на FE, протекающий через них.
[энергия]../ae2-mechanics/energy.md) рисовать.

Самая популярная форма P2P

Наиболее распространенным вариантом использования туннелей P2P является использование туннеля ME P2P для уплотнения плотности канала.../ae2-mechanics/channels.md) транспорт.
Вместо пучка плотного кабеля можно использовать один плотный кабель для переноса многих каналов.

В этом примере 8 входов ME P2P берут 256 каналов (8*32) из основной сети.<ItemLink id="controller" />8 ME P2P выходы
Выведите их в другое место. Посмотрите, как каждый вход или выход P2P туннеля занимает 1 канал. Таким образом, можно запустить несколько каналов.
Через тонкий кабель. А так как наши туннели P2P находятся в выделенной [подсети]../ae2-mechanics/subnetworks.md), мы не
Используйте для этого любые каналы из основной сети! Читайте также: Туннели P2P можно установить напрямую
Смарт-кабель (Dense Smart cable)../items-blocks-machines/cables.md#smart-cable) могут быть размещены между ними, чтобы легче визуализировать каналы.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/p2p_compact_channels.snbt" />

  <BoxAnnotation color="#dddddd" min="1.3 1.3 6.3" max="2 2.7 6.7">
Quartz Fiber делит энергию между основной сетью и подсетью p2p.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4.1 0 5.7" max="5 2.3 6.4">
Вы можете либо поместить вход туннеля непосредственно на контроллер, либо в него поместить кабель.
  </BoxAnnotation>

  <IsometricCamera yaw="225" pitch="30" />
</GameScene>

Для другого примера (включая его использование с [квантовыми мостами])quantum_bridge.md) см. диаграмму MS Paint I can't be bothered
Чтобы прикоснуться:

![P2P и квантовые мосты](../активы/диаграммы/p2p_quantum_network.png)

## Нестинг

Однако вы не можете использовать это для передачи бесконечных каналов через один кабель. Канал для туннеля ME P2P не будет
Пройдите через другой туннель ME P2P, так что вы не сможете повторно вставить их. Посмотрите, как внешний слой туннелей ME P2P
Красные кабели отключены. Обратите внимание, что это относится только к туннелям ME P2P, другие типы туннелей P2P могут проходить через туннель ME P2P.
Тоннели Redstone P2P работают нормально.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_nesting.snbt" />
  <IsometricCamera yaw="225" pitch="30" />
</GameScene>

##Связь

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_linking_frequency.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Концы туннельного соединения P2P могут быть соединены с помощью<ItemLink id="memory_card" />. Частота будет отображаться
как массив цветов 2х2 на задней части туннеля.
- Shift-right-click для генерации новой частоты P2P-ссылок.
- Нажмите правой кнопкой мыши, чтобы вставить настройки, обновить карты или частоту ссылок.

Туннель, который вы перемещаете вправо, будет входом, а туннель, в который вы щелкаете вправо, будет выходом. Вы можете иметь несколько выходов,
Но с туннелями ME P2P каналы, текущие во входе, будут разделены между выходами, поэтому вы не можете дублировать каналы.

## Рецепт

<RecipeFor id="me_p2p_tunnel" />