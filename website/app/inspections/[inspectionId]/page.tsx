'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { apiService } from '@/lib/api';
import { Inspection } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { StatCard } from '@/components/common/StatCard';

export default function InspectionDetailPage() {
  const params = useParams();
  const inspectionId = (params.inspectionId as string) || 'INS-0098';

  const [inspection, setInspection] = useState<Inspection | null>(null);

  useEffect(() => {
    apiService.getInspectionById(inspectionId).then((i) => i && setInspection(i));
  }, [inspectionId]);

  if (!inspection) return <div className="p-8 text-center text-secondary">Loading inspection dossier...</div>;

  return (
    <div className="space-y-6">
      {/* Dossier Header */}
      <div className="p-6 bg-surface-container-lowest rounded-lg border border-outline-variant space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-secondary">{inspection.code}</span>
              <StatusBadge status={inspection.status} />
            </div>
            <h1 className="font-headline-xl text-headline-xl text-primary font-bold">
              Inspection Dossier: {inspection.mineName}
            </h1>
            <p className="font-body-md text-body-md text-secondary">
              Inspector: <strong>{inspection.inspectorName} ({inspection.inspectorBadge})</strong> • Date: {inspection.date}
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Link
              href={`/inspections/${inspection.id}/route`}
              className="px-3.5 py-2 bg-surface-container hover:bg-surface-container-high text-secondary rounded font-title-md text-title-md flex items-center gap-1.5"
            >
              <span className="material-symbols-outlined text-sm">route</span>
              <span>Inspect GPS Route</span>
            </Link>
            <Link
              href={`/inspections/${inspection.id}/evidence/EVID-04`}
              className="px-3.5 py-2 bg-primary hover:bg-primary-container text-on-primary rounded font-title-md text-title-md font-semibold flex items-center gap-1.5"
            >
              <span className="material-symbols-outlined text-sm">photo_library</span>
              <span>View Evidence (11 Files)</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Inspection Risk Score"
          value={`${inspection.riskScore}/100`}
          subtitle="Flagged Threshold"
          icon="shield_with_heart"
          trend="Critical Risk"
          trendType="danger"
          highlight
        />
        <StatCard
          title="Methane Gas (CH4)"
          value={`${inspection.methaneLevelPpm} PPM`}
          subtitle="Sensor #CH4-B44"
          icon="sensors"
          trend="+150% Spike"
          trendType="danger"
        />
        <StatCard
          title="Strata Stability"
          value={`${inspection.strataStability}/100`}
          subtitle="Grid 44 Load-Cell"
          icon="layers"
        />
        <StatCard
          title="Anomalies & Evidence"
          value={`${inspection.anomalyCount} Flagged`}
          subtitle="11 Evidence Files"
          icon="description"
        />
      </div>

      {/* Flagged Anomalies Section */}
      <div className="bg-surface-container-lowest p-6 rounded-lg border border-outline-variant space-y-4">
        <div className="flex items-center justify-between border-b border-outline-variant pb-3">
          <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
            <span className="material-symbols-outlined text-error">warning</span>
            Flagged Inspection Anomalies ({inspection.anomalies.length})
          </h3>
        </div>

        <div className="space-y-4">
          {inspection.anomalies.map((anom) => (
            <div
              key={anom.id}
              className="p-4 rounded-lg bg-surface-container-low border-l-4 border-error space-y-3"
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <span className="font-mono text-xs font-bold text-secondary">{anom.id} • {anom.category}</span>
                  <h4 className="font-title-md text-title-md text-primary font-bold">{anom.section}</h4>
                </div>
                <StatusBadge status={anom.severity} />
              </div>

              <p className="text-body-md text-secondary">{anom.description}</p>

              {anom.telemetrySpike && (
                <div className="text-body-sm font-semibold text-error">
                  Telemetry Spike Detected: {anom.telemetrySpike}
                </div>
              )}

              <div className="pt-2 flex flex-wrap items-center gap-3">
                <Link
                  href={`/inspections/${inspection.id}/anomalies/${anom.id}`}
                  className="px-3 py-1.5 bg-primary text-on-primary rounded font-title-md text-title-md font-semibold flex items-center gap-1"
                >
                  <span>Deep-Dive Anomaly Investigation</span>
                  <span className="material-symbols-outlined text-sm">arrow_forward</span>
                </Link>
                {anom.evidenceId && (
                  <Link
                    href={`/inspections/${inspection.id}/evidence/${anom.evidenceId}`}
                    className="px-3 py-1.5 bg-surface-container hover:bg-surface-container-high text-secondary rounded font-title-md text-title-md flex items-center gap-1"
                  >
                    <span className="material-symbols-outlined text-sm">image</span>
                    <span>View Linked Evidence ({anom.evidenceId})</span>
                  </Link>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
