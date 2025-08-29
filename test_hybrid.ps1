# 🧪 Тестовый скрипт для гибридного автосервисного бота
# Запустите этот скрипт для проверки работы функций

Write-Host "🚗 Тестирование гибридного автосервисного бота" -ForegroundColor Green
Write-Host ("=" * 50) -ForegroundColor Yellow

# Имитация глобального хранилища JAICP
$global:bookings = @()

# Имитация сессии
$session = @{
    userId = 'test_user_123'
    bookingData = @{}
}

# Функции из main.sc (упрощенные версии для тестирования)
function NormalizePhone($rawPhone) {
    $digits = $rawPhone -replace '\D', ''
    if ($digits.Length -eq 10) {
        return "+7$digits"
    } elseif ($digits.Length -eq 11 -and $digits.StartsWith('8')) {
        return "+7" + $digits.Substring(1)
    }
    return "+$digits"
}

function ValidateName($name) {
    $words = $name.Trim() -split '\s+'
    return $words.Length -ge 2
}

function SendBookingRequest($bookingData) {
    $bookingId = "BK" + [DateTimeOffset]::Now.ToUnixTimeMilliseconds()

    $booking = @{
        id = $bookingId
        userId = $session.userId
        name = $bookingData.name
        phone = NormalizePhone $bookingData.phone
        brand = $bookingData.brand
        status = 'new'
        createdAt = [DateTime]::UtcNow.ToString("o")
    }

    $global:bookings += $booking

    return @{
        success = $true
        bookingId = $bookingId
        message = 'Заявка успешно сохранена'
    }
}

function GetUserBookings($userId) {
    return $global:bookings | Where-Object { $_.userId -eq $userId }
}

# 🧪 ТЕСТЫ
Write-Host "`n🧪 Запуск тестов...`n" -ForegroundColor Cyan

# Тест 1: Нормализация телефона
Write-Host "📱 Тест 1: Нормализация телефона" -ForegroundColor Magenta
$phones = @('8-999-123-45-67', '+7(800)555-01-23', '79991234567')
foreach ($phone in $phones) {
    $normalized = NormalizePhone $phone
    Write-Host "  `"$phone`" → `"$normalized`"" -ForegroundColor White
}

# Тест 2: Валидация имени
Write-Host "`n👤 Тест 2: Валидация имени" -ForegroundColor Magenta
$names = @('Иван Иванов', 'Анна', 'Мария Петрова Сергеевна')
foreach ($name in $names) {
    $valid = ValidateName $name
    $status = if ($valid) { "✅ Валидно" } else { "❌ Невалидно" }
    Write-Host "  `"$name`" → $status" -ForegroundColor White
}

# Тест 3: Создание заявки
Write-Host "`n📝 Тест 3: Создание заявки" -ForegroundColor Magenta
$testBooking = @{
    name = 'Иван Иванов'
    phone = '8-999-123-45-67'
    brand = 'Toyota Camry'
}

$result = SendBookingRequest $testBooking
$status = if ($result.success) { "✅ Успешно" } else { "❌ Ошибка" }
Write-Host "  Результат: $status" -ForegroundColor White
Write-Host "  ID заявки: $($result.bookingId)" -ForegroundColor White

# Тест 4: Получение заявок пользователя
Write-Host "`n📋 Тест 4: Получение заявок пользователя" -ForegroundColor Magenta
$userBookings = GetUserBookings $session.userId
Write-Host "  Найдено заявок: $($userBookings.Count)" -ForegroundColor White
foreach ($booking in $userBookings) {
    Write-Host "  Заявка:" -ForegroundColor White
    Write-Host "    ID: $($booking.id)" -ForegroundColor Gray
    Write-Host "    Имя: $($booking.name)" -ForegroundColor Gray
    Write-Host "    Телефон: $($booking.phone)" -ForegroundColor Gray
    Write-Host "    Автомобиль: $($booking.brand)" -ForegroundColor Gray
    Write-Host "    Статус: $($booking.status)" -ForegroundColor Gray
    Write-Host "    Дата: $($booking.createdAt)" -ForegroundColor Gray
}

# Тест 5: Проверка общего количества заявок
Write-Host "`n📊 Тест 5: Общая статистика" -ForegroundColor Magenta
Write-Host "  Всего заявок в системе: $($global:bookings.Count)" -ForegroundColor White
Write-Host "  Заявок пользователя $($session.userId): $((GetUserBookings $session.userId).Count)" -ForegroundColor White

Write-Host "`n$("-" * 50)" -ForegroundColor Yellow
Write-Host "🎉 Все тесты завершены!" -ForegroundColor Green
Write-Host "`n💡 Для запуска реального бота используйте:" -ForegroundColor Cyan
Write-Host "   dotnet run" -ForegroundColor White
Write-Host "`n📖 Документацию смотрите в README_hybrid.md" -ForegroundColor Cyan
