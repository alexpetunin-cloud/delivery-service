import { CartProvider } from "./context/CartContext";
import LoginPage from "./pages/LoginPage";
import RestaurantsPage from "./pages/RestaurantsPage";
import RestaurantPage from "./pages/RestaurantPage";
import CartPage from "./pages/CartPage";
import OrdersPage from "./pages/OrdersPage";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";
import {
    BrowserRouter,
    Routes,
    Route,
    Navigate,
} from "react-router-dom";
import HomeRedirect from "./components/HomeRedirect";
import OrderSuccessPage from "./pages/OrderSuccessPage";
import OrderPage from "./pages/OrderPage";

function App() {
    return (
        <BrowserRouter>
            <CartProvider>
                <Navbar />

                <Routes>
                    <Route
                        path="/"
                        element={<HomeRedirect />}
                    />

                    <Route
                        path="/login"
                        element={<LoginPage />}
                    />

                    <Route
                        path="/restaurants"
                        element={
                            <ProtectedRoute>
                                <RestaurantsPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/restaurants/:id"
                        element={
                            <ProtectedRoute>
                                <RestaurantPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/cart"
                        element={
                            <ProtectedRoute>
                                <CartPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/orders"
                        element={
                            <ProtectedRoute>
                                <OrdersPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/order-success"
                        element={
                            <ProtectedRoute>
                                <OrderSuccessPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/orders/:id"
                        element={
                            <ProtectedRoute>
                                <OrderPage />
                            </ProtectedRoute>
                        }
                    />
                </Routes>
            </CartProvider>
        </BrowserRouter>
    );
}

export default App;