/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import org.oxycblt.auxio.image.AlbumArtFetcher
import org.oxycblt.auxio.image.ArtistImageFetcher
import org.oxycblt.auxio.image.CrossfadeFactory
import org.oxycblt.auxio.image.GenreImageFetcher
import org.oxycblt.auxio.image.MusicKeyer
import org.oxycblt.auxio.settings.SettingsManager

@Suppress("UNUSED")
class AuxioApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        // Init SettingsManager here so that there aren't any race conditions
        // [e.g PlaybackService gets SettingsManager before activity can init SettingsManager]
        SettingsManager.init(applicationContext)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .components {
                add(AlbumArtFetcher.SongFactory())
                add(AlbumArtFetcher.AlbumFactory())
                add(ArtistImageFetcher.Factory())
                add(GenreImageFetcher.Factory())
                add(MusicKeyer())
            }
            .transitionFactory(CrossfadeFactory())
            .diskCachePolicy(CachePolicy.DISABLED) // Not downloading anything, so no disk-caching
            .build()
    }
}
