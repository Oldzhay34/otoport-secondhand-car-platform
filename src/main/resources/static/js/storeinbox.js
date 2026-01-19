"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

// Endpoints (backend controller aşağıda)
const INQUIRIES_URL = `${API_BASE}/api/store/inquiries`;
const THREAD_URL = (inquiryId) => `${API_BASE}/api/store/inquiries/${inquiryId}`;
const REPLY_URL = (inquiryId) => `${API_BASE}/api/store/inquiries/${inquiryId}/reply`;
const MARK_READ_URL = (inquiryId) => `${API_BASE}/api/store/inquiries/${inquiryId}/read`;

// (opsiyonel) listing'e gitmek için
const LISTING_PAGE = (listingId) => `/templates/storelistingdetail.html?id=${encodeURIComponent(listingId)}`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const metaEl = $("meta");

const qEl = $("q");
const searchBtn = $("searchBtn");
const refreshBtn = $("refreshBtn");

const threadsEl = $("threads");
const messagesEl = $("messages");

const tTitle = $("tTitle");
const tMeta = $("tMeta");
const openListingBtn = $("openListingBtn");

const replyForm = $("replyForm");
const replyText = $("replyText");
const sendBtn = $("sendBtn");

let threadsCache = [];
let activeInquiryId = null;
let activeListingId = null;

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

function goNotifications(){ window.location.href = "/templates/storenotifications.html"; }
window.goNotifications = goNotifications;

function goStoreProfile(){ window.location.href = "/templates/storeprofile.html"; }
window.goStoreProfile = goStoreProfile;

function setActiveThread(id){
    activeInquiryId = id;
    [...(threadsEl?.querySelectorAll(".thread") || [])].forEach(el => {
        el.classList.toggle("active", Number(el.dataset.id) === Number(id));
    });
}

function renderThreadRow(t){
    const unread = Number(t.unreadCount || 0) > 0;
    const who = t.clientName || t.guestName || t.clientEmail || t.guestEmail || "Müşteri";
    const listing = t.listingTitle || "İlan";
    const last = t.lastMessage || "—";

    return `
    <div class="thread" data-id="${t.inquiryId}">
      <div class="t">${escapeHtml(listing)}</div>
      <div class="s">
        <span>${escapeHtml(who)}</span>
        <span>•</span>
        <span>${escapeHtml(fmtDate(t.lastSentAt))}</span>
        ${unread ? `<span class="pill unread">${t.unreadCount}</span>` : ``}
      </div>
      <div class="muted tiny" style="margin-top:6px;">${escapeHtml(last)}</div>
    </div>
  `;
}

function renderBubble(m){
    const cls = (m.senderType === "STORE") ? "store" : "client";
    const who = (m.senderType === "STORE") ? "Store" : "Client";
    return `
    <div class="bubble ${cls}">
      <div>${escapeHtml(m.content || "")}</div>
      <div class="meta">
        <span>${who}</span>
        <span>•</span>
        <span>${escapeHtml(fmtDate(m.sentAt))}</span>
      </div>
    </div>
  `;
}

async function loadThreads(q){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    const url = q && q.trim() ? `${INQUIRIES_URL}?q=${encodeURIComponent(q.trim())}` : INQUIRIES_URL;

    const data = await fetchJson(url, {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    const items = Array.isArray(data.items) ? data.items : [];
    threadsCache = items;

    const unreadTotal = items.reduce((a, x) => a + Number(x.unreadCount || 0), 0);
    if (metaEl){
        metaEl.textContent = `Toplam: ${items.length} • Okunmamış mesaj: ${unreadTotal}`;
    }

    if (!threadsEl) return;
    if (!items.length){
        threadsEl.innerHTML = `<div class="muted tiny">Görüşme yok.</div>`;
        return;
    }
    threadsEl.innerHTML = items.map(renderThreadRow).join("");
}

async function loadThread(inquiryId){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);

    const data = await fetchJson(THREAD_URL(inquiryId), {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    // Header
    activeListingId = data.listingId || null;
    tTitle.textContent = data.listingTitle || "İlan";
    const who = data.clientName || data.guestName || data.clientEmail || data.guestEmail || "Müşteri";
    tMeta.textContent = `${who} • ${data.status || "OPEN"} • ${fmtDate(data.createdAt)}`;

    openListingBtn.disabled = !activeListingId;

    // Messages
    const msgs = Array.isArray(data.messages) ? data.messages : [];
    if (!messagesEl) return;

    if (!msgs.length){
        messagesEl.innerHTML = `<div class="muted tiny">Mesaj yok.</div>`;
    } else {
        messagesEl.innerHTML = msgs.map(renderBubble).join("");
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    // Reply enable
    replyText.disabled = false;
    sendBtn.disabled = false;

    // Açınca okundu yap (store)
    await fetchJson(MARK_READ_URL(inquiryId), {
        method:"PATCH",
        headers: { Authorization: `Bearer ${token.trim()}` }
    }).catch(()=>{});

    // Listeyi refresh (unread pill kalksın)
    await loadThreads(qEl?.value || "").catch(()=>{});
}

async function sendReply(inquiryId, text){
    const token = localStorage.getItem(TOKEN_KEY);
    await fetchJson(REPLY_URL(inquiryId), {
        method:"POST",
        headers: {
            Authorization: `Bearer ${token.trim()}`,
            "Content-Type":"application/json"
        },
        body: JSON.stringify({ message: text })
    });
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    refreshBtn?.addEventListener("click", async () => {
        await loadThreads(qEl?.value || "");
        if (activeInquiryId) await loadThread(activeInquiryId).catch(()=>{});
    });

    searchBtn?.addEventListener("click", async () => {
        await loadThreads(qEl?.value || "");
    });

    qEl?.addEventListener("keydown", async (e) => {
        if (e.key === "Enter"){
            await loadThreads(qEl?.value || "");
        }
    });

    threadsEl?.addEventListener("click", async (e) => {
        const row = e.target.closest(".thread[data-id]");
        if (!row) return;
        const inquiryId = Number(row.dataset.id);
        if (!Number.isFinite(inquiryId)) return;

        setActiveThread(inquiryId);
        await loadThread(inquiryId);
    });

    openListingBtn?.addEventListener("click", () => {
        if (!activeListingId) return;
        window.location.href = LISTING_PAGE(activeListingId);
    });

    replyForm?.addEventListener("submit", async (e) => {
        e.preventDefault();
        if (!activeInquiryId) return;

        const text = (replyText.value || "").trim();
        if (!text) return;

        try{
            replyText.value = "";
            await sendReply(activeInquiryId, text);
            await loadThread(activeInquiryId);
        } catch(err){
            showAlert("err", err.message || "Mesaj gönderilemedi.");
        }
    });

    try{
        await loadThreads("");
    } catch(e){
        showAlert("err", e.message || "Inbox yüklenemedi.");
    }
});
