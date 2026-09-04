package com.mineinspect.app.data.local

/**
 * Per-row sync lifecycle (plan §9). PROCESSING/COMPLETED only mean anything for
 * entities with server-side finalization (InspectionEntity, EvidenceEntity) — other
 * entities treat SYNCED as terminal in the normal case.
 */
enum class SyncState {
    LOCAL,
    SYNC_PENDING,
    SYNCING,
    SYNCED,
    PROCESSING,
    COMPLETED,
    SYNC_FAILED
}
