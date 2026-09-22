import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import {
    getToken,
    removeEmail,
    removeToken,
} from "../services/authService";

export default function Navbar() {
    const navigate = useNavigate();
    const { cart, clearCart } = useCart();

    const handleLogout = () => {
        removeToken();
        removeEmail();
        clearCart();

        navigate("/login");
    };

    const cartItemsCount =
        cart?.items.reduce(
            (sum, item) => sum + item.quantity,
            0
        ) ?? 0;

    if (!getToken()) {
        return null;
    }

    return (
        <header className="navbar">
            <div className="navbar-content">
                <Link
                    to="/restaurants"
                    className="navbar-logo"
                >
                    🚚 Delivery
                </Link>

                <nav className="navbar-links">
                    <Link to="/restaurants">
                        Рестораны
                    </Link>

                    <Link to="/cart">
                        Корзина ({cartItemsCount})
                    </Link>

                    <Link to="/orders">
                        Заказы
                    </Link>

                    <button
                        className="logout-button"
                        onClick={handleLogout}
                    >
                        Выйти
                    </button>
                </nav>
            </div>
        </header>
    );
}