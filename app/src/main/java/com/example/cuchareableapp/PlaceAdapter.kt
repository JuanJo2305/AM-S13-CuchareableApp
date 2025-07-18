package com.example.cuchareableapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(
    private var lugares: List<PlaceModel>,
    private val onClick: (PlaceModel) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreLugar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val lugar = lugares[position]
        holder.nombre.text = lugar.nombre
        holder.itemView.setOnClickListener { onClick(lugar) }
    }

    override fun getItemCount(): Int = lugares.size

    fun actualizarLugares(nuevos: List<PlaceModel>) {
        lugares = nuevos
        notifyDataSetChanged()
    }
}
