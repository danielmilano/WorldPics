package dreamlab.worldpics.ui.photo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import dreamlab.worldpics.BuildConfig
import dreamlab.worldpics.R
import dreamlab.worldpics.databinding.ItemBannerBinding
import dreamlab.worldpics.databinding.ItemLoaderBinding
import dreamlab.worldpics.databinding.ItemPhotoBinding
import dreamlab.worldpics.model.Photo
import dreamlab.worldpics.repository.NetworkState
import dreamlab.worldpics.repository.Status

/**
 * Created by danielm on 10/02/2018.
 */

class PhotoAdapter(
    private val adRequest: AdRequest?,
    private val onPhotoClicked: (Photo?) -> Unit,
    private val retryCallback: () -> Unit
) : PagedListAdapter<Photo, PhotoAdapter.ItemsViewHolder>(ItemsDiffCallback) {

    private var networkState: NetworkState? = null

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_loader
        } else {
            R.layout.item_photo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_photo ->
                PhotoViewHolder(
                    ItemPhotoBinding.inflate(inflater, parent, false)
                )
            R.layout.item_banner ->
                BannerItemHolder(
                    ItemBannerBinding.inflate(inflater, parent, false),
                    adRequest
                )
            R.layout.item_loader -> LoaderItemHolder(
                ItemLoaderBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_photo -> {
                val item = getItem(position)
                (holder as PhotoViewHolder).bind(item, onPhotoClicked)
            }
            R.layout.item_loader -> {
                (holder as LoaderItemHolder).bind(networkState, retryCallback)
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    open inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        ItemsViewHolder(binding.root) {
        fun bind(item: Photo?, onPhotoClicked: (Photo?) -> Unit) {
            binding.photo = item
            binding.root.setOnClickListener {
                onPhotoClicked(item)
            }
        }
    }

    inner class LoaderItemHolder(
        private val binding: ItemLoaderBinding
    ) : ItemsViewHolder(binding.root) {

        fun bind(networkState: NetworkState?, retryCallback: () -> Unit) {
            binding.progressBar.visibility = toVisibility(networkState?.status == Status.RUNNING)
            binding.retry.visibility = toVisibility(networkState?.status == Status.FAILED)
            binding.error.visibility = toVisibility(networkState?.msg != null)
            binding.error.text = networkState?.msg
            binding.retry.setOnClickListener { retryCallback() }
        }

        private fun toVisibility(constraint: Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    inner class BannerItemHolder(binding: ItemBannerBinding, private val adRequest: AdRequest?) :
        ItemsViewHolder(binding.root) {

        init {
            val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
            binding.adView.adUnitId = BuildConfig.banner_item_id
            binding.adView.adSize = AdSize.SMART_BANNER
        }

        fun bind(item: AdView) {
            adRequest?.let {
                item.loadAd(it)
            }
        }
    }

    object ItemsDiffCallback : DiffUtil.ItemCallback<Photo>() {

        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}

