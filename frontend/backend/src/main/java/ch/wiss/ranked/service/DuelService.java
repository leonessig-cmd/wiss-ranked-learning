package ch.wiss.ranked.service;

import ch.wiss.ranked.dto.request.SubmitAnswerRequest;
import ch.wiss.ranked.dto.response.AnswerFeedbackResponse;
import ch.wiss.ranked.dto.response.DuelResponse;
import ch.wiss.ranked.dto.response.QuestionResponse;
import ch.wiss.ranked.entity.*;
import ch.wiss.ranked.enums.DuelStatus;
import ch.wiss.ranked.dto.response.QuestionResponse;
import ch.wiss.ranked.exception.*;
import ch.wiss.ranked.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuelService {

    private final DuelRepository duelRepo;
    private final DuelPlayerRepository duelPlayerRepo;
    private final DuelAnswerRepository duelAnswerRepo;
    private final QuestionRepository questionRepo;
    private final AnswerRepository answerRepo;
    private final TopicRepository topicRepo;
    private final UserRepository userRepo;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int DUEL_QUESTION_COUNT = 5;

    @Transactional
    public DuelResponse joinOrCreateDuel(Long userId, Long topicId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User nicht gefunden"));
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Thema nicht gefunden"));

        // Check if there's an open duel to join
        return duelRepo.findOpenDuelByTopicId(topicId)
                .filter(d -> d.getPlayers().stream().noneMatch(p -> p.getUser().getId().equals(userId)))
                .map(existingDuel -> joinDuel(existingDuel, user))
                .orElseGet(() -> createNewDuel(user, topic));
    }

    private DuelResponse createNewDuel(User user, Topic topic) {
        Duel duel = Duel.builder().topic(topic).status(DuelStatus.WAITING).build();
        duel = duelRepo.save(duel);

        DuelPlayer player = DuelPlayer.builder().duel(duel).user(user).build();
        duelPlayerRepo.save(player);
        duel.getPlayers().add(player);

        notifyDuelUpdate(duel.getId(), "WAITING_FOR_OPPONENT");
        return toDuelResponse(duel);
    }

    @Transactional
    private DuelResponse joinDuel(Duel duel, User user) {
        DuelPlayer player = DuelPlayer.builder().duel(duel).user(user).build();
        duelPlayerRepo.save(player);
        duel.getPlayers().add(player);

        duel.setStatus(DuelStatus.IN_PROGRESS);
        duel.setStartedAt(LocalDateTime.now());
        duelRepo.save(duel);

        notifyDuelUpdate(duel.getId(), "DUEL_STARTED");
        return toDuelResponse(duel);
    }

    @Transactional
    public AnswerFeedbackResponse submitAnswer(Long userId, Long duelId, SubmitAnswerRequest req) {
        Duel duel = duelRepo.findById(duelId)
                .orElseThrow(() -> new ResourceNotFoundException("Duell nicht gefunden"));
        if (duel.getStatus() != DuelStatus.IN_PROGRESS)
            throw new BadRequestException("Duell ist nicht aktiv");

        boolean alreadyAnswered = duelAnswerRepo.existsByDuelIdAndUserIdAndQuestionId(
                duelId, userId, req.getQuestionId());
        if (alreadyAnswered)
            throw new BadRequestException("Diese Frage wurde bereits beantwortet");

        Question question = questionRepo.findById(req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Frage nicht gefunden"));
        Answer selected = answerRepo.findById(req.getSelectedAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Antwort nicht gefunden"));

        boolean correct = selected.isCorrect();

        DuelAnswer duelAnswer = DuelAnswer.builder()
                .duel(duel).user(userRepo.getReferenceById(userId))
                .question(question).selectedAnswer(selected).correct(correct).build();
        duelAnswerRepo.save(duelAnswer);

        if (correct) {
            duelPlayerRepo.findByDuelIdAndUserId(duelId, userId).ifPresent(p -> {
                p.setScore(p.getScore() + question.getPoints());
                duelPlayerRepo.save(p);
            });
        }

        notifyDuelUpdate(duelId, "ANSWER_SUBMITTED");
        checkDuelCompletion(duel);

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

    private void checkDuelCompletion(Duel duel) {
        long totalAnswers = duelAnswerRepo.countByDuelId(duel.getId());
        long expectedAnswers = (long) duel.getPlayers().size() * DUEL_QUESTION_COUNT;

        if (totalAnswers >= expectedAnswers) {
            finishDuel(duel);
        }
    }

    @Transactional
    private void finishDuel(Duel duel) {
        duel.setStatus(DuelStatus.FINISHED);
        duel.setFinishedAt(LocalDateTime.now());

        DuelPlayer winner = duel.getPlayers().stream()
                .max((a, b) -> Integer.compare(a.getScore(), b.getScore()))
                .orElse(null);

        if (winner != null) {
            duel.setWinner(winner.getUser());
            userRepo.findById(winner.getUser().getId()).ifPresent(u -> {
                u.setTotalPoints(u.getTotalPoints() + 50); // bonus for winning
                userRepo.save(u);
            });
        }
        duelRepo.save(duel);
        notifyDuelUpdate(duel.getId(), "DUEL_FINISHED");
    }

    public DuelResponse getDuel(Long duelId) {
        Duel duel = duelRepo.findById(duelId)
                .orElseThrow(() -> new ResourceNotFoundException("Duell nicht gefunden"));
        return toDuelResponse(duel);
    }

    public List<DuelResponse> getMyDuels(Long userId) {
        return duelRepo.findByPlayerId(userId).stream().map(this::toDuelResponse).toList();
    }

    public List<QuestionResponse> getDuelQuestions(Long duelId, Long topicId) {
        duelRepo.findById(duelId)
                .orElseThrow(() -> new ResourceNotFoundException("Duell nicht gefunden"));
        List<Question> questions = questionRepo.findRandomByTopicId(topicId, DUEL_QUESTION_COUNT);
        return questions.stream().map(q -> {
            var opts = q.getAnswers().stream()
                    .map(a -> QuestionResponse.AnswerOptionResponse.builder()
                            .id(a.getId()).answerText(a.getAnswerText()).build())
                    .toList();
            return QuestionResponse.builder()
                    .id(q.getId()).questionText(q.getQuestionText())
                    .difficulty(q.getDifficulty()).points(q.getPoints())
                    .answers(opts).build();
        }).toList();
    }

    private void notifyDuelUpdate(Long duelId, String event) {
        messagingTemplate.convertAndSend("/topic/duel/" + duelId, event);
    }

    private DuelResponse toDuelResponse(Duel d) {
        List<DuelResponse.PlayerScoreResponse> players = d.getPlayers().stream()
                .map(p -> DuelResponse.PlayerScoreResponse.builder()
                        .userId(p.getUser().getId())
                        .username(p.getUser().getUsername())
                        .score(p.getScore()).build())
                .toList();
        return DuelResponse.builder()
                .id(d.getId())
                .topicTitle(d.getTopic().getTitle())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .startedAt(d.getStartedAt())
                .finishedAt(d.getFinishedAt())
                .winnerUsername(d.getWinner() != null ? d.getWinner().getUsername() : null)
                .players(players)
                .build();
    }
}
