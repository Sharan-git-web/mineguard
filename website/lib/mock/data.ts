import { Mine, Inspection, EvidenceItem, Violation, CorrectiveAction, StatutoryAlert, StatutoryReport, User } from '@/types';

export const mockMines: Mine[] = [
  {
    id: 'mine-a',
    code: 'MINE-RAJ-01',
    name: 'Rajrappa Open Cast Mine A',
    region: 'Eastern Coalfields Zone 4',
    type: 'Open Cast',
    riskLevel: 'CRITICAL',
    status: 'OPERATIONAL',
    productionTonnage: 14200,
    methaneGasPpm: 1250,
    strataStabilityScore: 68,
    activeViolationsCount: 7,
    pendingCapasCount: 4,
    inspectorAssigned: 'Insp. Rajesh Kumar (DGMS-8821)',
    coordinates: { lat: 23.6289, lng: 85.7144 },
    lastInspectedAt: '2026-09-03 14:30 IST'
  },
  {
    id: 'mine-b',
    code: 'MINE-JHA-02',
    name: 'Jharia Underground Colliery B',
    region: 'Dhanbad Mining Circle',
    type: 'Underground',
    riskLevel: 'HIGH',
    status: 'SHUTDOWN_NOTICE',
    productionTonnage: 8900,
    methaneGasPpm: 1840,
    strataStabilityScore: 54,
    activeViolationsCount: 12,
    pendingCapasCount: 5,
    inspectorAssigned: 'Insp. Anita Sharma (DGMS-4412)',
    coordinates: { lat: 23.7516, lng: 86.4172 },
    lastInspectedAt: '2026-09-02 11:15 IST'
  },
  {
    id: 'mine-c',
    code: 'MINE-KOR-03',
    name: 'Korba North Pit',
    region: 'Central Coalfields Belt',
    type: 'Open Cast',
    riskLevel: 'MEDIUM',
    status: 'OPERATIONAL',
    productionTonnage: 22400,
    methaneGasPpm: 420,
    strataStabilityScore: 88,
    activeViolationsCount: 2,
    pendingCapasCount: 1,
    inspectorAssigned: 'Insp. Vikram Singh (DGMS-9932)',
    coordinates: { lat: 22.3595, lng: 82.7501 },
    lastInspectedAt: '2026-09-04 09:00 IST'
  },
  {
    id: 'mine-d',
    code: 'MINE-SIN-04',
    name: 'Singrauli Deep Shaft',
    region: 'Northern Coalfields Division',
    type: 'Underground',
    riskLevel: 'LOW',
    status: 'OPERATIONAL',
    productionTonnage: 19800,
    methaneGasPpm: 310,
    strataStabilityScore: 94,
    activeViolationsCount: 0,
    pendingCapasCount: 0,
    inspectorAssigned: 'Insp. Priya Patel (DGMS-1109)',
    coordinates: { lat: 24.2001, lng: 82.6653 },
    lastInspectedAt: '2026-09-01 16:45 IST'
  },
  {
    id: 'mine-e',
    code: 'MINE-TAL-05',
    name: 'Talcher South Block',
    region: 'Mahanadi Coalfields Zone',
    type: 'Mixed',
    riskLevel: 'HIGH',
    status: 'UNDER_REVIEW',
    productionTonnage: 16500,
    methaneGasPpm: 1100,
    strataStabilityScore: 71,
    activeViolationsCount: 5,
    pendingCapasCount: 3,
    inspectorAssigned: 'Insp. Suresh Nair (DGMS-7734)',
    coordinates: { lat: 20.9500, lng: 85.2333 },
    lastInspectedAt: '2026-09-03 10:20 IST'
  }
];

