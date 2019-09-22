package solve;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import utils.CommandType;

public class Visualizer {

	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			return new ImageIcon(path, description);
		}
	}

	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			CommandType cmd = CommandType.valueOf(ae.getActionCommand());

			switch (cmd) {
			case PROBLEM:
				break;
			case SOLUTION:
				break;
			case LOADPROBLEM:
				break;
			case LOADSOLUTION:
				break;
			case INITIALIZE:
				break;
			case PLAY:
				break;
			case PAUSE:
				break;
			case STOP:
				break;
			case EXIT:
				container.setVisible(false);
				System.exit(0);
				break;
			}
		}
	}

	private Container container;
	private MenuListener menuListener;
	private JButton playPauseButton;
	private JButton stopButton;
	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon stopIcon;

	public Visualizer(Container c) {
		this.container = c;
		this.menuListener = new MenuListener();
		this.playIcon = createImageIcon("assets/play.gif", "Play");
		this.pauseIcon = createImageIcon("assets/pause.gif", "Pause");
		this.stopIcon = createImageIcon("assets/stop.gif", "Stop");
	}

}
