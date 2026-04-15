package com.wangshu.textus.note.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wangshu.textus.note.model.main.MainActivity

class MainPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {
    private var fragments: List<Fragment> = emptyList()

    fun updateData(newList: List<Fragment>) {
        this.fragments = newList
        notifyDataSetChanged() // ViewPager2 会更优雅地处理这个刷新
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    // 关键：重写此方法确保 Fragment 身份唯一，防止刷新时索引混乱
    override fun getItemId(position: Int): Long = fragments[position].hashCode().toLong()
    override fun containsItem(itemId: Long): Boolean = fragments.any { it.hashCode().toLong() == itemId }
}
