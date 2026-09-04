'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const router = useRouter();
  const [officerId, setOfficerId] = useState('DGMS-8821');
  const [password, setPassword] = useState('••••••••••••');
  const [mfaCode, setMfaCode] = useState('');
  const [step, setStep] = useState<'credentials' | 'mfa'>('credentials');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (step === 'credentials') {
      setStep('mfa');
    } else {
      router.push('/dashboard');
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 select-none font-sans">
      <div className="max-w-md w-full bg-white rounded-lg shadow-xl border border-slate-200 p-8 space-y-6">
        {/* Header Branding */}
        <div className="text-center space-y-2">
          <div className="w-14 h-14 rounded-lg bg-slate-900 text-white mx-auto flex items-center justify-center shadow-xs">
            <span className="material-symbols-outlined text-2xl">shield</span>
          </div>
          <h1 className="text-lg font-bold text-slate-900 tracking-tight">
            SMART MINE GOVERNANCE
          </h1>
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
            Coal Mine Safety & Compliance Directorate
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          {step === 'credentials' ? (
            <>
              <div>
                <label className="block font-bold text-slate-900 mb-1">
                  Officer Badge ID / Email
                </label>
                <input
                  type="text"
                  required
                  value={officerId}
                  onChange={(e) => setOfficerId(e.target.value)}
                  className="w-full h-9 px-3 bg-slate-50 rounded border border-slate-200 text-slate-900 focus:outline-none focus:border-slate-400"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-900 mb-1">
                  Password
                </label>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full h-9 px-3 bg-slate-50 rounded border border-slate-200 text-slate-900 focus:outline-none focus:border-slate-400"
                />
              </div>

              <button
                type="submit"
                className="w-full h-9 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-bold transition-colors flex items-center justify-center gap-1.5 shadow-xs"
              >
                <span>Proceed to MFA Verification</span>
                <span className="material-symbols-outlined text-sm">arrow_forward</span>
              </button>
            </>
          ) : (
            <>
              <div className="text-center space-y-1">
                <div className="font-bold text-slate-900">Multi-Factor Authentication</div>
                <p className="text-slate-500 text-xs">
                  Enter 6-digit TOTP code sent to officer device.
                </p>
              </div>

              <div>
                <input
                  type="text"
                  maxLength={6}
                  required
                  autoFocus
                  value={mfaCode}
                  onChange={(e) => setMfaCode(e.target.value)}
                  placeholder="e.g. 849201"
                  className="w-full h-11 text-center text-lg font-mono tracking-widest bg-slate-50 rounded border border-slate-200 text-slate-900 focus:outline-none focus:border-slate-400"
                />
              </div>

              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setStep('credentials')}
                  className="w-1/3 h-9 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded"
                >
                  Back
                </button>
                <button
                  type="submit"
                  className="w-2/3 h-9 bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold rounded transition-colors flex items-center justify-center gap-1.5 shadow-xs"
                >
                  <span className="material-symbols-outlined text-sm">lock_open</span>
                  <span>Verify & Login</span>
                </button>
              </div>
            </>
          )}
        </form>
      </div>
    </div>
  );
}
