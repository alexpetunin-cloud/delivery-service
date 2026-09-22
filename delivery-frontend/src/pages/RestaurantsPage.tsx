import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getRestaurants } from "../api/restaurantApi";
import type { RestaurantResponse } from "../types/restaurant";

export default function RestaurantsPage() {
    const [restaurants, setRestaurants] = useState<RestaurantResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const loadRestaurants = async () => {
            try {
                const data = await getRestaurants();

                setRestaurants(data);
            } catch (error) {
                console.error(error);
                setError("Не удалось загрузить рестораны");
            } finally {
                setLoading(false);
            }
        };

        loadRestaurants();
    }, []);

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

    return (
        <main className="page">
            <section className="page-header">
                <h1>Рестораны</h1>
                <p>
                    Выберите ресторан и закажите любимые блюда
                </p>
            </section>

            <section className="restaurant-grid">
                {restaurants.map((restaurant) => (
                    <article
                        className="restaurant-card"
                        key={restaurant.id}
                    >
                        <div className="restaurant-card-content">
                            <div className="restaurant-icon">
                                🍴
                            </div>

                            <h2>{restaurant.name}</h2>

                            <p className="restaurant-address">
                                {restaurant.address}
                            </p>

                            <Link
                                className="restaurant-link"
                                to={`/restaurants/${restaurant.id}`}
                            >
                                Посмотреть меню
                                <span>→</span>
                            </Link>
                        </div>
                    </article>
                ))}
            </section>
        </main>
    );
}