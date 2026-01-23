"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const STORES_URL = `${API_BASE}/api/admin/notifications/stores`;
const SEND_STORE_URL = `${API_BASE}/api/admin/notifications/send-to-store`;
const SEND_ALL_URL = `${API_BASE}/api/admin/notifications/broadcast-to-stores`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const formEl = $("form");

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

function token(){
    const t = localStorage.getItem(TOKEN_KEY);
    return t && t.trim() ? t.trim() : null;
}

async function fetchJsonAuth(url, options = {}){
    const t = token();
    if (!t) throw new Error("Unauthorized");

    const res = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
            Authorization: `Bearer ${t}`
        }
    });

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(() => null)
        : await res.text().catch(() => null);

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

async function loadStores(){
    const sel = $("storeId");
    if (!sel) return;

    const list = await fetchJsonAuth(STORES_URL, { method:"GET" });
    // list: [{id, name, city, district}]
    (Array.isArray(list) ? list : (list.items || [])).forEach(s => {
        const opt = document.createElement("option");
        opt.value = String(s.id);
        opt.textContent = `${s.name}${(s.city || s.district) ? " • " + [s.city, s.district].filter(Boolean).join(" / ") : ""}`;
        sel.appendChild(opt);
    });
}

function buildRequest(){
    const storeVal = $("storeId").value;
    const type = $("type").value;
    const title = $("title").value.trim();
    const message = ($("message").value || "").trim() || null;
    const payloadJson = ($("payloadJson").value || "").trim() || null;

    return { storeVal, type, title, message, payloadJson };
}

async function submitForm(e){
    e.preventDefault();
    hideAlert();

    const { storeVal, type, title, message, payloadJson } = buildRequest();

    if (!title){
        showAlert("err", "Başlık zorunlu.");
        return;
    }

    const body = {
        type,        // NotificationType
        title,
        message,
        payloadJson
    };

    try{
        if (storeVal === "ALL"){
            await fetchJsonAuth(SEND_ALL_URL, { method:"POST", body: JSON.stringify(body) });
            showAlert("ok", "Tüm mağazalara bildirim gönderildi.");
        } else {
            await fetchJsonAuth(SEND_STORE_URL, {
                method:"POST",
                body: JSON.stringify({ ...body, storeId: Number(storeVal) })
            });
            showAlert("ok", "Seçili mağazaya bildirim gönderildi.");
        }

        $("title").value = "";
        $("message").value = "";
        $("payloadJson").value = "";

    } catch (err){
        showAlert("err", err.message || "Gönderim başarısız.");
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    $("logoutBtn")?.addEventListener("click", logout);
    formEl?.addEventListener("submit", submitForm);

    const t = token();
    if (!t) return logout();

    try{
        await loadStores();
    } catch (e){
        showAlert("err", e.message || "Store listesi alınamadı.");
    }
});
