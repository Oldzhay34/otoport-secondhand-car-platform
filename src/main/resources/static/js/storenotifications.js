"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const LIST_URL = (unreadOnly) =>
    `${API_BASE}/api/store/notifications${unreadOnly ? "?unreadOnly=true" : ""}`;

const READ_URL = (id) => `${API_BASE}/api/store/notifications/${id}/read`;
const READ_ALL_URL = `${API_BASE}/api/store/notifications/read-all`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const listEl = $("list");
const metaEl = $("meta");

const tabAll = $("tabAll");
const tabUnread = $("tabUnread");
const markAllBtn = $("markAllBtn");

const detailsPanel = $("detailsPanel");
const dTitle = $("dTitle");
const dMeta = $("dMeta");
const dType = $("dType");
const dMessage = $("dMessage");
const dPayload = $("dPayload");
const closeDetails = $("closeDetails");

let currentUnreadOnly = false;
let cache = [];

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
        const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || `HTTP ${res.status}`;
        throw new Error(msg);
    }
    return body;
}

function escapeHtml(s){
    return String(s ?? "").replace(/[&<>"']/g, m => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[m]));
}

function fmtDate(iso){
    if (!iso) return "—";
    try{
        const d = new Date(iso);
        return new Intl.DateTimeFormat("tr-TR", {
            year:"numeric", month:"2-digit", day:"2-digit",
            hour:"2-digit", minute:"2-digit"
        }).format(d);
    } catch {
        return iso;
    }
}

function logout(){
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goStoreHome(){ window.location.href = "/templates/storehome.html"; }
window.goStoreHome = goStoreHome;

function goCreateListing(){ window.location.href = "/templates/storecreateListing.html"; }
window.goCreateListing = goCreateListing;

function goInbox(){ window.location.href = "/templates/storeinbox.html"; }
window.goInbox = goInbox;

function goStoreProfile(){ window.location.href = "/templates/storeprofile.html"; }
window.goStoreProfile = goStoreProfile;

function renderRow(n){
    const unread = n.isRead === false;
    return `
    <div class="notif ${unread ? "unread" : ""}" data-id="${n.id}">
      <div>
        <div class="title">${escapeHtml(n.title || "Bildirim")}</div>
        <div class="sub">
          <span>${escapeHtml(n.type || "SYSTEM")}</span>
          <span>•</span>
          <span>${escapeHtml(fmtDate(n.createdAt))}</span>
          ${unread ? `<span>•</span><span>Okunmadı</span>` : ``}
        </div>
      </div>
      <div class="badge ${unread ? "unread" : ""}">${unread ? "NEW" : "OK"}</div>
    </div>
  `;
}

function showDetails(n){
    if (!detailsPanel) return;
    detailsPanel.style.display = "block";
    dTitle.textContent = n.title || "Bildirim";
    dMeta.textContent = `${fmtDate(n.createdAt)}${n.isRead ? " • Okundu" : " • Okunmadı"}`;
    dType.textContent = n.type || "SYSTEM";
    dMessage.textContent = n.message || "—";

    if (n.payloadJson) {
        try {
            const obj = JSON.parse(n.payloadJson);
            dPayload.textContent = JSON.stringify(obj, null, 2);
        } catch {
            dPayload.textContent = n.payloadJson;
        }
    } else {
        dPayload.textContent = "—";
    }
}

function hideDetails(){
    if (!detailsPanel) return;
    detailsPanel.style.display = "none";
}

async function loadList(){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    const data = await fetchJson(LIST_URL(currentUnreadOnly), {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    const items = Array.isArray(data.items) ? data.items : [];
    cache = items;

    const unreadCount = items.filter(x => x.isRead === false).length;
    if (metaEl){
        metaEl.textContent = currentUnreadOnly
            ? `Okunmamış: ${items.length}`
            : `Toplam: ${items.length} • Okunmamış: ${unreadCount}`;
    }

    if (!listEl) return;
    if (!items.length){
        listEl.innerHTML = `<div class="muted tiny">Bildirim yok.</div>`;
        return;
    }
    listEl.innerHTML = items.map(renderRow).join("");
}

async function markOneAsRead(id){
    const token = localStorage.getItem(TOKEN_KEY);
    await fetchJson(READ_URL(id), {
        method:"PATCH",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });
}

async function markAllAsRead(){
    const token = localStorage.getItem(TOKEN_KEY);
    await fetchJson(READ_ALL_URL, {
        method:"PATCH",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });
}

function setTabs(){
    if (!tabAll || !tabUnread) return;
    tabAll.classList.toggle("active", !currentUnreadOnly);
    tabUnread.classList.toggle("active", currentUnreadOnly);
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    tabAll?.addEventListener("click", async () => {
        currentUnreadOnly = false;
        setTabs();
        hideDetails();
        await loadList();
    });

    tabUnread?.addEventListener("click", async () => {
        currentUnreadOnly = true;
        setTabs();
        hideDetails();
        await loadList();
    });

    markAllBtn?.addEventListener("click", async () => {
        try{
            await markAllAsRead();
            showAlert("ok", "Tüm bildirimler okundu olarak işaretlendi.");
            hideDetails();
            await loadList();
        } catch(e){
            showAlert("err", e.message || "İşlem başarısız.");
        }
    });

    closeDetails?.addEventListener("click", hideDetails);

    listEl?.addEventListener("click", async (e) => {
        const card = e.target.closest(".notif[data-id]");
        if (!card) return;

        const id = Number(card.dataset.id);
        const n = cache.find(x => Number(x.id) === id);
        if (!n) return;

        // Detayı göster
        showDetails(n);

        // Okunmadıysa, tıklayınca okundu yap
        if (n.isRead === false){
            try{
                await markOneAsRead(id);
                n.isRead = true;
                // listeyi refreshleyelim (unread filter açıksa item listeden düşebilir)
                await loadList();
            } catch(e){
                showAlert("err", e.message || "Okundu işaretlenemedi.");
            }
        }
    });

    try{
        setTabs();
        await loadList();
    } catch(e){
        showAlert("err", e.message || "Bildirimler yüklenemedi.");
    }
});
