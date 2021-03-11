package com.example.nestedrecyclersample.hhd;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nestedrecyclersample.R;
import com.example.nestedrecyclersample.data.domain.Animal;
import com.example.nestedrecyclersample.data.domain.AnimalSection;
import com.example.nestedrecyclersample.utils.SingleScrollDirectionEnforcer;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 类描述：主页面。
 *
 * @author HeHongdan
 * @date 3/11/21
 * @since v3/11/21
 */
public class MainActivity extends AppCompatActivity {

    public static final String sectionsKey = "sectionsKey";
    private List<AnimalSection> sections;
    private Moshi moshi;
    private JsonAdapter<List<AnimalSection>> animalSectionJsonAdapter;
    private AnimalSectionAdapter sectionAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moshi = new Moshi.Builder().build();
        animalSectionJsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, AnimalSection.class));

        initViews(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(sectionsKey, animalSectionJsonAdapter.toJson(sections));
        super.onSaveInstanceState(outState);
    }

    private void initViews(Bundle savedInstanceState) {
        String savedSections = savedInstanceState == null ? null : savedInstanceState.getString(sectionsKey);

        if (sections != null) {
            Log.d("HHD", "sections 不为空" + savedSections);
        } else {
            Log.d("HHD", "sections 为空" + savedSections);
        }

        if (sections == null && savedSections != null) {
            try {
                sections = animalSectionJsonAdapter.fromJson(savedSections);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("HHD", "JSon解析出错");
            }
        }
        if (sections == null) {
            sections = createSections(50, 25);
        }

        ConcatAdapter concatAdapter = new ConcatAdapter();
        sectionAdapter = new AnimalSectionAdapter(sections);
        concatAdapter.addAdapter(sectionAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(concatAdapter);
        enforceSingleScrollDirection(recyclerView);

    }

    /**
     * 2/4-RV 水平方向与垂直方向滑动的冲突解决。
     *
     * @param rv RV。
     */
    private void enforceSingleScrollDirection(RecyclerView rv) {
        SingleScrollDirectionEnforcer enforcer = new SingleScrollDirectionEnforcer();
        rv.addOnItemTouchListener(enforcer);
        rv.addOnScrollListener(enforcer);
    }

    /**
     * Creates and populates given number of sections with given number of items.
     *
     * [numberOfSections] the size of returned list of Sections
     * [itemsPerSection] the number of items inside each section
     */
    private List<AnimalSection> createSections(int numberOfSections, int itemsPerSection) {
        List<AnimalSection> sections = new ArrayList<>();

        for (int i = 0; i < numberOfSections; i++) {
            List<Animal> animals = new ArrayList<>();
            String title = "动物" + i;

            for (int j = 0; j < itemsPerSection; j++) {
                Animal animal = new Animal("Cat" + j, "https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png");
                animals.add(animal);
            }

            AnimalSection section = new AnimalSection(UUID.randomUUID().toString(), title, animals);
            sections.add(section);
        }
        return sections;
    }


}
