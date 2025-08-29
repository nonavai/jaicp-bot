theme: /

state: greeting
    q: q:greeting
    a: Здравствуйте! Добро пожаловать в автосервис АвтоПрофи!
    script:
        $session.bookingData = {}
        if (!$global.bookings) {
            $global.bookings = []
        }

state: farewell
    q: q:farewell
    a: Спасибо за обращение! До свидания!

state: working_hours
    q: q:working_hours
    a: Часы работы: Понедельник-Пятница 8:00-20:00

state: price_inquiry
    q: q:price_inquiry
    a: Цены: Компактные автомобили от 2500руб

state: service_booking
    q: q:service_booking
    script:
        var params = 0
        if ($parseTree._name) params++
        if ($parseTree._phone) params++
        if ($parseTree._brand) params++

        if (params >= 2) {
            $session.bookingData = {
                name: $parseTree._name || $session.bookingData.name,
                phone: $parseTree._phone || $session.bookingData.phone,
                brand: $parseTree._brand || $session.bookingData.brand
            }
            $reactions.transition("confirm_booking")
        } else {
            if ($parseTree._name) $session.bookingData.name = $parseTree._name
            if ($parseTree._phone) $session.bookingData.phone = $parseTree._phone
            if ($parseTree._brand) $session.bookingData.brand = $parseTree._brand

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
    script:
        $session.bookingData.name = $parseTree._name
        if (!$session.bookingData.phone) {
            $reactions.transition("ask_phone")
        } else if (!$session.bookingData.brand) {
            $reactions.transition("ask_car_brand")
        } else {
            $reactions.transition("confirm_booking")
        }

state: ask_phone
    q: * @Phone *
    script:
        $session.bookingData.phone = $parseTree._phone
        if (!$session.bookingData.name) {
            $reactions.transition("ask_name")
        } else if (!$session.bookingData.brand) {
            $reactions.transition("ask_car_brand")
        } else {
            $reactions.transition("confirm_booking")
        }

state: ask_car_brand
    q: * @CarBrand *
    script:
        $session.bookingData.brand = $parseTree._brand
        if (!$session.bookingData.name) {
            $reactions.transition("ask_name")
        } else if (!$session.bookingData.phone) {
            $reactions.transition("ask_phone")
        } else {
            $reactions.transition("confirm_booking")
        }

state: confirm_booking
    a: Подтвердите данные для записи: Имя {{ $session.bookingData.name }}, Телефон {{ $session.bookingData.phone }}, Автомобиль {{ $session.bookingData.brand }}. Все верно?

state: process_confirmation
    q: * @Confirmation *
    script:
        $reactions.transition("send_booking_request")

state: send_booking_request
    script:
        var bookingId = 'BK' + Date.now()
        var booking = {
            id: bookingId,
            userId: $session.userId || 'anonymous',
            name: $session.bookingData.name,
            phone: $session.bookingData.phone,
            brand: $session.bookingData.brand,
            status: 'new',
            createdAt: new Date().toISOString(),
            confirmedAt: null,
            processedAt: null
        }
        $global.bookings.push(booking)
        $session.lastBookingId = bookingId
        $reactions.transition("booking_success")

state: booking_success
    a: Заявка успешно оформлена! Номер заявки {{ $session.lastBookingId }}

state: error_handling
    a: Извините, произошла ошибка. Попробуйте еще раз.

state: view_bookings
    q: * (мои заявки|посмотреть заявки|статус заявки|мои записи) *
    script:
        if (!$global.bookings) {
            $global.bookings = []
        }
        var userBookings = []
        for (var i = 0; i < $global.bookings.length; i++) {
            if ($global.bookings[i].userId === ($session.userId || 'anonymous')) {
                userBookings.push($global.bookings[i])
            }
        }
        if (userBookings.length === 0) {
            $reactions.answer("У вас пока нет активных заявок.")
        } else {
            var message = "Ваши заявки: "
            for (var j = 0; j < userBookings.length; j++) {
                var booking = userBookings[j]
                message += "ID: " + booking.id + ", " + booking.brand + ", " + booking.status + ". "
            }
            $reactions.answer(message)
        }

state: default
    a: Извините, не понял. Могу записать на ТО или показать заявки.
