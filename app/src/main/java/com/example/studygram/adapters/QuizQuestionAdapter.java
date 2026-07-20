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

    private ArrayList<QuizQuestion> fragen;

    public QuizQuestionAdapter(ArrayList<QuizQuestion> fragen) {
        this.fragen = fragen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_question, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        QuizQuestion frage = fragen.get(position);

        holder.tvFrage.setText(frage.getFrage());
        holder.tvTyp.setText("Typ: " + frage.getTyp());

    }

    @Override
    public int getItemCount() {
        return fragen.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFrage;
        TextView tvTyp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFrage = itemView.findViewById(R.id.tvFrage);
            tvTyp = itemView.findViewById(R.id.tvTyp);
        }
    }
}
