package work.fmhr.repertory.dto;

public record SongSummaryResponse(
    long totalCount,
    long readyCount,
    long practicingCount,
    long pendingCount
) {}
