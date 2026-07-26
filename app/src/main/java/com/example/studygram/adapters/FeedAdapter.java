package com.example.studygram.adapters;
//damit wird für jeden Post meine Vorlage aus der xml Datei genutzt

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygram.R;
import com.example.studygram.models.Post;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostViewHolder> {

    private List<Post> postList;



    public FeedAdapter(List<Post> postList) {
        this.postList = postList; //Konstruktor, für den Adapter meiner Posts
    }

    @NonNull
    @Override

    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false); //für jeden Post soll die XML-Vorlage benutzt werden

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("savedPosts")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("postId", post.getPostId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        post.setSaved(true);
                        holder.btnSave.setAlpha(1f);
                    } else {
                        post.setSaved(false);
                        holder.btnSave.setAlpha(0.5f);
                    }

                });

        holder.tvTitle.setText(post.getTitle()); //"schreibe den Titel der Posts in die Textview"
        holder.tvSubject.setText(post.getSubject());
        holder.tvDescription.setText(post.getDescription());
        holder.tvUsername.setText(post.getUsername());
        holder.tvLikes.setText("❤️ " + post.getLikes());

        holder.btnLike.setOnClickListener(v -> {

            if (!post.isLiked()) {

                post.setLiked(true);
                post.setLikes(post.getLikes() + 1);

            } else {

                post.setLiked(false);
                post.setLikes(post.getLikes() - 1);

            }

            holder.tvLikes.setText("❤️ " + post.getLikes());

        });

        holder.btnSave.setOnClickListener(v -> {

            if (!post.isSaved()) {

                post.setSaved(true);
                holder.btnSave.setAlpha(1f);

                Map<String, Object> savedPost = new HashMap<>();
                savedPost.put("userId", currentUserId);
                savedPost.put("postId", post.getPostId());

                db.collection("savedPosts")
                        .add(savedPost);

            } else {

                post.setSaved(false);
                holder.btnSave.setAlpha(0.5f);

                db.collection("savedPosts")
                        .whereEqualTo("userId", currentUserId)
                        .whereEqualTo("postId", post.getPostId())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                doc.getReference().delete();
                            }

                        });

            }

        });


    }



    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvSubject;
        TextView tvDescription;
        TextView tvUsername;
        TextView tvLikes;

        ImageButton btnLike;
        ImageButton btnSave;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnSave = itemView.findViewById(R.id.btnSave);

        }

    }
    public void updateList(List<Post> neueListe){
        postList = neueListe;
        notifyDataSetChanged();
    }
}