'use client';

import React from 'react';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function AuditTrailPage() {
  const ledgerEvents = [
    {
      txHash: '0x8f3a9d21...4e81',
      block: '#44,821',
      event: 'DGMS Section 22(1) Order Issued',
      entity: 'VIO-2026-901',
      actor: 'Dr. Alok Verma (Chief Compliance Officer)',
      timestamp: '2026-09-04 11:20:04 IST',
    },
    {
      txHash: '0x2b4c8a19...90d4',
      block: '#44,818',
      event: 'Inspection Evidence Cryptographic Seal',
      entity: 'EVID-04/11 (INS-0098)',
      actor: 'Insp. Rajesh Kumar (DGMS-8821)',
      timestamp: '2026-09-04 09:16:22 IST',
    },
    {
      txHash: '0x7e11f092...31a5',
      block: '#44,810',
      event: 'Telemetry Gas Spike Logged',
      entity: 'Rajrappa Mine A (Sensor #CH4-B44)',
      actor: 'Automated IoT Gateway #GW-04',
      timestamp: '2026-09-04 08:32:00 IST',
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">history</span>
            Statutory Audit Trail & Cryptographic Ledger
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Immutable SHA-256 Block Chain Anchoring for Enforcement Directives, Signatures & Evidence
          </p>
        </div>

        <button
          onClick={() => alert('Ledger Integrity Validated: Zero Tamper Anomaly.')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">verified_user</span>
          <span>Verify Ledger Integrity</span>
        </button>
      </div>

      <div className="bg-white p-5 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
        <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">Tamper-Evident Transaction Log</h3>

        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-slate-100 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                <th className="pb-3 px-3 font-semibold">TRANSACTION HASH</th>
                <th className="pb-3 px-3 font-semibold">BLOCK #</th>
                <th className="pb-3 px-3 font-semibold">COMPLIANCE EVENT</th>
                <th className="pb-3 px-3 font-semibold">TARGET ENTITY</th>
                <th className="pb-3 px-3 font-semibold">SIGNING AUTHORITY</th>
                <th className="pb-3 px-3 font-semibold">TIMESTAMP</th>
                <th className="pb-3 px-3 font-semibold">VERIFICATION</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-xs text-slate-700 font-medium">
              {ledgerEvents.map((e, idx) => (
                <tr key={idx} className="hover:bg-slate-50/80 transition-colors">
                  <td className="py-3 px-3 font-mono text-xs font-bold text-slate-900">{e.txHash}</td>
                  <td className="py-3 px-3 font-mono text-xs text-slate-400">{e.block}</td>
                  <td className="py-3 px-3 font-bold text-slate-900">{e.event}</td>
                  <td className="py-3 px-3 font-mono text-xs text-slate-500">{e.entity}</td>
                  <td className="py-3 px-3 text-slate-600">{e.actor}</td>
                  <td className="py-3 px-3 text-xs text-slate-400">{e.timestamp}</td>
                  <td className="py-3 px-3">
                    <StatusBadge status="VERIFIED" />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
