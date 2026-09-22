import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getRestaurantById } from "../api/restaurantApi";
import type { RestaurantResponse } from "../types/restaurant";
import { useCart } from "../context/CartContext";

export default function RestaurantPage() {
    const { id } = useParams<{ id: string }>();
    const { addItem } = useCart();

    const [restaurant, setRestaurant] =
        useState<RestaurantResponse | null>(null);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const loadRestaurant = async () => {
            if (!id) {
                setError("Restaurant ID is missing");
                setLoading(false);
                return;
            }

            try {
                const data = await getRestaurantById(Number(id));
                setRestaurant(data);
            } catch (error) {
                console.error(error);
                setError("Не удалось загрузить ресторан");
            } finally {
                setLoading(false);
            }
        };

        loadRestaurant();
    }, [id]);

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
                <p>{error}</p>
            </main>
        );
    }

    if (!restaurant) {
        return (
            <main className="page">
                <p>Ресторан не найден</p>
            </main>
        );
    }

    return (
        <main className="page">
            <section className="restaurant-header">
                <div>
                    <h1>{restaurant.name}</h1>

                    <p className="restaurant-address">
                        {restaurant.address}
                    </p>
                </div>
            </section>

            <section className="menu-section">
                <div className="section-header">
                    <h2>Меню</h2>

                    <span>
                        {restaurant.menu.length} блюд
                    </span>
                </div>

                <div className="menu-grid">
                    {restaurant.menu.map((dish) => (
                        <article
                            className="dish-card"
                            key={dish.id}
                        >
                            <div className="dish-image">
                                🍕
                            </div>

                            <div className="dish-content">
                                <h3>{dish.name}</h3>

                                <div className="dish-footer">
                                    <strong>
                                        {dish.price} ₽
                                    </strong>

                                    <button
                                        className="add-button"
                                        onClick={() =>
                                            addItem(
                                                restaurant.id,
                                                restaurant.name,
                                                {
                                                    dishId: dish.id,
                                                    dishName: dish.name,
                                                    price: dish.price,
                                                    quantity: 1,
                                                }
                                            )
                                        }
                                    >
                                        +
                                    </button>
                                </div>
                            </div>
                        </article>
                    ))}
                </div>
            </section>
        </main>
    );
}