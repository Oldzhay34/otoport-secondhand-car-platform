"use strict";

console.log("[WAL] JS LOADED ✅");

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";

function $(id){ return document.getElementById(id); }

function token(){ return localStorage.getItem("token"); }

function applyTheme(theme){
    const t = theme === "dark" ? "dark" : "light";
    document.body.setAttribute("data-theme", t);
    localStorage.setItem(THEME_KEY, t);
}

function initThemeToggle(){
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    applyTheme(saved);
    const toggle = $("themeToggle");
    if (!toggle) return;
    toggle.checked = saved === "dark";
    toggle.addEventListener("change", () => applyTheme(toggle.checked ? "dark" : "light"));
}

function showAlert(type, msg){
    const el = $("alert");
    if (!el) return;
    if (!msg){
        el.style.display = "none";
        el.textContent = "";
        el.className = "alert";
        return;
    }
    el.style.display = "block";
    el.textContent = msg;
    el.className = "alert " + (type === "ok" ? "ok" : "err");
}

async function apiGet(path){
    const t = token();
    const res = await fetch(API_BASE + path, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            ...(t ? { "Authorization": "Bearer " + t } : {}),
        },
    });

    const text = await res.text();
    let body = text;
    try { body = JSON.parse(text); } catch {}

    if (!res.ok){
        const msg = (body && (body.message || body.error || body.details)) ||
            (typeof body === "string" && body) || ("HTTP " + res.status);
        throw new Error(msg);
    }
    return body;
}

async function apiPost(path, data){
    const t = token();
    const res = await fetch(API_BASE + path, {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            ...(t ? { "Authorization": "Bearer " + t } : {}),
        },
        body: JSON.stringify(data || {}),
    });

    const text = await res.text();
    let body = text;
    try { body = JSON.parse(text); } catch {}

    if (!res.ok){
        const msg = (body && (body.message || body.error || body.details)) ||
            (typeof body === "string" && body) || ("HTTP " + res.status);
        throw new Error(msg);
    }
    return body;
}

function fmtTime(iso){
    try{
        const d = new Date(iso);
        return new Intl.DateTimeFormat("tr-TR", {
            timeZone: "Europe/Istanbul",
            year:"numeric", month:"2-digit", day:"2-digit",
            hour:"2-digit", minute:"2-digit", second:"2-digit"
        }).format(d);
    }catch{ return String(iso || ""); }
}

function statusBadge(code){
    const c = Number(code || 0);
    let cls = "warn";
    if (c >= 200 && c < 400) cls = "ok";
    if (c >= 400) cls = "err";
    return `<span class="bad ${cls}">${c || "—"}</span>`;
}

function clip(s, n){
    if (s == null) return "";
    s = String(s);
    if (s.length <= n) return s;
    return s.slice(0, n) + "…";
}

function renderRows(list){
    const tbody = $("rows");
    if (!tbody) return;
    tbody.innerHTML = "";

    for (const r of (list || [])){
        const actor = `${r.actorType || "—"}:${r.actorId ?? "—"}`;
        const req = `${r.method || "—"} ${r.path || "—"}${r.queryString ? ("?" + r.queryString) : ""}`;

        const bodyPreview = [
            r.requestBody ? ("REQ:\n" + clip(r.requestBody, 900)) : "",
            r.responseBody ? ("\n\nRES:\n" + clip(r.responseBody, 900)) : ""
        ].join("");

        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td class="mono">${fmtTime(r.createdAt)}</td>
      <td class="mono">${actor}</td>
      <td class="mono">${clip(req, 180)}</td>
      <td>${statusBadge(r.status)}</td>
      <td class="mono">${r.ipAddress || "—"}</td>
      <td title="${(r.userAgent || "").replaceAll('"','&quot;')}" class="mono">${clip(r.userAgent || "—", 70)}</td>
      <td class="bodycell">${bodyPreview ? `<pre class="mono">${bodyPreview}</pre>` : `<span class="mono">—</span>`}</td>
    `;
        tbody.appendChild(tr);
    }

    $("count").textContent = String((list || []).length);
    $("lastFetch").textContent = fmtTime(new Date().toISOString());
}

function buildSearchPayload(){
    const limit = Number($("limit")?.value || 100);
    const sort = $("sort")?.value || "desc";
    const method = ($("method")?.value || "").trim();
    const status = ($("status")?.value || "").trim();
    const actorId = ($("actorId")?.value || "").trim();
    const pathContains = ($("pathContains")?.value || "").trim();
    const q = ($("q")?.value || "").trim();

    return {
        limit,
        sort,
        actorType: "ADMIN",
        actorId: actorId ? Number(actorId) : null,
        method: method || null,
        status: status ? Number(status) : null,
        pathContains: pathContains || null,
        q: q || null,
        from: null,
        to: null
    };
}

async function refreshRecent(){
    try{
        showAlert(null, null);
        const limit = Number($("limit")?.value || 100);
        const sort = $("sort")?.value || "desc";
        const data = await apiGet(`/api/admin/wal/recent?limit=${encodeURIComponent(limit)}&sort=${encodeURIComponent(sort)}`);
        renderRows(data);
    }catch(e){
        console.error(e);
        showAlert("err", "WAL alınamadı: " + (e.message || e));
        renderRows([]);
    }
}

async function applySearch(){
    try{
        showAlert(null, null);
        const payload = buildSearchPayload();
        const data = await apiPost("/api/admin/wal/search", payload);
        renderRows(data);
    }catch(e){
        console.error(e);
        showAlert("err", "Arama başarısız: " + (e.message || e));
    }
}

// nav helpers
function goAdminHome(){ window.location.href = "/templates/adminhome.html"; }
function goAuditLogs(){ window.location.href = "/templates/auditlog.html"; }
function logout(){
    localStorage.removeItem("token");
    window.location.href = "/templates/login.html";
}

window.goAdminHome = goAdminHome;
window.goAuditLogs = goAuditLogs;
window.logout = logout;
window.refreshRecent = refreshRecent;

window.addEventListener("DOMContentLoaded", async () => {
    initThemeToggle();

    $("btnSearch")?.addEventListener("click", applySearch);
    $("btnApply")?.addEventListener("click", applySearch);

    $("q")?.addEventListener("keydown", (e) => {
        if (e.key === "Enter") applySearch();
    });

    await refreshRecent();
});
