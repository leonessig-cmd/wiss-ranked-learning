package ch.wiss.ranked.service;

import ch.wiss.ranked.dto.request.SubmitAnswerRequest;
import ch.wiss.ranked.dto.response.*;
import ch.wiss.ranked.entity.*;
import ch.wiss.ranked.exception.*;
import ch.wiss.ranked.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizAttemptRepository quizAttemptRepo;
    private final UserAnswerRepository userAnswerRepo;
    private final QuestionRepository questionRepo;
    private final AnswerRepository answerRepo;
    private final TopicRepository topicRepo;
    private final UserRepository userRepo;

    @Transactional
    public Long startQuiz(Long userId, Long topicId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User nicht gefunden"));
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Thema nicht gefunden"));

        QuizAttempt attempt = QuizAttempt.builder()
                .user(user).topic(topic).build();
        return quizAttemptRepo.save(attempt).getId();
    }

    public List<QuestionResponse> getQuestionsForTopic(Long topicId, int limit) {
    List<Question> questions = questionRepo.findRandomByModuleAndRank("117", "Bronze II", limit);
    return questions.stream().map(this::toQuestionResponse).toList();
}

public List<QuestionResponse> getQuestionsForModuleAndRank(String moduleNumber, String rankName, int limit) {
    List<Question> questions = questionRepo.findRandomByModuleAndRank(moduleNumber, rankName, limit);
    return questions.stream().map(this::toQuestionResponse).toList();
}

    @Transactional
    public AnswerFeedbackResponse submitAnswer(Long userId, Long attemptId, SubmitAnswerRequest req) {
        QuizAttempt attempt = quizAttemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Versuch nicht gefunden"));
        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Zugriff verweigert");
        if (attempt.getFinishedAt() != null)
            throw new BadRequestException("Quiz bereits beendet");

        Question question = questionRepo.findById(req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Frage nicht gefunden"));
        Answer selected = answerRepo.findById(req.getSelectedAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Antwort nicht gefunden"));

        boolean correct = selected.isCorrect();
        if (correct) {
            attempt.setScore(attempt.getScore() + question.getPoints());
        }
        attempt.setMaxScore(attempt.getMaxScore() + question.getPoints());

        UserAnswer ua = UserAnswer.builder()
                .quizAttempt(attempt).question(question)
                .selectedAnswer(selected).correct(correct).build();
        userAnswerRepo.save(ua);
        quizAttemptRepo.save(attempt);

        if (correct) addPointsToUser(userId, question.getPoints());

        Answer correctAnswer = question.getAnswers().stream()
                .filter(Answer::isCorrect).findFirst().orElse(selected);

        return AnswerFeedbackResponse.builder()
                .questionId(question.getId())
                .selectedAnswerId(selected.getId())
                .correctAnswerId(correctAnswer.getId())
                .correct(correct)
                .pointsEarned(correct ? question.getPoints() : 0)
                .explanation("Die richtige Antwort ist: " + correctAnswer.getAnswerText())
                .build();
    }

    @Transactional
    public QuizResultResponse finishQuiz(Long userId, Long attemptId) {
        QuizAttempt attempt = quizAttemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Versuch nicht gefunden"));
        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Zugriff verweigert");

        attempt.setFinishedAt(LocalDateTime.now());
        quizAttemptRepo.save(attempt);

        List<AnswerFeedbackResponse> feedbacks = userAnswerRepo.findByQuizAttemptId(attemptId)
                .stream().map(ua -> {
                    Answer correct = ua.getQuestion().getAnswers().stream()
                            .filter(Answer::isCorrect).findFirst().orElse(ua.getSelectedAnswer());
                    return AnswerFeedbackResponse.builder()
                            .questionId(ua.getQuestion().getId())
                            .selectedAnswerId(ua.getSelectedAnswer().getId())
                            .correctAnswerId(correct.getId())
                            .correct(ua.isCorrect())
                            .pointsEarned(ua.isCorrect() ? ua.getQuestion().getPoints() : 0)
                            .explanation("Richtige Antwort: " + correct.getAnswerText())
                            .build();
                }).toList();

        int percentage = attempt.getMaxScore() > 0
                ? (attempt.getScore() * 100) / attempt.getMaxScore() : 0;

        return QuizResultResponse.builder()
                .attemptId(attemptId)
                .score(attempt.getScore())
                .maxScore(attempt.getMaxScore())
                .percentage(percentage)
                .finishedAt(attempt.getFinishedAt())
                .answers(feedbacks)
                .build();
    }

    private QuestionResponse toQuestionResponse(Question q) {
        List<QuestionResponse.AnswerOptionResponse> opts = q.getAnswers().stream()
                .map(a -> QuestionResponse.AnswerOptionResponse.builder()
                        .id(a.getId()).answerText(a.getAnswerText()).build())
                .toList();
        return QuestionResponse.builder()
                .id(q.getId()).questionText(q.getQuestionText())
                .difficulty(q.getDifficulty()).points(q.getPoints())
                .answers(opts).build();
    }

    private void addPointsToUser(Long userId, int points) {
        userRepo.findById(userId).ifPresent(u -> {
            u.setTotalPoints(u.getTotalPoints() + points);
            userRepo.save(u);
        });
    }
}
