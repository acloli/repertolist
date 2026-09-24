"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { createSong } from "@/lib/api";
import type { SongStatus } from "@/types";

const KEY_OPTIONS = [
  "+7",
  "+6",
  "+5",
  "+4",
  "+3",
  "+2",
  "+1",
  "原曲",
  "-1",
  "-2",
  "-3",
  "-4",
  "-5",
  "-6",
  "-7",
] as const;

export default function NewSongPage() {
  const router = useRouter();

  const [title, setTitle] = useState("");
  const [artist, setArtist] = useState("");
  const [musicKey, setMusicKey] = useState("原曲");
  const [status, setStatus] = useState<SongStatus>("ready");
  const [memo, setMemo] = useState("");

  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!title.trim()) {
      setError("曲タイトルを入力してください");
      return;
    }
    if (title.length > 100) {
      setError("曲タイトルは100文字以内で入力してください");
      return;
    }
    if (artist.length > 100) {
      setError("アーティスト名は100文字以内で入力してください");
      return;
    }
    if (musicKey.length > 20) {
      setError("キー設定は20文字以内で入力してください");
      return;
    }
    if (memo.length > 500) {
      setError("メモは500文字以内で入力してください");
      return;
    }

    setSubmitting(true);
    try {
      await createSong({
        title: title.trim(),
        artist: artist.trim() || undefined,
        musicKey: musicKey.trim() || undefined,
        status: status || undefined,
        memo: memo.trim() || undefined,
      });

      router.push("/songs");
      router.refresh();
    } catch (e) {
      setError(e instanceof Error ? e.message : "登録に失敗しました");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="song-form-container">
      <h2 className="song-form-title">曲の新規登録</h2>

      <form onSubmit={handleSubmit} className="song-form">
        <div className="song-form__group">
          <label htmlFor="title" className="song-form__label">
            曲タイトル（必須）
          </label>
          <input
            id="title"
            type="text"
            className="song-form__input"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            maxLength={100}
            placeholder="例：アイドル"
            disabled={submitting}
          />
          <span className="song-form__counter">{title.length} / 100</span>
        </div>

        <div className="song-form__group">
          <label htmlFor="artist" className="song-form__label">
            アーティスト名
          </label>
          <input
            id="artist"
            type="text"
            className="song-form__input"
            value={artist}
            onChange={(e) => setArtist(e.target.value)}
            maxLength={100}
            placeholder="例：YOASOBI"
            disabled={submitting}
          />
        </div>

        <div className="song-form__group">
          <label htmlFor="musicKey" className="song-form__label">
            キー設定
          </label>
          <select
            id="musicKey"
            className="song-form__select"
            value={musicKey}
            onChange={(e) => setMusicKey(e.target.value)}
            disabled={submitting}
          >
            {KEY_OPTIONS.map((key) => (
              <option key={key} value={key}>
                {key}
              </option>
            ))}
          </select>
        </div>

        <div className="song-form__group">
          <label htmlFor="status" className="song-form__label">
            ステータス
          </label>
          <select
            id="status"
            className="song-form__select"
            value={status}
            onChange={(e) => setStatus(e.target.value as SongStatus)}
            disabled={submitting}
          >
            <option value="ready">持ち曲</option>
            <option value="practicing">練習中</option>
            <option value="pending">保留</option>
          </select>
        </div>

        <div className="song-form__group">
          <label htmlFor="memo" className="song-form__label">
            メモ
          </label>
          <textarea
            id="memo"
            className="song-form__textarea"
            value={memo}
            onChange={(e) => setMemo(e.target.value)}
            maxLength={500}
            rows={3}
            placeholder="歌唱のコツや配信でのメモ"
            disabled={submitting}
          />
          <span className="song-form__counter">{memo.length} / 500</span>
        </div>

        {error && <p className="song-form__error">{error}</p>}

        <div className="song-form__buttons">
          <button
            type="submit"
            className="button button--primary"
            disabled={submitting || !title.trim()}
          >
            {submitting ? "登録中..." : "登録する"}
          </button>
          <Link href="/songs" className="button">
            キャンセル
          </Link>
        </div>
      </form>
    </div>
  );
}
