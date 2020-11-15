/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.controls;

import fr.fsm.gui.views.TranslatableHomotethicPane;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;

/**
 * A class that control the zoom events for fsm panes
 *
 * @author KNAFF Lucas
 */
public class ZoomManager {

    private final TranslatableHomotethicPane node;

    /**
     *
     * @param node
     */
    @SuppressWarnings("Convert2Lambda")
    public ZoomManager(TranslatableHomotethicPane node) {
        this.node = node;
        node.addEventHandler(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(final ScrollEvent scrollEvent) {
                double zoomFactor = 1.05;

                if (scrollEvent.getDeltaY() < 0) {
                    zoomFactor = 2.0 - zoomFactor;
                }
                node.appendScale(zoomFactor, scrollEvent.getX(), scrollEvent.getY());
                scrollEvent.consume();
            }
        });

    }
}
