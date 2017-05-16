package jungen.com.cashtracker.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;
import java.util.HashMap;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.misc.PurchaseQueryPublisher;
import jungen.com.cashtracker.misc.PurchaseQuerySubscriber;
import jungen.com.cashtracker.model.Purchase;

/**
 * Show information about the purchase list. E.g. time interval and running total of prices.
 */
public class PurchaseInfoFragment extends Fragment implements PurchaseQuerySubscriber {

    private OnPurchaseInfoFragmentInteractionListener mListener;

    private TextView tvTotal;
    private HashMap<String, Double> mPurchaseMapping;
    private Double mTotal;

    public PurchaseInfoFragment() {

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
        tvTotal = (TextView) view.findViewById(R.id.tvTotal);
        tvTotal.setText("0");

        PurchaseQueryPublisher publisher = FirebaseNodes.getInstance();
        publisher.subscribe(this);
        updateQueryChanged();

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
        updateTotalView();

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
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addPurchase(String key, Purchase purchase) {
        double amount = purchase.getPrice();
        mPurchaseMapping.put(key, amount);
        addToTotal(amount);

        updateTotalView();
    }

    private void updatePurchase(String key, Purchase purchase) {
        Double oldAmount = mPurchaseMapping.get(key);
        double newAmount = purchase.getPrice();
        mPurchaseMapping.put(key, newAmount);
        deductFromTotal(oldAmount);
        addToTotal(newAmount);

        updateTotalView();
    }

    private void removePurchase(String key, Purchase purchase) {
        mPurchaseMapping.remove(key);
        double amount = purchase.getPrice();
        deductFromTotal(amount);

        updateTotalView();
    }

    private void addToTotal(double amount) {
        mTotal += amount;
    }

    private void deductFromTotal(double amount) {
        mTotal -= amount;
    }

    private void updateTotalView() {
        DecimalFormat format = new DecimalFormat("0.00");
        String strTotal = format.format(mTotal);
        tvTotal.setText(strTotal);
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
