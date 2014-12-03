package uk.me.wjt.id_ebooks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.inputmethodservice.InputMethodService;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IdEbooksIME extends InputMethodService implements Loader.OnLoadCompleteListener<Cursor> {
    private static final String TAG = IdEbooksIME.class.getPackage().getName();

    private static final int LOADER_ID_SMS_SENT = 0;
    private static final Random RANDOM = new Random();

    private CursorLoader smsLoader;
    private List<String> words = Collections.emptyList();

    private Button go;

    @Override
    public void onCreate() {
        super.onCreate();

        smsLoader = new CursorLoader(this, Telephony.Sms.Sent.CONTENT_URI, null, null, null, null);
        smsLoader.registerListener(LOADER_ID_SMS_SENT, this);
        smsLoader.startLoading();
    }

    @Override
    public void onDestroy() {
        if (smsLoader != null) {
            smsLoader.unregisterListener(this);
            smsLoader.cancelLoad();
            smsLoader.stopLoading();

            smsLoader = null;
        }

        super.onDestroy();
    }

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.ebooks_ime, null);

        go = (Button) view.findViewById(R.id.ime_go);

        go.setEnabled(!words.isEmpty());
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        return view;
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "onLoadFinished");
        int n = data.getCount();

        StringBuilder sb = new StringBuilder();
        int bodyIx = data.getColumnIndexOrThrow(Telephony.Sms.Sent.BODY);
        if (data.moveToFirst()) {
            for (int i = 0; i < n; i++) {
                sb.append(data.getString(bodyIx)).append(" ");
                data.moveToNext();
            }
        }
        Log.i(TAG, String.format("%d chars from %d messages", sb.length(), n));
        words = Arrays.asList(sb.toString().split("\\s+"));
        if (go != null) {
            go.setEnabled(true);
        }
    }

    private void go() {
        final int minLength = 5;
        final int extra = 7;
        final int i = RANDOM.nextInt(words.size() - (minLength + extra));
        final int j = i + minLength + RANDOM.nextInt(extra);

        String tweet = TextUtils.join(" ", words.subList(i, j));

        Log.i(TAG, String.format("[%d, %d) %s", i, j, tweet));

        InputConnection ic = getCurrentInputConnection();
        ic.setComposingText(tweet, 1);
    }
}
