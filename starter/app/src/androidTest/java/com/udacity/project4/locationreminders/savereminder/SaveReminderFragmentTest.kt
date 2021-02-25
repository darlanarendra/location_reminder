package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.geofence.GeofenceConstants
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.EspressoIdlingResource
import com.udacity.project4.util.getString
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SaveReminderFragmentTest{
    private lateinit var viewModel: SaveReminderViewModel
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun registerIdlingResources():Unit = IdlingRegistry.getInstance().run {
        register(EspressoIdlingResource.countingIdlingResource)
        register(dataBindingIdlingResource)
    }


    @After
    fun unregisterIdlingResources():Unit = IdlingRegistry.getInstance().run {
        unregister(EspressoIdlingResource.countingIdlingResource)
        unregister(dataBindingIdlingResource)
    }


    @Before
    fun setup(){
        stopKoin()
        val appModule = module{
            single{
                SaveReminderViewModel(ApplicationProvider.getApplicationContext(),
                get() as ReminderDataSource)
            }
            single<ReminderDataSource> { RemindersLocalRepository(get())}
            single{ LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext())}
        }
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(appModule))
        }
        viewModel = GlobalContext.get().koin.get()
    }

    @Test
    fun noTitleWillFail(){
        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!,navController)
        }
        onView(withId(R.id.saveReminder)).perform(click())
        Truth.assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun noLocationWillFail(){
        viewModel.setSelectedRadius(GeofenceConstants.DEFAULT_RADIUS_IN_METERS)
        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY,R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())
        Truth.assertThat(viewModel.showSnackBarInt.value).isEqualTo(R.string.err_select_location)
    }


    @Test
    fun validDataSucceeds(){
        val validDataItem = ReminderDataItem("","","",12.90,12.99,300f)
        val latLng = LatLng(validDataItem.latitude!!, validDataItem.longitude!!)
        viewModel.setselectedLocation(PointOfInterest(latLng, validDataItem.location,null))
        viewModel.setSelectedRadius(GeofenceConstants.DEFAULT_RADIUS_IN_METERS)
        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("Description"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())
        Truth.assertThat(viewModel.showToast.value).isEqualTo(getString(R.string.reminder_saved))
    }
}