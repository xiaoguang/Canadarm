package solve;

import java.awt.Color;

import javax.swing.JComponent;

public class VisualizationPanel extends JComponent {

	private static final long serialVersionUID = -609530771419701598L;
	private Visualizer visualizer;

	public VisualizationPanel(Visualizer v) {
		super();
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		this.visualizer = v;
	}

}
