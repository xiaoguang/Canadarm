package solve;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

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

	private JPanel infoPanel;
	private JLabel infoLabel;
	private VisualizationPanel panel;
	private Container container;
	private MenuListener menuListener;
	private JButton playPauseButton;
	private JButton stopButton;
	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon stopIcon;

	public Visualizer(Container c) {
		this.panel = new VisualizationPanel(this);
		this.container = c;
		this.menuListener = new MenuListener();
		this.playIcon = createImageIcon("assets/play.gif", "Play");
		this.pauseIcon = createImageIcon("assets/pause.gif", "Pause");
		this.stopIcon = createImageIcon("assets/stop.gif", "Stop");

		JPanel wp = new JPanel(new BorderLayout());
		wp.add(this.panel, BorderLayout.CENTER);
		container.setLayout(new BorderLayout());
		wp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 10, 10, 10),
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
		container.add(wp, BorderLayout.CENTER);

		infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout());

		infoLabel = new JLabel("No problem to display.");
		infoPanel.add(infoLabel);
		container.add(infoPanel, BorderLayout.NORTH);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Assignment 1 visualiser");
		Visualizer vis = new Visualizer(frame);
		/*-
		if (args.length > 0) {
			vis.loadProblem(new File(args[0]));
			if (vis.hasProblem() && args.length >= 2) {
				vis.loadSolution(new File(args[1]));
			}
		}
		*/
		frame.setSize(700, 766);
		frame.setLocation(300, 20);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}

}
