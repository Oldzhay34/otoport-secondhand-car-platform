"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const HOME_URL = `${API_BASE}/api/store/home`;
const DELETE_LISTING_URL = (id) => `${API_BASE}/api/store/listings/${id}`;
const UNREAD_COUNT_URL = `${API_BASE}/api/store/notifications/unread-count`;
const INBOX_UNREAD_COUNT_URL = `${API_BASE}/api/store/inquiries/unread-count`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const listEl = $("list");
const storeMeta = $("storeMeta");
const countMeta = $("countMeta");
const qEl = $("q");
const searchBtn = $("searchBtn");

/* ✅ modal search */
const searchModal = $("searchModal");
const openSearchBtn = $("openSearch");
const closeSearchBtn = $("closeSearch");
const stickySearchBtn = $("stickySearch");
const qMobile = $("qMobile");
const searchBtnMobile = $("searchBtnMobile");

/* =========================
   HELPERS
========================= */
function showAlert(type, msg){
    if (!alertBox) return;
    alertBox.className = "alert " + (type === "ok" ? "ok" : "err");
    alertBox.textContent = msg;
    alertBox.style.display = "block";
}
function hideAlert(){
    if (!alertBox) return;
    alertBox.style.display = "none";
    alertBox.textContent = "";
}

function applyThemeFromStorage(){
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.body.setAttribute("data-theme", saved);
}
function initThemeToggle(){
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

async function fetchJson(url, options = {}){
    const res = await fetch(url, { ...options, cache:"no-store" });
    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(()=>null)
        : await res.text().catch(()=>null);

    if (!res.ok){
        const msg = (body && (body.message || body.error)) ||
            (typeof body === "string" ? body : "") ||
            `HTTP ${res.status}`;
        throw new Error(msg);
    }
    return body;
}

function money(v, currency){
    if (v == null) return "—";
    try {
        return new Intl.NumberFormat("tr-TR", { style:"currency", currency: currency || "TRY" }).format(Number(v));
    } catch {
        return `${v} ${currency || ""}`.trim();
    }
}
function escapeHtml(s){
    return String(s ?? "").replace(/[&<>"']/g, m => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[m]));
}

/* ✅ img url normalizer (KAPAK FOTO FIX) */
function normalizeImgUrl(p){
    if (!p) return "/imagesforapp/logo2.png";

    const s = String(p).trim();
    if (!s) return "/imagesforapp/logo2.png";

    // tam url ise dokunma
    if (s.startsWith("http://") || s.startsWith("https://")) return s;

    // /uploads/... ise API_BASE ile tamamla (cross-origin için garanti)
    if (s.startsWith("/uploads/")) return API_BASE + s;

    // uploads/... (başında slash yoksa)
    if (s.startsWith("uploads/")) return API_BASE + "/" + s;

    // diğer relative pathler (static içindekiler)
    if (s.startsWith("/")) return s;
    return "/" + s;
}

/* NAV */
function logout(){
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goStoreHome(){ window.location.href = "/templates/storehome.html"; }
window.goStoreHome = goStoreHome;

function goCreateListing(){ window.location.href = "/templates/storecreateListing.html"; }
window.goCreateListing = goCreateListing;

function goNotifications(){ window.location.href = "/templates/storenotifications.html"; }
window.goNotifications = goNotifications;

function goInbox(){ window.location.href = "/templates/storeinbox.html"; }
window.goInbox = goInbox;

function goStoreProfile(){ window.location.href = "/templates/storeprofile.html"; }
window.goStoreProfile = goStoreProfile;

/* =========================
   UNREAD COUNTS
========================= */
async function loadUnreadCount(){
    const badge = $("notifBadge");
    if (!badge) return;

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()) return;

    try{
        const data = await fetchJson(UNREAD_COUNT_URL, {
            method:"GET",
            headers: { Authorization: `Bearer ${token.trim()}` }
        });

        const c = Number(data?.count ?? 0);
        if (c > 0){
            badge.textContent = String(c);
            badge.style.display = "flex";
        } else {
            badge.style.display = "none";
        }
    } catch {
        badge.style.display = "none";
    }
}

async function loadInboxUnreadCount(){
    const badge = $("inboxBadge");
    if (!badge) return;

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()) return;

    try{
        const data = await fetchJson(INBOX_UNREAD_COUNT_URL, {
            method:"GET",
            headers: { Authorization: `Bearer ${token.trim()}` }
        });

        const c = Number(data?.count ?? 0);
        if (c > 0){
            badge.textContent = String(c);
            badge.style.display = "flex";
        } else {
            badge.style.display = "none";
        }
    } catch {
        badge.style.display = "none";
    }
}

/* =========================
   ROW RENDER
========================= */
function rowHtml(item){
    const img = normalizeImgUrl(item.coverImageUrl);
    const status = item.status || "—";
    const price = money(item.price, item.currency);

    return `
    <div class="row" data-id="${item.id}">
      <div class="cover">
        <img src="${escapeHtml(img)}" alt="car"
             onerror="this.onerror=null;this.src='/imagesforapp/logo2.png';">
      </div>

      <div class="desc">
        <div class="t">${escapeHtml(item.title || "İlan")}</div>
        <div class="s">
          <span>Durum: ${escapeHtml(status)}</span>
          <span>Fiyat: ${escapeHtml(price)}</span>
        </div>
      </div>

      <div class="row-actions">
        <div class="btn-group">
          <button class="btn primary" data-act="update" data-id="${item.id}">update</button>
          <button class="btn danger" data-act="delete" data-id="${item.id}">delete</button>
        </div>

        <button class="kebab" type="button" aria-label="İşlemler" data-act="sheet" data-id="${item.id}">⋯</button>
      </div>
    </div>
  `;
}

/* =========================
   ACTION SHEET
========================= */
let sheetEl = null;
let sheetListingId = null;

function ensureSheet(){
    if (sheetEl) return sheetEl;

    const div = document.createElement("div");
    div.className = "sheet";
    div.id = "actionSheet";
    div.innerHTML = `
    <div class="sheet-backdrop" data-sheet-close="1"></div>
    <div class="sheet-panel" role="dialog" aria-modal="true" aria-label="İşlemler">
      <div class="sheet-grab"></div>

      <div class="sheet-head">
        <div>
          <div class="sheet-title" id="sheetTitle">İlan işlemleri</div>
          <div class="sheet-sub" id="sheetSub">Seçili ilan</div>
        </div>
        <button class="sheet-x" type="button" aria-label="Kapat" data-sheet-close="1">×</button>
      </div>

      <div class="sheet-actions">
        <button class="sheet-btn" type="button" data-sheet-act="update">
          Düzenle <span>✎</span>
        </button>
        <button class="sheet-btn danger" type="button" data-sheet-act="delete">
          Sil <span>🗑</span>
        </button>
        <button class="sheet-btn" type="button" data-sheet-close="1">
          Vazgeç <span>⟲</span>
        </button>
      </div>
    </div>
  `;
    document.body.appendChild(div);
    sheetEl = div;

    sheetEl.addEventListener("click", (e) => {
        const close = e.target.closest("[data-sheet-close]");
        if (close) closeSheet();
    });

    sheetEl.addEventListener("click", async (e) => {
        const actBtn = e.target.closest("[data-sheet-act]");
        if (!actBtn) return;

        const act = actBtn.dataset.sheetAct;
        const id = sheetListingId;
        if (!id) return;

        if (act === "update"){
            closeSheet();
            window.location.href = `/templates/storelistingedit.html?id=${encodeURIComponent(id)}`;
            return;
        }

        if (act === "delete"){
            closeSheet();
            if (!confirm("Bu ilanı silmek istediğine emin misin?")) return;

            try{
                hideAlert();
                const token = localStorage.getItem(TOKEN_KEY);
                await fetchJson(DELETE_LISTING_URL(id), {
                    method: "DELETE",
                    headers: { Authorization: `Bearer ${token}` }
                });
                showAlert("ok", "İlan silindi.");
                await loadHome(qEl?.value || "");
            } catch (err){
                showAlert("err", err.message || "Silme işlemi başarısız.");
            }
        }
    });

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && sheetEl?.classList.contains("show")) closeSheet();
    });

    return sheetEl;
}

