package com.example.studygram;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            int atIndex = email.indexOf("@");
            String username = email.substring(0, atIndex);
            binding.tvUsername.setText(user.getEmail());
        }

        binding.btnSavedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Saved Posts Screen kommt später
                Toast.makeText(ProfileActivity.this, "Saved Posts kommt bald", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Settings Screen kommt später
                Toast.makeText(ProfileActivity.this, "Settings kommt bald", Toast.LENGTH_SHORT).show();
            }
        });
        // Test-Liste, um zu prüfen ob der RecyclerView funktioniert
        List<Post> testPosts = new ArrayList<>();
        testPosts.add(new Post("Testpost 1", "Mathe", "Beschreibung 1", "", ""));
        testPosts.add(new Post("Testpost 2", "Englisch", "Beschreibung 2", "", ""));
        testPosts.add(new Post("Testpost 3", "Biologie", "Beschreibung 3", "", ""));

        PostAdapter adapter = new PostAdapter(testPosts);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPosts.setAdapter(adapter);
    }
}