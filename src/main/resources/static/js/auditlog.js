"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const sub = $("sub");
const rowsEl = $("rows");
const sortSel = $("sortSel");
const limitSel = $("limitSel");
const refreshBtn = $("refreshBtn");

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

async function fetchJson(url, options = {}){
    const res = await fetch(url, { ...options, cache:"no-store" });
    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(()=>null)
        : await res.text().catch(()=>null);

    if (!res.ok){
        const msg =
            (body && (body.message || body.error)) ||
            (typeof body === "string" ? body : "") ||
            `HTTP ${res.status}`;
        throw new Error(msg);
    }
    return body;
}

function escapeHtml(s){
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[m]));
}

function logout(){
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

// Admin home route sende neyse ona göre değiştir
function goAdminHome(){
    window.location.href = "/templates/adminhome.html";
}
window.goAdminHome = goAdminHome;

function fmtTime(iso){
    if (!iso) return "—";
    try{
        const d = new Date(iso);
        return d.toLocaleString("tr-TR");
    }catch(e){
        return String(iso);
    }
}

function renderRows(list){
    if (!rowsEl) return;
    rowsEl.innerHTML = "";

    if (!Array.isArray(list) || list.length === 0){
        rowsEl.innerHTML = `<tr><td colspan="7" class="muted">Kayıt yok.</td></tr>`;
        return;
    }

    for (const r of list){
        const actor = `${escapeHtml(r.actorType || "—")}#${escapeHtml(r.actorId ?? "—")}`;
        const entity = `${escapeHtml(r.entityType || "—")}#${escapeHtml(r.entityId ?? "—")}`;

        rowsEl.insertAdjacentHTML("beforeend", `
      <tr>
        <td><span class="badge">${escapeHtml(fmtTime(r.createdAt))}</span></td>
        <td>${actor}</td>
        <td><span class="badge">${escapeHtml(r.action || "—")}</span></td>
        <td>${entity}</td>
        <td class="code">${escapeHtml(r.details || "")}</td>
        <td>${escapeHtml(r.ipAddress || "")}</td>
        <td class="code">${escapeHtml(r.userAgent || "")}</td>
      </tr>
    `);
    }
}

async function load(){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token){
        window.location.href = "/templates/Login.html";
        return;
    }

    const sort = (sortSel?.value || "desc").toLowerCase();
    const limit = Number(limitSel?.value || "200");

    if (sub) sub.textContent = `Sıralama: ${sort.toUpperCase()} • Limit: ${limit}`;

    rowsEl.innerHTML = `<tr><td colspan="7" class="muted">Yükleniyor…</td></tr>`;

    const url = `${API_BASE}/api/admin/audit-logs?limit=${encodeURIComponent(limit)}&sort=${encodeURIComponent(sort)}`;

    const data = await fetchJson(url, {
        method: "GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    renderRows(data);
}

document.addEventListener("DOMContentLoaded", () => {
    refreshBtn?.addEventListener("click", () => load());
    sortSel?.addEventListener("change", () => load());
    limitSel?.addEventListener("change", () => load());
    load().catch(e => showAlert("err", e.message || "Audit loglar alınamadı."));
});
