import React from "react";
import { router } from "@inertiajs/react";
import dayjs from "dayjs";
import DriverLayout from "@/Layouts/DriverLayout";

export default function CompanyJobs({ active = [], upcoming = [], done = [] }) {
    return (
        <DriverLayout current="company-jobs">
            <div className="space-y-8">
                <h1 className="text-3xl font-extrabold text-black">Իմ հանձնարարումները</h1>

                {/* Активные (в пути) */}
                <Section title="Ընթացքում" emptyText="Չկան ընթացիկ երթուղիներ">
                    {active.map(t => <TripCard key={t.id} t={t} kind="active" />)}
                </Section>

                {/* Назначенные на меня */}
                <Section title="Նշանակված ինձ" emptyText="Չկան նշանակված երթուղիներ">
                    {upcoming.map(t => <TripCard key={t.id} t={t} kind="upcoming" />)}
                </Section>

                {/* Завершённые */}
                <Section title="Վերջին ավարտվածները" emptyText="Դեռ չկան ավարտվածներ">
                    {done.map(t => <TripCard key={t.id} t={t} kind="done" />)}
                </Section>
            </div>
        </DriverLayout>
    );
}

function Section({ title, children, emptyText }) {
    const has = React.Children.count(children) > 0;
    return (
        <section className="rounded-3xl border border-black/10 bg-white p-5">
            <div className="mb-3 text-xl font-bold text-black">{title}</div>
            {has ? <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-3">{children}</div>
                : <div className="text-black/60">{emptyText}</div>}
        </section>
    );
}

function TripCard({ t, kind }) {
    const seatsLeft = Math.max(0, (t.seats_total ?? 0) - (t.seats_taken ?? 0));
    const when = t.departure_at ? dayjs(t.departure_at).format("MM-DD HH:mm") : "—";
    const statusBadge = badge(t.status);
    const driverBadge = driverBadgeCls(t.driver_state);

    return (
        <div className="rounded-2xl border border-black/10 bg-white p-4">
            <div className="text-sm text-black/60">
                {t.company ? `${t.company.name}` : "—"} · {t.vehicle ? `${t.vehicle.brand} ${t.vehicle.model} · ${t.vehicle.plate}` : "—"}
            </div>
            <div className="mt-1 font-semibold text-black">
                {t.from_addr} → {t.to_addr}
            </div>
            <div className="text-sm text-black/70">Մեկնում՝ {when}</div>
            <div className="text-sm text-black/70">Գին՝ {fmt(t.price_amd)} AMD · Տեղեր՝ {t.seats_taken}/{t.seats_total}</div>

            <div className="mt-2 flex flex-wrap gap-2">
                <span className={`text-xs px-2 py-1 rounded ${statusBadge.cls}`}>{statusBadge.text}</span>
                <span className={`text-xs px-2 py-1 rounded ${driverBadge.cls}`}>{driverBadge.text}</span>
                <span className="text-xs text-black/60">հայտեր՝ {t.pending_requests_count} սպասում · {t.accepted_requests_count} հաստատված</span>
            </div>

            <div className="mt-3 flex gap-2">
                {kind === "upcoming" && (
                    <button
                        onClick={() => router.post(route('driver.jobs.start', { trip: t.id }))}
                        className="px-3 py-1.5 rounded bg-black text-[#ffdd2c]"
                    >
                        Սկսել երթուղին
                    </button>
                )}
                {kind === "active" && (
                    <button
                        onClick={() => router.post(route('driver.jobs.finish', { trip: t.id }))}
                        className="px-3 py-1.5 rounded bg-emerald-600 text-white"
                    >
                        Ավարտել
                    </button>
                )}
            </div>
        </div>
    );
}

const fmt = n => new Intl.NumberFormat('hy-AM').format(n || 0);

function badge(status){
    if (status === 'published') return { cls: 'bg-emerald-100 text-emerald-700', text: 'Հրապարակված' };
    if (status === 'draft')     return { cls: 'bg-amber-100 text-amber-700',   text: 'Սևագիր' };
    if (status === 'archived')  return { cls: 'bg-slate-100 text-slate-700',   text: 'Արխիվ' };
    return { cls: 'bg-rose-100 text-rose-700', text: status };
}
function driverBadgeCls(s){
    if (s === 'en_route') return { cls: 'bg-blue-100 text-blue-700', text: 'Ընթացքում' };
    if (s === 'done')     return { cls: 'bg-slate-100 text-slate-700', text: 'Ավարտված' };
    return { cls: 'bg-amber-100 text-amber-700', text: 'Նշանակված' };
}
