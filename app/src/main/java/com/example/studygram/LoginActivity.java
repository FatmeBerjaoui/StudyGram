package com.example.studygram;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvGoToRegister.setPaintFlags(binding.tvGoToRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        binding.tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null && user.isEmailVerified()) {
                goToMainActivity();
            } else {
                mAuth.signOut();
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText("Bitte bestätige zuerst deine Email-Adresse");
            }
        } else {

        }

        if (email.isEmpty()) {
            binding.etEmail.setError("Email wird benötigt");
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Passwort wird benötigt");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    goToMainActivity();
                } else {
                    binding.tvError.setVisibility(View.VISIBLE);
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        binding.tvError.setText("Email oder Passwort falsch");
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        binding.tvError.setText("Kein Account mit dieser Email gefunden");
                    } else {
                        binding.tvError.setText("Login fehlgeschlagen");
                    }
                }
            }
        });

    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Verhindert, dass man mit "Zurück" wieder zum Login kommt
        startActivity(intent);
        finish();
    }
}
