# Inspector App ↔ Backend Integration Plan

Analysis and integration plan for connecting the existing MineInspect Android app (Kotlin/Jetpack Compose) to the future FastAPI + Supabase backend. This document is the output of a full, code-verified audit of the current Android project — no backend or mobile implementation has started. See §20 for the approved 5-phase MVP order.

---

## 1. Current Android Architecture

- Single-Activity Jetpack Compose app, package `com.mineinspect.app`, at repo root `app/`.
- Kotlin 2.1.20, AGP 8.3.2, compileSdk/targetSdk 34, minSdk 26, Java 17. No Gradle version catalog (`libs.versions.toml` absent) — all deps hardcoded as strings in `app/build.gradle.kts`.
- **Dependencies present:** core-ktx, lifecycle-runtime-ktx, activity-compose, Compose BOM 2024.02.00, material3 + material-icons-extended, navigation-compose 2.7.7, coil-compose 2.6.0. That's the entire list.
- **Dependencies absent (verified by whole-project grep):** Room, DataStore, SharedPreferences, Retrofit/Ktor/OkHttp, WorkManager, CameraX, Play Services Location, Hilt/Koin/Dagger, Supabase/Firebase SDKs, Accompanist Permissions, any map SDK.
- **No ViewModel layer anywhere** (grep for `ViewModel`/`@HiltViewModel` = zero matches). No repository pattern. No `Application` subclass (manifest `<application>` has no `android:name`). All state is Compose `remember { mutableStateOf(...) }`, local to each screen.
- Only two pieces of cross-screen state exist, both global singleton `object`s in the newly-added (untracked in git) `app/src/main/java/com/mineinspect/app/data/`:
  - `CameraState.lastCapturedUri: Uri?` — most recent photo only, lost on process death.
  - `InspectionState` — `activeSectionId` (default `"2"`) + `section1Photos`/`section2Photos`/`section3Photos` Ints, each capped at 3.
- Navigation: single `NavHost` in `navigation/AppNavGraph.kt`, routes defined in `navigation/Routes.kt`, start destination `login`. The only nav argument used anywhere is a string `sectionId` path segment (`section_start/{sectionId}`, `section_monitoring/{sectionId}`). All other cross-screen data flows through the two singletons above, not through nav args or a shared ViewModel.
- Manifest permissions: `INTERNET`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `CAMERA`; one exported `MainActivity`; one non-exported `FileProvider` (authority `com.mineinspect.app.fileprovider`, paths in untracked `res/xml/file_paths.xml`); no services.
- Shared design system worth preserving as-is: `ui/theme/` (Color/Dimens/Theme/Type) and `ui/components/` (AppTopBar, BottomNavBar, Buttons, ChecklistRow, PhotoThumbnail, ProgressBar, SeveritySelector [enum `Severity`: LOW/MED/HIGH/CRITICAL], StatCard, StatusBadge [enum `BadgeStatus`: SUCCESS/WARNING/CRITICAL/NEUTRAL], ThresholdGauge).

## 2. Current Implementation Status (workflow stage → status)

