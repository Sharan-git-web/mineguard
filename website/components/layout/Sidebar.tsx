'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

export const Sidebar: React.FC = () => {
  const pathname = usePathname();
  const [showShutdownModal, setShowShutdownModal] = useState(false);

  const navGroups = [
    {
      title: 'OVERVIEW',
      items: [
        { name: 'Dashboard', href: '/dashboard', icon: 'grid_view' },
      ],
    },
    {
      title: 'MONITORING',
      items: [
        { name: 'Mines', href: '/mines', icon: 'mountain' },
        { name: 'Inspections', href: '/inspections', icon: 'assignment' },
        { name: 'Violations', href: '/violations', icon: 'warning_amber' },
        { name: 'Corrective Actions', href: '/corrective-actions', icon: 'build' },
      ],
    },
    {
      title: 'INTELLIGENCE',
      items: [
        { name: 'GIS Map', href: '/gis', icon: 'map' },
        { name: 'AI Insights', href: '/ai-insights', icon: 'psychology' },
        { name: 'Alerts', href: '/alerts', icon: 'notifications_none' },
      ],
    },
    {
      title: 'RECORDS',
      items: [
        { name: 'Documents', href: '/evidence-vault', icon: 'description' },
        { name: 'Reports', href: '/reports', icon: 'bar_chart' },
        { name: 'Audit Trail', href: '/audit-trail', icon: 'history' },
      ],
    },
    {
      title: 'ADMINISTRATION',
      items: [
        { name: 'Users & Roles', href: '/users', icon: 'group' },
        { name: 'Settings', href: '/settings', icon: 'settings' },
      ],
    },
  ];

  return (
    <>
      <aside className="fixed top-0 left-0 h-screen w-56 bg-white border-r border-slate-200 flex flex-col z-30 shrink-0 select-none">
        {/* Header Logo */}
        <div className="p-4 border-b border-slate-100 flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-slate-900 text-white flex items-center justify-center shrink-0 shadow-xs">
            <span className="material-symbols-outlined text-xl">shield</span>
          </div>
          <div>
            <h1 className="font-extrabold text-xs tracking-tight text-slate-900 leading-tight uppercase">
              SMART MINE<br />GOVERNANCE
            </h1>
          </div>
        </div>

        {/* Navigation List */}
        <nav className="flex-1 overflow-y-auto px-3 py-3 space-y-4 custom-scrollbar">
          {navGroups.map((group, idx) => (
            <div key={idx} className="space-y-1">
              <div className="px-2 pb-1 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                {group.title}
              </div>
              {group.items.map((item) => {
                const isActive = pathname === item.href || (item.href !== '/dashboard' && pathname.startsWith(item.href));
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={`flex items-center gap-2.5 px-2.5 py-1.5 rounded-md text-xs transition-colors ${
                      isActive
                        ? 'bg-slate-100 text-slate-900 font-bold'
                        : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50 font-medium'
                    }`}
                  >
                    <span className={`material-symbols-outlined text-base ${isActive ? 'text-slate-900' : 'text-slate-500'}`}>
                      {item.icon}
                    </span>
                    <span>{item.name}</span>
                  </Link>
                );
              })}
            </div>
          ))}
        </nav>

        {/* Emergency Statutory Shutdown Button */}
        <div className="p-3 border-t border-slate-100 bg-slate-50">
          <button
            onClick={() => setShowShutdownModal(true)}
            className="w-full py-1.5 bg-red-600 hover:bg-red-700 text-white text-xs font-bold rounded flex items-center justify-center gap-1.5 transition-colors shadow-xs"
          >
            <span className="material-symbols-outlined text-sm">warning</span>
            <span>SHUTDOWN DIRECTIVE</span>
          </button>
        </div>
      </aside>

      {/* Emergency Modal */}
      {showShutdownModal && (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg shadow-2xl max-w-md w-full border border-red-200 p-6 space-y-4">
            <div className="flex items-center gap-3 text-red-600">
              <div className="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center">
                <span className="material-symbols-outlined text-red-600 text-xl">warning</span>
              </div>
              <div>
                <h3 className="font-bold text-lg text-slate-900">Emergency Statutory Shutdown</h3>
                <p className="text-xs text-slate-500">DGMS Section 22(1) Emergency Directive</p>
              </div>
            </div>
            <p className="text-xs text-slate-600">
              Issuing a statutory shutdown notice will halt operations and trigger mandatory evacuation alerts.
            </p>
            <div className="flex items-center justify-end gap-2 pt-2">
              <button
                onClick={() => setShowShutdownModal(false)}
                className="px-3 py-1.5 rounded text-xs font-medium text-slate-600 hover:bg-slate-100"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  alert('Statutory Shutdown Directive Issued.');
                  setShowShutdownModal(false);
                }}
                className="px-3 py-1.5 rounded text-xs font-bold bg-red-600 text-white hover:bg-red-700"
              >
                Confirm Shutdown
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
