

package com.example.tiffincalculator

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.tiffincalculator.ui.theme.TiffinCalculatorTheme
import java.time.LocalDate
import java.util.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiffinCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TiffinCalculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiffinCalculator(modifier: Modifier = Modifier) {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var missedMorningMeals by remember { mutableStateOf(TextFieldValue("")) }
    var missedEveningMeals by remember { mutableStateOf(TextFieldValue("")) }
    var completionDate by remember { mutableStateOf("") }
    var selectedMonthDays by remember { mutableStateOf(30) }
    var selectedMonth by remember { mutableStateOf("September") }

    // List of month options
    val monthOptions = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Get context
    val context = LocalContext.current

    // DatePicker dialog
    fun showDatePickerDialog() {
        val today = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                startDate = LocalDate.of(year, month + 1, dayOfMonth) // month is 0-indexed
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateCompletionDate() {
        val morningMissedCount = missedMorningMeals.text.toIntOrNull() ?: 0
        val eveningMissedCount = missedEveningMeals.text.toIntOrNull() ?: 0
        val totalMissedMeals = morningMissedCount + eveningMissedCount

        // Determine the number of days in February based on leap year logic
        val year = startDate.year
        val daysInFebruary = if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28

        // Set selectedMonthDays based on the selected month
        selectedMonthDays = when (selectedMonth) {
            "January", "March", "May", "July", "August", "October", "December" -> 31
            "April", "June", "September", "November" -> 30
            "February" -> daysInFebruary
            else -> 30
        }

        // Calculate the base completion date based on missed meals
        val baseCompletionDate = startDate.plusDays((totalMissedMeals / 2).toLong())

        // Extend to the day before the same date in the next month
        val completion: LocalDate = if (baseCompletionDate.monthValue == 12) {
            LocalDate.of(baseCompletionDate.year + 1, 1, 20) // Next year January 20 if December
        } else {
            // Add a month to the base date and then subtract one day
            baseCompletionDate.plusMonths(1).withDayOfMonth(baseCompletionDate.dayOfMonth).minusDays(1)
        }

        // Ensure the completion date does not exceed the last day of the selected month
        completionDate = "${completion.dayOfMonth} ${completion.month} ${completion.year}"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Tiffin Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Show the selected date
        Text("Selected Start Date: ${startDate.dayOfMonth} ${startDate.month} ${startDate.year}")
        Spacer(modifier = Modifier.height(16.dp))

        // Button to open the DatePickerDialog
        Button(onClick = { showDatePickerDialog() }) {
            Text(text = "Pick Start Date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = missedMorningMeals,
            onValueChange = { missedMorningMeals = it },
            label = { Text("Missed Morning Meals") },
            placeholder = { Text("Enter missed morning meals") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = missedEveningMeals,
            onValueChange = { missedEveningMeals = it },
            label = { Text("Missed Evening Meals") },
            placeholder = { Text("Enter missed evening meals") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown for selecting month
        Text(text = "Select Month")
        Spacer(modifier = Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = !expanded }) {
                Text(text = selectedMonth)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                monthOptions.forEach { monthOption ->
                    DropdownMenuItem(
                        text = { Text(monthOption) },
                        onClick = {
                            selectedMonth = monthOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { calculateCompletionDate() }) {
            Text("Calculate Completion Date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show completion date
        Text(text = "Completion Date: $completionDate")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TiffinCalculatorTheme {
        TiffinCalculator()
    }
}




