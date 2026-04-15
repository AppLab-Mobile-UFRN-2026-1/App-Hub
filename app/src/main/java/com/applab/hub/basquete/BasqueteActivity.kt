package com.applab.hub.basquete

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.applab.hub.R

class BasqueteActivity : AppCompatActivity() {

    private var pontuacaoTimeA: Int = 0
    private var pontuacaoTimeB: Int = 0

    private lateinit var pTimeA: TextView
    private lateinit var pTimeB: TextView
    private lateinit var textCronometro: TextView

    private var countDownTimer: CountDownTimer? = null
    private var tempoRestanteInMillis: Long = 600000
    private var timerRodando: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        if (savedInstanceState != null) {
            pontuacaoTimeA = savedInstanceState.getInt("pontuacaoA", 0)
            pontuacaoTimeB = savedInstanceState.getInt("pontuacaoB", 0)
            tempoRestanteInMillis = savedInstanceState.getLong("tempoRestante", 600000)
            timerRodando = savedInstanceState.getBoolean("timerRodando", false)
        }

        setContentView(R.layout.basquete_activity_main)
        setupViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.basquete_activity_main)
        setupViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("pontuacaoA", pontuacaoTimeA)
        outState.putInt("pontuacaoB", pontuacaoTimeB)
        outState.putLong("tempoRestante", tempoRestanteInMillis)
        outState.putBoolean("timerRodando", timerRodando)
    }

    private fun setupViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_basquete)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Toolbar branca cobre a status bar (igual ao MatrizFoco e Calculadora)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        // Padding inferior para a barra de navegação
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, 0, 0, navBar)
            insets
        }

        pTimeA = findViewById(R.id.placarTimeA)
        pTimeB = findViewById(R.id.placarTimeB)
        textCronometro = findViewById(R.id.cronometro)

        pTimeA.text = pontuacaoTimeA.toString()
        pTimeB.text = pontuacaoTimeB.toString()
        atualizarTextoCronometro()

        if (timerRodando) {
            iniciarTimer()
        }

        val bTresPontosTimeA: Button = findViewById(R.id.tresPontosA)
        val bDoisPontosTimeA: Button = findViewById(R.id.doisPontosA)
        val bTLivreTimeA: Button = findViewById(R.id.tiroLivreA)
        val bTresPontosTimeB: Button = findViewById(R.id.tresPontosB)
        val bDoisPontosTimeB: Button = findViewById(R.id.doisPontosB)
        val bTLivreTimeB: Button = findViewById(R.id.tiroLivreB)
        val bReiniciar: Button = findViewById(R.id.reiniciarPartida)

        bTresPontosTimeA.setOnClickListener { adicionarPontos(3, "A") }
        bDoisPontosTimeA.setOnClickListener { adicionarPontos(2, "A") }
        bTLivreTimeA.setOnClickListener { adicionarPontos(1, "A") }
        bTresPontosTimeB.setOnClickListener { adicionarPontos(3, "B") }
        bDoisPontosTimeB.setOnClickListener { adicionarPontos(2, "B") }
        bTLivreTimeB.setOnClickListener { adicionarPontos(1, "B") }

        bReiniciar.setOnClickListener { reiniciarPartida() }

        textCronometro.setOnClickListener {
            if (timerRodando) pausarTimer() else iniciarTimer()
        }
    }

    private fun adicionarPontos(pontos: Int, time: String) {
        if (time == "A") pontuacaoTimeA += pontos else pontuacaoTimeB += pontos
        atualizaPlacar(time)

        if (pontuacaoTimeA >= 100) mostrarTelaVencedor("TIME A")
        else if (pontuacaoTimeB >= 100) mostrarTelaVencedor("TIME B")
    }

    private fun atualizaPlacar(time: String) {
        if (time == "A") pTimeA.text = pontuacaoTimeA.toString()
        else pTimeB.text = pontuacaoTimeB.toString()
    }

    private fun iniciarTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(tempoRestanteInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempoRestanteInMillis = millisUntilFinished
                atualizarTextoCronometro()
            }

            override fun onFinish() {
                timerRodando = false
                Toast.makeText(this@BasqueteActivity, "Fim de Quarto!", Toast.LENGTH_LONG).show()
            }
        }.start()
        timerRodando = true
    }

    private fun pausarTimer() {
        countDownTimer?.cancel()
        timerRodando = false
    }

    private fun atualizarTextoCronometro() {
        val minutos = (tempoRestanteInMillis / 1000) / 60
        val segundos = (tempoRestanteInMillis / 1000) % 60
        textCronometro.text = String.format("%02d:%02d", minutos, segundos)
    }

    private fun reiniciarPartida() {
        pontuacaoTimeA = 0
        pTimeA.text = pontuacaoTimeA.toString()
        pontuacaoTimeB = 0
        pTimeB.text = pontuacaoTimeB.toString()

        pausarTimer()
        tempoRestanteInMillis = 600000
        atualizarTextoCronometro()

        Toast.makeText(this, "Partida e Cronômetro reiniciados", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarTelaVencedor(nomeDoTime: String) {
        pausarTimer()
        AlertDialog.Builder(this)
            .setTitle("🏆 Fim de Jogo!")
            .setMessage("O $nomeDoTime atingiu 100 pontos e venceu a partida!")
            .setCancelable(false)
            .setPositiveButton("Nova Partida") { dialog, _ ->
                reiniciarPartida()
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
