'use client';

import React from 'react';
import { usePathname } from 'next/navigation';
import { Sidebar } from '@/components/layout/Sidebar';
import { Header } from '@/components/layout/Header';

export function MainLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const isLoginPage = pathname === '/login';

  if (isLoginPage) {
    return <main className="min-h-screen bg-slate-50">{children}</main>;
  }

  return (
    <div className="min-h-screen flex bg-slate-50">
      <Sidebar />
      <div className="flex-1 ml-56 flex flex-col min-w-0">
        <Header />
        <main className="mt-14 p-6 flex-1 bg-slate-50">
          {children}
        </main>
      </div>
    </div>
  );
}
