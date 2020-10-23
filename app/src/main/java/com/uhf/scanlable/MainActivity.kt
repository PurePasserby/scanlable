/*
* Create By Chen Ye
* */
package com.uhf.scanlable

import android.os.*
import android.service.autofill.OnClickAction
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.util.*

const val serial:String="/dev/ttyMT1"  //串口名称
const val tty_speed:Int=57600          //窗口通讯速率

class MainActivity : AppCompatActivity() {

    private val mainActivity:MainActivity=this

    private lateinit var infoView: RecyclerView
    //private lateinit var viewAdapter: RecyclerView.Adapter<*>
    //private lateinit var viewManager: RecyclerView.LayoutManager

    private var dataSet=emptyList<UHfData.InventoryTagMap>()

    private lateinit var btn_Scan:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_Scan=findViewById(R.id.btn_Scan)

        btn_Scan.setOnClickListener{
            UHfData.Inventory_6c_Mask(0, 16, 0, UHfData.UHfGetData.hexStringToBytes("E200"))
            //dataSet=UHfData.lsTagList

            var dataSet: List<UHfData.InventoryTagMap>
            UHfData.Inventory_6c_Mask(0, 16, 0, UHfData.UHfGetData.hexStringToBytes("E200"))
            dataSet=UHfData.lsTagList
            if(!dataSet.isEmpty()) {
                for (data in dataSet)
                    Log.d("chenye", data.strEPC)
            }
            else
                Log.d("chenye","empty")
        }

        //viewManager = LinearLayoutManager(this)
        //viewAdapter = ScanInfoAdapter()

        infoView = findViewById<RecyclerView>(R.id.info_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            //setHasFixedSize(true)

            // use a linear layout manager
            //layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            //adapter = viewAdapter

        }
    }

    inline fun timer(
        name: String? = null,
        daemon: Boolean = false,
        startAt: Date,
        period: Long,
        crossinline action: TimerTask.() -> Unit
    ){

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.list_view_1-> {
                Log.d("chenye","list_view_1")
                true
            }
            R.id.list_view_2-> {
                Log.d("chenye","list_view_2")
                true
            }
            R.id.grid_view_1 -> {
                Log.d("chenye","grid_view_1")
                true
            }
            R.id.grid_view_2 -> {
                Log.d("chenye","grid_view_2")
                true
            }
            R.id.stagger_view_1->{
                Log.d("chenye","stagger_view_1")
                true
            }
            R.id.stagger_view_2->{
                Log.d("chenye","stagger_view_2")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private val mHandler:Handler=object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Looper.prepare()
            when (msg?.obj) {
                "0" -> Toast.makeText(mainActivity, "success", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(mainActivity, "failed", Toast.LENGTH_LONG).show()
            }
            Looper.loop()
        }
    }

    override fun onStart() {
        super.onStart()
        var msg= Message()
        Thread{
            try {
                var result=UHfData.UHfGetData.OpenUHf(serial, tty_speed)
                msg.obj=result.toString()
            }catch (e:Exception) {
                msg.obj=e
            }
            mHandler.handleMessage(msg)
        }.start()
    }

    override fun onStop() {
        super.onStop()
        UHfData.UHfGetData.CloseUHf()
    }

    fun test_commit(){

    }
}