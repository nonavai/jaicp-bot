using AutoserviceBot.Application.DTOs;
using AutoserviceBot.Domain.Entities;

namespace AutoserviceBot.Application.Interfaces;

/// <summary>
/// Сервис для работы с заявками на техобслуживание
/// </summary>
public interface IBookingService
{
    /// <summary>
    /// Создать новую заявку
    /// </summary>
    /// <param name="request">Данные заявки</param>
    /// <returns>Результат создания заявки</returns>
    Task<CreateBookingResponse> CreateBookingAsync(CreateBookingRequest request);
    
    /// <summary>
    /// Получить заявку по идентификатору
    /// </summary>
    /// <param name="id">Идентификатор заявки</param>
    /// <returns>Заявка или null</returns>
    Task<Booking?> GetBookingByIdAsync(string id);
    
    /// <summary>
    /// Получить заявки пользователя
    /// </summary>
    /// <param name="userId">Идентификатор пользователя</param>
    /// <returns>Список заявок</returns>
    Task<IEnumerable<Booking>> GetUserBookingsAsync(string userId);
    
    /// <summary>
    /// Обновить статус заявки
    /// </summary>
    /// <param name="id">Идентификатор заявки</param>
    /// <param name="status">Новый статус</param>
    /// <returns>Обновленная заявка или null</returns>
    Task<Booking?> UpdateBookingStatusAsync(string id, BookingStatus status);
    
    /// <summary>
    /// Подтвердить заявку
    /// </summary>
    /// <param name="id">Идентификатор заявки</param>
    /// <returns>Подтвержденная заявка или null</returns>
    Task<Booking?> ConfirmBookingAsync(string id);
}
