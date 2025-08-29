using System.ComponentModel.DataAnnotations;

namespace AutoserviceBot.Application.DTOs;

/// <summary>
/// Запрос на создание заявки от чат-бота
/// </summary>
public class CreateBookingRequest
{
    /// <summary>
    /// Идентификатор пользователя
    /// </summary>
    public string? UserId { get; set; }
    
    /// <summary>
    /// Полное имя клиента
    /// </summary>
    [Required(ErrorMessage = "Имя обязательно для заполнения")]
    [StringLength(100, ErrorMessage = "Имя не может быть длиннее 100 символов")]
    public string Name { get; set; } = string.Empty;
    
    /// <summary>
    /// Номер телефона клиента
    /// </summary>
    [Required(ErrorMessage = "Телефон обязателен для заполнения")]
    [StringLength(20, ErrorMessage = "Телефон не может быть длиннее 20 символов")]
    public string Phone { get; set; } = string.Empty;
    
    /// <summary>
    /// Марка и модель автомобиля
    /// </summary>
    [Required(ErrorMessage = "Марка автомобиля обязательна для заполнения")]
    [StringLength(50, ErrorMessage = "Марка автомобиля не может быть длиннее 50 символов")]
    public string CarBrand { get; set; } = string.Empty;
    
    /// <summary>
    /// Дополнительные заметки
    /// </summary>
    [StringLength(500, ErrorMessage = "Заметки не могут быть длиннее 500 символов")]
    public string? Notes { get; set; }
    
    /// <summary>
    /// Временная метка создания запроса
    /// </summary>
    public DateTime? Timestamp { get; set; }
}
