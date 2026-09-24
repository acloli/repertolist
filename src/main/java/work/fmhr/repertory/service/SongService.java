package work.fmhr.repertory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.fmhr.repertory.dto.SongRequest;
import work.fmhr.repertory.dto.SongResponse;
import work.fmhr.repertory.dto.SongSummaryResponse;
import work.fmhr.repertory.entity.Song;
import work.fmhr.repertory.exception.SongNotFoundException;
import work.fmhr.repertory.repository.SongRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SongService {

    private static final Logger log = LoggerFactory.getLogger(SongService.class);

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    // 曲一覧を取得
    public List<SongResponse> findAll(String status) {
        List<Song> songs;
        if (status != null && !status.isBlank()) {
            songs = songRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            songs = songRepository.findAllByOrderByCreatedAtDesc();
        }
        return songs.stream()
                .map(SongResponse::from)
                .toList();
    }

    // 曲を1件取得
    public SongResponse findById(Long id) {
        return SongResponse.from(getEntityById(id));
    }

    // Entityを1件取得
    public Song getEntityById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new SongNotFoundException(id));
    }

    // 曲の新規登録
    @Transactional
    public SongResponse create(SongRequest request) {
        log.info("曲を登録します: title={}, artist={}", request.title(), request.artist());
        Song song = new Song(
                request.title().trim(),
                request.artist(),
                request.musicKey(),
                request.status(),
                request.memo());
        Song saved = songRepository.save(song);
        log.info("曲を登録しました: id={}, title={}", saved.getId(), saved.getTitle());
        return SongResponse.from(saved);
    }

    // 曲情報の更新
    @Transactional
    public SongResponse update(Long id, SongRequest request) {
        log.info("曲情報を更新します: id={}", id);
        Song song = getEntityById(id);

        song.setTitle(request.title().trim());
        song.setArtist(request.artist());
        song.setMusicKey(request.musicKey());
        song.setStatus(request.status());
        song.setMemo(request.memo());

        Song updated = songRepository.save(song);
        log.info("曲情報を更新しました: id={}, title={}", updated.getId(), updated.getTitle());
        return SongResponse.from(updated);
    }

    // 曲の削除
    @Transactional
    public void delete(Long id) {
        log.info("曲を削除します: id={}", id);
        if (!songRepository.existsById(id)) {
            throw new SongNotFoundException(id);
        }
        songRepository.deleteById(id);
        log.info("曲を削除しました: id={}", id);
    }

    // ステータス別集計情報を取得
    public SongSummaryResponse getSummary() {
        long total = songRepository.count();
        long ready = songRepository.countByStatus("ready");
        long practicing = songRepository.countByStatus("practicing");
        long pending = songRepository.countByStatus("pending");
        return new SongSummaryResponse(total, ready, practicing, pending);
    }
}
