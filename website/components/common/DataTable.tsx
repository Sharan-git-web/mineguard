'use client';

import React, { useState } from 'react';

interface Column<T> {
  header: string;
  accessorKey?: keyof T;
  cell?: (row: T) => React.ReactNode;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  searchPlaceholder?: string;
  onRowClick?: (row: T) => void;
}

export function DataTable<T extends Record<string, any>>({
  columns,
  data,
  searchPlaceholder = 'Search records...',
  onRowClick
}: DataTableProps<T>) {
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 8;

  const filteredData = data.filter(row => {
    if (!searchTerm) return true;
    return Object.values(row).some(val =>
      String(val).toLowerCase().includes(searchTerm.toLowerCase())
    );
  });

  const totalPages = Math.ceil(filteredData.length / pageSize) || 1;
  const paginatedData = filteredData.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  return (
    <div className="bg-white rounded-lg border border-slate-200/80 shadow-xs p-5 space-y-4">
      {/* Table Toolbar */}
      <div className="flex items-center justify-between gap-4">
        <div className="relative flex-1 max-w-sm">
          <span className="material-symbols-outlined absolute left-2.5 top-2 text-slate-400 text-sm">
            search
          </span>
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setCurrentPage(1);
            }}
            placeholder={searchPlaceholder}
            className="w-full h-8 pl-8 pr-3 bg-slate-50 rounded-md border border-slate-200 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-slate-300"
          />
        </div>

        <div className="text-xs text-slate-500 font-medium">
          Showing {filteredData.length} records
        </div>
      </div>

      {/* Main Table */}
      <div className="overflow-x-auto custom-scrollbar">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-100 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
              {columns.map((col, idx) => (
                <th key={idx} className="pb-3 px-3 font-semibold">
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-xs text-slate-700 font-medium">
            {paginatedData.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="text-center py-8 text-slate-400 italic">
                  No matching records found.
                </td>
              </tr>
            ) : (
              paginatedData.map((row, rowIdx) => (
                <tr
                  key={rowIdx}
                  onClick={() => onRowClick && onRowClick(row)}
                  className={`hover:bg-slate-50/80 transition-colors ${
                    onRowClick ? 'cursor-pointer' : ''
                  }`}
                >
                  {columns.map((col, colIdx) => (
                    <td key={colIdx} className="py-3 px-3">
                      {col.cell ? col.cell(row) : (col.accessorKey ? String(row[col.accessorKey]) : '')}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Bar */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between pt-3 border-t border-slate-100 text-xs text-slate-500 font-medium">
          <span>Page {currentPage} of {totalPages}</span>
          <div className="flex items-center gap-1">
            <button
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(p => p - 1)}
              className="px-2.5 py-1 rounded bg-slate-100 hover:bg-slate-200 disabled:opacity-50 text-slate-700 font-semibold"
            >
              Previous
            </button>
            <button
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(p => p + 1)}
              className="px-2.5 py-1 rounded bg-slate-100 hover:bg-slate-200 disabled:opacity-50 text-slate-700 font-semibold"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
