package com.hardus.storyapp.main


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hardus.storyapp.R
import com.hardus.storyapp.addstory.AddStoryActivity
import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.databinding.ActivityMainBinding
import com.hardus.storyapp.location.MapsActivity
import com.hardus.storyapp.profile.ProfileActivity
import com.hardus.storyapp.util.Constant.EXTRA_TOKEN
import com.hardus.storyapp.util.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: ListStoryAdapter

    private var token: String = ""

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this, R.anim.to_bottom_anim
        )
    }

    private var clicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        token = intent.getStringExtra(EXTRA_TOKEN) ?: ""

        lifecycleScope.launchWhenCreated {
            launch {
                mainViewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        getAllStories()
        setRecyclerView()
        refreshView()
        goToSettings()

    }

    private fun refreshView() {
        mainBinding.swipeRefresh.setOnRefreshListener {
            getAllStories()
            mainBinding.swipeRefresh.isRefreshing = false
        }
    }

    private fun getAllStories() {
        mainViewModel.getAllStories(token).observe(this@MainActivity) {
            updateRecyclerViewData(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_apps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_language_mode -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            }
            else -> true
        }
    }


    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this@MainActivity)
        listAdapter = ListStoryAdapter()

        listAdapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1 || loadState.source.refresh is LoadState.Error) {
                mainBinding.apply {
                    tvNoData.animateVisibility(true)
                    ivNoData.animateVisibility(true)
                    rvListStory.animateVisibility(false)
                }
            } else {
                mainBinding.apply {
                    tvNoData.animateVisibility(false)
                    ivNoData.animateVisibility(false)
                    rvListStory.animateVisibility(true)
                }
            }
            mainBinding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = mainBinding.rvListStory
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = CustomLoadAdapter {
                        listAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun goToSettings() {
        mainBinding.fabIcon.setOnClickListener {
            onAddButtonClicked()
        }
        mainBinding.fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))

        }
        mainBinding.fabEdit.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))

        }
        mainBinding.fabLocation.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<EntityStory>) {

        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        listAdapter.submitData(lifecycle, stories)

        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimationFloatButton(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        mainBinding.apply {
            if (!clicked) {
                fabEdit.visibility = View.VISIBLE
                fabProfile.visibility = View.VISIBLE
                fabLocation.visibility = View.VISIBLE
            } else {
                fabEdit.visibility = View.INVISIBLE
                fabProfile.visibility = View.INVISIBLE
                fabLocation.visibility = View.INVISIBLE
            }
        }
    }

    private fun setAnimationFloatButton(clicked: Boolean) {
        mainBinding.apply {
            if (!clicked) {
                fabEdit.startAnimation(fromBottom)
                fabProfile.startAnimation(fromBottom)
                fabLocation.startAnimation(fromBottom)
                fabIcon.startAnimation(rotateOpen)

            } else {
                fabEdit.startAnimation(toBottom)
                fabProfile.startAnimation(toBottom)
                fabLocation.startAnimation(toBottom)
                fabIcon.startAnimation(rotateClose)
            }
        }
    }

}