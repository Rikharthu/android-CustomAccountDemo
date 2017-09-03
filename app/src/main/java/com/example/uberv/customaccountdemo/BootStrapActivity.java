package com.example.uberv.customaccountdemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.uberv.customaccountdemo.auth.AuthenticatorActivity;

import timber.log.Timber;

public class BootStrapActivity extends AppCompatActivity {

    private static final int NEW_ACCOUNT = 1000;
    private static final int EXISTING_ACCOUNT = 1001;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootstrap);


        // TODO simulate loading
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAccounts();
            }
        }, 3000);

    }

    private void checkAccounts() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager
                .getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

        if (accounts.length == 0) {
            // No account exists on the system yet
            Timber.d("No accounts of type " + AuthenticatorActivity.PARAM_ACCOUNT_TYPE + " exist on this device yet");

            // Create a new account
            final Intent i = new Intent(this, AuthenticatorActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            i.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            i.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE,AuthenticatorActivity.AUTH_TYPE_JWT);
            startActivityForResult(i, NEW_ACCOUNT);
        } else {
            // There is an existing account
            Timber.d("There is an existing account(s)");
            // TODO create an account chooser or use shared prefs to select last used one
            String password = mAccountManager.getPassword(accounts[0]);
            if (password == null) {
                Timber.d("Could not retrieve password for account " + accounts[0].name + ", starting AuthenticatorActivity");
                final Intent i = new Intent(this, AuthenticatorActivity.class);
                i.putExtra(AuthenticatorActivity.ARG_USER, accounts[0].name);
                startActivityForResult(i, EXISTING_ACCOUNT);
            } else {
//                startActivityForResult(new Intent(this,MainActivity.class));
                // TODO proceed with launch
                // finish();
                Timber.d("OK, can proceed");
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        }
    }
}
