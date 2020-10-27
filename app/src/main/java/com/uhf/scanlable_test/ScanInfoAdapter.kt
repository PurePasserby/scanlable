package com.uhf.scanlable_test

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.uhf.scanlable.R
import com.uhf.scanlable.UHfData

interface OnItemClickListener{
    fun onItemClick(view:View,position:Int)
}

class ScanInfoAdapter(context:Context, private val data:List<UHfData.InventoryTagMap>, onItemClickListener: OnItemClickListener)
    :RecyclerView.Adapter<ScanInfoAdapter.ViewHolder>(){

    private var mContext=context
    private var mData=data
    private var mOnItemClickListener=onItemClickListener

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View= LayoutInflater.from(mContext).inflate(R.layout.scan_info,parent,false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var tv_id:TextView=view.findViewById(R.id.tv_id)
        var tv_epc:TextView=view.findViewById(R.id.tv_epc)
        var tv_times:TextView=view.findViewById(R.id.tv_times)
        var tv_rssi:TextView=view.findViewById(R.id.tv_rssi)

        fun initialize(item: UHfData.InventoryTagMap, action: OnItemClickListener){
            tv_epc.text=item.strEPC
            tv_times.text=item.nReadCount.toString()
            tv_rssi.text=item.strRSSI

            itemView.setOnClickListener{
                action.onItemClick(itemView,adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.tv_id?.text = position.toString()

        holder.initialize(mData[position],mOnItemClickListener)
    }


}
