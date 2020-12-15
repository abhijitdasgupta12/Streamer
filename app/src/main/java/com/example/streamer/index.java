package com.example.streamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class index extends AppCompatActivity
{
    TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    ProgressBar progressBar;
    TextView goToSignUpPage;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Typecasting
        emailTextInputLayout= findViewById(R.id.email_login);
        passwordTextInputLayout= findViewById(R.id.password_login);
        progressBar= findViewById(R.id.progressBar);
        goToSignUpPage= findViewById(R.id.textView2);

        //Redirecting to signup page
        goToSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(index.this, signup.class));
            }
        });

        //Checking for the availability of last logged in user for auto login
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
        {
            Intent intent= new Intent(index.this, dashboard.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Welcome back "+firebaseUser.getEmail()+"!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void logIn(View view)
    {
        progressBar.setVisibility(View.VISIBLE);

        mAuth= FirebaseAuth.getInstance();

        final String LoginEmail= emailTextInputLayout.getEditText().getText().toString();
        String LoginPassword= passwordTextInputLayout.getEditText().getText().toString();

        if (LoginEmail.isEmpty() || LoginPassword.isEmpty()) //Fields can not be empty
        {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
        }
        if (LoginPassword.length() < 8) //Password should be at least 8 characters long
        {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(LoginEmail,LoginPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                emailTextInputLayout.getEditText().setText("");
                                passwordTextInputLayout.getEditText().setText("");

                                Toast.makeText(getApplicationContext(), "Welcome back "+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                                intent.putExtra("email",mAuth.getCurrentUser().getEmail());
                                intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                                startActivity(intent);

                                finish();
                            }
                            else
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}
