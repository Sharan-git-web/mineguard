'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { apiService } from '@/lib/api';
import { Mine } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DataTable } from '@/components/common/DataTable';

export default function MinesPage() {
  const router = useRouter();
  const [mines, setMines] = useState<Mine[]>([]);
  const [riskFilter, setRiskFilter] = useState('ALL');
  const [typeFilter, setTypeFilter] = useState('ALL');
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');

  useEffect(() => {
    apiService.getMines().then(setMines);
  }, []);

  const filteredMines = mines.filter(m => {
    if (riskFilter !== 'ALL' && m.riskLevel !== riskFilter) return false;
    if (typeFilter !== 'ALL' && m.type !== typeFilter) return false;
    return true;
  });

  const columns = [
    { header: 'MINE CODE', accessorKey: 'code' as const },
    { header: 'MINE NAME', accessorKey: 'name' as const },
    { header: 'REGION', accessorKey: 'region' as const },
    { header: 'TYPE', accessorKey: 'type' as const },
    {
      header: 'RISK LEVEL',
      cell: (row: Mine) => <StatusBadge status={row.riskLevel} />
    },
    {
      header: 'STATUS',
      cell: (row: Mine) => <StatusBadge status={row.status} />
    },
    {
      header: 'METHANE (PPM)',
      accessorKey: 'methaneGasPpm' as const
    },
    {
      header: 'ACTION',
      cell: (row: Mine) => (
        <Link
          href={`/mines/${row.id}`}
          className="px-3 py-1 bg-slate-900 text-white rounded text-xs font-bold hover:bg-slate-800"
        >
          View Profile
        </Link>
      )
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            All Monitored Mines Directory
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            DGMS Registered Coal Mines, Open Pit & Underground Seam Directory
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setViewMode(viewMode === 'grid' ? 'table' : 'grid')}
            className="px-3.5 py-1.5 bg-white hover:bg-slate-50 text-slate-700 rounded-md border border-slate-200 text-xs font-semibold flex items-center gap-1.5 shadow-xs"
          >
            <span className="material-symbols-outlined text-base">
              {viewMode === 'grid' ? 'table_view' : 'grid_view'}
            </span>
            <span>{viewMode === 'grid' ? 'Table View' : 'Grid View'}</span>
          </button>
          <button
            onClick={() => alert('New Mine Registration Modal')}
            className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs"
          >
            <span className="material-symbols-outlined text-base">add</span>
            <span>Register Mine Seam</span>
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="p-4 bg-white rounded-lg border border-slate-200/80 shadow-xs flex flex-wrap items-center justify-between gap-4 text-xs">
        <div className="flex flex-wrap items-center gap-4">
          <div className="flex items-center gap-2">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Risk Filter:</span>
            <select
              value={riskFilter}
              onChange={(e) => setRiskFilter(e.target.value)}
              className="h-8 px-2 bg-slate-50 rounded border border-slate-200 text-xs text-slate-900 font-medium"
            >
              <option value="ALL">All Risk Levels</option>
              <option value="CRITICAL">Critical</option>
              <option value="HIGH">High</option>
              <option value="MEDIUM">Medium</option>
              <option value="LOW">Low</option>
            </select>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Type:</span>
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              className="h-8 px-2 bg-slate-50 rounded border border-slate-200 text-xs text-slate-900 font-medium"
            >
              <option value="ALL">All Mine Types</option>
              <option value="Open Cast">Open Cast</option>
              <option value="Underground">Underground</option>
              <option value="Mixed">Mixed</option>
            </select>
          </div>
        </div>

        <div className="text-xs text-slate-500 font-medium">
          Showing {filteredMines.length} of {mines.length} Mines
        </div>
      </div>

      {/* Grid or Table */}
      {viewMode === 'grid' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredMines.map((mine) => (
            <div
              key={mine.id}
              className="p-5 bg-white rounded-lg border border-slate-200/80 shadow-xs space-y-4 hover:shadow-md transition-shadow flex flex-col justify-between"
            >
              <div className="space-y-2">
                <div className="flex items-start justify-between">
                  <div>
                    <span className="font-mono text-xs text-slate-400 font-bold">{mine.code}</span>
                    <h3 className="text-base font-bold text-slate-900 leading-snug">
                      {mine.name}
                    </h3>
                    <p className="text-xs text-slate-500">{mine.region}</p>
                  </div>
                  <StatusBadge status={mine.riskLevel} />
                </div>

                <div className="pt-2 border-t border-slate-100 grid grid-cols-2 gap-3 text-xs">
                  <div className="p-2 rounded bg-slate-50">
                    <span className="block text-[10px] text-slate-400 font-bold uppercase">Type</span>
                    <strong className="text-slate-900 font-semibold">{mine.type}</strong>
                  </div>
                  <div className="p-2 rounded bg-slate-50">
                    <span className="block text-[10px] text-slate-400 font-bold uppercase">Status</span>
                    <StatusBadge status={mine.status} />
                  </div>
                  <div className="p-2 rounded bg-slate-50">
                    <span className="block text-[10px] text-slate-400 font-bold uppercase">Methane Gas</span>
                    <strong className="text-slate-900 font-semibold">{mine.methaneGasPpm} PPM</strong>
                  </div>
                  <div className="p-2 rounded bg-slate-50">
                    <span className="block text-[10px] text-slate-400 font-bold uppercase">Strata Index</span>
                    <strong className="text-slate-900 font-semibold">{mine.strataStabilityScore}/100</strong>
                  </div>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                <div className="text-slate-500 font-medium">
                  Violations: <strong className="text-red-600 font-bold">{mine.activeViolationsCount}</strong>
                </div>
                <Link
                  href={`/mines/${mine.id}`}
                  className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-bold flex items-center gap-1"
                >
                  <span>View Details</span>
                  <span className="material-symbols-outlined text-sm">arrow_forward</span>
                </Link>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={filteredMines}
          searchPlaceholder="Search mines by name, region, or code..."
          onRowClick={(m) => router.push(`/mines/${m.id}`)}
        />
      )}
    </div>
  );
}
