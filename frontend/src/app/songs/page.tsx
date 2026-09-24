import { fetchSongs } from "@/lib/api";
import SongListTable from "./SongListTable";

export default async function SongsPage() {
  const songs = await fetchSongs();

  return <SongListTable initialSongs={songs} />;
}
