package playlist

import api.google.sheets.SheetDimension

class SongLocationRegistry(songsByDimension: Map<SheetDimension, Song>) {
    private val songLocations: Map<SheetDimension, SongWithoutMetadata> =
        songsByDimension.mapValues { it.value.withoutMetadata() }

    val rowStart = songLocations.keys.minOrNull()
    val rowEnd = songLocations.keys.maxOrNull()

    operator fun get(row: SheetDimension): SongWithoutMetadata? = songLocations[row]
}
