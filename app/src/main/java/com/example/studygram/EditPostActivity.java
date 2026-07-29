package com.example.studygram;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.example.studygram.databinding.ActivityEditPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private ActivityEditPostBinding binding;
    private Post post;
    private Uri newImageUri;
    private String newImageUrl = "";

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new androidx.activity.result.ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        newImageUri = uri;
                        binding.imgEditPreview.setImageURI(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        post = (Post) getIntent().getSerializableExtra("post");

        if (post == null) {
            finish();
            return;
        }

        // Aktuelle Daten anzeigen
        binding.etEditTitle.setText(post.getTitle());
        binding.etEditDescription.setText(post.getDescription());
        binding.actEditSubject.setText(post.getSubject());

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this).load(post.getImageUrl()).into(binding.imgEditPreview);
        }

        // Modul-Liste (wie in AddPostFragment)
        String[] module = {"Programmierung", "Software Engineering", "Datenbanken", "Webentwicklung", "IT-Sicherheit", "Betriebssysteme", "Rechnernetze", "Wirtschaftsinformatik", "BWL", "VWL", "Rechnungswesen", "Controlling", "Marketing", "Personalmanagement", "Projektmanagement", "Statistik", "Mathematik", "Business Intelligence", "ERP-Systeme (SAP)", "Sonstiges"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, module);
        binding.actEditSubject.setAdapter(adapter);
        binding.actEditSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.actEditSubject.showDropDown();
            }
        });

        binding.btnUploadNewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker.launch("image/*");
            }
        });

        binding.btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String title = binding.etEditTitle.getText().toString().trim();
        String subject = binding.actEditSubject.getText().toString().trim();
        String description = binding.etEditDescription.getText().toString().trim();

        if (title.isEmpty() || subject.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newImageUri != null) {
            // Erst neues Bild hochladen
            uploadImageAndSave(title, subject, description);
        } else {
            // Nur Textdaten aktualisieren
            updateFirestore(title, subject, description, post.getImageUrl());
        }
    }

    private void uploadImageAndSave(String title, String subject, String description) {
        MediaManager.get().upload(newImageUri)
                .unsigned("studygram_upload")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        newImageUrl = resultData.get("secure_url").toString();
                        updateFirestore(title, subject, description, newImageUrl);
                    }
                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Toast.makeText(EditPostActivity.this, "Upload fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {}
                }).dispatch();
    }

    private void updateFirestore(String title, String subject, String description, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("subject", subject);
        updates.put("description", description);
        updates.put("imageUrl", imageUrl);

        FirebaseFirestore.getInstance().collection("posts")
                .document(post.getPostId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditPostActivity.this, "Beitrag aktualisiert", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}