package com.buzzware.iridedriver.Models.response.directions;

import java.util.List;

public class DirectionsApiResponse {
    public List<GeocodedWaypoint> geocoded_waypoints;
    public List<Route> routes;
    public String status;
}
