# AI Smart Governance & Compliance Platform for Coal Mining

A two-client system for coal mine safety governance: a **Kotlin/Jetpack Compose field inspector app** and a **Next.js officer command portal**, sharing one planned backend (FastAPI + Supabase). This repository hosts both clients.

> **Status:** Hackathon / prototype build. Both clients are UI/architecture-complete to differing degrees, but the shared backend (FastAPI + Supabase) has not been implemented yet — see [Project Status](#project-status).

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [Solution Overview](#solution-overview)
- [Repository Structure](#repository-structure)
- [Mobile App — Inspector Field App](#mobile-app--inspector-field-app)
- [Web App — Officer Command Portal](#web-app--officer-command-portal)
- [Key Features (Web Portal)](#key-features-web-portal)
- [Project Status](#project-status)
- [Technology Stack](#technology-stack)
- [Installation & Running Locally](#installation--running-locally)
- [Environment Variables](#environment-variables)
- [Future Improvements](#future-improvements)

---

## Problem Statement

Coal mine safety compliance under regulatory frameworks (e.g. DGMS in India) depends on a chain of manual, paper-heavy processes: field inspectors record statutory inspections and hazard findings on paper or disconnected tools, safety officers manually cross-reference reports to identify violations, corrective action tracking happens over email/spreadsheets with no SLA visibility, and there is no real-time, portfolio-wide view of mine risk or a tamper-evident audit trail for enforcement actions.

## Solution Overview

- **Inspector mobile app** (Kotlin/Jetpack Compose): field data capture — GPS-verified mine entry, section-by-section evidence photos, observations, and sensor measurements.
- **Officer web portal** (Next.js): command and enforcement — mine risk dashboard, inspection/anomaly review, violation issuance, CAPA workflow tracking, GIS mapping, statutory alerts, an immutable audit trail, and statutory report generation.
- **Shared backend** (planned): one FastAPI service and one Supabase Postgres database behind both clients, so a photo captured in the field flows through to the anomaly, violation, and corrective action an officer sees on the dashboard.

## Repository Structure

```text
mineguard/
├── app/                 # Kotlin/Jetpack Compose Inspector mobile app
│   └── src/
├── website/             # Next.js officer command portal
│   ├── app/             # App Router — all routes
│   ├── components/
│   ├── lib/
│   ├── types/
│   └── docs/            # Backend requirements, route map, page checklist
├── build.gradle.kts     # Android project build config
├── settings.gradle.kts
├── gradle/, gradlew*
├── INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md
├── README.md
└── .gitignore
```

## Mobile App — Inspector Field App

Kotlin 2.1.20 / Jetpack Compose app (`app/`) for field inspectors. Real device I/O exists for camera capture; most data-persistence and sync flows are being built out per a 5-phase integration plan. Full architecture, per-screen implementation status, and the planned Room/Hilt/WorkManager/Retrofit stack are documented in [`INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md`](INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md).

## Web App — Officer Command Portal

Next.js 14 (App Router) application (`website/`) for safety officers and administrators — see [Key Features](#key-features-web-portal) below. Backend requirements, the full route map, and the Stitch-design-to-route mapping live in `website/docs/`.

## Key Features (Web Portal)

- **Dashboard & Monitoring** — executive KPI overview, high-risk mine radar, statutory alert feed, GIS overview.
- **Inspection & Violation Management** — inspection registry and dossiers, anomaly deep-dives, statutory violations register with Section 22 order tracking and deadline countdowns.
- **Corrective Actions (CAPA)** — Kanban workflow across the full remediation lifecycle (Open → In Progress → Evidence Submitted → Under Verification → Closed), SLA countdowns, verification sign-off.
- **Evidence Vault** — searchable vault for cryptographically-signed inspection evidence and regulatory documents, with a high-resolution viewer (EXIF/GPS metadata, SHA-256 hashing).
- **AI / Computer-Vision Capabilities** — evidence viewer supports AI-generated bounding-box overlays for detected hazards, plus a dedicated AI Predictive Safety & Insights view for ML-driven hazard forecasting. *(Rendered against sample data in this build — see Project Status.)*
- **GIS / Geospatial Monitoring** — interactive map with mine boundaries, sensor overlays, and inspector GPS route/breadcrumb tracking.
- **Statutory Alerts** — real-time IoT telemetry alert queue (gas spikes, strata convergence, unauthorized entry) with acknowledgement/execution actions.
- **Audit Trail** — immutable, SHA-256 hash-chained ledger view for enforcement directives and signatures.
- **Reports** — statutory report generation surface (Form IV, DGMS Monthly Compliance, incident summaries) with certified PDF export presentation.

## Project Status

| Area | Status |
|---|---|
| Web portal UI (all routes) | ✅ Fully implemented, Material 3-based design system |
| Web portal data layer | ⚠️ In-memory mock service (`website/lib/mock`), not a live database |
| Mobile app UI | ✅ 11 core screens wired; 13 additional screens planned (Phase 4) |
| Mobile device I/O | ⚠️ Camera capture is real; GPS, sync, and submission are mocked pending later phases |
| AI/CV detections | ⚠️ Rendered/planned against sample data; no live inference pipeline connected |
| Shared backend (FastAPI + Supabase) | 📝 Designed, not yet implemented |

Both clients are architected so the real backend can be plugged in without UI rewrites — the web portal already reads through a typed `lib/api` service layer, and the mobile app's integration plan is phased around the same API contract.

## Technology Stack

| Layer | Technology |
|---|---|
| Mobile | Kotlin 2.1.20, Jetpack Compose, Material 3, Coil, Navigation Compose |
| Web | Next.js 14 (App Router), TypeScript (strict), Tailwind CSS, Recharts, Lucide React |
| Planned Backend | FastAPI, Supabase (Postgres, Auth, Storage), Bearer JWT shared across both clients |

## Installation & Running Locally

### Web Portal
```bash
cd website
npm install
npm run dev
```
Open [http://localhost:3000](http://localhost:3000).

### Mobile App
Open the repository root in Android Studio, let Gradle sync, then run `app` on an emulator or device (minSdk 26).

## Environment Variables

No environment variables are required to run the current prototype builds. Once a live backend is connected, create `website/.env.local` (already covered by `.gitignore`, never committed) with variables such as:

```bash
NEXT_PUBLIC_API_BASE_URL=https://api.governance.example.gov/v1
NEXT_PUBLIC_SUPABASE_URL=
NEXT_PUBLIC_SUPABASE_ANON_KEY=
```

## Future Improvements

- Implement the shared FastAPI + Supabase backend and connect both clients to it.
- Replace the web portal's mock GIS map with a production map SDK using existing mine coordinate data.
- Wire real Supabase Auth and role-based route guarding on the web portal.
- Connect a live AI/CV pipeline to replace sample-data hazard detections.
- Complete the mobile app's remaining phases (GPS/evidence wiring, observations/measurements, submission & sync UX) per `INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md`.
- Add real PDF report generation and signed-URL delivery for the web portal's Reports module.