| Stage | Status | Evidence |
|---|---|---|
| Login | **MOCKED** | `LoginScreen.kt`: hardcoded ID `"INS-102"`, hardcoded PIN `"84920119"` (show/hide toggle only, no real input), Sign In does `delay(500)` then calls `onSignIn()` unconditionally. No credential check, no token, no Supabase Auth reference anywhere in repo. |
| Home / Assigned Mines | **MOCKED** | `HomeScreen.kt`: fixed 3-item mine list, every row navigates identically regardless of which mine tapped. Stat cards (GPS accuracy, hazard index, ledger) are literal strings. |
| Mine Briefing | **MOCKED** | `MineBriefingScreen.kt`: permit, hazard index, violation count, evidence quotas all hardcoded literals. |
| GPS Verification (gate) | **MOCKED** | `GpsGateScreen.kt`: "RTK FIX"/"14 Sat"/"±6.2m" are literal strings. No permission check, no `FusedLocationProviderClient`, no real read anywhere — the gate always passes. |
| Start Inspection | **PARTIALLY IMPLEMENTED** | Real navigation transition exists; no `Inspection` record (local or remote) is created — nothing persists that an inspection started. |
| Active Inspection / Tracking | **PARTIALLY IMPLEMENTED (mocked data)** | `ActiveTrackingScreen.kt` has a real local stopwatch (`LaunchedEffect` ticking every 1s) but no real location tracking; breadcrumb trail is an empty placeholder `Box`; "Drop Geo-Hazard Marker" flashes a boolean for 2.2s and persists nothing. |
| Area/Section navigation | **PARTIALLY IMPLEMENTED** | `AreasCoverageScreen.kt` is the one screen with real (in-memory) logic — reads `InspectionState.getPhotoCount()` to gate section unlock. Section count (3) and quota (3 photos) are hardcoded, not server-driven. |
| Capture Evidence | **IMPLEMENTED (device I/O only)** | `EvidenceCaptureScreen.kt` — real CAMERA permission request, real system-camera hand-off via `ActivityResultContracts.TakePicture()`, real file write + `FileProvider`. No metadata (mine/section/inspector/GPS/hash) attached at capture; only one URI remembered at a time despite multiple photos being required per section. |
| Evidence review/details | **MOCKED (except the image itself)** | `EvidenceDetailsScreen.kt` shows the real photo via Coil, but GPS coordinates, timestamp, inspector signature, and a "SHA-256" hash are all hardcoded literals unrelated to the actual file. |
| Measurements | **MISSING** | Route `MEASUREMENT_ENTRY` reserved in `Routes.kt`, no screen file, no `composable{}` entry, no data model. |
| Observations | **MISSING** | Route `MANUAL_OBSERVATION` (and `ANOMALY_WARNING`) reserved, not implemented. `Severity` enum exists in `ui/components/` but unused by any real observation flow. |
| Random Evidence Requests | **MISSING** | Route `RANDOM_EVIDENCE` reserved, not implemented. |
| PPE / Worker Verification | **MISSING** | Routes `PPE_VERIFICATION`, `WORKER_VERIFICATION` reserved, not implemented. |
| Review | **MISSING** | Routes `INSPECTION_SUMMARY`, `FINAL_REVIEW`, `FINAL_LOCATION_CHECK` reserved, not implemented. |
| Submit | **MOCKED (no-op)** | `AreasCoverageScreen.kt`'s "Complete & Submit Inspection Audit" calls `onCompleteAudit`, which the nav graph wires to a plain `popBackStack(HOME)` — no network call, no server confirmation. |
| Offline handling | **MISSING** | No Room, no local DB of any kind — "offline" data is just process-lifetime Compose state that vanishes on process death. |
| Sync | **MOCKED (no-op)** | `HomeScreen.kt`'s Sync tab "sync" button does `delay(1500)` then flips a boolean back — no WorkManager, no networking, no real queue. Routes `OFFLINE_SAVE`, `SYNCHRONIZATION`, `SUBMISSION_COMPLETE` reserved, not implemented. |

**13 routes reserved in `Routes.kt` with no matching screen file and no `composable{}` entry in `AppNavGraph.kt`:** `ANOMALY_WARNING`, `RANDOM_EVIDENCE`, `PPE_VERIFICATION`, `MANUAL_OBSERVATION`, `MEASUREMENT_ENTRY`, `WORKER_VERIFICATION`, `SECTION_COMPLETION`, `INSPECTION_SUMMARY`, `FINAL_REVIEW`, `FINAL_LOCATION_CHECK`, `OFFLINE_SAVE`, `SYNCHRONIZATION`, `SUBMISSION_COMPLETE`. These names are treated throughout this plan as the specification for the still-missing flow.

## 3. Mobile Screens and Responsibilities

11 screens wired today (`login`, `home`, `mine_briefing`, `gps_gate`, `active_tracking`, `route_map`, `areas_coverage`, `section_start/{id}`, `section_monitoring/{id}`, `evidence_capture`, `evidence_details`), each keeping its current UI/layout untouched. Responsibility going forward per screen = "replace hardcoded literals/singleton reads with ViewModel-backed real data," not redesign. 13 more screens to be built against reserved routes in Phase 4 (§20), reusing the existing `ui/components/` design system (`SeveritySelector`, `ThresholdGauge`, `ChecklistRow`, `StatusBadge`).

## 4. Mobile Data Models (Kotlin domain layer to introduce)

No domain data classes exist today — introduce these as plain Kotlin `data class`es backing the Room entities in §5: `Mine`, `Section`, `Inspection`, `Evidence`, `GpsPoint`, `Observation`, `Measurement`, `SyncState` (enum), `Severity` (already exists, reuse). Each Room `*Entity` (§5) maps 1:1 to a domain class exposed to ViewModels via repositories, keeping Room types out of the UI layer.

## 5. Room Database Design

**Design decision: per-entity `syncState` column, no separate `SyncQueueEntity`.** A queue table would duplicate state already on each row and risk drifting from it, especially across process death. The workflow is strictly hierarchical (Inspection → Section → Evidence/Observation/Measurement/GpsPoint), so the sync worker just queries each DAO for `WHERE syncState = SYNC_PENDING`. The one place a queue-like split *is* used is Evidence, which gets a separate `uploadState` column (binary file upload is a different operation from metadata row sync).

