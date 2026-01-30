"use strict";

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";
const TOKEN_KEY = "token";

// -------------------- ENDPOINTS --------------------
const DETAIL_URL = (id) => `${API_BASE}/api/listings/${id}/detail`;

const EXPERT_BY_LISTING_URL = (id) => `${API_BASE}/api/listings/${id}/expert-report`;
const EXPERT_BY_CAR_URL = (carId) => `${API_BASE}/api/cars/${carId}/expert-report`;

const FAV_ADD_URL = (id) => `${API_BASE}/api/client/favorites/${id}`;
const FAV_DEL_URL = (id) => `${API_BASE}/api/client/favorites/${id}`;
const FAV_LIST_URL = `${API_BASE}/api/client/favorites`;

const VIEW_ONCE_URL = (id) => `${API_BASE}/api/client/listings/${id}/view`;

// ✅ INQUIRY (CLIENT ONLY)
const INQ_UPSERT_URL = `${API_BASE}/api/client/inquiries/upsert`; // POST
const INQ_THREAD_BY_LISTING_URL = (listingId) =>
    `${API_BASE}/api/client/inquiries/thread?listingId=${encodeURIComponent(listingId)}`; // GET

// -------------------- DOM HELPERS --------------------
function $(id) { return document.getElementById(id); }

// ---- UI refs ----
const alertBox = $("alert");

const storeBox = $("storeBox");
const storeAvatar = $("storeAvatar");
const storeNameEl = $("storeName");
const storeLocEl = $("storeLoc");

const favBtn = $("favBtn");
const favIcon = $("favIcon");
const favText = $("favText");

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
const storePhoneA = $("storePhone");

// ---- Expert UI refs ----
const expertPanel = $("expertPanel");
const expertResultBadge = $("expertResultBadge");
const expertMeta = $("expertMeta");
const expertNotes = $("expertNotes");
const expertTable = $("expertTable");
const expertTbody = $("expertTbody");
const expertHint = $("expertHint");

// ---- Sketch UI refs ----
const carSketchPanel = $("carSketchPanel");
const sketchBaseImg = $("sketchBase");
const sketchOutline = $("sketchOutline");
const sketchParts = $("sketchParts");
const sketchHint = $("sketchHint");

// ---- Inquiry UI refs ----
const guestFields = $("guestFields");
const guestName = $("guestName");
const guestEmail = $("guestEmail");
const guestPhone = $("guestPhone");

const msgTa = $("msg");
const sendBtn = $("sendBtn");
const inquiryHint = $("inquiryHint");
const messagesEl = $("messages");

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

