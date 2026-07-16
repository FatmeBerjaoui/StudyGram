package com.example.studygram;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygram.adapters.FeedAdapter;
import com.example.studygram.models.Post;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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
                "Max Müller",
                "Mathe",
                "Analysis",
                "Integralrechnung erklärt",
                25
        ));

        posts.add(new Post(
                "Sarah",
                "Java",
                "Programmierung",
                "Klassen und Objekte",
                16
        ));

        filteredPosts = new ArrayList<>(posts);
        adapter = new FeedAdapter(filteredPosts);

        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPosts.setAdapter(adapter);
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}