package de.walhalla.app2.fragment.chargen;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.dialog.ChangeSemesterDialog;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.ImageDownload;
import de.walhalla.app2.utils.Variables;

public class Fragment extends CustomFragment {
    private static final String TAG = "chargen.Fragment";
    private final ArrayList<Person> board;
    private TextView name;
    private TextView PoB;
    private TextView address;
    private TextView mobile;
    private TextView major;
    private ImageView picture;
    private LinearLayout linearLayout;

    public Fragment() {
        board = new ArrayList<>();
    }

    @NotNull
    private ListenerRegistration updateRegistration(@NotNull Semester chosenSemester) {
        if (registration.size() != 0) {
            for (ListenerRegistration listener : registration) {
                if (listener != null) {
                    listener.remove();
                }
            }
            registration.clear();
        }
        return Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(chosenSemester.getID()))
                .collection("Chargen")
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    try {
                        linearLayout.removeAllViews();
                    } catch (Exception ignored) {
                    }
                    if (error != null) {
                        Log.e(TAG, "start: listener registration failed", error);
                    }
                    if (value != null && !value.isEmpty()) {
                        for (DocumentSnapshot snapshot : value) {
                            Person p = snapshot.toObject(Person.class);
                            board.add(p);
                        }
                        formatData();
                    }
                });
    }

    @Override
    public void start() {
        registration.add(updateRegistration(App.getChosenSemester()));
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {

    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {
        LinearLayout subtitle = toolbar.findViewById(R.id.custom_title);
        subtitle.setVisibility(View.VISIBLE);
        TextView title = subtitle.findViewById(R.id.action_bar_title);
        title.setText(String.format("%s %s", getString(R.string.charge_aktive), App.getChosenSemester().getShort()));
        toolbar.setOnClickListener(v -> {
            ChangeSemesterDialog changeSem = new ChangeSemesterDialog(this);
            changeSem.show(getParentFragmentManager(), TAG);
        });
        subtitle.setOnClickListener(v -> {
            ChangeSemesterDialog changeSem = new ChangeSemesterDialog(this);
            changeSem.show(getParentFragmentManager(), TAG);
        });
    }

    @Override
    public void authChange() {
        board.clear();
        toolbarContent();
        registration.add(updateRegistration(App.getChosenSemester()));
    }

    @Override
    public void displayChange() {
        board.clear();
        toolbarContent();
        registration.add(updateRegistration(App.getChosenSemester()));
    }

    @Override
    public void stop() {

    }

    /**
     * Format the downloaded data and add them to the ui
     */
    private void formatData() {
        try {
            linearLayout = requireView().findViewById(R.id.fragment_container);
            ScrollView sc = new ScrollView(getContext());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < board.size(); i++) {
                Person p = board.get(i);
                layout.addView(onePerson(p, i));
            }
            sc.addView(layout);
            linearLayout.addView(sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param person   current person to display
     * @param position position inside the array
     * @return the pre-filled view for the ui
     */
    @NotNull
    @SuppressLint("InflateParams")
    private View onePerson(@NotNull Person person, int position) {
        View view = getLayoutInflater().inflate(R.layout.item_person, null);

        name = view.findViewById(R.id.item_charge_name);
        major = view.findViewById(R.id.item_charge_major);
        PoB = view.findViewById(R.id.item_charge_birth_place);
        address = view.findViewById(R.id.item_charge_address);
        mobile = view.findViewById(R.id.item_charge_mobile);
        TextView mail = view.findViewById(R.id.item_charge_mail);
        picture = view.findViewById(R.id.item_charge_image);
        TextView title = view.findViewById(R.id.item_charge_title);

        if (!person.getFirst_Name().equals("")) {
            switch (position) {
                case 0:
                    title.setText(R.string.charge_x);
                    mail.setText(Variables.Walhalla.MAIL_SENIOR);
                    fillPerson(person);
                    break;
                case 1:
                    title.setText(R.string.charge_vx);
                    mail.setText(Variables.Walhalla.MAIL_CONSENIOR);
                    fillPerson(person);
                    break;
                case 2:
                    title.setText(R.string.charge_fm);
                    mail.setText(Variables.Walhalla.MAIL_FUXMAJOR);
                    fillPerson(person);
                    break;
                case 3:
                    title.setText(R.string.charge_xx);
                    mail.setText(Variables.Walhalla.MAIL_SCHRIFTFUEHRER);
                    fillPerson(person);
                    break;
                case 4:
                    title.setText(R.string.charge_xxx);
                    mail.setText(Variables.Walhalla.MAIL_KASSIER);
                    fillPerson(person);
                    break;
                default:
                    title.setText(R.string.error_download);
                    break;
            }
        } else {
            //Set everything Visibility.GONE
            title.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            major.setVisibility(View.GONE);
            PoB.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
            mobile.setVisibility(View.GONE);
            mail.setVisibility(View.GONE);
            picture.setVisibility(View.GONE);
        }

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        try {
            view.startAnimation(anim);
        } catch (Exception e) {
            Log.d(TAG, "An error occurred while animating an entry", e);
        }

        return view;
    }

    /**
     * Fill the pre-filled view with data and download
     * the image, if the person has one
     *
     * @param person data to fill
     */
    private void fillPerson(Person person) {
        try {
            name.setText(person.getFullName());
            String majorStr = "stud. " + person.getMajor();
            major.setText(majorStr);
            String PoBStr = requireContext().getString(R.string.charge_from) + " " + person.getPoB();
            PoB.setText(PoBStr);
            String addressStr;
            addressStr = person.getAddress().get(Person.ADDRESS_STREET).toString() + " " +
                    person.getAddress().get(Person.ADDRESS_NUMBER).toString() + "\n" +
                    person.getAddress().get(Person.ADDRESS_ZIP_CODE).toString() + " " +
                    person.getAddress().get(Person.ADDRESS_CITY).toString();
            address.setText(addressStr);
            mobile.setText(person.getMobile());

            //Download the profile picture if the person has one
            if (person.getPicture_path() != null) {
                final ImageView pictureFinal = picture;
                new ImageDownload(pictureFinal::setImageBitmap, person.getPicture_path()).execute();
            }
        } catch (Exception e) {
            Log.d(TAG, "No person filled that position", e);
        }
    }
}