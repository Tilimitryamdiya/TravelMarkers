package ru.netology.travelmarkers.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.flow.collectLatest
import ru.netology.travelmarkers.R
import ru.netology.travelmarkers.databinding.FragmentMapBinding
import ru.netology.travelmarkers.databinding.PlaceBinding
import ru.netology.travelmarkers.viewmodel.BookmarksViewModel

class MapFragment : Fragment() {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
    }

    private var mapView: MapView? = null

    private val viewModel: BookmarksViewModel by viewModels()

    private lateinit var userLocationLayer: UserLocationLayer

    private val mapTapListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            findNavController().navigate(R.id.action_map_fragment_to_add_bookmark_fragment,
                bundleOf(
                    AddBookmarkFragment.LAT_KEY to point.latitude,
                    AddBookmarkFragment.LONG_KEY to point.longitude
                )
            )
        }
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(userLocationView: UserLocationView) {
            userLocationView.arrow
                .setIcon(ImageProvider.fromResource(requireContext(), R.drawable.location_pin))
        }

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocationLayer.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, 10F, 0F, 0F))
            }
            userLocationLayer.setObjectListener(null)
        }
    }


    private val bookmarkTapListener = MapObjectTapListener { mapObject, _ ->
        ConfirmDeleteDialog
            .newInstance(mapObject.userData as Long)
            .show(childFragmentManager, null)
        true
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                    userLocationLayer.cameraPosition()?.target?.also {
                        val map = mapView?.map ?: return@registerForActivityResult
                        val cameraPosition = map.cameraPosition
                        map.move(
                            CameraPosition(
                                it,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt,
                            )
                        )
                    }
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.permission_request),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView.apply {
            userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            if (requireActivity()
                    .checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = false
            }

            map.addInputListener(mapTapListener)

            val collection = map.mapObjects.addCollection()
            viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
                viewModel.data.collectLatest { data ->
                    collection.clear()
                    data.forEach { place ->
                        val placeBinding = PlaceBinding.inflate(layoutInflater)
                        collection.addPlacemark(
                            Point(place.latitude, place.longitude),
                            ViewProvider(placeBinding.root)
                        ).apply {
                            userData = place.id
                        }
                    }
                }
            }
            collection.addTapListener(bookmarkTapListener)

            // Переход к точке на карте после клика на списке
            val arguments = arguments
            if (arguments != null &&
                arguments.containsKey(LAT_KEY) &&
                arguments.containsKey(LONG_KEY)
            ) {
                val cameraPosition = map.cameraPosition
                map.move(
                    CameraPosition(
                        Point(arguments.getDouble(LAT_KEY), arguments.getDouble(LONG_KEY)),
                        10F,
                        cameraPosition.azimuth,
                        cameraPosition.tilt,
                    )
                )
                arguments.remove(LAT_KEY)
                arguments.remove(LONG_KEY)
            } else {
                // При входе в приложение показываем текущее местоположение
                userLocationLayer.setObjectListener(locationObjectListener)
            }
        }

        binding.plus.setOnClickListener {
            binding.mapView.map.move(
                CameraPosition(
                    binding.mapView.map.cameraPosition.target,
                    binding.mapView.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null
            )
        }

        binding.minus.setOnClickListener {
            binding.mapView.map.move(
                CameraPosition(
                    binding.mapView.map.cameraPosition.target,
                    binding.mapView.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null,
            )
        }

        binding.fabLocation.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
                menuInflater.inflate(R.menu.bottom_nav_menu, menu)


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                if (menuItem.itemId == R.id.list) {
                    findNavController().navigate(R.id.action_map_fragment_to_bookmarks_fragment)
                    true
                } else {
                    false
                }

        }, viewLifecycleOwner)


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView = null
        super.onDestroy()
    }
}
