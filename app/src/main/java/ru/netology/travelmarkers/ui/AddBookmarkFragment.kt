package ru.netology.travelmarkers.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.travelmarkers.R
import ru.netology.travelmarkers.databinding.FragmentAddBookmarkBinding
import ru.netology.travelmarkers.dto.Bookmark
import ru.netology.travelmarkers.viewmodel.BookmarksViewModel

class AddBookmarkFragment : Fragment() {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
        const val ID_BOOKMARK = "ID_BOOKMARK"
    }

    private var _binding: FragmentAddBookmarkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookmarkBinding.inflate(inflater, container, false)

        val latitude = requireArguments().getDouble(LAT_KEY)
        val longitude = requireArguments().getDouble(LONG_KEY)
        var savedBookmark: Bookmark?

        requireArguments().getLong(ID_BOOKMARK).apply {
            savedBookmark = viewModel.getById(this)
        }



        with(binding) {
            val lat = "%.4f".format(latitude)
            val long = "%.4f".format(longitude)
            addressLat.text = lat
            addressLong.text = long
            if (savedBookmark != null) {
                editName.setText(savedBookmark!!.name)
                editDescription.setText(savedBookmark!!.description)
            }



            confirmButton.setOnClickListener {
                val name = editName.text.toString().takeIf { it.isNotBlank() } ?: run {
                    Toast.makeText(requireContext(), R.string.empty_name, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val description = editDescription.text.toString()

                viewModel.save(
                    Bookmark(
                        id = savedBookmark?.id ?: 0,
                        name = name,
                        latitude = latitude,
                        longitude = longitude,
                        description = description
                    )
                )
                findNavController().navigateUp()
            }

            cancelButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}