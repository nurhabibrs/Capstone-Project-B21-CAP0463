package com.dicoding.anarki.ui.recent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.databinding.FragmentRecentBinding
import com.dicoding.anarki.viemodel.ViewModelFactory
import com.dicoding.anarki.vo.Resource
import com.google.android.material.snackbar.Snackbar

class RecentFragment : Fragment() {

    private lateinit var binding: FragmentRecentBinding
    private lateinit var viewModel: RecentViewModel
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var predict: LiveData<Resource<PredictEntity>>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //itemTouchHelper.attachToRecyclerView(binding.rvMovie)
        if (activity != null) {

            val factory = ViewModelFactory.getInstance(requireActivity())
            viewModel = ViewModelProvider(this, factory)[RecentViewModel::class.java]

            recentAdapter = RecentAdapter()

            viewModel.getHistory().observe(viewLifecycleOwner, { history ->
                if (history != null) {
                    recentAdapter.submitList(history)
                    //showProgressBar(false)
                }
            })
            with(binding.rvHistory) {
                layoutManager = GridLayoutManager(context, 2)
                setHasFixedSize(true)
                this.adapter = recentAdapter
            }

            binding.clearHistory.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    deleteHistory()
                }

            })
        }
    }

    private fun deleteHistory() {
        viewModel.deleteHistory()
    }
}

/*private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
        makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = true
    @Suppress("DEPRECATION")
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (view != null) {
            val swipedPosition = viewHolder.adapterPosition
            val predictEntity = recentAdapter.getSwipedData(swipedPosition)
            predictEntity?.let { viewModel.setFavMovie(it) }
            val snackbar = Snackbar.make(view as View, R.string.message_undo, Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.message_ok) { v ->
                movieEntity?.let { viewModel.setFavMovie(it) }
            }
            snackbar.show()
        }
    }
})*/

