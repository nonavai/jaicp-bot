// Основной Script файл для автосервисного чат-бота

// Инициализация сессии
init: $session.bookingData = {};

// Функция нормализации номера телефона
function normalizePhone(rawPhone) {
    // Убираем все символы кроме цифр
    var digits = rawPhone.replace(/\D/g, '');
    
    // Нормализуем российский номер
    if (digits.length === 10) {
        return '+7' + digits;
    } else if (digits.length === 11 && digits.startsWith('8')) {
        return '+7' + digits.substring(1);
    } else if (digits.length === 11 && digits.startsWith('7')) {
        return '+' + digits;
    } else if (digits.length === 11 && digits.startsWith('1')) {
        return '+' + digits;
    }
    
    return '+' + digits;
}

// Функция валидации имени
function validateName(name) {
    // Проверяем, что имя содержит минимум 2 слова
    var words = name.trim().split(/\s+/);
    if (words.length < 2) {
        return false;
    }
    
    // Проверяем, что каждое слово начинается с заглавной буквы
    for (var i = 0; i < words.length; i++) {
        if (!/^[А-Яа-яA-Za-z]/.test(words[i])) {
            return false;
        }
    }
    
    return true;
}

// Функция валидации марки автомобиля
function validateCarBrand(brand) {
    // Проверяем, что марка не пустая и содержит только буквы и цифры
    if (!brand || brand.trim().length === 0) {
        return false;
    }
    
    // Проверяем, что марка содержит только допустимые символы
    if (!/^[А-Яа-яA-Za-z0-9\s\-]+$/.test(brand)) {
        return false;
    }
    
    return true;
}

// Функция препроцессинга входящего текста
function preprocessInput(text) {
    // Проверяем длину
    if (text.length > 400) {
        return {
            isValid: false,
            error: "Слишком длинный запрос. Пожалуйста, сформулируйте короче."
        };
    }
    
    // Нормализуем пробелы
    var normalized = text.replace(/\s+/g, ' ').trim();
    
    // Убираем мусорные символы
    normalized = normalized.replace(/[^\w\s\-\(\)\+\.\,\!\?]/g, '');
    
    return {
        isValid: true,
        text: normalized
    };
}

// Функция проверки достаточности параметров
function checkParametersSufficiency(bookingData) {
    var params = 0;
    var missing = [];
    
    if (bookingData.name && validateName(bookingData.name)) {
        params++;
    } else {
        missing.push('имя');
    }
    
    if (bookingData.phone && bookingData.phone.length >= 10) {
        params++;
    } else {
        missing.push('телефон');
    }
    
    if (bookingData.brand && validateCarBrand(bookingData.brand)) {
        params++;
    } else {
        missing.push('марка автомобиля');
    }
    
    return {
        sufficient: params >= 2,
        count: params,
        missing: missing
    };
}

// Функция отправки заявки на backend
function sendBookingRequest(bookingData) {
    // Здесь будет HTTP-запрос к backend
    // Пока что просто возвращаем успех
    
    var requestData = {
        userId: $session.userId || 'anonymous',
        name: bookingData.name,
        phone: normalizePhone(bookingData.phone),
        brand: bookingData.brand,
        timestamp: new Date().toISOString()
    };
    
    // В реальном проекте здесь будет fetch или axios
    console.log('Отправка заявки:', requestData);
    
    return {
        success: true,
        bookingId: 'BK' + Date.now(),
        message: 'Заявка успешно отправлена'
    };
}

// Обработчик входящего сообщения
input: $text
script:
    // Препроцессинг входящего текста
    var preprocessed = preprocessInput($text);
    if (!preprocessed.isValid) {
        $reactions.answer(preprocessed.error);
        return;
    }
    
    // Обновляем текст для дальнейшей обработки
    $text = preprocessed.text;
    
    // Проверяем, есть ли активная сессия записи
    if ($session.bookingData && Object.keys($session.bookingData).length > 0) {
        // Если есть активная сессия, обрабатываем как часть процесса записи
        handleBookingFlow($text);
    } else {
        // Обычная обработка запроса
        handleRegularRequest($text);
    }

