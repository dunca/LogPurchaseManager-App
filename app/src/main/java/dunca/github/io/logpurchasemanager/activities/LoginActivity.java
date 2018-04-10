package dunca.github.io.logpurchasemanager.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.BuyerModel;

public class LoginActivity extends AppCompatActivity {
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

        BuyerModel buyer;

        try {
            buyer = mDbHelper.getBuyerModelDao().queryBuilder().where()
                    .eq("login", username)
                    .queryForFirst();
        } catch (SQLException e) {
            // cannot talk to the db, missing tables, etc.
            String errorString = getString(R.string.activity_login_cannot_read_credentials_msg);

            Snackbar.make(mRootLayout, errorString, Snackbar.LENGTH_INDEFINITE).show();

            Log.e(TAG, String.format("%s: %s", errorString, e.getMessage()));
            return;
        }

        /*
        no BuyerModel with the given username, or the supplied password doesn't match the one
        in the db
        */
        if (buyer == null || !buyer.getPassword().equals(password)) {
            Snackbar.make(mRootLayout, R.string.activity_login_invalid_credentials_msg,
                    Snackbar.LENGTH_INDEFINITE).show();

            return;
        }

        // TODO start next activity

        String successMessage = getString(R.string.activity_login_successfully_logged_in_msg);
        Snackbar.make(mRootLayout, successMessage, Snackbar.LENGTH_INDEFINITE).show();

        Log.i(TAG, successMessage);
    }
}
