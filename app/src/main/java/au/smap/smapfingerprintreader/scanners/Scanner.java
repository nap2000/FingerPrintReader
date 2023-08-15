package au.smap.smapfingerprintreader.scanners;

public abstract class Scanner {

    public abstract void connect();
    public abstract void startCapture(int minQuality, int timeOut);
    public abstract void destroy();
}
