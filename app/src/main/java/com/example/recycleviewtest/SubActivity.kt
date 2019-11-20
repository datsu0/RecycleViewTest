package com.example.recycleviewtest

import android.provider.Telephony.Mms.Part.FILENAME

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast

import com.google.android.material.floatingactionbutton.FloatingActionButton

import org.w3c.dom.Text

import java.io.Serializable



class SubActivity : AppCompatActivity() {
    private val figures = arrayOfNulls<String>(5)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        initViews()
    }


    private fun initViews() {
        var numPicker: NumberPicker = findViewById<NumberPicker>(R.id.numPicker0) as NumberPicker
        var numPicker1: NumberPicker = findViewById<NumberPicker>(R.id.numPicker1) as NumberPicker
        var numPicker2: NumberPicker = findViewById<NumberPicker>(R.id.numPicker2) as NumberPicker
        var numPicker3: NumberPicker = findViewById<NumberPicker>(R.id.numPicker3) as NumberPicker
        var numPicker4: NumberPicker = findViewById<NumberPicker>(R.id.numPicker4) as NumberPicker
        var button1: Button = findViewById(R.id.button1)
        var fab: FloatingActionButton = findViewById(R.id.fabMain)
        var textView1: TextView? = null
        numPicker.maxValue = 9
        numPicker.minValue = 0
        numPicker1.maxValue = 9
        numPicker1.minValue = 0
        numPicker2.maxValue = 9
        numPicker2.minValue = 0
        numPicker3.maxValue = 9
        numPicker3.minValue = 0
        numPicker4.maxValue = 9
        numPicker4.minValue = 0


        button1.setOnClickListener {
            //textView1.setText(numPicker.getValue() + "");
            //                figures[0] = String.valueOf(numPicker.getValue());
            //                figures[1] = String.valueOf(numPicker1.getValue());
            //                figures[2] = String.valueOf(numPicker2.getValue());
            //                figures[3] = String.valueOf(numPicker3.getValue());
            //                figures[4] = String.valueOf(numPicker4.getValue());
            //                String str = String.format("%s%s%s.%s%s",
            //                        figures[0], figures[1], figures[2], figures[3], figures[4]);

            val getNum = numPicker.value + numPicker1.value * 10 + numPicker2.value * 100 +
                    numPicker3.value * 1000 + numPicker4.value * 10000


            val intent = intent
            val num = intent.getIntExtra("num", 0)
            val data = intent.getStringExtra("data")
            //Toast.makeText(SubActivity.this,data,Toast.LENGTH_SHORT).show();
//            val sp = getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
//            val e = sp.edit()
//            e.putInt("num" + data!!, num + getNum)
//            e.commit()
            val intent1 = Intent(this@SubActivity, MainActivity::class.java)
            startActivity(intent1)
            finish()
        }

        fab.setOnClickListener {
            val intent = Intent(this@SubActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}