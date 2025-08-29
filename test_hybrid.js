// 🧪 Тестовый скрипт для гибридного автосервисного бота
// Запустите этот скрипт для проверки работы функций

console.log("🚗 Тестирование гибридного автосервисного бота");
console.log("=" .repeat(50));

// Имитация глобального хранилища JAICP
global.bookings = [];

// Имитация сессии
var session = {
    userId: 'test_user_123',
    bookingData: {}
};

// Функции из main.sc (упрощенные версии для тестирования)
function normalizePhone(rawPhone) {
    var digits = rawPhone.replace(/\D/g, '');
    if (digits.length === 10) {
        return '+7' + digits;
    } else if (digits.length === 11 && digits.startsWith('8')) {
        return '+7' + digits.substring(1);
    }
    return '+' + digits;
}

function validateName(name) {
    var words = name.trim().split(/\s+/);
    return words.length >= 2;
}

function sendBookingRequest(bookingData) {
    var bookingId = 'BK' + Date.now();

    var booking = {
        id: bookingId,
        userId: session.userId,
        name: bookingData.name,
        phone: normalizePhone(bookingData.phone),
        brand: bookingData.brand,
        status: 'new',
        createdAt: new Date().toISOString()
    };

    global.bookings.push(booking);

    return {
        success: true,
        bookingId: bookingId,
        message: 'Заявка успешно сохранена'
    };
}

function getUserBookings(userId) {
    return global.bookings.filter(function(booking) {
        return booking.userId === userId;
    });
}

// 🧪 ТЕСТЫ
console.log("🧪 Запуск тестов...\n");

// Тест 1: Нормализация телефона
console.log("📱 Тест 1: Нормализация телефона");
var phones = ['8-999-123-45-67', '+7(800)555-01-23', '79991234567'];
phones.forEach(function(phone) {
    var normalized = normalizePhone(phone);
    console.log(`  "${phone}" → "${normalized}"`);
});

// Тест 2: Валидация имени
console.log("\n👤 Тест 2: Валидация имени");
var names = ['Иван Иванов', 'Анна', 'Мария Петрова Сергеевна'];
names.forEach(function(name) {
    var valid = validateName(name);
    console.log(`  "${name}" → ${valid ? '✅ Валидно' : '❌ Невалидно'}`);
});

// Тест 3: Создание заявки
console.log("\n📝 Тест 3: Создание заявки");
var testBooking = {
    name: 'Иван Иванов',
    phone: '8-999-123-45-67',
    brand: 'Toyota Camry'
};

var result = sendBookingRequest(testBooking);
console.log(`  Результат: ${result.success ? '✅ Успешно' : '❌ Ошибка'}`);
console.log(`  ID заявки: ${result.bookingId}`);

// Тест 4: Получение заявок пользователя
console.log("\n📋 Тест 4: Получение заявок пользователя");
var userBookings = getUserBookings(session.userId);
console.log(`  Найдено заявок: ${userBookings.length}`);
userBookings.forEach(function(booking, index) {
    console.log(`  Заявка ${index + 1}:`);
    console.log(`    ID: ${booking.id}`);
    console.log(`    Имя: ${booking.name}`);
    console.log(`    Телефон: ${booking.phone}`);
    console.log(`    Автомобиль: ${booking.brand}`);
    console.log(`    Статус: ${booking.status}`);
    console.log(`    Дата: ${booking.createdAt}`);
});

// Тест 5: Проверка общего количества заявок
console.log("\n📊 Тест 5: Общая статистика");
console.log(`  Всего заявок в системе: ${global.bookings.length}`);
console.log(`  Заявок пользователя ${session.userId}: ${getUserBookings(session.userId).length}`);

console.log("\n" + "=".repeat(50));
console.log("🎉 Все тесты завершены!");
console.log("\n💡 Для запуска реального бота используйте:");
console.log("   dotnet run");
console.log("\n📖 Документацию смотрите в README_hybrid.md");
