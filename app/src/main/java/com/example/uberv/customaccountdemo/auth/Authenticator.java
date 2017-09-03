package com.example.uberv.customaccountdemo.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.uberv.customaccountdemo.App;
import com.example.uberv.customaccountdemo.api.models.AuthData;
import com.example.uberv.customaccountdemo.api.models.UserCredentials;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Gets addressed by {@link android.accounts.AccountManager} to fulfill all account relevant tasks:
 * <ol>
 * <li>Getting stored auth-token</li>
 * <li>Presenting the account log-in screen</li>
 * <li>Handling the user authentication against the server</li>
 * </ol>
 * <p>
 * Most it's methods return a {@link Bundle} that must containt an Intent that will be used
 * to launch your custom authenticator activity as well as provide and special initialization providers
 * </p>
 * <p>
 * To use Authenticator you need to add an intent filter for android.accounts.AccountAuthenticator"
 * to your app manifest and supply XML resources that define name of custom account type that
 * this Authenticator handles and icon that the system will display next to accoutns of this type
 * </p>
 * <br/>
 * More info: {@link AbstractAccountAuthenticator}
 */
public class Authenticator extends AbstractAccountAuthenticator {

    public static final List<String> SUPPORTED_TOKEN_TYPES = Arrays.asList(AuthenticatorActivity.AUTH_TYPE_JWT, "oauth");

    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType) {
        Timber.d("editing properties for account type: " + accountType);
        return null;
    }

    /**
     * Called when the user wants to log-in and add a new account to the device or by AccountManager#addAccount(...).
     * <br/>
     * We need to return a Bundle with the Intent to start our AccountAuthenticatorActivity.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Timber.d(String.format("Adding account of type %s, authTokenType=%s, requiredFeatures=%s", accountType, authTokenType, requiredFeatures.toString()));

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        // We are creating a new account
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        // Pack intent into a bundle
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    /**
     *
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Timber.d("Getting auth-token of type " + authTokenType + " for account " + account.name);

        // Check whether requested auth-token type is supported
        if (!SUPPORTED_TOKEN_TYPES.contains(authTokenType)) {
            // auth-token type is not supported
            Timber.d("Auth-token of type " + authTokenType + " is not supported");
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid auth token type");

            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        // check whether there is an existing cached auth-token
        String authToken = am.peekAuthToken(account, authTokenType);

        // Try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String username = account.name;
            final String password = am.getPassword(account);
            if (password != null) {
                // TODO ask backend for auth token using passed credentials
                Timber.d("Requesting auth token for " + username);
                // TODO mock token
//                authToken = "XYZ123";

                UserCredentials credentials = new UserCredentials(account.name, password);
                try {
                    Response<AuthData> apiResponse = App.getApiService().debugAuthenticate().execute();
                    authToken = apiResponse.body().getToken();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity
        Timber.d("Could not access user's password/auth token, starting AuthenticatorActivity");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
        /*
        If the auth-token we got from this method is not valid anymore, because of time expiration
        or changed password from a different client, you need to invalidate the current auth-token
        on the AccountManager and ask for a token once again. Invalidating the current token is done
        by calling AccountManager#invalidateAuthToken(). The next call to getAuthToken() will try
        to log-in with the stored password and if it fails - the user will have to enter his
        credentials again.
         */
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}
