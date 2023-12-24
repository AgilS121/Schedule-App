package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var  viewModel: AddCourseViewModel
    private lateinit var courseName : TextInputEditText
    private lateinit var spinnerDay : Spinner
    private lateinit var startTime : TextView
    private lateinit var endTime : TextView
    private lateinit var lecture : TextInputEditText
    private lateinit var note : TextInputEditText
    private lateinit var startButton : ImageButton
    private lateinit var endButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = AddViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory).get(AddCourseViewModel::class.java)

        courseName = findViewById(R.id.ed_course_name)
        spinnerDay = findViewById(R.id.spinner_day)
        startTime = findViewById(R.id.tv_start_time)
        endTime = findViewById(R.id.tv_end_time)
        lecture = findViewById(R.id.ed_lecture)
        note = findViewById(R.id.ed_note)

        viewModel.saved.observe(this) {condition ->
            if (condition.getContentIfNotHandled() == true) {
                onBackPressed()
            } else {
                Toast.makeText(this, getString(R.string.data_empty), Toast.LENGTH_SHORT).show()
            }
        }

        startButton = findViewById(R.id.ib_start_time)
        endButton = findViewById(R.id.ib_end_time)

        startButton.setOnClickListener {
            showStartTimes()
        }

        endButton.setOnClickListener {
            showEndTimes()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                insertCourse()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun insertCourse() {
        val courses = courseName.text.toString()
        val startTimes = startTime.text.toString()
        val spinnerDays = spinnerDay.selectedItemPosition
        val endTimes = endTime.text.toString()
        val lecturer = lecture.text.toString()
        val notes = note.text.toString()

        viewModel.insertCourse(courses, spinnerDays, startTimes, endTimes, lecturer, notes)
    }

    private fun showStartTimes() {
        TimePickerFragment().show(
            supportFragmentManager, "startTime"
        )
    }

    private fun showEndTimes() {
        TimePickerFragment().show(
            supportFragmentManager, "endTime"
        )
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val times = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
           "startTime" -> startTime.text = times.format(calendar.time)
            "endTime" -> endTime.text = times.format(calendar.time)
        }
    }
}