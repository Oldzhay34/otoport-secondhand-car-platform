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
        // home sayfasında badge hatası kullanıcıyı bozmasın
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

function logout(){
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

// Navigasyon (senin sayfa isimlerine göre değiştir)
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

function rowHtml(item){
    const img = item.coverImageUrl || "/imagesforapp/logo2.png";
    const status = item.status || "—";
    const price = money(item.price, item.currency);

    return `
    <div class="row">
      <div class="cover"><img src="${escapeHtml(img)}" alt="car"></div>

      <div class="desc">
        <div class="t">${escapeHtml(item.title || "İlan")}</div>
        <div class="s">
          <span>Durum: ${escapeHtml(status)}</span>
          <span>Fiyat: ${escapeHtml(price)}</span>
        </div>
      </div>

      <button class="btn primary" data-act="update" data-id="${item.id}">update</button>
      <button class="btn danger" data-act="delete" data-id="${item.id}">delete</button>
    </div>
  `;
}

function bindRowActions(){
    listEl?.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-act]");
        if (!btn) return;

        const act = btn.dataset.act;
        const id = Number(btn.dataset.id);

        if (!Number.isFinite(id)) return;

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
                await loadHome(); // refresh
            } catch (err){
                showAlert("err", err.message || "Silme işlemi başarısız.");
            }
        }
    });
}

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

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();
    bindRowActions();

    searchBtn?.addEventListener("click", async () => {
        await loadHome(qEl?.value || "");
        await loadUnreadCount().catch(()=>{});
    });

    qEl?.addEventListener("keydown", async (e) => {
        if (e.key === "Enter") {
            await loadHome(qEl?.value || "");
            await loadUnreadCount().catch(()=>{});
        }
    });

    try {
        await loadHome("");
    } catch (e) {
        showAlert("err", e.message || "An sayfa yüklenemedi.");
    }

    // unread count home'u bozmasın
    await loadUnreadCount().catch(()=>{});
    setInterval(() => loadUnreadCount().catch(()=>{}), 20000);

    await loadInboxUnreadCount().catch(()=>{});
    setInterval(() => loadInboxUnreadCount().catch(()=>{}), 20000);
});
