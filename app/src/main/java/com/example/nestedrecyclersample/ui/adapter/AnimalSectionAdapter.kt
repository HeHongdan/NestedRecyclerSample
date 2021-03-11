package com.example.nestedrecyclersample.ui.adapter

import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nestedrecyclersample.R
import com.example.nestedrecyclersample.data.domain.AnimalSection
import com.example.nestedrecyclersample.ui.adapter.base.BaseAdapter
import com.example.nestedrecyclersample.ui.adapter.base.BaseViewHolder

class AnimalSectionAdapter(
    items: List<AnimalSection> = emptyList(),
) : BaseAdapter<AnimalSection>(
    R.layout.item_animal_section,
    items,
) {
    private val scrollStates: MutableMap<String, Parcelable?> = mutableMapOf()

    private val viewPool = RecyclerView.RecycledViewPool()

    private fun getSectionID(position: Int): String {
        return items[position].id
    }

    override fun onViewRecycled(holder: BaseViewHolder<AnimalSection>) {
        super.onViewRecycled(holder)

        //save horizontal scroll state
        //保存水平滚动状态
        val key = getSectionID(holder.layoutPosition)
        scrollStates[key] =
            holder.itemView.findViewById<RecyclerView>(R.id.titledSectionRecycler).layoutManager?.onSaveInstanceState()
    }

    override fun bind(
        itemView: View,
        item: AnimalSection,
        position: Int,
        viewHolder: BaseViewHolderImp
    ) {
        itemView.findViewById<TextView>(R.id.sectionTitleTextView)?.text = item.title
        val layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        /**
         * 4/4RV 设置预加载的数量
         */
        layoutManager.initialPrefetchItemCount = 4

        val titledSectionRecycler = itemView.findViewById<RecyclerView>(R.id.titledSectionRecycler)
        titledSectionRecycler?.run {
            /**
             * 3/4RV 回收池
             */
            this.setRecycledViewPool(viewPool)
            this.layoutManager = layoutManager
            this.adapter = AnimalAdapter(item.animals)
        }

        //restore horizontal scroll state
        //还原水平滚动状态
        val key = getSectionID(viewHolder.layoutPosition)
        val state = scrollStates[key]
        /**
         * 1/4RV 滑动位置的丢失
         */
        if (state != null) {
            titledSectionRecycler.layoutManager?.onRestoreInstanceState(state)
        } else {
            titledSectionRecycler.layoutManager?.scrollToPosition(0)
        }
    }
}