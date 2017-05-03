package jungen.com.cashtracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
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
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.model.Purchase;

public class MainActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    /**
     * Reference to purchase node of the current user
     */
    private DatabaseReference mPurchaseRef;
    /**
     * Stores all categories of current list view item
     */
    private ArrayList<String> categorySuggestions;
    /**
     * Stores all subcategories of current list view item
     */
    private ArrayList<String> subcategorySuggestions;
    private ListView lvPurchaseList;
    FirebaseListAdapter<Purchase> purchaseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categorySuggestions = new ArrayList<>();
        subcategorySuggestions = new ArrayList<>();

        lvPurchaseList = (ListView) findViewById(R.id.lvPurchase);

        lvPurchaseList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvPurchaseList.setMultiChoiceModeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            purchaseListAdapter = new FirebaseListAdapter<Purchase>(this,
                    Purchase.class, R.layout.list_row_purchase, mPurchaseRef) {
                @Override
                protected void populateView(View view, Purchase model, int position) {
                    TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
                    TextView tvSubcategory = (TextView) view.findViewById(R.id.tvSubcategory);
                    TextView tvDate = (TextView) view.findViewById(R.id.btnTime);
                    TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);

                    String category = model.getCategory();
                    String subcategory = model.getSubcategory();
                    String time = model.getTimeAsString();
                    String price = model.getPriceAsString();

                    tvCategory.setText(category);
                    tvSubcategory.setText(subcategory);
                    tvDate.setText(time);
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
            lvPurchaseList.setAdapter(purchaseListAdapter);
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
                    Log.d("Purchase", "Add purchase:" + purchase.getCategory());
                }
                break;
            case AddPurchaseActivity.REQUEST_EDIT:
                int position = data.getIntExtra(AddPurchaseActivity.KEY_POSITION, -1);
                if (position != -1){
                    Purchase purchase = (Purchase) data.getSerializableExtra(
                            Purchase.class.getSimpleName());
                    purchaseListAdapter.getRef(position).setValue(purchase);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    SparseBooleanArray checkedState = new SparseBooleanArray();
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        checkedState.put(position, checked);
        Log.d("CheckState", "Checked:" + checked+ " at index " + position);

        int selectionCounter = 0;
        for (int pos = 0; pos < checkedState.size(); pos++){
            if (checkedState.valueAt(pos)){
                selectionCounter++;
                if (selectionCounter > 1){
                    break;
                }
            }
        }
        if (selectionCounter > 1){
            mode.getMenu().getItem(1).setVisible(false);
            Log.d("CheckState", "Hide edit menu icon");
        } else {
            mode.getMenu().getItem(1).setVisible(true);
            Log.d("CheckState", "Show edit menu icon");
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        checkedState.clear();
        mode.getMenuInflater().inflate(R.menu.purchase_selected, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int pos;
        int key;
        SparseBooleanArray checked;
        switch (item.getItemId()){
            case R.id.menu_edit:
               key = -1;
                for (pos = 0; pos < checkedState.size(); pos++){
                    key = checkedState.keyAt(pos);
                    if (checkedState.get(key)) {
                        break;
                    }
                }
                Log.d("CheckState", "Edit value at index " + key);
                Purchase purchase = purchaseListAdapter.getItem(key);
                Intent intent = new Intent(this, AddPurchaseActivity.class);
                intent.putExtra(AddPurchaseActivity.KEY_CATEGORIES, categorySuggestions);
                intent.putExtra(AddPurchaseActivity.KEY_SUBCATEGORIES, subcategorySuggestions);
                intent.putExtra(AddPurchaseActivity.KEY_POSITION, key);
                intent.putExtra(Purchase.class.getSimpleName(), purchase);
                startActivityForResult(intent, AddPurchaseActivity.REQUEST_EDIT);
                break;
            case R.id.menu_delete:
                for (pos = 0; pos < checkedState.size(); pos++){
                    key = checkedState.keyAt(pos);
                    if (checkedState.get(key)) {
                        purchaseListAdapter.getRef(key).removeValue();
                        Log.d("CheckState", "Remove value at index " + key);
                    }
                }
                break;
            default:
                return false;
        }
        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
