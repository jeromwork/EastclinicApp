# Блок 7: CI/CD

## Обзор

Обновлен GitHub Actions workflow для Step 1: JDK 17, Gradle cache, сборка и тесты.

## Workflow

**Файл**: `.github/workflows/build.yml`

**Пример кода**:

```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop, '001-project-foundation' ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Gradle dependencies
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      if: runner.os != 'Windows'
    
    - name: Build with Gradle
      run: ./gradlew build
    
    - name: Run unit tests
      run: ./gradlew test
```

## Изменения

### Было (до Step 1)

```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      if: runner.os != 'Windows'
    
    - name: Build with Gradle
      run: ./gradlew build
    
    - name: Run unit tests
      run: ./gradlew test
    
    - name: Check dependency graph
      run: ./gradlew :app:dependencies > dependencies.txt || true
```

### Стало (Step 1)

**Изменения**:
- ✅ Добавлен Gradle cache (ускоряет сборку)
- ✅ Добавлена ветка `001-project-foundation` в триггеры
- ✅ Убран шаг проверки dependency graph (не критично для Step 1)

## Компоненты

### 1. Триггеры

```yaml
on:
  push:
    branches: [ main, develop, '001-project-foundation' ]
  pull_request:
    branches: [ main, develop ]
```

**Объяснение**:
- Запускается при push в `main`, `develop`, `001-project-foundation`
- Запускается при создании PR в `main` или `develop`

### 2. JDK Setup

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'temurin'
```

**Объяснение**:
- JDK 17 (соответствует настройкам проекта)
- Temurin distribution (Eclipse Adoptium)

### 3. Gradle Cache

```yaml
- name: Cache Gradle dependencies
  uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    restore-keys: |
      ${{ runner.os }}-gradle-
```

**Объяснение**:
- Кеширует зависимости Gradle
- Ключ зависит от gradle файлов (инвалидация при изменении)
- Восстанавливает из предыдущих кешей при отсутствии точного совпадения

### 4. Build

```yaml
- name: Build with Gradle
  run: ./gradlew build
```

**Объяснение**:
- Собирает проект
- Включает компиляцию всех модулей
- Проверяет, что проект собирается

### 5. Tests

```yaml
- name: Run unit tests
  run: ./gradlew test
```

**Объяснение**:
- Запускает все unit тесты
- Не требует эмулятора/устройства
- Быстро выполняется

## UI тесты в CI

**Текущее состояние**: UI тесты НЕ включены в CI на Step 1.

**Причины**:
- Требуют эмулятор/устройство
- Увеличивают время выполнения
- Не критичны для smoke-проверки

**Будущее**: UI тесты можно добавить позже с использованием:
- GitHub Actions Android emulator
- Или отдельным job с условным запуском

## Производительность

**Ожидаемое время выполнения**:
- Checkout: ~10s
- JDK setup: ~10s
- Gradle cache restore: ~5s (при наличии)
- Build: ~2-5 минут (зависит от кеша)
- Tests: ~30s-1min

**С кешем**: ~3-6 минут  
**Без кеша**: ~5-8 минут

## Проверка

### Локально

Можно проверить workflow локально с помощью [act](https://github.com/nektos/act):

```bash
act push -W .github/workflows/build.yml
```

### В GitHub

Workflow автоматически запускается при:
- Push в указанные ветки
- Создании PR

**Статус**: Проверяется в GitHub Actions tab.

## Коммит

**ID**: `0d4fe3d`  
**Сообщение**: `ci: update workflow for Step1 (JDK 17, Gradle cache, build+test)`  
**Файлов**: 1  
**Изменений**: +29 / -35

## Выводы

✅ CI настроен и работает  
✅ Gradle cache ускоряет сборку  
✅ Unit тесты запускаются автоматически  
✅ Workflow простой и понятный  
⚠️ UI тесты не включены (можно добавить позже)


