package es.ucm.myconference.accountmanager;


import es.ucm.myconference.Login;
import es.ucm.myconference.util.Constants;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


public class Authenticator extends AbstractAccountAuthenticator {

	private Context context;
	private final String TAG = "Authenticator";
	// Simple constructor
    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

	@Override
	public Bundle addAccount( AccountAuthenticatorResponse response, String accountType, String authTokenType, 
						String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		Log.d(TAG, "addAccount()");
		
		final Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("ACCOUNT_TYPE", accountType);
		//intent.putExtra("AUTHTOKEN_TYPE", authTokenType);
		//intent.putExtra("ADDING_ACCOUNT", true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
            					Bundle options) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken()");
        
    	
    	// If the caller requested an authToken type we don't support, then return an error
        if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid authTokenType");
            return result;
        }
        
     // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
    	final AccountManager am = AccountManager.get(context);
    	String authToken = am.peekAuthToken(account, authTokenType);
    	
    	// Lets give another try to authenticate the user
    	if(TextUtils.isEmpty(authToken)){
    		final String password = am.getPassword(account);
    		if(password != null){
    			// call login method
    			Login login = new Login(context, account.name, password);
    			try {
					authToken = login.userLogin();
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	// If we get an authToken - we return it
    	if(!TextUtils.isEmpty(authToken)){
    		final Bundle result = new Bundle();
    		result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
    		result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
    		result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
    		return result;
    	}
    	
    	// If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
    	final Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("ACCOUNT_NAME", account.name);
    	intent.putExtra("ACCOUNT_TYPE", account.type);
		intent.putExtra("AUTHTOKEN_TYPE", authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    	final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    	
    	return bundle;
    }

	// Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse r,  Account account,
            						Bundle bundle) throws NetworkErrorException {
        return null;
    }

	// Editing properties is not supported
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
		throw new UnsupportedOperationException();
	}

    // Getting a label for the auth token is not supported
    @Override
    public String getAuthTokenLabel(String s) {
        throw new UnsupportedOperationException();
    }

    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse r, Account account, String[] strings) 
    							throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

	// Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse r, Account account, String s, 
    								Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
