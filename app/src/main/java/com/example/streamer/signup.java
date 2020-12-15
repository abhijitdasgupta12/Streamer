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

public class signup extends AppCompatActivity
{
    TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    ProgressBar progressBar;
    TextView demo;

    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        emailTextInputLayout=findViewById(R.id.email_login);
        passwordTextInputLayout=findViewById(R.id.password_login);
        progressBar=findViewById(R.id.progressBar);
        demo=findViewById(R.id.textView2);

        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), index.class));
            }
        });
    }

    public void signUp(View view)
    {
        progressBar.setVisibility(View.VISIBLE);

        final String SignupEmail = emailTextInputLayout.getEditText().getText().toString();
        String SignupPassword = passwordTextInputLayout.getEditText().getText().toString();

        mAuth = FirebaseAuth.getInstance();

        if (SignupEmail.isEmpty() || SignupPassword.isEmpty()) //Fields can not be empty
        {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
        }
        if (SignupPassword.length() < 8) //Password should be at least 8 characters long
        {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(SignupEmail,SignupPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                emailTextInputLayout.getEditText().setText("");
                                passwordTextInputLayout.getEditText().setText("");

                                Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(),index.class);
                                startActivity(intent);

                                finish();
                            }
                            else
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}