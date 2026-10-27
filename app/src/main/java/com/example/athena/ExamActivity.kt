package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class ExamActivity : AppCompatActivity() {

    private data class Question(
        val text: String,
        val options: List<String>,
        val correctOptionIndex: Int
    )

    private val questions = listOf(
        Question(
            "1. Si un automóvil viaja a una velocidad constante de 60 km/h, ¿qué distancia recorre en 2 horas y media?",
            listOf("A) 120 km", "B) 150 km", "C) 180 km", "D) 200 km"),
            1
        ),
        Question(
            "2. ¿Cuál es el órgano principal responsable del intercambio gaseoso (hematosis) en el sistema respiratorio humano?",
            listOf("A) Tráquea", "B) Bronquios", "C) Alvéolos pulmonares", "D) Diafragma"),
            2
        ),
        Question(
            "3. En un texto argumentativo, ¿cuál es la función principal de la tesis?",
            listOf(
                "A) Presentar un resumen de la obra.",
                "B) Exponer la postura o idea principal que se defiende.",
                "C) Narrar los acontecimientos secundarios.",
                "D) Incluir las referencias bibliográficas."
            ),
            1
        ),
        Question(
            "4. ¿Cuál de los siguientes eventos históricos marcó el inicio de la Edad Contemporánea?",
            listOf("A) La caída del Imperio Romano de Oriente", "B) La Revolución Francesa", "C) El descubrimiento de América", "D) La invención de la imprenta"),
            1
        ),
        Question(
            "5. Choose the correct option to complete the sentence: 'If I ___ more time, I would study another language.'",
            listOf("A) have", "B) had", "C) will have", "D) would have"),
            1
        )
    )

    private var currentQuestionIndex = 0
    private val selectedAnswers = HashMap<Int, Int>() // questionIndex -> optionIndex

    private lateinit var tvExamTitle: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvQuestionCounter: TextView
    private lateinit var progressBarExam: ProgressBar
    private lateinit var tvQuestionText: TextView
    private lateinit var radioGroupOptions: RadioGroup
    private lateinit var rbOptionA: RadioButton
    private lateinit var rbOptionB: RadioButton
    private lateinit var rbOptionC: RadioButton
    private lateinit var rbOptionD: RadioButton
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Simulacro Oficial Saber 11"

        tvExamTitle = findViewById(R.id.tvExamTitle)
        tvTimer = findViewById(R.id.tvTimer)
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter)
        progressBarExam = findViewById(R.id.progressBarExam)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        radioGroupOptions = findViewById(R.id.radioGroupOptions)
        rbOptionA = findViewById(R.id.rbOptionA)
        rbOptionB = findViewById(R.id.rbOptionB)
        rbOptionC = findViewById(R.id.rbOptionC)
        rbOptionD = findViewById(R.id.rbOptionD)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)

        tvExamTitle.text = examTitle

        startTimer(4 * 3600 * 1000L + 30 * 60 * 1000L) // 4 hours 30 minutes

        displayQuestion(currentQuestionIndex)

        btnPrev.setOnClickListener {
            saveCurrentAnswer()
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion(currentQuestionIndex)
            }
        }

        btnNext.setOnClickListener {
            saveCurrentAnswer()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayQuestion(currentQuestionIndex)
            } else {
                // Last question -> Finish exam
                finishExam()
            }
        }
    }

    private fun startTimer(durationMillis: Long) {
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                tvTimer.text = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00:00"
                Toast.makeText(this@ExamActivity, "El tiempo del simulacro ha finalizado", Toast.LENGTH_LONG).show()
                finishExam()
            }
        }.start()
    }

    private fun displayQuestion(index: Int) {
        val q = questions[index]
        tvQuestionText.text = q.text
        tvQuestionCounter.text = "Pregunta ${index + 1} de ${questions.size}"
        progressBarExam.progress = index + 1

        val radioButtons = listOf(rbOptionA, rbOptionB, rbOptionC, rbOptionD)
        radioGroupOptions.clearCheck()

        for (i in q.options.indices) {
            radioButtons[i].text = q.options[i]
            radioButtons[i].visibility = View.VISIBLE
        }

        // Restore previously selected answer if any
        if (selectedAnswers.containsKey(index)) {
            val savedOption = selectedAnswers[index]!!
            if (savedOption in radioButtons.indices) {
                radioButtons[savedOption].isChecked = true
            }
        }

        btnPrev.isEnabled = index > 0
        if (index == questions.size - 1) {
            btnNext.text = "Finalizar"
        } else {
            btnNext.text = "Siguiente"
        }
    }

    private fun saveCurrentAnswer() {
        val radioButtons = listOf(rbOptionA, rbOptionB, rbOptionC, rbOptionD)
        for (i in radioButtons.indices) {
            if (radioButtons[i].isChecked) {
                selectedAnswers[currentQuestionIndex] = i
                break
            }
        }
    }

    private fun finishExam() {
        countDownTimer?.cancel()
        Toast.makeText(this, "Prueba finalizada con éxito. ${selectedAnswers.size}/${questions.size} respondidas.", Toast.LENGTH_LONG).show()
        val isPractice = intent.getBooleanExtra("IS_PRACTICE", false)
        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Simulacro Oficial Saber 11"
        val intent = Intent(this, ExamResultActivity::class.java).apply {
            putExtra("IS_PRACTICE", isPractice)
            putExtra("EXAM_TITLE", examTitle)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