function openSheet({ id, title, sub }){
    ensureSheet();
    sheetListingId = id;

    const t = document.getElementById("sheetTitle");
    const s = document.getElementById("sheetSub");
    if (t) t.textContent = title || "İlan işlemleri";
    if (s) s.textContent = sub || `İlan #${id}`;

    sheetEl.classList.add("show");
    document.body.style.overflow = "hidden";
}

function closeSheet(){
    if (!sheetEl) return;
    sheetEl.classList.remove("show");
    document.body.style.overflow = "";
    sheetListingId = null;
}

function bindRowActions(){
    listEl?.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-act]");
        if (!btn) return;

        const act = btn.dataset.act;
        const id = Number(btn.dataset.id);
        if (!Number.isFinite(id)) return;

        if (act === "sheet"){
            const row = btn.closest(".row");
            const title = row?.querySelector(".desc .t")?.textContent?.trim() || "İlan";
            const sub = row?.querySelector(".desc .s")?.textContent?.trim() || `İlan #${id}`;
            openSheet({ id, title, sub });
            return;
        }

        if (act === "update"){
            window.location.href = `/templates/storelistingedit.html?id=${encodeURIComponent(id)}`;
            return;
        }

        if (act === "delete"){
            if (!confirm("Bu ilanı silmek istediğine emin misin?")) return;

            try{
                hideAlert();
                const token = localStorage.getItem(TOKEN_KEY);
                await fetchJson(DELETE_LISTING_URL(id), {
                    method: "DELETE",
                    headers: { Authorization: `Bearer ${token}` }
                });
                showAlert("ok", "İlan silindi.");
                await loadHome(qEl?.value || "");
            } catch (err){
                showAlert("err", err.message || "Silme işlemi başarısız.");
            }
        }
    });
}

