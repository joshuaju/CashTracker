package jungen.com.cashtracker.model;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jungen.com.cashtracker.R;

public class MainActivity extends AppCompatActivity {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    private DatabaseReference mPurchaseRef;
    private ArrayList<String> categorySuggestions;
    private ArrayList<String> subcategorySuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categorySuggestions = new ArrayList<>();
        subcategorySuggestions = new ArrayList<>();
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
            categorySuggestions.clear();
            subcategorySuggestions.clear();

            mPurchaseRef = FirebaseDatabase.getInstance().getReference(
                    FirebaseNodes.PURCHASES + "/" + user.getUid());
            FirebaseListAdapter<Purchase> adapter = new FirebaseListAdapter<Purchase>(this,
                    Purchase.class, R.layout.list_row_purchase, mPurchaseRef) {
                @Override
                protected void populateView(View view, Purchase model, int position) {
                    TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
                    TextView tvSubcategory = (TextView) view.findViewById(R.id.tvSubcategory);
                    TextView tvDate = (TextView) view.findViewById(R.id.btnDate);
                    TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);

                    String category = model.getCategory();
                    String subcategory = model.getSubcategory();
                    String date = dateFormat.format(model.getDate());
                    String price = "" + model.getPrice();

                    tvCategory.setText(category);
                    tvSubcategory.setText(subcategory);
                    tvDate.setText(date);
                    tvPrice.setText(price);

                    if (!categorySuggestions.contains(category)) {
                        categorySuggestions.add(category);
                        Log.d("Suggestions", "Add Category:" + category);
                    }
                    if (!subcategorySuggestions.contains(subcategory)) {
                        subcategorySuggestions.add(subcategory);
                        Log.d("Suggestions", "Add Subcategory:" + subcategory);
                    }
                }
            };
            lvPurchaseList.setAdapter(adapter);
        }
    }

    public void fabOnClick(View view) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);
        intent.putStringArrayListExtra(AddPurchaseActivity.KEY_CATEGORIES, categorySuggestions);
        intent.putStringArrayListExtra(AddPurchaseActivity.KEY_SUBCATEGORIES, subcategorySuggestions);
        startActivityForResult(intent, AddPurchaseActivity.REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AddPurchaseActivity.REQUEST_ADD:
                if (resultCode == RESULT_OK) {
                    Purchase purchase = (Purchase) data.getSerializableExtra(
                            Purchase.class.getSimpleName());
                    mPurchaseRef.push().setValue(purchase);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
