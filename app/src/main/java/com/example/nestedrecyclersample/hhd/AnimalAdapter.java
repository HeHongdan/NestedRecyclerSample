package com.example.nestedrecyclersample.hhd;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nestedrecyclersample.R;
import com.example.nestedrecyclersample.data.domain.Animal;
import com.example.nestedrecyclersample.ui.adapter.base.BaseAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 类描述：内层(纵向)适配器。。
 *
 * @author HeHongdan
 * @date 3/11/21
 * @since v3/11/21
 */
public class AnimalAdapter extends BaseAdapter<Animal> {
    public TextView text;
    public ImageView image;


    public AnimalAdapter(@NotNull List<? extends Animal> items) {
        super(R.layout.item_animal, items);
    }

    @Override
    public void bind(@NotNull View itemView, Animal item, int position, @NotNull BaseViewHolderImp viewHolder) {
         text = (TextView) itemView.findViewById(R.id.titleTextView);
         image = (ImageView) itemView.findViewById(R.id.imageView);
    }
}
