package com.example.studygram;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentQuizBinding.inflate(inflater, container, false);

        binding.btnLikedQuiz.setOnClickListener(v -> {

            Toast.makeText(getContext(),
                    "Liked Quiz",
                    Toast.LENGTH_SHORT).show();

        });

        binding.btnSavedQuiz.setOnClickListener(v -> {

            Toast.makeText(getContext(),
                    "Saved Quiz",
                    Toast.LENGTH_SHORT).show();

        });

        binding.btnWrongQuiz.setOnClickListener(v -> {

            Toast.makeText(getContext(),
                    "Wrong Questions Quiz",
                    Toast.LENGTH_SHORT).show();

        });

        return binding.getRoot();
    }
    Bundle bundle = new Bundle();
    bundle.putString("quizType", "liked");

NavHostFragment.findNavController(this)
        .navigate(R.id.action_quizFragment_to_quizGameFragment, bundle);

    Bundle bundle = new Bundle();
bundle.putString("quizType", "saved");

NavHostFragment.findNavController(this)
        .navigate(R.id.action_quizFragment_to_quizGameFragment, bundle);

    Bundle bundle = new Bundle();
bundle.putString("quizType", "wrong");

NavHostFragment.findNavController(this)
        .navigate(R.id.action_quizFragment_to_quizGameFragment, bundle);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}