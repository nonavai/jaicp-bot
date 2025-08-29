using System.ComponentModel.DataAnnotations;

namespace AutoserviceBot.Domain.Entities;

/// <summary>
/// Заявка на техобслуживание
/// </summary>
public class Booking
{
    /// <summary>
    /// Уникальный идентификатор заявки
    /// </summary>
    public string Id { get; set; } = string.Empty;
    
    /// <summary>
    /// Идентификатор пользователя
    /// </summary>
    public string UserId { get; set; } = string.Empty;
    
    /// <summary>
    /// Полное имя клиента
    /// </summary>
    [Required]
    [StringLength(100)]
    public string Name { get; set; } = string.Empty;
    
    /// <summary>
    /// Номер телефона клиента
    /// </summary>
    [Required]
    [StringLength(20)]
    public string Phone { get; set; } = string.Empty;
    
    /// <summary>
    /// Марка и модель автомобиля
    /// </summary>
    [Required]
    [StringLength(50)]
    public string CarBrand { get; set; } = string.Empty;
    
    /// <summary>
    /// Дата и время создания заявки
    /// </summary>
    public DateTime CreatedAt { get; set; }
    
    /// <summary>
    /// Статус заявки
    /// </summary>
    public BookingStatus Status { get; set; } = BookingStatus.New;
    
    /// <summary>
    /// Дополнительные заметки
    /// </summary>
    [StringLength(500)]
    public string? Notes { get; set; }
    
    /// <summary>
    /// Дата и время подтверждения заявки
    /// </summary>
    public DateTime? ConfirmedAt { get; set; }
    
    /// <summary>
    /// Дата и время обработки заявки
    /// </summary>
    public DateTime? ProcessedAt { get; set; }
}

/// <summary>
/// Статус заявки
/// </summary>
public enum BookingStatus
{
    /// <summary>
    /// Новая заявка
    /// </summary>
    New = 0,
    
    /// <summary>
    /// Подтверждена клиентом
    /// </summary>
    Confirmed = 1,
    
    /// <summary>
    /// В обработке
    /// </summary>
    InProgress = 2,
    
    /// <summary>
    /// Завершена
    /// </summary>
    Completed = 3,
    
    /// <summary>
    /// Отменена
    /// </summary>
    Cancelled = 4
}
