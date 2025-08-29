namespace AutoserviceBot.Domain.Entities;

/// <summary>
/// Сессия чат-бота
/// </summary>
public class ChatSession
{
    /// <summary>
    /// Идентификатор пользователя
    /// </summary>
    public string UserId { get; set; } = string.Empty;
    
    /// <summary>
    /// Текущее состояние диалога
    /// </summary>
    public string CurrentState { get; set; } = string.Empty;
    
    /// <summary>
    /// Собранные данные в процессе диалога
    /// </summary>
    public Dictionary<string, string> CollectedData { get; set; } = new();
    
    /// <summary>
    /// Время создания сессии
    /// </summary>
    public DateTime CreatedAt { get; set; }
    
    /// <summary>
    /// Время последней активности
    /// </summary>
    public DateTime LastActivityAt { get; set; }
    
    /// <summary>
    /// Флаг активности сессии
    /// </summary>
    public bool IsActive { get; set; } = true;
    
    /// <summary>
    /// История сообщений
    /// </summary>
    public List<ChatMessage> MessageHistory { get; set; } = new();
    
    /// <summary>
    /// Обновить время последней активности
    /// </summary>
    public void UpdateActivity()
    {
        LastActivityAt = DateTime.UtcNow;
    }
    
    /// <summary>
    /// Добавить сообщение в историю
    /// </summary>
    public void AddMessage(string text, bool isFromUser)
    {
        MessageHistory.Add(new ChatMessage
        {
            Text = text,
            IsFromUser = isFromUser,
            Timestamp = DateTime.UtcNow
        });
        
        UpdateActivity();
    }
    
    /// <summary>
    /// Очистить собранные данные
    /// </summary>
    public void ClearData()
    {
        CollectedData.Clear();
        CurrentState = string.Empty;
    }
    
    /// <summary>
    /// Проверить, истекла ли сессия
    /// </summary>
    public bool IsExpired(TimeSpan timeout)
    {
        return DateTime.UtcNow - LastActivityAt > timeout;
    }
}

/// <summary>
/// Сообщение в чате
/// </summary>
public class ChatMessage
{
    /// <summary>
    /// Текст сообщения
    /// </summary>
    public string Text { get; set; } = string.Empty;
    
    /// <summary>
    /// Флаг: сообщение от пользователя
    /// </summary>
    public bool IsFromUser { get; set; }
    
    /// <summary>
    /// Время отправки сообщения
    /// </summary>
    public DateTime Timestamp { get; set; }
}
