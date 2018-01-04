class Timer {


    float endtime, duration;
    boolean started = false;



    Timer() { }
    Timer(float d) { set(d); }


    void start() {
        started = true;
        endtime = millis() + duration;
    }

    boolean started() {
        return started;
    }

    boolean startedAndFinished() {
        return (started() && finished());
    }

    void set(float d) {
        duration = int(d*1000);
    }

    void setAndStart(float d) {
        set(d);
        start();
    }

    boolean finished() {
        boolean r = false;
        if (endtime <= millis()) { r = true; }
        return r;
    }

    float progress() {
        return map(millis(), endtime-duration, endtime, 0, 1);
    }

}
