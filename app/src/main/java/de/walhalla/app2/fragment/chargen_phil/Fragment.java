package de.walhalla.app2.fragment.chargen_phil;

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
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.ImageDownload;

import static de.walhalla.app2.utils.Variables.Walhalla.MAIL_CONSENIOR_PHIL;
import static de.walhalla.app2.utils.Variables.Walhalla.MAIL_FUXMAJOR_PHIL;
import static de.walhalla.app2.utils.Variables.Walhalla.MAIL_SENIOR_PHIL;
import static de.walhalla.app2.utils.Variables.Walhalla.MAIL_WH_PHIL;

public class Fragment extends CustomFragment {
    private static final String TAG = "chargen_phil.Fragment";
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
        toolbar.setTitle(R.string.menu_chargen_phil);
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
                layout.addView(onePerson(p));
            }
            sc.addView(layout);
            linearLayout.addView(sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param person current person to display
     * @return the pre-filled view for the ui
     */
    @NotNull
    @SuppressLint("InflateParams")
    private View onePerson(@NotNull Person person) {
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
            switch (person.getId()) {
                case "x":
                    title.setText(R.string.charge_x_phil);
                    mail.setText(MAIL_SENIOR_PHIL);
                    fillPerson(person);
                    break;
                case "xx":
                    title.setText(R.string.charge_vx_phil);
                    mail.setText(MAIL_CONSENIOR_PHIL);
                    fillPerson(person);
                    break;
                case "xxx":
                    title.setText(R.string.charge_fm_phil);
                    mail.setText(MAIL_FUXMAJOR_PHIL);
                    fillPerson(person);
                    break;
                case "HW":
                case "hw":
                    title.setText(R.string.charge_phil_hw);
                    mail.setText(MAIL_WH_PHIL);
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
            String majorStr = person.getMajor();
            major.setText(majorStr);
            PoB.setVisibility(View.GONE);
            try {
                boolean test = !person.getMobile().isEmpty();
                mobile.setText(person.getMobile());
            } catch (Exception e) {
                mobile.setVisibility(View.GONE);
            }
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
                .collection("Chargen_Phil")
                .limit(5)
                .orderBy("id")
                .addSnapshotListener((value, error) -> {
                    try {
                        linearLayout.removeAllViews();
                    } catch (Exception ignored) {
                    }
                    if (error != null) {
                        Log.e(TAG, "start: listener registration failed", error);
                    }
                    if (value != null && !value.isEmpty()) {
                        board.clear();
                        for (DocumentSnapshot snapshot : value) {
                            try {
                                Person p = snapshot.toObject(Person.class);
                                assert p != null;
                                p.setId(snapshot.getId());
                                board.add(p);
                            } catch (Exception ignored) {
                            }
                        }
                        formatData();
                    }
                });
    }
}