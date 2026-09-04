'use client';

import React from 'react';
import dynamic from 'next/dynamic';

const DynamicAreaChart = dynamic(
  () =>
    import('recharts').then((recharts) => {
      const { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } = recharts;
      return function AreaChartComponent({ data }: { data: Array<{ time: string; ch4: number; strata: number }> }) {
        return (
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={data}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e2e1" />
              <XAxis dataKey="time" stroke="#555f6d" />
              <YAxis stroke="#555f6d" />
              <Tooltip />
              <Area type="monotone" dataKey="ch4" stroke="#ba1a1a" fill="#ffdad6" name="CH4 Gas (PPM)" />
              <Area type="monotone" dataKey="strata" stroke="#002367" fill="#dbe1ff" name="Strata Stability Index" />
            </AreaChart>
          </ResponsiveContainer>
        );
      };
    }),
  {
    ssr: false,
    loading: () => <div className="h-64 w-full bg-slate-50 animate-pulse rounded-md"></div>,
  }
);

interface MineTelemetryChartProps {
  data: Array<{ time: string; ch4: number; strata: number }>;
}

export const MineTelemetryChart: React.FC<MineTelemetryChartProps> = ({ data }) => {
  return (
    <div className="h-64 w-full">
      <DynamicAreaChart data={data} />
    </div>
  );
};
