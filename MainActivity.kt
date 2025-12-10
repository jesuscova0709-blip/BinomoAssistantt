package com.yourname.binomoassistant

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE = 1001
    private lateinit var imgPreview: ImageView
    private lateinit var tvResult: TextView
    private lateinit var btnSelect: Button
    private lateinit var btnAnalyze: Button
    private var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgPreview = findViewById(R.id.imgPreview)
        tvResult = findViewById(R.id.tvResult)
        btnSelect = findViewById(R.id.btnSelect)
        btnAnalyze = findViewById(R.id.btnAnalyze)

        btnSelect.setOnClickListener {
            val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
            i.type = "image/*"
            startActivityForResult(i, PICK_IMAGE)
        }

        btnAnalyze.setOnClickListener {
            selectedBitmap?.let { bmp ->
                tvResult.text = "Analizando..."
                Thread {
                    val analyzer = ImageAnalyzer()
                    val result = analyzer.analyzeBitmap(bmp)
                    runOnUiThread {
                        tvResult.text = "Señal: ${'$'}{result.finalSignal}\nProb UP: ${'$'}{"%.2f".format(result.probabilityUp*100)}%"
                    }
                }.start()
            } ?: run {
                tvResult.text = "Primero selecciona una captura"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val input: InputStream? = contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(input)
                input?.close()
                selectedBitmap = bmp
                imgPreview.setImageBitmap(bmp)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
