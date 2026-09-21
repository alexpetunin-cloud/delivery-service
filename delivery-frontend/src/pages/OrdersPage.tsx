import { useEffect, useState } from "react";
import { getOrders } from "../api/orderApi";
import type { OrderResponse } from "../types/order";

const getStatusText = (status: OrderResponse["status"]): string => {
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
                setError("Failed to load orders");
            } finally {
                setLoading(false);
            }
        };

        loadOrders();
    }, []);

    if (loading) {
        return <h1>Loading...</h1>;
    }

    if (error) {
        return <h1>{error}</h1>;
    }

    return (
        <div>
            <h1>My Orders</h1>

            {orders.length === 0 ? (
                <p>No orders yet</p>
            ) : (
                orders.map((order) => (
                    <div key={order.id}>
                        <h2>
                            Order #{order.id}
                        </h2>

                        <p>
                            Restaurant: {order.restaurantName}
                        </p>

                        <p>
                            Status: {getStatusText(order.status)}
                        </p>

                        <p>
                            Total: {order.totalPrice} ₽
                        </p>

                        <p>
                            Date: {order.dateTime}
                        </p>

                        <h3>Items</h3>

                        {order.items.map((item) => (
                            <div key={item.dishId}>
                                <p>
                                    {item.dishName} — {item.quantity} × {item.price} ₽
                                </p>

                                <p>
                                    Сумма: {item.quantity * item.price} ₽
                                </p>
                            </div>
                        ))}

                        <hr />
                    </div>
                ))
            )}
        </div>
    );
}