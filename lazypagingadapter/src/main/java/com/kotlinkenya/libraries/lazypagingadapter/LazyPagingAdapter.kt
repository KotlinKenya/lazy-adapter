package com.kotlinkenya.libraries.lazypagingadapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kotlinkenya.lazyadapter.common.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * LazyAdapter
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 7/21/22 at 2:06 PM
 */
class LazyPagingAdapter<T : LazyCompare, V : ViewBinding> :
    PagingDataAdapter<T, LazyPagingAdapter<T, V>.LazyViewHolder>(lazyComparator()) {

    private var mCreate: ((parent: ViewGroup) -> V)? = null

    private var mBind: (V.(item: T) -> Unit)? = null
    private var mBindPosition: (V.(item: T, position: Int) -> Unit)? = null
    private var mBindSelected: (V.(item: T, selected: Boolean) -> Unit)? = null

    private var onItemClicked: ((item: T) -> Unit)? = null
    private var onItemLongClicked: ((item: T) -> Boolean)? = null

    private var onItemSelected: ((item: T?) -> Unit)? = null
    private var onItemsSelected: ((items: List<T?>) -> Unit)? = null

    private val selectedItems = mutableListOf<Long>()

    inner class LazyViewHolder(
        val context: Context,
        val binding: V?
    ) : RecyclerView.ViewHolder(binding?.root ?: View(context)) {

        init {

            binding?.let {
                it.root.setOnClickListener {

                    // ON CLICK
                    onItemClicked?.invoke(getItem(absoluteAdapterPosition) as T)

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
                it.root.setOnLongClickListener {
                    getItem(absoluteAdapterPosition)?.let { item ->
                        onItemLongClicked?.invoke(item)
                    } ?: false
                }
            }

        }

        fun bindHolder(item: T) {
            mBind?.let { block -> binding?.block(item) }
            mBindPosition?.let { block -> binding?.block(item, absoluteAdapterPosition) }
        }

        fun bindHolder(item: T, selected: Boolean) {
            mBindSelected?.let { block -> binding?.block(item, selected) }
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
     * CREATE VIEWS
     */

    fun onCreate(create: (parent: ViewGroup) -> V) = apply {
        mCreate = create
    }

    /**
     * INVOKE BINDINGS
     */

    fun onBind(bind: V.(item: T) -> Unit) = apply {
        mBind = bind
    }

    @JvmName("onBindWithPosition")
    fun onBind(block: V.(item: T, position: Int) -> Unit) = apply {
        mBindPosition = block
    }

    @JvmName("onBindWithSelected")
    fun onBind(bind: V.(item: T, selected: Boolean) -> Unit) = apply {
        mBindSelected = bind
    }

    /**
     * ADAPTER SELECTIONS, CLICKS, SWIPES
     */

    fun onItemClicked(block: ((item: T) -> Unit)? = null) = apply {
        onItemClicked = block
    }

    fun onItemLongClicked(block: ((item: T) -> Boolean)? = null) = apply {
        onItemLongClicked = block
    }

    fun onItemSelected(block: ((item: T?) -> Unit)? = null) = apply {
        onItemSelected = block
    }

    fun onItemsSelected(block: ((items: List<T?>) -> Unit)? = null) = apply {
        onItemsSelected = block
    }

    fun onSwipedRight(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        remove: Boolean = false,
        view: RecyclerView,
        swiped: (item: T) -> Unit
    ) {
        val fields = LazySwipeFields(
            drawable = icon,
            iconColor = iconColor,
            background = color
        )
        val swiper = object : SwipeRight(context = view.context, lazyField = fields) {
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
        ItemTouchHelper(swiper).attachToRecyclerView(view)
    }

    fun onSwipedLeft(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        remove: Boolean = true,
        view: RecyclerView,
        swiped: (item: T) -> Unit
    ) {
        val fields = LazySwipeFields(
            drawable = icon,
            iconColor = iconColor,
            background = color
        )
        val swiper = object : SwipeLeft(context = view.context, lazyField = fields) {
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
        ItemTouchHelper(swiper).attachToRecyclerView(view)
    }

    /**
     * HELPER FUNCTIONS FOR PAGING DATA LIST
     */

    private fun getMutableList(): MutableList<T> = this.snapshot().filterNotNull().toMutableList()

    private fun updatedPagedData(list: List<T>): PagingData<T> = PagingData.from(list)

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

    private fun updateList(list: List<T>) = CoroutineScope(Dispatchers.Main).launch {
        submitData(updatedPagedData(list))
    }

    /**
     * ADAPTER FUNCTIONS
     */
    fun add(item: T) {
        val list: MutableList<T> = getMutableList()
        list.add(item)
        updateList(list)
    }

    fun add(item: T, index: Int) {
        val list: MutableList<T> = getMutableList()
        list.add(index = index, element = item)
        updateList(list)
    }

    fun update(item: T, index: Int) {
        val list: MutableList<T> = getMutableList()
        list.set(index = index, element = item)
        updateList(list)
    }

    fun remove(item: T) {
        val list = getMutableList()
        if (!list.contains(item)) return
        list.remove(item)
        updateList(list)
    }

    fun remove(index: Int) {
        val list = getMutableList()
        if (index >= list.size) return
        list.removeAt(index)
        updateList(list)
    }

    fun isEmpty() = getMutableList().isEmpty()

}