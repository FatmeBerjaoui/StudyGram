package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studygram.adapters.QuizQuestionAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.studygram.databinding.FragmentAddPostBinding;

public class AddPostFragment extends Fragment {
    private ArrayList<QuizQuestion> quizFragen;
    private QuizQuestionAdapter adapter;
    private FragmentAddPostBinding binding;
    private FirebaseFirestore db;
    private Uri imageUri;
    private String imageUrl = "";


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
        try {
            MediaManager.get();
        } catch (Exception e) {

            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "tsxii2y0");

            MediaManager.init(requireContext(), config);
        }
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
        binding.actSubject.setOnClickListener(v ->
                binding.actSubject.showDropDown());


        quizFragen = new ArrayList<>(); //Quiz Fragen

        adapter = new QuizQuestionAdapter(quizFragen);

        //binding.rvQuestions.setLayoutManager(
                new LinearLayoutManager(getContext()));

        //binding.rvQuestions.setAdapter(adapter);

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
            uploadImageToCloudinary(imageUri, titel, modul, beschreibung);


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
    private void uploadImageToCloudinary(Uri selectedImageUri,
                                         String titel,
                                         String modul,
                                         String beschreibung) {

        MediaManager.get().upload(selectedImageUri)
                .unsigned("studygram_upload")
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        imageUrl = resultData.get("secure_url").toString();

                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        Map<String, Object> post = new HashMap<>();
                        post.put("userId", currentUserId);
                        post.put("username", username);
                        post.put("title", titel);
                        post.put("subject", modul);
                        post.put("description", beschreibung);
                        post.put("imageUrl", imageUrl);
                        post.put("likes", 0);
                        post.put("quizFragen", quizFragen);

                        db.collection("posts")
                                .add(post)
                                .addOnSuccessListener(documentReference -> {

                                    requireActivity().runOnUiThread(() -> {

                                        Toast.makeText(getContext(),
                                                "Beitrag erfolgreich veröffentlicht!",
                                                Toast.LENGTH_SHORT).show();

                                        binding.etTitle.setText("");
                                        binding.actSubject.setText("");
                                        binding.etDescription.setText("");
                                        binding.etQuestion.setText("");
                                        binding.etAnswer.setText("");

                                        AddPostFragment.this.imageUri = null;
                                        imageUrl = "";
                                        binding.imgPreview.setImageDrawable(null);
                                        binding.imgPreview.setVisibility(View.GONE);

                                        quizFragen.clear();
                                        adapter.notifyDataSetChanged();

                                    });

                                })
                                .addOnFailureListener(e -> {

                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(getContext(),
                                                    "Fehler: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show());

                                });
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        "Upload fehlgeschlagen",
                                        Toast.LENGTH_LONG).show());

                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {

                    }
                })
                .dispatch();

    }
}