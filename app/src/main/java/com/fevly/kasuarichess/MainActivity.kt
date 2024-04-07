package com.fevly.kasuarichess
/*==========================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com

board schema :
0,0 0,1 0,2 0,3 0,4 0,5 0,6 0,7
1,0 1,1 1,2 1,3 1,4 1,5 1,6 1,7
2,0 2,1 2,2 2,3 2,4 2,5 2,6 2,7
3,0 3,1 3,2 3,3 3,4 3,5 3,6 3,7
4,0 4,1 4,2 4,3 4,4 4,5 4,6 4,7
5,0 5,1 5,2 5,3 5,4 5,5 5,6 5,7
6,0 6,1 6,2 6,3 6,4 6,5 6,6 6,7
7,0 7,1 7,2 7,3 7,4 7,5 7,6 7,7
=========================================*/
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fevly.kasuarichess.fragments.AnalysisFragment
import com.fevly.kasuarichess.fragments.BoardFragment
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
   lateinit var viewPager: ViewPager

    private fun createTabIcons() {
        val tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//        tabOne.text = "Board"
     tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.boardgame, 0, 0)
        tabLayout.getTabAt(0)!!.customView = tabOne
     val tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//       tabTwo.text = "Analysis"
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.analysis, 0, 0)
        tabLayout.getTabAt(1)!!.customView = tabTwo


    }

    private fun createViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(BoardFragment(), "")
        adapter.addFrag(AnalysisFragment(), "")
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