package com.fevly.kasuarichess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
   lateinit var viewPager: ViewPager

    private fun createTabIcons() {
        val tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabOne.text = "Board"
     tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.boardgame, 0, 0)
        tabLayout.getTabAt(0)!!.customView = tabOne
//        val tabTwo = LayoutInflater.from(this).inflate(R.layout., null) as TextView
//        tabTwo.text = "Tab 2"
//        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.alert_light_frame, 0, 0)
//        tabLayout.getTabAt(1)!!.customView = tabTwo
//        val tabThree = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//        tabThree.text = "Tab 3"
//        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.arrow_down_float, 0, 0)
//        tabLayout.getTabAt(2)!!.customView = tabThree
    }

    private fun createViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(BoardFragment(), "Board title")
//        adapter.addFrag(Fragment2(), "Tab 2")
//        adapter.addFrag(Fragment3(), "Tab 3")
        viewPager.adapter = adapter
    }

    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList<Fragment>()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        createViewPager(viewPager)

        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
        createTabIcons()

    } // ends on create

}