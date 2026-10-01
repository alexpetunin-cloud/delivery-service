const restaurantImages = import.meta.glob(
    '../assets/images/restaurants/*.{png,jpg,jpeg,webp,svg}',
    { eager: true }
);

const dishImages = import.meta.glob(
    '../assets/images/dishes/**/*.{png,jpg,jpeg,webp,svg}',
    { eager: true }
);

const restaurantPlaceholder = new URL(
    '../assets/images/restaurants/placeholder.png',
    import.meta.url
).href;

const dishPlaceholder = new URL(
    '../assets/images/dishes/placeholder.png',
    import.meta.url
).href;

const RESTAURANT_SLUGS: Record<string, string> = {
    "Грильница": "grillnitsa",
    "Бургер Кинг": "burger-king",
    "Додо": "dodo",
};

const DISH_SLUGS: Record<string, string> = {
    // === Грильница ===
    "Гавайская": "gavaiskaya",

    // === Burger King ===
    "Воппер": "wopper",

    // === Додо Пицца ===
    "Пепперони": "pepperoni",
};

const toSlug = (name: string): string => {
    return name
        .toLowerCase()
        .trim()
        .replace(/\s+/g, "-")
        .replace(/[^a-z0-9а-яё\-]/gi, "")
        .replace(/[а-яё]/g, (char) => {
            const map: Record<string, string> = {
                а: "a", б: "b", в: "v", г: "g", д: "d", е: "e", ё: "e",
                ж: "zh", з: "z", и: "i", й: "y", к: "k", л: "l", м: "m",
                н: "n", о: "o", п: "p", р: "r", с: "s", т: "t", у: "u",
                ф: "f", х: "h", ц: "ts", ч: "ch", ш: "sh", щ: "sch",
                ъ: "", ы: "y", ь: "", э: "e", ю: "yu", я: "ya",
            };
            return map[char] ?? char;
        });
};

export const getRestaurantImage = (restaurantName: string): string => {
    let slug = RESTAURANT_SLUGS[restaurantName];

    if (!slug) {
        slug = toSlug(restaurantName);
    }

    const key = `../assets/images/restaurants/${slug}.png`;

    if (restaurantImages[key]) {
        return (restaurantImages[key] as { default: string }).default;
    }

    return restaurantPlaceholder;
};

export const getDishImage = (
    dishName: string,
    restaurantName?: string
): string => {
    let dishSlug = DISH_SLUGS[dishName];
    if (!dishSlug) {
        dishSlug = toSlug(dishName);
    }

    let restaurantSlug: string | undefined;
    if (restaurantName) {
        restaurantSlug = RESTAURANT_SLUGS[restaurantName];
        if (!restaurantSlug) {
            restaurantSlug = toSlug(restaurantName);
        }
    }

    if (restaurantSlug) {
        const key = `../assets/images/dishes/${restaurantSlug}/${dishSlug}.png`;
        if (dishImages[key]) {
            return (dishImages[key] as { default: string }).default;
        }
    }

    const flatKey = `../assets/images/dishes/${dishSlug}.png`;
    if (dishImages[flatKey]) {
        return (dishImages[flatKey] as { default: string }).default;
    }

    return dishPlaceholder;
};