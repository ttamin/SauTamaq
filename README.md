# Кулинарное приложение: Бэкенд для мобильного приложения с рецептами

Этот проект представляет собой бэкенд для мобильного приложения, которое предоставляет пользователям различные рецепты блюд. Разработанный с использованием Spring Boot, приложение обеспечивает удобный интерфейс для взаимодействия с базой данных рецептов, как для администраторов, так и для обычных пользователей.

## Основные характеристики

- **Управление рецептами**: Позволяет добавлять, просматривать, редактировать и удалять рецепты через API.
- **Безопасность**: Защита ресурсов приложения с помощью Spring Security, обеспечивая базовую аутентификацию и авторизацию.
- **Обработка данных**: Использование Spring Data JPA для взаимодействия с базой данных PostgreSQL и MapStruct для маппинга объектов DTO.

## Технологии

- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- MapStruct
- jjwt для работы с JWT

## Начало работы

Чтобы запустить проект:

1. **Клонируйте репозиторий** на ваш локальный компьютер.
2. **Настройте соединение** с вашей базой данных PostgreSQL, указав необходимые параметры в `application.properties`.
3. **Соберите проект** с помощью Maven, выполнив команду `mvn clean install`.
4. **Запустите собранный .jar файл**, используя `java -jar target/your-application.jar`.
