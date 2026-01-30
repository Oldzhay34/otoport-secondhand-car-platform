"use strict";

/* =========================================================
   CONFIG
========================================================= */
const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const CREATE_URL = `${API_BASE}/api/store/listings`;
const CATALOG_URL = "/filejson/AutomobileWithPackeages.json";

/* =========================================================
   HELPERS
========================================================= */
function $(id){ return document.getElementById(id); }
const safeStr = (v) => (v ?? "").toString();

function normalizeToken(raw){
    if(!raw) return "";
    let t = String(raw).trim();
    if (t.toLowerCase().startsWith("bearer ")) t = t.slice(7).trim();
    return t;
}

function clearAuth(){
    localStorage.removeItem(TOKEN_KEY);
}

function redirectToLogin(){
    clearAuth();
    window.location.href = "/templates/Login.html";
}

async function apiFetch(pathOrUrl, { method="GET", headers={}, body=null, auth=true } = {}){
    const url = pathOrUrl.startsWith("http") ? pathOrUrl : `${API_BASE}${pathOrUrl}`;

    const h = { ...headers };

    if (auth){
        const token = normalizeToken(localStorage.getItem(TOKEN_KEY));
        if (!token) throw new Error("Token yok. Lütfen tekrar giriş yap.");
        h["Authorization"] = `Bearer ${token}`;
    }

    const res = await fetch(url, { method, headers: h, body, cache:"no-store" });

    const ct = res.headers.get("content-type") || "";
    const data = ct.includes("application/json")
        ? await res.json().catch(()=>null)
        : await res.text().catch(()=>null);

    if (!res.ok){
        if (res.status === 401){
            redirectToLogin();
            return null;
        }
        const msg =
            (data && (data.message || data.error || data.details)) ||
            (typeof data === "string" ? data : "") ||
            `HTTP ${res.status}`;
        throw new Error(msg);
    }

    return data;
}

/* =========================================================
   DOM
========================================================= */
const alertBox = $("alert");
const formEl = $("form");

const brandSel = $("brand");
const modelSel = $("model");
const variantSel = $("variant");
const engineSel = $("engine");
const pkgSel = $("pkg");

const imagesInp = $("images");
const preview = $("preview");

const uploader = $("uploader");
const pickBtn = $("pickBtn");
const fileCountEl = $("fileCount");
const uploadTip = $("uploadTip");

const stickyCta = $("stickyCta");
const ctaSentinel = $("ctaSentinel");
const ctaCancel = $("ctaCancel");
const ctaSubmit = $("ctaSubmit");
const stickyMeta = $("stickyMeta");

let selectedFiles = []; // max 10

/* =========================================================
   ALERT
========================================================= */
function showAlert(type, msg){
    if (!alertBox) return;
    alertBox.className = "alert " + (type === "ok" ? "ok" : "err");
    alertBox.textContent = safeStr(msg);
    alertBox.style.display = "block";
}
function hideAlert(){
    if (!alertBox) return;
    alertBox.style.display = "none";
    alertBox.textContent = "";
}

/* =========================================================
   THEME
========================================================= */
function applyThemeFromStorage(){
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.body.setAttribute("data-theme", saved);
}
function initThemeToggle(){
    const toggle = $("themeToggle");
    if (!toggle) return;
    toggle.checked = (localStorage.getItem(THEME_KEY) || "dark") === "dark";
    toggle.addEventListener("change", () => {
        const t = toggle.checked ? "dark" : "light";
        document.body.setAttribute("data-theme", t);
        localStorage.setItem(THEME_KEY, t);
    });
}

