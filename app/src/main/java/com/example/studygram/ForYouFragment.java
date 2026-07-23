package com.example.studygram;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studygram.adapters.FeedAdapter;
import com.example.studygram.models.Post;

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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentForYouBinding.inflate(inflater, container, false);
        posts = new ArrayList<>();

        posts.add(new Post(
                "",
                "Max Müller",
                "Mathe",
                "Analysis",
                "Integralrechnung erklärt",
                "",
                25));

        posts.add(new Post(
                "",
                "Sarah",
                "Java",
                "Programmierung",
                "Klassen und Objekte",
                "",
                16));

        filteredPosts = new ArrayList<>(posts);
        adapter = new FeedAdapter(filteredPosts);


        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPosts.setAdapter(adapter);

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