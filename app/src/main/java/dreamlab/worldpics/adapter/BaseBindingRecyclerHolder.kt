package dreamlab.worldpics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseBindingRecyclerHolder<in T, out DB : ViewDataBinding>(parent: ViewGroup, layoutId: Int) : RecyclerView.ViewHolder(DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutId, parent, false).root) {

    val binding: DB = DataBindingUtil.getBinding(itemView)!!

    abstract fun bind(item: T)
}
