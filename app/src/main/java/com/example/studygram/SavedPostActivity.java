package com.example.studygram;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studygram.databinding.ActivitySavedPostsBinding;
public class SavedPostActivity {
    private ActivitySavedPostsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
