package jungen.com.cashtracker.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.misc.PurchaseQueryPublisher;
import jungen.com.cashtracker.misc.PurchaseQuerySubscriber;
import jungen.com.cashtracker.model.Purchase;

/**
 * Show information about the purchase list. E.g. time interval and running total of prices.
 */
public class PurchaseInfoFragment extends Fragment implements PurchaseQuerySubscriber,
        AdapterView.OnItemSelectedListener {

    private OnPurchaseInfoFragmentInteractionListener mListener;

    private TextView tvTotal;
    private HashMap<String, Double> mPurchaseMapping;
    private Double mTotal;

    public PurchaseInfoFragment() {
        PurchaseQueryPublisher publisher = FirebaseNodes.getInstance();
        publisher.subscribe(this);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PurchaseInfoFragment.
     */
    public static PurchaseInfoFragment newInstance() {
        PurchaseInfoFragment fragment = new PurchaseInfoFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mTotal = 0.0;
        mPurchaseMapping = new HashMap<String, Double>();

        View view = inflater.inflate(R.layout.fragment_purchase_info, container, false);
        Spinner spnInterval = (Spinner) view.findViewById(R.id.spnInterval);
        tvTotal = (TextView) view.findViewById(R.id.tvTotal);

        String[] intervals = getResources().getStringArray(R.array.spinner_interval_entries);
        spnInterval.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, intervals));
        spnInterval.setOnItemSelectedListener(this);


        tvTotal.setText("0");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPurchaseInfoFragmentInteractionListener) {
            mListener = (OnPurchaseInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPurchaseInfoFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateQueryChanged() {
        mPurchaseMapping.clear();
        mTotal = 0.00;
        Query mPurchaseRef = FirebaseNodes.getInstance().getQueriedPurchases();
        mPurchaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addPurchase(dataSnapshot.getKey(), dataSnapshot.getValue(Purchase.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updatePurchase(dataSnapshot.getKey(), dataSnapshot.getValue(Purchase.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removePurchase(dataSnapshot.getKey(), dataSnapshot.getValue(Purchase.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void addPurchase(String key, Purchase purchase){
        double amount = purchase.getPrice();
        mPurchaseMapping.put(key, amount);
        addToTotal(amount);

        updateTotalView();
    }

    private void updatePurchase(String key, Purchase purchase){
        Double oldAmount = mPurchaseMapping.get(key);
        double newAmount = purchase.getPrice();
        mPurchaseMapping.put(key, newAmount);
        deductFromTotal(oldAmount);
        addToTotal(newAmount);

        updateTotalView();
    }

    private void removePurchase(String key, Purchase purchase){
        mPurchaseMapping.remove(key);
        double amount = purchase.getPrice();
        deductFromTotal(amount);

        updateTotalView();
    }

    private void addToTotal(double amount){
        mTotal += amount;
    }

    private void deductFromTotal(double amount){
        mTotal -= amount;
    }

    private void updateTotalView(){
        DecimalFormat format = new DecimalFormat("0.00");
        String strTotal = format.format(mTotal);
        tvTotal.setText(strTotal);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            String interval = parent.getItemAtPosition(position).toString();
            if (interval.equals("All time")){
                FirebaseNodes.getInstance().setPurchaseQuery(null);
                return;
            } else if (interval.equals("Year")) {
                start.set(Calendar.MONTH, Calendar.JANUARY);
                start.set(Calendar.DAY_OF_MONTH, start.getActualMinimum(Calendar.DAY_OF_MONTH));
                end.set(Calendar.MONTH, Calendar.DECEMBER);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else if (interval.equals("Month")) {
                start.set(Calendar.DAY_OF_MONTH, start.getActualMinimum(Calendar.DAY_OF_MONTH));
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else if (interval.equals("Week")) {
                start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                start.set(Calendar.HOUR_OF_DAY, start.getActualMinimum(Calendar.HOUR_OF_DAY));
                end.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                end.set(Calendar.HOUR_OF_DAY, end.getActualMaximum(Calendar.HOUR_OF_DAY));
            } else if (interval.equals("Day")) {
                start.set(Calendar.HOUR_OF_DAY, start.getActualMinimum(Calendar.HOUR_OF_DAY));
                end.set(Calendar.HOUR_OF_DAY, end.getActualMaximum(Calendar.HOUR_OF_DAY));
            }
            long startInMillis = start.getTimeInMillis();
            long endInMillis = end.getTimeInMillis();

            FirebaseNodes nodes = FirebaseNodes.getInstance();
            nodes.clearPurchaseQuery(false);
            Query query = nodes.getQueriedPurchases().startAt(startInMillis).endAt(endInMillis);
            nodes.setPurchaseQuery(query);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Check whether possible, if yes remove query so no purchases are display or set spinner to 'All time'
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPurchaseInfoFragmentInteractionListener {

    }

}
