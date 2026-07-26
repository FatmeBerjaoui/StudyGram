package com.example.studygram.adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.studygram.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
public class PostOptionsHelper {
    // Zeigt das Optionsmenü nur an, wenn der Post dem aktuell eingeloggten User gehört
    public static void showOptionsIfOwnPost(Post post, Context context, List<Post> postList, FeedAdapter adapter) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isOwnPost = post.getUserId() != null && post.getUserId().equals(currentUserId);

        if (!isOwnPost) {
            return;
        }

        String[] options = {"Bearbeiten", "Löschen", "Teilen", "Likes anzeigen"};

        new AlertDialog.Builder(context)
                .setTitle(post.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(context, "Bearbeiten kommt bald", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            deletePost(post, context, postList, adapter);
                            break;
                        case 2:
                            sharePost(post, context);
                            break;
                        case 3:
                            Toast.makeText(context, post.getLikes() + " Likes", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private static void deletePost(Post post, Context context, List<Post> postList, FeedAdapter adapter) {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(post.getPostId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    postList.remove(post);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Post gelöscht", Toast.LENGTH_SHORT).show();
                });
    }

    private static void sharePost(Post post, Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n" + post.getDescription());
        context.startActivity(Intent.createChooser(shareIntent, "Teilen über"));
    }
}
