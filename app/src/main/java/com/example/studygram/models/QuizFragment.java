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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}