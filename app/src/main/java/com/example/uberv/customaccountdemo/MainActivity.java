package com.example.uberv.customaccountdemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.uberv.customaccountdemo.auth.AuthenticatorActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager
                .getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

        Account account = accounts[0];
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, AuthenticatorActivity.AUTH_TYPE_JWT, null, this, null, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bundle = future.getResult();
                    final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    Timber.d("Auth token is: " + authtoken);

                    mAccountManager.invalidateAuthToken(AuthenticatorActivity.PARAM_ACCOUNT_TYPE,authtoken);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }).start();


        Timber.d(accounts.toString());
    }
}
