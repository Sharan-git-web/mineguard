'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { mockAlerts } from '@/lib/mock/data';

export const Header: React.FC = () => {
  const pathname = usePathname();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const pathSegments = pathname.split('/').filter(Boolean);
  const currentPageName = pathSegments.length > 0 ? pathSegments[0].charAt(0).toUpperCase() + pathSegments[0].slice(1) : 'Dashboard';

  const unreadAlerts = mockAlerts.filter(a => !a.isRead);

  return (
    <header className="fixed top-0 right-0 left-56 h-14 bg-white border-b border-slate-200 flex items-center justify-between px-6 z-20 select-none">
      {/* Breadcrumb Title */}
      <div className="flex items-center gap-2 text-xs">
        <span className="font-bold text-slate-900">{currentPageName}</span>
        <span className="text-slate-400">/</span>
        <span className="text-slate-400 font-medium">Officer Portal</span>
      </div>

      {/* Global Search Bar */}
      <div className="relative w-96">
        <span className="material-symbols-outlined absolute left-3 top-2.5 text-slate-400 text-sm">
          search
        </span>
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search mines, inspections, violations..."
          className="w-full h-9 pl-9 pr-3 bg-slate-50 rounded-lg border border-slate-200 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-slate-400 focus:bg-white transition-colors"
        />
      </div>

      {/* Right Header Status & Profile */}
      <div className="flex items-center gap-4">
        {/* Notification Bell */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="relative w-8 h-8 rounded-full hover:bg-slate-100 flex items-center justify-center text-slate-600 transition-colors"
          >
            <span className="material-symbols-outlined text-lg">notifications</span>
            {unreadAlerts.length > 0 && (
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border border-white"></span>
            )}
          </button>

          {/* Drawer */}
          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl border border-slate-200 p-3 space-y-2 z-50">
              <div className="flex items-center justify-between border-b border-slate-100 pb-2">
                <h4 className="text-xs font-bold text-slate-900 flex items-center gap-1.5">
                  <span className="material-symbols-outlined text-red-500 text-sm">warning</span>
                  Recent Alerts ({unreadAlerts.length})
                </h4>
                <Link
                  href="/alerts"
                  onClick={() => setShowNotifications(false)}
                  className="text-[11px] font-semibold text-blue-600 hover:underline"
                >
                  View all
                </Link>
              </div>

              <div className="space-y-2 max-h-64 overflow-y-auto custom-scrollbar">
                {mockAlerts.map((alert) => (
                  <div
                    key={alert.id}
                    className="p-2.5 rounded-md bg-slate-50 border border-slate-100 text-xs space-y-1 hover:bg-slate-100 cursor-pointer"
                  >
                    <div className="font-bold text-slate-900 flex items-center justify-between">
                      <span className="truncate">{alert.title}</span>
                      <span className="text-[10px] text-slate-400">{alert.timestamp}</span>
                    </div>
                    <p className="text-slate-500 text-[11px] line-clamp-2">{alert.description}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Officer Profile Dropdown Pill */}
        <div className="relative">
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="flex items-center gap-2 p-1.5 rounded-lg hover:bg-slate-100 transition-colors"
          >
            <div className="w-7 h-7 rounded-full bg-slate-800 text-white flex items-center justify-center text-xs font-bold">
              <span className="material-symbols-outlined text-sm">shield</span>
            </div>
            <div className="text-left hidden md:block">
              <div className="text-xs font-bold text-slate-900 leading-tight">Officer</div>
              <div className="text-[10px] text-slate-400 font-medium">Regional Officer</div>
            </div>
            <span className="material-symbols-outlined text-slate-400 text-sm">expand_more</span>
          </button>

          {showUserMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl border border-slate-200 py-1 z-50 text-xs">
              <div className="px-3 py-2 border-b border-slate-100">
                <div className="font-bold text-slate-900">Dr. Alok Verma</div>
                <div className="text-[10px] text-slate-400">DGMS Officer</div>
              </div>
              <Link href="/users" onClick={() => setShowUserMenu(false)} className="block px-3 py-2 text-slate-700 hover:bg-slate-50">Profile & Roles</Link>
              <Link href="/settings" onClick={() => setShowUserMenu(false)} className="block px-3 py-2 text-slate-700 hover:bg-slate-50">Settings</Link>
              <Link href="/login" onClick={() => setShowUserMenu(false)} className="block px-3 py-2 text-red-600 hover:bg-red-50 font-semibold border-t border-slate-100">Logout</Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
