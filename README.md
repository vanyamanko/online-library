# 9. API для системы рецензирования книг

## Содержание

- [Функционал](#Функционал)
- [Примеры хранимых сущностей](#Примеры-хранимых-сущностей)
- [Используемые технологии](#Используемые-технологии)
- [Полезные ссылки](#Полезные-ссылки)


## Функционал

### Основные возможности:
1. **Управление книгами**
   - Получение списка книг (пагинация, фильтрация)
   - Поиск книг по названию/автору 
   - Просмотр детальной информации о книге (описание, жанр, обложка)
   - Добавление/удаление книг в каталог (для модераторов)

2. **Рецензирование**
   - Добавление/редактирование/удаление рецензий
   - Оценка книг по 5-звездочной шкале
   - Лайки/дизлайки для рецензий
   - Комментирование рецензий

3. **Персонализация**
   - Избранные книги (закладки)
   - История просмотров

4. **Поиск и фильтрация**
   - По жанрам (фэнтези, научная литература и т.д.)
   - По рейтингу (средняя оценка)
   - По дате публикации

5. **Администрирование**
   - Модерация контента
   - Управление пользователями
   - Аналитика (топ книг, активные пользователи)

---

### Дополнительный функционал
Рекомендации на основе:
- Жанровых предпочтений (k-NN)

- Оценок похожих пользователей (коллаборативная фильтрация)
---
## Примеры хранимых сущностей

### 1. Книга (Book)
```json
{
  "id": "ISBN_123456789",
  "title": "1984",
  "authors": ["George Orwell"],
  "publisher": "Secker & Warburg",
  "publishedDate": "1949-06-08",
  "description": "A dystopian social science fiction novel...",
  "pageCount": 328,
  "genres": ["dystopian", "political fiction"],
  "coverUrl": "http://example.com/covers/1984.jpg",
  "averageRating": 4.5,
  "reviewsCount": 42
}
```

### 2. Рецензия (Review)
```json
{
  "id": "rev_987654",
  "bookId": "ISBN_123456789",
  "userId": "user_13579",
  "rating": 5,
  "text": "One of the most influential books of the 20th century...",
  "likes": 24,
  "dislikes": 2,
  "createdAt": "2023-05-15T14:30:00Z",
  "updatedAt": "2023-05-20T09:15:00Z"
}
```

### 3. Пользователь (User)
```json
{
  "id": "user_24680",
  "username": "book_lover42",
  "email": "user@example.com",
  "preferredGenres": ["sci-fi", "biography"],
  "joinedAt": "2022-11-03T10:00:00Z",
  "lastActive": "2023-06-01T18:45:00Z"
}
```

### 4. Комментарий (Comment)
```json
{
  "id": "comm_54321",
  "reviewId": "rev_987654",
  "userId": "user_24680",
  "text": "I completely agree with your analysis!",
  "createdAt": "2023-05-16T08:20:00Z"
}
```

## Используемые технологии

- Java 17+
- Spring Boot
- Spring Web
- Spring Security (JWT)
- Spring Data JPA
- Liquibase / Flyway
- Swagger / OpenAPI (springdoc-openapi)
- JUnit 5
- GitFlow
- OpenCSV

## Полезные ссылки

- [Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [Flyway Documentation](https://documentation.red-gate.com/fd)
- [springdoc-openapi Documentation](https://springdoc.org/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [GitFlow Documentation (GitHub Flow)](https://docs.github.com/en/get-started/using-github/github-flow)
- [OpenCSV Documentation](http://opencsv.sourceforge.net/)
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
