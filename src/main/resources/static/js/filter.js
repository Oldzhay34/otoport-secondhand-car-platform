"use strict";

// filter.js

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";

// ✅ Dinamik katalog seçimi
const CAR_JSON_URLS = {
    CAR: "/filejson/AutomobileWithPackeages.json",
    SUV: "/filejson/suvwithpackages.json",
    MINIVAN: "/filejson/minivanwithpackages.json",
};

const STORES_URL = `${API_BASE}/api/home/stores?limit=200`;
const LISTINGS_URL = `${API_BASE}/api/listings`;

function $(id) { return document.getElementById(id); }

function applyThemeFromStorage(defaultTheme = "dark") {
    const saved = localStorage.getItem(THEME_KEY) || defaultTheme;
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

function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[m]));
}

function toQuery(params) {
    const qs = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
        if (v === null || v === undefined) return;
        const s = String(v).trim();
        if (!s) return;
        qs.set(k, s);
    });
    const str = qs.toString();
    return str ? `?${str}` : "";
}

function money(v, currency) {
    if (v === null || v === undefined) return "";
    try {
        const cur = currency || "TRY";
        return new Intl.NumberFormat("tr-TR", { style: "currency", currency: cur }).format(Number(v));
    } catch {
        return `${v} ${currency || ""}`.trim();
    }
}

function pickStoreLogo(store) {
    return store?.image
        || store?.logo
        || store?.logoUrl
        || store?.imageUrl
        || store?.imageLink
        || store?.profileImage
        || "/imagesforapp/login_logo.png";
}

function pickStoreName(store) {
    return store?.storeName || store?.name || store?.title || "Mağaza";
}

function pickStoreCity(store) {
    return store?.city || store?.location || "";
}

function toPublicImg(u) {
    if (!u) return u;
    if (u.startsWith("http") || u.startsWith("/")) return u;
    return "/uploads/" + u;
}

function pickCover(listing) {
    const raw = listing?.coverImageUrl
        || listing?.cover
        || listing?.imageUrl
        || listing?.image
        || "/imagesforapp/logo2.png";

    return toPublicImg(raw);
}

// --------------------
// ✅ BodyType -> Catalog key
// --------------------
function normalizeBodyTypeForCatalog(v) {
    const s = String(v || "").toUpperCase().trim();
    if (!s) return "CAR";

    if (s.includes("SUV") || s.includes("CROSSOVER")) return "SUV";
    if (s.includes("MINIVAN") || s.includes("PANELVAN") || s.includes("PANEL VAN") || s === "VAN") return "MINIVAN";

    return "CAR";
}

function getFilterCatalogUrl() {
    // 1) URL param varsa onu kullan
    const params = new URLSearchParams(window.location.search);
    const bodyFromUrl = params.get("bodyType") || "";

    // 2) yoksa selectten oku
    const bodyFromSelect = $("bodyTypeSelect")?.value || "";

    const key = normalizeBodyTypeForCatalog(bodyFromUrl || bodyFromSelect);
    return CAR_JSON_URLS[key] || CAR_JSON_URLS.CAR;
}

// --------------------
// STATE
// --------------------
let storesIndex = new Map();
let carData = null;

let selectedBrandObj = null;
let selectedModelObj = null;
let selectedVariantObj = null;
let selectedEngineObj = null;

// --------------------
// URL -> inputs
// --------------------
function applyQueryParamsToInputs() {
    const params = new URLSearchParams(window.location.search);

    const brand = params.get("brand") || "";
    const model = params.get("model") || "";
    const engine = params.get("engine") || "";
    const pack = params.get("pack") || "";
    const bodyType = params.get("bodyType") || "";

    const yearMin = params.get("yearMin") || "";
    const yearMax = params.get("yearMax") || "";

    if ($("yearMin")) $("yearMin").value = yearMin;
    if ($("yearMax")) $("yearMax").value = yearMax;

    if ($("bodyTypeSelect")) $("bodyTypeSelect").value = bodyType;

    return { brand, model, engine, pack, bodyType };
}

