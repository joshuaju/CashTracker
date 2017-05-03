package jungen.com.cashtracker.view.activity;

import static jungen.com.cashtracker.misc.CredentialHelper.isPasswordValid;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import jungen.com.cashtracker.R;
import jungen.com.cashtracker.misc.CredentialHelper;

public class SignUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setSubtitle(R.string.title_activity_sign_up);
    }

    public void onClick(final View view) {
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        EditText etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);


        if (CredentialHelper.isEmailValid(this, etEmail) && CredentialHelper.isPasswordValid(this,
                etPassword, etPasswordConfirm)) {
            String email = CredentialHelper.getEmail(etEmail);
            String password = CredentialHelper.getPassword(etPassword);

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.w(SignUpActivity.this.getClass().getSimpleName(),
                                        "createUserWithEmail:success");
                                finish();
                            } else {
                                Log.w(SignUpActivity.this.getClass().getSimpleName(),
                                        "createUserWithEmail:failure", task.getException());
                                Snackbar.make(view, "Error. Please try again.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

}
