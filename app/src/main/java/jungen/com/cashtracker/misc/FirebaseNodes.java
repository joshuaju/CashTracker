package jungen.com.cashtracker.misc;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Joshua Jungen on 29.04.2017.
 */

public class FirebaseNodes implements PurchaseQueryPublisher {

    public static final String PURCHASES = "purchases";

    private static FirebaseNodes instance;

    public static FirebaseNodes getInstance() {
        if (instance == null) {
            instance = new FirebaseNodes();
        }
        return instance;
    }

    private ArrayList<PurchaseQuerySubscriber> subscribers;
    private DatabaseReference mPurchaseReference;
    private Query mQueriedPurchaseReference;

    public DatabaseReference getUserPurchaseReference() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (mPurchaseReference == null) {
                mPurchaseReference = FirebaseDatabase.getInstance().getReference(
                        PURCHASES + "/" + user.getUid());
            }
            return mPurchaseReference;
        } else {
            throw new RuntimeException("No current user");
        }
    }


    public Query getQueriedPurchases() {
        if (mQueriedPurchaseReference == null) {
            return getUserPurchaseReference().orderByChild("time");
        }
        return mQueriedPurchaseReference;
    }

    /**
     * Clears the current query on the purchases.
     * @param notify If true, all listeners are notified.
     */
    public void clearPurchaseQuery(boolean notify){
        if (notify) {
            setPurchaseQuery(null);
        } else {
            mQueriedPurchaseReference = null;
        }
    }

    /**
     * Sets the query on the purchases and updated all listeners.
     * @param query
     */
    public void setPurchaseQuery(Query query) {
        mQueriedPurchaseReference = query;
        notifyAllQueryChanged();
    }

    @Override
    public void subscribe(PurchaseQuerySubscriber subscriber) {
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }

        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(PurchaseQuerySubscriber subscriber) {
        if (subscriber == null) return;

        subscribers.remove(subscriber);
    }

    @Override
    public void notifyAllQueryChanged() {
        if (subscribers == null) return;

        for (PurchaseQuerySubscriber subscriber : subscribers) {
            subscriber.updateQueryChanged();
        }
    }
}
