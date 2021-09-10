package com.example.mobilepaindiary.fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilepaindiary.R;
import com.example.mobilepaindiary.databinding.HomeFragmentBinding;
import com.example.mobilepaindiary.databinding.MapFragmentBinding;
import com.example.mobilepaindiary.viewmodel.SharedViewModel;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.io.IOException;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MapFragment extends Fragment {
    private MapFragmentBinding mapBinding;
    private MapView mapView;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = getString(R.string.mapbox_access_token);
        Mapbox.getInstance(this.getActivity(), token);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        mapBinding = MapFragmentBinding.inflate(inflater, container, false);
        View view = mapBinding.getRoot();


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initial map centric coordination in hong kong
        final LatLng latLng = new LatLng(22.302711, 114.177216);
        mapView = (MapView) getView().findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        generateMap(latLng);

        // search by address
        mapBinding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mapBinding.addressInput.getText().toString();
                LatLng latLng = getLocationByAddress(view.getContext(), address);
                generateMap(latLng);
            }
        });
    }

    /**
     * Search location by name to get the most probable coordination
     *
     * @param context
     * @param address
     * @return a LadLng object that contain the location coordination of the address
     */
    public LatLng getLocationByAddress(Context context, String address) {
        Geocoder coder = new Geocoder(context);
        List<Address> addrList;
        LatLng latLng = null;
        try {
            // search the string address, and get a possible address list of at most 3 items
            addrList = coder.getFromLocationName(address, 3);
            if (addrList != null) {
                // get the most possible address latlng
                Address location = addrList.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLng;
    }

    /**
     * generate Map based on the latLng object value
     *
     * @param latLng
     */
    public void generateMap(LatLng latLng) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // add annotation
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("currentPosition"));
                        // set camera position
                        CameraPosition position = new CameraPosition.Builder().target(latLng)
                                .zoom(13)
                                .build();
                        mapboxMap.setCameraPosition(position);
                    }
                });


            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapBinding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}

