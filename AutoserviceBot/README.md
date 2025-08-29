# AutoserviceBot - Backend для автосервисного чат-бота

Полноценное C# решение для обработки заявок от чат-бота JAICP, реализованное с соблюдением принципов Clean Architecture.

## 🏗️ Архитектура

Проект построен по принципам Clean Architecture с разделением на слои:

```
AutoserviceBot/
├── AutoserviceBot.API/           # Web API слой
├── AutoserviceBot.Application/    # Слой приложения (сервисы, DTOs)
├── AutoserviceBot.Domain/         # Доменный слой (сущности, value objects)
└── AutoserviceBot.Infrastructure/ # Инфраструктурный слой (реализации)
```

### Слои и их ответственность

- **Domain** - бизнес-логика, сущности, value objects
- **Application** - сервисы приложения, DTOs, интерфейсы
- **Infrastructure** - реализация сервисов, внешние зависимости
- **API** - HTTP контроллеры, конфигурация приложения

## 🚀 Основные возможности

- **Обработка заявок** - создание и управление заявками на техобслуживание
- **Нормализация данных** - приведение данных к стандартному формату
- **Валидация** - проверка корректности входящих данных
- **Логирование** - полное логирование всех операций
- **Обработка ошибок** - graceful handling ошибок

## 🔧 Технические особенности

### Value Objects
- **PhoneNumber** - валидация и нормализация номеров телефонов
- **FullName** - валидация и нормализация ФИО

### Сервисы
- **DataNormalizationService** - нормализация данных от чат-бота
- **BookingService** - управление заявками на техобслуживание

### API Endpoints
- `POST /api/booking` - создание заявки
- `GET /api/booking/{id}` - получение заявки
- `GET /api/booking/user/{userId}` - заявки пользователя
- `POST /api/booking/{id}/confirm` - подтверждение заявки
- `PUT /api/booking/{id}/status` - обновление статуса

## 📋 Требования

- .NET 8.0
- Visual Studio 2022 или JetBrains Rider
- Доступ к интернету для восстановления NuGet пакетов

## 🚀 Запуск проекта

### 1. Клонирование и восстановление
```bash
git clone <repository-url>
cd AutoserviceBot
dotnet restore
```

### 2. Сборка проекта
```bash
dotnet build
```

### 3. Запуск API
```bash
cd AutoserviceBot.API
dotnet run
```

API будет доступен по адресу: `https://localhost:7001` (или другому порту)

### 4. Swagger документация
После запуска API откройте: `https://localhost:7001/swagger`

## 🧪 Тестирование

### Тестирование API endpoints
```bash
# Создание заявки
curl -X POST "https://localhost:7001/api/booking" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Иван Иванов",
    "phone": "8-999-123-45-67",
    "carBrand": "Toyota Camry"
  }'

# Получение заявки
curl "https://localhost:7001/api/booking/{id}"
```

## 🔌 Интеграция с JAICP

### Webhook URL
Настройте в JAICP webhook на endpoint:
```
POST https://your-domain.com/api/booking
```

### Формат данных
```json
{
  "userId": "string",
  "name": "string",
  "phone": "string",
  "carBrand": "string",
  "notes": "string?",
  "timestamp": "ISO 8601?"
}
```

### Ответ API
```json
{
  "success": true,
  "bookingId": "BK1234567890",
  "message": "Заявка успешно создана",
  "errors": []
}
```

## 🛠️ Разработка

### Добавление новых сущностей
1. Создайте класс в `AutoserviceBot.Domain/Entities/`
2. Добавьте DTO в `AutoserviceBot.Application/DTOs/`
3. Создайте интерфейс сервиса в `AutoserviceBot.Application/Interfaces/`
4. Реализуйте сервис в `AutoserviceBot.Infrastructure/Services/`
5. Добавьте контроллер в `AutoserviceBot.API/Controllers/`

### Добавление новых value objects
1. Создайте класс в `AutoserviceBot.Domain/ValueObjects/`
2. Реализуйте интерфейс `IEquatable<T>`
3. Добавьте валидацию и нормализацию

### Расширение функциональности
1. Следуйте принципам SOLID
2. Используйте dependency injection
3. Добавляйте логирование для всех операций
4. Обрабатывайте ошибки gracefully

## 📚 Документация

- [.NET 8 Documentation](https://docs.microsoft.com/en-us/dotnet/)
- [ASP.NET Core Documentation](https://docs.microsoft.com/en-us/aspnet/core/)
- [Clean Architecture Principles](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## 🤝 Поддержка

При возникновении вопросов или проблем:
1. Проверьте логи приложения
2. Убедитесь в корректности конфигурации
3. Проверьте доступность всех зависимостей
4. Обратитесь к документации .NET

## 📄 Лицензия

Проект создан в рамках тестового задания для демонстрации возможностей .NET и Clean Architecture.
