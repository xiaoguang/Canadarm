package solve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import comp.VisualizationPanel;
import utils.GlbCfg;

public class Visualizer {

	private Container container;
	private VisualizationPanel visualPanel;

	private JPanel animationControls;

	private JMenuBar menuBar;
	private JMenuItem loadProblemItem;
	private JMenuItem loadSolutionItem;
	private JMenuItem exitItem;

	private JMenu fileMenu;
	private JSlider manualSlider;
	private JSlider framerateSlider;

	private JButton playPauseButton;
	private JButton stopButton;

	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon stopIcon;
	private Image bgImage;

	private boolean animating;
	private boolean wasPlaying;
	private boolean playing;
	private boolean hasProblem;
	private boolean hasSolution;

	private File defaultPath;
	private ResizeListener resizeListener;
	private MenuListener menuListener;

	@SuppressWarnings("unused")
	private Visualizer() {
	}

	public Visualizer(Container container) {
		this.container = container;
		try {
			this.defaultPath = new File(".").getCanonicalFile();
		} catch (IOException e) {
			this.defaultPath = null;
		}

		this.playIcon = createImageIcon("assets/play.gif", "Play");
		this.pauseIcon = createImageIcon("assets/pause.gif", "Pause");
		this.stopIcon = createImageIcon("assets/stop.gif", "Stop");
		this.bgImage = createImage(
				createImageIcon("assets/background_1.png", "BackGround"));

		this.resizeListener = new ResizeListener();
		this.menuListener = new MenuListener();

		this.animating = false;
		this.wasPlaying = false;
		this.playing = false;
		this.hasProblem = false;
		this.hasSolution = false;

		createComponents();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Canadarm Visualizer");
		Visualizer visualizer = new Visualizer(frame);
		if (args.length > 0) {
			visualizer.loadProblem(new File(args[0]));
			if (visualizer.hasProblem() && args.length >= 2) {
				visualizer.loadSolution(new File(args[1]));
			}
		}
		frame.setSize(GlbCfg.frameSizeX, GlbCfg.frameSizeY);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}
		return new ImageIcon(path, description);
	}

	private Image createImage(ImageIcon icon) {
		return icon.getImage();
	}

	private void createComponents() {
		this.wasPlaying = false;
		this.visualPanel = new VisualizationPanel(this, this.bgImage);
		this.visualPanel.setSize(new Dimension(GlbCfg.displayPanelSize,
				GlbCfg.displayPanelSize));
		JPanel displayPanel = new JPanel(new BorderLayout());
		displayPanel.add(this.visualPanel, BorderLayout.CENTER);
		displayPanel.setSize(GlbCfg.displayPanelSize, GlbCfg.displayPanelSize);
		this.container.setLayout(new BorderLayout());
		displayPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.BLACK)));
		this.container.add(displayPanel, BorderLayout.CENTER);
		createMenu();
		createAnimationControl();
	}

	private void createMenu() {
		this.menuBar = new JMenuBar();

		fileMenu = new JMenu(GlbCfg.file);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext()
				.setAccessibleDescription("Load configs or close the app.");
		menuBar.add(fileMenu);

		loadProblemItem = new JMenuItem(GlbCfg.loadProblem);
		loadProblemItem.setMnemonic(KeyEvent.VK_P);
		loadProblemItem.addActionListener(menuListener);
		fileMenu.add(loadProblemItem);

		loadSolutionItem = new JMenuItem(GlbCfg.loadSolution);
		loadSolutionItem.setMnemonic(KeyEvent.VK_S);
		loadSolutionItem.addActionListener(menuListener);
		loadSolutionItem.setEnabled(false);
		fileMenu.add(loadSolutionItem);

		fileMenu.addSeparator();
		exitItem = new JMenuItem(GlbCfg.exit);
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(menuListener);
		fileMenu.add(exitItem);

		if (this.container instanceof JFrame) {
			((JFrame) container).setJMenuBar(this.menuBar);
		}
	}

	private void createAnimationControl() {
		Font sliderFont = new Font("Arial", Font.PLAIN, 7);
		Font lableFont = new Font("Arial", Font.BOLD, 11);

		animationControls = new JPanel();
		animationControls.setLayout(
				new BoxLayout(animationControls, BoxLayout.PAGE_AXIS));
		animationControls.setSize(new Dimension(GlbCfg.controlPanelSizeX,
				GlbCfg.controlPanelSizeY));
		animationControls.add(new JSeparator(JSeparator.HORIZONTAL));

		JLabel manualLabel = new JLabel("Position");
		manualLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		manualLabel.setFont(lableFont);

		manualSlider = new JSlider(JSlider.HORIZONTAL);
		manualSlider.setPaintTicks(true);
		manualSlider.setPaintLabels(true);
		manualSlider.setFont(sliderFont);
		manualSlider.setLabelTable(manualSlider.createStandardLabels(10, 10));
		manualSlider.addChangeListener(manualSliderListener);
		manualSlider.addMouseListener(manualSliderClickListener);
		manualSlider.setMinorTickSpacing(1);
		manualSlider.addComponentListener(resizeListener);

		JPanel manualPanel = new JPanel();
		manualPanel.setLayout(new BoxLayout(manualPanel, BoxLayout.PAGE_AXIS));
		manualPanel.add(manualLabel);
		manualPanel.add(Box.createRigidArea(new Dimension(0, 2)));
		manualPanel.add(manualSlider);

		JLabel framerateLabel = new JLabel("Rate");
		framerateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		framerateLabel.setFont(lableFont);

		framerateSlider = new JSlider(JSlider.HORIZONTAL, GlbCfg.frameRateMin,
				GlbCfg.frameRateMax, GlbCfg.frameRateInit);
		framerateSlider.setPaintTicks(true);
		framerateSlider.setPaintLabels(true);
		framerateSlider
				.setLabelTable(framerateSlider.createStandardLabels(10, 10));
		framerateSlider.setFont(sliderFont);
		framerateSlider.addChangeListener(framerateListener);

		JPanel frameratePanel = new JPanel();
		frameratePanel
				.setLayout(new BoxLayout(frameratePanel, BoxLayout.PAGE_AXIS));
		frameratePanel.add(framerateLabel);
		frameratePanel.add(Box.createRigidArea(new Dimension(0, 2)));
		frameratePanel.add(framerateSlider);

		playPauseButton = new JButton(playIcon);
		playPauseButton.addActionListener(playPauseListener);
		stopButton = new JButton(stopIcon);
		stopButton.addActionListener(stopListener);

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
		p2.add(playPauseButton);
		p2.add(manualPanel);

		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.LINE_AXIS));
		p3.add(stopButton);
		p3.add(frameratePanel);

		animationControls.add(p2);
		animationControls.add(p3);
		animationControls.setVisible(true);
		animationControls
				.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		container.add(animationControls, BorderLayout.SOUTH);
	}

	private File askForFile() {
		JFileChooser fc = new JFileChooser(defaultPath);
		int returnVal = fc.showOpenDialog(container);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		return fc.getSelectedFile();
	}

	private void showFileError(File f) {
		JOptionPane.showMessageDialog(container, "Error loading " + f.getName(),
				"File I/O Error", JOptionPane.ERROR_MESSAGE);
	}

	private void loadProblem(File f) {
		try {
			this.visualPanel.getProblemAndSolution()
					.readProblemFromInput(f.getPath());
			setHasProblem(true);
		} catch (Exception e1) {
			showFileError(f);
			setHasProblem(false);
		}
	}

	private void loadProblem() {
		File f = askForFile();
		if (f == null) {
			return;
		}
		loadProblem(f);
	}

	private void loadSolution(File f) {
		try {
			this.visualPanel.getProblemAndSolution()
					.readSolutionFromInput(f.getPath());
			setHasSolution(true);
		} catch (Exception e1) {
			showFileError(f);
			setHasSolution(false);
		}
	}

	private void loadSolution() {
		File f = askForFile();
		if (f == null) {
			return;
		}
		loadSolution(f);
	}

	private void playPause() {
		if (!animating) {
			setAnimating(true);
		}
		this.visualPanel.playPauseAnimation();
	}

	private void setAnimating(boolean animating) {
		if (animating) {
			this.visualPanel.initAnimation();
		} else {
			this.visualPanel.stopAnimation();
		}
		if (this.animating == animating) {
			return;
		}
		this.animating = animating;
		this.container.validate();
		this.visualPanel.repaint();
	}

	public void setPlaying(boolean playing) {
		if (this.playing == playing) {
			return;
		}
		this.playing = playing;
		if (playing) {
			playPauseButton.setIcon(pauseIcon);
		} else {
			playPauseButton.setIcon(playIcon);
		}
		playPauseButton.repaint();
	}

	private void setHasProblem(boolean hasProblem) {
		this.hasProblem = hasProblem;
		loadSolutionItem.setEnabled(hasProblem);
		setHasSolution(false);
		this.visualPanel.repaint();
	}

	private void setHasSolution(boolean hasSolution) {
		this.hasSolution = hasSolution;
		setAnimating(hasSolution);
		this.visualPanel.repaint();
	}

	public boolean hasSolution() {
		return hasSolution;
	}

	public boolean hasProblem() {
		return hasProblem;
	}

	public void updateMaximum() {
		int maximum = this.visualPanel.probNSolt.robotStates.size() - 1;
		this.manualSlider.setMaximum(maximum);
		updateTickSpacing();
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
		updateSliderSpacing(manualSlider);
		updateSliderSpacing(framerateSlider);
	}

	public void setFrameNumber(int frameNumber) {
		manualSlider.setValue(frameNumber);
	}

	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			CommandType cmd = CommandType.valueOf(event.getActionCommand()
					.trim().toUpperCase().replaceAll("\\s+", ""));
			switch (cmd) {
			case LOADPROBLEM:
				setAnimating(false);
				loadProblem();
				break;
			case LOADSOLUTION:
				setAnimating(false);
				loadSolution();
				break;
			case EXIT:
				System.exit(0);
				break;
			default:
				System.exit(-1);
			}
		}
	}

	private class ResizeListener implements ComponentListener {
		@Override
		public void componentResized(ComponentEvent event) {
			updateTickSpacing();
		}

		@Override
		public void componentHidden(ComponentEvent event) {
		}

		@Override
		public void componentMoved(ComponentEvent event) {
		}

		@Override
		public void componentShown(ComponentEvent event) {
		}
	}

	private MouseListener manualSliderClickListener = new MouseListener() {
		@Override
		public void mousePressed(MouseEvent event) {
			if (playing) {
				wasPlaying = true;
				visualPanel.playPauseAnimation();
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

	private ChangeListener manualSliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent event) {
			if (!manualSlider.getValueIsAdjusting() && wasPlaying) {
				wasPlaying = false;
				if (manualSlider.getValue() < manualSlider.getMaximum()) {
					visualPanel.playPauseAnimation();
				}
			}
			visualPanel.gotoFrame(manualSlider.getValue());
		}
	};

	private ChangeListener framerateListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent event) {
			visualPanel.setFramerate(framerateSlider.getValue());
		}
	};

	private ActionListener playPauseListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			playPause();
		}
	};

	private ActionListener stopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			setAnimating(false);
		}
	};

	enum CommandType {
		LOADPROBLEM, LOADSOLUTION, EXIT;
	}

}
