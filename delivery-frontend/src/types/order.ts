export type OrderStatus =
    | "PENDING"
    | "CONFIRMED"
    | "COOKING"
    | "READY"
    | "DELIVERING"
    | "DELIVERED"
    | "CANCELED";

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

export interface OrderItemResponse {
    [key: string]: unknown;
}