package com.example.studygram.adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.studygram.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
public class PostOptionsHelper {


    //gehört Post wirklich aktuellen user?
    public static void showOptionsIfOwnPost(Post post, Context context, List<Post> postList, FeedAdapter adapter) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isOwnPost = post.getUserId() != null && post.getUserId().equals(currentUserId);

        if (!isOwnPost) {
            return;
        }

        String[] options = {"Bearbeiten", "Löschen", "Teilen"};
//erstell Pop Up mit Optionen
        new AlertDialog.Builder(context)
                .setTitle(post.getTitle())
                //array option wird übergeben
                .setItems(options, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(context, com.example.studygram.EditPostActivity.class);
                            intent.putExtra("post", post);
                            context.startActivity(intent);
                        } else if (which == 1) {
                            deletePost(post, context, postList, adapter);
                        } else if (which == 2) {
                            sharePost(post, context);
                        }
                    }
                })
                .show();
    }

    private static void deletePost(Post post, Context context, List<Post> postList, FeedAdapter adapter) {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(post.getPostId())//genau diesen PostId übergeben
                .delete()
                .addOnSuccessListener(aVoid -> { //wenn succsess post aus Liste in der App
                    postList.remove(post);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Post gelöscht", Toast.LENGTH_SHORT).show();
                });
    }

    private static void sharePost(Post post, Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);  //INtent mit aktiion send
        shareIntent.setType("text/plain"); //datentyp festlegen
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n" + post.getDescription());
        context.startActivity(Intent.createChooser(shareIntent, "Teilen über")); //chooser öffnen
    }
}
