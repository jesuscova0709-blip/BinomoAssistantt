package com.yourname.binomoassistant

import android.graphics.Bitmap
import kotlin.math.max
import kotlin.math.min

data class AnalysisResult(val finalSignal: String, val probabilityUp: Double)

class ImageAnalyzer {

    fun analyzeBitmap(bmp: Bitmap): AnalysisResult {
        // resize for stability
        val width = 400
        val height = 300
        val scaled = Bitmap.createScaledBitmap(bmp, width, height, true)

        // compute column averages (brightness)
        val cols = width
        val rows = height
        val series = DoubleArray(cols)
        for (c in 0 until cols) {
            var sum = 0.0
            for (r in 0 until rows) {
                val px = scaled.getPixel(c, r)
                val rC = (px shr 16) and 0xFF
                val gC = (px shr 8) and 0xFF
                val bC = px and 0xFF
                // grayscale luminance
                val lum = 0.299*rC + 0.587*gC + 0.114*bC
                sum += lum
            }
            series[c] = 255.0 - (sum / rows) // invert so higher -> higher price
        }

        // sample to N points
        val N = 60
        val sampled = DoubleArray(N)
        for (i in 0 until N) {
            val idx = (i * (cols - 1) / (N - 1))
            sampled[i] = series[idx]
        }

        // normalize
        val minv = sampled.minOrNull() ?: 0.0
        val maxv = sampled.maxOrNull() ?: 1.0
        val norm = sampled.map { (it - minv) / (maxv - minv + 1e-9) }

        // features
        val momentum = norm.last() - (if (norm.size >= 6) norm[norm.size - 6] else norm.first())
        val sma5 = norm.takeLast(min(5, norm.size)).average()
        val sma20 = norm.takeLast(min(20, norm.size)).average()
        val diff = sma5 - sma20

        var score = 0
        if (momentum > 0.0) score += 1
        if (diff > 0.0) score += 1

        val ruleConf = 0.4 + 0.3 * score
        val slope = norm.last() - norm.first()
        var mlProb = 0.5 + (slope * 0.5)
        if (mlProb < 0.01) mlProb = 0.01
        if (mlProb > 0.99) mlProb = 0.99

        val upProb = 0.6 * mlProb + 0.4 * (if (score >= 1) ruleConf else 1.0 - ruleConf)
        val finalSignal = if (upProb >= 0.5) "UP (CALL)" else "DOWN (PUT)"

        return AnalysisResult(finalSignal, upProb)
    }
}
