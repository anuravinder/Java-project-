package com.lbads;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

public class MapsPoints implements Serializable {
    public LatLng latlng;
    public String title;
    public String description;
    public String type;
    public String location;
    public String offer;
    public String timings;
    public String imgs;
    public String phno;
    public String from_date;
    public String to_date;

    public MapsPoints(LatLng latlng,String title,String description,String type,String location,String offer,String timings,String imgs,String phno,String from_date,String to_date){
        this.latlng=latlng;
        this.title=title;
        this.description=description;
        this.type=type;
        this.location=location;
        this.offer=offer;
        this.timings=timings;
        this.imgs = imgs;
        this.phno=phno;
        this.from_date=from_date;
        this.to_date=to_date;
    }
}
