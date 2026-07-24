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
        // View Binding verbindet das Layout mit dem Code, statt findViewById für jedes Element
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // "Register"-Link unterstreichen, damit er als Link erkennbar ist
        binding.tvGoToRegister.setPaintFlags(binding.tvGoToRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Firebase Auth Instanz holen, um Login-Anfragen zu stellen
        mAuth = FirebaseAuth.getInstance();

        // Klick auf Login-Button startet den Login-Vorgang
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Klick auf "Register" führt zum Registrierungs-Screen
        binding.tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Prüft die Eingaben und versucht den Login bei Firebase
    private void loginUser() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();

        // Eingabeprüfung, bevor wir überhaupt zu Firebase schicken
        if (email.isEmpty()) {
            binding.etEmail.setError("Email wird benötigt");
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Passwort wird benötigt");
            return;
        }

        // Login-Anfrage an Firebase, Antwort kommt asynchron zurück
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Nur eingeloggt bleiben, wenn die Email-Adresse bestätigt wurde
                    if (user != null && user.isEmailVerified()) {
                        goToMainActivity();

                        // Falls Email nicht bestätigt: sofort wieder ausloggen, damit man nicht "halb eingeloggt" bleibt
                    } else {
                        mAuth.signOut();
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText("Bitte bestätige zuerst deine Email-Adresse");
                    }

                    // Login fehlgeschlagen -> je nach genauem Fehlertyp passende Meldung anzeigen
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

    // Wechselt nach erfolgreichem Login zum Hauptbereich der App
    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Verhindert, dass man mit "Zurück" wieder zum Login kommt
        startActivity(intent);
        finish();
    }
}
