package xh.zero.desktoptest.my

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import xh.zero.desktoptest.App
import xh.zero.desktoptest.R

class AppInfoAdapter(
    private val items: List<App>,
    private val itemClick: (pkgName: String) -> Unit
) : PlainListAdapter<App>(items) {
    override fun itemLayoutId(): Int = R.layout.item_app_info

    override fun bindView(v: View, item: App, position: Int) {
        val itemView = v.findViewById<AppLauncherView>(R.id.v_app_launcher)
        itemView.setIcon(item.icon)
        // com.android.calendar
        // com.android.calculator2
        // com.android.camera2
        // com.android.contacts
        // com.android.music
        // com.android.dialer
        // com.android.settings
        // com.android.deskclock
        // com.android.documentsui
        // app图标替换
        itemView.setLabel(item.label)

//        item._isLimited = position % 5 == 0

        itemView.setOnClickListener {
            itemClick(item.packageName)
        }
//        if (MainActivity.getLauncher() != null) {
//            itemView.setOnLongClickListener(MainActivity.getLauncher()!!.getItemOptionView().getLongClickListener(item))
//        }
    }
}