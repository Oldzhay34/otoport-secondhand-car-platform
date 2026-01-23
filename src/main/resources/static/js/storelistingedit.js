"use strict";

const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "token";
const THEME_KEY = "theme";

const GET_URL = (id) => `${API_BASE}/api/store/listings/${id}`;
const PUT_URL = (id) => `${API_BASE}/api/store/listings/${id}`;
const DEL_URL = (id) => `${API_BASE}/api/store/listings/${id}`;

function $(id){ return document.getElementById(id); }

/* ===================== DOM ===================== */
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

/* ===================== ALERT ===================== */
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

/* ===================== THEME ===================== */
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

/* ===================== FETCH ===================== */
async function fetchJson(url, options = {}){
    const res = await fetch(url, { ...options, cache:"no-store" });

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json")
        ? await res.json().catch(()=>null)
        : await res.text().catch(()=>null);

    if (!res.ok){
        const msg =
            (body && (body.message || body.error)) ||
            (typeof body === "string" ? body : "") ||
            `HTTP ${res.status}`;
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

/* ===================== TOAST ===================== */
let toastWrap = null;

function ensureToastWrap(){
    if (toastWrap) return toastWrap;
    toastWrap = document.createElement("div");
    toastWrap.className = "toast-wrap";
    document.body.appendChild(toastWrap);
    return toastWrap;
}
function escapeHtml(s){
    return String(s ?? "").replace(/[&<>"']/g, (m) => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[m]));
}
function toast(type, title, message, ms = 1500){
    const wrap = ensureToastWrap();
    const el = document.createElement("div");
    el.className = "toast " + (type === "err" ? "err" : "ok");
    el.innerHTML = `
    <div class="t-top">
      <div class="t-title"><span class="t-dot"></span>${escapeHtml(title || "")}</div>
    </div>
    ${message ? `<div class="t-msg">${escapeHtml(message)}</div>` : ``}
  `;
    wrap.appendChild(el);

    window.setTimeout(() => {
        el.classList.add("out");
        window.setTimeout(() => el.remove(), 220);
    }, ms);
}

/* ===================== NAV ===================== */
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

/* ===================== FORM FILL ===================== */
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
        carHint.textContent = "Expertiz kalıcı kayıt için Kaydet’e basmalısın.";
    }
}

/* ===================== EXPERTIZ / KROKI ===================== */
const sketchBaseImg = $("sketchBase");
const sketchOutline = $("sketchOutline");
const sketchParts = $("sketchParts");
const sketchHint = $("sketchHint");

const partSelect = $("partSelect");
const statusSelect = $("statusSelect");
const applyPartStatusBtn = $("applyPartStatusBtn");
const sideInfo = $("sideInfo");
const resetExpertBtn = $("resetExpertBtn");

const STATUS_CYCLE = ["ORIGINAL", "PAINTED", "LOCAL_PAINT", "REPLACED"];
const expertState = new Map(); // part -> status

const BASE_FILE = "expertiz raporu cam ve lastikk.png";
const ASSET_CONF = { root: "/images", folder: "expertiz" };
function asset(p){ return encodeURI(p); }

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
    TRUNK_LID: "ekspertiz bagaj.png"
};

const ALL_PARTS = Object.keys(PARTS);

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
        HOOD: "Kaput",
        ROOF: "Tavan",
        FRONT_BUMPER: "Ön Tampon",
        REAR_BUMPER: "Arka Tampon",
        FRONT_LEFT_FENDER: "Sol Ön Çamurluk",
        FRONT_RIGHT_FENDER: "Sağ Ön Çamurluk",
        FRONT_LEFT_DOOR: "Sol Ön Kapı",
        FRONT_RIGHT_DOOR: "Sağ Ön Kapı",
        REAR_LEFT_DOOR: "Sol Arka Kapı",
        REAR_RIGHT_DOOR: "Sağ Arka Kapı",
        REAR_LEFT_FENDER: "Sol Arka Çamurluk",
        REAR_RIGHT_FENDER: "Sağ Arka Çamurluk",
        TRUNK_LID: "Bagaj Kapağı"
    };
    return map[part] || part;
}
function trStatusName(s){
    const map = {
        ORIGINAL: "Orijinal",
        PAINTED: "Boyalı",
        LOCAL_PAINT: "Lokal Boya",
        REPLACED: "Değişen"
    };
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

    if (sideInfo){
        sideInfo.innerHTML = `
      <div class="sel-badge"><i></i> Seçili Parça</div>
      <div class="sel-row"><div class="sel-k">Parça</div><div class="sel-v">${escapeHtml(trPartName(p))}</div></div>
      <div class="sel-row"><div class="sel-k">Durum</div><div class="sel-v">${escapeHtml(trStatusName(cur))}</div></div>
      <div class="sel-row"><div class="sel-k">İpucu</div><div class="sel-v" style="font-weight:900;color:var(--muted);">Kroki üstünden tıklayarak da değiştirebilirsin</div></div>
    `;
    }
}

