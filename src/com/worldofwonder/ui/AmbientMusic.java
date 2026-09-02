package com.worldofwonder.ui;

import javax.sound.sampled.*;
import javax.swing.Timer;

/**
 * Procedural ambient music generator that creates a gentle, looping soundtrack.
 * Uses sine-wave synthesis (same approach as SoundUtil) to create calming ambient pads.
 */
public class AmbientMusic {

    private static boolean playing = false;
    private static boolean enabled = true;
    private static Clip currentClip;
    private static Timer loopTimer;

    // Musical note frequencies (C major pentatonic scale, octave 3-4)
    private static final double[] NOTES = {
            130.81, // C3
            146.83, // D3
            164.81, // E3
            196.00, // G3
            220.00, // A3
            261.63, // C4
            293.66, // D4
            329.63, // E4
    };

    public static synchronized void start() {
        if (playing || !enabled) return;
        playing = true;
        playAmbientLoop();
    }

    public static synchronized void stop() {
        playing = false;
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        if (loopTimer != null) {
            loopTimer.stop();
            loopTimer = null;
        }
    }

    public static void setEnabled(boolean flag) {
        enabled = flag;
        if (!flag) stop();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isPlaying() {
        return playing;
    }

    private static void playAmbientLoop() {
        if (!playing || !enabled) return;
        try {
            byte[] audio = generateAmbientChord();
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            if (!AudioSystem.isLineSupported(info)) return;

            currentClip = (Clip) AudioSystem.getLine(info);
            currentClip.open(format, audio, 0, audio.length);

            // Lower volume
            FloatControl volume = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-18f); // Quiet background

            currentClip.start();

            // Schedule next chord when this one ends
            int durationMs = (int) ((audio.length / 2.0 / 44100.0) * 1000);
            loopTimer = new Timer(durationMs, e -> {
                if (playing && enabled) {
                    playAmbientLoop();
                }
            });
            loopTimer.setRepeats(false);
            loopTimer.start();

        } catch (Exception e) {
            // Audio not available — silently ignore
        }
    }

    private static byte[] generateAmbientChord() {
        float sampleRate = 44100;
        float duration = 4.0f + (float) (Math.random() * 2.0); // 4-6 seconds
        int samples = (int) (sampleRate * duration);
        byte[] data = new byte[samples * 2]; // 16-bit

        // Pick 3 random notes for a gentle chord
        int n1 = (int) (Math.random() * NOTES.length);
        int n2 = (n1 + 2) % NOTES.length;
        int n3 = (n1 + 4) % NOTES.length;

        double freq1 = NOTES[n1];
        double freq2 = NOTES[n2];
        double freq3 = NOTES[n3];

        for (int i = 0; i < samples; i++) {
            double t = i / (double) sampleRate;

            // Envelope: fade in 1s, sustain, fade out 1.5s
            double env;
            double fadeIn = 1.0, fadeOut = 1.5;
            if (t < fadeIn) {
                env = t / fadeIn;
            } else if (t > duration - fadeOut) {
                env = (duration - t) / fadeOut;
            } else {
                env = 1.0;
            }
            env = Math.max(0, Math.min(1, env));

            // Mix 3 sine waves with slight detuning for richness
            double val = 0;
            val += Math.sin(2 * Math.PI * freq1 * t) * 0.3;
            val += Math.sin(2 * Math.PI * freq2 * t * 1.002) * 0.25; // Slight detune
            val += Math.sin(2 * Math.PI * freq3 * t * 0.998) * 0.2;

            // Add very subtle sub-bass
            val += Math.sin(2 * Math.PI * freq1 * 0.5 * t) * 0.15;

            val *= env * 0.5; // Master volume

            short sample = (short) (val * Short.MAX_VALUE);
            data[i * 2] = (byte) (sample & 0xFF);
            data[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return data;
    }
}
