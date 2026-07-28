package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

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
    private long startZeit;
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
        binding.btnNext.setOnClickListener(v -> {

            if (quizFragen.isEmpty()) {
                return;
            }

            String eingabe = binding.etAnswer.getText().toString().trim();

            QuizQuestion frage = quizFragen.get(aktuelleFrage);

            if (eingabe.equalsIgnoreCase(frage.getAntwort())) {

                richtigeAntworten++;

                Toast.makeText(getContext(),
                        "✅ Bravo! Die Antwort ist richtig.",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getContext(),
                        "❌ Falsch.\nRichtige Antwort: "
                                + frage.getAntwort(),
                        Toast.LENGTH_LONG).show();

                Map<String, Object> wrongQuestion = new HashMap<>();

                wrongQuestion.put("userId", currentUser.getUid());
                wrongQuestion.put("frage", frage.getFrage());
                wrongQuestion.put("antwort", frage.getAntwort());

                db.collection("wrongQuestions")
                        .add(wrongQuestion);

            }

            aktuelleFrage++;

            zeigeFrage();

        });

        binding.btnBack.setOnClickListener(v -> {
            androidx.navigation.fragment.NavHostFragment.findNavController(this)
                    .popBackStack();
        });

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void ladeLikedQuiz() {

        quizFragen.clear();

        db.collection("likedPosts")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot likedDoc : queryDocumentSnapshots.getDocuments()) {

                        String postId = likedDoc.getString("postId");

                        if (postId == null) {
                            continue;
                        }

                        db.collection("posts")
                                .document(postId)
                                .get()
                                .addOnSuccessListener(postDocument -> {

                                    if (!postDocument.exists()) {
                                        return;
                                    }

                                    ArrayList<?> fragen =
                                            (ArrayList<?>) postDocument.get("quizFragen");

                                    if (fragen != null) {

                                        for (Object obj : fragen) {

                                            if (obj instanceof java.util.Map) {

                                                java.util.Map<?, ?> map =
                                                        (java.util.Map<?, ?>) obj;

                                                String frage =
                                                        (String) map.get("frage");

                                                String antwort =
                                                        (String) map.get("antwort");

                                                quizFragen.add(
                                                        new QuizQuestion(frage, antwort)
                                                );
                                            }
                                        }
                                    }

                                    if (!quizFragen.isEmpty()) {

                                        Collections.shuffle(quizFragen);

                                        if (quizFragen.size() > 10) {

                                            quizFragen = new ArrayList<>(
                                                    quizFragen.subList(0, 10)
                                            );
                                        }

                                        startZeit = System.currentTimeMillis();
                                        zeigeFrage();

                                    }

                                });

                    }

                });

    }
    private void zeigeFrage() {

        if (aktuelleFrage >= quizFragen.size()) {

            long dauerMillis = System.currentTimeMillis() - startZeit;

            long sekunden = dauerMillis / 1000;
            long minuten = sekunden / 60;
            sekunden = sekunden % 60;

            int falscheAntworten = quizFragen.size() - richtigeAntworten;

            int score = (int) (((double) richtigeAntworten / quizFragen.size()) * 100);

            binding.tvQuestion.setText(
                    "🎉 Quiz beendet!\n\n" +
                            "Fragen: " + quizFragen.size() + "\n\n" +
                            "Richtig: " + richtigeAntworten + "\n" +
                            "Falsch: " + falscheAntworten + "\n\n" +
                            "Score: " + score + "%\n\n" +
                            "Dauer: " + String.format("%02d:%02d", minuten, sekunden)
            );

            binding.tvQuestionNumber.setText("");

            binding.btnNext.setEnabled(false);
            binding.etAnswer.setEnabled(false);

            return;
        }

        QuizQuestion frage = quizFragen.get(aktuelleFrage);

        binding.tvQuestionNumber.setText(
                "Frage "
                        + (aktuelleFrage + 1)
                        + " von "
                        + quizFragen.size());

        binding.tvQuestion.setText(frage.getFrage());

        binding.etAnswer.setText("");

    }

    private void ladeSavedQuiz() {

        quizFragen.clear();

        db.collection("savedPosts")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot savedDoc : queryDocumentSnapshots.getDocuments()) {

                        String postId = savedDoc.getString("postId");

                        if (postId == null) {
                            continue;
                        }

                        db.collection("posts")
                                .document(postId)
                                .get()
                                .addOnSuccessListener(postDocument -> {

                                    if (!postDocument.exists()) {
                                        return;
                                    }

                                    ArrayList<?> fragen =
                                            (ArrayList<?>) postDocument.get("quizFragen");

                                    if (fragen != null) {

                                        for (Object obj : fragen) {

                                            if (obj instanceof java.util.Map) {

                                                java.util.Map<?, ?> map =
                                                        (java.util.Map<?, ?>) obj;

                                                String frage =
                                                        (String) map.get("frage");

                                                String antwort =
                                                        (String) map.get("antwort");

                                                quizFragen.add(
                                                        new QuizQuestion(frage, antwort)
                                                );
                                            }
                                        }
                                    }

                                    if (!quizFragen.isEmpty()) {

                                        Collections.shuffle(quizFragen);

                                        if (quizFragen.size() > 10) {

                                            quizFragen = new ArrayList<>(
                                                    quizFragen.subList(0, 10)
                                            );
                                        }

                                        startZeit = System.currentTimeMillis();
                                        zeigeFrage();
                                    }

                                });

                    }

                });

    }

    private void ladeWrongQuiz() {

        Toast.makeText(getContext(),
                "Wrong Quiz folgt noch.",
                Toast.LENGTH_SHORT).show();

    }

}