/* =========================================================
   NAV
========================================================= */
function logout(){
    clearAuth();
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goStoreHome(){
    window.location.href = "/templates/storehome.html";
}
window.goStoreHome = goStoreHome;

/* =========================================================
   CATALOG (STABLE CASCADE)
========================================================= */
let catalog = null;

let selectedBrandObj = null;
let selectedModelObj = null;
let selectedVariantObj = null;

async function loadCatalog(){
    const res = await fetch(CATALOG_URL, { cache:"no-store" });
    if (!res.ok) throw new Error("Araç kataloğu okunamadı (AutomobileWithPackeages.json).");
    catalog = await res.json();
}

function setOptions(select, items, placeholder){
    const arr = Array.isArray(items) ? items : [];
    select.innerHTML = "";

    const ph = document.createElement("option");
    ph.value = "";
    ph.textContent = placeholder || "Seç";
    select.appendChild(ph);

    arr.forEach(v => {
        const opt = document.createElement("option");
        opt.value = v;
        opt.textContent = v;
        select.appendChild(opt);
    });
}

function setDisabled(select, disabled){
    if (!select) return;
    select.disabled = !!disabled;
    select.classList.toggle("disabled", !!disabled);
}

function getBrandObj(){
    const b = brandSel.value;
    return catalog?.brands?.find(x => x.brand === b) || null;
}
function getModelObj(){
    const bo = selectedBrandObj || getBrandObj();
    const m = modelSel.value;
    return bo?.models?.find(x => x.model === m) || null;
}
function getVariantObj(){
    const mo = selectedModelObj || getModelObj();
    const v = variantSel.value;
    return mo?.variants?.find(x => x.variant === v) || null;
}

function resetBelowModel(){
    setOptions(variantSel, [], "Variant (ops.)");
    setOptions(engineSel, [], "Engine/Trim (ops.)");
    setOptions(pkgSel, [], "Package (ops.)");

    setDisabled(variantSel, true);
    setDisabled(engineSel, true);
    setDisabled(pkgSel, true);
}

function resetBelowVariant(){
    setOptions(engineSel, [], "Engine/Trim (ops.)");
    setOptions(pkgSel, [], "Package (ops.)");
    setDisabled(engineSel, true);
    setDisabled(pkgSel, true);
}

function refreshBrand(){
    const brands = (catalog?.brands || []).map(b => b.brand).filter(Boolean);
    setOptions(brandSel, brands, "Marka seç");

    setOptions(modelSel, [], "Model seç");
    resetBelowModel();
}

function refreshModel(){
    selectedBrandObj = getBrandObj();
    selectedModelObj = null;
    selectedVariantObj = null;

    const models = (selectedBrandObj?.models || []).map(m => m.model).filter(Boolean);
    setOptions(modelSel, models, "Model seç");
    resetBelowModel();
}

function refreshVariantEnginePackageForModel(){
    selectedModelObj = getModelObj();
    selectedVariantObj = null;

    setOptions(variantSel, [], "Variant (ops.)");
    setOptions(engineSel, [], "Engine/Trim (ops.)");
    setOptions(pkgSel, [], "Package (ops.)");
    setDisabled(pkgSel, true);

    const mo = selectedModelObj;
    if (!mo){
        setDisabled(variantSel, true);
        setDisabled(engineSel, true);
        return;
    }

    if (Array.isArray(mo.variants) && mo.variants.length > 0){
        const vars = mo.variants.map(v => v.variant).filter(Boolean);
        setOptions(variantSel, vars, "Variant seç");
        setDisabled(variantSel, false);

        setOptions(engineSel, [], "Engine/Trim (ops.)");
        setDisabled(engineSel, true);
        return;
    }

    if (Array.isArray(mo.engines) && mo.engines.length > 0){
        setDisabled(variantSel, true);
        setOptions(variantSel, [], "Variant (yok)");

        const engines = mo.engines.map(e => e.engine).filter(Boolean);
        setOptions(engineSel, engines, "Motor seç");
        setDisabled(engineSel, false);
        return;
    }

    if (Array.isArray(mo.trims) && mo.trims.length > 0){
        setDisabled(variantSel, true);
        setOptions(variantSel, [], "Variant (yok)");

        setOptions(engineSel, mo.trims.filter(Boolean), "Trim seç");
        setDisabled(engineSel, false);
        return;
    }

    setDisabled(variantSel, true);
    setDisabled(engineSel, true);
    setDisabled(pkgSel, true);
}

function refreshEnginePackageForVariant(){
    selectedVariantObj = getVariantObj();
    resetBelowVariant();

    const vo = selectedVariantObj;
    if (!vo){
        setDisabled(engineSel, true);
        return;
    }

    if (Array.isArray(vo.engines) && vo.engines.length > 0){
        setOptions(engineSel, vo.engines.map(e => e.engine).filter(Boolean), "Motor seç");
        setDisabled(engineSel, false);
        return;
    }

    if (Array.isArray(vo.trims) && vo.trims.length > 0){
        setOptions(engineSel, vo.trims.filter(Boolean), "Trim seç");
        setDisabled(engineSel, false);
        return;
    }

    if (Array.isArray(vo.packages) && vo.packages.length > 0){
        setDisabled(engineSel, true);
        setOptions(engineSel, [], "Engine/Trim (yok)");

        setOptions(pkgSel, vo.packages.filter(Boolean), "Paket seç");
        setDisabled(pkgSel, false);
        return;
    }

    setDisabled(engineSel, true);
}

function refreshPackageForEngine(){
    setOptions(pkgSel, [], "Package (ops.)");
    setDisabled(pkgSel, true);

    const engineValue = engineSel.value;
    if (!engineValue) return;

    const mo = selectedModelObj || getModelObj();
    const vo = selectedVariantObj || getVariantObj();

    const engineList =
        (vo && Array.isArray(vo.engines) ? vo.engines :
            mo && Array.isArray(mo.engines) ? mo.engines : null);

    if (!engineList) return;

    const eo = engineList.find(e => e.engine === engineValue) || null;
    const packages = eo?.packages;

    if (Array.isArray(packages) && packages.length > 0){
        setOptions(pkgSel, packages.filter(Boolean), "Paket seç");
        setDisabled(pkgSel, false);
    }
}

/* =========================================================
   FILES / UPLOADER
========================================================= */
function isFull(){ return selectedFiles.length >= 10; }

function syncInputFiles(){
    const dt = new DataTransfer();
    selectedFiles.slice(0, 10).forEach(f => dt.items.add(f));
    imagesInp.files = dt.files;
}

function showFullTip(){
    if (!uploader) return;
    uploader.classList.add("show-tip");
    setTimeout(() => uploader.classList.remove("show-tip"), 1200);
}

function updateUploaderDisabledState(){
    if (!uploader) return;
    const full = isFull();
    uploader.classList.toggle("disabled", full);
    uploader.setAttribute("aria-disabled", full ? "true" : "false");
}

function updateFileCountUi(){
    const n = Math.min(selectedFiles.length, 10);
    if (fileCountEl) fileCountEl.textContent = `${n} / 10`;

    if (stickyMeta){
        const cover = selectedFiles[0]?.name ? selectedFiles[0].name : "yok";
        stickyMeta.textContent = `Kapak: ${cover} • ${n}/10 fotoğraf`;
    }

    updateUploaderDisabledState();
}

function pushFiles(fileList){
    const incoming = Array.from(fileList || []).filter(f => /^image\//.test(f.type));
    if (incoming.length === 0) return;

    if (isFull()){
        showFullTip();
        return;
    }

    for (const f of incoming){
        if (selectedFiles.length >= 10) break;
        selectedFiles.push(f);
    }

    syncInputFiles();
    renderPreview();
    updateFileCountUi();
}

function renderPreview(){
    if (!preview) return;
    preview.innerHTML = "";

    selectedFiles.slice(0, 10).forEach((f, idx) => {
        const url = URL.createObjectURL(f);

        const item = document.createElement("div");
        item.className = "pitem";
        item.draggable = true;
        item.dataset.index = String(idx);

        const img = document.createElement("img");
        img.src = url;
        img.alt = "preview";

        if (idx === 0){
            const badge = document.createElement("div");
            badge.className = "pbadge";
            badge.textContent = "Kapak";
            item.appendChild(badge);
        }

        const rm = document.createElement("button");
        rm.type = "button";
        rm.className = "premove";
        rm.setAttribute("aria-label", "Fotoğrafı kaldır");
        rm.textContent = "×";
        rm.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            selectedFiles.splice(idx, 1);
            syncInputFiles();
            renderPreview();
            updateFileCountUi();
        });

        item.appendChild(rm);
        item.appendChild(img);

        item.addEventListener("dragstart", (e) => {
            item.classList.add("dragging");
            e.dataTransfer.setData("text/plain", String(idx));
            e.dataTransfer.effectAllowed = "move";
        });

        item.addEventListener("dragend", () => {
            item.classList.remove("dragging");
            preview.querySelectorAll(".pitem").forEach(x => x.classList.remove("over"));
        });

        item.addEventListener("dragover", (e) => {
            e.preventDefault();
            item.classList.add("over");
            e.dataTransfer.dropEffect = "move";
        });

        item.addEventListener("dragleave", () => item.classList.remove("over"));

        item.addEventListener("drop", (e) => {
            e.preventDefault();
            const from = Number(e.dataTransfer.getData("text/plain"));
            const to = idx;
            if (!Number.isFinite(from) || from === to) return;

            const moved = selectedFiles.splice(from, 1)[0];
            selectedFiles.splice(to, 0, moved);

            syncInputFiles();
            renderPreview();
            updateFileCountUi();
        });

        preview.appendChild(item);
    });
}

