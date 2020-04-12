package com.example.placeofinterest;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//class DataParserog {
//
//    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
//    {
//        HashMap<String, String> googlePlaceMap = new HashMap<>();
//        String placeName = "";
//        String vicinity= "";
//        String latitude= "";
//        String longitude="";
//        String reference="";
//        String rating="";
//        String user_ratings_total="";
//
//        Log.d("DataParser","jsonobject ="+googlePlaceJson.toString());
//
//
//        try {
//            if (!googlePlaceJson.isNull("name")) {
//                placeName = googlePlaceJson.getString("name");
//                rating = googlePlaceJson.getString("rating");
//                user_ratings_total = googlePlaceJson.getString("user_ratings_total");
//            }
//            if (!googlePlaceJson.isNull("vicinity")) {
//                vicinity = googlePlaceJson.getString("vicinity");
//            }
//
//            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
//            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
//
//            reference = googlePlaceJson.getString("reference");
//
//
//
//            googlePlaceMap.put("place_name", placeName);
//            googlePlaceMap.put("vicinity", vicinity);
//            googlePlaceMap.put("lat", latitude);
//            googlePlaceMap.put("lng", longitude);
//            googlePlaceMap.put("reference", reference);
//            googlePlaceMap.put("rating", rating);
//            googlePlaceMap.put("user_ratings_total", user_ratings_total);
//
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return googlePlaceMap;
//
//    }
//    private List<HashMap<String, String>>getPlaces(JSONArray jsonArray)
//    {
//        int count = jsonArray.length();
//        List<HashMap<String, String>> placelist = new ArrayList<>();
//        HashMap<String, String> placeMap = null;
//
//        for(int i = 0; i<count;i++)
//        {
//            try {
//                placeMap = getPlace((JSONObject) jsonArray.get(i));
//                placelist.add(placeMap);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return placelist;
//    }
//
//    List<HashMap<String, String>> parse(String jsonData)
//    {
//        JSONArray jsonArray = null;
//        JSONObject jsonObject;
//
//        Log.d("json data", jsonData);
//
//        try {
//            jsonObject = new JSONObject(jsonData);
//            jsonArray = jsonObject.getJSONArray("results");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return getPlaces(jsonArray);
//    }
//}

class DataParserog {
    List<List<HashMap<String,String>>> parse(JSONObject jObject){
        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");
/** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
/** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
/** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
/** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude) );
                            hm.put("lng", Double.toString((list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }
    /**
     * Method to decode polyline points
     * */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}