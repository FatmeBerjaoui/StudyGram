package com.example.studygram;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.studygram.models.Post;
import com.example.studygram.adapters.FeedAdapter;

public class
ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // View Binding verbindet das Layout mit dem Code
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Username aus der Email ableiten (Teil vor dem @-Zeichen) und anzeigen
        if (user != null) {
            String displayName = user.getDisplayName();

            if (displayName != null && !displayName.isEmpty()) {
                binding.tvUsername.setText(displayName);
            } else {
                String email = user.getEmail();
                int atIndex = email.indexOf("@");
                String username = email.substring(0, atIndex);
                binding.tvUsername.setText(username);
            }
        }

// Klick auf "Saved Posts" führt zum Saved-Posts-Screen
        binding.btnSavedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SavedPostActivity.class);
                startActivity(intent);
            }
        });

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        setupRecyclerView();

    }
    // Test-Liste, um zu prüfen ob der RecyclerView funktioniert
    private void setupRecyclerView() {
        List<Post> postList= new ArrayList<>();
        FeedAdapter adapter = new FeedAdapter(postList);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPosts.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }
        String currentUserId = currentUser.getUid();

        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (var document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                post.setPostId(document.getId());
                                postList.add(post);
                            }
                            adapter.notifyDataSetChanged();
                            binding.tvPosts.setText("Posts (" + postList.size() + ")");
                            
                            if (postList.isEmpty()) {
                                binding.tvEmptyMessage.setVisibility(View.VISIBLE);
                            } else {
                                binding.tvEmptyMessage.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Posts konnten nicht geladen werden", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}