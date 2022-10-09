package com.example.donttouchthis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)

        val fragmentW: FileChooserFragment = supportFragmentManager.findFragmentById(R.id.fragment_fileChooser) as FileChooserFragment
        val fragmentB: FileChooserFragment = supportFragmentManager.findFragmentById(R.id.fragment_fileChoose2r) as FileChooserFragment

        fragmentW.editTextPath.text = "Выбор таблицы Колес(.xls)"
        fragmentB.editTextPath.text = "Выбор таблицы Пробега на подшипника(.xls)"

        val nextWPAct = findViewById<Button>(R.id.nextWPAct)

        nextWPAct.setOnClickListener {passIntent( fragmentW,fragmentB) }
    }

    private fun passIntent(w:FileChooserFragment,b:FileChooserFragment){

        val editText = findViewById<EditText>(R.id.editText)
        val editTextText = editText.text.toString()

        val intent = Intent(
            applicationContext,
            WheelPairsActivity::class.java
        )
            intent.putExtra("wheelsFilePath", w.path)
            intent.putExtra("bringsFilePath", b.path)
            intent.putExtra("searchQ", editTextText)
        startActivity(intent)
    }
}