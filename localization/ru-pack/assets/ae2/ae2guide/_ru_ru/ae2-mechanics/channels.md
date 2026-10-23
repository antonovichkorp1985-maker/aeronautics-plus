---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: каналы
  icon: controller
---

#каналы

Прикладная энергетика 2 [ME сети]me-network-connections.md) требовать
Каналы для поддержки [устройств]../ae2-mechanics/devices.md) которые используют сетевое хранилище или другую сеть
услуг. Подумайте о каналах, таких как USB-кабель, для всех ваших устройств. Компьютер имеет много USB-портов и может поддерживать только
Столько устройств подключено к нему. Большинство машин, блок-устройств и стандартных кабелей могут проходить только через
До 8 каналов. Вы можете думать о полноблоковых устройствах и стандартных кабелях как о связке из 8 «канальных проводов». Однако [плотные кабели]../items-blocks-machines/cables.md#dense-cable) может поддерживать
32 канала. Единственные устройства, способные передавать 32<ItemLink id="me_p2p_tunnel" />
Квантовый сетевой мост (Quantum Network Bridge)../items-blocks-machines/quantum_bridge.md). Каждый раз, когда устройство использует канал, представьте, что вы вытаскиваете «проводку» из веб-сайта.
Пакет, что, очевидно, означает, что «провод» недоступен дальше по линии.

<GameScene zoom="7" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_demonstration_1.snbt" />

  <LineAnnotation color="#33ff33" from="1 .4 .7" to="2.4 .4 .7" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .7" to="2.4 .6 .7" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .6" to="2.6 .4 .6" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .6" to="2.6 .6 .6" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .6" to="2.6 .6 .6" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.4 .6 .7" to="2.4 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.4 .4 .7" to="2.4 .4 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .6 .6" to="2.6 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .4 .6" to="2.6 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.1 .6 1.5" to="2.4 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .4 1.5" to="2.9 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.6 .6 1.5" to="2.6 .9 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.4 .1 1.5" to="2.4 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1 .6 .4" to="3.5 .6 .4" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .4" to="3.5 .4 .4" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="3.5 .6 .4" to="3.5 .9 .4" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="3.5 .1 .4" to="3.5 .4 .4" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1 .6 .3" to="1.5 .6 .3" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .3" to="1.5 .4 .3" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1.5 .6 .3" to="1.5 .9 .3" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1.5 .1 .3" to="1.5 .4 .3" alwaysOnTop={true}/>

  <LineAnnotation color="#ff3333" from="3.5 .5 .5" to="5.5 .5 .5" alwaysOnTop={true}>
Все 8 каналов в кабеле были использованы, поэтому накопитель не получает ни одного.
  </LineAnnotation>

  <LineAnnotation color="#993333" from="1 .5 .5" to="1.25 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="1.5 .5 .5" to="1.75 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="2 .5 .5" to="2.25 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="2.5 .5 .5" to="2.75 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="3 .5 .5" to="3.25 .5 .5" alwaysOnTop={true}/>

  <DiamondAnnotation pos="3.6 0.5 0.5" color="#ff0000">
Все 8 каналов в кабеле были использованы, поэтому накопитель не получает ни одного.
    </DiamondAnnotation>

  <IsometricCamera yaw="15" pitch="30" />
</GameScene>

Простой способ увидеть, как каналы используются и маршрутизируются через вашу сеть, — использовать умные кабели.../items-blocks-machines/cables.md), которые будут отображать на них пути и использование каналов.

Каналы будут потреблять 1⁄128 аэ/т на узел, который они пересекают.
Добавление<ItemLink id="controller" />для
Сеть с 8 устройствами и более 96 узлами, которые могут использовать вашу энергию
Снижает энергопотребление, так как изменяется распределение каналов.

Заметим, ** Каналы не имеют ничего общего с цветом кабеля**, весь цвет кабеля делает кабели не соединяются.

## Маршрут канала

