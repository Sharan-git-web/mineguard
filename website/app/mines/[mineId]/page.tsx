'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { apiService } from '@/lib/api';
import { Mine, Inspection, Violation } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { StatCard } from '@/components/common/StatCard';
import { MapContainer } from '@/components/gis/MapContainer';
import { MineTelemetryChart } from '@/components/charts/MineTelemetryChart';

export default function MineDetailPage() {
  const params = useParams();
  const mineId = (params.mineId as string) || 'mine-a';

  const [mine, setMine] = useState<Mine | null>(null);
  const [inspections, setInspections] = useState<Inspection[]>([]);
  const [violations, setViolations] = useState<Violation[]>([]);
  const [activeTab, setActiveTab] = useState<'overview' | 'violations' | 'inspections'>('overview');

  useEffect(() => {
    apiService.getMineById(mineId).then((m) => m && setMine(m));
    apiService.getInspections().then(setInspections);
    apiService.getViolations().then(setViolations);
  }, [mineId]);

  if (!mine) return <div className="p-8 text-center text-slate-400">Loading mine telemetry profile...</div>;

  const chartData = [
    { time: '00:00', ch4: 450, strata: 85 },
    { time: '04:00', ch4: 480, strata: 84 },
    { time: '08:00', ch4: 890, strata: 76 },
    { time: '12:00', ch4: 1250, strata: 68 },
    { time: '16:00', ch4: 1180, strata: 70 },
    { time: '20:00', ch4: 950, strata: 72 },
  ];

  return (
    <div className="space-y-6">
      {/* Mine Profile Banner */}
      <div className="p-6 bg-white rounded-lg border border-slate-200/80 shadow-xs space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-slate-400">{mine.code}</span>
              <StatusBadge status={mine.riskLevel} />
              <StatusBadge status={mine.status} />
            </div>
            <h1 className="text-2xl font-bold text-slate-900">
              {mine.name}
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              {mine.region} • Assigned: <strong className="text-slate-800">{mine.inspectorAssigned}</strong>
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => alert('Issuing Section 22 Notice for ' + mine.name)}
              className="px-3.5 py-2 bg-red-600 hover:bg-red-700 text-white rounded text-xs font-bold flex items-center gap-1.5 shadow-xs"
            >
              <span className="material-symbols-outlined text-sm">gavel</span>
              <span>Issue Section 22 Order</span>
            </button>
            <Link
              href="/inspections"
              className="px-3.5 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-bold flex items-center gap-1.5 shadow-xs"
            >
              <span className="material-symbols-outlined text-sm">assignment</span>
              <span>Dispatch Inspector</span>
            </Link>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="flex items-center gap-6 border-b border-slate-100 pt-2 text-xs font-bold text-slate-500">
          <button
            onClick={() => setActiveTab('overview')}
            className={`pb-2 border-b-2 transition-colors ${
              activeTab === 'overview' ? 'border-slate-900 text-slate-900' : 'border-transparent hover:text-slate-900'
            }`}
          >
            Overview & Telemetry
          </button>
          <button
            onClick={() => setActiveTab('violations')}
            className={`pb-2 border-b-2 transition-colors ${
              activeTab === 'violations' ? 'border-slate-900 text-slate-900' : 'border-transparent hover:text-slate-900'
            }`}
          >
            Active Violations ({mine.activeViolationsCount})
          </button>
          <button
            onClick={() => setActiveTab('inspections')}
            className={`pb-2 border-b-2 transition-colors ${
              activeTab === 'inspections' ? 'border-slate-900 text-slate-900' : 'border-transparent hover:text-slate-900'
            }`}
          >
            Inspection History
          </button>
        </div>
      </div>

      {/* Main Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Methane Gas (CH4)"
          value={`${mine.methaneGasPpm} PPM`}
          subtitle="Limit: 1,000 PPM"
          icon="air"
          topBorderColor="red"
        />
        <StatCard
          title="Strata Stability Score"
          value={`${mine.strataStabilityScore}/100`}
          subtitle="Convergence Grid 44"
          icon="layers"
          topBorderColor="amber"
        />
        <StatCard
          title="Active Violations"
          value={mine.activeViolationsCount}
          subtitle="DGMS Enforcement"
          icon="gavel"
          topBorderColor="red"
        />
        <StatCard
          title="Pending CAPA Plans"
          value={mine.pendingCapasCount}
          subtitle="SLA 38h remaining"
          icon="build"
          topBorderColor="blue"
        />
      </div>

      {/* Overview & Telemetry Tab */}
      {activeTab === 'overview' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Strata Telemetry Chart */}
          <div className="lg:col-span-2 bg-white p-5 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
                <span className="material-symbols-outlined text-slate-600">show_chart</span>
                Real-Time Gas & Strata Telemetry Trend (24h)
              </h3>
              <span className="text-[11px] text-slate-400 font-mono">Sensor: #LC-44 / CH4-B44</span>
            </div>

            <MineTelemetryChart data={chartData} />
          </div>

          {/* GIS Coordinates & Seam Location */}
          <div className="bg-white p-5 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
            <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
              <span className="material-symbols-outlined text-slate-600">place</span>
              GIS Boundary & Coordinates
            </h3>
            <div className="space-y-2 text-xs text-slate-600">
              <div className="flex justify-between">
                <span>Latitude / Longitude:</span>
                <strong className="text-slate-900 font-mono">{mine.coordinates.lat}° N, {mine.coordinates.lng}° E</strong>
              </div>
              <div className="flex justify-between">
                <span>Daily Tonnage:</span>
                <strong className="text-slate-900">{mine.productionTonnage.toLocaleString()} MT</strong>
              </div>
              <div className="flex justify-between">
                <span>Last Audit:</span>
                <strong className="text-slate-900">{mine.lastInspectedAt}</strong>
              </div>
            </div>
            <MapContainer height="h-48" selectedMineId={mine.id} />
          </div>
        </div>
      )}

      {/* Violations Tab */}
      {activeTab === 'violations' && (
        <div className="bg-white p-5 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
          <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">Active Violations & Notices</h3>
          <div className="space-y-3">
            {violations.map((v) => (
              <div key={v.id} className="p-4 rounded-md bg-slate-50 border border-slate-200/80 flex items-center justify-between gap-4 text-xs">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-bold text-slate-900">{v.code}</span>
                    <StatusBadge status={v.severity} />
                  </div>
                  <h4 className="font-bold text-slate-900 mt-1 text-sm">{v.title}</h4>
                  <p className="text-slate-500 mt-0.5">{v.description}</p>
                </div>
                <Link
                  href={`/violations/${v.id}`}
                  className="px-3 py-1.5 bg-slate-900 text-white rounded text-xs font-bold hover:bg-slate-800 shrink-0"
                >
                  View Case File
                </Link>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Inspection History Tab */}
      {activeTab === 'inspections' && (
        <div className="bg-white p-5 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
          <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">Recent Inspections Dossiers</h3>
          <div className="space-y-3">
            {inspections.map((i) => (
              <div key={i.id} className="p-4 rounded-md bg-slate-50 border border-slate-200/80 flex items-center justify-between gap-4 text-xs">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-bold text-slate-900">{i.code}</span>
                    <StatusBadge status={i.status} />
                  </div>
                  <h4 className="font-bold text-slate-900 mt-1 text-sm">Inspector: {i.inspectorName} ({i.inspectorBadge})</h4>
                  <p className="text-slate-500 mt-0.5">Date: {i.date} • Methane: {i.methaneLevelPpm} PPM</p>
                </div>
                <Link
                  href={`/inspections/${i.id}`}
                  className="px-3 py-1.5 bg-slate-900 text-white rounded text-xs font-bold hover:bg-slate-800 shrink-0"
                >
                  Open Dossier
                </Link>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
