package com.example.prmproj2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prmproj2.R
import com.example.prmproj2.adapters.EntryListAdapter
import com.example.prmproj2.data.EntryRepository
import com.example.prmproj2.data.RepositoryLocator
import com.example.prmproj2.databinding.FragmentEntryBinding
import com.example.prmproj2.databinding.FragmentVerificationBinding

class EntryFragment : Fragment() {
    lateinit var binding: FragmentEntryBinding
    lateinit var entryRepository: EntryRepository
    lateinit var entryListAdapter: EntryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryRepository = RepositoryLocator.entryRepository
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentEntryBinding.inflate(layoutInflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entryListAdapter = EntryListAdapter()

        binding.entryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entryListAdapter
        }

        entryListAdapter.entryList = entryRepository.getEntryList();
    }
}