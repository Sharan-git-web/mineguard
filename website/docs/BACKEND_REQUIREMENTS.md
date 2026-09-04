# Smart Mine Governance — Website Backend Requirements

Code-level analysis of `coal-mine-website/` (Next.js 14 App Router, TypeScript, Tailwind) to determine what `coal-mine-backend/` (FastAPI + Supabase) must provide. No backend, database, or AI/CV work has been done — this is analysis only, derived from the actual code in `app/`, `components/`, `lib/`, and `types/index.ts`, cross-referenced against `../app/INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md` (the Kotlin Inspector App's own backend analysis) so both frontends are designed against **one** backend rather than two.

Every page currently sources data from `lib/api/index.ts` (`apiService`), a stub that resolves in-memory arrays from `lib/mock/data.ts` after a fake `Promise.resolve`. The method signatures on `apiService` are already shaped like a real API client (`getMines()`, `getMineById(id)`, `getInspectionById(id)`, etc.) — this is the seam where the real backend plugs in with minimal frontend change. Three pages (`dashboard`, `ai-insights`, `audit-trail`) do **not** call `apiService` at all; their data is inline literals in the component, which is called out explicitly below since it means those pages currently have zero backend contract.

---

## 1. Complete Route Inventory

21 routes across 5 nav groups (Overview, Monitoring, Intelligence, Records, Administration), defined in `components/layout/Sidebar.tsx`. Root `/` (`app/page.tsx`) redirects to `/dashboard`.

| # | Route | Page (file) | Purpose | Data source today | Dynamic params |
|---|---|---|---|---|---|
| 1 | `/login` | `app/login/page.tsx` | Officer credential + MFA (TOTP) login, 2-step form | Local component state only; `handleSubmit` just advances a step and on final submit does `router.push('/dashboard')` — no auth call at all | — |
| 2 | `/dashboard` | `app/dashboard/page.tsx` | Executive KPI overview: 6 stat cards, mine risk table, risk trend chart, recent alerts, quick actions | **Inline literals** (`minesData`, `alertsData`, `riskTrendData`), not `apiService` | — |
| 3 | `/mines` | `app/mines/page.tsx` | Directory of all mines, grid/table toggle, risk + type filters, search | `apiService.getMines()` | — |
| 4 | `/mines/[mineId]` | `app/mines/[mineId]/page.tsx` | Mine profile: telemetry chart, GIS coords, tabs (Overview / Violations / Inspections) | `apiService.getMineById`, `getInspections()` (unfiltered), `getViolations()` (unfiltered) | `mineId` |
| 5 | `/inspections` | `app/inspections/page.tsx` | Registry of all inspections, sortable/searchable table | `apiService.getInspections()` | — |
| 6 | `/inspections/[inspectionId]` | `.../[inspectionId]/page.tsx` | Inspection dossier: risk/CH4/strata/anomaly stat cards, flagged anomalies list | `apiService.getInspectionById` | `inspectionId` |
| 7 | `/inspections/[inspectionId]/anomalies/[anomalyId]` | `.../anomalies/[anomalyId]/page.tsx` | Deep-dive on one anomaly: load-cell + gas telemetry, inspector note, linked evidence thumbnail | **No data fetch at all** — every field (severity, section name, telemetry values, evidence image, GPS, AI detection) is hardcoded JSX; `anomalyId`/`inspectionId` are read from the URL but only used to build links, never to fetch | `inspectionId`, `anomalyId` |
| 8 | `/inspections/[inspectionId]/route` | `.../route/page.tsx` | GPS breadcrumb trail + statutory checkpoint log | `apiService.getInspectionById` (uses `.checkpoints`) | `inspectionId` |
| 9 | `/inspections/[inspectionId]/evidence/[evidenceId]` | `.../evidence/[evidenceId]/page.tsx` | High-res evidence viewer: zoom/contrast controls, AI bounding-box overlay, EXIF/GPS/hash metadata panel | `apiService.getEvidenceById` | `inspectionId` (unused in fetch — evidence is fetched by `evidenceId` only), `evidenceId` |
| 10 | `/violations` | `app/violations/page.tsx` | Statutory violations register, searchable table | `apiService.getViolations()` | — |
| 11 | `/violations/[violationId]` | `.../[violationId]/page.tsx` | Violation case file: enforcement order text, fine, deadline, linked CAPA | `apiService.getViolationById` | `violationId` |
| 12 | `/corrective-actions` | `app/corrective-actions/page.tsx` | CAPA kanban board, 5 fixed status columns | `apiService.getCapas()` | — |
| 13 | `/corrective-actions/[capaId]` | `.../[capaId]/page.tsx` | CAPA case file: remediation checklist, SLA countdown, sign-off button | `apiService.getCapaById` | `capaId` |
| 14 | `/alerts` | `app/alerts/page.tsx` | Real-time alert/event queue, per-alert "Execute Action" | `apiService.getAlerts()` | — |
| 15 | `/ai-insights` | `app/ai-insights/page.tsx` | ML hazard predictions list + model precision stat cards | **Inline literal** (`aiPredictions`), not `apiService` | — |
| 16 | `/gis` | `app/gis/page.tsx` | Full-screen interactive geospatial map | `MapContainer` component, all data hardcoded inside it | — |
| 17 | `/evidence-vault` | `app/evidence-vault/page.tsx` | Searchable table of all evidence assets across mines | `apiService.getEvidenceList()` | — |
| 18 | `/reports` | `app/reports/page.tsx` | Statutory report cards (Form IV, DGMS Monthly, etc.), download buttons | `apiService.getReports()` | — |
| 19 | `/audit-trail` | `app/audit-trail/page.tsx` | Immutable ledger table (tx hash, block #, event, actor, timestamp) | **Inline literal** (`ledgerEvents`), not `apiService` | — |
| 20 | `/users` | `app/users/page.tsx` | User/role management table, MFA status, "Edit Permissions" | `apiService.getUsers()` | — |
| 21 | `/settings` | `app/settings/page.tsx` | Gas/SLA threshold config, FastAPI endpoint URL, MFA-enforcement toggle | Local component state only (`useState` defaults), no fetch, no save call | — |

**Forms/inputs found:** login (2-step credential+MFA), mines filter bar (risk/type selects + search), settings (4 fields: gas threshold number, SLA hours number, FastAPI URL text, MFA-enforced checkbox), DataTable search box (client-side only, generic across 6 pages).

**Modals found:** none are real modals — `alert('...')` browser dialogs stand in for: "New Mine Registration", "Schedule Inspection", "Issue Section 22 Order" (×2, mines + violations), "Initiate CAPA Plan", "Upload Signed Evidence", "Provision Officer", header notification drawer and user-menu dropdown (these two are real, just local `useState`), and the one genuine modal — the Sidebar's "Emergency Statutory Shutdown Directive" confirm dialog.

**Charts:** `RiskTrendChart` (dashboard, 3-line trend over 6 weeks), `MineTelemetryChart` (mine profile, CH4 + strata dual-axis over 24h) — both Recharts, both fed hardcoded arrays by their parent page today.

**Maps:** `MapContainer` (used in `/mines/[mineId]`, `/inspections/[inspectionId]/route`, `/gis`) — a custom SVG/div mock, not a real map SDK. All 5 mine pin positions/coordinates and the route-trail path are hardcoded inside the component itself, ignoring its `selectedMineId` prop for anything but which pin shows a tooltip by default.

---

## 2. Component Analysis

| Component | File | Data source |
|---|---|---|
| `Sidebar` | `components/layout/Sidebar.tsx` | Nav structure hardcoded (static — this is app config, not backend data). Shutdown modal has no backend call — `onClick` just closes itself after an `alert`. |
| `Header` | `components/layout/Header.tsx` | Imports `mockAlerts` **directly from `lib/mock/data`** (not via `apiService`) to compute unread count and populate the notification drawer. Officer identity ("Dr. Alok Verma", "Regional Officer") is hardcoded, not tied to any session. |
| `MainLayout` | `components/layout/MainLayout.tsx` | Pure composition (Sidebar + Header + children), no data. |
| `DataTable` | `components/common/DataTable.tsx` | Generic — takes `data`/`columns` as props, does client-side search + pagination (page size 8) in-memory. No server-side pagination/sort/filter today — this is a design decision the backend API shape needs to account for (see §7, §17). |
| `StatCard` | `components/common/StatCard.tsx` | Pure props (`title`, `value`, `subtitle`, `icon`, color) — no data source, fully reusable. |
| `StatusBadge` | `components/common/StatusBadge.tsx` | Pure function of a `status` string prop — maps ~20 known enum values (across Mine/Inspection/Violation/CAPA/Evidence/Alert/Report statuses) to one of 5 color classes via string matching. **Any new backend status value not in this list silently falls through to the default gray/"status as-is" style** — worth knowing when defining backend enums. |
| `MineTelemetryChart`, `RiskTrendChart` | `components/charts/*.tsx` | Pure props, Recharts wrappers — no data source. |
| `MapContainer` | `components/gis/MapContainer.tsx` | **All 5 mines' positions, names, risk, and CH4 values are hardcoded inside the component** (`minesOnMap` array), ignoring real `Mine` data entirely. `showRouteTrail` draws a fixed hardcoded SVG path, not real GPS points. |

**Hardcoded/mock data inventory (file → what → replacement entity):**

| File | Variable | Represents | Backend entity |
|---|---|---|---|
| `lib/mock/data.ts` | `mockMines`, `mockInspections`, `mockEvidenceItems`, `mockViolations`, `mockCapas`, `mockAlerts`, `mockReports`, `mockUsers` | Seed data for every domain entity | Mine, Inspection, Evidence, Violation, CorrectiveAction, Alert, Report, User |
| `app/dashboard/page.tsx` | `minesData`, `alertsData`, `riskTrendData` | Dashboard table rows, alert feed, 6-week trend | Aggregated from Mine/Alert + a time-series risk table |
| `app/ai-insights/page.tsx` | `aiPredictions` | ML hazard forecast cards | New `AIPrediction`/`RiskForecast` entity |
| `app/audit-trail/page.tsx` | `ledgerEvents` | Immutable ledger rows | New `AuditLogEntry` entity |
| `app/inspections/[id]/anomalies/[id]/page.tsx` | entire JSX body | Anomaly deep-dive telemetry, note, evidence | `Anomaly` (already typed) + linked `EvidenceItem` |
| `components/gis/MapContainer.tsx` | `minesOnMap` | Map pin coordinates | `Mine.coordinates` (already exists — just not wired) |
| `components/layout/Header.tsx` | officer name/role text | Logged-in user identity | `User` (session-derived) |
| `app/settings/page.tsx` | 4 default `useState` values | Regulatory config | New `SystemSettings` entity |

---

## 3. Data Requirements Per Page

Derived strictly from `types/index.ts` field usage — no invented fields.

- **Dashboard** needs (currently faked, but implied by the UI): total mine count, high-risk mine count, open violation count (+ critical subset), pending-action count (+ overdue subset), inspections-due-this-week count, alert count (+ unread subset); a per-mine summary row (`name, region, riskScore 0-100, statusBucket, violationCount, actionCount, lastInspectionDate`); a 6-point risk-trend time series (3 lines — the 3 lines are unlabeled in code, plausibly top-3 highest-risk mines); top-5 recent alerts.
- **Mines list**: full `Mine` record per row — `id, code, name, region, type, riskLevel, status, methaneGasPpm, strataStabilityScore, activeViolationsCount, pendingCapasCount, inspectorAssigned, coordinates, lastInspectedAt, productionTonnage`.
- **Mine detail**: same `Mine` fields, plus that mine's `Inspection[]` and `Violation[]` (**today fetched unfiltered — see §22 bug**), plus a 6-point 24h `{time, ch4, strata}` telemetry series.
- **Inspections list / detail**: full `Inspection` record — `id, code, mineId, mineName, inspectorName, inspectorBadge, date, status, riskScore, methaneLevelPpm, strataStability, observationsCount, evidenceCount, anomalyCount, anomalies: Anomaly[], checkpoints: Checkpoint[]`.
- **Anomaly detail**: `Anomaly` fields (`id, section, category, severity, description, status, evidenceId?, recommendedCapa?, telemetrySpike?`) — not currently fetched at all, but the type already exists on `Inspection.anomalies`.
- **GPS route page**: `Checkpoint[]` — `id, name, timestamp, lat, lng, verified, speed, elevation`.
- **Evidence viewer / vault**: full `EvidenceItem` — `id, code, title, type, timestamp, mineId, mineName, inspectionId, section, inspectorName, aiDetections: string[], gpsCoords, sha256Hash, imageUrl, status, fileSize`.
- **Violations list / detail**: full `Violation` — `id, code, mineId, mineName, title, category, dgmsSection, severity, status, dateIssued, deadline, inspectorName, fineAmount, description, capaId?`.
- **CAPA board / detail**: full `CorrectiveAction` — `id, code, title, mineId, mineName, violationId, priority, status, assignedTo, targetDate, slaHoursRemaining, description, evidenceCount, steps: {id, title, completed, completedAt?}[]`.
- **Alerts**: full `StatutoryAlert` — `id, type, title, description, mineId, mineName, severity, timestamp, isRead, actionRequired`.
- **Reports**: full `StatutoryReport` — `id, code, title, type, mineId, mineName, period, generatedAt, fileSize, status, downloadUrl`.
- **Users**: full `User` — `id, name, email, role, badgeNumber, region, status, mfaEnabled, avatarUrl, lastActive`.
- **AI Insights**: not typed anywhere yet — inferred shape: `{id, title, mineId/mineName, section, probabilityPct, windowHours, severity, recommendation, confidencePct}`.
- **Audit Trail**: not typed anywhere yet — inferred shape: `{txHash, blockNumber, eventName, entityRef, actorName, timestamp, verified}`.
- **Settings**: `{gasThresholdPpm, slaHours, fastapiEndpoint, mfaEnforced}` — no type exists, single global row implied (not per-mine).

---

## 4. CRUD Requirements (as evidenced by the UI, not assumed)

| Entity | CREATE | READ | UPDATE | DELETE | Evidence in code |
|---|---|---|---|---|---|
| Mine | UI stub only ("Register Mine Seam" → `alert()`) | ✅ list + by-id | none in UI | none | `apiService.getMines/getMineById` real; create is a fake button |
| Inspection | UI stub only ("Schedule Inspection" → `alert()`) | ✅ list + by-id | implied by "Dispatch Inspector" button (no-op) | none | read-only in practice |
| Anomaly | none | ✅ (nested under Inspection; no standalone fetch exists) | implied by "Assign CAPA Plan" button (no-op, just links) | none | purely nested read today |
| Checkpoint/GPS | none in website | ✅ (nested under Inspection) | none | none | website is read-only for GPS; **mobile app is the writer** (§20) |
| Evidence | UI stub only ("Upload Signed Evidence" → `alert()`) | ✅ list + by-id | none | none | website read-only; **mobile app is the writer** (§20) |
| Violation | UI stub only ("Issue Section 22 Order" → `alert()`, appears twice) | ✅ list + by-id | implied (status transitions: ACTIVE → SECTION_22_ORDER → UNDER_APPEAL → REMEDIATED) but no UI control performs it | none | severity/status enums exist; no UI writes them yet |
| CorrectiveAction (CAPA) | UI stub only ("Initiate CAPA Plan" → `alert()`) | ✅ board (grouped by status) + by-id | ✅ implied strongly: step checkboxes are `readOnly` today but clearly designed to toggle `completed`; "Verify & Sign-off CAPA" button (`alert()` today) is a genuine status-transition action (→ `CLOSED`) | none | this is the entity most clearly designed for real UPDATE — checklist + sign-off are the core interaction |
| Alert | none (alerts are system-generated) | ✅ list | ✅ "Acknowledge All" (`isRead` bulk-true, `alert()` today); per-alert "Execute Action" (`alert()` today, likely triggers a real downstream action e.g. CAPA/violation creation) | none | read + bulk-update pattern |
| Report | ✅ "Generate Form IV Dossier" (`alert()` today) | ✅ list | none | none | generation is a real backend job (PDF), not client-side |
| User | ✅ "Provision Officer" (`alert()` today) | ✅ list | ✅ "Edit Permissions" (`alert()` today) | none in UI (suspend implied by `status: 'SUSPENDED'` enum existing) | RBAC management surface |
| Settings | n/a (singleton) | implied (form pre-fills with defaults, not fetched) | ✅ "Save Settings" (`alert()` today) | n/a | single-row config write |
| AuditLogEntry | none (system-generated only) | ✅ list ("Verify Ledger Integrity" is a read-verify action) | none | none | append-only by design |
| AIPrediction | none from UI ("Re-run Predictive Model" triggers backend compute, not a user CREATE) | ✅ list | none | none | backend-computed, UI only re-triggers |

---

## 5. Backend Domain Models

Entities justified by the frontend, cross-checked against the mobile plan's §4/§5 (which independently derived `Mine, Section, Inspection, Evidence, GpsPoint, Observation, Measurement`). Fields marked **(web)** are only evidenced by the website; **(mobile)** only by the Android plan; unmarked fields are corroborated by both or are obvious PK/FK/timestamp scaffolding.

**Mine** — PK `id` (mobile uses server UUID; website's mock `id` like `"mine-a"` is a slug, so the real PK should be UUID with `code` as the human slug/display key). Fields: `code, name, region, type(OpenCast|Underground|Mixed), riskLevel, status, productionTonnage, methaneGasPpm, strataStabilityScore, activeViolationsCount, pendingCapasCount, inspectorAssigned, coordinates{lat,lng}, lastInspectedAt` **(web)**; `permitNumber, hazardIndex, evidenceQuota, sectionCount` **(mobile)**. `riskLevel/methaneGasPpm/strataStabilityScore/activeViolationsCount/pendingCapasCount` are **derived/calculated** (see §8), not raw input — only `code/name/region/type/coordinates/permitNumber` are core mine-registry fields. Relationships: has-many Inspection, Violation, CorrectiveAction (via Violation), Section.

**Section** — PK `id`. Fields: `mineId(FK), sectionIndex, label, description, evidenceQuota` **(mobile)**. Referenced by website only indirectly as free-text (`Anomaly.section`, `EvidenceItem.section` are strings like "Section B — Roof Bolting Grid 44", not FKs) — **normalizing these to a real Section FK is a MVP judgment call, not yet forced by either frontend.**

**Inspection** — PK `id` (client UUIDv4 per mobile plan — inspector app is the writer). Fields: `code, mineId(FK), mineName(denorm), inspectorId/inspectorName/inspectorBadge, date/startedAt/submittedAt, status(SCHEDULED|IN_PROGRESS|COMPLETED|FLAGGED|DRAFT|SUBMITTED — website and mobile use different status vocab, needs reconciling), riskScore, methaneLevelPpm, strataStability, observationsCount, evidenceCount, anomalyCount, gpsGateResult`. Required: `mineId, inspectorId, status`. Relationships: has-many Anomaly, Checkpoint/GpsPoint, Evidence, Observation, Measurement.

**Checkpoint / GpsPoint** — website's `Checkpoint{id,name,timestamp,lat,lng,verified,speed,elevation}` and mobile's `GpsPointEntity{id,inspectionId,sectionIndex?,latitude,longitude,accuracyMeters,source,capturedAt}` are the **same underlying entity described from two ends** — website adds a display `name` (checkpoint label) and computed `verified`/`speed`/`elevation`; mobile adds `source(GPS_GATE|SECTION_ENTRY|BREADCRUMB|HAZARD_MARKER)` and `accuracyMeters`. Unify as one `GpsPoint` table; `name`/`verified` become website-side derived/joined display fields (checkpoint name from Section, verified = within-geofence check already computed server-side).

**Anomaly** — PK `id`. Fields: `inspectionId(FK), section, category, severity, description, status(UNRESOLVED|UNDER_REVIEW|CAPA_ASSIGNED|RESOLVED), evidenceId?(FK), recommendedCapaId?(FK), telemetrySpike?`. This overlaps conceptually with mobile's `ObservationEntity` (`category, severity, notes, linkedEvidenceId?`) — **likely the same entity**: mobile's Observation is the raw inspector-authored record; website's "Anomaly" may be that same row after a severity/category worthy of flagging, or the AI/rules-engine-elevated version of it. This needs one explicit decision before schema design (see §23 open question), not two parallel tables.

**Measurement** — mobile-only today (`MeasurementEntity: metricType, value, unit, thresholdStatus`). Website never fetches a standalone Measurement, but *displays* measurement-derived values everywhere (`methaneLevelPpm`, `strataStability`, telemetry chart points) — meaning website needs **READ/aggregate access** to Measurement even though it has no dedicated Measurement UI.

**Evidence** — PK `id` (client UUIDv4 from mobile). Fields: `code(display), title, type(PHOTO|THERMAL|GAS_TELEMETRY|DOCUMENT), timestamp/capturedAt, mineId, inspectionId(FK), sectionIndex/section, inspectorName, gpsPointId?(FK), fileHash(sha256), imageUrl/remoteUrl, uploadState, status, fileSize`; `aiDetections: string[]` **(web-only today — needs to become a real relationship, see AIFinding below)**.

**AIFinding** *(new — not in either doc as a named entity, but required by both)* — website's `EvidenceItem.aiDetections: string[]` and the evidence-viewer's bounding-box overlay, plus mobile's explicit "AI/CV evidence analysis lives server-side" responsibility (§16/§17 of the mobile plan), together require: PK `id`, `evidenceId(FK), label, confidenceScore, boundingBox{x,y,w,h}, modelVersion, createdAt`. Website currently renders `aiDetections` as plain strings with confidence baked into the label text (`"Strata Crack (Severity 0.89)"`) — that's a display-layer flattening of what should be structured AIFinding rows.

**Violation** — PK `id`. Fields: `code, mineId(FK), title, category, dgmsSection, severity, status(ACTIVE|SECTION_22_ORDER|UNDER_APPEAL|REMEDIATED), dateIssued, deadline, inspectorId/inspectorName, fineAmount, description, capaId?(FK)`. Relationships: belongs-to Mine, has-one-or-many CorrectiveAction, may reference an Inspection/Anomaly it originated from (not modeled in current `Violation` type — `mineId` only, no `inspectionId`/`anomalyId` FK, even though every mock violation clearly originates from a specific inspection anomaly. **Gap worth fixing at schema time.**)

**CorrectiveAction (CAPA)** — PK `id`. Fields: `code, title, mineId(FK), violationId(FK), priority(URGENT|HIGH|NORMAL), status(OPEN|IN_PROGRESS|EVIDENCE_SUBMITTED|UNDER_VERIFICATION|CLOSED), assignedTo, targetDate, slaHoursRemaining(calculated, not stored), description, evidenceCount`. Has-many `CapaStep{id, capaId(FK), title, completed, completedAt?}` (currently embedded array in the type, should be its own child table for real toggling).

**StatutoryAlert** — PK `id`. Fields: `type(GAS_SPIKE|ROOF_MOVEMENT|UNAUTHORIZED_ENTRY|SECTION_22_ISSUED|INSPECTION_OVERDUE), title, description, mineId(FK), severity, timestamp, isRead, actionRequired`. System-generated (from Measurement threshold breach, Violation issuance, or Inspection-overdue rule) — no user CREATE path.

**StatutoryReport** — PK `id`. Fields: `code, title, type(FORM_IV|DGMS_MONTHLY|INCIDENT_SUMMARY|ENVIRONMENTAL_AUDIT), mineId(FK), period, generatedAt, fileSize, status(CERTIFIED|PENDING_SIGNATURE|ARCHIVED), downloadUrl(Storage path)`.

**User** — PK `id`. Fields: `name, email, role(DGMS_CHIEF_OFFICER|COMPLIANCE_INSPECTOR|MINE_SAFETY_OFFICER|SYSTEM_ADMIN), badgeNumber, region, status(ACTIVE|SUSPENDED), mfaEnabled, avatarUrl, lastActive`. This is the **website-side** user model (officers/admins); mobile's `inspectorId` is presumably the same underlying user row filtered to `role=COMPLIANCE_INSPECTOR` — one `users` table, not two.

**AuditLogEntry** *(new)* — `id, txHash, blockNumber, eventType, entityType, entityId, actorUserId, timestamp, verified`. Append-only, system-generated on every statutory-significant write (violation issuance, evidence seal, Section 22 order, sign-off).

**AIPrediction** *(new)* — `id, title, mineId(FK), sectionRef, probabilityPct, windowHours, severity, recommendation, confidencePct, modelVersion, generatedAt`.

**SystemSettings** *(new)* — single-row (or per-region) config: `gasThresholdPpm, slaHours, fastapiEndpoint, mfaEnforced, updatedBy, updatedAt`.

---

## 6. Relationship Analysis (as supported by the actual frontend)

```
Mine 1──* Section 1──* (evidence quota / progress, mobile-only display)
Mine 1──* Inspection 1──* GpsPoint/Checkpoint
                      1──* Evidence ──1 AIFinding (*)
                      1──* Anomaly ──1 Evidence (evidenceId link, optional)
                                   ──1 CorrectiveAction (recommendedCapaId, optional)
                      1──* Observation / Measurement (mobile-authored, website reads aggregates)
Mine 1──* Violation 1──1(or *) CorrectiveAction 1──* CapaStep
Violation *──1 Inspection/Anomaly  (NOT modeled in current Violation type — gap, see §5)
Mine 1──* StatutoryAlert
Mine 1──* StatutoryReport
User(role=INSPECTOR) 1──* Inspection (authorship)
User(any role) 1──* AuditLogEntry (as actor)
Evidence *──1 GpsPoint (gpsCoords / gpsPointId)
```

The website never displays a Mine→Section relationship explicitly (sections appear only as free-text labels inside Anomaly/Evidence), so Section can be modeled purely for the mobile app's quota-tracking needs and joined by string match or a lightweight FK on the website side — it is not a hard website requirement to expose a `/sections` endpoint.

---

## 7. API Requirements (FastAPI endpoints — not implemented, only specified)

Base path `/api/v1`. Auth: Bearer JWT (Supabase-issued) on every endpoint except `/auth/*`, matching the convention already fixed by the mobile plan (§12 there) so both clients share one auth scheme.

| Method & Path | Purpose | Query params | Request body | Response | Auth | CRUD | Entity |
|---|---|---|---|---|---|---|---|
| `POST /auth/login` | Officer login (website's 2-step credential+MFA form) | — | `{officerId/email, password, mfaCode?}` | `{accessToken, refreshToken, expiresIn, user}` | none | — | User |
| `GET /dashboard/summary` | Dashboard KPI cards + mine risk table + alert feed | `region?` | — | `{totalMines, highRiskMines, openViolations, criticalViolations, pendingActions, overdueActions, inspectionsDueThisWeek, unreadAlerts, mines:[{...}], recentAlerts:[...], riskTrend:[{week, ...}]}` | JWT | READ | Mine, Violation, CorrectiveAction, Inspection, Alert (aggregate) |
| `GET /mines` | Mines directory | `riskLevel?, type?, search?, page?, pageSize?` | — | `{items: Mine[], total}` | JWT | READ | Mine |
| `POST /mines` | Register mine ("Register Mine Seam") | — | `{code, name, region, type, permitNumber, coordinates}` | `Mine` | JWT (admin) | CREATE | Mine |
| `GET /mines/{mineId}` | Mine profile | — | — | `Mine` | JWT | READ | Mine |
| `PATCH /mines/{mineId}` | Update mine registry fields | — | partial `Mine` | `Mine` | JWT (admin) | UPDATE | Mine |
| `GET /mines/{mineId}/telemetry` | 24h CH4/strata chart | `hours?` | — | `[{time, ch4, strata}]` | JWT | READ | Measurement (aggregate) |
| `GET /mines/{mineId}/inspections` | Inspection history for a mine (fixes §22 bug) | `status?` | — | `Inspection[]` | JWT | READ | Inspection |
| `GET /mines/{mineId}/violations` | Active violations for a mine (fixes §22 bug) | `status?` | — | `Violation[]` | JWT | READ | Violation |
| `GET /inspections` | Inspections registry | `mineId?, status?, search?, page?` | — | `{items: Inspection[], total}` | JWT | READ | Inspection |
| `POST /inspections` | Schedule inspection ("Schedule Inspection") — also the mobile app's create-on-start call | — | `{mineId, inspectorId, scheduledDate?, startedAt?, gpsGateResult?}` | `Inspection` | JWT | CREATE | Inspection |
| `GET /inspections/{id}` | Inspection dossier | — | — | `Inspection` (nested `anomalies`, `checkpoints`) | JWT | READ | Inspection |
| `PATCH /inspections/{id}` | Status transition ("Dispatch Inspector") | — | `{status?, submittedAt?}` | `Inspection` | JWT | UPDATE | Inspection |
| `GET /inspections/{id}/anomalies/{anomalyId}` | Anomaly deep-dive (currently unfetched — §1 gap) | — | — | `Anomaly` + linked `Evidence` | JWT | READ | Anomaly |
| `PATCH /anomalies/{anomalyId}` | Assign CAPA to anomaly ("Assign CAPA Plan") | — | `{recommendedCapaId?, status?}` | `Anomaly` | JWT | UPDATE | Anomaly |
| `GET /inspections/{id}/checkpoints` | GPS breadcrumb + checkpoint log | — | — | `Checkpoint[]` | JWT | READ | GpsPoint |
| `GET /evidence` | Evidence vault list | `mineId?, type?, search?, page?` | — | `{items: EvidenceItem[], total}` | JWT | READ | Evidence |
| `GET /evidence/{id}` | Evidence viewer detail | — | — | `EvidenceItem` + `AIFinding[]` | JWT | READ | Evidence, AIFinding |
| `GET /violations` | Violations register | `severity?, status?, mineId?, search?, page?` | — | `{items: Violation[], total}` | JWT | READ | Violation |
| `POST /violations` | Issue Section 22 order ("Issue Section 22 Order") | — | `{mineId, title, category, dgmsSection, severity, description, fineAmount, deadline, inspectionId?, anomalyId?}` | `Violation` | JWT (inspector+) | CREATE | Violation |
| `GET /violations/{id}` | Violation case file | — | — | `Violation` + linked CAPA | JWT | READ | Violation |
| `PATCH /violations/{id}` | Status transition (appeal/remediate) | — | `{status}` | `Violation` | JWT | UPDATE | Violation |
| `GET /corrective-actions` | CAPA board | `status?, mineId?` | — | `CorrectiveAction[]` (client groups by status) | JWT | READ | CorrectiveAction |
| `POST /corrective-actions` | Initiate CAPA plan | — | `{title, mineId, violationId, priority, assignedTo, targetDate, description, steps:[{title}]}` | `CorrectiveAction` | JWT | CREATE | CorrectiveAction |
| `GET /corrective-actions/{id}` | CAPA case file | — | — | `CorrectiveAction` (nested `steps`) | JWT | READ | CorrectiveAction |
| `PATCH /corrective-actions/{id}/steps/{stepId}` | Toggle checklist step | — | `{completed}` | `CapaStep` | JWT | UPDATE | CapaStep |
| `POST /corrective-actions/{id}/verify` | "Verify & Sign-off CAPA" | — | `{signedBy}` | `CorrectiveAction` (status→CLOSED) | JWT (verifier role) | UPDATE | CorrectiveAction |
| `GET /alerts` | Alert queue | `isRead?, severity?` | — | `StatutoryAlert[]` | JWT | READ | Alert |
| `PATCH /alerts/read-all` | "Acknowledge All" | — | — | `{updated: number}` | JWT | UPDATE | Alert |
| `POST /alerts/{id}/execute` | "Execute Action" | — | — | `{result}` | JWT | UPDATE | Alert (+ side effect entity) |
| `GET /ai-insights/predictions` | AI predictive risk cards | `mineId?, severity?` | — | `AIPrediction[]` + `{modelPrecision, activeCount, preventiveActionsIssued}` | JWT | READ | AIPrediction |
| `POST /ai-insights/retrain` | "Re-run Predictive Model" | — | — | `{status: "queued"}` | JWT (admin) | — | AIPrediction (job trigger) |
| `GET /reports` | Reports list | `mineId?, type?` | — | `StatutoryReport[]` | JWT | READ | StatutoryReport |
| `POST /reports/generate` | "Generate Form IV Dossier" | — | `{type, mineId, period}` | `{status:"queued", reportId}` | JWT | CREATE | StatutoryReport (async job) |
| `GET /reports/{id}/download` | "Download Certified PDF" | — | — | signed Storage URL | JWT | READ | StatutoryReport |
| `GET /audit-trail` | Ledger table | `entityType?, page?` | — | `{items: AuditLogEntry[], total}` | JWT | READ | AuditLogEntry |
| `POST /audit-trail/verify` | "Verify Ledger Integrity" | — | — | `{valid: boolean, checkedBlocks}` | JWT | — | AuditLogEntry |
| `GET /users` | Users & roles table | `role?, status?, search?` | — | `User[]` | JWT (admin) | READ | User |
| `POST /users` | "Provision Officer" | — | `{name, email, role, badgeNumber, region}` | `User` | JWT (admin) | CREATE | User |
| `PATCH /users/{id}` | "Edit Permissions" | — | `{role?, status?, region?}` | `User` | JWT (admin) | UPDATE | User |
| `GET /settings` | Settings page pre-fill (not fetched today — gap) | — | — | `SystemSettings` | JWT (admin) | READ | SystemSettings |
| `PUT /settings` | "Save Settings" | — | `SystemSettings` | `SystemSettings` | JWT (admin) | UPDATE | SystemSettings |

Every `GET` list endpoint should accept `page`/`pageSize` even though `DataTable` today paginates client-side over a fully-fetched array — that only scales while mock data is small (§17 also flags this).

---

## 8. Dashboard Data — Stored vs. Calculated vs. AI

| Stat/Card | Source entity | Calculated? | AI/risk needed? |
|---|---|---|---|
| Total Mines | Mine | count() | no |
| High Risk | Mine | count() where riskLevel in (CRITICAL,HIGH) | no (riskLevel itself may be AI/rule-derived — see Mine.riskLevel below) |
| Open Violations (+ critical) | Violation | count() where status=ACTIVE, count() where severity=CRITICAL | no |
| Pending Actions (+ overdue) | CorrectiveAction | count() where status≠CLOSED; overdue = targetDate < now | no |
| Inspections Due | Inspection | count() where status=SCHEDULED and date within 7d | no |
| Alerts (+ unread) | StatutoryAlert | count(), count(isRead=false) | no |
| Mine Risk Overview table `risk` column | Mine.riskLevel/riskScore | **calculated** — composite of methane level, strata stability, active violations (exact formula is a risk-engine design decision, not yet specified anywhere in either frontend) | yes, eventually (rule-based MVP, ML-refined later per AI Insights page) |
| Risk Trend chart | historical Mine/Inspection risk scores | **calculated aggregate over time** — requires a stored historical snapshot (risk score is a point-in-time value; trending it means storing it per-period, not recomputing from raw sensor history each time) | no (trend of already-calculated values) |
| Recent Alerts | StatutoryAlert | stored (system-generated on threshold breach) | no |

**Mine.riskLevel / Mine.strataStabilityScore**: stored as flat fields on the `Mine` type today, but conceptually these are the *output* of aggregating that mine's latest Measurements + open Violations — i.e., calculated/cached fields, refreshed on a schedule or on write-trigger, not user-editable input.

---

## 9. Inspection Data

Website needs, per inspection: **Inspector** (name, badge — denormalized from User), **Mine** (id, name — denormalized), **GPS** (checkpoints with lat/lng/speed/elevation/verified), **Evidence** (count + linked items), **Observations** (only as an aggregate `observationsCount` — no observation list UI exists on website), **Measurements** (only as aggregate `methaneLevelPpm`/`strataStability` — no measurement list UI), **AI findings** (via `Evidence.aiDetections`), **Anomalies** (full nested list with severity/status/telemetry), **Violations** (not directly nested on Inspection type, but every mock violation traces to one — gap noted in §5), **Risk** (`riskScore`), **Timeline** (`date`; no explicit stage-by-stage timeline UI beyond the checkpoint log), **Submission status** (`status` enum). The website is **read-only** for all of this — every write to Inspection/GpsPoint/Evidence/Observation/Measurement originates from the Kotlin Inspector App per its own plan (§20 below).

---

## 10. Violation System

Fields actually represented: **type/category, dgmsSection** (the regulatory citation), **severity** (CRITICAL/HIGH/MEDIUM/LOW via `StatusBadge`), **status** (ACTIVE/SECTION_22_ORDER/UNDER_APPEAL/REMEDIATED), **mine relationship** (`mineId`, direct), **evidence relationship** (none direct on `Violation` type — only reachable by first finding the originating Anomaly, itself a gap), **CAPA relationship** (`capaId`, direct, 1:1 in current type though UI text implies it could be 1:many), **due dates** (`dateIssued`, `deadline` — `deadline` is a free-text string today, e.g. `"2026-09-07 (72 Hours SLA)"`, mixing a date and an SLA duration in one field — **should split into `deadlineAt: timestamp` + `slaHours: number` for real backend use**), **assignment** (`inspectorName` — the issuing officer, not an assignee for remediation; remediation assignment lives on CorrectiveAction), **fine amount** (`fineAmount` — also free-text with currency symbol and extra clause baked in, e.g. `"₹ 5,00,000 + Statutory Cease Work"` — should split into `fineAmountInr: number` + `additionalDirectives: string`). **No AI-finding relationship exists on the Violation type directly** — inferred only through the Anomaly it likely originated from. **No verification field exists on Violation** — verification lives entirely on the linked CorrectiveAction's sign-off.

---

## 11. Corrective Actions (CAPA)

**Assignment**: `assignedTo` (free-text name+title today, e.g. `"Er. Somnath Mukherjee (Chief Mine Engineer)"` — should become a `User` FK). **Status**: 5-stage enum matching the kanban columns exactly (`OPEN, IN_PROGRESS, EVIDENCE_SUBMITTED, UNDER_VERIFICATION, CLOSED`). **Deadlines**: `targetDate` + `slaHoursRemaining` (the latter is **calculated** from `targetDate - now`, not stored — storing it would go stale instantly). **Evidence**: `evidenceCount` only (no evidence-list UI on the CAPA page itself, though the sidebar text "3 Photogrammetry Files" implies one is expected) — CAPA evidence is a distinct upload channel from Inspection evidence and should link the same `Evidence` table via a `capaId` FK rather than only `inspectionId`. **Verification**: the "Verify & Sign-off CAPA" button is the one clear write action in the whole website UI — it should require a `signedBy` (current officer), timestamp, and transition status to `CLOSED`. **Escalation/Comments**: **no UI evidence of either** — do not build these for MVP; nothing in the frontend implies them.

---

## 12. Evidence + CV Requirements

Evidence images appear in 3 places: evidence-vault table (thumbnail-free, text row), inspection dossier's "View Evidence" link, and the dedicated evidence viewer (full detail). **Metadata shown**: code, title, type, timestamp, mine/section/inspector, file size, SHA-256 hash, GPS coords, status (`VERIFIED_AI_CONFIRMED` etc.). **AI findings displayed**: a flat `string[]` (`aiDetections`) rendered as pill badges, e.g. `"Strata Crack (Severity 0.89)"` — confidence is embedded in the label text, not a separate field. **Bounding boxes**: yes — the evidence viewer draws one hardcoded overlay box (`top-1/3 left-1/3, w-36 h-28`) labeled "AI Crack 89%" — this is **entirely hardcoded positioning**, not derived from any real coordinate data, since `EvidenceItem` has no bounding-box field today. **Confidence scores**: shown only embedded in label text, never as a distinct numeric field. **GPS/timestamp**: yes, both present (`gpsCoords`, `timestamp`).

**Backend interface the frontend expects from CV** (not implementing CV — just the contract the UI already assumes): each evidence item, once processed, needs zero-or-more `AIFinding{label, confidenceScore, boundingBox{x,y,width,height} (normalized 0-1, so overlay renders at any image scale), modelVersion}` rows, returned alongside `EvidenceItem` from `GET /evidence/{id}`. Flow: `Image upload → CV Processing → AIFinding rows → (rule engine matches finding+threshold → creates/updates Anomaly) → (Anomaly severity ≥ threshold → creates Violation) → (Violation → risk recompute for Mine)`.

---

## 13. Risk Engine

Risk appears at 3 levels: **Mine-level** (`riskLevel` enum + implied 0-100 score used in dashboard table sorting), **Inspection-level** (`riskScore: number`), **Anomaly/Violation severity** (`RiskLevel` enum reused across all 3). **AI confidence** appears only inside evidence labels (§12). **Categories**: the single `RiskLevel` enum (`CRITICAL|HIGH|MEDIUM|LOW`) is reused everywhere — there is no separate "risk category" taxonomy (e.g., no split between structural/gas/ventilation risk categories in the type system, even though anomaly `category` free-text implies one exists conceptually). **Trends**: only the dashboard's 6-week trend chart, and it's the trend of an already-calculated score, not raw sensor trend.

**Stored**: `Mine.riskLevel`, `Inspection.riskScore` (cached/snapshotted values, refreshed on a schedule or trigger). **Calculated**: the score itself, from a formula over Measurement + Violation + Anomaly data (formula unspecified by any frontend — an MVP business-rule decision, not inferable from UI). **AI-generated**: the AI Insights page's `probability`/`confidence` fields are explicitly the output of a trained model (`"94.2% AI Model Confidence"`), distinct from the rule-based risk score used elsewhere — **two separate systems, not one**: a deterministic/rule-based risk score (Mine/Inspection level, MVP-feasible without ML) vs. a predictive ML model (AI Insights page, later phase).

---

## 14. Reports

**Filters**: none implemented in UI beyond the implicit per-mine/per-period grouping already in the mock data — no date-range picker, no multi-select filter bar exists on `/reports` today. **Aggregations**: report generation is described as compiling one mine's period data into a dossier — backend must aggregate Inspection/Violation/Evidence for that `mineId + period` at generation time. **Tables/Charts**: none inside the reports page itself (it's a card grid, not a data table). **Export**: "Download Certified PDF" — implies backend-generated PDF, stored in Supabase Storage, served via signed URL, not client-side PDF generation (no PDF library imported anywhere in the website). **PDF requirement**: yes, explicit — `fileSize` field and "Certified PDF" language both assume a real generated binary, not a live-rendered page.

---

## 15. Authentication + Roles

**Login**: 2-step form (credentials → TOTP MFA code) — currently **zero backend calls**, `handleSubmit` just advances local state and finally `router.push('/dashboard')` unconditionally. **Logout**: a single `<Link href="/login">` in the header user-menu — no session-clearing call exists. **Profile**: header dropdown shows a hardcoded name/role, links to `/users` and `/settings` — no "my profile" page exists distinct from the Users table. **Roles**: 4-value enum (`DGMS_CHIEF_OFFICER, COMPLIANCE_INSPECTOR, MINE_SAFETY_OFFICER, SYSTEM_ADMIN`) on `User.role` — no route-level guarding exists in the website today (every route is reachable regardless of role; `/users` and `/settings` *should* be admin-gated per their content, but nothing in the code enforces it — a backend/middleware decision, not something to infer as already-designed). **Admin functions**: Users & Roles page (`/users`), Settings page (`/settings`). **Officer functions**: everything else. **MFA**: `User.mfaEnabled` field exists and is displayed in the Users table; the login flow's MFA step is UI-complete but backend-unconnected.

---

## 16. Notifications / Alerts

**Types**: 5-value enum (`GAS_SPIKE, ROOF_MOVEMENT, UNAUTHORIZED_ENTRY, SECTION_22_ISSUED, INSPECTION_OVERDUE`) — clearly system/rule-generated, not user-authored. **Severity**: reuses `RiskLevel`. **Related entity**: `mineId`/`mineName` only (no link to the specific Violation/Inspection/Measurement that triggered it, even though the description text references one — another denormalization gap worth fixing: add `sourceEntityType`/`sourceEntityId`). **Read/unread**: `isRead: boolean`, both a header-bell unread-count and a full page exist. **Timestamps**: `timestamp` is currently a relative string (`"12 mins ago"`) in mock data — backend should return an absolute ISO timestamp and let the frontend format it relatively. **Actions**: `actionRequired: string` (free-text description) + an "Execute Action" button that has no defined backend behavior yet (per-alert-type action dispatch is unspecified — reasonable MVP behavior is type-specific: e.g. `SECTION_22_ISSUED` → creates/links a Violation, `INSPECTION_OVERDUE` → creates/schedules an Inspection).

---

## 17. Preliminary Supabase/PostgreSQL Schema (proposed only — not created)

**CORE TABLES** (primary business records, directly editable):
`mines`, `sections`, `users`, `inspections`, `gps_points`, `observations`, `measurements`, `evidence`, `violations`, `corrective_actions`, `capa_steps`, `system_settings` — each justified by §5/§1 above; used respectively by nearly every route (Mine by `/mines*`, `/dashboard`, `/gis`; Inspection by `/inspections*`; GpsPoint by `/inspections/[id]/route`; Evidence by `/evidence-vault`, evidence viewer; Violation by `/violations*`; CorrectiveAction/CapaStep by `/corrective-actions*`; User by `/users`, auth; SystemSettings by `/settings`).

**DERIVED / CALCULATED DATA** (recomputed or cached, not primary input): `mine_risk_snapshots` (time-series, powers dashboard risk-trend chart and Mine.riskLevel — §8), `dashboard_aggregates` (or computed on-request via SQL views rather than a stored table — MVP choice), `capa.sla_hours_remaining` (computed at read time from `target_date`, never stored). Used by `/dashboard`, `/mines`, `/mines/[id]`.

**AI/CV DATA**: `ai_findings` (per-evidence bounding boxes + confidence — §12, powers evidence viewer overlay), `anomalies` (rule-engine or inspector-flagged, sits between raw Observation and formal Violation — §5/§6), `ai_predictions` (ML hazard forecasts — powers `/ai-insights`). Kept separate from core tables because they're populated by a processing pipeline (§19), not direct user CRUD.

**AUDIT DATA**: `audit_log_entries` (append-only, powers `/audit-trail`), plus standard `created_at/updated_at/created_by` columns on every core table for basic traceability (not itself a separate table, just a convention).

`alerts` and `reports` sit between CORE and DERIVED: `alerts` rows are system-generated (from measurement thresholds / violation issuance / schedule checks) but are directly user-actioned (read/execute), so they behave like a core table operationally even though nothing creates them via a form. `reports` rows are metadata for backend-generated PDF jobs — the row is core, the PDF binary is Storage.

---

## 18. Supabase Storage Requirements

Buckets implied by the UI: **evidence** (inspection photos/thermal images — largest volume, written by mobile per its own plan §8, read by website's evidence viewer/vault), **reports** (generated PDF dossiers — written by backend report-generation job, read via `/reports` download button), **avatars** (`User.avatarUrl` — currently Unsplash URLs in mock data, should migrate to real uploaded avatars for `/users`), **capa-evidence** (proof-of-remediation files referenced by CAPA sidebar text "3 Photogrammetry Files" — distinct from inspection evidence per §11). Access pattern for all: signed URLs (already the pattern used for evidence upload per the mobile plan §8/§13, and appropriate for report downloads too — nothing in the website justifies public buckets).

---

## 19. CV / AI Architecture (conceptual only)

```
Inspector App (capture)
   ↓ presigned upload (mobile plan §8/§13, #9-11)
Supabase Storage (evidence bucket)
   ↓ triggers (webhook/queue) on confirm-upload
CV Processing Service (external to FastAPI's request cycle — async job)
   ↓ writes
ai_findings (label, confidence, bbox) ── per Evidence row
   ↓ rule engine evaluates findings + measurement thresholds
anomalies (created/updated, severity assigned)
   ↓ severity ≥ threshold
violations (created, DGMS section auto-suggested or officer-confirmed)
   ↓ recompute
mine_risk_snapshots (risk engine re-scores affected Mine)
   ↓
Officer Website (dashboard, mine profile, evidence viewer, violations register — all read the results)
```

Data crossing each boundary, per what the website actually renders: Storage→CV needs the raw image + `evidenceId`; CV→AIFinding needs `label, confidenceScore, boundingBox`; AIFinding→Anomaly needs a threshold/mapping table (not specified by any frontend — an MVP rule-engine decision); Anomaly→Violation needs the DGMS section citation (currently always manually meaningful text like "DGMS Section 22(1) Order" — likely officer-confirmed, not fully automated, since real legal citations shouldn't be silently AI-assigned); Violation→Risk needs just the mine + severity to trigger a recompute.

---

## 20. Mobile + Website Shared Backend

| Entity | Mobile → Backend | Website → Backend | Both |
|---|---|---|---|
| Mine | read cache (`GET /mines?assignedTo=`) | full CRUD-ish (register/edit/read) | ✅ shared read model |
| Section | read cache, quota tracking | not directly exposed (free-text today) | mobile-primary |
| Inspection | **write** (create/update/submit) | **read-only** (dossier, registry) | ✅ core shared entity |
| GpsPoint/Checkpoint | **write** (gate, breadcrumb, hazard marker) | read-only (route page) | ✅ |
| Evidence | **write** (capture, upload) | read-only (viewer, vault) | ✅ |
| Observation | **write** | not directly exposed (aggregate count only) | mobile-primary, website reads via Anomaly/count |
| Measurement | **write** | not directly exposed (aggregate values only) | mobile-primary, website reads via aggregate |
| Anomaly | indirectly (elevated from Observation, likely server-side) | read + limited update (assign CAPA) | ✅ |
| Violation | none (mobile has no violation UI) | full (create/read/update) | website-primary |
| CorrectiveAction | none in mobile plan today | full (board/detail/verify) | website-primary |
| Alert | none | read + acknowledge/execute | website-primary |
| Report | none | generate/read/download | website-primary |
| User | auth only (login as inspector) | full RBAC management | ✅ shared auth, website-primary management |
| AuditLogEntry | none | read/verify | website-primary (system-generated from both) |
| AIFinding/AIPrediction | none (mobile never reads these back today) | read | website-primary |

This confirms the FastAPI backend is genuinely shared, not two separate APIs: Mine, Inspection, GpsPoint, Evidence, User, and (indirectly) Anomaly must be modeled once and served to both clients: the mobile plan's `/api/v1/mines`, `/api/v1/inspections`, `/api/v1/evidence` endpoints and this document's website equivalents are **the same resources**, just consumed differently (mobile writes what website reads).

---

## 21. Preliminary Frontend-Backend Contract

The full endpoint table in §7 **is** this contract for the website; combined with the mobile plan's §12-13 table, together they form one contract document. Key cross-client consistency points to lock down before backend implementation starts:

- **One `/api/v1/mines` resource**, mobile filtering by `assignedTo`, website by `riskLevel/type/search` — same underlying rows, different query params, same response shape (superset of fields; mobile's client can ignore `activeViolationsCount` etc.).
- **One `/api/v1/inspections/{id}` resource** — mobile's `GET` (per its §13 #12) returns "full nested inspection + PROCESSING/COMPLETED results (risk score, AI tags)"; website's dossier page needs exactly that same nested shape (`anomalies`, `checkpoints`). No divergence needed.
- **Auth**: both plans converge on Bearer JWT (Supabase-issued). Website's officer login and mobile's inspector login are different `POST /auth/login` payload shapes (officer: email+password+MFA; inspector: badge ID+PIN) but should resolve to the **same `User` table and the same JWT shape**, differing only in role claim.
- **Status enum reconciliation needed**: website's `InspectionStatus` (`COMPLETED|IN_PROGRESS|SCHEDULED|FLAGGED`) vs. mobile's `InspectionEntity.status` (`DRAFT|IN_PROGRESS|SUBMITTED|COMPLETED`) are not the same set — must be unified into one enum before schema design, since both clients read/render the same column.

---

## 22. Notable Frontend Bugs/Gaps Worth Backend Awareness

(Not asked to fix — flagged because they affect API design.)

1. `/mines/[mineId]` fetches **all** inspections and **all** violations (`getInspections()`, `getViolations()` with no filter), not scoped to that mine — the real endpoints must be `GET /mines/{id}/inspections` / `GET /mines/{id}/violations`, not a client-side filter of an unscoped fetch (§7 already specifies these correctly).
2. `/inspections/[id]/anomalies/[anomalyId]` performs **no data fetch whatsoever** — everything is hardcoded JSX. The backend needs `GET /inspections/{id}/anomalies/{anomalyId}` (§7) even though nothing calls it yet.
3. Dashboard, AI Insights, and Audit Trail pages have **zero backend contract today** (inline literals) — these need the most new design work since there's no existing `apiService` method shape to mirror.
4. `apiService.getMineById/getInspectionById/getEvidenceById/getViolationById/getCapaById` all **fall back to `array[0]`** when no match is found (e.g. `mockMines.find(...) || mockMines[0]`) — a real backend must return 404, and the frontend's not-found handling will need to change accordingly (currently invisible because mock data always "matches" something).
5. `Header.tsx` imports `mockAlerts` directly rather than going through `apiService` — this bypasses the fetch layer entirely and will break silently if `lib/mock/data.ts` is ever removed without updating `Header.tsx` too.

---

## 23. Open Questions (mirroring the mobile plan's practice of flagging unresolved decisions rather than guessing)

- **Anomaly vs. Observation**: are these the same entity at different lifecycle stages, or genuinely distinct? Affects whether `anomalies` is a real table or a filtered view over `observations`.
- **Risk score formula**: no frontend specifies the weighting of methane/strata/violations/anomalies into a single 0-100 score. Needs an explicit business-rule decision before the risk engine (rule-based MVP) can be built.
- **Violation ← Inspection/Anomaly linkage**: current `Violation` type has no `inspectionId`/`anomalyId` FK despite every example clearly originating from one. Add it, or treat violations as sometimes-standalone (e.g. audit-initiated, not inspection-initiated)?
- **Role-based route gating**: `/users` and `/settings` are admin-content but the website enforces no role check anywhere today. Should this be FastAPI-side (401/403 on the API) with the website trusting that, client-side route guards, or Supabase RLS — or all three?

---

## 24. MVP vs. Phase 2 vs. Future

**MVP** (matches mobile plan's phased approach — auth, core entities, read paths first): `users`+auth (shared login/JWT), `mines` (CRUD-lite: register+read+edit), `inspections`+`gps_points` (read for website, write already scoped by mobile Phase 1-3), `evidence` (read for website; write via mobile Phase 3), `violations` (read+create+status update), `corrective_actions`+`capa_steps` (full CRUD incl. step toggle + sign-off — the CAPA sign-off flow is the single clearest "must work" write path on the whole website), `alerts` (read+acknowledge), basic rule-based risk score (no ML), `system_settings` (single-row read/write).

**Phase 2**: `ai_findings` (real bounding boxes replacing hardcoded overlay), `anomalies` as a first-class linked entity (not just nested JSON), `reports` generation (PDF job + Storage), `audit_trail` (append-only ledger, real hash chaining), dashboard server-side aggregation endpoint (replacing today's inline literals), server-side pagination/sort/filter for `DataTable`-backed pages (mines/inspections/violations/evidence/users lists) once row counts outgrow client-side paging.

**Future**: `ai_predictions` (trained ML model for `/ai-insights`, distinct from the MVP's rule-based risk score per §13), real GIS map SDK integration (replacing the hardcoded `MapContainer` mock — coordinates already exist in `Mine`/`GpsPoint`, just need a real map library wired to them), CAPA escalation/comments (no current UI evidence, would be new scope), per-alert-type automated action dispatch (`Execute Action` currently has no defined backend behavior per type).
