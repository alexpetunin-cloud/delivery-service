import { api } from "./axios";
import type {
    OrderRequest,
    OrderResponse
} from "../types/order";

export const getOrderById = async (
    id: number
): Promise<OrderResponse> => {
    const response = await api.get<OrderResponse>(
        `/api/orders/${id}`
    );

    return response.data;
};

export const createOrder = async (
    data: OrderRequest
): Promise<OrderResponse> => {
    const response = await api.post<OrderResponse>(
        "/api/orders",
        data
    );

    return response.data;
};

export const getOrders = async (): Promise<OrderResponse[]> => {
    const response = await api.get<OrderResponse[]>(
        "/api/orders"
    );

    return response.data;
};