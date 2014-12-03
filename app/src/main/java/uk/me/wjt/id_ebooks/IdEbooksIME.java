package uk.me.wjt.id_ebooks;

import android.app.Service;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.view.View;

public class IdEbooksIME extends InputMethodService {
    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.ebooks_ime, null);

        return view;
    }
}
