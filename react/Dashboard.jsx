import React from "react";
import CompanyLayout from "./Layout";

export default function Dashboard({ company }) {
    return (
        <CompanyLayout company={company} current="dashboard">
            <h1 className="text-2xl font-bold mb-4">Ընկերության վահանակ</h1>
            <div className="grid md:grid-cols-4 gap-4">
                <Card title="Մեքենաներ" value={company.vehicles_count}/>
                <Card title="Երթուղիներ" value={company.trips_count}/>
                <Card title="Սպասվող հայտեր" value={company.pending_requests}/>
                <Card title="Սեփականատեր" value={company.owner?.name}/>
            </div>
        </CompanyLayout>
    );
}

function Card({title, value}) {
    return (
        <div className="rounded-2xl border bg-white p-4">
            <div className="text-sm text-black/60">{title}</div>
            <div className="text-2xl font-bold mt-1">{value ?? '—'}</div>
        </div>
    );
}
