#!/usr/bin/env pwsh

$SDK_PATH = "$env:LOCALAPPDATA\Android\Sdk"
$EMULATOR = "$SDK_PATH\emulator\emulator.exe"
$AVD_NAME = "Medium_Phone_API_36.1"

Write-Host "Запуск эмулятора $AVD_NAME..."

# Остановка старых процессов
Get-Process -Name emulator*, qemu-system-x86_64* -ErrorAction SilentlyContinue | Stop-Process -Force

# Запуск без специфичных флагов размера, чтобы не конфликтовать
Start-Process -FilePath $EMULATOR -ArgumentList "-avd $AVD_NAME -gpu swiftshader_indirect -no-snapshot-load" -WindowStyle Normal

Write-Host "Эмулятор запущен. Ожидание окна для настройки размера и положения..."

# Логика управления окном через Win32 API
$code = @"
using System;
using System.Runtime.InteropServices;
public class Win32 {
    [DllImport("user32.dll")]
    public static extern bool MoveWindow(IntPtr hWnd, int X, int Y, int nWidth, int nHeight, bool bRepaint);
    
    [DllImport("user32.dll")]
    public static extern bool SetForegroundWindow(IntPtr hWnd);
}
"@
Add-Type -TypeDefinition $code

$timeout = 60
$adjusted = $false
while ($timeout -gt 0) {
    # Ищем процесс эмулятора
    $process = Get-Process -Name qemu-system-x86_64 -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -like "*Android Emulator*" }
    if ($process) {
        $hwnd = $process.MainWindowHandle
        if ($hwnd -ne [IntPtr]::Zero) {
            Write-Host "Окно найдено. Устанавливаю положение (100, 100) и размер (400, 850)..."
            
            # Принудительно задаем координаты и РАЗМЕР
            # 400x850 - это примерно соответствует Medium Phone на экране
            [Win32]::MoveWindow($hwnd, 100, 100, 400, 850, $true) | Out-Null
            [Win32]::SetForegroundWindow($hwnd) | Out-Null
            
            $adjusted = $true
            break
        }
    }
    Start-Sleep -Seconds 1
    $timeout--
}

if (-not $adjusted) {
    Write-Warning "Не удалось настроить окно за 60 секунд."
}

Write-Host "Ожидание полной загрузки системы (adb)..."
while ($true) {
    $devices = adb devices
    if ($devices -match "emulator-5554\s+device") {
        $booted = adb shell getprop sys.boot_completed
        if ($booted -match "1") {
            Write-Host "Система загружена!"
            break
        }
    }
    Start-Sleep -Seconds 5
}