// Функция обработки обычного запроса
function handleRegularRequest(text) {
    // Здесь будет логика определения интента
    // Пока что просто отвечаем приветствием
    $reactions.answer("Здравствуйте! Чем могу помочь?");
}

// Функция обработки процесса записи
function handleBookingFlow(text) {
    var currentData = $session.bookingData;
    
    // Пытаемся извлечь параметры из текста
    var extracted = extractParameters(text);
    
    // Обновляем данные
    if (extracted.name) currentData.name = extracted.name;
    if (extracted.phone) currentData.phone = extracted.phone;
    if (extracted.brand) currentData.brand = extracted.brand;
    
    // Проверяем достаточность параметров
    var check = checkParametersSufficiency(currentData);
    
    if (check.sufficient) {
        // Переходим к подтверждению
        $reactions.transition("confirm_booking");
    } else {
        // Спрашиваем недостающий параметр
        askMissingParameter(check.missing[0]);
    }
}

// Функция извлечения параметров из текста
function extractParameters(text) {
    var result = {};
    
    // Извлекаем имя (простой паттерн)
    var nameMatch = text.match(/([А-Яа-яA-Za-z]+ [А-Яа-яA-Za-z]+( [А-Яа-яA-Za-z]+)?)/);
    if (nameMatch) {
        result.name = nameMatch[1];
    }
    
    // Извлекаем телефон
    var phoneMatch = text.match(/(\+?[78]?[\d\-\s\(\)]{10,}|[\d\-\s\(\)]{10,})/);
    if (phoneMatch) {
        result.phone = phoneMatch[1];
    }
    
    // Извлекаем марку автомобиля
    var brandMatch = text.match(/(Skoda|Lada|Toyota|KIA|Hyundai|Volkswagen|BMW|Mercedes|Audi|Ford|Chevrolet|Nissan|Mazda|Honda|Renault|Peugeot|Citroen|Opel|Volvo|Lexus|Infiniti|Acura|Subaru|Mitsubishi|Suzuki|Daihatsu|Fiat|Alfa Romeo|Seat)/i);
    if (brandMatch) {
        result.brand = brandMatch[1];
    }
    
    return result;
}

// Функция запроса недостающего параметра
function askMissingParameter(paramType) {
    switch(paramType) {
        case 'имя':
            $reactions.transition("ask_name");
            break;
        case 'телефон':
            $reactions.transition("ask_phone");
            break;
        case 'марка автомобиля':
            $reactions.transition("ask_car_brand");
            break;
        default:
            $reactions.answer("Пожалуйста, уточните " + paramType);
    }
}

// Обработчик подтверждения записи
state: confirm_booking
input: $text
script:
    // Проверяем, является ли это подтверждением
    if (isConfirmation($text)) {
        // Отправляем заявку
        var result = sendBookingRequest($session.bookingData);
        if (result.success) {
            $reactions.transition("booking_success");
        } else {
            $reactions.transition("error_handling");
        }
    } else if (isNegation($text)) {
        // Начинаем заново
        $session.bookingData = {};
        $reactions.answer("Хорошо, давайте начнем заново. Что вам нужно?");
    } else {
        // Просим уточнить
        $reactions.answer("Пожалуйста, подтвердите или опровергните данные. Все верно?");
    }

// Функция проверки подтверждения
function isConfirmation(text) {
    var confirmWords = ['да', 'конечно', 'разумеется', 'точно', 'именно', 'правильно', 'верно', 'ага', 'угу', 'подтверждаю', 'согласен', 'согласна'];
    return confirmWords.some(word => text.toLowerCase().includes(word));
}

// Функция проверки отрицания
function isNegation(text) {
    var negateWords = ['нет', 'неправильно', 'неверно', 'ошибка', 'не то', 'не так', 'изменить', 'исправить', 'заново'];
    return negateWords.some(word => text.toLowerCase().includes(word));
}
