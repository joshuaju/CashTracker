package jungen.com.cashtracker.model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import jungen.com.cashtracker.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSignIn:
                signIn(view);
                break;
            case R.id.btnSignUp:
                startActivity(new Intent(this, SignUpActivity.class));
                finish();
                break;
        }
    }

    private void signIn(final View view) {
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);

        if (CredentialHelper.isEmailValid(this, etEmail) && CredentialHelper.isPasswordValid(this,
                etPassword)) {
            String email = CredentialHelper.getEmail(etEmail);
            String password = CredentialHelper.getPassword(etPassword);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,
                    password).addOnCompleteListener(

                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                Snackbar.make(view, "Error. Please try again.",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
