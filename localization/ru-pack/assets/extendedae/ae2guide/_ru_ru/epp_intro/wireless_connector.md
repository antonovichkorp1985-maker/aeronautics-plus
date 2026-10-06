---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Беспроводной коннектор ME
    icon: extendedae:wireless_connect
categories:
- extended devices
item_ids:
- extendedae:wireless_connect
- extendedae:wireless_tool
---

# Беспроводной коннектор

<Row gap="20">
<BlockImage id="extendedae:wireless_connect" scale="6"></BlockImage>
<ItemImage id="extendedae:wireless_tool" scale="6"></ItemImage>
</Row>

ME Wireless Connector может связывать две сети<ItemLink id="ae2:quantum_link" />но имеет ограниченный диапазон и не может
Соединение через измерения. ME Wireless Connector поддерживает только соединения один к одному. Использовать<ItemLink id="extendedae:wireless_hub" />
Если вы хотите много-много связей.

### Связать беспроводные коннекторы

Используйте беспроводную настройку ME Набор, чтобы щелкнуть два беспроводных коннектора, которые вы хотите связать.

Нажмите + щелкните, чтобы очистить текущую настройку ME Wireless Setup Kit.

ME Wireless Connector меняет текстуру, когда ссылка успешно устанавливается.

Беспроводные разъемы ME Wireless Connectors

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_off.snbt"></ImportStructure>
</GameScene>

Связанные беспроводные коннекторы ME

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_on.snbt"></ImportStructure>
</GameScene>

## Цвет

Беспроводные соединители могут быть окрашены как кабели и могут подключаться только к кабелям / соединителям того же цвета.

Тебе нужен<ItemLink id="ae2:color_applicator" />Чтобы окрасить разъем.

Вы можете настроить беспроводные разъемы так:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

##Использование энергии

Беспроводные коннекторы потребляют больше энергии, чем дальше друг от друга. Поскольку кривая расстояния не является линейной, мощность
Стоимость может быть очень высокой, когда они находятся слишком далеко друг от друга.

Вы можете использовать<ItemLink id="ae2:energy_card" />Чтобы сэкономить электроэнергию, каждая карта снижает стоимость энергии на 10%.

