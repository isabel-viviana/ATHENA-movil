package com.example.athena

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private enum class Page(val title: String, val icon: String) {
        HOME("Inicio", "⌂"),
        EXAMS("Simulacros", "▤"),
        PRACTICE("Prácticas", "✦"),
        TUTOR("Tutor IA", "✧"),
        STATISTICS("Estadísticas", "◔"),
        RANKING("Ranking", "★"),
        PROFILE("Perfil", "●"),
        NOTIFICATIONS("Notificaciones", "♢"),
    }

    private val worker = Executors.newSingleThreadExecutor()
    private val pageBackground = Color.rgb(7, 11, 24)
    private val cardColor = Color.rgb(21, 31, 54)
    private val blue = Color.rgb(56, 103, 232)
    private val textColor = Color.rgb(245, 247, 255)
    private val muted = Color.rgb(167, 180, 204)
    private val errorColor = Color.rgb(255, 119, 119)
    private val subjects = listOf(
        "Matemáticas",
        "Ciencias Naturales",
        "Lectura Crítica",
        "Sociales y Ciudadanas",
        "Inglés",
    )

    private lateinit var root: FrameLayout
    private lateinit var appColumn: LinearLayout
    private lateinit var pageContent: LinearLayout
    private lateinit var bottomNavigation: LinearLayout
    private lateinit var loginScreen: View
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginError: TextView
    private var currentPage = Page.HOME
    private var selectedExamType = "full"
    private var examDifficulty = "medium"
    private var practiceDifficulty = "medium"
    private var practiceSubject = 1
    private var attemptId = 0
    private var questions = JSONArray()
    private var questionIndex = 0
    private var selectedOptionId: Int? = null
    private var answerInFlight = false
    private var conversationId: String? = null
    private var unreadNotifications = true
    private var profileName = "Estudiante"
    private var quizOpenedFromPractice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        profileName = getSharedPreferences("athena", MODE_PRIVATE)
            .getString("profile_name", "Estudiante") ?: "Estudiante"

        root = FrameLayout(this).apply {
            setBackgroundColor(pageBackground)
            id = View.generateViewId()
        }
        val app = buildAppShell()
        root.addView(
            app,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        loginScreen = buildLoginScreen()
        root.addView(
            loginScreen,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        setContentView(root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        worker.shutdownNow()
        super.onDestroy()
    }

    private fun buildAppShell(): View {
        appColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(pageBackground)
        }
        val toolbar = label("ATHENA", 18f, textColor, true).apply {
            setPadding(dp(20), dp(12), dp(20), dp(8))
        }
        appColumn.addView(toolbar)

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        pageContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(12), dp(18), dp(24))
        }
        scrollView.addView(
            pageContent,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
        appColumn.addView(
            scrollView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ),
        )

        val navScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            setBackgroundColor(cardColor)
        }
        bottomNavigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        Page.entries.forEach { page ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                background = roundedDrawable(Color.TRANSPARENT, 12)
                setPadding(dp(6), dp(4), dp(6), dp(4))
            }
            item.addView(label(page.icon, 22f, textColor, false))
            item.addView(label(page.title, 9f, muted, false))
            item.setOnClickListener { showPage(page) }
            bottomNavigation.addView(
                item,
                LinearLayout.LayoutParams(dp(82), dp(64)).apply {
                    marginEnd = dp(2)
                },
            )
        }
        navScroll.addView(bottomNavigation)
        appColumn.addView(
            navScroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(72),
            ),
        )
        return appColumn
    }

    private fun buildLoginScreen(): View {
        val scroll = ScrollView(this).apply {
            setBackgroundColor(pageBackground)
            isFillViewport = true
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(28), dp(28), dp(28))
        }
        content.addView(label("ATHENA", 30f, textColor, true))
        content.addView(
            label(
                "Ingresa a tu cuenta de estudiante ATHENA",
                22f,
                textColor,
                true,
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(18), 0, dp(8))
            },
        )
        content.addView(
            label(
                "Usa el mismo acceso que utilizas en la web.",
                15f,
                muted,
                false,
            ).apply { gravity = Gravity.CENTER },
        )
        emailInput = EditText(this).apply {
            hint = "Correo electrónico"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setSingleLine()
            setTextColor(textColor)
            setHintTextColor(muted)
            setPadding(dp(16), 0, dp(16), 0)
            this.background = roundedDrawable(cardColor, 12)
        }
        content.addView(
            emailInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54),
            ).apply { topMargin = dp(24) },
        )
        passwordInput = EditText(this).apply {
            hint = "Contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSingleLine()
            setTextColor(textColor)
            setHintTextColor(muted)
            setPadding(dp(16), 0, dp(16), 0)
            this.background = roundedDrawable(cardColor, 12)
        }
        content.addView(
            passwordInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54),
            ).apply { topMargin = dp(12) },
        )
        loginError = label("", 13f, errorColor, false).apply {
            visibility = View.GONE
        }
        content.addView(loginError)
        content.addView(
            actionButton("Iniciar sesión") {
                if (emailInput.text.isBlank() || passwordInput.text.isBlank()) {
                    loginError.text = "Completa tu correo y contraseña para continuar."
                    loginError.visibility = View.VISIBLE
                } else {
                    loginScreen.visibility = View.GONE
                    showPage(Page.HOME)
                }
            },
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52),
            ).apply { topMargin = dp(16) },
        )
        scroll.addView(content)
        return scroll
    }

    private fun showPage(page: Page) {
        currentPage = page
        updateNavigationSelection()
        pageContent.removeAllViews()
        when (page) {
            Page.HOME -> renderHome()
            Page.EXAMS -> renderExamConfiguration()
            Page.PRACTICE -> renderPracticeConfiguration()
            Page.TUTOR -> renderTutor()
            Page.STATISTICS -> renderStatistics()
            Page.RANKING -> renderRanking()
            Page.PROFILE -> renderProfile()
            Page.NOTIFICATIONS -> renderNotifications()
        }
    }

    private fun updateNavigationSelection() {
        for (index in 0 until bottomNavigation.childCount) {
            val item = bottomNavigation.getChildAt(index) as LinearLayout
            val selected = Page.entries[index] == currentPage
            item.background = roundedDrawable(
                if (selected) Color.rgb(38, 63, 117) else Color.TRANSPARENT,
                12,
            )
            (item.getChildAt(1) as TextView).setTextColor(if (selected) blue else muted)
        }
    }

    private fun renderHome() {
        pageContent.addView(label("¡Hola, $profileName!", 27f, textColor, true))
        pageContent.addView(
            label("Aquí tienes un resumen de tu progreso académico.", 14f, muted, false)
                .apply { setPadding(0, dp(6), 0, dp(18)) },
        )
        val loading = label("Cargando tu progreso...", 14f, muted, false)
        pageContent.addView(loading)
        request("progress", onSuccess = { data ->
            if (currentPage != Page.HOME) return@request
            pageContent.removeView(loading)
            val summary = data.optJSONObject("resumen_general") ?: JSONObject()
            val total = summary.optInt("total_simulacros_completados")
            val accuracy = summary.optDouble("porcentaje_global_aciertos")
            val stats = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            stats.addView(
                statCard("$total", "SIMULACROS\nCompletados"),
                LinearLayout.LayoutParams(0, dp(120), 1f).apply { marginEnd = dp(6) },
            )
            stats.addView(
                statCard(String.format(Locale.getDefault(), "%.2f%%", accuracy), "ACIERTO GLOBAL"),
                LinearLayout.LayoutParams(0, dp(120), 1f).apply { marginStart = dp(6) },
            )
            pageContent.addView(stats)
            pageContent.addView(
                card().apply {
                    addView(label("PRÁCTICAS ICFES", 13f, blue, true))
                    addView(label("Bienvenido a ATHENA", 21f, textColor, true), topMargin(8))
                    addView(
                        label(
                            "Continúa practicando y prepara tus pruebas Saber 11.",
                            14f,
                            muted,
                            false,
                        ),
                        topMargin(6),
                    )
                    addView(
                        actionButton("Comenzar práctica") { showPage(Page.PRACTICE) },
                        topMargin(14),
                    )
                },
                topMargin(18),
            )
            sectionButton("Configurar simulacro") { showPage(Page.EXAMS) }
            sectionButton("Ver mis estadísticas") { showPage(Page.STATISTICS) }
        }, onError = { message ->
            if (currentPage == Page.HOME) {
                loading.text = "No se pudo cargar el progreso: $message"
                loading.setTextColor(errorColor)
                pageContent.addView(actionButton("Reintentar") { showPage(Page.HOME) }, topMargin(8))
            }
        })
    }

    private fun renderExamConfiguration() {
        pageContent.addView(label("Configurar simulacro", 25f, textColor, true))
        pageContent.addView(
            label(
                "Selecciona la modalidad y el nivel de dificultad.",
                14f,
                muted,
                false,
            ),
            topMargin(6),
        )
        pageContent.addView(
            card().apply {
                addView(label("Modalidad de simulacro", 17f, textColor, true))
                addView(
                    choiceButton(
                        "${if (selectedExamType == "full") "✓ " else ""}Simulacro completo ICFES",
                        "20 preguntas de las áreas oficiales",
                    ) {
                        selectedExamType = "full"
                        renderExamConfiguration()
                    },
                    topMargin(14),
                )
                addView(
                    choiceButton(
                        "${if (selectedExamType == "quick") "✓ " else ""}Mini simulacro diagnóstico",
                        "5 preguntas para una evaluación rápida",
                    ) {
                        selectedExamType = "quick"
                        renderExamConfiguration()
                    },
                    topMargin(10),
                )
                addView(label("Dificultad", 14f, muted, true), topMargin(18))
                addView(difficultySelector(false), topMargin(6))
                addView(
                    actionButton("Iniciar prueba") {
                        startExam(selectedExamType, examDifficulty, false)
                    },
                    topMargin(18),
                )
            },
            topMargin(18),
        )
        pageContent.addView(
            label(
                "El simulacro se crea con las áreas del cuadernillo ICFES, igual que en la web.",
                13f,
                muted,
                false,
            ),
            topMargin(12),
        )
    }

    private fun renderPracticeConfiguration() {
        pageContent.addView(label("Prácticas por área", 25f, textColor, true))
        pageContent.addView(
            label(
                "Elige un área y practica con preguntas del banco. Cada sesión completa requiere 25 preguntas.",
                14f,
                muted,
                false,
            ),
            topMargin(6),
        )
        subjects.forEachIndexed { index, subject ->
            pageContent.addView(
                choiceButton(
                    subject,
                    if (index + 1 == practiceSubject) "Área seleccionada" else "Seleccionar área",
                ) {
                    practiceSubject = index + 1
                    renderPracticeConfiguration()
                },
                topMargin(10),
            )
        }
        pageContent.addView(card().apply {
            addView(label("Dificultad", 15f, textColor, true))
            addView(difficultySelector(true), topMargin(8))
            addView(
                actionButton("Iniciar práctica de $subjectName") {
                    startExam("practice", practiceDifficulty, true)
                },
                topMargin(14),
            )
        }, topMargin(14))
    }

    private val subjectName: String
        get() = subjects.getOrElse(practiceSubject - 1) { subjects.first() }

    private fun difficultySelector(forPractice: Boolean): View {
        val choices = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        listOf("easy" to "Fácil", "medium" to "Media", "hard" to "Difícil").forEach { (value, title) ->
            val selected = if (forPractice) practiceDifficulty == value else examDifficulty == value
            val button = actionButton(title, selected) {
                if (forPractice) practiceDifficulty = value else examDifficulty = value
                if (forPractice) renderPracticeConfiguration() else renderExamConfiguration()
            }
            choices.addView(
                button,
                LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                    marginEnd = dp(4)
                    marginStart = dp(4)
                },
            )
        }
        return choices
    }

    private fun startExam(type: String, difficulty: String, practice: Boolean) {
        quizOpenedFromPractice = practice
        val payload = JSONObject()
            .put("exam_type", type)
            .put("difficulty", difficulty)
            .put("subject_ids", if (practice) JSONArray().put(practiceSubject) else JSONArray().put(1).put(2).put(3).put(4).put(5))
        pageContent.removeAllViews()
        pageContent.addView(label("Preparando tu ${if (practice) "práctica" else "simulacro"}...", 18f, textColor, true))
        request("exams/configure", "POST", payload, onSuccess = { data ->
            val attempt = data.optJSONObject("attempt")
            val id = attempt?.optInt("id", 0) ?: data.optInt("attempt_id", 0)
            if (id <= 0) {
                showError("El servidor no devolvió el identificador del intento.")
                return@request
            }
            attemptId = id
            loadExamQuestions(id)
        }, onError = { message -> showError(message) })
    }

    private fun loadExamQuestions(id: Int) {
        request("exams/$id/questions", onSuccess = { data ->
            attemptId = data.optInt("attempt_id", id)
            questions = data.optJSONArray("questions") ?: JSONArray()
            questionIndex = 0
            if (questions.length() == 0) {
                showError("El servidor no asignó preguntas a este intento. Intenta otro simulacro.")
            } else {
                renderQuestion()
            }
        }, onError = { message -> showError(message) })
    }

    private fun renderQuestion() {
        pageContent.removeAllViews()
        val question = questions.optJSONObject(questionIndex) ?: run {
            showError("No se pudo leer la pregunta.")
            return
        }
        selectedOptionId = if (question.isNull("selected_option_id")) {
            null
        } else {
            question.optInt("selected_option_id").takeIf { it > 0 }
        }
        answerInFlight = false
        val total = questions.length()
        val subject = question.optJSONObject("subject")?.optString("name", "ICFES") ?: "ICFES"
        pageContent.addView(
            card().apply {
                addView(label(subject, 13f, blue, true))
                addView(
                    label("Pregunta ${questionIndex + 1} de $total", 21f, textColor, true),
                    topMargin(6),
                )
                addView(
                    label("Simulacro #$attemptId", 12f, muted, false),
                    topMargin(4),
                )
            },
        )
        val stimulus = question.optJSONObject("stimulus")
        if (stimulus != null) {
            val title = stimulus.optString("title")
            val content = stimulus.optString("content_text")
            if (title.isNotBlank() || content.isNotBlank()) {
                pageContent.addView(card().apply {
                    if (title.isNotBlank()) addView(label(title, 17f, textColor, true))
                    if (content.isNotBlank()) addView(label(content, 14f, muted, false), topMargin(8))
                }, topMargin(12))
            }
        }
        pageContent.addView(
            card().apply {
                val statement = question.optString("statement", "Pregunta sin enunciado.")
                addView(label(statement, 17f, textColor, true))
                val image = question.optString("question_image_url")
                if (image.isNotBlank() && image != "null") {
                    addView(label("Imagen de la pregunta: $image", 12f, muted, false), topMargin(8))
                }
            },
            topMargin(12),
        )

        val options = question.optJSONArray("options") ?: JSONArray()
        if (options.length() == 0) {
            pageContent.addView(label("Esta pregunta no tiene opciones disponibles.", 14f, errorColor, false), topMargin(10))
        }
        for (index in 0 until options.length()) {
            val option = options.optJSONObject(index) ?: continue
            val optionId = option.optInt("id")
            val letter = option.optString("option_letter", ('A' + index).toString())
            val optionText = option.optString("option_text", "Opción $letter")
            pageContent.addView(
                actionButton(
                    "$letter. $optionText",
                    selectedOptionId == optionId,
                    enabled = !answerInFlight,
                ) {
                    saveAnswer(question.optInt("id"), optionId, false)
                },
                topMargin(10),
            )
        }
        pageContent.addView(
            label("Tu respuesta se guarda en el servidor al seleccionarla.", 12f, muted, false),
            topMargin(12),
        )
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        if (questionIndex > 0) {
            actions.addView(
                actionButton("Anterior") {
                    questionIndex--
                    renderQuestion()
                },
                LinearLayout.LayoutParams(0, dp(48), 1f).apply { marginEnd = dp(6) },
            )
        }
        val nextLabel = if (questionIndex == total - 1) "Finalizar simulacro" else "Siguiente"
        actions.addView(
            actionButton(nextLabel) {
                if (selectedOptionId == null) {
                    saveAnswer(question.optInt("id"), null, true)
                } else {
                    moveToNextQuestion()
                }
            },
            LinearLayout.LayoutParams(0, dp(48), 1f).apply { marginStart = dp(6) },
        )
        pageContent.addView(actions, topMargin(18))
    }

    private fun saveAnswer(questionId: Int, optionId: Int?, continueAfterSave: Boolean) {
        if (answerInFlight) return
        answerInFlight = true
        val payload = JSONObject()
            .put("question_id", questionId)
            .put("time_spent_seconds", 0)
        if (optionId != null) payload.put("selected_option_id", optionId)
        else payload.put("selected_option_id", JSONObject.NULL)
        request(
            "exams/$attemptId/answer",
            "POST",
            payload,
            onSuccess = {
                answerInFlight = false
                selectedOptionId = optionId
                questions.optJSONObject(questionIndex)
                    ?.put("selected_option_id", optionId ?: JSONObject.NULL)
                if (continueAfterSave) moveToNextQuestion() else renderQuestion()
            },
            onError = { message ->
                answerInFlight = false
                Toast.makeText(this, "No se pudo guardar la respuesta: $message", Toast.LENGTH_LONG).show()
                renderQuestion()
            },
        )
    }

    private fun moveToNextQuestion() {
        if (questionIndex < questions.length() - 1) {
            questionIndex++
            renderQuestion()
        } else {
            finishExam()
        }
    }

    private fun finishExam() {
        pageContent.removeAllViews()
        pageContent.addView(label("Calificando tu simulacro...", 18f, textColor, true))
        request("exams/$attemptId/finish", "POST", JSONObject(), onSuccess = {
            request("exams/$attemptId/results", onSuccess = { results ->
                renderResults(results)
            }, onError = { message -> showError("El simulacro se calificó, pero no se pudieron cargar los resultados: $message") })
        }, onError = { message -> showError(message) })
    }

    private fun renderResults(results: JSONObject) {
        pageContent.removeAllViews()
        pageContent.addView(label("Resultados del simulacro", 25f, textColor, true))
        val score = results.optDouble("score_percentage")
        pageContent.addView(
            statCard(String.format(Locale.getDefault(), "%.0f%%", score), "PUNTAJE"),
            topMargin(14),
        )
        pageContent.addView(
            card().apply {
                addView(label("Respuestas correctas: ${results.optInt("correct_answers")}", 16f, textColor, true))
                addView(label("Incorrectas: ${results.optInt("incorrect_answers")}", 15f, muted, false), topMargin(8))
                addView(label("Omitidas: ${results.optInt("omitted_answers")}", 15f, muted, false), topMargin(6))
                addView(label("Total de preguntas: ${results.optInt("total_questions")}", 15f, muted, false), topMargin(6))
            },
            topMargin(12),
        )
        val subjectResults = results.optJSONArray("subject_performance") ?: JSONArray()
        if (subjectResults.length() > 0) {
            pageContent.addView(label("Rendimiento por área", 18f, textColor, true), topMargin(18))
            for (index in 0 until subjectResults.length()) {
                val item = subjectResults.optJSONObject(index) ?: continue
                pageContent.addView(
                    label(
                        "${item.optString("subject_name")}: ${item.optInt("score_percentage")}% " +
                            "(${item.optInt("correct")}/${item.optInt("total")})",
                        14f,
                        muted,
                        false,
                    ),
                    topMargin(8),
                )
            }
        }
        pageContent.addView(
            actionButton(if (quizOpenedFromPractice) "Volver a prácticas" else "Volver a simulacros") {
                showPage(if (quizOpenedFromPractice) Page.PRACTICE else Page.EXAMS)
            },
            topMargin(20),
        )
        pageContent.addView(actionButton("Ir al inicio") { showPage(Page.HOME) }, topMargin(8))
    }

    private fun renderStatistics() {
        pageContent.addView(label("Estadísticas y progreso", 24f, textColor, true))
        pageContent.addView(label("Rendimiento basado en tus intentos completados.", 14f, muted, false), topMargin(6))
        val loading = label("Cargando estadísticas...", 14f, muted, false)
        pageContent.addView(loading, topMargin(14))
        request("progress/stats", onSuccess = { data ->
            if (currentPage != Page.STATISTICS) return@request
            pageContent.removeView(loading)
            pageContent.addView(card().apply {
                addView(label("Simulacros completados", 14f, muted, false))
                addView(label(data.optInt("total_simulacros_completados").toString(), 30f, textColor, true), topMargin(4))
                addView(label("Acierto global: ${data.optDouble("porcentaje_global_aciertos")}%", 16f, blue, true), topMargin(8))
                addView(label("Preguntas respondidas: ${data.optInt("total_preguntas_respondidas")}", 14f, muted, false), topMargin(6))
                addView(label("Correctas: ${data.optInt("total_correctas")} · Incorrectas: ${data.optInt("total_incorrectas")} · Omitidas: ${data.optInt("total_omitidas")}", 13f, muted, false), topMargin(6))
                addView(label("Mejor resultado: ${data.optInt("mejor_resultado")}% · Promedio: ${data.optDouble("promedio_porcentaje_simulacro")}%", 13f, muted, false), topMargin(6))
            })
            loadHistory()
        }, onError = { message ->
            if (currentPage == Page.STATISTICS) {
                loading.text = "No se pudieron cargar las estadísticas: $message"
                loading.setTextColor(errorColor)
                pageContent.addView(actionButton("Reintentar") { showPage(Page.STATISTICS) }, topMargin(8))
            }
        })
    }

    private fun loadHistory() {
        request("progress/history", onSuccess = { data ->
            if (currentPage != Page.STATISTICS) return@request
            val list = when {
                data is JSONArray -> data
                data.has("data") -> data.optJSONArray("data") ?: JSONArray()
                else -> JSONArray()
            }
            pageContent.addView(label("Actividad reciente", 18f, textColor, true), topMargin(18))
            if (list.length() == 0) {
                pageContent.addView(label("Aún no tienes simulacros en tu historial.", 14f, muted, false), topMargin(8))
            } else {
                for (index in 0 until list.length()) {
                    val attempt = list.optJSONObject(index) ?: continue
                    pageContent.addView(card().apply {
                        addView(label("${attempt.optString("tipo", "Simulacro").replaceFirstChar { it.uppercase() }} · ${attempt.optString("fecha", "")}", 15f, textColor, true))
                        addView(label("Puntaje: ${attempt.optDouble("porcentaje")}% · Correctas: ${attempt.optInt("correctas")}", 13f, muted, false), topMargin(6))
                    }, topMargin(8))
                }
            }
        }, onError = { message ->
            if (currentPage == Page.STATISTICS) {
                pageContent.addView(label("No se pudo cargar el historial: $message", 13f, errorColor, false), topMargin(12))
            }
        })
    }

    private fun renderTutor() {
        pageContent.addView(label("Tutor IA de ATHENA", 24f, textColor, true))
        pageContent.addView(
            label(
                "Pregunta sobre temas ICFES y recibe una explicación paso a paso.",
                14f,
                muted,
                false,
            ),
            topMargin(6),
        )
        val chat = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        chat.addView(
            label(
                "¡Hola! Soy ATHENA. ¿Qué tema te gustaría repasar?",
                15f,
                textColor,
                false,
            ),
            topMargin(16),
        )
        pageContent.addView(chat)
        val messageInput = EditText(this).apply {
            hint = "Escribe tu pregunta..."
            minLines = 2
            maxLines = 5
            gravity = Gravity.TOP
            setTextColor(textColor)
            setHintTextColor(muted)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = roundedDrawable(cardColor, 12)
        }
        pageContent.addView(
            messageInput,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(104)).apply {
                topMargin = dp(16)
            },
        )
        lateinit var send: Button
        send = actionButton("Enviar pregunta") {
            val text = messageInput.text.toString().trim()
            if (text.isEmpty()) {
                messageInput.error = "Escribe una pregunta."
                return@actionButton
            }
            send.isEnabled = false
            val userLine = label("Tú: $text", 14f, blue, true)
            chat.addView(userLine, topMargin(12))
            messageInput.text.clear()
            hideKeyboard(messageInput)
            val pending = label("ATHENA está preparando una respuesta...", 14f, muted, false)
            chat.addView(pending, topMargin(8))
            val payload = JSONObject().put("message", text)
            conversationId?.let { payload.put("conversation_id", it) }
            request("ai/message", "POST", payload, onSuccess = { data ->
                send.isEnabled = true
                chat.removeView(pending)
                conversationId = data.optString("conversation_id").takeIf { it.isNotBlank() }
                val answer = data.optString("ai_response")
                chat.addView(
                    label(
                        if (answer.isBlank()) "ATHENA no devolvió una respuesta válida." else "ATHENA: $answer",
                        15f,
                        textColor,
                        false,
                    ),
                    topMargin(8),
                )
            }, onError = { error ->
                send.isEnabled = true
                pending.text = "No se pudo enviar la pregunta: $error"
                pending.setTextColor(errorColor)
            })
        }
        pageContent.addView(send, topMargin(10))
    }

    private fun renderRanking() {
        pageContent.addView(label("Ranking ICFES", 24f, textColor, true))
        pageContent.addView(
            label("Mejores puntajes por departamento y top nacional.", 14f, muted, false),
            topMargin(6),
        )
        pageContent.addView(
            card().apply {
                addView(label("Mejor departamento", 13f, muted, false))
                addView(label("Bogotá D.C.", 21f, textColor, true), topMargin(6))
                addView(label("Puntaje promedio ICFES de 309", 13f, muted, false), topMargin(4))
            },
            topMargin(14),
        )
        pageContent.addView(label("Ranking por departamento", 18f, textColor, true), topMargin(18))
        val departments = listOf(
            "Bogotá D.C." to 309,
            "Antioquia" to 302,
            "Valle del Cauca" to 298,
            "Cundinamarca" to 292,
            "Atlántico" to 289,
            "Santander" to 284,
            "Boyacá" to 281,
            "Nariño" to 278,
            "Caldas" to 275,
            "Tolima" to 272,
        )
        departments.forEachIndexed { index, (name, score) ->
            pageContent.addView(
                card().apply {
                    addView(label("${index + 1}. $name", 15f, textColor, true))
                    addView(label("$score puntos", 13f, muted, false), topMargin(4))
                },
                topMargin(8),
            )
        }
        pageContent.addView(label("Top nacional", 18f, textColor, true), topMargin(18))
        listOf(
            "Marcos Lopez" to 320,
            "Angela Diaz" to 315,
            "Carlos Mendoza" to 311,
            "Ana Rodriguez" to 308,
            "Luis Fernandez" to 304,
        ).forEachIndexed { index, (name, score) ->
            pageContent.addView(
                label("${index + 1}. $name — $score pts", 14f, muted, false),
                topMargin(8),
            )
        }
        pageContent.addView(
            label("Ranking informativo, igual que la versión web; no hay una API de clasificación conectada todavía.", 12f, muted, false),
            topMargin(16),
        )
    }

    private fun renderProfile() {
        pageContent.addView(label("Perfil", 24f, textColor, true))
        pageContent.addView(label("Administra tus datos de estudiante en este dispositivo.", 14f, muted, false), topMargin(6))
        val nameInput = EditText(this).apply {
            setText(profileName)
            hint = "Nombre"
            setTextColor(textColor)
            setHintTextColor(muted)
            setPadding(dp(14), 0, dp(14), 0)
            background = roundedDrawable(cardColor, 12)
        }
        pageContent.addView(nameInput, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)).apply { topMargin = dp(16) })
        pageContent.addView(
            actionButton("Guardar nombre en este dispositivo") {
                val name = nameInput.text.toString().trim()
                if (name.isBlank()) {
                    nameInput.error = "Escribe un nombre."
                } else {
                    profileName = name
                    getSharedPreferences("athena", MODE_PRIVATE).edit().putString("profile_name", name).apply()
                    Toast.makeText(this, "Nombre guardado", Toast.LENGTH_SHORT).show()
                    showPage(Page.PROFILE)
                }
            },
            topMargin(10),
        )
        pageContent.addView(
            label(
                "La web aún no ofrece una API de perfil para sincronizar estos cambios con tu cuenta.",
                12f,
                muted,
                false,
            ),
            topMargin(12),
        )
        pageContent.addView(actionButton("Cerrar sesión") {
            loginScreen.visibility = View.VISIBLE
            passwordInput.text.clear()
        }, topMargin(20))
    }

    private fun renderNotifications() {
        pageContent.addView(label("Notificaciones", 24f, textColor, true))
        val markRead = actionButton(if (unreadNotifications) "Marcar todo como leído" else "Todo leído") {
            unreadNotifications = false
            renderNotifications()
        }
        pageContent.addView(markRead, topMargin(10))
        val items = listOf(
            "Hoy" to listOf(
                "Nuevo simulacro disponible" to "El simulacro nacional #4 ya está abierto.",
                "Racha en peligro" to "¡No olvides practicar hoy para mantener tus 12 días!",
            ),
            "Ayer" to listOf(
                "Logro desbloqueado" to "Has conseguido «Lector Voraz».",
                "Mejora detectada" to "Tu puntaje en matemáticas subió un 5%.",
            ),
            "Esta semana" to listOf("¡Bien hecho!" to "Completaste 3 prácticas esta semana."),
            "Anteriores" to listOf("Plan Premium activado" to "Tu plan estará activo hasta el 12 de mayo de 2026."),
        )
        items.forEach { (group, notifications) ->
            pageContent.addView(label(group, 16f, blue, true), topMargin(18))
            notifications.forEach { (title, body) ->
                pageContent.addView(card().apply {
                    addView(label(title, 16f, textColor, true))
                    addView(label(body, 13f, muted, false), topMargin(6))
                    addView(label(if (unreadNotifications) "Sin leer" else "Leída", 11f, blue, false), topMargin(6))
                }, topMargin(8))
            }
        }
        pageContent.addView(
            label(
                "Estas notificaciones son las que muestra la web. El backend aún no tiene una API funcional de notificaciones.",
                12f,
                muted,
                false,
            ),
            topMargin(14),
        )
    }

    private fun showError(message: String) {
        pageContent.removeAllViews()
        pageContent.addView(label("No se pudo completar la acción", 21f, errorColor, true))
        pageContent.addView(label(message, 14f, muted, false), topMargin(10))
        pageContent.addView(actionButton("Volver a intentar") {
            when (currentPage) {
                Page.EXAMS -> renderExamConfiguration()
                Page.PRACTICE -> renderPracticeConfiguration()
                else -> showPage(currentPage)
            }
        }, topMargin(14))
        pageContent.addView(actionButton("Volver al inicio") { showPage(Page.HOME) }, topMargin(8))
    }

    private fun request(
        path: String,
        method: String = "GET",
        body: JSONObject? = null,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit = {},
    ) {
        worker.execute {
            var connection: HttpURLConnection? = null
            try {
                connection = URL("$API_BASE_URL/api/$path").openConnection() as HttpURLConnection
                connection.requestMethod = method
                connection.connectTimeout = 15000
                connection.readTimeout = 30000
                connection.setRequestProperty("Accept", "application/json")
                if (body != null) {
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body.toString()) }
                }
                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val responseText = stream?.let { input ->
                    BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { it.readText() }
                }.orEmpty()
                val response = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
                if (code !in 200..299 || !response.optBoolean("success", true)) {
                    throw IllegalStateException(
                        response.optString("message").ifBlank { "El servidor respondió con el código $code." },
                    )
                }
                val data = response.optJSONObject("data") ?: response
                runOnUiThread { onSuccess(data) }
            } catch (error: Exception) {
                val message = error.message ?: "No fue posible conectar con el servidor."
                runOnUiThread { onError(message) }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun statCard(value: String, caption: String): View =
        card().apply {
            gravity = Gravity.CENTER
            addView(label(value, 27f, textColor, true))
            addView(label(caption, 11f, muted, false).apply { gravity = Gravity.CENTER }, topMargin(6))
        }

    private fun choiceButton(title: String, subtitle: String, onClick: () -> Unit): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedDrawable(cardColor, 12)
            isClickable = true
            isFocusable = true
            setPadding(dp(14), dp(12), dp(14), dp(12))
            addView(label(title, 15f, textColor, true))
            addView(label(subtitle, 12f, muted, false), topMargin(5))
            setOnClickListener { onClick() }
        }

    private fun card(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            this.background = roundedDrawable(cardColor, 16)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

    private fun sectionButton(title: String, onClick: () -> Unit) {
        pageContent.addView(actionButton(title, onClick = onClick), topMargin(10))
    }

    private fun actionButton(
        title: String,
        selected: Boolean = false,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ): Button =
        Button(this).apply {
            text = title
            isAllCaps = false
            isEnabled = enabled
            setTextColor(textColor)
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            background = roundedDrawable(if (selected) blue else cardColor, 12)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setOnClickListener { if (isEnabled) onClick() }
        }

    private fun label(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean,
    ): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setLineSpacing(dp(2).toFloat(), 1f)
        }

    private fun roundedDrawable(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
        }

    private fun topMargin(value: Int): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply { topMargin = dp(value) }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun hideKeyboard(view: View) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val API_BASE_URL = "http://10.0.2.2:8000"
    }
}
