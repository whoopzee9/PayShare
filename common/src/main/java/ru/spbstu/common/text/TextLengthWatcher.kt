package ru.spbstu.common.text

import android.text.Editable
import android.text.TextWatcher

abstract class TextLengthWatcher : TextWatcher {
    /**
     * The function that performs something before the minimum of text limit  is reached.
     */
    abstract fun runBeforeTextEntered()

    /**
     * The function that performs something after the minimum of text limit  is reached.
     */
    abstract fun runAfterTextEntered()

    /**
     * The function that performs something when text entered.
     */
    abstract fun runOnTextEntered()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        runBeforeTextEntered()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        runOnTextEntered()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty()) {
            runBeforeTextEntered()
        } else {
            runAfterTextEntered()
        }
    }
}