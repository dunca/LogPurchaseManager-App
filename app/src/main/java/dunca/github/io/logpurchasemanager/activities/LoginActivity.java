package dunca.github.io.logpurchasemanager.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;

public class LoginActivity extends AppCompatActivity {
    private final String PROP_USERNAME = "prop_username";

    private final String TAG = LoginActivity.class.getName();

    private DatabaseHelper mDbHelper;

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private View mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDbHelper = new DatabaseHelper(this);

        initViews();
        setOnClickListeners();

        retrievePreviousUsername();
    }

    private void initViews() {
        mRootLayout = findViewById(R.id.rootLayout_login_activity);

        mEtUsername = findViewById(R.id.etUsername_login_activity);
        mEtPassword = findViewById(R.id.etPassword_login_activity);
        mBtnLogin = findViewById(R.id.btnLogin_login_activity);
    }

    private void setOnClickListeners() {
        mBtnLogin.setOnClickListener(source -> handleLogin());
    }

    private void handleLogin() {
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();

        Acquirer acquirer;

        try {
            acquirer = mDbHelper.getAcquirerDao().queryBuilder().where()
                    .eq(CommonFieldNames.USERNAME, username)
                    .queryForFirst();
        } catch (SQLException e) {
            // cannot talk to the db, missing tables, etc.
            String errorString = getString(R.string.activity_login_cannot_read_credentials_msg);

            PopupUtil.snackbar(mRootLayout, errorString);
            Log.e(TAG, String.format("%s: %s", errorString, e.getMessage()));

            return;
        }

        /*
        no Acquirer with the given username, or the supplied password doesn't match the one
        in the db
        */
        if (acquirer == null || !acquirer.getPassword().equals(password)) {
            PopupUtil.snackbar(mRootLayout, R.string.activity_login_invalid_credentials_msg);

            return;
        }

        String successMessage = getString(R.string.activity_login_successfully_logged_in_msg);

        PopupUtil.snackbar(mRootLayout, successMessage);
        Log.i(TAG, successMessage);

        saveUsername();

        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainTabbedActivity.class);
        startActivity(intent);
    }

    /**
     * Saves the value of {@link #mEtUsername} as a preference with the {@link #PROP_USERNAME}
     * key
     */
    private void saveUsername() {
        String username = mEtUsername.getText().toString();

        if (TextUtils.isEmpty(username)) {
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putString(PROP_USERNAME, username);
        editor.apply();
    }

    /**
     * Attempts to retrieve the value of  the {@link #PROP_USERNAME} preference. If it succeeds,
     * its value is set as the value of {@link #mEtUsername}
     */
    private void retrievePreviousUsername() {
        String username = getSharedPreferences().getString(PROP_USERNAME, null);

        if (username == null) {
            return;
        }

        mEtUsername.setText(username);
    }

    /**
     * Returns the {@link SharedPreferences} instance associated with the current activity
     *
     * @return the {@link SharedPreferences} instance associated with the current activity
     */
    private SharedPreferences getSharedPreferences() {
        return getPreferences(Context.MODE_PRIVATE);
    }
}
