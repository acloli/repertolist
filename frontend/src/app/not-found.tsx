import Link from "next/link";

export default function NotFound() {
  return (
    <div className="error">
      <h2 className="error__title">ページが見つかりません（404）</h2>
      <p className="error__message">
        お探しの曲またはページは存在しないか、削除された可能性があります。
      </p>
      <Link href="/songs" className="button button--primary error__button">
        曲一覧へ戻る
      </Link>
    </div>
  );
}
