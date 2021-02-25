package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao

    private val reminderData = ReminderDTO("test",
    "description",
    "location",
    2.3434,
    1.2323,
    300f)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDatabase(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
        RemindersDatabase::class.java).build()
        dao = database.reminderDao()
    }
    @After
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun insertIntoDBSucceeds() = runBlockingTest {
        dao.saveReminder(reminderData)
        Truth.assertThat(dao.getReminders()).hasSize(1)
        Truth.assertThat(dao.getReminders()).contains(reminderData)
    }

    @Test
    fun retrieveFromDBSucceeds() = runBlockingTest {
        dao.saveReminder(reminderData)
        val reminder = dao.getReminderById(reminderData.id)
        Truth.assertThat(reminder).isNotNull()
        Truth.assertThat(reminder?.title).isEqualTo(reminderData.title)
        Truth.assertThat(reminder?.description).isEqualTo(reminderData.description)
        Truth.assertThat(reminder?.location).isEqualTo(reminderData.location)
        Truth.assertThat(reminder?.latitude).isEqualTo(reminderData.latitude)
        Truth.assertThat(reminder?.longitude).isEqualTo(reminderData.longitude)
        Truth.assertThat(reminder?.radius).isEqualTo(reminderData.radius)
    }

    @Test
    fun deleteFromDBSuccess() = runBlockingTest{
        dao.saveReminder(reminderData)
        Truth.assertThat(dao.getReminders()).hasSize(1)
        dao.deleteAllReminders()
        Truth.assertThat(dao.getReminders()).isEmpty()
    }

}