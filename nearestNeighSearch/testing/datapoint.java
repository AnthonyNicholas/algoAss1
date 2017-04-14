/**
 * Created by New on 14/04/2017.
 */
public class datapoint {
    public int ID;
    public float lat;
    public float lon;
    public String cat;

    public datapoint(int id, float Lat, float Lon, String Cat) {
        ID = id;
        lat = Lat;
        lon = Lon;
        cat = Cat;
    }

    public int getId() {
        return ID;
    }

    public String getCat() {
        return cat;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
