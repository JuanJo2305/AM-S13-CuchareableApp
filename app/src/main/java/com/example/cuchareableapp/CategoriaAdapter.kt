package com.example.cuchareableapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriaAdapter(
    private val categorias: List<Categoria>,
    private val onClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvCategoria)
        val ivIcono: ImageView = view.findViewById(R.id.ivCategoria)

        fun bind(categoria: Categoria) {
            tvNombre.text = categoria.nombre
            ivIcono.setImageResource(categoria.iconoResId)
            itemView.setOnClickListener { onClick(categoria) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = categorias.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categorias[position])
    }
}
