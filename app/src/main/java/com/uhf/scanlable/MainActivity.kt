/*
* Create By Chen Ye
* */
package com.uhf.scanlable

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

const val serial:String="/dev/ttyMT1"  //串口名称
const val tty_speed:Int=57600          //窗口通讯速率

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private val mainActivity:MainActivity=this

    private lateinit var sp_mem:Spinner
    private lateinit var tv_count:TextView
    private lateinit var btn_Scan:Button
    private lateinit var cb_tid:CheckBox
    private lateinit var infoView: RecyclerView

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var data:List<UHfData.InventoryTagMap> = emptyList()

    private lateinit var timer:Timer
    private var isCaneled:Boolean=true
    private val SCAN_INTERVAL:Long = 5
    private val MSG_UPDATE_LISTVIEW = 0
    private val MODE_18000 = 1
    private var Scanflag = false
    private var selectedEd = 0
    private var TidFlag = 0
    private var AntIndex = 0

    private var startTime:Long = 0
    private var keyUpFlag:Boolean=true

    private val mHandler:Handler=object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Looper.prepare()
            when (msg?.obj) {
                "0" -> Toast.makeText(mainActivity, "Port Connect Success", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(mainActivity, "Port Connect Failed", Toast.LENGTH_LONG).show()
            }
            if(isCaneled)
                return
            when(msg?.what){
                MSG_UPDATE_LISTVIEW->{
                    data=UHfData.lsTagList
                    viewAdapter?:let {
                        viewAdapter=ScanInfoAdapter(mainActivity,data,ClickCallback())
                        infoView.adapter=viewAdapter
                    }?.let { infoView.adapter=viewAdapter }
                    tv_count.text = "数量："+viewAdapter.itemCount
                    viewAdapter.notifyDataSetChanged()
                    if(UHfData.mIsNew){
                        Thread{
                            //to do play sound
                        }
                        UHfData.mIsNew=false
                    }
                }
                else->{}
            }
            Looper.loop()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_scan->{
                Log.d("chenye","btn_scan")
                try{
                    if(timer==null){
                        if(viewAdapter!=null) {
                            tv_count.text="数量：0"
                            UHfData.lsTagList.clear()
                            UHfData.dtIndexMap.clear()
                            viewAdapter.notifyDataSetChanged()
                            mHandler.removeMessages(MSG_UPDATE_LISTVIEW)
                            mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW)
                        }
                        selectedEd=sp_mem.selectedItemPosition
                        if(cb_tid.isChecked)
                            TidFlag=1
                        else
                            TidFlag=0
                        if(selectedEd==2)
                            selectedEd=255
                        AntIndex=0
                        isCaneled=false
                        timer=Timer()
                        timer.schedule(timerTask(){
                            if(Scanflag)
                                return@timerTask
                            Scanflag=true
                            UHfData.Inventory_6c(selectedEd,TidFlag)
                            mHandler.removeMessages(MSG_UPDATE_LISTVIEW)
                            mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW)
                            Scanflag=false
                        },SCAN_INTERVAL)
                        btn_Scan.text="停止"
                    }
                }catch (e:Exception){
                    Log.d("chenye",e.message.toString())
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private inline fun initView(){
        sp_mem = findViewById(R.id.sp_mem)
        tv_count=findViewById(R.id.tv_count)
        cb_tid=findViewById(R.id.cb_tid)

        btn_Scan=findViewById(R.id.btn_scan)
        btn_Scan.setOnClickListener(this)

        infoView = findViewById(R.id.info_view)
        viewManager = LinearLayoutManager(this)
        infoView.layoutManager=viewManager
        //viewAdapter = ScanInfoAdapter(this, data,ClickCallback())
        infoView.adapter=viewAdapter
    }

    private fun cancelScan(){
        isCaneled=true
        mHandler.removeMessages(MSG_UPDATE_LISTVIEW)
        if(timer!=null){
            timer.cancel()
            btn_Scan.text="扫描"
            UHfData.lsTagList.clear()
            UHfData.dtIndexMap.clear()
            if(viewAdapter!=null){
                //todo clear data
                //....
                viewAdapter.notifyDataSetChanged()
            }
            tv_count.text="数量：0"
        }
    }

    inner class ClickCallback:OnClickCallback{
        override fun onClick(view: View, position: Int) {
            val tv_epc:TextView=view.findViewById(R.id.tv_epc);
            Toast.makeText(this@MainActivity,tv_epc.text,Toast.LENGTH_SHORT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.list_view_1 -> {
                Log.d("chenye", "list_view_1")
                true
            }
            R.id.list_view_2 -> {
                Log.d("chenye", "list_view_2")
                true
            }
            R.id.grid_view_1 -> {
                Log.d("chenye", "grid_view_1")
                true
            }
            R.id.grid_view_2 -> {
                Log.d("chenye", "grid_view_2")
                true
            }
            R.id.stagger_view_1 -> {
                Log.d("chenye", "stagger_view_1")
                true
            }
            R.id.stagger_view_2 -> {
                Log.d("chenye", "stagger_view_2")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        var msg= Message()
        Thread{
            try {
                var result=UHfData.UHfGetData.OpenUHf(serial, tty_speed)
                msg.obj=result.toString()
            }catch (e: Exception) {
                msg.obj=e
            }
            mHandler.handleMessage(msg)
        }.start()
    }

    override fun onStop() {
        super.onStop()
        UHfData.UHfGetData.CloseUHf()
    }

    override fun onPause() {
        super.onPause()
        cancelScan()
    }



}