package com.example.lifegoalcalulator

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var tvGreeting: TextView
    private lateinit var infoIcon: ImageView
    private lateinit var etCurrentAge: EditText
    private lateinit var spinnerIncome: Spinner
    private lateinit var etSavingsPercentage: EditText
    private lateinit var btnHouse: Button
    private lateinit var btnCar: Button
    private lateinit var btnTravel: Button
    private lateinit var btnAddCustomGoal: Button
    private lateinit var btnCalculate: Button

    // Track selected goals
    private val selectedGoals = mutableListOf<String>()
    private val customGoals = mutableListOf<String>()

    // Income levels for spinner
    private val incomeRanges = arrayOf(
        "Below $2,000",
        "$2,000 - $3,999",
        "$4,000 - $5,999",
        "$6,000 - $7,999",
        "$8,000 - $9,999",
        "$10,000 and above"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        initializeViews()
        setupListeners()
        setupSpinner()
    }

    private fun initializeViews() {
        tvGreeting = findViewById(R.id.tvGreeting)
        infoIcon = findViewById(R.id.infoIcon)
        etCurrentAge = findViewById(R.id.etCurrentAge)
        spinnerIncome = findViewById(R.id.spinnerIncome)
        etSavingsPercentage = findViewById(R.id.etSavingsPercentage)
        btnHouse = findViewById(R.id.btnHouse)
        btnCar = findViewById(R.id.btnCar)
        btnTravel = findViewById(R.id.btnTravel)
        btnAddCustomGoal = findViewById(R.id.btnAddCustomGoal)
        btnCalculate = findViewById(R.id.btnCalculate)

        // Set default greeting (can be personalized with user data)
        val username = "myesha" // This could come from user preferences
        tvGreeting.text = "Hello, $username!"
    }

    private fun setupListeners() {
        // Info icon click
        infoIcon.setOnClickListener {
            showInfoDialog()
        }

        // Goal selection buttons
        btnHouse.setOnClickListener { toggleGoalSelection(btnHouse, "House") }
        btnCar.setOnClickListener { toggleGoalSelection(btnCar, "Car") }
        btnTravel.setOnClickListener { toggleGoalSelection(btnTravel, "Travel") }

        // Add custom goal button
        btnAddCustomGoal.setOnClickListener {
            showAddCustomGoalDialog()
        }

        // Calculate button
        btnCalculate.setOnClickListener {
            if (validateInput()) {
                calculateFinancialGoals()
            }
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            incomeRanges
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIncome.adapter = adapter
    }

    private fun toggleGoalSelection(button: Button, goalName: String) {
        if (selectedGoals.contains(goalName)) {
            // Deselect
            selectedGoals.remove(goalName)
            button.setBackgroundColor(getColor(R.color.goal_button_normal))
        } else {
            // Select
            selectedGoals.add(goalName)
            button.setBackgroundColor(getColor(R.color.goal_button_selected))
        }
    }

    private fun showAddCustomGoalDialog() {
        val editText = EditText(this).apply {
            hint = "Enter goal name"
            setPadding(32, 32, 32, 32)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Custom Goal")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val goalName = editText.text.toString().trim()
                if (goalName.isNotEmpty()) {
                    addCustomGoal(goalName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addCustomGoal(goalName: String) {
        customGoals.add(goalName)
        selectedGoals.add(goalName)

        // Create a new button for this goal
        val customButton = Button(this).apply {
            text = "🎯 $goalName"
            setBackgroundColor(getColor(R.color.goal_button_selected))
            setTextColor(getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 8
            }

            setOnClickListener {
                toggleGoalSelection(this, goalName)
            }
        }

        // Find the LinearLayout inside the CardView to add the button
        val goalsLayout = (findViewById<CardView>(R.id.cardGoals)
            .getChildAt(0) as LinearLayout)
            .getChildAt(1) as LinearLayout

        goalsLayout.addView(customButton)
    }

    private fun validateInput(): Boolean {
        // Check if age is valid
        val age = etCurrentAge.text.toString()
        if (age.isEmpty()) {
            showError("Please enter your current age")
            return false
        }

        // Check if savings percentage is valid
        val savingsPercentage = etSavingsPercentage.text.toString()
        if (savingsPercentage.isEmpty()) {
            showError("Please enter a savings percentage")
            return false
        }

        val savingsValue = savingsPercentage.toIntOrNull()
        if (savingsValue == null || savingsValue < 0 || savingsValue > 100) {
            showError("Savings percentage must be between 0 and 100")
            return false
        }

        // Check if at least one goal is selected
        if (selectedGoals.isEmpty()) {
            showError("Please select at least one goal")
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun calculateFinancialGoals() {
        val age = etCurrentAge.text.toString().toInt()
        val incomeRange = spinnerIncome.selectedItem.toString()
        val savingsPercentage = etSavingsPercentage.text.toString().toInt()

        // Get average income value from selected range
        val averageIncome = getAverageIncomeFromRange(incomeRange)

        // Calculate monthly savings
        val monthlySavings = averageIncome * savingsPercentage / 100

        // Calculate time to reach goals based on estimated costs
        val results = StringBuilder()

        for (goal in selectedGoals) {
            val goalCost = when (goal) {
                "House" -> 300000
                "Car" -> 30000
                "Travel" -> 5000
                else -> 10000  // Default for custom goals
            }

            // Calculate months to reach goal (simple calculation)
            val monthsToReachGoal = goalCost / monthlySavings
            val yearsToReachGoal = monthsToReachGoal / 12

            results.append("$goal: ")
            results.append("$yearsToReachGoal years (${monthsToReachGoal.toInt()} months)\n")
        }

        // Show results
        showResultsDialog(results.toString(), monthlySavings)
    }

    private fun getAverageIncomeFromRange(incomeRange: String): Double {
        return when (incomeRange) {
            "Below $2,000" -> 1500.0
            "$2,000 - $3,999" -> 3000.0
            "$4,000 - $5,999" -> 5000.0
            "$6,000 - $7,999" -> 7000.0
            "$8,000 - $9,999" -> 9000.0
            "$10,000 and above" -> 12000.0
            else -> 5000.0
        }
    }

    private fun showResultsDialog(results: String, monthlySavings: Double) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Financial Goals Projection")
            .setMessage("Based on your monthly savings of $${monthlySavings.toInt()}, here's how long it will take to reach your goals:\n\n$results")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showInfoDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("About Financial Goals App")
            .setMessage("This app helps you plan your financial future by calculating how long it will take to reach your savings goals based on your current income and savings rate.\n\n" +
                    "Enter your details, select your goals, and tap Calculate to see your personalized projection.")
            .setPositiveButton("OK", null)
            .show()
    }
}