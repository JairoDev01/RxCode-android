package com.gt.rxcode.learn.ui.activitis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gt.rxcode.learn.R;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private EditText txtEmail;
    private EditText txtPassword;
    private TextInputLayout lyInputEmail;
    private TextInputLayout lyInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        lyInputEmail = (TextInputLayout)findViewById(R.id.input_user_name);
        lyInputPassword = (TextInputLayout)findViewById(R.id.input_password);
        txtEmail = (EditText) findViewById(R.id.username);
        txtPassword = (EditText)findViewById(R.id.password);
        Button btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();
                    // FirebaseUser.getToken() instead.
                    String uid = user.getUid();
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.e(TAG, "Email" + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // [START_EXCLUDE]
            handleGoogleSignInResult(result);
            // [END_EXCLUDE]
        }
    }
    private boolean esCorreoValido(String correo) {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            lyInputEmail.setError("Correo electrónico inválido");
            return false;
        } else {
            lyInputEmail.setError(null);
        }

        return true;
    }

    private boolean validadTelefono(String telefono){
        return Patterns.PHONE.matcher(telefono).matches();
    }
    private boolean validarNombre(String nombre){
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        return patron.matcher(nombre).matches() || nombre.length() > 30;
    }
    private void validarDatos() {
        String correo = lyInputEmail.getEditText().getText().toString().trim();
        String pass = lyInputPassword.getEditText().getText().toString().trim();
        //String nombre = tilNombre.getEditText().getText().toString();
        //String telefono = tilTelefono.getEditText().getText().toString();


        boolean a = esCorreoValido(correo);
       // boolean b = esTelefonoValido(telefono);

        /*if (a && b) {
            // OK, se pasa a la siguiente acción
            Toast.makeText(this, "Se guarda el registro", Toast.LENGTH_LONG).show();
        }*/

    }
    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            //txtEmail.setErrorEnabled(true);
            txtEmail.setError(getString(R.string.required));
            valid = false;
        } else {
            //lyInputEmail.setErrorEnabled(true);
            txtEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            //lyInputPassword.setErrorEnabled(true);
            txtPassword.setError(getString(R.string.required));
            //lyPassword.requestFocus();
            valid = false;
        } else if (password.length() < 6) {
            //lyInputPassword.setErrorEnabled(true);
            txtPassword.setError(getString(R.string.error_weak_password));
            //lyPassword.requestFocus();
            valid = false;
        } else {
            //lyInputPassword.setErrorEnabled(true);
            txtPassword.setError(null);
        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void login() {
        String email = null;
        String password = null;
        try {
            email = txtEmail.getText().toString().trim();
            password = txtPassword.getText().toString().trim();
            validateForm(email,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(Login.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(null);
                    }
                });
    }
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.getStatus());
        if (result.isSuccess()) {
            // Successful Google sign in, authenticate with Firebase.
            GoogleSignInAccount acct = result.getSignInAccount();
            // firebaseAuthWithGoogle(acct);
            //mStatusTextView.setText( acct.getEmail());

        } else {
            // Unsuccessful Google Sign In, show signed-out UI
            //updateUI(null);
        }
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            //mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            //findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            //findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


}
