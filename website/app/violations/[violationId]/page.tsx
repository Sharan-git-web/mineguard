'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { apiService } from '@/lib/api';
import { Violation } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function ViolationDetailPage() {
  const params = useParams();
  const violationId = (params.violationId as string) || 'VIO-2026-901';

  const [violation, setViolation] = useState<Violation | null>(null);

  useEffect(() => {
    apiService.getViolationById(violationId).then((v) => v && setViolation(v));
  }, [violationId]);

  if (!violation) return <div className="p-8 text-center text-secondary">Loading violation case file...</div>;

  return (
    <div className="space-y-6">
      {/* Case Header */}
      <div className="p-6 bg-surface-container-lowest rounded-lg border border-outline-variant space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-secondary">{violation.code}</span>
              <StatusBadge status={violation.severity} />
              <StatusBadge status={violation.status} />
            </div>
            <h1 className="font-headline-xl text-headline-xl text-primary font-bold">
              {violation.title}
            </h1>
            <p className="font-body-md text-body-md text-secondary">
              Mine: <strong>{violation.mineName}</strong> • DGMS Act: <strong>{violation.dgmsSection}</strong>
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Link
              href={`/corrective-actions/CAPA-881`}
              className="px-3.5 py-2 bg-primary hover:bg-primary-container text-on-primary rounded font-title-md text-title-md font-semibold flex items-center gap-1.5"
            >
              <span className="material-symbols-outlined text-sm">published_with_changes</span>
              <span>View CAPA Execution Plan (CAPA-881)</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Grid details */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Legal Dossier */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
            <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
              <span className="material-symbols-outlined text-error">gavel</span>
              DGMS Statutory Enforcement Order
            </h3>

            <div className="p-4 rounded bg-error-container/30 border border-error/40 space-y-2 text-on-error-container">
              <div className="font-bold flex items-center gap-1">
                <span className="material-symbols-outlined text-sm">warning</span>
                Mandatory Section 22 Cease Work Directive
              </div>
              <p className="text-body-sm">{violation.description}</p>
              <div className="pt-2 flex items-center justify-between text-xs font-mono font-bold">
                <span>Fine Assessed: {violation.fineAmount}</span>
                <span>Deadline: {violation.deadline}</span>
              </div>
            </div>

            <div className="space-y-2 pt-2 text-body-md text-secondary">
              <h4 className="font-title-md text-title-md text-primary font-bold">Compliance Requirements for Order Revocation:</h4>
              <ul className="list-disc pl-5 space-y-1">
                <li>Complete double-strand resin anchor re-bolting along Grid 44-48.</li>
                <li>Submit clear gas clearance certificate (CH4 &lt; 500 PPM for 48 consecutive hours).</li>
                <li>Obtain formal verification sign-off from Chief Inspector.</li>
              </ul>
            </div>
          </div>
        </div>

        {/* Case File Sidebar */}
        <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
          <h3 className="font-headline-sm text-headline-sm text-primary border-b border-outline-variant pb-2">
            Case Metadata
          </h3>

          <div className="space-y-3 text-body-sm text-secondary">
            <div>
              <span className="block font-label-sm text-label-sm uppercase">Issuing Inspector</span>
              <strong className="text-on-surface">{violation.inspectorName}</strong>
            </div>

            <div>
              <span className="block font-label-sm text-label-sm uppercase">Date Issued</span>
              <strong className="text-on-surface">{violation.dateIssued}</strong>
            </div>

            <div>
              <span className="block font-label-sm text-label-sm uppercase">Linked CAPA Case</span>
              <Link href="/corrective-actions/CAPA-881" className="text-on-tertiary-container hover:underline font-mono font-bold block">
                {violation.capaId || 'CAPA-881'}
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
