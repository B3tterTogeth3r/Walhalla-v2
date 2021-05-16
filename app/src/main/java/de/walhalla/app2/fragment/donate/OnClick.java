package de.walhalla.app2.fragment.donate;

import android.content.ClipData;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import de.walhalla.app2.R;

public class OnClick extends Fragment implements View.OnClickListener {

    public OnClick() {
    }

    @Override
    public void onClick(View v) {
        if (v == hbv) {
            String text = hbv.getText().toString();
            ClipData clipData = ClipData.newPlainText("konto", text);
            clipboardManager.setPrimaryClip(clipData);
            //Log.i("Copy", text + " was put into Clipboard.");

            Snackbar.make(v, R.string.footer_copy_hbvkonto, Snackbar.LENGTH_LONG).show();
        } else if (v == aktive) {
            String text = aktive.getText().toString();
            ClipData clipData = ClipData.newPlainText("konto", text);
            clipboardManager.setPrimaryClip(clipData);
            //Log.i("Copy", text + " was put into Clipboard.");

            Snackbar.make(v, R.string.footer_copy_aktivenkonto, Snackbar.LENGTH_LONG).show();
        } else if (v == kstv) {
            String text = kstv.getText().toString();
            ClipData clipData = ClipData.newPlainText("konto", text);
            clipboardManager.setPrimaryClip(clipData);
            //Log.i("Copy", text + " was put into Clipboard.");

            Snackbar.make(v, R.string.footer_copy_kstvkonto, Snackbar.LENGTH_LONG).show();
        } else if (v == hbv_donate) {
            String text = hbv_donate.getText().toString();
            ClipData clipData = ClipData.newPlainText("konto", text);
            clipboardManager.setPrimaryClip(clipData);
            //Log.i("Copy", text + " was put into Clipboard.");

            Snackbar.make(v, R.string.footer_copy_hbvdonatekonto, Snackbar.LENGTH_LONG).show();
        }
    }
}
