"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { deleteSong } from "@/lib/api";
import { SONG_STATUS_LABELS, type Song, type SongStatus } from "@/types";

export default function SongListTable({ initialSongs }: { initialSongs: Song[] }) {
  const router = useRouter();

  const [statusFilter, setStatusFilter] = useState<string>("all");
  const [searchKeyword, setSearchKeyword] = useState<string>("");
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const filteredSongs = initialSongs.filter((song) => {
    // ステータス絞り込み
    if (statusFilter !== "all" && song.status !== statusFilter) {
      return false;
    }

    // キーワード検索
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.trim().toLowerCase();
      const matchTitle = song.title.toLowerCase().includes(keyword);
      const matchArtist = song.artist?.toLowerCase().includes(keyword) ?? false;
      if (!matchTitle && !matchArtist) {
        return false;
      }
    }

    return true;
  });

  // 削除処理
  const handleDelete = async (song: Song) => {
    if (!confirm(`「${song.title}」を削除しますか？`)) return;

    setDeletingId(song.id);
    try {
      await deleteSong(song.id);
      router.refresh();
    } catch (e) {
      alert(e instanceof Error ? e.message : "削除に失敗しました");
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <main className="songs-page" style={{ maxWidth: "750px" }}>
      <h3 className="songs-page__title">配信レパートリー管理</h3>

      <div className="songs-toolbar">
        {/* ボタン列 */}
        <div className="songs-toolbar__row">
          <Link href="/songs/new" className="button button--primary">
            ＋ 新規曲を追加
          </Link>
        </div>

        {/* 絞り込み & 検索行 */}
        <div className="songs-toolbar__row">
          <label style={{ fontSize: "0.9rem" }}>
            絞り込み:{" "}
            <select
              className="filter-select"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="all">すべて</option>
              <option value="ready">持ち曲</option>
              <option value="practicing">練習中</option>
              <option value="pending">保留</option>
            </select>
          </label>

          <label style={{ fontSize: "0.9rem", flex: 1, display: "flex", alignItems: "center" }}>
            検索:{" "}
            <input
              type="text"
              className="search-input"
              style={{ marginLeft: "0.5rem", flex: 1 }}
              placeholder="曲名・アーティスト名..."
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
            />
          </label>
        </div>

        {/* 登録曲数表示 */}
        <div className="songs-toolbar__count">
          登録曲数: 全 {filteredSongs.length} 件
          {statusFilter !== "all" || searchKeyword.trim() ? (
            <span style={{ fontSize: "0.85rem", color: "#888", fontWeight: "normal", marginLeft: "0.5rem" }}>
              （全体 {initialSongs.length} 件）
            </span>
          ) : null}
        </div>
      </div>

      {/* 一覧テーブル */}
      <table className="songs-table">
        <thead>
          <tr>
            <th style={{ width: "50px" }}>順番</th>
            <th>曲名 / アーティスト</th>
            <th style={{ width: "80px" }}>キー</th>
            <th style={{ width: "80px" }}>状態</th>
            <th style={{ width: "110px" }}>操作</th>
          </tr>
        </thead>
        <tbody>
          {filteredSongs.length === 0 ? (
            <tr>
              <td colSpan={5} className="songs-page__list__empty">
                該当する曲がありません
              </td>
            </tr>
          ) : (
            filteredSongs.map((song, index) => {
              const statusLabel =
                SONG_STATUS_LABELS[song.status as SongStatus] ?? song.status ?? "-";
              return (
                <tr key={song.id}>
                  <td>{index + 1}</td>
                  <td className="songs-table__title-cell">
                    <span style={{ fontWeight: "bold" }}>{song.title}</span>
                    {song.artist && <span style={{ color: "#666" }}> / {song.artist}</span>}
                  </td>
                  <td>{song.musicKey || "原曲"}</td>
                  <td>{statusLabel}</td>
                  <td>
                    <div className="songs-table__actions">
                      <Link href={`/songs/${song.id}`} className="button button--small">
                        編集
                      </Link>
                      <button
                        type="button"
                        onClick={() => handleDelete(song)}
                        className="button button--small button--danger"
                        disabled={deletingId === song.id}
                        title="削除"
                      >
                        x
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </main>
  );
}