export const mockInspections: Inspection[] = [
  {
    id: 'INS-0098',
    code: 'INS-2026-0098',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    inspectorName: 'Insp. Rajesh Kumar',
    inspectorBadge: 'DGMS-8821',
    date: '2026-09-04 08:30 IST',
    status: 'FLAGGED',
    riskScore: 84,
    methaneLevelPpm: 1250,
    strataStability: 68,
    observationsCount: 14,
    evidenceCount: 11,
    anomalyCount: 3,
    anomalies: [
      {
        id: 'ANOM-981',
        section: 'Section B — Roof Bolting Grid 44',
        category: 'Strata Instability',
        severity: 'CRITICAL',
        description: 'Load-cell sensor #LC-44 showing 42% excessive displacement beyond DGMS Section 22 safety limits. Methane gas accumulation observed at 1,250 PPM.',
        status: 'CAPA_ASSIGNED',
        evidenceId: 'EVID-04',
        recommendedCapa: 'CAPA-881',
        telemetrySpike: '1,250 PPM (Normal < 500 PPM)'
      },
      {
        id: 'ANOM-982',
        section: 'Section C — Haulage Road Ramp 2',
        category: 'Ventilation Bypass',
        severity: 'HIGH',
        description: 'Auxiliary ventilation fan ducting severed, causing stagnant pockets of coal dust.',
        status: 'UNRESOLVED',
        evidenceId: 'EVID-06',
        recommendedCapa: 'CAPA-884',
        telemetrySpike: 'Air Flow 1.2 m/s (Min 3.0 m/s)'
      }
    ],
    checkpoints: [
      { id: 'chk-1', name: 'Pit Entrance Gate 1', timestamp: '08:35:12', lat: 23.6291, lng: 85.7140, verified: true, speed: '4.2 km/h', elevation: '240m' },
      { id: 'chk-2', name: 'Underground Incline Ramp 3', timestamp: '08:52:40', lat: 23.6285, lng: 85.7148, verified: true, speed: '2.1 km/h', elevation: '180m' },
      { id: 'chk-3', name: 'Section B Roof Bolting Grid 44', timestamp: '09:15:04', lat: 23.6278, lng: 85.7155, verified: true, speed: '0.0 km/h', elevation: '120m' },
      { id: 'chk-4', name: 'Main Haulage Junction West', timestamp: '10:04:18', lat: 23.6269, lng: 85.7162, verified: true, speed: '3.5 km/h', elevation: '115m' }
    ]
  },
  {
    id: 'INS-0097',
    code: 'INS-2026-0097',
    mineId: 'mine-b',
    mineName: 'Jharia Underground Colliery B',
    inspectorName: 'Insp. Anita Sharma',
    inspectorBadge: 'DGMS-4412',
    date: '2026-09-02 10:00 IST',
    status: 'COMPLETED',
    riskScore: 92,
    methaneLevelPpm: 1840,
    strataStability: 54,
    observationsCount: 22,
    evidenceCount: 18,
    anomalyCount: 5,
    anomalies: [],
    checkpoints: []
  }
];

export const mockEvidenceItems: EvidenceItem[] = [
  {
    id: 'EVID-04',
    code: 'EVID-04/11',
    title: 'Section B Roof Bolting Fractures & Displacement',
    type: 'PHOTO',
    timestamp: '2026-09-04 09:16:22 IST',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    inspectionId: 'INS-0098',
    section: 'Section B — Strata Grid 44',
    inspectorName: 'Insp. Rajesh Kumar (DGMS-8821)',
    aiDetections: ['Strata Crack (Severity 0.89)', 'Shear Bolt Deformation', 'Water Seepage Flag'],
    gpsCoords: { lat: 23.6278, lng: 85.7155 },
    sha256Hash: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    imageUrl: 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=1200&q=80',
    status: 'VERIFIED_AI_CONFIRMED',
    fileSize: '4.8 MB'
  },
  {
    id: 'EVID-06',
    code: 'EVID-06/11',
    title: 'Thermal Camera Leakage Detection Ramp 2',
    type: 'THERMAL',
    timestamp: '2026-09-04 09:42:10 IST',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    inspectionId: 'INS-0098',
    section: 'Section C — Ramp 2',
    inspectorName: 'Insp. Rajesh Kumar (DGMS-8821)',
    aiDetections: ['Thermal Anomaly +42°C', 'Friction Overheat Risk'],
    gpsCoords: { lat: 23.6269, lng: 85.7162 },
    sha256Hash: 'a7c390218bfa982348574c829e018a7d65418b762514ab8179268392174c8712',
    imageUrl: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=1200&q=80',
    status: 'VERIFIED_AI_CONFIRMED',
    fileSize: '3.2 MB'
  }
];

export const mockViolations: Violation[] = [
  {
    id: 'VIO-2026-901',
    code: 'VIO-2026-901',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    title: 'Roof Support Failure & Strata Instability',
    category: 'Underground Strata Safety',
    dgmsSection: 'DGMS Section 22(1) Order',
    severity: 'CRITICAL',
    status: 'SECTION_22_ORDER',
    dateIssued: '2026-09-04',
    deadline: '2026-09-07 (72 Hours SLA)',
    inspectorName: 'Insp. Rajesh Kumar',
    fineAmount: '₹ 5,00,000 + Statutory Cease Work',
    description: 'Catastrophic shear fracture in roof bolting grid 44 underground seam. Methane levels spiked to 1,250 PPM without mandatory statutory notification.',
    capaId: 'CAPA-881'
  },
  {
    id: 'VIO-2026-889',
    code: 'VIO-2026-889',
    mineId: 'mine-b',
    mineName: 'Jharia Underground Colliery B',
    title: 'Methane Gas Accumulation Exceeding Permissible Limits',
    category: 'Mine Atmosphere & Gas Safety',
    dgmsSection: 'CMR Regulation 153',
    severity: 'CRITICAL',
    status: 'ACTIVE',
    dateIssued: '2026-09-02',
    deadline: '2026-09-06',
    inspectorName: 'Insp. Anita Sharma',
    fineAmount: '₹ 2,50,000',
    description: 'CH4 levels surpassed 1.5% v/v in working gallery 9 without automatic power trip activation.',
    capaId: 'CAPA-875'
  }
];

