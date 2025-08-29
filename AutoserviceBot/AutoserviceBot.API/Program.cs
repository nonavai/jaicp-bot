using AutoserviceBot.Application.Interfaces;
using AutoserviceBot.Infrastructure.Interfaces;
using AutoserviceBot.Infrastructure.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Регистрируем сервисы
builder.Services.AddScoped<IBookingService, BookingService>();
builder.Services.AddScoped<IDataNormalizationService, DataNormalizationService>();

// Добавляем CORS для тестирования
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

// Используем CORS
app.UseCors("AllowAll");

app.UseAuthorization();

app.MapControllers();

app.Run();