function initUploaderUi(){
    if (!uploader || !imagesInp) return;

    const openPicker = () => {
        if (isFull()){
            showFullTip();
            return;
        }
        imagesInp.click();
    };

    pickBtn?.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation();
        openPicker();
    });

    uploader.addEventListener("click", openPicker);
    uploader.addEventListener("keydown", (e) => {
        if (e.key === "Enter" || e.key === " "){
            e.preventDefault();
            openPicker();
        }
    });

    ["dragenter","dragover"].forEach(ev => {
        uploader.addEventListener(ev, (e) => {
            e.preventDefault();
            e.stopPropagation();
            uploader.classList.add("dragover");
        });
    });

    ["dragleave","drop"].forEach(ev => {
        uploader.addEventListener(ev, (e) => {
            e.preventDefault();
            e.stopPropagation();
            uploader.classList.remove("dragover");
        });
    });

    uploader.addEventListener("drop", (e) => {
        if (isFull()){
            showFullTip();
            return;
        }
        const dt = e.dataTransfer;
        if (!dt || !dt.files) return;
        pushFiles(dt.files);
    });

    imagesInp.addEventListener("change", () => {
        pushFiles(imagesInp.files);
        imagesInp.value = "";
    });
}

/* =========================================================
   STICKY CTA
========================================================= */
function setSubmitting(isOn){
    const formSubmit = formEl?.querySelector('button[type="submit"]');
    if (formSubmit){
        formSubmit.disabled = isOn;
        formSubmit.textContent = isOn ? "Gönderiliyor..." : "İlanı Oluştur";
    }

    if (ctaSubmit){
        ctaSubmit.disabled = isOn;
        const txt = ctaSubmit.querySelector(".ctaText");
        if (txt) txt.textContent = isOn ? "Gönderiliyor..." : "İlanı Oluştur";
    }
}

