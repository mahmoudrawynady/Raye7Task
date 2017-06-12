package Module;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
/*
* this class is designed for the route object and its data that we will load from the Maps APIs.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