function setInquiryHint(text) {
    if (!inquiryHint) return;
    inquiryHint.textContent = text || "—";
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

// -------------------- NAV --------------------
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goHome() { window.location.href = "/templates/home.html"; }
window.goHome = goHome;

function goProfile() {
    const token = localStorage.getItem(TOKEN_KEY);
    const hasToken = token && token.trim().length > 0;
    window.location.href = hasToken ? "/templates/profile.html" : "/templates/profileviewexc.html";
}
window.goProfile = goProfile;

function goFavorites() {
    const t = localStorage.getItem(TOKEN_KEY);
    const hasToken = t && t.trim().length > 0;
    window.location.href = hasToken ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;

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

function asset(path) {
    return encodeURI(path);
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

// -------------------- FAVORITE UI --------------------
function setFavUI(isFav, enabled) {
    if (!favBtn) return;
    favBtn.disabled = !enabled;
    favBtn.classList.toggle("on", !!isFav);
    if (favText) favText.textContent = isFav ? "Favoride" : "Favori";
    if (favIcon) favIcon.textContent = isFav ? "❤" : "♡";
}

async function fetchIsFavoritedFallback(listingId) {
    try {
        const list = await fetchJsonAuth(FAV_LIST_URL, { method: "GET" });
        const arr = Array.isArray(list) ? list : (list?.items || list?.favorites || []);
        return Array.isArray(arr) && arr.some(x => Number(x.listingId) === Number(listingId));
    } catch {
        return false;
    }
}

async function toggleFavorite(listingId, currentIsFav) {
    const token = getTokenOrNull();
    if (!token) {
        showAlert("err", "Favori için giriş yapmalısın.");
        return currentIsFav;
    }

    const url = currentIsFav ? FAV_DEL_URL(listingId) : FAV_ADD_URL(listingId);
    const method = currentIsFav ? "DELETE" : "POST";

    await fetchJsonAuth(url, { method });
    return !currentIsFav;
}

// -------------------- GALLERY (✅ FIXED) --------------------
let galleryUrls = [];
let galleryIndex = 0;

function toPublicImg(u) {
    if (!u) return null;

    const s = String(u).trim();
    if (!s) return null;

    // absolute url
    if (s.startsWith("http://") || s.startsWith("https://")) return s;

    // already root-based like /uploads/... or /images...
    if (s.startsWith("/")) return s;

    // "uploads/abc.jpg" => "/uploads/abc.jpg"
    if (s.startsWith("uploads/")) return "/" + s;

    // only filename => assume uploads
    return "/uploads/" + s;
}

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

    galleryUrls = imgs
        .map(x => toPublicImg(x?.imagePath))
        .filter(Boolean);

    if (!galleryUrls.length) {
        const cover = toPublicImg(detail?.coverImageUrl);
        galleryUrls = [cover || "/imagesforapp/logo2.png"];
    }

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

    // main image fallback (broken)
    if (mainImg) {
        mainImg.onerror = () => { mainImg.src = "/imagesforapp/logo2.png"; };
    }

    console.log("[vehicleinfo] galleryUrls =", galleryUrls);
}

// -------------------- KV RENDER --------------------
function kvRow(label, value) {
    return `
    <div class="kv-row">
      <div class="kv-k">${escapeHtml(label)}</div>
      <div class="kv-v">${escapeHtml(value ?? "—")}</div>
    </div>
  `;
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
        kvRow("Kasa", car.bodyType) +
        kvRow("Yakıt", car.fuelType) +
        kvRow("Vites", car.transmission) +
        kvRow("Renk", car.color);
}

function renderStoreHeader(detail) {
    const s = detail.store;
    if (!s) return;

    if (storeNameEl) storeNameEl.textContent = s.storeName || "Mağaza";
    if (storeLocEl) storeLocEl.textContent = [s.city, s.district].filter(Boolean).join(" • ") || "—";

    if (storeAvatar) {
        const letter = (s.storeName || "M").trim().charAt(0).toUpperCase();
        storeAvatar.textContent = letter;
    }

    if (storeBox && s.id != null) {
        storeBox.style.cursor = "pointer";
        storeBox.onclick = () => {
            window.location.href = `/templates/store.html?storeId=${encodeURIComponent(s.id)}`;
        };
    }
}

function renderStorePhone(detail) {
    const s = detail.store;
    if (!storePhoneA) return;
    const phone = s?.phone || "";
    storePhoneA.textContent = phone || "—";
    storePhoneA.href = phone ? `tel:${phone}` : "#";
}

/* -------------------- EXPERT REPORT -------------------- */
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

/* -------------------- ✅ KROKİ (AUTO PATH FIX) -------------------- */
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
        .map(p => ({ part: p, status: "ORIGINAL", note: "Varsayılan: mağaza expertiz girmedi" }));

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

/* -------------------- EXPERT RENDER -------------------- */
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

// -------------------- INQUIRY (CLIENT ONLY) --------------------
let currentListingId = null;
let currentStoreId = null;

function setGuestModeVisible(visible) {
    if (!guestFields) return;
    guestFields.style.display = visible ? "grid" : "none";
}

function renderMessageBubble(m) {
    const cls = (m.senderType === "STORE") ? "bubble store" : "bubble client";
    const who = (m.senderType === "STORE") ? "Mağaza" : "Müşteri";
    return `
    <div class="${cls}">
      <div>${escapeHtml(m.content || "")}</div>
      <div class="meta">
        <span>${escapeHtml(who)}</span>
        <span>•</span>
        <span>${escapeHtml(fmtDate(m.sentAt))}</span>
      </div>
    </div>
  `;
}

function renderThread(thread) {
    if (!messagesEl) return;
    const msgs = Array.isArray(thread?.messages) ? thread.messages : [];
    if (!msgs.length) {
        messagesEl.innerHTML = `<div class="muted tiny">Henüz mesaj yok.</div>`;
        return;
    }
    messagesEl.innerHTML = msgs.map(renderMessageBubble).join("");
    messagesEl.scrollTop = messagesEl.scrollHeight;
}

// ✅ listingId ile thread getir (AUTH zorunlu)
async function loadThreadByListing(listingId) {
    if (!listingId) return null;
    const data = await fetchJsonAuth(INQ_THREAD_BY_LISTING_URL(listingId), { method: "GET" });
    renderThread(data);
    return data;
}

// ✅ upsert + mesaj gönder (AUTH zorunlu)
async function upsertInquiryAndSendMessage({ listingId, storeId, message }) {
    return await fetchJsonAuth(INQ_UPSERT_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ listingId, storeId, message })
    });
}

// ✅ SEND button handler (CLIENT ONLY)
async function handleSendClick() {
    hideAlert();

    const token = getTokenOrNull();
    if (!token) {
        showAlert("err", "Mesaj göndermek için giriş yapmalısın.");
        setInquiryHint("Giriş yapmalısın.");
        return;
    }

    if (!currentListingId || !currentStoreId) {
        showAlert("err", "İlan/mağaza bilgisi yok.");
        return;
    }

    const text = (msgTa?.value || "").trim();
    if (!text) {
        showAlert("err", "Mesaj boş olamaz.");
        return;
    }

    try {
        sendBtn && (sendBtn.disabled = true);
        setInquiryHint("Gönderiliyor...");

        await upsertInquiryAndSendMessage({
            listingId: currentListingId,
            storeId: currentStoreId,
            message: text
        });

        msgTa.value = "";
        showAlert("ok", "Mesaj gönderildi.");

        await loadThreadByListing(currentListingId);

        setInquiryHint("Mesaj yazıp Gönder'e bas.");
    } catch (e) {
        console.error(e);
        const msg = String(e?.message || "");
        if (msg.toLowerCase().includes("unauthorized")) {
            showAlert("err", "Oturum süren dolmuş. Tekrar giriş yap.");
            localStorage.removeItem(TOKEN_KEY);
            window.location.href = "/templates/Login.html";
            return;
        }
        showAlert("err", msg || "Mesaj gönderilemedi.");
        setInquiryHint("—");
    } finally {
        sendBtn && (sendBtn.disabled = false);
    }
}

