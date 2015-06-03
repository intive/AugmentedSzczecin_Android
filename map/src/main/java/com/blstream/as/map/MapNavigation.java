package com.blstream.as.map;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapNavigation extends AsyncTask<LatLng, Document, Document> {

    private static final String BASIC_URL = "http://maps.googleapis.com/maps/api/directions/xml?";
    private static final String NAVIGATION_SETTINGS_URL = "&sensor=false&units=metric&mode=driving";
    private static final int TIMEOUT = 15;
    private static final String START_LOCATION_NAME = "start_location";
    private static final String POLYLINE_NAME = "polyline";
    private static final String END_LOCATION_NAME = "end_location";
    private static final String LATITUDE_NAME = "lat";
    private static final String LONGITUDE_NAME = "lng";
    private static final String POINTS_NAME = "points";

    private String fullUrl;

    private Document document;

    private LatLng startPosition;
    private LatLng endPosition;

    public interface MapNavigationCallbacks {
        void onRouteGenerated(Document document);
    }

    MapNavigationCallbacks fragmentConnector;

    public MapNavigation(Fragment fragment) {
        if (fragment instanceof MapNavigationCallbacks) {
            fragmentConnector = (MapNavigationCallbacks) fragment;
        } else {
            throw new ClassCastException(fragment.toString()
                    + " must implement MapNavigation.MapsFragmentCallbacks");
        }
    }

    public Document getDocument(LatLng startPosition, LatLng endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        setFullUrl();
        setDocument();
        return document;
    }

    private void setFullUrl() {
        if (startPosition != null && endPosition != null) {
            fullUrl = BASIC_URL;
            fullUrl += "origin=" + startPosition.latitude;
            fullUrl += "," + startPosition.longitude;
            fullUrl += "&destination=" + endPosition.latitude;
            fullUrl += "," + endPosition.longitude;
            fullUrl += NAVIGATION_SETTINGS_URL;
        }
    }

    private void setDocument() {
        try {
            connectToDocument();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void connectToDocument() throws IOException, ParserConfigurationException, SAXException {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        httpClient.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        Request request = new Request.Builder()
                    .url(fullUrl)
                    .build();
        Response response = httpClient.newCall(request).execute();

        InputStream inputStream = response.body().byteStream();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = builder.parse(inputStream);
    }

    public ArrayList<LatLng> getDirection(Document document) {
        ArrayList<LatLng> geoPointsList = new ArrayList<>();
        NodeList nodeList = document.getElementsByTagName("step");
        NodeList nodeListChildren;
        NodeList nodeListAtLocation;
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node locationNode;
                Node latitudeNode;
                Node longitudeNode;
                nodeListChildren = node.getChildNodes();

                int nodeIndex = getNodeIndex(nodeListChildren, START_LOCATION_NAME);
                if (nodeIndex != -1 && nodeListChildren != null) {
                    locationNode = nodeListChildren.item(nodeIndex);
                    nodeListAtLocation = locationNode.getChildNodes();

                    latitudeNode = nodeListAtLocation.item(getNodeIndex(nodeListAtLocation, LATITUDE_NAME));
                    double latitude = getValueFromNode(latitudeNode);

                    longitudeNode = nodeListAtLocation.item(getNodeIndex(nodeListAtLocation, LONGITUDE_NAME));
                    double longitude = getValueFromNode(longitudeNode);
                    geoPointsList.add(new LatLng(latitude, longitude));
                }

                nodeIndex = getNodeIndex(nodeListChildren, POLYLINE_NAME);
                if (nodeIndex != -1 && nodeListChildren != null) {
                    locationNode = nodeListChildren.item(nodeIndex);
                    nodeListAtLocation = locationNode.getChildNodes();

                    latitudeNode = nodeListAtLocation.item(getNodeIndex(nodeListAtLocation, POINTS_NAME));
                    List<LatLng> pointsList = PolyUtil.decode(latitudeNode.getTextContent());
                    for (int j = 0; j < pointsList.size(); j++) {
                        geoPointsList.add(new LatLng(pointsList.get(j).latitude, pointsList.get(j).longitude));
                    }
                }

                nodeIndex = getNodeIndex(nodeListChildren, END_LOCATION_NAME);
                if (nodeIndex != -1 && nodeListChildren != null) {
                    locationNode = nodeListChildren.item(nodeIndex);
                    nodeListAtLocation = locationNode.getChildNodes();

                    latitudeNode = nodeListAtLocation.item(getNodeIndex(nodeListAtLocation, LATITUDE_NAME));
                    double latitude = getValueFromNode(latitudeNode);

                    longitudeNode = nodeListAtLocation.item(getNodeIndex(nodeListAtLocation, LONGITUDE_NAME));
                    double longitude = getValueFromNode(longitudeNode);
                    geoPointsList.add(new LatLng(latitude, longitude));
                }
            }
        }

        return geoPointsList;
    }



    private double getValueFromNode(Node node) {
        double value = 0.0;
        if (node != null) {
            value = Double.parseDouble(node.getTextContent());
        }
        return value;
    }

    private int getNodeIndex(NodeList nodeList, String nodeName) {
        if (nodeList == null)
            return -1;

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals(nodeName))
                return i;
        }
        return -1;
    }

    @Override
    protected Document doInBackground(LatLng... params) {
        return getDocument(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(Document document) {
        fragmentConnector.onRouteGenerated(document);
    }
}
