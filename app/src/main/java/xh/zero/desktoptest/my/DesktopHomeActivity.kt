package xh.zero.desktoptest.my

import android.appwidget.AppWidgetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import xh.zero.desktoptest.*
import xh.zero.desktoptest.homeparts.HpDragOption

class DesktopHomeActivity : AppCompatActivity() {

    companion object {

        var launcher: DesktopHomeActivity? = null

    }

    private lateinit var appLoader: AppManager
    private lateinit var itemOptionView: ItemOptionView
    private lateinit var cellLayout: CellLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desktop_home)
        launcher = this

        itemOptionView = findViewById<ItemOptionView>(R.id.item_option_view)
        cellLayout = findViewById(R.id.cell_layout)

        itemOptionView.registerDropTarget(object : DropTargetListener {
            override fun getView(): View {
                return cellLayout
            }

            override fun onStart(
                action: DragAction.Action?,
                location: PointF?,
                isInside: Boolean
            ): Boolean {
               return true
            }

            override fun onStartDrag(action: DragAction.Action?, location: PointF?) {

            }

            override fun onDrop(action: DragAction.Action?, location: PointF?, item: Item?) {
                val x = location?.x?.toInt()
                val y = location?.y?.toInt()
            }

            override fun onMove(action: DragAction.Action?, location: PointF?) {

            }

            override fun onEnter(action: DragAction.Action?, location: PointF?) {

            }

            override fun onExit(action: DragAction.Action?, location: PointF?) {

            }

            override fun onEnd() {

            }
        })

        appLoader = AppManager.getInstance(this)
        appLoader.addUpdateListener { apps ->
            createDesktop(apps)
            false
        }
        appLoader.init()

    }

    fun getItemOptionView() : ItemOptionView {
        return findViewById(R.id.item_option_view)
    }

    private fun createDesktop(apps: List<App>) {
        val cell = findViewById<CellLayout>(R.id.cell_layout)
        for (i in 0..13) {
            val tv = TextView(this)
            tv.text = "cell-${i}"
            cell.addView(tv)
            tv.setBackgroundColor(Color.CYAN)
            tv.gravity = Gravity.CENTER
            tv.setOnLongClickListener(DragHandler.getLongClick(Item.newAppItem(apps.first()), DragAction.Action.DESKTOP,  null))
        }
    }


}