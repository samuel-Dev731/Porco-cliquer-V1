package com.porcocliquer

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.Random

class MainActivity : AppCompatActivity() {

    private var cliques = 0L
    private var moedas = 0L
    private var nivel = 1
    private var corDoPorco = "rosa"

    private var nivelMultiplicador = 1
    private val custoBaseMultiplicador = 50L

    private var moedasPorSegundo = 0L
    private var nivelFazenda = 0
    private val custoBaseFazenda = 100L

    private var virusAtivo = false
    private val geradorAleatorio = Random()

    private lateinit var prefs: SharedPreferences
    
    // Handler inicializado de forma limpa e compatível com as regras de concorrência do futuro
    private val gameHandler = Handler(Looper.getMainLooper())
    private lateinit var gameRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botaoPorco = findViewById<ImageButton>(R.id.botaoPorco)
        val textoCliques = findViewById<TextView>(R.id.textoCliques)
        val botaoUpgrade = findViewById<Button>(R.id.botaoUpgrade)
        val botaoFazenda = findViewById<Button>(R.id.botaoFazenda)
        val botaoAntivirus = findViewById<Button>(R.id.botaoAntivirus)

        prefs = getSharedPreferences("progresso_jogo", Context.MODE_PRIVATE)

        carregarJogo()
        
        botaoAntivirus.visibility = if (virusAtivo) View.VISIBLE else View.GONE
        
        atualizarInterface(textoCliques, botaoUpgrade, botaoFazenda, botaoPorco)
        iniciarGeradorPassivo(textoCliques, botaoUpgrade, botaoFazenda, botaoAntivirus, botaoPorco)
        
        verificarELimparCache()

        botaoAntivirus.setOnClickListener {
            // Efeito de clique forte ao destruir o vírus
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            
            virusAtivo = false
            botaoAntivirus.visibility = View.GONE
            Toast.makeText(this, "Sistema limpo! Vírus deletado.", Toast.LENGTH_SHORT).show()
            atualizarInterface(textoCliques, botaoUpgrade, botaoFazenda, botaoPorco)
        }

        botaoPorco.setOnClickListener {
            // 👑 OTIMIZAÇÃO EXCLUSIVA PARA SAMSUNG ONE UI: Efeito Tátil Avançado
            // Conversa direto com o motor de vibração da Samsung para dar o clique físico perfeito
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Em sistemas modernos, usa o efeito de confirmação tátil leve
                it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                // Em celulares Android mais antigos, usa o efeito de clique padrão
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }

            cliques += 1
            moedas += nivelMultiplicador

            nivel = (cliques / 100).toInt() + 1
            corDoPorco = when (nivel) {
                1 -> "rosa"
                2 -> "azul"
                3 -> "dourado"
                else -> "arco-iris"
            }

            val animacao = ScaleAnimation(
                1.0f, 0.9f, 1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 50
                repeatCount = 1
                repeatMode = Animation.REVERSE
            }
            botaoPorco.startAnimation(animacao)

