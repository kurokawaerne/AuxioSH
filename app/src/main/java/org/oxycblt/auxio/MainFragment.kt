package org.oxycblt.auxio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.library.LibraryFragment
import org.oxycblt.auxio.songs.SongsFragment
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.getInactiveAlpha
import org.oxycblt.auxio.theme.getTransparentAccent
import org.oxycblt.auxio.theme.toColor

class MainFragment : Fragment() {

    private val shownFragments = listOf(0, 1)

    private val libraryFragment: LibraryFragment by lazy { LibraryFragment() }
    private val songsFragment: SongsFragment by lazy { SongsFragment() }

    private val colorSelected: Int by lazy {
        accent.first.toColor(requireContext())
    }

    private val colorDeselected: Int by lazy {
        getTransparentAccent(
            requireContext(),
            accent.first,
            getInactiveAlpha(accent.first)
        )
    }

    private val tabIcons = listOf(
        R.drawable.ic_library,
        R.drawable.ic_music
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false
        )

        val adapter = PagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // Link the ViewPager & Tab View
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.icon = ContextCompat.getDrawable(requireContext(), tabIcons[position])

            // Set the icon tint to deselected if its not the default tab
            if (position > 0) {
                tab.icon?.setTint(colorDeselected)
            }

            // Init the fragment
            fragmentAt(position)
        }.attach()

        // Set up the selected/deselected colors
        binding.tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(colorSelected)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(colorDeselected)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            }
        )

        Log.d(this::class.simpleName, "Fragment Created.")

        return binding.root
    }

    private fun fragmentAt(position: Int): Fragment {
        return when (position) {
            0 -> libraryFragment
            1 -> songsFragment

            else -> libraryFragment
        }
    }

    private inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = shownFragments.size

        override fun createFragment(position: Int): Fragment {
            Log.d(this::class.simpleName, "Switching to fragment $position.")

            if (shownFragments.contains(position)) {
                return fragmentAt(position)
            }

            // Not sure how this would happen but it might
            Log.e(
                this::class.simpleName,
                "Attempted to index a fragment that shouldn't be shown. Returning libraryFragment."
            )

            return libraryFragment
        }
    }
}