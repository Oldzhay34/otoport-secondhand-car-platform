"use strict";

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";
const TOKEN_KEY = "token";

// ✅ STORE DETAIL ENDPOINT
const STORE_DETAIL_URL = (id) => `${API_BASE}/api/store/listings/${id}/detail`;

// Expert endpoints (mevcut projene göre)
const EXPERT_BY_LISTING_URL = (id) => `${API_BASE}/api/listings/${id}/expert-report`;
const EXPERT_BY_CAR_URL = (carId) => `${API_BASE}/api/cars/${carId}/expert-report`;

// -------------------- DOM HELPERS --------------------
function $(id) { return document.getElementById(id); }

const alertBox = $("alert");

const storeBox = $("storeBox");
const storeAvatar = $("storeAvatar");
const storeNameEl = $("storeName");
const storeLocEl = $("storeLoc");
const storePhoneA = $("storePhone");

const statusBadge = $("statusBadge");
const favTotalEl = $("favTotal");
const viewTotalEl = $("viewTotal");

const mainImg = $("mainImg");
const thumbs = $("thumbs");
const prevBtn = $("prevBtn");
const nextBtn = $("nextBtn");

const titleEl = $("title");
const priceEl = $("price");
const metaPill = $("metaPill");
const kvEl = $("kv");
const descText = $("descText");

// Expert UI refs
const expertPanel = $("expertPanel");
const expertResultBadge = $("expertResultBadge");
const expertMeta = $("expertMeta");
const expertNotes = $("expertNotes");
const expertTable = $("expertTable");
const expertTbody = $("expertTbody");
const expertHint = $("expertHint");

// Sketch UI refs
const carSketchPanel = $("carSketchPanel");
const sketchBaseImg = $("sketchBase");
const sketchOutline = $("sketchOutline");
const sketchParts = $("sketchParts");
const sketchHint = $("sketchHint");

// -------------------- UI HELPERS --------------------
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

// -------------------- NAV (Store) --------------------
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goStoreHome(){ window.location.href = "/templates/storehome.html"; }
window.goStoreHome = goStoreHome;

function goStoreInbox(){ window.location.href = "/templates/storeinbox.html"; }
window.goStoreInbox = goStoreInbox;

function goCreateListing(){ window.location.href = "/templates/storecreateListing.html"; }
window.goCreateListing = goCreateListing;

function goNotifications(){ window.location.href = "/templates/storenotifications.html"; }
window.goNotifications = goNotifications;

function goStoreProfile(){ window.location.href = "/templates/storeprofile.html"; }
window.goStoreProfile = goStoreProfile;

// -------------------- UTILS --------------------
function getListingIdFromQuery() {
    const qs = new URLSearchParams(window.location.search);
    const v = qs.get("id");
    const id = v ? Number(v) : NaN;
    return Number.isFinite(id) ? id : null;
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

function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[m]));
}

function getTokenOrNull() {
    const t = localStorage.getItem(TOKEN_KEY);
    return t && t.trim() ? t.trim() : null;
}

function fmtDate(iso) {
    if (!iso) return "—";
    try {
        const d = new Date(iso);
        return new Intl.DateTimeFormat("tr-TR", {
            year: "numeric", month: "2-digit", day: "2-digit",
            hour: "2-digit", minute: "2-digit"
        }).format(d);
    } catch {
        return iso;
    }
}

// -------------------- FETCH --------------------
async function fetchJson(url, options = {}) {
    const res = await fetch(url, { ...options, cache: "no-store" });
    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(() => null)
        : await res.text().catch(() => null);

    if (!res.ok) {
        const msg =
            (body && (body.message || body.error)) ||
            (typeof body === "string" ? body : "") ||
            `HTTP ${res.status}`;
        throw new Error(msg);
    }
    return body;
}

