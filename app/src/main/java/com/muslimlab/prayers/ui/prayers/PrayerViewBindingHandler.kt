package com.muslimlab.prayers.ui.prayers

import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.muslimlab.prayers.R
import com.muslimlab.prayers.databinding.FragmentHomeBinding
import com.muslimlab.prayers.databinding.PrayerTimeItemBinding
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class PrayerViewBindingHandler(
    private val viewModel: PrayersViewModel,
    private val binding: FragmentHomeBinding
) {

    private val disposables = mutableListOf<Disposable>()

    private val inflater by lazy { LayoutInflater.from(binding.root.context) }

    init {
        bindHeader()
        addPrayerViews()
    }

    private fun bindHeader() {
        //current prayer
        //mins to go
        toDisposable {
            viewModel.todayDateAndTime
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.todayDate.text = it.first
                    binding.todayTime.text = it.second
                }
        }
    }

    

    private fun addPrayerViews() {
        Glide.with(binding.root)
            .load(R.drawable.prayer_banner)
            .centerCrop()
            .into(binding.prayerBannerImage)

        toDisposable {
            viewModel.prayers
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bindPrayer(it[PrayerName.imsak])
                    bindPrayer(it[PrayerName.fajir])
                    bindPrayer(it[PrayerName.duhur])
                    bindPrayer(it[PrayerName.asr])
                    bindPrayer(it[PrayerName.maghrib])
                    bindPrayer(it[PrayerName.isha])
                }
        }
    }

    private fun toDisposable(disposable: () -> Disposable) {
        disposables.add(disposable.invoke())
    }

    private fun bindPrayer(
        item: PrayerItem?
    ) {
        item?.let {
            binding.prayerContent.prayersContainer.run {
                addView(
                    PrayerTimeItemBinding.inflate(inflater, this, false).apply {
                        prayerNameText.text = it.name.name
                        prayerTimeText.text = it.time
                        prayerAlarmIcon.setImageResource(
                            if (it.isAlarmOn) R.drawable.ic_sound_on
                            else R.drawable.ic_sound_off
                        )
                    }.root
                )
            }

        }
    }
}

private fun Disposable.toDisposable(disposables: MutableList<Disposable>) {
    disposables.add(this)
}
