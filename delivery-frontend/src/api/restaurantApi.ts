import { api } from "./axios";
import type { RestaurantResponse } from "../types/restaurant";

export const getRestaurants = async (): Promise<RestaurantResponse[]> => {
    const response = await api.get<RestaurantResponse[]>(
        "/api/restaurants"
    );

    return response.data;
};

export const getRestaurantById = async (
    id: number
): Promise<RestaurantResponse> => {
    const response = await api.get<RestaurantResponse>(
        `/api/restaurants/${id}`
    );

    return response.data;
};