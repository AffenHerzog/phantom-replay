package de.affenherzog.phantomreplay.playback;

public record PlaybackStats(int totalCount, int privateCount, int globalCount, int activeCount, int inactiveCount) {
}
