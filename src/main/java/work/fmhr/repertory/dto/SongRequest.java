package work.fmhr.repertory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SongRequest(
    @NotBlank(message = "曲タイトルは必須です")
    @Size(max = 100, message = "曲タイトルは100文字以内で入力してください")
    String title,

    @Size(max = 100, message = "アーティスト名は100文字以内で入力してください")
    String artist,

    @Size(max = 20, message = "キー設定は20文字以内で入力してください")
    String musicKey,

    @Size(max = 20, message = "ステータスは20文字以内で入力してください")
    String status,

    @Size(max = 500, message = "メモは500文字以内で入力してください")
    String memo
) {}
