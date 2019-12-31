package com.muslimlab.prayers.ui.prayers

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.muslimlab.prayers.PrayersApplication
import com.muslimlab.prayers.R
import com.muslimlab.prayers.databinding.FragmentPrayersBinding

/**
 * A placeholder fragment containing a simple view.
 */
class PrayersFragment : Fragment() {

    lateinit var viewModel: PrayersViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = PrayersViewModel(
            PrayerRepositoryFactory
                .createNetworkPrayerRepository(
                    context.getSharedPreferences("pref_prayer_data", Context.MODE_PRIVATE),
                    (activity?.application as PrayersApplication).getRetrofitInstance()
                ),

            context.getSharedPreferences("pref_prayer_data", Context.MODE_PRIVATE),
            PrayerAlarmManagerImpl(context)
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPrayersBinding.inflate(inflater).run {
        this.vm = viewModel
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
        fun newInstance(): PrayersFragment {
            return PrayersFragment()
        }
    }
}
