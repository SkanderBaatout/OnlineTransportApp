package com.skander.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private EditText phoneEditText;
    private CountryCodePicker ccp;
    private PinView firstPinView;
    private ConstraintLayout phoneLayout;
    private String selected_country_code ="+216";
    private static final int CREDENTIAL_PICKER_REQUEST =120 ;
    private ProgressBar progressBar;
    //firebase auth number
    private String mVerificationId ;
    private PhoneAuthProvider.ForceResendingToken mResentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        phoneEditText=(EditText) findViewById(R.id.editTextTextPersonName);
        firstPinView=(PinView) findViewById(R.id.firstPinView);
        phoneLayout=(ConstraintLayout)findViewById(R.id.phoneLayout);
        ccp=(CountryCodePicker) findViewById(R.id.ccp);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage
                selected_country_code=ccp.getSelectedCountryCodeWithPlus();
            }
        });
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().length()==8){
                   // Toast.makeText(PhoneLoginActivity.this,"hello",Toast.LENGTH_SHORT).show();
                  //  phoneLayout.setVisibility(View.GONE);
                    //firstPinView.setVisibility(View.VISIBLE);
                    sendOtp();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().length()==6){
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential  = PhoneAuthProvider.getCredential(mVerificationId,firstPinView.getText().toString().trim());
                    signInWithAuthCredentials(credential);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();


        PendingIntent intent = Credentials.getClient(PhoneLoginActivity.this).getHintPickerIntent(hintRequest);
        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {
            e.printStackTrace();
        }
        //callbacks
        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null){
                    firstPinView.setText(code);
                    signInWithAuthCredentials(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.VISIBLE);
                firstPinView.setVisibility(View.GONE);



            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId=verificationId;
                mResentToken=token;
                Toast.makeText(PhoneLoginActivity.this,"code sent successfully",Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.GONE);
                firstPinView.setVisibility(View.VISIBLE);
            }
        };


        // calbacks
    }


    private void sendOtp() {
        progressBar.setVisibility(View.VISIBLE);
        String phoneNumber = selected_country_code+phoneEditText.getText().toString();
        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(mAuth)
                .setTimeout(60L, TimeUnit.SECONDS)
                        .setPhoneNumber(phoneNumber)
                .setActivity(PhoneLoginActivity.this)
                        .setCallbacks(callBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            /* EditText.setText(credentials.getId().substring(3));*/ //get the selected phone number
                //Do what ever you want to do with your selected phone number here

          //  Toast.makeText(this, "MOB"+credentials.getId().substring(3), Toast.LENGTH_SHORT).show();
            phoneEditText.setText(credentials.getId().substring(3));


        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            Toast.makeText(PhoneLoginActivity.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }


    }
    private void signInWithAuthCredentials(PhoneAuthCredential credentials) {
        mAuth.signInWithCredential(credentials)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneLoginActivity.this, "Logged successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PhoneLoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(PhoneLoginActivity.this, "LogIn failed.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PhoneLoginActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

}