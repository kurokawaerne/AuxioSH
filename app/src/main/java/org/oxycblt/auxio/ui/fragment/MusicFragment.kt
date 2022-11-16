/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.ui.fragment

import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.picker.PickerMode
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

/**
 * A fragment capable of creating menus. Automatically keeps track of and disposes of menus,
 * preventing UI issues and memory leaks.
 * @author OxygenCobalt
 */
abstract class MusicFragment<T : ViewBinding> : ViewBindingFragment<T>() {
    private var currentMenu: PopupMenu? = null

    protected val playbackModel: PlaybackViewModel by androidActivityViewModels()
    protected val navModel: NavigationViewModel by activityViewModels()

    /**
     * Run the UI flow to perform a specific [PickerMode] action with a particular artist from
     * [song].
     */
    fun doArtistDependentAction(song: Song, mode: PickerMode) {
        if (song.artists.size == 1) {
            when (mode) {
                PickerMode.PLAY -> playbackModel.playFromArtist(song, song.artists[0])
                PickerMode.SHOW -> navModel.exploreNavigateTo(song.artists[0])
            }
        } else {
            navModel.mainNavigateTo(
                MainNavigationAction.Directions(
                    MainFragmentDirections.actionPickArtist(song.uid, mode)))
        }
    }

    /** Run the UI flow to navigate to a particular artist from [album]. */
    fun navigateToArtist(album: Album) {
        if (album.artists.size == 1) {
            navModel.exploreNavigateTo(album.artists[0])
        } else {
            navModel.mainNavigateTo(
                MainNavigationAction.Directions(
                    MainFragmentDirections.actionPickArtist(album.uid, PickerMode.SHOW)))
        }
    }

    /**
     * Opens the given menu in context of [song]. Assumes that the menu is only composed of common
     * [Song] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, song: Song) {
        logD("Launching new song menu: ${song.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play_next -> {
                    playbackModel.playNext(song)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(song)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_go_artist -> {
                    doArtistDependentAction(song, PickerMode.SHOW)
                }
                R.id.action_go_album -> {
                    navModel.exploreNavigateTo(song.album)
                }
                R.id.action_song_detail -> {
                    navModel.mainNavigateTo(
                        MainNavigationAction.Directions(
                            MainFragmentDirections.actionShowDetails(song.uid)))
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }

            true
        }
    }

    /**
     * Opens the given menu in context of [album]. Assumes that the menu is only composed of common
     * [Album] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, album: Album) {
        logD("Launching new album menu: ${album.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play -> {
                    playbackModel.play(album)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(album)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(album)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(album)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_go_artist -> {
                    navigateToArtist(album)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }

            true
        }
    }

    /**
     * Opens the given menu in context of [artist]. Assumes that the menu is only composed of common
     * [Artist] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, artist: Artist) {
        logD("Launching new artist menu: ${artist.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play -> {
                    playbackModel.play(artist)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(artist)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(artist)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(artist)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }

            true
        }
    }

    /**
     * Opens the given menu in context of [genre]. Assumes that the menu is only composed of common
     * [Genre] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, genre: Genre) {
        logD("Launching new genre menu: ${genre.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play -> {
                    playbackModel.play(genre)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(genre)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(genre)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(genre)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }

            true
        }
    }

    private fun musicMenuImpl(anchor: View, @MenuRes menuRes: Int, onSelect: (Int) -> Boolean) {
        menu(anchor, menuRes) {
            for (item in menu.children) {
                if (item.itemId == R.id.action_play_next || item.itemId == R.id.action_queue_add) {
                    item.isEnabled = playbackModel.song.value != null
                }
            }

            setOnMenuItemClickListener { item -> onSelect(item.itemId) }
        }
    }

    /**
     * Open a generic menu with configuration in [block]. If a menu is already opened, then this
     * function is a no-op.
     */
    protected fun menu(anchor: View, @MenuRes menuRes: Int, block: PopupMenu.() -> Unit) {
        if (currentMenu != null) {
            logD("Menu already present, not launching")
            return
        }

        currentMenu =
            PopupMenu(requireContext(), anchor).apply {
                inflate(menuRes)
                block()
                setOnDismissListener { currentMenu = null }
                show()
            }
    }

    override fun onDestroyBinding(binding: T) {
        super.onDestroyBinding(binding)
        currentMenu?.dismiss()
        currentMenu = null
    }
}
