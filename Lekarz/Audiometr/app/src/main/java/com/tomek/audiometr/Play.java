package com.tomek.audiometr;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by tokli on 31.01.2018.
 */

public class Play {

    private double frequency = 0.0;
    private double amplitude = 0.0;
    private int duration = 1;


    public Play(double frequency, double amplitude, int duration) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.duration = duration*44100;

        playSound();
    }

    // generowanie sygnału sinusoidalnego z wykorzystaniem klasy AudioTrack
    private void playSound() {
        // AudioTrack definition
        int mBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);

        // Sine wave
        double[] mSound = new double[duration];
        short[] mBuffer = new short[duration];


        for (int i = 0; i < mSound.length; i++) {
            mSound[i] = Math.sin((2*Math.PI * i/(44100/frequency)));
            mBuffer[i] = (short) (mSound[i]*Short.MAX_VALUE);
        }

        mAudioTrack.setStereoVolume((float) amplitude, (float) amplitude);

        mAudioTrack.play();

        mAudioTrack.write(mBuffer, 0, mSound.length);
        mAudioTrack.stop();
        mAudioTrack.release();

    }

}
