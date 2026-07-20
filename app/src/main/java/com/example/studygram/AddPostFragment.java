package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studygram.adapters.QuizQuestionAdapter;
import com.example.studygram.models.QuizQuestion;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.studygram.databinding.FragmentAddPostBinding;

public class AddPostFragment extends Fragment {
    private ArrayList<QuizQuestion> quizFragen;
    private QuizQuestionAdapter adapter;
    private FragmentAddPostBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddPostBinding.inflate(inflater, container, false);
        String[] Modul = {
                "Programmierung",
                "Software Engineering",
                "Datenbanken",
                "Webentwicklung",
                "IT-Sicherheit",
                "Betriebssysteme",
                "Rechnernetze",
                "Wirtschaftsinformatik",
                "BWL",
                "VWL",
                "Rechnungswesen",
                "Controlling",
                "Marketing",
                "Personalmanagement",
                "Projektmanagement",
                "Statistik",
                "Mathematik",
                "Business Intelligence",
                "ERP-Systeme (SAP)",
                "Sonstiges"
        };

        ArrayAdapter<String> moduladapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        Modul);

        binding.actSubject.setAdapter(moduladapter);


        quizFragen = new ArrayList<>(); //Quiz Fragen

        adapter = new QuizQuestionAdapter(quizFragen);

        binding.rvQuestions.setLayoutManager(
                new LinearLayoutManager(getContext()));

        binding.rvQuestions.setAdapter(adapter);


        binding.btnPublish.setOnClickListener(v -> {  //Publish Button

            String titel = binding.etTitle.getText().toString().trim();
            String modul = binding.actSubject.getText().toString().trim();
            String beschreibung = binding.etDescription.getText().toString().trim();

            if (titel.isEmpty() || modul.isEmpty() || beschreibung.isEmpty()) {

                Toast.makeText(getContext(),
                        "Bitte fülle alle Felder aus.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Toast.makeText(getContext(),
                    "Beitrag ist bereit zum Veröffentlichen!",
                    Toast.LENGTH_SHORT).show();

        });
        binding.btnAddQuestion.setOnClickListener(v -> {

            String frage = binding.etQuestion.getText().toString().trim();
            String antwort = binding.etAnswer.getText().toString().trim();

            if (frage.isEmpty() || antwort.isEmpty()) {

                Toast.makeText(getContext(),
                        "Bitte Frage und Antwort eingeben.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            quizFragen.add(new QuizQuestion(frage, antwort));

            adapter.notifyItemInserted(quizFragen.size() - 1);

            binding.etQuestion.setText("");
            binding.etAnswer.setText("");

            Toast.makeText(getContext(),
                    "Quizfrage hinzugefügt!",
                    Toast.LENGTH_SHORT).show();

        });
        return binding.getRoot();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}