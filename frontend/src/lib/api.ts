import type { Song, SongRequest, SongSummaryResponse, ErrorResponse } from '@/types';

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

// バックエンドのErrorResponseを読み取る
async function extractErrorMessage(res: Response, defaultMessage: string): Promise<string> {
  let message = `${defaultMessage}（${res.status}）`;
  try {
    const error: ErrorResponse = await res.json();
    if (error.details && error.details.length > 0) {
      message = error.details.join('\n');
    } else if (error.message) {
      message = error.message;
    }
  } catch {}
  return message;
}

// 曲一覧取得
// GET /api/v1/songs
export async function fetchSongs(status?: string): Promise<Song[]> {
  const url = new URL(`${BASE_URL}/api/v1/songs`);
  if (status) {
    url.searchParams.set('status', status);
  }

  const res = await fetch(url.toString(), {
    cache: 'no-store',
  });

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res, '曲一覧の取得に失敗しました'));
  }

  return res.json();
}

// 曲の1件取得
// GET /api/v1/songs/{id}
export async function fetchSong(id: number): Promise<Song> {
  const res = await fetch(`${BASE_URL}/api/v1/songs/${id}`, {
    cache: 'no-store',
  });

  if (res.status === 404) {
    throw new Error('指定された曲が見つかりません');
  }

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res, '曲の取得に失敗しました'));
  }

  return res.json();
}

// 曲の新規登録
// POST /api/v1/songs
export async function createSong(input: SongRequest): Promise<Song> {
  let res: Response;
  try {
    res = await fetch(`${BASE_URL}/api/v1/songs`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(input),
    });
  } catch {
    throw new Error('サーバーに接続できません。起動状態を確認してください。');
  }

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res, '曲の登録に失敗しました'));
  }

  return res.json();
}

// 曲情報の更新
// PUT /api/v1/songs/{id}
export async function updateSong(id: number, input: SongRequest): Promise<Song> {
  let res: Response;
  try {
    res = await fetch(`${BASE_URL}/api/v1/songs/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(input),
    });
  } catch {
    throw new Error('サーバーに接続できません。起動状態を確認してください。');
  }

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res, '曲の更新に失敗しました'));
  }

  return res.json();
}

// 曲の削除
// DELETE /api/v1/songs/{id}
export async function deleteSong(id: number): Promise<void> {
  let res: Response;
  try {
    res = await fetch(`${BASE_URL}/api/v1/songs/${id}`, {
      method: 'DELETE',
    });
  } catch {
    throw new Error('サーバーに接続できません。起動状態を確認してください。');
  }

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res, '曲の削除に失敗しました'));
  }
}

// ステータス別集計取得
// GET /api/v1/songs/summary
export async function fetchSongSummary(): Promise<SongSummaryResponse> {
  const res = await fetch(`${BASE_URL}/api/v1/songs/summary`, {
    cache: 'no-store',
  });

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res, '集計情報の取得に失敗しました'));
  }

  return res.json();
}

// target/docs.md 向けエイリアス
export const getSongs = fetchSongs;
export const getSong = fetchSong;
