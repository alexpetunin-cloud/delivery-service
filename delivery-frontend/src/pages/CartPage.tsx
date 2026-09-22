import { createOrder } from "../api/orderApi";
import { getEmail } from "../services/authService";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";

export default function CartPage() {
    const navigate = useNavigate();

    const { cart, removeItem, clearCart } = useCart();

    if (!cart || cart.items.length === 0) {
        return (
            <main className="page">
                <div className="empty-state">
                    <div className="empty-icon">🛒</div>

                    <h1>Корзина пуста</h1>

                    <p>
                        Добавьте блюда из ресторана,
                        чтобы оформить заказ
                    </p>

                    <Link
                        to="/restaurants"
                        className="primary-button"
                    >
                        Выбрать ресторан
                    </Link>
                </div>
            </main>
        );
    }

    const total = cart.items.reduce(
        (sum, item) =>
            sum + item.price * item.quantity,
        0
    );

    const handleCreateOrder = async () => {
       const email = getEmail();

       if (!email) {
           console.error("User email not found");
           return;
       }

       try {
           await createOrder({
               email,
               restaurantId: cart.restaurantId,
               items: cart.items.map((item) => ({
                   dishId: item.dishId,
                   quantity: item.quantity,
               })),
           });

           clearCart();

           navigate("/order-success");
       } catch (error) {
           console.error(
               "ORDER CREATION ERROR:",
               error
           );
       }
   };

    return (
        <main className="page">
            <div className="page-header">
                <h1>Корзина</h1>

                <p>{cart.restaurantName}</p>
            </div>

            <div className="cart-layout">
                <section className="cart-items">
                    {cart.items.map((item) => (
                        <article
                            className="cart-item"
                            key={item.dishId}
                        >
                            <div className="cart-item-icon">
                                🍕
                            </div>

                            <div className="cart-item-info">
                                <h3>{item.dishName}</h3>

                                <p>
                                    {item.quantity} ×{" "}
                                    {item.price} ₽
                                </p>
                            </div>

                            <strong className="cart-item-price">
                                {item.quantity *
                                    item.price}{" "}
                                ₽
                            </strong>

                            <button
                                className="remove-button"
                                onClick={() =>
                                    removeItem(
                                        item.dishId
                                    )
                                }
                            >
                                ×
                            </button>
                        </article>
                    ))}
                </section>

                <aside className="cart-summary">
                    <h2>Ваш заказ</h2>

                    <div className="summary-row">
                        <span>Блюда</span>
                        <span>{total} ₽</span>
                    </div>

                    <div className="summary-row">
                        <span>Доставка</span>
                        <span>Бесплатно</span>
                    </div>

                    <div className="summary-divider" />

                    <div className="summary-total">
                        <span>Итого</span>
                        <strong>{total} ₽</strong>
                    </div>

                    <button
                        className="checkout-button"
                        onClick={handleCreateOrder}
                    >
                        Оформить заказ
                    </button>
                </aside>
            </div>
        </main>
    );
}