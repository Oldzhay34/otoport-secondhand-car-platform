"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const GET_URL = (id) => `${API_BASE}/api/store/listings/${id}`;
const PUT_URL = (id) => `${API_BASE}/api/store/listings/${id}`;
const DEL_URL = (id) => `${API_BASE}/api/store/listings/${id}`;

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const sub = $("sub");

const titleEl = $("title");
const descEl = $("description");
const priceEl = $("price");
const currencyEl = $("currency");
const negotiableEl = $("negotiable");
const cityEl = $("city");
const districtEl = $("district");

const yearEl = $("year");
const kmEl = $("kilometer");
const colorEl = $("color");
const volEl = $("engineVolumeCc");
const hpEl = $("enginePowerHp");

const saveBtn = $("saveBtn");
const deleteBtn = $("deleteBtn");
const backBtn = $("backBtn");
const carHint = $("carHint");

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

function getIdFromQuery(){
    const qs = new URLSearchParams(window.location.search);
    const v = qs.get("id");
    const id = v ? Number(v) : NaN;
    return Number.isFinite(id) ? id : null;
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
function goStoreProfile(){ window.location.href = "/templates/storeprofile.html"; }
window.goStoreProfile = goStoreProfile;

function fillForm(data){
    titleEl.value = data.title ?? "";
    descEl.value = data.description ?? "";
    priceEl.value = data.price ?? "";
    currencyEl.value = data.currency ?? "TRY";
    negotiableEl.checked = data.negotiable === true;
    cityEl.value = data.city ?? "";
    districtEl.value = data.district ?? "";

    yearEl.value = data.year ?? "";
    kmEl.value = data.kilometer ?? "";
    colorEl.value = data.color ?? "";
    volEl.value = data.engineVolumeCc ?? "";
    hpEl.value = data.enginePowerHp ?? "";

    if (sub){
        sub.textContent = `İlan #${data.id} • CarId: ${data.carId ?? "—"}`;
    }
    if (carHint){
        carHint.textContent = "Araç alanları opsiyoneldir. Boş bırakırsan değişmez.";
    }
}

function buildPayload(){
    const payload = {
        title: titleEl.value.trim() || null,
        description: descEl.value ?? null,
        price: priceEl.value ? Number(priceEl.value) : null,
        currency: (currencyEl.value || "TRY").trim().toUpperCase(),
        negotiable: negotiableEl.checked,
        city: cityEl.value.trim() || null,
        district: districtEl.value.trim() || null,

        year: yearEl.value ? Number(yearEl.value) : null,
        kilometer: kmEl.value ? Number(kmEl.value) : null,
        color: colorEl.value.trim() || null,
        engineVolumeCc: volEl.value ? Number(volEl.value) : null,
        enginePowerHp: hpEl.value ? Number(hpEl.value) : null
    };

    // description boş string olmasın (istersen)
    if (payload.description != null && String(payload.description).trim() === "") payload.description = "";

    return payload;
}

async function loadListing(id){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) { window.location.href = "/templates/Login.html"; return; }

    const data = await fetchJson(GET_URL(id), {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    fillForm(data);
}

async function saveListing(id){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) { window.location.href = "/templates/Login.html"; return; }

    const payload = buildPayload();

    const data = await fetchJson(PUT_URL(id), {
        method:"PUT",
        headers: {
            "Content-Type":"application/json",
            Authorization: `Bearer ${token.trim()}`
        },
        body: JSON.stringify(payload)
    });

    showAlert("ok", "İlan güncellendi.");
    fillForm(data);
}

async function deleteListing(id){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) { window.location.href = "/templates/Login.html"; return; }

    await fetchJson(DEL_URL(id), {
        method:"DELETE",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    showAlert("ok", "İlan silindi. Store home’a dönüyorsun...");
    setTimeout(() => goStoreHome(), 400);
}

document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const id = getIdFromQuery();
    if (!id){
        showAlert("err", "Listing id bulunamadı.");
        return;
    }

    backBtn?.addEventListener("click", () => goStoreHome());
    saveBtn?.addEventListener("click", async () => {
        try { await saveListing(id); }
        catch(e){ showAlert("err", e.message || "Güncelleme başarısız."); }
    });
    deleteBtn?.addEventListener("click", async () => {
        if (!confirm("Bu ilanı silmek istediğine emin misin?")) return;
        try { await deleteListing(id); }
        catch(e){ showAlert("err", e.message || "Silme başarısız."); }
    });

    try{
        await loadListing(id);
    } catch (e){
        showAlert("err", e.message || "İlan bilgisi alınamadı.");
    }
});
