package com.kotlinkenya.lazyadapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlinkenya.lazyadapter.databinding.ActivityMainBinding
import com.kotlinkenya.lazyadapter.normal.NormalFragment
import com.kotlinkenya.lazyadapter.paged.PagedFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewPager()
    }

    private fun setupViewPager() {
        val titles = listOf("Normal", "Paged")
        val fragments = listOf(NormalFragment(), PagedFragment())

        val adapter = PagerAdapter(this, fragments)

        binding.apply {
            pager.isUserInputEnabled = false
            pager.adapter = adapter
            TabLayoutMediator(tabs, pager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }

    }

    class PagerAdapter(
        manager: FragmentActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(manager) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

}