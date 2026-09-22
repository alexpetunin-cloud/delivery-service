import { Link } from "react-router-dom";

export default function OrderSuccessPage() {
    return (
        <main className="page">
            <div className="success-state">
                <div className="success-icon">
                    ✓
                </div>

                <h1>Заказ оформлен!</h1>

                <p>
                    Спасибо за заказ.
                    <br />
                    Ресторан уже получил информацию.
                </p>

                <div className="success-actions">
                    <Link
                        to="/orders"
                        className="primary-button"
                    >
                        Посмотреть заказы
                    </Link>

                    <Link
                        to="/restaurants"
                        className="secondary-button"
                    >
                        Вернуться к ресторанам
                    </Link>
                </div>
            </div>
        </main>
    );
}