package au.smap.smapfingerprintreader.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {

    private MutableLiveData<byte[]> image;

    public MutableLiveData<byte[]> getImage() {
        if(image == null) {
            image = new MutableLiveData<byte[]> ();
        }
        return image;
    }
}
