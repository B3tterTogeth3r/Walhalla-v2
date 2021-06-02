package de.walhalla.app2.fragment.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import de.walhalla.app2.App;
import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.User;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.dialog.ChangeSemesterDialog;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.SemesterListener;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Rank;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.ImageDownload;
import de.walhalla.app2.utils.Variables;

import static android.app.Activity.RESULT_OK;

public class Fragment extends CustomFragment {
    private static final String TAG = "profile.Fragment";
    private final ArrayList<Rank> ranks = new ArrayList<>();
    AtomicBoolean imageGotClicked = new AtomicBoolean(false);
    private LayoutInflater inflater;
    private Person user;
    private CircleImageView imageView;
    private Bitmap imageBitmap;
    private TextView rank;
    private View view;
    private ProgressBar progressBar;
    private int stepNumber = -1;
    private float downloadProgress;
    private Uri image_uri;

    @Override
    public void start() {

    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        this.inflater = inflater;
        LinearLayout layout = view.findViewById(R.id.fragment_container);
        user = App.getUser().getData();
        layout.removeAllViewsInLayout();
        layout.addView(profileData());
    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_profile_edit);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.save_text);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                //Check that all fields are filled
                boolean errorExists = false;
                try {
                    user.getFullName();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getAddressString();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getDoBString();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getMobile();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getPoB();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getMajor();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getRank();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                try {
                    user.getJoined();
                } catch (Exception ignored) {
                    errorExists = true;
                }
                if (errorExists) {
                    Toast.makeText(requireContext(), R.string.error_fill_all_fields, Toast.LENGTH_LONG).show();
                } else {
                    //show progress bar, disable all other fields (greyisch color over the whole screen
                    // or something like that
                    progressBar.setVisibility(View.VISIBLE);
                    LinearLayout layout = view.findViewById(R.id.profile_layout);
                    layout.setBackgroundResource(R.color.darkGray);
                    layout.setAlpha(0.5f);
                    view.findViewById(R.id.profile_name_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_address_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_dob_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_mobile_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_pob_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_major_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_rank_layout).setOnClickListener(null);
                    view.findViewById(R.id.profile_joined_layout).setOnClickListener(null);
                    view.findViewById(R.id.login_profile_image_layout).setOnClickListener(null);
                    view.findViewById(R.id.login_register).setOnClickListener(null);
                    uploadData();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void authChange() {
        displayChange();
    }

    @Override
    public void displayChange() {
        //TODO Redirect to home
    }

    @Override
    public void stop() {
    }

    /**
     * Upload the data, starting with the image. Put the <tt>image_path</tt> in {@link #user}
     * and Auth.<tt>profilePicture</tt>. Than register the user and send a verification to users {@link Person#getMail() email}. Get
     * the uid and set it via {@link Person#setUid(String)}. <br>
     * After that upload the data to firestore.
     *
     * @see #profileData()
     * @since 1.8
     */
    private void uploadData() {
        stepNumber++;
        // [START Upload Image]
        if (stepNumber == 0) {
            if (imageGotClicked.get()) {
                String imagePath = (user.getFullName()).replace(" ", "_") + ".jpg";
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] data = baos.toByteArray();
                user.setPicture_path("pictures/" + imagePath);
                Firebase.IMAGES.child(imagePath).putBytes(data)
                        .addOnFailureListener(e -> {
                            //Upload failed
                            Firebase.CRASHLYTICS.log("ProfileFragment.onStop: upload of user data failed");
                            Firebase.CRASHLYTICS.recordException(e);
                            Log.e(TAG, "stop: upload failed", e);
                        }).addOnCompleteListener(task -> {
                    //Set uri to User and update Auth user
                    Firebase.IMAGES.child(imagePath).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                this.image_uri = uri;
                                uploadData();
                            });
                });
            } else {
                uploadData();
            }
        }
        // [END Upload image]

        // [START Update user data]
        if (stepNumber == 1) {
            Log.d(TAG, "uploadData: " + user.getPicture_path());
            Firebase.FIRESTORE.collection("Person")
                    .document(App.getUser().getId())
                    .update(user.toMap())
                    .addOnSuccessListener(unused -> {
                        uploadData();
                        Log.d(TAG, "uploadData: update complete");
                    });
        }
        // [END Upload user data]

        // [START Load the new user data into the app]
        if (stepNumber == 2) {
            Firebase.FIRESTORE.collection("Person")
                    .whereEqualTo("uid", Firebase.USER.getUid())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            try {
                                User userdata = new User();
                                Person p = d.toObject(Person.class);
                                userdata.setId(d.getId());
                                userdata.setUid(Firebase.USER.getUid());
                                userdata.setImage(Firebase.USER.getPhotoUrl());
                                userdata.setEmail(Firebase.USER.getEmail());
                                userdata.setData(p);
                                Firebase.ANALYTICS.setUserProperty("user_rank", p.getRank());
                                App.setUser(userdata);
                                Log.d(TAG, "uploadData: " + stepNumber);
                                uploadData();
                            } catch (Exception e) {
                                Log.e(TAG, "uploadUserData: loading the userdata did not work", e);
                                Firebase.CRASHLYTICS.log(TAG + "uploadUserData: loading the userdata did not work");
                                Firebase.CRASHLYTICS.recordException(e);
                            }
                        }
                    });
        }
        // [END Load the new user data into the app]
        try {
            updateProgressbar();
        } catch (Exception e) {
            Log.e(TAG, "uploadData: ", e);
        }
    }

    private void updateProgressbar() {
        downloadProgress += (100f / 4);
        progressBar.setProgress((int) downloadProgress);
        Log.d(TAG, String.valueOf(downloadProgress));
        if (downloadProgress == 100) {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(MainActivity.parentLayout, R.string.upload_complete, Snackbar.LENGTH_LONG).show();
            //Better reload fragment
            try {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app2.fragment.profile.Fragment()).commit();
            } catch (Exception e) {
                Log.e(TAG, "updateProgressbar: fragment change didn't work", e);
            }
        }
    }

    /**
     * This function displays all the values the user has to fill out
     * in order to complete registration. The same function can be used
     * to edit the profile later. Every row belongs to at least one value
     * of {@link Person Person}. This function combines most
     * of previous functions from version 1.3 till 1.7
     * <p>
     * The user has to fill the fields
     * <code>email, name, address, dob, mobile, pob, major, rank</code> and
     * <code>joined</code>. The user has to select a <code>profile image</code>,
     * too. If any except the image is <tt>null</tt>, the user can't proceed
     * with the registration.
     *
     * @return the view
     * @see Person
     * @since 1.1
     */
    @SuppressLint("InflateParams")
    @NotNull
    public View profileData() {
        view = inflater.inflate(R.layout.item_profile, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.findViewById(R.id.button_row).setVisibility(View.GONE);
        view.findViewById(R.id.profile_title_image).setVisibility(View.GONE);
        view.findViewById(R.id.profile_title_text).setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.profile_loader);
        TextView email = view.findViewById(R.id.profile_mail);
        TextView name = view.findViewById(R.id.profile_name);
        TextView address = view.findViewById(R.id.profile_address);
        TextView dob = view.findViewById(R.id.profile_dob);
        TextView mobile = view.findViewById(R.id.profile_mobile);
        TextView pob = view.findViewById(R.id.profile_pob);
        TextView major = view.findViewById(R.id.profile_major);
        rank = view.findViewById(R.id.profile_rank);
        TextView joined = view.findViewById(R.id.profile_joined);
        imageView = view.findViewById(R.id.login_profile_image);

        // [START Field filling]
        // Fill all fields if user is not null
        // and has a value for that object
        if (user != null) {
            try {
                name.setText(user.getFullName());
            } catch (Exception ignored) {
            }
            try {
                address.setText(user.getAddressString());
            } catch (Exception ignored) {
            }
            try {
                dob.setText(user.getDoBString());
            } catch (Exception ignored) {
            }
            try {
                mobile.setText(user.getMobile());
            } catch (Exception ignored) {
            }
            try {
                pob.setText(user.getPoB());
            } catch (Exception ignored) {
            }
            try {
                major.setText(user.getMajor());
            } catch (Exception ignored) {
            }
            try {
                rank.setText(user.getRank());
            } catch (Exception ignored) {
            }
            try {
                if (user.getJoined() != 0) {
                    joined.setText(Variables.SEMESTER_ARRAY_LIST.get(user.getJoined() - 1).getLong());
                }
            } catch (Exception ignored) {
            }
            try {
                if (!user.getPicture_path().equals("")) {
                    //Get image
                    final ImageView pictureFinal = imageView;
                    new ImageDownload(pictureFinal::setImageBitmap, user.getPicture_path()).execute();
                }
            } catch (Exception ignored) {
            }
        }
        // [END Field filling]

        // Email is not editable, so the arrow has to be disabled.
        (view.findViewById(R.id.profile_email_arrow)).setVisibility(View.GONE);
        email.setText(user.getMail());

        // [START first & last name]
        // If dialog is closed with the positive button, the input is saved and the TV updated.
        view.findViewById(R.id.profile_name_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View firstNameView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText firstName = firstNameView.findViewById(R.id.customEditText);
                    firstName.setHint(R.string.profile_first_name);
                    View lastNameView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText lastName = lastNameView.findViewById(R.id.customEditText);
                    lastName.setHint(R.string.profile_lastName);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(firstNameView);
                    layout.addView(lastNameView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        firstName.setText(user.getFirst_Name());
                        lastName.setText(user.getLast_Name());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setFirst_Name(firstName.getText().toString());
                                    user.setLast_Name(lastName.getText().toString());

                                    //write full name in the TV
                                    name.setText(user.getFullName());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: name too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END first & last name]

        // [START address]
        view.findViewById(R.id.profile_address_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog_address, null);
                    EditText city, number, street, zip;
                    city = dialogView.findViewById(R.id.profile_address_city);
                    number = dialogView.findViewById(R.id.profile_address_number);
                    street = dialogView.findViewById(R.id.profile_address_street);
                    zip = dialogView.findViewById(R.id.profile_address_zip);
                    try {
                        city.setText((String) user.getAddress().get(Person.ADDRESS_CITY));
                        number.setText((String) user.getAddress().get(Person.ADDRESS_NUMBER));
                        street.setText((String) user.getAddress().get(Person.ADDRESS_STREET));
                        zip.setText((String) user.getAddress().get(Person.ADDRESS_ZIP_CODE));
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                try {
                                    //Read user input and write it into the user object
                                    Map<String, Object> addressMap = new HashMap<>();
                                    addressMap.put(Person.ADDRESS_CITY, city.getText().toString());
                                    addressMap.put(Person.ADDRESS_NUMBER, number.getText().toString());
                                    addressMap.put(Person.ADDRESS_STREET, street.getText().toString());
                                    addressMap.put(Person.ADDRESS_ZIP_CODE, zip.getText().toString());
                                    user.setAddress(addressMap);

                                    //write address in the TV
                                    address.setText(user.getAddressString());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: address error", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END address]

        // [START Birthday]
        view.findViewById(R.id.profile_dob_layout)
                .setOnClickListener(v -> {
                    try {
                        new MainActivity().hideKeyboard(getView());
                    } catch (Exception ignored) {
                    }
                    Log.d(TAG, "profileData: select a birthday");
                    //open Date-picker
                    Date date;
                    try {
                        date = user.getDoB().toDate();
                    } catch (Exception e) {
                        date = new Date();
                    }
                    int[] DoB = new int[3];
                    SimpleDateFormat getYear = new SimpleDateFormat("yyyy", Variables.LOCALE);
                    SimpleDateFormat getMonth = new SimpleDateFormat("MM", Variables.LOCALE);
                    SimpleDateFormat getDay = new SimpleDateFormat("dd", Variables.LOCALE);
                    DoB[0] = Integer.parseInt(getYear.format(date));
                    DoB[1] = Integer.parseInt(getMonth.format(date)) - 1;
                    DoB[2] = Integer.parseInt(getDay.format(date));
                    try {
                        new DatePickerDialog(inflater.getContext(),
                                (view1, year, monthOfYear, dayOfMonth) -> {
                                    String month = String.valueOf(monthOfYear + 1);
                                    if (Integer.parseInt(month) < 10) {
                                        month = "0" + month;
                                    }
                                    String day = String.valueOf(dayOfMonth);
                                    if (Integer.parseInt(day) < 10) {
                                        day = "0" + day;
                                    }
                                    String result = day + "." + month + "." + year;
                                    dob.setText(result);
                                    result = year + "-" + month + "-" + day;
                                    Log.i(TAG, result);
                                    Calendar c = Calendar.getInstance();
                                    c.set(year, monthOfYear, dayOfMonth);
                                    user.setDoB(new Timestamp(c.getTime()));
                                }, DoB[0], DoB[1], DoB[2]).show();
                    } catch (Exception e) {
                        Log.e(TAG, "profileData: ", e);
                    }
                });
        // [END Birthday]

        // [START phone number]
        view.findViewById(R.id.profile_mobile_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View mobileView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText mobileET = mobileView.findViewById(R.id.customEditText);
                    mobileET.setHint(R.string.profile_mobile);
                    mobileET.setInputType(InputType.TYPE_CLASS_PHONE);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(mobileView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        mobileET.setText(user.getMobile());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setMobile(mobileET.getText().toString());

                                    //write mobile number in the TV
                                    mobile.setText(user.getMobile());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: number too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END phone number]

        // [START origin / place of birth]
        view.findViewById(R.id.profile_pob_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View pobView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText pobET = pobView.findViewById(R.id.customEditText);
                    pobET.setHint(R.string.profile_pob);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(pobView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        pobET.setText(user.getPoB());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setPoB(pobET.getText().toString());

                                    //write mobile number in the TV
                                    pob.setText(user.getPoB());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: number too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END origin / place of birth]

        // [START major or occupation]
        view.findViewById(R.id.profile_major_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View majorView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText majorET = majorView.findViewById(R.id.customEditText);
                    majorET.setHint(R.string.profile_major);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(majorView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        majorET.setText(user.getMajor());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setMajor(majorET.getText().toString());

                                    //write mobile number in the TV
                                    major.setText(user.getMajor());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: number too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END major or occupation]

        // [START rank]
        view.findViewById(R.id.profile_rank_layout)
                .setOnClickListener(v ->
                        downloadRank()
                );
        // [END rank]

        // [START joined semester]
        //TODO select users current as start semester
        view.findViewById(R.id.profile_joined_layout)
                .setOnClickListener(v -> {
                    ChangeSemesterDialog dialog = new ChangeSemesterDialog(new SemesterListener() {
                        @Override
                        public void displayChangeDone() {

                        }

                        @Override
                        public void joinedSelectionDone(Semester chosenSemester) {
                            try {
                                Log.d(TAG, "joinedSelectionDone: chosenSemester.getLong() == " + chosenSemester.getLong());
                                user.setJoined(chosenSemester.getID());
                                joined.setText(chosenSemester.getLong());
                            } catch (Exception e) {
                                Log.e(TAG, "joinedSelectionDone: getJoined() did not work", e);
                            }
                        }
                    }, Person.JOINED, user.getJoined());
                    try {
                        dialog.show(getChildFragmentManager(),
                                TAG);
                    } catch (Exception e) {
                        Log.e(TAG, "setFratData: opening semester dialog did not work", e);
                    }
                });
        // [END joined semester]

        // [START profile picture]
        view.findViewById(R.id.login_profile_image_layout)
                .setOnClickListener(v -> {
                    try {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                    } catch (Exception e) {
                        Log.e(TAG, "setImage: choosing a photo did not work", e);
                    }
                    imageGotClicked.set(true);
                });
        // [END profile picture]

        return view;
    }

    /**
     * Display the selected image in the image view
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                try {
                    assert data != null;
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarContent();
    }

    /**
     * Download the ranks from the database. After that format them
     * into an ArrayList and display the dialog for the user to select one.
     */
    @SuppressWarnings("unchecked")
    private void downloadRank() {
        Firebase.FIRESTORE
                .collection("Kind")
                .document("rank")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> dataSet = documentSnapshot.getData();
                        if (dataSet != null) {
                            int size = dataSet.size();
                            for (int i = 0; i < size; i++) {
                                try {
                                    Map<String, Object> data = (Map<String, Object>) dataSet.get(String.valueOf(i));
                                    Rank r = new Rank();
                                    r.setId(i);
                                    r.setFirst_fraternity((boolean) data.get(Rank.FIRST_FRATERNITY));
                                    r.setFull_member((boolean) data.get(Rank.FULL_MEMBER));
                                    r.setIn_loco((boolean) data.get(Rank.IN_LOCO));
                                    r.setPrice_semester((Map<String, Object>) data.get(Rank.PRICE));
                                    r.setRank_name((String) data.get(Rank.NAME));
                                    ranks.add(r);
                                } catch (Exception e) {
                                    Log.d(TAG, "Something went wrong", e);
                                }
                            }

                            rankDialog(ranks)
                                    .show();
                        } else {
                            Log.d(TAG, "2 something went wrong while downloading the ranks");
                        }
                    } else {
                        Log.d(TAG, "1 something went wrong while downloading the ranks");
                    }
                });
    }

    /**
     * Display the ranks a user can choose from
     *
     * @return The finished Builder.
     */
    @NotNull
    private AlertDialog.Builder rankDialog(@NotNull ArrayList<Rank> ranks) {
        String[] options = new String[ranks.size()];
        for (int i = 0; i < ranks.size(); i++) {
            options[i] = ranks.get(i).getRank_name();
        }
        AlertDialog.Builder rankPicker = new AlertDialog.Builder(inflater.getContext());
        rankPicker.setTitle(R.string.profile_choose_rank)
                .setCancelable(false)
                .setNegativeButton(R.string.abort, (dialog, which) -> {
                    rank.setText(R.string.profile_choose_rank);
                    this.ranks.clear();
                    dialog.dismiss();
                })
                .setItems(options, (dialog, which) -> {
                    rank.setText(options[which]);
                    Log.i(TAG, which + " is the selected value");
                    user.setRank(options[which]);
                    user.setRankSetting(ranks.get(which));
                    this.ranks.clear();
                });
        return rankPicker;
    }
}