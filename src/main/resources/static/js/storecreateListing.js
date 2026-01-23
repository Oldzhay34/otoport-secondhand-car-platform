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
    alertBox.textContent = msg;
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
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goStoreHome(){
    window.location.href = "/templates/storehome.html";
}
window.goStoreHome = goStoreHome;

/* =========================================================
   CATALOG
========================================================= */
let catalog = null;

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

function getBrandObj(){
    const b = brandSel.value;
    return catalog?.brands?.find(x => x.brand === b) || null;
}

function refreshBrand(){
    const brands = (catalog?.brands || []).map(b => b.brand).filter(Boolean);
    setOptions(brandSel, brands, "Marka seç");
}

function refreshModel(){
    const bo = getBrandObj();
    const models = (bo?.models || []).map(m => m.model).filter(Boolean);
    setOptions(modelSel, models, "Model seç");

    setOptions(variantSel, [], "Variant (ops.)");
    setOptions(engineSel, [], "Engine/Trim (ops.)");
    setOptions(pkgSel, [], "Package (ops.)");
}

/* =========================================================
   FILES / UPLOADER (premium)
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

        // drag reorder
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

    // drag & drop
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
        imagesInp.value = ""; // aynı dosya tekrar seçilebilsin
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
        const hide = e.isIntersecting;      // sentinel görünüyorsa sticky kapanır
        stickyCta.classList.toggle("show", !hide);
    }, { threshold: 0.01 });

    io.observe(ctaSentinel);

    requestAnimationFrame(() => {
        const rect = ctaSentinel.getBoundingClientRect();
        const visible = rect.top >= 0 && rect.top <= window.innerHeight;
        stickyCta.classList.toggle("show", !visible);
    });
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
async function submitForm(e){
    e.preventDefault();
    hideAlert();

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    if (!brandSel.value || !modelSel.value){
        showAlert("err", "Marka ve model seçmelisin.");
        return;
    }

    try{
        setSubmitting(true);

        const fd = new FormData();
        fd.append("data", new Blob([buildDataJson()], { type:"application/json" }));

        selectedFiles.slice(0, 10).forEach(f => fd.append("images", f));

        const res = await fetch(CREATE_URL, {
            method: "POST",
            headers: { Authorization: `Bearer ${token.trim()}` },
            body: fd
        });

        const ct = res.headers.get("content-type") || "";
        const body = ct.includes("application/json")
            ? await res.json().catch(()=>null)
            : await res.text().catch(()=>null);

        if (!res.ok){
            const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || `HTTP ${res.status}`;
            showAlert("err", msg);
            setSubmitting(false);
            return;
        }

        showAlert("ok", "İlan oluşturuldu. Ana Ekrana yönlendiriliyorsun...");
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

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    try{
        await loadCatalog();
        refreshBrand();

        brandSel.addEventListener("change", refreshModel);

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
