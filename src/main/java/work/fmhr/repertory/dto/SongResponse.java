package work.fmhr.repertory.dto;

import work.fmhr.repertory.entity.Song;

import java.time.LocalDateTime;

public record SongResponse(
    Long id,
    String title,
    String artist,
    String musicKey,
    String status,
    String memo,
    LocalDateTime createdAt
) {
    public static SongResponse from(Song song) {
        return new SongResponse(
            song.getId(),
            song.getTitle(),
            song.getArtist(),
            song.getMusicKey(),
            song.getStatus(),
            song.getMemo(),
            song.getCreatedAt()
        );
    }
}
