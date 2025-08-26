import React, { useMemo, useState, useEffect } from 'react'
import { useForm, router } from '@inertiajs/react'
import { MapContainer, TileLayer, Marker, Polyline, useMapEvents } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import dayjs from 'dayjs'
import DriverLayout from '@/Layouts/DriverLayout'

// leaflet marker
import L from 'leaflet'
const DefaultIcon = L.icon({iconUrl:'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png', shadowUrl:'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'})
L.Marker.prototype.options.icon = DefaultIcon

export default function DriverDashboard(props){
    return (
        <DriverLayout current="dashboard">
            <div className="space-y-8">
                <h1 className="text-3xl font-extrabold text-black">Վարորդի վահանակ</h1>
                <a id="vehicle" />
                <VehicleCard vehicle={props.vehicle} />
                <TripCreateCard vehicle={props.vehicle} />
                <a id="trips" />
                <TripsList trips={props.trips || []} />
                <a id="requests" />
                <RequestsList requests={props.requests || []} />
            </div>
        </DriverLayout>
    )
}

// ———————————— ԻՄ ՄԵՔԵՆԱՆ ————————————
function VehicleCard({ vehicle }){
    const { data, setData, post, processing, errors, transform } = useForm({
        brand: vehicle?.brand ?? '',
        model: vehicle?.model ?? '',
        seats: vehicle?.seats ?? 4,
        color: vehicle?.color ?? '#0ea5e9',
        plate: vehicle?.plate ?? '',
        photo: null,
    })
    function submit(e){
        e.preventDefault()
        transform(d => { const f = new FormData(); Object.entries(d).forEach(([k,v])=>f.append(k,v)); return f })
        post('/driver/vehicle', { preserveScroll:true })
    }
    return (
        <section className="rounded-3xl border border-black/10 bg-white p-5">
            <div className="flex items-center justify-between mb-3">
                <div className="text-xl font-bold text-black">Իմ մեքենան</div>
                <span className="text-xs text-black/60">միայն վարորդի համար</span>
            </div>
            <form onSubmit={submit} className="grid md:grid-cols-3 gap-3">
                <Input label="Մարքա" value={data.brand} onChange={v=>setData('brand',v)} error={errors.brand}/>
                <Input label="Մոդել" value={data.model} onChange={v=>setData('model',v)} error={errors.model}/>
                <Input type="number" label="Տեղերի քանակ (ուղևոր)" value={data.seats} onChange={v=>setData('seats',v)} error={errors.seats}/>
                <Input label="Գույն (hex)" value={data.color} onChange={v=>setData('color',v)} />
                <Input label="Պետ. համար" value={data.plate} onChange={v=>setData('plate',v)} />
                <File  label="Լուսանկար" onChange={f=>setData('photo',f)} />
                <div className="md:col-span-3 flex justify-end">
                    <button disabled={processing} className="px-4 py-2 rounded-xl bg-black text-[#ffdd2c] font-semibold hover:brightness-95">Պահպանել</button>
                </div>
            </form>
        </section>
    )
}

