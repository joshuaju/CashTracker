package jungen.com.cashtracker.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.model.Purchase;

public class MainActivity extends AppCompatActivity implements
        PurchaseListFragment.OnPurchaseListFragmentInteractionListener {
    public static final int REQUEST_FINISH = -1;
    /**
     * Reference to purchase node of the current user
     */
    private DatabaseReference mPurchaseRef;
    private PurchaseListFragment mPurchaseListFragment;

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
        mPurchaseRef = FirebaseNodes.createPurchasesOfCurrentUserReference();
        mPurchaseListFragment = PurchaseListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentPurchaseListContainer,
                mPurchaseListFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void fabOnClick(View view) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);
        startActivityForResult(intent, AddPurchaseActivity.REQUEST_ADD_PURCHASE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED || resultCode == RESULT_FIRST_USER) {
            Log.d("onActivityResult", "Activity resulted with:" + resultCode);
        } else {
            Purchase purchase = (Purchase) data.getSerializableExtra(
                    Purchase.class.getSimpleName());

            switch (requestCode) {
                case AddPurchaseActivity.REQUEST_ADD_PURCHASE:
                    mPurchaseListFragment.addPurchase(purchase);
                    break;
                case AddPurchaseActivity.REQUEST_EDIT_PURCHASE:
                    int position = data.getIntExtra(AddPurchaseActivity.KEY_POSITION, -1);
                    if (position != -1) {
                        mPurchaseListFragment.editPurchase(position, purchase);
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void requestForEdit(int position, Purchase purchase) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);
        intent.putExtra(AddPurchaseActivity.KEY_POSITION, position);
        intent.putExtra(Purchase.class.getSimpleName(), purchase);
        startActivityForResult(intent, AddPurchaseActivity.REQUEST_EDIT_PURCHASE);
    }
}