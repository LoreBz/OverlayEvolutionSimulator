package exercise;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.Network;
import model.OverlayGraph;
import model.UnderlayGraph;

public class Test {

	public Test() {

	}

	public static void main(String[] args) {
		Test test = new Test();
		test.setLookAndFeel();
		File graphFile = test.importGraphFile();

		UnderlayGraph underlayGraph = new UnderlayGraph(graphFile);
		underlayGraph.buildOLSRtables();

		OverlayGraph overlayGraph = new OverlayGraph(null, null);
		overlayGraph.randomInit(underlayGraph);
		
		Network network = new Network(underlayGraph, overlayGraph, null);
		network.updateNetwork();
		
		 System.out.println(network.getUnderlayGraph());
		 System.out.println(network.getOverlayGraph());
		// test.displayall();
	}

	final boolean setLookAndFeel() {
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	File importGraphFile() {
		final JFileChooser fc = new JFileChooser();
		// FileNameExtensionFilter filter = new
		// FileNameExtensionFilter("edges");
		// fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ fc.getSelectedFile().getName());
			return fc.getSelectedFile();
		}
		return null;
	}

	void displayall() {
		// far partire la grafica con bottoni e disegnino del grafo!
	}
}
