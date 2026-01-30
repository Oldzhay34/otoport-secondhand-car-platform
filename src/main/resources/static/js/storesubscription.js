"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const LIST_URL = `${API_BASE}/api/admin/store-subscriptions/stores`;
const SET_PLAN_URL = (storeId) => `${API_BASE}/api/admin/store-subscriptions/${storeId}/plan`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const rowsEl = $("rows");
const qEl = $("q");

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
    alertBox.className = "alert";
}

function token(){
    const t = localStorage.getItem(TOKEN_KEY);
    return t && t.trim() ? t.trim() : null;
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

async function fetchJsonAuth(url, options = {}){
    const t = token();
    if (!t) throw new Error("Unauthorized");

    const res = await fetch(url, {
        ...options,
        headers: {
            "Content-Type":"application/json",
            ...(options.headers || {}),
            Authorization: `Bearer ${t}`
        }
    });

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

function goAdminHome(){ window.location.href = "/templates/adminhome.html"; }
window.goAdminHome = goAdminHome;

function logout(){
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/login.html";
}

function escapeHtml(s){
    return String(s ?? "").replace(/[&<>"']/g, m => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[m]));
}

function render(list){
    if (!rowsEl) return;

    const q = (qEl?.value || "").trim().toLowerCase();
    const filtered = !q ? list : list.filter(x => {
        const s = `${x.storeId||""} ${x.storeName||""} ${x.city||""} ${x.district||""}`.toLowerCase();
        return s.includes(q);
    });

    rowsEl.innerHTML = filtered.map(r => {
        const loc = [r.city, r.district].filter(Boolean).join(" / ") || "—";
        const activePill = r.isActive
            ? `<span class="pill badge-ok">● Active</span>`
            : `<span class="pill badge-off">● Passive</span>`;

        return `
      <tr data-id="${r.storeId}">
        <td>${r.storeId}</td>
        <td><b>${escapeHtml(r.storeName || "—")}</b></td>
        <td>${escapeHtml(loc)}</td>
        <td>
          <select class="plan">
            ${["BASIC","PLUS","PRO"].map(p => `<option value="${p}" ${p===r.plan?"selected":""}>${p}</option>`).join("")}
          </select>
        </td>
        <td>${r.listingLimit ?? "—"}</td>
        <td>${r.featuredLimit ?? "—"}</td>
        <td>${activePill}</td>
        <td>
          <div class="row-actions">
            <button class="btn-save" type="button">Kaydet</button>
          </div>
        </td>
      </tr>
    `;
    }).join("");
}

async function load(){
    hideAlert();
    const data = await fetchJsonAuth(LIST_URL, { method:"GET" });
    cache = Array.isArray(data) ? data : (data.items || []);
    render(cache);
}

async function savePlan(storeId, plan){
    await fetchJsonAuth(SET_PLAN_URL(storeId), {
        method:"PATCH",
        body: JSON.stringify({ plan })
    });
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    $("logoutBtn")?.addEventListener("click", logout);
    $("refreshBtn")?.addEventListener("click", load);
    qEl?.addEventListener("input", () => render(cache));

    const t = token();
    if (!t) return logout();

    rowsEl?.addEventListener("click", async (e) => {
        const btn = e.target.closest("button.btn-save");
        if (!btn) return;

        const tr = e.target.closest("tr[data-id]");
        const storeId = Number(tr.dataset.id);
        const plan = tr.querySelector("select.plan").value;

        try{
            await savePlan(storeId, plan);
            showAlert("ok", `Store #${storeId} paketi ${plan} olarak güncellendi.`);
            await load();
        } catch(err){
            showAlert("err", err.message || "Güncelleme başarısız");
        }
    });

    try{
        await load();
    } catch(e){
        showAlert("err", e.message || "Liste alınamadı");
    }
});
