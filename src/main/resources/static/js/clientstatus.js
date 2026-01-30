"use strict";

console.log("[CLIENTSTATUS] JS LOADED ✅");

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";

function $(id){ return document.getElementById(id); }
function token(){ return localStorage.getItem("token"); }

function applyTheme(theme) {
    const t = theme === "dark" ? "dark" : "light";
    document.body.setAttribute("data-theme", t);
    localStorage.setItem(THEME_KEY, t);
}

function initThemeToggle() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    applyTheme(saved);

    const toggle = $("themeToggle");
    if (!toggle) return;
    toggle.checked = saved === "dark";
    toggle.addEventListener("change", () => applyTheme(toggle.checked ? "dark" : "light"));
}

function showOk(msg){
    const el = $("alertOk");
    if(!el) return;
    el.textContent = msg || "";
    el.style.display = msg ? "block" : "none";
    if(msg) setTimeout(() => { el.style.display="none"; }, 2000);
}

function showErr(msg){
    const el = $("alertErr");
    if(!el) return;
    el.textContent = msg || "";
    el.style.display = msg ? "block" : "none";
}

async function api(path, opts = {}) {
    const t = token();
    const res = await fetch(API_BASE + path, {
        ...opts,
        headers: {
            "Accept": "application/json",
            ...(opts.body ? {"Content-Type":"application/json"} : {}),
            ...(t ? {"Authorization":"Bearer " + t} : {}),
            ...(opts.headers || {})
        }
    });

    const text = await res.text();
    let body = text;
    try { body = JSON.parse(text); } catch {}

    if(!res.ok){
        const msg =
            (body && (body.message || body.error || body.details)) ||
            (typeof body === "string" && body) ||
            ("HTTP " + res.status);
        throw new Error(msg);
    }
    return body;
}

/** AccountStatus: ACTIVE | SUSPENDED | DELETED */
function statusBadge(status){
    const s = String(status || "").toUpperCase();

    if (s === "ACTIVE") {
        return `<span class="badge active"><span class="dot"></span>AKTİF</span>`;
    }
    if (s === "SUSPENDED") {
        return `<span class="badge passive"><span class="dot"></span>PASİF</span>`;
    }
    if (s === "DELETED") {
        return `<span class="badge deleted"><span class="dot"></span>SİLİNDİ</span>`;
    }
    return `<span class="badge"><span class="dot"></span>${s || "—"}</span>`;
}

function rowHtml(c){
    const full = `${c.firstName ?? ""} ${c.lastName ?? ""}`.trim();
    const st = String(c.status || "").toUpperCase();

    const isActive = st === "ACTIVE";
    const isDeleted = st === "DELETED";

    const btnText = isActive ? "Pasife Al" : "Aktife Al";
    const btnCls  = isActive ? "btn-mini danger" : "btn-mini";

    return `
    <tr data-id="${c.id}">
      <td>${c.id}</td>
      <td>${full || "—"}</td>
      <td>${c.email || "—"}</td>
      <td>${statusBadge(c.status)}</td>
      <td class="td-actions">
        <button class="${btnCls}" type="button" data-action="toggle" ${isDeleted ? "disabled" : ""}>
          ${isDeleted ? "Silindi" : btnText}
        </button>
      </td>
    </tr>
  `;
}

let allClients = [];

function render(list){
    const tb = $("tbody");
    const count = $("count");
    if(!tb) return;

    if (count) count.textContent = `${list.length} kullanıcı`;

    if(list.length === 0){
        tb.innerHTML = `<tr><td colspan="5" class="muted">Kayıt yok.</td></tr>`;
        return;
    }
    tb.innerHTML = list.map(rowHtml).join("");
}

function applyFilter(){
    const q = ($("q")?.value || "").trim().toLowerCase();
    if(!q){ render(allClients); return; }

    const filtered = allClients.filter(c => {
        const full  = `${c.firstName ?? ""} ${c.lastName ?? ""}`.toLowerCase();
        const email = (c.email ?? "").toLowerCase();
        return full.includes(q) || email.includes(q) || String(c.id).includes(q);
    });

    render(filtered);
}

async function load(){
    showErr("");
    // showOk("") istemiyorsan kaldırabilirsin
    // showOk("");

    const data = await api("/api/admin/clients/status", { method:"GET" });
    allClients = Array.isArray(data) ? data : [];
    applyFilter();
}

async function toggleClient(id){
    // local state’ten bul
    const c = allClients.find(x => String(x.id) === String(id));
    const current = String(c?.status || "").toUpperCase();

    if (current === "DELETED") {
        showErr("Silinmiş kullanıcı aktif/pasif yapılamaz.");
        return;
    }

    // ACTIVE <-> SUSPENDED
    const next = (current === "ACTIVE") ? "SUSPENDED" : "ACTIVE";

    await api(`/api/admin/clients/${encodeURIComponent(id)}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status: next })
    });

    // en sağlamı: tekrar liste çek
    await load();

    showOk(`Kullanıcı #${id} ${next === "ACTIVE" ? "AKTİF" : "PASİF"} yapıldı.`);
}

function goAuditLogs(){
    window.location.href = "/templates/auditlog.html";
}
window.goAuditLogs = goAuditLogs;

function goWal(){
    window.location.href = "/templates/whiteaheadlogging.html";
}
window.goWal = goWal;

window.addEventListener("DOMContentLoaded", async () => {
    initThemeToggle();

    $("logoutBtn")?.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/templates/login.html";
    });

    $("refreshBtn")?.addEventListener("click", async () => {
        try { await load(); } catch(e){ showErr(e.message || String(e)); }
    });

    $("q")?.addEventListener("input", applyFilter);

    // event delegation
    $("tbl")?.addEventListener("click", async (ev) => {
        const btn = ev.target.closest("button[data-action='toggle']");
        if(!btn) return;

        const tr = btn.closest("tr");
        const id = tr?.getAttribute("data-id");
        if(!id) return;

        try{
            btn.disabled = true;
            await toggleClient(id);
        }catch(e){
            showErr("İşlem başarısız: " + (e.message || e));
        }finally{
            btn.disabled = false;
        }
    });

    try{
        await load();
    }catch(e){
        showErr("Veri alınamadı: " + (e.message || e));
        render([]);
    }
});
