package com.muslimlab.prayers.ui.prayers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.GlideBuilder
import com.muslimlab.prayers.PrayersApplication
import com.muslimlab.prayers.databinding.FragmentHomeBinding

/**
 * A placeholder fragment containing a simple view.
 */
class PrayersFragment : Fragment() {

    lateinit var viewModel: PrayersViewModel
    lateinit var bindingHandler: PrayerViewBindingHandler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentHomeBinding.inflate(inflater).run {
        viewModel = PrayersViewModel(
            PrayerRepositoryFactory
                .createNetworkPrayerRepository(
                    root.context.getSharedPreferences("pref_prayer_data", Context.MODE_PRIVATE),
                    (activity?.application as PrayersApplication).getRetrofitInstance()
                ),

            root.context.getSharedPreferences("pref_prayer_data", Context.MODE_PRIVATE),
            PrayerAlarmManagerImpl(root.context)
        )

        bindingHandler = PrayerViewBindingHandler(viewModel, this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onViewDestroyed()
    }

    companion object {
        fun newInstance(): PrayersFragment = PrayersFragment()
    }
}
