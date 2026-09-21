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
        <nav>
            <Link to="/restaurants">
                Restaurants
            </Link>

            {" | "}

            <Link to="/cart">
                Cart ({cartItemsCount})
            </Link>

            {" | "}

            <Link to="/orders">
                Orders
            </Link>

            {" | "}

            <button onClick={handleLogout}>
                Logout
            </button>
        </nav>
    );
}