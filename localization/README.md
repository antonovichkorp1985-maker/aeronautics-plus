# Русификация сборки

Здесь хранится исходное дерево resource pack, чтобы изменения переводов были
проверяемыми и не зависели от временных файлов Google Drive.

- `ru-pack/` — содержимое актуального Aeronautics Plus RU Pack.
- `../scripts/build_ru_pack.py` — валидация JSON и воспроизводимая сборка ZIP.
- `../scripts/audit_ru_localization.py` — сравнение `en_us`/`ru_ru` из всех JAR
  с учётом встроенных Jar-in-Jar и наложения resource pack.
- `../docs/localization-audit/` — полный результат последнего аудита.

## Сборка

```bash
python3 scripts/build_ru_pack.py \
  --source localization/ru-pack \
  --output AeronauticsPlus-RU-Pack-v0.12-test.zip
```

## Повторный аудит

JAR-файлы сторонних модов намеренно не добавляются в Git. Укажите локальную
папку экземпляра:

```bash
python3 scripts/audit_ru_localization.py \
  --mods /path/to/instance/mods \
  --resource-pack AeronauticsPlus-RU-Pack-v0.12-test.zip \
  --output docs/localization-audit
```

Аудит считает покрытие строго по ключам. Английские значения в `ru_ru` отдельно
попадают в `suspicious_values.csv`, поскольку бренды и аббревиатуры нельзя
автоматически объявлять ошибкой без ручной проверки.
