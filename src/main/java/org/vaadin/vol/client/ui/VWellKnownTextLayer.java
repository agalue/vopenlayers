package org.vaadin.vol.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.vol.client.wrappers.GwtOlHandler;
import org.vaadin.vol.client.wrappers.Projection;
import org.vaadin.vol.client.wrappers.StyleMap;
import org.vaadin.vol.client.wrappers.Vector;
import org.vaadin.vol.client.wrappers.control.SelectFeature;
import org.vaadin.vol.client.wrappers.format.WKT;
import org.vaadin.vol.client.wrappers.layer.VectorLayer;
import org.vaadin.vol.client.wrappers.layer.WebFeatureServiceLayer;

import com.google.gwt.core.client.JsArray;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VWellKnownTextLayer extends
        VAbstracMapLayer<VectorLayer> {

    private String wkt;
    private String layers;
    private Boolean isBaseLayer;
    private Double opacity;
    private String format;
    private boolean transparent;
    private String display;
    private ApplicationConnection client;
    private VectorLayer layer;
    private SelectFeature control;
    private String paintableId;
    private StyleMap styleMap;
    private String projection;

    @Override
    VectorLayer createLayer() {
        if (layer == null) {
            layer = VectorLayer.create(display, styleMap);
        }
        return layer;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, final ApplicationConnection client) {
        this.client = client;
        this.paintableId = uidl.getId();
        if (!uidl.hasAttribute("cached")) {
            display = uidl.getStringAttribute("display");
            projection = uidl.hasAttribute("projection") ? uidl
                    .getStringAttribute("projection") : "EPSG:4326";
            wkt = uidl.getStringAttribute("wkt");
            styleMap = VVectorLayer.getStyleMap(uidl);
        }
        super.updateFromUIDL(uidl, client);
        
        getLayer().removeAllFeatures();
        
        Projection targetProjection = getMap().getProjection();
        Projection sourceProjection = Projection.get(projection);
        WKT wktFormatter = WKT.create(sourceProjection, targetProjection);
        JsArray<Vector> read = wktFormatter.read(wkt);
        for(int i = 0; i < read.length(); i++) {
            Vector vector = read.get(i);
            getLayer().addFeature(vector);
        }
        
        if(control == null) {
            
            // TODO create select/unselect feature and communicate fid to server
            layer.registerHandler("featureselected", new GwtOlHandler() {
                public void onEvent(JsArray arguments) {
                    ValueMap javaScriptObject = arguments.get(0).cast();
                    Vector vector = javaScriptObject.getValueMap("feature")
                            .cast();
                    String fid = vector.getFeatureId();
                    ValueMap attr = vector.getAttributes();
                    client.updateVariable(paintableId, "fid", fid, false);
                    Map<String, Object> hashMap = new HashMap<String, Object>();
                    for(String key :attr.getKeySet()) {
                        hashMap.put(key, attr.getString(key));
                    }
                    client.updateVariable(paintableId, "attr", hashMap, false);
                    client.sendPendingVariableChanges();
                }
            });
            control = SelectFeature.create(layer);
            getMap().addControl(control );
            control.activate();

        }


    }

    public String getUri() {
        return wkt;
    }

    public String getLayers() {
        return layers;
    }

    public String getDisplay() {
        return display;
    }

    public Boolean isBaseLayer() {
        return isBaseLayer;
    }

    public Double getOpacity() {
        return opacity;
    }

    public String getFormat() {
        return format;
    }

    public boolean isTransparent() {
        return transparent;
    }
}