function initStickyCta(){
    if (!stickyCta || !ctaSentinel) return;

    ctaCancel?.addEventListener("click", () => goStoreHome());
    ctaSubmit?.addEventListener("click", () => {
        if (formEl?.requestSubmit) formEl.requestSubmit();
        else formEl?.dispatchEvent(new Event("submit", { cancelable: true, bubbles: true }));
    });

    const io = new IntersectionObserver((entries) => {
        const e = entries[0];
        const hide = e.isIntersecting;
        stickyCta.classList.toggle("show", !hide);
    }, { threshold: 0.01 });

    io.observe(ctaSentinel);
}

/* =========================================================
   ✅ EXPERT KROKI
========================================================= */
const STATUS_CYCLE = ["ORIGINAL", "PAINTED", "LOCAL_PAINT", "REPLACED"];

const PARTS = {
    HOOD: "ekspertiz kaput.png",
    ROOF: "ekspertiz tavan.png",
    FRONT_BUMPER: "ekspertiz ön tampon.png",
    REAR_BUMPER: "ekspertiz arka tampon.png",
    FRONT_LEFT_FENDER: "ekspertiz sol ön çamurluk.png",
    FRONT_RIGHT_FENDER: "ekspertiz sağ ön çamurluk.png",
    FRONT_LEFT_DOOR: "sol ön kapı.png",
    FRONT_RIGHT_DOOR: "ekspertiz sağ ön kapı.png",
    REAR_LEFT_DOOR: "sol arka kapı.png",
    REAR_RIGHT_DOOR: "ekspertiz sağ arka kapı.png",
    REAR_LEFT_FENDER: "sol arka çamurluk.png",
    REAR_RIGHT_FENDER: "ekspertiz sağ arka çamurluk.png",
    TRUNK_LID: "ekspertiz bagaj.png",
    CHASSIS: "ekspertiz şasi.png",
    PILLAR_LEFT: "ekspertiz sol direk.png",
    PILLAR_RIGHT: "ekspertiz sağ direk.png"
};

