package com.mineinspect.app.util

/**
 * Section ids appear in the UI/nav layer as strings ("1"/"A", "2"/"B", "3"/"C" — see
 * SectionStartScreen/SectionMonitorScreen's pre-existing display mapping), but Room's
 * schema (plan §5) stores sectionIndex as an Int. This is the single shared conversion
 * point so new code doesn't re-derive the mapping ad hoc.
 */
fun sectionIndexOf(sectionId: String): Int = when (sectionId.uppercase()) {
    "1", "A" -> 1
    "2", "B" -> 2
    "3", "C" -> 3
    else -> sectionId.toIntOrNull() ?: 2
}
