'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { StatCard } from '@/components/common/StatCard';
import { StatusBadge } from '@/components/common/StatusBadge';
import { RiskTrendChart } from '@/components/charts/RiskTrendChart';

export default function DashboardPage() {
  const [mineSearch, setMineSearch] = useState('');

  const minesData = [
    { name: 'Mine A', region: 'Korba', risk: 91, status: 'HIGH', violations: 8, actions: 5, lastInsp: '04 Sep 2026', link: '/mines/mine-a' },
    { name: 'Mine D', region: 'Kothagudem', risk: 84, status: 'HIGH', violations: 7, actions: 2, lastInsp: '02 Sep 2026', link: '/mines/mine-d' },
    { name: 'Mine C', region: 'Sambalpur', risk: 64, status: 'MEDIUM', violations: 5, actions: 3, lastInsp: '03 Sep 2026', link: '/mines/mine-c' },
    { name: 'Mine B', region: 'Dhanbad', risk: 23, status: 'GOOD', violations: 1, actions: 1, lastInsp: '04 Sep 2026', link: '/mines/mine-b' },
    { name: 'Mine E', region: 'Ranchi', risk: 18, status: 'GOOD', violations: 0, actions: 0, lastInsp: '01 Sep 2026', link: '/mines/mine-e' },
  ];

  const filteredMines = minesData.filter(m =>
    m.name.toLowerCase().includes(mineSearch.toLowerCase()) ||
    m.region.toLowerCase().includes(mineSearch.toLowerCase())
  );

  const alertsData = [
    { id: '1', badge: 'CRITICAL', title: 'Methane threshold exceeded', subtitle: 'Mine A · 10:34 AM', unread: true },
    { id: '2', badge: 'CRITICAL', title: 'Roof support violation', subtitle: 'Mine A · 10:31 AM', unread: true },
    { id: '3', badge: 'HIGH', title: 'Corrective action overdue', subtitle: 'Mine C · 09:00 AM', unread: true },
    { id: '4', badge: 'REVIEW', title: 'Inspection unusually short', subtitle: 'Mine B · Yesterday', unread: false },
    { id: '5', badge: 'MEDIUM', title: 'Ventilation maintenance due', subtitle: 'Mine C · Yesterday', unread: false },
  ];

  const riskTrendData = [
    { week: 'W1', line1: 72, line2: 68, line3: 54 },
    { week: 'W2', line1: 75, line2: 70, line3: 56 },
    { week: 'W3', line1: 78, line2: 72, line3: 59 },
    { week: 'W4', line1: 82, line2: 75, line3: 63 },
    { week: 'W5', line1: 88, line2: 79, line3: 66 },
    { week: 'W6', line1: 91, line2: 84, line3: 64 },
  ];

  return (
    <div className="space-y-6">
      {/* Page Title & Subtitle */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Dashboard</h1>
        <p className="text-xs text-slate-500 mt-0.5">Overview of mine compliance and current risks.</p>
      </div>

      {/* Top 6 Stat Cards Row */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
        <StatCard
          title="TOTAL MINES"
          value={18}
          subtitle="across 4 districts"
          icon="mountain"
          topBorderColor="gray"
        />
        <StatCard
          title="HIGH RISK"
          value={4}
          subtitle="require attention"
          icon="warning"
          topBorderColor="red"
        />
        <StatCard
          title="OPEN VIOLATIONS"
          value={37}
          subtitle="6 critical"
          icon="warning"
          topBorderColor="red"
        />
        <StatCard
          title="PENDING ACTIONS"
          value={12}
          subtitle="2 overdue"
          icon="build"
          topBorderColor="amber"
        />
        <StatCard
          title="INSPECTIONS DUE"
          value={6}
          subtitle="this week"
          icon="assignment"
          topBorderColor="blue"
        />
        <StatCard
          title="ALERTS"
          value={5}
          subtitle="3 unread"
          icon="notifications"
          topBorderColor="red"
        />
      </div>

      {/* Main Two-Column Layout (Left 2/3, Right 1/3) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Left Column (2 Columns wide on desktop) */}
        <div className="lg:col-span-2 space-y-6">
          
          {/* Card 1: Mine Risk Overview Table */}
          <div className="bg-white rounded-lg border border-slate-200/80 shadow-xs p-5 space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Mine Risk Overview
              </h3>

              <div className="relative w-52">
                <span className="material-symbols-outlined absolute left-2.5 top-2 text-slate-400 text-sm">
                  search
                </span>
                <input
                  type="text"
                  value={mineSearch}
                  onChange={(e) => setMineSearch(e.target.value)}
                  placeholder="Search mine..."
                  className="w-full h-8 pl-8 pr-3 bg-slate-50 rounded-md border border-slate-200 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-slate-300"
                />
              </div>
            </div>

            <div className="overflow-x-auto custom-scrollbar">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-slate-100 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                    <th className="pb-3 font-semibold">MINE</th>
                    <th className="pb-3 font-semibold flex items-center gap-0.5">RISK <span className="text-[10px]">v</span></th>
                    <th className="pb-3 font-semibold">STATUS</th>
                    <th className="pb-3 font-semibold">VIOLATIONS</th>
                    <th className="pb-3 font-semibold">ACTIONS</th>
                    <th className="pb-3 font-semibold">LAST INSP.</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 text-xs text-slate-700">
                  {filteredMines.map((m, idx) => {
                    let riskColor = 'text-emerald-600 font-bold';
                    if (m.risk >= 80) riskColor = 'text-red-600 font-bold';
                    else if (m.risk >= 50) riskColor = 'text-amber-600 font-bold';

                    return (
                      <tr key={idx} className="hover:bg-slate-50/80 transition-colors">
                        <td className="py-3 font-bold text-slate-900">
                          <Link href={m.link} className="hover:underline">
                            {m.name}
                          </Link>
                          <div className="text-[11px] text-slate-400 font-normal">{m.region}</div>
                        </td>
                        <td className={`py-3 ${riskColor}`}>{m.risk}</td>
                        <td className="py-3">
                          <StatusBadge status={m.status} />
                        </td>
                        <td className="py-3 text-slate-600 font-medium">{m.violations}</td>
                        <td className="py-3 text-slate-600 font-medium">{m.actions}</td>
                        <td className="py-3 text-slate-500 font-medium">{m.lastInsp}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          {/* Card 2: Risk Trend Chart */}
          <div className="bg-white rounded-lg border border-slate-200/80 shadow-xs p-5 space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Risk Trend
              </h3>
              <span className="text-[11px] text-slate-400 font-medium">Last 6 weeks</span>
            </div>

            <RiskTrendChart data={riskTrendData} />
          </div>

        </div>

        {/* Right Column (1 Column wide) */}
        <div className="space-y-6">

          {/* Card 1: Recent Alerts */}
          <div className="bg-white rounded-lg border border-slate-200/80 shadow-xs p-5 space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Recent Alerts
              </h3>
              <Link href="/alerts" className="text-xs font-semibold text-blue-600 hover:underline">
                View all
              </Link>
            </div>

            <div className="space-y-3.5">
              {alertsData.map((alert) => (
                <div key={alert.id} className="flex items-center justify-between text-xs py-0.5">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <StatusBadge status={alert.badge} />
                      <span className="font-bold text-slate-900">{alert.title}</span>
                    </div>
                    <div className="text-[11px] text-slate-400 pl-1">{alert.subtitle}</div>
                  </div>
                  {alert.unread && (
                    <span className="w-2 h-2 rounded-full bg-blue-600 shrink-0"></span>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Card 2: Quick Actions */}
          <div className="bg-white rounded-lg border border-slate-200/80 shadow-xs p-5 space-y-3">
            <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
              Quick Actions
            </h3>

            <div className="space-y-2">
              <Link
                href="/mines"
                className="flex items-center justify-between p-3 rounded-lg border border-slate-200/80 hover:bg-slate-50 transition-colors text-xs font-semibold text-slate-800 group"
              >
                <div className="flex items-center gap-2.5">
                  <span className="material-symbols-outlined text-slate-500 group-hover:text-slate-900">mountain</span>
                  <span>View Mines</span>
                </div>
                <span className="text-slate-400 group-hover:text-slate-900 text-sm font-mono">↗</span>
              </Link>

              <Link
                href="/alerts"
                className="flex items-center justify-between p-3 rounded-lg border border-slate-200/80 hover:bg-slate-50 transition-colors text-xs font-semibold text-slate-800 group"
              >
                <div className="flex items-center gap-2.5">
                  <span className="material-symbols-outlined text-slate-500 group-hover:text-slate-900">notifications_none</span>
                  <span>Review Critical Alerts</span>
                </div>
                <span className="text-slate-400 group-hover:text-slate-900 text-sm font-mono">↗</span>
              </Link>

              <Link
                href="/inspections"
                className="flex items-center justify-between p-3 rounded-lg border border-slate-200/80 hover:bg-slate-50 transition-colors text-xs font-semibold text-slate-800 group"
              >
                <div className="flex items-center gap-2.5">
                  <span className="material-symbols-outlined text-slate-500 group-hover:text-slate-900">assignment</span>
                  <span>Review Due Inspections</span>
                </div>
                <span className="text-slate-400 group-hover:text-slate-900 text-sm font-mono">↗</span>
              </Link>

              <Link
                href="/corrective-actions"
                className="flex items-center justify-between p-3 rounded-lg border border-slate-200/80 hover:bg-slate-50 transition-colors text-xs font-semibold text-slate-800 group"
              >
                <div className="flex items-center gap-2.5">
                  <span className="material-symbols-outlined text-slate-500 group-hover:text-slate-900">build</span>
                  <span>Open Action Queue</span>
                </div>
                <span className="text-slate-400 group-hover:text-slate-900 text-sm font-mono">↗</span>
              </Link>
            </div>
          </div>

        </div>

      </div>
    </div>
  );
}