const BASE_FILE = "expertiz raporu cam ve lastikk.png";
const ALL_PARTS = Object.keys(PARTS);

const sketchBaseImg = $("sketchBase");
const sketchOutline = $("sketchOutline");
const sketchParts = $("sketchParts");
const sketchHint = $("sketchHint");

const partSelect = $("partSelect");
const statusSelect = $("statusSelect");
const applyPartStatusBtn = $("applyPartStatusBtn");
const sideInfo = $("sideInfo");

const expertState = new Map();

function asset(p){ return encodeURI(p); }
let ASSET_CONF = { root: "/images", folder: "expertiz" };

function baseUrl(){
    return asset(`${ASSET_CONF.root}/${ASSET_CONF.folder}/${BASE_FILE}`);
}
function partUrl(partEnum){
    const file = PARTS[partEnum];
    if (!file) return null;
    return asset(`${ASSET_CONF.root}/${ASSET_CONF.folder}/parçalar/${file}`);
}

function trPartName(part){
    const map = {
        HOOD:"Kaput", ROOF:"Tavan",
        FRONT_BUMPER:"Ön Tampon", REAR_BUMPER:"Arka Tampon",
        FRONT_LEFT_FENDER:"Sol Ön Çamurluk", FRONT_RIGHT_FENDER:"Sağ Ön Çamurluk",
        FRONT_LEFT_DOOR:"Sol Ön Kapı", FRONT_RIGHT_DOOR:"Sağ Ön Kapı",
        REAR_LEFT_DOOR:"Sol Arka Kapı", REAR_RIGHT_DOOR:"Sağ Arka Kapı",
        REAR_LEFT_FENDER:"Sol Arka Çamurluk", REAR_RIGHT_FENDER:"Sağ Arka Çamurluk",
        TRUNK_LID:"Bagaj Kapağı",
        CHASSIS:"Şasi", PILLAR_LEFT:"Sol Direk", PILLAR_RIGHT:"Sağ Direk"
    };
    return map[part] || part;
}
function trStatusName(s){
    const map = { ORIGINAL:"Orijinal", PAINTED:"Boyalı", LOCAL_PAINT:"Lokal Boya", REPLACED:"Değişen" };
    return map[s] || s;
}
function fillColor(st){
    if (st === "REPLACED") return "#ef4444";
    if (st === "PAINTED") return "#3b82f6";
    if (st === "LOCAL_PAINT") return "#f97316";
    return null;
}
function cycleStatus(cur){
    const idx = STATUS_CYCLE.indexOf(cur);
    return STATUS_CYCLE[(idx + 1) % STATUS_CYCLE.length];
}
function fillPartSelect(){
    if (!partSelect) return;
    partSelect.innerHTML = "";
    ALL_PARTS.forEach(p => {
        const opt = document.createElement("option");
        opt.value = p;
        opt.textContent = trPartName(p);
        partSelect.appendChild(opt);
    });
}
function syncStatusSelectToCurrentPart(){
    const p = partSelect?.value;
    if (!p) return;
    const cur = expertState.get(p) || "ORIGINAL";
    if (statusSelect) statusSelect.value = cur;
    if (sideInfo) sideInfo.textContent = `${trPartName(p)} → ${trStatusName(cur)}`;
}
function updateSketchHint(){
    if (!sketchHint) return;
    let paintedCount = 0;
    expertState.forEach(v => { if (v !== "ORIGINAL") paintedCount++; });
    sketchHint.textContent = paintedCount === 0 ? "Tüm parçalar orijinal" : `${paintedCount} parça işaretlendi`;
}
function applySelectedPartStatus(){
    const p = partSelect?.value;
    const st = statusSelect?.value;
    if (!p || !st) return;

    if (!PARTS[p]){
        showAlert("err", "Bu parça için maske dosyası tanımlı değil.");
        return;
    }

    expertState.set(p, st);
    renderSketch();
    updateSketchHint();
    syncStatusSelectToCurrentPart();
}
function renderSketch(){
    if (!sketchOutline || !sketchParts) return;
    sketchOutline.innerHTML = "";
    sketchParts.innerHTML = "";

    ALL_PARTS.forEach(part => {
        const mask = partUrl(part);
        if (!mask) return;

        const o = document.createElement("div");
        o.className = "sketch-part";
        o.style.setProperty("--fill", "rgba(255,255,255,.16)");
        o.style.setProperty("--mask", `url("${mask}")`);
        o.title = `${trPartName(part)} (tıkla)`;

        o.onclick = (e) => {
            e.preventDefault();
            e.stopPropagation();

            const next = cycleStatus(expertState.get(part) || "ORIGINAL");
            expertState.set(part, next);

            if (partSelect && partSelect.value === part){
                if (statusSelect) statusSelect.value = next;
                if (sideInfo) sideInfo.textContent = `${trPartName(part)} → ${trStatusName(next)}`;
            }

            renderSketch();
            updateSketchHint();
        };

        sketchOutline.appendChild(o);

        const st = expertState.get(part) || "ORIGINAL";
        const fill = fillColor(st);
        if (fill){
            const c = document.createElement("div");
            c.className = "sketch-part";
            c.style.setProperty("--fill", fill);
            c.style.setProperty("--mask", `url("${mask}")`);
            c.title = `${trPartName(part)}: ${trStatusName(st)} (tıkla)`;
            c.onclick = o.onclick;
            sketchParts.appendChild(c);
        }
    });
}
function initExpert(){
    ALL_PARTS.forEach(p => expertState.set(p, "ORIGINAL"));
    if (sketchBaseImg) sketchBaseImg.src = baseUrl();

    fillPartSelect();
    syncStatusSelectToCurrentPart();
    updateSketchHint();
    renderSketch();

    partSelect?.addEventListener("change", syncStatusSelectToCurrentPart);
    statusSelect?.addEventListener("change", applySelectedPartStatus);
    applyPartStatusBtn?.addEventListener("click", applySelectedPartStatus);
}

