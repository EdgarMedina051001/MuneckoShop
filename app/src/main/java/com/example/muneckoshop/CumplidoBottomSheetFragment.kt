package com.example.muneckoshop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.muneckoshop.databinding.FragmentCumplidoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CumplidoBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCumplidoBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCumplidoBottomSheetBinding.inflate(layoutInflater, container, false)
        binding.btnIrMenu.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}