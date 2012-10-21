package org.vaadin.vol.demo;

import org.vaadin.vol.AbstractAutoPopulatedVectorLayer.BeforeFeatureSelectedEvent;
import org.vaadin.vol.AbstractAutoPopulatedVectorLayer.BeforeFeatureSelectedListener;
import org.vaadin.vol.OpenLayersMap;
import org.vaadin.vol.OpenStreetMapLayer;
import org.vaadin.vol.Style;
import org.vaadin.vol.StyleMap;
import org.vaadin.vol.WebFeatureServiceLayer;

import com.vaadin.ui.Component;

/**
 * Loads different feature types from a wfs use beforefeature select event
 * to show messages.
 * TODO - only the last added Listener catch the mouse clicks, seems to be a bug
 * in mouse click handling
 */
public class WebFeatureServiceLayerTest2 extends AbstractVOLTest {

    @Override
    public String getDescription() {    	
        return "Just another WFS example. Shows reclickable feature";
    }

    private WebFeatureServiceLayer createWfsLayer(String displayName,
            String proxyUrl, String featureType) {
        WebFeatureServiceLayer wfsLayer = new WebFeatureServiceLayer();
        wfsLayer.setDisplayName(displayName);
        wfsLayer.setUri(proxyUrl);
        wfsLayer.setFeatureType(featureType);
        wfsLayer.setFeatureNS("http://www.openplans.org/topp");
        wfsLayer.setProjection("EPSG:4326");
        return wfsLayer;
    }

    private void setStyle(WebFeatureServiceLayer wfs, double opacity,
            String fillColor, String strokeColor, double pointRadius,
            double strokeWidth) {
        Style style = new Style();
        style.extendCoreStyle("default");
        style.setFillColor(fillColor);
        style.setStrokeColor(strokeColor);
        style.setStrokeWidth(strokeWidth);
        style.setPointRadius(pointRadius);
        style.setFillOpacity(opacity);
        StyleMap styleMap = new StyleMap(style);
        styleMap.setExtendDefault(true);
        wfs.setStyleMap(styleMap);

    }

    @Override
    Component getMap() {
        OpenLayersMap openLayersMap = new OpenLayersMap();
        OpenStreetMapLayer osmLayer = new OpenStreetMapLayer();
        osmLayer.setUrl("http://b.tile.openstreetmap.org/${z}/${x}/${y}.png");
        osmLayer.setDisplayName("OSM");

        String proxyUrl = getApplication().getURL()
                + "../WFSPROXY/demo.opengeo.org/geoserver/wfs";

        WebFeatureServiceLayer wfsCities = createWfsLayer("Cities", proxyUrl,
                "tasmania_cities");
        setStyle(wfsCities, 1, "yellow", "red", 4, 2);
        wfsCities.addListener(new BeforeFeatureSelectedListener() {
            public boolean beforeFeatureSelected(BeforeFeatureSelectedEvent event) {
                showNotification("I'm a city");
                return false;
            }
        });

        final WebFeatureServiceLayer wfsRoads = createWfsLayer("Roads", proxyUrl,
                "tasmania_roads");
        setStyle(wfsRoads, 1, "gray", "gray", 0, 4);
        // don't use beforeselected and selected listener at the same time to show massages
        wfsRoads.addListener(new BeforeFeatureSelectedListener() {
            public boolean beforeFeatureSelected(BeforeFeatureSelectedEvent event) {
                Object typeName = event.getAttributes().get("TYPE");
                showNotification("Before feature Selected: Road type: " + typeName);
                return false;
            }
        });
        WebFeatureServiceLayer wfsBoundaries = createWfsLayer("Boundaries",
                proxyUrl, "tasmania_state_boundaries");
        wfsBoundaries.setEnabled(false);
        wfsBoundaries.addListener(new BeforeFeatureSelectedListener() {
            public boolean beforeFeatureSelected(BeforeFeatureSelectedEvent event) {
                showNotification("No idea what I am :'-(");
                return false;
            }
        });
        
        WebFeatureServiceLayer wfsWater = createWfsLayer("Water", proxyUrl,
                "tasmania_water_bodies");
        setStyle(wfsWater, 0.5, "blue", "blue", 1, 2);
        wfsWater.addListener(new BeforeFeatureSelectedListener() {
            public boolean beforeFeatureSelected(BeforeFeatureSelectedEvent event) {
                showNotification("I am water :-D");
                return false;
            }
        });

        openLayersMap.addLayer(osmLayer);
        openLayersMap.addLayer(wfsCities);
        openLayersMap.addLayer(wfsRoads);
        openLayersMap.addLayer(wfsWater);
        openLayersMap.addLayer(wfsBoundaries);
        openLayersMap.setSizeFull();

        openLayersMap.setCenter(146.9417, -42.0429);
        openLayersMap.setZoom(7);

        return openLayersMap;
    }

}