При использовании a<ItemLink id="controller" />,
Каналы проходят по 3 шагам. Сначала они проходят кратчайший путь через соседние машины к ближайшему обычному кабелю.../items-blocks-machines/cables.md)
(стекло, крытый или умный). Затем они проходят кратчайший путь через этот обычный кабель к ближайшему [плотному кабелю].../items-blocks-machines/cables.md)
(плотный или плотный умный). Затем они проходят кратчайший путь по этому плотному кабелю.<ItemLink id="controller" />.
Если же кратчайший путь уже исчерпан, то некоторыеdevices.md) может не иметь необходимых каналов, использовать
Цветные кабели, кабельные якоря и туннели в ваших интересах, чтобы убедиться, что ваши каналы идут по пути, который вы хотите.

Например, в этом случае некоторые диски не получают каналы, потому что, хотя в кабелях достаточно емкости, некоторые диски не имеют каналов.
Каналы пытаются пройти кратчайший путь, перегружая одни кабели, оставляя другие пустыми.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_path_length_issue.snbt" />

  <LineAnnotation color="#33ff33" from="3 .5 1.4" to="0.4 0.5 1.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 .5 1.4" to="0.4 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 0.5 3.6" to="1.4 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.4 0.5 3.6" to="1.4 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="3 0.5 3.6" to="1.6 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.6 0.5 3.6" to="1.6 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#ff3333" from="3 .5 1.6" to="0.6 .5 1.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="0.6 .5 1.6" to="0.6 .5 3.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="0.6 .5 3.4" to="1.4 .5 3.4" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#ff3333" from="3 .5 3.4" to="1.6 .5 3.4" alwaysOnTop={true} thickness="0.05"/>

  <BoxAnnotation color="#dddddd" min="1.2 0.2 3.2" max="1.8 0.8 3.8" alwaysOnTop={true} thickness="0.05">
Более 8 каналов пытаются пройти здесь, поэтому некоторые из них отключены.
  </BoxAnnotation>

  <IsometricCamera yaw="90" pitch="90" />

</GameScene>

Это может быть исправлено путем более тщательного ограничения каналов. Сети должны быть древовидными (или кустарниковыми).
Петли и двусмысленные каналы должны быть сведены к минимуму.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_path_length_issue_fix.snbt" />

  <LineAnnotation color="#33ff33" from="3 .5 1.4" to="0.4 0.5 1.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 .5 1.4" to="0.4 0.5 5.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 0.5 5.6" to="1 0.5 5.6" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="3 0.5 3.6" to="1.6 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.6 0.5 3.6" to="1.6 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <IsometricCamera yaw="90" pitch="90" />

</GameScene>

## Ad-Hoc сети

Сеть без a<ItemLink id="controller" />
Ad-Hoc может поддерживать до 8 каналов с помощью устройств.
После того, как вы превысите 8 устройств, канал сети с помощью устройств отключится.
Вы можете удалить устройства или добавить<ItemLink id="controller" />.

В отличие от управляемых сетей, [умные кабели]../items-blocks-machines/cables.md) в специальных сетях будет указываться номер
Каналы, используемые в сети, вместо количества каналов, проходящих через этот конкретный кабель.

При использовании специальных сетей каждое устройство будет
использовать сеть 1 канала, это сильно отличается от того, как<ItemLink id="controller" />Распределение каналов на основе
Самый короткий маршрут.

## Дизайн

Как упоминалось ранее в [маршрутизации канала]channels.md#channel-routing), лучше всего проектировать свою сеть в древовидной структуре, с плотными кабелями, выходящими из контроллера, обычными кабелями
Ветвление из плотного и [устройства]../ae2-mechanics/devices.md) в кластерах 8 или менее на обычных кабелях.

Вот пример того, чего не следует делать:

Следуя траекториям канала,

1. Немедленно выйдя из контроллера вправо, мы попадаем в 8 каналов, потому что привод действует как обычный кабель.
Однако, поскольку мы не используем интеллектуальный кабель, мы не можем видеть, сколько каналов используется. Осталось 8 каналов.
2. Водитель берет канал.
Осталось 7 каналов.
3,2 канала идут к терминалам.
Осталось 5 каналов.
4. Продолжая вправо, интерфейс берет другой канал.
Осталось 4 канала.
5.1 канал переходит к провайдеру шаблонов.
Осталось 3 канала.
6. Продолжая направо, 1 канал поднимается до импортного автобуса.
Осталось 2 канала.
7. Кластер провайдеров шаблонов, питающих сборщиков, получает только 2 канала, поэтому 2 провайдера не получают каналы.

