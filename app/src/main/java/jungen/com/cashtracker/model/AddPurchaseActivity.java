package jungen.com.cashtracker.model;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jungen.com.cashtracker.R;

public class AddPurchaseActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button tvDate = (Button) findViewById(R.id.btnDate);
        tvDate.setText(getDateAsString(Calendar.getInstance()));
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_purchase, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                if (isInputValid()) {
                    Snackbar.make(getCurrentFocus(), "Add Item", Snackbar.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this, this, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(

                Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    private boolean isInputValid() {
        EditText etCategory = (EditText) findViewById(R.id.etCategory);
        EditText etSubcategory = (EditText) findViewById(R.id.etSubcategory);
        EditText etPrice = (EditText) findViewById(R.id.etPrice);

        boolean isValid = true;

        String categeory = etCategory.getText().toString().trim();
        if (categeory.length() == 0){
            etCategory.setError(getString(R.string.error_field_required));
            isValid = false;
        }
        String subcategory = etSubcategory.getText().toString().trim();

        String strPrice = etPrice.getText().toString().trim();
        if (strPrice.length() == 0){
            etPrice.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (!isValid){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calender = Calendar.getInstance();
        calender.set(year, month, dayOfMonth);
        String date = getDateAsString(calender);

        Button tvDate = (Button) findViewById(R.id.btnDate);
        tvDate.setText(date);
    }

    private String getDateAsString(Calendar calendar){
        DateFormat format = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
        String date = format.format(calendar.getTime());
        return date;
    }
}
