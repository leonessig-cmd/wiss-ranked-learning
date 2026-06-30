package ch.wiss.ranked.controller;

import ch.wiss.ranked.dto.request.JoinDuelRequest;
import ch.wiss.ranked.dto.request.SubmitAnswerRequest;
import ch.wiss.ranked.dto.response.AnswerFeedbackResponse;
import ch.wiss.ranked.dto.response.DuelResponse;
import ch.wiss.ranked.dto.response.QuestionResponse;
import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.service.DuelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/duels")
@RequiredArgsConstructor
public class DuelController {

    private final DuelService duelService;

    @PostMapping("/join")
    public ResponseEntity<DuelResponse> joinOrCreate(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JoinDuelRequest req) {
        return ResponseEntity.ok(duelService.joinOrCreateDuel(user.getId(), req.getTopicId()));
    }

    @GetMapping("/{duelId}")
    public ResponseEntity<DuelResponse> getDuel(@PathVariable Long duelId) {
        return ResponseEntity.ok(duelService.getDuel(duelId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<DuelResponse>> getMyDuels(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(duelService.getMyDuels(user.getId()));
    }

    @GetMapping("/{duelId}/questions")
    public ResponseEntity<List<QuestionResponse>> getDuelQuestions(
            @PathVariable Long duelId,
            @RequestParam Long topicId) {
        return ResponseEntity.ok(duelService.getDuelQuestions(duelId, topicId));
    }

    @PostMapping("/{duelId}/answer")
    public ResponseEntity<AnswerFeedbackResponse> submitAnswer(
            @AuthenticationPrincipal User user,
            @PathVariable Long duelId,
            @Valid @RequestBody SubmitAnswerRequest req) {
        return ResponseEntity.ok(duelService.submitAnswer(user.getId(), duelId, req));
    }
}
