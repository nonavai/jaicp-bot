using System.Text.RegularExpressions;

namespace AutoserviceBot.Domain.ValueObjects;

/// <summary>
/// Номер телефона как value object
/// </summary>
public class PhoneNumber : IEquatable<PhoneNumber>
{
    private static readonly Regex PhoneRegex = new(
        @"^\+?[1-9]\d{1,14}$",
        RegexOptions.Compiled | RegexOptions.IgnoreCase);

    /// <summary>
    /// Значение номера телефона в международном формате
    /// </summary>
    public string Value { get; }

    /// <summary>
    /// Конструктор
    /// </summary>
    /// <param name="value">Номер телефона</param>
    /// <exception cref="ArgumentException">Если номер невалиден</exception>
    public PhoneNumber(string value)
    {
        if (string.IsNullOrWhiteSpace(value))
            throw new ArgumentException("Номер телефона не может быть пустым", nameof(value));

        var normalized = Normalize(value);
        
        if (!IsValid(normalized))
            throw new ArgumentException($"Некорректный формат номера телефона: {value}", nameof(value));

        Value = normalized;
    }

    /// <summary>
    /// Создать номер телефона из строки (без валидации)
    /// </summary>
    public static PhoneNumber? TryCreate(string? value)
    {
        if (string.IsNullOrWhiteSpace(value))
            return null;

        try
        {
            return new PhoneNumber(value);
        }
        catch
        {
            return null;
        }
    }

    /// <summary>
    /// Нормализация номера телефона
    /// </summary>
    public static string Normalize(string rawPhone)
    {
        if (string.IsNullOrEmpty(rawPhone))
            return rawPhone;

        // Убираем все символы кроме цифр
        var digits = new string(rawPhone.Where(char.IsDigit).ToArray());

        // Нормализуем российский номер
        if (digits.Length == 10)
            return "+7" + digits;
        
        if (digits.Length == 11 && digits.StartsWith("8"))
            return "+7" + digits[1..];
        
        if (digits.Length == 11 && digits.StartsWith("7"))
            return "+" + digits;
        
        if (digits.Length == 11 && digits.StartsWith("1"))
            return "+" + digits;

        return "+" + digits;
    }

    /// <summary>
    /// Проверка валидности номера телефона
    /// </summary>
    public static bool IsValid(string phone)
    {
        return !string.IsNullOrEmpty(phone) && PhoneRegex.IsMatch(phone);
    }

    /// <summary>
    /// Получить номер в российском формате
    /// </summary>
    public string ToRussianFormat()
    {
        if (Value.StartsWith("+7"))
        {
            var digits = Value[2..];
            return $"8-{digits[0..3]}-{digits[3..6]}-{digits[6..8]}-{digits[8..]}";
        }
        
        return Value;
    }

    /// <summary>
    /// Получить номер в международном формате
    /// </summary>
    public string ToInternationalFormat()
    {
        return Value;
    }

    /// <summary>
    /// Получить только цифры
    /// </summary>
    public string GetDigitsOnly()
    {
        return new string(Value.Where(char.IsDigit).ToArray());
    }

    public override string ToString() => Value;

    public bool Equals(PhoneNumber? other)
    {
        if (other is null) return false;
        if (ReferenceEquals(this, other)) return true;
        return Value == other.Value;
    }

    public override bool Equals(object? obj)
    {
        return Equals(obj as PhoneNumber);
    }

    public override int GetHashCode()
    {
        return Value.GetHashCode();
    }

    public static bool operator ==(PhoneNumber? left, PhoneNumber? right)
    {
        return Equals(left, right);
    }

    public static bool operator !=(PhoneNumber? left, PhoneNumber? right)
    {
        return !Equals(left, right);
    }

    public static implicit operator string(PhoneNumber phoneNumber) => phoneNumber.Value;
}
