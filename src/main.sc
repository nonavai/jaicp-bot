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
        q: * здравствуй *
        q: * добрый день *
        q: * доброе утро *
        q: * добрый вечер *
        q: * доброго времени *
        q: * добро пожаловать *
        q: * приветствую *
        q: * хай *
        q: * hi *
        q: * hello *
        q: * начать *
        q: * старт *
        q: * помощь *
        q: * начинаем *
        q: * как дела *
        q: * что нового *
        q: * салют *
        a: Здравствуйте! Добро пожаловать в автосервис АвтоПрофи! Я помогу записаться на техобслуживание.
        script:
            $session.bookingData = {}

    state: farewell  
        q: * пока *
        q: * до свидания *
        q: * до встречи *
        q: * увидимся *
        q: * спасибо *
        q: * благодарю *
        q: * спасибо за помощь *
        q: * всего хорошего *
        q: * всего доброго *
        q: * bye *
        q: * пока пока *
        q: * чао *
        q: * адьос *
        q: * покеда *
        q: * прощай *
        q: * удачи *
        a: Спасибо за обращение! До свидания! Будем рады видеть вас в автосервисе АвтоПрофи!

    state: working_hours
        q: * часы *
        q: * время *
        q: * работаете *
        q: * расписание *
        q: * график *
        q: * режим работы *
        q: * какое время *
        q: * во сколько *
        q: * когда можно *
        q: * когда работаете *
        q: * в какое время *
        q: * открыты *
        q: * закрыты *
        q: * выходные *
        q: * рабочие дни *
        q: * рабочее время *
        q: * время приема *
        q: * режим *
        q: * когда открыто *
        q: * когда можно прийти *
        q: * работает ли *
        a: Часы работы автосервиса АвтоПрофи: Понедельник-Пятница 8:00-20:00, Суббота 9:00-18:00. Воскресенье - выходной. Запись возможна в любое рабочее время!

    state: price_inquiry
        q: * цены *
        q: * цена *
        q: * стоимость *
        q: * сколько стоит *
        q: * сколько *
        q: * тариф *
        q: * тарифы *
        q: * расценки *
        q: * прайс *
        q: * прайслист *
        q: * оплата *
        q: * плата *
        q: * денег *
        q: * рублей *
        q: * деньги *
        q: * какая цена *
        q: * какие цены *
        q: * во сколько обойдется *
        q: * сколько платить *
        q: * что стоит *
        q: * дорого *
        q: * дешево *
        q: * бюджет *
        a: Цены на техобслуживание: Компактные автомобили от 2500 руб, Среднеразмерные от 3500 руб, Полноразмерные от 4500 руб.

    state: service_booking
        q: * записаться *
        q: * запись *
        q: * записать *
        q: * записывать *
        q: * ТО *
        q: * техобслуживание *
        q: * техосмотр *
        q: * техническое обслуживание *
        q: * попасть *
        q: * диагностика *
        q: * ремонт *
        q: * сервис *
        q: * обслуживание *
        q: * как записаться *
        q: * хочу записаться *
        q: * хочу попасть *
        q: * мне нужно *
        q: * нужна запись *
        q: * хочу на ТО *
        q: * нужно на ТО *
        q: * надо на ТО *
        q: * пройти ТО *
        q: * сделать ТО *
        q: * провести ТО *
        q: * машину на ТО *
        q: * автомобиль на ТО *
        q: * время ТО *
        q: * подошло ТО *
        q: * нужен сервис *
        q: * требуется обслуживание *
        q: * проверить машину *
        q: * осмотр *
        q: * профилактика *
        q: * замена масла *
        q: * проверка *
        q: * ремонт автомобиля *
        q: * автосервис *
        q: * СТО *
        q: * станция технического обслуживания *
        q: * мастерская *
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
        q: * угу *
        q: * точно *
        q: * именно *
        q: * все правильно *
        q: * все верно *
        q: * все так *
        q: * подтверждаю *
        q: * согласен *
        q: * согласна *
        q: * ok *
        q: * окей *
        q: * хорошо *
        q: * отлично *
        q: * ок *
        q: * yes *
        q: * кайф *
        q: * давай *
        q: * го *
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
        q: * неверно *
        q: * ошибка *
        q: * не так *
        q: * не то *
        q: * неточно *
        q: * исправить *
        q: * изменить *
        q: * переделать *
        q: * заново *
        q: * сначала *
        q: * отмена *
        q: * отменить *
        q: * назад *
        q: * не согласен *
        q: * не согласна *
        q: * no *
        q: * wrong *
        a: Хорошо, давайте уточним данные. Что нужно изменить?
        script:
            $session.bookingData = {}
            $reactions.transition("service_booking")

    state: view_bookings
        q: * заявки *
        q: * заявка *
        q: * записи *
        q: * запись *
        q: * статус *
        q: * показать *
        q: * посмотреть *
        q: * мои заявки *
        q: * мои записи *
        q: * мой статус *
        q: * где моя заявка *
        q: * что с заявкой *
        q: * статус заявки *
        q: * номер заявки *
        q: * список заявок *
        q: * активные заявки *
        q: * текущие заявки *
        q: * история *
        q: * история заявок *
        q: * мои обращения *
        q: * проверить статус *
        q: * узнать статус *
        q: * отследить *
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

    state: help
        q: * помощь *
        q: * помоги *
        q: * что умеешь *
        q: * что можешь *
        q: * что делаешь *
        q: * команды *
        q: * меню *
        q: * функции *
        q: * возможности *
        q: * что ты *
        q: * как работать *
        q: * как пользоваться *
        q: * инструкция *
        q: * справка *
        q: * help *
        q: * как тебя использовать *
        q: * что говорить *
        q: * не понимаю *
        q: * объясни *
        q: * расскажи *
        a: Я умею помочь вам с автосервисом!\n\n📝 Для записи на ТО скажите: "записаться", "запись", "хочу на ТО", "нужно ТО"\n💰 Для цен скажите: "цены", "стоимость", "сколько стоит"\n⏰ Для времени работы: "часы", "время", "расписание", "когда работаете"\n📋 Для просмотра заявок: "заявки", "мои записи", "статус"\n🆘 Для помощи: "помощь", "что умеешь", "команды"

    state: location
        q: * адрес *
        q: * где находитесь *
        q: * как добраться *
        q: * местоположение *
        q: * координаты *
        q: * проезд *
        q: * как доехать *
        q: * где вы *
        q: * навигация *
        q: * карта *
        a: Автосервис АвтоПрофи находится по адресу: ул. Автомобильная, 15, г. Москва. Удобный подъезд, парковка на территории.

    state: contacts
        q: * контакты *
        q: * телефон *
        q: * связаться *
        q: * связь *
        q: * как связаться *
        q: * номер телефона *
        q: * контактная информация *
        q: * phone *
        q: * contact *
        a: Контакты автосервиса АвтоПрофи:\n📞 Телефон: +7 (495) 123-45-67\n📧 Email: info@avtoprofi.ru\n🌐 Сайт: www.avtoprofi.ru

    state: services
        q: * услуги *
        q: * что делаете *
        q: * виды работ *
        q: * какие услуги *
        q: * сервисы *
        q: * работы *
        q: * что можете *
        q: * список услуг *
        a: Услуги автосервиса АвтоПрофи:\n🔧 Техническое обслуживание\n🛠 Диагностика\n🔩 Ремонт двигателя\n⚙️ Замена масла и фильтров\n🚗 Кузовной ремонт\n🛞 Шиномонтаж

    state: default
        event: noMatch
        a: Извините, не понял ваш запрос. Попробуйте:\n• "записаться" - для записи на ТО\n• "цены" - узнать стоимость\n• "часы" - время работы\n• "заявки" - посмотреть записи\n• "помощь" - показать все команды\n• "услуги" - список наших работ\n• "адрес" - как нас найти\n• "контакты" - связаться с нами