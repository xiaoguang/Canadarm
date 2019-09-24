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
import utils.GlobalCfg;

public class Visualizer {

	private Container container;
	private VisualizationPanel vp;

	private JPanel animationControls;

	private JMenuBar menuBar;
	private JMenuItem loadProblemItem;
	private JMenuItem loadSolutionItem;
	private JMenuItem exitItem;
	private JMenuItem initializeItem;
	private JMenuItem playPauseItem;
	private JMenuItem stopItem;
	private JMenuItem problemItem;
	private JMenuItem solutionItem;

	private JMenu fileMenu;
	private JMenu displayMenu;
	private JMenu animationMenu;

	private JSlider manualSlider;
	private JSlider framerateSlider;

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}
		return new ImageIcon(path, description);
	}

	protected Image createImage(ImageIcon icon) {
		return icon.getImage();
	}

	private JButton playPauseButton, stopButton;
	private ImageIcon playIcon = createImageIcon("assets/play.gif", "Play");
	private ImageIcon pauseIcon = createImageIcon("assets/pause.gif", "Pause");
	private ImageIcon stopIcon = createImageIcon("assets/stop.gif", "Stop");
	private Image bgImage = createImage(
			createImageIcon("assets/background_1.png", "BackGround"));

	private boolean animating;
	private boolean wasPlaying;
	private boolean playing;
	private boolean hasProblem;
	private boolean hasSolution;

	private File defaultPath;

	@SuppressWarnings("unused")
	private Visualizer() {
	}

	public Visualizer(Container container, File defaultPath) {
		this.container = container;
		this.defaultPath = defaultPath;
		createComponents();
	}

	public Visualizer(Container container) {
		this.container = container;
		try {
			this.defaultPath = new File(".").getCanonicalFile();
		} catch (IOException e) {
			this.defaultPath = null;
		}
		createComponents();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Canadarm Visualizer");
		Visualizer vis = new Visualizer(frame);
		if (args.length > 0) {
			vis.loadProblem(new File(args[0]));
			if (vis.hasProblem() && args.length >= 2) {
				vis.loadSolution(new File(args[1]));
			}
		}
		frame.setSize(GlobalCfg.frameSizeX, GlobalCfg.frameSizeY);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("Problem")) {
				setAnimating(false);
				vp.setDisplayingSolution(false);
				vp.repaint();
			} else if (cmd.equals("Solution")) {
				setAnimating(false);
				vp.setDisplayingSolution(true);
				vp.repaint();
			} else if (cmd.equals("Load problem")) {
				setAnimating(false);
				loadProblem();
			} else if (cmd.equals("Load solution")) {
				setAnimating(false);
				loadSolution();
			} else if (cmd.equals("Exit")) {
				container.setVisible(false);
				System.exit(0);
			} else if (cmd.equals("Initialize")) {
				setAnimating(true);
			} else if (cmd.equals("Play")) {
				playPause();
			} else if (cmd.equals("Pause")) {
				playPause();
			} else if (cmd.equals("Stop")) {
				setAnimating(false);
			}
		}
	}

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
	private MenuListener menuListener = new MenuListener();

	private ChangeListener manualSliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (!manualSlider.getValueIsAdjusting() && wasPlaying) {
				wasPlaying = false;
				if (manualSlider.getValue() < manualSlider.getMaximum()) {
					vp.playPauseAnimation();
				}
			}
			vp.gotoFrame(manualSlider.getValue());
		}
	};

	private MouseListener manualSliderClickListener = new MouseListener() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (playing) {
				wasPlaying = true;
				vp.playPauseAnimation();
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

	private ChangeListener framerateListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			vp.setFramerate(framerateSlider.getValue());
		}
	};

	private ActionListener playPauseListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			playPause();
		}
	};

	private ActionListener stopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setAnimating(false);
		}
	};

	private void createComponents() {
		this.wasPlaying = false;
		vp = new VisualizationPanel(this, this.bgImage);
		vp.setSize(new Dimension(GlobalCfg.displayPanelSize,
				GlobalCfg.displayPanelSize));
		JPanel wp = new JPanel(new BorderLayout());
		wp.add(vp, BorderLayout.CENTER);
		wp.setSize(GlobalCfg.displayPanelSize, GlobalCfg.displayPanelSize);
		container.setLayout(new BorderLayout());
		wp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.BLACK)));
		container.add(wp, BorderLayout.CENTER);
		createMenus();
		createAnimationControls();
	}

	private void createMenus() {
		menuBar = new JMenuBar();
		createFileMenu();
		createDisplayMenu();
		createAnimationMenu();
		if (container instanceof JFrame) {
			((JFrame) container).setJMenuBar(menuBar);
		}
	}

	private void createFileMenu() {
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext()
				.setAccessibleDescription("Load configs or close the app.");
		menuBar.add(fileMenu);

		loadProblemItem = new JMenuItem("Load problem");
		loadProblemItem.setMnemonic(KeyEvent.VK_P);
		loadProblemItem.addActionListener(menuListener);
		fileMenu.add(loadProblemItem);

		loadSolutionItem = new JMenuItem("Load solution");
		loadSolutionItem.setMnemonic(KeyEvent.VK_S);
		loadSolutionItem.addActionListener(menuListener);
		loadSolutionItem.setEnabled(false);
		fileMenu.add(loadSolutionItem);

		fileMenu.addSeparator();
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(menuListener);
		fileMenu.add(exitItem);
	}

	private void createDisplayMenu() {
		displayMenu = new JMenu("Display");
		displayMenu.setMnemonic(KeyEvent.VK_D);
		fileMenu.getAccessibleContext()
				.setAccessibleDescription("Display the problem and solution.");
		menuBar.add(displayMenu);

		problemItem = new JMenuItem("Problem");
		problemItem.setMnemonic(KeyEvent.VK_P);
		problemItem.addActionListener(menuListener);
		problemItem.setEnabled(false);
		displayMenu.add(problemItem);

		solutionItem = new JMenuItem("Solution");
		solutionItem.setMnemonic(KeyEvent.VK_S);
		solutionItem.addActionListener(menuListener);
		solutionItem.setEnabled(false);
		displayMenu.add(solutionItem);
	}

	private void createAnimationMenu() {
		animationMenu = new JMenu("Animation");
		animationMenu.setMnemonic(KeyEvent.VK_A);
		fileMenu.getAccessibleContext()
				.setAccessibleDescription("Manage the animation.");
		menuBar.add(animationMenu);
		animationMenu.setEnabled(false);

		initializeItem = new JMenuItem("Initialize");
		initializeItem.setMnemonic(KeyEvent.VK_I);
		initializeItem.addActionListener(menuListener);
		animationMenu.add(initializeItem);

		playPauseItem = new JMenuItem("Play");
		playPauseItem.setMnemonic(KeyEvent.VK_P);
		playPauseItem.addActionListener(menuListener);
		animationMenu.add(playPauseItem);

		stopItem = new JMenuItem("Stop");
		stopItem.setMnemonic(KeyEvent.VK_T);
		stopItem.addActionListener(menuListener);
		stopItem.setEnabled(false);
		animationMenu.add(stopItem);
	}

	private void createAnimationControls() {
		Font sliderFont = new Font("Arial", Font.PLAIN, 7);
		Font lableFont = new Font("Arial", Font.BOLD, 11);

		animationControls = new JPanel();
		animationControls.setLayout(
				new BoxLayout(animationControls, BoxLayout.PAGE_AXIS));
		animationControls.setSize(new Dimension(GlobalCfg.controlPanelSizeX,
				GlobalCfg.controlPanelSizeY));
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

		framerateSlider = new JSlider(JSlider.HORIZONTAL,
				GlobalCfg.frameRateMin, GlobalCfg.frameRateMax,
				GlobalCfg.frameRateInit);
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
			vp.getProblemAndSolution().readProblemFromInput(f.getPath());
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
			vp.getProblemAndSolution().readSolutionFromInput(f.getPath());
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
		vp.playPauseAnimation();
	}

	private void setHasProblem(boolean hasProblem) {
		this.hasProblem = hasProblem;
		loadSolutionItem.setEnabled(hasProblem);
		problemItem.setEnabled(hasProblem);
		setHasSolution(false);
		vp.repaint();
	}

	public boolean hasProblem() {
		return hasProblem;
	}

	private void setHasSolution(boolean hasSolution) {
		this.hasSolution = hasSolution;
		solutionItem.setEnabled(hasSolution);
		animationMenu.setEnabled(hasSolution);
		vp.setDisplayingSolution(hasSolution);
		setAnimating(hasSolution);
		vp.repaint();
	}

	public boolean hasSolution() {
		return hasSolution;
	}

	private void setAnimating(boolean animating) {
		if (animating) {
			vp.initAnimation();
		} else {
			vp.stopAnimation();
		}
		if (this.animating == animating) {
			return;
		}
		this.animating = animating;
		stopItem.setEnabled(animating);
		container.validate();
		vp.repaint();
	}

	public void setPlaying(boolean playing) {
		if (this.playing == playing) {
			return;
		}
		this.playing = playing;
		if (playing) {
			playPauseItem.setText("Pause");
			playPauseButton.setIcon(pauseIcon);
		} else {
			playPauseItem.setText("Play");
			playPauseButton.setIcon(playIcon);
		}
		playPauseButton.repaint();
	}

	public void updateMaximum() {
		int maximum = vp.probNSolt.robotStates.size() - 1;
		manualSlider.setMaximum(maximum);
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

}
