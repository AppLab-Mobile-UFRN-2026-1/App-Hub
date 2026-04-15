package com.applab.hub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.applab.hub.basquete.BasqueteActivity
import com.applab.hub.calculadora.CalculadoraActivity
import com.applab.hub.matrizfoco.MatrizFocoActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setContentView(R.layout.activity_main)

        findViewById<MaterialCardView>(R.id.cardBasquete).setOnClickListener {
            startActivity(Intent(this, BasqueteActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardCalculadora).setOnClickListener {
            startActivity(Intent(this, CalculadoraActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardMatriz).setOnClickListener {
            startActivity(Intent(this, MatrizFocoActivity::class.java))
        }
    }
}
