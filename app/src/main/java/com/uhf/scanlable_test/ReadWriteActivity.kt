package com.uhf.scanlable_test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pda.serialport.Tools
import com.uhf.scanlable.R
import com.uhf.scanlable.UHfData
import com.uhf.scanlable.UHfData.UHfGetData
import com.uhf.scanlable_test.Util.isEtEmpty
import com.uhf.scanlable_test.Util.isLenLegal
import com.uhf.scanlable_test.Util.play
import com.uhf.scanlable_test.Util.showWarning

class ReadWriteActivity : AppCompatActivity(),View.OnClickListener {

    private val mode = 0
    var selectedEd = 3
    var selectedWhenPause = 0

    lateinit var tv_EPC: TextView
    lateinit var sp_select: Spinner
    lateinit var et_stadr: EditText
    lateinit var et_len: EditText
    lateinit var et_pwd: EditText
    lateinit var et_data: EditText
    lateinit var btn_read: Button
    lateinit var btn_write: Button
    lateinit var btn_writeEpc: Button

    private val CHECK_W_6B = 0
    private val CHECK_R_6B = 1
    private val CHECK_W_6C = 2
    private val CHECK_R_6C = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_write)

        init()

    }

    override fun onResume() {
        if(!UHfData.getuhf_id().equals(tv_EPC.text.toString())){
            tv_EPC.text= UHfData.getuhf_id()
        }
        UHfData.setuhf_id("10100100500006FF00")
        et_data.setText(UHfData.getuhf_id())
        super.onResume()
    }

    fun init(){
        tv_EPC=findViewById(R.id.tv_rw_epc)
        sp_select=findViewById(R.id.sp_select)

        et_stadr=findViewById(R.id.et_stadr)
        et_stadr.setText("0")
        et_len=findViewById(R.id.et_len)
        et_len.setText("4")
        et_pwd=findViewById(R.id.et_pwd)
        et_pwd.setText("00000000")
        et_data=findViewById(R.id.et_data)
        btn_read=findViewById(R.id.btn_read)
        btn_read.setOnClickListener(this)
        btn_write=findViewById(R.id.btn_write)
        btn_write.setOnClickListener(this)
        btn_writeEpc=findViewById(R.id.btn_writeEpc)
        btn_writeEpc.setOnClickListener(this)
    }

    private fun checkContent(check: Int):Boolean{
        when(check){
            CHECK_W_6C -> {
                if (isEtEmpty(et_data))
                    return showWarning(this, R.string.content_empty_warning)
                if (Integer.valueOf(et_len.text.toString()) != et_data.text.length / 4)
                    return showWarning(this, R.string.length_content_warning)
                if (!(isLenLegal(et_data)))
                    return showWarning(this, R.string.str_lenght_odd_warning)
            }
            CHECK_R_6C -> {
                if (isEtEmpty(et_stadr))
                    return showWarning(this, R.string.wordptr_empty_warning)
                if (isEtEmpty(et_len))
                    return showWarning(this, R.string.length_empty_warning)
                if (isEtEmpty(et_pwd))
                    return showWarning(this, R.string.pwd_empty_warning)
                if (!isLenLegal(et_pwd))
                    return showWarning(this, R.string.str_lenght_odd_warning)
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_write -> {
                Log.d("chenye","on write button click")
                if (!checkContent(CHECK_W_6C)) return
                try {
                    var stadr: Int = Integer.valueOf(et_stadr.text.toString())
                    var word: ByteArray = Tools.intToByte(stadr)
                    val result = UHfGetData.Write6c(
                        (Integer.valueOf(et_len.text.toString()) as Int).toByte(),
                        (tv_EPC.text.toString().length / 4) as Byte,
                        UHfGetData.hexStringToBytes(tv_EPC.text.toString()),
                        selectedEd.toByte(),
                        word,
                        UHfGetData.hexStringToBytes(et_data.text.toString()),
                        UHfGetData.hexStringToBytes(et_pwd.text.toString())
                    )
                    if (result != 0) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.write_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        et_data.setText("")
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.write_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {

                }
            }
            R.id.btn_read -> {
                Log.d("chenye","on read button click")
                if (!checkContent(CHECK_R_6C)) return
                try {
                    val wordPtr: Int = Integer.valueOf(et_stadr.text.toString())
                    val word = Tools.intToByte(wordPtr)
                    val result = UHfGetData.Read6C(
                        (tv_EPC.getText().toString().length / 4) as Byte,
                        UHfGetData.hexStringToBytes(tv_EPC.text.toString()),
                        selectedEd.toByte(),
                        word,
                        java.lang.Byte.valueOf(et_len.text.toString()),
                        UHfGetData.hexStringToBytes(et_pwd.getText().toString())
                    )
                    val temp = UHfGetData.bytesToHexString(
                        UHfGetData.getRead6Cdata(),
                        0,
                        java.lang.Byte.valueOf(et_len.getText().toString()) * 2
                    ).toUpperCase()
                    if (result != 0) {
                        et_data.setText("")
                        showToast(getString(R.string.read_fail))
                    } else {
                        et_data.setText(temp.toUpperCase())
                        showToast(getString(R.string.read_success))
                        play(1, 0)
                    }
                } catch (ex: java.lang.Exception) {
                }
            }
            R.id.btn_writeEpc -> {
            }
            else->{}
        }
    }

    private var mToast: Toast? = null

    private fun showToast(content: String) {
        if (mToast == null) {
            mToast = Toast.makeText(this, content, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(content)
        }
        mToast!!.show()
    }

}