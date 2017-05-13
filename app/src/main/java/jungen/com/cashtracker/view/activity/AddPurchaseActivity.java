package jungen.com.cashtracker.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.DateFormatHelper;
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.model.Purchase;

public class AddPurchaseActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    public final static int REQUEST_ADD_PURCHASE = 1;
    public final static int REQUEST_EDIT_PURCHASE = 2;

    public final static String KEY_POSITION = "position";

    private Purchase mPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button tvDate = (Button) findViewById(R.id.btnTime);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        AutoCompleteTextView etCategory = (AutoCompleteTextView) findViewById(
                R.id.etCategory);
        AutoCompleteTextView etSubcategory = (AutoCompleteTextView) findViewById(
                R.id.etSubcategory);

        DatabaseReference ref = FirebaseNodes.getInstance().getQueriedPurchases().getRef();
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item);
        final ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item);
        etCategory.setAdapter(categoryAdapter);
        etSubcategory.setAdapter(subcategoryAdapter);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // the two HashMaps make sure that there are no duplicate entries in the
                        // autocomplete adapters
                        HashMap<String, Boolean> checkedCategory = new HashMap<String, Boolean>();
                        HashMap<String, Boolean> checkedSubategory = new HashMap<String, Boolean>();
                        for (DataSnapshot tmpSnapshot : dataSnapshot.getChildren()) {
                            String category = tmpSnapshot.child("category").getValue(String.class);
                            String subcategory = tmpSnapshot.child("subcategory").getValue(
                                    String.class);

                            if (!checkedCategory.containsKey(category)) {
                                categoryAdapter.add(category);
                                checkedCategory.put(category, true);
                            }
                            if (subcategory != null && subcategory.length() > 0
                                    && !checkedSubategory.containsKey(subcategory)) {
                                subcategoryAdapter.add(subcategory);
                                checkedSubategory.put(subcategory, true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Purchase purchase = (Purchase) getIntent().getSerializableExtra(
                Purchase.class.getSimpleName());

        if (purchase == null) {
            mPurchase = new Purchase();
            mPurchase.setTime(Calendar.getInstance().getTimeInMillis());
        } else {
            mPurchase = purchase;
        }
        updateText();
    }

    public void updateText() {
        AutoCompleteTextView etCategory = (AutoCompleteTextView) findViewById(
                R.id.etCategory);
        AutoCompleteTextView etSubcategory = (AutoCompleteTextView) findViewById(
                R.id.etSubcategory);
        EditText etPrice = (EditText) findViewById(R.id.etPrice);
        Button btnDate = (Button) findViewById(R.id.btnTime);

        etCategory.setText(mPurchase.getCategory());
        etSubcategory.setText(mPurchase.getSubcategory());
        if (mPurchase.getPrice() > 0) {
            etPrice.setText("" + mPurchase.getPrice());
        } else {
            etPrice.setText("");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mPurchase.getTime());
        btnDate.setText(DateFormatHelper.format(calendar));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_purchase, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if (isInputValid()) {
                    Intent result = getIntent();
                    result.putExtra(Purchase.class.getSimpleName(), mPurchase);
                    setResult(RESULT_OK, result);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mPurchase.getTime());
        DatePickerDialog dlg = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(

                Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    /**
     * Checks all input fields and sets error messages if necessary. The member variable {@link
     * AddPurchaseActivity#mPurchase}
     * stores valid values.
     *
     * @return True if all inputs are valid. It is safe to use mPurchase. Otherwise false
     */
    private boolean isInputValid() {
        EditText etCategory = (EditText) findViewById(R.id.etCategory);
        EditText etSubcategory = (EditText) findViewById(R.id.etSubcategory);
        EditText etPrice = (EditText) findViewById(R.id.etPrice);

        boolean isValid = true;

        String categeory = etCategory.getText().toString().trim();
        if (categeory.length() == 0) {
            etCategory.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            mPurchase.setCategory(categeory);
        }
        String subcategory = etSubcategory.getText().toString().trim();
        mPurchase.setSubcategory(subcategory);

        String strPrice = etPrice.getText().toString().trim();
        if (strPrice.length() == 0) {
            etPrice.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            mPurchase.setPrice(Double.parseDouble(strPrice));
        }

        if (!isValid) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        String date = DateFormatHelper.format(calendar);

        Button btnDate = (Button) findViewById(R.id.btnTime);
        btnDate.setText(date);
        mPurchase.setTime(calendar.getTimeInMillis());
    }

}
