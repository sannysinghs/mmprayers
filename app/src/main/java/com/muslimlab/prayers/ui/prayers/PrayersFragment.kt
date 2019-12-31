package com.muslimlab.prayers.ui.prayers

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.muslimlab.prayers.PrayersApplication
import com.muslimlab.prayers.R
import com.muslimlab.prayers.databinding.FragmentPrayersBinding
import com.muslimlab.prayers.ui.utils.applyTransparentStatusBar

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
                )
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

@BindingAdapter("app:prayerAlarmOn")
fun bindPrayerAlarmIcon(view: ImageView, isAlarmTurnedOn: Boolean) {
    view.setImageResource(if (isAlarmTurnedOn) R.drawable.ic_sound_on else R.drawable.ic_sound_off )
}
