package au.smap.smapfingerprintreader.scanners;

public abstract class Scanner {

    public abstract void connect();
    public abstract void startCapture();
    public abstract void destroy();
    public abstract void isConnected();
}