// ———————————— ԱՎԵԼԱՑՆԵԼ ՈՒՂԵՎՈՐՈՒԹՅՈՒՆ ————————————
function TripCreateCard({ vehicle }){
    const [from, setFrom] = useState({ lat: 40.1776, lng: 44.5126, addr: 'Երևան' })
    const [to,   setTo]   = useState({ lat: 40.7890, lng: 43.8470, addr: 'Գյումրի' })
    const [which, setWhich] = useState('from')

    const { data, setData, post, processing, errors } = useForm({
        vehicle_id: vehicle?.id ?? '',
        from_lat: from.lat, from_lng: from.lng, from_addr: from.addr,
        to_lat: to.lat, to_lng: to.lng, to_addr: to.addr,
        departure_at: dayjs().add(2,'hour').format('YYYY-MM-DDTHH:mm'),
        seats_total: vehicle?.seats ?? 4,
        price_amd: 2500,
        pay_methods: ['cash'],
    })

    // если машину сохранили — подставить id/места
    useEffect(() => {
        if (vehicle?.id) setData(d => ({ ...d, vehicle_id: vehicle.id, seats_total: vehicle.seats || d.seats_total }))
    }, [vehicle?.id, vehicle?.seats])

    useEffect(() => { setData(d => ({ ...d, from_lat: from.lat, from_lng: from.lng, from_addr: from.addr })) }, [from.lat, from.lng, from.addr])
    useEffect(() => { setData(d => ({ ...d, to_lat: to.lat, to_lng: to.lng, to_addr: to.addr })) }, [to.lat, to.lng, to.addr])

    const canSubmit = !!vehicle?.id

    const saveDraft = (e) => { e.preventDefault(); post('/driver/trip', { preserveScroll:true }) }
    const publishNow = (e) => { e.preventDefault(); post('/driver/trip/store-and-publish', { preserveScroll:true }) }

    return (
        <section className="rounded-3xl border border-black/10 bg-white p-5">
            <div className="flex items-center justify-between mb-3">
                <div className="text-xl font-bold text-black">Ավելացնել ուղևորություն</div>
                {!vehicle && <div className="text-sm text-rose-600">Սկզբում լրացրեք «Իմ մեքենան» բաժինը</div>}
            </div>

            <div className="grid lg:grid-cols-2 gap-4">
                {/* Форма */}
                <form className="space-y-3 order-2 lg:order-1">
                    <div className="grid grid-cols-2 gap-3">
                        <Input label="Սկիզբ (հասցե)" value={data.from_addr} onChange={v=>setFrom(p=>({ ...p, addr:v }))} error={errors.from_addr}/>
                        <Input label="Վերջ (հասցե)"  value={data.to_addr}   onChange={v=>setTo(p=>({ ...p, addr:v }))}   error={errors.to_addr}/>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <Input type="datetime-local" label="Ելքի ժամանակ" value={data.departure_at} onChange={v=>setData('departure_at', v)} error={errors.departure_at}/>
                        <Input type="number" label="Մեկ նստատեղի գին (AMD)" value={data.price_amd} onChange={v=>setData('price_amd', v)} error={errors.price_amd}/>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <Input type="number" label="Տեղերի քանակ" value={data.seats_total} onChange={v=>setData('seats_total', v)} error={errors.seats_total}/>
                        <PayMethods value={data.pay_methods} onChange={arr=>setData('pay_methods', arr)} />
                    </div>

                    <div className="flex flex-wrap gap-2">
                        <button disabled={!canSubmit || processing} onClick={saveDraft}
                                className="px-4 py-2 rounded-xl bg-black text-[#ffdd2c] font-semibold hover:brightness-95 disabled:opacity-50">
                            Պահպանել (սևագիր)
                        </button>
                        {/* ВСЕГДА ВИДИМАЯ кнопка ПУБЛИКАЦИИ */}
                        <button disabled={!canSubmit || processing} onClick={publishNow}
                                className="px-4 py-2 rounded-xl bg-[#ffdd2c] text-black font-semibold border border-black/10 hover:brightness-95 disabled:opacity-50">
                            Հրապարակել հիմա
                        </button>
                    </div>

                    <div className="text-xs text-black/60">Սևագրից հետո կարող եք «Հրապարակել» նաև քարտի մեջ:</div>
                </form>

                {/* Карта */}
                <div className="order-1 lg:order-2 space-y-2">
                    <div className="text-sm text-black/70">Քարտեզի վրա սեղմեք՝ <b>{which==='from'?'Սկիզբ':'Վերջ'}</b> կետի ընտրության համար.</div>
                    <div className="h-80 overflow-hidden rounded-2xl border border-black/10">
                        <Map from={from} to={to} setFrom={setFrom} setTo={setTo} which={which} setWhich={setWhich} />
                    </div>
                </div>
            </div>
        </section>
    )
}

function PayMethods({ value = [], onChange }) {
    const toggle = (k)=> onChange(value.includes(k) ? value.filter(i=>i!==k) : [...value, k])
    return (
        <div className="rounded-xl border border-black/10 p-3">
            <div className="text-sm font-medium mb-2 text-black">Վճարման եղանակ</div>
            <div className="flex gap-3 text-sm">
                <label className="inline-flex items-center gap-2"><input type="checkbox" checked={value.includes('cash')} onChange={()=>toggle('cash')} /> Կանխիկ</label>
                <label className="inline-flex items-center gap-2"><input type="checkbox" checked={value.includes('card')} onChange={()=>toggle('card')} /> Քարտ</label>
            </div>
        </div>
    )
}

function Map({ from, to, setFrom, setTo, which, setWhich }){
    const center = [(from.lat + to.lat)/2, (from.lng + to.lng)/2]
    function Clicker(){
        useMapEvents({ click(e){ const {lat,lng}=e.latlng; if (which==='from') setFrom({...from,lat,lng}); else setTo({...to,lat,lng}); setWhich(which==='from'?'to':'from') } })
        return null
    }
    return (
        <MapContainer center={center} zoom={8} style={{height:'100%'}}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            <Marker position={[from.lat, from.lng]} />
            <Marker position={[to.lat, to.lng]} />
            <Polyline positions={[[from.lat,from.lng],[to.lat,to.lng]]} />
            <Clicker/>
        </MapContainer>
    )
}

