'use client';

import React, { useEffect, useState } from 'react';
import { apiService } from '@/lib/api';
import { User } from '@/types';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DataTable } from '@/components/common/DataTable';

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
    apiService.getUsers().then(setUsers);
  }, []);

  const columns = [
    {
      header: 'OFFICER / USER',
      cell: (row: User) => (
        <div className="flex items-center gap-2.5">
          <img src={row.avatarUrl} alt={row.name} className="w-7 h-7 rounded-full border border-slate-200 object-cover" />
          <div>
            <div className="font-bold text-slate-900">{row.name}</div>
            <div className="text-[11px] text-slate-400 font-medium">{row.email}</div>
          </div>
        </div>
      )
    },
    { header: 'BADGE NUMBER', accessorKey: 'badgeNumber' as const },
    { header: 'ROLE', accessorKey: 'role' as const },
    { header: 'JURISDICTION REGION', accessorKey: 'region' as const },
    {
      header: 'STATUS',
      cell: (row: User) => <StatusBadge status={row.status} />
    },
    {
      header: 'MFA STATUS',
      cell: (row: User) => (
        <span className={`px-2 py-0.5 rounded text-[11px] font-bold ${row.mfaEnabled ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
          {row.mfaEnabled ? 'MFA ACTIVE' : 'NO MFA'}
        </span>
      )
    },
    {
      header: 'ACTION',
      cell: (row: User) => (
        <button
          onClick={() => alert(`Editing roles for ${row.name}`)}
          className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded text-xs font-bold"
        >
          Edit Permissions
        </button>
      )
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <span className="material-symbols-outlined text-slate-900 text-2xl">group</span>
            Users & Roles Management
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            DGMS Officers, Compliance Inspectors, Mine Safety Personnel & RBAC Privileges
          </p>
        </div>

        <button
          onClick={() => alert('Provision Officer Account Modal')}
          className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-bold flex items-center gap-1.5 shadow-xs self-start md:self-auto"
        >
          <span className="material-symbols-outlined text-base">person_add</span>
          <span>Provision Officer</span>
        </button>
      </div>

      <DataTable
        columns={columns}
        data={users}
        searchPlaceholder="Search officers by name, email, or badge..."
      />
    </div>
  );
}
