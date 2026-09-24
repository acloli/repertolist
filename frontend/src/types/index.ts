// 曲のステータス種別
// ready: 持ち曲
// practicing: 練習中
// pending: 保留
export type SongStatus = 'ready' | 'practicing' | 'pending';

// ステータス表示ラベルの定数マッピング
export const SONG_STATUS_LABELS: Record<SongStatus, string> = {
  ready: '持ち曲',
  practicing: '練習中',
  pending: '保留',
} as const;

// APIから受け取る曲データ
export type Song = {
  id: number;
  title: string;
  artist: string | null;
  musicKey: string | null;
  status: SongStatus | string;
  memo: string | null;
  createdAt: string;
};

// 曲レスポンスのエイリアス
export type SongResponse = Song;

// APIへ送信する曲データ、新規登録（POST）および更新（PUT）共通
export type SongRequest = {
  title: string;
  artist?: string;
  musicKey?: string;
  status?: SongStatus | string;
  memo?: string;
};

// ステータス別集計レスポンス
export type SongSummaryResponse = {
  totalCount: number;
  readyCount: number;
  practicingCount: number;
  pendingCount: number;
};

// APIのエラーレスポンス
export type ErrorResponse = {
  status: number;
  message: string;
  details: string[];
  timestamp: string;
};
