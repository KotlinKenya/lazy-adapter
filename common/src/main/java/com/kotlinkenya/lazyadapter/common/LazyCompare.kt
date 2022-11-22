package com.kotlinkenya.lazyadapter.common

import androidx.recyclerview.widget.DiffUtil

/**
 * @project LazyAdapter
 * @author mambobryan
 * @email mambobryan@gmail.com
 * Tue Nov 2022
 */
abstract class LazyCompare {
    open fun areItemsSame(newItem: Any?): Boolean = this == newItem
    open fun areContentsSame(newItem: Any?): Boolean = this == newItem
}

fun <T: LazyCompare> lazyComparator() = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.areItemsSame(newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.areContentsSame(newItem)
}