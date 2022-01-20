package dreamlab.worldpics.ui.photo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dreamlab.worldpics.databinding.ItemFavouritePhotoBinding
import dreamlab.worldpics.databinding.ItemPhotoBinding
import dreamlab.worldpics.model.Photo

class FavouritePhotoAdapter(private val onPhotoClicked: (Photo) -> Unit) :
    RecyclerView.Adapter<FavouritePhotoAdapter.PhotoViewHolder>() {

    private val mDiffer: AsyncListDiffer<Photo> =
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }
        })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(ItemFavouritePhotoBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(mDiffer.currentList[position], onPhotoClicked)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.count()
    }

    fun setPhotos(photos: ArrayList<Photo>) {
        mDiffer.submitList(photos)
    }

    inner class PhotoViewHolder(private val binding: ItemFavouritePhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Photo, onPhotoClicked: (Photo) -> Unit) {
            binding.photo = item
            binding.root.setOnClickListener {
                onPhotoClicked(item)
            }
        }
    }
}