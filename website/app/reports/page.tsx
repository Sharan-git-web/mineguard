'use client';

import React, { useEffect, useState } from 'react';
import { apiService } from '@/lib/api';
import { StatutoryReport } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function ReportsPage() {
  const [reports, setReports] = useState<StatutoryReport[]>([]);

  useEffect(() => {
    apiService.getReports().then(setReports);
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">bar_chart</span>
            Statutory Reports & Dossier Generation
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            DGMS Form IV Monthly Safety Dossiers, Environmental Audits & Compliance PDF Exports
          </p>
        </div>

        <button
          onClick={() => alert('Generates Form IV Dossier PDF.')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">post_add</span>
          <span>Generate Form IV Dossier</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {reports.map((rep) => (
          <div key={rep.id} className="p-6 bg-white rounded-lg border border-slate-200/80 shadow-xs space-y-4 hover:shadow-md transition-shadow text-xs">
            <div className="flex items-start justify-between">
              <div>
                <span className="font-mono text-xs font-bold text-slate-400">{rep.code}</span>
                <h3 className="text-base font-bold text-slate-900 mt-1">{rep.title}</h3>
                <p className="text-xs text-slate-500 mt-0.5">{rep.mineName} • Period: {rep.period}</p>
              </div>
              <StatusBadge status={rep.status} />
            </div>

            <div className="p-3 rounded-md bg-slate-50 border border-slate-100 text-xs text-slate-600 flex items-center justify-between font-medium">
              <span>Generated: <strong className="text-slate-900">{rep.generatedAt}</strong></span>
              <span>File Size: <strong className="text-slate-900">{rep.fileSize}</strong></span>
            </div>

            <div className="flex items-center justify-end gap-3 pt-2">
              <button
                onClick={() => alert(`Downloading ${rep.title}`)}
                className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-bold flex items-center gap-1.5 shadow-xs"
              >
                <span className="material-symbols-outlined text-sm">download</span>
                <span>Download Certified PDF</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
