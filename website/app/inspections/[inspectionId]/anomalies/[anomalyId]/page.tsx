'use client';

import React from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function AnomalyDetailPage() {
  const params = useParams();
  const inspectionId = (params.inspectionId as string) || 'INS-0098';
  const anomalyId = (params.anomalyId as string) || 'ANOM-981';

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="p-6 bg-surface-container-lowest rounded-lg border border-outline-variant space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-secondary">{anomalyId} • {inspectionId}</span>
              <StatusBadge status="CRITICAL" />
              <StatusBadge status="UNRESOLVED" />
            </div>
            <h1 className="font-headline-xl text-headline-xl text-primary font-bold">
              Anomaly Investigation: Section B Roof Bolting Grid 44
            </h1>
            <p className="font-body-md text-body-md text-secondary">
              Rajrappa Open Cast Mine A • Strata Instability & Gas Telemetry Spike
            </p>
          </div>

          <div className="flex items-center gap-3">
            <Link
              href={`/corrective-actions/CAPA-881`}
              className="px-3.5 py-2 bg-primary hover:bg-primary-container text-on-primary rounded font-title-md text-title-md font-semibold flex items-center gap-1.5"
            >
              <span className="material-symbols-outlined text-sm">published_with_changes</span>
              <span>Assign CAPA Plan (CAPA-881)</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Grid details */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Anomaly Technical Telemetry */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
            <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
              <span className="material-symbols-outlined text-error">analytics</span>
              Load-Cell & Gas Telemetry Analysis
            </h3>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="p-4 rounded bg-error-container/40 border border-error/30 space-y-1">
                <span className="font-label-md text-label-md text-secondary uppercase">Load-Cell Displacement</span>
                <div className="font-display-lg text-display-lg text-error font-bold">+42% Over Limit</div>
                <p className="text-body-sm text-on-error-container">Sensor #LC-44 registered 18.4mm roof sag (DGMS Max Limit: 12.0mm)</p>
              </div>

              <div className="p-4 rounded bg-error-container/40 border border-error/30 space-y-1">
                <span className="font-label-md text-label-md text-secondary uppercase">CH4 Gas Accumulation</span>
                <div className="font-display-lg text-display-lg text-error font-bold">1,250 PPM</div>
                <p className="text-body-sm text-on-error-container">Methane accumulation in unventilated roof cavity. Ignition risk high.</p>
              </div>
            </div>

            <div className="p-4 rounded bg-surface-container-low space-y-2 text-body-md text-on-surface">
              <h4 className="font-title-md text-title-md text-primary font-bold">Inspector Field Observation:</h4>
              <p className="text-secondary">
                &ldquo;Visual inspection confirmed shear bolt deformation along 14m of Section B roof grid. Water seepage accelerating strata deterioration. Recommended immediate Section 22 evacuation.&rdquo;
              </p>
            </div>
          </div>
        </div>

        {/* Linked Evidence Preview */}
        <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
          <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
            <span className="material-symbols-outlined text-primary">photo_library</span>
            Linked Evidence (EVID-04)
          </h3>

          <div className="relative rounded overflow-hidden border border-outline-variant group">
            {/* Mock Evidence Image */}
            <img
              src="https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80"
              alt="Roof Bolting Defect"
              className="w-full h-48 object-cover"
            />
            <div className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <Link
                href={`/inspections/${inspectionId}/evidence/EVID-04`}
                className="px-3 py-1.5 bg-white text-primary rounded font-title-md font-bold shadow-lg"
              >
                Inspect High-Res Evidence
              </Link>
            </div>
          </div>

          <div className="space-y-1 text-body-sm text-secondary">
            <div>EXIF GPS: <strong>23.6278° N, 85.7155° E</strong></div>
            <div>AI Detection: <strong className="text-error font-semibold">Strata Crack (Confidence 89%)</strong></div>
            <div>Cryptographic Hash: <code className="text-xs bg-surface-container p-1 rounded font-mono truncate block">e3b0c44298fc...</code></div>
          </div>
        </div>
      </div>
    </div>
  );
}
