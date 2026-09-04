package com.mineinspect.app.data.local

/** Binary-upload state for EvidenceEntity, tracked separately from row-metadata syncState (plan §5). */
enum class UploadState {
    NOT_UPLOADED,
    UPLOADING,
    UPLOADED,
    UPLOAD_FAILED
}
