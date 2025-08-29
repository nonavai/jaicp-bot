using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace AutoserviceBot.Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BookingController : ControllerBase
    {
        private readonly ILogger<BookingController> _logger;
        private readonly IBookingService _bookingService;

        public BookingController(ILogger<BookingController> logger, IBookingService bookingService)
        {
            _logger = logger;
            _bookingService = bookingService;
        }

        /// <summary>
        /// Обработка заявки на техобслуживание от чат-бота
        /// </summary>
        [HttpPost]
        public async Task<IActionResult> CreateBooking([FromBody] BookingRequest request)
        {
            try
            {
                _logger.LogInformation("Получена заявка от чат-бота: {Request}", request);

                // Валидация входящих данных
                var validationResult = ValidateBookingRequest(request);
                if (!validationResult.IsValid)
                {
                    return BadRequest(new { success = false, errors = validationResult.Errors });
                }

                // Нормализация данных
                var normalizedRequest = NormalizeBookingRequest(request);

                // Создание заявки
                var booking = await _bookingService.CreateBookingAsync(normalizedRequest);

                _logger.LogInformation("Заявка успешно создана: {BookingId}", booking.Id);

                return Ok(new
                {
                    success = true,
                    bookingId = booking.Id,
                    message = "Заявка успешно создана"
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при создании заявки");
                return StatusCode(500, new { success = false, message = "Внутренняя ошибка сервера" });
            }
        }

        /// <summary>
        /// Валидация заявки
        /// </summary>
        private ValidationResult ValidateBookingRequest(BookingRequest request)
        {
            var result = new ValidationResult();

            if (string.IsNullOrWhiteSpace(request.Name))
            {
                result.Errors.Add("Имя обязательно для заполнения");
            }
            else if (!IsValidName(request.Name))
            {
                result.Errors.Add("Некорректный формат имени");
            }

            if (string.IsNullOrWhiteSpace(request.Phone))
            {
                result.Errors.Add("Телефон обязателен для заполнения");
            }
            else if (!IsValidPhone(request.Phone))
            {
                result.Errors.Add("Некорректный формат телефона");
            }

            if (string.IsNullOrWhiteSpace(request.Brand))
            {
                result.Errors.Add("Марка автомобиля обязательна для заполнения");
            }
            else if (!IsValidCarBrand(request.Brand))
            {
                result.Errors.Add("Некорректная марка автомобиля");
            }

            return result;
        }

        /// <summary>
        /// Нормализация данных заявки
        /// </summary>
        private NormalizedBookingRequest NormalizeBookingRequest(BookingRequest request)
        {
            return new NormalizedBookingRequest
            {
                UserId = request.UserId ?? "anonymous",
                Name = NormalizeName(request.Name),
                Phone = NormalizePhone(request.Phone),
                Brand = NormalizeCarBrand(request.Brand),
                Timestamp = request.Timestamp ?? DateTime.UtcNow
            };
        }

        /// <summary>
        /// Нормализация номера телефона
        /// </summary>
        private string NormalizePhone(string rawPhone)
        {
            // Убираем все символы кроме цифр
            var digits = new string(rawPhone.Where(char.IsDigit).ToArray());

            // Нормализуем российский номер
            if (digits.Length == 10)
            {
                return "+7" + digits;
            }
            else if (digits.Length == 11 && digits.StartsWith("8"))
            {
                return "+7" + digits.Substring(1);
            }
            else if (digits.Length == 11 && digits.StartsWith("7"))
            {
                return "+" + digits;
            }
            else if (digits.Length == 11 && digits.StartsWith("1"))
            {
                return "+" + digits;
            }

            return "+" + digits;
        }

        /// <summary>
        /// Нормализация имени
        /// </summary>
        private string NormalizeName(string rawName)
        {
            // Приводим к правильному регистру
            var words = rawName.Trim().Split(' ', StringSplitOptions.RemoveEmptyEntries);
            var normalizedWords = words.Select(word => 
                char.ToUpper(word[0]) + word.Substring(1).ToLower());
            
            return string.Join(" ", normalizedWords);
        }

        /// <summary>
        /// Нормализация марки автомобиля
        /// </summary>
        private string NormalizeCarBrand(string rawBrand)
        {
            // Приводим к правильному регистру
            var words = rawBrand.Trim().Split(' ', StringSplitOptions.RemoveEmptyEntries);
            var normalizedWords = words.Select(word => 
                char.ToUpper(word[0]) + word.Substring(1).ToLower());
            
            return string.Join(" ", normalizedWords);
        }

        /// <summary>
        /// Валидация имени
        /// </summary>
        private bool IsValidName(string name)
        {
            // Проверяем, что имя содержит минимум 2 слова
            var words = name.Trim().Split(' ', StringSplitOptions.RemoveEmptyEntries);
            if (words.Length < 2)
            {
                return false;
            }

            // Проверяем, что каждое слово начинается с буквы
            var namePattern = @"^[А-Яа-яA-Za-z]+ [А-Яа-яA-Za-z]+(\s[А-Яа-яA-Za-z]+)*$";
            return Regex.IsMatch(name, namePattern);
        }

        /// <summary>
        /// Валидация телефона
        /// </summary>
        private bool IsValidPhone(string phone)
        {
            // Проверяем, что телефон содержит минимум 10 цифр
            var digits = phone.Where(char.IsDigit).Count();
            return digits >= 10;
        }

        /// <summary>
        /// Валидация марки автомобиля
        /// </summary>
        private bool IsValidCarBrand(string brand)
        {
            // Проверяем, что марка не пустая и содержит только допустимые символы
            if (string.IsNullOrWhiteSpace(brand))
            {
                return false;
            }

            var brandPattern = @"^[А-Яа-яA-Za-z0-9\s\-]+$";
            return Regex.IsMatch(brand, brandPattern);
        }
    }

    /// <summary>
    /// Модель заявки от чат-бота
    /// </summary>
    public class BookingRequest
    {
        public string UserId { get; set; }
        public string Name { get; set; }
        public string Phone { get; set; }
        public string Brand { get; set; }
        public DateTime? Timestamp { get; set; }
    }

    /// <summary>
    /// Нормализованная модель заявки
    /// </summary>
    public class NormalizedBookingRequest
    {
        public string UserId { get; set; }
        public string Name { get; set; }
        public string Phone { get; set; }
        public string Brand { get; set; }
        public DateTime Timestamp { get; set; }
    }

    /// <summary>
    /// Результат валидации
    /// </summary>
    public class ValidationResult
    {
        public bool IsValid => !Errors.Any();
        public List<string> Errors { get; set; } = new List<string>();
    }

    /// <summary>
    /// Интерфейс сервиса заявок
    /// </summary>
    public interface IBookingService
    {
        Task<Booking> CreateBookingAsync(NormalizedBookingRequest request);
    }

    /// <summary>
    /// Модель заявки
    /// </summary>
    public class Booking
    {
        public string Id { get; set; }
        public string UserId { get; set; }
        public string Name { get; set; }
        public string Phone { get; set; }
        public string Brand { get; set; }
        public DateTime CreatedAt { get; set; }
        public string Status { get; set; }
    }
}
