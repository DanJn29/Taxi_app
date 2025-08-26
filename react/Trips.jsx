import React, { useEffect, useMemo, useState } from "react";
import { router } from "@inertiajs/react";
import CompanyLayout from "./Layout";
import { MapContainer, TileLayer, Marker, Polyline, useMapEvents } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import dayjs from "dayjs";
import L from "leaflet";

// Leaflet marker fix
const DefaultIcon = L.icon({
    iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});
L.Marker.prototype.options.icon = DefaultIcon;

export default function Trips({ company, trips, vehicles, drivers }) {
    // адреса как текст
    const [fromAddr, setFromAddr] = useState("");
    const [toAddr, setToAddr] = useState("");

    // координаты — обязательны
    const [from, setFrom] = useState({ lat: null, lng: null });
    const [to, setTo] = useState({ lat: null, lng: null });

    // кого ставим кликом: from или to
    const [which, setWhich] = useState("from");

    const [form, setForm] = useState({
        vehicle_id: vehicles[0]?.id || "",
        assigned_driver_id: drivers[0]?.id || "",
        from_addr: "",
        to_addr: "",
        from_lat: "",
        from_lng: "",
        to_lat: "",
        to_lng: "",
        price_amd: 2500,
        seats_total: 4,
        departure_at: "",
        pay_methods: ["cash"],
    });

    // синхронизация адресов и координат в form перед отправкой
    useEffect(() => {
        setForm((s) => ({ ...s, from_addr: fromAddr, to_addr: toAddr }));
    }, [fromAddr, toAddr]);

    useEffect(() => {
        setForm((s) => ({
            ...s,
            from_lat: from.lat ?? "",
            from_lng: from.lng ?? "",
        }));
    }, [from.lat, from.lng]);

    useEffect(() => {
        setForm((s) => ({
            ...s,
            to_lat: to.lat ?? "",
            to_lng: to.lng ?? "",
        }));
    }, [to.lat, to.lng]);

    function submit(e) {
        e.preventDefault();

        // Жесткая валидация на фронте, чтобы не слать пустые координаты
        const hasFrom =
            Number.isFinite(Number(form.from_lat)) && Number.isFinite(Number(form.from_lng));
        const hasTo =
            Number.isFinite(Number(form.to_lat)) && Number.isFinite(Number(form.to_lng));

        if (!hasFrom || !hasTo) {
            alert("Քարտեզի վրա ընտրիր «Որտեղից» և «Ուր» կետերը (երկու նշան)");
            return;
        }

        // Нормализуем типы
        const payload = {
            ...form,
            vehicle_id: String(form.vehicle_id || ""),
            assigned_driver_id: String(form.assigned_driver_id || ""),
            price_amd: Number(form.price_amd) || 0,
            seats_total: Number(form.seats_total) || 1,
            from_lat: Number(form.from_lat),
            from_lng: Number(form.from_lng),
            to_lat: Number(form.to_lat),
            to_lng: Number(form.to_lng),
            departure_at: form.departure_at, // "YYYY-MM-DDTHH:mm"
        };

        router.post(route("company.trips.store", company.id), payload, { preserveScroll: true });
    }

    // центр карты — если точек нет, ставим в центр Армении
    const center = useMemo(() => {
        if (
            Number.isFinite(from.lat) &&
            Number.isFinite(from.lng) &&
            Number.isFinite(to.lat) &&
            Number.isFinite(to.lng)
        ) {
            return [(from.lat + to.lat) / 2, (from.lng + to.lng) / 2];
        }
        return [40.3, 44.3];
    }, [from, to]);

    return (
        <CompanyLayout company={company} current="trips">
            <h1 className="mb-4 text-2xl font-bold">Երթուղիներ</h1>

            {/* Форма создания */}
            <form
                onSubmit={submit}
                className="mb-6 grid gap-4 rounded-2xl border bg-white p-4 lg:grid-cols-2"
            >
                {/* Левая колонка: поля */}
                <div className="space-y-3">
                    <Select
                        label="Մեքենա"
                        value={form.vehicle_id}
                        onChange={(v) => setForm((s) => ({ ...s, vehicle_id: v }))}
                        options={vehicles.map((v) => ({
                            v: v.id,
                            t: `${v.brand} ${v.model} · ${v.plate}`,
                        }))}
                    />

                    <Select
                        label="Վարորդ"
                        value={form.assigned_driver_id}
                        onChange={(v) => setForm((s) => ({ ...s, assigned_driver_id: v }))}
                        options={drivers.map((d) => ({ v: d.id, t: d.name }))}
                    />

                    <Input label="Որտեղից (հասցե)" value={fromAddr} onChange={setFromAddr} />
                    <Input label="Ուր (հասցե)" value={toAddr} onChange={setToAddr} />

                    <div className="grid grid-cols-2 gap-3">
                        <Input
                            type="datetime-local"
                            label="Մեկնման ժամանակ"
                            value={form.departure_at}
                            onChange={(v) => setForm((s) => ({ ...s, departure_at: v }))}
                        />
                        <Input
                            type="number"
                            label="Գին (AMD)"
                            value={form.price_amd}
                            onChange={(v) => setForm((s) => ({ ...s, price_amd: Number(v) || 0 }))}
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <Input
                            type="number"
                            label="Տեղերի թիվ"
                            value={form.seats_total}
                            onChange={(v) => setForm((s) => ({ ...s, seats_total: Number(v) || 1 }))}
                        />
                        <PayMethods
                            value={form.pay_methods}
                            onChange={(arr) => setForm((s) => ({ ...s, pay_methods: arr }))}
                        />
                    </div>

                    {/* Координаты read-only для контроля */}
                    <div className="grid grid-cols-2 gap-3 text-xs text-black/70">
                        <div className="rounded-xl border p-3">
                            <div className="font-medium">Ընտրված «Որտեղից»</div>
                            <div>lat: {from.lat ?? "—"}</div>
                            <div>lng: {from.lng ?? "—"}</div>
                        </div>
                        <div className="rounded-xl border p-3">
                            <div className="font-medium">Ընտրված «Ուր»</div>
                            <div>lat: {to.lat ?? "—"}</div>
                            <div>lng: {to.lng ?? "—"}</div>
                        </div>
                    </div>

                    <button className="w-full rounded-xl bg-black px-4 py-2 font-semibold text-[#ffdd2c]">
                        Ստեղծել երթուղի
                    </button>
                    <div className="text-sm text-black/60">
                        Քարտեզի վրա սեղմիր՝ նախ «Որտեղից», ապա «Ուր» կետերը: Կոորդինատները պարտադիր են։
                    </div>
                </div>

                {/* Правая колонка: карта */}
                <div className="space-y-2">
                    <ToggleWhich which={which} setWhich={setWhich} />
                    <div className="h-96 overflow-hidden rounded-2xl border">
                        <MapPick
                            center={center}
                            from={from}
                            to={to}
                            which={which}
                            setFrom={setFrom}
                            setTo={setTo}
                            setWhich={setWhich}
                        />
                    </div>
                </div>
            </form>

            {/* Список уже созданных рейсов */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {trips.map((t) => {
                    const seatsTaken = t.seats_taken ?? 0;
                    const seatsTotal = t.seats_total ?? 0;
                    const seatsLeft = Math.max(0, seatsTotal - seatsTaken);
                    const canPublish = t.status === "draft" && seatsLeft > 0;

                    const badgeCls =
                        t.status === "published"
                            ? "bg-emerald-100 text-emerald-700"
                            : t.status === "draft"
                                ? "bg-amber-100 text-amber-700"
                                : t.status === "archived"
                                    ? "bg-slate-100 text-slate-700"
                                    : "bg-rose-100 text-rose-700";
                    const statusLabel = (s) =>
                        ({ draft: "Սևագիր", published: "Հրապարակված", archived: "Արխիվ" }[s] || s);

                    return (
                        <div key={t.id} className="rounded-2xl border bg-white p-4">
                            <div className="text-sm text-black/60">
                                {t.vehicle?.brand} {t.vehicle?.model} · {t.vehicle?.plate}
                            </div>
                            <div className="mt-1 font-semibold">
                                {t.from_addr} → {t.to_addr}
                            </div>
                            <div className="text-sm text-black/70">
                                Գին՝ {new Intl.NumberFormat("hy-AM").format(t.price_amd)} AMD
                            </div>
                            <div className="text-sm text-black/70">
                                Տեղեր՝ {seatsTaken}/{seatsTotal}
                            </div>
                            <div className="text-sm text-black/70">
                                Մեկնում՝ {t.departure_at ? dayjs(t.departure_at).format("YYYY-MM-DD HH:mm") : "—"}
                            </div>

                            <div className="mt-2 flex items-center gap-2">
                <span className={`text-xs px-2 py-1 rounded ${badgeCls}`}>
                  {statusLabel(t.status)}
                </span>
                                <span className="text-xs text-black/60">
                  Սպասվող հայտեր՝ {t.pending_requests_count} · Հաստատված՝{" "}
                                    {t.accepted_requests_count}
                </span>
                            </div>

                            <div className="mt-3 flex flex-wrap gap-2">
                                {canPublish && (
                                    <button
                                        onClick={() =>
                                            router.post(route("company.trips.publish", [company.id, t.id]), {}, { preserveScroll: true })
                                        }
                                        className="rounded border border-black/10 bg-[#ffdd2c] px-3 py-1.5 text-black"
                                    >
                                        Հրապարակել
                                    </button>
                                )}

                                {t.status !== "archived" && (
                                    <button
                                        onClick={() =>
                                            router.post(route("company.trips.archive", [company.id, t.id]), {}, { preserveScroll: true })
                                        }
                                        className="rounded bg-black px-3 py-1.5 text-[#ffdd2c]"
                                    >
                                        Արխիվացնել
                                    </button>
                                )}

                                {t.status === "archived" && (
                                    <button
                                        onClick={() =>
                                            router.post(route("company.trips.unarchive", [company.id, t.id]), {}, { preserveScroll: true })
                                        }
                                        className="rounded bg-slate-200 px-3 py-1.5 text-slate-900"
                                    >
                                        Վերադարձնել (սևագիր)
                                    </button>
                                )}
                            </div>
                        </div>
                    );
                })}
                {trips.length === 0 && (
                    <div className="md:col-span-2 rounded-2xl border bg-white p-6 text-center text-black/60">
                        Դատարկ է
                    </div>
                )}
            </div>
        </CompanyLayout>
    );
}

function ToggleWhich({ which, setWhich }) {
    return (
        <div className="flex items-center gap-2 text-sm">
            <span className="text-black/70">Ընտրում ես՝</span>
            <button
                type="button"
                onClick={() => setWhich("from")}
                className={`rounded-lg px-3 py-1.5 ${which === "from" ? "bg-black text-[#ffdd2c]" : "border"}`}
            >
                Որտեղից
            </button>
            <button
                type="button"
                onClick={() => setWhich("to")}
                className={`rounded-lg px-3 py-1.5 ${which === "to" ? "bg-black text-[#ffdd2c]" : "border"}`}
            >
                Ուր
            </button>
            <span className="text-black/50">Քարտեզի վրա սեղմիր մարկեր դնելու համար</span>
        </div>
    );
}

function MapPick({ center, from, to, which, setFrom, setTo, setWhich }) {
    function Clicker() {
        useMapEvents({
            click(e) {
                const { lat, lng } = e.latlng;
                if (which === "from") {
                    setFrom({ lat, lng });
                    setWhich("to");
                } else {
                    setTo({ lat, lng });
                    setWhich("from");
                }
            },
        });
        return null;
    }

    const line =
        Number.isFinite(from.lat) &&
        Number.isFinite(from.lng) &&
        Number.isFinite(to.lat) &&
        Number.isFinite(to.lng)
            ? [
                [from.lat, from.lng],
                [to.lat, to.lng],
            ]
            : null;

    return (
        <MapContainer center={center} zoom={8} style={{ height: "100%" }}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            {Number.isFinite(from.lat) && Number.isFinite(from.lng) && (
                <Marker position={[from.lat, from.lng]} />
            )}
            {Number.isFinite(to.lat) && Number.isFinite(to.lng) && (
                <Marker position={[to.lat, to.lng]} />
            )}
            {line && <Polyline positions={line} />}
            <Clicker />
        </MapContainer>
    );
}

function PayMethods({ value = [], onChange }) {
    const toggle = (k) => onChange(value.includes(k) ? value.filter((i) => i !== k) : [...value, k]);
    return (
        <div className="rounded-xl border p-3">
            <div className="mb-2 text-sm font-medium text-black">Վճարման եղանակ</div>
            <div className="flex gap-3 text-sm">
                <label className="inline-flex items-center gap-2">
                    <input type="checkbox" checked={value.includes("cash")} onChange={() => toggle("cash")} />{" "}
                    Կանխիկ
                </label>
                <label className="inline-flex items-center gap-2">
                    <input type="checkbox" checked={value.includes("card")} onChange={() => toggle("card")} />{" "}
                    Քարտ
                </label>
            </div>
        </div>
    );
}

function Input({ label, value, onChange, type = "text" }) {
    return (
        <label className="block text-sm">
            <div className="mb-1 text-black/70">{label}</div>
            <input
                type={type}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className="w-full rounded-xl border px-3 py-2"
            />
        </label>
    );
}

function Select({ label, value, onChange, options }) {
    return (
        <label className="block text-sm">
            <div className="mb-1 text-black/70">{label}</div>
            <select
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className="w-full rounded-xl border px-3 py-2"
            >
                {options.map((o) => (
                    <option key={o.v} value={o.v}>
                        {o.t}
                    </option>
                ))}
            </select>
        </label>
    );
}
