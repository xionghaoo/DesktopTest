package xh.zero.desktoptest

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import xh.zero.desktoptest.homeparts.HpDragOption

class MainActivity : AppCompatActivity() {

    companion object {
        var itemTouchX = 0f
        var itemTouchY = 0f

        var launcher: MainActivity? = null

        var db: DatabaseHelper? = null
        var appLoader: AppManager? = null

        const val REQUEST_PICK_APPWIDGET = 0x2678

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launcher = this
        db = DatabaseHelper(this)
        appLoader = AppManager.getInstance(this)

//        appWidgetManager = AppWidgetManager.getInstance(this)
//        appWidgetHost = WidgetHost(applicationContext, R.id.app_widget_host)
//        appWidgetHost?.startListening()

        val findViewById = findViewById<View>(R.id.leftDragHandle)
        val findViewById2 = findViewById<View>(R.id.rightDragHandle)

        val hpDragOption = HpDragOption()
        hpDragOption.initDragNDrop(this, findViewById, findViewById2, getItemOptionView())

        getDesktop().setPageIndicator(getDesktopIndicator())
        getDesktopIndicator().setMode(PagerIndicator.Mode.DOTS)

        initAppManager()

//        val cell = findViewById<CellLayout>(R.id.cell_layout)
//        for (i in 0..13) {
//            val tv = TextView(this)
//            tv.text = "cell-${i}"
//            cell.addView(tv)
//            tv.gravity = Gravity.CENTER
//        }
    }

    fun getItemOptionView() : ItemOptionView {
        return findViewById(R.id.item_option_view)
    }

    fun getDesktop(): Desktop {
        return findViewById(R.id.desktop)
    }

    fun getGroupPopup(): GroupPopupView {
        return findViewById(R.id.groupPopup)
    }

    fun getDesktopIndicator(): PagerIndicator {
        return findViewById(R.id.desktopIndicator)
    }

    protected fun initAppManager() {
        if (true) {
//            Setup.appSettings().setAppFirstLaunch(false)
//            Setup.appSettings().setAppShowIntro(false)
            val appDrawerBtnItem = Item.newActionItem(8)
            appDrawerBtnItem._x = 2
            db?.saveItem(
                appDrawerBtnItem,
                0,
                Definitions.ItemPosition.Dock
            )
        }
        getDesktop().initDesktop()
        appLoader?.addUpdateListener(AppUpdateListener { apps ->
            for (i in 0..20) {
                val appItem = Item.newAppItem(apps[i])
                val pos = getDesktop().currentPage.findFreeSpace()
                if (pos != null) {
                    appItem._x = pos.x
                    appItem._y = pos.y
                    db?.saveItem(appItem, 0, Definitions.ItemPosition.Desktop)
                    getDesktop().addItemToPage(appItem, getDesktop().currentItem)
                }
            }
//            getDock().initDock()
            false
        })
        appLoader?.addDeleteListener(AppDeleteListener { apps ->
            getDesktop().initDesktop()
//            getDock().initDock()
            false
        })
        appLoader?.init()

    }

//    fun updateAdapter(apps: List<App>) {
////        com.benny.openlauncher.widget.AppDrawerGrid._apps = apps
//        val items: ArrayList<IconLabelItem> = ArrayList<IconLabelItem>()
//        for (i in apps.indices) {
//            val app = apps[i]
//            val item = IconLabelItem(app.icon, app.label)
//                .withIconSize(52)
//                .withTextColor(Color.WHITE)
//                .withTextVisibility(true)
//                .withIconPadding(8)
//                .withTextGravity(Gravity.CENTER)
//                .withIconGravity(Gravity.TOP)
//                .withOnClickAnimate(false)
//                .withIsAppLauncher(true) // 点击打开app
//                .withOnClickListener(View.OnClickListener { v ->
////                        Tool.startApp(
////                            v.context,
////                            app,
////                            null
////                        )
//                }) // 长按icon拖动到桌面
//                .withOnLongClickListener(
//                    DragHandler.getLongClick(
//                        Item.newAppItem(app),
//                        DragAction.Action.DRAWER,
//                        null
//                    )
//                )
//            db?.saveItem(item)
//        }
////        _gridDrawerAdapter.set(items)
//    }
}