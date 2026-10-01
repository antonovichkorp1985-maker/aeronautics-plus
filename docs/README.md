# Актуальная документация Aeronautics Plus

Это индекс действующих документов. Исторические снимки и заменённые спецификации
вынесены в [`archive/`](archive/README.md), чтобы их нельзя было случайно принять за
текущие требования.

## Архитектура и модели

- [`architecture/MICRO_ASSEMBLED_ROTOR.md`](architecture/MICRO_ASSEMBLED_ROTOR.md) —
  действующая архитектура собираемого соосного ротора из микродеталей.
- [`modeling/HELICOPTER_ROTOR_BLENDER_SPEC.md`](modeling/HELICOPTER_ROTOR_BLENDER_SPEC.md) —
  действующий контракт геометрии, осей, объектов и ассетов ротора.

## Научная база

Начальная точка:
[`references/INTEGRATED_SCIENCE_CURRICULUM_RU.md`](references/INTEGRATED_SCIENCE_CURRICULUM_RU.md).
Она задаёт последовательность и prerequisites, а следующие файлы остаются активными
подробными reference volumes:

- [`references/MATHEMATICS_STUDY_GUIDE_RU.md`](references/MATHEMATICS_STUDY_GUIDE_RU.md);
- [`references/REALISM_STUDY_GUIDE_RU.md`](references/REALISM_STUDY_GUIDE_RU.md);
- [`references/CHEMISTRY_MOD_STUDY_GUIDE_RU.md`](references/CHEMISTRY_MOD_STUDY_GUIDE_RU.md);
- [`references/MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md`](references/MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md);
- [`references/INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md`](references/INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md);
- [`references/EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md`](references/EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md);
- [`references/SCIENCE_COVERAGE_MAP_RU.md`](references/SCIENCE_COVERAGE_MAP_RU.md) —
  аудит полноты и рисков, а не отдельный учебник.

## Текущие аудиты русификации

- [`localization-audit/CREATE_6_0_10.md`](localization-audit/CREATE_6_0_10.md) — точная
  проверка установленной версии Create;
- [`localization-audit/CREATE_ADDON_PONDER.md`](localization-audit/CREATE_ADDON_PONDER.md) —
  последний аудит Ponder у Create-аддонов;
- [`localization-audit/LATIN_SCRIPT_AUDIT.md`](localization-audit/LATIN_SCRIPT_AUDIT.md) —
  текущая очередь редакторской проверки латиницы и смешанных строк.

Связанные CSV/JSON в `localization-audit/` являются входами и результатами текущих
проверок. Старый полный аудит v0.10 и датированные отчёты находятся только в архиве.
