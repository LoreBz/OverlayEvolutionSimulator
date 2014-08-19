package model;

import java.io.File;

import org.graphstream.ui.swingViewer.View;

public class Network {
	
	UnderlayGraph underlayGraph;
	OverlayGraph overlayGraph;
	View graphview;
	
	public Network(UnderlayGraph underlayGraph, OverlayGraph overlayGraph,
			View graphview) {
		super();
		this.underlayGraph = underlayGraph;
		this.overlayGraph = overlayGraph;
		this.graphview = graphview;
	}
	
	public Network () {
	}
	
	void init (File f) {
	}
 
	
}
