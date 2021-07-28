package xh.zero.desktoptest.my

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xh.zero.desktoptest.R

class DragTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_test)

        val rc = findViewById<RecyclerView>(R.id.recycler_view)
        rc.adapter = DragItemAdapter(listOf("cell 1", "cell 2", "cell 3"))
        rc.layoutManager = GridLayoutManager(this, 5)
    }
}