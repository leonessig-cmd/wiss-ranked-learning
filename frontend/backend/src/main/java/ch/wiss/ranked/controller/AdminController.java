package ch.wiss.ranked.controller;

import ch.wiss.ranked.dto.request.CreateExamRequest;
import ch.wiss.ranked.dto.request.CreateQuestionRequest;
import ch.wiss.ranked.entity.*;
import ch.wiss.ranked.enums.Difficulty;
import ch.wiss.ranked.exception.ResourceNotFoundException;
import ch.wiss.ranked.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final SubjectRepository subjectRepo;
    private final TopicRepository topicRepo;
    private final QuestionRepository questionRepo;
    private final AnswerRepository answerRepo;
    private final ExamRepository examRepo;
    private final RankRepository rankRepo;

    // --- Subjects ---
    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectRepo.save(subject));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectRepo.findAll());
    }

    // --- Topics ---
    @PostMapping("/topics")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicRepo.save(topic));
    }

    @GetMapping("/subjects/{subjectId}/topics")
    public ResponseEntity<List<Topic>> getTopicsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(topicRepo.findBySubjectId(subjectId));
    }

    // --- Questions ---
    @PostMapping("/questions")
    public ResponseEntity<Map<String, Long>> createQuestion(@Valid @RequestBody CreateQuestionRequest req) {
        Topic topic = topicRepo.findById(req.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Thema nicht gefunden"));

        Question question = Question.builder()
                .topic(topic)
                .questionText(req.getQuestionText())
                .difficulty(req.getDifficulty() != null ? req.getDifficulty() : Difficulty.EASY)
                .points(req.getPoints())
                .build();
        question = questionRepo.save(question);

        for (CreateQuestionRequest.AnswerOption opt : req.getAnswers()) {
            Answer answer = Answer.builder()
                    .question(question)
                    .answerText(opt.getAnswerText())
                    .correct(opt.isCorrect())
                    .build();
            answerRepo.save(answer);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("questionId", question.getId()));
    }

    // --- Exams ---
    @PostMapping("/exams")
    public ResponseEntity<Map<String, Long>> createExam(@Valid @RequestBody CreateExamRequest req) {
        List<Question> questions = req.getQuestionIds().stream()
                .map(id -> questionRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Frage " + id + " nicht gefunden")))
                .toList();

        Exam exam = Exam.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .durationMinutes(req.getDurationMinutes())
                .questions(questions)
                .build();
        exam = examRepo.save(exam);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("examId", exam.getId()));
    }

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepo.findAll());
    }

    // --- Ranks ---
    @PostMapping("/ranks")
    public ResponseEntity<Rank> createRank(@RequestBody Rank rank) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rankRepo.save(rank));
    }

    @GetMapping("/ranks")
    public ResponseEntity<List<Rank>> getAllRanks() {
        return ResponseEntity.ok(rankRepo.findAll());
    }
}
