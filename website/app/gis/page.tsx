'use client';

import React from 'react';
import { MapContainer } from '@/components/gis/MapContainer';

export default function GisMapPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">map</span>
            GIS Geospatial Command & Remote Sensing Map
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Regional Topographical Overlays, Seam Boundary Layers, IoT Sensor Telemetry & Geofenced Trajectory Tracing
          </p>
        </div>
      </div>

      <div className="bg-white p-4 rounded-lg border border-slate-200/80 shadow-xs space-y-4">
        <MapContainer height="h-[600px]" interactive={true} showRouteTrail={true} />
      </div>
    </div>
  );
}
