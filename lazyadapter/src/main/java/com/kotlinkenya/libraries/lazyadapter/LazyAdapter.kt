package com.kotlinkenya.libraries.lazyadapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kotlinkenya.libraries.lazyadapter.utils.*

/**
 * LazyAdapter
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 7/21/22 at 10:08 AM
 */
class LazyAdapter<T : LazyCompare, V : ViewBinding> :
    ListAdapter<T, LazyAdapter<T, V>.LazyViewHolder>(getDefaultLazyComparator()) {

    /**
     * CREATING
     */
    private var mCreate: ((parent: ViewGroup) -> V)? = null

    /**
     * BINDING
     */
    private var mBind: (V.(item: T) -> Unit)? = null
    private var mBindPosition: (V.(item: T, position: Int) -> Unit)? = null
    private var mBindSelected: (V.(item: T, selected: Boolean) -> Unit)? = null

    /**
     * CLICKS
     */
    private var mClicked: ((item: T) -> Unit)? = null
    private var mLongClicked: ((item: T) -> Boolean)? = null

    /**
     * SELECTIONS
     */
    private var onItemSelected: ((item: T?) -> Unit)? = null
    private var onItemsSelected: ((items: List<T?>) -> Unit)? = null

    private val selectedItems = mutableListOf<Long>()

    inner class LazyViewHolder(context: Context, private val binding: V?) :
        RecyclerView.ViewHolder(binding?.root ?: View(context)) {

        init {
            binding?.root?.setOnClickListener {
                mClicked?.invoke(getItem(absoluteAdapterPosition) as T)
                // CHECK SELECTIONS AND TOGGLE
                if (onItemSelected != null || onItemsSelected != null)
                    when (selectedItems.contains(absoluteAdapterPosition.toLong())) {
                        true -> removeSelection(absoluteAdapterPosition)
                        false -> addSelection(absoluteAdapterPosition)
                    }

                // ON SINGLE ITEM SELECTED
                onItemSelected?.let { mSelect ->
                    val item = selectedItems.map { position -> getItem(position.toInt()) }
                        .firstOrNull()
                    mSelect.invoke(item)
                }

                // ON MULTIPLE ITEM SELECTED
                onItemsSelected?.let { mSelects ->
                    val list = selectedItems.map { position -> getItem(position.toInt()) }
                    mSelects.invoke(list)
                }
            }
        }

        fun bindHolder(item: T, selected: Boolean? = null) {
            when (selected) {
                null -> {
                    mBind?.let { block -> binding?.block(item) }
                    mBindPosition?.let { block -> binding?.block(item, absoluteAdapterPosition) }
                }
                else -> {
                    mBindSelected?.let { block -> binding?.block(item, selected) }
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LazyViewHolder {
        val binding = mCreate?.invoke(parent)
        return LazyViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: LazyViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            if (onItemSelected != null || onItemsSelected != null)
                holder.bindHolder(it, selectedItems.contains(position.toLong()))
            else
                holder.bindHolder(it)
        }
    }

    /**
     * ADAPTER FUNCTIONS
     */
    private fun currentMutableList() = currentList.toMutableList()

    private fun addSelection(position: Int) {
        if (selectedItems.contains(position.toLong())) return
        val previousPosition = selectedItems.firstOrNull()
        if (onItemSelected != null) selectedItems.clear()
        selectedItems.add(position.toLong())
        previousPosition?.toInt()?.let { notifyItemChanged(it) }
        notifyItemChanged(position)
    }

    private fun removeSelection(position: Int) {
        if (!selectedItems.contains(position.toLong())) return
        selectedItems.remove(position.toLong())
        notifyItemChanged(position)
    }

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

    @JvmName("onBindWithSelection")
    fun onBind(bind: V.(item: T, selected: Boolean) -> Unit) = apply {
        mBindSelected = bind
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

    fun onItemSelected(block: ((item: T?) -> Unit)? = null) = apply {
        onItemSelected = block
    }

    fun onItemsSelected(block: ((items: List<T?>) -> Unit)? = null) = apply {
        onItemsSelected = block
    }

    fun onSwipedRight(
        recyclerView: RecyclerView,
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
        val swiper = object : SwipeRight(context = recyclerView.context, lazyField = fields) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                getItem(position)?.let { item ->
                    when (remove) {
                        true -> remove(position)
                        false -> notifyItemChanged(position)
                    }
                    swiped.invoke(item)
                }
            }

        }

        ItemTouchHelper(swiper).attachToRecyclerView(recyclerView)

    }

    fun onSwipedLeft(
        recyclerView: RecyclerView,
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
        val swiper = object : SwipeLeft(context = recyclerView.context, lazyField = fields) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                getItem(position)?.let { item ->
                    when (remove) {
                        true -> remove(position)
                        false -> notifyItemChanged(position)
                    }
                    swiped.invoke(item)
                }
            }
        }

        ItemTouchHelper(swiper).attachToRecyclerView(recyclerView)

    }
}