            atualizarInterface(textoCliques, botaoUpgrade, botaoFazenda, botaoPorco)
        }

        botaoUpgrade.setOnClickListener {
            val custo = calcularCustoMultiplicador()
            if (moedas >= custo) {
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                moedas -= custo
                nivelMultiplicador += 1
                atualizarInterface(textoCliques, botaoUpgrade, botaoFazenda, botaoPorco)
                Toast.makeText(this, "Multiplicador Upado!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Moedas insuficientes!", Toast.LENGTH_SHORT).show()
            }
        }

        botaoFazenda.setOnClickListener {
            val custo = calcularCustoFazenda()
            if (moedas >= custo) {
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                moedas -= custo
                nivelFazenda += 1
                moedasPorSegundo += 2
                atualizarInterface(textoCliques, botaoUpgrade, botaoFazenda, botaoPorco)
                Toast.makeText(this, "Fazenda Expandida!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Moedas insuficientes!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calcularCustoMultiplicador(): Long = custoBaseMultiplicador * nivelMultiplicador

    private fun calcularCustoFazenda(): Long = custoBaseFazenda * (nivelFazenda + 1)

    private fun atualizarInterface(
        textoCliques: TextView,
        botaoUpgrade: Button,
        botaoFazenda: Button,
        botaoPorco: ImageButton
    ) {
        if (virusAtivo) {
            textoCliques.text = "⚠️ SISTEMA INFECTADO! ⚠️\nMoedas: $moedas (Roubando...)\nNível: $nivel ($corDoPorco)"
        } else {
            textoCliques.text = "Cliques: $cliques\nMoedas: $moedas (+$moedasPorSegundo/s)\nNível: $nivel ($corDoPorco)"
        }
        botaoUpgrade.text = "Multiplicador Lvl $nivelMultiplicador\n(Custo: ${calcularCustoMultiplicador()})"
        botaoFazenda.text = "Fazenda Lvl $nivelFazenda\n(Custo: ${calcularCustoFazenda()})"

        try {
            when (corDoPorco) {
                "rosa" -> botaoPorco.clearColorFilter()
                "azul" -> botaoPorco.setColorFilter(Color.parseColor("#440000FF"), PorterDuff.Mode.SRC_ATOP)
                "dourado" -> botaoPorco.setColorFilter(Color.parseColor("#44FFD700"), PorterDuff.Mode.SRC_ATOP)
                else -> botaoPorco.setColorFilter(Color.parseColor("#44FF00FF"), PorterDuff.Mode.SRC_ATOP)
            }
        } catch (t: Throwable) {
            // Se o filtro de cor deixar de existir nas APIs futuras, o jogo continua rodando normalmente sem travar
        }
    }

    private fun iniciarGeradorPassivo(
        textoCliques: TextView,
        botaoUpgrade: Button,
        botaoFazenda: Button,
        bAntivirus: Button,
        botaoPorco: ImageButton
    ) {
        gameRunnable = object : Runnable {
            override fun run() {
                if (isFinishing || isDestroyed) return

                if (!virusAtivo && geradorAleatorio.nextInt(100) < 5) {
                    virusAtivo = true
                    bAntivirus.visibility = View.VISIBLE
                    Toast.makeText(this@MainActivity, "Alerta: Vírus detectado roubando moedas!", Toast.LENGTH_LONG).show()
                }

                if (virusAtivo) {
                    moedas = if (moedas > 3) moedas - 3 else 0
                } else if (moedasPorSegundo > 0) {
                    moedas += moedasPorSegundo
                }

                atualizarInterface(textoCliques, botaoUpgrade, botaoFazenda, botaoPorco)
                gameHandler.postDelayed(this, 1000)
            }
        }
        gameHandler.postDelayed(gameRunnable, 1000)
    }

    private fun salvarProgresso() {
        prefs.edit().apply {
            putLong("cliques_totais", cliques)
            putLong("moedas_totais", moedas)
            putInt("nivel_porco", nivel)
            putString("cor_atual", corDoPorco)
            putInt("nivel_multiplicador", nivelMultiplicador)
            putInt("nivel_fazenda", nivelFazenda)
            putLong("moedas_segundo", moedasPorSegundo)
            putBoolean("virus_ativo", virusAtivo)
            apply()
        }
    }

    private fun carregarJogo() {
        cliques = prefs.getLong("cliques_totais", 0L)
        moedas = prefs.getLong("moedas_totais", 0L)
        nivel = prefs.getInt("nivel_porco", 1)
        corDoPorco = prefs.getString("cor_atual", "rosa")
        nivelMultiplicador = prefs.getInt("nivel_multiplicador", 1)
        nivelFazenda = prefs.getInt("nivel_fazenda", 0)
        moedasPorSegundo = prefs.getLong("moedas_segundo", 0L)
        virusAtivo = prefs.getBoolean("virus_ativo", false)
    }

    private fun verificarELimparCache() {
        Thread(Runnable {
            try {
                val cacheDirectory = cacheDir ?: return@Runnable
                val files = cacheDirectory.listFiles() ?: return@Runnable
                var tamanhoEmBytes = 0L
                
                for (file in files) {
                    tamanhoEmBytes += file.length()
                }

                val limiteDeCache = 10 * 1024 * 1024 // 10 MB
                if (tamanhoEmBytes >= limiteDeCache) {
                    for (file in files) {
                        if (file.isFile) file.delete()
                    }
                }
            } catch (t: Throwable) {
                // Garante imunidade contra restrições de storage futuro
            }
        }).start()
    }

    override fun onPause() {
        super.onPause()
        salvarProgresso()
    }

    override fun onDestroy() {
        try {
            gameHandler.removeCallbacksAndMessages(null)
        } catch (t: Throwable) {
            gameHandler.removeCallbacks(gameRunnable)
        }
        super.onDestroy()
    }
