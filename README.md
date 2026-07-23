# 🚚 Delivery Service API

Сервис доставки еды с полноценной бизнес-логикой: заказы, оплата, рестораны, доставка и курьеры.

---

## 📌 О проекте

Это **монолитное Spring Boot приложение**, которое моделирует работу сервиса доставки еды.
Реализован полный цикл заказа:

1. Пользователь выбирает блюда из меню ресторана
2. Создаёт заказ (`PENDING`)
3. Инициализирует и обрабатывает платёж
4. После успешной оплаты заказ подтверждается (`CONFIRMED`)
5. Ресторан начинает готовку (`COOKING`) и отмечает готовность (`READY`)
6. Система назначает свободного курьера (`DELIVERING`)
7. Курьер завершает доставку (`DELIVERED`)

---

## 🛠️ Технологии

| Технология      | Версия      |
|:----------------|:------------|
| Java            | 26          |
| Spring Boot     | 4.1.0       |
| Spring Data JPA | 4.1.0       |
| Spring Security | 7.1.0       |
| PostgreSQL      | 18.4        |
| H2 (тесты)      | 2.4.240     |
| Maven           | 4.0.0       |
| Docker          | 4.82.0      |
| JUnit 5         | 6.0.3       |
| Mockito         | 5.23.0      |
| JJWT (JWT)      | 0.11.5      |
| Hibernate       | 7.4.1.Final |
| Jackson         | 2.18.0      |

---

## 🧱 Архитектура

### Модули

| Модуль        | Описание                            |
|:--------------|:------------------------------------|
| `users`       | Пользователи, роли, регистрация     |
| `restaurants` | Рестораны и блюда                   |
| `orders`      | Заказы и позиции заказов            |
| `payments`    | Платежи (симуляция банка)           |
| `deliveries`  | Доставка и курьеры                  |
| `security`    | JWT-аутентификация                  |
| `common`      | Базовые классы (пагинация, мапперы) |
| `exception`   | Централизованная обработка ошибок   |

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

## 🚀 Как запустить

### 1. Клонировать репозиторий

```bash
git clone https://github.com/alexpetunin-cloud/delivery-service.git
cd delivery-service
```

### 2. Запустить PostgreSQL через Docker

```bash
docker run --name postgres-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=delivery_db -p 5432:5432 -d postgres
```

### 3. Настроить `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/delivery_db
spring.datasource.username=postgres
spring.datasource.password=postgres

jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000
```

### 4. Запустить приложение

```bash
mvn spring-boot:run
```

ИЛИ ЕСЛИ ПЕРВЫЙ СПОСОБ НЕ РАБОТАЕТ

```bash
./mvnw spring-boot:run
```

Приложение запустится на порту `8080`.

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

## 🧪 Тесты

```bash
mvn test
```

ИЛИ ЕСЛИ ПЕРВЫЙ СПОСОБ НЕ РАБОТАЕТ

```bash
./mvnw test
```

Покрыты:
- OrderController, OrderRepository, OrderService, OrderItemService, CourierService, DeliveryService, PaymentService
- Сквозной E2E-тест

---

## 📂 Структура проекта

```
src/
├── main/
│   └── java/
│       ├── com.petunincloud.delivery.service/
│       │   ├── common/
│       │   ├── deliveries/
│       │   │   ├── courier/
│       │   │   └── delivery/
│       │   ├── exception/
│       │   ├── orders/
│       │   │   ├── order/
│       │   │   └── orderItem/
│       │   ├── payments/
│       │   ├── restaurants/
│       │   │   ├── dish/
│       │   │   └── restaurant/
│       │   ├── security/
│       │   ├── users/
│       │   └── DeliveryServiceApplication.java
│       └── resources/
│           └── application.properties
└── test/
    ├── java/
    │   └── com.petunincloud.delivery.service/
    │       ├── orders/
    │       │   ├── order/
    │       │   └── orderItem/
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