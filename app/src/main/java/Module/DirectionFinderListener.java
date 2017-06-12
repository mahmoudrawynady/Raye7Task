package Module;

import java.util.List;
/*
* this listener provide away for communicating between the MapsActivity and the Directionfinder Class.
 */

public interface DirectionFinderListener {
    //to start finding the direction and the data that we need.
    void onDirectionFinderStart();
    //to handle many things after the success of finding the direction such as :- drawing the routes.
    void onDirectionFinderSuccess(List<Route> route);
}
