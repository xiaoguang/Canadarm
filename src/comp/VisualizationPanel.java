package comp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;

import comp.RobotState.RobotStateOutPut;
import solve.Visualizer;
import utils.GlobalCfg;

public class VisualizationPanel extends JComponent {

	private static final long serialVersionUID = 4825793222637922728L;

	public ProblemAndSolution probNSolt = new ProblemAndSolution();

	private Image bgImage;
	private Visualizer visualizer;
	private AffineTransform translation = AffineTransform
			.getTranslateInstance(0, -1);
	private AffineTransform transform = null;

	// State Information
	private RobotStateOutPut currentRobotState;
	private List<BoundingBox> obstacles;
	private RobotState initRobotState;
	private RobotState goalRobotState;
	private List<Coordinate> grapples;

	private boolean animating = false;
	private boolean displayingSolution = false;
	private Timer animationTimer;
	private int framePeriod = 20; // 50 FPS
	private Integer frameNumber = null;
	private int maxFrameNumber;

	private int samplingPeriod = 100;

	@SuppressWarnings("unused")
	private VisualizationPanel() {
	}

	public VisualizationPanel(Visualizer visualizer, Image img) {
		super();
		this.bgImage = img;
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		this.visualizer = visualizer;
	}

	public void setDisplayingSolution(boolean displayingSolution) {
		this.displayingSolution = displayingSolution;
		repaint();
	}

	public boolean isDisplayingSolution() {
		return displayingSolution;
	}

	public void setFramerate(int framerate) {
		this.framePeriod = 1000 / framerate;
		if (animationTimer != null) {
			animationTimer.setDelay(framePeriod);
		}
	}

