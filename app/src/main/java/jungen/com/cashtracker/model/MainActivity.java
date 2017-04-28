package jungen.com.cashtracker.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jungen.com.cashtracker.R;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mPurchaseRef;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ListView lvPurchaseList = (ListView) findViewById(R.id.lvPurchase);
        lvPurchaseList.setVisibility(View.INVISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignInActivity.class));
        } else {
            lvPurchaseList.setVisibility(View.VISIBLE);

            mPurchaseRef = FirebaseDatabase.getInstance().getReference(
                    FirebaseNodes.PURCHASES + "/" + user.getUid());

            FirebaseListAdapter<Purchase> adapter = new FirebaseListAdapter<Purchase>(this,
                    Purchase.class, R.layout.list_row_purchase, mPurchaseRef) {
                @Override
                protected void populateView(View view, Purchase model, int position) {
                    TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
                    TextView tvSubcategory = (TextView) view.findViewById(R.id.tvSubcategory);
                    TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
                    TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);

                    String category = model.getCategory();
                    String subcategory = model.getSubcategory();
                    String date = dateFormat.format(model.getDate());
                    String price = "" + model.getPrice();

                    tvCategory.setText(category);
                    tvSubcategory.setText(subcategory);
                    tvDate.setText(date);
                    tvPrice.setText(price);
                }
            };
            lvPurchaseList.setAdapter(adapter);
        }
    }

    public void fabOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dlgView = getLayoutInflater().inflate(R.layout.dialog_add_purchase, null);
        EditText etDate = (EditText) dlgView.findViewById(R.id.etDate);
        etDate.setText(dateFormat.format(new Date()));
        builder.setView(dlgView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etCategory = (EditText) dlgView.findViewById(R.id.etCategory);
                EditText etSubcategory = (EditText) dlgView.findViewById(R.id.etSubcategory);
                EditText etDate = (EditText) dlgView.findViewById(R.id.etDate);
                EditText etPrice = (EditText) dlgView.findViewById(R.id.etPrice);

                String category = etCategory.getText().toString().trim();
                String subcategory = etSubcategory.getText().toString().trim();
                Date date = null;
                try {
                    date = dateFormat.parse(etDate.getText().toString());
                } catch (ParseException e) {
                    date = new Date();
                }
                Double price = Double.parseDouble(etPrice.getText().toString());
                mPurchaseRef.push().setValue(new Purchase(category, subcategory, date, price));
            }
        });
        builder.show();
    }

}
