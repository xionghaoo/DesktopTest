package xh.zero.desktoptest

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat

class CellLayout : ViewGroup {
    private val row = 10
    private val col = 6
    private val cellRectList = ArrayList<ArrayList<Rect>>()
    private var cellWidth: Int = 0
    private var cellHeight: Int = 0

    private var detector: GestureDetectorCompat = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {

        private var touchX: Float = 0f
        private var touchY: Float = 0f

        override fun onLongPress(e: MotionEvent?) {
            if (e != null) {
                val spanX = e.x.toInt() / cellWidth
                val spanY = e.y.toInt() / cellHeight
                val index: Int = spanY * row + spanX
                val selectedView = getChildAt(index)

                selectedView.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            touchX = event.x
                            touchY = event.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            v.translationX = event.x - touchX
                            v.translationY = event.y - touchY
                        }
                        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                            v.translationX = 0f
                            v.translationY = 0f
                        }
                    }
                    return@setOnTouchListener true
                }
            }
        }
    })

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (detector.onTouchEvent(event)) {
            return true
        } else {
            return super.onTouchEvent(event)
        }
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
            var rect = cellRectList[i / row][i % row]
            child.layout(rect.left, rect.top, rect.right, rect.bottom)
        }

    }

    private fun initialCellRect(l: Int, t: Int, r: Int, b: Int) {
        var curLeft = l
        var curTop = t
        var curRight: Int = l + cellWidth
        var curBottom: Int = t + cellHeight

        for (i in 0.until(col)) {
            val rowArr = ArrayList<Rect>()
            if (i != 0) {
                curTop += cellHeight
                curBottom += cellHeight
            }
            for (j in 0.until(row)) {
                if (j != 0) {
                    curLeft += cellWidth
                    curRight += cellWidth
                }
                val rect = Rect(curLeft, curTop, curRight, curBottom)
                rowArr.add(rect)
            }
//            curTop = t
//            curBottom = t + cellHeight
            // 重置水平偏移
            curLeft = l
            curRight = l + cellWidth
            cellRectList.add(rowArr)
        }
    }
}