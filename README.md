# 🚚 Delivery Service API

Сервис доставки еды с полноценной бизнес-логикой: заказы, оплата, рестораны, доставка и курьеры.

---

## 📌 О проекте

Это **монолитное Spring Boot приложение**, которое моделирует работу сервиса доставки еды.
Реализован полный цикл заказа:

1. Сначала пользователь регистрируется/логинится
2. Пользователь выбирает блюда из меню ресторана
3. Создаёт заказ (`PENDING`)
4. Инициализирует и обрабатывает платёж
5. После успешной оплаты заказ подтверждается (`CONFIRMED`)
6. Ресторан начинает готовку (`COOKING`) и отмечает готовность (`READY`)
7. Система назначает свободного курьера (`DELIVERING`)
8. Курьер завершает доставку (`DELIVERED`)

---

## 🛠️ Технологии

| Технология      | Версия                         |
|:----------------|:-------------------------------|
| Java            | 21 (Amazon Corretto, в Docker) |
| Spring Boot     | 4.1.0                          |
| Spring Data JPA | 4.1.0                          |
| Spring Security | 7.1.0                          |
| PostgreSQL      | 16 (Docker)                    |
| H2 (тесты)      | 2.4.240                        |
| Maven           | 4.0.0                          |
| Docker          | 4.82.0                         |
| JUnit 5         | 6.0.3                          |
| Mockito         | 5.23.0                         |
| JJWT (JWT)      | 0.11.5                         |
| Hibernate       | 7.4.1.Final                    |
| Jackson         | 2.18.0                         |

---

## 🧱 Архитектура

### Модули

| Модуль        | Описание                            |
|:--------------|:------------------------------------|
| `common`      | Базовые классы (пагинация, мапперы) |
| `deliveries`  | Доставка и курьеры                  |
| `exception`   | Централизованная обработка ошибок   |
| `orders`      | Заказы и позиции заказов            |
| `payments`    | Платежи (симуляция банка)           |
| `restaurants` | Рестораны и блюда                   |
| `security`    | JWT-аутентификация                  |
| `users`       | Пользователи, роли, регистрация     |

### Сущности

1. `User`
2. `Role`
3. `Restaurant`
4. `Dish`
5. `Order`
6. `OrderItem`
7. `Payment`
8. `Delivery`
9. `Courier`

### Статусная модель заказа

```
PENDING → CONFIRMED → COOKING → READY → DELIVERING → DELIVERED
↘ CANCELED
```

---

## 🚀 Быстрый старт (Docker Compose) — рекомендуется

### 1. Клонировать репозиторий

```bash
git clone https://github.com/alexpetunin-cloud/delivery-service.git
cd delivery-service
```

### 2. Запустить всё одной командой

```bash
docker compose up -d
```

Приложение будет доступно по адресу: `http://localhost:8080`

### 3. Остановить приложение

```bash
docker compose down
```

### 4. Пересобрать после изменений в коде

```bash
docker compose up -d --build
```

---

## 🧪 Запуск без Docker (локально)

### 1. Запустить PostgreSQL через Docker

```bash
docker run --name postgres-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=delivery_db -p 5432:5432 -d postgres
```

### 2. Настроить `application.properties`

```properties
# Подключение к БД
spring.datasource.url=jdbc:postgresql://localhost:5432/delivery_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Hibernate (JPA)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000

# Логирование (опционально)
logging.level.com.petunincloud.delivery.service=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### 3. Запустить приложение

```bash
mvn spring-boot:run
```

Или через Maven Wrapper:

```bash
./mvnw spring-boot:run
```

---

## 📬 Примеры запросов

### Регистрация

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "client@mail.com",
  "password": "123456",
  "name": "Александр",
  "phone": "+79991234567",
  "address": "ул. Пушкина 10"
}
```

### Логин

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "client@mail.com",
  "password": "123456"
}
```

Ответ:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Создание заказа

```http
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "client@mail.com",
  "restaurantId": 1,
  "items": [
    {"dishId": 1, "quantity": 2}
  ]
}
```

---

## 🧪 Тесты

### 📋 Настройка для тестов (`application-test.properties`)

Для запуска тестов используется **H2 (in-memory)** — быстрая база данных, которая не требует установки.

Создайте файл `src/test/resources/application-test.properties`:

```properties
# H2 (in-memory)
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT (для тестов)
jwt.secret=test-secret-key-for-tests
jwt.expiration=86400000

# Логирование
logging.level.root=INFO
logging.level.com.petunincloud.delivery.service=DEBUG
logging.level.org.hibernate.SQL=INFO
```

---

### 🧪 Запуск тестов

```bash
./mvnw test
```

или

```bash
mvn test
```

Покрыты:
- Все сервисы
- Все контроллеры
- Все репозитории
- Сквозной E2E-тест

---

## 📂 Структура проекта

```
src/
├── main/
│   ├── java/
│   │   └── com.petunincloud.delivery.service/
│   │       ├── common/
│   │       ├── deliveries/
│   │       │   ├── courier/
│   │       │   └── delivery/
│   │       ├── exception/
│   │       ├── orders/
│   │       │   ├── order/
│   │       │   └── orderItem/
│   │       ├── payments/
│   │       ├── restaurants/
│   │       │   ├── dish/
│   │       │   └── restaurant/
│   │       ├── security/
│   │       ├── users/
│   │       └── DeliveryServiceApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/
    │   └── com.petunincloud.delivery.service/
    │       ├── deliveries/
    │       │   ├── courier/
    │       │   └── delivery/
    │       ├── orders/
    │       │   ├── order/
    │       │   └── orderItem/
    │       ├── payments/
    │       ├── restaurants/
    │       │   ├── dish/
    │       │   └── restaurant/
    │       ├── security/
    │       ├── users/
    │       ├── E2ETest.java
    │       └── TestSecurityConfig
    │    
    └── resources/
        └── application-test.properties
```

---

## 👨‍💻 Автор

**Петунин Александр**  
[GitHub](https://github.com/alexpetunin-cloud)

---

## 📄 Лицензия

Этот проект создан в учебных целях.