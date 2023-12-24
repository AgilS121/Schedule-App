package com.dicoding.courseschedule.ui.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.ui.add.AddCourseActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeActivityTest {
    @get:Rule
    val homeActivity = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @Test
    fun test_validateUsersAddTask() {
        onView(withId(R.id.action_add)).perform(click())
        intended(hasComponent(AddCourseActivity::class.java.name))
    }

    @After
    fun clearIntents() {
        Intents.release()
    }
}