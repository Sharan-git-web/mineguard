'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { apiService } from '@/lib/api';
import { Inspection } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DataTable } from '@/components/common/DataTable';

export default function InspectionsPage() {
  const [inspections, setInspections] = useState<Inspection[]>([]);

  useEffect(() => {
    apiService.getInspections().then(setInspections);
  }, []);

  const columns = [
    { header: 'DOSSIER CODE', accessorKey: 'code' as const },
    { header: 'MINE NAME', accessorKey: 'mineName' as const },
    { header: 'INSPECTOR', accessorKey: 'inspectorName' as const },
    { header: 'DATE', accessorKey: 'date' as const },
    {
      header: 'STATUS',
      cell: (row: Inspection) => <StatusBadge status={row.status} />
    },
    {
      header: 'RISK SCORE',
      cell: (row: Inspection) => (
        <span className={`font-bold ${row.riskScore > 80 ? 'text-red-600' : 'text-slate-900'}`}>
          {row.riskScore}/100
        </span>
      )
    },
    { header: 'ANOMALIES', accessorKey: 'anomalyCount' as const },
    {
      header: 'ACTIONS',
      cell: (row: Inspection) => (
        <div className="flex items-center gap-2">
          <Link
            href={`/inspections/${row.id}`}
            className="px-2.5 py-1 bg-slate-900 text-white rounded text-xs font-bold hover:bg-slate-800"
          >
            Dossier
          </Link>
          <Link
            href={`/inspections/${row.id}/route`}
            className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded text-xs font-bold flex items-center gap-1"
          >
            <span className="material-symbols-outlined text-xs">route</span>
            GPS
          </Link>
        </div>
      )
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            Inspections Registry & Route Tracker
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            DGMS Statutory Inspection Dossiers, GPS Checkpoint Validation & Breadcrumb Audits
          </p>
        </div>

        <button
          onClick={() => alert('Schedule Inspection Modal')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">add_task</span>
          <span>Schedule Inspection</span>
        </button>
      </div>

      <DataTable
        columns={columns}
        data={inspections}
        searchPlaceholder="Search by code, inspector, or mine..."
      />
    </div>
  );
}
