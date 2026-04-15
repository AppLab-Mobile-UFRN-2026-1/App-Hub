package com.applab.hub.calculadora

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.applab.hub.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.*

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var tvExpression: TextView
    private lateinit var scientificPanel: View
    private lateinit var btnToggleSci: ImageButton
    private lateinit var rootConstraint: ConstraintLayout

    private var currentInput: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var expressionForDisplay: String = ""
    private var isScientificMode: Boolean = false
    private var justCalculated: Boolean = false

    private val historyExpressions = mutableListOf<String>()
    private val historyResults = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setContentView(R.layout.calc_activity_main)

        val toolbar = findViewById<Toolbar>(R.id.topBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculadora"

        // Empurra o conteúdo da toolbar abaixo da status bar (igual ao MatrizFoco)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        rootConstraint  = findViewById(R.id.rootConstraint)

        // Padding para a barra de navegação em todas as orientações
        ViewCompat.setOnApplyWindowInsetsListener(rootConstraint) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, 0, sysBars.right, sysBars.bottom)
            insets
        }
        tvDisplay       = findViewById(R.id.txtResultado)
        tvExpression    = findViewById(R.id.txtExpression)
        scientificPanel = findViewById(R.id.scientificPanel)
        btnToggleSci    = findViewById(R.id.btnToggleSci)

        setupDigitButtons()
        setupOperatorButtons()
        setupActionButtons()
        setupScientificButtons()

        updateDisplay()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun View.click(action: () -> Unit) {
        setOnClickListener {
            performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            action()
        }
    }

    private fun setupDigitButtons() {
        listOf(
            "0" to R.id.btn0, "1" to R.id.btn1, "2" to R.id.btn2,
            "3" to R.id.btn3, "4" to R.id.btn4, "5" to R.id.btn5,
            "6" to R.id.btn6, "7" to R.id.btn7, "8" to R.id.btn8,
            "9" to R.id.btn9, "," to R.id.btnPonto
        ).forEach { (digit, id) ->
            findViewById<Button>(id).click { appendDigit(digit) }
        }
    }

    private fun setupOperatorButtons() {
        listOf(
            "+" to R.id.btnSomar, "−" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar, "÷" to R.id.btnDividir
        ).forEach { (op, id) ->
            findViewById<Button>(id).click { onOperator(op) }
        }
        findViewById<Button>(R.id.btnIgual).click { onEquals() }
    }

    private fun setupActionButtons() {
        findViewById<Button>(R.id.btnClear).click { clearAll() }
        findViewById<Button>(R.id.btnBackspace).click { backspace() }
        findViewById<Button>(R.id.btnPlusMinus).click { toggleSign() }
        findViewById<Button?>(R.id.btnPercent)?.click { applyPercent() }
        btnToggleSci.click { toggleScientificMode() }
        findViewById<ImageButton?>(R.id.btnHistory)?.click { showHistory() }
    }

    private fun setupScientificButtons() {
        fun bind(id: Int, action: () -> Unit) =
            findViewById<Button?>(id)?.click { action() }
        bind(R.id.btnSin)        { appendScientificFunc("sin(") }
        bind(R.id.btnCos)        { appendScientificFunc("cos(") }
        bind(R.id.btnTan)        { appendScientificFunc("tan(") }
        bind(R.id.btnLog)        { appendScientificFunc("log(") }
        bind(R.id.btnLn)         { appendScientificFunc("ln(") }
        bind(R.id.btnSqrt)       { appendScientificFunc("√(") }
        bind(R.id.btnPi)         { appendConstant("π") }
        bind(R.id.btnE)          { appendConstant("ℯ") }
        bind(R.id.btnPow)        { appendRawToken("^") }
        bind(R.id.btnOpenParen)  { appendRawToken("(") }
        bind(R.id.btnCloseParen) { appendRawToken(")") }
        bind(R.id.btnFactorial)  { applyFactorial() }
    }

    private fun lastChar(): Char? = currentInput.lastOrNull()
    private fun Char.isValueEnd() = isDigit() || this == ')' || this == 'π' || this == 'ℯ'
    private fun Char.isBinaryOp() = this == '+' || this == '−' || this == '×' || this == '÷'

    private fun appendDigit(d: String) {
        if (justCalculated) {
            currentInput = ""; expressionForDisplay = ""; justCalculated = false
        }
        val internalD = if (d == ",") "." else d

        if (internalD == ".") {
            val lastSep = currentInput.indexOfLast { it == '+' || it == '−' || it == '×' || it == '÷' || it == '^' || it == '(' }
            val segment = currentInput.substring(lastSep + 1)
            if (segment.contains(".")) return
        }

        val last = lastChar()
        if (last != null && last.isValueEnd() && !last.isDigit()) {
            currentInput += "*"
        }

        currentInput = if (currentInput == "0" && internalD != ".") internalD
                       else currentInput + internalD
        updateDisplay()
    }

    private fun evalAsDouble(expr: String): Double? =
        expr.toDoubleOrNull() ?: try { evaluateExpression(expr) } catch (_: Exception) { null }

    private fun onOperator(op: String) {
        justCalculated = false

        if (currentInput.isEmpty()) {
            if (op == "−") { currentInput = "-"; updateDisplay() }
            return
        }

        if (currentInput == "-") {
            if (op == "−") { currentInput = ""; updateDisplay() }
            return
        }

        val last = lastChar()

        if (last == '(') {
            if (op == "−") { currentInput += "-"; updateDisplay() }
            return
        }

        if (last == '.') currentInput = currentInput.dropLast(1)

        val newLast = lastChar()
        if (newLast != null && newLast.isBinaryOp()) {
            currentInput = currentInput.dropLast(1) + op
            updateDisplay()
            return
        }

        val value = evalAsDouble(currentInput)
        if (value != null) {
            if (operand == null) {
                operand = value
                expressionForDisplay = formatForDisplay(value)
            } else {
                val result = performOperation(operand!!, value, pendingOp)
                operand = result
                expressionForDisplay = formatForDisplay(result)
            }
            currentInput = ""
            pendingOp = op
            expressionForDisplay += " $op"
        } else {
            currentInput += op
            expressionForDisplay = ""
        }
        updateDisplay()
    }

    private fun onEquals() {
        if (operand != null && currentInput.isEmpty()) {
            currentInput = formatNumber(operand!!)
        }
        if (operand != null && currentInput.isNotEmpty()) {
            val value = evalAsDouble(currentInput) ?: return
            val result = performOperation(operand!!, value, pendingOp)
            val exprStr = "$expressionForDisplay ${formatForDisplay(value)}"
            addToHistory(exprStr, formatForDisplay(result))
            expressionForDisplay = "$exprStr ="
            operand = null; pendingOp = null
            currentInput = formatNumber(result)
            justCalculated = true
            updateDisplay()
        } else if (currentInput.isNotEmpty() && pendingOp == null) {
            val expr = currentInput
            try {
                val result = evaluateExpression(expr)
                addToHistory(displayString(expr), formatForDisplay(result))
                expressionForDisplay = "${displayString(expr)} ="
                currentInput = formatNumber(result)
                justCalculated = true
                updateDisplay()
            } catch (e: Exception) { showError() }
        }
    }

    private fun addToHistory(expression: String, result: String) {
        historyExpressions.add(0, expression)
        historyResults.add(0, result)
        if (historyExpressions.size > 50) {
            historyExpressions.removeAt(historyExpressions.lastIndex)
            historyResults.removeAt(historyResults.lastIndex)
        }
    }

    private fun showHistory() {
        val dialog = BottomSheetDialog(this, R.style.HistoryBottomSheet)
        val view = layoutInflater.inflate(R.layout.calc_bottom_sheet_history, null)
        dialog.setContentView(view)

        val container = view.findViewById<LinearLayout>(R.id.historyContainer)
        val emptyView = view.findViewById<TextView>(R.id.tvHistoryEmpty)
        val clearBtn  = view.findViewById<Button>(R.id.btnClearHistory)
        val closeBtn  = view.findViewById<Button>(R.id.btnCloseHistory)

        closeBtn.setOnClickListener { dialog.dismiss() }

        if (historyExpressions.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            clearBtn.visibility  = View.GONE
        } else {
            emptyView.visibility = View.GONE
            clearBtn.visibility  = View.VISIBLE
            for (i in historyExpressions.indices) {
                container.addView(createHistoryEntry(i, dialog))
            }
        }

        clearBtn.setOnClickListener {
            historyExpressions.clear(); historyResults.clear(); dialog.dismiss()
        }
        dialog.show()
    }

    private fun createHistoryEntry(index: Int, dialog: BottomSheetDialog): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 16, 0, 0)
            isClickable = true; isFocusable = true
            setOnClickListener {
                currentInput = historyResults[index].replace(",", ".")
                expressionForDisplay = ""; justCalculated = true
                updateDisplay(); dialog.dismiss()
            }
        }

        val exprText = TextView(this).apply {
            text = historyExpressions[index]
            setTextColor(0xFF888888.toInt())
            textSize = 13f
        }
        val resultText = TextView(this).apply {
            text = historyResults[index]
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 28f
        }
        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            ).apply { topMargin = 16 }
            setBackgroundColor(0xFF2C2C2E.toInt())
        }

        layout.addView(exprText)
        layout.addView(resultText)
        layout.addView(divider)
        return layout
    }

    private fun appendScientificFunc(fn: String) {
        justCalculated = false
        val last = lastChar()
        if (last != null && last.isValueEnd()) currentInput += "*"
        currentInput += fn
        updateDisplay()
    }

    private fun appendConstant(value: String) {
        justCalculated = false
        val last = lastChar()
        when {
            currentInput.isEmpty() || currentInput == "0" -> currentInput = value
            last != null && last.isValueEnd() -> currentInput += "*$value"
            else -> currentInput += value
        }
        updateDisplay()
    }

    private fun appendRawToken(token: String) {
        justCalculated = false
        val last = lastChar()
        when (token) {
            "^" -> {
                if (last == null || !last.isValueEnd()) return
                currentInput += "^"
            }
            "(" -> {
                if (last != null && last.isValueEnd()) currentInput += "*"
                currentInput += "("
            }
            ")" -> {
                val open  = currentInput.count { it == '(' }
                val close = currentInput.count { it == ')' }
                if (open <= close) return
                if (last == null || last.isBinaryOp() || last == '(' || last == '^' || last == '-') return
                if (last == '.') currentInput = currentInput.dropLast(1)
                currentInput += ")"
            }
            else -> currentInput += token
        }
        updateDisplay()
    }

    private fun applyFactorial() {
        justCalculated = false
        val n = evalAsDouble(currentInput)
        if (n != null && n >= 0 && n == floor(n) && n <= 20) {
            val result = factorial(n.toLong())
            addToHistory("${formatForDisplay(n)}!", formatForDisplay(result.toDouble()))
            expressionForDisplay = "${formatForDisplay(n)}! ="
            currentInput = formatNumber(result.toDouble())
            justCalculated = true
        } else showError()
        updateDisplay()
    }

    private fun factorial(n: Long): Long = if (n <= 1L) 1L else n * factorial(n - 1L)

    private fun clearAll() {
        currentInput = ""; operand = null; pendingOp = null
        expressionForDisplay = ""; justCalculated = false; updateDisplay()
    }

    private fun backspace() {
        if (justCalculated) { justCalculated = false }
        if (currentInput.isNotEmpty()) {
            currentInput = when {
                currentInput.endsWith("sin(") -> currentInput.dropLast(4)
                currentInput.endsWith("cos(") -> currentInput.dropLast(4)
                currentInput.endsWith("tan(") -> currentInput.dropLast(4)
                currentInput.endsWith("log(") -> currentInput.dropLast(4)
                currentInput.endsWith("ln(")  -> currentInput.dropLast(3)
                currentInput.endsWith("√(")   -> currentInput.dropLast(2)
                currentInput.endsWith("π")    -> currentInput.dropLast(1)
                currentInput.endsWith("ℯ")    -> currentInput.dropLast(1)
                else                          -> currentInput.dropLast(1)
            }
            updateDisplay()
        } else if (operand != null && pendingOp != null) {
            currentInput = formatNumber(operand!!)
            operand = null
            pendingOp = null
            expressionForDisplay = ""
            updateDisplay()
        }
    }

    private fun toggleSign() {
        val value = evalAsDouble(currentInput) ?: return
        currentInput = formatNumber(-value); updateDisplay()
    }

    private fun applyPercent() {
        val value = evalAsDouble(currentInput) ?: return
        currentInput = formatNumber(value / 100.0); updateDisplay()
    }

    private fun isLandscape() =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun toggleScientificMode() {
        // Em landscape o painel científico é sempre visível — não altera layout
        if (isLandscape()) return

        isScientificMode = !isScientificMode
        scientificPanel.visibility = if (isScientificMode) View.VISIBLE else View.GONE

        val cs = ConstraintSet()
        cs.clone(rootConstraint)
        val percent = if (isScientificMode) 0.73f else 0.56f
        cs.constrainPercentHeight(R.id.keypadContainer, percent)
        cs.applyTo(rootConstraint)

        val tintColor = if (isScientificMode)
            resources.getColor(R.color.colorButtonOperator, theme)
        else
            0xFF888888.toInt()
        btnToggleSci.setColorFilter(tintColor)
    }

    private fun performOperation(a: Double, b: Double, op: String?): Double {
        return when (op) {
            "+"  -> a + b
            "−"  -> a - b
            "×"  -> a * b
            "÷"  -> {
                if (b == 0.0) { Toast.makeText(this, getString(R.string.error_division_by_zero), Toast.LENGTH_SHORT).show(); Double.NaN }
                else a / b
            }
            "^"  -> a.pow(b)
            else -> b
        }
    }

    private fun evaluateExpression(raw: String): Double {
        var expr = raw.trim()
            .replace("π", Math.PI.toString())
            .replace("ℯ", Math.E.toString())
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
        val unclosed = expr.count { it == '(' } - expr.count { it == ')' }
        if (unclosed > 0) expr += ")".repeat(unclosed)
        expr = expr.replace("()", "")
        expr = expr.trimEnd('+', '-', '*', '/', '^')
        return ExprParser(expr).parse()
    }

    private inner class ExprParser(private val input: String) {
        private var pos = 0
        fun parse(): Double {
            val r = parseAddSub()
            while (pos < input.length && input[pos] == ')') pos++
            if (pos < input.length) throw IllegalArgumentException("Unexpected: ${input[pos]}")
            return r
        }
        private fun parseAddSub(): Double {
            var l = parseMulDiv()
            while (pos < input.length) when (input[pos]) {
                '+' -> { pos++; l += parseMulDiv() }
                '-' -> { pos++; l -= parseMulDiv() }
                else -> break
            }
            return l
        }
        private fun parseMulDiv(): Double {
            var l = parsePow()
            while (pos < input.length) when (input[pos]) {
                '*' -> { pos++; l *= parsePow() }
                '/' -> { pos++; val r = parsePow(); if (r == 0.0) { Toast.makeText(this@CalculadoraActivity, getString(R.string.error_division_by_zero), Toast.LENGTH_SHORT).show(); return Double.NaN } else l /= r }
                else -> break
            }
            return l
        }
        private fun parsePow(): Double {
            val b = parseUnary()
            return if (pos < input.length && input[pos] == '^') { pos++; b.pow(parsePow()) } else b
        }
        private fun parseUnary(): Double {
            return if (pos < input.length && input[pos] == '-') { pos++; -parseUnary() } else parseAtom()
        }
        private fun parseAtom(): Double {
            for ((fn, op) in listOf(
                "sin(" to { x: Double -> sin(x) },
                "cos(" to { x: Double -> cos(x) },
                "tan(" to { x: Double -> tan(x) },
                "log(" to { x: Double -> log10(x) },
                "ln("  to { x: Double -> ln(x) },
                "√("   to { x: Double -> sqrt(x) }
            )) {
                if (input.startsWith(fn, pos)) {
                    pos += fn.length; val arg = parseAddSub()
                    if (pos < input.length && input[pos] == ')') pos++
                    return op(arg)
                }
            }
            if (pos < input.length && input[pos] == '(') {
                pos++; val r = parseAddSub()
                if (pos < input.length && input[pos] == ')') pos++
                return r
            }
            return parseNumber()
        }
        private fun parseNumber(): Double {
            val start = pos
            if (pos < input.length && input[pos] == '-') pos++
            while (pos < input.length && (input[pos].isDigit() || input[pos]=='.' || input[pos]=='E' || input[pos]=='e')) pos++
            return input.substring(start, pos).toDoubleOrNull() ?: throw NumberFormatException("Invalid: ${input.substring(start,pos)}")
        }
    }

    private fun updateDisplay() {
        tvExpression.text = expressionForDisplay
        val raw = if (currentInput.isNotEmpty()) currentInput else "0"
        tvDisplay.text = displayString(raw)
    }

    private fun formatNumber(value: Double): String {
        if (value.isNaN()) return "Erro"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        if (abs(value) < 1e-10) return "0"
        val bd = value.toBigDecimal().round(java.math.MathContext(12))
        val rounded = bd.toDouble()
        return if (rounded == floor(rounded) && abs(rounded) < 1e15)
            rounded.toLong().toString()
        else
            bd.stripTrailingZeros().toPlainString()
    }

    private fun formatForDisplay(value: Double) = formatNumber(value).replace(".", ",")

    private val superMap = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '-' to '⁻', '(' to '⁽', ')' to '⁾', '+' to '⁺', ',' to ','
    )

    private fun displayString(s: String): String {
        val src = s.replace(".", ",").replace("*", "×")
        val sb = StringBuilder()
        var i = 0
        while (i < src.length) {
            if (src[i] == '^') {
                i++
                if (i < src.length && src[i] == '(') {
                    var depth = 0
                    while (i < src.length) {
                        val c = src[i]
                        sb.append(superMap[c] ?: c)
                        if (c == '(') depth++
                        else if (c == ')') { depth--; i++; if (depth == 0) break }
                        else i++
                    }
                } else {
                    if (i < src.length && src[i] == '-') { sb.append('⁻'); i++ }
                    while (i < src.length && src[i].isDigit()) {
                        sb.append(superMap[src[i]] ?: src[i]); i++
                    }
                }
            } else {
                sb.append(src[i]); i++
            }
        }
        return sb.toString()
    }

    private fun showError() = Toast.makeText(this, getString(R.string.error_invalid_expression), Toast.LENGTH_SHORT).show()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentInput", currentInput)
        outState.putDouble("operand", operand ?: Double.NaN)
        outState.putString("pendingOp", pendingOp)
        outState.putString("expressionForDisplay", expressionForDisplay)
        outState.putBoolean("isScientificMode", isScientificMode)
        outState.putBoolean("justCalculated", justCalculated)
        outState.putStringArrayList("historyExpressions", ArrayList(historyExpressions))
        outState.putStringArrayList("historyResults", ArrayList(historyResults))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentInput = savedInstanceState.getString("currentInput", "")
        val opnd = savedInstanceState.getDouble("operand", Double.NaN)
        operand = if (opnd.isNaN()) null else opnd
        pendingOp = savedInstanceState.getString("pendingOp")
        expressionForDisplay = savedInstanceState.getString("expressionForDisplay", "")
        isScientificMode = savedInstanceState.getBoolean("isScientificMode", false)
        justCalculated = savedInstanceState.getBoolean("justCalculated", false)
        savedInstanceState.getStringArrayList("historyExpressions")?.let { historyExpressions.addAll(it) }
        savedInstanceState.getStringArrayList("historyResults")?.let { historyResults.addAll(it) }

        if (isScientificMode && !isLandscape()) {
            scientificPanel.visibility = View.VISIBLE
            btnToggleSci.setColorFilter(resources.getColor(R.color.colorButtonOperator, theme))
            val cs = ConstraintSet(); cs.clone(rootConstraint)
            cs.constrainPercentHeight(R.id.keypadContainer, 0.73f); cs.applyTo(rootConstraint)
        }
        updateDisplay()
    }
}
