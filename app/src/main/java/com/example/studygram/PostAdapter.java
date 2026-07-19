package com.example.studygram;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studygram.databinding.PostItemBinding;
import java.util.List;
import com.example.studygram.models.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    // Wird aufgerufen, wenn eine neue Zeile (Item) erstellt werden muss
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostItemBinding binding = PostItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    // Wird aufgerufen, um eine Zeile mit den Daten eines bestimmten Posts zu füllen
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.binding.tvPostTitle.setText(post.getTitle());
        holder.binding.tvPostSubject.setText(post.getSubject());
    }

    // Gibt an, wie viele Elemente die Liste hat
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder hält die Views einer einzelnen Zeile fest (ähnlich wie "binding" bei einer Activity)
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        PostItemBinding binding;

        public PostViewHolder(PostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
