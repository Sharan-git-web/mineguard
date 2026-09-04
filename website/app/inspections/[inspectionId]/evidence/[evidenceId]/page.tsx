'use client';

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { apiService } from '@/lib/api';
import { EvidenceItem } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';

export default function EvidenceViewerPage() {
  const params = useParams();
  const evidenceId = (params.evidenceId as string) || 'EVID-04';

  const [evidence, setEvidence] = useState<EvidenceItem | null>(null);
  const [zoomLevel, setZoomLevel] = useState(1);
  const [contrast, setContrast] = useState(100);

  useEffect(() => {
    apiService.getEvidenceById(evidenceId).then((e) => e && setEvidence(e));
  }, [evidenceId]);

  if (!evidence) return <div className="p-8 text-center text-secondary">Loading statutory evidence asset...</div>;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="p-6 bg-surface-container-lowest rounded-lg border border-outline-variant space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-xs font-bold text-secondary">{evidence.code}</span>
              <StatusBadge status={evidence.status} />
              <StatusBadge status={evidence.type} />
            </div>
            <h1 className="font-headline-xl text-headline-xl text-primary font-bold">
              {evidence.title}
            </h1>
            <p className="font-body-md text-body-md text-secondary">
              {evidence.mineName} • Section: {evidence.section} • Inspector: {evidence.inspectorName}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => alert('Downloading original uncompressed file with cryptographic signature.')}
              className="px-3.5 py-2 bg-primary hover:bg-primary-container text-on-primary rounded font-title-md text-title-md font-semibold flex items-center gap-1.5"
            >
              <span className="material-symbols-outlined text-sm">download</span>
              <span>Download Signed File ({evidence.fileSize})</span>
            </button>
          </div>
        </div>
      </div>

      {/* Viewer & Metadata Sidebar */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main High-Res Viewer Area */}
        <div className="lg:col-span-2 bg-slate-950 p-4 rounded-lg border border-outline-variant flex flex-col justify-between space-y-4">
          {/* Controls Bar */}
          <div className="flex items-center justify-between text-white text-xs px-2">
            <span className="font-mono text-slate-400">Timestamp: {evidence.timestamp}</span>
            <div className="flex items-center gap-3">
              <button onClick={() => setZoomLevel(z => Math.min(z + 0.2, 2.5))} className="hover:text-amber-400">Zoom +</button>
              <button onClick={() => setZoomLevel(z => Math.max(z - 0.2, 0.8))} className="hover:text-amber-400">Zoom -</button>
              <button onClick={() => setZoomLevel(1)} className="hover:text-amber-400">Reset</button>
              <button onClick={() => setContrast(c => c === 100 ? 150 : 100)} className="hover:text-amber-400">Enhance Contrast</button>
            </div>
          </div>

          {/* Image Display */}
          <div className="relative overflow-hidden rounded flex items-center justify-center min-h-[400px]">
            <img
              src={evidence.imageUrl}
              alt={evidence.title}
              style={{ transform: `scale(${zoomLevel})`, filter: `contrast(${contrast}%)` }}
              className="max-h-[500px] w-auto object-contain transition-transform duration-150"
            />

            {/* AI Bounding Box Overlay */}
            <div className="absolute top-1/3 left-1/3 w-36 h-28 border-2 border-error bg-error/20 rounded pointer-events-none flex items-start justify-start p-1">
              <span className="bg-error text-on-error font-mono text-[10px] font-bold px-1 rounded">
                AI Crack 89%
              </span>
            </div>
          </div>

          <div className="text-slate-400 text-xs font-mono text-center pt-2 border-t border-slate-800">
            Cryptographic Lock: {evidence.sha256Hash}
          </div>
        </div>

        {/* Forensic Metadata Panel */}
        <div className="bg-surface-container-lowest p-5 rounded-lg border border-outline-variant space-y-5">
          <h3 className="font-headline-sm text-headline-sm text-primary flex items-center gap-2 border-b border-outline-variant pb-2">
            <span className="material-symbols-outlined text-primary">fingerprint</span>
            Statutory Forensic EXIF Metadata
          </h3>

          <div className="space-y-3 text-body-sm text-secondary">
            <div>
              <span className="block font-label-sm text-label-sm text-secondary uppercase">File Asset ID</span>
              <strong className="text-on-surface font-mono">{evidence.code}</strong>
            </div>

            <div>
              <span className="block font-label-sm text-label-sm text-secondary uppercase">EXIF GPS Location</span>
              <strong className="text-on-surface">{evidence.gpsCoords.lat}° N, {evidence.gpsCoords.lng}° E</strong>
            </div>

            <div>
              <span className="block font-label-sm text-label-sm text-secondary uppercase">AI Vision Detection Flags</span>
              <div className="mt-1 space-y-1">
                {evidence.aiDetections.map((det, i) => (
                  <span key={i} className="inline-block mr-1 mb-1 px-2 py-0.5 bg-error-container text-on-error-container font-semibold rounded text-xs">
                    {det}
                  </span>
                ))}
              </div>
            </div>

            <div>
              <span className="block font-label-sm text-label-sm text-secondary uppercase">SHA-256 Ledger Hash</span>
              <code className="text-xs bg-surface-container p-2 rounded font-mono break-all block text-on-surface">
                {evidence.sha256Hash}
              </code>
            </div>

            <div>
              <span className="block font-label-sm text-label-sm text-secondary uppercase">Legally Binding Status</span>
              <strong className="text-emerald-700 flex items-center gap-1 font-semibold">
                <span className="material-symbols-outlined text-xs">gavel</span>
                DGMS Court Admissible
              </strong>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
