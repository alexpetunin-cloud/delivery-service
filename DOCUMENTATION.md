## 🧰 Модуль `common` (Базовые абстракции)

| Класс                | Назначение                                                                  |
|:---------------------|:----------------------------------------------------------------------------|
| **`BaseController`** | Абстрактный контроллер с общим GET-эндпоинтом для пагинации (`getAll`).     |
| **`BaseFilter`**     | Интерфейс для фильтров пагинации (поля `pageSize`, `pageNumber`).           |
| **`BaseMapper`**     | Интерфейс для маппинга `Entity ↔ DTO` (`toDto`, `toEntity`).                |
| **`BaseService`**    | Абстрактный сервис с общей логикой пагинации (`search` + `findWithFilter`). |


## 🚚 Модуль `deliveries` (Доставка и Курьеры)

| Класс                                    | Назначение                                                                                                                                     |
|:-----------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------|
| **`CourierRequest / CourierResponse`**   | DTO для создания/обновления курьера и ответа.                                                                                                  |
| **`CourierController`**                  | Эндпоинт: `POST /api/couriers` - создание курьера.                                                                                             |
| **`CourierEntity`**                      | Курьер (имя, телефон, статус).                                                                                                                 |
| **`CourierMapper`**                      | Маппер `Entity → Response`, `Request → Entity`.                                                                                                |
| **`CourierRepository`**                  | Поиск с пагинацией по `name`, `phone`, `status`. Методы: `findTopByStatus` (первый свободный), `findByPhone` (уникальность).                   |
| **`CourierSearchFilter`**                | Фильтр: `name` (частичный), `phone` (точный), `status` (`AVAILABLE/BUSY/OFFLINE`).                                                             |
| **`CourierService`**                     | Создание курьера (статус `AVAILABLE`). Поиск свободного курьера (`findAvailableCourier`).                                                      |
| **`CourierStatus`**                      | Enum: `AVAILABLE`, `BUSY`, `OFFLINE`.                                                                                                          |
| **`DeliveryRequest / DeliveryResponse`** | DTO для инициализации доставки и ответа.                                                                                                       |
| **`DeliveryController`**                 | Эндпоинты: `POST /assign/{orderId}` - назначение доставки; `PATCH /{deliveryId}/complete` - завершение.                                        |
| **`DeliveryEntity`**                     | Доставка (заказ, курьер, адреса, дата/время, статус).                                                                                          |
| **`DeliveryMapper`**                     | Маппер `Entity → Response`.                                                                                                                    |
| **`DeliveryRepository`**                 | Поиск с пагинацией по `orderId`, `courierId`, `status`, `assignedAt`, `deliveredAt`. Методы: `existsByOrderId`, `findByIdWithOrderAndCourier`. |
| **`DeliverySearchFilter`**               | Фильтр: `orderId`, `courierId`, `status`, `assignedAt`, `deliveredAt`.                                                                         |
| **`DeliveryService`**                    | Назначение курьера на заказ (`READY` → `DELIVERING`). Завершение доставки (`DELIVERING` → `DELIVERED`, курьер → `AVAILABLE`).                  |
| **`DeliveryStatus`**                     | Enum: `ASSIGNED`, `PICKED_UP`, `IN_PROGRESS`, `DELIVERED`, `FAILED`.                                                                           |


## ❌ Модуль `exception` (Обработка ошибок)

| Класс                        | Назначение                                                                                                                           |
|:-----------------------------|:-------------------------------------------------------------------------------------------------------------------------------------|
| **`ErrorResponse`**          | DTO для красивого ответа об ошибке (время, статус, ошибка, сообщение, путь).                                                         |
| **`GlobalExceptionHandler`** | Централизованный перехват всех исключений: `IllegalArgumentException` → `400`, `IllegalStateException` → `409`, `Exception` → `500`. |


## 🛒 Модуль `orders` (Заказы)

