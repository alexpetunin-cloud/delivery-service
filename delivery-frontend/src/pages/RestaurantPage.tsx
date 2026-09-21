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
                setError("Failed to load restaurant");
            } finally {
                setLoading(false);
            }
        };

        loadRestaurant();
    }, [id]);

    if (loading) {
        return <h1>Loading...</h1>;
    }

    if (error) {
        return <h1>{error}</h1>;
    }

    if (!restaurant) {
        return <h1>Restaurant not found</h1>;
    }

    return (
        <div>
            <h1>{restaurant.name}</h1>

            <p>{restaurant.address}</p>

            <h2>Menu</h2>

            {restaurant.menu.map((dish) => (
                <div key={dish.id}>
                    <h3>{dish.name}</h3>

                    <p>{dish.price} ₽</p>

                    <button
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
                        В корзину
                    </button>
                </div>
            ))}
        </div>
    );
}