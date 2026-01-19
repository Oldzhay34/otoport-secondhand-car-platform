// storecreateListing.js
"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const CREATE_URL = `${API_BASE}/api/store/listings`;

// ✅ Dosya adın burada birebir aynı olmalı (static/filejson altındaki isimle)
const CATALOG_URL = "/filejson/AutomobileWithPackeages.json";

function $(id){ return document.getElementById(id); }

const alertBox = $("alert");
const formEl = $("form"); // HTML'de id="form"

const brandSel = $("brand");
const modelSel = $("model");
const variantSel = $("variant");
const engineSel = $("engine");
const pkgSel = $("pkg");

const imagesInp = $("images");
const preview = $("preview");

let catalog = null;

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
    document.body.setAttribute("data-theme", saved === "dark" ? "dark" : "light");
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

function logout(){
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = "/templates/Login.html";
}
window.logout = logout;

function goStoreHome(){ window.location.href = "/templates/storehome.html"; }
window.goStoreHome = goStoreHome;

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

async function loadCatalog(){
    const res = await fetch(CATALOG_URL, { cache:"no-store" });
    if (!res.ok) throw new Error("Araç kataloğu okunamadı (AutomobileWithPackeages.json).");
    catalog = await res.json();
}

function getBrandObj(){
    const b = brandSel.value;
    return catalog?.brands?.find(x => x.brand === b) || null;
}
function getModelObj(){
    const bo = getBrandObj();
    const m = modelSel.value;
    return bo?.models?.find(x => x.model === m) || null;
}

function computeEnginesOrTrims(modelObj, variantName){
    if (!modelObj) return { engines: [], packagesByEngine: new Map(), trims: [] };

    let enginesArr = [];
    let trimsArr = [];
    const packagesByEngine = new Map();

    // Variant seçiliyse önce variant içeriği
    if (variantName && Array.isArray(modelObj.variants)) {
        const vo = modelObj.variants.find(v => v.variant === variantName);
        if (vo) {
            if (Array.isArray(vo.engines)) {
                enginesArr = vo.engines.map(e => e.engine).filter(Boolean);
                vo.engines.forEach(e => packagesByEngine.set(e.engine, Array.isArray(e.packages) ? e.packages : []));
            } else if (Array.isArray(vo.trims)) {
                trimsArr = vo.trims;
            } else if (Array.isArray(vo.packages)) {
                packagesByEngine.set("", vo.packages);
            }
            return { engines: enginesArr, packagesByEngine, trims: trimsArr };
        }
    }

    // Model düzeyi
    if (Array.isArray(modelObj.engines)) {
        enginesArr = modelObj.engines.map(e => e.engine).filter(Boolean);
        modelObj.engines.forEach(e => packagesByEngine.set(e.engine, Array.isArray(e.packages) ? e.packages : []));
    } else if (Array.isArray(modelObj.trims)) {
        trimsArr = modelObj.trims;
    }

    return { engines: enginesArr, packagesByEngine, trims: trimsArr };
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

function refreshVariantEnginePackage(){
    const mo = getModelObj();

    const variants = Array.isArray(mo?.variants)
        ? mo.variants.map(v => v.variant).filter(Boolean)
        : [];
    setOptions(variantSel, variants, "Variant (ops.)");

    const variantName = variantSel.value || "";
    const { engines, trims, packagesByEngine } = computeEnginesOrTrims(mo, variantName);

    if (engines.length) setOptions(engineSel, engines, "Engine (ops.)");
    else if (trims.length) setOptions(engineSel, trims, "Trim (ops.)");
    else setOptions(engineSel, [], "Engine/Trim (ops.)");

    refreshPackage(packagesByEngine);
}

function refreshPackage(packagesByEngine){
    const engine = engineSel.value || "";
    const pk = packagesByEngine.get(engine) || packagesByEngine.get("") || [];
    setOptions(pkgSel, pk, "Package (ops.)");
}

function buildDataJson(){
    return JSON.stringify({
        // Listing
        title: $("title").value.trim(),
        description: $("description").value || null,
        price: Number($("price").value),
        currency: $("currency").value,
        negotiable: $("negotiable").checked === true,
        city: $("city").value.trim(),
        district: ($("district").value || "").trim() || null,

        // Catalog selection
        brand: brandSel.value,
        model: modelSel.value,
        variant: variantSel.value || null,
        engine: engineSel.value || null,
        carPackage: pkgSel.value || null,

        // Required enums
        transmission: $("transmission").value,
        fuelType: $("fuelType").value,
        bodyType: $("bodyType").value,

        // Car numeric/text
        year: Number($("year").value),
        kilometer: Number($("km").value),
        color: ($("color").value || "").trim() || null,
        engineVolumeCc: ($("engineCc").value || "").trim() ? Number($("engineCc").value) : null,
        enginePowerHp: ($("engineHp").value || "").trim() ? Number($("engineHp").value) : null
    });
}

function updatePreview(){
    if (!preview) return;

    const files = Array.from(imagesInp.files || []);
    const limited = files.slice(0, 10);

    preview.innerHTML = "";
    limited.forEach(f => {
        const url = URL.createObjectURL(f);
        const img = document.createElement("img");
        img.src = url;
        img.alt = "preview";
        preview.appendChild(img);
    });

    if (files.length > 10) showAlert("err", "10'dan fazla resim seçtin, ilk 10'u kullanılacak.");
}

async function submitForm(e){
    e.preventDefault();
    hideAlert();

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || !token.trim()){
        window.location.href = "/templates/Login.html";
        return;
    }

    if (!brandSel.value || !modelSel.value) {
        showAlert("err", "Marka ve model seçmelisin.");
        return;
    }

    const fd = new FormData();
    fd.append(
        "data",
        new Blob([buildDataJson()], { type: "application/json" })
    );


    const files = Array.from(imagesInp.files || []).slice(0, 10);
    files.forEach(f => fd.append("images", f));

    const res = await fetch(CREATE_URL, {
        method: "POST",
        headers: { Authorization: `Bearer ${token.trim()}` },
        body: fd
    });

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(()=>null)
        : await res.text().catch(()=>null);

    if (!res.ok) {
        const msg = (body && (body.message || body.error)) || (typeof body === "string" ? body : "") || `HTTP ${res.status}`;
        showAlert("err", msg);
        return;
    }

    showAlert("ok", "İlan oluşturuldu. Ana Ekrana yönlendiriliyorsun...");
    setTimeout(() => goStoreHome(), 450);
}

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

        brandSel.addEventListener("change", () => refreshModel());

        modelSel.addEventListener("change", () => {
            // model değişince variant/engine/package alanlarını yeniden kur
            refreshVariantEnginePackage();
        });

        variantSel.addEventListener("change", () => {
            refreshVariantEnginePackage();
        });

        engineSel.addEventListener("change", () => {
            const mo = getModelObj();
            const { packagesByEngine } = computeEnginesOrTrims(mo, variantSel.value || "");
            refreshPackage(packagesByEngine);
        });

        imagesInp.addEventListener("change", updatePreview);

        // HTML'de form id="form"
        if (!formEl) throw new Error("Form bulunamadı (id='form').");
        formEl.addEventListener("submit", submitForm);

    } catch (e){
        showAlert("err", e.message || "İlan oluşturma ekranı hazırlanamadı.");
    }
});
