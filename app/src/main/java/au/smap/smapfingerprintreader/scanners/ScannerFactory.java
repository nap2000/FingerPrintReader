package au.smap.smapfingerprintreader.scanners;

import android.content.Context;

public class ScannerFactory {

    public static Scanner getScanner(String name, Context context) {
        if(name.equals("MFS500")) {
            return new MFS500Scanner(context);
        } else  if(name.equals("MFS100")) {
            return new MFS100Scanner(context);
        } else  if(name.equals("Demo")) {
            return new DemoScanner(context);
        } else {
            return new MFS500Scanner(context);
        }
    }
}
