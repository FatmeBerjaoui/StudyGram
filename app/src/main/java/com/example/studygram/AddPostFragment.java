package com.example.studygram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.studygram.databinding.FragmentAddPostBinding;

public class AddPostFragment extends Fragment {

    private FragmentAddPostBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddPostBinding.inflate(inflater, container, false);
        String[] modul = {
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

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        modul);

        binding.actSubject.setAdapter(adapter);

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
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(AddPostFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}