async function fetchJsonAuth(url, options = {}) {
    const token = getTokenOrNull();
    if (!token) throw new Error("Unauthorized");

    const res = await fetch(url, {
        ...options,
        cache: "no-store",
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${token}`
        }
    });

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(() => null)
        : await res.text().catch(() => null);

    if (res.status === 401 || res.status === 403) throw new Error("Unauthorized");

    if (!res.ok) {
        const msg =
            (body && (body.message || body.error)) ||
            (typeof body === "string" ? body : "") ||
            `HTTP ${res.status}`;
        throw new Error(msg);
    }

    return body;
}

// -------------------- GALLERY --------------------
let galleryUrls = [];
let galleryIndex = 0;

function setMainImageByIndex(idx) {
    if (!galleryUrls.length) return;
    galleryIndex = (idx + galleryUrls.length) % galleryUrls.length;

    const url = galleryUrls[galleryIndex];
    if (mainImg) mainImg.src = url;

    thumbs?.querySelectorAll(".thumb").forEach(t => t.classList.remove("active"));
    const active = thumbs?.querySelector(`.thumb[data-idx="${galleryIndex}"]`);
    if (active) active.classList.add("active");
}

function renderGallery(detail) {
    const imgs = Array.isArray(detail?.images) ? detail.images : [];
    galleryUrls = imgs.map(x => x?.imagePath).filter(Boolean);

    if (!galleryUrls.length) galleryUrls = [detail?.coverImageUrl || "/imagesforapp/logo2.png"];

    if (thumbs) thumbs.innerHTML = "";
    galleryUrls.forEach((url, i) => {
        const div = document.createElement("div");
        div.className = "thumb" + (i === 0 ? " active" : "");
        div.dataset.idx = String(i);
        div.innerHTML = `<img src="${escapeHtml(url)}" alt="thumb">`;
        div.onclick = () => setMainImageByIndex(i);
        thumbs?.appendChild(div);
    });

    if (prevBtn) prevBtn.onclick = () => setMainImageByIndex(galleryIndex - 1);
    if (nextBtn) nextBtn.onclick = () => setMainImageByIndex(galleryIndex + 1);

    setMainImageByIndex(0);
}

// -------------------- KV --------------------
function kvRow(label, value) {
    return `
    <div class="kv-row">
      <div class="kv-k">${escapeHtml(label)}</div>
      <div class="kv-v">${escapeHtml(value ?? "—")}</div>
    </div>
  `;
}

function trEnum(enumVal) {
    const v = String(enumVal || "").toUpperCase();
    const map = {
        AUTOMATIC: "Otomatik",
        MANUAL: "Manuel",
        DIESEL: "Dizel",
        GASOLINE: "Benzin",
        HYBRID: "Hibrit",
        ELECTRIC: "Elektrik"
    };
    return map[v] || enumVal || "—";
}

function renderKv(detail) {
    if (!kvEl) return;
    const car = detail.car || {};
    kvEl.innerHTML =
        kvRow("Marka", car.brandName) +
        kvRow("Model", car.modelName) +
        kvRow("Paket/Trim", car.trimName) +
        kvRow("Yıl", car.year != null ? String(car.year) : "—") +
        kvRow("Km", car.kilometer != null ? `${car.kilometer} km` : "—") +
        kvRow("Kasa", trEnum(car.bodyType)) +
        kvRow("Yakıt", trEnum(car.fuelType)) +
        kvRow("Vites", trEnum(car.transmission)) +
        kvRow("Motor (cc)", car.engineVolumeCc != null ? String(car.engineVolumeCc) : "—") +
        kvRow("Güç (hp)", car.enginePowerHp != null ? String(car.enginePowerHp) : "—") +
        kvRow("Renk", car.color);
}

// -------------------- STORE HEADER --------------------
function renderStoreHeader(detail) {
    const s = detail.store;
    if (!s) return;

    if (storeNameEl) storeNameEl.textContent = s.storeName || "Mağaza";
    if (storeLocEl) storeLocEl.textContent = [s.city, s.district].filter(Boolean).join(" • ") || "—";

    if (storeAvatar) {
        const letter = (s.storeName || "M").trim().charAt(0).toUpperCase();
        storeAvatar.textContent = letter;
    }

    // store kendi ekranı -> profile’a götürelim
    if (storeBox) {
        storeBox.style.cursor = "pointer";
        storeBox.onclick = () => goStoreProfile();
    }

    const phone = s?.phone || "";
    if (storePhoneA) {
        storePhoneA.textContent = phone || "—";
        storePhoneA.href = phone ? `tel:${phone}` : "#";
    }
}

// -------------------- EXPERT / KROKİ HELPERS (vehicleinfo’dan) --------------------
function trResultName(r) {
    const map = { CLEAN: "Temiz", MINOR: "Hafif Kusur", MAJOR: "Ağır Kusur", UNKNOWN: "Bilinmiyor" };
    return map[r] || r || "—";
}
function trStatusName(s) {
    const map = {
        ORIGINAL: "Orijinal",
        PAINTED: "Boyalı",
        LOCAL_PAINT: "Lokal Boya",
        REPLACED: "Değişen",
        REPAIRED: "Onarılmış",
        DAMAGED: "Hasarlı",
        UNKNOWN: "Bilinmiyor"
    };
    return map[s] || s || "—";
}
function statusClass(s) {
    switch (s) {
        case "ORIGINAL": return "status-original";
        case "PAINTED": return "status-painted";
        case "LOCAL_PAINT": return "status-localpaint";
        case "REPLACED": return "status-replaced";
        case "REPAIRED": return "status-repaired";
        case "DAMAGED": return "status-damaged";
        default: return "status-unknown";
    }
}
function statusPillHtml(status) {
    const cls = statusClass(status);
    return `<span class="status-pill ${cls}"><span class="dot"></span>${escapeHtml(trStatusName(status))}</span>`;
}
function trPartName(part) {
    const map = {
        HOOD: "Kaput",
        ROOF: "Tavan",
        FRONT_BUMPER: "Ön Tampon",
        REAR_BUMPER: "Arka Tampon",
        FRONT_LEFT_FENDER: "Sol Ön Çamurluk",
        FRONT_RIGHT_FENDER: "Sağ Ön Çamurluk",
        FRONT_LEFT_DOOR: "Sol Ön Kapı",
        FRONT_RIGHT_DOOR: "Sağ Ön Kapı",
        REAR_LEFT_DOOR: "Sol Arka Kapı",
        REAR_RIGHT_DOOR: "Sağ Arka Kapı",
        REAR_LEFT_FENDER: "Sol Arka Çamurluk",
        REAR_RIGHT_FENDER: "Sağ Arka Çamurluk",
        TRUNK_LID: "Bagaj Kapağı"
    };
    return map[part] || part || "—";
}

/* ✅ KROKİ assets */
const BASE_FILE = "expertiz raporu cam ve lastikk.png";
const PART_FILE = {
    HOOD: "ekspertiz kaput.png",
    ROOF: "ekspertiz tavan.png",
    FRONT_BUMPER: "ekspertiz ön tampon.png",
    REAR_BUMPER: "ekspertiz arka tampon.png",
    FRONT_LEFT_FENDER: "ekspertiz sol ön çamurluk.png",
    FRONT_RIGHT_FENDER: "ekspertiz sağ ön çamurluk.png",
    FRONT_LEFT_DOOR: "sol ön kapı.png",
    FRONT_RIGHT_DOOR: "ekspertiz sağ ön kapı.png",
    REAR_LEFT_DOOR: "sol arka kapı.png",
    REAR_RIGHT_DOOR: "ekspertiz sağ arka kapı.png",
    REAR_LEFT_FENDER: "sol arka çamurluk.png",
    REAR_RIGHT_FENDER: "ekspertiz sağ arka çamurluk.png",
    TRUNK_LID: "ekspertiz bagaj.png"
};
const ALL_PARTS = Object.keys(PART_FILE);

let ASSET_CONF = null;

function asset(path) { return encodeURI(path); }

async function urlExists(u) {
    try {
        const res = await fetch(u, { method: "GET", cache: "no-store" });
        return res.ok;
    } catch {
        return false;
    }
}

async function detectAssetConfig() {
    const roots = ["/images", "../images", "./images"];
    const folders = ["expertiz", "ekspertiz"];

    for (const root of roots) {
        for (const folder of folders) {
            const testUrl = asset(`${root}/${folder}/${BASE_FILE}`);
            if (await urlExists(testUrl)) return { root, folder };
        }
    }
    return { root: "/images", folder: "expertiz" };
}

function baseUrl() {
    if (!ASSET_CONF) return asset(`/images/expertiz/${BASE_FILE}`);
    return asset(`${ASSET_CONF.root}/${ASSET_CONF.folder}/${BASE_FILE}`);
}
function partUrl(enumPart) {
    const file = PART_FILE[enumPart];
    if (!file) return null;
    if (!ASSET_CONF) return asset(`/images/expertiz/parçalar/${file}`);
    return asset(`${ASSET_CONF.root}/${ASSET_CONF.folder}/parçalar/${file}`);
}

function fillColorByStatus(statusEnum) {
    switch (statusEnum) {
        case "REPLACED": return "#ef4444";
        case "PAINTED": return "#3b82f6";
        case "LOCAL_PAINT": return "#f97316";
        default: return null;
    }
}

function initSketchImages() {
    if (!sketchBaseImg) return;
    sketchBaseImg.src = baseUrl();
}

function renderSketchFromExpert(report) {
    if (!carSketchPanel || !sketchParts || !sketchOutline) return;

    const rawItems = Array.isArray(report?.items) ? report.items : [];
    const seen = new Set(rawItems.map(x => String(x?.part || "").trim()));
    const filled = ALL_PARTS
        .filter(p => !seen.has(p))
        .map(p => ({ part: p, status: "ORIGINAL", note: "Varsayılan" }));

    const items = rawItems.concat(filled);

    sketchOutline.innerHTML = "";
    sketchParts.innerHTML = "";

    for (const p of ALL_PARTS) {
        const m = partUrl(p);
        if (!m) continue;

        const div = document.createElement("div");
        div.className = "sketch-part";
        div.style.setProperty("--fill", "rgba(255,255,255,0.16)");
        div.style.setProperty("--mask", `url("${m}")`);
        div.title = trPartName(p);
        sketchOutline.appendChild(div);
    }

    let paintedCount = 0;
    for (const it of items) {
        const p = String(it?.part || "").trim();
        const st = String(it?.status || "").trim().toUpperCase();

        const fill = fillColorByStatus(st);
        if (!fill) continue;

        const m = partUrl(p);
        if (!m) continue;

        const div = document.createElement("div");
        div.className = "sketch-part";
        div.style.setProperty("--fill", fill);
        div.style.setProperty("--mask", `url("${m}")`);
        div.title = `${trPartName(p)}: ${trStatusName(st)}`;
        sketchParts.appendChild(div);
        paintedCount++;
    }

    if (sketchHint) {
        if (paintedCount === 0) sketchHint.textContent = "Değişen / boyalı / lokal boya parça yok.";
        else sketchHint.textContent = `${paintedCount} parça kroki üzerinde renklendirildi.`;
    }
}

function renderExpert(report) {
    if (!expertPanel) return;

    if (!report) {
        if (expertMeta) expertMeta.textContent = "Expertiz raporu bulunamadı.";
        if (expertHint) expertHint.textContent = "—";
        if (expertResultBadge) expertResultBadge.style.display = "none";
        if (expertNotes) expertNotes.style.display = "none";
        if (expertTable) expertTable.style.display = "none";
        if (expertTbody) expertTbody.innerHTML = "";
        renderSketchFromExpert(null);
        return;
    }

    const metaPieces = [
        report.companyName ? `Firma: ${report.companyName}` : null,
        report.reportNo ? `Rapor No: ${report.reportNo}` : null,
        report.reportDate ? `Tarih: ${report.reportDate}` : null
    ].filter(Boolean);

    if (expertMeta) expertMeta.textContent = metaPieces.join(" • ") || "Mağaza expertiz raporu girmedi.";

    if (expertResultBadge) {
        expertResultBadge.style.display = "inline-flex";
        expertResultBadge.textContent = `Sonuç: ${trResultName(report.result)}`;
    }

    const notes = (report.notes || "").trim();
    if (expertNotes) {
        if (notes) {
            expertNotes.style.display = "block";
            expertNotes.textContent = notes;
        } else {
            expertNotes.style.display = "none";
            expertNotes.textContent = "";
        }
    }

    const items = Array.isArray(report.items) ? report.items : [];
    if (expertTbody) expertTbody.innerHTML = "";

    if (!items.length) {
        if (expertTable) expertTable.style.display = "none";
        if (expertHint) expertHint.textContent = "Raporda parça detayı yok.";
        renderSketchFromExpert(report);
        return;
    }

    items.forEach(it => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${escapeHtml(trPartName(String(it.part || "").trim()))}</td>
      <td>${statusPillHtml(String(it.status || "UNKNOWN").trim().toUpperCase())}</td>
      <td>${escapeHtml(it.note ?? "—")}</td>
    `;
        expertTbody?.appendChild(tr);
    });

    if (expertTable) expertTable.style.display = "table";
    if (expertHint) expertHint.textContent = `${items.length} parça kaydı listelendi.`;

    renderSketchFromExpert(report);
}

