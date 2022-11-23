package com.kotlinkenya.lazyadapter.normal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kotlinkenya.lazyadapter.R
import com.kotlinkenya.lazyadapter.databinding.FragmentGenericBinding
import com.kotlinkenya.lazyadapter.databinding.ItemGenericBinding
import com.kotlinkenya.libraries.lazyadapter.LazyAdapter
import com.kotlinkenya.libraries.lazyadapter.utils.LazyCompare

/**
 * @project LazyAdapter
 * @author mambobryan
 * @email mambobryan@gmail.com
 * Tue 22 Nov 2022
 */
class NormalFragment : Fragment(R.layout.fragment_generic) {

    private var _binding: FragmentGenericBinding? = null
    private val binding get() = _binding!!

    inner class NormalItem(val number: Int) : LazyCompare()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGenericBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        val adapter = LazyAdapter<NormalItem, ItemGenericBinding>()
            .onCreate { parent: ViewGroup ->
                Log.i("SOMETHING", "creating")

                ItemGenericBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
            .onBind { item: NormalItem ->
                Log.i("SOMETHING", "binding")

                tvText.text = item.number.toString()
            }
            .onItemClicked {
                Log.i("SOMETHING", "clicking")

                Toast.makeText(requireContext(), "Item ${it.number} clicked!", Toast.LENGTH_SHORT).show()
            }
            .onSwipedRight(binding.root) {
                Toast.makeText(
                    requireContext(),
                    "Swiped Right for ${it.number}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSwipedLeft(binding.root) {
                Toast.makeText(requireContext(), "Swiped Left for ${it.number}", Toast.LENGTH_SHORT)
                    .show()
            }

        binding.root.adapter = adapter
        adapter.submitList(generateList())
    }

    private fun generateList(): List<NormalItem> {
        val range = 0..10
        return range.map { NormalItem(it) }
    }

}