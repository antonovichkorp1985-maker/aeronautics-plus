---
navigation:
  parent: example-setups/example-setups-index.md
  title: Ферма аметистов
  icon: minecraft:amethyst_shard
---

# Фермерство аметистов

Пока<ItemLink id="growth_accelerator" />работает над аметистом, обычными методами фильтрации [цертусовых почек]../items-blocks-machines/budding_certus.md)
с ан<ItemLink id="annihilation_plane" />Не работайте с аметистными почками. В отличие от незрелых почек церта, которые падают
<ItemLink id="certus_quartz_dust" />Незрелые почки аметиста ничего не опускают, поэтому плоскость аннигиляции всегда сломает их.
Сеть всегда может хранить «ничего».

Путь вокруг этого состоит в том, чтобы очаровать плоскость уничтожения шелковым прикосновением. Затем незрелые почки аметиста *do* бросают что-то
(различные стадии физических блоков бутонов) и, таким образом, могут быть отфильтрованы.

The<ItemLink id="minecraft:amethyst_cluster" />Затем он должен быть вновь помещен в<ItemLink id="formation_plane" />затем быть
Повторно сломленный одним<ItemLink id="annihilation_plane" />без шелкового прикосновения, чтобы получить<ItemLink id="minecraft:amethyst_shard" />s.

Обратите внимание, что из-за направленности кластера должна быть сплошная блоковая грань, прямо противоположная плоскости образования.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/amethyst_farm.snbt" />

  <BoxAnnotation color="#dddddd" min="2.7 1 1" max="3 2 2">
(1) План уничтожения #1: Нет графического интерфейса для настройки, но очарован Silk Touch.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 1 1" max="2.3 2 2">
(2) План формирования: отфильтрованный до кластера аметистов.
        <ItemImage id="minecraft:amethyst_cluster" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1.3 0.7 1" max="2 1 2">
(3) Самолет аннигиляции No2: нет графического интерфейса для настройки, но вы можете быть очарованы Fortune.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 1" max="1.3 1 2">
(4) Автобус No1: Отфильтрованный до Аметиста Шарда.
        <ItemImage id="minecraft:amethyst_shard" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 .7" max="1 1 1">
(5) Автобус No2: Отфильтрованный до Аметиста Шарда. Приоритет установлен выше, чем у основного хранилища.
        <ItemImage id="minecraft:amethyst_shard" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* Первый<ItemLink id="annihilation_plane" />(1) не имеет ГУИ и не может быть настроен, но должен быть очарован шелковым прикосновением.
* The<ItemLink id="formation_plane" />2 фильтруется для<ItemLink id="minecraft:amethyst_cluster" />.
* Второй<ItemLink id="annihilation_plane" />(3) не имеет графического интерфейса и не может быть настроен, но может быть очарован удачей.
* Первый<ItemLink id="storage_bus" />(4) фильтруется для<ItemLink id="minecraft:amethyst_shard" />.
* Второй<ItemLink id="storage_bus" />(5) фильтруется для<ItemLink id="minecraft:amethyst_shard" />и имеет свою
[Приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) установить выше, чем ваше основное хранилище.

## Как это работает

1. Первый<ItemLink id="annihilation_plane" />пытается сломать то, что перед ним, но может только сломать<ItemLink id="minecraft:amethyst_cluster" />
Единственным хранилищем в подсети является<ItemLink id="formation_plane" />, отфильтрованный до аметистного кластера. Это работает только потому, что
Самолет очарован шелковым прикосновением, иначе он смог бы сломать незрелые бутоны, потому что они ничего не сбрасывают.
2. The<ItemLink id="formation_plane" />Помещает кластер на блок, противостоящий ему.
3. Второй<ItemLink id="annihilation_plane" />Разрушает кластер, производя<ItemLink id="minecraft:amethyst_shard" />.
4. Первый<ItemLink id="storage_bus" />Хранит осколки в бочке. Это технически не нужно фильтровать, потому что
То, с чем должен столкнуться второй план аннигиляции, — это полностью разросшиеся кластеры.
5. Второй<ItemLink id="storage_bus" />обеспечивает доступ к основной сети ко всем осколкам аметиста в стволе. Он установлен для
высокий [приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) чтобы осколки аметиста были преимущественно
Положите обратно в бочку, а не в основное хранилище.
