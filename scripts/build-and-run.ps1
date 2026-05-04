#!/usr/bin/env pwsh
param([switch]$Clean)

$PACKAGE_NAME = "com.eastclinic.app"
$ACTIVITY_NAME = ".MainActivity"
$APK_PATH = "app\build\outputs\apk\debug\app-debug.apk"

Write-Host "--- Начинаю цикл сборки и запуска ---"

# 1. Проверка эмулятора
$devices = adb devices
if ($devices -notmatch "emulator-5554\s+device") {
    Write-Host "Эмулятор не запущен. Запускаю..."
    & .\scripts\launch-emulator.ps1
} else {
    Write-Host "Эмулятор уже запущен."
}

# 2. Сборка
if ($Clean) {
    Write-Host "Очистка проекта..."
    .\gradlew clean
}

Write-Host "Компиляция APK (Fast Mode)..."
# Используем -x для пропуска тяжелых проверок и тестов
.\gradlew :app:assembleDebug -x lint -x test --configuration-cache

if ($LASTEXITCODE -ne 0) {
    Write-Error "Сборка провалилась!"
    exit 1
}

# 3. Установка
Write-Host "Полная очистка старой версии..."
adb uninstall $PACKAGE_NAME | Out-Null

Write-Host "Установка приложения..."
adb install -r -d $APK_PATH

if ($LASTEXITCODE -ne 0) {
    Write-Error "Ошибка установки!"
    exit 1
}

# 4. Запуск
Write-Host "Запуск $PACKAGE_NAME..."
adb shell am start -n "$PACKAGE_NAME/$PACKAGE_NAME$ACTIVITY_NAME"

Write-Host "--- Готово! ---"

# 5. Мониторинг логов (опционально)
Write-Host "Вывожу логи (Ctrl+C для остановки)..."
adb logcat -v time | Select-String "com.eastclinic.app"
