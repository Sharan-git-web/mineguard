'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { apiService } from '@/lib/api';
import { CorrectiveAction, CapaStatus } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function CorrectiveActionsPage() {
  const [capas, setCapas] = useState<CorrectiveAction[]>([]);

  useEffect(() => {
    apiService.getCapas().then(setCapas);
  }, []);

  const columns: { title: string; status: CapaStatus }[] = [
    { title: 'Open / Assigned', status: 'OPEN' },
    { title: 'In Progress', status: 'IN_PROGRESS' },
    { title: 'Evidence Submitted', status: 'EVIDENCE_SUBMITTED' },
    { title: 'Under Verification', status: 'UNDER_VERIFICATION' },
    { title: 'Closed / Certified', status: 'CLOSED' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            Corrective & Preventive Action (CAPA) Workflow
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Lifecycle tracking for statutory remediation, structural repairs, and DGMS sign-offs
          </p>
        </div>

        <button
          onClick={() => alert('Initiate CAPA Plan Modal')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">add</span>
          <span>Initiate CAPA Plan</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 overflow-x-auto custom-scrollbar pb-4">
        {columns.map((col) => {
          const colCapas = capas.filter((c) => c.status === col.status || (col.status === 'OPEN' && c.status === 'OPEN'));

          return (
            <div key={col.status} className="bg-white rounded-lg border border-slate-200/80 shadow-xs p-4 space-y-3 min-w-[220px]">
              <div className="flex items-center justify-between border-b border-slate-100 pb-2 text-xs font-bold text-slate-900">
                <span>{col.title}</span>
                <span className="px-2 py-0.5 rounded bg-slate-100 text-slate-600 font-bold">
                  {colCapas.length}
                </span>
              </div>

              <div className="space-y-3">
                {colCapas.length === 0 ? (
                  <div className="text-center py-6 text-slate-400 text-xs italic">No actions</div>
                ) : (
                  colCapas.map((capa) => (
                    <Link
                      key={capa.id}
                      href={`/corrective-actions/${capa.id}`}
                      className="block p-3 rounded.md bg-slate-50 border border-slate-200/80 hover:bg-slate-100/80 transition-colors space-y-2 text-xs"
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-mono text-[11px] font-bold text-slate-900">{capa.code}</span>
                        <StatusBadge status={capa.priority} />
                      </div>

                      <h4 className="font-bold text-slate-900 leading-snug">
                        {capa.title}
                      </h4>

                      <p className="text-[11px] text-slate-500 truncate">{capa.mineName}</p>

                      <div className="pt-2 border-t border-slate-200/60 flex items-center justify-between text-[11px]">
                        <span className="text-red-600 font-bold">{capa.slaHoursRemaining}h SLA</span>
                        <span className="text-slate-500 font-medium">{capa.evidenceCount} Files</span>
                      </div>
                    </Link>
                  ))
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
