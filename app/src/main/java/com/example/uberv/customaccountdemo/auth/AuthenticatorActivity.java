package com.example.uberv.customaccountdemo.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.uberv.customaccountdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    // Must match registered account type in res/xml/authenticator.xml
    public static final String PARAM_ACCOUNT_TYPE = "com.example.uberv.customaccountdemo";
    /**
     * Determines account type
     */
    public static final String ARG_ACCOUNT_TYPE = "arg_account_type";
    /**
     * Determines auth-token type
     */
    public static final String ARG_AUTH_TYPE = "arg_auth_type";
    /**
     * Indicates whether we are registering a new account or signing-in existing one
     */
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "arg_is_adding_new_account";
    private static final String ARG_USER_PASS = "arg_user_pass";
    public static final String ARG_USER = "arg_user";
    public static final String AUTH_TYPE_JWT = "AUTH_TYPE_JWT";

    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.email_et)
    EditText mEmailEt;
    @BindView(R.id.password_et)
    EditText mPasswordEt;

    private String mAuthTokenType;
    private AccountManager mAccountManager;
    private boolean mIsAddingNewAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        ButterKnife.bind(this);

        mAccountManager = AccountManager.get(this);

        Intent intent = getIntent();
        if (intent != null) {
            mAuthTokenType = intent.getStringExtra(ARG_AUTH_TYPE);
            mIsAddingNewAccount = intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        }

        if (mIsAddingNewAccount) {
            mLoginBtn.setText("Register");
        }
    }

    @OnClick(R.id.login_btn)
    public void onSubmit() {
        final String email = mEmailEt.getText().toString();
        final String password = mPasswordEt.getText().toString();

        // TODO validate input

        // Login to back-end
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                // TODO implement
//                String authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                // TODO mock api call
                String authtoken = "XYZ123";
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
                res.putExtra(ARG_USER_PASS, password);

                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(intent);
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        Timber.d("Finishing login");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(ARG_USER_PASS);

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (mIsAddingNewAccount) {
            // Request was to create a new account
            Timber.d("Registering new account for " + accountName);
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Timber.d("Updating password for account " + account);
            mAccountManager.setPassword(account, accountPassword);
        }

        // Return information back to the account Authenticator
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
