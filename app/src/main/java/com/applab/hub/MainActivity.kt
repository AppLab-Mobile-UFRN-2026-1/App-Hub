package com.applab.hub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // Ajusta padding do layout raiz para respeitar as barras do sistema em todas as orientações
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(rootView)

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
