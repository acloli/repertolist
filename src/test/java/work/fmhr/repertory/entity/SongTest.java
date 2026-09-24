package work.fmhr.repertory.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SongTest {

    @Test
    @DisplayName("新規作成用コンストラクタとGetterの確認")
    void testConstructorAndGetters() {
        Song song = new Song("残酷な天使のテーゼ", "高橋洋子", "原曲", "ready", "持ち曲メモ");

        assertThat(song.getId()).isNull();
        assertThat(song.getTitle()).isEqualTo("残酷な天使のテーゼ");
        assertThat(song.getArtist()).isEqualTo("高橋洋子");
        assertThat(song.getMusicKey()).isEqualTo("原曲");
        assertThat(song.getStatus()).isEqualTo("ready");
        assertThat(song.getMemo()).isEqualTo("持ち曲メモ");
        assertThat(song.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("全フィールドコンストラクタの確認")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Song song = new Song(1L, "アイドル", "YOASOBI", "+2", "practicing", "練習中メモ", now);

        assertThat(song.getId()).isEqualTo(1L);
        assertThat(song.getTitle()).isEqualTo("アイドル");
        assertThat(song.getArtist()).isEqualTo("YOASOBI");
        assertThat(song.getMusicKey()).isEqualTo("+2");
        assertThat(song.getStatus()).isEqualTo("practicing");
        assertThat(song.getMemo()).isEqualTo("練習中メモ");
        assertThat(song.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Setterによる値の更新確認")
    void testSetters() {
        Song song = new Song();
        LocalDateTime now = LocalDateTime.now();

        song.setId(10L);
        song.setTitle("Subtitle");
        song.setArtist("Official髭男dism");
        song.setMusicKey("-1");
        song.setStatus("pending");
        song.setMemo("保留メモ");
        song.setCreatedAt(now);

        assertThat(song.getId()).isEqualTo(10L);
        assertThat(song.getTitle()).isEqualTo("Subtitle");
        assertThat(song.getArtist()).isEqualTo("Official髭男dism");
        assertThat(song.getMusicKey()).isEqualTo("-1");
        assertThat(song.getStatus()).isEqualTo("pending");
        assertThat(song.getMemo()).isEqualTo("保留メモ");
        assertThat(song.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("onCreateでcreatedAtが自動設定される確認")
    void testOnCreate() {
        Song song = new Song("タイトル", "アーティスト", "原曲", "ready", "メモ");
        song.onCreate();

        assertThat(song.getCreatedAt()).isNotNull();
    }
}
