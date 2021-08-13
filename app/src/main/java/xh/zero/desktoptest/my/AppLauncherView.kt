package xh.zero.desktoptest.my

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import xh.zero.desktoptest.databinding.WidgetAppLauncherBinding

class AppLauncherView : FrameLayout {

    private lateinit var binding: WidgetAppLauncherBinding

    constructor(context: Context) : super(context) {
        initial(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initial(context)
    }

    private fun initial(context: Context) {
        binding = WidgetAppLauncherBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setIcon(drawable: Drawable?) {
        binding.ivAppIcon.setImageDrawable(drawable)
    }

    fun setLabel(label: String?) {
        binding.tvAppLabel.text = label
    }
}