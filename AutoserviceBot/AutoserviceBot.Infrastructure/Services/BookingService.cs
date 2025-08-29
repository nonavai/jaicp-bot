using AutoserviceBot.Application.DTOs;
using AutoserviceBot.Application.Interfaces;
using AutoserviceBot.Domain.Entities;

namespace AutoserviceBot.Infrastructure.Services;

/// <summary>
/// Реализация сервиса для работы с заявками на техобслуживание
/// </summary>
public class BookingService : IBookingService
{
    private readonly Dictionary<string, Booking> _bookings = new();

    public BookingService()
    {
    }

    /// <summary>
    /// Создать новую заявку
    /// </summary>
    public async Task<CreateBookingResponse> CreateBookingAsync(CreateBookingRequest request)
    {
        try
        {

            // Генерируем уникальный ID
            var bookingId = $"BK{DateTime.UtcNow:yyyyMMddHHmmss}";

            // Создаем заявку
            var booking = new Booking
            {
                Id = bookingId,
                UserId = request.UserId ?? "anonymous",
                Name = request.Name,
                Phone = request.Phone,
                CarBrand = request.CarBrand,
                Notes = request.Notes,
                CreatedAt = request.Timestamp ?? DateTime.UtcNow,
                Status = BookingStatus.New
            };

            // Сохраняем в память (в реальном проекте - в базу данных)
            _bookings[bookingId] = booking;



            return CreateBookingResponse.SuccessResponse(bookingId);
        }
        catch (Exception ex)
        {
            return CreateBookingResponse.ErrorResponse("Ошибка при создании заявки");
        }
    }

    /// <summary>
    /// Получить заявку по идентификатору
    /// </summary>
    public async Task<Booking?> GetBookingByIdAsync(string id)
    {
        _bookings.TryGetValue(id, out var booking);
        return booking;
    }

    /// <summary>
    /// Получить заявки пользователя
    /// </summary>
    public async Task<IEnumerable<Booking>> GetUserBookingsAsync(string userId)
    {
        return _bookings.Values.Where(b => b.UserId == userId);
    }

    /// <summary>
    /// Обновить статус заявки
    /// </summary>
    public async Task<Booking?> UpdateBookingStatusAsync(string id, BookingStatus status)
    {
        if (_bookings.TryGetValue(id, out var booking))
        {
            booking.Status = status;
            
            switch (status)
            {
                case BookingStatus.Confirmed:
                    booking.ConfirmedAt = DateTime.UtcNow;
                    break;
                case BookingStatus.InProgress:
                    booking.ProcessedAt = DateTime.UtcNow;
                    break;
            }


            return booking;
        }

        return null;
    }

    /// <summary>
    /// Подтвердить заявку
    /// </summary>
    public async Task<Booking?> ConfirmBookingAsync(string id)
    {
        return await UpdateBookingStatusAsync(id, BookingStatus.Confirmed);
    }
}
