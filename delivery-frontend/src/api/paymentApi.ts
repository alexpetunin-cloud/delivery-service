import { api } from "./axios";

export interface PaymentRequest {
    orderId: number;
}

export interface PaymentResponse {
    id: number;
    orderId: number;
    userId: number;
    amount: number;
    paymentMethod: string;
    status: "PENDING" | "SUCCESS" | "FAILED";
    transactionId: string | null;
    completedAt: string | null;
    createdAt: string;
}

export const initiatePayment = async (
    data: PaymentRequest
): Promise<PaymentResponse> => {
    const response = await api.post<PaymentResponse>(
        "/api/payments/initiate",
        data
    );

    return response.data;
};

export const processPayment = async (
    paymentId: number
): Promise<PaymentResponse> => {
    const response = await api.post<PaymentResponse>(
        `/api/payments/${paymentId}/process`
    );

    return response.data;
};