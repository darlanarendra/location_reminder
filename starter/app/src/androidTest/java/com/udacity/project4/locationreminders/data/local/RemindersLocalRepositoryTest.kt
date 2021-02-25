package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var database :RemindersDatabase
    private lateinit var repository:RemindersLocalRepository

    private val reminderData = ReminderDTO(
        "Title",
        "description",
        "location",
        2.343434,
        1.2323,
        300f
    )

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDatabase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)
    }

    @After
    fun closeDatabse(){
        database.close()
    }


    @Test
    fun deleteRemindersSucceed() = runBlocking {
        repository.saveReminder(reminderData)
        repository.deleteAllReminders()
        val result = repository.getReminders()
        Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
        result as Result.Success
        Truth.assertThat(result.data).isEmpty()
    }

    @Test
    fun retrieveExisitingReminderFails() = runBlocking {
        val result = repository.getReminder(reminderData.id)
        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
        result as Result.Error
        Truth.assertThat(result.message).isEqualTo("Reminder not found!")
        Truth.assertThat(result.statusCode).isNull()
    }


    @Test
    fun retrieveExistingReminderSucceeds() = runBlocking {
        repository.saveReminder(reminderData)
        val result = repository.getReminder(reminderData.id)
        result as Result.Success
        Truth.assertThat(result.data).isNotNull()
        Truth.assertThat(result.data.title).isEqualTo(reminderData.title)
        Truth.assertThat(result.data.description).isEqualTo(reminderData.description)
        Truth.assertThat(result.data.longitude).isEqualTo(reminderData.longitude)
        Truth.assertThat(result.data.latitude).isEqualTo(reminderData.latitude)
        Truth.assertThat(result.data.location).isEqualTo(reminderData.location)
        Truth.assertThat(result.data.radius).isEqualTo(reminderData.radius)
    }

    @Test
    fun insertReminderSucceeds() = runBlocking {
        repository.saveReminder(reminderData)
        val result = repository.getReminders()
        Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
        result as Result.Success
        Truth.assertThat(result.data).isNotEmpty()
        Truth.assertThat(result.data).hasSize(1)
    }

}