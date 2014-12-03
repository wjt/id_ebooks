package uk.me.wjt.id_ebooks;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class TestActivity extends Activity {
    private static final String TAG = TestActivity.class.getPackage().getName();
    private static final int SENT_SMS_LOADER = 0;
    private static final Random RANDOM = new Random();

    private List<String> words = null;
    private Button button, clear;
    private EditText textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        clear = (Button) findViewById(R.id.clearButton);

        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
            }
        });

        getLoaderManager().initLoader(SENT_SMS_LOADER, null, sentSmsLoaderCallbacks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> sentSmsLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(TestActivity.this, Telephony.Sms.Sent.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
            button.setEnabled(true);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.w(TAG, "Reset, boo");
        }
    };

    private void go() {
        assert (words != null);

        final int i = RANDOM.nextInt(words.size());
        final int j = i + 5 + RANDOM.nextInt(7);

        List<String> tokens = words.subList(i, j);
        Log.i(TAG, tokens.toString());
        String tweet = TextUtils.join(" ", tokens);
        textView.append(tweet);
        textView.append(" ");
    }
}
