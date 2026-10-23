package com.tigerworkshop.sms2telegram.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.tigerworkshop.sms2telegram.R
import com.tigerworkshop.sms2telegram.data.SettingsRepository
import com.tigerworkshop.sms2telegram.databinding.ActivitySenderSettingsBinding

class SenderSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySenderSettingsBinding
    private lateinit var repository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySenderSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = SettingsRepository(this)
        binding.toolbar.setNavigationOnClickListener { finish() }
        render()
        binding.switchSenderFilter.setOnCheckedChangeListener { _, checked ->
            val current = repository.loadSenderAllowlist()
            if (current.enabled == checked) return@setOnCheckedChangeListener
            if (checked) {
                repository.saveSenderAllowlist(current.copy(enabled = true))
                render()
            } else {
                // Keep filtering until the user confirms allowing all senders.
                binding.switchSenderFilter.isChecked = true
                AlertDialog.Builder(this)
                    .setTitle(R.string.sender_disable_title)
                    .setMessage(R.string.sender_disable_message)
                    .setPositiveButton(R.string.sender_disable_confirm) { _, _ ->
                        repository.saveSenderAllowlist(repository.loadSenderAllowlist().copy(enabled = false))
                        render()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }
        binding.inputSender.doAfterTextChanged { binding.inputLayoutSender.error = null }
        binding.buttonAddSender.setOnClickListener { addSender() }
    }

    private fun addSender() {
        val sender = binding.inputSender.text?.toString()?.trim().orEmpty()
        val current = repository.loadSenderAllowlist()
        when {
            sender.isEmpty() -> binding.inputLayoutSender.error = getString(R.string.sender_blank_error)
            current.senders.any { it.equals(sender, ignoreCase = true) } ->
                binding.inputLayoutSender.error = getString(R.string.sender_duplicate_error)
            else -> {
                repository.saveSenderAllowlist(current.copy(senders = current.senders + sender))
                binding.inputSender.text?.clear()
                render()
            }
        }
    }

    private fun render() {
        val current = repository.loadSenderAllowlist()
        binding.switchSenderFilter.isChecked = current.enabled
        binding.textFilterStatus.text = getString(
            when {
                !current.enabled -> R.string.sender_filter_off
                current.senders.isEmpty() -> R.string.sender_filter_empty
                else -> R.string.sender_filter_active
            }, current.senders.size
        )
        binding.senderList.removeAllViews()
        if (current.senders.isEmpty()) {
            binding.senderList.addView(TextView(this).apply { setText(R.string.sender_list_empty) })
        }
        current.senders.sortedWith(String.CASE_INSENSITIVE_ORDER).forEach { sender ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            row.addView(TextView(this).apply {
                text = sender
                textSize = 16f
                setTextIsSelectable(true)
            }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                setText(R.string.sender_remove)
                contentDescription = getString(R.string.sender_remove_description, sender)
                setOnClickListener {
                    val latest = repository.loadSenderAllowlist()
                    repository.saveSenderAllowlist(latest.copy(senders = latest.senders - sender))
                    render()
                }
            })
            binding.senderList.addView(row)
        }
    }
}
