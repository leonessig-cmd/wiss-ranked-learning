package ch.wiss.ranked.controller;

import ch.wiss.ranked.dto.request.SubmitAnswerRequest;
import ch.wiss.ranked.dto.response.*;
import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/start/{topicId}")
    public ResponseEntity<Map<String, Long>> startQuiz(
            @AuthenticationPrincipal User user,
            @PathVariable Long topicId) {
        Long attemptId = quizService.startQuiz(user.getId(), topicId);
        return ResponseEntity.ok(Map.of("attemptId", attemptId));
    }

    @GetMapping("/topics/{topicId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getQuestionsForTopic(topicId, limit));
    }

    @GetMapping("/modules/{moduleNumber}/ranks/{rankName}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestionsForModuleAndRank(
            @PathVariable String moduleNumber,
            @PathVariable String rankName,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(
                quizService.getQuestionsForModuleAndRank(moduleNumber, rankName, limit)
        );
    }

    @PostMapping("/attempts/{attemptId}/answer")
    public ResponseEntity<AnswerFeedbackResponse> submitAnswer(
            @AuthenticationPrincipal User user,
            @PathVariable Long attemptId,
            @Valid @RequestBody SubmitAnswerRequest req) {
        return ResponseEntity.ok(quizService.submitAnswer(user.getId(), attemptId, req));
    }

    @PostMapping("/attempts/{attemptId}/finish")
    public ResponseEntity<QuizResultResponse> finishQuiz(
            @AuthenticationPrincipal User user,
            @PathVariable Long attemptId) {
        return ResponseEntity.ok(quizService.finishQuiz(user.getId(), attemptId));
    }
}