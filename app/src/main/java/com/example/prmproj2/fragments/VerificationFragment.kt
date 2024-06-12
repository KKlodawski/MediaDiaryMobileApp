package com.example.prmproj2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.prmproj2.R
import com.example.prmproj2.databinding.FragmentVerificationBinding


class VerificationFragment : Fragment() {
    lateinit var binding: FragmentVerificationBinding
    val pin = "147258"

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
        binding.verifyPinButton.apply {
            setOnClickListener{
                if(binding.pinInput.text.toString() == pin) {
                    findNavController().navigate(
                        R.id.action_verificationFragment_to_entryFragment
                    )
                } else {
                    Toast.makeText(context,R.string.WrongPIN, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}