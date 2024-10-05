package com.example.robinblue.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.robinblue.Activity.PatternLockAct;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.robinblue.applockpro.R;

import java.util.concurrent.Executor;

public class ResetPatternLockAct extends AppCompatActivity {

    private Button verfiy_gmailacc,verify_fingerprint;
    private TextView entergamil;

    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pattern_lock);

        // Initialize UI elements
        verfiy_gmailacc = findViewById(R.id.verify_gmail);
        entergamil = findViewById(R.id.editText);
        verify_fingerprint = findViewById(R.id.verify_fingerprint);


        verify_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verfiyFingerprint();
            }
        });

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Google Sign-In options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("780102785882-ib2b8b8u62j77jb4icd8di8ligirje79.apps.googleusercontent.com")  // <-- Use your client ID here
                .requestEmail()
                .build();

        // Initialize Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Set listener on EditText click to trigger Google Sign-In
        entergamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Google Sign-In intent
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });

        // Set listener on "Verify" button to authenticate with Google
        verfiy_gmailacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just trigger Google sign-in when the button is clicked
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });
    }

    private void verfiyFingerprint() {


            Executor executor = ContextCompat.getMainExecutor(this);
            androidx.biometric.BiometricPrompt biometricPrompt = new androidx.biometric.BiometricPrompt(ResetPatternLockAct.this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    //    Toast.makeText(PatternLockAct.this, "Fingerprint authentication error", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                 Intent intent=new Intent(ResetPatternLockAct.this,PatternLockAct.class);
                 //intent.putExtra("changePattern", true);
                    intent.putExtra("resetPattern", true);
                 startActivity(intent);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    //     Toast.makeText(PatternLockAct.this, "Fingerprint authentication failed", Toast.LENGTH_SHORT).show();
                }
            });

            androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Fingerprint Authentication")
                    .setSubtitle("Unlock the app using your fingerprint")
                    .setNegativeButtonText("Use Pattern")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Get the signed in Google account
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Authenticate with Firebase using Google credentials

                    String gmailtext= account.getEmail();
                    entergamil.setText(gmailtext);
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
                displayToast("Google Sign-In Failed");
            }
        }
    }

    // Authenticate the signed-in Google account with Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign-in successful
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    displayToast("Authentication Successful");
                    // Proceed to reset the pattern lock or navigate to another activity
                    startActivity(new Intent(ResetPatternLockAct.this, PatternLockAct.class));
                } else {
                    // Sign-in failed
                    displayToast("Authentication Failed: " + task.getException().getMessage());
                }
            }
        });
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
