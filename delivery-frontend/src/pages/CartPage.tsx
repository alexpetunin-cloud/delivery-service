import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { createOrder } from "../api/orderApi";
import { getEmail } from "../services/authService";

export default function CartPage() {
    const { cart, removeItem, clearCart } = useCart();

    const handleCreateOrder = async () => {
        if (!cart) {
            return;
        }

        const email = getEmail();

        if (!email) {
            console.error("User email not found");
            return;
        }

        try {
            const order = await createOrder({
                email,
                restaurantId: cart.restaurantId,
                items: cart.items.map((item) => ({
                    dishId: item.dishId,
                    quantity: item.quantity,
                })),
            });

            console.log("ORDER CREATED:", order);

            clearCart();
        } catch (error) {
            console.error("ORDER CREATION ERROR:", error);
        }
    };

    if (!cart || cart.items.length === 0) {
        return (
            <div>
                <h1>Корзина</h1>
                <p>Корзина пуста</p>

                <Link to="/restaurants">
                    Вернуться к ресторанам
                </Link>
            </div>
        );
    }

    const total = cart.items.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
    );

    return (
        <div>
            <h1>Корзина</h1>

            <h2>{cart.restaurantName}</h2>

            {cart.items.map((item) => (
                <div key={item.dishId}>
                    <h3>{item.dishName}</h3>

                    <p>
                        {item.price} ₽ × {item.quantity}
                    </p>

                    <p>
                        Сумма:{" "}
                        {item.price * item.quantity} ₽
                    </p>

                    <button
                        onClick={() => removeItem(item.dishId)}
                    >
                        Удалить
                    </button>
                </div>
            ))}

            <hr />

            <h2>Итого: {total} ₽</h2>

            <button onClick={handleCreateOrder}>
                Оформить заказ
            </button>

            <button onClick={clearCart}>
                Очистить корзину
            </button>

            <br />
            <br />

            <Link to="/restaurants">
                Вернуться к ресторанам
            </Link>
        </div>
    );
}