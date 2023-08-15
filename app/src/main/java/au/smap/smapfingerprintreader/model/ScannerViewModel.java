package au.smap.smapfingerprintreader.model;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {

    private MutableLiveData<String> scannerState = new MutableLiveData("disconnected");
    private MutableLiveData<Uri> image = new MutableLiveData<>();

    public MutableLiveData<String> getScannerState() {
        return scannerState;
    }
    public MutableLiveData<Uri> getImage() {
        return image;
    }

}
