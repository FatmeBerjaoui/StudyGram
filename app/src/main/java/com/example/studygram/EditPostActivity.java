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

    // Werkzeug zum Auswählen eines Bildes aus der Galerie
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

        // Daten des Posts von der vorherigen Seite empfangen
        post = (Post) getIntent().getSerializableExtra("post");

        // Falls die Daten nicht übertragen wurden, Seite zur Sicherheit schließen
        if (post == null) {
            finish();
            return;
        }

        // Die aktuellen Post-Daten in die Eingabefelder schreiben
        binding.etEditTitle.setText(post.getTitle());
        binding.etEditDescription.setText(post.getDescription());
        binding.actEditSubject.setText(post.getSubject());

        // Das bestehende Bild aus der Datenbank laden
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this).load(post.getImageUrl()).into(binding.imgEditPreview);
        } //glide lädt Bild asynchron aus der Cloud, damit App nicht verlangsamt wird

        // Auswahl-Liste für die Module/Kategorien vorbereiten
        String[] module = {"Programmierung", "Software Engineering", "Datenbanken", "Webentwicklung", "IT-Sicherheit", "Betriebssysteme", "Rechnernetze", "Wirtschaftsinformatik", "BWL", "VWL", "Rechnungswesen", "Controlling", "Marketing", "Personalmanagement", "Projektmanagement", "Statistik", "Mathematik", "Business Intelligence", "ERP-Systeme (SAP)", "Sonstiges"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, module);
        binding.actEditSubject.setAdapter(adapter);
        
        binding.actEditSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.actEditSubject.showDropDown();
            }
        });

        // Button-Klick für Bild-Upload
        binding.btnUploadNewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker.launch("image/*");
            }
        });

        // Button-Klick zum Speichern der Änderungen
        binding.btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    // Prüft die Eingaben und startet den Speicherprozess
    private void saveChanges() {
        String title = binding.etEditTitle.getText().toString().trim();
        String subject = binding.actEditSubject.getText().toString().trim();
        String description = binding.etEditDescription.getText().toString().trim();

        if (title.isEmpty() || subject.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Falls ein neues Bild gewählt wurde, muss es erst hochgeladen werden
        if (newImageUri != null) {
            uploadImageAndSave(title, subject, description);
        } else {
            // Ansonsten direkt die Textdaten in Firestore aktualisieren
            updateFirestore(title, subject, description, post.getImageUrl());
        }
    }

    // Lädt das neue Foto zu Cloudinary hoch
    private void uploadImageAndSave(String title, String subject, String description) {
        MediaManager.get().upload(newImageUri)  //neues Bild an Cloudinary
                .unsigned("studygram_upload")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}


                    //Erst wenn wir die neue Internet URL haben wird mit Speichern in der DB weitergemacht
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        newImageUrl = resultData.get("secure_url").toString();
                        updateFirestore(title, subject, description, newImageUrl);
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Toast.makeText(EditPostActivity.this, "Upload fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    }

                    //wenn Upload abgebrochen wird, also nicht sofort ausgeführt werden kann
                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {}
                }).dispatch(); //
    }

    // Aktualisiert das bestehende Dokument in der Firebase-Datenbank
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