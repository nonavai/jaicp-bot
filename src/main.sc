theme: /

init:
  $session.bookingData = {}
  if (!$global.bookings) {
    $global.bookings = []
  }

function normalizePhone(rawPhone) {
    var digits = rawPhone.replace(/\D/g, '')
    if (digits.length === 10) {
        return '+7' + digits
    } else if (digits.length === 11 && digits.startsWith('8')) {
        return '+7' + digits.substring(1)
    } else if (digits.length === 11 && digits.startsWith('7')) {
        return '+' + digits
    } else if (digits.length === 11 && digits.startsWith('1')) {
        return '+' + digits
    }
    return '+' + digits
}

function validateName(name) {
    var words = name.trim().split(/\s+/)
    if (words.length < 2) {
        return false
    }
    for (var i = 0; i < words.length; i++) {
        if (!/^[А-Яа-яA-Za-z]/.test(words[i])) {
            return false
        }
    }
    return true
}

function validateCarBrand(brand) {
    if (!brand || brand.trim().length === 0) {
        return false
    }
    if (!/^[А-Яа-яA-Za-z0-9\s\-]+$/.test(brand)) {
        return false
    }
    return true
}

function preprocessInput(text) {
    if (text.length > 400) {
        return {
            isValid: false,
            error: "Слишком длинный запрос. Пожалуйста, сформулируйте короче."
        }
    }
    var normalized = text.replace(/\s+/g, ' ').trim()
    normalized = normalized.replace(/[^\w\s\-\(\)\+\.\,\!\?]/g, '')
    return {
        isValid: true,
        text: normalized
    }
}

function checkParametersSufficiency(bookingData) {
    var params = 0
    var missing = []

    if (bookingData.name && validateName(bookingData.name)) {
        params++
    } else {
        missing.push('имя')
    }

    if (bookingData.phone && bookingData.phone.length >= 10) {
        params++
    } else {
        missing.push('телефон')
    }

    if (bookingData.brand && validateCarBrand(bookingData.brand)) {
        params++
    } else {
        missing.push('марка автомобиля')
    }

    return {
        sufficient: params >= 2,
        count: params,
        missing: missing
    }
}

function sendBookingRequest(bookingData) {
    var bookingId = 'BK' + Date.now()
    var booking = {
        id: bookingId,
        userId: $session.userId || 'anonymous',
        name: bookingData.name,
        phone: normalizePhone(bookingData.phone),
        brand: bookingData.brand,
        status: 'new',
        createdAt: new Date().toISOString(),
        confirmedAt: null,
        processedAt: null
    }
    $global.bookings.push(booking)
    return {
        success: true,
        bookingId: bookingId,
        message: 'Заявка успешно сохранена в системе'
    }
}

function getUserBookings(userId) {
    if (!$global.bookings) {
        return []
    }
    return $global.bookings.filter(function(booking) {
        return booking.userId === userId
    })
}

function getAllBookings() {
    return $global.bookings || []
}

function updateBookingStatus(bookingId, newStatus) {
    if (!$global.bookings) {
        return null
    }
    for (var i = 0; i < $global.bookings.length; i++) {
        if ($global.bookings[i].id === bookingId) {
            $global.bookings[i].status = newStatus
            if (newStatus === 'confirmed') {
                $global.bookings[i].confirmedAt = new Date().toISOString()
            } else if (newStatus === 'in_progress') {
                $global.bookings[i].processedAt = new Date().toISOString()
            }
            return $global.bookings[i]
        }
    }
    return null
}

input: $text
script:
    var preprocessed = preprocessInput($text)
    if (!preprocessed.isValid) {
        $reactions.answer(preprocessed.error)
        return
    }
    $text = preprocessed.text
    if ($session.bookingData && Object.keys($session.bookingData).length > 0) {
        handleBookingFlow($text)
    } else {
        handleRegularRequest($text)
    }

function handleRegularRequest(text) {
    $reactions.answer("Здравствуйте! Чем могу помочь?")
}

function handleBookingFlow(text) {
    var currentData = $session.bookingData
    var extracted = extractParameters(text)
    if (extracted.name) currentData.name = extracted.name
    if (extracted.phone) currentData.phone = extracted.phone
    if (extracted.brand) currentData.brand = extracted.brand
    var check = checkParametersSufficiency(currentData)
    if (check.sufficient) {
        $reactions.transition("confirm_booking")
    } else {
        askMissingParameter(check.missing[0])
    }
}

function extractParameters(text) {
    var result = {}
    var nameMatch = text.match(/([А-Яа-яA-Za-z]+ [А-Яа-яA-Za-z]+( [А-Яа-яA-Za-z]+)?)/)
    if (nameMatch) {
        result.name = nameMatch[1]
    }
    var phoneMatch = text.match(/(\+?[78]?[\d\-\s\(\)]{10,}|[\d\-\s\(\)]{10,})/)
    if (phoneMatch) {
        result.phone = phoneMatch[1]
    }
    var brandMatch = text.match(/(Skoda|Lada|Toyota|KIA|Hyundai|Volkswagen|BMW|Mercedes|Audi|Ford|Chevrolet|Nissan|Mazda|Honda|Renault|Peugeot|Citroen|Opel|Volvo|Lexus|Infiniti|Acura|Subaru|Mitsubishi|Suzuki|Daihatsu|Fiat|Alfa Romeo|Seat)/i)
    if (brandMatch) {
        result.brand = brandMatch[1]
    }
    return result
}

function askMissingParameter(paramType) {
    switch(paramType) {
        case 'имя':
            $reactions.transition("ask_name")
            break
        case 'телефон':
            $reactions.transition("ask_phone")
            break
        case 'марка автомобиля':
            $reactions.transition("ask_car_brand")
            break
        default:
            $reactions.answer("Пожалуйста, уточните " + paramType)
    }
}

state: confirm_booking
    input: $text
    script:
        if (isConfirmation($text)) {
            var result = sendBookingRequest($session.bookingData)
            if (result.success) {
                $session.lastBookingId = result.bookingId
                $reactions.transition("booking_success")
            } else {
                $reactions.transition("error_handling")
            }
        } else if (isNegation($text)) {
            $session.bookingData = {}
            $reactions.answer("Хорошо, давайте начнем заново. Что вам нужно?")
        } else {
            $reactions.answer("Пожалуйста, подтвердите или опровергните данные. Все верно?")
        }

function isConfirmation(text) {
    var confirmWords = ['да', 'конечно', 'разумеется', 'точно', 'именно', 'правильно', 'верно', 'ага', 'угу', 'подтверждаю', 'согласен', 'согласна']
    return confirmWords.some(word => text.toLowerCase().includes(word))
}

function isNegation(text) {
    var negateWords = ['нет', 'неправильно', 'неверно', 'ошибка', 'не то', 'не так', 'изменить', 'исправить', 'заново']
    return negateWords.some(word => text.toLowerCase().includes(word))
}
