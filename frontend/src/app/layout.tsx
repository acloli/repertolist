import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "配信レパートリー管理",
  description: "配信者向けレパートリー管理ツール",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ja">
      <body>
        <header className="header">
          <h1 className="header-title">配信レパートリー管理</h1>
          <nav className="header-nav">
            <Link href="/" className="header-nav__link">ホーム</Link>
            <Link href="/songs" className="header-nav__link">曲一覧</Link>
          </nav>
        </header>

        <main className="container">{children}</main>

        <footer className="footer">
          <p className="footer-text">&copy; 2026 fmhr</p>
        </footer>
      </body>
    </html>
  );
}
