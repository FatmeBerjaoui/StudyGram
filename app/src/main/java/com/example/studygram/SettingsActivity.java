package com.example.studygram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class SettingsActivity extends AppCompatActivity {

    // Verwaltung des View Bindings für den Zugriff auf die UI-Elemente
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            // Klick-Event für die Änderung des Benutzernamens
            public void onClick(View v) {
                String newUsername = binding.etNewUsername.getText().toString();

                if (newUsername.isEmpty()) {
                    binding.etNewUsername.setError("Bitte einen Username eingeben");
                    return;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser == null) {
                    return;
                }
            // Erstellung einer Anfrage zur Profil-Aktualisierung (Name setzen)
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build();

                currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.tvMessage.setVisibility(View.VISIBLE);
                            binding.tvMessage.setText("Username erfolgreich geändert");
                        } else {
                            binding.tvMessage.setVisibility(View.VISIBLE);
                            binding.tvMessage.setText("Fehler beim Ändern des Usernames");
                        }
                    }
                });
            }

        });
        // Klick-Event für das Zurücksetzen des Passworts per E-Mail

        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser == null) {
                    return;
                }

                String email = currentUser.getEmail();
                // Befehl an Firebase, eine Passwort-Reset-Mail zu senden
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.tvMessage.setVisibility(View.VISIBLE);
                            binding.tvMessage.setText("Reset-Mail wurde gesendet. Bitte Postfach prüfen.");
                        } else {
                            binding.tvMessage.setVisibility(View.VISIBLE);
                            binding.tvMessage.setText("Fehler beim Senden der Reset-Mail");
                        }
                    }
                });
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser == null) {
                    return;
                }

                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            binding.tvMessage.setVisibility(View.VISIBLE);
                            binding.tvMessage.setText("Account konnte nicht gelöscht werden");
                        }
                    }
                });
            }
        });
    }
}