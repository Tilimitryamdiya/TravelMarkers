package ru.netology.travelmarkers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.travelmarkers.R
import ru.netology.travelmarkers.databinding.CardBookmarkBinding
import ru.netology.travelmarkers.dto.Bookmark

class BookmarksAdapter(private val listener: Listener) :
    ListAdapter<Bookmark, BookmarksViewHolder>(BookmarksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardBookmarkBinding.inflate(inflater, parent, false)
        val holder = BookmarksViewHolder(binding)


        with(binding) {
            root.setOnClickListener {
                val place = getItem(holder.adapterPosition)
                listener.onBookmark(place)
            }
            bookmarkMenu.setOnClickListener {
                PopupMenu(root.context, it).apply {
                    inflate(R.menu.bookmark_menu)

                    setOnMenuItemClickListener { item ->
                        val bookmark = getItem(holder.adapterPosition)
                        when (item.itemId) {
                            R.id.delete -> {
                                listener.onDelete(bookmark)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(bookmark)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        val bookmark = getItem(position)
        holder.bind(bookmark)
    }
}

class BookmarksViewHolder(
    private val binding: CardBookmarkBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(bookmark: Bookmark) {
        val latitude = "%.4f".format(bookmark.latitude)
        val longitude = "%.4f".format(bookmark.longitude)
        binding.apply {
            bookmarkName.text = bookmark.name
            addressLat.text = latitude
            addressLong.text = longitude
            description.text = bookmark.description
        }
    }
}

class BookmarksDiffCallback : DiffUtil.ItemCallback<Bookmark>() {

    override fun areItemsTheSame(oldItem: Bookmark, newItem: Bookmark) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Bookmark, newItem: Bookmark) = oldItem == newItem
}

interface Listener {
    fun onDelete(bookmark: Bookmark)
    fun onBookmark(bookmark: Bookmark)
    fun onEdit(bookmark: Bookmark)
}