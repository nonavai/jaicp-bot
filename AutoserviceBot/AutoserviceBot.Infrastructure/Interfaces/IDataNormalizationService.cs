namespace AutoserviceBot.Infrastructure.Interfaces;

/// <summary>
/// Интерфейс сервиса нормализации данных
/// </summary>
public interface IDataNormalizationService
{
    /// <summary>
    /// Нормализация номера телефона
    /// </summary>
    string NormalizePhone(string raw);
    
    /// <summary>
    /// Нормализация имени
    /// </summary>
    string NormalizeName(string raw);
    
    /// <summary>
    /// Нормализация марки автомобиля
    /// </summary>
    string NormalizeCarBrand(string raw);
    
    /// <summary>
    /// Препроцессинг входящего текста
    /// </summary>
    string PreprocessInput(string text);
    
    /// <summary>
    /// Валидация данных заявки
    /// </summary>
    (bool IsValid, List<string> Errors) ValidateBookingData(string name, string phone, string carBrand);
}
