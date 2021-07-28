package xh.zero.desktoptest.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xh.zero.desktoptest.R

class DragItemAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<DragItemAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.list_item_drag_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tv = holder.itemView as TextView
        tv.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}