| Класс                                      | Назначение                                                                                                                                                                    |
|:-------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`OrderRequest / OrderResponse`**         | DTO для создания заказа (email, ресторан, позиции) и для ответа (со списком позиций).                                                                                         |
| **`OrderController`**                      | Эндпоинты: `POST /api/orders` - создание; `PATCH /{orderId}/cancel` - отмена; `GET /{id}` - получение.                                                                        |
| **`OrderEntity`**                          | Заказ (статус, сумма, пользователь, ресторан, список позиций, дата/время).                                                                                                    |
| **`OrderMapper`**                          | Маппер `Entity → Response`, `Entity → toItemResponse`, `UserEntity → Entity` (с пользователем).                                                                               |
| **`OrderRepository`**                      | Поиск с пагинацией по `userId`, `restaurantId`.                                                                                                                               |
| **`OrderSearchFilter`**                    | Фильтр: `userId`, `restaurantId`.                                                                                                                                             |
| **`OrderService`**                         | Создание заказа (расчёт суммы, установка `PENDING`). Отмена (проверка статуса).                                                                                               |
| **`OrderStatus`**                          | Enum: `PENDING`, `CONFIRMED`, `COOKING`, `READY`, `DELIVERING`, `DELIVERED`, `CANCELED`.                                                                                      |
| **`OrderItemRequest / OrderItemResponse`** | DTO для позиции заказа и для ответа.                                                                                                                                          |
| **`OrderItemController`**                  | Вложенные эндпоинты: `GET /orders/{orderId}/items` - список позиций; `POST` - добавление; `DELETE /{itemId}` - удаление.                                                      |
| **`OrderItemEntity`**                      | Позиция заказа (блюдо, количество, цена на момент заказа).                                                                                                                    |
| **`OrderItemMapper`**                      | Маппер `Entity → Response`, `Request (c заказом) → Entity`.                                                                                                                   |
| **`OrderItemRepository`**                  | Поиск позиций по ID заказа.                                                                                                                                                   |
| **`OrderItemService`**                     | Добавление позиции (проверка владельца и статуса) `addItemToOrder`. Удаление позиции (пересчёт суммы) `removeItemFromOrder`. Получение списка позиций заказа `getOrderItems`. |


## 💳 Модуль `payments` (Платежи)

| Класс                                  | Назначение                                                                                                                 |
|:---------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|
| **`PaymentRequest / PaymentResponse`** | DTO для инициализации платежа и для ответа (со статусом, транзакцией, датами).                                             |
| **`PaymentController`**                | Эндпоинты: `POST /initiate` - инициализация; `POST /{id}/process` - обработка.                                             |
| **`PaymentEntity`**                    | Платёж (сумма, статус, ссылка на заказ и пользователя, транзакция, даты, способ платежа).                                  |
| **`PaymentMapper`**                    | Маппер `Entity → Response`, `UserEntity, OrderEntity → Entity`.                                                            |
| **`PaymentRepository`**                | Поиск с пагинацией по `userId`, `orderId`, `status`, `createdAt`.                                                          |
| **`PaymentSearchFilter`**              | Фильтр: `userId`, `orderId`, `status`, `fromDate`, `toDate`.                                                               |
| **`PaymentService`**                   | Инициализация платежа (статус `PENDING`). Обработка (симуляция банка). При успехе → `confirmOrder` (заказ → `CONFIRMED`).  |
| **`PaymentStatus`**                    | Enum: `PENDING`, `SUCCESS`, `FAILED`, `CANCELED`.                                                                          |


## 🍽️ Модуль `restaurants` (Рестораны и Блюда)

| Класс                                        | Назначение                                                                                                                                                                                      |
|:---------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`DishRequest / DishResponse`**             | DTO для создания блюда и для ответа.                                                                                                                                                            |
| **`DishController`**                         | Эндпоинты: `GET /api/dishes` - список блюд с пагинацией.                                                                                                                                        |
| **`DishEntity`**                             | Блюдо (название, цена, ресторан).                                                                                                                                                               |
| **`DishMapper`**                             | Маппер `Entity → Response`.                                                                                                                                                                     |
| **`DishRepository`**                         | Поиск с пагинацией по `name` (частичный), `restaurantId`.                                                                                                                                       |
| **`DishSearchFilter`**                       | Фильтр: `name` (частичный), `restaurantId`.                                                                                                                                                     |
| **`DishService`**                            | Получение блюда по ID (`getDishById`).                                                                                                                                                          |
| **`RestaurantRequest / RestaurantResponse`** | DTO для создания ресторана и для ответа (со списком блюд).                                                                                                                                      |
| **`RestaurantController`**                   | Эндпоинты: `POST /api/restaurants` - создание; `POST /{restaurantId}/dishes` - добавление блюда; `PATCH /orders/{orderId}/cook` - готовка, `PATCH /orders/{orderId}/ready` - завершить готовку. |
| **`RestaurantEntity`**                       | Ресторан (название, адрес, список блюд `@OneToMany`).                                                                                                                                           |
| **`RestaurantMapper`**                       | Маппер `Entity → Response` (с вложенным списком блюд), `DishEntity → DishResponse`, `Request → Entity`.                                                                                         |
| **`RestaurantRepository`**                   | Поиск с пагинацией по `name` (частичный).                                                                                                                                                       |
| **`RestaurantSearchFilter`**                 | Фильтр: `name`.                                                                                                                                                                                 |
| **`RestaurantService`**                      | Создание ресторана `createRestaurant`. Добавление блюда в ресторан `addDishToRestaurant`. Начать готовку `startCooking`. Завершить, установить статус READY `markAsReady`.                      |


