package com.example.studygram;
import android.graphics.Paint;
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

        // View Binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());  //binding objekt: xml LAyout in Java obj
        setContentView(binding.getRoot()); //Layout auf Bildschirm anzeigen

        binding.tvLogin.setPaintFlags(binding.tvLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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
                // Beenden der aktuellen Activity, Rückkehr zum Login
                finish();
            }
        });
    }

    // Prüft die Eingaben und legt bei Erfolg einen neuen Firebase-Account an
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

        // Bei Firebase registrieren
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // wenn Erfolgreiche Erstellung
                    FirebaseUser user = mAuth.getCurrentUser();
                    sendVerificationEmail(user);
                } else {
                    // Fehlgeschlagen: Anzeige der Fehlermeldung
                    binding.tvError.setVisibility(View.VISIBLE);
                    Exception e = task.getException();

                    if (e instanceof FirebaseAuthUserCollisionException) {
                        // Diese Email ist bereits als Account registriert
                        binding.tvError.setText("Dieser Account existiert bereits");
                    } else if (e instanceof FirebaseAuthWeakPasswordException) {
                        // Firebase selbst stuft das Passwort als zu unsicher ein
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


    private void sendVerificationEmail(FirebaseUser user) {
        //sendVerificationEmail : schickt linkt an den User
        //Dieser Befehl kommt beim FIrebase User an und Firebase generiert den Link und setzt ihn in eine Mail Vorlage
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    // Information an den Nutzer über den Versand der E-Mail
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText("Erfolgreich registriert! Bitte bestätige deine Email.");
                }
            }
        });
    }

}
