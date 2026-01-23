"use strict";

const THEME_KEY = "theme";
const TOKEN_KEY = "token";

const STORE_INFO_URL = (id) => `/api/stores/${id}`;
const STORE_LISTINGS_FILTER_URL = (id) => `/api/stores/${id}/listings/filter`;

const alertBox = document.getElementById("alert");
const listingGrid = document.getElementById("listingGrid");

const storeNameEl = document.getElementById("storeName");
const storeSubEl = document.getElementById("storeSub");
const storeAddressEl = document.getElementById("storeAddress");
const storePhoneEl = document.getElementById("storePhone");
const storeFloorShopEl = document.getElementById("storeFloorShop");
const storeDirEl = document.getElementById("storeDir");
const verifiedBadge = document.getElementById("verifiedBadge");

// Filters
const bodyTypeEl = document.getElementById("bodyType");
const yearMinEl = document.getElementById("yearMin");
const yearMaxEl = document.getElementById("yearMax");
const priceMinEl = document.getElementById("priceMin");
const priceMaxEl = document.getElementById("priceMax");
const limitEl = document.getElementById("limit");
const applyBtn = document.getElementById("applyBtn");
const resetBtn = document.getElementById("resetBtn");

let allCards = []; // API’den gelen tüm kartlar (ham)

// ---------------- THEME ----------------
function applyThemeFromStorage() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.body.setAttribute("data-theme", saved);
}
function initThemeToggle() {
    const toggle = document.getElementById("themeToggle");
    if (!toggle) return;

    const saved = localStorage.getItem(THEME_KEY) || "dark";
    toggle.checked = saved === "dark";

    toggle.addEventListener("change", () => {
        const t = toggle.checked ? "dark" : "light";
        document.body.setAttribute("data-theme", t);
        localStorage.setItem(THEME_KEY, t);
    });
}

// ---------------- NAV ----------------
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goHome() { window.location.href = "/templates/home.html"; }
window.goHome = goHome;

function goProfile() {
    const token = localStorage.getItem("token");
    const hasToken = token && token.trim().length > 0;

    window.location.href = hasToken
        ? "/templates/profile.html"
        : "/templates/profileviewexc.html";
}
window.goProfile = goProfile;

// ---------------- UI helpers ----------------
function showAlert(type, msg) {
    if (!alertBox) return;
    alertBox.className = "alert " + (type === "ok" ? "ok" : "err");
    alertBox.textContent = msg;
    alertBox.style.display = "block";
}
function hideAlert() {
    if (!alertBox) return;
    alertBox.style.display = "none";
    alertBox.textContent = "";
}
function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[m]));
}
function getStoreId() {
    const qs = new URLSearchParams(window.location.search);
    const v = qs.get("storeId");
    const id = v ? Number(v) : NaN;
    return Number.isFinite(id) ? id : null;
}
function formatFloorShop(floor, shopNo) {
    const f = (floor === null || floor === undefined) ? "-" : floor;
    const s = shopNo ? shopNo : "-";
    return `${f} / ${s}`;
}
function money(v, currency) {
    if (v === null || v === undefined) return "—";
    try {
        const cur = currency || "TRY";
        return new Intl.NumberFormat("tr-TR", { style: "currency", currency: cur }).format(Number(v));
    } catch {
        return `${v} ${currency || ""}`.trim();
    }
}

function pickCover(c) {
    return c?.coverImageUrl || "/imagesforapp/logo2.png";
}

// ---------------- NORMALIZE (filter.js ile aynı mantık) ----------------
function norm(s) {
    return String(s ?? "").trim().toUpperCase().replace(/\s+/g, " ");
}

