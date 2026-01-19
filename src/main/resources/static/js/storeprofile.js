"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const ME_URL = `${API_BASE}/api/store/me/profile`;
const UPDATE_URL = `${API_BASE}/api/store/me/profile`;
const CHANGE_PW_URL = `${API_BASE}/api/store/me/password`;
const UPLOAD_LOGO_URL = `${API_BASE}/api/store/me/logo`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const metaEl = $("meta");
const verifiedPill = $("verifiedPill");

const logoImg = $("logoImg");
const logoFile = $("logoFile");
const uploadLogoBtn = $("uploadLogoBtn");

const oldPassword = $("oldPassword");
const newPassword = $("newPassword");
const confirmPassword = $("confirmPassword");
const changePwBtn = $("changePwBtn");

const formEl = $("form");
const saveBtn = $("saveBtn");
const resetBtn = $("resetBtn");

const f = {
    storeName: $("storeName"),
    authorizedPerson: $("authorizedPerson"),
    email: $("email"),
    phone: $("phone"),
    website: $("website"),
    taxNo: $("taxNo"),
    city: $("city"),
    district: $("district"),
    addressLine: $("addressLine"),
    floor: $("floor"),
    shopNo: $("shopNo"),
    directionNote: $("directionNote")
};

let original = null;

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


    const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || "";

    if (res.status === 401 || res.status === 403 || msg.toLowerCase().includes("unauthorized")) {
        localStorage.removeItem(TOKEN_KEY);
        window.location.href = "/templates/Login.html";
        return null;
    }

    if (!res.ok){
        throw new Error(msg || `HTTP ${res.status}`);
    }
    return body;
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
function goInbox(){ window.location.href = "/templates/storeinbox.html"; }
window.goInbox = goInbox;

function fillForm(d){
    if (!d) return;

    f.storeName.value = d.storeName || "";
    f.authorizedPerson.value = d.authorizedPerson || "";
    f.email.value = d.email || "";
    f.phone.value = d.phone || "";
    f.website.value = d.website || "";
    f.taxNo.value = d.taxNo || "";
    f.city.value = d.city || "";
    f.district.value = d.district || "";
    f.addressLine.value = d.addressLine || "";
    f.floor.value = (d.floor ?? "") === null ? "" : (d.floor ?? "");
    f.shopNo.value = d.shopNo || "";
    f.directionNote.value = d.directionNote || "";

    if (verifiedPill){
        verifiedPill.style.display = d.verified ? "inline-flex" : "none";
    }
    if (metaEl){
        metaEl.textContent = `ID: ${d.id ?? "—"} • Limit: ${d.listingLimit ?? "—"}`;
    }
    if (logoImg){
        logoImg.src = d.logoUrl ? (d.logoUrl + `?t=${Date.now()}`) : "/imagesforapp/logo2.png";
    }
}

function buildPayload(){
    return {
        storeName: f.storeName.value.trim(),
        authorizedPerson: (f.authorizedPerson.value || "").trim() || null,
        phone: (f.phone.value || "").trim() || null,
        website: (f.website.value || "").trim() || null,
        taxNo: (f.taxNo.value || "").trim() || null,
        city: (f.city.value || "").trim() || null,
        district: (f.district.value || "").trim() || null,
        addressLine: (f.addressLine.value || "").trim() || null,
        floor: (String(f.floor.value || "").trim() ? Number(f.floor.value) : null),
        shopNo: (f.shopNo.value || "").trim() || null,
        directionNote: (f.directionNote.value || "").trim() || null
    };
}

async function loadMe(){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    const d = await fetchJson(ME_URL, {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    if (!d) return; // 401 redirect olabilir

    original = d;
    fillForm(d);
}

async function save(){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    const payload = buildPayload();

    if (!payload.storeName){
        showAlert("err", "Mağaza adı zorunlu.");
        return;
    }

    await fetchJson(UPDATE_URL, {
        method:"PUT",
        headers: {
            Authorization: `Bearer ${token.trim()}`,
            "Content-Type":"application/json"
        },
        body: JSON.stringify(payload)
    });

    showAlert("ok", "Profil güncellendi.");
    await loadMe();
}

async function changePassword(){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);

    const payload = {
        oldPassword: (oldPassword.value || "").trim(),
        newPassword: (newPassword.value || "").trim(),
        confirmPassword: (confirmPassword.value || "").trim()
    };

    if (!payload.oldPassword || !payload.newPassword || !payload.confirmPassword){
        showAlert("err", "Şifre alanlarının hepsi zorunlu.");
        return;
    }
    if (payload.newPassword.length < 8){
        showAlert("err", "Yeni şifre en az 8 karakter olmalı.");
        return;
    }
    if (payload.newPassword !== payload.confirmPassword){
        showAlert("err", "Yeni şifreler uyuşmuyor.");
        return;
    }

    await fetchJson(CHANGE_PW_URL, {
        method:"PUT",
        headers: {
            Authorization: `Bearer ${token.trim()}`,
            "Content-Type":"application/json"
        },
        body: JSON.stringify(payload)
    });

    oldPassword.value = "";
    newPassword.value = "";
    confirmPassword.value = "";
    showAlert("ok", "Şifre değiştirildi.");
}

async function uploadLogo(){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);

    const file = logoFile?.files?.[0];
    if (!file){
        showAlert("err", "Lütfen bir logo dosyası seç.");
        return;
    }

    const fd = new FormData();
    fd.append("file", file);

    const res = await fetch(UPLOAD_LOGO_URL, {
        method:"POST",
        headers: { Authorization: `Bearer ${token.trim()}` },
        body: fd
    });

    if (res.status === 401 || res.status === 403){
        localStorage.removeItem(TOKEN_KEY);
        window.location.href = "/templates/Login.html";
        return;
    }

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(()=>null)
        : await res.text().catch(()=>null);

    if (!res.ok){
        const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || `HTTP ${res.status}`;
        throw new Error(msg);
    }

    const url = body?.logoUrl;
    if (url && logoImg){
        logoImg.src = url + `?t=${Date.now()}`;
    }

    showAlert("ok", "Logo güncellendi.");
    await loadMe().catch(()=>{});
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    resetBtn?.addEventListener("click", () => {
        if (original) fillForm(original);
    });

    formEl?.addEventListener("submit", async (e) => {
        e.preventDefault();
        saveBtn.disabled = true;
        try{
            await save();
        } catch(err){
            showAlert("err", err.message || "Kaydetme başarısız.");
        } finally {
            saveBtn.disabled = false;
        }
    });

    changePwBtn?.addEventListener("click", async () => {
        changePwBtn.disabled = true;
        try{
            await changePassword();
        } catch(err){
            showAlert("err", err.message || "Şifre değiştirilemedi.");
        } finally {
            changePwBtn.disabled = false;
        }
    });

    uploadLogoBtn?.addEventListener("click", async () => {
        uploadLogoBtn.disabled = true;
        try{
            await uploadLogo();
        } catch(err){
            showAlert("err", err.message || "Logo yüklenemedi.");
        } finally {
            uploadLogoBtn.disabled = false;
        }
    });

    try{
        await loadMe();
    } catch(err){
        showAlert("err", err.message || "Profil yüklenemedi.");
    }
});
