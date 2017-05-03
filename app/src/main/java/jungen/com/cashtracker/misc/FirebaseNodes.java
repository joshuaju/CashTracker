package jungen.com.cashtracker.misc;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Joshua Jungen on 29.04.2017.
 */

public class FirebaseNodes {

    public static final String PURCHASES = "purchases";

    public static DatabaseReference createPurchasesOfCurrentUserReference(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return FirebaseDatabase.getInstance().getReference(PURCHASES + "/" + user.getUid());
        } else {
            throw new RuntimeException("No current user");
        }
    }
}
