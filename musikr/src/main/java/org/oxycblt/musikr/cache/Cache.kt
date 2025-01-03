/*
 * Copyright (c) 2024 Auxio Project
 * Cache.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.musikr.cache

import android.content.Context
import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.pipeline.RawSong

abstract class Cache {
    internal abstract suspend fun read(file: DeviceFile, storedCovers: StoredCovers): CacheResult

    internal abstract suspend fun write(song: RawSong)

    companion object {
        fun from(context: Context): Cache = CacheImpl(CacheDatabase.from(context).cachedSongsDao())
    }
}

internal sealed interface CacheResult {
    data class Hit(val song: RawSong) : CacheResult

    data class Miss(val file: DeviceFile) : CacheResult
}

private class CacheImpl(private val cacheInfoDao: CacheInfoDao) : Cache() {
    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers) =
        cacheInfoDao.selectSong(file.uri.toString(), file.lastModified)?.let {
            CacheResult.Hit(it.intoRawSong(file, storedCovers))
        } ?: CacheResult.Miss(file)

    override suspend fun write(song: RawSong) =
        cacheInfoDao.updateSong(CachedSong.fromRawSong(song))
}

class WriteOnlyCache(private val inner: Cache) : Cache() {
    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers) = CacheResult.Miss(file)

    override suspend fun write(song: RawSong) = inner.write(song)
}
