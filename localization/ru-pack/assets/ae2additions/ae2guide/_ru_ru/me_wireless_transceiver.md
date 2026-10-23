---
navigation:
    title: Беспроводной приемопередатчик
    parent: aeadditions.md
item_ids:
  - ae2additions:me_wireless_transceiver
---
#Мне беспроводной приемопередатчик

### Что это может сделать?

Этот блок позволяет расширять сеть беспроводным способом на любое расстояние, но не по размерам. В общей сложности можно использовать 32 канала. Чем дальше вы размещаете приемопередатчики, тем выше их спрос на электроэнергию.

Что отличает это от квантового моста, так это то, что вы можете связать приемопередатчики с названным каналом. Как только приемопередатчик связан с каналом в качестве вещателя, вы можете подписаться на этот канал на любом приемопередатчике, который вам нужен, в том же измерении.

Если вы хотите передавать по измерениям, то следует использовать квантовый мост.

![](assets/wirelesstransceiver/transceiver_diagram.png)

# Базовая настройка

<GameScene zoom="4" interactive={true}>
    <ImportStructure src="assets/wirelesstransceiver/basic_setup.snbt" />
    <BoxAnnotation color="#dddddd" min="6 0 7" max="7 1 6">
Этот приемопередатчик настроен как вещатель:

![](assets/wirelesstransceiver/basic_setup_broadcaster.png)
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="10 0 7" max="11 1 6">
Этот приемопередатчик настроен как абонент:

![](assets/wirelesstransceiver/basic_setup_subscriber.png)
    </BoxAnnotation>
    <BoxAnnotation color="#dddddd" min="10.1 1.1 6.1" max="10.9 1.9 6">
Этот терминал теперь подключен к контроллеру, пока подается питание, а каналы max не заняты.
    </BoxAnnotation>
</GameScene>


#Рецепт
<RecipeFor id="me_wireless_transceiver"/>