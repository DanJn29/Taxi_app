import React from "react";
import { Link, usePage } from "@inertiajs/react";

export default function CompanyLayout({ company, current, children }) {
    const tabs = [
        { key: "dashboard", label: "Սկիզբ", href: route('company.show', company.id) },
        { key: "fleet", label: "Ֆլոտ", href: route('company.fleet.index', company.id) },
        { key: "drivers", label: "Վարորդներ", href: route('company.members.index', company.id) },
        { key: "trips", label: "Երթուղիներ", href: route('company.trips.index', company.id) },
        { key: "requests", label: "Հայտեր", href: route('company.requests.index', company.id) },
    ];

    return (
        <div className="min-h-screen bg-[#fffaf0]">
            <header className="sticky top-0 z-40 bg-[#ffdd2c] border-b border-black/10">
                <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
                    <div className="font-bold">Taxi Platform · {company.name}</div>
                    <nav className="flex gap-2">
                        {tabs.map(t => (
                            <Link key={t.key} href={t.href}
                                  className={`px-3 py-1.5 rounded-lg text-sm ${current===t.key?'bg-black text-[#ffdd2c]':'hover:bg-black/10'}`}>
                                {t.label}
                            </Link>
                        ))}
                    </nav>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 py-6">{children}</main>

            <footer className="text-xs text-black/60 text-center py-6">© 2025 Taxi Platform</footer>
        </div>
    );
}
