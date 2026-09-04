import React from 'react';

interface StatusBadgeProps {
  status: string;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  const norm = status.toUpperCase().trim();

  let style = 'bg-slate-100 text-slate-600 border-slate-200';
  let dotColor = 'bg-slate-400';
  let label = status;

  if (norm === 'CRITICAL' || norm === 'SECTION_22_ORDER' || norm === 'FLAGGED' || norm === 'SHUTDOWN_NOTICE') {
    style = 'bg-red-50 text-red-600 border-red-200';
    dotColor = 'bg-red-500';
    label = 'Critical';
  } else if (norm === 'HIGH' || norm === 'UNRESOLVED' || norm === 'URGENT') {
    style = 'bg-orange-50 text-orange-600 border-orange-200';
    dotColor = 'bg-orange-500';
    label = 'High';
  } else if (norm === 'MEDIUM' || norm === 'IN_PROGRESS' || norm === 'CAPA_ASSIGNED' || norm === 'UNDER_REVIEW') {
    style = 'bg-amber-50 text-amber-700 border-amber-200';
    dotColor = 'bg-amber-500';
    label = 'Medium';
  } else if (norm === 'GOOD' || norm === 'LOW' || norm === 'OPERATIONAL' || norm === 'COMPLETED' || norm === 'REMEDIATED' || norm === 'CERTIFIED' || norm === 'CLOSED') {
    style = 'bg-emerald-50 text-emerald-700 border-emerald-200';
    dotColor = 'bg-emerald-500';
    label = 'Good';
  } else if (norm === 'REVIEW' || norm === 'SCHEDULED' || norm === 'EVIDENCE_SUBMITTED') {
    style = 'bg-blue-50 text-blue-700 border-blue-200';
    dotColor = 'bg-blue-500';
    label = 'Review';
  }

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold border ${style}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${dotColor}`}></span>
      <span>{label}</span>
    </span>
  );
};
