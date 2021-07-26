package xh.zero.desktoptest

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup

class CellLayout : ViewGroup {
    private val row = 10
    private val col = 6
    private val cellRectList = ArrayList<ArrayList<Rect>>()
    private var cellWidth: Int = 0
    private var cellHeight: Int = 0

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l - paddingLeft - paddingRight
        val height = b - t - paddingTop - paddingBottom
        cellWidth = width / row
        cellHeight = height / col
        initialCellRect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
        for (i in 0.until(childCount)) {
            val child = getChildAt(i)
            child.measure(
                MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY)
            )
            var rect: Rect? = null
            if (i == 0) {
                rect = cellRectList[0][0]
            } else if (i == 1) {
                rect = cellRectList[1][0]
            } else if (i == 2) {
                rect = cellRectList[2][0]
            } else if (i == 3) {
                rect = cellRectList[3][0]
            }
            child.layout(rect!!.left, rect.top, rect.right, rect.bottom)
        }

    }

    private fun initialCellRect(l: Int, t: Int, r: Int, b: Int) {
        var curLeft = l
        var curTop = t
        var curRight: Int = l + cellWidth
        var curBottom: Int = t + cellHeight

        for (i in 0.until(row)) {
            val colArr = ArrayList<Rect>()
            if (i != 0) {
                curLeft += cellWidth
                curRight += cellWidth
            }
            for (j in 0.until(col)) {
                if (j != 0) {
                    curTop += cellHeight
                    curBottom += cellHeight
                }
                val rect = Rect(curLeft, curTop, curRight, curBottom)
                colArr.add(rect)
            }
            curTop = t
            curBottom = t + cellHeight
            cellRectList.add(colArr)
        }
    }
}