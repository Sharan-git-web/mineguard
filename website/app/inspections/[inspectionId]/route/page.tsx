'use client';

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { apiService } from '@/lib/api';
import { Inspection } from '@/types';
import { MapContainer } from '@/components/gis/MapContainer';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function RouteInvestigationPage() {
  const params = useParams();
  const inspectionId = (params.inspectionId as string) || 'INS-0098';

  const [inspection, setInspection] = useState<Inspection | null>(null);

  useEffect(() => {
    apiService.getInspectionById(inspectionId).then((i) => i && setInspection(i));
  }, [inspectionId]);

  if (!inspection) return <div className="p-8 text-center text-secondary">Loading GPS route data...</div>;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="p-6 bg-surface-container-lowest rounded-lg border border-outline-variant space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-secondary">{inspection.code}</span>
              <StatusBadge status="VERIFIED_ROUTE" />
            </div>
            <h1 className="font-headline-xl text-headline-xl text-primary font-bold">
              GPS Route Investigation & Inspector Breadcrumb Trail
            </h1>
            <p className="font-body-md text-body-md text-secondary">
              Inspector: <strong>{inspection.inspectorName} ({inspection.inspectorBadge})</strong> • {inspection.mineName}
            </p>
          </div>
        </div>
      </div>

      {/* Map & Timeline Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Route Map Canvas */}
        <div className="lg:col-span-2 bg-surface-container-lowest p-5 rounded-lg border border-outline-variant space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
              <span className="material-symbols-outlined text-primary">near_me</span>
              Geofenced Breadcrumb Path Map
            </h3>
            <span className="font-code-sm text-code-sm text-emerald-600 font-bold flex items-center gap-1">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              4 Checkpoints Validated
            </span>
          </div>

          <MapContainer height="h-96" showRouteTrail={true} />
        </div>

        {/* Checkpoint Timeline Audit */}
        <div className="bg-surface-container-lowest p-5 rounded-lg border border-outline-variant space-y-4">
          <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2">
            <span className="material-symbols-outlined text-primary">fact_check</span>
            Statutory Checkpoint Log
          </h3>

          <div className="space-y-4">
            {inspection.checkpoints.map((chk, idx) => (
              <div key={chk.id} className="relative pl-6 border-l-2 border-primary space-y-1">
                <div className="absolute -left-[9px] top-0 w-4 h-4 rounded-full bg-primary border-2 border-white flex items-center justify-center">
                  <span className="w-1.5 h-1.5 rounded-full bg-white"></span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="font-title-md text-title-md text-primary font-bold">{chk.name}</span>
                  <span className="font-mono text-xs text-secondary">{chk.timestamp}</span>
                </div>
                <div className="text-body-sm text-secondary flex items-center gap-3">
                  <span>Speed: <strong>{chk.speed}</strong></span>
                  <span>Elevation: <strong>{chk.elevation}</strong></span>
                </div>
                <div className="text-label-sm font-semibold text-emerald-700 flex items-center gap-1">
                  <span className="material-symbols-outlined text-xs">verified</span>
                  <span>GPS Geofence Match ({chk.lat}°, {chk.lng}°)</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
