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

    // Öffnet die Galerie, um ein neues Bild auszuwählen
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

        // Holt den Post ab, der von der vorherigen Seite (Profile) geschickt wurde
        post = (Post) getIntent().getSerializableExtra("post");
        // Sicherheit: Falls kein Post übergeben wurde, Seite schließen
        if (post == null) {
            finish();
            return;
        }

        //Füllt die Textfelder mit den aktuellen Daten des Posts aus
        binding.etEditTitle.setText(post.getTitle());
        binding.etEditDescription.setText(post.getDescription());
        binding.actEditSubject.setText(post.getSubject());

        // Lädt das aktuelle Bild aus dem Internet in das Vorschau-Feld
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this).load(post.getImageUrl()).into(binding.imgEditPreview);
        }

        // // Erstellt die Liste für die Modul-Auswahl (Dropdown)
        String[] module = {"Programmierung", "Software Engineering", "Datenbanken", "Webentwicklung", "IT-Sicherheit", "Betriebssysteme", "Rechnernetze", "Wirtschaftsinformatik", "BWL", "VWL", "Rechnungswesen", "Controlling", "Marketing", "Personalmanagement", "Projektmanagement", "Statistik", "Mathematik", "Business Intelligence", "ERP-Systeme (SAP)", "Sonstiges"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, module);
        binding.actEditSubject.setAdapter(adapter);

        // Öffnet die Liste beim Klick auf das Modul-Feld
        binding.actEditSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.actEditSubject.showDropDown();
            }
        });
// Startet die Bild-Auswahl
        binding.btnUploadNewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker.launch("image/*");
            }
        });
// Startet den Speicher-Vorgang
        binding.btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    // Prüft die Eingaben und entscheidet: Bild hochladen oder direkt speichern?
    private void saveChanges() {
        String title = binding.etEditTitle.getText().toString().trim();
        String subject = binding.actEditSubject.getText().toString().trim();
        String description = binding.etEditDescription.getText().toString().trim();

        if (title.isEmpty() || subject.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }
        // Wenn ein NEUES Bild gewählt wurde, muss es erst zu Cloudinary hochgeladen werden
        if (newImageUri != null) {
            // Erst neues Bild hochladen
            uploadImageAndSave(title, subject, description);
        } else {
            // Wenn kein neues Bild gewählt wurde, nutzen wir einfach die alte Bild-URL
            updateFirestore(title, subject, description, post.getImageUrl());
        }
    }

    // Lädt das neue Bild zu Cloudinary hoch
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
                        // Wenn der Upload fertig ist, bekommen wir die neue Internet-URL
                        newImageUrl = resultData.get("secure_url").toString();
                        // Jetzt kann man den Post in Firestore aktualisieren
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

    // Speichert die finalen Änderungen in der Firebase-Datenbank
    private void updateFirestore(String title, String subject, String description, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("subject", subject);
        updates.put("description", description);
        updates.put("imageUrl", imageUrl);

        // Sucht das Dokument in der Sammlung "posts" anhand der ID und aktualisiert es
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