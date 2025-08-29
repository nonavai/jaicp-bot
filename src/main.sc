require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start || sessionResult = "Сценарий начинается отсюда", sessionResultColor = "#143AD1"
        q!: $regex</start>
        image: https://248305.selcdn.ru/zfl_prod/64069/64072/Y6nDSc64tgJWac7N.png
        a: Добрый день! Я виртуальный секретарь Компании. Могу уточнить статус вашего заказа или рассказать о времени работы офиса. || htmlEnabled = true, html = "Добрый день! Я виртуальный секретарь <b>Компании</b>. Могу уточнить статус вашего заказа или рассказать о времени работы офиса."
        buttons:
            "Статус заказа" -> /Статус заказа
            "Часы работы" -> /Часы работы
            "Оставить отзыв" -> /Отзыв о работе
        intent: /Статус заказа || onlyThisState = false, toState = "/Статус заказа"
        intent: /Часы работы || onlyThisState = false, toState = "/Часы работы"
        intent: /Оставить отзыв || onlyThisState = false, toState = "/Отзыв о работе"
        intent: /sys/ru/aimylogic/parting || onlyThisState = false, toState = "/Bye"

    state: Bye
        a: Пока-пока!
        EndSession:
            actions = {}

    state: NoMatch || sessionResult = "Тут обрабатываем непонятные запросы", sessionResultColor = "#3E8080"
        event!: noMatch
        a: Простите, я вас не поняла. Не могли бы вы уточнить вопрос? || html = "Простите, я вас не поняла. Не могли бы вы уточнить вопрос?"
        go!: /Меню

    state: Статус заказа || sessionResult = "Статус заказа", sessionResultColor = "#7E47D1"
        a: Скажите, пожалуйста, какой номер у вашего заказа? || htmlEnabled = false, html = "Скажите, пожалуйста, какой номер у вашего заказа?"
        intent: /Номер заказа || onlyThisState = false, toState = "/Номер сохранен"
        event: noMatch || onlyThisState = false, toState = "/Неправильный номер"

    state: Часы работы || sessionResult = "Отвечаем про часы работы", sessionResultColor = "#15952F"
        a: Мы работаем с 10 утра до 8 вечера по будням и с 11 до 17 в субботу. Воскресенье — выходной. || htmlEnabled = false, html = "Мы работаем с 10 утра до 8 вечера по будням и с 11 до 17 в субботу. Воскресенье — выходной."
        go!: /Меню

    state: Отзыв о работе || sessionResult = "Пример работы с системными интентами", sessionResultColor = "#FFFFFF"
        a: Мы будем благодарны услышать ваш отзыв о работе. || htmlEnabled = false, html = "Мы будем благодарны услышать ваш отзыв о работе."
        intent: /sys/aimylogic/ru/approval || onlyThisState = false, toState = "/Отзыв о работе/Спасибо за оценку"
        intent: /sys/aimylogic/ru/insults || onlyThisState = false, toState = "/Отзыв о работе/Не хами"
        intent: /sys/aimylogic/ru/negative || onlyThisState = false, toState = "/Отзыв о работе/Извиниться"
        intent: /sys/aimylogic/ru/normal || onlyThisState = false, toState = "/Отзыв о работе/Будем стараться"
        event: noMatch || onlyThisState = false, toState = "/Отзыв о работе/Сохранить отзыв"

        state: Не хами
            a: Пожалуйста, сдерживайте ваши эмоции! || htmlEnabled = false, html = "Пожалуйста, сдерживайте ваши эмоции!"
            go!: /Меню

        state: Будем стараться
            a: Спасибо. В следующий раз мы постараемся быть лучше. || htmlEnabled = false, html = "Спасибо. В следующий раз мы постараемся быть лучше."
            go!: /Меню

        state: Извиниться
            a: Я сожалею, что мы доставили вам неудобства. От имени компании приношу вам свои извинения и обязательно передам вашу жалобу руководству. || htmlEnabled = false, html = "Я сожалею, что мы доставили вам неудобства. От имени компании приношу вам свои извинения и обязательно передам вашу жалобу руководству."
            go!: /Меню

        state: Спасибо за оценку
            a: Спасибо за высокую оценку! Мы рады стараться для вас! || htmlEnabled = false, html = "Спасибо за высокую оценку! Мы рады стараться для вас!"
            go!: /Меню

        state: Сохранить отзыв || sessionResult = "Тут мы поместили работу с отзывом в подсценарий", sessionResultColor = "#143AD1"
            a: Хорошо, я поняла. Ваш отзыв:
                
                {{$request.query}}
                
                Обязательно передам руководству! || htmlEnabled = true, html = "Хорошо, я поняла. Ваш отзыв: <br><br>{{$request.query}}  <br><br>Обязательно передам руководству!"
            go!: /Меню

    state: Меню
        a: Чем еще я могу вам помочь? || htmlEnabled = false, html = "Чем еще я могу вам помочь?"
        buttons:
            "Статус заказа" -> /Статус заказа
            "Часы работы" -> /Часы работы
            "Оставить отзыв" -> /Отзыв о работе
        intent: /Статус заказа || onlyThisState = false, toState = "/Статус заказа"
        intent: /Оставить отзыв || onlyThisState = false, toState = "/Отзыв о работе"
        intent: /Часы работы || onlyThisState = false, toState = "/Часы работы"
        intent: /sys/aimylogic/ru/parting || onlyThisState = false, toState = "/Bye"

    state: Номер сохранен || sessionResult = "Статус заказа", sessionResultColor = "#7E47D1"
        a: Хорошо, записала: {{$request.query}}. Уточняю статус. || htmlEnabled = false, html = "Хорошо, записала: {{$request.query}}. Уточняю статус."
        HttpRequest:
            url = https://httpbin.org/get?query={{$request.query}}
            method = GET
            dataType = 
            body = 
            okState = /Получили данные
            errorState = /Ошибка получения
            timeout = 0
            headers =
            vars = [{"name":"orderStatus","value":"$httpResponse.args.query"}]

    state: Ошибка получения || sessionResult = "Сервер недоступен", sessionResultColor = "#CD4C2B"
        a: К сожалению, я никак не могу связаться с сервером. Попробуйте позже, пожалуйста. || htmlEnabled = false, html = "К сожалению, я никак не могу связаться с сервером. Попробуйте позже, пожалуйста."
        go!: /Меню

    state: Неправильный номер || sessionResult = "Статус заказа", sessionResultColor = "#7E47D1"
        a: Это не похоже на номер заказа. || htmlEnabled = false, html = "Это не похоже на номер заказа."
        go!: /Статус заказа

    state: Получили данные || sessionResult = "Здесь надо будет научить бота обрабатывать ответ сервера", sessionResultColor = "#CD4C2B"
        a: Кажется, я не поняла, что мне ответили. Научите меня разбираться с этим, пожалуйста :) || htmlEnabled = false, html = "Кажется, я не поняла, что мне ответили. Научите меня разбираться с этим, пожалуйста :)"
        go!: /Меню
