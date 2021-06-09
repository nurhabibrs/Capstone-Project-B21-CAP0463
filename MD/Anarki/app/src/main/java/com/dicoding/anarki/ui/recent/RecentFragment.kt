package com.dicoding.anarki.ui.recent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.anarki.databinding.FragmentRecentBinding
import com.dicoding.anarki.viemodel.ViewModelFactory

class RecentFragment : Fragment() {

    private lateinit var binding: FragmentRecentBinding
    private lateinit var viewModel: RecentViewModel
    private lateinit var recentAdapter: RecentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val factory = ViewModelFactory.getInstance(requireActivity())
            viewModel = ViewModelProvider(this, factory)[RecentViewModel::class.java]

            recentAdapter = RecentAdapter()

            viewModel.getHistory().observe(viewLifecycleOwner, { history ->
                if (history != null) {
                    recentAdapter.submitList(history)
                }
            })
            with(binding.rvHistory) {
                layoutManager = GridLayoutManager(context, 2)
                setHasFixedSize(true)
                this.adapter = recentAdapter
            }

            binding.clearHistory.setOnClickListener { deleteHistory() }
        }
    }

    private fun deleteHistory() {
        viewModel.deleteHistory()
    }
}