/* =========================================================
   BACKEND DTO
========================================================= */
function buildExpertReportDto(){
    const items = [];
    expertState.forEach((status, part) => {
        if (status !== "ORIGINAL") items.push({ part, status, note: null });
    });

    let result = "CLEAN";
    if (items.some(i => i.status === "REPLACED")) result = "MAJOR";
    else if (items.length > 0) result = "MINOR";

    return {
        companyName: null,
        reportDate: null,
        reportNo: null,
        result,
        notes: null,
        items
    };
}

/* =========================================================
   FORM JSON
========================================================= */
function buildDataJson(){
    return JSON.stringify({
        title: $("title").value.trim(),
        description: $("description").value || null,
        price: Number($("price").value),
        currency: $("currency").value,
        negotiable: $("negotiable").checked === true,
        city: $("city").value.trim(),
        district: ($("district").value || "").trim() || null,

        brand: brandSel.value,
        model: modelSel.value,
        variant: variantSel.value || null,
        engine: engineSel.value || null,
        carPackage: pkgSel.value || null,

        transmission: $("transmission").value,
        fuelType: $("fuelType").value,
        bodyType: $("bodyType").value,

        year: Number($("year").value),
        kilometer: Number($("km").value),
        color: ($("color").value || "").trim() || null,
        engineVolumeCc: ($("engineCc").value || "").trim() ? Number($("engineCc").value) : null,
        enginePowerHp: ($("engineHp").value || "").trim() ? Number($("engineHp").value) : null,

        expertReport: buildExpertReportDto()
    });
}

