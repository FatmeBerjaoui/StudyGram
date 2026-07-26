package com.example.studygram;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygram.adapters.FeedAdapter;
import com.example.studygram.models.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.studygram.databinding.FragmentForYouBinding;

public class ForYouFragment extends Fragment {

    private FragmentForYouBinding binding;
    private ArrayList<Post> posts;
    private ArrayList<Post> filteredPosts;
    private FeedAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentForYouBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        posts = new ArrayList<>();
        adapter = new FeedAdapter(posts);

        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPosts.setAdapter(adapter);

        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    posts.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Post post = document.toObject(Post.class);
                        post.setPostId(document.getId());
                        posts.add(post);

                    }

                    adapter.updateList(posts);

                })
                .addOnFailureListener(e -> {

                });

// HIER kommt der TextWatcher

        binding.etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayList<Post> neueListe = new ArrayList<>();

                for (Post post : posts) {

                    String suche = s.toString().toLowerCase();

                    if (post.getTitle().toLowerCase().contains(suche)
                            || post.getSubject().toLowerCase().contains(suche)
                            || post.getDescription().toLowerCase().contains(suche)) {

                        neueListe.add(post);
                    }
                }

                adapter.updateList(neueListe);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}