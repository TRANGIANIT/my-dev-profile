import { FormEvent, useEffect, useMemo, useState } from "react";
import {
  LogIn,
  LogOut,
  PackagePlus,
  RefreshCw,
  Save,
  Ban,
  ShoppingCart,
  Trash2,
} from "lucide-react";
import {
  cancelOrder,
  createOrder,
  createProduct,
  deleteProduct,
  getOrders,
  login,
  register,
  refreshSession,
  searchProducts,
  updateProduct,
  uploadProductImage,
} from "./api";
import type { AuthResponse, Order, Product, ProductRequest, Role } from "./types";

type Notice = {
  type: "success" | "error";
  message: string;
};

const emptyProductForm: ProductRequest = {
  name: "",
  description: "",
  price: 1000,
  stockQuantity: 10,
};

function App() {
  const [auth, setAuth] = useState<AuthResponse | null>(() => {
    const raw = localStorage.getItem("ecommerce.auth");
    return raw ? JSON.parse(raw) as AuthResponse : null;
  });
  const [products, setProducts] = useState<Product[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [productPage, setProductPage] = useState({ page: 0, size: 10, totalPages: 0, totalElements: 0 });
  const [keyword, setKeyword] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [inStock, setInStock] = useState(false);
  const [productForm, setProductForm] = useState<ProductRequest>(emptyProductForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [authMode, setAuthMode] = useState<"login" | "register">("login");
  const [username, setUsername] = useState("admin@example.com");
  const [password, setPassword] = useState("password123");
  const [role, setRole] = useState<Role>("ADMIN");
  const [quantities, setQuantities] = useState<Record<number, number>>({});
  const [notice, setNotice] = useState<Notice | null>(null);
  const [loading, setLoading] = useState(false);

  const isAdmin = auth?.role === "ADMIN";
  const totalStock = useMemo(
    () => products.reduce((sum, product) => sum + product.stockQuantity, 0),
    [products],
  );
  const totalRevenue = useMemo(
    () => orders.reduce((sum, order) => sum + Number(order.totalAmount), 0),
    [orders],
  );

  useEffect(() => {
    void loadProducts();
  }, []);

  useEffect(() => {
    if (auth) {
      localStorage.setItem("ecommerce.auth", JSON.stringify(auth));
      void loadOrders(auth.token);
    } else {
      localStorage.removeItem("ecommerce.auth");
      setOrders([]);
    }
  }, [auth]);

  async function run(action: () => Promise<void>, success?: string) {
    setLoading(true);
    setNotice(null);
    try {
      await action();
      if (success) {
        setNotice({ type: "success", message: success });
      }
    } catch (error) {
      setNotice({
        type: "error",
        message: error instanceof Error ? error.message : "Unexpected error",
      });
    } finally {
      setLoading(false);
    }
  }

  async function loadProducts() {
    await run(async () => {
      const page = await searchProducts({
        keyword,
        minPrice,
        maxPrice,
        inStock,
        page: productPage.page,
        size: productPage.size,
      });
      setProducts(page.content);
      setProductPage({
        page: page.number,
        size: page.size,
        totalPages: page.totalPages,
        totalElements: page.totalElements,
      });
    });
  }

  async function loadProductPage(nextPage: number) {
    await run(async () => {
      const page = await searchProducts({
        keyword,
        minPrice,
        maxPrice,
        inStock,
        page: nextPage,
        size: productPage.size,
      });
      setProducts(page.content);
      setProductPage({
        page: page.number,
        size: page.size,
        totalPages: page.totalPages,
        totalElements: page.totalElements,
      });
    });
  }

  async function loadOrders(token = auth?.token) {
    if (!token) {
      return;
    }
    await run(async () => {
      setOrders(await getOrders(token));
    });
  }

  function handleAuthSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    void run(async () => {
      const response = authMode === "login"
        ? await login(username, password)
        : await register(username, password, role);
      setAuth(response);
    }, authMode === "login" ? "Logged in" : "Account created");
  }

  function handleProductSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!auth) {
      setNotice({ type: "error", message: "Admin login is required" });
      return;
    }

    void run(async () => {
      if (editingId) {
        await updateProduct(auth.token, editingId, productForm);
      } else {
        await createProduct(auth.token, productForm);
      }
      setProductForm(emptyProductForm);
      setEditingId(null);
      await loadProductPage(productPage.page);
    }, editingId ? "Product updated" : "Product created");
  }

  function startEdit(product: Product) {
    setEditingId(product.id);
    setProductForm({
      name: product.name,
      description: product.description ?? "",
      price: Number(product.price),
      stockQuantity: product.stockQuantity,
    });
  }

  function handleDelete(productId: number) {
    if (!auth) {
      return;
    }

    void run(async () => {
      await deleteProduct(auth.token, productId);
      await loadProductPage(productPage.page);
    }, "Product deleted");
  }

  function handleCreateOrder(productId: number) {
    if (!auth) {
      setNotice({ type: "error", message: "Login is required to create orders" });
      return;
    }

    const quantity = quantities[productId] ?? 1;
    void run(async () => {
      await createOrder(auth.token, productId, quantity);
      await loadProductPage(productPage.page);
      setOrders(await getOrders(auth.token));
    }, "Order created");
  }

  function handleCancelOrder(orderId: number) {
    if (!auth) {
      return;
    }

    void run(async () => {
      await cancelOrder(auth.token, orderId);
      await loadProductPage(productPage.page);
      setOrders(await getOrders(auth.token));
    }, "Order cancelled");
  }

  function handleImageUpload(productId: number, file: File | null) {
    if (!auth || !file) {
      return;
    }

    void run(async () => {
      await uploadProductImage(auth.token, productId, file);
      await loadProductPage(productPage.page);
    }, "Product image uploaded");
  }

  function logout() {
    setAuth(null);
    setNotice({ type: "success", message: "Logged out" });
  }

  function handleRefreshSession() {
    if (!auth) {
      return;
    }

    void run(async () => {
      setAuth(await refreshSession(auth.refreshToken));
    }, "Session refreshed");
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Java Backend Portfolio</p>
          <h1>Ecommerce Operations</h1>
        </div>
        <div className="session">
          {auth ? (
            <>
              <span>{auth.username}</span>
              <strong>{auth.role}</strong>
              <button className="icon-button" type="button" onClick={handleRefreshSession} title="Refresh session">
                <RefreshCw size={18} />
              </button>
              <button className="icon-button" type="button" onClick={logout} title="Log out">
                <LogOut size={18} />
              </button>
            </>
          ) : (
            <span>Not logged in</span>
          )}
        </div>
      </header>

      <section className="metrics" aria-label="Metrics">
        <div>
          <span>Products</span>
          <strong>{products.length}</strong>
        </div>
        <div>
          <span>Stock</span>
          <strong>{totalStock}</strong>
        </div>
        <div>
          <span>Orders</span>
          <strong>{orders.length}</strong>
        </div>
        <div>
          <span>Revenue</span>
          <strong>{formatMoney(totalRevenue)}</strong>
        </div>
      </section>

      {notice && <p className={`notice ${notice.type}`}>{notice.message}</p>}

      <div className="workspace">
        <section className="panel auth-panel">
          <div className="panel-title">
            <h2>Access</h2>
            {auth && <button type="button" onClick={() => void loadOrders()} disabled={loading}>Sync orders</button>}
          </div>
          <form onSubmit={handleAuthSubmit} className="form-grid">
            <div className="segmented">
              <button type="button" className={authMode === "login" ? "active" : ""} onClick={() => setAuthMode("login")}>
                Login
              </button>
              <button type="button" className={authMode === "register" ? "active" : ""} onClick={() => setAuthMode("register")}>
                Register
              </button>
            </div>
            <label>
              Username
              <input value={username} onChange={(event) => setUsername(event.target.value)} required />
            </label>
            <label>
              Password
              <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required minLength={8} />
            </label>
            {authMode === "register" && (
              <label>
                Role
                <select value={role} onChange={(event) => setRole(event.target.value as Role)}>
                  <option value="ADMIN">ADMIN</option>
                  <option value="USER">USER</option>
                </select>
              </label>
            )}
            <button className="primary" type="submit" disabled={loading}>
              <LogIn size={18} />
              {authMode === "login" ? "Login" : "Register"}
            </button>
          </form>
        </section>

        <section className="panel product-panel">
          <div className="panel-title">
            <h2>Products</h2>
            <button type="button" onClick={() => void loadProducts()} disabled={loading} title="Refresh products">
              <RefreshCw size={18} />
              Refresh
            </button>
          </div>
          <form
            className="filters"
            onSubmit={(event) => {
              event.preventDefault();
              void loadProductPage(0);
            }}
          >
            <input placeholder="Search name or description" value={keyword} onChange={(event) => setKeyword(event.target.value)} />
            <input placeholder="Min price" type="number" min={0} value={minPrice} onChange={(event) => setMinPrice(event.target.value)} />
            <input placeholder="Max price" type="number" min={0} value={maxPrice} onChange={(event) => setMaxPrice(event.target.value)} />
            <label className="check-field">
              <input type="checkbox" checked={inStock} onChange={(event) => setInStock(event.target.checked)} />
              In stock
            </label>
            <button type="submit" disabled={loading}>Apply</button>
          </form>
          <div className="product-list">
            {products.map((product) => (
              <article className="product-row" key={product.id}>
                {product.imageUrl && (
                  <img className="product-image" src={`http://localhost:8080${product.imageUrl}`} alt={product.name} />
                )}
                <div>
                  <h3>{product.name}</h3>
                  <p>{product.description || "No description"}</p>
                  <span>{formatMoney(product.price)} · Stock {product.stockQuantity}</span>
                </div>
                <div className="row-actions">
                  <input
                    aria-label={`Quantity for ${product.name}`}
                    type="number"
                    min={1}
                    max={Math.max(product.stockQuantity, 1)}
                    value={quantities[product.id] ?? 1}
                    onChange={(event) => setQuantities({ ...quantities, [product.id]: Number(event.target.value) })}
                  />
                  <button type="button" onClick={() => handleCreateOrder(product.id)} disabled={loading || product.stockQuantity < 1}>
                    <ShoppingCart size={18} />
                    Order
                  </button>
                  {isAdmin && <button type="button" onClick={() => startEdit(product)}>Edit</button>}
                  {isAdmin && (
                    <label className="upload-button">
                      Image
                      <input
                        type="file"
                        accept="image/png,image/jpeg,image/webp"
                        onChange={(event) => handleImageUpload(product.id, event.target.files?.[0] ?? null)}
                      />
                    </label>
                  )}
                  {isAdmin && (
                    <button className="danger" type="button" onClick={() => handleDelete(product.id)} title="Delete product">
                      <Trash2 size={18} />
                    </button>
                  )}
                </div>
              </article>
            ))}
            {products.length === 0 && <p className="empty">No products found.</p>}
          </div>
          <div className="pagination">
            <span>
              Page {productPage.totalPages === 0 ? 0 : productPage.page + 1} / {productPage.totalPages}
              · {productPage.totalElements} products
            </span>
            <div>
              <button type="button" disabled={loading || productPage.page <= 0} onClick={() => void loadProductPage(productPage.page - 1)}>
                Previous
              </button>
              <button
                type="button"
                disabled={loading || productPage.totalPages === 0 || productPage.page >= productPage.totalPages - 1}
                onClick={() => void loadProductPage(productPage.page + 1)}
              >
                Next
              </button>
            </div>
          </div>
        </section>

        <section className="panel admin-panel">
          <div className="panel-title">
            <h2>{editingId ? "Edit Product" : "Create Product"}</h2>
            <span>{isAdmin ? "Admin enabled" : "Admin role required"}</span>
          </div>
          <form onSubmit={handleProductSubmit} className="form-grid">
            <label>
              Name
              <input value={productForm.name} onChange={(event) => setProductForm({ ...productForm, name: event.target.value })} required maxLength={120} />
            </label>
            <label>
              Description
              <textarea value={productForm.description} onChange={(event) => setProductForm({ ...productForm, description: event.target.value })} maxLength={1000} />
            </label>
            <div className="split-fields">
              <label>
                Price
                <input type="number" min={1} step={100} value={productForm.price} onChange={(event) => setProductForm({ ...productForm, price: Number(event.target.value) })} required />
              </label>
              <label>
                Stock
                <input type="number" min={0} value={productForm.stockQuantity} onChange={(event) => setProductForm({ ...productForm, stockQuantity: Number(event.target.value) })} required />
              </label>
            </div>
            <button className="primary" type="submit" disabled={!isAdmin || loading}>
              {editingId ? <Save size={18} /> : <PackagePlus size={18} />}
              {editingId ? "Save" : "Create"}
            </button>
          </form>
        </section>

        <section className="panel orders-panel">
          <div className="panel-title">
            <h2>Orders</h2>
            <span>{orders.length} records</span>
          </div>
          <div className="order-list">
            {orders.map((order) => (
              <article className="order-row" key={order.id}>
                <div>
                  <strong>#{order.id}</strong>
                  <span>{order.status}</span>
                </div>
                <p>{order.items.map((item) => `${item.productName} x ${item.quantity}`).join(", ")}</p>
                <div className="order-footer">
                  <strong>{formatMoney(order.totalAmount)}</strong>
                  {order.status === "CREATED" && (
                    <button className="danger" type="button" onClick={() => handleCancelOrder(order.id)} disabled={loading}>
                      <Ban size={18} />
                      Cancel
                    </button>
                  )}
                </div>
              </article>
            ))}
            {orders.length === 0 && <p className="empty">Login and create an order to see history.</p>}
          </div>
        </section>
      </div>
    </main>
  );
}

function formatMoney(value: number) {
  return new Intl.NumberFormat("ja-JP", {
    style: "currency",
    currency: "JPY",
    maximumFractionDigits: 0,
  }).format(Number(value));
}

export default App;
