package com.example.studygram;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivitySavedPostsBinding;

import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.studygram.adapters.FeedAdapter;
import com.example.studygram.Post;
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
    // Verwaltung des View Bindings für den Zugriff auf Layout-Elemente
    private ActivitySavedPostsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialisierung des View Bindings und Verknüpfung mit dem Layout
        binding = ActivitySavedPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Aufruf der Methode zum Laden der gespeicherten Beiträge
        loadSavedPosts();

        // Definition des Klick-Events für den Zurück-Button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beenden der Activity und Rückkehr zur vorherigen Seite

                finish();
            }
        });
    }

    // Abruf der Liste aller Posts, die der Nutzer gespeichert hat
    private void loadSavedPosts() {
        // Erstellung der Liste für die Post-Objekte und Initialisierung des Adapters
        List<Post> savedPostsList = new ArrayList<>();
        FeedAdapter adapter = new FeedAdapter(savedPostsList);


        // Konfiguration des RecyclerViews mit einem LayoutManager und dem Adapter
        binding.rvSavedPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSavedPosts.setAdapter(adapter);

        // Abruf des aktuell angemeldeten Nutzers über Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        // Abbruch der Methode, falls kein Nutzer angemeldet ist
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
                            // Erstellung einer Liste für die gefundenen Post-IDs
                            List<String> postIds = new ArrayList<>();

                            // Extraktion der IDs aus den Datenbank-Dokumenten
                            for (QueryDocumentSnapshot document : savedTask.getResult()) {
                                String postId = document.getString("postId");
                                postIds.add(postId);
                            }
                            // Anzeige einer Meldung, falls keine gespeicherten Posts vorhanden sind
                            if (postIds.isEmpty()) {
                                binding.tvEmptyMessage.setVisibility(View.VISIBLE);
                            } else {
                                binding.tvEmptyMessage.setVisibility(View.GONE);
                            }
                            // Aufruf der Methode zum Laden der tatsächlichen Post-Inhalte anhand der
                            loadPostsByIds(postIds, savedPostsList, adapter);
                        } else {
                            // Anzeige einer Fehlermeldung bei fehlgeschlagener Datenbankabfrage
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
                        post.setPostId(postTask.getResult().getId());
                        savedPostsList.add(post);
                        adapter.notifyDataSetChanged(); // Aktualisierung des Adapters wenn einzelner Post erfolgreich nachgeladen
                    }
                }
            });
        }
    }
}
