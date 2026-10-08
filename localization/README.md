# Русификация сборки

Здесь хранится исходное дерево resource pack, чтобы изменения переводов были
проверяемыми и не зависели от временных файлов Google Drive.

- `ru-pack/` — содержимое актуального Aeronautics Plus RU Pack.
- `../scripts/build_ru_pack.py` — валидация JSON и воспроизводимая сборка ZIP.
- `../scripts/audit_ru_localization.py` — сравнение `en_us`/`ru_ru` из всех JAR
  с учётом встроенных Jar-in-Jar и наложения resource pack.
- `../scripts/audit_localization_values.py` — отдельная проверка смешанных и
  полностью латинских значений, ключей и placeholders по exact JAR.
- `../scripts/retranslate_chisel.py` — воспроизводимая сборка полного перевода
  Chisel Modern 1.4.1 из вручную выверенных терминов.
- `../docs/localization-audit/` — текущие точечные аудиты и CSV-очереди ручной
  проверки значений;
- `../docs/archive/localization-audit/` — старые полные отчёты и датированные снимки,
  которые больше нельзя использовать как текущую статистику.

## Сборка

```bash
python3 scripts/build_ru_pack.py \
  --source localization/ru-pack \
  --output AeronauticsPlus-RU-Pack-v0.14-test.zip
```

## Повторный аудит

JAR-файлы сторонних модов намеренно не добавляются в Git. Укажите локальную
папку экземпляра:

```bash
python3 scripts/audit_ru_localization.py \
  --mods /path/to/instance/mods \
  --resource-pack AeronauticsPlus-RU-Pack-v0.14-test.zip \
  --output docs/localization-audit
```

Основной аудит считает покрытие строго по ключам. Качество самих значений
проверяется отдельно и сохраняется как воспроизводимая CSV-очередь:

```bash
python3 scripts/audit_localization_values.py \
  --jar /path/to/chisel.jar \
  --jar /path/to/chipped.jar
```

Смешанная кириллица/латиница и полностью латинские значения не исправляются
автоматически: в очередь попадают также допустимые бренды, аббревиатуры, формулы
и единицы измерения. Решения ручной проверки хранятся отдельно в
`docs/localization-audit/LATIN_SCRIPT_REVIEW.json`, поэтому не теряются при
перегенерации CSV.
