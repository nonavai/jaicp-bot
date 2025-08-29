theme: /

    init:
        script:
            if (!$global.bookings) {
                $global.bookings = []
            }
            if (!$global.bookingCounter) {
                $global.bookingCounter = 1000
            }
            
            // Функция нормализации телефона
            function normalizePhone(raw) {
                if (!raw) return raw
                
                var digits = raw.replace(/\D/g, '')
                
                if (digits.length === 10) return "+7" + digits
                if (digits.length === 11 && digits.charAt(0) === "8") return "+7" + digits.substring(1)
                if (digits.length === 11 && digits.charAt(0) === "7") return "+" + digits
                if (digits.length > 11) return "+" + digits
                
                return digits
            }
            
            // Функция нормализации имени
            function normalizeName(raw) {
                if (!raw) return raw
                
                var parts = raw.trim().split(/\s+/)
                for (var i = 0; i < parts.length; i++) {
                    var p = parts[i].toLowerCase()
                    parts[i] = p.charAt(0).toUpperCase() + p.substring(1)
                }
                
                return parts.join(" ")
            }
            
            // Функция нормализации марки автомобиля
            function normalizeCarBrand(raw) {
                if (!raw) return raw
                
                var brandMap = {
                    "шкода": "Skoda",
                    "лада": "Lada", 
                    "тойота": "Toyota",
                    "киа": "KIA",
                    "хендай": "Hyundai",
                    "фольксваген": "Volkswagen",
                    "бмв": "BMW",
                    "мерседес": "Mercedes",
                    "ауди": "Audi",
                    "форд": "Ford",
                    "шевроле": "Chevrolet",
                    "ниссан": "Nissan",
                    "мазда": "Mazda",
                    "хонда": "Honda",
                    "рено": "Renault",
                    "пежо": "Peugeot",
                    "ситроен": "Citroen",
                    "опель": "Opel",
                    "вольво": "Volvo",
                    "лексус": "Lexus",
                    "инфинити": "Infiniti",
                    "акура": "Acura",
                    "субару": "Subaru",
                    "мицубиси": "Mitsubishi",
                    "сузуки": "Suzuki"
                }
                
                var normalized = brandMap[raw.toLowerCase()]
                return normalized || raw.charAt(0).toUpperCase() + raw.substring(1).toLowerCase()
            }
            
            // Функция валидации данных заявки
            function validateBookingData(booking) {
                var errors = []
                
                if (!booking.name || booking.name.length < 2) {
                    errors.push("Некорректное имя")
                }
                
                if (!booking.phone || booking.phone.length < 10) {
                    errors.push("Некорректный номер телефона")
                }
                
                if (!booking.brand || booking.brand.length < 2) {
                    errors.push("Некорректная марка автомобиля")
                }
                
                return {
                    isValid: errors.length === 0,
                    errors: errors
                }
            }
            
            // Функция форматирования даты
            function formatDate(isoString) {
                var date = new Date(isoString)
                return date.toLocaleDateString("ru-RU") + " " + date.toLocaleTimeString("ru-RU", {hour: '2-digit', minute:'2-digit'})
            }

    state: greeting
        q: * привет *
        q: * здравствуйте *
        q: * добрый *
        q: * начать *
        q: * помощь *
        a: Здравствуйте! Добро пожаловать в автосервис АвтоПрофи! Я помогу записаться на техобслуживание.
        script:
            $session.bookingData = {}

    state: farewell  
        q: * пока *
        q: * спасибо *
        q: * свидания *
        a: Спасибо за обращение! До свидания!

    state: working_hours
        q: * часы *
        q: * время *
        q: * работаете *
        q: * расписание *
        a: Часы работы автосервиса АвтоПрофи: Понедельник-Пятница 8:00-20:00, Суббота 9:00-18:00. Воскресенье - выходной.

    state: price_inquiry
        q: * цены *
        q: * стоимость *
        q: * сколько *
        q: * тариф *
        a: Цены на техобслуживание: Компактные автомобили от 2500 руб, Среднеразмерные от 3500 руб, Полноразмерные от 4500 руб.

    state: service_booking
        q: * записаться *
        q: * запись *
        q: * ТО *
        q: * техобслуживание *
        q: * попасть *
        q: * записать *
        q: * диагностика *
        q: * ремонт *
        q: * сервис *
        q: * обслуживание *
        a: Хорошо! Давайте оформим запись на техобслуживание. Укажите ваше имя, телефон и марку автомобиля.
        script:
            $session.bookingData = {}
            $reactions.transition("ask_name")

    state: ask_name
        a: Как вас зовут?
        script:
            // Собираем имя из входящего сообщения
            var text = $request.query
            if (text && text.length > 2) {
                // Простая проверка на имя (буквы и пробелы)
                if (/^[А-Яа-яA-Za-z\s]+$/.test(text)) {
                    $session.bookingData.name = normalizeName(text)
                    $reactions.transition("ask_phone")
                    return
                }
            }
            $reactions.answer("Пожалуйста, введите ваше полное имя (например: Иван Петров)")

    state: ask_phone
        a: Укажите ваш номер телефона
        script:
            var text = $request.query
            if (text && text.length > 6) {
                // Проверяем есть ли цифры в тексте
                if (/\d/.test(text)) {
                    $session.bookingData.phone = normalizePhone(text)
                    $reactions.transition("ask_car_brand")
                    return
                }
            }
            $reactions.answer("Пожалуйста, введите номер телефона (например: 8-999-123-45-67)")

    state: ask_car_brand
        a: Какая марка вашего автомобиля?
        script:
            var text = $request.query
            if (text && text.length > 1) {
                $session.bookingData.brand = normalizeCarBrand(text)
                $reactions.transition("confirm_booking")
                return
            }
            $reactions.answer("Пожалуйста, введите марку автомобиля (например: Toyota)")

    state: confirm_booking
        a: Подтвердите данные для записи на техобслуживание:\n📝 Имя: {{ $session.bookingData.name }}\n📞 Телефон: {{ $session.bookingData.phone }}\n🚗 Автомобиль: {{ $session.bookingData.brand }}\n\nВсе верно? Напишите "да" для подтверждения или "нет" для изменения.

    state: process_confirmation
        q: * да *
        q: * конечно *
        q: * верно *
        q: * правильно *
        q: * ага *
        script:
            var bookingId = "BK" + (++$global.bookingCounter)
            
            var booking = {
                id: bookingId,
                userId: $session.userId || "anonymous",
                name: $session.bookingData.name,
                phone: $session.bookingData.phone,
                brand: $session.bookingData.brand,
                status: "New",
                createdAt: new Date().toISOString(),
                confirmedAt: new Date().toISOString()
            }
            
            var validation = validateBookingData(booking)
            if (!validation.isValid) {
                $reactions.answer("Ошибка в данных: " + validation.errors.join(", "))
                return
            }
            
            $global.bookings.push(booking)
            $session.lastBookingId = bookingId
            
            $reactions.answer("✅ Заявка успешно оформлена!\n📋 Номер заявки: " + bookingId + "\n⏰ Наш сотрудник свяжется с вами в течение 30 минут для уточнения времени.")

    state: process_negation
        q: * нет *
        q: * не *
        q: * неправильно *
        q: * ошибка *
        a: Хорошо, давайте уточним данные. Что нужно изменить?
        script:
            $session.bookingData = {}
            $reactions.transition("service_booking")

    state: view_bookings
        q: * заявки *
        q: * записи *
        q: * статус *
        q: * показать *
        script:
            var userId = $session.userId || "anonymous"
            var userBookings = []
            
            for (var i = 0; i < $global.bookings.length; i++) {
                if ($global.bookings[i].userId === userId) {
                    userBookings.push($global.bookings[i])
                }
            }
            
            if (userBookings.length === 0) {
                $reactions.answer("У вас пока нет активных заявок.")
            } else {
                var message = "📋 Ваши заявки:\n\n"
                for (var j = 0; j < userBookings.length; j++) {
                    var booking = userBookings[j]
                    message += "🆔 " + booking.id + "\n"
                    message += "🚗 " + booking.brand + "\n"
                    message += "📊 Статус: " + booking.status + "\n"
                    message += "📅 Создано: " + formatDate(booking.createdAt) + "\n\n"
                }
                $reactions.answer(message)
            }

    state: default
        event: noMatch
        a: Извините, не понял ваш запрос. Попробуйте:\n• "записаться" - для записи на ТО\n• "цены" - узнать стоимость\n• "часы" - время работы\n• "заявки" - посмотреть записи