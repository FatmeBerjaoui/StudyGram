package com.example.studygram;

public class QuizGameFragment extends Fragment {

    private FragmentQuizGameBinding binding;

    private ArrayList<QuizQuestion> quizFragen = new ArrayList<>();

    private int aktuelleFrage = 0;
    private int richtigeAntworten = 0;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;

    private String quizType;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        quizType = getArguments().getString("quizType");

        if (quizType.equals("liked")) {

            ladeLikedQuiz();

        } else if (quizType.equals("saved")) {

            ladeSavedQuiz();

        } else {

            ladeWrongQuiz();

        }
        Collections.shuffle(quizFragen);

        if (quizFragen.size() > 10) {

            quizFragen =
                    new ArrayList<>(quizFragen.subList(0, 10));

        }
        binding = FragmentQuizGameBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}