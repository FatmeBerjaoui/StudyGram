package com.example.studygram;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
public class RegisterActivity  extends AppCompatActivity {


    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding verbindet das Layout mit dem Code
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Auth Instanz holen
        mAuth = FirebaseAuth.getInstance();

        // Klick auf Register-Button startet die Registrierung
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Klick auf "Login" geht zurück zum Login-Screen
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        // Eingaben prüfen, bevor wir zu Firebase schicken
        if (email.isEmpty()) {
            binding.etEmail.setError("Email wird benötigt");
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Passwort wird benötigt");
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Passwort muss mindestens 6 Zeichen haben");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwörter stimmen nicht überein");
            return;
        }

        // Bei Firebase registrieren, Antwort kommt asynchron zurück
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    sendVerificationEmail(user);
                } else {
                    binding.tvError.setVisibility(View.VISIBLE);
                    Exception e = task.getException();

                    if (e instanceof FirebaseAuthUserCollisionException) {
                        binding.tvError.setText("Dieser Account existiert bereits");
                    } else if (e instanceof FirebaseAuthWeakPasswordException) {
                        binding.tvError.setText("Passwort ist zu schwach");
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        binding.tvError.setText("Ungültiges Email-Format");
                    } else {
                        binding.tvError.setText("Registrierung fehlgeschlagen");
                    }
                }
            }
        });
    }

    // Schickt eine Bestätigungsmail an den neu registrierten User
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText("Erfolgreich registriert! Bitte bestätige deine Email.");
                }
            }
        });
    }

}
