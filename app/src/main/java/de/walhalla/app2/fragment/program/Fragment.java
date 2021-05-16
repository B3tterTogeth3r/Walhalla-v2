package de.walhalla.app2.fragment.program;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.dialog.ChangeSemesterDialog;
import de.walhalla.app2.fragment.program.ui.Show;
import de.walhalla.app2.model.Event;

public class Fragment extends CustomFragment {
    private static final String TAG = "program.Fragment";
    protected static Fragment f;
    protected static Event event, toUpload;

    public Fragment() {
        event = new Event();
        toUpload = new Event();
    }

    @Override
    public void start() {
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        Fragment.f = this;
        TableLayout content = new TableLayout(getContext());
        content.setStretchAllColumns(true);

        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);

        linearLayout.addView(content);
        content.removeAllViewsInLayout();

        new Show();
        ScrollView sc = new ScrollView(getContext());
        sc.addView(Show.load(false, false));
        content.addView(sc);
    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {
        try {
            //Set the subtitle and make it clickable to change the displayed semester
            LinearLayout subtitle = toolbar.findViewById(R.id.custom_title);
            subtitle.setVisibility(View.VISIBLE);
            //subtitle.setOnClickListener(v -> Log.d(TAG, "toolbar got clicked"));
            TextView title = subtitle.findViewById(R.id.action_bar_title);
            title.setText(String.format("%s %s %s", getString(R.string.program), getString(R.string.of), App.getChosenSemester().getShort()));
            toolbar.setOnClickListener(v -> {
                ChangeSemesterDialog changeSem = new ChangeSemesterDialog(this);
                changeSem.show(getParentFragmentManager(), TAG);
            });
            subtitle.setOnClickListener(v -> {
                ChangeSemesterDialog changeSem = new ChangeSemesterDialog(this);
                changeSem.show(getParentFragmentManager(), TAG);
            });
            toolbar.getMenu().clear();

            /*if (User.isLogIn()) {
                toolbar.inflateMenu(R.menu.program_filter);
                if (!User.hasCharge()) {
                    toolbar.getMenu().removeItem(R.id.action_add);
                    toolbar.getMenu().removeItem(R.id.action_draft);
                }
            }

            toolbar.setOnMenuItemClickListener(item -> {
                try {
                    if (item.getItemId() == R.id.action_add) {
                        Log.i(TAG, "add a new Event.");
                        Edit.display(f.getParentFragmentManager(), null, null);
                    } else if (item.getItemId() == R.id.action_private) {
                        Show.setBooleans(true, false);
                    } else if (item.getItemId() == R.id.action_draft) {
                        Show.setBooleans(false, true);
                    } else if (item.getItemId() == R.id.action_public) {
                        Show.setBooleans(false, false);
                    }
                    return true;
                } catch (NullPointerException e) {
                    Log.d(TAG, "add a new Event.", e);
                    return false;
                }
            });*/
        } catch (Exception e) {
            Log.d(TAG, "Setting the toolbar was unsuccessful", e);
        }
    }

    @Override
    public void authChange() {
        toolbarContent();
        Show.listener.eventChanged();
    }

    @Override
    public void displayChange() {
        toolbarContent();
        Show.listener.eventChanged();
    }

    @Override
    public void stop() {

    }
}