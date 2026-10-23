package com.tigerworkshop.sms2telegram.ui

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.tigerworkshop.sms2telegram.R
import com.tigerworkshop.sms2telegram.data.SettingsRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SenderSettingsActivityTest {
    private lateinit var repository: SettingsRepository
    @Before fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("sms_forwarder_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        repository = SettingsRepository(context)
    }
    @Test fun userCanAddEnableAndRemoveLastSender() {
        val controller = Robolectric.buildActivity(SenderSettingsActivity::class.java).setup()
        val activity = controller.get()
        activity.findViewById<EditText>(R.id.input_sender).setText(" HelloBike ")
        activity.findViewById<android.view.View>(R.id.button_add_sender).performClick()
        activity.findViewById<SwitchMaterial>(R.id.switch_sender_filter).performClick()
        assertTrue(repository.loadSenderAllowlist().allows("hellobike"))
        assertFalse(repository.loadSenderAllowlist().allows("OtherSender"))
        val row = activity.findViewById<LinearLayout>(R.id.sender_list).getChildAt(0) as LinearLayout
        row.getChildAt(1).performClick()
        assertTrue(repository.loadSenderAllowlist().enabled)
        assertTrue(repository.loadSenderAllowlist().senders.isEmpty())
        assertEquals(activity.getString(R.string.sender_filter_empty), activity.findViewById<TextView>(R.id.text_filter_status).text.toString())
        controller.pause().stop().destroy()
    }
    @Test fun duplicateAndBlankSenderDoNotChangeList() {
        val controller = Robolectric.buildActivity(SenderSettingsActivity::class.java).setup()
        val activity = controller.get()
        val input = activity.findViewById<EditText>(R.id.input_sender)
        val add = activity.findViewById<android.view.View>(R.id.button_add_sender)
        input.setText(" "); add.performClick()
        assertTrue(repository.loadSenderAllowlist().senders.isEmpty())
        input.setText("HelloBike"); add.performClick()
        input.setText("HELLOBIKE"); add.performClick()
        assertEquals(setOf("HelloBike"), repository.loadSenderAllowlist().senders)
        controller.pause().stop().destroy()
    }
    @Test fun disablingRequiresConfirmationAndCancelKeepsFilter() {
        val controller = Robolectric.buildActivity(SenderSettingsActivity::class.java).setup()
        val toggle = controller.get().findViewById<SwitchMaterial>(R.id.switch_sender_filter)
        toggle.performClick(); toggle.performClick()
        assertTrue(repository.loadSenderAllowlist().enabled)
        ShadowAlertDialog.getLatestAlertDialog().getButton(android.content.DialogInterface.BUTTON_NEGATIVE).performClick()
        assertTrue(toggle.isChecked)
        toggle.performClick()
        ShadowAlertDialog.getLatestAlertDialog().getButton(android.content.DialogInterface.BUTTON_POSITIVE).performClick()
        assertFalse(repository.loadSenderAllowlist().enabled)
        assertFalse(toggle.isChecked)
        controller.pause().stop().destroy()
    }
    @Test fun settingsSurviveActivityRecreation() {
        val controller = Robolectric.buildActivity(SenderSettingsActivity::class.java).setup()
        controller.get().findViewById<EditText>(R.id.input_sender).setText("HelloBike")
        controller.get().findViewById<android.view.View>(R.id.button_add_sender).performClick()
        controller.get().findViewById<SwitchMaterial>(R.id.switch_sender_filter).performClick()
        controller.recreate()
        assertTrue(controller.get().findViewById<SwitchMaterial>(R.id.switch_sender_filter).isChecked)
        val row = controller.get().findViewById<LinearLayout>(R.id.sender_list).getChildAt(0) as LinearLayout
        assertEquals("HelloBike", (row.getChildAt(0) as TextView).text.toString())
        controller.pause().stop().destroy()
    }
}
