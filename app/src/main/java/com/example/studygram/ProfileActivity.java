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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
            binding.tvUsername.setText(username);
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
        setupRecyclerView();

    }
    // Test-Liste, um zu prüfen ob der RecyclerView funktioniert
    private void setupRecyclerView() {
        List<Post> PostList = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(PostList);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPosts.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (Post post : task.getResult().toObjects(Post.class)) {
                                postList.add(post);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Posts konnten nicht geladen werden", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}