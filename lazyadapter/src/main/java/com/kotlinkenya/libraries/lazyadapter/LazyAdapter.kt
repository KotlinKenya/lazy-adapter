package com.kotlinkenya.libraries.lazyadapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kotlinkenya.lazyadapter.common.*

/**
 * LazyAdapter
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 7/21/22 at 10:08 AM
 */
class LazyAdapter<T : LazyCompare, V : ViewBinding> :
    ListAdapter<T, LazyAdapter<T, V>.LazyViewHolder>(lazyComparator()) {

    /**
     * CREATING
     */
    private var mCreate: ((parent: ViewGroup) -> V)? = null

    /**
     * BINDING
     */
    private var mBind: (V.(item: T) -> Unit)? = null
    private var mBindPosition: (V.(item: T, position: Int) -> Unit)? = null

    /**
     * DEFAULT CLICKS
     */
    private var mClicked: ((item: T) -> Unit)? = null
    private var mLongClicked: ((item: T) -> Boolean)? = null

    /**
     * MAIN VIEWGROUP
     */
    private lateinit var parent: ViewGroup

    inner class LazyViewHolder(context: Context, private val binding: V?) :
        RecyclerView.ViewHolder(binding?.root ?: View(context)) {

        init {
            binding?.root?.setOnClickListener {
                mClicked?.invoke(getItem(absoluteAdapterPosition) as T)
            }
        }

        fun bindHolder(item: T) {
            mBind?.let { block -> binding?.block(item) }
            mBindPosition?.let { block -> binding?.block(item, absoluteAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LazyViewHolder {
        this.parent = parent
        val binding = mCreate?.invoke(parent)
        return LazyViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: LazyViewHolder, position: Int) {
        val item = getItem(position) as T
        holder.bindHolder(item)
    }

    /**
     * ADAPTER FUNCTIONS
     */
    private fun currentMutableList() = currentList.toMutableList()

    private fun updateList(list: List<T>) {
        submitList(list)
    }

    fun add(item: T) {
        val list: MutableList<T> = currentMutableList()
        list.add(item)
        updateList(list)
    }

    fun add(item: T, index: Int) {
        val list: MutableList<T> = currentList.toMutableList()
        list.add(index = index, element = item)
        updateList(list)
    }

    fun update(item: T, index: Int) {
        val list: MutableList<T> = currentList.toMutableList()
        list.set(index = index, element = item)
        updateList(list)
    }

    fun remove(item: T) {
        val list = currentList.toMutableList()
        if (!list.contains(item)) return
        list.remove(item)
        updateList(list)
    }

    fun remove(index: Int) {
        val list = currentList.toMutableList()
        if (index >= list.size) return
        list.removeAt(index)
        updateList(list)
    }

    fun isEmpty(): Boolean {
        return currentList.isEmpty()
    }

    /**
     * CREATING
     */

    fun onCreate(create: (parent: ViewGroup) -> V) = apply {
        mCreate = create
    }

    /**
     * BINDING
     */

    fun onBind(bind: V.(item: T) -> Unit) = apply {
        mBind = bind
    }

    @JvmName("onBindWithPosition")
    fun onBind(block: V.(item: T, position: Int) -> Unit) = apply {
        mBindPosition = block
    }

    /**
     * CLICKING
     */
    fun onItemClicked(block: ((item: T) -> Unit)? = null) = apply {
        mClicked = block
    }

    fun onItemLongClicked(block: ((item: T) -> Boolean)? = null) = apply {
        mLongClicked = block
    }

    fun onSwipedRight(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        remove: Boolean = false,
        swiped: (item: T) -> Unit
    ) = apply {
        val fields = LazySwipeFields(
            drawable = icon,
            iconColor = iconColor,
            background = color
        )
        val swiper = object : SwipeRight(context = parent.context, lazyField = fields) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                getItem(position)?.let {
                    when (remove) {
                        true -> remove(position)
                        false -> notifyItemChanged(position)
                    }
                    swiped.invoke(it)
                }
            }

        }
        ItemTouchHelper(swiper).attachToRecyclerView(parent as RecyclerView)
    }

    fun onSwipedLeft(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        remove: Boolean = true,
        swiped: (item: T) -> Unit
    ) = apply {
        val fields = LazySwipeFields(
            drawable = icon,
            iconColor = iconColor,
            background = color
        )
        val swiper = object : SwipeLeft(context = parent.context, lazyField = fields) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                getItem(position)?.let {
                    when (remove) {
                        true -> remove(position)
                        false -> notifyItemChanged(position)
                    }
                    swiped.invoke(it)
                }
            }

        }
        ItemTouchHelper(swiper).attachToRecyclerView(parent as RecyclerView)
    }

}