"use strict";

// ======================
// CONFIG
// ======================
const API_BASE = "http://localhost:8080";

const CLIENT_HOME = "/templates/home.html";
const STORE_HOME  = "/templates/storehome.html";
const ADMIN_HOME  = "/templates/adminhome.html";

// ======================
// THEME SYNC
// ======================
const THEME_KEY = "theme"; // "light" | "dark"

function applyTheme(theme) {
    const t = theme === "dark" ? "dark" : "light";
    document.body.setAttribute("data-theme", t);
    localStorage.setItem(THEME_KEY, t);
}

function initThemeToggle() {
    const saved = localStorage.getItem(THEME_KEY) || "light";
    applyTheme(saved);

    const toggle = document.getElementById("themeToggle");
    if (!toggle) return;

    toggle.checked = saved === "dark";
    toggle.addEventListener("change", () =>
        applyTheme(toggle.checked ? "dark" : "light")
    );
}

// ======================
// HELPERS
// ======================
function $(id) { return document.getElementById(id); }

function showMsg(text, type = "err") {
    const el = $("msg");
    if (!el) return;
    el.classList.remove("hidden", "ok", "err");
    el.classList.add(type === "ok" ? "ok" : "err");
    el.textContent = text;
}

function clearMsg() {
    const el = $("msg");
    if (!el) return;
    el.classList.add("hidden");
    el.textContent = "";
}

function setTab(which) {
    const isLogin = which === "login";
    $("tab-login")?.classList.toggle("active", isLogin);
    $("tab-register")?.classList.toggle("active", !isLogin);
    $("login-form")?.classList.toggle("hidden", !isLogin);
    $("register-form")?.classList.toggle("hidden", isLogin);
    clearMsg();
}

// ======================
// TOKEN (🔥 KRİTİK)
// ======================
const TOKEN_KEY = "token";

function normalizeToken(raw) {
    if (!raw) return "";
    let t = raw.trim();
    if (t.toLowerCase().startsWith("bearer ")) {
        t = t.slice(7).trim();
    }
    return t;
}

function saveAuth(token) {
    const clean = normalizeToken(token);
    localStorage.setItem(TOKEN_KEY, clean);
}

function clearAuth() {
    localStorage.removeItem(TOKEN_KEY);
}

// ======================
// JWT decode (payload)
// ======================
function decodeJwtPayload(token) {
    try {
        const payload = token.split(".")[1];
        const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
        const json = decodeURIComponent(atob(base64).split("").map(c =>
            "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(""));
        return JSON.parse(json);
    } catch {
        return null;
    }
}

function normalizeRole(role) {
    if (!role) return "CLIENT";
    const r = String(role).trim().toUpperCase();
    if (r.startsWith("ROLE_")) return r.substring(5);
    return r;
}

// ======================
// ROLE → REDIRECT
// ======================
function redirectAfterAuth(token) {
    const payload = decodeJwtPayload(token);
    const role = normalizeRole(payload?.role);

    let url = CLIENT_HOME;
    if (role === "STORE") url = STORE_HOME;
    else if (role === "ADMIN") url = ADMIN_HOME;

    window.location.href = url;
}

// ======================
// FETCH WRAPPER
// ======================
async function api(path, options = {}) {
    const headers = Object.assign(
        { "Content-Type": "application/json" },
        options.headers || {}
    );

    if (options.auth === true) {
        const token = localStorage.getItem(TOKEN_KEY);
        if (token) headers["Authorization"] = `Bearer ${token}`;
    }

    const { auth, ...fetchOptions } = options;

    const res = await fetch(`${API_BASE}${path}`, {
        ...fetchOptions,
        headers
    });

    const contentType = res.headers.get("content-type") || "";
    const body = contentType.includes("application/json")
        ? await res.json().catch(() => null)
        : await res.text().catch(() => null);

    if (!res.ok) {
        const msg =
            (body && (body.message || body.error || body.details)) ||
            (typeof body === "string" && body) ||
            `HTTP ${res.status}`;
        throw new Error(msg);
    }

    return body;
}

// ======================
// EVENTS
// ======================
window.addEventListener("DOMContentLoaded", () => {
    initThemeToggle();

    $("tab-login")?.addEventListener("click", () => setTab("login"));
    $("tab-register")?.addEventListener("click", () => setTab("register"));
    $("go-register")?.addEventListener("click", (e) => {
        e.preventDefault(); setTab("register");
    });
    $("go-login")?.addEventListener("click", (e) => {
        e.preventDefault(); setTab("login");
    });

    // ----------------------
    // GUEST
    // ----------------------
    $("guest-btn")?.addEventListener("click", async () => {
        try {
            await api("/api/home/guest-hit", { method: "POST", auth: false });
        } catch {}
        clearAuth();
        window.location.href = CLIENT_HOME;
    });

    // ----------------------
    // LOGIN
    // ----------------------
    $("login-form")?.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearMsg();

        const email = $("login-email")?.value?.trim();
        const password = $("login-password")?.value;

        if (!email || !password) {
            showMsg("Email ve şifre zorunlu.");
            return;
        }

        try {
            const data = await api("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ email, password }),
                auth: false
            });

            if (!data?.token) throw new Error("Token alınamadı.");

            saveAuth(data.token);
            showMsg("Giriş başarılı. Yönlendiriliyorsun...", "ok");
            setTimeout(() => redirectAfterAuth(normalizeToken(data.token)), 200);

        } catch (err) {
            showMsg(err.message || "Giriş başarısız.");
        }
    });

    // ----------------------
    // REGISTER
    // ----------------------
    $("register-form")?.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearMsg();

        const firstName = $("reg-firstName")?.value?.trim();
        const lastName  = $("reg-lastName")?.value?.trim();
        const email     = $("reg-email")?.value?.trim();
        const password  = $("reg-password")?.value;
        const phone     = $("reg-phone")?.value?.trim() || null;
        const marketingConsent = $("reg-consent")?.checked === true;

        if (!firstName || !lastName || !email || !password) {
            showMsg("Tüm zorunlu alanları doldur.");
            return;
        }

        try {
            const data = await api("/api/auth/register", {
                method: "POST",
                body: JSON.stringify({
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    marketingConsent
                }),
                auth: false
            });

            if (data?.token) {
                saveAuth(data.token);
                showMsg("Kayıt başarılı. Giriş yapıldı!", "ok");
                setTimeout(() => redirectAfterAuth(normalizeToken(data.token)), 200);
                return;
            }

            showMsg("Kayıt başarılı. Giriş ekranına yönlendiriliyorsun...", "ok");
            setTimeout(() => setTab("login"), 400);

        } catch (err) {
            showMsg(err.message || "Kayıt başarısız.");
        }
    });
});
