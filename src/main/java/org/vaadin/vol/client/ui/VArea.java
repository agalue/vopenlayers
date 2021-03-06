package org.vaadin.vol.client.ui;

import org.vaadin.vol.client.wrappers.Projection;
import org.vaadin.vol.client.wrappers.Vector;
import org.vaadin.vol.client.wrappers.geometry.LinearRing;
import org.vaadin.vol.client.wrappers.geometry.Point;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VArea extends VAbstractVector {

    @SuppressWarnings("unchecked")
    @Override
    protected void createOrUpdateVector(UIDL childUIDL, ApplicationConnection client) {
        Projection mapProjection = getMap().getProjection();
        String[] stringArrayAttribute = childUIDL
                .getStringArrayAttribute("points");
        JsArray<Point> points = (JsArray<Point>) JsArray.createArray();
        for (int i = 0; i < stringArrayAttribute.length; i++) {
            Point p = Point.create(stringArrayAttribute[i]);
            p.transform(getProjection(), mapProjection);
            points.push(p);
        }

        LinearRing lr = LinearRing.create(points);

        JavaScriptObject style = null;
        ValueMap attributes = getAttributes();
        if (vector == null) {
            vector = Vector.create(lr, attributes, style);
        } else {
            vector.setGeometry(lr);
            vector.setStyle(style);
            vector.setAttributes(attributes);
        }
    }

}
