package ru.spbstu.feature.calendar.presentation.helper

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.feature.R

@SuppressLint("UseCompatLoadingForDrawables")
class DotViewCreator(private val context: Context) {

    private val blackBgColor by lazy { context.getDrawable((R.drawable.background_calendar_today_circle)) }

    fun generateView(countView: Int): MutableList<Pair<View, RelativeLayout.LayoutParams>> {
        val viewParamPairList: MutableList<Pair<View, RelativeLayout.LayoutParams>> =
            mutableListOf()
        val rowCount = kotlin.math.ceil((countView.toDouble() / ROW_LIMIT)).toInt()

        var topConstraint = RelativeLayout.ALIGN_PARENT_TOP
        var prevTopViewDot = RelativeLayout.TRUE

        var startConstraint = RelativeLayout.ALIGN_PARENT_START
        var prevEndViewDot = RelativeLayout.TRUE

        var start = 0
        var end = if (countView < ROW_LIMIT) countView else ROW_LIMIT

        for (i in 0 until rowCount) {
            // column
            for (j in start until end) {
                // create view dot
                val dotBinding = View(context)
                // create layout parameter for dot
                val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                dotBinding.id = View.generateViewId()
                dotBinding.background = blackBgColor
                layoutParams.addRule(topConstraint, prevTopViewDot)
                layoutParams.addRule(startConstraint, prevEndViewDot)

                layoutParams.height = context.dpToPx(6F).toInt()
                layoutParams.width = context.dpToPx(6F).toInt()
                layoutParams.leftMargin = context.dpToPx(2F).toInt()
                layoutParams.topMargin = context.dpToPx(2F).toInt()

                prevEndViewDot = dotBinding.id
                startConstraint = RelativeLayout.END_OF
                viewParamPairList.add(dotBinding to layoutParams)
            }
            topConstraint = RelativeLayout.BELOW
            prevTopViewDot = viewParamPairList[i * ROW_LIMIT].first.id
            startConstraint = RelativeLayout.ALIGN_PARENT_START
            prevEndViewDot = RelativeLayout.TRUE

            start = end
            end = if ((end + ROW_LIMIT) > countView) countView else end + ROW_LIMIT
        }
        return viewParamPairList
    }

    companion object {
        private const val ROW_LIMIT = 3
    }
}
