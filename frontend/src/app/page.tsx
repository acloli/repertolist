import Link from "next/link";
import { fetchSongSummary } from "@/lib/api";

export default async function HomePage() {
  let summary = {
    totalCount: 0,
    readyCount: 0,
    practicingCount: 0,
    pendingCount: 0,
  };

  try {
    summary = await fetchSongSummary();
  } catch {
  }

  return (
    <div className="home">
      <p className="home__description">
        歌枠配信や動画で歌う持ち曲・練習曲のキー設定やメモを一元管理できるツールです。
      </p>

      {/* 現在の登録状況サマリー */}
      <div className="home__summary">
        <div className="stat-item">
          <span className="stat-label">全曲数:</span>
          <span className="stat-value">{summary.totalCount}曲</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">持ち曲:</span>
          <span className="stat-value">{summary.readyCount}曲</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">練習中:</span>
          <span className="stat-value">{summary.practicingCount}曲</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">保留:</span>
          <span className="stat-value">{summary.pendingCount}曲</span>
        </div>
      </div>

      {/* アクションボタン */}
      <div className="home__actions">
        <Link href="/songs" className="button button--primary">
          曲一覧・管理へ
        </Link>
        <Link href="/songs/new" className="button">
          ＋ 新しい曲を追加
        </Link>
      </div>

      {/* アプリの特徴・使い方 */}
      <div className="home__features">
        <h3 className="home__features-title">このアプリでできること</h3>
        <ul className="home__features-list">
          <li>
            <strong>ステータス管理</strong>: 持ち曲・練習中・保留の3段階でレパートリーを整理
          </li>
          <li>
            <strong>キー設定の記録</strong>: 原曲キーや自分に合った移調キー（+2, -1など）を保存
          </li>
          <li>
            <strong>メモ機能</strong>: 歌唱のコツや配信時の注意点を曲ごとに記録
          </li>
          <li>
            <strong>スムーズな選曲</strong>: 配信中のリクエスト対応や歌枠の選曲をサポート
          </li>
        </ul>
      </div>
    </div>
  );
}
