import Link from "next/link";
import { fetchSongs } from "@/lib/api";
import { SONG_STATUS_LABELS, type SongStatus } from "@/types";

type Props = {
  searchParams?: Promise<{ status?: string }>;
};

export default async function SongsPage({ searchParams }: Props) {
  const resolvedSearchParams = searchParams ? await searchParams : {};
  const statusFilter = resolvedSearchParams.status;

  const allSongs = await fetchSongs();

  // 統計情報
  const allCount = allSongs.length;
  const readyCount = allSongs.filter((song) => song.status === "ready").length;
  const practicingCount = allSongs.filter((song) => song.status === "practicing").length;
  const pendingCount = allSongs.filter((song) => song.status === "pending").length;

  // 絞り込み
  const filteredSongs = allSongs.filter((song) => {
    if (!statusFilter || statusFilter === "all") return true;
    return song.status === statusFilter;
  });

  return (
    <main className="songs-page">
      <h3 className="songs-page__title">曲一覧</h3>

      <div className="songs-page__stats">
        <p>全{allCount}件</p>
        <p>持ち曲{readyCount}件</p>
        <p>練習中{practicingCount}件</p>
        <p>保留{pendingCount}件</p>
      </div>

      <div className="songs-page__filter">
        <Link className="songs-page__filter-link" href="?status=all">すべて</Link>
        <Link className="songs-page__filter-link" href="?status=ready">持ち曲</Link>
        <Link className="songs-page__filter-link" href="?status=practicing">練習中</Link>
        <Link className="songs-page__filter-link" href="?status=pending">保留</Link>
      </div>

      {filteredSongs.length === 0 ? (
        <p className="songs-page__list__empty">曲が登録されていません。新規登録してください。</p>
      ) : (
        <ul className="songs-page__list">
          {filteredSongs.map((song) => {
            const statusLabel =
              SONG_STATUS_LABELS[song.status as SongStatus] ?? song.status ?? "-";
            return (
              <li key={song.id} className="song-item">
                <Link href={`/songs/${song.id}`} className="song-item__title">
                  {song.title}
                </Link>
                <span className="song-item__artist">
                  {song.artist || "-"}
                </span>
                <span className="song-item__key">
                  {song.musicKey ? `キー:${song.musicKey}` : "-"}
                </span>
                <span className="song-item__status">
                  {statusLabel}
                </span>
                <span className="song-item__button">
                  <Link href={`/songs/${song.id}`} className="button">
                    詳細
                  </Link>
                </span>
              </li>
            );
          })}
        </ul>
      )}
    </main>
  );
}
