package com.example.prmproj2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.prmproj2.R
import com.example.prmproj2.databinding.FragmentVerificationBinding


class VerificationFragment : Fragment() {
    lateinit var binding: FragmentVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentVerificationBinding.inflate(layoutInflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tmpNextDestination.apply {
            setOnClickListener{
                findNavController().navigate(
                    R.id.action_verificationFragment_to_entryFragment
                )
            }
        }
    }

}