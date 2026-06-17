# Post Client Service


[![Java](https://img.shields.io/badge/Java-blue.svg)](https://adoptium.net/)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6DB33F.svg)](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F.svg)](https://spring.io/projects/spring-security)

[![REST API](https://img.shields.io/badge/REST%20API-orange.svg)](https://restfulapi.net/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-red.svg)](https://www.thymeleaf.org/)

[![HTML](https://img.shields.io/badge/HTML-E34F26.svg)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![CSS](https://img.shields.io/badge/CSS-1572B6.svg)](https://developer.mozilla.org/en-US/docs/Web/CSS)
[![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E.svg)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)

`PostClientService` — web-клиент для PostApp, написанный на Spring MVC + Thymeleaf.

Клиент рендерит HTML-страницы, хранит JWT в `HttpSession` и взаимодействует с отдельным REST API сервером — [`PostRestService`](https://github.com/SiVitaliy/post-rest-api).



---

## Содержание

- [Что умеет клиент](#что-умеет-клиент)
- [Архитектура](#архитектура)
- [Технологии](#технологии)
- [Авторизация](#авторизация)
- [Обработка ошибок](#обработка-ошибок)
- [Пагинация](#пагинация)
- [Конфигурация](#конфигурация)
- [Environment variables](#environment-variables)
- [Локальный запуск без Docker](#локальный-запуск-без-docker)
- [Docker](#docker)
- [Деплой на Render](#деплой-на-render)
- [Работа со статическими ресурсами](#работа-со-статическими-ресурсами)
- [Изображения пользователей и постов](#изображения-пользователей-и-постов)
- [Основные страницы](#основные-страницы)


---

## Что умеет клиент

### 👤 Пользователи

- Регистрация пользователя
- Авторизация через REST API
- Хранение JWT в `HttpSession`
- Просмотр своего профиля
- Редактирование профиля
- Просмотр публичных профилей пользователей
- Поиск пользователей

### 📝 Посты

- Просмотр списка постов
- Пагинация списка постов
- Просмотр детальной страницы поста
- Создание постов
- Редактирование постов
- Удаление постов
- Проверка авторства: только автор может редактировать и удалять свой пост
- Загрузка изображений для постов

### 💬 Комментарии

- Создание комментариев
- Редактирование своих комментариев
- Удаление своих комментариев
- Автор поста может удалять любые комментарии под своим постом
- Пагинация комментариев на детальной странице поста
- Пагинация комментариев на странице редактирования поста

### 🔐 Безопасность и ошибки

- Передача JWT в REST API через `Authorization: Bearer <token>`
- Обработка `400`, `401`, `403`, `500` ответов от REST API
- Страницы ошибок `403` и `500`
- Вывод ошибок валидации через `BindingResult`
- Вывод ошибок REST API через model attributes

### 🏗️ Сборка и деплой

- Maven-сборка
- Docker-сборка
- Production profile
- Деплой на Render
- Конфигурация через environment variables

---

## Архитектура

Проект разделен на два приложения:

```text
PostClientService  → Thymeleaf web client
PostRestService    → REST API server
```

Клиент не работает напрямую с базой данных.  
Все данные он получает через HTTP-запросы к REST API.

Схема взаимодействия:

```text
Browser
   ↓
PostClientService
   |
   | RestTemplate + JWT 
   ↓
PostRestService
   ↓
PostgreSQL
```

Такое разделение позволяет заменить web-клиент на другой frontend, например React/Vue/mobile app, не переписывая backend.

### Зоны ответственности

| Service | Responsibility |
|---|---|
| `PostClientService` | HTML-страницы, формы, пользовательский интерфейс, обработка UI-ошибок |
| `PostRestService` | Бизнес-логика, безопасность, работа с БД, JWT, файлы, REST API |

---

## Технологии

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Thymeleaf
- RestTemplate
- Bean Validation
- Maven
- Docker
- Render

---

## Авторизация

Авторизация построена через JWT.

Сценарий:

1. Пользователь отправляет email/password через форму логина.
2. `PostClientService` отправляет запрос на REST API `/api/auth/login`.
3. REST-сервер проверяет данные пользователя.
4. REST-сервер возвращает JWT.
5. Клиент сохраняет JWT в `HttpSession`.
6. При следующих запросах к REST API клиент добавляет токен в HTTP-заголовок.

Пример заголовка:

```http
Authorization: Bearer <jwt-token>
```

JWT хранится на стороне клиентского приложения:

```java
session.setAttribute("jwt_token", jwt.getToken());
```

Браузер взаимодействует с `PostClientService` через обычную session cookie.

Схема:

```text
Browser
   ↓ session cookie
PostClientService
   ↓ Authorization: Bearer JWT
PostRestService
```

Такой подход удобен для pet-проекта: браузер не работает с JWT напрямую, а клиентское приложение само добавляет токен в REST-запросы.

---

## Обработка ошибок

Клиент разделяет ошибки по HTTP-статусам.

| Status | Meaning | Client behavior |
|---|---|---|
| `400 Bad Request` | Ошибка валидации или некорректный запрос | Показывается ошибка на форме |
| `401 Unauthorized` | Неверный логин/пароль или нет авторизации | Возврат на страницу логина |
| `403 Forbidden` | Пользователь авторизован, но нет прав | Показывается страница `error/403` |
| `500 Internal Server Error` | Ошибка сервера или интеграции | Показывается страница `error/500` |

Ошибки REST API маппятся в клиентские исключения:

```text
BadLoginException
ClientForbiddenException
ClientApiException
```

Пример логики:

```text
400 → ошибка формы
401 → страница логина
403 → error/403
500 → error/500
```

Ошибки локальной валидации формы обрабатываются через `BindingResult`.

Ошибки, пришедшие от REST API, добавляются в `Model` как общий error message.

---

## Пагинация

Клиент работает с пагинированными ответами REST API.

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

При большом количестве элементов на страницах выводится навигация вида:

```text
< 1 2 3 4 5 >
```

Пагинация используется для:

- главной страницы с постами;
- комментариев на странице поста;
- комментариев на странице редактирования поста.

Обычно клиент передает номер страницы через query parameter:

```text
/posts?page=0
/posts/{id}?commentPage=0
```

Размер страницы может быть фиксирован на стороне клиента или REST API.

---

## Конфигурация

Локальная конфигурация находится в:

```text
src/main/resources/application.properties
```

Production-конфигурация для Render:

```text
src/main/resources/application-prod.properties
```

Пример production-конфигурации:

```properties
spring.application.name=PostClientService

server.port=${PORT:8081}

post.client.base-url=${POST_REST_API_BASE_URL:https://post-rest-api-nev6.onrender.com/api}

post.client.user-url=${post.client.base-url}/user
post.client.login-url=${post.client.base-url}/auth/login
post.client.registration-url=${post.client.base-url}/auth/registration
post.client.posts-url=${post.client.base-url}/posts
post.client.commentaries-url=${post.client.base-url}/commentaries

spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=${MAX_FILE_SIZE:10MB}
spring.servlet.multipart.max-request-size=${MAX_REQUEST_SIZE:10MB}

spring.thymeleaf.cache=true

logging.level.org.springframework.web=${SPRING_WEB_LOG_LEVEL:INFO}
logging.level.org.springframework.security=${SPRING_SECURITY_LOG_LEVEL:INFO}
```

---

## Environment variables

Для production-запуска используются переменные окружения.

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |
| `POST_REST_API_BASE_URL` | URL REST API сервера | `https://post-rest-api-nev6.onrender.com/api` |
| `MAX_FILE_SIZE` | Максимальный размер одного файла | `10MB` |
| `MAX_REQUEST_SIZE` | Максимальный размер multipart-запроса | `10MB` |
| `SPRING_WEB_LOG_LEVEL` | Уровень логов Spring Web | `INFO` |
| `SPRING_SECURITY_LOG_LEVEL` | Уровень логов Spring Security | `INFO` |

`PORT` на Render задается автоматически.

Приложение читает порт так:

```properties
server.port=${PORT:8081}
```

---

## Локальный запуск без Docker

Перед запуском клиента должен быть доступен REST API сервер.

По умолчанию клиент ожидает REST API здесь:

```text
http://localhost:8080/api
```

Запуск через Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Или если Maven установлен глобально:

```powershell
mvn spring-boot:run
```

Клиент будет доступен по адресу:

```text
http://localhost:8081
```

---

## Сборка jar

```powershell
.\mvnw.cmd clean package -DskipTests
```

После сборки jar появится в директории:

```text
target/
```

---

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

### Почему используется `8086:8081`

```text
-p 8086:8081
   ↑    ↑
   |    порт внутри контейнера
   порт на локальной машине
```

Это удобно, если локальный порт `8081` уже занят.

---

## Деплой на Render

Для деплоя клиента на Render:

1. Создать новый **Web Service**.
2. Подключить GitHub repository с `PostClientService`.
3. Выбрать **Docker** environment.
4. Указать root directory, если проект находится в monorepo.
5. Добавить environment variables.
6. Запустить deploy.

Минимальные env-переменные для Render:

```text
SPRING_PROFILES_ACTIVE=prod
POST_REST_API_BASE_URL=https://post-rest-api-nev6.onrender.com/api
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB
SPRING_WEB_LOG_LEVEL=INFO
SPRING_SECURITY_LOG_LEVEL=INFO
```

`PORT` вручную указывать не нужно. Render передает его автоматически.

После деплоя клиент будет доступен по URL, который выдаст Render:

```text
https://post-client.onrender.com/
```

---

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

Пример статического ресурса:

```text
/images/default-profile.png
```

---

## Изображения пользователей и постов

Загруженные пользователями изображения сейчас хранятся на REST-сервере.

Важно: на Render Free локальное файловое хранилище контейнера ненадежно.  
При redeploy/restart загруженные файлы могут пропасть.

Правильная production-схема:

```text
REST server получает MultipartFile
REST server валидирует файл
REST server загружает файл в локальное хранилище
REST server получает public URL
REST server сохраняет URL в БД
Client отображает изображение по URL
```

---

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
| `/error/403` | Страница ошибки доступа |
| `/error/500` | Страница ошибки сервера |

---

## Что важно знать по проекту

- Клиент не ходит напрямую в БД.
- Все данные получаются через REST API.
- JWT хранится в `HttpSession`.
- Для REST-запросов используется `RestTemplate`.
- Ошибки REST API преобразуются в клиентские исключения.
- Ошибки форм показываются через `BindingResult`.
- Production-настройки передаются через environment variables.
- Dockerfile сам собирает jar и запускает приложение.
- Render деплоит приложение из Dockerfile.
- Статические файлы клиента попадают внутрь jar и Docker image.

---

## Ограничения

- На Render Free загруженные изображения могут пропасть после перезапуска или редеплоя.
- `HttpSession` хранитсяв оперативной памяти, поэтому после рестарта клиента пользователь может разлогиниться.

---

## Возможные усовершенствования 

- Подключить Cloudinary/S3/Supabase Storage для изображений.
- Добавить лайки к постам.
- Добавить подписки на пользователей.
- Добавить фильтр постов по популярности.
- Добавить ленту постов по подпискам.
