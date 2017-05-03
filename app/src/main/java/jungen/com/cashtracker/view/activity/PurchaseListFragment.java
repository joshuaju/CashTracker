package jungen.com.cashtracker.view.activity;

import static android.R.attr.key;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.FirebaseNodes;
import jungen.com.cashtracker.model.Purchase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPurchaseListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PurchaseListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchaseListFragment extends Fragment implements AbsListView.MultiChoiceModeListener {

    private OnPurchaseListFragmentInteractionListener mListener;
    private DatabaseReference mPurchaseRef;
    private SparseBooleanArray checkedState;
    FirebaseListAdapter<Purchase> purchaseListAdapter;

    public PurchaseListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PurchaseListFragment.
     */
    public static PurchaseListFragment newInstance() {
        PurchaseListFragment fragment = new PurchaseListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkedState = new SparseBooleanArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase_list, container, false);

        ListView lvPurchaseList;
        lvPurchaseList = (ListView) view.findViewById(R.id.lvPurchase);
        lvPurchaseList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvPurchaseList.setMultiChoiceModeListener(this);
        mPurchaseRef = FirebaseNodes.createPurchasesOfCurrentUserReference();
        purchaseListAdapter = new FirebaseListAdapter<Purchase>(getActivity(),
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
            }
        };
        lvPurchaseList.setAdapter(purchaseListAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPurchaseListFragmentInteractionListener) {
            mListener = (OnPurchaseListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPurchaseListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        checkedState.put(position, checked);

        int selectionCounter = 0;
        for (int pos = 0; pos < checkedState.size(); pos++) {
            if (checkedState.valueAt(pos)) {
                selectionCounter++;
                if (selectionCounter > 1) {
                    break;
                }
            }
        }

        if (selectionCounter > 1) {
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
        int position;
        SparseBooleanArray checked;
        switch (item.getItemId()) {
            case R.id.menu_edit:
                position = -1;
                for (int idx = 0; idx < checkedState.size(); idx++) {
                    position = checkedState.keyAt(idx);
                    if (checkedState.get(position)) {
                        break;
                    }
                }
                Purchase purchase = purchaseListAdapter.getItem(position);
                mListener.requestForEdit(position, purchase);
                break;
            case R.id.menu_delete:
                for (int idx = 0; idx < checkedState.size(); idx++) {
                    position = checkedState.keyAt(idx);
                    if (checkedState.get(position)) {
                        purchaseListAdapter.getRef(position).removeValue();
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

    public interface OnPurchaseListFragmentInteractionListener {
        void requestForEdit(int pos, Purchase purchase);
    }

    public void addPurchase(Purchase purchase){
        mPurchaseRef.push().setValue(purchase);
    }

    public void editPurchase(int position, Purchase updatedPurchase) {
        if (position >= 0 && position < purchaseListAdapter.getCount()) {
            purchaseListAdapter.getRef(position).setValue(updatedPurchase);
        } else {
            throw new IndexOutOfBoundsException("position:" + position);
        }
    }
}
