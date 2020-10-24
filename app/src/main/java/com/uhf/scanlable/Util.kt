package com.uhf.scanlable

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.SystemClock
import java.util.*

class Util {
    var sp: SoundPool? = null
    var suondMap: MutableMap<Int, Int>? = null
    var context: Context? = null

    //初始化声音池
    fun initSoundPool(context: Context) {
        this.context = context
        sp = SoundPool(1, AudioManager.STREAM_MUSIC, 1)
        suondMap = HashMap()
        (suondMap as HashMap<Int, Int>)[1] = sp!!.load(context, R.raw.msg0, 1)
    }

    var soundfinished = false

    //播放声音池声音
    fun play(sound: Int, number: Int) {
        soundfinished = true
        val am = this.context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //返回当前AlarmManager最大音量
        val audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

        //返回当前AudioManager对象的音量值
        val audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volumnRatio = audioCurrentVolume / audioMaxVolume
        sp!!.play(
                suondMap!![sound]!!,  //播放的音乐Id
                audioCurrentVolume,  //左声道音量
                audioCurrentVolume,  //右声道音量
                1,  //优先级，0为最低
                number, 1f) //回放速度，值在0.5-2.0之间，1为正常速度
        SystemClock.sleep(200)
        soundfinished = false
    }
    
}