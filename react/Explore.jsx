import React, { useEffect, useState } from "react";
import { Link, router, usePage } from "@inertiajs/react";
import dayjs from "dayjs";
import { createPortal } from "react-dom";
import {
    MapContainer,
    TileLayer,
    Marker,
    Polyline,
} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import ClientLayout from "@/Layouts/ClientLayout";
import L from "leaflet";

/* ----- Leaflet marker fix ----- */
const DefaultIcon = L.icon({
    iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});
L.Marker.prototype.options.icon = DefaultIcon;

/* utils */
function num(v, fb = null) {
    const n = typeof v === "string" ? parseFloat(v) : (typeof v === "number" ? v : NaN);
    return Number.isFinite(n) ? n : fb;
}
function formatAMD(n){ try{ return new Intl.NumberFormat("hy-AM").format(n||0);}catch{return n;} }
function distributeDots(n, taken, pending, free){
    const arr=[]; let r=Math.min(n,taken); for(let i=0;i<r;i++)arr.push("red");
    let a=Math.min(n-arr.length,pending); for(let i=0;i<a;i++)arr.push("amber");
    let g=Math.min(n-arr.length,free); for(let i=0;i<g;i++)arr.push("green");
    while(arr.length<n)arr.push("green"); return arr.slice(0,n);
}

/* ====================================================== */

export default function Explore() {
    const { trips, filters } = usePage().props;
    return (
        <ClientLayout current="trips">
            <div className="space-y-6">
                <h1 className="text-3xl font-extrabold">Գտնել ուղևորություն</h1>
                <SearchForm filters={filters}/>
                <ResultsList trips={trips}/>
            </div>
        </ClientLayout>
    );
}

/* ------------ SEARCH ------------ */
function SearchForm({ filters }) {
    const [f, setF] = useState({
        from: filters.from || "",
        to: filters.to || "",
        date: filters.date || "",
        max_price: filters.max_price || "",
        seats: filters.seats || "",
        pay: filters.pay || "",
    });

    function submit(e){
        e.preventDefault();
        router.get("/trips", f, { preserveScroll:true, preserveState:true });
    }

    return (
        <form onSubmit={submit} className="grid gap-3 rounded-3xl border border-black/10 bg-white p-4 md:grid-cols-6">
            <Field label="Որտեղից" value={f.from} onChange={v=>setF(s=>({...s,from:v}))}/>
            <Field label="Ուր" value={f.to} onChange={v=>setF(s=>({...s,to:v}))}/>
            <Field type="date" label="Ամսաթիվ" value={f.date} onChange={v=>setF(s=>({...s,date:v}))}/>
            <Field type="number" label="Գինը մինչև (AMD)" value={f.max_price} onChange={v=>setF(s=>({...s,max_price:v}))}/>
            <Field type="number" label="Տեղերի թիվ" value={f.seats} onChange={v=>setF(s=>({...s,seats:v}))}/>
            <Select label="Վճարում" value={f.pay} onChange={v=>setF(s=>({...s,pay:v}))}
                    options={[{v:"",t:"Բոլորը"},{v:"cash",t:"Կանխիկ"},{v:"card",t:"Քարտ"}]}/>
            <div className="md:col-span-6 flex justify-end">
                <button className="rounded-xl bg-black px-4 py-2 font-semibold text-[#ffdd2c] hover:brightness-95">Փնտրել</button>
            </div>
        </form>
    );
}

/* ------------ RESULTS ------------ */
function ResultsList({ trips }) {
    return (
        <section className="space-y-3">
            {trips?.data?.length === 0 && (
                <div className="rounded-2xl border border-black/10 bg-white p-6 text-black/70">
                    Արդյունքներ չեն գտնվել։
                </div>
            )}

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {trips?.data?.map(t => <TripCard key={t.id} t={t}/>)}
            </div>

            <Pagination meta={trips?.meta} links={trips?.links}/>
        </section>
    );
}

