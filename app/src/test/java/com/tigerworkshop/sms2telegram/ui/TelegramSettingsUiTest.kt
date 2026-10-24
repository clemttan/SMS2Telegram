package com.tigerworkshop.sms2telegram.ui

import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import com.tigerworkshop.sms2telegram.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TelegramSettingsUiTest {
    @Test fun chatIdsAllowMultipleLinesAndTokenStartsMasked() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("sms_forwarder_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val ids = activity.findViewById<EditText>(R.id.input_chat_id)
        ids.setText("111,222\n-100333")
        assertEquals("111,222\n-100333", ids.text.toString())
        assertTrue(ids.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE != 0)
        assertTrue(activity.findViewById<EditText>(R.id.input_token).transformationMethod is PasswordTransformationMethod)
        controller.pause().stop().destroy()
    }
    private val chats = listOf(
        com.tigerworkshop.sms2telegram.data.TelegramChatInfo(111L, "Example One", null, null),
        com.tigerworkshop.sms2telegram.data.TelegramChatInfo(222L, "Example Two", null, null)
    )

    private fun latestDialog() =
        org.robolectric.shadows.ShadowDialog.getLatestDialog() as androidx.appcompat.app.AlertDialog

    private fun clickRow(position: Int) {
        latestDialog().listView.performItemClick(null, position, position.toLong())
    }

    private fun pressDialogButton(which: Int) {
        latestDialog().getButton(which).performClick()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test fun selectorPrechecksCurrentIdsAndAppliesMultipleSelections() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val ids = activity.findViewById<EditText>(R.id.input_chat_id)
        ids.setText("111")
        activity.showChatSelectionDialog(chats)
        assertTrue(latestDialog().listView.isItemChecked(0))
        assertFalse(latestDialog().listView.isItemChecked(1))
        clickRow(1)
        assertEquals("111", ids.text.toString())
        pressDialogButton(android.content.DialogInterface.BUTTON_POSITIVE)
        assertEquals(listOf("111", "222"), com.tigerworkshop.sms2telegram.data.ChatRecipients.parse(ids.text.toString()))
        controller.pause().stop().destroy()
    }

    @Test fun cancelDoesNotChangeTheExistingRecipientText() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val ids = activity.findViewById<EditText>(R.id.input_chat_id)
        ids.setText("111")
        activity.showChatSelectionDialog(chats)
        clickRow(1)
        pressDialogButton(android.content.DialogInterface.BUTTON_NEGATIVE)
        assertEquals("111", ids.text.toString())
        controller.pause().stop().destroy()
    }

    @Test fun uncheckingRemovesListedIdsButKeepsUnlistedManualIds() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val ids = activity.findViewById<EditText>(R.id.input_chat_id)
        ids.setText("111,-100999")
        activity.showChatSelectionDialog(chats)
        clickRow(0)
        clickRow(1)
        pressDialogButton(android.content.DialogInterface.BUTTON_POSITIVE)
        assertEquals(listOf("-100999", "222"), com.tigerworkshop.sms2telegram.data.ChatRecipients.parse(ids.text.toString()))
        controller.pause().stop().destroy()
    }

    @Test fun applyingNoSelectionsClearsTheListedRecipients() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val ids = activity.findViewById<EditText>(R.id.input_chat_id)
        ids.setText("111,222")
        activity.showChatSelectionDialog(chats)
        clickRow(0)
        clickRow(1)
        pressDialogButton(android.content.DialogInterface.BUTTON_POSITIVE)
        assertTrue(ids.text.isEmpty())
        controller.pause().stop().destroy()
    }

}
