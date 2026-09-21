import {
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";

interface CartItem {
    dishId: number;
    dishName: string;
    price: number;
    quantity: number;
}

interface Cart {
    restaurantId: number;
    restaurantName: string;
    items: CartItem[];
}

interface CartContextType {
    cart: Cart | null;
    addItem: (
        restaurantId: number,
        restaurantName: string,
        item: CartItem
    ) => void;
    removeItem: (dishId: number) => void;
    clearCart: () => void;
}

const CART_KEY = "cart";

const CartContext = createContext<CartContextType | undefined>(
    undefined
);

export function CartProvider({ children }: { children: React.ReactNode }) {
    const [cart, setCart] = useState<Cart | null>(() => {
        const savedCart = localStorage.getItem(CART_KEY);

        if (!savedCart) {
            return null;
        }

        return JSON.parse(savedCart);
    });

    useEffect(() => {
        if (cart === null) {
            localStorage.removeItem(CART_KEY);
            return;
        }

        localStorage.setItem(
            CART_KEY,
            JSON.stringify(cart)
        );
    }, [cart]);

    const addItem = (
        restaurantId: number,
        restaurantName: string,
        item: CartItem
    ) => {
        setCart((currentCart) => {
            if (!currentCart) {
                return {
                    restaurantId,
                    restaurantName,
                    items: [item],
                };
            }

            if (currentCart.restaurantId !== restaurantId) {
                return currentCart;
            }

            const existingItem = currentCart.items.find(
                (cartItem) => cartItem.dishId === item.dishId
            );

            if (existingItem) {
                return {
                    ...currentCart,
                    items: currentCart.items.map((cartItem) =>
                        cartItem.dishId === item.dishId
                            ? {
                                  ...cartItem,
                                  quantity:
                                      cartItem.quantity + item.quantity,
                              }
                            : cartItem
                    ),
                };
            }

            return {
                ...currentCart,
                items: [...currentCart.items, item],
            };
        });
    };

    const removeItem = (dishId: number) => {
        setCart((currentCart) => {
            if (!currentCart) {
                return null;
            }

            const items = currentCart.items.filter(
                (item) => item.dishId !== dishId
            );

            if (items.length === 0) {
                return null;
            }

            return {
                ...currentCart,
                items,
            };
        });
    };

    const clearCart = () => {
        setCart(null);
    };

    return (
        <CartContext.Provider
            value={{
                cart,
                addItem,
                removeItem,
                clearCart,
            }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart(): CartContextType {
    const context = useContext(CartContext);

    if (!context) {
        throw new Error("useCart must be used inside CartProvider");
    }

    return context;
}