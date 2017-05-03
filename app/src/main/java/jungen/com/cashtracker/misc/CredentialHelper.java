package jungen.com.cashtracker.misc;

import static android.R.attr.password;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import jungen.com.cashtracker.R;

/**
 * Created by Joshua Jungen on 29.04.2017.
 */

public class CredentialHelper {

    public static boolean isEmailValid(Context context, EditText etEmail) {
        etEmail.setError(null);

        String email = etEmail.getText().toString().trim();
        if (email.length() == 0) {
            etEmail.setError(context.getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    public static boolean isPasswordValid(Context context, EditText etPassword){
        etPassword.setError(null);
        String password = etPassword.getText().toString();
        if (password.length() == 0) {
            etPassword.setError(context.getString(R.string.error_invalid_password));
            return false;
        }
        return true;
    }

    public static boolean isPasswordValid(Context context, EditText etPassword, EditText etPasswordConfirm) {
        etPassword.setError(null);
        etPasswordConfirm.setError(null);

        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();

        if (!isPasswordValid(context, etPassword)){
            return false;
        }
        if (passwordConfirm.length() == 0) {
            etPasswordConfirm.setError(context.getString(R.string.error_field_required));
            return false;
        }
        return true;
    }

    @NonNull
    public static String getEmail(EditText etEmail){
        return etEmail.getText().toString().trim();
    }

    @NonNull
    public static String getPassword(EditText etPassword) {
        return etPassword.getText().toString();
    }
}
