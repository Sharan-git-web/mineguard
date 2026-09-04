import {
  mockMines,
  mockInspections,
  mockEvidenceItems,
  mockViolations,
  mockCapas,
  mockAlerts,
  mockReports,
  mockUsers
} from '@/lib/mock/data';
import { Mine, Inspection, EvidenceItem, Violation, CorrectiveAction, StatutoryAlert, StatutoryReport, User } from '@/types';

export const apiService = {
  // Mines
  async getMines(): Promise<Mine[]> {
    return Promise.resolve([...mockMines]);
  },
  async getMineById(id: string): Promise<Mine | undefined> {
    return Promise.resolve(mockMines.find(m => m.id === id || m.code === id) || mockMines[0]);
  },

  // Inspections
  async getInspections(): Promise<Inspection[]> {
    return Promise.resolve([...mockInspections]);
  },
  async getInspectionById(id: string): Promise<Inspection | undefined> {
    return Promise.resolve(mockInspections.find(i => i.id === id || i.code === id) || mockInspections[0]);
  },

  // Evidence
  async getEvidenceList(): Promise<EvidenceItem[]> {
    return Promise.resolve([...mockEvidenceItems]);
  },
  async getEvidenceById(id: string): Promise<EvidenceItem | undefined> {
    return Promise.resolve(mockEvidenceItems.find(e => e.id === id || e.code === id) || mockEvidenceItems[0]);
  },

  // Violations
  async getViolations(): Promise<Violation[]> {
    return Promise.resolve([...mockViolations]);
  },
  async getViolationById(id: string): Promise<Violation | undefined> {
    return Promise.resolve(mockViolations.find(v => v.id === id || v.code === id) || mockViolations[0]);
  },

  // CAPA
  async getCapas(): Promise<CorrectiveAction[]> {
    return Promise.resolve([...mockCapas]);
  },
  async getCapaById(id: string): Promise<CorrectiveAction | undefined> {
    return Promise.resolve(mockCapas.find(c => c.id === id || c.code === id) || mockCapas[0]);
  },

  // Alerts
  async getAlerts(): Promise<StatutoryAlert[]> {
    return Promise.resolve([...mockAlerts]);
  },

  // Reports
  async getReports(): Promise<StatutoryReport[]> {
    return Promise.resolve([...mockReports]);
  },

  // Users
  async getUsers(): Promise<User[]> {
    return Promise.resolve([...mockUsers]);
  }
};
