package com.engineerskasa.safetapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.engineerskasa.safetapp.Model.PantryListObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.engineerskasa.safetapp.Utility.Tools;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class AddNewItemActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_GALLERY_CODE = 1000 ;
    private static final int REQUEST_CAMERA_CODE = 2000 ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, user_items;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private FirebaseAuth auth;
    private FirebaseUser user;

    RecyclerView recyclerView;

    private EditText txt_item_name, txt_notes, txt_units_threshold, txt_quantity, txt_mode_pres,
    txt_exp_date, txt_unit_price;
    Spinner spn_category, spn_units;
    Button btn_save, btn_update;
    CheckBox exp_notice;
    private String category_name, units_selected, exp_date, selected_date, days_to_expire,
            item_name, description, minimum_units, quantity, unit_price, notify_me, mode_of_preservation;

    private LinearLayout expLayout;

    String[] units;
    ArrayList<String> categoryArray = new ArrayList<>();

    private ImageButton add_category;
    private RoundedImageView open_camera_sheet;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    private Uri item_image;
    private Bitmap camera_item_image;

    private String currentPhotoPath, imageFileName;

    private File filepath;
    private boolean selected_camera = false;

    private String[] downloadURL = new String[1];

    ArrayAdapter<String> arrayCategoryAdapter;
    ArrayAdapter<String> arrayUnitsAdapter;

    String selectedKey;
    String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Item");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);
        user_items = databaseReference.child(Constants.PANTRY);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        units = getResources().getStringArray(R.array.item_units);

        // initialize UI
        spn_units = (Spinner) findViewById(R.id.spn_units);
        spn_category = (Spinner) findViewById(R.id.spn_category_name);

        txt_item_name = (EditText)findViewById(R.id.txt_item_name);
        txt_notes = (EditText)findViewById(R.id.txt_description);
        txt_units_threshold = (EditText)findViewById(R.id.txt_qty_threshold);
        txt_quantity = (EditText)findViewById(R.id.txt_item_qty);
        txt_mode_pres = (EditText)findViewById(R.id.txt_pres_mode);
        txt_exp_date = (EditText)findViewById(R.id.txt_exp_date);
        txt_unit_price = (EditText)findViewById(R.id.txt_unit_price);

        expLayout = (LinearLayout) findViewById(R.id.exp_layout);

        exp_notice = (CheckBox) findViewById(R.id.exp_notice);

        add_category = (ImageButton) findViewById(R.id.add_cat);
        open_camera_sheet = (RoundedImageView) findViewById(R.id.open_cam_sheet);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        btn_save = (Button) findViewById(R.id.btn_save_item);
        btn_update = (Button) findViewById(R.id.btn_update_item);

        add_category.setOnClickListener(this);
        open_camera_sheet.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_update.setOnClickListener(this);

        txt_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cur_calender = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                long date_ship_millis = calendar.getTimeInMillis();


                                txt_exp_date.setText(Tools.getFormattedDateSimple(date_ship_millis));

                                selected_date = Tools.getSimpleFormattedDate(date_ship_millis);

                                expLayout.setVisibility(View.VISIBLE);

                                try {
                                    cur_calender.get(Calendar.YEAR);
                                    cur_calender.get(Calendar.MONTH);
                                    cur_calender.get(Calendar.DAY_OF_MONTH);

                                    long today = cur_calender.getTimeInMillis();
                                    String todayDate = Tools.getSimpleFormattedDate(today);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                                    Date currentDate = simpleDateFormat.parse(todayDate);

                                    Date userDate = simpleDateFormat.parse(selected_date);

                                   days_to_expire = Tools.printDifference(currentDate, userDate);
                                    Log.e("TAG", "today: "+ currentDate +
                                            " userDate: "+ userDate + "" +
                                            " selected date: "+ selected_date +
                                            " days: "+ days_to_expire+
                                            " cal: "+ todayDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        cur_calender.get(Calendar.YEAR),
                        cur_calender.get(Calendar.MONTH),
                        cur_calender.get(Calendar.DAY_OF_MONTH)


                );
                // set light theme
                datePickerDialog.setThemeDark(false);
                datePickerDialog.setMinDate(cur_calender);
                datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                datePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");


            }
        });


        databaseReference.child(Constants.CATEGORY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                            String cat_name = categorySnapshot.child("categoryTitle").getValue(String.class);
                            categoryArray.add(cat_name);
                        }

                        assign_spinner(categoryArray);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

         arrayUnitsAdapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.network_spinner_layout, R.id.network_spn, units);

        spn_units.setAdapter(arrayUnitsAdapter);

        spn_units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                units_selected = units[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category_name = categoryArray.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getIntent().getStringExtra(Constants.EDIT_ITEM_KEY) != null) {
            btn_update.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.GONE);
            selectedKey = getIntent().getStringExtra(Constants.EDIT_ITEM_KEY);
           user_items.child(user.getUid()).child(selectedKey)
                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           imageURL = dataSnapshot.child("item_image").getValue(String.class);
                           txt_item_name.setText(dataSnapshot.child("itemName").getValue(String.class));
                           txt_notes.setText(dataSnapshot.child("description").getValue(String.class));
                           txt_units_threshold.setText(dataSnapshot.child("quantity_threshold").getValue(String.class));
                           txt_quantity.setText(dataSnapshot.child("quantity").getValue(String.class));
                           txt_mode_pres.setText(dataSnapshot.child("mode_of_preservation").getValue(String.class));
                           txt_exp_date.setText(dataSnapshot.child("expiration_date").getValue(String.class));
                           txt_unit_price.setText(dataSnapshot.child("unit_price").getValue(String.class));

                           String category = dataSnapshot.child("category").getValue(String.class);
                           String units = dataSnapshot.child("unit").getValue(String.class);

                           int unitsPos = arrayUnitsAdapter.getPosition(units);
                           spn_units.setSelection(unitsPos);

                           int categoryPosition = arrayCategoryAdapter.getPosition(category);
                           spn_category.setSelection(categoryPosition);

                           if (!imageURL.isEmpty()) {
                               Glide.with(getApplicationContext()).load(imageURL)
                                       .diskCacheStrategy(DiskCacheStrategy.NONE)
                                       .into(open_camera_sheet);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
        }
    }

    private void assign_spinner(ArrayList<String> categoryArray) {
        arrayCategoryAdapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.network_spinner_layout, R.id.network_spn, categoryArray);

        spn_category.setAdapter(arrayCategoryAdapter);
    }


    @Override
    public void onClick(View v) {
        if (v == add_category) {
            startActivity(new Intent(AddNewItemActivity.this, AddCategoryActivity.class));
        }

        if (v == btn_update) {
            item_name = txt_item_name.getText().toString();
            description = txt_notes.getText().toString();
            minimum_units = txt_units_threshold.getText().toString();
            quantity = txt_quantity.getText().toString();
            unit_price = txt_unit_price.getText().toString();
            mode_of_preservation = txt_mode_pres.getText().toString();
            exp_date = txt_exp_date.getText().toString();

            if (exp_notice.isChecked())
                notify_me = "yes";
            else
                notify_me = "no";

            if (TextUtils.isEmpty(item_name)) {
                txt_item_name.setError("Please enter item name");
                txt_item_name.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(minimum_units)) {
                txt_units_threshold.setError("Please enter minimum threshold");
                txt_units_threshold.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(quantity)) {
                txt_quantity.setError("Please enter item quantity");
                txt_quantity.requestFocus();
                return;
            }
            PantryListObject object = new PantryListObject();
            object.setItemName(item_name);
            object.setCategory(category_name);
            object.setDescription(description);
            object.setQuantity_threshold(minimum_units);
            object.setQuantity(quantity);
            object.setUnit(units_selected);
            object.setMode_of_preservation(mode_of_preservation);
            object.setExpiration_date(exp_date);
            object.setUnit_price(unit_price);
            object.setExp_notice(notify_me);
            object.setDays_left_to_expire(days_to_expire);
            object.setItem_image(imageURL);
            final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Updating Item");
            user_items.child(user.getUid()).child(selectedKey)
                    .setValue(object)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            waitingDialog.dismiss();
                            Toast.makeText(AddNewItemActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    waitingDialog.dismiss();
                    Toast.makeText(AddNewItemActivity.this, "Couldn't Update", Toast.LENGTH_SHORT).show();
                    Log.e("ASDF", "onFailure: "+e.getMessage() );
                }
            });

        }

        if (v == open_camera_sheet) {
            Dexter.withActivity(this)
                    .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE)
                    .withListener(new BaseMultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            super.onPermissionsChecked(report);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            super.onPermissionRationaleShouldBeShown(permissions, token);
                        }
                    }).check();
            if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            final View view = getLayoutInflater().inflate(R.layout.open_gallery_layout, null);

            ((View) view.findViewById(R.id.open_camera)).setOnClickListener(v12 -> {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddNewItemActivity.this,
                                "com.engineerskasa.safetapp.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
                    }
                }
            });

            ((View) view.findViewById(R.id.open_gallery)).setOnClickListener(v1 -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(Intent.createChooser(intent, "Select Item Image"), REQUEST_GALLERY_CODE);
            });

            mBottomSheetDialog = new BottomSheetDialog(this);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBottomSheetDialog = null;
                }
            });
        }

        if (v == btn_save) {
            item_name = txt_item_name.getText().toString();
            description = txt_notes.getText().toString();
            minimum_units = txt_units_threshold.getText().toString();
            quantity = txt_quantity.getText().toString();
            unit_price = txt_unit_price.getText().toString();
            mode_of_preservation = txt_mode_pres.getText().toString();
            exp_date = txt_exp_date.getText().toString();

            if (exp_notice.isChecked())
                notify_me = "yes";
            else
                notify_me = "no";

            if (TextUtils.isEmpty(item_name)) {
                txt_item_name.setError("Please enter item name");
                txt_item_name.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(minimum_units)) {
                txt_units_threshold.setError("Please enter minimum threshold");
                txt_units_threshold.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(quantity)) {
                txt_quantity.setError("Please enter item quantity");
                txt_quantity.requestFocus();
                return;
            }
            final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Saving Item");
            if (item_image != null) {
                StorageReference image_bucket = storageReference.child(Constants.ITEM_IMAGES+ "/"+ imageFileName);
                image_bucket.putFile(item_image).addOnSuccessListener(taskSnapshot ->
                        image_bucket.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadURL[0] = uri.toString().substring(0, uri.toString().indexOf("&token"));
                    PantryListObject object = new PantryListObject();
                    object.setItemName(item_name);
                    object.setCategory(category_name);
                    object.setDescription(description);
                    object.setQuantity_threshold(minimum_units);
                    object.setQuantity(quantity);
                    object.setUnit(units_selected);
                    object.setMode_of_preservation(mode_of_preservation);
                    object.setExpiration_date(exp_date);
                    object.setUnit_price(unit_price);
                    object.setExp_notice(notify_me);
                    object.setDays_left_to_expire(days_to_expire);
                    object.setItem_image(downloadURL[0]);

                    user_items.child(user.getUid()).push()
                            .setValue(object)
                            .addOnSuccessListener(aVoid -> {
                                waitingDialog.dismiss();
                                Toast.makeText(AddNewItemActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                waitingDialog.dismiss();
                                Toast.makeText(AddNewItemActivity.this, "Couldn't add item.. try again later", Toast.LENGTH_SHORT).show();
                                Log.e("ASDF", "onFailure: "+e.getMessage() );
                            });

                })).addOnFailureListener(e -> {
                    waitingDialog.dismiss();
                    Toast.makeText(AddNewItemActivity.this, "Upload Failed:", Toast.LENGTH_SHORT).show();
                    Log.e("ASDF", "onFailure: "+e.getMessage() );
                });
            } else {
                PantryListObject object = new PantryListObject();
                object.setItemName(item_name);
                object.setCategory(category_name);
                object.setDescription(description);
                object.setQuantity_threshold(minimum_units);
                object.setQuantity(quantity);
                object.setUnit(units_selected);
                object.setMode_of_preservation(mode_of_preservation);
                object.setExpiration_date(exp_date);
                object.setUnit_price(unit_price);
                object.setExp_notice(notify_me);
                object.setDays_left_to_expire(days_to_expire);
                object.setItem_image("");

                user_items.child(user.getUid()).push()
                        .setValue(object)
                        .addOnSuccessListener(aVoid -> {
                            waitingDialog.dismiss();
                            Toast.makeText(AddNewItemActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            waitingDialog.dismiss();
                            Toast.makeText(AddNewItemActivity.this, "Couldn't add item.. try again later", Toast.LENGTH_SHORT).show();
                            Log.e("ASDF", "onFailure: "+e.getMessage() );
                        });
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_CODE  && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mBottomSheetDialog.dismiss();

            selected_camera = false;

            item_image = data.getData();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = timeStamp + "." + getFileExt(item_image);

            open_camera_sheet.setImageURI(item_image);

        }
        if (requestCode == REQUEST_CAMERA_CODE  && resultCode == RESULT_OK) {
            mBottomSheetDialog.dismiss();
            selected_camera = true;

            filepath = new File(currentPhotoPath);
            open_camera_sheet.setImageURI(Uri.fromFile(filepath));

            imageFileName = filepath.getName();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            item_image = Uri.fromFile(filepath);
            mediaScanIntent.setData(item_image);
            this.sendBroadcast(mediaScanIntent);


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
