"use strict";

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";
const TOKEN_KEY = "token";

const FAVORITES_URL = `${API_BASE}/api/client/favorites`;
const FAVORITE_DEL_URL = (listingId) => `${API_BASE}/api/client/favorites/${listingId}`;

function $(id){ return document.getElementById(id); }

// THEME
function applyThemeFromStorage() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.body.setAttribute("data-theme", saved);
}
function initThemeToggle() {
    const toggle = $("themeToggle");
    if (!toggle) return;
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    toggle.checked = saved === "dark";
    toggle.addEventListener("change", () => {
        const t = toggle.checked ? "dark" : "light";
        document.body.setAttribute("data-theme", t);
        localStorage.setItem(THEME_KEY, t);
    });
}

// NAV
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goHome(){ window.location.href="/templates/home.html"; }
window.goHome = goHome;

function goProfile(){
    const token = getTokenOrNull();
    window.location.href = token ? "/templates/profile.html" : "/templates/profileviewexc.html";
}
window.goProfile = goProfile;

function goFavorites(){
    const token = getTokenOrNull();
    window.location.href = token ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;

// HELPERS
function getTokenOrNull(){
    const t = localStorage.getItem(TOKEN_KEY);
    return t && t.trim() ? t.trim() : null;
}

function showAlert(type, msg){
    const a = $("alert");
    if (!a) return;
    a.className = "alert " + (type === "ok" ? "ok" : "err");
    a.textContent = msg;
    a.style.display = "block";
}
function hideAlert(){
    const a = $("alert");
    if (!a) return;
    a.style.display = "none";
    a.textContent = "";
}

function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[m]));
}

function money(v, currency) {
    if (v === null || v === undefined) return "—";
    try {
        const cur = currency || "TRY";
        return new Intl.NumberFormat("tr-TR", { style:"currency", currency:cur }).format(Number(v));
    } catch {
        return `${v} ${currency || ""}`.trim();
    }
}

function pickCover(x){
    return x?.coverImageUrl || "/imagesforapp/logo2.png";
}

async function fetchJsonAuth(url, options = {}) {
    const token = getTokenOrNull();
    if (!token) {
        localStorage.setItem("authTab", "login");
        localStorage.setItem("authMsg", "Favorileri görmek için giriş yapmalısın.");
        window.location.href = "/templates/Login.html";
        return null;
    }

    const res = await fetch(url, {
        ...options,
        cache: "no-store",
        headers: {
            ...(options.headers || {}),
            "Authorization": `Bearer ${token}`
        }
    });

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(() => null)
        : await res.text().catch(() => null);

    // SADECE 401/403 login'e atsın
    if (res.status === 401 || res.status === 403) {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.setItem("authTab", "login");
        localStorage.setItem("authMsg", "Oturum süresi dolmuş olabilir. Tekrar giriş yap.");
        window.location.href = "/templates/Login.html";
        return null;
    }

    if (!res.ok) {
        const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || `HTTP ${res.status}`;
        throw new Error(msg);
    }

    return body;
}

// UI
function buildSubtitle(c){
    const right = [c.year, c.kilometer ? `${c.kilometer} km` : null].filter(Boolean).join(" • ");
    const loc = [c.city, c.district].filter(Boolean).join(" / ");
    const store = c.storeName ? `Mağaza: ${c.storeName}` : "";
    return [store, right, loc].filter(Boolean).join(" | ");
}

function render(list){
    const grid = $("grid");
    const count = $("count");
    if (!grid) return;

    const q = ($("q")?.value || "").trim().toLowerCase();
    const filtered = q
        ? (list || []).filter(x =>
            String(x.title||"").toLowerCase().includes(q) ||
            String(x.storeName||"").toLowerCase().includes(q) ||
            String(x.city||"").toLowerCase().includes(q)
        )
        : (list || []);

    if (count) count.textContent = `${filtered.length} ilan`;

    grid.innerHTML = "";
    if (filtered.length === 0) {
        grid.innerHTML = `<div style="color:var(--muted)">Favori ilan yok.</div>`;
        return;
    }

    filtered.forEach(c => {
        const div = document.createElement("div");
        div.className = "card";

        div.innerHTML = `
      <div class="card-img">
        <img src="${escapeHtml(pickCover(c))}" alt="" style="width:100%;height:100%;object-fit:cover;display:block;">
      </div>
      <div class="card-body">
        <div class="title">${escapeHtml(c.title || "İlan")}</div>
        <div class="sub">${escapeHtml(buildSubtitle(c))}</div>
        <div class="row">
          <div class="price">${escapeHtml(money(c.price, c.currency))}</div>
          <button class="remove" type="button" data-id="${c.listingId}">Kaldır</button>
        </div>
      </div>
    `;

        div.addEventListener("click", (e) => {
            if (e.target && e.target.classList.contains("remove")) return;
            window.location.href = `/templates/vehicleinfo.html?id=${encodeURIComponent(c.listingId)}`;
        });

        div.querySelector(".remove")?.addEventListener("click", async (e) => {
            e.stopPropagation();
            const id = Number(e.currentTarget.getAttribute("data-id"));
            if (!Number.isFinite(id)) return;

            try {
                await fetchJsonAuth(FAVORITE_DEL_URL(id), { method: "DELETE" });
                showAlert("ok", "Favoriden kaldırıldı.");
                await load();
            } catch (err) {
                showAlert("err", err.message || "Kaldırılamadı");
            }
        });

        grid.appendChild(div);
    });
}

async function load(){
    hideAlert();
    const data = await fetchJsonAuth(FAVORITES_URL, { method: "GET" });
    if (!data) return;
    const list = Array.isArray(data) ? data : (data.items || data.favorites || []);
    render(Array.isArray(list) ? list : []);
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    $("searchBtn")?.addEventListener("click", load);
    $("q")?.addEventListener("keydown", (e) => { if (e.key === "Enter") load(); });

    try { await load(); }
    catch(e){ showAlert("err", e.message || "Favoriler yüklenemedi"); }
});
