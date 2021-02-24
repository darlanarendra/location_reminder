package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.util.*

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    private lateinit var viewModel:SaveReminderViewModel
    private lateinit var fakeDataSource:FakeDataSource
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    val instantTaskExecRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
    }
    @Test
    fun viewModel_savingDataWithoutRadiusWillFail(){
        val returnValue = viewModel.validateAndSaveReminder(dataItemWithOutRadius)
        println("returnValue"+returnValue)
        assertThat(returnValue).isFalse()
        assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.radius_must_be_specified)
    }

    @Test
    fun viewModel_saveDataWithoutLocationCoordinatesWillFail(){
        val returnValue = viewModel.validateAndSaveReminder(dataItemWithoutLocationCoordinates)
        assertThat(returnValue).isFalse()
        assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_select_location)
    }

    @Test
    fun viewModel_saveDataWithoutTitleWillFail(){
        val returnValue = viewModel.validateAndSaveReminder(dataItemWithOutTitle)
        assertThat(returnValue).isFalse()
        assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun viewModel_saveDataWithAllNullsFail(){
        val returnData = viewModel.validateAndSaveReminder(dataItemWithAllNulls)
        assertThat(returnData).isFalse()
        assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_enter_title)
    }


    @Test
    fun viewModel_savingDataNavigatesBack(){
        viewModel.validateAndSaveReminder(dataItemValid)
        assertThat(viewModel.navigationCommand.value).isEqualTo(NavigationCommand.Back)
    }


    @Test
    fun viewModel_savingValidItemSucceeds(){
        val returnValue = viewModel.validateAndSaveReminder(dataItemValid)
        assertThat(returnValue).isTrue()
        assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.reminder_saved)
    }

    @Test
    fun viewModel_showLoadingStatusAtStartAndHidesOnceDone(){
        mainCoroutineRule.pauseDispatcher()
        viewModel.validateAndSaveReminder(dataItemValid)
        assertThat(viewModel.showLoading.value).isTrue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.value).isFalse()
    }
}