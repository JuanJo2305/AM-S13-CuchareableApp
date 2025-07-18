package com.example.cuchareableapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.java

class HomeActivity : AppCompatActivity() {
    private lateinit var rvCategorias: RecyclerView
    private lateinit var categorias: List<Categoria>
    private lateinit var adapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvCategorias = findViewById(R.id.rvCategorias)

        categorias = listOf(
            Categoria("Comida Marina", R.drawable.ic_marina),
            Categoria("Chifa", R.drawable.ic_chifa),
            Categoria("Pollo a la brasa", R.drawable.ic_pollo),
            Categoria("Parrillas", R.drawable.ic_parrilla),
            Categoria("Cafe", R.drawable.ic_cafe)
        )

        adapter = CategoriaAdapter(categorias) { categoria ->
            val intent = Intent(this, MapaActivity::class.java)
            intent.putExtra("nombre_categoria", categoria.nombre)
            startActivity(intent)
        }

        rvCategorias.layoutManager = GridLayoutManager(this, 2)
        rvCategorias.adapter = adapter
    }
}
