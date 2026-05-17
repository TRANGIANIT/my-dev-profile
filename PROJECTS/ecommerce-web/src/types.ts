export type Role = "USER" | "ADMIN";

export type AuthResponse = {
  token: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  role: Role;
};

export type Product = {
  id: number;
  name: string;
  description: string | null;
  price: number;
  stockQuantity: number;
  imageUrl: string | null;
  createdAt: string;
  updatedAt: string;
};

export type ProductRequest = {
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
};

export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export type OrderItem = {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
};

export type Order = {
  id: number;
  status: string;
  totalAmount: number;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
};
