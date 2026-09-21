export interface DishResponse {
    id: number;
    name: string;
    price: number;
}

export interface RestaurantResponse {
    id: number;
    name: string;
    address: string;
    menu: DishResponse[];
}