// --------------------
// STORES
// --------------------
async function loadStores() {
    const res = await fetch(STORES_URL, { cache: "no-store" });
    if (!res.ok) throw new Error("Store listesi alınamadı: HTTP " + res.status);

    const data = await res.json();
    const arr = data?.stores || data?.data || data?.items || data || [];
    storesIndex = new Map(arr.map(s => [Number(s.id), s]));
    return arr;
}

// --------------------
// CAR JSON -> dropdowns (DYNAMIC)
// --------------------
async function initCarDropdowns() {
    const brandSelect = $("brandSelect");
    const modelSelect = $("modelSelect");
    const variantSelect = $("variantSelect");
    const engineSelect = $("engineSelect");
    const packageSelect = $("packageSelect");

    if (!brandSelect || !modelSelect) return;

    try {
        const url = getFilterCatalogUrl();
        const res = await fetch(url, { cache: "no-store" });
        if (!res.ok) throw new Error("JSON yüklenemedi: HTTP " + res.status + ` (${url})`);

        carData = await res.json();

        const brands = (carData?.brands || [])
            .map(b => b.brand)
            .filter(Boolean)
            .sort((a, b) => a.localeCompare(b, "tr"));

        brandSelect.innerHTML =
            `<option value="">Hepsi</option>` +
            brands.map(b => `<option value="${escapeHtml(b)}">${escapeHtml(b)}</option>`).join("");

        brandSelect.disabled = false;

        brandSelect.addEventListener("change", () => {
            const brandName = brandSelect.value || "";
            selectedBrandObj = (carData?.brands || []).find(b => b.brand === brandName) || null;

            selectedModelObj = null;
            selectedVariantObj = null;
            selectedEngineObj = null;

            if (!selectedBrandObj) {
                modelSelect.disabled = true;
                modelSelect.innerHTML = `<option value="">Önce marka seç</option>`;
                if (variantSelect) { variantSelect.disabled = true; variantSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
                if (engineSelect) { engineSelect.disabled = true; engineSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
                if (packageSelect) { packageSelect.disabled = true; packageSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
                return;
            }

            const models = (selectedBrandObj.models || [])
                .map(m => m.model)
                .filter(Boolean)
                .sort((a, b) => a.localeCompare(b, "tr"));

            modelSelect.disabled = false;
            modelSelect.innerHTML =
                `<option value="">Hepsi</option>` +
                models.map(m => `<option value="${escapeHtml(m)}">${escapeHtml(m)}</option>`).join("");

            if (variantSelect) { variantSelect.disabled = true; variantSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
            if (engineSelect) { engineSelect.disabled = true; engineSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
            if (packageSelect) { packageSelect.disabled = true; packageSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
        });

        modelSelect.addEventListener("change", () => {
            const modelName = modelSelect.value || "";
            selectedModelObj = (selectedBrandObj?.models || []).find(m => m.model === modelName) || null;

            selectedVariantObj = null;
            selectedEngineObj = null;

            if (!selectedModelObj) {
                if (variantSelect) { variantSelect.disabled = true; variantSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
                if (engineSelect) { engineSelect.disabled = true; engineSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
                if (packageSelect) { packageSelect.disabled = true; packageSelect.innerHTML = `<option value="">(Opsiyonel)</option>`; }
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
        });

        variantSelect?.addEventListener("change", () => {
            const variantName = variantSelect.value || "";
            selectedVariantObj = (selectedModelObj?.variants || []).find(v => v.variant === variantName) || null;
            selectedEngineObj = null;

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
        });

        engineSelect?.addEventListener("change", () => {
            const engineName = engineSelect.value || "";

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
        });

    } catch (e) {
        console.warn("Car JSON okunamadı:", e.message);
    }
}

// --------------------
// LISTINGS fetch
// --------------------
async function loadListingsWithFilters(filters) {
    const url = LISTINGS_URL + toQuery(filters);
    const res = await fetch(url, { cache: "no-store" });

    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`İlanlar alınamadı: HTTP ${res.status}${text ? " - " + text : ""}`);
    }

    const data = await res.json();
    const arr =
        Array.isArray(data) ? data :
            Array.isArray(data?.content) ? data.content :
                Array.isArray(data?.listings) ? data.listings :
                    Array.isArray(data?.items) ? data.items :
                        Array.isArray(data?.data) ? data.data :
                            [];

    return arr;
}

// --------------------
// Normalize helpers
// --------------------
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

    return s;
}

function engineMatches(listingEngine, selectedEngine) {
    const e = norm(listingEngine);
    const s = norm(selectedEngine);
    if (!s) return true;
    if (!e) return false;
    if (e === s) return true;

    const parts = s.split(" ").filter(Boolean);
    const num = parts.find(p => /^\d+$/.test(p));
    const rest = parts.filter(p => p !== num);

    if (num) {
        const numOk = new RegExp(`\\b${num}\\b`).test(e);
        if (!numOk) return false;
    }
    for (const p of rest) {
        if (!e.includes(p)) return false;
    }
    return true;
}

function getBodyTypeValue(l) {
    const raw = (l?.bodyType ?? l?.car?.bodyType ?? "");
    return normalizeBodyType(raw);
}

// --------------------
// Client-side filtering
// --------------------
function clientSideFilter(listings, filters) {
    let out = Array.isArray(listings) ? listings.slice() : [];

    const q = (filters.q || "").toLowerCase();
    if (q) {
        out = out.filter(l => {
            const storeName = (l.storeName || "").toLowerCase();
            const title = (l.title || "").toLowerCase();
            const brand = String(l.brand || "").toLowerCase();
            const model = String(l.model || "").toLowerCase();
            const engine = String(l.engine || "").toLowerCase();
            return storeName.includes(q) || title.includes(q) || brand.includes(q) || model.includes(q) || engine.includes(q);
        });
    }

    if (filters.brand) {
        const b = filters.brand.trim().toLowerCase();
        out = out.filter(l => String(l.brand || "").trim().toLowerCase() === b);
    }

    if (filters.model) {
        const m = filters.model.trim().toLowerCase();
        out = out.filter(l => String(l.model || "").trim().toLowerCase() === m);
    }

    if (filters.yearMin != null) out = out.filter(l => l.year != null && Number(l.year) >= filters.yearMin);
    if (filters.yearMax != null) out = out.filter(l => l.year != null && Number(l.year) <= filters.yearMax);

    if (filters.priceMin != null) out = out.filter(l => Number(l.price || 0) >= filters.priceMin);
    if (filters.priceMax != null) out = out.filter(l => Number(l.price || 0) <= filters.priceMax);

    if (filters.bodyType) {
        const bt = normalizeBodyType(filters.bodyType);
        out = out.filter(l => getBodyTypeValue(l) === bt);
    }

    if (filters.engine) {
        out = out.filter(l => engineMatches(l.engine, filters.engine));
    }

    return out;
}

// --------------------
// Render helpers
// --------------------
function getStoreIdValue(l) { return Number(l?.storeId ?? l?.store_id ?? l?.store?.id ?? 0); }

function groupByStore(listings) {
    const map = new Map();
    (listings || []).forEach(l => {
        const sid = getStoreIdValue(l);
        if (!map.has(sid)) map.set(sid, []);
        map.get(sid).push(l);
    });
    return map;
}

function goFavorites() {
    const t = localStorage.getItem("token");
    const hasToken = t && t.trim().length > 0;
    window.location.href = hasToken ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;

function render(groupsMap) {
    const root = $("storeGroups");
    const count = $("count");
    if (!root) return;

    root.innerHTML = "";

    const storeIds = Array.from(groupsMap.keys()).filter(Boolean);
    const totalListings = Array.from(groupsMap.values()).reduce((a, x) => a + x.length, 0);
    if (count) count.textContent = `${storeIds.length} mağaza • ${totalListings} ilan`;

    storeIds.sort((a, b) => a - b);

    storeIds.forEach(storeId => {
        const store = storesIndex.get(storeId) || { id: storeId, storeName: "Mağaza", city: "" };
        const listings = groupsMap.get(storeId) || [];

        const bandHtml = `
      <div class="store-group">
        <div class="store-band">
          <div class="store-left">
            <div class="store-logo">
              <img src="${escapeHtml(pickStoreLogo(store))}" alt="logo" />
            </div>
            <div class="store-meta">
              <div class="store-name">${escapeHtml(pickStoreName(store))}</div>
              <div class="store-city">${escapeHtml(pickStoreCity(store))}</div>
            </div>
          </div>
          <div class="store-right">
            <div class="pill">${listings.length} ilan</div>
          </div>
        </div>

        <div class="cars">
          ${listings.map(l => `
            <div class="car-card" data-id="${l.id}">
              <div class="car-img">
                <img src="${escapeHtml(pickCover(l))}" alt="car" />
              </div>
              <div class="car-body">
                <div class="car-title">${escapeHtml(l.title || "İlan")}</div>
                <div class="car-sub">
                  <span>${escapeHtml(l.city || "")}</span>
                  <span>${l.year ? escapeHtml(l.year) : ""}</span>
                  <span>${l.kilometer ? escapeHtml(l.kilometer) + " km" : ""}</span>
                </div>
                <div class="car-price">${escapeHtml(money(l.price, l.currency))}</div>
              </div>
            </div>
          `).join("")}
        </div>
      </div>
    `;

        const wrap = document.createElement("div");
        wrap.innerHTML = bandHtml;
        root.appendChild(wrap.firstElementChild);
    });

    root.querySelectorAll(".car-card").forEach(card => {
        card.addEventListener("click", () => {
            const id = card.getAttribute("data-id");
            if (!id) return;
            window.location.href = `/templates/vehicleinfo.html?id=${encodeURIComponent(id)}`;
        });
    });
}

function renderEmpty(msg = "Sonuç bulunamadı") {
    const root = $("storeGroups");
    const count = $("count");
    if (count) count.textContent = "0 mağaza • 0 ilan";
    if (!root) return;

    root.innerHTML = `
    <div class="store-group">
      <div class="store-band">
        <div class="store-left">
          <div class="store-meta">
            <div class="store-name">${escapeHtml(msg)}</div>
            <div class="store-city">Filtreleri değiştirip tekrar dene.</div>
          </div>
        </div>
      </div>
    </div>
  `;
}

// --------------------
// Read filters
// --------------------
function readFilters() {
    const brand = ($("brandSelect")?.value || "").trim();
    const model = ($("modelSelect")?.value || "").trim();

    const engine = (!$("engineSelect")?.disabled ? ($("engineSelect")?.value || "").trim() : "");
    const pack = (!$("packageSelect")?.disabled ? ($("packageSelect")?.value || "").trim() : "");

    const bodyRaw = ($("bodyTypeSelect")?.value || "").trim();
    const bodyType = normalizeBodyType(bodyRaw);

    const yearMin = ($("yearMin")?.value || "").trim();
    const yearMax = ($("yearMax")?.value || "").trim();

    const priceMin = Number($("priceMin")?.value || 0) || null;
    const priceMax = Number($("priceMax")?.value || 0) || null;

    const q = ($("q")?.value || "").trim();

    return {
        api: {
            brand: null,
            model: null,
            yearMin: null,
            yearMax: null
        },
        client: {
            q,
            brand,
            model,
            bodyType,
            engine,
            pack,
            yearMin: yearMin ? Number(yearMin) : null,
            yearMax: yearMax ? Number(yearMax) : null,
            priceMin,
            priceMax
        }
    };
}

// --------------------
// Main flow
// --------------------
async function refresh() {
    try {
        if ($("count")) $("count").textContent = "Yükleniyor...";

        const { api, client } = readFilters();
        let listings = await loadListingsWithFilters(api);

        listings = clientSideFilter(listings, client);

        if (!listings || listings.length === 0) {
            renderEmpty();
            return;
        }

        const groups = groupByStore(listings);
        render(groups);

    } catch (e) {
        console.error(e);
        renderEmpty(e.message || "Bir hata oluştu");
    }
}

function resetFilters() {
    if ($("brandSelect")) $("brandSelect").value = "";
    if ($("modelSelect")) {
        $("modelSelect").value = "";
        $("modelSelect").disabled = true;
        $("modelSelect").innerHTML = `<option value="">Önce marka seç</option>`;
    }
    if ($("variantSelect")) { $("variantSelect").value = ""; $("variantSelect").disabled = true; }
    if ($("engineSelect")) { $("engineSelect").value = ""; $("engineSelect").disabled = true; }
    if ($("packageSelect")) { $("packageSelect").value = ""; $("packageSelect").disabled = true; }
    if ($("bodyTypeSelect")) $("bodyTypeSelect").value = "";

    ["yearMin", "yearMax", "priceMin", "priceMax", "q"].forEach(id => {
        const el = $(id);
        if (el) el.value = "";
    });
}

// --------------------
// Init
// --------------------
document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage("dark");
    initThemeToggle();

    const wanted = applyQueryParamsToInputs();

    try {
        await loadStores();
    } catch (e) {
        console.warn("Store listesi çekilemedi:", e.message);
    }

    // ✅ Önce bodyType set edildi (URL’den geldiyse), sonra doğru katalog çekilecek
    await initCarDropdowns();

    if (wanted.brand && $("brandSelect")) {
        $("brandSelect").value = wanted.brand;
        $("brandSelect").dispatchEvent(new Event("change"));
    }
    if (wanted.model && $("modelSelect")) {
        setTimeout(() => {
            $("modelSelect").value = wanted.model;
            $("modelSelect").dispatchEvent(new Event("change"));
        }, 0);
    }

    setTimeout(() => {
        if (wanted.engine && $("engineSelect") && !$("engineSelect").disabled) {
            $("engineSelect").value = wanted.engine;
            $("engineSelect").dispatchEvent(new Event("change"));
        }
        if (wanted.pack && $("packageSelect") && !$("packageSelect").disabled) {
            $("packageSelect").value = wanted.pack;
        }
    }, 50);

    $("applyBtn")?.addEventListener("click", refresh);
    $("resetBtn")?.addEventListener("click", () => { resetFilters(); refresh(); });
    $("searchBtn")?.addEventListener("click", refresh);
    $("q")?.addEventListener("keydown", (e) => { if (e.key === "Enter") refresh(); });

    // ✅ bodyType değişince: katalog değişsin + filtre uygula
    $("bodyTypeSelect")?.addEventListener("change", async () => {
        // dropdown seçimlerini temizle
        if ($("brandSelect")) $("brandSelect").value = "";
        if ($("modelSelect")) { $("modelSelect").value = ""; $("modelSelect").disabled = true; $("modelSelect").innerHTML = `<option value="">Önce marka seç</option>`; }
        if ($("variantSelect")) { $("variantSelect").value = ""; $("variantSelect").disabled = true; }
        if ($("engineSelect")) { $("engineSelect").value = ""; $("engineSelect").disabled = true; }
        if ($("packageSelect")) { $("packageSelect").value = ""; $("packageSelect").disabled = true; }

        selectedBrandObj = null;
        selectedModelObj = null;
        selectedVariantObj = null;
        selectedEngineObj = null;

        await initCarDropdowns();
        refresh();
    });

    refresh();
});
