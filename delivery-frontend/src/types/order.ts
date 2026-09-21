export type OrderStatus =
    | "PENDING"
    | "CONFIRMED"
    | "COOKING"
    | "READY"
    | "DELIVERING"
    | "DELIVERED"
    | "CANCELED";

export interface OrderItemRequest {
    dishId: number;
    quantity: number;
}

export interface OrderRequest {
    email: string;
    restaurantId: number;
    items: OrderItemRequest[];
}

export interface OrderItemResponse {
    dishId: number;
    dishName: string;
    quantity: number;
    price: number;
}

export interface OrderResponse {
    id: number;
    userId: number;
    restaurantId: number;
    restaurantName: string;
    dateTime: string;
    status: OrderStatus;
    totalPrice: number;
    items: OrderItemResponse[];
}