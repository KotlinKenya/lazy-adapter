package com.kotlinkenya.lazyadapter.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @project LazyAdapter
 * @author mambobryan
 * @email mambobryan@gmail.com
 * Tue 22 Nov 2022
 */
abstract class SwipeRight(
    private val context: Context,
    private val lazyField: LazySwipeFields?
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val icon =
        ContextCompat.getDrawable(
            context,
            lazyField?.drawable ?: R.drawable.lazy_edit
        )

    private val intrinsicWidth = icon?.intrinsicWidth ?: 0
    private val intrinsicHeight = icon?.intrinsicHeight ?: 0

    private val background = ColorDrawable()
    private val backgroundColor =
        ContextCompat.getColor(
            context,
            lazyField?.background ?: android.R.color.darker_gray
        )
    private val clearPaint =
        Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.absoluteAdapterPosition == RecyclerView.NO_POSITION) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            return
        }

        // Draw the background
        background.apply {
            color = backgroundColor
            setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            draw(c)
        }

        // Calculate position of the icon
        val iconMargin = (itemHeight - intrinsicHeight) / 2
        val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val iconBottom = iconTop + intrinsicHeight
        val iconRight = itemView.left + iconMargin + intrinsicWidth
        val iconLeft = itemView.left + iconMargin

        // Draw the icon
        icon?.setTint(
            ContextCompat.getColor(context, lazyField?.iconColor ?: android.R.color.white)
        )
        icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

}

abstract class SwipeLeft(
    private val context: Context,
    private val lazyField: LazySwipeFields?
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val icon =
        ContextCompat.getDrawable(
            context,
            lazyField?.drawable ?: R.drawable.lazy_delete
        )

    private val intrinsicWidth = icon?.intrinsicWidth ?: 0
    private val intrinsicHeight = icon?.intrinsicHeight ?: 0
    private val background = ColorDrawable()
    private val backgroundColor =
        ContextCompat.getColor(
            context,
            lazyField?.background ?: android.R.color.darker_gray
        )
    private val clearPaint =
        Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.absoluteAdapterPosition == RecyclerView.NO_POSITION) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            return
        }

        // Draw the background
        background.apply {
            color = backgroundColor
            setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            draw(c)
        }


        // Calculate position of the icon
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        // Draw the the icon
        icon?.setTint(
            ContextCompat.getColor(context, lazyField?.iconColor ?: android.R.color.white)
        )
        icon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        icon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

}