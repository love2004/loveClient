package me.love2004.porn.util;

public class Timer {
    private long time = -1L;

    public boolean passedS(double s) {
        return this.passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return this.passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return this.passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }

    public static class Timeridk{
        private long current;

        public Timeridk() {
            this.current = System.currentTimeMillis();
        }

        public boolean hasReached(final long delay) {
            return System.currentTimeMillis() - this.current >= delay;
        }

        public boolean hasReached(final long delay, final boolean reset) {
            if (reset) {
                this.reset();
            }
            return System.currentTimeMillis() - this.current >= delay;
        }

        public void reset() {
            this.current = System.currentTimeMillis();
        }

        public long getTimePassed() {
            return System.currentTimeMillis() - this.current;
        }

        public boolean sleep(final long time) {
            if (this.time() >= time) {
                this.reset();
                return true;
            }
            return false;
        }

        public long time() {
            return System.currentTimeMillis() - this.current;
        }
    }
}

