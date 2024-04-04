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
import com.fevly.kasuarichess.depend.StockfishEngine
import com.fevly.kasuarichess.stockengine.StockfishFeeder
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




/*    ========================040424===================
        info depth 8 seldepth 6 multipv 1 score cp -21 nodes 4939 nps 66743 hashfull 1 tbhits 0 time 74 pv c7c5 g1f3 b8c6 b1c3 e7e5
        info depth 8 seldepth 6 multipv 1 score cp -21 nodes 4939 nps 66743 hashfull 1 tbhits 0 time 74 pv c7c5 g1f3 b8c6 b1c3 e7e5
        info depth 8 seldepth 6 multipv 1 score cp -21 nodes 4939 nps 66743 hashfull 1 tbhits 0 time 74 pv c7c5 g1f3 b8c6 b1c3 e7e5
        info depth 8 seldepth 6 multipv 1 score cp -21 nodes 4939 nps 66743 hashfull 1 tbhits 0 time 74 pv c7c5 g1f3 b8c6 b1c3 e7e5
        ================================================*/
        var stockfishFeeder = StockfishFeeder(this)


    } // ends on create

}