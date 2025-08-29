using System.Globalization;
using AutoserviceBot.Domain.ValueObjects;
using AutoserviceBot.Infrastructure.Interfaces;

namespace AutoserviceBot.Infrastructure.Services;

/// <summary>
/// Сервис нормализации данных от чат-бота
/// </summary>
public class DataNormalizationService : IDataNormalizationService
{
    /// <summary>
    /// Нормализация номера телефона
    /// </summary>
    public string NormalizePhone(string raw)
    {
        if (string.IsNullOrEmpty(raw)) return raw;
        
        var digits = new string(raw.Where(char.IsDigit).ToArray());
        
        if (digits.Length == 10) return "+7" + digits;
        if (digits.Length == 11 && digits.StartsWith("8")) return "+7" + digits[1..];
        if (digits.Length == 11 && digits.StartsWith("7")) return "+" + digits;
        if (digits.Length > 11) return "+" + digits;
        
        return digits;
    }

    /// <summary>
    /// Нормализация имени
    /// </summary>
    public string NormalizeName(string raw)
    {
        if (string.IsNullOrEmpty(raw)) return raw;
        
        var parts = raw.Split(new[] { ' ' }, StringSplitOptions.RemoveEmptyEntries);
        for (int i = 0; i < parts.Length; i++)
        {
            var p = parts[i].ToLowerInvariant();
            parts[i] = char.ToUpper(p[0]) + p[1..];
        }
        
        return string.Join(" ", parts);
    }

    /// <summary>
    /// Нормализация марки автомобиля
    /// </summary>
    public string NormalizeCarBrand(string raw)
    {
        if (string.IsNullOrEmpty(raw)) return raw;
        
        var map = new Dictionary<string, string>(StringComparer.InvariantCultureIgnoreCase)
        {
            ["шкода"] = "Skoda",
            ["шк."] = "Skoda",
            ["лада"] = "Lada",
            ["тойота"] = "Toyota",
            ["киа"] = "KIA",
            ["хендай"] = "Hyundai",
            ["фольксваген"] = "Volkswagen",
            ["бмв"] = "BMW",
            ["мерседес"] = "Mercedes",
            ["ауди"] = "Audi",
            ["форд"] = "Ford",
            ["шевроле"] = "Chevrolet",
            ["ниссан"] = "Nissan",
            ["мазда"] = "Mazda",
            ["хонда"] = "Honda",
            ["рено"] = "Renault",
            ["пежо"] = "Peugeot",
            ["ситроен"] = "Citroen",
            ["опель"] = "Opel",
            ["вольво"] = "Volvo",
            ["лексус"] = "Lexus",
            ["инфинити"] = "Infiniti",
            ["акура"] = "Acura",
            ["субару"] = "Subaru",
            ["мицубиси"] = "Mitsubishi",
            ["сузуки"] = "Suzuki",
            ["дайхатсу"] = "Daihatsu",
            ["фиат"] = "Fiat",
            ["альфа ромео"] = "Alfa Romeo",
            ["сит"] = "Seat"
        };
        
        var key = raw.Trim();
        
        return map.TryGetValue(key, out var val)
            ? val
            : CultureInfo.InvariantCulture.TextInfo.ToTitleCase(key.ToLowerInvariant());
    }

    /// <summary>
    /// Препроцессинг входящего текста
    /// </summary>
    public string PreprocessInput(string text)
    {
        if (string.IsNullOrEmpty(text)) return string.Empty;
        
        // Ограничиваем длину
        if (text.Length > 400)
        {
            text = text[..400];
        }
        
        // Нормализуем пробелы
        var normalized = System.Text.RegularExpressions.Regex.Replace(text, @"\s+", " ").Trim();
        
        // Убираем мусорные символы, оставляем только буквы, цифры, пробелы, дефисы, скобки, плюс
        normalized = System.Text.RegularExpressions.Regex.Replace(normalized, @"[^\w\s\-\(\)\+\.\,\!\?]", "");
        
        return normalized;
    }

    /// <summary>
    /// Валидация данных заявки
    /// </summary>
    public (bool IsValid, List<string> Errors) ValidateBookingData(string name, string phone, string carBrand)
    {
        var errors = new List<string>();

        // Валидация имени
        if (string.IsNullOrWhiteSpace(name))
        {
            errors.Add("Имя обязательно для заполнения");
        }
        else if (!FullName.IsValid(name))
        {
            errors.Add("Некорректный формат имени. Требуется ФИО (минимум 2 слова)");
        }

        // Валидация телефона
        if (string.IsNullOrWhiteSpace(phone))
        {
            errors.Add("Телефон обязателен для заполнения");
        }
        else if (!PhoneNumber.IsValid(phone))
        {
            errors.Add("Некорректный формат телефона");
        }

        // Валидация марки автомобиля
        if (string.IsNullOrWhiteSpace(carBrand))
        {
            errors.Add("Марка автомобиля обязательна для заполнения");
        }
        else if (carBrand.Length > 50)
        {
            errors.Add("Марка автомобиля слишком длинная");
        }

        return (errors.Count == 0, errors);
    }
}
