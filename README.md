# Post Client Service
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-red.svg)](https://www.thymeleaf.org/)

Web-клиент для PostApp, написанный на Spring MVC + Thymeleaf.  
Приложение отображает HTML-страницы и взаимодействует с отдельным REST API сервером — `PostRestService`.

## Что умеет клиент
### 👤 Пользователи
- Регистрация пользователя
- Авторизация через REST API
- Просмотр своего профиля
- Редактирование профиля
- Просмотр публичных профилей пользователей
### 📝 Посты
- Создание, редактирование и удаление постов
- Проверка авторства (только автор может редактировать/удалять)
- Просмотр списка постов
- Пагинация постов
- Просмотр детальной страницы поста
- Загрузка изображений для постов и аватаров пользователя
### 💬 Комментарии
- Создание комментариев
- Редактирование и удаление своих комментариев
- Автор поста может удалять любые комментарии под своим постом
- Пагинация комментариев в детальной странице поста 

### 🔐 Безопасность
- Хранение JWT в `HttpSession`
- Обработка ошибок REST API
- Страницы ошибок `403` и `500`

### 🏗️Сборка
- Docker-сборка
- Деплой на Render

## Архитектура

Проект разделен на два приложения:

```text
PostClientService  → Thymeleaf web client
PostRestService    → REST API server
```

Клиент не работает напрямую с базой данных.  
Все данные он получает через HTTP-запросы к REST API.

Схема:

```text
Browser
   ↓
PostClientService
   ↓ RestTemplate + JWT
PostRestService
   ↓
PostgreSQL
```

Такое разделение позволяет заменить клиент, не переписывая backend.

## Технологии

- Java
- Spring Boot
- Spring MVC
- Spring Security
- Thymeleaf
- RestTemplate
- Bean Validation
- Maven
- Docker


## Авторизация

Авторизация построена через JWT.

1. Пользователь отправляет email/password через форму логина.
2. `PostClientService` отправляет запрос на REST API `/api/auth/login`.
3. REST-сервер возвращает JWT.
4. Клиент сохраняет JWT в `HttpSession`.
5. При следующих запросах к REST API клиент добавляет токен в заголовок:

```http
Authorization: Bearer <jwt-token>
```

JWT хранится на стороне клиентского приложения:

```java
session.setAttribute("jwt_token", jwt.getToken());
```

Браузер работает с клиентом через обычную сессию.

## Обработка ошибок

Клиент разделяет ошибки по статусам:

| Status | Meaning | Client behavior |
|---|---|---|
| `400 Bad Request` | Ошибка валидации | Показывается ошибка на форме |
| `401 Unauthorized` | Неверный логин/пароль или нет авторизации | Возврат на страницу логина |
| `403 Forbidden` | Нет прав на действие | Показывается страница `error/403` |
| `500 Internal Server Error` | Ошибка сервера | Показывается страница `error/500` |

Ошибки от REST API маппятся в клиентские исключения, например:

```text
ClientForbiddenException
ClientApiException
BadLoginException
```

## Пагинация

Клиент работает с пагинированными ответами от REST API.

Пример DTO:

```java
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
}
```

При большом количестве элементов а страницах выводится навигация вида:

```text
< 1 2 3 4 5 >
```

Пагинация используется для:

- главной страницы с постами;
- комментариев на странице поста;
- комментариев на странице редактирования поста.

## Конфигурация

Локальный конфиг находится в:

```text
src/main/resources/application.properties
```

Production-конфиг для Render:

```text
src/main/resources/application-prod.properties
```


## Environment variables

Для production-запуска используются переменные окружения:

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |
| `POST_REST_API_BASE_URL` | URL REST API сервера | `https://post-rest-api-nev6.onrender.com/api` |
| `MAX_FILE_SIZE` | Максимальный размер одного файла | `10MB` |
| `MAX_REQUEST_SIZE` | Максимальный размер multipart-запроса | `10MB` |
| `SPRING_WEB_LOG_LEVEL` | Уровень логов Spring Web | `INFO` |
| `SPRING_SECURITY_LOG_LEVEL` | Уровень логов Spring Security | `INFO` |

`PORT` на Render задается автоматически.

## Локальный запуск без Docker

Перед запуском должен быть доступен REST API сервер.

По умолчанию клиент ожидает REST API здесь:

```text
http://localhost:8080/api
```

Запуск:

```powershell
.\mvnw.cmd spring-boot:run
```

или если Maven установлен глобально:

```powershell
mvn spring-boot:run
```

Клиент будет доступен по адресу:

```text
http://localhost:8081
```

## Сборка jar

```powershell
.\mvnw.cmd clean package -DskipTests
```

После сборки jar появится в:

```text
target/
```

## Docker

### Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
```

### Сборка Docker image

```powershell
docker build -t post-client-service .
```

### Запуск Docker container

```powershell
docker run --rm -p 8086:8081 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e PORT=8081 `
  -e POST_REST_API_BASE_URL=https://post-rest-api-nev6.onrender.com/api `
  post-client-service
```

После запуска клиент будет доступен по адресу:

```text
http://localhost:8086
```

```

`PORT` вручную указывать не нужно. Render передает его автоматически, а приложение читает его через:

```properties
server.port=${PORT:8081}
```

## Работа со статическими ресурсами

Статические файлы клиента лежат в:

```text
src/main/resources/static
```

Например:

```text
src/main/resources/static/css
src/main/resources/static/images/default-profile.png
```

Эти файлы попадают внутрь jar и Docker image, поэтому доступны после деплоя.

Пример:

```text
/images/default-profile.png
```

## Изображения пользователей и постов

Загруженные пользователями изображения сейчас хранятся на REST-сервере.

Важно: на Render Free локальное файловое хранилище контейнера ненадежно.  
При redeploy/restart загруженные файлы могут пропасть.



## Основные страницы

| Page | Description |
|---|---|
| `/auth/login` | Страница логина |
| `/auth/registration` | Страница регистрации |
| `/posts` | Главная страница с постами |
| `/posts/create` | Создание поста |
| `/posts/{id}` | Детальная страница поста |
| `/posts/{id}/update` | Редактирование поста |
| `/me` | Профиль текущего пользователя |
| `/me/update` | Редактирование профиля |
| `/users` | Список пользователей |
| `/user/{id}` | Публичный профиль пользователя |

