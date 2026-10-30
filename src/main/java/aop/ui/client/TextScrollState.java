package aop.ui.client;

/** Frame-rate independent ping-pong motion in pixels, with a readable pause at each end. */
final class TextScrollState {
    private long previous = Long.MIN_VALUE;
    private double offset, limit, pause = 1;
    private int direction = 1;
    private boolean accelerated;

    double advance(long now, double overflow, boolean fast) {
        overflow = Math.max(0, overflow);
        if (overflow != limit) { reset(); limit = overflow; }
        if (previous == Long.MIN_VALUE) { previous = now; accelerated = fast; pause = fast ? .3 : 1; return offset; }
        double elapsed = Math.clamp((now - previous) / 1000d, 0, .25);
        previous = now;
        if (limit == 0) return 0;
        if (fast && !accelerated) pause = Math.min(pause, .3);
        accelerated = fast;
        double speed = fast ? 42 : 12;
        while (elapsed > 0) {
            if (pause > 0) { double used = Math.min(pause, elapsed); pause -= used; elapsed -= used; continue; }
            double distance = direction > 0 ? limit - offset : offset;
            double duration = distance / speed;
            if (elapsed < duration) { offset += direction * speed * elapsed; break; }
            offset = direction > 0 ? limit : 0;
            elapsed -= duration;
            direction = -direction;
            pause = fast ? .35 : 1;
        }
        return offset;
    }

    void reset() { previous = Long.MIN_VALUE; offset = 0; direction = 1; pause = 1; accelerated = false; }
}