/* =========================
   HOME LOAD
========================= */
async function loadHome(q){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    const url = q && q.trim()
        ? `${HOME_URL}?q=${encodeURIComponent(q.trim())}`
        : HOME_URL;

    const data = await fetchJson(url, {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    if (storeMeta){
        const loc = [data.city, data.district].filter(Boolean).join(" • ");
        storeMeta.textContent = `${data.storeName || "Mağaza"}${loc ? " • " + loc : ""}${data.verified ? " • Verified" : ""}`;
    }

    const items = Array.isArray(data.listings) ? data.listings : [];
    if (countMeta) countMeta.textContent = `${items.length} ilan`;

    if (listEl){
        if (!items.length){
            listEl.innerHTML = `<div class="muted tiny">Henüz ilan yok.</div>`;
        } else {
            listEl.innerHTML = items.map(rowHtml).join("");
        }
    }
}

/* ✅ modal logic */
function openSearchModal(){
    if (!searchModal) return;
    searchModal.classList.add("show");
    searchModal.setAttribute("aria-hidden","false");
    if (qMobile){
        qMobile.value = qEl?.value || "";
        setTimeout(() => qMobile.focus(), 30);
    }
}
function closeSearchModal(){
    if (!searchModal) return;
    searchModal.classList.remove("show");
    searchModal.setAttribute("aria-hidden","true");
}

function initSearchModal(){
    openSearchBtn?.addEventListener("click", openSearchModal);
    stickySearchBtn?.addEventListener("click", openSearchModal);
    closeSearchBtn?.addEventListener("click", closeSearchModal);

    searchModal?.addEventListener("click", (e) => {
        const t = e.target;
        if (t && t.dataset && t.dataset.close === "1") closeSearchModal();
    });

    window.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && searchModal?.classList.contains("show")) closeSearchModal();
    });

    searchBtnMobile?.addEventListener("click", async () => {
        const v = qMobile?.value || "";
        if (qEl) qEl.value = v;
        await loadHome(v);
        await loadUnreadCount().catch(()=>{});
        await loadInboxUnreadCount().catch(()=>{});
        closeSearchModal();
    });

    qMobile?.addEventListener("keydown", async (e) => {
        if (e.key === "Enter") searchBtnMobile?.click();
    });
}

/* =========================
   INIT
========================= */
document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();
    bindRowActions();
    initSearchModal();

    searchBtn?.addEventListener("click", async () => {
        await loadHome(qEl?.value || "");
        await loadUnreadCount().catch(()=>{});
        await loadInboxUnreadCount().catch(()=>{});
    });

    qEl?.addEventListener("keydown", async (e) => {
        if (e.key === "Enter") {
            await loadHome(qEl?.value || "");
            await loadUnreadCount().catch(()=>{});
            await loadInboxUnreadCount().catch(()=>{});
        }
    });

    try {
        await loadHome("");
    } catch (e) {
        showAlert("err", e.message || "Ana sayfa yüklenemedi.");
    }

    await loadUnreadCount().catch(()=>{});
    setInterval(() => loadUnreadCount().catch(()=>{}), 20000);

    await loadInboxUnreadCount().catch(()=>{});
    setInterval(() => loadInboxUnreadCount().catch(()=>{}), 20000);
});
