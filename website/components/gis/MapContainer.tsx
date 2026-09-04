'use client';

import React, { useState } from 'react';

interface MapContainerProps {
  interactive?: boolean;
  selectedMineId?: string;
  showRouteTrail?: boolean;
  height?: string;
}

export const MapContainer: React.FC<MapContainerProps> = ({
  interactive = true,
  selectedMineId,
  showRouteTrail = false,
  height = 'h-96'
}) => {
  const [activeLayer, setActiveLayer] = useState<'satellite' | 'strata' | 'methane'>('satellite');
  const [selectedMarker, setSelectedMarker] = useState<string | null>(selectedMineId || 'mine-a');

  const minesOnMap = [
    { id: 'mine-a', name: 'Rajrappa Open Cast Mine A', risk: 'CRITICAL', x: '45%', y: '38%', ch4: '1,250 PPM' },
    { id: 'mine-b', name: 'Jharia Underground Colliery B', risk: 'HIGH', x: '68%', y: '52%', ch4: '1,840 PPM' },
    { id: 'mine-c', name: 'Korba North Pit', risk: 'MEDIUM', x: '28%', y: '65%', ch4: '420 PPM' },
    { id: 'mine-d', name: 'Singrauli Deep Shaft', risk: 'LOW', x: '32%', y: '25%', ch4: '310 PPM' },
    { id: 'mine-e', name: 'Talcher South Block', risk: 'HIGH', x: '82%', y: '70%', ch4: '1,100 PPM' },
  ];

  return (
    <div className={`relative w-full ${height} bg-slate-900 rounded-lg overflow-hidden border border-outline-variant select-none`}>
      {/* Map Graphic Canvas Mock */}
      <div className="absolute inset-0 bg-[radial-gradient(#1e293b_1px,transparent_1px)] [background-size:16px_16px] opacity-70"></div>

      {/* Grid Lines Overlay */}
      <svg className="absolute inset-0 w-full h-full stroke-slate-800/80 pointer-events-none" width="100%" height="100%">
        <line x1="0" y1="25%" x2="100%" y2="25%" strokeWidth="1" strokeDasharray="4 4" />
        <line x1="0" y1="50%" x2="100%" y2="50%" strokeWidth="1" strokeDasharray="4 4" />
        <line x1="0" y1="75%" x2="100%" y2="75%" strokeWidth="1" strokeDasharray="4 4" />
        <line x1="33%" y1="0" x2="33%" y2="100%" strokeWidth="1" strokeDasharray="4 4" />
        <line x1="66%" y1="0" x2="66%" y2="100%" strokeWidth="1" strokeDasharray="4 4" />

        {/* Route trail SVG if requested */}
        {showRouteTrail && (
          <path
            d="M 280 140 Q 320 180 350 210 T 420 280"
            fill="none"
            stroke="#ef4444"
            strokeWidth="3"
            strokeDasharray="6 6"
            className="animate-pulse"
          />
        )}
      </svg>

      {/* Map Control Buttons */}
      <div className="absolute top-3 left-3 bg-surface/90 backdrop-blur-xs p-1 rounded border border-outline-variant flex items-center gap-1 z-10 text-xs font-medium">
        <button
          onClick={() => setActiveLayer('satellite')}
          className={`px-2.5 py-1 rounded transition-colors ${
            activeLayer === 'satellite' ? 'bg-primary text-on-primary font-bold' : 'text-secondary hover:bg-surface-container'
          }`}
        >
          Satellite & Topo
        </button>
        <button
          onClick={() => setActiveLayer('strata')}
          className={`px-2.5 py-1 rounded transition-colors ${
            activeLayer === 'strata' ? 'bg-primary text-on-primary font-bold' : 'text-secondary hover:bg-surface-container'
          }`}
        >
          Strata Seams
        </button>
        <button
          onClick={() => setActiveLayer('methane')}
          className={`px-2.5 py-1 rounded transition-colors ${
            activeLayer === 'methane' ? 'bg-primary text-on-primary font-bold' : 'text-secondary hover:bg-surface-container'
          }`}
        >
          Gas Heatmap
        </button>
      </div>

      {/* Map Legend */}
      <div className="absolute bottom-3 left-3 bg-surface/90 backdrop-blur-xs p-2.5 rounded border border-outline-variant text-[11px] space-y-1.5 z-10 text-secondary">
        <div className="font-bold text-primary">GIS Compliance Legend</div>
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-error animate-ping"></span>
          <span>Section 22 Statutory Shutdown / Critical Risk</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span>
          <span>High Risk Violation / Overdue Inspection</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
          <span>Compliant / Normal Telemetry</span>
        </div>
      </div>

      {/* Interactive Mine Pins */}
      {minesOnMap.map((mine) => {
        const isSelected = selectedMarker === mine.id;
        const colorClass = mine.risk === 'CRITICAL' ? 'bg-error text-on-error' : mine.risk === 'HIGH' ? 'bg-amber-500 text-white' : 'bg-emerald-500 text-white';

        return (
          <div
            key={mine.id}
            onClick={() => setSelectedMarker(mine.id)}
            style={{ left: mine.x, top: mine.y }}
            className={`absolute transform -translate-x-1/2 -translate-y-1/2 cursor-pointer z-10 group`}
          >
            {/* Ping effect for critical */}
            {mine.risk === 'CRITICAL' && (
              <span className="absolute -inset-2 rounded-full bg-error/40 animate-ping"></span>
            )}

            <div className={`w-8 h-8 rounded-full ${colorClass} shadow-lg flex items-center justify-center font-bold text-xs border-2 border-white`}>
              <span className="material-symbols-outlined text-sm">terrain</span>
            </div>

            {/* Tooltip on hover or selected */}
            {(isSelected || interactive) && (
              <div className="absolute left-1/2 bottom-full mb-2 transform -translate-x-1/2 w-48 p-2 bg-surface rounded-lg shadow-xl border border-outline-variant text-left z-20 pointer-events-none group-hover:block hidden">
                <div className="font-bold text-xs text-primary truncate">{mine.name}</div>
                <div className="text-[10px] text-secondary flex justify-between mt-1">
                  <span>Risk: <strong className="text-error">{mine.risk}</strong></span>
                  <span>CH4: <strong>{mine.ch4}</strong></span>
                </div>
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
};
