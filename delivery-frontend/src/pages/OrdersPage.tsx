import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getOrders } from "../api/orderApi";
import type { OrderResponse } from "../types/order";

const getStatusText = (
    status: OrderResponse["status"]
): string => {
    switch (status) {
        case "PENDING":
            return "Ожидает подтверждения";
        case "CONFIRMED":
            return "Подтверждён";
        case "COOKING":
            return "Готовится";
        case "READY":
            return "Готов к доставке";
        case "DELIVERING":
            return "Доставляется";
        case "DELIVERED":
            return "Доставлен";
        case "CANCELED":
            return "Отменён";
        default:
            return status;
    }
};

export default function OrdersPage() {
    const [orders, setOrders] = useState<OrderResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const loadOrders = async () => {
            try {
                const data = await getOrders();

                setOrders(data);
            } catch (error) {
                console.error(error);
                setError("Не удалось загрузить заказы");
            } finally {
                setLoading(false);
            }
        };

        loadOrders();
    }, []);

    if (loading) {
        return (
            <main className="page">
                <p>Загрузка...</p>
            </main>
        );
    }

    if (error) {
        return (
            <main className="page">
                <p>{error}</p>
            </main>
        );
    }

    if (orders.length === 0) {
        return (
            <main className="page">
                <div className="empty-state">
                    <div className="empty-icon">
                        📦
                    </div>

                    <h1>Заказов пока нет</h1>

                    <p>
                        Здесь появятся ваши заказы
                    </p>
                </div>
            </main>
        );
    }

    return (
        <main className="page">
            <section className="page-header">
                <h1>Мои заказы</h1>

                <p>
                    История ваших заказов
                </p>
            </section>

            <section className="orders-list">
                {orders.map((order) => (
                    <Link
                        to={`/orders/${order.id}`}
                        className="order-card"
                        key={order.id}
                    >
                        <div className="order-header">
                            <div>
                                <span className="order-number">
                                    Заказ #{order.id}
                                </span>

                                <h2>
                                    {order.restaurantName}
                                </h2>

                                <p>
                                    {new Date(
                                        order.dateTime
                                    ).toLocaleString("ru-RU")}
                                </p>
                            </div>

                            <span
                                className={`status-badge status-${order.status.toLowerCase()}`}
                            >
                                {getStatusText(
                                    order.status
                                )}
                            </span>
                        </div>

                        <div className="order-items">
                            {order.items.map((item) => (
                                <div
                                    className="order-item"
                                    key={item.dishId}
                                >
                                    <div className="order-item-icon">
                                        🍕
                                    </div>

                                    <div className="order-item-info">
                                        <strong>
                                            {item.dishName}
                                        </strong>

                                        <span>
                                            {item.quantity} ×{" "}
                                            {item.price} ₽
                                        </span>
                                    </div>

                                    <strong>
                                        {item.quantity *
                                            item.price}{" "}
                                        ₽
                                    </strong>
                                </div>
                            ))}
                        </div>

                        <div className="order-footer">
                            <span>Итого</span>

                            <strong>
                                {order.totalPrice} ₽
                            </strong>
                        </div>
                    </Link>
                ))}
            </section>
        </main>
    );
}