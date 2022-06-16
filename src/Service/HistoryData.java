package Service;

public final class HistoryData {
    private final int[][] tab;
    private final int score;

    public HistoryData(int[][] tab, int score) {
        this.tab = tab;
        this.score = score;
    }

    public int[][] getTab() {
        return tab;
    }

    public int getScore() {
        return score;
    }
}
