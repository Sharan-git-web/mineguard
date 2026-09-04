'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { apiService } from '@/lib/api';
import { Violation } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DataTable } from '@/components/common/DataTable';

export default function ViolationsPage() {
  const [violations, setViolations] = useState<Violation[]>([]);

  useEffect(() => {
    apiService.getViolations().then(setViolations);
  }, []);

  const columns = [
    { header: 'VIOLATION CODE', accessorKey: 'code' as const },
    { header: 'MINE NAME', accessorKey: 'mineName' as const },
    { header: 'TITLE', accessorKey: 'title' as const },
    { header: 'DGMS SECTION', accessorKey: 'dgmsSection' as const },
    {
      header: 'SEVERITY',
      cell: (row: Violation) => <StatusBadge status={row.severity} />
    },
    {
      header: 'STATUS',
      cell: (row: Violation) => <StatusBadge status={row.status} />
    },
    { header: 'DEADLINE', accessorKey: 'deadline' as const },
    {
      header: 'ACTION',
      cell: (row: Violation) => (
        <Link
          href={`/violations/${row.id}`}
          className="px-2.5 py-1 bg-slate-900 text-white rounded text-xs font-bold hover:bg-slate-800"
        >
          View Case File
        </Link>
      )
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            Statutory Violations Register
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            DGMS Enforced Breaches, Section 22 Cease Work Orders & Fine Assessments
          </p>
        </div>

        <button
          onClick={() => alert('Issue Section 22 Order Modal')}
          className="px-3.5 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">gavel</span>
          <span>Issue Section 22 Order</span>
        </button>
      </div>

      <DataTable
        columns={columns}
        data={violations}
        searchPlaceholder="Search violations by title, section, or mine..."
      />
    </div>
  );
}
