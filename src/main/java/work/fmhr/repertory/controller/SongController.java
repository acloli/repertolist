package work.fmhr.repertory.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.fmhr.repertory.dto.SongRequest;
import work.fmhr.repertory.dto.SongResponse;
import work.fmhr.repertory.dto.SongSummaryResponse;
import work.fmhr.repertory.service.SongService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // 曲一覧取得: GET /api/v1/songs または /api/v1/songs?status=ready
    @GetMapping
    public List<SongResponse> list(@RequestParam(required = false) String status) {
        return songService.findAll(status);
    }

    // ステータス別集計取得: GET /api/v1/songs/summary
    @GetMapping("/summary")
    public SongSummaryResponse summary() {
        return songService.getSummary();
    }

    // 曲の1件取得: GET /api/v1/songs/{id}
    @GetMapping("/{id}")
    public SongResponse detail(@PathVariable Long id) {
        return songService.findById(id);
    }

    // 曲の新規登録: POST /api/v1/songs
    @PostMapping
    public ResponseEntity<SongResponse> create(@Valid @RequestBody SongRequest request) {
        SongResponse created = songService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/songs/" + created.id()))
                .body(created);
    }

    // 曲情報の更新: PUT /api/v1/songs/{id}
    @PutMapping("/{id}")
    public SongResponse update(@PathVariable Long id, @Valid @RequestBody SongRequest request) {
        return songService.update(id, request);
    }

    // 曲の削除: DELETE /api/v1/songs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        songService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
