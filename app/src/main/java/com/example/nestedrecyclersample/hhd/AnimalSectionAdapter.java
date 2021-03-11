package com.example.nestedrecyclersample.hhd;

import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nestedrecyclersample.R;
import com.example.nestedrecyclersample.data.domain.AnimalSection;
import com.example.nestedrecyclersample.ui.adapter.AnimalAdapter;
import com.example.nestedrecyclersample.ui.adapter.base.BaseAdapter;
import com.example.nestedrecyclersample.ui.adapter.base.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 类描述：外层(横向)适配器。
 *
 * @author HeHongdan
 * @date 3/11/21
 * @since v3/11/21
 */
public class AnimalSectionAdapter extends BaseAdapter<AnimalSection> {

    private ArrayMap<String, Parcelable> scrollStates = new ArrayMap<>();
    private RecyclerView.RecycledViewPool viewPool;

    public AnimalSectionAdapter(@NotNull List<? extends AnimalSection> items) {
        super(R.layout.item_animal_section, items);
    }

    @NotNull
    @Override
    public BaseViewHolder<AnimalSection> onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView){
            viewPool = ((RecyclerView)parent).getRecycledViewPool();
            Log.d("HHD", "获取父Pool= " + viewPool);
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder<AnimalSection> holder) {
        super.onViewRecycled(holder);

        String key = getSectionID(holder.getLayoutPosition());
        RecyclerView rv = (RecyclerView) holder.itemView.findViewById(R.id.titledSectionRecycler);
        scrollStates.put(key, rv.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void bind(@NotNull View itemView, AnimalSection item, int position, @NotNull BaseViewHolderImp viewHolder) {
        TextView text = itemView.findViewById(R.id.sectionTitleTextView);
        text.setText(item.getTitle());

        LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        /**
         * 4/4RV 设置预加载的数量
         */
        layoutManager.setInitialPrefetchItemCount(4);

        RecyclerView titledSectionRecycler = (RecyclerView) itemView.findViewById(R.id.titledSectionRecycler);
        /**
         * 3/4RV 回收池
         */
        Log.d("HHD", "共用父Pool= " + viewPool);
        titledSectionRecycler.setRecycledViewPool(viewPool);
        titledSectionRecycler.setLayoutManager(layoutManager);
        titledSectionRecycler.setAdapter(new AnimalAdapter(item.getAnimals()));

        String key = getSectionID(viewHolder.getLayoutPosition());
        Parcelable state = scrollStates.get(key);

        /**
         * 1/4RV 滑动位置的丢失
         */
        if (state != null){
            titledSectionRecycler.getLayoutManager().onRestoreInstanceState(state);
        } else {
            titledSectionRecycler.getLayoutManager().scrollToPosition(0);
        }

    }

    private String getSectionID(int position){
        Log.d("HHD", "itme数据的唯一Id= " + getItems().get(position).getId());
        return getItems().get(position).getId();
    }
}
