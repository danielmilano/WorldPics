package dreamlab.worldpics.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dreamlab.worldpics.R
import dreamlab.worldpics.WorldPics
import dreamlab.worldpics.databinding.CellBannerItemBinding
import dreamlab.worldpics.databinding.CellPhotoItemBinding
import dreamlab.worldpics.fragment.main.photo.data.Photo
import kotlin.collections.ArrayList

/**
 * Created by danielm on 10/02/2018.
 */

class PhotoAdapter(val context: Context?,
                   private val mValues: ArrayList<Photo?>,
                   val adBuilder: AdRequest.Builder?,
                   val mListener: Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDiffer: AsyncListDiffer<Photo?> = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Photo?>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.user_id == newItem.user_id
        }
    })

    companion object {
        val PHOTO_VIEW_TYPE = 0
        val AD_VIEW_TYPE = 1
        val ITEMS_PER_AD = 10
    }

    override fun getItemViewType(position: Int): Int {
        return if (WorldPics.isPremium) {
            PHOTO_VIEW_TYPE
        } else {
            if (position % ITEMS_PER_AD == 0 && adBuilder != null) {
                AD_VIEW_TYPE
            } else {
                PHOTO_VIEW_TYPE
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAdMob -> {
                mValues.add(position, null)
                mDiffer.submitList(ArrayList(mValues))
                holder.bind(AdView(context), position)
            }
            is PhotoViewHolder -> holder.bind(mDiffer.currentList[position] as Photo, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AD_VIEW_TYPE -> ViewHolderAdMob(parent)
            PHOTO_VIEW_TYPE -> PhotoViewHolder(parent)
            else -> throw RuntimeException("VIEW_TYPE NOT FOUND")
        }
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    fun setValues(photos: ArrayList<Photo>) {
        mValues.addAll(photos)
        mDiffer.submitList(ArrayList(mValues))
    }

    inner class PhotoViewHolder : BaseBindingRecyclerHolder<Photo, CellPhotoItemBinding> {

        constructor(parent: ViewGroup) : super(parent, R.layout.cell_photo_item){
            binding.root.setOnClickListener { mListener.onPhotoClick(binding.photo!!) }
        }

        override fun bind(item: Photo, position: Int) {
            binding.photo = item
        }
    }

    inner class ViewHolderAdMob : BaseBindingRecyclerHolder<AdView, CellBannerItemBinding> {

        constructor(parent: ViewGroup) : super(parent, R.layout.cell_banner_item) {
            val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
        }

        override fun bind(item: AdView, position: Int) {
            item.loadAd(adBuilder?.build())
        }
    }

    interface Listener {
        fun onPhotoClick(photo: Photo)
    }
}