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
}
