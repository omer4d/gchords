package com.stas.gchords;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InstrumentSample {
    public Sound sound;
    public double pitchScale;

    public InstrumentSample(Sound sound, double pitchScale) {
        this.sound = sound;
        this.pitchScale = pitchScale;
    }

    public void play() {
        this.sound.play(1, (float)pitchScale, 0);
    }

    public void play(double vel) {
        this.sound.play((float)vel, (float)pitchScale, 0);
    }

    private static int getNoteIndexByName(String name) {
        int[] n = {0, 2, 4, 5, 7, 9, 11};

        Pattern patt = Pattern.compile(".*-([a-g])(#?)([0-9])");
        Matcher matcher = patt.matcher(name);
        matcher.find();

        return n[Util.mod(matcher.group(1).charAt(0) - 99, n.length)] +
                (matcher.group(2).isEmpty() ? 0 : 1) +
                Integer.valueOf(matcher.group(3)) * 12 + 12;
    }

    private static double getNoteFreqByIndex(int idx) {
        return 440 * Math.pow(Math.pow(2, 1.0/12.0), (idx - 69));
    }

    private static void fillDown(InstrumentSample[] instrumentSamples, Sound maxIdxSound, int minIdx, int maxIdx) {
        for(int i = minIdx; i <= maxIdx; ++i) {
            instrumentSamples[i] = new InstrumentSample(maxIdxSound, getNoteFreqByIndex(i) / getNoteFreqByIndex(maxIdx));
        }
    }

    public static InstrumentSample[] load(String dirPath) {
        InstrumentSample[] instrumentSamples = new InstrumentSample[128];

        FileHandle[] sampleFiles = Gdx.files.internal(dirPath).list(".wav");
        Arrays.sort(sampleFiles, new Comparator<FileHandle>() {
            @Override
            public int compare(FileHandle a, FileHandle b) {
                return getNoteIndexByName(a.nameWithoutExtension()) - getNoteIndexByName(b.nameWithoutExtension());
            }
        });

        int lastMin = 0;
        for(int i = 0; i < sampleFiles.length; ++i) {
            int nidx = getNoteIndexByName(sampleFiles[i].nameWithoutExtension());
            Sound nsound = Gdx.audio.newSound(sampleFiles[i]);
            fillDown(instrumentSamples, nsound, lastMin, nidx);
            lastMin = nidx + 1;
        }

        return instrumentSamples;
    }

}
