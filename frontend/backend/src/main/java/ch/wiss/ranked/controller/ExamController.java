package ch.wiss.ranked.controller;

import ch.wiss.ranked.dto.request.SubmitAnswerRequest;
import ch.wiss.ranked.dto.response.AnswerFeedbackResponse;
import ch.wiss.ranked.dto.response.ExamAttemptResponse;
import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/{examId}/start")
    public ResponseEntity<Map<String, Long>> startExam(
            @AuthenticationPrincipal User user,
            @PathVariable Long examId) {
        Long attemptId = examService.startExam(user.getId(), examId);
        return ResponseEntity.ok(Map.of("attemptId", attemptId));
    }

    @PostMapping("/attempts/{attemptId}/answer")
    public ResponseEntity<AnswerFeedbackResponse> submitAnswer(
            @AuthenticationPrincipal User user,
            @PathVariable Long attemptId,
            @Valid @RequestBody SubmitAnswerRequest req) {
        return ResponseEntity.ok(examService.submitAnswer(user.getId(), attemptId, req));
    }

    @PostMapping("/attempts/{attemptId}/finish")
    public ResponseEntity<ExamAttemptResponse> finishExam(
            @AuthenticationPrincipal User user,
            @PathVariable Long attemptId) {
        return ResponseEntity.ok(examService.finishExam(user.getId(), attemptId));
    }

    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<ExamAttemptResponse> getAttempt(
            @AuthenticationPrincipal User user,
            @PathVariable Long attemptId) {
        return ResponseEntity.ok(examService.getAttempt(user.getId(), attemptId));
    }
}