function updateSketchHint(){
    if (!sketchHint) return;
    let changed = 0;
    expertState.forEach(v => { if (v && v !== "ORIGINAL") changed++; });
    sketchHint.textContent = changed === 0 ? "Tüm parçalar orijinal" : `${changed} parça işaretlendi`;
}

function applySelectedPartStatus(){
    const p = partSelect?.value;
    const st = statusSelect?.value;
    if (!p || !st) return;

    expertState.set(p, st);

    renderSketch();
    updateSketchHint();
    syncStatusSelectToCurrentPart();

    toast("ok", "Uygulandı", `${trPartName(p)} → ${trStatusName(st)} (Kaydet ile kalıcı olur)`, 1400);
}

function renderSketch(){
    if (!sketchOutline || !sketchParts) return;

    sketchOutline.innerHTML = "";
    sketchParts.innerHTML = "";

    ALL_PARTS.forEach(part => {
        const mask = partUrl(part);
        if (!mask) return;

        const onclick = (e) => {
            e.preventDefault();
            e.stopPropagation();

            const next = cycleStatus(expertState.get(part) || "ORIGINAL");
            expertState.set(part, next);

            if (partSelect && partSelect.value === part){
                if (statusSelect) statusSelect.value = next;
                syncStatusSelectToCurrentPart();
            }

            renderSketch();
            updateSketchHint();
            toast("ok", "Güncellendi", `${trPartName(part)} → ${trStatusName(next)}`, 900);
        };

        const o = document.createElement("div");
        o.className = "sketch-part";
        o.style.setProperty("--fill", "rgba(255,255,255,.16)");
        o.style.setProperty("--mask", `url("${mask}")`);
        o.title = `${trPartName(part)} (tıkla)`;
        o.onclick = onclick;
        sketchOutline.appendChild(o);

        const st = expertState.get(part) || "ORIGINAL";
        const fill = fillColor(st);
        if (fill){
            const c = document.createElement("div");
            c.className = "sketch-part";
            c.style.setProperty("--fill", fill);
            c.style.setProperty("--mask", `url("${mask}")`);
            c.title = `${trPartName(part)}: ${trStatusName(st)} (tıkla)`;
            c.onclick = onclick;
            sketchParts.appendChild(c);
        }
    });
}

function resetExpert(){
    ALL_PARTS.forEach(p => expertState.set(p, "ORIGINAL"));
    renderSketch();
    updateSketchHint();
    syncStatusSelectToCurrentPart();
    toast("ok", "Sıfırlandı", "Expertiz tüm parçalar orijinale döndü.", 1400);
}

function initExpertUI(){
    ALL_PARTS.forEach(p => expertState.set(p, "ORIGINAL"));
    if (sketchBaseImg) sketchBaseImg.src = baseUrl();

    fillPartSelect();
    syncStatusSelectToCurrentPart();
    updateSketchHint();
    renderSketch();

    partSelect?.addEventListener("change", syncStatusSelectToCurrentPart);
    applyPartStatusBtn?.addEventListener("click", applySelectedPartStatus);
    resetExpertBtn?.addEventListener("click", resetExpert);
}

