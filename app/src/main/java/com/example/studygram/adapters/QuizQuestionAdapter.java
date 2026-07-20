package com.example.studygram.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygram.R;
import com.example.studygram.models.QuizQuestion;

import java.util.ArrayList;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.ViewHolder> {

    private String frage;
    private String antwort;

    public QuizQuestion(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }

    public String getFrage() {
        return frage;
    }

    public String getAntwort() {
        return antwort;
    }