import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getOrders } from "../api/orderApi";
import type { OrderResponse } from "../types/order";

const activeStatuses = [
    "PENDING",
    "CONFIRMED",
    "COOKING",
    "READY",
    "DELIVERING",
];

const getStatusText = (status: string) => {
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

    const activeOrders = orders.filter((order) =>
        activeStatuses.includes(order.status)
    );

    const historyOrders = orders.filter(
        (order) => !activeStatuses.includes(order.status)
    );

    return (
        <main className="page">
            <section className="page-header">
                <h1>Мои заказы</h1>
                <p>Здесь вы можете посмотреть текущие и прошлые заказы</p>
            </section>

            {activeOrders.length > 0 && (
                <section className="orders-section">
                    <div className="section-header">
                        <h2>Текущий заказ</h2>
                    </div>

                    <div className="orders-list orders-list-active">
                        {activeOrders.map((order) => (
                            <Link
                                key={order.id}
                                to={`/orders/${order.id}`}
                                className="order-card order-card-active"
                            >
                                <div className="order-card-header">
                                    <div>
                                        <h3>{order.restaurantName}</h3>

                                        <p>
                                            {order.items
                                                .map(
                                                    (item) =>
                                                        `${item.dishName} × ${item.quantity}`
                                                )
                                                .join(", ")}
                                        </p>
                                    </div>

                                    <span
                                        className={`order-status status-${order.status.toLowerCase()}`}
                                    >
                                        {getStatusText(order.status)}
                                    </span>
                                </div>

                                <div className="order-card-footer">
                                    <strong>{order.totalPrice} ₽</strong>

                                    <span className="order-arrow">
                                        →
                                    </span>
                                </div>
                            </Link>
                        ))}
                    </div>
                </section>
            )}

            {historyOrders.length > 0 && (
                <section className="orders-section">
                    <div className="section-header">
                        <h2>История заказов</h2>
                    </div>

                    <div className="orders-list">
                        {historyOrders.map((order) => (
                            <Link
                                key={order.id}
                                to={`/orders/${order.id}`}
                                className="order-card"
                            >
                                <div className="order-card-header">
                                    <div>
                                        <h3>{order.restaurantName}</h3>

                                        <p>
                                            {order.items
                                                .map(
                                                    (item) =>
                                                        `${item.dishName} × ${item.quantity}`
                                                )
                                                .join(", ")}
                                        </p>
                                    </div>

                                    <span
                                        className={`order-status status-${order.status.toLowerCase()}`}
                                    >
                                        {getStatusText(order.status)}
                                    </span>
                                </div>

                                <div className="order-card-footer">
                                    <span className="order-date">
                                        {new Date(
                                            order.dateTime
                                        ).toLocaleDateString("ru-RU")}
                                    </span>

                                    <strong>{order.totalPrice} ₽</strong>
                                </div>
                            </Link>
                        ))}
                    </div>
                </section>
            )}

            {orders.length === 0 && (
                <div className="empty-state">
                    <div className="empty-icon">📦</div>

                    <h2>Заказов пока нет</h2>

                    <p>
                        Выберите ресторан и сделайте свой первый заказ
                    </p>

                    <Link
                        to="/restaurants"
                        className="primary-button"
                    >
                        Перейти к ресторанам
                    </Link>
                </div>
            )}
        </main>
    );
}