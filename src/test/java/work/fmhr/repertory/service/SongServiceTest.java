package work.fmhr.repertory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import work.fmhr.repertory.dto.SongRequest;
import work.fmhr.repertory.dto.SongResponse;
import work.fmhr.repertory.dto.SongSummaryResponse;
import work.fmhr.repertory.entity.Song;
import work.fmhr.repertory.exception.SongNotFoundException;
import work.fmhr.repertory.repository.SongRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song sampleSong;

    @BeforeEach
    void setUp() {
        sampleSong = new Song(1L, "アイドル", "YOASOBI", "+2", "ready", "持ち曲メモ", LocalDateTime.now());
    }

    @Test
    @DisplayName("全曲一覧取得（ステータス指定なし）")
    void testFindAllWithoutStatus() {
        when(songRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleSong));

        List<SongResponse> result = songService.findAll(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("アイドル");
        verify(songRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("ステータス絞り込みでの一覧取得")
    void testFindAllWithStatus() {
        when(songRepository.findByStatusOrderByCreatedAtDesc("ready")).thenReturn(List.of(sampleSong));

        List<SongResponse> result = songService.findAll("ready");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("アイドル");
        verify(songRepository, times(1)).findByStatusOrderByCreatedAtDesc("ready");
    }

    @Test
    @DisplayName("IDによる曲の1件取得（正常系）")
    void testFindByIdSuccess() {
        when(songRepository.findById(1L)).thenReturn(Optional.of(sampleSong));

        SongResponse result = songService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("アイドル");
    }

    @Test
    @DisplayName("存在しないIDの曲取得時にSongNotFoundExceptionが発生すること")
    void testFindByIdNotFound() {
        when(songRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.findById(999L))
                .isInstanceOf(SongNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("新規登録が成功すること")
    void testCreateSuccess() {
        SongRequest request = new SongRequest("新時代", "Ado", "原曲", "ready", "メモ");
        Song savedSong = new Song(2L, "新時代", "Ado", "原曲", "ready", "メモ", LocalDateTime.now());

        when(songRepository.save(any(Song.class))).thenReturn(savedSong);

        SongResponse result = songService.create(request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.title()).isEqualTo("新時代");
        verify(songRepository, times(1)).save(any(Song.class));
    }

    @Test
    @DisplayName("曲情報更新が成功すること")
    void testUpdateSuccess() {
        SongRequest request = new SongRequest("アイドル (English Ver.)", "YOASOBI", "+2", "ready", "英語バージョン");
        when(songRepository.findById(1L)).thenReturn(Optional.of(sampleSong));
        when(songRepository.save(any(Song.class))).thenReturn(sampleSong);

        SongResponse result = songService.update(1L, request);

        assertThat(result.title()).isEqualTo("アイドル (English Ver.)");
        verify(songRepository, times(1)).save(sampleSong);
    }

    @Test
    @DisplayName("存在しないIDの曲更新時にSongNotFoundExceptionが発生すること")
    void testUpdateNotFound() {
        SongRequest request = new SongRequest("タイトル", "歌手", "原曲", "ready", "メモ");
        when(songRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.update(999L, request))
                .isInstanceOf(SongNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("曲の削除が成功すること")
    void testDeleteSuccess() {
        when(songRepository.existsById(1L)).thenReturn(true);

        songService.delete(1L);

        verify(songRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("存在しないIDの曲削除時にSongNotFoundExceptionが発生すること")
    void testDeleteNotFound() {
        when(songRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> songService.delete(999L))
                .isInstanceOf(SongNotFoundException.class)
                .hasMessageContaining("999");

        verify(songRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("集計情報の取得が成功すること")
    void testGetSummary() {
        when(songRepository.count()).thenReturn(10L);
        when(songRepository.countByStatus("ready")).thenReturn(5L);
        when(songRepository.countByStatus("practicing")).thenReturn(3L);
        when(songRepository.countByStatus("pending")).thenReturn(2L);

        SongSummaryResponse summary = songService.getSummary();

        assertThat(summary.totalCount()).isEqualTo(10L);
        assertThat(summary.readyCount()).isEqualTo(5L);
        assertThat(summary.practicingCount()).isEqualTo(3L);
        assertThat(summary.pendingCount()).isEqualTo(2L);
    }
}
