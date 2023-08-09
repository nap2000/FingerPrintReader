package au.smap.smapfingerprintreader.model;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {

    private MutableLiveData<Uri> image;

    public MutableLiveData<Uri> getImage() {
        if(image == null) {
            image = new MutableLiveData<>();
        }
        return image;
    }
}