	public void initAnimation() {
		if (!probNSolt.isSolutionLoaded()) {
			return;
		}
		if (animationTimer != null) {
			animationTimer.stop();
		}
		animating = true;
		gotoFrame(0);
		maxFrameNumber = this.probNSolt.robotStates.size() - 1;
		animationTimer = new Timer(framePeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int newFrameNumber = frameNumber + 1;
				if (newFrameNumber >= maxFrameNumber) {
					animationTimer.stop();
					visualizer.setPlaying(false);
				}
				if (newFrameNumber <= maxFrameNumber) {
					gotoFrame(newFrameNumber);
				}
			}
		});
		visualizer.setPlaying(false);
		visualizer.updateMaximum();
	}

	public void gotoFrame(int frameNumber) {
		if (!animating || (this.frameNumber != null
				&& this.frameNumber == frameNumber)) {
			return;
		}
		this.frameNumber = frameNumber;
		this.visualizer.setFrameNumber(frameNumber);
		this.currentRobotState = this.probNSolt.robotStates.get(frameNumber);
		this.obstacles = Board.obstacles;
		this.initRobotState = Board.initRobotState;
		this.goalRobotState = Board.goalRobotState;
		this.grapples = Board.grapples;
		repaint();
	}

	public int getFrameNumber() {
		return frameNumber;
	}

	public void playPauseAnimation() {
		if (animationTimer.isRunning()) {
			animationTimer.stop();
			visualizer.setPlaying(false);
		} else {
			if (frameNumber >= maxFrameNumber) {
				gotoFrame(0);
			}
			animationTimer.start();
			visualizer.setPlaying(true);
		}
	}

	public void stopAnimation() {
		if (animationTimer != null) {
			animationTimer.stop();
		}
		animating = false;
		visualizer.setPlaying(false);
		frameNumber = null;
	}

	public ProblemAndSolution getProblemAndSolution() {
		return this.probNSolt;
	}

	public void calculateTransform() {
		System.out.println(getWidth());
		System.out.println(getHeight());
		transform = AffineTransform.getScaleInstance(getWidth(), -getHeight());
		transform.concatenate(translation);
	}

	private void paintRobot(Graphics2D g2, RobotState rs, Color color) {
		if ((g2 == null) || (rs == null) || (color == null)) {
			System.exit(-1);
		}

		g2.setColor(color);
		g2.setStroke(new BasicStroke(3f));

		// Robot
		for (int i = 0; i < rs.joints.size() - 1; i++) {
			Coordinate p = rs.joints.get(i);
			Coordinate q = rs.joints.get(i + 1);
			g2.draw(transform.createTransformedShape(
					new Line2D.Double(p.X, p.Y, q.X, q.Y)));
		}

		double X1 = (rs.ee1.X
				+ (Math.cos(rs.ee1Segments.get(0).angle.radian + Math.PI)
						* GlobalCfg.eeLabelOffset))
				* GlobalCfg.displayPanelSize;
		double Y1 = (1 - (rs.ee1.Y
				+ (Math.sin(rs.ee1Segments.get(0).angle.radian + Math.PI)
						* GlobalCfg.eeLabelOffset)))
				* GlobalCfg.displayPanelSize;

		double X2 = (rs.ee2.X
				+ (Math.cos(rs.ee2Segments.get(0).angle.radian + Math.PI)
						* GlobalCfg.eeLabelOffset))
				* GlobalCfg.displayPanelSize;

		double Y2 = (1 - (rs.ee2.Y
				+ (Math.sin(rs.ee2Segments.get(0).angle.radian + Math.PI)
						* GlobalCfg.eeLabelOffset)))
				* GlobalCfg.displayPanelSize;

		FontMetrics fm = g2.getFontMetrics();

		X1 = X1 - fm.stringWidth("ee1") / 2;
		X2 = X2 - fm.stringWidth("ee2") / 2;

		Y1 = Y1 + fm.getAscent() / 2;
		Y2 = Y2 + fm.getAscent() / 2;

		g2.drawString("ee1", (float) X1, (float) Y1);
		g2.drawString("ee2", (float) X2, (float) Y2);
	}

	private void paintObstacle(Graphics2D g2, BoundingBox box, Color color) {
		if ((g2 == null) || (box == null) || (color == null)) {
			System.exit(-1);
		}

		g2.setColor(color);
		g2.setStroke(new BasicStroke(3f));

		Rectangle2D.Double o = new Rectangle2D.Double(box.bl.X, box.bl.Y,
				(box.tr.X - box.bl.X), (box.tr.Y - box.bl.Y));
		g2.fill(transform.createTransformedShape(o));
	}

	private void paintGrapple(Graphics2D g2, Coordinate gpl, Color color) {
		if ((g2 == null) || (gpl == null) || (color == null)) {
			System.exit(-1);
		}

		g2.setColor(color);
		g2.setStroke(new BasicStroke(3f));

		double X = gpl.X - GlobalCfg.gpRadius;
		double Y = gpl.Y - GlobalCfg.gpRadius;

		Ellipse2D.Double g = new Ellipse2D.Double(X, Y, 2 * GlobalCfg.gpRadius,
				2 * GlobalCfg.gpRadius);
		g2.fill(transform.createTransformedShape(g));
	}

	/*-
	public void paintState(Graphics2D g2, RobotConfig rc, List<Box> mb,
			List<Box> mo) {
		if ((rc == null) || (mb == null) || (mo == null)) {
			return;
		}
		// Define Robot
		Line2D.Float robot = new Line2D.Float(
				rc.getX1(problemSetup.getRobotWidth()),
				rc.getY1(problemSetup.getRobotWidth()),
				rc.getX2(problemSetup.getRobotWidth()),
				rc.getY2(problemSetup.getRobotWidth()));
	
		// Draw Moving Boxes
		g2.setColor(Color.blue);
		g2.draw(robot);
		for (Box box : mb) {
			g2.draw(transform.createTransformedShape(box.getRect()));
		}
	
		// Draw Moving Obstacles
		g2.setColor(Color.orange);
		g2.draw(robot);
		for (Box box : mo) {
			g2.draw(transform.createTransformedShape(box.getRect()));
		}
	
		// Draw Robot last so it's on top
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(2));
		g2.draw(transform.createTransformedShape(robot));
	
	}
	*/

	public void setSamplingPeriod(int samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
		repaint();
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if (!this.probNSolt.isProblemLoaded()) {
			return;
		}
		this.setSize(GlobalCfg.displayPanelSize, GlobalCfg.displayPanelSize);
		this.calculateTransform();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.drawImage(this.bgImage, 0, 0, null);

		// System.out.println("[Paint] : " + this.probNSolt.board.state);
		for (BoundingBox ob : Board.obstacles)
			this.paintObstacle(g2, ob, Color.WHITE);

		for (Coordinate gpl : Board.grapples)
			this.paintGrapple(g2, gpl, Color.RED);

		this.paintRobot(g2, this.probNSolt.board.state, Color.BLUE);
		this.paintRobot(g2, Board.goalRobotState, Color.GREEN);
		/*-
		if (this.obstacles != null) {
			g2.setColor(Color.red);
			for (BoundingBox ob : this.obstacles) {
				Shape transformed = transform
						.createTransformedShape(ob.getRect());
				g2.draw(transformed);
			}
		}
		
		List<Box> movingBoxEndPositions = generateMovingBoxes(
				problemSetup.getMovingBoxEndPositions());
		g2.setColor(Color.green);
		for (Box box : movingBoxEndPositions) {
			Shape transformed = transform.createTransformedShape(box.getRect());
			g2.draw(transformed);
		}
		
		g2.setStroke(new BasicStroke(2));
		if (!animating) {
			if (displayingSolution) {
				List<RobotConfig> robotConfigPath = problemSetup.getRobotPath();
				List<List<Box>> movingBoxPath = problemSetup.getMovingBoxPath();
				List<List<Box>> movingObstaclePath = problemSetup
						.getMovingObstaclePath();
				int lastIndex = robotConfigPath.size() - 1;
				for (int i = 0; i < lastIndex; i += samplingPeriod) {
					float t = (float) i / lastIndex;
					g2.setColor(new Color(0, t, 1 - t));
					paintState(g2, robotConfigPath.get(i), movingBoxPath.get(i),
							movingObstaclePath.get(i));
				}
				g2.setColor(Color.green);
				paintState(g2, robotConfigPath.get(lastIndex),
						movingBoxPath.get(lastIndex),
						movingObstaclePath.get(lastIndex));
			} else {
				paintState(g2, problemSetup.getInitialRobotConfig(),
						problemSetup.getMovingBoxes(),
						problemSetup.getMovingObstacles());
			}
		} else {
			g2.setColor(Color.blue);
			paintState(g2, currentRobotConfig, currentMovingBoxes,
					currentMovingObstacles);
		}
		*/
	}

	/*-
	public List<Box> generateMovingBoxes(List<Point2D> movingBoxCentres) {
		List<Box> movingBoxes = new ArrayList<>();
		Point2D centre;
		for (int i = 0; i < movingBoxCentres.size(); i++) {
			centre = movingBoxCentres.get(i);
			movingBoxes
					.add(new MovingBox(centre, problemSetup.getRobotWidth()));
		}
		return movingBoxes;
	}
	*/

}
