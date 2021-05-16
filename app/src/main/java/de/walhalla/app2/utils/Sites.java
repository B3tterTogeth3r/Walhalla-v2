package de.walhalla.app2.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;

@SuppressWarnings("unchecked")
public class Sites {
    private static final String TAG = "Sites";

    public static void create(Context context, LinearLayout parentLayout, @NotNull Map<String, Object> content) {
        Log.d(TAG, "create: I do stuff now");
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_custom_linear_layout, null);
            layout.removeAllViewsInLayout();
            layout.setOrientation(LinearLayout.VERTICAL);

            int size = content.size();
            for (int i = 0; i < size; i++) {
                try {
                    //Saved text is only a String
                    String text = (String) (content.get(String.valueOf(i)));
                    TextView line = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
                    line.setText(text);
                    layout.addView(line);
                } catch (Exception e) {
                    try {
                        //Saved text is a document reference to a list of pictures
                        final DocumentReference ref = (DocumentReference) (content.get(String.valueOf(i)));
                        Log.d(TAG, ref.getPath());
                        //Import diashow with the images etc
                        RelativeLayout diashow = (RelativeLayout) inflater.inflate(R.layout.image_slider, null);
                        ref.get().addOnSuccessListener(documentSnapshot -> {
                            ArrayList<Object> list = (ArrayList<Object>) documentSnapshot.get("picture_names");
                            if (list != null && list.size() != 0) {
                                Slider.load(diashow, list);
                            }
                        });
                        layout.addView(diashow);
                    } catch (Exception er) {
                        //Saved value is a map for further design instructions
                        Map<String, Object> different = (Map<String, Object>) content.get(String.valueOf(i));
                        try {
                            String text = (String) different.get("title");
                            Log.d(TAG, text);
                            TextView line = (TextView) inflater.inflate(R.layout.layout_custom_title, null);
                            line.setText(text);
                            layout.addView(line);
                        } catch (Exception ignored) {
                            try {
                                String text = (String) different.get("subtitle1");
                                Log.d(TAG, text);
                                TextView line = (TextView) inflater.inflate(R.layout.layout_custom_subtitle, null);
                                line.setText(text);
                                layout.addView(line);
                            } catch (Exception ignored2) {
                                try {
                                    String text = (String) different.get("subtitle2");
                                    Log.d(TAG, text);
                                    TextView line = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);
                                    line.setText(text);
                                    layout.addView(line);
                                } catch (Exception ignored3) {
                                    try {
                                        String text = (String) different.get("button");
                                        Log.d(TAG, text);
                                        Button line = (Button) inflater.inflate(R.layout.layout_custom_button, null);
                                        line.setText(text);
                                        line.setOnClickListener(v -> {
                                            //Open browser with the set link
                                            try {
                                                String url = (String) different.get("link");
                                                MainActivity.listener.browser(url);
                                            } catch (Exception ignored1) {
                                                //No link, so Email to
                                                MainActivity.listener.email();
                                            }
                                        });
                                        layout.addView(line);
                                        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(line.getLayoutParams());
                                        buttonParams.setMargins(0, 8, 0, 8);
                                        line.setLayoutParams(buttonParams);
                                    } catch (Exception ignored4) {
                                        try {
                                            HorizontalScrollView sc = (HorizontalScrollView) inflater.inflate(R.layout.layout_custom_table, null);
                                            TableLayout line = sc.findViewById(R.id.table);
                                            line.removeAllViewsInLayout();
                                            Map<String, Object> table = (Map<String, Object>) different.get("table");
                                            //Add TableRows, starting with the row title, than sorted by number
                                            TableRow titleRow = new TableRow(context);
                                            titleRow.removeAllViews();
                                            ArrayList<String> row = (ArrayList<String>) table.get("title");
                                            int rowSize = row.size();
                                            for (int j = 0; j < rowSize; j++) {
                                                TextView textView = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);
                                                int padding = (int) Variables.SCALE * 2;
                                                textView.setPadding(0, padding, 20, (padding * 2));
                                                textView.setText(row.get(j));
                                                titleRow.addView(textView);
                                            }
                                            line.addView(titleRow);
                                            table.remove("title");
                                            List<String> sortedKeysStr = new ArrayList<>(table.keySet());
                                            List<Integer> sortedKeys = new ArrayList<>();
                                            for (int l = 0; l < sortedKeysStr.size(); l++) {
                                                sortedKeys.add(Integer.parseInt(sortedKeysStr.get(l)));
                                            }
                                            Collections.sort(sortedKeys);
                                            int sizeTable = sortedKeys.size();
                                            for (int j = 0; j < sizeTable; j++) {//Map.Entry<String, Object> oneRow : table.entrySet()) {
                                                //Map<String, Object> oneRow = (Map<String, Object>);
                                                TableRow contentRow = (TableRow) inflater.inflate(R.layout.layout_custom_table_row, null);
                                                contentRow.removeAllViews();
                                                row = (ArrayList<String>) table.get(sortedKeys.get(j).toString());
                                                rowSize = row.size();
                                                for (int k = 0; k < rowSize; k++) {
                                                    TextView textView = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
                                                    int padding = (int) Variables.SCALE * 2;
                                                    textView.setPadding(0, padding, 20, (padding * 2));
                                                    textView.setText(row.get(k));
                                                    contentRow.addView(textView);
                                                }
                                                line.addView(contentRow);
                                            }
                                            layout.addView(sc);
                                        } catch (@SuppressWarnings("CatchMayIgnoreException") Exception ignored5) {
                                            Log.d(TAG, "While formatting the table an error occurred.", ignored5);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            parentLayout.addView(layout);
        } catch (Exception e) {
            Log.d(TAG, "Site isn't active anymore so there is a NullPointerException at some reference.");
        }
    }
}
