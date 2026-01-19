"use strict";

/**
 * profile.js
 * - Theme: home.js ile aynı mantık (THEME_KEY + body[data-theme])
 * - Profile GET/PUT (email değişmez)
 * - ✅ Guest (token yok) ise: Login.html'e yönlendir + Register sekmesini aç + mesaj göster
 */

const THEME_KEY = "theme"; // "light" | "dark"
const TOKEN_KEY = "token";

// ✅ Endpoint'leri gerekirse değiştir
const PROFILE_GET_URL = "/api/client/profile/me";      // GET -> ClientProfileGetResponse
const PROFILE_UPDATE_URL = "/api/client/profile/me";   // PUT -> ClientProfileResponse

// UI refs
const form = document.getElementById("profileForm");
const alertBox = document.getElementById("alert");

const firstNameEl = document.getElementById("firstName");
const lastNameEl = document.getElementById("lastName");
const emailEl = document.getElementById("email");
const phoneEl = document.getElementById("phone");
const birthDateEl = document.getElementById("birthDate");
const marketingConsentEl = document.getElementById("marketingConsent");

const saveBtn = document.getElementById("saveBtn");
const reloadBtn = document.getElementById("reloadBtn");
const clientIdEl = document.getElementById("clientId");

// ----------------------
// THEME
// ----------------------
function applyThemeFromStorage() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.body.setAttribute("data-theme", saved);
}

function initThemeToggle() {
    const toggle = document.getElementById("themeToggle");
    if (!toggle) return;

    const saved = localStorage.getItem(THEME_KEY) || "dark";
    toggle.checked = saved === "dark";

    toggle.addEventListener("change", () => {
        const t = toggle.checked ? "dark" : "light";
        document.body.setAttribute("data-theme", t);
        localStorage.setItem(THEME_KEY, t);
    });
}

// ----------------------
// NAV / AUTH
// ----------------------
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goHome() {
    window.location.href = "/templates/home.html";
}
window.goHome = goHome;

function goProfile() {
    window.location.href = "/templates/profile.html";
}
window.goProfile = goProfile;

function goFavorites(){
    const t = localStorage.getItem("token");
    const hasToken = t && t.trim().length > 0;
    window.location.href = hasToken ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;


// ✅ Guest -> Login/Register yönlendirmesi (3. maddeye göre)
function redirectToRegister(reasonMsg) {
    localStorage.setItem("authTab", "register");
    if (reasonMsg) localStorage.setItem("authMsg", reasonMsg);
    window.location.href = "/templates/Login.html";
}

function getTokenOrNull() {
    const t = localStorage.getItem(TOKEN_KEY);
    return t && t.trim() ? t.trim() : null;
}

function showAlert(type, msg) {
    if (!alertBox) return;
    alertBox.className = "alert " + (type === "ok" ? "ok" : "err");
    alertBox.textContent = msg;
    alertBox.style.display = "block";
}

function hideAlert() {
    if (!alertBox) return;
    alertBox.style.display = "none";
    alertBox.textContent = "";
    alertBox.className = "alert";
}

// ----------------------
// API
// ----------------------
async function apiFetch(url, options = {}) {
    const token = getTokenOrNull();

    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };

    // JWT gerekiyorsa ekle
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const res = await fetch(url, {
        ...options,
        headers,
        cache: "no-store"
    });

    // SecurityConfig: yetkisiz -> 401
    if (res.status === 401) {
        // Token var ama yetkisiz/expired olabilir -> login'e gönder
        localStorage.setItem("authTab", "login");
        localStorage.setItem("authMsg", "Oturum süreniz dolmuş olabilir. Lütfen tekrar giriş yapın.");
        logout();
        return null;
    }

    return res;
}

// ----------------------
// DATA BINDING
// ----------------------
function isoDateToInputValue(iso) {
    if (!iso) return "";
    return String(iso).slice(0, 10);
}

function fillForm(profile) {
    if (!profile) return;

    if (clientIdEl) clientIdEl.textContent = String(profile.id ?? "-");

    firstNameEl.value = profile.firstName ?? "";
    lastNameEl.value = profile.lastName ?? "";
    emailEl.value = profile.email ?? "";
    phoneEl.value = profile.phone ?? "";
    birthDateEl.value = isoDateToInputValue(profile.birthDate);
    marketingConsentEl.checked = Boolean(profile.marketingConsent);
}

function collectPayload() {
    return {
        firstName: (firstNameEl.value || "").trim(),
        lastName: (lastNameEl.value || "").trim(),
        phone: (phoneEl.value || "").trim() || null,
        birthDate: birthDateEl.value ? birthDateEl.value : null, // "YYYY-MM-DD"
        marketingConsent: Boolean(marketingConsentEl.checked)
    };
}

// ----------------------
// LOAD / SAVE
// ----------------------
async function loadProfile() {
    hideAlert();

    const token = getTokenOrNull();

    // ✅ (3'e göre) Guest ise: profil yerine register ekranına gönder
    if (!token) {
        redirectToRegister("Profil bilgilerinizi görmek için lütfen kaydolun veya giriş yapın.");
        return;
    }

    saveBtn && (saveBtn.disabled = true);
    reloadBtn && (reloadBtn.disabled = true);

    try {
        const res = await apiFetch(PROFILE_GET_URL, { method: "GET" });
        if (!res) return;

        if (!res.ok) {
            const text = await res.text().catch(() => "");
            showAlert("err", `Profil getirilemedi (HTTP ${res.status}). ${text || ""}`.trim());
            return;
        }

        const data = await res.json();

        // (opsiyonel) backend authenticated=false döndürürse
        if (data && data.authenticated === false) {
            redirectToRegister("Profil bilgilerinizi görmek için lütfen kaydolun veya giriş yapın.");
            return;
        }

        fillForm(data?.profile);
    } catch (e) {
        console.error(e);
        showAlert("err", "Bir hata oluştu. Konsolu kontrol et.");
    } finally {
        saveBtn && (saveBtn.disabled = false);
        reloadBtn && (reloadBtn.disabled = false);
    }
}

async function saveProfile() {
    hideAlert();

    const payload = collectPayload();

    if (!payload.firstName || !payload.lastName) {
        showAlert("err", "Ad ve Soyad zorunludur.");
        return;
    }

    saveBtn && (saveBtn.disabled = true);
    reloadBtn && (reloadBtn.disabled = true);

    try {
        const res = await apiFetch(PROFILE_UPDATE_URL, {
            method: "PUT",
            body: JSON.stringify(payload)
        });
        if (!res) return;

        if (!res.ok) {
            let msg = `Güncelleme başarısız (HTTP ${res.status}).`;
            const text = await res.text().catch(() => "");
            if (text) msg += " " + text;
            showAlert("err", msg);
            return;
        }

        const data = await res.json();
        fillForm(data?.profile);
        showAlert("ok", "Profil başarıyla güncellendi.");
    } catch (e) {
        console.error(e);
        showAlert("err", "Bir hata oluştu. Konsolu kontrol et.");
    } finally {
        saveBtn && (saveBtn.disabled = false);
        reloadBtn && (reloadBtn.disabled = false);
    }
}

// ----------------------
// INIT
// ----------------------
document.addEventListener("DOMContentLoaded", () => {
    applyThemeFromStorage();
    initThemeToggle();

    reloadBtn?.addEventListener("click", loadProfile);

    form?.addEventListener("submit", (ev) => {
        ev.preventDefault();
        saveProfile();
    });

    loadProfile();
});
