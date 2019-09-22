package solve;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import comp.Board;
import comp.RobotState;
import utils.CommandType;
import utils.GlobalCfg;

public class Visualizer {

	private JPanel infoPanel;
	private JPanel animationControls;
	private JLabel infoLabel;
	private JMenuBar menuBar;
	private VisualizationPanel panel;
	private Container container;
	private MenuListener menuListener;
	private JButton playPauseButton;
	private JButton stopButton;
	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon stopIcon;
	private boolean hasProblem;
	private boolean hasSolution;
	private boolean playing;
	private boolean wasPlaying;
	private boolean animating;
	private JMenuItem loadProblemItem;
	private JMenuItem loadSolutionItem;
	private JMenuItem exitItem;
	private JMenuItem problemItem;
	private JMenuItem solutionItem;
	private JMenuItem initializeItem;
	private JMenuItem playPauseItem;
	private JMenuItem stopItem;
	private JMenu animationMenu;
	private JMenu fileMenu;
	private JMenu displayMenu;
	private JSpinner samplingSpinner;
	private JSlider manualSlider;
	private JSlider frameRateSlider;

	public Visualizer(Container c) {
		this.container = c;
		this.menuListener = new MenuListener();
		this.playIcon = createImageIcon("assets/play.gif", "Play");
		this.pauseIcon = createImageIcon("assets/pause.gif", "Pause");
		this.stopIcon = createImageIcon("assets/stop.gif", "Stop");
		this.hasProblem = false;
		this.playing = false;
		this.wasPlaying = false;
		this.createComponents();
	}

	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			return new ImageIcon(path, description);
		}
	}

	private Board loadProblem(String fileName) {
		Board board = null;
		try {
			board = Board.readBoardFromInput(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return board;
	}

	private void loadSolution(String fileName) {
		try {
			RobotState.readRobotStateFromInput(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setInfoText() {
		if (!hasProblem) {
			infoLabel.setText("No problem to display.");
			samplingSpinner.setVisible(false);
		} else if (animating) {
			infoLabel.setText(
					"Play the animation, or use the slider to control it manually.");
			samplingSpinner.setVisible(false);
		} else if (this.panel.isDisplayingSolution()) {
			infoLabel.setText("Displaying the solution; sampling period:");
			samplingSpinner.setVisible(true);
		} else {
			infoLabel.setText(
					"Problem: black = robot, blue = moving boxes, orange = moving obstacles, "
							+ "red = static obstacles, green = goals.");
			samplingSpinner.setVisible(false);
		}
	}

	private void playPause() {
		if (!animating) {
			setAnimating(true);
		}
		this.panel.playPauseAnimation();
	}

	private void setAnimating(boolean animating) {
		if (animating) {
			this.panel.initAnimation();
		} else {
			this.panel.stopAnimation();
		}
		if (this.animating == animating) {
			return;
		}
		this.animating = animating;
		this.stopItem.setEnabled(animating);
		this.animationControls.setVisible(animating);
		this.container.validate();
		this.panel.calculateTransform();
		this.panel.repaint();
		setInfoText();
	}

	private void setHasProblem(boolean hasProblem) {
		this.hasProblem = hasProblem;
		this.loadSolutionItem.setEnabled(hasProblem);
		this.problemItem.setEnabled(hasProblem);
		this.setHasSolution(false);
		this.panel.repaint();
	}

	private void setHasSolution(boolean hasSolution) {
		this.hasSolution = hasSolution;
		this.solutionItem.setEnabled(hasSolution);
		this.animationMenu.setEnabled(hasSolution);
		this.panel.repaint();
	}

	private void createComponents() {
		this.panel = new VisualizationPanel(this);
		JPanel wp = new JPanel(new BorderLayout());
		wp.add(this.panel, BorderLayout.CENTER);
		this.container.setLayout(new BorderLayout());
		wp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 10, 10, 10),
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
		this.container.add(wp, BorderLayout.CENTER);

		this.infoPanel = new JPanel();
		this.infoPanel.setLayout(new FlowLayout());
		this.infoLabel = new JLabel("No problem to display.");
		this.samplingSpinner = new JSpinner(new SpinnerNumberModel(
				GlobalCfg.samplingPeriodInit, 1, null, 1));
		this.samplingSpinner.addChangeListener(this.samplingListener);
		this.samplingSpinner.setPreferredSize(new Dimension(50, 20));
		this.samplingSpinner.setVisible(false);
		this.panel.setSamplingPeriod(GlobalCfg.samplingPeriodInit);
		this.infoPanel.add(this.infoLabel);
		this.infoPanel.add(this.samplingSpinner);
		this.container.add(this.infoPanel, BorderLayout.NORTH);

		this.createMenus();
		createAnimationControls();
	}

	private void createMenus() {
		this.menuBar = new JMenuBar();
		this.createFileMenu();
		this.createDisplayMenu();
		this.createAnimationMenu();
		if (container instanceof JFrame) {
			((JFrame) container).setJMenuBar(menuBar);
		} else if (container instanceof JApplet) {
			((JApplet) container).setJMenuBar(menuBar);
		}
	}

	private void createFileMenu() {
		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic(KeyEvent.VK_F);
		this.fileMenu.getAccessibleContext()
				.setAccessibleDescription("Load configs or close the app.");
		this.menuBar.add(fileMenu);

		this.loadProblemItem = new JMenuItem("Load problem");
		this.loadProblemItem.setMnemonic(KeyEvent.VK_P);
		this.loadProblemItem.addActionListener(this.menuListener);
		this.fileMenu.add(this.loadProblemItem);

		this.loadSolutionItem = new JMenuItem("Load solution");
		this.loadSolutionItem.setMnemonic(KeyEvent.VK_S);
		this.loadSolutionItem.addActionListener(this.menuListener);
		this.loadSolutionItem.setEnabled(false);
		this.fileMenu.add(this.loadSolutionItem);

		this.fileMenu.addSeparator();
		this.exitItem = new JMenuItem("Exit");
		this.exitItem.setMnemonic(KeyEvent.VK_X);
		this.exitItem.addActionListener(this.menuListener);
		this.fileMenu.add(this.exitItem);
	}

	private void createDisplayMenu() {
		this.displayMenu = new JMenu("Display");
		this.displayMenu.setMnemonic(KeyEvent.VK_D);
		this.fileMenu.getAccessibleContext()
				.setAccessibleDescription("Display the problem and solution.");
		this.menuBar.add(this.displayMenu);

		this.problemItem = new JMenuItem("Problem");
		this.problemItem.setMnemonic(KeyEvent.VK_P);
		this.problemItem.addActionListener(this.menuListener);
		this.problemItem.setEnabled(false);
		this.displayMenu.add(this.problemItem);

		this.solutionItem = new JMenuItem("Solution");
		this.solutionItem.setMnemonic(KeyEvent.VK_S);
		this.solutionItem.addActionListener(this.menuListener);
		this.solutionItem.setEnabled(false);
		this.displayMenu.add(this.solutionItem);
	}

	private void createAnimationMenu() {
		this.animationMenu = new JMenu("Animation");
		this.animationMenu.setMnemonic(KeyEvent.VK_A);
		this.fileMenu.getAccessibleContext()
				.setAccessibleDescription("Manage the animation.");
		this.menuBar.add(this.animationMenu);
		this.animationMenu.setEnabled(false);

		this.initializeItem = new JMenuItem("Initialize");
		this.initializeItem.setMnemonic(KeyEvent.VK_I);
		this.initializeItem.addActionListener(this.menuListener);
		this.animationMenu.add(this.initializeItem);

		this.playPauseItem = new JMenuItem("Play");
		this.playPauseItem.setMnemonic(KeyEvent.VK_P);
		this.playPauseItem.addActionListener(this.menuListener);
		this.animationMenu.add(this.playPauseItem);

		this.stopItem = new JMenuItem("Stop");
		this.stopItem.setMnemonic(KeyEvent.VK_T);
		this.stopItem.addActionListener(this.menuListener);
		this.stopItem.setEnabled(false);
		this.animationMenu.add(this.stopItem);
	}

	private void createAnimationControls() {
		Font sliderFont = new Font("Arial", Font.PLAIN, 12);

		this.animationControls = new JPanel();
		this.animationControls.setLayout(
				new BoxLayout(this.animationControls, BoxLayout.PAGE_AXIS));

		JLabel manualLabel = new JLabel("Frame #");
		manualLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.manualSlider = new JSlider(JSlider.HORIZONTAL);
		this.manualSlider.setPaintTicks(true);
		this.manualSlider.setPaintLabels(true);
		this.manualSlider.setFont(sliderFont);
		this.manualSlider.addChangeListener(this.manualSliderListener);
		this.manualSlider.addMouseListener(this.manualSliderClickListener);
		this.manualSlider.setMinorTickSpacing(1);
		this.manualSlider.addComponentListener(this.resizeListener);

		JLabel frameRateLabel = new JLabel("FrameRate");
		frameRateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.frameRateSlider = new JSlider(JSlider.HORIZONTAL,
				GlobalCfg.frameRateInit, GlobalCfg.frameRateMax,
				GlobalCfg.frameRateInit);
		this.frameRateSlider.setMajorTickSpacing(10);
		this.frameRateSlider.setMinorTickSpacing(1);
		this.frameRateSlider.setPaintTicks(true);
		this.frameRateSlider.setPaintLabels(true);
		this.frameRateSlider.setLabelTable(
				this.frameRateSlider.createStandardLabels(10, 10));
		this.frameRateSlider.setFont(sliderFont);
		this.frameRateSlider.addChangeListener(this.frameRateListener);
		JPanel frameratePanel = new JPanel();
		frameratePanel
				.setLayout(new BoxLayout(frameratePanel, BoxLayout.PAGE_AXIS));
		frameratePanel.add(frameRateLabel);
		frameratePanel.add(Box.createRigidArea(new Dimension(0, 2)));
		frameratePanel.add(this.frameRateSlider);

		this.playPauseButton = new JButton(this.playIcon);
		this.playPauseButton.addActionListener(this.playPauseListener);
		this.stopButton = new JButton(this.stopIcon);
		this.stopButton.addActionListener(this.stopListener);

		this.animationControls.add(new JSeparator(JSeparator.HORIZONTAL));
		this.animationControls.add(Box.createRigidArea(new Dimension(0, 2)));
		this.animationControls.add(manualLabel);
		this.animationControls.add(Box.createRigidArea(new Dimension(0, 2)));
		this.animationControls.add(this.manualSlider);
		this.animationControls.add(Box.createRigidArea(new Dimension(0, 5)));
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
		p2.add(this.playPauseButton);
		p2.add(Box.createRigidArea(new Dimension(10, 0)));
		p2.add(this.stopButton);
		p2.add(frameratePanel);
		this.animationControls.add(p2);
		this.animationControls.setVisible(false);
		this.animationControls
				.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
		this.container.add(this.animationControls, BorderLayout.SOUTH);
	}

	public void updateSliderSpacing(JSlider slider) {
		int width = slider.getBounds().width;
		int max = slider.getMaximum();
		int spacing = 1;
		int mode = 1;
		double pxPerLabel = (double) width * spacing / max;
		if (pxPerLabel <= 0) {
			return;
		}
		while (pxPerLabel <= 30) {
			if (mode == 1) {
				spacing *= 2;
				pxPerLabel *= 2;
				mode = 2;
			} else if (mode == 2) {
				spacing = spacing * 5 / 2;
				pxPerLabel *= 2.5;
				mode = 5;
			} else {
				spacing *= 2;
				pxPerLabel *= 2;
				mode = 1;
			}
		}

		slider.setMajorTickSpacing(spacing);
		int min = slider.getMinimum();
		if (min % spacing > 0) {
			min += (spacing - (min % spacing));
		}
		slider.setLabelTable(slider.createStandardLabels(spacing, min));
	}

	public void updateTickSpacing() {
		updateSliderSpacing(this.manualSlider);
		updateSliderSpacing(this.frameRateSlider);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Assignment 1 visualiser");
		Visualizer visualizer = new Visualizer(frame);

		if (args.length > 0) {
			visualizer.loadProblem(args[0]);
			if (visualizer.hasProblem && args.length >= 2) {
				visualizer.loadSolution(args[1]);
			}
		}

		frame.setSize(700, 766);
		frame.setLocation(300, 20);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
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

	private MouseListener mouseMovementListener = new MouseListener() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (playing) {
				wasPlaying = true;
				panel.playPauseAnimation();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	};

	private ChangeListener samplingListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			panel.setSamplingPeriod((Integer) samplingSpinner.getValue());
		}
	};

	private ChangeListener manualSliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (!manualSlider.getValueIsAdjusting() && wasPlaying) {
				wasPlaying = false;
				if (manualSlider.getValue() < manualSlider.getMaximum()) {
					panel.playPauseAnimation();
				}
			}
			panel.gotoFrame(manualSlider.getValue());
		}
	};

	private MouseListener manualSliderClickListener = new MouseListener() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (playing) {
				wasPlaying = true;
				panel.playPauseAnimation();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	};

	private class ResizeListener implements ComponentListener {
		@Override
		public void componentResized(ComponentEvent e) {
			updateTickSpacing();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}
	}

	private ResizeListener resizeListener = new ResizeListener();

	private ChangeListener frameRateListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			panel.setFramerate(frameRateSlider.getValue());
		}
	};

	private ActionListener playPauseListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			playPause();
		}
	};

	private ActionListener stopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setAnimating(false);
		}
	};

}
