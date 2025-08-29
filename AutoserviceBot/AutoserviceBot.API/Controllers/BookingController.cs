using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using AutoserviceBot.Application.DTOs;
using AutoserviceBot.Application.Interfaces;
using AutoserviceBot.Infrastructure.Interfaces;

namespace AutoserviceBot.API.Controllers;

/// <summary>
/// Контроллер для обработки заявок на техобслуживание от чат-бота
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class BookingController : ControllerBase
{
    private readonly ILogger<BookingController> _logger;
    private readonly IBookingService _bookingService;
    private readonly IDataNormalizationService _normalizationService;

    public BookingController(
        ILogger<BookingController> logger,
        IBookingService bookingService,
        IDataNormalizationService normalizationService)
    {
        _logger = logger;
        _bookingService = bookingService;
        _normalizationService = normalizationService;
    }

    /// <summary>
    /// Создать заявку на техобслуживание
    /// </summary>
    /// <param name="request">Данные заявки</param>
    /// <returns>Результат создания заявки</returns>
    [HttpPost]
    public async Task<ActionResult<CreateBookingResponse>> CreateBooking([FromBody] CreateBookingRequest request)
    {
        try
        {
            _logger.LogInformation("Получена заявка от чат-бота: {@Request}", request);

            // Валидация входящих данных
            var validationResult = _normalizationService.ValidateBookingData(
                request.Name, request.Phone, request.CarBrand);

            if (!validationResult.IsValid)
            {
                _logger.LogWarning("Валидация заявки не пройдена: {@Errors}", validationResult.Errors);
                return BadRequest(CreateBookingResponse.ErrorResponse(
                    "Данные заявки не прошли валидацию", validationResult.Errors));
            }

            // Нормализация данных
            var normalizedRequest = new CreateBookingRequest
            {
                UserId = request.UserId ?? "anonymous",
                Name = _normalizationService.NormalizeName(request.Name),
                Phone = _normalizationService.NormalizePhone(request.Phone),
                CarBrand = _normalizationService.NormalizeCarBrand(request.CarBrand),
                Notes = request.Notes,
                Timestamp = request.Timestamp ?? DateTime.UtcNow
            };

            _logger.LogInformation("Нормализованные данные заявки: {@NormalizedRequest}", normalizedRequest);

            // Создание заявки
            var result = await _bookingService.CreateBookingAsync(normalizedRequest);

            if (result.Success)
            {
                _logger.LogInformation("Заявка успешно создана: {BookingId}", result.BookingId);
                return Ok(result);
            }
            else
            {
                _logger.LogError("Ошибка при создании заявки: {Message}", result.Message);
                return BadRequest(result);
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Неожиданная ошибка при создании заявки");
            return StatusCode(500, CreateBookingResponse.ErrorResponse(
                "Внутренняя ошибка сервера при создании заявки"));
        }
    }

    /// <summary>
    /// Получить заявку по идентификатору
    /// </summary>
    /// <param name="id">Идентификатор заявки</param>
    /// <returns>Заявка</returns>
    [HttpGet("{id}")]
    public async Task<ActionResult<object>> GetBooking(string id)
    {
        try
        {
            var booking = await _bookingService.GetBookingByIdAsync(id);
            
            if (booking == null)
            {
                return NotFound(new { message = "Заявка не найдена" });
            }

            return Ok(booking);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Ошибка при получении заявки {Id}", id);
            return StatusCode(500, new { message = "Внутренняя ошибка сервера" });
        }
    }

    /// <summary>
    /// Получить заявки пользователя
    /// </summary>
    /// <param name="userId">Идентификатор пользователя</param>
    /// <returns>Список заявок</returns>
    [HttpGet("user/{userId}")]
    public async Task<ActionResult<IEnumerable<object>>> GetUserBookings(string userId)
    {
        try
        {
            var bookings = await _bookingService.GetUserBookingsAsync(userId);
            return Ok(bookings);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Ошибка при получении заявок пользователя {UserId}", userId);
            return StatusCode(500, new { message = "Внутренняя ошибка сервера" });
        }
    }

    /// <summary>
    /// Подтвердить заявку
    /// </summary>
    /// <param name="id">Идентификатор заявки</param>
    /// <returns>Подтвержденная заявка</returns>
    [HttpPost("{id}/confirm")]
    public async Task<ActionResult<object>> ConfirmBooking(string id)
    {
        try
        {
            var booking = await _bookingService.ConfirmBookingAsync(id);
            
            if (booking == null)
            {
                return NotFound(new { message = "Заявка не найдена" });
            }

            return Ok(booking);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Ошибка при подтверждении заявки {Id}", id);
            return StatusCode(500, new { message = "Внутренняя ошибка сервера" });
        }
    }

    /// <summary>
    /// Обновить статус заявки
    /// </summary>
    /// <param name="id">Идентификатор заявки</param>
    /// <param name="status">Новый статус</param>
    /// <returns>Обновленная заявка</returns>
    [HttpPut("{id}/status")]
    public async Task<ActionResult<object>> UpdateBookingStatus(string id, [FromBody] string status)
    {
        try
        {
            if (!Enum.TryParse<AutoserviceBot.Domain.Entities.BookingStatus>(status, out var bookingStatus))
            {
                return BadRequest(new { message = "Некорректный статус заявки" });
            }

            var booking = await _bookingService.UpdateBookingStatusAsync(id, bookingStatus);
            
            if (booking == null)
            {
                return NotFound(new { message = "Заявка не найдена" });
            }

            return Ok(booking);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Ошибка при обновлении статуса заявки {Id}", id);
            return StatusCode(500, new { message = "Внутренняя ошибка сервера" });
        }
    }
}
