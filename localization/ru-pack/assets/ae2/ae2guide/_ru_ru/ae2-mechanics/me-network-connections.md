---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: Сетевые подключения
  icon: fluix_glass_cable
---

# Сетевые подключения

## Что означает «сеть»?

«Сеть» — это группа [устройств].../ae2-mechanics/devices.md) связаны блоками, которые могут проходить [каналы](../ae2-mechanics/channels.md),
как [кабели]../items-blocks-machines/cables.md) или полноблокировочные машины и [устройства]../ae2-mechanics/devices.md). 
(<ItemLink id="charger" />, <ItemLink id="interface" />, <ItemLink id="drive" />и т.д.
Технически, один кабель — это сеть.

##Помимо позиционирования устройства

Для [устройств]../ae2-mechanics/devices.mdкоторые имеют определенную сетевую функцию (например,<ItemLink id="interface" />
Нажимать и вытягивать из [сетевого хранилища]../ae2-mechanics/import-export-storage.md), a <ItemLink id="level_emitter" />
чтение содержимого сетевого хранилища, а<ItemLink id="drive" />сетевое хранилище и т.д.
Физическое положение устройства не имеет значения.

Опять же, ** физическое положение устройства не имеет значения **. Важно лишь то, что устройство подключено к сети.
(и, конечно, к какой сети он подключен).

## Сетевые соединения

Простой способ определить, что подключено к сети, - это использовать<ItemLink id="network_tool" />. Он покажет каждому
компонент в сети, так что если вы видите вещи, которые вы не должны или не видите, у вас есть проблема.

Например, это 2 отдельные сети.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/2_networks_1.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1 2 2">
Сеть 1
  </BoxAnnotation>

<BoxAnnotation color="#5CA7CD" min="2 0 0" max="3 2 2">
Сеть 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Это также 2 отдельные сети, потому что<ItemLink id="quartz_fiber" />Акции [энергия]../ae2-mechanics/energy.md)
без обеспечения сетевого подключения.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/2_networks_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1 2 2">
Сеть 1
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="1.3 0 0" max="3 2 2">
Сеть 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Однако это всего лишь 1 сеть, а не 2 отдельных. [Квантовый мост]../items-blocks-machines/quantum_bridge.md) действует как
Беспроводной [плотный кабель]../items-blocks-machines/cables.md#dense-cableТаким образом, оба конца находятся в одной сети.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/actually_1_network.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="7 3 3">
Все 1 сеть
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Это также всего 1 сеть, как [кабель].../items-blocks-machines/cables.md) цвет не имеет ничего общего с сетевыми соединениями, кроме кабелей разных цветов
соединяться друг с другом. Все цвета соединяются с кабелями Fluix (или «нецветными»).

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/actually_1_network_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="4 2 2">
Все 1 сеть
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

##Подключения в контексте подсетей

[Подсети]../ae2-mechanics/subnetworks.md) воспользоваться сетевыми подключениями (и, в частности, ** НЕ** быть подключенным)
Ограничить то, что../ae2-mechanics/devices.md) иметь доступ к другим устройствам.

Вся подсеть на самом деле является отдельной сетью.

Возьмем, к примеру, Automatic Ore Fortuner.../example-setups/ore-fortuner.md). Здесь есть 3 отдельные сети.
Все они служат определенной цели в установке.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/ore_fortuner.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 2" max="3 1 3">
Сеть 1, действует как подсеть трубы, ограничивает доступ к импортной шине, поэтому она «хранит» рудоблоки через сеть.
Формирование самолетов.
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="0 0 0" max="3 1 1">
Сеть 2, действует как другая подсеть трубы, ограничивает то, к чему имеют доступ самолеты уничтожения.
Удачные куски руды в бочке, а не в вашей основной сети. Это также означает, что они не используют никаких каналов в сети.
Основная сеть.
  </BoxAnnotation>

  <BoxAnnotation color="#82CD5C" min="2 0 1" max="4 1 2">
Сеть 3, основная сеть со всем вашим хранилищем и крафтом на нем. Только для того, чтобы поставлять энергию, на самом деле и конкретно.
*не подключен к 2 подсетям.
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Связи в контексте P2P

Один из вариантов [Туннель P2P]../items-blocks-machines/p2p_tunnels.md) ходы [каналы]channels.md) вместо элементов или жидкостей
или сигнал красного камня, и это почему-то сбивает людей с толку. Сеть, на которой установлен туннель, не имеет никакого отношения к
Сеть туннель несет. Они могут быть одной и той же сетью, но они не должны быть и, как правило, нет.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_channels_network_connection.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1.98 2 1">
Сеть 1, сеть, которая переносится (обычно ваша основная сеть)
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="2.02 0 0" max="3.98 1 1">
Сеть 2, сеть, работающая в туннелях ME P2P (обычно *не* ваша основная сеть)
  </BoxAnnotation>

  <BoxAnnotation color="#915dcd" min="4.02 0 0" max="6 1 1">
Сеть 1, сеть, которая переносится (обычно ваша основная сеть)
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Менее интуитивные связи

В данном случае это всего лишь 1 сеть, так как<ItemLink id="pattern_provider" />Будучи полноблоковым устройством, действует как
кабель, а также<ItemLink id="inscriber" />Делает то же самое. Таким образом, сетевое соединение проходит через
Поставщик и автор.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_network_connection_1.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="4 2 2">
Все 1 сеть
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Чтобы предотвратить это (полезно для многих установок автопроизводства с участием [подсетей])../ae2-mechanics/subnetworks.md)),
Вы можете нажать на провайдера с помощью<ItemLink id="certus_quartz_wrench" />Чтобы сделать его направленным, в этом случае он
Не пропускайте каналы через одну сторону.

<Row gap="40">
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_network_connection_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1.98 2 2">
Сеть 1
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="2.02 0 0" max="4 2 2">
Сеть 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_directional_connection.snbt" />

  <BoxAnnotation color="#ee3333" min="1 .3 .3" max="1.3 .7 .7">
Посмотрите, как не подключается кабель
  </BoxAnnotation>

  <IsometricCamera yaw="255" pitch="30" />
</GameScene>
</Row>

Другие части, которые не обеспечивают направленные сетевые соединения, являются наиболее важными.../ae2-mechanics/cable-subparts.md)
[устройства]../ae2-mechanics/devices.mdкак<ItemLink id="import_bus" />Сес,<ItemLink id="storage_bus" />и
<ItemLink id="cable_interface" />s.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/subpart_no_connection.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>