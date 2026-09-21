import { useEffect, useState } from "react";
import { getRestaurants } from "../api/restaurantApi";
import type { RestaurantResponse } from "../types/restaurant";
import { Link } from "react-router-dom";

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
                setError("Failed to load restaurants");
            } finally {
                setLoading(false);
            }
        };

        loadRestaurants();
    }, []);

    if (loading) {
        return <h1>Loading...</h1>;
    }

    if (error) {
        return <h1>{error}</h1>;
    }

    return (
        <div>
            <h1>Restaurants</h1>

            {restaurants.map((restaurant) => (
                <div key={restaurant.id}>
                    <h2>
                        <Link to={`/restaurants/${restaurant.id}`}>
                            {restaurant.name}
                        </Link>
                    </h2>

                    <p>{restaurant.address}</p>
                </div>
            ))}
        </div>
    );
}