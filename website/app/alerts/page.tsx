'use client';

import React, { useEffect, useState } from 'react';
import { apiService } from '@/lib/api';
import { StatutoryAlert } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<StatutoryAlert[]>([]);

  useEffect(() => {
    apiService.getAlerts().then(setAlerts);
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            Centralized Statutory Alerts & Event Queue
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Real-Time IoT Sensor Spikes, Gas Threshold Overruns & Emergency Directives
          </p>
        </div>

        <button
          onClick={() => window.alert('All alerts marked as acknowledged.')}
          className="px-3.5 py-1.5 bg-white hover:bg-slate-50 text-slate-700 rounded-md border border-slate-200 text-xs font-semibold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">done_all</span>
          <span>Acknowledge All</span>
        </button>
      </div>

      <div className="space-y-4">
        {alerts.map((alertItem) => (
          <div
            key={alertItem.id}
            className="p-5 bg-white rounded-lg border border-slate-200/80 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4 hover:shadow-md transition-shadow text-xs"
          >
            <div className="space-y-1.5 flex-1">
              <div className="flex items-center gap-2">
                <span className="font-mono text-xs font-bold text-red-600 uppercase">{alertItem.type}</span>
                <StatusBadge status={alertItem.severity} />
                <span className="font-mono text-[11px] text-slate-400">• {alertItem.timestamp}</span>
              </div>
              <h3 className="text-base font-bold text-slate-900">{alertItem.title}</h3>
              <p className="text-slate-600">{alertItem.description}</p>
              <div className="text-xs font-semibold text-slate-900 mt-1">
                Mine: <strong>{alertItem.mineName}</strong> • Required Action: <span className="text-red-600 font-bold">{alertItem.actionRequired}</span>
              </div>
            </div>

            <div className="flex items-center gap-3 shrink-0">
              <button
                onClick={() => window.alert(`Executing: ${alertItem.actionRequired}`)}
                className="px-3.5 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded text-xs font-bold flex items-center gap-1 shadow-xs"
              >
                <span className="material-symbols-outlined text-sm">bolt</span>
                <span>Execute Action</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
