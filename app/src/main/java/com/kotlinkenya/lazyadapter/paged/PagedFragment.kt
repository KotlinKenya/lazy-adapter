package com.kotlinkenya.lazyadapter.paged

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kotlinkenya.lazyadapter.R
import com.kotlinkenya.lazyadapter.databinding.FragmentGenericBinding
import com.kotlinkenya.lazyadapter.databinding.ItemGenericBinding
import com.kotlinkenya.libraries.lazyadapter.LazyPagingAdapter
import com.kotlinkenya.libraries.lazyadapter.utils.LazyCompare
import java.text.DecimalFormat

/**
 * @project LazyAdapter
 * @author mambobryan
 * @email mambobryan@gmail.com
 * Tue 22 Nov 2022
 */
class PagedFragment : Fragment(R.layout.fragment_generic) {

    private var _binding: FragmentGenericBinding? = null
    private val binding get() = _binding!!

    inner class PagedItem(val number: String) : LazyCompare()

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
        val adapter = LazyPagingAdapter<PagedItem, ItemGenericBinding>()
        adapter
            .onCreate { parent: ViewGroup ->
                ItemGenericBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
            .onBind { item: PagedItem ->
                tvText.text = item.number
            }
            .onItemClicked {
                Toast.makeText(requireContext(), "Item ${it.number} clicked!", Toast.LENGTH_SHORT)
                    .show()
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

        adapter.add(generateList())
    }

    private fun generateList(): List<PagedItem> {
        val range = 0..10
        return range.map { PagedItem(Words.convert(it.toLong())) }
    }

    object Words {

        private val tensNames = arrayOf(
            "", " ten", " twenty", " thirty", " forty",
            " fifty", " sixty", " seventy", " eighty", " ninety"
        )
        private val numNames = arrayOf(
            "", " one", " two", " three", " four", " five",
            " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen",
            " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen"
        )

        private fun convertLessThanOneThousand(n: Int): String {
            var number = n
            var soFar: String
            if (number % 100 < 20) {
                soFar = numNames[number % 100]
                number /= 100
            } else {
                soFar = numNames[number % 10]
                number /= 10
                soFar = tensNames[number % 10] + soFar
                number /= 10
            }
            return if (number == 0) soFar else numNames[number] + " hundred" + soFar
        }

        fun convert(number: Long): String {
            // 0 to 999 999 999 999
            if (number == 0L) {
                return "zero"
            }
            val snumber: String

            // pad with "0"
            val mask = "000000000000"
            val df = DecimalFormat(mask)
            snumber = df.format(number)

            val billions = snumber.substring(0, 3).toInt()
            val millions = snumber.substring(3, 6).toInt()
            val hundredThousands = snumber.substring(6, 9).toInt()
            val thousands = snumber.substring(9, 12).toInt()
            val tradBillions: String = when (billions) {
                0 -> ""
                1 -> convertLessThanOneThousand(billions) + " billion "
                else -> convertLessThanOneThousand(billions) + " billion "
            }
            var result = tradBillions
            val tradMillions: String = when (millions) {
                0 -> ""
                1 -> convertLessThanOneThousand(millions) + " million "
                else -> convertLessThanOneThousand(millions) + " million "
            }
            result += tradMillions
            val tradHundredThousands: String = when (hundredThousands) {
                0 -> ""
                1 -> "one thousand "
                else -> convertLessThanOneThousand(hundredThousands) + " thousand "
            }
            result = result.plus(tradHundredThousands)
            val tradThousand: String = convertLessThanOneThousand(thousands)
            result += tradThousand

            return result.replace("^\\s+".toRegex(), "").replace("\\b\\s{2,}\\b".toRegex(), " ")
        }
    }

}