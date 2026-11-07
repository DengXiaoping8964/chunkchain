package dev.fish.chunkchain;

public final class ChainState {
    public static final int[] SIZES = {3, 5, 7, 9, 11};

    public boolean enabled = false;
    public int size = 3;

    public int cycleSize() {
        for (int i = 0; i < SIZES.length; i++) {
            if (SIZES[i] == size) {
                size = SIZES[(i + 1) % SIZES.length];
                return size;
            }
        }
        size = SIZES[0];
        return size;
    }

    public static boolean isValidSize(int n) {
        for (int s : SIZES) {
            if (s == n) return true;
        }
        return false;
    }
}