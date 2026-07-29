package com.example.studygram;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studygram.adapters.FeedAdapter;
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

public class ForYouFragment extends Fragment { //erbt alle Funktionen eines Android-Fragments

    private FragmentForYouBinding binding;
    private ArrayList<Post> posts;
    private FeedAdapter adapter; //Adapter gesiepichert
    private FirebaseFirestore db; //Verbindung zur DB

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

                    posts.clear(); //Post Liste geleert

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Post post = document.toObject(Post.class);
                        post.setPostId(document.getId());
                        posts.add(post);

                    }

                    adapter.updateList(posts); //Adapter bekommt komplett neue aktualisierte Liste

                })
                .addOnFailureListener(e -> {  //Fall Fehler aufkommt landet das Programm hier

                });

// HIER kommt der TextWatcher (Suche)

        binding.etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //Methode läuft bei jeder Eingabe eines Buchstaben

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