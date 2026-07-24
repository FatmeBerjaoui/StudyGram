package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import android.content.Intent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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
    private FirebaseFirestore db;

    private Uri imageUri;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            imageUri = uri;

                            binding.imgPreview.setVisibility(View.VISIBLE);
                            binding.imgPreview.setImageURI(uri);
                        }
                    });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddPostBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
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

        ArrayAdapter<String> modulAdapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        Modul);

        binding.actSubject.setAdapter(modulAdapter);


        quizFragen = new ArrayList<>(); //Quiz Fragen

        adapter = new QuizQuestionAdapter(quizFragen);

        binding.rvQuestions.setLayoutManager(
                new LinearLayoutManager(getContext()));

        binding.rvQuestions.setAdapter(adapter);

        binding.btnUpload.setOnClickListener(v -> {  //öffnet Galerie
            imagePicker.launch("image/*");
        });


        binding.btnPublish.setOnClickListener(v -> {  //Publish Button

            String titel = binding.etTitle.getText().toString().trim();
            String modul = binding.actSubject.getText().toString().trim();
            String beschreibung = binding.etDescription.getText().toString().trim();

            if (titel.isEmpty()
                    || modul.isEmpty()
                    || beschreibung.isEmpty()
                    || imageUri == null) {

                Toast.makeText(getContext(),
                        "Bitte alle Felder ausfüllen und ein Bild auswählen.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Map<String, Object> post = new HashMap<>();

            post.put("titel", titel);
            post.put("modul", modul);
            post.put("beschreibung", beschreibung);
            post.put("username", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            post.put("likes", 0);
            post.put("quizFragen", quizFragen);

            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(getContext(),
                                "Beitrag erfolgreich veröffentlicht!",
                                Toast.LENGTH_SHORT).show();

                        binding.etTitle.setText("");
                        binding.actSubject.setText("");
                        binding.etDescription.setText("");
                        binding.etQuestion.setText("");
                        binding.etAnswer.setText("");

                        quizFragen.clear();
                        adapter.notifyDataSetChanged();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(getContext(),
                                "Fehler: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                    });

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
    private void uploadImageToCloudinary(Uri imageUri) {

    }
}