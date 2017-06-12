package com.example.ph_data01221240053.raye7task;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Module.DirectionFinder;
import Module.DirectionFinderListener;
import Module.Route;

/*
this activity is responsible for the actions on the map.
* this MapsActivity is the activity that i use to get , draw and handle the maps and its activities.
* this activity handles its operations and its logics using alot of methods that i had created.
* */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {
    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Location mCurrentLocation;
    private DirectionFinderListener listener;
    private boolean locationEnabled;
    private String sourceaddress;
    private String distinationAddress;
    private LatLng sourceLocation;
    private LatLng distinationLoction;
    private String alertMessage;
    private String positiveButtonLabel;
    private String NegativeButtonLabel;
    private ImageView sClear;
    private ImageView dClear;
    private LatLng defaultArea;
    final double defaultLat = 30.0594699, defaultLon = 31.3285055;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setListener(this);
        btnFindPath = (Button) findViewById(R.id.button4);
        etOrigin = (EditText) findViewById(R.id.editText1);
        etDestination = (EditText) findViewById(R.id.editText);
        sClear = (ImageView) findViewById(R.id.sClear);
        dClear = (ImageView) findViewById(R.id.dClear);
        sClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOrigin.setText("");
            }
        });
        dClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDestination.setText("");
            }
        });


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    setAlertMessage(getstringResource(R.string.connectionAlert));
                    setPositiveButtonLabel(getstringResource(R.string.eWifi));
                    setNegativeButtonLabel(getstringResource(R.string.notwifi));
                    makeAlertMessage(getAlertMessage(), getPositiveButtonLabel(),
                            getNegativeButtonLabel()
                            , Settings.ACTION_WIFI_SETTINGS);
                } else
                    sendRequest();
            }
        });
        if (savedInstanceState != null) {
            restoreDataAfterRotation(savedInstanceState);
        }
    }

    /*
    * this method is used to check the connection of the device
    */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /*
   * this method is used to get saved data during the rotation
   */
    public void restoreDataAfterRotation(Bundle savedState) {
        setLocationEnabled(savedState.getBoolean(getstringResource(R.string.current)));
        setSourceaddress(savedState.getString(getResources().getString(R.string.sourceTag)));
        setDistinationAddress(savedState.getString(getResources().getString(R.string.destTag)));

        if (!checkNullForDestinationAndSource()) {
            try {
                new DirectionFinder(this, getSourceaddress(),
                        getDistinationAddress())
                        .execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    /*
   * this method is used to save oure data in configuration changing such as rotation
   */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(getstringResource(R.string.sourceTag), getSourceaddress());
        outState.putString(getstringResource(R.string.destTag), getDistinationAddress());
        outState.putBoolean(getstringResource(R.string.current), isLocationEnabled());
        outState.putString("org",etOrigin.getText().toString());
        outState.putString("dest",etDestination.getText().toString());

    }

    /*
   * this method is used to set the destination  Location in lat and lon
   */
    public boolean checkNullForDestinationAndSource() {
        if (getSourceaddress() == null || getDistinationAddress() == null)
            return true;
        return false;
    }

    public void setDistinationLoction(LatLng distinationLoction) {
        this.distinationLoction = distinationLoction;
    }

    /*
    * this method is used to get the destination  Location in lat and lon
    */
    public LatLng getDistinationLoction() {
        return distinationLoction;
    }

    /*
    * this method is used to marke that we in the current and longpress feature
    */
    public void setLocationEnabled(boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    public boolean isLocationEnabled() {
        return locationEnabled;
    }
    /*
    * this method is used to set the source address
    */

    public void setSourceaddress(String sourceaddress) {
        this.sourceaddress = sourceaddress;
    }
    /*
    * this method is used to get the source addreess
    */

    public String getSourceaddress() {
        return sourceaddress;
    }

    /*
    * this method is used to set the distination address
    */
    public void setDistinationAddress(String distinationAddress) {
        this.distinationAddress = distinationAddress;
    }
    /*
    * this method is used to get the distination addreess
    */

    public String getDistinationAddress() {
        return distinationAddress;
    }

    /*
    * this method is used to send request to the maps apis to get data
    */
    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        setSourceaddress(origin);
        setDistinationAddress(destination);
        if (origin.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.SourceAlertMessage), Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.DestAlertMessage), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkNullForDestinationAndSource()) {
            try {
                new DirectionFinder(this, getSourceaddress(), getDistinationAddress()).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * this method is used to set the current location
    */
    public void setSourceLocation(LatLng sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    /*
    * this method is used to set the source address
    */
    public LatLng getSourceLocation() {
        return sourceLocation;
    }

    /*
    * this method is used to concate the lat an the lon to make the location
    * this method but comma between the lat and lon to send the result to the url
    * this method when we use the current location and the longpressed to get the route
    */
    public String convertLocationTOString(LatLng loc) {
        double latitude = loc.latitude;
        double longtude = loc.longitude;
        String combination = latitude + "," + longtude;
        return combination;
    }
     /*
     * this method is used to put the cairo area as adefult for the map
       * when you open the map you will find the cairo region
     */

    public void goToDefaultArea() {
        defaultArea = new LatLng(defaultLat, defaultLon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultArea, 8));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        goToDefaultArea();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setLocationEnabled(lm.isProviderEnabled(LocationManager.GPS_PROVIDER));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                setLocationEnabled(lm.isProviderEnabled(LocationManager.GPS_PROVIDER));
                if (!isLocationEnabled()) {
                    setAlertMessage(getstringResource(R.string.DestAlertMessage));
                    setPositiveButtonLabel(getstringResource(R.string.DialogPositiveButtonLabel));
                    setNegativeButtonLabel(getstringResource(R.string.DialogNegativeButtonLabel));
                    makeAlertMessage(getAlertMessage(), getPositiveButtonLabel()
                            , getNegativeButtonLabel(),
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                }


                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                    setLocationEnabled(true);
                    mCurrentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (mCurrentLocation == null) {
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        String provider = lm.getBestProvider(criteria, true);
                        mCurrentLocation = lm.getLastKnownLocation(provider);
                        LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 8));
                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .position(current)));
                    }
                }
                return true;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {


            @Override
            public void onMapLongClick(LatLng latLng) {
                if (isLocationEnabled()) {
                    if (mCurrentLocation == null) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                        }
                        mCurrentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    mMap.clear();

                    setSourceLocation(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                    setDistinationLoction(latLng);
                    String convertedSource = convertLocationTOString(getSourceLocation());
                    String convertedDistination = convertLocationTOString(getDistinationLoction());
                    try {
                        new DirectionFinder(listener, convertedSource, convertedDistination).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public void makeAlertMessage(String message, String pButtonLabel, String nButtonLabel, final String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(pButtonLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(action);
                                startActivity(intent);

                            }
                        }).setNegativeButton(nButtonLabel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor(getString(R.string.ButtonLabelsColor)));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor(getString(R.string.ButtonLabelsColor)));


    }

    /*
   * this method is used at the Beggining of the Direction finding
   */
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.TitleMessage),
                getResources().getString(R.string.Message), true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }
     /*
    * This method is used to Draw the route between the source and the destination
    * After the data comes from the connection this method work to daw the route and handle data
    */

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 8));
            setSourceaddress(route.startAddress);
            setDistinationAddress(route.endAddress);
            etOrigin.setText(getSourceaddress());
            etDestination.setText(getDistinationAddress());
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(R.color.colorPrimary).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
            Toast.makeText(MapsActivity.this, getResources().getString(R.string.TimeMessage) + " "
                    + route.duration.text, Toast.LENGTH_LONG)
                    .show();
        }
    }
    /*
    * this method is used to set the Message of the AlertDialog
    */

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    /*
    * this method is used to get the Message of the AlertDialog
    */
    public String getAlertMessage() {
        return alertMessage;
    }

    /*
    * this method is used to set the Label of the PositiveButton in the AlertDialog
    */
    public void setPositiveButtonLabel(String positiveButtonLabel) {
        this.positiveButtonLabel = positiveButtonLabel;
    }

    /*
    *  this method is used to get the Label of the PositiveButton in the AlertDialog
    */
    public String getPositiveButtonLabel() {
        return positiveButtonLabel;
    }

    /*
    * this method is used to set the Label of the NegativeButton in the AlertDialog
    */
    public void setNegativeButtonLabel(String negativeButtonLabel) {
        NegativeButtonLabel = negativeButtonLabel;
    }

    /*
    * this method is used to get the Label of the NegativeButton in the AlertDialog
    */
    public String getNegativeButtonLabel() {
        return NegativeButtonLabel;
    }

    /*
    * this method to get any string resource easly
    */
    public String getstringResource(int id) {
        String stringResource = getResources().getString(id);
        return stringResource;


    }

    /*
   * this method is used to set the directionfinder listener
   */
    public void setListener(DirectionFinderListener listener) {
        this.listener = listener;
    }

    /*
   * this method is used to get the listner of directionfinding
   */
    public DirectionFinderListener getListener() {
        return listener;
    }

}
