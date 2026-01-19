"use strict";

console.log("[ADMINHOME] JS LOADED ✅");

const API_BASE = "http://localhost:8080";
const THEME_KEY = "theme";

function $(id) { return document.getElementById(id); }

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

function showErr(msg) {
    const el = $("adminErr");
    if (!el) return;
    el.textContent = msg || "";
    el.style.display = msg ? "block" : "none";
}

function setText(id, v) {
    const el = $(id);
    if (!el) {
        console.warn("[ADMINHOME] Missing element id:", id);
        return;
    }
    el.textContent = (v === null || v === undefined) ? "—" : String(v);
}

// ✅ TR (Europe/Istanbul) tarihini YYYY-MM-DD üret
function todayTR() {
    return new Intl.DateTimeFormat("en-CA", {
        timeZone: "Europe/Istanbul",
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
    }).format(new Date());
}

function token() { return localStorage.getItem("token"); }

async function apiGet(path) {
    const t = token();
    console.log("[ADMINHOME] GET", path, "token?", !!t);

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

    console.log("[ADMINHOME] RES", res.status, body);

    if (!res.ok) {
        const msg =
            (body && (body.message || body.error || body.details)) ||
            (typeof body === "string" && body) ||
            ("HTTP " + res.status);
        throw new Error(msg);
    }

    return body;
}

async function postNoAuth(path) {
    try {
        await fetch(API_BASE + path, { method: "POST" });
    } catch (e) {
        console.warn("[ADMINHOME] log post failed:", e);
    }
}

// ✅ Dashboard DTO okuma: { total, guest, client, store }
async function loadDaily() {
    const date = todayTR();
    const dto = await apiGet(`/api/admin/dashboard/daily?date=${encodeURIComponent(date)}`);

    const total  = Number(dto.total  ?? 0);
    const guest  = Number(dto.guest  ?? 0);
    const client = Number(dto.client ?? 0);
    const store  = Number(dto.store  ?? 0);

    setText("statTotal", total);
    setText("statGuest", guest);
    setText("statClient", client);
    setText("statStore", store);
}

window.addEventListener("DOMContentLoaded", async () => {
    initThemeToggle();

    $("logoutBtn")?.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/templates/login.html";
    });

    try {
        showErr("");
        await loadDaily();
    } catch (e) {
        console.error("[ADMINHOME] ERROR", e);
        showErr("Veri alınamadı: " + (e.message || e));
        setText("statTotal", 0);
        setText("statGuest", 0);
        setText("statClient", 0);
        setText("statStore", 0);
    }

    await fetch(API_BASE + "/api/visit?target=" + encodeURIComponent("/templates/adminhome.html"), {
        method: "POST"
    });

});
