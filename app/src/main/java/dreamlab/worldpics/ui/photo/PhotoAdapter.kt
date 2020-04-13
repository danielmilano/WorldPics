package dreamlab.worldpics.ui.photo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import dreamlab.worldpics.BuildConfig
import dreamlab.worldpics.R
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.databinding.ItemBannerBinding
import dreamlab.worldpics.databinding.ItemPhotoBinding
import dreamlab.worldpics.fragment.main.photo.data.Photo
import javax.inject.Inject

/**
 * Created by danielm on 10/02/2018.
 */

class PhotoAdapter(private val onPhotoClicked: (Photo) -> Unit) :
    ListAdapter<Any, ItemsViewHolder>(
        ItemsDiffCallback
    ) {

    @Inject
    lateinit var context: Context

    private var mValues = ArrayList<Any>()

    override fun submitList(list: List<Any>?) {

        list?.let {
            mValues.addAll(it)

            if (!WorldPics.isPremium) {
                for (index in mValues.indices step 10) {
                    val adView = AdView(context)
                    mValues.add(index, adView)
                }
            }
        }

        super.submitList(mValues)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is Photo -> R.layout.item_photo
            is AdView -> R.layout.item_banner
            else -> throw IllegalStateException("Unknown type: ${item::class.java.simpleName}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_photo ->
                ItemsViewHolder.PhotoViewHolder(
                    ItemPhotoBinding.inflate(inflater, parent, false)
                )
            R.layout.item_banner ->
                ItemsViewHolder.BannerItemHolder(
                    ItemBannerBinding.inflate(inflater, parent, false)
                )
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        when (holder) {
            is ItemsViewHolder.PhotoViewHolder -> {
                holder.bind(holder, getItem(position) as Photo, onPhotoClicked)
            }
            is ItemsViewHolder.BannerItemHolder -> {
                holder.bind(holder.binding.adView)
            }
        }
    }
}

sealed class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class PhotoViewHolder(val binding: ItemPhotoBinding) : ItemsViewHolder(binding.root) {
        fun bind(holder: PhotoViewHolder, item: Photo, onPhotoClicked: (Photo) -> Unit) {
            holder.binding.photo = item
            holder.binding.root.setOnClickListener {
                onPhotoClicked(item)
            }
        }
    }

    class BannerItemHolder(val binding: ItemBannerBinding) : ItemsViewHolder(binding.root) {

        @Inject
        lateinit var adBuilder: AdRequest.Builder

        init {
            val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
            binding.adView.adUnitId = BuildConfig.banner_item_id
            binding.adView.adSize = AdSize.SMART_BANNER
        }

        fun bind(item: AdView) {
            item.loadAd(adBuilder.build())
        }
    }

}

internal object ItemsDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is Photo && newItem is Photo -> oldItem.id == newItem.id
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is Photo && newItem is Photo -> oldItem == newItem
            else -> false
        }
    }
}