package com.example.cuchareableapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PlaceBottomSheetFragment(
    private val lugares: List<PlaceModel>,
    private val onItemClick: (PlaceModel) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_place_bottom_sheet, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerLugares)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PlaceAdapter(lugares, onItemClick)

        return view
    }
}
