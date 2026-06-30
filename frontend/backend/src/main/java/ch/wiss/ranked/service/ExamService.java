package ch.wiss.ranked.service;

import ch.wiss.ranked.dto.request.SubmitAnswerRequest;
import ch.wiss.ranked.dto.response.AnswerFeedbackResponse;
import ch.wiss.ranked.dto.response.ExamAttemptResponse;
import ch.wiss.ranked.entity.*;
import ch.wiss.ranked.enums.ExamStatus;
import ch.wiss.ranked.exception.*;
import ch.wiss.ranked.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepo;
    private final ExamAttemptRepository examAttemptRepo;
    private final QuestionRepository questionRepo;
    private final AnswerRepository answerRepo;
    private final UserRepository userRepo;

    @Transactional
    public Long startExam(Long userId, Long examId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Prüfung nicht gefunden"));

        examAttemptRepo.findByUserIdAndExamIdAndStatus(userId, examId, ExamStatus.STARTED)
                .ifPresent(a -> { throw new BadRequestException("Du hast bereits eine laufende Prüfung"); });

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User nicht gefunden"));

        ExamAttempt attempt = ExamAttempt.builder()
                .user(user).exam(exam)
                .maxScore(exam.getQuestions().stream().mapToInt(Question::getPoints).sum())
                .build();
        return examAttemptRepo.save(attempt).getId();
    }

    @Transactional
    public AnswerFeedbackResponse submitAnswer(Long userId, Long attemptId, SubmitAnswerRequest req) {
        ExamAttempt attempt = examAttemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Prüfungsversuch nicht gefunden"));
        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Zugriff verweigert");
        if (attempt.getStatus() != ExamStatus.STARTED)
            throw new BadRequestException("Prüfung bereits abgeschlossen");

        // Check time limit
        if (LocalDateTime.now().isAfter(attempt.getStartedAt().plusMinutes(attempt.getExam().getDurationMinutes()))) {
            attempt.setStatus(ExamStatus.TIMED_OUT);
            attempt.setFinishedAt(LocalDateTime.now());
            examAttemptRepo.save(attempt);
            throw new BadRequestException("Prüfungszeit abgelaufen");
        }

        Question question = questionRepo.findById(req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Frage nicht gefunden"));
        Answer selected = answerRepo.findById(req.getSelectedAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Antwort nicht gefunden"));

        boolean correct = selected.isCorrect();
        if (correct) attempt.setScore(attempt.getScore() + question.getPoints());

        ExamAnswer ea = ExamAnswer.builder()
                .examAttempt(attempt).question(question)
                .selectedAnswer(selected).correct(correct).build();
        attempt.getExamAnswers().add(ea);
        examAttemptRepo.save(attempt);

        Answer correctAnswer = question.getAnswers().stream()
                .filter(Answer::isCorrect).findFirst().orElse(selected);

        return AnswerFeedbackResponse.builder()
                .questionId(question.getId())
                .selectedAnswerId(selected.getId())
                .correctAnswerId(correctAnswer.getId())
                .correct(correct)
                .pointsEarned(correct ? question.getPoints() : 0)
                .explanation("Richtige Antwort: " + correctAnswer.getAnswerText())
                .build();
    }

    @Transactional
    public ExamAttemptResponse finishExam(Long userId, Long attemptId) {
        ExamAttempt attempt = examAttemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Prüfungsversuch nicht gefunden"));
        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Zugriff verweigert");

        attempt.setStatus(ExamStatus.FINISHED);
        attempt.setFinishedAt(LocalDateTime.now());
        examAttemptRepo.save(attempt);

        // Award points based on score
        int bonus = attempt.getScore() / 2;
        userRepo.findById(userId).ifPresent(u -> {
            u.setTotalPoints(u.getTotalPoints() + bonus);
            userRepo.save(u);
        });

        return toResponse(attempt);
    }

    public ExamAttemptResponse getAttempt(Long userId, Long attemptId) {
        ExamAttempt attempt = examAttemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Prüfungsversuch nicht gefunden"));
        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Zugriff verweigert");
        return toResponse(attempt);
    }

    private ExamAttemptResponse toResponse(ExamAttempt a) {
        List<AnswerFeedbackResponse> feedbacks = a.getExamAnswers().stream()
                .map(ea -> {
                    Answer correct = ea.getQuestion().getAnswers().stream()
                            .filter(Answer::isCorrect).findFirst().orElse(ea.getSelectedAnswer());
                    return AnswerFeedbackResponse.builder()
                            .questionId(ea.getQuestion().getId())
                            .selectedAnswerId(ea.getSelectedAnswer().getId())
                            .correctAnswerId(correct.getId())
                            .correct(ea.isCorrect())
                            .pointsEarned(ea.isCorrect() ? ea.getQuestion().getPoints() : 0)
                            .explanation("Richtige Antwort: " + correct.getAnswerText())
                            .build();
                }).toList();

        int pct = a.getMaxScore() > 0 ? (a.getScore() * 100) / a.getMaxScore() : 0;
        return ExamAttemptResponse.builder()
                .id(a.getId())
                .examTitle(a.getExam().getTitle())
                .score(a.getScore()).maxScore(a.getMaxScore()).percentage(pct)
                .status(a.getStatus())
                .startedAt(a.getStartedAt()).finishedAt(a.getFinishedAt())
                .answers(feedbacks)
                .build();
    }
}
