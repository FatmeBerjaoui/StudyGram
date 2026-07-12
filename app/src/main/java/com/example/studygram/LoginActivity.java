package com.example.studygram;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity{
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding statt findViewById
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Auth Instanz holen
        mAuth = FirebaseAuth.getInstance();

        // Login-Button Klick
        binding.tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Register-Screen kommt später
                Toast.makeText(LoginActivity.this, "Register kommt bald", Toast.LENGTH_SHORT).show();
            }
        });

        // Klick auf "Noch kein Konto? Register hier"
        binding.tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Einfache Eingabeprüfung
        if (email.isEmpty()) {
            binding.etEmail.setError("Email wird benötigt");
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Passwort wird benötigt");
            return;
        }

        // Firebase Login-Anfrage
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login erfolgreich
                        FirebaseUser user = mAuth.getCurrentUser();
                        goToMainActivity();
                    } else {
                        // Login fehlgeschlagen -> Fehlermeldung anzeigen
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText("Login fehlgeschlagen: Email oder Passwort falsch");
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Verhindert, dass man mit "Zurück" wieder zum Login kommt
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
