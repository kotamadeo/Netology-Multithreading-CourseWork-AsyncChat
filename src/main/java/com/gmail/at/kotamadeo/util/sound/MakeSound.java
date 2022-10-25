package com.gmail.at.kotamadeo.util.sound;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MakeSound {

    private static boolean soundOptions;

    public static synchronized void playSound(final String url) {
        if (!soundOptions) {
            new Thread(() -> {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }).start();
        }
    }

    public static synchronized void off() {
        soundOptions = true;
    }

    public static synchronized void on() {
        soundOptions = false;
    }

    public static boolean isIncluded() {
        return soundOptions;
    }
}
