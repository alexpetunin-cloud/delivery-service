import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
    getOrderById,
    cancelOrder,
} from "../api/orderApi";
import type { OrderResponse } from "../types/order";
import {
    initiatePayment,
    processPayment,
} from "../api/paymentApi";

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

export default function OrderPage() {
    const { id } = useParams<{ id: string }>();

    const [order, setOrder] =
        useState<OrderResponse | null>(null);

    const [canceling, setCanceling] = useState(false);
    const [paying, setPaying] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const loadOrder = async () => {
            if (!id) {
                setError("ID заказа отсутствует");
                setLoading(false);
                return;
            }

            try {
                const data = await getOrderById(Number(id));
                setOrder(data);
            } catch (error) {
                console.error(error);
                setError("Не удалось загрузить заказ");
            } finally {
                setLoading(false);
            }
        };

        loadOrder();
    }, [id]);

    const handleCancelOrder = async () => {
        if (!order) {
            return;
        }

        const confirmed = window.confirm(
            "Вы действительно хотите отменить заказ?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setCanceling(true);

            const updatedOrder = await cancelOrder(order.id);

            setOrder(updatedOrder);
        } catch (error) {
            console.error(error);
            setError("Не удалось отменить заказ");
        } finally {
            setCanceling(false);
        }
    };

    const handlePayment = async () => {
        if (!order) {
            return;
        }

        try {
            setPaying(true);
            setError(null);

            const payment = await initiatePayment({
                orderId: order.id,
            });

            const processedPayment = await processPayment(payment.id);

            if (processedPayment.status === "SUCCESS") {
                const updatedOrder = await getOrderById(order.id);
                setOrder(updatedOrder);
            }
        } catch (error) {
            console.error(error);
            setError("Не удалось выполнить оплату");
        } finally {
            setPaying(false);
        }
    };

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
                <div className="empty-state">
                    <div className="empty-icon">⚠️</div>

                    <h1>{error}</h1>

                    <Link
                        to="/orders"
                        className="primary-button"
                    >
                        Вернуться к заказам
                    </Link>
                </div>
            </main>
        );
    }

    if (!order) {
        return (
            <main className="page">
                <div className="empty-state">
                    <h1>Заказ не найден</h1>

                    <Link
                        to="/orders"
                        className="primary-button"
                    >
                        Вернуться к заказам
                    </Link>
                </div>
            </main>
        );
    }

return (
    <main className="page">
        <div className="order-page">
            <Link
                to="/orders"
                className="back-link"
            >
                ← Все заказы
            </Link>

            <div className="order-page-header">
                <div>
                    <h1>{order.restaurantName}</h1>

                    <p>
                        {new Date(order.dateTime).toLocaleDateString(
                            "ru-RU"
                        )}{" "}
                        {new Date(order.dateTime).toLocaleTimeString(
                            "ru-RU",
                            {
                                hour: "2-digit",
                                minute: "2-digit",
                                hour12: false,
                            }
                        )}
                    </p>
                </div>

                <span
                    className={`status-badge status-${order.status.toLowerCase()}`}
                >
                    {getStatusText(order.status)}
                </span>
            </div>

            <section className="order-restaurant">
                <div className="order-restaurant-icon">
                    🍴
                </div>

                <div>
                    <span>Ресторан</span>
                    <strong>{order.restaurantName}</strong>
                </div>
            </section>

            <section className="order-items-card">
                <h2>Состав заказа</h2>

                <div className="order-items">
                    {order.items.map((item) => (
                        <div
                            className="order-item"
                            key={item.dishId}
                        >
                            <div className="order-item-image">
                                🍕
                            </div>

                            <div className="order-item-main">
                                <strong>{item.dishName}</strong>

                                <span>
                                    {item.quantity} × {item.price} ₽
                                </span>
                            </div>

                            <strong className="order-item-price">
                                {item.quantity * item.price} ₽
                            </strong>
                        </div>
                    ))}
                </div>

                <div className="order-total">
                    <span>Итого</span>
                    <strong>{order.totalPrice} ₽</strong>
                </div>
            </section>

            <div className="order-actions">
                {order.status === "PENDING" && (
                    <button
                        className="primary-button"
                        onClick={handlePayment}
                        disabled={paying}
                    >
                        {paying ? "Оплачиваем..." : "Оплатить"}
                    </button>
                )}

                {order.status !== "CANCELED" &&
                    order.status !== "DELIVERED" && (
                        <button
                            className="cancel-order-button"
                            onClick={handleCancelOrder}
                            disabled={canceling}
                        >
                            {canceling
                                ? "Отменяем..."
                                : "Отменить заказ"}
                        </button>
                    )}
            </div>
        </div>
    </main>
    );
}