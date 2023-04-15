package ru.netology.travelmarkers.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import ru.netology.travelmarkers.R
import ru.netology.travelmarkers.adapter.BookmarksAdapter
import ru.netology.travelmarkers.adapter.Listener
import ru.netology.travelmarkers.databinding.FragmentBookmarksBinding
import ru.netology.travelmarkers.dto.Bookmark
import ru.netology.travelmarkers.ui.AddBookmarkFragment.Companion.ID_BOOKMARK
import ru.netology.travelmarkers.ui.AddBookmarkFragment.Companion.LAT_KEY
import ru.netology.travelmarkers.ui.AddBookmarkFragment.Companion.LONG_KEY
import ru.netology.travelmarkers.viewmodel.BookmarksViewModel

class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {
    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        val viewModel by viewModels<BookmarksViewModel>()

        val adapter = BookmarksAdapter(object : Listener {
            override fun onDelete(bookmark: Bookmark) {
                ConfirmDeleteDialog
                    .newInstance(bookmark.id)
                    .show(childFragmentManager, null)
            }

            override fun onBookmark(bookmark: Bookmark) {
                findNavController().navigate(
                    R.id.action_bookmarks_fragment_to_map_fragment,
                    bundleOf(
                        MapFragment.LAT_KEY to bookmark.latitude,
                        MapFragment.LONG_KEY to bookmark.longitude
                    )
                )
            }

            override fun onEdit(bookmark: Bookmark) {
                findNavController().navigate(R.id.action_bookmarks_fragment_to_add_bookmark_fragment,
                    bundleOf(
                        LAT_KEY to bookmark.latitude,
                        LONG_KEY to bookmark.longitude,
                        ID_BOOKMARK to bookmark.id
                    )
                )
            }
        })

        binding.list.adapter = adapter


        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.data.collectLatest { data ->
                adapter.submitList(data)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}