package com.uhf.scanlable

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

interface OnClickCallback{
    fun onClick(view: View,position: Int)
}

class ScanInfoAdapter(private val context:Context, private val data:List<UHfData.InventoryTagMap>,onClickCallback:OnClickCallback)
    :RecyclerView.Adapter<ScanInfoAdapter.ViewHolder>(){

    private var mContext=context
    private var mData=data
    private var mOnClickCallback=onClickCallback

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view:View= LayoutInflater.from(mContext).inflate(R.layout.scan_info,parent,false)
        view.setOnClickListener(ClickListener())
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var tv_id:TextView=view.findViewById(R.id.tv_id)
        var tv_epc:TextView=view.findViewById(R.id.tv_epc)
        var tv_times:TextView=view.findViewById(R.id.tv_times)
        var tv_rssi:TextView=view.findViewById(R.id.tv_rssi)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.tv_id?.text = mData[position].btAntId.toString()
        holder?.tv_epc?.text=mData[position].strEPC
        holder?.tv_times?.text=mData[position].nReadCount.toString()
        holder?.tv_rssi?.text=mData[position].strRSSI
    }

    inner class ClickListener:View.OnClickListener{
        override fun onClick(v: View?) {
            mOnClickCallback.onClick(v!!,v.tag as Int)
        }
    }

}
