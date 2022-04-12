package com.example.pearappearanceevaluatesystemandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newEvaluateButton: Button = findViewById(R.id.new_evaluate_button)
        val viewPastEvaluateButton: Button = findViewById(R.id.view_past_evaluate_button)

        newEvaluateButton.setOnClickListener {
            val intent = Intent(applicationContext, NewEvaluatePear::class.java)
            startActivity(intent)
        }

        viewPastEvaluateButton.setOnClickListener {
            val intent = Intent(applicationContext, PearListActivity::class.java)
            startActivity(intent)
        }
    }
}