## 🔐 Модуль `security` (Авторизация и JWT)

| Класс                            | Назначение                                                                                                                                                                |
|:---------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`AuthRequest / AuthResponse`** | DTO для логина/регистрации (email, пароль, имя, телефон, адрес) и ответа с токеном.                                                                                       |
| **`AuthController`**             | Эндпоинты: `POST /api/auth/register` - регистрация; `POST /api/auth/login` - логин (выдача JWT).                                                                          |
| **`JwtAuthenticationFilter`**    | Перехватывает каждый запрос. Проверяет `Authorization: Bearer <token>`. Если токен валиден - кладёт пользователя в `SecurityContextHolder`.                               |
| **`JwtUtils`**                   | Генерация и валидация JWT. Извлекает email (`extractUsername`), проверяет срок (`validateToken`, `isTokenExpired`).                                                       |
| **`SecurityConfig`**             | Настройка доступа: `permitAll()` для `/api/auth/**`; `authenticated()` для заказов и платежей; `hasAnyRole()` для ресторанов, курьеров, доставок. Stateless (без сессий). |
| **`SecurityUtils`**              | Получение текущего пользователя из `SecurityContextHolder` (`getCurrentUser`).                                                                                            |
| **`UserDetailsServiceImpl`**     | Загрузка пользователя по email из БД (`loadUserByUsername`). Преобразование в `UserDetails` с ролями.                                                                     |


## 👤 Модуль `users` (Пользователи)

| Класс                                               | Назначение                                                                                                                                                                                   |
|:----------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`UserRequest / UserResponse / UserPatchRequest`** | DTO для создания/обновления пользователя и для ответа. UserPatchRequest как UserRequest только без проверки на null.                                                                         |
| **`DataInitializer`**                               | Заполняет БД ролями (`ROLE_CLIENT/ADMIN/COURIER/RESTAURANT`) и тестовыми пользователями при старте.                                                                                          |
| **`RoleEntity`**                                    | Сущность роли. Поле `name` (уникальное).                                                                                                                                                     |
| **`RoleRepository`**                                | Поиск роли по имени (`findByName`).                                                                                                                                                          |
| **`UserController`**                                | Эндпоинты: `POST /api/users` - создание; `PATCH /{id}` - обновление; `DELETE /{id}` - удаление.                                                                                              |
| **`UserEntity`**                                    | Сущность пользователя (email, пароль, имя, телефон, адрес, роли `@ManyToMany`).                                                                                                              |
| **`UserMapper`**                                    | Маппер `Entity → Response`, `Request → Entity`.                                                                                                                                              |
| **`UserRepository`**                                | Поиск с пагинацией по `name`, `email`, `phone`, `address`. Методы: `findByEmail`, `findByPhone`.                                                                                             |
| **`UserSearchFilter`**                              | Фильтр: `name` (частичный), `email` (точный), `phone` (точный), `address` (частичный).                                                                                                       |
| **`UserService`**                                   | Создание пользователя (проверка уникальности email/phone) `createUser`. Поиск по email/phone `findByEmail` `findByPhone`. Обновление данных `updateUser`. Удаление пользователя `deleteUser` |

---
`DeliveryServiceApplication` - запускающий класс, находится в `com.petunincloud.delivery.service`