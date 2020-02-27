package com.engineerskasa.safetapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.engineerskasa.safetapp.Utility.Tools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewItemActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    EditText txt_item_name, txt_notes, txt_units_threshold, txt_quantity, txt_mode_pres,
    txt_exp_date, txt_unit_price;
    Spinner spn_category, spn_units;
    Button btn_save;

    String category_name, units_selected, selected_date, days_to_expire;

    String[] units;
    ArrayList<String> categoryArray = new ArrayList<>();


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
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getApplicationContext(), R.layout.network_spinner_layout, R.id.network_spn, categoryArray
                        );

                        spn_category.setAdapter(arrayAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.network_spinner_layout, R.id.network_spn, units
        );

        spn_units.setAdapter(arrayAdapter);

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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
