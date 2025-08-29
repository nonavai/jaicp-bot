using System.Text.RegularExpressions;

namespace AutoserviceBot.Domain.ValueObjects;

/// <summary>
/// Полное имя как value object
/// </summary>
public class FullName : IEquatable<FullName>
{
    private static readonly Regex NameRegex = new(
        @"^[А-Яа-яA-Za-z]+ [А-Яа-яA-Za-z]+(\s[А-Яа-яA-Za-z]+)*$",
        RegexOptions.Compiled);

    /// <summary>
    /// Значение имени
    /// </summary>
    public string Value { get; }

    /// <summary>
    /// Фамилия
    /// </summary>
    public string LastName { get; }

    /// <summary>
    /// Имя
    /// </summary>
    public string FirstName { get; }

    /// <summary>
    /// Отчество (может быть null)
    /// </summary>
    public string? MiddleName { get; }

    /// <summary>
    /// Конструктор
    /// </summary>
    /// <param name="value">Полное имя</param>
    /// <exception cref="ArgumentException">Если имя невалидно</exception>
    public FullName(string value)
    {
        if (string.IsNullOrWhiteSpace(value))
            throw new ArgumentException("Имя не может быть пустым", nameof(value));

        var normalized = Normalize(value);
        
        if (!IsValid(normalized))
            throw new ArgumentException($"Некорректный формат имени: {value}", nameof(value));

        Value = normalized;
        
        // Разбираем имя на части
        var parts = normalized.Split(' ', StringSplitOptions.RemoveEmptyEntries);
        FirstName = parts[0];
        LastName = parts[1];
        MiddleName = parts.Length > 2 ? parts[2] : null;
    }

    /// <summary>
    /// Создать имя из строки (без валидации)
    /// </summary>
    public static FullName? TryCreate(string? value)
    {
        if (string.IsNullOrWhiteSpace(value))
            return null;

        try
        {
            return new FullName(value);
        }
        catch
        {
            return null;
        }
    }

    /// <summary>
    /// Нормализация имени
    /// </summary>
    public static string Normalize(string rawName)
    {
        if (string.IsNullOrEmpty(rawName))
            return rawName;

        // Приводим к правильному регистру
        var words = rawName.Trim().Split(' ', StringSplitOptions.RemoveEmptyEntries);
        var normalizedWords = words.Select(word => 
            char.ToUpper(word[0]) + word.Substring(1).ToLower());
        
        return string.Join(" ", normalizedWords);
    }

    /// <summary>
    /// Проверка валидности имени
    /// </summary>
    public static bool IsValid(string name)
    {
        if (string.IsNullOrWhiteSpace(name))
            return false;

        // Проверяем, что имя содержит минимум 2 слова
        var words = name.Trim().Split(' ', StringSplitOptions.RemoveEmptyEntries);
        if (words.Length < 2)
            return false;

        // Проверяем, что каждое слово начинается с буквы
        foreach (var word in words)
        {
            if (!char.IsLetter(word[0]))
                return false;
        }

        return NameRegex.IsMatch(name);
    }

    /// <summary>
    /// Получить инициалы
    /// </summary>
    public string GetInitials()
    {
        var result = $"{LastName} {FirstName[0]}.";
        if (MiddleName != null)
            result += $"{MiddleName[0]}.";
        return result;
    }

    /// <summary>
    /// Получить короткую форму (Фамилия И.О.)
    /// </summary>
    public string GetShortForm()
    {
        var result = $"{LastName} {FirstName[0]}.";
        if (MiddleName != null)
            result += $"{MiddleName[0]}.";
        return result;
    }

    /// <summary>
    /// Получить полную форму (И.О. Фамилия)
    /// </summary>
    public string GetFullForm()
    {
        var result = $"{FirstName[0]}.{MiddleName?[0]}. {LastName}";
        return result;
    }

    public override string ToString() => Value;

    public bool Equals(FullName? other)
    {
        if (other is null) return false;
        if (ReferenceEquals(this, other)) return true;
        return Value == other.Value;
    }

    public override bool Equals(object? obj)
    {
        return Equals(obj as FullName);
    }

    public override int GetHashCode()
    {
        return Value.GetHashCode();
    }

    public static bool operator ==(FullName? left, FullName? right)
    {
        return Equals(left, right);
    }

    public static bool operator !=(FullName? left, FullName? right)
    {
        return !Equals(left, right);
    }

    public static implicit operator string(FullName fullName) => fullName.Value;
}
