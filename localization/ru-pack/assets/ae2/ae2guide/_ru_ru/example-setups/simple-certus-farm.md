---
navigation:
  parent: example-setups/example-setups-index.md
  title: Простая ферма Сертуса
  icon: certus_quartz_crystal
  position: 110
---

# Простая ферма Сертуса

Как говорится в [Certus Growth]../ae2-mechanics/certus-growth.md), автоматизация<ItemLink id="certus_quartz_crystal" />
Сбор урожая включает<ItemLink id="annihilation_plane" />s и<ItemLink id="storage_bus" />Сес.
<ItemLink id="growth_accelerator" />s используются для массового ускорения роста почек кварца, а затем и плоскостей.
Полностью разросшийся<ItemLink id="quartz_cluster" />. Они фильтруются, используя подозрительно удачную черту, которая незрела.
шейные почки капают<ItemLink id="certus_quartz_dust" />Вместо того, чтобы ничего не уронить.

Эта ферма работает автоматически.<ItemLink id="flawless_budding_quartz" />но с дефектами, сколами и повреждениями
Почковательный кварц вам придется заменить почковательный блок вручную. Или, как описано в [Semi-Auto Certus Farm]semiauto-certus-farm.md)
(Перенаправлено с Advanced Certus Farm)advanced-certus-farm.md), автоматически.

См. [Certus Growth]../ae2-mechanics/certus-growth.md) для расчетных скоростей.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/simple_certus_farm.snbt" />

  <BoxAnnotation color="#dddddd" min="3.7 1 1" max="4 2 2">
(1) Аннигиляционный самолет: нет графического интерфейса для настройки, но вы можете быть очарованы Fortune.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="3.3 2 2">
(2) Хранилище No1: Фильтровать до Certus Quartz Crystal.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 .7" max="2 2 1">
(3) Хранилище No2: Фильтровать до Certus Quartz Crystal. Приоритет установлен выше, чем у основного хранилища.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="1 0.5 0.5" color="#00ff00">
На главную сеть
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* Первый<ItemLink id="annihilation_plane" />(1) не имеет графического интерфейса и не может быть настроен, но может быть очарован удачей.
* Первый<ItemLink id="storage_bus" />2 фильтруется для<ItemLink id="certus_quartz_crystal" />.
* Второй<ItemLink id="storage_bus" />3 фильтруется для<ItemLink id="certus_quartz_crystal" />и имеет свою
[Приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) установить выше, чем основное хранилище.

## Как это работает

1. The<ItemLink id="annihilation_plane" />пытается сломать то, что перед ним, но может только сломать<ItemLink id="quartz_cluster" />
Единственным хранилищем в подсети является<ItemLink id="storage_bus" />Отфильтрованный на<ItemLink id="certus_quartz_crystal" />.
4. Первый<ItemLink id="storage_bus" />Хранит кристаллы кварца в бочке.
5. Второй<ItemLink id="storage_bus" />обеспечивает доступ к основной сети ко всем кристаллам кварца в стволе. Он установлен для
высокий [приоритет]../ae2-mechanics/import-export-storage.md#storage-priority) чтобы кристаллы кварца кертуса были предпочтительно
Положите обратно в бочку, а не в основное хранилище.
