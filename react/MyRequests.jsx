import React from "react";
import { Link, usePage } from "@inertiajs/react";
import dayjs from "dayjs";
import ClientLayout from "@/Layouts/ClientLayout";

export default function MyRequests(){
    const { items } = usePage().props;
    return (
        <ClientLayout current="requests">
            <div className="space-y-6">
                <h1 className="text-3xl font-extrabold">Իմ հայտերը</h1>
                <div className="space-y-3">
                    {items?.data?.length===0 && <div className="rounded-2xl border border-black/10 bg-white p-6 text-black/70">Դուք դեռ հայտ չեք ուղարկել։</div>}
                    {items?.data?.map(r => <ReqRow key={r.id} r={r}/>)}
                </div>
                <div className="flex gap-2 justify-center">
                    {items?.links?.map((l,i)=>(
                        <Link key={i} href={l.url || ''} preserveScroll
                              className={`px-3 py-1.5 rounded-lg text-sm ${l.active?'bg-black text-[#ffdd2c]':'bg-white border border-black/10 hover:bg-black/5'} ${!l.url?'pointer-events-none opacity-40':''}`}
                              dangerouslySetInnerHTML={{__html:l.label}}/>
                    ))}
                </div>
            </div>
        </ClientLayout>
    )
}

function ReqRow({ r }){
    const badge = r.status==='accepted' ? 'bg-emerald-100 text-emerald-700'
        : r.status==='pending' ? 'bg-amber-100 text-amber-700'
            : r.status==='rejected' ? 'bg-rose-100 text-rose-700'
                : 'bg-slate-100 text-slate-700';
    const st = { pending:'Սպասում է', accepted:'Ընդունված', rejected:'Մերժված', cancelled:'Չեղարկված' }[r.status] || r.status

    return (
        <div className="rounded-2xl border border-black/10 bg-white p-4 flex items-center justify-between">
            <div>
                <div className="text-sm text-black/60">{r.trip?.driver || ''}</div>
                <div className="font-semibold">{r.trip?.from_addr} → {r.trip?.to_addr}</div>
                <div className="text-sm text-black/80">{dayjs(r.trip?.departure_at).format('YYYY-MM-DD HH:mm')} · {r.seats} տեղ · {r.payment==='card'?'Քարտ':'Կանխիկ'}</div>
            </div>
            <span className={`text-xs px-2 py-1 rounded ${badge}`}>{st}</span>
        </div>
    )
}
