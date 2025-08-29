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
        q: * (привет|здравствуйте|добрый день|доброе утро|добрый вечер|здравствуй) *
        q: * (начать|помощь|старт) *
        a: Здравствуйте! Добро пожаловать в автосервис АвтоПрофи! Я помогу записаться на техобслуживание.
        script:
            $session.bookingData = {}

    state: farewell  
        q: * (пока|до свидания|спасибо|увидимся) *
        a: Спасибо за обращение! До свидания!

    state: working_hours
        q: * (часы работы|время работы|когда работаете|расписание) *
        a: Часы работы автосервиса АвтоПрофи: Понедельник-Пятница 8:00-20:00, Суббота 9:00-18:00. Воскресенье - выходной.

    state: price_inquiry
        q: * (цены|стоимость|сколько стоит|тариф|расценки) *
        a: Цены на техобслуживание: Компактные автомобили от 2500 руб, Среднеразмерные от 3500 руб, Полноразмерные от 4500 руб.

    state: service_booking
        q: * (записаться|запись|хочу записаться|нужно ТО|техобслуживание|попасть на ТО|как записаться) *
        q: * (диагностика|ремонт|сервис|обслуживание) *
        a: Хорошо! Давайте оформим запись на техобслуживание. Укажите ваше имя, телефон и марку автомобиля.
        script:
            var name = $parseTree._name
            var phone = $parseTree._phone  
            var brand = $parseTree._brand
            
            if (name) {
                name = normalizeName(name)
                $session.bookingData.name = name
            }
            if (phone) {
                phone = normalizePhone(phone)
                $session.bookingData.phone = phone
            }
            if (brand) {
                brand = normalizeCarBrand(brand)
                $session.bookingData.brand = brand
            }
            
            var params = 0
            if ($session.bookingData.name) params++
            if ($session.bookingData.phone) params++
            if ($session.bookingData.brand) params++
            
            if (params >= 2) {
                $reactions.transition("confirm_booking")
            } else {
                if (!$session.bookingData.name) {
                    $reactions.transition("ask_name")
                } else if (!$session.bookingData.phone) {
                    $reactions.transition("ask_phone")
                } else if (!$session.bookingData.brand) {
                    $reactions.transition("ask_car_brand")
                }
            }

    state: ask_name
        q: * @FullName *
        a: Как вас зовут?
        script:
            if ($parseTree._name) {
                $session.bookingData.name = normalizeName($parseTree._name)
                if ($session.bookingData.phone && $session.bookingData.brand) {
                    $reactions.transition("confirm_booking")
                } else if (!$session.bookingData.phone) {
                    $reactions.transition("ask_phone")
                } else {
                    $reactions.transition("ask_car_brand")
                }
            }

    state: ask_phone
        q: * @Phone *
        a: Укажите ваш номер телефона
        script:
            if ($parseTree._phone) {
                $session.bookingData.phone = normalizePhone($parseTree._phone)
                if ($session.bookingData.name && $session.bookingData.brand) {
                    $reactions.transition("confirm_booking")
                } else if (!$session.bookingData.name) {
                    $reactions.transition("ask_name")
                } else {
                    $reactions.transition("ask_car_brand")
                }
            }

    state: ask_car_brand
        q: * @CarBrand *
        a: Какая марка вашего автомобиля?
        script:
            if ($parseTree._brand) {
                $session.bookingData.brand = normalizeCarBrand($parseTree._brand)
                if ($session.bookingData.name && $session.bookingData.phone) {
                    $reactions.transition("confirm_booking")
                } else if (!$session.bookingData.name) {
                    $reactions.transition("ask_name")
                } else {
                    $reactions.transition("ask_phone")
                }
            }

    state: confirm_booking
        a: Подтвердите данные для записи на техобслуживание:\n📝 Имя: {{ $session.bookingData.name }}\n📞 Телефон: {{ $session.bookingData.phone }}\n🚗 Автомобиль: {{ $session.bookingData.brand }}\n\nВсе верно? (да/нет)

    state: process_confirmation
        q: * (да|конечно|верно|правильно|ага|точно) *
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
        q: * (нет|не|неправильно|неверно|ошибка) *
        a: Хорошо, давайте уточним данные. Что нужно изменить?
        script:
            $session.bookingData = {}
            $reactions.transition("service_booking")

    state: view_bookings
        q: * (мои заявки|посмотреть заявки|статус заявки|мои записи|показать заявки) *
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
        a: Извините, не понял ваш запрос. Я могу:\n• 📝 Записать на техобслуживание\n• 📋 Показать ваши заявки\n• ⏰ Рассказать о часах работы\n• 💰 Сообщить цены