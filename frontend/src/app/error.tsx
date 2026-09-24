"use client";

import { useEffect } from "react";

export default function Error({ error, reset }: { error: Error & { digest?: string }; reset: () => void; }) {
  useEffect(() => { console.error(error); }, [error]);
  return (
    <div className="error">
      <h2 className="error__title">エラーが発生しました</h2>
      <p className="error__message">
        {error.message || "データの取得に失敗しました。サーバーが起動しているか確認してください。"}
      </p>
      <button type="button" className="button button--primary error__button" onClick={() => reset()}>
        再試行する
      </button>
    </div>
  );
}
