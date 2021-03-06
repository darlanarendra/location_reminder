package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
    }

    @Test
    fun remindersUnavailable_showsError(){
        fakeDataSource.setShouldReturnError(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.value).isNotEmpty()
    }

    @Test
    fun noData_resultIsEmpty() = runBlockingTest {
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.value?.isEmpty()).isTrue()
        assertThat(viewModel.showNoData.value).isTrue()
    }

    @Test
    fun withReminders_resultNotEmpty()= runBlockingTest {
        fakeDataSource.saveReminder(ReminderDTO("title", "description","location", 1.2, 1.1,400f))
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.value?.isEmpty()).isFalse()
        assertThat(viewModel.showLoading.value).isFalse()
        assertThat(viewModel.showNoData.value).isFalse()
    }

    @Test
    fun noData_ShowLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.value).isTrue()
    }

    @Test
    fun noData_hidesLoading() = runBlockingTest {
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.value).isFalse()
    }

}