function TripCard({ t }) {
    const taken   = t.seats_taken || 0;
    const pending = t.pending_requests_count || 0;
    const free    = Math.max(0, (t.seats_total||0) - taken);
    const dots    = distributeDots(4, taken, pending, free);
    const canBook = usePage().props?.auth?.user;

    const [showInteractive, setShowInteractive] = useState(false);

    const closeInteractive = () => setShowInteractive(false);
    const openBook = () => setShowInteractive(false); // чтобы при модалке карта не мешала

    return (
        <div className="relative overflow-hidden rounded-2xl border border-black/10 bg-white">
            <div className="flex items-center justify-between p-3">
                <div className="text-sm text-black/60">{t.driver?.name || "Վարորդ"}</div>
                <div className="text-xs text-black/60">{dayjs(t.departure_at).format("MM-DD HH:mm")}</div>
            </div>

            {/* Мини-карта: статичная + поверх нее по требованию интерактив */}
            <div className="mini-map relative h-36 overflow-hidden">
                <StaticMiniMap t={t}/>
                {showInteractive && <InteractiveMiniMap t={t} onClose={closeInteractive}/>}
                <div className="pointer-events-none absolute left-3 top-3 rounded-lg bg-black/70 px-2 py-1 text-xs text-[#ffdd2c]">
                    {t.vehicle?.brand} {t.vehicle?.model} · {t.vehicle?.plate}
                </div>
                <button
                    type="button"
                    className="absolute bottom-2 right-2 rounded-lg bg-black/80 px-2 py-1 text-[11px] text-[#ffdd2c] shadow"
                    onClick={() => setShowInteractive(v=>!v)}
                >
                    {showInteractive ? "Ավարտել" : "Շարժել քարտեզը"}
                </button>
            </div>

            <div className="space-y-3 p-4">
                <div className="font-semibold">{t.from_addr} → {t.to_addr}</div>
                <div className="text-sm text-black/80">Գին՝ <b>{formatAMD(t.price_amd)} AMD</b></div>

                <div>
                    <div className="flex items-center justify-between text-xs text-black/60">
                        <span>Տեղեր՝ {taken}/{t.seats_total}</span>
                        <span>Սպասում են՝ {pending}</span>
                    </div>
                    <div className="mt-2 flex gap-2">
                        {dots.map((c,i)=>
                            <span key={i} className={`h-3 w-3 rounded-full ${c==="red"?"bg-rose-500":c==="amber"?"bg-amber-400":"bg-emerald-500"}`}/>
                        )}
                    </div>
                </div>

                <div className="flex items-center justify-between">
                    <div className="text-xs text-black/60">
                        Վճարում՝ {t.pay_methods?.includes("card")?"Քարտ":""}
                        {t.pay_methods?.includes("card")&&t.pay_methods?.includes("cash")?" · ":""}
                        {t.pay_methods?.includes("cash")?"Կանխիկ":""}
                    </div>
                    {canBook ? (
                        <BookButton t={t} disabled={free<=0} onOpen={openBook}/>
                    ) : (
                        <Link href="/login" className="rounded-lg bg-black px-3 py-1.5 text-sm text-[#ffdd2c]">
                            Մուտք գործել
                        </Link>
                    )}
                </div>
            </div>
        </div>
    );
}

/* -------- MiniMap: статичная (не ловит события вообще) -------- */
function StaticMiniMap({ t }) {
    const fl = num(t.from_lat), fn = num(t.from_lng), tl = num(t.to_lat), tn = num(t.to_lng);
    if (![fl,fn,tl,tn].every(Number.isFinite)) {
        return <div className="grid h-full w-full place-items-center bg-[#fff8d1] text-sm text-black/60">Քարտեզը հասանելի չէ</div>;
    }
    const center = [(fl+tl)/2, (fn+tn)/2];
    return (
        <div className="absolute inset-0">
            <MapContainer
                center={center}
                zoom={8}
                style={{height:"100%",width:"100%", pointerEvents:"none"}} /* самое важное */
                dragging={false}
                scrollWheelZoom={false}
                doubleClickZoom={false}
                touchZoom={false}
                keyboard={false}
                boxZoom={false}
                zoomControl={false}
                attributionControl={false}
                className="z-0"
            >
                <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"/>
                <Marker position={[fl,fn]}/>
                <Marker position={[tl,tn]}/>
                <Polyline positions={[[fl,fn],[tl,tn]]}/>
            </MapContainer>
        </div>
    );
}

/* -------- MiniMap: интерактив поверх статичной, но внутри карточки -------- */
function InteractiveMiniMap({ t, onClose }) {
    const fl = num(t.from_lat), fn = num(t.from_lng), tl = num(t.to_lat), tn = num(t.to_lng);
    const center = [(fl+tl)/2, (fn+tn)/2];

    return (
        <div className="mini-map__interactive absolute inset-0 z-10">
            <MapContainer
                center={center}
                zoom={8}
                style={{height:"100%",width:"100%"}}
                dragging
                scrollWheelZoom
                doubleClickZoom
                touchZoom
                keyboard
                boxZoom
                zoomControl={false}
            >
                <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"/>
                <Marker position={[fl,fn]}/>
                <Marker position={[tl,tn]}/>
                <Polyline positions={[[fl,fn],[tl,tn]]}/>
            </MapContainer>

            {/* маленькая кнопка “закрыть” внутри слоя карты */}
            <button
                type="button"
                onClick={onClose}
                className="absolute right-2 top-2 rounded-md bg-black/80 px-2 py-[2px] text-[11px] text-[#ffdd2c]"
            >
                Կանգնեցնել
            </button>
        </div>
    );
}

