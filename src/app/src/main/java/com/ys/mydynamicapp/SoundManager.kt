package com.ys.mydynamicapp

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

class SoundManager(private val context: Context) {

    private val soundPool = SoundPool.Builder().setAudioAttributes(
        AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    ).setMaxStreams(2).build()

    private val loadedSounds = ArrayList<LoadedSound>()


    // Sound class
    abstract inner class LoadedSound(private val soundID: Int, private val durationMS: Int) {
        protected var lastPlayedMS = 0L
        var usable = true

        val duration: Int
            get() = durationMS

        open fun isPlaying(): Boolean {
            return usable && System.currentTimeMillis() - lastPlayedMS < durationMS
        }


        fun getLastPlayed(): Long {
            return lastPlayedMS
        }

        protected fun playSound(leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Int {
            lastPlayedMS = System.currentTimeMillis()
            return soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate)
        }

        open fun play(leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Boolean {
            if (usable)
                return playSound(leftVolume, rightVolume, priority, loop, rate) != 0
            return false
        }

        open fun stop() {}

        fun unload()
        {
            soundPool.unload(soundID)
            usable = false
        }
    }

    inner class StackableLoadedSound(soundID: Int,durationMS: Int) : LoadedSound(soundID, durationMS) {
        private val streamIDs = ArrayList<Int>()
        override fun play(leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Boolean {
            if (usable)
            {

                val streamID = playSound(leftVolume, rightVolume, priority, loop, rate)
                if (streamID != 0)
                {
                    streamIDs.add(streamID)
                    return true
                }
            }
            return false
        }

        override fun stop()
        {
            for (streamID in streamIDs)
            {
                soundPool.stop(streamID)
            }
            streamIDs.clear()
        }
    }

    inner class UnstackableLoadedSound(soundID: Int,durationMS: Int) : LoadedSound(soundID, durationMS) {
        private var streamID = 0
        override fun play(leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Boolean {
            if (usable && !isPlaying())
            {
                lastPlayedMS = System.currentTimeMillis()
                streamID = playSound(leftVolume, rightVolume, priority, loop, rate)
                return streamID != 0
            }
            return false
        }

        override fun isPlaying(): Boolean {
            if (streamID == 0)
                return false
            if (!super.isPlaying()){
                streamID = 0
                return false
            }
            return true
        }

        override fun stop() {
            if (usable && isPlaying())
            {
                soundPool.stop(streamID)
                streamID = 0
            }
        }
    }

    fun load(resId: Int, priority: Int, stackable: Boolean): LoadedSound? {
        val soundID = soundPool.load(context, resId, priority)
        if (soundID != 0)
        {
            // Get duration using media player. one time thing.
            val soundMP = MediaPlayer.create(context, resId)
            val duration = soundMP.duration
            // Release media player
            soundMP.reset()
            soundMP.release()

            // Create sound object, add it to the array, and return it.
            val loadedSound: LoadedSound = if (stackable)
                StackableLoadedSound(soundID, duration)
            else
                UnstackableLoadedSound(soundID, duration)
            loadedSounds.add(loadedSound)
            return loadedSound
        }
        return null
    }

    fun release()
    {
        for (loadedSound in loadedSounds)
            loadedSound.unload()
        loadedSounds.clear()
        soundPool.release()
    }

//    fun getDuration(soundID: Int): Int? {
//        return getLoadedSound(soundID)?.durationMS
//    }

//    fun unload(sound: LoadedSound) {
//        soundPool.unload(sound.soundID)
//    }


//    fun play(soundID: Int, leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Boolean {
//        val loadedSound = getLoadedSound(soundID)
//        if (loadedSound != null)
//        {
//            return loadedSound.play(leftVolume, rightVolume, priority, loop, rate)
//        }
//        return false
//    }
//
//    fun stop(soundID: Int)
//    {
//        val sound = getLoadedSound(soundID)
//        sound!!.stop()
//    }

}