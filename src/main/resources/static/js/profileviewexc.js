"use strict";

const THEME_KEY = "theme";
const TOKEN_KEY = "token";

// Sayfalar
const PROFILE_PAGE = "/templates/profile.html";
const LOGIN_PAGE = "/templates/Login.html";

const guestGate = document.getElementById("guestGate");
const goRegisterBtn = document.getElementById("goRegisterBtn");
const goLoginBtn = document.getElementById("goLoginBtn");

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
// NAV
// ----------------------
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = LOGIN_PAGE;
}
window.logout = logout;

function goHome() {
    window.location.href = "/templates/home.html";
}
window.goHome = goHome;

// Login.html'e git ve sekme seçtir
function goAuth(tab) {
    localStorage.setItem("authTab", tab); // "login" | "register"
    localStorage.setItem("authMsg", "Profil bilgilerini görmek için lütfen giriş yapın veya kaydolun.");
    window.location.href = LOGIN_PAGE;
}
function goFavorites(){
    const t = localStorage.getItem("token");
    const hasToken = t && t.trim().length > 0;
    window.location.href = hasToken ? "/templates/favorites.html" : "/templates/favoriteviewexc.html";
}
window.goFavorites = goFavorites;



// ----------------------
// helpers
// ----------------------
function getTokenOrNull() {
    const t = localStorage.getItem(TOKEN_KEY);
    return t && t.trim() ? t.trim() : null;
}

// ----------------------
// INIT
// ----------------------
document.addEventListener("DOMContentLoaded", () => {
    applyThemeFromStorage();
    initThemeToggle();

    goRegisterBtn?.addEventListener("click", () => goAuth("register"));
    goLoginBtn?.addEventListener("click", () => goAuth("login"));

    const token = getTokenOrNull();

    if (token) {
        // ✅ giriş varsa direkt profile'a geç
        window.location.replace(PROFILE_PAGE);
        return;
    }

    // ✅ guest ise burada kal ve gate göster
    if (guestGate) guestGate.style.display = "flex";
});