function hydrateExpertFromListing(data){
    const report = data?.expertReport || null;
    const items = Array.isArray(report?.items) ? report.items : [];

    ALL_PARTS.forEach(p => expertState.set(p, "ORIGINAL"));

    items.forEach(it => {
        const part = String(it?.part || "").trim();
        const st = String(it?.status || "").trim().toUpperCase();
        // sadece UI’da tanımlı parçaları uygula
        if (PARTS[part] && STATUS_CYCLE.includes(st)) {
            expertState.set(part, st);
        }
    });

    renderSketch();
    updateSketchHint();
    syncStatusSelectToCurrentPart();
}

function buildExpertReportDto(){
    const items = [];
    expertState.forEach((status, part) => {
        if (status && status !== "ORIGINAL") items.push({ part, status, note: null });
    });

    // result hesapla
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

/* ===================== PAYLOAD ===================== */
function buildPayload(){
    return {
        title: titleEl.value.trim() || null,
        description: (descEl.value ?? ""),
        price: priceEl.value ? Number(priceEl.value) : null,
        currency: (currencyEl.value || "TRY").trim().toUpperCase(),
        negotiable: !!negotiableEl.checked,
        city: cityEl.value.trim() || null,
        district: districtEl.value.trim() || null,

        year: yearEl.value ? Number(yearEl.value) : null,
        kilometer: kmEl.value ? Number(kmEl.value) : null,
        color: colorEl.value.trim() || null,
        engineVolumeCc: volEl.value ? Number(volEl.value) : null,
        enginePowerHp: hpEl.value ? Number(hpEl.value) : null,

        expertReport: buildExpertReportDto()
    };
}

/* ===================== API CALLS ===================== */
async function loadListing(id){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) { window.location.href = "/templates/Login.html"; return; }

    const data = await fetchJson(GET_URL(id), {
        method:"GET",
        headers: { Authorization: `Bearer ${token.trim()}` }
    });

    fillForm(data);
    hydrateExpertFromListing(data);
}

async function saveListing(id){
    hideAlert();
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) { window.location.href = "/templates/Login.html"; return; }

    const payload = buildPayload();
    window.__lastPayload = payload; // DEBUG: console’dan incele

    console.log("[SAVE] PUT ->", PUT_URL(id), payload); // DEBUG

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
    hydrateExpertFromListing(data);

    toast("ok", "Kaydedildi", "İlan + expertiz başarıyla güncellendi.", 1600);
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
    setTimeout(() => goStoreHome(), 450);
}

/* ===================== INIT ===================== */
document.addEventListener("DOMContentLoaded", async () => {
    applyThemeFromStorage();
    initThemeToggle();
    initExpertUI();

    const id = getIdFromQuery();
    if (!id){
        showAlert("err", "Listing id bulunamadı.");
        return;
    }

    backBtn?.addEventListener("click", () => goStoreHome());

    // Kaydet click -> PUT garanti
    saveBtn?.addEventListener("click", async () => {
        console.log("[CLICK] saveBtn"); // DEBUG
        try { await saveListing(id); }
        catch(e){
            console.error("[SAVE ERROR]", e);
            showAlert("err", e.message || "Güncelleme başarısız.");
            toast("err", "Hata", e.message || "Güncelleme başarısız.", 2000);
        }
    });

    deleteBtn?.addEventListener("click", async () => {
        if (!confirm("Bu ilanı silmek istediğine emin misin?")) return;
        try { await deleteListing(id); }
        catch(e){
            console.error("[DELETE ERROR]", e);
            showAlert("err", e.message || "Silme başarısız.");
            toast("err", "Hata", e.message || "Silme başarısız.", 2000);
        }
    });

    try{
        await loadListing(id);
    } catch (e){
        console.error("[LOAD ERROR]", e);
        showAlert("err", e.message || "İlan bilgisi alınamadı.");
    }
});
console.log("JS LOADED");

const id = getIdFromQuery();
console.log("ID =", id);

saveBtn.addEventListener("click", async () => {
    console.log("SAVE CLICKED");
    await saveListing(id);
});