/* ------------ BOOK MODAL (portal) ------------ */
function BookButton({ t, disabled, onOpen }) {
    const [open,setOpen] = useState(false);
    return (
        <>
            <button
                disabled={disabled}
                onClick={()=>{ onOpen?.(); setOpen(true); }}
                className="rounded-lg border border-black/10 bg-[#ffdd2c] px-3 py-1.5 text-sm text-black disabled:opacity-50"
            >
                Ամրագրել
            </button>
            {open && <BookModal t={t} onClose={()=>setOpen(false)}/>}
        </>
    );
}

function BookModal({ t, onClose }) {
    const [name,setName] = useState("");
    const [phone,setPhone] = useState("");
    const [seats,setSeats] = useState(1);
    const [payment,setPayment] = useState("cash");
    const [errors,setErrors] = useState({});
    const [processing,setProcessing] = useState(false);

    useEffect(()=>{ document.body.classList.add("modal-open"); return ()=>document.body.classList.remove("modal-open"); },[]);

    function submit(e){
        e.preventDefault();
        setProcessing(true);
        router.post(`/trips/${t.id}/book`,
            { passenger_name:name, phone, seats, payment },
            {
                preserveScroll:true,
                onError: (e)=>{ setErrors(e); setProcessing(false); },
                onSuccess: ()=>{ setProcessing(false); onClose(); }
            }
        );
    }

    const total = seats*(t.price_amd||0);

    return createPortal(
        <div className="fixed inset-0 z-[9999] grid place-items-center bg-black/60 p-4" onClick={onClose}>
            <div className="w-full max-w-md rounded-2xl border border-black/10 bg-white p-4 shadow-xl" onClick={e=>e.stopPropagation()}>
                <div className="mb-2 font-bold">Ամրագրել ուղևորություն</div>
                <div className="mb-3 text-sm text-black/60">{t.from_addr} → {t.to_addr} · {formatAMD(t.price_amd)} AMD / տեղ</div>

                <form onSubmit={submit} className="space-y-3">
                    <Field label="Անուն Ազգանուն" value={name} onChange={setName} error={errors.passenger_name}/>
                    <Field label="Հեռախոս" value={phone} onChange={setPhone} error={errors.phone}/>
                    <div className="grid grid-cols-2 gap-3">
                        <Field type="number" label="Տեղերի թիվ" value={seats} onChange={v=>setSeats(Math.max(1,Math.min(3,Number(v)||1)))} error={errors.seats}/>
                        <Select label="Վճարում" value={payment} onChange={setPayment} options={[{v:"cash",t:"Կանխիկ"},{v:"card",t:"Քարտ"}]}/>
                    </div>

                    <div className="text-sm text-black/80">Ընդհանուր՝ <b>{formatAMD(total)} AMD</b></div>

                    <div className="flex justify-end gap-2">
                        <button type="button" onClick={onClose} className="rounded-lg px-3 py-1.5 text-sm hover:bg-black/10">Փակել</button>
                        <button disabled={processing} className="rounded-lg bg-black px-3 py-1.5 text-sm text-[#ffdd2c]">Ուղարկել հայտը</button>
                    </div>
                </form>
            </div>
        </div>,
        document.body
    );
}

/* ------------ small UI helpers ------------ */
function Field({ label, value, onChange, type="text", error }) {
    return (
        <label className="block text-sm">
            <div className="mb-1 text-black/80">{label}</div>
            <input type={type} value={value} onChange={e=>onChange(e.target.value)} className="w-full rounded-xl border border-black/10 px-3 py-2"/>
            {error && <div className="mt-1 text-xs text-rose-600">{error}</div>}
        </label>
    );
}
function Select({ label, value, onChange, options=[], error }) {
    return (
        <label className="block text-sm">
            <div className="mb-1 text-black/80">{label}</div>
            <select value={value} onChange={e=>onChange(e.target.value)} className="w-full rounded-xl border border-black/10 px-3 py-2">
                {options.map(o=><option key={o.v} value={o.v}>{o.t}</option>)}
            </select>
            {error && <div className="mt-1 text-xs text-rose-600">{error}</div>}
        </label>
    );
}
function Pagination({ meta, links }) {
    if (!meta || !links) return null;
    return (
        <div className="mt-2 flex justify-center gap-2">
            {links.map((l,i)=>(
                <Link key={i} href={l.url||""} preserveScroll
                      className={`rounded-lg px-3 py-1.5 text-sm ${l.active?"bg-black text-[#ffdd2c]":"border border-black/10 bg-white hover:bg-black/5"} ${!l.url?"pointer-events-none opacity-40":""}`}
                      dangerouslySetInnerHTML={{__html:l.label}} />
            ))}
        </div>
    );
}
