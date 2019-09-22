package solve;

import javax.swing.JApplet;

public class VisualizationApplet extends JApplet {

	private static final long serialVersionUID = 9100014557980608785L;

	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					new Visualizer(VisualizationApplet.this);
				}
			});
		} catch (Exception e) {
			System.err.println("Could not create the visualizer.");
		}
		this.setSize(800, 600);
	}
}
