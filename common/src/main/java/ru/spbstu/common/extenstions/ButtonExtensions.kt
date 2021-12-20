package ru.spbstu.common.extenstions

import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import ru.spbstu.common.R

fun MaterialButton.setFilledButtonClickability(condition: Boolean) {
    if (condition) {
        this.isEnabled = true
        this.setBackgroundColor(
            ContextCompat.getColor(context, R.color.button_tint_primary)
        )
    } else {
        this.isEnabled = false
        this.setBackgroundColor(
            ContextCompat.getColor(context, R.color.button_tint_disabled)
        )
    }
}
