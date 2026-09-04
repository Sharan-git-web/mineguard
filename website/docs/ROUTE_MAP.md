# Smart Mine Governance & Compliance Monitoring System — Route Map

This document establishes the official routing structure mapping every Stitch-generated HTML screen to its corresponding Next.js App Router path.

| # | Stitch Design Page / Folder | Next.js App Router Path | Description & Key Components |
|---|---|---|---|
| 1 | `smart_mine_governance_officer_secure_login_mfa_authentication` | `/login` | Secure Officer Login with Multi-Factor Authentication (MFA), biometric/smartcard options, and compliance notice. |
| 2 | `smart_mine_governance_officer_web_command_portal` | `/dashboard` | Executive Command Dashboard: Regional Coal Command Workspace, High-Risk Mines, Active Inspections, Critical Violations, Map Overview, Statutory Alerts. |
| 3 | `smart_mine_governance_all_mines_directory` | `/mines` | Complete Directory of all monitored coal mines with risk filters, operational status badges, production metrics, search, and region tags. |
| 4 | `smart_mine_governance_mine_profile_mine_a` | `/mines/[mineId]` | Mine A (Rajrappa Open Cast) Profile: Comprehensive risk metrics, underground strata telemetry, active violations, historical audit logs, GIS coordinates. |
| 5 | `smart_mine_governance_inspections_registry_route_tracker` | `/inspections` | Registry of all statutory inspections, inspector tracking, route verification status, scheduling, and risk severity metrics. |
| 6 | `smart_mine_governance_inspection_details_ins_0098` | `/inspections/[inspectionId]` | Detailed Inspection Dossier (`INS-0098`): Inspector details, GPS verification, strata gas telemetry, evidence gallery, anomaly flags. |
| 7 | `smart_mine_governance_inspection_anomaly_ins_0098_section_b` | `/inspections/[inspectionId]/anomalies/[anomalyId]` | Deep-dive Anomaly Inspection (`Section B Roof Bolting`): Methane gas detection, load-cell structural failure alerts, high-resolution evidence. |
| 8 | `smart_mine_governance_gps_route_investigation_ins_0098` | `/inspections/[inspectionId]/route` | GPS Route Tracker & Inspector Breadcrumb Investigation: Real-time GPS path, geofenced checkpoint validation, speed/altitude logs. |
| 9 | `smart_mine_governance_evidence_viewer_ins_0098_evidence_04_of_11` | `/inspections/[inspectionId]/evidence/[evidenceId]` | High-Resolution Evidence Viewer (`EVID-04/11`): Image inspection, metadata overlay, AI defect identification, EXIF GPS verification, cryptographic hash. |
| 10 | `smart_mine_governance_violations_register` | `/violations` | Statutory Violations Register: Categorized regulatory breaches, Section 22 notices, risk severity levels, deadline countdowns, filter bar. |
| 11 | `smart_mine_governance_violation_details_vio_2026_901_roof_support_failure` | `/violations/[violationId]` | Violation Case File (`VIO-2026-901`): Roof Support Failure, DGMS Section 22(1) Order, AI structural assessment, timeline, assigned CAPA. |
| 12 | `smart_mine_governance_corrective_actions_workflow` | `/corrective-actions` | CAPA Workflow Board & List: Corrective and Preventive Action management across lifecycle states (Open, In Progress, Submitted, Verification, Closed). |
| 13 | `smart_mine_governance_corrective_action_details_capa_881_repair_roof_support` | `/corrective-actions/[capaId]` | CAPA Case File (`CAPA-881`): Strata Reinforcement, evidence proof, verification sign-off workflow, SLA countdown, inspector reassignment. |
| 14 | `smart_mine_governance_centralized_statutory_alerts_event_queue` | `/alerts` | Centralized Statutory Alerts & Event Queue: Real-time telemetry alerts, Gas sensor spikes, Unauthorized entry, Immediate action triggers. |
| 15 | `smart_mine_governance_ai_predictive_safety_risk_insights` | `/ai-insights` | AI Predictive Risk Engine: Machine learning strata movement predictions, hazard forecasting models, risk heatmaps, preventative advisories. |
| 16 | `smart_mine_governance_gis_geospatial_command_map` | `/gis` | Interactive GIS Geospatial Command Map: Live mine boundary layers, sensor overlays, inspection pin clusters, high-risk zone geofencing. |
| 17 | `smart_mine_governance_statutory_documents_evidence_vault` | `/evidence-vault` | Central Statutory Evidence & Document Vault: Cryptographically signed inspection dossiers, DGMS forms, environmental approvals, search & tags. |
| 18 | `smart_mine_governance_statutory_reports_dossier_generation` | `/reports` | Statutory Report Generator & Dossier Export: Form IV, DGMS Monthly Compliance, Incident Summaries, PDF/Excel export preview. |
| 19 | `smart_mine_governance_statutory_audit_trail_cryptographic_ledger` | `/audit-trail` | Immutable Audit Ledger: SHA-256 block chain verification of inspection logs, officer signatures, tamper-evident timeline. |
| 20 | `smart_mine_governance_users_roles_management` | `/users` | User & Role Management: DGMS Inspectors, Mine Managers, Compliance Officers, RBAC permissions, MFA enforcement status. |
| 21 | `smart_mine_governance_system_settings_regulatory_configuration` | `/settings` | System Settings & Compliance Configuration: Gas threshold limits, SLA alert timelines, API integration keys, notification channels. |

---

## Shared Layout & Components Architecture

- **Main Navigation Sidebar**: Persistent left sidebar with 13 main section tabs, regulatory shutdown action CTA, authority branding, and badge counts.
- **Global Header**: Breadcrumbs, quick search, active authority badge, notification bell, live system status, and user profile menu.
- **Design Tokens**: Material 3 / Custom Tailwind palette matching `smart_mine_governance/DESIGN.md` (`primary: #0a1422`, `surface: #fcf9f8`, `surface-container`, `error: #ba1a1a`, typography Inter).