В конечном счете, ошибка заключается в том, что мы не думаем о том, как будут распределены каналы.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/bad_network_structure.snbt" />

<LineAnnotation color="#33ff33" from="6.5 .5 1.5" to="6 .5 1.5" alwaysOnTop={true} thickness="0.4">
32 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="6 .5 1.5" to="5.5 .5 1.5" alwaysOnTop={true} thickness="0.2">
8 каналов
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="5.5 1.5 1.5" alwaysOnTop={true} thickness="0.1">
2 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="5.5 .3 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 1.5 1.5" to="5.5 2.5 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 2.5 1.5" to="5.5 2.5 1.1" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="4.5 .5 1.5" alwaysOnTop={true} thickness="0.158">
5 каналов
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="4.5 .3 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="4.5 1.5 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="3.5 .5 1.5" alwaysOnTop={true} thickness="0.122">
3 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 .5 1.5" to="3.5 2.5 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 2.5 1.5" to="3.7 2.5 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 .5 1.5" to="1.5 .5 1.5" alwaysOnTop={true} thickness="0.1">
2 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="1.5 0.5 1.5" to="1.5 0.3 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="1.5 0.5 1.5" to="0.5 0.5 1.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="0.5 0.5 1.5" to="0.5 0.5 0.5" alwaysOnTop={true} thickness="0.071">
1 канал
</LineAnnotation>

<LineAnnotation color="#ff3333" from="0.5 1.5 1.5" to="0.5 1.3 1.5" alwaysOnTop={true} thickness="0.071">
Нет каналов
</LineAnnotation>

<LineAnnotation color="#ff3333" from="1.5 1.5 0.5" to="1.5 1.3 0.5" alwaysOnTop={true} thickness="0.071">
Нет каналов
</LineAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

---

Here is an example of a good structure:

<GameScene zoom="2.5" interactive={true}>
  <ImportStructure src="../assets/assemblies/treelike_network_structure.snbt" />

    <BoxAnnotation color="#dddddd" min="6.9 0 4.9" max="9.1 4 7.1" thickness="0.05">
        Notice that the pattern providers are in separate groups of 8.
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="5 4 4" max="8 5 5" thickness="0.05">
        Two regular cables full of channels coming together mean you need a dense cable.
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="5 0 13" max="8 1 14" thickness="0.05">
        Different cable colors are used to prevent adjacent cables from connecting.
    </BoxAnnotation>


  <IsometricCamera yaw="315" pitch="30" />
</GameScene>

## Channel Modes

AE2 10.0.0 for Minecraft 1.18 introduces new options to change how AE2 channels behave in your world.
There's a new configuration option in the general section (`channels`) which controls this option, and a new in-game
command for operators to change the mode and the config from inside the game. The command is `/ae2 channelmode <mode>`
to change it and `/ae2 channelmode` to show the current mode. When the mode is changed in-game, all existing grids will
reboot and use the new mode immediately.

This resurrects and improves upon the option that was available in Minecraft 1.12 and introduces better options for
players that just want a little more laid back gameplay but don't want the mechanic to be removed entirely.

The following table lists the available modes in both the configuration file and command.

| Setting    | Description                                                                                                                                                                                                                               |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `default`  | The standard mode with the channel capacities of cable and ad-hoc networks as described throughout this website                                                                                                                           |
| `x2`       | All channel capacities are doubled (16 on normal cable, 64 on dense cable, ad-hoc networks support 16 channels)                                                                                                                           |
| `x3`       | All channel capacities are tripled (24 on normal cable, 92 on dense cable, ad-hoc networks support 24 channels)                                                                                                                           |
| `x4`       | All channel capacities are quadrupled (32 on normal cable, 128 on dense cable, ad-hoc networks support 32 channels)                                                                                                                       |
| `infinite` | All channel restrictions are removed. Controllers still reduce the power consumption of grids *significantly*. Smart cables will only toggle between completely off (no channels carried) and completely on (1 or more channels carried). |