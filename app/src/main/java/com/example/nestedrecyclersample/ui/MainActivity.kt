package com.example.nestedrecyclersample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nestedrecyclersample.data.DataSource
import com.example.nestedrecyclersample.data.domain.AnimalSection
import com.example.nestedrecyclersample.databinding.ActivityMainBinding
import com.example.nestedrecyclersample.ui.adapter.AnimalSectionAdapter
import com.example.nestedrecyclersample.utils.enforceSingleScrollDirection
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sectionAdapter: AnimalSectionAdapter
    private var sections: List<AnimalSection>? = null

    companion object {
        const val sectionsKey = "sectionsKey"
    }

    private val moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private val animalSectionJsonAdapter: JsonAdapter<List<AnimalSection>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, AnimalSection::class.java))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViews(savedInstanceState)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        //restore state if possible
        //IRL this would most likely be persisted inside a viewModel and you wouldn't need to worry about it
        //如果可能的话恢复状态
        // IRL这很可能会保留在viewModel中，而您不必担心
        val savedSections = savedInstanceState?.getString(sectionsKey)
        if (sections == null && savedSections != null) {
            sections = animalSectionJsonAdapter.fromJson(savedSections)
        }

        if (sections == null) {
            //create a populated list of sections
            //IRL you'd most likely be getting the data from a server on a background thread inside a viewModel
            //创建部分的填充列表
            // IRL您最有可能在viewModel内部的后台线程上从服务器获取数据
            sections = DataSource.createSections(numberOfSections = 50, itemsPerSection = 25)
        }
        //create an instance of ConcatAdapter
        //创建ConcatAdapter的实例
        val concatAdapter = ConcatAdapter()

        //create AnimalSectionAdapter for the sections and add to ConcatAdapter
        //为这些部分创建AnimalSectionAdapter并添加到ConcatAdapter
        sectionAdapter = AnimalSectionAdapter(sections ?: mutableListOf())
        concatAdapter.addAdapter(sectionAdapter)

        //setup the recycler
        //设置回收视图
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = concatAdapter
            enforceSingleScrollDirection()
        }
    }

    /**
     * ConcatAdapter
     *      |
     *      AnimalAdapter
     *      |
     *      AnimalSectionAdapter
     *
     */

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(sectionsKey, animalSectionJsonAdapter.toJson(sections))
        super.onSaveInstanceState(outState)
    }
}