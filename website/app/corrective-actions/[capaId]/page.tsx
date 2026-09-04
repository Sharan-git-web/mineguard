'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { apiService } from '@/lib/api';
import { CorrectiveAction } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function CapaDetailPage() {
  const params = useParams();
  const capaId = (params.capaId as string) || 'CAPA-881';

  const [capa, setCapa] = useState<CorrectiveAction | null>(null);

  useEffect(() => {
    apiService.getCapaById(capaId).then((c) => c && setCapa(c));
  }, [capaId]);

  if (!capa) return <div className="p-8 text-center text-secondary">Loading CAPA case file...</div>;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="p-6 bg-surface-container-lowest rounded-lg border border-outline-variant space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-secondary">{capa.code}</span>
              <StatusBadge status={capa.priority} />
              <StatusBadge status={capa.status} />
            </div>
            <h1 className="font-headline-xl text-headline-xl text-primary font-bold">
              {capa.title}
            </h1>
            <p className="font-body-md text-body-md text-secondary">
              Mine: <strong>{capa.mineName}</strong> • Assigned: <strong>{capa.assignedTo}</strong>
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => alert('Inspector Sign-off Certified. Section 22 Lifted.')}
              className="px-3.5 py-2 bg-emerald-700 hover:bg-emerald-800 text-white rounded font-title-md text-title-md font-bold flex items-center gap-1.5"
            >
              <span className="material-symbols-outlined text-sm">verified</span>
              <span>Verify & Sign-off CAPA</span>
            </button>
          </div>
        </div>
      </div>

      {/* Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Remediation Steps & Plan */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
            <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
              <span className="material-symbols-outlined text-primary">checklist</span>
              Remediation Action Protocol Steps
            </h3>

            <div className="space-y-3">
              {capa.steps.map((step) => (
                <div key={step.id} className="p-3.5 rounded bg-surface-container-low border border-outline-variant flex items-start gap-3">
                  <input
                    type="checkbox"
                    checked={step.completed}
                    readOnly
                    className="mt-1 w-4 h-4 text-primary rounded"
                  />
                  <div className="flex-1">
                    <div className={`font-title-md text-title-md ${step.completed ? 'line-through text-secondary' : 'text-primary font-bold'}`}>
                      {step.title}
                    </div>
                    {step.completedAt && (
                      <span className="text-xs font-mono text-emerald-700 font-semibold">
                        Completed at {step.completedAt}
                      </span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* SLA & Proof Sidebar */}
        <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
          <h3 className="font-headline-sm text-headline-sm text-primary border-b border-outline-variant pb-2">
            SLA & Statutory Deadline
          </h3>

          <div className="p-4 rounded bg-error-container/30 border border-error/40 text-center space-y-1">
            <span className="font-label-sm text-label-sm text-secondary uppercase">SLA Countdown</span>
            <div className="font-display-lg text-display-lg text-error font-bold">{capa.slaHoursRemaining} Hours Remaining</div>
            <p className="text-body-sm text-on-error-container">Target Resolution: {capa.targetDate}</p>
          </div>

          <div className="space-y-2 pt-2 text-body-sm text-secondary">
            <div>Linked Violation: <Link href="/violations/VIO-2026-901" className="text-on-tertiary-container hover:underline font-mono font-bold">{capa.violationId}</Link></div>
            <div>Submitted Proof Files: <strong>3 Photogrammetry Files</strong></div>
          </div>
        </div>
      </div>
    </div>
  );
}
