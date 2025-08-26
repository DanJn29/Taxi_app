import React, { useState } from "react";
import { router } from "@inertiajs/react";
import CompanyLayout from "./Layout";

export default function Fleet({ company, vehicles }) {
    const [form, setForm] = useState({ brand:"", model:"", plate:"", color:"#0ea5e9", seats:4, user_id:"" });

    function submit(e){
        e.preventDefault();
        router.post(route('company.fleet.store', company.id), form);
    }

    return (
        <CompanyLayout company={company} current="fleet">
            <h1 className="text-2xl font-bold mb-4">Ֆլոտ</h1>
            <div className="grid md:grid-cols-2 gap-6">
                <form onSubmit={submit} className="rounded-2xl border bg-white p-4 space-y-3">
                    <h2 className="font-semibold">Ավելացնել մեքենա</h2>
                    <Input label="Մարկա" value={form.brand} onChange={v=>setForm(s=>({...s,brand:v}))}/>
                    <Input label="Մոդել" value={form.model} onChange={v=>setForm(s=>({...s,model:v}))}/>
                    <Input label="Պետ․ համար" value={form.plate} onChange={v=>setForm(s=>({...s,plate:v}))}/>
                    <Input label="Գույն (#hex)" value={form.color} onChange={v=>setForm(s=>({...s,color:v}))}/>
                    <Input type="number" label="Տեղերի թիվ" value={form.seats} onChange={v=>setForm(s=>({...s,seats:Number(v)||4}))}/>
                    <button className="rounded-xl px-4 py-2 bg-black text-[#ffdd2c]">Պահպանել</button>
                </form>

                <div>
                    <h2 className="font-semibold mb-2">Մեքենաների ցուցակ</h2>
                    <div className="grid gap-3">
                        {vehicles.map(v=>(
                            <div key={v.id} className="rounded-2xl border bg-white p-4">
                                <div className="font-semibold">{v.brand} {v.model} · {v.plate}</div>
                                <div className="text-sm text-black/60">Տեղեր՝ {v.seats}</div>
                            </div>
                        ))}
                        {vehicles.length===0 && <div className="rounded-xl border bg-white p-6 text-center text-black/60">Դատարկ է</div>}
                    </div>
                </div>
            </div>
        </CompanyLayout>
    );
}

function Input({label, value, onChange, type="text"}) {
    return (
        <label className="block text-sm">
            <div className="mb-1 text-black/70">{label}</div>
            <input type={type} value={value} onChange={e=>onChange(e.target.value)}
                   className="w-full rounded-xl border px-3 py-2"/>
        </label>
    );
}
