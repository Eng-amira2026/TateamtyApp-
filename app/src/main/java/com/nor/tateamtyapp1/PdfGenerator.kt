package com.nor.tateamtyapp1

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    private val primaryColor = "#005A9C".toColorInt() 
    private val secondaryColor = "#4CAF50".toColorInt() 

    fun generateCertificatePdf(
        context: Context,
        childName: String,
        nationalId: String,
        dob: String,
        vaccines: List<String>,
        signerName: String
    ) {
        val pdfDocument = PdfDocument()
        // تم تغيير المقاسات لتصبح عرضية (Landscape): العرض 842 والطول 595
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create() 
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // 1. رسم الإطار الخارجي المزدوج (بمقاسات العرض الجديدة)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.color = primaryColor
        canvas.drawRect(30f, 30f, 812f, 565f, paint)

        paint.strokeWidth = 2f
        canvas.drawRect(40f, 40f, 802f, 555f, paint)

        // 2. الخلفية العلوية (Header)
        paint.style = Paint.Style.FILL
        paint.color = primaryColor
        canvas.drawRect(40f, 40f, 802f, 120f, paint)

        // 3. عنوان الشهادة (في المنتصف الجديد)
        paint.color = Color.WHITE
        paint.textSize = 35f 
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("VACCINATION COMPLETION CERTIFICATE", 421f, 85f, paint)

        paint.textSize = 16f 
        paint.isFakeBoldText = false
        canvas.drawText("Federal Ministry of Health - Digital Immunization System", 421f, 110f, paint)

        // 4. بيانات الطفل (توزيع عرضي)
        var yPos = 180f
        paint.textAlign = Paint.Align.LEFT
        
        // مستطيل خلفية البيانات
        paint.color = "#F9F9F9".toColorInt()
        canvas.drawRoundRect(RectF(60f, 150f, 400f, 350f), 15f, 15f, paint)

        paint.color = primaryColor
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("Child Information", 80f, yPos, paint)

        paint.color = Color.BLACK
        paint.textSize = 17f
        yPos += 45f
        paint.isFakeBoldText = false
        canvas.drawText("Name: ", 80f, yPos, paint)
        paint.isFakeBoldText = true
        canvas.drawText(childName, 150f, yPos, paint)

        yPos += 35f
        paint.isFakeBoldText = false
        canvas.drawText("National ID: ", 80f, yPos, paint)
        paint.isFakeBoldText = true
        canvas.drawText(nationalId, 180f, yPos, paint)

        yPos += 35f
        paint.isFakeBoldText = false
        canvas.drawText("Birth Date: ", 80f, yPos, paint)
        paint.isFakeBoldText = true
        canvas.drawText(dob, 180f, yPos, paint)

        // 5. سجل التطعيمات (في جهة اليمين لتوفير المساحة)
        paint.color = primaryColor
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("Vaccination History", 450f, 180f, paint)
        
        paint.strokeWidth = 3f
        canvas.drawLine(450f, 190f, 780f, 190f, paint)

        var vYPos = 225f
        paint.textSize = 15f
        
        // تقسيم اللقاحات لعمودين إذا زادت عن 6 لكي لا تخرج من الصفحة
        vaccines.forEachIndexed { index, vaccine ->
            val xOffset = if (index > 4) 620f else 450f
            val currentVY = if (index > 4) 225f + ((index-5) * 40f) else vYPos + (index * 40f)
            
            paint.color = secondaryColor
            paint.style = Paint.Style.FILL
            canvas.drawCircle(xOffset + 10f, currentVY - 6f, 6f, paint)
            
            paint.color = Color.BLACK
            paint.isFakeBoldText = true
            canvas.drawText(vaccine, xOffset + 30f, currentVY, paint)
        }

        // 6. التذييل (Footer)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        paint.isFakeBoldText = false
        val currentDate = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Date of Issue: $currentDate", 60f, 500f, paint)

        canvas.drawText("Authorized Official: ", 60f, 535f, paint)
        paint.color = primaryColor
        paint.isFakeBoldText = true
        canvas.drawText(signerName, 200f, 535f, paint)

        // الختم الرسمي (في زاوية مميزة)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = "#D32F2F".toColorInt()
        val stampRect = RectF(650f, 430f, 780f, 530f)
        canvas.drawOval(stampRect, paint)
        
        paint.style = Paint.Style.FILL
        paint.textSize = 12f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("APPROVED", 715f, 475f, paint)
        canvas.drawText("HEALTH OFFICE", 715f, 495f, paint)

        pdfDocument.finishPage(page)

        val fileName = "Certificate_${nationalId}_Landscape.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            openPdfFile(context, file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pdfDocument.close()
    }

    private fun openPdfFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_LONG).show()
        }
    }
}
