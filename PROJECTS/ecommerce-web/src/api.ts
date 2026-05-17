import type { AuthResponse, Order, PageResponse, Product, ProductRequest, Role } from "./types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

type ApiOptions = {
  token?: string;
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
};

async function apiRequest<T>(path: string, options: ApiOptions = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type") ?? "";
  const data = contentType.includes("application/json") ? await response.json() : await response.text();

  if (!response.ok) {
    const message = typeof data === "object" && data !== null && "message" in data
      ? String(data.message)
      : `Request failed with status ${response.status}`;
    throw new Error(message);
  }

  return data as T;
}

export function register(username: string, password: string, role: Role) {
  return apiRequest<AuthResponse>("/api/auth/register", {
    method: "POST",
    body: { username, password, role },
  });
}

export function login(username: string, password: string) {
  return apiRequest<AuthResponse>("/api/auth/login", {
    method: "POST",
    body: { username, password },
  });
}

export function refreshSession(refreshToken: string) {
  return apiRequest<AuthResponse>("/api/auth/refresh", {
    method: "POST",
    body: { refreshToken },
  });
}

export function getProducts() {
  return apiRequest<Product[]>("/api/products");
}

export type ProductSearchParams = {
  keyword?: string;
  minPrice?: string;
  maxPrice?: string;
  inStock?: boolean;
  page?: number;
  size?: number;
};

export function searchProducts(params: ProductSearchParams) {
  const query = new URLSearchParams();
  if (params.keyword) {
    query.set("keyword", params.keyword);
  }
  if (params.minPrice) {
    query.set("minPrice", params.minPrice);
  }
  if (params.maxPrice) {
    query.set("maxPrice", params.maxPrice);
  }
  if (params.inStock) {
    query.set("inStock", "true");
  }
  query.set("page", String(params.page ?? 0));
  query.set("size", String(params.size ?? 10));
  query.set("sort", "createdAt,desc");

  return apiRequest<PageResponse<Product>>(`/api/products/search?${query.toString()}`);
}

export function createProduct(token: string, product: ProductRequest) {
  return apiRequest<Product>("/api/products", {
    token,
    method: "POST",
    body: product,
  });
}

export function updateProduct(token: string, id: number, product: ProductRequest) {
  return apiRequest<Product>(`/api/products/${id}`, {
    token,
    method: "PUT",
    body: product,
  });
}

export function deleteProduct(token: string, id: number) {
  return apiRequest<void>(`/api/products/${id}`, {
    token,
    method: "DELETE",
  });
}

export async function uploadProductImage(token: string, id: number, file: File) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_BASE_URL}/api/products/${id}/image`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message ?? `Request failed with status ${response.status}`);
  }

  return data as Product;
}

export function createOrder(token: string, productId: number, quantity: number) {
  return apiRequest<Order>("/api/orders", {
    token,
    method: "POST",
    body: { items: [{ productId, quantity }] },
  });
}

export function getOrders(token: string) {
  return apiRequest<Order[]>("/api/orders", { token });
}

export function cancelOrder(token: string, orderId: number) {
  return apiRequest<Order>(`/api/orders/${orderId}/cancel`, {
    token,
    method: "PATCH",
  });
}
