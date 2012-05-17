package cn.keke.qqtetris;

public class StopWatch {
    private long start;
    private String name;

    public StopWatch(String name) {
        this.name = name;
    }

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public int time() {
        return (int) (System.currentTimeMillis() - this.start);
    }

    public long printTime(String taskName) {
        final long duration = System.currentTimeMillis() - this.start;
        System.out.println(this.name + " (" + formatMillis(duration) + "): " + taskName);
        return duration;
    }

    private String formatMillis(long l) {
        if (l < 1000) {
            return String.valueOf(l);
        } else if (l < 1000 * 60) {
            return (l / 1000) + "." + (l % 1000);
        } else if (l < 1000 * 60 * 60) {
            return (l / 1000 / 60) + ":" + (l / 1000 % 60) + "." + (l % 1000);
        } else if (l < 1000 * 60 * 60 * 24) {
            return (l / 1000 / 60 / 60) + ":" + (l / 1000 / 60 % 60) + ":" + (l / 1000 % 60) + "." + (l % 1000);
        } else {
            return (l / 1000 / 60 / 60 / 24) + ":" + (l / 1000 / 60 / 60 % 24) + ":" + (l / 1000 / 60 % 60) + ":" + (l / 1000 % 60) + "." + (l % 1000);
        }
    }

    public long measure() {
        return System.currentTimeMillis() - this.start;
    }
}
