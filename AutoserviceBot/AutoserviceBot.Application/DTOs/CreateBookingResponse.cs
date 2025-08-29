namespace AutoserviceBot.Application.DTOs;

/// <summary>
/// Ответ на создание заявки
/// </summary>
public class CreateBookingResponse
{
    /// <summary>
    /// Флаг успешности операции
    /// </summary>
    public bool Success { get; set; }
    
    /// <summary>
    /// Идентификатор созданной заявки
    /// </summary>
    public string? BookingId { get; set; }
    
    /// <summary>
    /// Сообщение о результате операции
    /// </summary>
    public string Message { get; set; } = string.Empty;
    
    /// <summary>
    /// Список ошибок валидации
    /// </summary>
    public List<string> Errors { get; set; } = new();
    
    /// <summary>
    /// Создать успешный ответ
    /// </summary>
    public static CreateBookingResponse SuccessResponse(string bookingId, string message = "Заявка успешно создана")
    {
        return new CreateBookingResponse
        {
            Success = true,
            BookingId = bookingId,
            Message = message
        };
    }
    
    /// <summary>
    /// Создать ответ с ошибкой
    /// </summary>
    public static CreateBookingResponse ErrorResponse(string message, List<string>? errors = null)
    {
        return new CreateBookingResponse
        {
            Success = false,
            Message = message,
            Errors = errors ?? new List<string>()
        };
    }
}
