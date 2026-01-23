"use strict";

// home.js

const THEME_KEY = "theme"; // "light" | "dark"
const CAR_JSON_URL = "/filejson/AutomobileWithPackeages.json";
const STORES_API_URL = "/api/home/stores?limit=50";

let carData = null;
let selectedBrandObj = null;
let selectedModelObj = null;
let selectedVariantObj = null;
let selectedEngineObj = null;

// UI
const brandSelect = document.getElementById("brandSelect");
const modelSelect = document.getElementById("modelSelect");
const variantSelect = document.getElementById("variantSelect");
const engineSelect = document.getElementById("engineSelect");
const packageSelect = document.getElementById("packageSelect");
const storeList = document.getElementById("storeList");

// ----------------------
// THEME
// ----------------------
function applyThemeFromStorage() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.body.setAttribute("data-theme", saved);
}

function initHomeThemeToggle() {
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

// ----------------------
// HELPERS
// ----------------------
function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    }[m]));
}

function safeImageUrl(url) {
    if (!url) return "/imagesforapp/logo2.png";
    try {
        if (url.startsWith("/uploads/")) {
            const name = url.substring("/uploads/".length);
            return "/uploads/" + encodeURIComponent(name);
        }
        return url;
    } catch {
        return url;
    }
}

function goFavorites() {
    const t = localStorage.getItem("token");
    const hasToken = t && t.trim().length > 0;
    window.location.href = hasToken ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;

function goProfile() {
    const token = localStorage.getItem("token");
    const hasToken = token && token.trim().length > 0;
    window.location.href = hasToken ? "/templates/profile.html" : "/templates/profileviewexc.html";
}
window.goProfile = goProfile;

function logout() {
    localStorage.removeItem("token");
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

// ----------------------
// STORES (API)
// ----------------------
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

function normalizeStoreDto(s) {
    // backend StoreCardDto alanlarına göre
    const id = s?.id ?? null;
    const name = s?.storeName ?? s?.name ?? "Mağaza";
    const city = s?.city ?? "";
    const district = s?.district ?? "";

    // image/logo
    const image =
        s?.logoUrl ||
        s?.logoURL ||
        s?.image ||
        "/imagesforapp/logo2.png";

    // --- Badge / Trust fields (opsiyonel) ---
    // Backend’in hangi alanları gönderdiğini bilmediğimiz için olabildiğince tolerant okuyoruz.
    const listingCount =
        s?.listingCount ??
        s?.adsCount ??
        s?.carCount ??
        s?.count ??
        null;

    const verified =
        s?.verified ??
        s?.isVerified ??
        s?.trusted ??
        false;

    const rating =
        s?.rating ??
        s?.score ??
        null;

    return { id, name, city, district, image, listingCount, verified, rating };
}

function renderStores(list) {
    if (!storeList) return;
    storeList.innerHTML = "";

    if (!Array.isArray(list) || list.length === 0) {
        storeList.innerHTML = `<div style="color:var(--muted)">Henüz mağaza yok.</div>`;
        return;
    }

    list.forEach((s) => {
        const div = document.createElement("div");
        div.className = "store-card";

        const loc = [s.city, s.district].filter(Boolean).join(" • ");
        const cityOnly = s.city || "—";
        const countText = (typeof s.listingCount === "number" || typeof s.listingCount === "string")
            ? `${escapeHtml(s.listingCount)} ilan`
            : null;

        // Badge HTML
        const badgesHtml = `
      <div class="store-badges">
        ${s.verified ? `<span class="badge verified">✔ Doğrulanmış</span>` : ``}
        ${countText ? `<span class="badge count">${countText}</span>` : ``}
        <span class="badge city">${escapeHtml(cityOnly)}</span>
      </div>
    `;

        // Trust HTML (rating opsiyonel)
        const trustHtml = `
      <div class="store-trust">
        <span class="trust-pill">🛡️ Güvenilir</span>
        ${s.rating ? `<span class="trust-pill">⭐ ${escapeHtml(s.rating)}</span>` : ``}
      </div>
    `;

        div.innerHTML = `
      ${badgesHtml}
      ${trustHtml}

      <img src="${escapeHtml(safeImageUrl(s.image))}" alt="">
      <div class="store-info">
        <h4>${escapeHtml(s.name)}</h4>
        <span>${escapeHtml(loc || "—")}</span>
      </div>
    `;

        div.onclick = () => {
            if (!s.id) return;
            window.location.href = `/templates/store.html?storeId=${encodeURIComponent(s.id)}`;
        };

        storeList.appendChild(div);
    });
}

async function loadStores() {
    try {
        const data = await fetchJson(STORES_API_URL);
        const arr = data?.stores || data?.items || data || [];
        const normalized = (Array.isArray(arr) ? arr : []).map(normalizeStoreDto);
        renderStores(normalized);
    } catch (e) {
        console.error("HOME STORES ERROR:", e);
        renderStores([]);
    }
}

// ----------------------
// CAR FILTERS: LOAD JSON
// ----------------------
async function initCarFilters() {
    try {
        if (brandSelect) brandSelect.innerHTML = `<option value="">Yükleniyor...</option>`;

        const res = await fetch(CAR_JSON_URL, { cache: "no-store" });
        if (!res.ok) throw new Error("JSON yüklenemedi: HTTP " + res.status);

        carData = await res.json();
        populateBrands(carData);
        attachEvents();

    } catch (e) {
        console.error("CAR JSON ERROR:", e);
        if (brandSelect) brandSelect.innerHTML = `<option value="">Araç datası bulunamadı</option>`;
    }
}

function attachEvents() {
    brandSelect?.addEventListener("change", onBrandChange);
    modelSelect?.addEventListener("change", onModelChange);
    variantSelect?.addEventListener("change", onVariantChange);
    engineSelect?.addEventListener("change", onEngineChange);

    document.getElementById("applyBtn")?.addEventListener("click", applyCarFilters);
    document.getElementById("resetBtn")?.addEventListener("click", resetFilters);
}

function populateBrands(data) {
    const brands = (data?.brands || []).map(b => b.brand).filter(Boolean);
    if (!brandSelect) return;

    brandSelect.innerHTML =
        `<option value="">Marka seç</option>` +
        brands.map(b => `<option value="${escapeHtml(b)}">${escapeHtml(b)}</option>`).join("");

    resetDownstream("brand");
}

function onBrandChange() {
    const brandName = brandSelect?.value || "";
    selectedBrandObj = (carData?.brands || []).find(b => b.brand === brandName) || null;

    if (!selectedBrandObj) {
        resetDownstream("brand");
        return;
    }

    const models = selectedBrandObj.models || [];
    if (modelSelect) {
        modelSelect.disabled = false;
        modelSelect.innerHTML =
            `<option value="">Model seç</option>` +
            models.map(m => `<option value="${escapeHtml(m.model)}">${escapeHtml(m.model)}</option>`).join("");
    }

    resetDownstream("model");
}

function onModelChange() {
    const modelName = modelSelect?.value || "";
    selectedModelObj = (selectedBrandObj?.models || []).find(m => m.model === modelName) || null;

    if (!selectedModelObj) {
        resetDownstream("model");
        return;
    }

    const hasVariants = Array.isArray(selectedModelObj.variants) && selectedModelObj.variants.length > 0;
    const hasEngines = Array.isArray(selectedModelObj.engines) && selectedModelObj.engines.length > 0;

    if (variantSelect) {
        if (hasVariants) {
            variantSelect.disabled = false;
            variantSelect.innerHTML =
                `<option value="">(Hepsi)</option>` +
                selectedModelObj.variants.map(v => `<option value="${escapeHtml(v.variant)}">${escapeHtml(v.variant)}</option>`).join("");
        } else {
            variantSelect.disabled = true;
            variantSelect.innerHTML = `<option value="">(Variant yok)</option>`;
        }
    }

    if (engineSelect) {
        if (!hasVariants && hasEngines) {
            engineSelect.disabled = false;
            engineSelect.innerHTML =
                `<option value="">(Hepsi)</option>` +
                selectedModelObj.engines.map(e => `<option value="${escapeHtml(e.engine)}">${escapeHtml(e.engine)}</option>`).join("");
        } else {
            engineSelect.disabled = true;
            engineSelect.innerHTML = `<option value="">(Motor opsiyonel)</option>`;
        }
    }

    if (packageSelect) {
        packageSelect.disabled = true;
        packageSelect.innerHTML = `<option value="">(Paket opsiyonel)</option>`;
    }

    selectedVariantObj = null;
    selectedEngineObj = null;
}

function onVariantChange() {
    const variantName = variantSelect?.value || "";
    selectedVariantObj = (selectedModelObj?.variants || []).find(v => v.variant === variantName) || null;

    const engines = selectedVariantObj?.engines || [];
    const packages = selectedVariantObj?.packages || [];

    if (engineSelect) {
        if (engines.length > 0) {
            engineSelect.disabled = false;
            engineSelect.innerHTML =
                `<option value="">(Hepsi)</option>` +
                engines.map(e => `<option value="${escapeHtml(e.engine)}">${escapeHtml(e.engine)}</option>`).join("");
        } else {
            engineSelect.disabled = true;
            engineSelect.innerHTML = `<option value="">(Motor yok)</option>`;
        }
    }

    if (packageSelect) {
        if (packages.length > 0) {
            packageSelect.disabled = false;
            packageSelect.innerHTML =
                `<option value="">(Hepsi)</option>` +
                packages.map(p => `<option value="${escapeHtml(p)}">${escapeHtml(p)}</option>`).join("");
        } else {
            packageSelect.disabled = true;
            packageSelect.innerHTML = `<option value="">(Paket opsiyonel)</option>`;
        }
    }

    selectedEngineObj = null;
}

function onEngineChange() {
    const engineName = engineSelect?.value || "";

    let engineArr = [];
    if (selectedVariantObj?.engines) engineArr = selectedVariantObj.engines;
    else if (selectedModelObj?.engines) engineArr = selectedModelObj.engines;

    selectedEngineObj = engineArr.find(e => e.engine === engineName) || null;

    const packages = selectedEngineObj?.packages || [];
    if (packageSelect) {
        if (packages.length > 0) {
            packageSelect.disabled = false;
            packageSelect.innerHTML =
                `<option value="">(Hepsi)</option>` +
                packages.map(p => `<option value="${escapeHtml(p)}">${escapeHtml(p)}</option>`).join("");
        } else {
            packageSelect.disabled = true;
            packageSelect.innerHTML = `<option value="">(Paket yok)</option>`;
        }
    }
}

function resetDownstream(level) {
    if (level === "brand") {
        if (modelSelect) {
            modelSelect.disabled = true;
            modelSelect.innerHTML = `<option value="">Önce marka seç</option>`;
        }
    }

    if (variantSelect) {
        variantSelect.disabled = true;
        variantSelect.innerHTML = `<option value="">(Opsiyonel)</option>`;
    }
    if (engineSelect) {
        engineSelect.disabled = true;
        engineSelect.innerHTML = `<option value="">(Opsiyonel)</option>`;
    }
    if (packageSelect) {
        packageSelect.disabled = true;
        packageSelect.innerHTML = `<option value="">(Opsiyonel)</option>`;
    }

    selectedBrandObj = level === "brand" ? null : selectedBrandObj;
    selectedModelObj = null;
    selectedVariantObj = null;
    selectedEngineObj = null;
}

function applyCarFilters() {
    const brand = brandSelect?.value || "";
    const model = modelSelect?.value || "";
    const engine = (!engineSelect?.disabled ? (engineSelect?.value || "") : "");
    const pack = (!packageSelect?.disabled ? (packageSelect?.value || "") : "");
    const yearMin = document.getElementById("yearMin")?.value || "";
    const yearMax = document.getElementById("yearMax")?.value || "";

    const qs = new URLSearchParams();
    if (brand) qs.set("brand", brand);
    if (model) qs.set("model", model);
    if (engine) qs.set("engine", engine);
    if (pack) qs.set("pack", pack);
    if (yearMin) qs.set("yearMin", yearMin);
    if (yearMax) qs.set("yearMax", yearMax);

    window.location.href = `/templates/filter.html?${qs.toString()}`;
}

function resetFilters() {
    if (brandSelect) brandSelect.value = "";
    resetDownstream("brand");

    const y1 = document.getElementById("yearMin");
    const y2 = document.getElementById("yearMax");
    if (y1) y1.value = "";
    if (y2) y2.value = "";
}

// ----------------------
// INIT
// ----------------------
document.addEventListener("DOMContentLoaded", () => {
    applyThemeFromStorage();
    initHomeThemeToggle();
    loadStores();
    initCarFilters();
});