function normalizeBodyType(v) {
    let s = String(v ?? "").trim().toUpperCase();
    if (!s) return "";

    const k = s.replace(/[^A-Z0-9]/g, "");

    if (k === "HB" || k.includes("HATCHBACK") || k.includes("HATCH") || k.includes("HBACK")) return "HATCHBACK";
    if (k.includes("SEDAN") || k === "SD") return "SEDAN";
    if (k.includes("SUV") || k.includes("CROSSOVER")) return "SUV";
    if (k.includes("CABRIO") || k.includes("CABRIOLET") || k.includes("CONVERTIBLE")) return "CABRIO";
    if (k.includes("COUPE")) return "COUPE";
    if (k.includes("STATIONWAGON") || k.includes("WAGON") || k === "SW") return "STATION_WAGON";
    if (k.includes("PICKUP")) return "PICKUP";
    if (k.includes("VAN")) return "VAN";

    return s;
}

function cardBodyType(c) {
    return normalizeBodyType(c?.bodyType);
}

// ---------------- API ----------------
async function fetchJson(url, options = {}) {
    const res = await fetch(url, { ...options, cache: "no-store" });
    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(() => null)
        : await res.text().catch(() => null);

    if (!res.ok) {
        const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || `HTTP ${res.status}`;
        throw new Error(msg);
    }
    return body;
}

async function loadStore(storeId) {
    const store = await fetchJson(STORE_INFO_URL(storeId), { method: "GET" });

    storeNameEl.textContent = store.storeName || "Mağaza";
    const city = store.city || "";
    const district = store.district || "";
    storeSubEl.textContent = [city, district].filter(Boolean).join(" • ") || "—";

    storeAddressEl.textContent = store.addressLine || "—";
    storePhoneEl.textContent = store.phone || "—";
    storeFloorShopEl.textContent = formatFloorShop(store.floor, store.shopNo);
    storeDirEl.textContent = store.directionNote || "—";

    verifiedBadge.style.display = store.verified === true ? "inline-flex" : "none";
}

function buildFilterPayload() {
    // backend tarafına sadece limit/bodyType yollayalım, geri kalan client-side
    const bodyTypeRaw = (bodyTypeEl?.value || "").trim();
    const limit = Number(limitEl?.value || 30);

    return {
        bodyType: bodyTypeRaw ? normalizeBodyType(bodyTypeRaw) : null,
        featureIds: [],
        matchMode: "ANY",
        floor: null,
        limit: Number.isFinite(limit) ? limit : 30
    };
}

