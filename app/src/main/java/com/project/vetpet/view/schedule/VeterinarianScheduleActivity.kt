package com.project.vetpet.view.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.project.vetpet.R
import com.project.vetpet.model.Veterinarian
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class VeterinarianScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_veterinarian_schedule)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }

    private val dateFormat = DateTimeFormat.forPattern(DATE_FORMAT)

    private fun getCurrentDate(): DateTime {
        return DateTime.now()
    }

    private fun getDateForPosition(position: Int): DateTime {
        return getCurrentDate().plusDays(position)
    }

    private fun formatDate(dateTime: DateTime): String {
        return dateTime.toString(DATE_FORMAT)
    }

    private inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val date = getDateForPosition(position).toString(dateFormat)
            val veterinarian = intent.getSerializableExtra("veterinarian") as Veterinarian
            return ScheduleFragment.newInstance(date, veterinarian)
        }

        override fun getCount(): Int {
            return 7
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return formatDate(getDateForPosition(position))
        }
    }

    companion object {
        private const val DATE_FORMAT = "dd:MM:yyyy"
    }
}