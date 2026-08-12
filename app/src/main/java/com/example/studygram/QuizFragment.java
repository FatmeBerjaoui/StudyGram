package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.studygram.databinding.FragmentQuizBinding;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater,// Lädt/erstellt die XML-Oberfläche
                             ViewGroup container,            // Übergeordneter Container des Fragments
                             Bundle savedInstanceState) {   // Kann gespeicherte Zustände enthalten

        binding = FragmentQuizBinding.inflate(inflater, container, false);

        binding.btnLikedQuiz.setOnClickListener(v -> {

            Bundle bundle = new Bundle(); // Bundle = Container, um Daten an das nächste Fragment zu übergeben
            bundle.putString("quizType", "liked"); // Wir speichern den Quiz-Typ "liked" im Bundle, Schlüssel: quizType, Wert: liked

            NavHostFragment.findNavController(this) // Navigation vom QuizFragment zum QuizGameFragment mit Bundle
                    .navigate(R.id.action_QuizFragment_to_QuizGameFragment, bundle);

        });

        binding.btnSavedQuiz.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("quizType", "saved");

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_QuizFragment_to_QuizGameFragment, bundle);

        });

        binding.btnWrongQuiz.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("quizType", "wrong");

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_QuizFragment_to_QuizGameFragment, bundle);

        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}