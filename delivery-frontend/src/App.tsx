import { BrowserRouter, Routes, Route } from "react-router-dom";
import { CartProvider } from "./context/CartContext";
import LoginPage from "./pages/LoginPage";
import RestaurantsPage from "./pages/RestaurantsPage";
import RestaurantPage from "./pages/RestaurantPage";
import CartPage from "./pages/CartPage";
import OrdersPage from "./pages/OrdersPage";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <BrowserRouter>
            <CartProvider>
                <Navbar />

                <Routes>
                    <Route path="/login" element={<LoginPage />} />
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
                </Routes>
            </CartProvider>
        </BrowserRouter>
    );
}

export default App;