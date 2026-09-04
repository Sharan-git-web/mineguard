export type RiskLevel = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
export type MineStatus = 'OPERATIONAL' | 'SHUTDOWN_NOTICE' | 'SUSPENDED' | 'UNDER_REVIEW';

export interface Mine {
  id: string;
  code: string;
  name: string;
  region: string;
  type: 'Open Cast' | 'Underground' | 'Mixed';
  riskLevel: RiskLevel;
  status: MineStatus;
  productionTonnage: number;
  methaneGasPpm: number;
  strataStabilityScore: number;
  activeViolationsCount: number;
  pendingCapasCount: number;
  inspectorAssigned: string;
  coordinates: {
    lat: number;
    lng: number;
  };
  lastInspectedAt: string;
}

export type InspectionStatus = 'COMPLETED' | 'IN_PROGRESS' | 'SCHEDULED' | 'FLAGGED';

export interface Checkpoint {
  id: string;
  name: string;
  timestamp: string;
  lat: number;
  lng: number;
  verified: boolean;
  speed: string;
  elevation: string;
}

export interface Anomaly {
  id: string;
  section: string;
  category: string;
  severity: RiskLevel;
  description: string;
  status: 'UNRESOLVED' | 'UNDER_REVIEW' | 'CAPA_ASSIGNED' | 'RESOLVED';
  evidenceId?: string;
  recommendedCapa?: string;
  telemetrySpike?: string;
}

export interface Inspection {
  id: string;
  code: string;
  mineId: string;
  mineName: string;
  inspectorName: string;
  inspectorBadge: string;
  date: string;
  status: InspectionStatus;
  riskScore: number;
  methaneLevelPpm: number;
  strataStability: number;
  observationsCount: number;
  evidenceCount: number;
  anomalyCount: number;
  anomalies: Anomaly[];
  checkpoints: Checkpoint[];
}

export interface EvidenceItem {
  id: string;
  code: string;
  title: string;
  type: 'PHOTO' | 'THERMAL' | 'GAS_TELEMETRY' | 'DOCUMENT';
  timestamp: string;
  mineId: string;
  mineName: string;
  inspectionId: string;
  section: string;
  inspectorName: string;
  aiDetections: string[];
  gpsCoords: {
    lat: number;
    lng: number;
  };
  sha256Hash: string;
  imageUrl: string;
  status: string;
  fileSize: string;
}

export interface Violation {
  id: string;
  code: string;
  mineId: string;
  mineName: string;
  title: string;
  category: string;
  dgmsSection: string;
  severity: RiskLevel;
  status: 'ACTIVE' | 'SECTION_22_ORDER' | 'UNDER_APPEAL' | 'REMEDIATED';
  dateIssued: string;
  deadline: string;
  inspectorName: string;
  fineAmount: string;
  description: string;
  capaId?: string;
}

export type CapaStatus = 'OPEN' | 'IN_PROGRESS' | 'EVIDENCE_SUBMITTED' | 'UNDER_VERIFICATION' | 'CLOSED';

export interface CorrectiveAction {
  id: string;
  code: string;
  title: string;
  mineId: string;
  mineName: string;
  violationId: string;
  priority: 'URGENT' | 'HIGH' | 'NORMAL';
  status: CapaStatus;
  assignedTo: string;
  targetDate: string;
  slaHoursRemaining: number;
  description: string;
  evidenceCount: number;
  steps: {
    id: string;
    title: string;
    completed: boolean;
    completedAt?: string;
  }[];
}

export interface StatutoryAlert {
  id: string;
  type: 'GAS_SPIKE' | 'ROOF_MOVEMENT' | 'UNAUTHORIZED_ENTRY' | 'SECTION_22_ISSUED' | 'INSPECTION_OVERDUE';
  title: string;
  description: string;
  mineId: string;
  mineName: string;
  severity: RiskLevel;
  timestamp: string;
  isRead: boolean;
  actionRequired: string;
}

export interface StatutoryReport {
  id: string;
  code: string;
  title: string;
  type: 'FORM_IV' | 'DGMS_MONTHLY' | 'INCIDENT_SUMMARY' | 'ENVIRONMENTAL_AUDIT';
  mineId: string;
  mineName: string;
  period: string;
  generatedAt: string;
  fileSize: string;
  status: 'CERTIFIED' | 'PENDING_SIGNATURE' | 'ARCHIVED';
  downloadUrl: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'DGMS_CHIEF_OFFICER' | 'COMPLIANCE_INSPECTOR' | 'MINE_SAFETY_OFFICER' | 'SYSTEM_ADMIN';
  badgeNumber: string;
  region: string;
  status: 'ACTIVE' | 'SUSPENDED';
  mfaEnabled: boolean;
  avatarUrl: string;
  lastActive: string;
}
