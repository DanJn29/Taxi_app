import React, { useState } from "react";
import { router } from "@inertiajs/react";
import CompanyLayout from "./Layout";

export default function Members({ company, members }) {
    const [form, setForm] = useState({ name:"", email:"", password:"", role:"driver" });

    function submit(e){
        e.preventDefault();
        router.post(route('company.members.store', company.id), form);
    }

    return (
        <CompanyLayout company={company} current="drivers">
            <h1 className="text-2xl font-bold mb-4">Վարորդներ / աշխատակիցներ</h1>

            <div className="grid md:grid-cols-2 gap-6">
                <form onSubmit={submit} className="rounded-2xl border bg-white p-4 space-y-3">
                    <h2 className="font-semibold mb-1">Ավելացնել աշխատակից</h2>
                    <Input label="Անուն Ազգանուն" value={form.name} onChange={v=>setForm(s=>({...s,name:v}))}/>
                    <Input label="Էլ․ փոստ" value={form.email} onChange={v=>setForm(s=>({...s,email:v}))}/>
                    <Input type="password" label="Գաղտնաբառ" value={form.password} onChange={v=>setForm(s=>({...s,password:v}))}/>
                    <label className="block text-sm">
                        <div className="mb-1 text-black/70">Դեր</div>
                        <select value={form.role} onChange={e=>setForm(s=>({...s,role:e.target.value}))}
                                className="w-full rounded-xl border px-3 py-2">
                            <option value="driver">Վարորդ</option>
                            <option value="dispatcher">Դիսպետչեր</option>
                        </select>
                    </label>
                    <button className="rounded-xl px-4 py-2 bg-black text-[#ffdd2c]">Պահպանել</button>
                    <div className="text-xs text-black/60">Վարորդը կստանա նամակ մուտք գործելու համար (email verify).</div>
                </form>

                <div>
                    <h2 className="font-semibold mb-2">Աշխատակիցների ցուցակ</h2>
                    <div className="space-y-2">
                        {members.map(m=>(
                            <div key={m.id} className="rounded-xl border bg-white p-3 flex items-center justify-between">
                                <div>
                                    <div className="font-medium">{m.name}</div>
                                    <div className="text-sm text-black/60">{m.email} · դեր՝ {m.role}</div>
                                </div>
                            </div>
                        ))}
                        {members.length===0 && <div className="rounded-xl border bg-white p-6 text-center text-black/60">Դատարկ է</div>}
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
