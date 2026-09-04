'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { apiService } from '@/lib/api';
import { EvidenceItem } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DataTable } from '@/components/common/DataTable';

export default function EvidenceVaultPage() {
  const [evidenceList, setEvidenceList] = useState<EvidenceItem[]>([]);

  useEffect(() => {
    apiService.getEvidenceList().then(setEvidenceList);
  }, []);

  const columns = [
    { header: 'EVIDENCE CODE', accessorKey: 'code' as const },
    { header: 'TITLE / DESCRIPTION', accessorKey: 'title' as const },
    { header: 'MINE', accessorKey: 'mineName' as const },
    { header: 'SECTION', accessorKey: 'section' as const },
    {
      header: 'TYPE',
      cell: (row: EvidenceItem) => <StatusBadge status={row.type} />
    },
    { header: 'FILE SIZE', accessorKey: 'fileSize' as const },
    {
      header: 'ACTION',
      cell: (row: EvidenceItem) => (
        <Link
          href={`/inspections/${row.inspectionId}/evidence/${row.id}`}
          className="px-2.5 py-1 bg-slate-900 text-white rounded text-xs font-bold hover:bg-slate-800"
        >
          Open Asset
        </Link>
      )
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">description</span>
            Statutory Documents & Evidence Vault
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Cryptographically Anchored Photogrammetry, Thermal Telemetry & DGMS Legal Notices Repository
          </p>
        </div>

        <button
          onClick={() => alert('Upload Signed Evidence Modal')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">upload_file</span>
          <span>Upload Signed Evidence</span>
        </button>
      </div>

      <DataTable
        columns={columns}
        data={evidenceList}
        searchPlaceholder="Search evidence by title, code, or section..."
      />
    </div>
  );
}
