package work.fmhr.repertory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import work.fmhr.repertory.entity.Song;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    // ステータスで絞り込み
    List<Song> findByStatus(String status);

    // ステータス別の件数を取得
    long countByStatus(String status);

    // 登録日時の降順で全件取得
    List<Song> findAllByOrderByCreatedAtDesc();

    // 曲タイトルの昇順で全件取得
    List<Song> findAllByOrderByTitleAsc();

    // ステータスで絞り込み、登録日時の降順で取得
    List<Song> findByStatusOrderByCreatedAtDesc(String status);

    // 曲名またはアーティスト名にキーワードが含まれる曲を検索
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(String titleKeyword, String artistKeyword);
}
