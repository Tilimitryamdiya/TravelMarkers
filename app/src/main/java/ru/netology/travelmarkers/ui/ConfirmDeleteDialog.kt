package ru.netology.travelmarkers.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.netology.travelmarkers.R
import ru.netology.travelmarkers.viewmodel.BookmarksViewModel

class ConfirmDeleteDialog : DialogFragment() {

    companion object {
        private const val BOOKMARK_ID = "ID"
        fun newInstance(id: Long) = ConfirmDeleteDialog().apply {
            arguments = bundleOf(BOOKMARK_ID to id)
        }
    }

    private val viewModel: BookmarksViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(context)
        .setMessage(R.string.confirm_delete)
        .setPositiveButton(R.string.delete) { _, _ ->
             viewModel.removeById(requireArguments().getLong(BOOKMARK_ID))
        }
        .setNegativeButton(R.string.cancel, null)
        .create()

}