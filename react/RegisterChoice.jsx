import React from 'react';
import { Link } from '@inertiajs/react';


export default function RegisterChoice(){
    const Card = ({title,desc,to,color}) => (
        <Link href={to} className="block rounded-2xl border p-5 hover:shadow transition bg-white">
            <div className="text-sm text-slate-500">Регистрация</div>
            <div className="text-2xl font-bold" style={{color}}>{title}</div>
            <div className="text-slate-600 mt-2">{desc}</div>
        </Link>
    );
    return (
        <div className="max-w-4xl mx-auto py-12 px-4 grid md:grid-cols-3 gap-4">
            <Card title="Клиент" desc="Быстрый старт: бронирования, профиль" to="/register/client" color="#10b981" />
            <Card title="Таксист" desc="Загрузка фото, ожидание одобрения" to="/register/driver" color="#6366f1" />
            <Card title="Таксопарк" desc="Регистрация компании, сотрудники" to="/register/company" color="#f59e0b" />
        </div>
    );
}
