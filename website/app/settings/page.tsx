'use client';

import React, { useState } from 'react';

export default function SettingsPage() {
  const [gasThreshold, setGasThreshold] = useState('1000');
  const [slaHours, setSlaHours] = useState('72');
  const [fastapiEndpoint, setFastapiEndpoint] = useState('https://api.governance.dgms.gov.in/v1');
  const [mfaEnforced, setMfaEnforced] = useState(true);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">settings</span>
            System Settings & Regulatory Configuration
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            DGMS Statutory Parameters, Threshold Limits, FastAPI Endpoints & Security Enforcements
          </p>
        </div>

        <button
          onClick={() => alert('Settings saved.')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">save</span>
          <span>Save Settings</span>
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Gas & Strata Thresholds */}
        <div className="bg-white p-6 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
          <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2 border-b border-slate-100 pb-2">
            <span className="material-symbols-outlined text-slate-600">tune</span>
            Statutory Gas & Strata Threshold Limits
          </h3>

          <div className="space-y-4 text-xs">
            <div>
              <label className="block font-bold text-slate-900 mb-1">
                Methane CH4 Gas Warning Limit (PPM)
              </label>
              <input
                type="number"
                value={gasThreshold}
                onChange={(e) => setGasThreshold(e.target.value)}
                className="w-full h-9 px-3 bg-slate-50 rounded border border-slate-200 text-slate-900 font-mono"
              />
              <p className="text-[11px] text-slate-400 mt-1">Exceeding this limit automatically triggers Section 22 alert queue.</p>
            </div>

            <div>
              <label className="block font-bold text-slate-900 mb-1">
                Section 22 Order Remediation SLA (Hours)
              </label>
              <input
                type="number"
                value={slaHours}
                onChange={(e) => setSlaHours(e.target.value)}
                className="w-full h-9 px-3 bg-slate-50 rounded border border-slate-200 text-slate-900 font-mono"
              />
            </div>
          </div>
        </div>

        {/* Backend & Security Parameters */}
        <div className="bg-white p-6 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
          <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2 border-b border-slate-100 pb-2">
            <span className="material-symbols-outlined text-slate-600">api</span>
            Backend API & Authentication Policy
          </h3>

          <div className="space-y-4 text-xs">
            <div>
              <label className="block font-bold text-slate-900 mb-1">
                FastAPI Gateway URL Endpoint
              </label>
              <input
                type="text"
                value={fastapiEndpoint}
                onChange={(e) => setFastapiEndpoint(e.target.value)}
                className="w-full h-9 px-3 bg-slate-50 rounded border border-slate-200 text-slate-900 font-mono"
              />
            </div>

            <div className="flex items-center justify-between p-3 bg-slate-50 rounded border border-slate-200">
              <div>
                <div className="font-bold text-slate-900">Mandatory MFA Hardware Security</div>
                <div className="text-[11px] text-slate-400">Require TOTP or Smartcard for all Officers</div>
              </div>
              <input
                type="checkbox"
                checked={mfaEnforced}
                onChange={(e) => setMfaEnforced(e.target.checked)}
                className="w-4 h-4 text-slate-900 rounded"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