/* =========================================================
   SUBMIT
========================================================= */
function decodeJwtPayload(token) {
    try {
        const payload = token.split(".")[1];
        const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
        return JSON.parse(atob(base64));
    } catch {
        return null;
    }
}

async function submitForm(e){
    e.preventDefault();
    hideAlert();

    const token = normalizeToken(localStorage.getItem(TOKEN_KEY));

    console.log("TOKEN (first 20):", (token || "").slice(0, 20));
    const payload = decodeJwtPayload(token || "");
    console.log("JWT payload:", payload);

    if (payload?.exp) {
        const expMs = payload.exp * 1000;
        console.log("JWT exp (local):", new Date(expMs).toString());
        console.log("JWT expired?:", Date.now() > expMs);
    }

    if (!token){
        redirectToLogin();
        return;
    }

    if (!brandSel.value || !modelSel.value){
        showAlert("err", "Marka ve model seçmelisin.");
        return;
    }

    try{
        setSubmitting(true);

        const fd = new FormData();
        fd.append("data", buildDataJson());
        selectedFiles.slice(0, 10).forEach(f => fd.append("images", f));

        // ✅ wrapper: token otomatik
        const body = await apiFetch(CREATE_URL, {
            method: "POST",
            auth: true,
            // FormData gönderdiğimiz için Content-Type set ETMİYORUZ
            body: fd
        });

        showAlert("ok", "İlan oluşturuldu. Ana Ekrana yönlendiriliyorsun...");
        setSubmitting(false);
        setTimeout(() => goStoreHome(), 650);

    } catch(err){
        console.error(err);
        showAlert("err", err?.message || "İlan gönderilemedi.");
        setSubmitting(false);
    }
}

/* =========================================================
   INIT
========================================================= */
document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();

    const token = normalizeToken(localStorage.getItem(TOKEN_KEY));
    if (!token){
        redirectToLogin();
        return;
    }

    try{
        await loadCatalog();
        refreshBrand();

        brandSel.addEventListener("change", refreshModel);
        modelSel.addEventListener("change", refreshVariantEnginePackageForModel);
        variantSel.addEventListener("change", refreshEnginePackageForVariant);
        engineSel.addEventListener("change", refreshPackageForEngine);

        if (!formEl) throw new Error("Form bulunamadı (id='form').");
        formEl.addEventListener("submit", submitForm);

        initExpert();
        initUploaderUi();
        initStickyCta();

        updateFileCountUi();
        renderPreview();

    } catch (e){
        showAlert("err", e.message || "İlan oluşturma ekranı hazırlanamadı.");
    }
});
