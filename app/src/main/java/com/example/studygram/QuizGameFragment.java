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

    private ArrayList<QuizQuestion> quizFragen = new ArrayList<>();  // Hier werden die Quizfragen gespeichert, die gerade gespielt werden

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

        quizType = getArguments().getString("quizType"); // Übergebene Information aus dem vorherigen Fragment holen.

        // Je nach Quiz-Typ wird eine andere Methode zum Laden der Fragen aufgerufen
        if (quizType.equals("liked")) {

            ladeLikedQuiz();

        } else if (quizType.equals("saved")) {

            ladeSavedQuiz();

        } else {

            ladeWrongQuiz();

        }
        binding.btnNext.setOnClickListener(v -> { // Wird ausgeführt, wenn der Benutzer auf "Antwort prüfen" klickt

            if (quizFragen.isEmpty()) {
                return;
            }

            String eingabe = binding.etAnswer.getText().toString().trim();

            QuizQuestion frage = quizFragen.get(aktuelleFrage);

           if (eingabe.equalsIgnoreCase(frage.getAntwort())) { // Benutzerantwort mit der richtigen Antwort vergleichen (inkl. nicht Beachten der Groß-und Kleinschreibung)

                richtigeAntworten++; // Zähler erhöhen

                Toast.makeText(getContext(),
                        "✅ Bravo! Die Antwort ist richtig.",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getContext(),
                        "❌ Falsch.\nRichtige Antwort: "
                                + frage.getAntwort(),
                        Toast.LENGTH_LONG).show();

                Map<String, Object> wrongQuestion = new HashMap<>(); // Map wird verwendet, um die Daten für Firestore zusammenzustellen

                wrongQuestion.put("userId", currentUser.getUid());
                wrongQuestion.put("frage", frage.getFrage());
                wrongQuestion.put("antwort", frage.getAntwort());

                db.collection("wrongQuestions")
                        .add(wrongQuestion);

            }

            aktuelleFrage++; // Wechsel zur nächsten Frage

            zeigeFrage();

        });

        binding.btnBack.setOnClickListener(v -> { // -Button
            androidx.navigation.fragment.NavHostFragment.findNavController(this)
                    .popBackStack(); // popBackStack() geht im Navigationsverlauf eine Seite zurück
        });

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //LIKED QUIZ METHODE
    private void ladeLikedQuiz() {

        quizFragen.clear(); // alte Fragen entfernen

        db.collection("likedPosts")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> { // Es werden nur Likes des aktuell eingeloggten Users geladen + Laden der "likedPost" Collection aus der DB

                    for (DocumentSnapshot likedDoc : queryDocumentSnapshots.getDocuments()) { // Alle gefundenen Dokumente durchlaufen

                        String postId = likedDoc.getString("postId");

                        if (postId == null) { //falls keine Post-ID vorhanden-->überspringen
                            continue;
                        }

                        db.collection("posts")// Mit der Post-ID den eigentlichen Post aus "posts" laden
                                .document(postId)
                                .get()
                                .addOnSuccessListener(postDocument -> {

                                    if (!postDocument.exists()) { //falls Dokument gelöscht wurde passiert nichts
                                        return;
                                    }

                                    ArrayList<?> fragen =
                                            (ArrayList<?>) postDocument.get("quizFragen"); // Quizfragen aus dem Post holen

                                    if (fragen != null) {

                                        for (Object obj : fragen) { // Jede gespeicherte Frage durchlaufen

                                            if (obj instanceof java.util.Map) { //überprüft Datentyp eines Objekts

                                                java.util.Map<?, ?> map = //Objekt in Map umgewandelt
                                                        (java.util.Map<?, ?>) obj;

                                                String frage =
                                                        (String) map.get("frage");

                                                String antwort =
                                                        (String) map.get("antwort");

                                                quizFragen.add(
                                                        new QuizQuestion(frage, antwort) //aus Firestore Daten echtes Objekt
                                                );
                                            }
                                        }
                                    }

                                    if (!quizFragen.isEmpty()) {

                                        Collections.shuffle(quizFragen); // Fragen zufällig mischen

                                        if (quizFragen.size() > 10) { // max 10 Fragen verwenden

                                            quizFragen = new ArrayList<>(
                                                    quizFragen.subList(0, 10)
                                            );
                                        }

                                        startZeit = System.currentTimeMillis(); //speichert wann Quiz beginnt
                                        zeigeFrage();

                                    }

                                });

                    }

                });

    }
    //FRAGE ANZEIGEN
    private void zeigeFrage() {

        if (aktuelleFrage >= quizFragen.size()) { // Wenn keine Fragen mehr übrig sind → Quiz ist beendet

            long dauerMillis = System.currentTimeMillis() - startZeit; // Berechnen, wie lange das Quiz gedauert hat

            long sekunden = dauerMillis / 1000;
            long minuten = sekunden / 60;
            sekunden = sekunden % 60; // Umrechnen

            int falscheAntworten = quizFragen.size() - richtigeAntworten;

            int score = (int) (((double) richtigeAntworten / quizFragen.size()) * 100); // Prozentualen Score berechnen

            // Ergebnis auf Bildschirm anzeigen lassen
            binding.tvQuestion.setText(
                    "🎉 Quiz beendet!\n\n" +
                            "Fragen: " + quizFragen.size() + "\n\n" +
                            "Richtig: " + richtigeAntworten + "\n" +
                            "Falsch: " + falscheAntworten + "\n\n" +
                            "Score: " + score + "%\n\n" +
                            "Dauer: " + String.format("%02d:%02d", minuten, sekunden)
            );

            binding.tvQuestionNumber.setText(""); // "Frage X von Y" entfernen

            binding.btnNext.setEnabled(false);
            binding.etAnswer.setEnabled(false); //Eingabefeld deaktiviert

            return;
        }

        QuizQuestion frage = quizFragen.get(aktuelleFrage);

        binding.tvQuestionNumber.setText( // Anzeige "Frage 1 von 10" aktualisieren
                "Frage "
                        + (aktuelleFrage + 1)
                        + " von "
                        + quizFragen.size());

        binding.tvQuestion.setText(frage.getFrage());

        binding.etAnswer.setText("");

    }

    // SAVED QUIZ --> dasselbe Prinzip, wie bei liked Post
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

    // WRONG QUIZ
    private void ladeWrongQuiz() {

        quizFragen.clear();

        db.collection("wrongQuestions")// Falsch beantwortete Fragen des aktuellen Users laden
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                        String frage = document.getString("frage"); // Frage und richtige Antwort aus Firestore holen
                        String antwort = document.getString("antwort");

                        if (frage != null && antwort != null) { // Nur vollständige Fragen hinzufügen

                            quizFragen.add(new QuizQuestion(frage, antwort));

                        }

                    }
                    // ab hier ist es dasselbe wie bei LIKEDPOST und SAVED
                    if (!quizFragen.isEmpty()) {

                        Collections.shuffle(quizFragen);

                        if (quizFragen.size() > 10) {

                            quizFragen = new ArrayList<>(
                                    quizFragen.subList(0, 10)
                            );

                        }

                        startZeit = System.currentTimeMillis();

                        zeigeFrage();

                    } else {

                        Toast.makeText(getContext(),
                                "Keine falsch beantworteten Fragen vorhanden.",
                                Toast.LENGTH_SHORT).show();

                    }

                })
                .addOnFailureListener(e ->

                        Toast.makeText(getContext(),
                                "Fehler beim Laden des Quiz.",
                                Toast.LENGTH_SHORT).show()

                );

    }

}