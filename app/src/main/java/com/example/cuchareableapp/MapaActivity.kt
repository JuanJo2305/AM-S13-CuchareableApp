package com.example.cuchareableapp

import android.Manifest
import android.content.pm.PackageManager
import kotlin.random.Random
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapaActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeAdapter: PlaceAdapter
    private var currentLocation: LatLng? = null
    private val listaLugares = mutableListOf<PlaceModel>()
    private lateinit var categoriaSeleccionada: String
    private var ubicacionSeleccionada: LatLng? = null
    private val categorias = listOf("Comida criolla", "Mariscos", "Chifa", "Postres", "Cafetería")
    private var nombreTemporal: String? = null
    private var categoriaTemporal: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        categoriaSeleccionada = intent.getStringExtra("nombre_categoria") ?: "restaurant"

        recyclerView = findViewById(R.id.rvPlaces)
        recyclerView.layoutManager = LinearLayoutManager(this)
        placeAdapter = PlaceAdapter(emptyList()) { place ->
            val latLng = LatLng(place.latitud, place.longitud)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            mMap.addMarker(MarkerOptions().position(latLng).title(place.nombre))

        }
        recyclerView.adapter = placeAdapter

        Places.initialize(applicationContext, getString(R.string.key))
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarHuarique)
        fabAgregar.setOnClickListener {
            mostrarDialogoAgregarHuarique()
        }

        val fabVerLugares = findViewById<FloatingActionButton>(R.id.fabVerLugares)
        fabVerLugares.setOnClickListener {
            mostrarBottomSheetConLugares()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        obtenerUbicacionActual()
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f))
                mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Tú estás aquí"))

                buscarLugaresPorCategoria(categoriaSeleccionada)
            }
        }
    }

    private fun buscarLugaresPorCategoria(categoria: String) {
        val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES))

        val locationBias = RectangularBounds.newInstance(
            LatLng(currentLocation!!.latitude - 0.01, currentLocation!!.longitude - 0.01),
            LatLng(currentLocation!!.latitude + 0.01, currentLocation!!.longitude + 0.01)
        )

        val nearbyRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery(categoria)
            .setLocationBias(locationBias)
            .build()

        placesClient.findAutocompletePredictions(nearbyRequest).addOnSuccessListener { response ->
            val places = response.autocompletePredictions.mapNotNull { prediction ->
                // Para cada predicción, podemos intentar obtener sus detalles
                val placeLatLng = LatLng(
                    currentLocation!!.latitude + Random.nextDouble(-0.005, 0.005), // Simulado
                    currentLocation!!.longitude + Random.nextDouble(-0.005, 0.005)
                )
                PlaceModel(
                    nombre = prediction.getPrimaryText(null).toString(),
                    categoria = "Desconocida", // puedes colocar una categoría por defecto si no la tienes
                    latitud = placeLatLng.latitude,
                    longitud = placeLatLng.longitude
                )
            }

            listaLugares.clear()
            listaLugares.addAll(places)

            val bottomSheet = PlaceBottomSheetFragment(places) { lugar ->
                val latLng = LatLng(lugar.latitud, lugar.longitud)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                mMap.addMarker(MarkerOptions().position(latLng).title(lugar.nombre))
            }
            bottomSheet.show(supportFragmentManager, "PlaceBottomSheet")
        }
    }
    private fun mostrarDialogoAgregarHuarique() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_huarique, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreHuarique)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val btnElegirUbicacion = dialogView.findViewById<Button>(R.id.btnElegirUbicacion)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardarHuarique)

        // Llenar Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        // Restaurar valores temporales si existen
        etNombre.setText(nombreTemporal ?: "")
        categoriaTemporal?.let {
            val index = categorias.indexOf(it)
            if (index >= 0) spinner.setSelection(index)
        }


        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Elegir ubicación
        btnElegirUbicacion.setOnClickListener {

            // Guardar temporalmente los valores
            nombreTemporal = etNombre.text.toString()
            categoriaTemporal = spinner.selectedItem.toString()

            Toast.makeText(this, "Toca el mapa para seleccionar ubicación", Toast.LENGTH_SHORT).show()

            mMap.setOnMapClickListener { latLng ->
                ubicacionSeleccionada = latLng
                Toast.makeText(this, "Ubicación elegida: ${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_SHORT).show()

                // Feedback visual
                btnElegirUbicacion.text = "Ubicación seleccionada ✅"
                mMap.setOnMapClickListener(null) // Detener escucha después de una selección
            }
        }

        // Guardar
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val categoria = spinner.selectedItem.toString()

            if (nombre.isBlank() || ubicacionSeleccionada == null) {
                Toast.makeText(this, "Completa todos los campos y selecciona una ubicación", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoHuarique = PlaceModel(
                    nombre,
                    categoria,
                    ubicacionSeleccionada!!.latitude,
                    ubicacionSeleccionada!!.longitude
                )

                agregarMarcador(nuevoHuarique)
                listaLugares.add(nuevoHuarique)

                Toast.makeText(this, "Huarique agregado correctamente", Toast.LENGTH_SHORT).show()

                nombreTemporal = null
                categoriaTemporal = null
                ubicacionSeleccionada = null

                alertDialog.dismiss()
            }
        }


        alertDialog.show()
    }

    private fun agregarMarcador(place: PlaceModel) {
        val markerOptions = MarkerOptions()
            .position(LatLng(place.latitud, place.longitud))
            .title(place.nombre)
            .snippet(place.categoria)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        mMap.addMarker(markerOptions)
    }

    fun mostrarBottomSheetConLugares() {
        val bottomSheet = PlaceBottomSheetFragment(listaLugares) { place ->
            val latLng = LatLng(place.latitud, place.longitud)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            mMap.addMarker(MarkerOptions().position(latLng).title(place.nombre))
        }
        bottomSheet.show(supportFragmentManager, "PlaceBottomSheet")
    }





}
