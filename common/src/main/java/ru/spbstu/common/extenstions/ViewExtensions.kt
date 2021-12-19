package ru.spbstu.common.extenstions

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.text.TextLengthWatcher
import ru.spbstu.common.utils.DebounceClickListener
import ru.spbstu.common.utils.DebouncePostHandler
import java.time.YearMonth
import java.util.*

@Suppress("DEPRECATION")
fun View.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            systemUiVisibility = flags
        }
    }
}

@Suppress("DEPRECATION")
fun View.clearLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            systemUiVisibility = flags
        }
    }
}

fun View.setDebounceClickListener(
    delay: Long = DebouncePostHandler.DEFAULT_DELAY,
    onClickListener: View.OnClickListener
) {
    setOnClickListener(DebounceClickListener(delay, onClickListener))
}

@SuppressLint("ResourceType")
fun TextView.setResourceColor(@ColorRes resId: Int) {
    val color = Color.parseColor(resources.getString(resId))
    this.setTextColor(color)
}

fun View.rotateView(startAngle: Float = 0f, endAngle: Float = 180f, duration: Long = 300) {
    val rotate = ObjectAnimator.ofFloat(this, "rotation", startAngle, endAngle)
    rotate.duration = duration
    rotate.start()
}

fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this).toInt() }
        top?.run { topMargin = dpToPx(this).toInt() }
        right?.run { rightMargin = dpToPx(this).toInt() }
        bottom?.run { bottomMargin = dpToPx(this).toInt() }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun View.dpToPx(dp: Float): Float = context.dpToPx(dp)

/**
 * Allows you to perform actions when changing the text.
 *
 * @param runBeforeTextEntered Optional. The function that performs something before the minimum is reached.
 * @param runAfterTextEntered Optional. The function that performs something after the minimum is reached.
 * @param runOnTextEntered Optional. The function that performs something when text entered.
 */
fun EditText.setTextLengthWatcher(
    runBeforeTextEntered: (() -> Unit)? = null,
    runAfterTextEntered: (() -> Unit)? = null,
    runOnTextEntered: (() -> Unit)? = null,
) = addTextChangedListener(object : TextLengthWatcher() {
    override fun runBeforeTextEntered() {
        runBeforeTextEntered?.invoke()
    }

    override fun runAfterTextEntered() {
        runAfterTextEntered?.invoke()
    }

    override fun runOnTextEntered() {
        runOnTextEntered?.invoke()
    }
})

fun TextView.setTextColorFromRes(@ColorRes resId: Int) {
    val color = ContextCompat.getColor(context, resId)
    this.setTextColor(color)
}

/**
 * This extension set the capitalized name of the month in STANDALONE format.
 * example: July | Июль
 *
 * @param yearMonth The date in the year month format
 * @param text The text after month string
 */
@SuppressLint("NewApi")
fun TextView.setStandaloneMonthString(yearMonth: YearMonth, text: String = "") {
    val calendar = Calendar.getInstance()
    calendar.set(
        yearMonth.year,
        yearMonth.monthValue - 1,
        yearMonth.atDay(1).dayOfMonth
    )
    val month = android.text.format.DateFormat.format("LLLL", calendar).toString()
        .capitalize()
    this.text = context.getString(R.string.calendar_month_header, month, text)
}