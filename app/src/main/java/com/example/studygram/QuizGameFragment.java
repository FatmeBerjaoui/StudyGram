package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.studygram.databinding.FragmentQuizGameBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class QuizGameFragment extends Fragment {

    private FragmentQuizGameBinding binding;

    private ArrayList<QuizQuestion> quizFragen = new ArrayList<>();

    private int aktuelleFrage = 0;
    private int richtigeAntworten = 0;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;

    private String quizType;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuizGameBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        quizType = getArguments().getString("quizType");

        if (quizType.equals("liked")) {

            ladeLikedQuiz();

        } else if (quizType.equals("saved")) {

            ladeSavedQuiz();

        } else {

            ladeWrongQuiz();

        }
        Collections.shuffle(quizFragen);

        if (quizFragen.size() > 10) {

            quizFragen =
                    new ArrayList<>(quizFragen.subList(0, 10));

        }

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void ladeLikedQuiz() {

    }

    private void ladeSavedQuiz() {

    }

    private void ladeWrongQuiz() {

    }

}