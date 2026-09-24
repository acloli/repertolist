package work.fmhr.repertory.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import work.fmhr.repertory.entity.Song;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SongRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();

        songRepository.save(new Song("アイドル", "YOASOBI", "+2", "ready", "持ち曲1"));
        songRepository.save(new Song("残酷な天使のテーゼ", "高橋洋子", "原曲", "ready", "持ち曲2"));
        songRepository.save(new Song("Subtitle", "Official髭男dism", "-1", "practicing", "練習中1"));
        songRepository.save(new Song("Lemon", "米津玄師", "原曲", "pending", "保留1"));
    }

    @Test
    @DisplayName("ステータスでの絞り込みが正しく動作すること")
    void testFindByStatus() {
        List<Song> readySongs = songRepository.findByStatus("ready");
        assertThat(readySongs).hasSize(2)
                .extracting(Song::getTitle)
                .containsExactlyInAnyOrder("アイドル", "残酷な天使のテーゼ");

        List<Song> practicingSongs = songRepository.findByStatus("practicing");
        assertThat(practicingSongs).hasSize(1)
                .extracting(Song::getTitle)
                .containsExactly("Subtitle");
    }

    @Test
    @DisplayName("ステータス別件数のカウントが正しく動作すること")
    void testCountByStatus() {
        assertThat(songRepository.countByStatus("ready")).isEqualTo(2);
        assertThat(songRepository.countByStatus("practicing")).isEqualTo(1);
        assertThat(songRepository.countByStatus("pending")).isEqualTo(1);
        assertThat(songRepository.countByStatus("unknown")).isEqualTo(0);
        assertThat(songRepository.count()).isEqualTo(4);
    }

    @Test
    @DisplayName("タイトル昇順（五十音順）での全件取得が動作すること")
    void testFindAllByOrderByTitleAsc() {
        List<Song> songs = songRepository.findAllByOrderByTitleAsc();
        assertThat(songs).hasSize(4);
        assertThat(songs.get(0).getTitle()).isEqualTo("Lemon");
        assertThat(songs.get(1).getTitle()).isEqualTo("Subtitle");
        assertThat(songs.get(2).getTitle()).isEqualTo("アイドル");
        assertThat(songs.get(3).getTitle()).isEqualTo("残酷な天使のテーゼ");
    }

    @Test
    @DisplayName("キーワード検索（タイトルまたはアーティスト）が動作すること")
    void testSearchByKeyword() {
        // アーティスト名で検索
        List<Song> songsByArtist = songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase("米津", "米津");
        assertThat(songsByArtist).hasSize(1);
        assertThat(songsByArtist.get(0).getTitle()).isEqualTo("Lemon");

        // 曲名で検索（小文字大文字区別なし）
        List<Song> songsByTitle = songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase("subtitle", "subtitle");
        assertThat(songsByTitle).hasSize(1);
        assertThat(songsByTitle.get(0).getTitle()).isEqualTo("Subtitle");
    }

    @Test
    @DisplayName("ステータス絞り込み + 新着順ソートが動作すること")
    void testFindByStatusOrderByCreatedAtDesc() {
        List<Song> readySongs = songRepository.findByStatusOrderByCreatedAtDesc("ready");
        assertThat(readySongs).hasSize(2);
    }
}