async function loadListingsFromApi(storeId) {
    const payload = buildFilterPayload();

    const data = await fetchJson(STORE_LISTINGS_FILTER_URL(storeId), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const cards = data.cards || data.listings || [];
    allCards = Array.isArray(cards) ? cards : [];
}

// ---------------- CLIENT-SIDE FILTER (filter.js gibi) ----------------
function readUiFilters() {
    const bodyType = normalizeBodyType((bodyTypeEl?.value || "").trim());
    const yearMin = (yearMinEl?.value || "").trim();
    const yearMax = (yearMaxEl?.value || "").trim();

    const priceMin = Number(priceMinEl?.value || 0) || null;
    const priceMax = Number(priceMaxEl?.value || 0) || null;

    return {
        bodyType: bodyType || "",
        yearMin: yearMin ? Number(yearMin) : null,
        yearMax: yearMax ? Number(yearMax) : null,
        priceMin,
        priceMax
    };
}

function clientSideFilterCards(cards, f) {
    let out = Array.isArray(cards) ? cards.slice() : [];

    if (f.yearMin != null) out = out.filter(c => c.year != null && Number(c.year) >= f.yearMin);
    if (f.yearMax != null) out = out.filter(c => c.year != null && Number(c.year) <= f.yearMax);

    if (f.priceMin != null) out = out.filter(c => Number(c.price || 0) >= f.priceMin);
    if (f.priceMax != null) out = out.filter(c => Number(c.price || 0) <= f.priceMax);

    if (f.bodyType) {
        const bt = normalizeBodyType(f.bodyType);
        out = out.filter(c => cardBodyType(c) === bt);
    }

    return out;
}
function goFavorites(){
    const t = localStorage.getItem("token");
    const hasToken = t && t.trim().length > 0;
    window.location.href = hasToken ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;



// ---------------- RENDER ----------------
function buildSubtitle(c) {
    const left = [c.brand, c.model, c.engine].filter(Boolean).join(" • ");
    const right = [c.year, c.kilometer ? `${c.kilometer} km` : null].filter(Boolean).join(" • ");
    const loc = [c.city, c.district].filter(Boolean).join(" / ");

    return [left, right, loc].filter(Boolean).join(" | ");
}

function renderListings(cards) {
    if (!listingGrid) return;
    listingGrid.innerHTML = "";

    if (!cards || cards.length === 0) {
        listingGrid.innerHTML = `<div class="muted">Bu filtreye uygun ilan bulunamadı.</div>`;
        return;
    }

    cards.forEach(c => {
        const div = document.createElement("div");
        div.className = "card";

        div.innerHTML = `
      <div class="card-img">
        <img src="${escapeHtml(pickCover(c))}" alt="" style="width:100%;height:100%;object-fit:cover;display:block;">
      </div>
      <div class="card-body">
        <div class="card-title">${escapeHtml(c.title || "İlan")}</div>
        <div class="card-sub">${escapeHtml(buildSubtitle(c))}</div>
        <div class="card-price">${escapeHtml(money(c.price, c.currency))}</div>
      </div>
    `;

        div.onclick = () => {
            const id = c?.id;
            if (!id) return;
            window.location.href = `/templates/vehicleinfo.html?id=${encodeURIComponent(id)}`;
        };


        listingGrid.appendChild(div);

    });
}

function refreshUiOnly() {
    const f = readUiFilters();
    const filtered = clientSideFilterCards(allCards, f);
    renderListings(filtered);
}

// ---------------- INIT ----------------
document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const storeId = getStoreId();
    if (!storeId) {
        showAlert("err", "storeId bulunamadı. Home'dan mağaza seçerek gelmelisin.");
        return;
    }

    applyBtn?.addEventListener("click", () => {
        try { refreshUiOnly(); }
        catch (e) { showAlert("err", e.message || "Filtre uygulanamadı."); }
    });

    resetBtn?.addEventListener("click", () => {
        if (bodyTypeEl) bodyTypeEl.value = "";
        if (yearMinEl) yearMinEl.value = "";
        if (yearMaxEl) yearMaxEl.value = "";
        if (priceMinEl) priceMinEl.value = "";
        if (priceMaxEl) priceMaxEl.value = "";
        if (limitEl) limitEl.value = "30";
        refreshUiOnly();
    });

    // İstersen auto-apply:
    bodyTypeEl?.addEventListener("change", refreshUiOnly);

    try {
        await loadStore(storeId);

        // İlk load: API’den çek
        await loadListingsFromApi(storeId);

        // UI filtresiyle bas
        refreshUiOnly();
    } catch (e) {
        console.error(e);
        showAlert("err", e.message || "Mağaza sayfası yüklenemedi.");
    }
});
// =====================================================
// HERO SHRINK (scroll)
// =====================================================
(function initHeroShrink(){
    const TH = 60; // kaç px scroll sonra compact
    let on = false;

    window.addEventListener("scroll", () => {
        const should = window.scrollY > TH;
        if (should === on) return;
        on = should;
        document.body.classList.toggle("hero-compact", should);
    }, { passive: true });
})();

// =====================================================
// RESULT COUNT (live update hook)
// - listingleri render ettiğin yerde çağır:
//   setResultCount(list.length)
// =====================================================
function setResultCount(n){
    const el = document.getElementById("resultCount");
    if(!el) return;

    const b = el.querySelector("b") || el;
    b.textContent = String(n);

    el.classList.remove("bump");
    // reflow trick
    void el.offsetWidth;
    el.classList.add("bump");
}
window.setResultCount = setResultCount;