async function initInquiryUI(detail) {
    currentListingId = detail?.id || currentListingId;
    currentStoreId = detail?.store?.id || currentStoreId;

    // guest kapalı (UI gizle)
    setGuestModeVisible(false);

    if (!currentListingId || !currentStoreId) {
        setInquiryHint("Mesajlaşma için ilan/mağaza bilgisi eksik.");
        sendBtn && (sendBtn.disabled = true);
        return;
    }

    const token = getTokenOrNull();
    if (!token) {
        setInquiryHint("Mesaj göndermek için giriş yapmalısın.");
        sendBtn && (sendBtn.disabled = true);
        renderThread({ messages: [] });
        return;
    }

    setInquiryHint("Mesaj yazıp Gönder'e bas.");
    sendBtn && (sendBtn.disabled = false);

    await loadThreadByListing(currentListingId).catch(() => { });
}

// -------------------- MAIN LOAD --------------------
async function loadDetail(listingId) {
    hideAlert();

    const token = getTokenOrNull();
    const headers = {};
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const detail = await fetchJson(DETAIL_URL(listingId), { method: "GET", headers });

    currentListingId = detail?.id || listingId;
    currentStoreId = detail?.store?.id || null;

    if (titleEl) titleEl.textContent = detail.title || "İlan";
    if (priceEl) priceEl.textContent = money(detail.price, detail.currency);

    const car = detail.car || {};
    if (metaPill) {
        const text = [detail.city, car.year].filter(Boolean).join(" • ") || "—";
        metaPill.textContent = text;
    }

    if (favTotalEl) favTotalEl.textContent = String(detail.favoriteCount ?? 0);
    if (viewTotalEl) viewTotalEl.textContent = String(detail.viewCount ?? 0);

    renderKv(detail);
    renderStoreHeader(detail);
    renderStorePhone(detail);
    renderGallery(detail);

    await loadExpertReportByListingId(listingId, detail);

    if (token) {
        fetchJsonAuth(VIEW_ONCE_URL(listingId), { method: "POST" })
            .then(() => {
                const cur = Number(viewTotalEl?.textContent || "0") || 0;
                if (viewTotalEl) viewTotalEl.textContent = String(cur + 1);
            })
            .catch(() => { });
    }

    const hasToken = !!token;

    let current = detail.favoritedByMe === true;
    if (hasToken && detail.favoritedByMe == null) {
        current = await fetchIsFavoritedFallback(listingId);
    }

    setFavUI(current, hasToken);

    if (favBtn) {
        favBtn.onclick = async () => {
            try {
                const next = await toggleFavorite(listingId, current);
                current = next;
                setFavUI(current, true);

                const curFav = Number(favTotalEl?.textContent || "0") || 0;
                if (favTotalEl) favTotalEl.textContent = String(current ? (curFav + 1) : Math.max(0, curFav - 1));

                showAlert("ok", current ? "Favoriye eklendi." : "Favoriden çıkarıldı.");
            } catch (e) {
                const msg = String(e?.message || "");
                if (msg.toLowerCase().includes("unauthorized")) {
                    showAlert("err", "Favori için giriş yapmalısın.");
                    localStorage.removeItem(TOKEN_KEY);
                    window.location.href = "/templates/Login.html";
                    return;
                }
                showAlert("err", msg || "Favori işlemi başarısız.");
            }
        };
    }

    // ✅ inquiry init
    await initInquiryUI(detail);
}

document.addEventListener("DOMContentLoaded", async () => {
    console.log("[vehicleinfo] loaded");

    applyThemeFromStorage();
    initThemeToggle();

    ASSET_CONF = await detectAssetConfig();
    console.log("[KROKİ] asset config:", ASSET_CONF);

    initSketchImages();

    const id = getListingIdFromQuery();
    if (!id) {
        showAlert("err", "İlan id bulunamadı. Filtre sayfasından tekrar tıkla.");
        return;
    }

    console.log("sendBtn =", sendBtn);
    sendBtn?.addEventListener("click", () => {
        console.log("SEND CLICK");
        handleSendClick();
    });

    try {
        await loadDetail(id);
    } catch (e) {
        console.error(e);
        showAlert("err", e.message || "İlan detayı alınamadı.");
    }
});
