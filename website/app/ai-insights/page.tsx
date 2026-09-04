'use client';

import React from 'react';
import { StatusBadge } from '@/components/common/StatusBadge';
import { StatCard } from '@/components/common/StatCard';

export default function AiInsightsPage() {
  const aiPredictions = [
    {
      id: 'AI-PRED-801',
      title: 'Strata Sag & Pillar Shear Acceleration',
      mine: 'Rajrappa Open Cast Mine A — Seam B',
      probability: '87% Risk Probability within 72h',
      severity: 'CRITICAL',
      recommendation: 'Pre-emptively evacuate Section B Grid 44 and inject high-strength resin grouting into tension cracks.',
      confidence: '94.2% AI Model Confidence'
    },
    {
      id: 'AI-PRED-802',
      title: 'Auxiliary Fan Stagnation & Gas Buildup',
      mine: 'Jharia Underground Colliery B — Gallery 9',
      probability: '74% Risk Probability within 48h',
      severity: 'HIGH',
      recommendation: 'Replace degraded ventilation canvas ducting and reset breaker threshold #VB-09.',
      confidence: '91.8% AI Model Confidence'
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">psychology</span>
            AI Predictive Safety & Hazard Risk Insights
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Machine Learning Strata Forecasting, Gas Accumulation Models & Early Warning Engine
          </p>
        </div>

        <button
          onClick={() => alert('AI Model Retrained.')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">refresh</span>
          <span>Re-run Predictive Model</span>
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard
          title="ACTIVE PREDICTIVE ALERTS"
          value={4}
          subtitle="2 High Confidence Spikes"
          icon="psychology"
          topBorderColor="red"
        />
        <StatCard
          title="MODEL FORECAST PRECISION"
          value="94.6%"
          subtitle="Validated against DGMS Audits"
          icon="track_changes"
          topBorderColor="emerald"
        />
        <StatCard
          title="PREVENTATIVE ACTIONS ISSUED"
          value={12}
          subtitle="Pre-empted Seam Collapses"
          icon="shield"
          topBorderColor="blue"
        />
      </div>

      <div className="space-y-4">
        <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">High-Confidence ML Hazard Predictions</h3>
        {aiPredictions.map((pred) => (
          <div key={pred.id} className="p-6 bg-white rounded-lg border border-slate-200/80 shadow-xs space-y-3 text-xs">
            <div className="flex items-center justify-between">
              <div>
                <span className="font-mono text-xs font-bold text-slate-400">{pred.id} • {pred.confidence}</span>
                <h4 className="text-base font-bold text-slate-900 mt-0.5">{pred.title}</h4>
              </div>
              <StatusBadge status={pred.severity} />
            </div>

            <p className="text-red-600 font-bold text-xs">{pred.mine} — {pred.probability}</p>

            <div className="p-3 rounded-md bg-slate-50 border border-slate-100 text-xs text-slate-700 space-y-1">
              <span className="font-bold text-slate-900 flex items-center gap-1">
                <span className="material-symbols-outlined text-sm">auto_awesome</span>
                AI Recommended Advisory:
              </span>
              <p className="text-slate-600 leading-relaxed">{pred.recommendation}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
