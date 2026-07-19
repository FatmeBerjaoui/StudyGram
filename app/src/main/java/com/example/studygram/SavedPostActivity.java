package com.example.studygram;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivitySavedPostsBinding;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.studygram.adapters.FeedAdapter;
import com.example.studygram.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;
public class SavedPostActivity extends AppCompatActivity {
    private ActivitySavedPostsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private void loadSavedPosts() {
        List<Post> savedPostsList = new ArrayList<>();
        FeedAdapter adapter = new FeedAdapter(savedPostsList);
        binding.rvSavedPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSavedPosts.setAdapter(adapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String currentUserId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Schritt 1: alle savedPosts-Einträge dieses Users holen
        db.collection("savedPosts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> savedTask) {
                        if (savedTask.isSuccessful()) {
                            List<String> postIds = new ArrayList<>();

                            for (QueryDocumentSnapshot document : savedTask.getResult()) {
                                String postId = document.getString("postId");
                                postIds.add(postId);
                            }

                            loadPostsByIds(postIds, savedPostsList, adapter);
                        } else {
                            Toast.makeText(SavedPostActivity.this, "Gespeicherte Posts konnten nicht geladen werden", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadPostsByIds(List<String> postIds, List<Post> savedPostsList, FeedAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String postId : postIds) {
            db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
                @Override
                public void onComplete(Task<com.google.firebase.firestore.DocumentSnapshot> postTask) {
                    if (postTask.isSuccessful() && postTask.getResult().exists()) {
                        Post post = postTask.getResult().toObject(Post.class);
                        savedPostsList.add(post);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
