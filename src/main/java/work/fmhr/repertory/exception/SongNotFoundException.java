package work.fmhr.repertory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SongNotFoundException extends RuntimeException {

    private final Long songId;

    public SongNotFoundException(Long songId) {
        super("指定された曲が見つかりません (id=" + songId + ")");
        this.songId = songId;
    }

    public Long getSongId() {
        return songId;
    }
}
