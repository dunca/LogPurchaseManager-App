package dunca.github.io.logpurchasemanager.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.service.AcquisitionDataService;
import dunca.github.io.logpurchasemanager.service.Callback;
import dunca.github.io.logpurchasemanager.service.StaticDataService;
import io.github.dunca.logpurchasemanager.shared.model.Acquirer;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;
import io.github.dunca.logpurchasemanager.shared.model.custom.AcquisitionData;
import io.github.dunca.logpurchasemanager.shared.model.custom.StaticData;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final String PROP_USERNAME = "prop_username";

    private final String TAG = LoginActivity.class.getName();

    private DatabaseHelper mDbHelper;

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private View mRootLayout;
    private View mLoginLayout;
    private TextView mTvSyncStaticDataPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDbHelper = new DatabaseHelper(this);

        initViews();
        setOnClickListeners();

        retrievePreviousUsername();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateLoginViewVisibility();
    }

    private void updateLoginViewVisibility() {
        if (!dbHasAcquirers()) {
            mLoginLayout.setVisibility(View.GONE);
            mTvSyncStaticDataPlaceholder.setVisibility(View.VISIBLE);
        } else {
            mLoginLayout.setVisibility(View.VISIBLE);
            mTvSyncStaticDataPlaceholder.setVisibility(View.GONE);
        }
    }

    private boolean dbHasAcquirers() {
        return mDbHelper.getAcquirerDao().countOf() > 0;
    }

    private void initViews() {
        mRootLayout = findViewById(R.id.rootLayout);
        mLoginLayout = findViewById(R.id.loginLayout);
        mEtUsername = findViewById(R.id.etUsername);
        mEtPassword = findViewById(R.id.etPassword);
        mBtnLogin = findViewById(R.id.btnLogin);

        mTvSyncStaticDataPlaceholder = findViewById(R.id.tvSyncStaticDataPlaceholder);
    }

    private void setOnClickListeners() {
        mBtnLogin.setOnClickListener(view -> handleLogin());
    }

    private void handleLogin() {
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();

        Acquirer acquirer;

        try {
            acquirer = mDbHelper.getAcquirerDao().queryBuilder().where()
                    .eq(CommonFieldNames.USERNAME, username).queryForFirst();
        } catch (SQLException e) {
            PopupUtil.snackbar(mRootLayout, R.string.activity_login_cannot_read_credentials_msg);
            e.printStackTrace();

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

        PopupUtil.snackbar(mRootLayout, R.string.activity_login_successfully_logged_in_msg);

        saveUsername();

        startAcquisitionListActivity();
    }

    private void startAcquisitionListActivity() {
        Intent intent = new Intent(this, AcquisitionListActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync_static_data) {
            syncStaticData();
        } else if (id == R.id.action_sync_acquisitions) {
            syncAcquisitionData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncStaticData() {
        StaticDataService.getInstance().getStaticData(new Callback<StaticData>(this) {
            @Override
            public void onResponse(Response<StaticData> response) {
                if (response.isSuccessful()) {
                    StaticData newStaticData = response.body();

                    try {
                        mDbHelper.replaceStaticData(newStaticData);
                    } catch (SQLException e) {
                        PopupUtil.snackbar(mRootLayout, R.string.activity_login_cannot_save_static_data_msg);
                        e.printStackTrace();
                        return;
                    }

                    updateLoginViewVisibility();
                    PopupUtil.snackbar(mRootLayout, R.string.activity_login_successfully_synced_static_data_msg);
                } else {
                    PopupUtil.serviceErrorSnackbar(mRootLayout, response.code());
                    logUnsuccessfulServiceCall(response);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                PopupUtil.serviceUnreachableSnackbar(mRootLayout);
                logFailedServiceCall(throwable);
            }
        });
    }

    private void syncAcquisitionData() {
        AcquisitionData acquisitionData;

        try {
            acquisitionData = mDbHelper.getUnsyncedAcquisitionData();
        } catch (SQLException e) {
            PopupUtil.snackbar(mRootLayout, R.string.activity_login_could_not_read_unsynced_acquisition_data_msg);
            e.printStackTrace();
            return;
        }

        if (acquisitionData.getAcquisitionItemList().isEmpty() &&
                acquisitionData.getAcquisitionList().isEmpty() &&
                acquisitionData.getLogPriceList().isEmpty()) {

            PopupUtil.snackbar(mRootLayout, R.string.activity_login_no_acquisition_data_to_sync);
            return;
        }

        AcquisitionDataService.getInstance().postAcquisitionData(new Callback<AcquisitionData>(this) {
            @Override
            public void onResponse(Response<AcquisitionData> response) {
                if (response.isSuccessful()) {
                    AcquisitionData data = response.body();
                    mDbHelper.markAcquisitionDataAsSynced(data);

                    PopupUtil.snackbar(mRootLayout, R.string.activity_login_successfully_synced_acquisition_data_msg);
                } else {
                    PopupUtil.serviceErrorSnackbar(mRootLayout, response.code());
                    logUnsuccessfulServiceCall(response);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                PopupUtil.serviceUnreachableSnackbar(mRootLayout);
                logFailedServiceCall(throwable);
            }
        }, acquisitionData);
    }

    private void logUnsuccessfulServiceCall(Response response) {
        try {
            Log.e(TAG, "Response body: " + response.errorBody().string());
        } catch (IOException e) {
            Log.e(TAG, "Could not read the body of a failed service request", e);
        }
    }

    private void logFailedServiceCall(Throwable throwable) {
        Log.e(TAG, "Could not reach service", throwable);
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
