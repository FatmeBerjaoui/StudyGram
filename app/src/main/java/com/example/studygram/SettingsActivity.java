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


public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


        });

        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser == null) {
                    return;
                }

                String email = currentUser.getEmail();

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

        binding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this, "xxxx", Toast.LENGTH_SHORT).show();
            }
        });
    }
}