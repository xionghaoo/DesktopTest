package xh.zero.desktoptest.my

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xh.zero.desktoptest.*
import xh.zero.desktoptest.databinding.FragmentAppMarketBinding
import java.lang.IllegalArgumentException


/**
 * 已安装的App列表
 */
class AppMarketFragment : Fragment() {

    private var appMaxNum: Int = 0
    private var position: Int = 0
    private var pageNum: Int = 0

//    private lateinit var adapter: AppInfoAdapter

    private var _binding: FragmentAppMarketBinding? = null
    private val binding get() = _binding!!

    private var listener: OnFragmentActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            listener = context
        } else {
            throw IllegalArgumentException("Activity must implement OnFragmentActionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun setPosition(p: Int) {
        position = p
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            appMaxNum = getInt(ARG_LIMIT_APP_NUM)
            pageNum = getInt(ARG_PAGE_NUM)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.rcAppList.layoutManager = object : GridLayoutManager(context, 6) {
//            override fun canScrollVertically(): Boolean = false
//            override fun canScrollHorizontally(): Boolean = false
//        }
//        adapter = AppInfoAdapter(emptyList()) { pkgName ->
//            // 点击图标时启动app
//            requireActivity().packageManager.apply {
//                val launchIntent = getLaunchIntentForPackage(pkgName)
//                startActivity(launchIntent)
//            }
//        }
//        binding.rcAppList.adapter = adapter
        updateApps()

    }

    fun updateApps() {
//        listener?.getApps()?.also {
//            adapter.updateData(it.filterIndexed { index, _ ->
//                index >= position * appMaxNum && index < position * appMaxNum + appMaxNum
//            })
//        }
        AppManager.getInstance(context).getAllApps()
        AppManager.getInstance(context).addUpdateListener { apps ->
//            adapter.updateData(apps.filterIndexed { index, _ ->
//                index >= position * appMaxNum && index < position * appMaxNum + appMaxNum
//            })

            for (i in 0.until(apps.size)) {
                val item = Item.newAppItem(apps[i])
                val pos = binding.cellContainer.findFreeSpace()
                if (pos != null) {
                    item._x = pos.x
                    item._y = pos.y
                    val itemView = ItemViewFactory.getItemView(context, null, DragAction.Action.DESKTOP, item)
                    item._location = Definitions.ItemPosition.Desktop
                    binding.cellContainer.addViewToGrid(itemView, item._x, item._y, item._spanX, item._spanY)
                }
            }
            false
        }
    }

    interface OnFragmentActionListener {
        fun getApps(): List<App>
    }

    companion object {
        val GAME_LIST = arrayOf("Blockly WebView")
        private const val ARG_LIMIT_APP_NUM = "ARG_LIMIT_APP_NUM"
        private const val ARG_PAGE_NUM = "ARG_PAGE_NUM"
        fun newInstance(limitAppNum: Int, pageNum: Int) = AppMarketFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_LIMIT_APP_NUM, limitAppNum)
                putInt(ARG_PAGE_NUM, pageNum)
            }
        }
    }
}