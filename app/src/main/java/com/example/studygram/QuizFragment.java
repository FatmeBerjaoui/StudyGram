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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentQuizBinding.inflate(inflater, container, false);

        binding.btnLikedQuiz.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("quizType", "liked");

            NavHostFragment.findNavController(this)
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