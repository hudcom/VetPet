package com.project.vetpet.view.tabs.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.vetpet.R
import com.project.vetpet.view.BaseFragment
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory
import java.io.IOException
import java.util.Locale

class MapsFragment : BaseFragment(), OnLocationSearchListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val mapsViewModel: MapsViewModel by viewModels { factory() }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        enableMyLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createSearchLine(view)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        setOnBackPressedKeyListener(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        /*// Отримати список адрес клінік з аргументів
        clinicAddresses = arguments?.getStringArray("clinicAddresses")?.toList()
        Log.d(TAG,"Maps fragment: clinicsAddress: ${arguments?.getStringArray("clinicAddresses")}")*/

        // Отримати значення searchQuery з аргументів
        val searchQuery = arguments?.getString("searchQuery")
        Log.d(TAG, "MapsFragment: $searchQuery")

        if (searchQuery != null && searchQuery != "") {
            setSearchAndOpenKeyboard(searchQuery)
        }
    }

    private fun showKeyboard(editText: EditText) {
        editText.requestFocus() // Фокусуємо поле введення

        val inputMethodManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) // Показуємо клавіатуру
    }

    private fun setSearchAndOpenKeyboard(searchQuery: String) {
        val searchEditText = view?.findViewById<EditText>(R.id.searchEditText)
        searchEditText?.setText(searchQuery) // Встановлюємо текст
        searchEditText?.requestFocus() // Фокусуємо поле введення

        // Відкриваємо клавіатуру
        searchEditText?.let { editText ->
            showKeyboard(editText)
        }
    }
    private fun createSearchLine(view: View) {
        // Отримати посилання на поле введення адреси
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)

        // Налаштувати слухача для поля введення адреси
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val address = searchEditText.text.toString()
                onLocationSearch(address)
                return@setOnEditorActionListener true
            }
            false
        }
    }
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запросити дозволи
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        if (::googleMap.isInitialized) {
            googleMap.isMyLocationEnabled = true
            var userLocation: LatLng? = null
            // Отримати останнє відоме місцезнаходження користувача
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(MarkerOptions().position(userLocation!!).title("Ви тут"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    // Додавання маркерів клінік в радіусі 1 км, якщо список адрес присутній
                    clinicAddresses?.let { addresses ->
                        val searchRadius = 1000 // Визначте радіус пошуку в метрах
                        addClinicMarkersInRange(addresses, searchRadius)
                        // Додати коло для відображення радіуса пошуку
                        if (userLocation != null) {
                            googleMap.addCircle(
                                CircleOptions()
                                    .center(userLocation)
                                    .radius(searchRadius.toDouble())
                                    .strokeColor(Color.BLUE)
                                    .fillColor(Color.argb(50, 50, 150, 250))
                            )
                        } else {
                            Log.e(TAG, "userLocation is null")
                        }
                    }

                } ?: run {
                    Log.e(TAG, "Не вдалося отримати місцезнаходження")
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Помилка отримання місцезнаходження: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Якщо дозволи надані, увімкнути функціональність визначення місцезнаходження
                enableMyLocation()
            } else {
                // Якщо дозволи не надані, покажіть повідомлення або увімкніть обмежений функціонал
                Toast.makeText(requireContext(), "Дозволи на доступ до місцезнаходження не надано", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onLocationSearch(address: String) {
        if (!::googleMap.isInitialized) {
            Log.e(TAG, "GoogleMap is not initialized")
            return
        }

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        mapsViewModel.setSearchQuery(address)
        try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    val location = addressList[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(MarkerOptions().position(latLng).title(address))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    Toast.makeText(requireContext(), "Адреса не знайдена", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Помилка під час пошуку адреси: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        hideKeyboard(requireView())
    }
    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun addClinicMarkersInRange(clinicAddresses: List<String>, searchRadius: Int) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запросити дозволи, якщо їх ще немає
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLocation = LatLng(it.latitude, it.longitude)
                clinicAddresses.forEach { address ->
                    try {
                        val addressList = geocoder.getFromLocationName(address, 1)
                        if (!addressList.isNullOrEmpty()) {
                            val clinicLocation = addressList[0]
                            val clinicLatLng = LatLng(clinicLocation.latitude, clinicLocation.longitude)
                            val distance = FloatArray(1)
                            Location.distanceBetween(
                                userLocation.latitude, userLocation.longitude,
                                clinicLatLng.latitude, clinicLatLng.longitude, distance
                            )
                            if (distance[0] <= searchRadius) {
                                googleMap.addMarker(MarkerOptions().position(clinicLatLng).title(address))
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } ?: run {
                Toast.makeText(requireContext(), "Не вдалося отримати місцезнаходження", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Помилка отримання місцезнаходження: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }





    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        var clinicAddresses: List<String>? = null
    }
}

interface OnLocationSearchListener {
    fun onLocationSearch(address: String)
}