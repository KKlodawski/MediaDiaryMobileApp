package com.example.prmproj2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prmproj2.R
import com.example.prmproj2.adapters.Database
import com.example.prmproj2.adapters.EntryListAdapter
import com.example.prmproj2.databinding.FragmentEntryBinding

class EntryFragment : Fragment() {
    lateinit var binding: FragmentEntryBinding
    lateinit var entryListAdapter: EntryListAdapter
    lateinit var database: Database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEntryBinding.inflate(layoutInflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entryListAdapter = context?.let { EntryListAdapter(it) }!!

        binding.entryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entryListAdapter
        }

        entryListAdapter.entryList = database.getAllEntries()

        binding.entryAddButton.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_entryAddFormFragment)
            }
        }
    }
}