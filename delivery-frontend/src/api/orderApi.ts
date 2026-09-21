import { api } from "./axios";
import type { OrderResponse } from "../types/order";

export const getOrderById = async (
    id: number
): Promise<OrderResponse> => {
    const response = await api.get<OrderResponse>(
        `/api/orders/${id}`
    );

    return response.data;
};