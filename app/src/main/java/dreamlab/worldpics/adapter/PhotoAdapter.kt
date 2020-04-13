package dreamlab.worldpics.adapter

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
import dreamlab.worldpics.databinding.ItemBannerBinding
import dreamlab.worldpics.databinding.ItemPhotoBinding
import dreamlab.worldpics.fragment.main.photo.data.Photo

/**
 * Created by danielm on 10/02/2018.
 */

class PhotoAdapter(
    val context: Context?,
    private val adBuilder: AdRequest.Builder? = null,
    val mListener: Listener
) : ListAdapter<Any, ItemsViewHolder>(ItemsDiffCallback) {

    private var mValues = ArrayList<Any>()

    override fun submitList(list: List<Any>?) {
        var arrayList: ArrayList<Any>? = null

        list?.let {
            arrayList = ArrayList(it)
            mValues.addAll(arrayList!!)
            for (index in arrayList!!.indices step 10) {
                val adView = AdView(context)
                arrayList!!.add(index, adView)
            }
        }

        super.submitList(arrayList)
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
                ItemsViewHolder.PhotoViewHolder(ItemPhotoBinding.inflate(inflater, parent, false))
            R.layout.item_banner ->
                ItemsViewHolder.BannerItemHolder(ItemBannerBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        when (holder) {
            is ItemsViewHolder.PhotoViewHolder -> {
                bindPhotoItemHolder(holder, getItem(position) as Photo)
            }
            is ItemsViewHolder.BannerItemHolder -> {
                bindBannerItemHolder(holder.binding.adView)
            }
        }
    }

    private fun bindPhotoItemHolder(holder: ItemsViewHolder.PhotoViewHolder, item: Photo) {
        holder.binding.photo = item
        holder.binding.root.setOnClickListener { mListener.onPhotoClick(item) }
    }

    private fun bindBannerItemHolder(item: AdView) {
        item.loadAd(adBuilder?.build())
    }

    interface Listener {
        fun onPhotoClick(photo: Photo)
    }
}

sealed class ItemsViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    class PhotoViewHolder(
        val binding: ItemPhotoBinding
    ) : ItemsViewHolder(binding.root)

    class BannerItemHolder(val binding: ItemBannerBinding) :
        ItemsViewHolder(binding.root) {

        init {
            val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
            binding.adView.adUnitId = BuildConfig.banner_item_id
            binding.adView.adSize = AdSize.SMART_BANNER
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