**`MineCacheEntity`** (read-only cache, no syncState — refreshed from server, not mutated locally)
`mineId` (PK, server UUID), `name`, `permitNumber`, `hazardIndex`, `evidenceQuota` (replaces hardcoded "3"), `sectionCount` (replaces hardcoded "3 sections"), `lastBriefingText`, `cachedAt`.

**`SectionDefEntity`** (read-only cache, derived from Mine detail)
`id` (PK, `"{mineId}:{sectionIndex}"`), `mineId` (FK), `sectionIndex`, `label`, `description`, `evidenceQuota`.
Section *progress* is **derived at query time** via `COUNT(*)` over `EvidenceEntity` filtered by `inspectionId + sectionIndex` — not stored redundantly. This is the direct fix for the double-increment bug (§22).

**`InspectionEntity`** (root aggregate, synced)
`id` (PK, client UUIDv4 — idempotency key), `mineId` (FK), `inspectorId`, `status` (DRAFT/IN_PROGRESS/SUBMITTED/COMPLETED), `startedAt`, `submittedAt`, `gpsGateResult`, `syncState`, `syncAttempts`, `lastSyncError`, `updatedAt`. Indexes: `(mineId)`, `(syncState)`.

**`EvidenceEntity`** (synced; supports multiple photos per section, fixing the `CameraState` single-URI gap)
`id` (PK, client UUIDv4), `inspectionId` (FK), `sectionIndex`, `localFilePath`, `remoteUrl?`, `capturedAt`, `gpsPointId?` (FK), `inspectorId`, `fileHash` (real on-device SHA-256, fixes the fabricated hash bug), `uploadState` (NOT_UPLOADED/UPLOADING/UPLOADED/UPLOAD_FAILED), `syncState`, `syncAttempts`, `lastSyncError`, `updatedAt`. Indexes: `(inspectionId, sectionIndex)`, `(syncState)`, `(uploadState)`.

**`GpsPointEntity`** (synced; every real fix — gate, section entry, breadcrumb, hazard marker)
`id` (PK, client UUIDv4), `inspectionId` (FK), `sectionIndex?`, `latitude`, `longitude`, `accuracyMeters`, `source` (GPS_GATE/SECTION_ENTRY/BREADCRUMB/HAZARD_MARKER), `capturedAt`, `syncState`, `syncAttempts`, `lastSyncError`, `updatedAt`. Indexes: `(inspectionId)`, `(syncState)`.

**`ObservationEntity`** (synced; backs `MANUAL_OBSERVATION`/`ANOMALY_WARNING`)
`id` (PK, client UUIDv4), `inspectionId` (FK), `sectionIndex`, `category`, `severity` (reuse existing `Severity` enum), `notes`, `linkedEvidenceId?` (FK), `gpsPointId?` (FK), `recordedAt`, `syncState`, `syncAttempts`, `lastSyncError`, `updatedAt`. Indexes: `(inspectionId, sectionIndex)`, `(syncState)`.

**`MeasurementEntity`** (synced; backs `MEASUREMENT_ENTRY`)
`id` (PK, client UUIDv4), `inspectionId` (FK), `sectionIndex`, `metricType`, `value`, `unit`, `thresholdStatus?` (cached server-computed display value — **never computed on-device**, see §17), `recordedAt`, `syncState`, `syncAttempts`, `lastSyncError`, `updatedAt`. Indexes: `(inspectionId, sectionIndex)`, `(syncState)`.

Auth tokens are **not** a Room entity — small, security-sensitive, single-row — handled via `EncryptedSharedPreferences` instead (§11).

## 6. Local CRUD Operations

**Create:** Inspection (on mine-briefing start), Evidence (on successful capture), GpsPoint (on every real fix — gate/section-entry/breadcrumb/hazard-marker), Observation, Measurement (once built).

**Read:** Mine/Section cache (Home, Briefing), current Inspection + children (all in-flow screens), derived section progress (COUNT query over Evidence).

**Update:** Inspection.status transitions (IN_PROGRESS→SUBMITTED locally, ahead of sync catching up), Evidence/Observation/Measurement metadata edits pre-submission, sync-state fields (system-owned, not user-facing).

