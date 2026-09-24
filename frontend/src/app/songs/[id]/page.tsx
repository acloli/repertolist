import { notFound } from "next/navigation";
import { fetchSong } from "@/lib/api";
import SongDetailForm from "./SongDetailForm";

type Props = {
  params: Promise<{ id: string }>;
};

export default async function SongDetailPage({ params }: Props) {
  const { id } = await params;
  const songId = Number(id);

  if (Number.isNaN(songId)) {
    notFound();
  }

  let song;
  try {
    song = await fetchSong(songId);
  } catch {
    notFound();
  }

  return <SongDetailForm song={song} />;
}
