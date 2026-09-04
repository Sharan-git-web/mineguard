package com.mineinspect.app.report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.media.ExifInterface
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.dao.SectionDefDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Builds a self-contained PDF of everything captured for an inspection, straight from
 * local Room data — no backend involved. Lets an inspector produce and view a record of
 * a completed inspection on-device before (or independent of) a server sync existing.
 */
class ReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inspectionDao: InspectionDao,
    private val evidenceDao: EvidenceDao,
    private val observationDao: ObservationDao,
    private val measurementDao: MeasurementDao,
    private val gpsPointDao: GpsPointDao,
    private val sectionDefDao: SectionDefDao
) {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    suspend fun generate(inspectionId: String): File = withContext(Dispatchers.IO) {
        val inspection = requireNotNull(inspectionDao.getById(inspectionId)) { "Inspection not found" }
        val sections = sectionDefDao.getForMine(inspection.mineId).associateBy { it.sectionIndex }
        val observations = observationDao.getAllForInspection(inspectionId)
        val measurements = measurementDao.getAllForInspection(inspectionId)
        val evidence = evidenceDao.getAllForInspection(inspectionId)
        val gpsPoints = gpsPointDao.getAllForInspection(inspectionId)

        val doc = PdfDocument()
        val writer = ReportPageWriter(doc)

        writer.coverBand("MineInspect", "Inspection Report")
        writer.infoCard(
            listOfNotNull(
                "Inspection ID" to inspection.id.take(13),
                "Mine ID" to inspection.mineId,
                "Inspector" to inspection.inspectorId,
                "Status" to inspection.status,
                "Started" to dateFormat.format(Date(inspection.startedAt)),
                inspection.submittedAt?.let { "Submitted" to dateFormat.format(Date(it)) },
                inspection.gpsGateResult?.let { "GPS Gate" to it },
                "Generated" to dateFormat.format(Date())
            )
        )

        writer.statRow(
            listOf(
                "${evidence.size}" to "Photos",
                "${observations.size}" to "Observations",
                "${measurements.size}" to "Measurements",
                "${gpsPoints.size}" to "GPS Points"
            )
        )

        writer.sectionHeading("Observations")
        if (observations.isEmpty()) {
            writer.emptyState("No observations recorded.")
        } else {
            observations.forEach { o ->
                val label = sections[o.sectionIndex]?.label ?: "Section ${o.sectionIndex}"
                writer.entryCard(
                    title = "$label  ·  ${o.category}",
                    badge = o.severity,
                    badgeColor = severityColor(o.severity),
                    detail = o.notes,
                    meta = dateFormat.format(Date(o.recordedAt))
                )
            }
        }

        writer.sectionHeading("Measurements")
        if (measurements.isEmpty()) {
            writer.emptyState("No measurements recorded.")
        } else {
            measurements.forEach { m ->
                val label = sections[m.sectionIndex]?.label ?: "Section ${m.sectionIndex}"
                writer.entryCard(
                    title = "$label  ·  ${m.metricType}",
                    badge = m.thresholdStatus,
                    badgeColor = m.thresholdStatus?.let { thresholdColor(it) },
                    detail = "${formatValue(m.value)} ${m.unit}",
                    meta = dateFormat.format(Date(m.recordedAt))
                )
            }
        }

        writer.sectionHeading("GPS Points")
        if (gpsPoints.isEmpty()) {
            writer.emptyState("No GPS points recorded.")
        } else {
            val columns = listOf("Latitude" to 90f, "Longitude" to 90f, "Accuracy" to 60f, "Source" to 100f, "Time" to 175f)
            writer.tableHeader(columns)
            gpsPoints.forEachIndexed { i, g ->
                writer.tableRow(
                    listOf(
                        "%.6f".format(g.latitude) to 90f,
                        "%.6f".format(g.longitude) to 90f,
                        "±${g.accuracyMeters.toInt()}m" to 60f,
                        g.source to 100f,
                        dateFormat.format(Date(g.capturedAt)) to 175f
                    ),
                    shaded = i % 2 == 1
                )
            }
        }

        writer.sectionHeading("Photo Evidence")
        if (evidence.isEmpty()) {
            writer.emptyState("No photos captured.")
        } else {
            evidence.forEach { e ->
                val label = sections[e.sectionIndex]?.label ?: "Section ${e.sectionIndex}"
                val bitmap = decodeSampledBitmap(e.localFilePath, 500)
                writer.photo(bitmap, "$label — ${dateFormat.format(Date(e.capturedAt))}")
                bitmap?.recycle()
            }
            writer.endPhotoGrid()
        }

        writer.finish()

        val outDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val fileName = "inspection_${inspectionId.take(8)}_${System.currentTimeMillis()}.pdf"
        val outFile = File(outDir, fileName)
        FileOutputStream(outFile).use { doc.writeTo(it) }
        doc.close()
        outFile
    }

    private fun formatValue(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

    private fun severityColor(severity: String): Int = when (severity.uppercase(Locale.getDefault())) {
        "LOW" -> 0xFF16A34A.toInt()
        "MED", "MEDIUM" -> 0xFFD97706.toInt()
        "HIGH" -> 0xFFEA580C.toInt()
        "CRITICAL" -> 0xFFDC2626.toInt()
        else -> 0xFF6B7280.toInt()
    }

    private fun thresholdColor(status: String): Int {
        val s = status.uppercase(Locale.getDefault())
        return when {
            "EXCEED" in s || "BREACH" in s || "CRITICAL" in s || "FAIL" in s -> 0xFFDC2626.toInt()
            "WARN" in s -> 0xFFD97706.toInt()
            else -> 0xFF16A34A.toInt()
        }
    }

    private fun decodeSampledBitmap(path: String, maxDimen: Int): Bitmap? {
        if (!File(path).exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        var sample = 1
        while (bounds.outWidth / sample > maxDimen * 2 || bounds.outHeight / sample > maxDimen * 2) {
            sample *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = BitmapFactory.decodeFile(path, opts) ?: return null
        return applyExifRotation(bitmap, path)
    }

    /** Camera photos carry an EXIF orientation tag rather than being stored pre-rotated;
     *  without this, portrait shots render sideways in the PDF. */
    private fun applyExifRotation(bitmap: Bitmap, path: String): Bitmap {
        val orientation = try {
            ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } catch (e: IOException) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated !== bitmap) bitmap.recycle()
        return rotated
    }
}

/** Paginated PDF layout: a colored cover band, an info card, stat tiles, card-style entry
 *  rows with severity badges, a compact GPS table, and a 2-column photo grid. Manual Canvas
 *  drawing throughout since PdfDocument has no layout system of its own. */
private class ReportPageWriter(private val doc: PdfDocument) {
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 40f
    private val contentWidth = pageWidth - margin * 2

    private val cPrimary = 0xFF1D4ED8.toInt()
    private val cText = 0xFF111827.toInt()
    private val cTextSecondary = 0xFF6B7280.toInt()
    private val cBorder = 0xFFE2E5EA.toInt()
    private val cCardBg = 0xFFF7F8FA.toInt()
    private val cWhite = 0xFFFFFFFF.toInt()

    private val pTitle = Paint().apply { textSize = 21f; isFakeBoldText = true; color = cWhite; isAntiAlias = true }
    private val pSubtitle = Paint().apply { textSize = 11.5f; color = cWhite; alpha = 220; isAntiAlias = true }
    private val pSectionHeading = Paint().apply { textSize = 13f; isFakeBoldText = true; color = cText; isAntiAlias = true }
    private val pLabel = Paint().apply { textSize = 8.5f; isFakeBoldText = true; color = cTextSecondary; isAntiAlias = true }
    private val pBody = Paint().apply { textSize = 10f; color = cText; isAntiAlias = true }
    private val pBodySmall = Paint().apply { textSize = 9f; color = cText; isAntiAlias = true }
    private val pSecondary = Paint().apply { textSize = 9f; color = cTextSecondary; isAntiAlias = true }
    private val pEntryTitle = Paint().apply { textSize = 10.5f; isFakeBoldText = true; color = cText; isAntiAlias = true }
    private val pStatNumber = Paint().apply { textSize = 18f; isFakeBoldText = true; color = cPrimary; isAntiAlias = true; textAlign = Paint.Align.CENTER }
    private val pStatLabel = Paint().apply { textSize = 8.5f; color = cTextSecondary; isAntiAlias = true; textAlign = Paint.Align.CENTER }
    private val pRunningHeader = Paint().apply { textSize = 8.5f; color = cTextSecondary; isAntiAlias = true }
    private val pBadgeText = Paint().apply { textSize = 8f; color = cWhite; isAntiAlias = true; textAlign = Paint.Align.CENTER }
    private val pPageNum = Paint().apply { textSize = 9f; color = cTextSecondary; isAntiAlias = true; textAlign = Paint.Align.CENTER }
    private val pBorder = Paint().apply { color = cBorder; style = Paint.Style.STROKE; strokeWidth = 0.75f }
    private val pCardBg = Paint().apply { color = cCardBg; style = Paint.Style.FILL }
    private val pBand = Paint().apply { color = cPrimary; style = Paint.Style.FILL }
    private val pAccentBar = Paint().apply { color = cPrimary; style = Paint.Style.FILL; alpha = 160 }

    private var pageNum = 0
    private var page: PdfDocument.Page = newPage(isFirst = true)
    private var canvas: Canvas = page.canvas
    private var y = 20f

    private var photoSlot = 0
    private var photoRowTop = 0f
    private val photoCellH = 190f

    private fun newPage(isFirst: Boolean = false): PdfDocument.Page {
        pageNum++
        val info = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
        val p = doc.startPage(info)
        if (!isFirst) {
            val c = p.canvas
            c.drawText("MineInspect — Inspection Report", margin, margin - 14f, pRunningHeader)
            c.drawLine(margin, margin - 6f, pageWidth - margin, margin - 6f, pBorder)
        }
        return p
    }

    private fun finishCurrentPage() {
        canvas.drawText("Page $pageNum", pageWidth / 2f, pageHeight - 20f, pPageNum)
        doc.finishPage(page)
    }

    private fun ensureSpace(needed: Float) {
        if (y + needed > pageHeight - margin - 10f) {
            finishCurrentPage()
            page = newPage()
            canvas = page.canvas
            y = margin + 14f
        }
    }

    fun coverBand(title: String, subtitle: String) {
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 78f, pBand)
        canvas.drawText(title, margin, 36f, pTitle)
        canvas.drawText(subtitle, margin, 56f, pSubtitle)
        y = 78f + 22f
    }

    fun infoCard(pairs: List<Pair<String, String>>) {
        val rows = (pairs.size + 1) / 2
        val rowH = 22f
        val cardH = rows * rowH + 18f
        ensureSpace(cardH + 16f)
        val top = y
        val rect = RectF(margin, top, pageWidth - margin, top + cardH)
        canvas.drawRoundRect(rect, 6f, 6f, pCardBg)
        canvas.drawRoundRect(rect, 6f, 6f, pBorder)
        val colW = contentWidth / 2f
        pairs.forEachIndexed { i, (k, v) ->
            val col = i % 2
            val row = i / 2
            val x = margin + 16f + col * colW
            val ty = top + 22f + row * rowH
            canvas.drawText(k.uppercase(Locale.getDefault()), x, ty, pLabel)
            canvas.drawText(v, x, ty + 13f, pBody)
        }
        y = top + cardH + 18f
    }

    fun statRow(stats: List<Pair<String, String>>) {
        val gutter = 10f
        val boxW = (contentWidth - gutter * (stats.size - 1)) / stats.size
        val boxH = 54f
        ensureSpace(boxH + 20f)
        stats.forEachIndexed { i, (value, label) ->
            val x = margin + i * (boxW + gutter)
            val rect = RectF(x, y, x + boxW, y + boxH)
            canvas.drawRoundRect(rect, 6f, 6f, pCardBg)
            canvas.drawRoundRect(rect, 6f, 6f, pBorder)
            canvas.drawText(value, x + boxW / 2f, y + 28f, pStatNumber)
            canvas.drawText(label.uppercase(Locale.getDefault()), x + boxW / 2f, y + 43f, pStatLabel)
        }
        y += boxH + 20f
    }

    fun sectionHeading(text: String) {
        ensureSpace(28f)
        canvas.drawRect(margin, y - 10f, margin + 3.5f, y + 3f, pBand)
        canvas.drawText(text, margin + 12f, y, pSectionHeading)
        y += 9f
        canvas.drawLine(margin, y, pageWidth - margin, y, pBorder)
        y += 16f
    }

    fun emptyState(text: String) {
        ensureSpace(18f)
        canvas.drawText(text, margin, y, pSecondary)
        y += 22f
    }

    /** A titled row card: bold title with an optional right-aligned status badge, a wrapped
     *  detail line, and a small meta (timestamp) line. Used for both observations and
     *  measurements so they read consistently. */
    fun entryCard(title: String, badge: String?, badgeColor: Int?, detail: String, meta: String) {
        val detailLines = wrap(detail, pBody, contentWidth - 24f)
        val lineH = 13f
        val h = 16f + detailLines.size * lineH + 14f
        ensureSpace(h + 10f)
        val top = y
        canvas.drawRect(margin, top - 2f, margin + 2.5f, top + h - 8f, pAccentBar)
        val tx = margin + 12f

        if (badge != null && badgeColor != null) {
            val badgePaint = Paint().apply { color = badgeColor; isAntiAlias = true }
            val badgeW = pBadgeText.measureText(badge) + 14f
            val bx = pageWidth - margin - badgeW
            canvas.drawRoundRect(RectF(bx, top - 2f, bx + badgeW, top + 11f), 6f, 6f, badgePaint)
            canvas.drawText(badge, bx + badgeW / 2f, top + 7f, pBadgeText)
            val titleMaxWidth = bx - tx - 8f
            canvas.drawText(ellipsize(title, pEntryTitle, titleMaxWidth), tx, top + 8f, pEntryTitle)
        } else {
            canvas.drawText(title, tx, top + 8f, pEntryTitle)
        }

        var ly = top + 8f + 16f
        detailLines.forEach { line ->
            canvas.drawText(line, tx, ly, pBody)
            ly += lineH
        }
        canvas.drawText(meta, tx, ly + 3f, pSecondary)
        y = top + h + 8f
        canvas.drawLine(margin, y - 6f, pageWidth - margin, y - 6f, pBorder)
    }

    fun tableHeader(columns: List<Pair<String, Float>>) {
        ensureSpace(22f)
        canvas.drawRect(margin, y - 10f, pageWidth - margin, y + 4f, pCardBg)
        var x = margin
        columns.forEach { (label, w) ->
            canvas.drawText(label.uppercase(Locale.getDefault()), x + 6f, y, pLabel)
            x += w
        }
        y += 8f
        canvas.drawLine(margin, y, pageWidth - margin, y, pBorder)
        y += 13f
    }

    fun tableRow(columns: List<Pair<String, Float>>, shaded: Boolean) {
        ensureSpace(16f)
        if (shaded) canvas.drawRect(margin, y - 10f, pageWidth - margin, y + 4f, pCardBg)
        var x = margin
        columns.forEach { (text, w) ->
            canvas.drawText(ellipsize(text, pBodySmall, w - 8f), x + 6f, y, pBodySmall)
            x += w
        }
        y += 16f
    }

    /** 2-column photo grid — call once per photo, then [endPhotoGrid] to close a trailing
     *  single-photo row. Each image is drawn aspect-fit (never stretched) inside its cell. */
    fun photo(bitmap: Bitmap?, caption: String) {
        val gutter = 12f
        val cellW = (contentWidth - gutter) / 2f
        val col = photoSlot % 2
        if (col == 0) {
            ensureSpace(photoCellH + 12f)
            photoRowTop = y
        }
        val x = margin + col * (cellW + gutter)
        val top = photoRowTop
        val rect = RectF(x, top, x + cellW, top + photoCellH)
        canvas.drawRoundRect(rect, 6f, 6f, pCardBg)
        canvas.drawRoundRect(rect, 6f, 6f, pBorder)
        val imgBox = RectF(x + 8f, top + 8f, x + cellW - 8f, top + photoCellH - 28f)
        if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
            val scale = minOf(imgBox.width() / bitmap.width, imgBox.height() / bitmap.height, 1f)
            val w = bitmap.width * scale
            val h = bitmap.height * scale
            val left = imgBox.left + (imgBox.width() - w) / 2f
            val top2 = imgBox.top + (imgBox.height() - h) / 2f
            canvas.drawBitmap(bitmap, null, RectF(left, top2, left + w, top2 + h), null)
        } else {
            canvas.drawText("Image unavailable", x + 14f, top + photoCellH / 2f, pSecondary)
        }
        val capLines = wrap(caption, pSecondary, cellW - 16f).take(2)
        var cy = top + photoCellH - 20f
        capLines.forEach { line ->
            canvas.drawText(line, x + 8f, cy, pSecondary)
            cy += 11f
        }
        if (col == 1) y = photoRowTop + photoCellH + 14f
        photoSlot++
    }

    fun endPhotoGrid() {
        if (photoSlot % 2 == 1) y = photoRowTop + photoCellH + 14f
    }

    private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) > maxWidth && current.isNotEmpty()) {
                lines.add(current.toString())
                current = StringBuilder(word)
            } else {
                current = StringBuilder(candidate)
            }
        }
        if (current.isNotEmpty()) lines.add(current.toString())
        return lines
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + "…") > maxWidth) end--
        return text.substring(0, end) + "…"
    }

    fun finish() {
        finishCurrentPage()
    }
}