**Delete:** allowed only for rows still in `LOCAL`/`SYNC_PENDING`/`SYNC_FAILED` state on an inspection that has not reached `SUBMITTED` — e.g. discarding a bad draft photo before submit. **Never** allowed once a row has reached `SYNCED` or later, and never allowed on any child of an `InspectionEntity` whose `status` is `SUBMITTED`/`COMPLETED` — this is the boundary that protects already-synced official records per the user's explicit constraint.

## 7. GPS Architecture

Today: fully mocked, no permission requested at runtime despite manifest declaring `ACCESS_FINE_LOCATION`/`ACCESS_COARSE_LOCATION` (dead entries — confirmed zero runtime request anywhere in code).

Planned: add `com.google.android.gms:play-services-location`. `GpsGateScreen` requests `ACCESS_FINE_LOCATION` via `ActivityResultContracts.RequestPermission`, takes one `FusedLocationProviderClient` fix, writes a `GpsPointEntity(source="GPS_GATE")`, sends it to the backend gate-evaluation endpoint (§13) — **the pass/fail accuracy threshold is a backend decision, not a hardcoded on-device constant** (§17). `ActiveTrackingScreen` requests periodic breadcrumb fixes (interval TBD by field-testing battery impact — start at ~15–30s, not the UI's claimed "5 seconds," since that cadence was never real) written as `GpsPointEntity(source="BREADCRUMB")` rows, feeding the currently-empty placeholder trail. No background/foreground-service tracking is proposed — everything happens while the app is in the foreground during an active inspection, consistent with current manifest permissions (no `ACCESS_BACKGROUND_LOCATION`, no foreground-service permission declared, and nothing in the request suggests background tracking is actually needed). Hazard-marker drop (`ActiveTrackingScreen`) and hazard-pin drop (`RouteMapScreen`, currently a literal no-op) both write `GpsPointEntity(source="HAZARD_MARKER")`. Offline GPS points are stored exactly like every other synced entity — Room row with `syncState`, batched to the backend via the metadata sync worker (§9, endpoint #6).

## 8. Camera / Evidence Architecture

Today: the one real device integration. `EvidenceCaptureScreen.kt` — real CAMERA permission, real system-camera intent (`ActivityResultContracts.TakePicture()`, not CameraX), real file write to `getExternalFilesDir(Pictures)`, real `FileProvider`. Gap: only `CameraState.lastCapturedUri` (single `Uri?`) is remembered, so 1st/2nd photos of a 3-photo section quota are silently dropped from app state (files remain orphaned on disk).

Planned: capture flow stays as-is (no CameraX migration needed — the existing intent-based flow works and rewriting it isn't warranted). On capture success, instead of setting a singleton, insert an `EvidenceEntity` row: `inspectionId`, `sectionIndex`, `localFilePath`, `capturedAt`, `gpsPointId` (most recent fix, nullable), `inspectorId`, on-device `fileHash` via `MessageDigest("SHA-256")` on the real file (replacing the fabricated hash string). `EvidenceDetailsScreen` then reads that real row plus the linked `GpsPointEntity` to replace every hardcoded metadata field (mine, section, coordinates, timestamp, signature).

**Upload strategy — presigned Supabase Storage URL, not multipart through FastAPI.** Photos are the largest payload the app produces, captured in a connectivity-constrained environment (underground sections); routing binaries through FastAPI just to re-forward to Storage adds a hop and doubles server bandwidth for no benefit. FastAPI's job is minting the signed URL, registering metadata, and confirming completion — the phone uploads the binary directly to Storage, retryable independent of the metadata sync. See §13 endpoints #9–11 and the two-worker split in §10.

## 9. Offline Architecture

Room (SQLite) for all structured entities in §5. Local files (photos) stay on device filesystem, referenced by path from `EvidenceEntity`, uploaded separately once online. No app functionality — capturing evidence, logging GPS, adding observations/measurements, progressing through sections — should require connectivity; only login (§11) and viewing server-computed results (risk score, threshold evaluation, AI findings) require it.

**State machine** (per-row `syncState`):
```
LOCAL → SYNC_PENDING → SYNCING → SYNCED → PROCESSING → COMPLETED
                              ↘ SYNC_FAILED (retried, returns to SYNC_PENDING until max attempts)
```

**PROCESSING/COMPLETED are not applied uniformly to every entity — they only apply where server-side processing/finalization actually happens after the row is accepted:**

- **InspectionEntity — full range applies, and matters most here.** `SYNCED` means the row was accepted; `PROCESSING` means the backend is still finalizing the submission (risk index computation, report generation); `COMPLETED` means finalization is done. This is what the `SYNCHRONIZATION`/`SUBMISSION_COMPLETE` screens should visualize.
- **EvidenceEntity — full range applies**, since photos go through server-side AI/CV processing after upload (`SYNCED` = file+metadata accepted, `PROCESSING` = AI pipeline running, `COMPLETED` = result attached).
- **GpsPointEntity — `SYNCED` is terminal.** No server-side processing exists for a GPS point; `PROCESSING`/`COMPLETED` are not used for this entity at all.
- **ObservationEntity/MeasurementEntity — `SYNCED` is terminal in the normal case.** `PROCESSING`/`COMPLETED` are only used for these if a specific measurement triggers server-side threshold evaluation the UI must wait on and reflect — not as a default for every row.

A row stuck in `SYNCING` for more than a few minutes (app killed mid-request) is reset to `SYNC_PENDING` by a startup check, since `syncState` is only set to `SYNCING` inside the same transaction as the network dispatch.

## 10. WorkManager Sync Architecture

Two workers, not one-per-entity (5 workers would be over-engineered at this scale) and not one monolithic worker (would make partial-failure handling awkward):

1. **`SyncMetadataWorker`** — enqueued immediately on any local write (`enqueueUniqueWork(..., APPEND_OR_REPLACE)`) plus a 15-min periodic fallback as a safety net. Iterates entities in dependency order — Inspection first (children reference `inspectionId`), then GpsPoint/Observation/Measurement/Evidence-metadata — upserting each `SYNC_PENDING` row. Constraint: `NetworkType.CONNECTED`.
2. **`EvidenceUploadWorker`** — one per evidence row, chained after that row's parent Inspection reaches `SYNCED`+ (checked at `doWork()` start; `Result.retry()` if not yet satisfied). Handles the binary PUT to the presigned URL independently so a stalled photo upload never blocks small JSON syncs elsewhere.

Retry: `BackoffPolicy.EXPONENTIAL` (30s initial, ~4h cap) handles automatic re-queuing; a row is only surfaced as terminally `SYNC_FAILED` (manual "retry" in the Home Sync tab) after 5 attempts.

**Idempotency:** every primary key is a client-generated UUIDv4, created at row-insert time and sent as the resource ID in the API call. Every write endpoint is an upsert keyed on that UUID (`INSERT ... ON CONFLICT (id) DO UPDATE` in Postgres/Supabase) — a retried POST after a timeout is always safe, never duplicates, and cleanly supports pre-submit edits without separate POST/PATCH branching.

## 11. Authentication Architecture

Today: `LoginScreen.kt` accepts any tap unconditionally after a fake 500ms delay — no credential check exists.

**The Inspector ID + PIN UI is preserved as-is and is not up for redesign.** The exact mechanism FastAPI uses to turn a validated Inspector ID + PIN into a Supabase Auth session is an **explicitly UNRESOLVED architecture decision** — this plan does not assume, default to, or design around a Supabase email/password or magic-link flow. It is called out here as open specifically so it is not silently decided by omission:

- **Open question:** how does FastAPI validate `{inspectorId, pin}` and obtain/issue a Supabase-backed session for it? Candidate approaches (not evaluated or chosen here) include FastAPI validating against an inspector table it owns and then minting its own JWT (not necessarily a Supabase Auth session at all), or FastAPI brokering a Supabase Auth session server-side behind the ID+PIN check. Both are legitimate and the choice affects Supabase Auth configuration, not the mobile app or its `POST /auth/login` contract.
- What **is** fixed regardless of that decision: the mobile app calls `POST /auth/login` with `{inspectorId, pin}` and receives back `{accessToken, refreshToken, expiresIn, inspector}` (§12–13, endpoint #1) — the Android-side contract does not change based on how the backend resolves the open question above. This must be settled explicitly in a future conversation before backend auth implementation begins, not inferred from general Supabase conventions.

Token storage: `androidx.security:security-crypto` `EncryptedSharedPreferences` — justified over DataStore here specifically because this is a single small (2–3 string values) security-sensitive blob with no existing SharedPreferences to migrate away from; DataStore-with-manual-Tink-encryption would be more code for no benefit at this scale. Token refresh: `POST /api/v1/auth/refresh`; if it fails while offline, the app keeps using the cached access token until it expires, then forces re-login on next foreground. Logout clears the encrypted token store and any in-memory session state; local Room data for already-submitted inspections is retained (it's the official record's local cache), not wiped. Unauthorized (401) responses from any endpoint trigger a silent refresh attempt, then forced logout if refresh also fails — never a fabricated/offline-only session.

## 12. Backend API Requirements & 13. Request/Response Contracts

Base path `/api/v1/`. Auth: Supabase-issued JWT as `Authorization: Bearer <token>` on every endpoint except login. All writes are idempotent upserts keyed by client UUID (§10).

| # | Method & Path | Request | Response | Auth | Offline behavior | Idempotency |
|---|---|---|---|---|---|---|
| 1 | `POST /auth/login` | `{inspectorId, pin}` | `{accessToken, refreshToken, expiresIn, inspector}` | none | blocked — requires connectivity, no fabricated session | N/A |
| 2 | `POST /auth/refresh` | `{refreshToken}` | `{accessToken, expiresIn}` | refresh token | keep using cached token until expiry if offline | N/A |
| 3 | `GET /mines?assignedTo={inspectorId}` | — | `[{id, name, permitNumber, hazardIndex, evidenceQuota, sectionCount, sections:[...]}]` | JWT | serve `MineCacheEntity`/`SectionDefEntity` from Room | N/A (read) |
| 4 | `POST /inspections` | `{id, mineId, inspectorId, startedAt, gpsGateResult}` | `{id, status}` | JWT | row stays LOCAL→SYNC_PENDING; flow proceeds fully offline against local row | client UUID upsert |
| 5 | `PATCH /inspections/{id}` | partial `{status?, submittedAt?}` | updated inspection | JWT | local status transitions first, sync catches up | upsert |
| 6 | `POST /inspections/{id}/gps-points` (batch) | `{points:[...]}` | `{accepted:[ids]}` | JWT | queued, batched to cut request count | per-point UUID upsert, partial success supported |
| 7 | `POST /inspections/{id}/observations` | `{id, sectionIndex, category, severity, notes, linkedEvidenceId?, gpsPointId?, recordedAt}` | `{id, syncState}` | JWT | queued | client UUID upsert |
| 8 | `POST /inspections/{id}/measurements` | `{id, sectionIndex, metricType, value, unit, recordedAt}` | `{id, thresholdStatus}` | JWT | queued; gauge shows "pending" until synced | client UUID upsert |
| 9 | `POST /evidence` (metadata) | `{id, inspectionId, sectionIndex, capturedAt, gpsPointId?, fileHash}` | `{id, uploadUrl, uploadFields?}` | JWT | queued; photo stays viewable locally regardless of sync state | client UUID upsert |
| 10 | binary PUT to presigned URL | file bytes | `{objectPath}` | signed URL, no app JWT | `EvidenceUploadWorker` retries independently | Storage upsert-by-path is naturally idempotent |
| 11 | `POST /evidence/{id}/confirm-upload` | `{objectPath}` | `{id, uploadState, syncState}` | JWT | retried cheaply (no re-upload) if this call alone fails | idempotent no-op if already confirmed |
| 12 | `GET /inspections/{id}` | — | full nested inspection + PROCESSING/COMPLETED results (risk score, AI tags) | JWT | falls back to local Room aggregate read if offline | N/A (read) |
| 13 | `POST /inspections/{id}/submit` | `{finalGpsPointId?}` | `{status: "PROCESSING"}` | JWT | blocked client-side if any child row is still SYNC_PENDING/SYNC_FAILED — surfaced via Sync/Review screens | idempotent — repeat calls on an already-PROCESSING/COMPLETED inspection just return current status |

## 14. Error Handling

Network/5xx errors → row stays/returns to `SYNC_PENDING`, `syncAttempts++`, exponential backoff via WorkManager, no data loss. 4xx (validation) errors → `SYNC_FAILED` immediately (not retried blindly), `lastSyncError` populated for surfacing in the Sync tab UI. 401 → silent refresh, then forced logout on repeated failure (§11). Submit is blocked client-side rather than sent and rejected, whenever local sync state shows outstanding children — the app should know before asking the server.

## 15. Idempotency Strategy

Covered in full in §10 — client-generated UUIDv4 primary keys, sent as the resource ID in every write, upserted server-side. This single mechanism covers retried-after-timeout requests, pre-submit edits, and the presigned-URL re-mint case (an expired signed URL is handled by re-calling endpoint #9, itself a safe upsert, to mint a fresh one).

## 16. AI Responsibilities

**Server-side AI (all of it, per explicit constraint):** anomaly detection, PPE verification (helmet/jacket/shoes/gloves/eye protection), restricted-zone/machinery-proximity detection, roof-support/unsafe-machinery flags, blocked-exit detection, water-accumulation detection, hazard/risk scoring, threshold evaluation for measurements. None of this exists in the app today (zero ML/CV code, no TFLite/ML Kit dependency) and none of it should move on-device.

**On-device AI: not implemented in the MVP.** All AI/CV processing stays server-side, full stop, for this implementation. One narrow, deterministic (no ML model, no inference — a Laplacian-variance-style blur check) photo-quality pre-check is noted only in §21 as a possible *future* enhancement, explicitly out of MVP scope, not something this plan builds now — it is not AI in any meaningful sense (no model, no training, no inference), but it is called out separately here so it is never mistaken for an on-device AI feature that ships in this phase.

## 17. Mobile vs. Backend Responsibility Matrix

| Responsibility | Lives in |
|---|---|
| UI, navigation, theme | Android |
| Local persistence/offline cache | Android (Room) |
| Camera capture, FileProvider | Android |
| Raw GPS fix acquisition | Android |
| **GPS gate pass/fail threshold** | **Backend** (app sends raw fix, server returns pass/fail) |
| Photo blur/quality pre-check (optional) | Android — narrow exception, not a business rule |
| SHA-256 file hashing | Android — pure function of the file |
| Section unlock/progression *authoritative* state | **Backend**, mirrored as local optimistic fallback while offline |
| Photo/observation/measurement quotas per section | **Backend**-configured, cached locally (never hardcoded "3") |
| Hazard/risk index computation | **Backend/AI** |
| Measurement threshold evaluation | **Backend** |
| AI/CV evidence analysis | **Backend/AI** |
| Auth/credential validation | **Backend (Supabase Auth)** |
| Sync orchestration | Android triggers (WorkManager); Backend enforces idempotency |
| Report/PDF generation, official record | **Backend** |
| Evidence file storage | Supabase Storage (presigned URL) |
| Authorization (who can act on what) | **Backend** (RLS + FastAPI checks) — never a client-side-only filter |

## 18. Security Architecture

HTTPS-only for all API/Storage traffic. Tokens in `EncryptedSharedPreferences` (§11), never in plain SharedPreferences/logs. Room database itself is not additionally encrypted in MVP (standard Android app-sandbox file protection); flag SQLCipher as a future enhancement if the threat model requires it (device-loss/rooted-device data exposure), not required for MVP given the data classification here. **The Android app holds no Supabase credential of any kind — no service-role key, no anon/publishable key, no Supabase SDK.** All backend traffic goes through FastAPI; the sole exception is the direct-to-Storage binary PUT against a short-lived presigned URL (§19), which is credential-free by construction. Authorization is enforced server-side (RLS + FastAPI checks), never trusted from client-supplied filtering alone.

## 19. Supabase Integration

**FastAPI is the only backend API the Android app consumes.** The Android app never calls Supabase Postgres or Supabase Auth directly, holds no Supabase service-role credential, and has no Supabase Kotlin SDK dependency. The single exception — the binary PUT to a presigned Storage URL (§8, §13 #10) — is not a Supabase API call in the SDK/credentialed sense: it's a plain HTTP PUT to a short-lived, narrowly-scoped signed URL that FastAPI mints and hands to the app; it carries no persistent credential, no service-role key, and no access to Postgres or Auth. FastAPI is the sole client of Supabase Auth and Supabase Postgres, and the sole minter of Storage presigned URLs.

## 20. MVP Implementation Order

**Phase 1 — Foundation** (auth, Room, DI, sync scaffolding, no new visible screens): add Hilt/Room/Retrofit+OkHttp+kotlinx.serialization/WorkManager/security-crypto deps; introduce `MineInspectApplication` (`@HiltAndroidApp`); Room `AppDatabase` + DAOs from §5; real `AuthRepository`/`LoginViewModel` replacing the hardcoded bypass in `LoginScreen.kt`; skeleton `SyncMetadataWorker`/`EvidenceUploadWorker`.

**Phase 2 — Mine list + Inspection lifecycle**: `GET /mines` populates `MineCacheEntity`/`SectionDefEntity`; `HomeViewModel` replaces the hardcoded, identically-navigating 3-item mine queue in `HomeScreen.kt`; `POST /inspections` creates a real `InspectionEntity` on briefing start; graph-scoped `InspectionViewModel` introduced; `MineBriefingScreen.kt` migrated off hardcoded text; real `mineId`/`inspectionId` threaded through `AppNavGraph.kt`.

**Phase 3 — GPS + evidence wiring** (the two screens with real device I/O today): add `play-services-location`, activate the currently-dead location permissions, wire `GpsGateScreen.kt` to a real fix + backend gate evaluation; rework `EvidenceCaptureScreen.kt`/`EvidenceDetailsScreen.kt` to write real `EvidenceEntity` rows (real SHA-256, real linked GPS/timestamp) instead of the `CameraState`/`InspectionState` singletons; delete both singleton files once migrated; `SectionMonitorScreen.kt`/`AreasCoverageScreen.kt` progress badges switch to Room COUNT queries, eliminating the double-increment bug by construction.

**Phase 4 — Observations, measurements, the 13 unwired routes**: build all 13 reserved-route screens directly against real `ObservationEntity`/`MeasurementEntity` writes from day one, reusing existing `ui/components/`; wire real breadcrumb persistence in `ActiveTrackingScreen.kt` and fix the no-op hazard-pin button in `RouteMapScreen.kt` against `GpsPointEntity`.

**Phase 5 — Submission + sync UX**: wire `AreasCoverageScreen.kt`'s submit to the real `POST /inspections/{id}/submit`, gated on full child sync; build `OFFLINE_SAVE`/`SYNCHRONIZATION`/`SUBMISSION_COMPLETE`/`FINAL_REVIEW`/`FINAL_LOCATION_CHECK`/`INSPECTION_SUMMARY` against `SyncViewModel` (WorkManager `WorkInfo` + Room sync-state observation); replace `HomeScreen.kt`'s fake `delay(1500)` sync button with a real forced-sync trigger.

## 21. Future Enhancements (explicitly out of MVP scope)

Optional on-device photo-quality pre-check (§16); SQLCipher-encrypted Room database if threat model warrants it; background/foreground-service continuous tracking if a real operational need for it emerges (not currently justified by the manifest or the workflow); push-notification-driven sync status instead of polling `GET /inspections/{id}`; a real map SDK (Google Maps/Mapbox) for `RouteMapScreen.kt`, currently an empty placeholder Box with no SDK dependency at all.

## 22. Problems Found in Current Codebase

1. **Double photo-count increment** — `SectionMonitorScreen.kt`'s "Take Photo" increments `InspectionState` *before* navigating to camera (counts even on cancel), and `EvidenceDetailsScreen.kt`'s "Save & Continue" increments it *again* — two increments per one real photo. Fix: derive count from a Room COUNT query over `EvidenceEntity`, incremented exactly once by the actual row insert (Phase 3).
2. **`CameraState` holds one URI, not a list** — 1st/2nd photos of a 3-photo section are silently lost from app state (files orphaned on disk). Fix: `EvidenceEntity` table (Phase 3); delete `CameraState.kt`.
3. **Dead location permissions** — `ACCESS_FINE_LOCATION`/`ACCESS_COARSE_LOCATION` declared but never requested at runtime anywhere; `GpsGateScreen.kt` fabricates all GNSS data and the gate always passes. Fix: Phase 3.
4. **`EvidenceDetailsScreen.kt` shows fabricated metadata** — GPS coordinates, timestamp, and SHA-256 hash are hardcoded literals unrelated to the real photo. Fix: Phase 3.
5. **`AreasCoverageScreen.kt`'s submit is a local no-op** — "Complete & Submit" just pops back to Home; a "submitted" inspection is indistinguishable from an abandoned one. Fix: Phase 5.
6. **`RouteMapScreen.kt`'s hazard-pin button is a literal `onClick = {}`** — does nothing at all. Fix: Phase 4.
7. **`ActiveTrackingScreen.kt`'s "Drop Geo-Hazard Marker" only flashes a boolean** — no data persisted. Fix: Phase 4.
8. **Hardcoded "3 sections"/"3 photos" everywhere** — baked into `InspectionState.kt` and `AreasCoverageScreen.kt`'s completion logic. Fix: Phase 2, source from `MineCacheEntity`/`SectionDefEntity`.
9. **`LoginScreen.kt` accepts any tap as valid sign-in** — no credential check exists today. Fix: Phase 1.
10. **`HomeScreen.kt`'s mine queue rows all navigate identically** — no `mineId` threaded through, so tapping any of the 3 hardcoded rows goes to the same place. Fix: Phase 2.

## 23. Recommended Changes to Existing Android Code

None of the above require touching `ui/theme/` or the visual layout of any Composable — every fix is "replace a hardcoded literal or singleton-object read with a ViewModel-backed real value or real DAO read/write," per the user's explicit instruction not to redesign the UI. Concretely: add `MineInspectApplication.kt`; add `data/local/` (Room DB + DAOs), `data/remote/` (Retrofit services), `data/repository/` (one per aggregate: Inspection/Mine/Evidence/Auth); add one ViewModel per screen that currently touches state; delete `data/CameraState.kt` and `data/InspectionState.kt` once Phase 3 migration completes; add real `mineId`/`inspectionId` nav arguments to `AppNavGraph.kt`; add `android:name=".MineInspectApplication"` to the manifest; add the 13 missing `composable{}` entries in Phase 4.
