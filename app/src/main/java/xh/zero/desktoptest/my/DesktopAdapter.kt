import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import xh.zero.desktoptest.my.AppMarketFragment
import xh.zero.desktoptest.my.HomeFragment

class DesktopAdapter(
    fm: FragmentManager,
    private val homePageTitles: List<String>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val MAX_APP_NUM = 18

    private var appNum: Int = 0
    private var appPageNum: Int = 0

    private val appPages = ArrayList<AppMarketFragment>()

    override fun getCount(): Int = homePageTitles.size + appPages.size

    override fun getItem(position: Int): Fragment {
        if (position < homePageTitles.size) {
            // 首页内容页
            return HomeFragment.newInstance(position.toString())
        } else {
            // App市场页
            val gridPosition = position - homePageTitles.size
            val frag = appPages[gridPosition]
            frag.setPosition(gridPosition)
            return frag
        }
    }

    // 删除页需要用到
    override fun getItemPosition(obj: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun addPageRight() {
        appPages.add(AppMarketFragment.newInstance(limitAppNum = 20, count))
        notifyDataSetChanged()
    }

    fun setAppNum(num: Int) {
        appNum = num
        appPageNum = if (appNum % MAX_APP_NUM == 0) appNum / MAX_APP_NUM else appNum / MAX_APP_NUM + 1
        appPages.clear()
        for (i in 0.until(appPageNum)) {
            appPages.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
        }
        notifyDataSetChanged()
    }

    fun updateAppGrids() {
        appPages.forEach { gridFrag ->
            if (gridFrag.isAdded) {
                gridFrag.updateApps()
            }
        }
    }

    fun addApp() {
        if (appNum % MAX_APP_NUM == 0) {
            // 添加新的一页
            appNum += 1
            appPageNum = appNum / MAX_APP_NUM
            appPages.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
            notifyDataSetChanged()
        } else {
            appNum += 1
            updateAppGrids()
        }
    }

    fun removeApp() {
        appNum -= 1
        if (appNum % MAX_APP_NUM == 0) {
            // 减少一页
            appPageNum = appNum / MAX_APP_NUM
            if (appPages.isNotEmpty()) {
                appPages.removeAt(appPages.size - 1)
            }
            notifyDataSetChanged()
        } else {
            updateAppGrids()
        }
    }
}