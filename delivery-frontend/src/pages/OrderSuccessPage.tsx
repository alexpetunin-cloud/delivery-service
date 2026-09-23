import { Link } from "react-router-dom";

export default function OrderSuccessPage() {
    return (
        <main className="page">
            <section className="order-success">
                <div className="order-success-icon">
                    ✓
                </div>

                <div className="order-success-content">
                    <div className="order-success-text">
                        <h1>Заказ оформлен</h1>

                        <p>
                            Ресторан получил ваш заказ и скоро
                            начнёт его готовить.
                        </p>

                        <div className="order-success-status">
                            <span className="order-success-dot" />
                            <span>Заказ принят</span>
                        </div>
                    </div>

                    <div className="order-success-actions">
                        <Link
                            to="/orders"
                            className="primary-button"
                        >
                            Мои заказы
                        </Link>

                        <Link
                            to="/restaurants"
                            className="secondary-button"
                        >
                            Рестораны
                        </Link>
                    </div>
                </div>
            </section>
        </main>
    );
}