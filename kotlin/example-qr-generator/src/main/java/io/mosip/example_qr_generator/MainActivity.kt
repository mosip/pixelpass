package io.mosip.example_qr_generator

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import io.mosip.pixelpass.PixelPass


class MainActivity : ComponentActivity() {

    var qrData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val textF = findViewById<View>(R.id.textF) as EditText

        textF.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pixelPass  = PixelPass()
                val bmp = pixelPass.generateQRCode(s.toString())
                findViewById<ImageView>(R.id.qrImage).setImageBitmap(bmp)
                findViewById<ImageView>(R.id.qrImage).invalidate()
            }
        })

        val qrImage = findViewById<View>(R.id.qrImage) as ImageView

        qrImage.setOnLongClickListener(OnLongClickListener {

            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", qrData)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                applicationContext, "QR data copied to clipboard",
                Toast.LENGTH_SHORT
            ).show()
            true
        })

        val decodeT = findViewById<View>(R.id.decoded) as TextView


        (findViewById<View>(R.id.btn) as Button).setOnClickListener {
            decodeT.text = PixelPass().decode(qrData)
        }


    }
}