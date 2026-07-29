package com.example.studygram.adapters;
//damit wird für jeden Post meine Vorlage aus der xml Datei genutzt

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FieldValue;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygram.R;
import com.example.studygram.Post;

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
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            post.setSaved(true);
                            holder.btnSave.setAlpha(1f);
                        } else {
                            post.setSaved(false);
                            holder.btnSave.setAlpha(0.5f);
                        }
                    }
                });
        db.collection("likedPosts")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("postId", post.getPostId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        post.setLiked(!queryDocumentSnapshots.isEmpty());
                    }
                });

        holder.tvTitle.setText(post.getTitle()); //"schreibe den Titel der Posts in die Textview"
        holder.tvSubject.setText(post.getSubject());
        holder.tvDescription.setText(post.getDescription());
        holder.tvUsername.setText(post.getUsername());
        holder.tvLikes.setText("❤️ " + post.getLikes());

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) { //Bild aus Cloudinary geholt und angezeigt
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .into(holder.imgPost);
            holder.imgPost.setVisibility(View.VISIBLE);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.isLiked()) {
                    post.setLiked(true); //lokal gespeichert, dass Post geliked wurde
                    db.collection("posts")
                            .document(post.getPostId())
                            .update("likes", FieldValue.increment(1));

                    Map<String, Object> likedPost = new HashMap<>();
                    likedPost.put("userId", currentUserId);
                    likedPost.put("postId", post.getPostId());

                    db.collection("likedPosts").add(likedPost);
                    post.setLikes(post.getLikes() + 1);
                } else {
                    post.setLiked(false);
                    db.collection("posts")
                            .document(post.getPostId())
                            .update("likes", FieldValue.increment(-1));

                    db.collection("likedPosts")
                            .whereEqualTo("userId", currentUserId) //wird gesucht welcher Like-Eintrag gelöscht werden soll
                            .whereEqualTo("postId", post.getPostId())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                        doc.getReference().delete();
                                    }
                                }
                            });
                    post.setLikes(post.getLikes() - 1);
                }
                holder.tvLikes.setText("❤️ " + post.getLikes());
            }
        });


        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                        doc.getReference().delete();
                                    }
                                }
                            });
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOptionsHelper.showOptionsIfOwnPost(post, holder.itemView.getContext(), postList, FeedAdapter.this);
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
        ImageView imgPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnSave = itemView.findViewById(R.id.btnSave);
            imgPost = itemView.findViewById(R.id.imgPost);

        }

    }
    public void updateList(List<Post> neueListe){
        postList = neueListe;
        notifyDataSetChanged();
    }
}