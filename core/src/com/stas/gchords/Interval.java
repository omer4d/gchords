package com.stas.gchords;

public enum Interval {
    Min2nd(1),
    Maj2nd(2),
    Min3rd(3),
    Maj3rd(4),
    Fourth(5),
    Dim5th(6),
    Fifth(7),
    Min6th(8),
    Maj6th(9),
    Min7th(10),
    Maj7th(11),
    Octave(12);

    public final int semitones;

    Interval(int semitones) {
        this.semitones = semitones;
    }
}
