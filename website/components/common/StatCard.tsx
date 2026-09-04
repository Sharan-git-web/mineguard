import React from 'react';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: string;
  topBorderColor?: 'red' | 'amber' | 'blue' | 'gray' | 'emerald';
  highlight?: boolean;
  trend?: string;
  trendType?: 'up' | 'down' | 'neutral' | 'danger';
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  subtitle,
  icon,
  topBorderColor,
  highlight
}) => {
  let effectiveColor = topBorderColor || (highlight ? 'red' : 'gray');

  let borderClass = 'bg-slate-200';
  if (effectiveColor === 'red') borderClass = 'bg-red-500';
  if (effectiveColor === 'amber') borderClass = 'bg-amber-500';
  if (effectiveColor === 'blue') borderClass = 'bg-blue-500';
  if (effectiveColor === 'emerald') borderClass = 'bg-emerald-500';

  return (
    <div className="relative bg-white rounded-lg border border-slate-200/80 p-4 shadow-xs overflow-hidden flex flex-col justify-between">
      {/* Top Accent Border Line */}
      <div className={`absolute top-0 left-0 right-0 h-1 ${borderClass}`} />

      <div className="flex items-center justify-between">
        <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">
          {title}
        </span>
        <span className="material-symbols-outlined text-slate-400 text-lg">
          {icon}
        </span>
      </div>

      <div className="mt-2 text-3xl font-extrabold text-slate-900 tracking-tight">
        {value}
      </div>

      {subtitle && (
        <div className="mt-1 text-xs text-slate-500 font-medium">
          {subtitle}
        </div>
      )}
    </div>
  );
};