// ———————————— ՑԱՆԿԵՐ ————————————
function TripsList({ trips }){
    return (
        <section className="rounded-3xl border border-black/10 bg-white p-5">
            <div className="flex items-center justify-between mb-3">
                <div className="text-xl font-bold text-black">Իմ ուղևորությունները</div>
            </div>
            <div className="grid md:grid-cols-2 gap-3">
                {trips.map(t => <TripItem key={t.id} t={t} />)}
                {trips.length===0 && <div className="text-black/60">Դեռ չկան</div>}
            </div>
        </section>
    )
}
function TripItem({ t }){
    const seatsLeft = t.seats_total - t.seats_taken
    const canPublish = t.status==='draft' && seatsLeft>0
    return (
        <div className="rounded-2xl border border-black/10 p-4">
            <div className="text-sm text-black/60">{t.from_addr} → {t.to_addr}</div>
            <div className="font-semibold text-black">{dayjs(t.departure_at).format('YYYY-MM-DD HH:mm')}</div>
            <div className="text-sm text-black/80">Գին․ {t.price_amd} AMD · Տեղեր՝ {t.seats_taken}/{t.seats_total}</div>
            <div className="mt-2 flex items-center gap-2">
                <span className={`text-xs px-2 py-1 rounded ${badge(t.status)}`}>{statusLabel(t.status)}</span>
                <span className="text-xs text-black/60">սպասվող հայտեր՝ {t.pending_requests_count ?? 0}</span>
            </div>
            <div className="mt-3 flex gap-2">
                {canPublish && (
                    <button onClick={()=>router.post(`/driver/trip/${t.id}/publish`)}
                            className="px-3 py-1.5 rounded bg-[#ffdd2c] text-black border border-black/10">Հրապարակել</button>
                )}
                {t.status!=='archived' && (
                    <button onClick={()=>router.post(`/driver/trip/${t.id}/archive`)}
                            className="px-3 py-1.5 rounded bg-black text-[#ffdd2c]">Արխիվացնել</button>
                )}
                <button onClick={()=>router.post(`/driver/trip/${t.id}/fake-request`)}
                        className="px-3 py-1.5 rounded bg-amber-500 text-white">Թեստային հայտ</button>
            </div>
        </div>
    )
}
const badge = (s)=> s==='published'? 'bg-emerald-100 text-emerald-700'
    : s==='draft' ? 'bg-amber-100 text-amber-700'
        : s==='archived' ? 'bg-slate-100 text-slate-700'
            : 'bg-rose-100 text-rose-700'
const statusLabel = (s)=> ({draft:'Սևագիր', published:'Հրապարակված', archived:'Արխիվ', cancelled:'Չեղարկված'})[s] || s

function RequestsList({ requests }){
    const groups = useMemo(()=>{
        const m = {}; requests.forEach(r=>{ const k = r.trip?.id + '|' + (r.trip?.from_addr||''); (m[k]??=[]).push(r) })
        return Object.entries(m)
    }, [requests])
    return (
        <section className="rounded-3xl border border-black/10 bg-white p-5">
            <div className="text-xl font-bold text-black mb-3">Սպասվող հայտեր</div>
            {groups.length===0 && <div className="text-black/60">Չկան</div>}
            <div className="space-y-4">
                {groups.map(([k,list])=>{
                    const trip = list[0].trip
                    return (
                        <div key={k} className="rounded-2xl border border-black/10 p-4">
                            <div className="text-sm text-black/60">{trip.from_addr} → {trip.to_addr} · {dayjs(trip.departure_at).format('MM-DD HH:mm')}</div>
                            <div className="divide-y">
                                {list.map(r=> <RequestRow key={r.id} r={r} />)}
                            </div>
                        </div>
                    )
                })}
            </div>
        </section>
    )
}
function RequestRow({ r }){
    const yellow = r.status==='pending'
    return (
        <div className={`py-2 flex items-center justify-between ${yellow?'bg-yellow-50':''}`}>
            <div>
                <div className="font-medium text-black">{r.passenger_name} · {r.phone}</div>
                <div className="text-sm text-black/70">Տեղեր՝ {r.seats} · Վճարում՝ {r.payment==='cash'?'Կանխիկ':'Քարտ'} · Կարգավիճակ՝ {statusReq(r.status)}</div>
            </div>
            <div className="flex gap-2">
                {r.status==='pending' && (
                    <>
                        <button onClick={()=>router.post(`/driver/request/${r.id}/accept`)} className="px-3 py-1.5 rounded bg-emerald-600 text-white">Ընդունել</button>
                        <button onClick={()=>router.post(`/driver/request/${r.id}/reject`)} className="px-3 py-1.5 rounded bg-rose-600 text-white">Մերժել</button>
                    </>
                )}
                {r.status!=='pending' && <span className={`text-xs px-2 py-1 rounded ${r.status==='accepted'?'bg-emerald-100 text-emerald-700':'bg-rose-100 text-rose-700'}`}>{statusReq(r.status)}</span>}
            </div>
        </div>
    )
}
const statusReq = (s)=> ({pending:'Սպասում է', accepted:'Ընդունված', rejected:'Մերժված', cancelled:'Չեղարկված'})[s] || s

// UI helpers
function Input({label,error,...p}){ return (
    <label className="block text-sm">
        <div className="mb-1 text-black/80">{label}</div>
        <input {...p} onChange={e=>p.onChange(e.target.value)} className="w-full rounded-xl border border-black/10 px-3 py-2"/>
        {error && <div className="text-xs text-rose-600 mt-1">{error}</div>}
    </label>
)}
function File({label,onChange}){ return (
    <label className="block text-sm">
        <div className="mb-1 text-black/80">{label}</div>
        <input type="file" accept="image/*" onChange={e=>onChange(e.target.files[0])} className="w-full"/>
    </label>
)}
