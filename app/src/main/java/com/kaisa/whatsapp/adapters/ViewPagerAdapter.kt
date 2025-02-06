package com.kaisa.whatsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kaisa.whatsapp.fragments.ContatosFragment
import com.kaisa.whatsapp.fragments.ConversasFragment

class ViewPagerAdapter(
    val abas : List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    // Quantidade de abas que voce tem
    override fun getItemCount(): Int {
        return abas.size //listOf("Conversas", "Contatos")
    }
// Aba selecionada
    override fun createFragment(position: Int): Fragment {
        when(position){
            1 -> return ContatosFragment()
        }
    return ConversasFragment()
    }
}