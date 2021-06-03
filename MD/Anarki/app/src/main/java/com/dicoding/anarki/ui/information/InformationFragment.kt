package com.dicoding.anarki.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.anarki.databinding.FragmentInformationBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class InformationFragment : Fragment() {

    private lateinit var binding:FragmentInformationBinding
    private lateinit var youTubePlayerView:YouTubePlayerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater, container, false)

        youTubePlayerView = binding.activityMainYoutubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        return binding.root
    }

}