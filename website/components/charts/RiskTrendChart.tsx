'use client';

import React from 'react';
import dynamic from 'next/dynamic';

const DynamicChart = dynamic(
  () =>
    import('recharts').then((recharts) => {
      const { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } = recharts;
      return function ChartComponent({ data }: { data: Array<{ week: string; line1: number; line2: number; line3: number }> }) {
        return (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
              <XAxis dataKey="week" stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} />
              <YAxis domain={[0, 100]} ticks={[50, 75, 100]} stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} />
              <Tooltip />
              <Line type="monotone" dataKey="line1" stroke="#ef4444" strokeWidth={2} dot={false} name="High Risk Mine A" />
              <Line type="monotone" dataKey="line2" stroke="#f97316" strokeWidth={2} dot={false} name="High Risk Mine D" />
              <Line type="monotone" dataKey="line3" stroke="#d97706" strokeWidth={2} dot={false} name="Medium Risk Mine C" />
            </LineChart>
          </ResponsiveContainer>
        );
      };
    }),
  {
    ssr: false,
    loading: () => <div className="h-44 w-full bg-slate-50 animate-pulse rounded-md"></div>,
  }
);

interface RiskTrendChartProps {
  data: Array<{ week: string; line1: number; line2: number; line3: number }>;
}

export const RiskTrendChart: React.FC<RiskTrendChartProps> = ({ data }) => {
  return (
    <div className="h-44 w-full">
      <DynamicChart data={data} />
    </div>
  );
};
