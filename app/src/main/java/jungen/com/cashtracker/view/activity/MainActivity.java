package jungen.com.cashtracker.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.model.Purchase;
import jungen.com.cashtracker.view.fragment.PurchaseInfoFragment;
import jungen.com.cashtracker.view.fragment.PurchaseListFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        PurchaseListFragment.OnPurchaseListFragmentInteractionListener,
        PurchaseInfoFragment.OnPurchaseInfoFragmentInteractionListener {
    public static final int REQUEST_FINISH = -1;
    /**
     * Reference to purchase node of the current user
     */
    private PurchaseListFragment mPurchaseListFragment;
    private PurchaseInfoFragment mPurchaseInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPurchaseListFragment == null) {
            mPurchaseListFragment = PurchaseListFragment.newInstance();
        }

        if (mPurchaseInfoFragment == null) {
            mPurchaseInfoFragment = PurchaseInfoFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentPurchaseListContainer, mPurchaseListFragment)
                .replace(R.id.fragmentPurchaseInfoContainer, mPurchaseInfoFragment)
                .commit();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = "None";
        switch (item.getItemId()) {
            case R.id.nav_interval_alltime:
                message = "all time";
                break;
            case R.id.nav_interval_year:
                message = "year";
                break;
            case R.id.nav_interval_month:
                message = "month";
                break;
            case R.id.nav_interval_week:
                message = "week";
                break;
            case R.id.nav_interval_day:
                message = "day";
                break;
        }
        Snackbar.make(findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