async function loadExpertReportByListingId(listingId, detail) {
    const token = getTokenOrNull();
    const headers = {};
    if (token) headers["Authorization"] = `Bearer ${token}`;

    try {
        const report = await fetchJson(EXPERT_BY_LISTING_URL(listingId), { method: "GET", headers });
        renderExpert(report);
        return;
    } catch { }

    const carId = detail?.car?.id;
    if (carId) {
        try {
            const report = await fetchJson(EXPERT_BY_CAR_URL(carId), { method: "GET", headers });
            renderExpert(report);
            return;
        } catch { }
    }

    renderExpert(null);
}

// -------------------- MAIN LOAD --------------------
function normalizeStatus(s){
    const v = String(s || "").toUpperCase();
    const map = {
        ACTIVE: "Aktif",
        PASSIVE: "Pasif",
        SOLD: "Satıldı",
        DRAFT: "Taslak"
    };
    return map[v] || (s || "—");
}

async function loadDetail(listingId) {
    hideAlert();

    // Store detail: auth zorunlu
    const detail = await fetchJsonAuth(STORE_DETAIL_URL(listingId), { method: "GET" });

    if (titleEl) titleEl.textContent = detail.title || "İlan";
    if (priceEl) priceEl.textContent = money(detail.price, detail.currency);

    const car = detail.car || {};
    if (metaPill) {
        const text = [detail.city, car.year].filter(Boolean).join(" • ") || "—";
        metaPill.textContent = text;
    }

    if (descText) descText.textContent = (detail.description || "").trim() || "—";

    if (favTotalEl) favTotalEl.textContent = String(detail.favoriteCount ?? 0);
    if (viewTotalEl) viewTotalEl.textContent = String(detail.viewCount ?? 0);

    if (statusBadge) {
        statusBadge.textContent = `Durum: ${normalizeStatus(detail.status)}`;
    }

    renderKv(detail);
    renderStoreHeader(detail);
    renderGallery(detail);

    await loadExpertReportByListingId(listingId, detail);
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const token = getTokenOrNull();
    if (!token) {
        window.location.href = "/templates/Login.html";
        return;
    }

    ASSET_CONF = await detectAssetConfig();
    initSketchImages();

    const id = getListingIdFromQuery();
    if (!id) {
        showAlert("err", "İlan id bulunamadı.");
        return;
    }

    try {
        await loadDetail(id);
    } catch (e) {
        const msg = String(e?.message || "");
        if (msg.toLowerCase().includes("unauthorized")) {
            localStorage.removeItem(TOKEN_KEY);
            window.location.href = "/templates/Login.html";
            return;
        }
        showAlert("err", msg || "İlan detayı alınamadı.");
    }
});
