package xh.zero.desktoptest.my

import DesktopAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.PagerAdapter

class DesktopViewPager : ViewPager {

    lateinit var _adapter: DesktopAdapter

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    fun initial(fm: FragmentManager, titles: List<String>) {
        _adapter = DesktopAdapter(fm, titles)
        adapter = _adapter
    }

    fun addPageRight() {
        val previousPage = currentItem
        _adapter.addPageRight()
        currentItem = previousPage + 1
    }

}