export const mockCapas: CorrectiveAction[] = [
  {
    id: 'CAPA-881',
    code: 'CAPA-881',
    title: 'Immediate Strata Reinforcement & Roof Bolt Replacement',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    violationId: 'VIO-2026-901',
    priority: 'URGENT',
    status: 'IN_PROGRESS',
    assignedTo: 'Er. Somnath Mukherjee (Chief Mine Engineer)',
    targetDate: '2026-09-07 18:00 IST',
    slaHoursRemaining: 38,
    description: 'Install double-strand resin-anchored cable bolts along Grid 44-48, evacuate seam section, and re-commission gas telemetry sensor #LC-44.',
    evidenceCount: 3,
    steps: [
      { id: 's1', title: 'Evacuate Seam 44 working face and declare 50m cordon zone', completed: true, completedAt: '2026-09-04 11:00' },
      { id: 's2', title: 'Deploy hydraulic roof jack supports and temporary timber props', completed: true, completedAt: '2026-09-04 14:30' },
      { id: 's3', title: 'Drill 24mm resin bolt channels and anchor 6m steel cables', completed: false },
      { id: 's4', title: 'Perform load-cell calibration test and submit DGMS verification proof', completed: false }
    ]
  }
];

export const mockAlerts: StatutoryAlert[] = [
  {
    id: 'ALT-101',
    type: 'GAS_SPIKE',
    title: 'Methane CH4 Telemetry Spike (1,250 PPM)',
    description: 'Sensor CH4-B44 at Rajrappa Mine A exceeded critical threshold (1,000 PPM Limit). Automatic alarm tripped.',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    severity: 'CRITICAL',
    timestamp: '12 mins ago',
    isRead: false,
    actionRequired: 'Trigger Section 22 Evacuation Protocol'
  },
  {
    id: 'ALT-102',
    type: 'ROOF_MOVEMENT',
    title: 'Strata Convergence Warning Grid 44',
    description: 'Extensometer #EXT-09 registered 14mm displacement in 2 hours.',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    severity: 'CRITICAL',
    timestamp: '35 mins ago',
    isRead: false,
    actionRequired: 'Inspect Roof Supports Immediately'
  },
  {
    id: 'ALT-103',
    type: 'INSPECTION_OVERDUE',
    title: 'Statutory Inspection Overdue — Jharia Colliery',
    description: 'Quarterly ventilation audit missed mandatory 30-day deadline.',
    mineId: 'mine-b',
    mineName: 'Jharia Underground Colliery B',
    severity: 'HIGH',
    timestamp: '2 hours ago',
    isRead: true,
    actionRequired: 'Dispatch Officer'
  }
];

export const mockReports: StatutoryReport[] = [
  {
    id: 'REP-01',
    code: 'REP-FORM-IV-2026-08',
    title: 'Form IV — Statutory Monthly Mine Safety & Compliance Dossier',
    type: 'FORM_IV',
    mineId: 'mine-a',
    mineName: 'Rajrappa Open Cast Mine A',
    period: 'August 2026',
    generatedAt: '2026-09-01',
    fileSize: '14.2 MB',
    status: 'CERTIFIED',
    downloadUrl: '#'
  },
  {
    id: 'REP-02',
    code: 'REP-DGMS-Q3-VENT',
    title: 'DGMS Statutory Ventilation & Strata Audit Dossier',
    type: 'DGMS_MONTHLY',
    mineId: 'mine-b',
    mineName: 'Jharia Underground Colliery B',
    period: 'Q3 2026',
    generatedAt: '2026-08-28',
    fileSize: '28.6 MB',
    status: 'PENDING_SIGNATURE',
    downloadUrl: '#'
  }
];

export const mockUsers: User[] = [
  {
    id: 'usr-1',
    name: 'Dr. Alok Verma',
    email: 'a.verma@dgms.gov.in',
    role: 'DGMS_CHIEF_OFFICER',
    badgeNumber: 'DGMS-CHIEF-01',
    region: 'National Headquarters',
    status: 'ACTIVE',
    mfaEnabled: true,
    avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80',
    lastActive: 'Active Now'
  },
  {
    id: 'usr-2',
    name: 'Insp. Rajesh Kumar',
    email: 'r.kumar@dgms.gov.in',
    role: 'COMPLIANCE_INSPECTOR',
    badgeNumber: 'DGMS-8821',
    region: 'Eastern Coalfields Zone 4',
    status: 'ACTIVE',
    mfaEnabled: true,
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80',
    lastActive: '10m ago'
  }
];
