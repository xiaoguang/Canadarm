package comp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

import javax.swing.JComponent;
import javax.swing.Timer;

import comp.RobotState.RobotStateOutPut;
import solve.Visualizer;
import utils.GlbCfg;

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

	private boolean animating = false;
	private Timer animationTimer;
	private int framePeriod = 20; // 50 FPS
	private Integer frameNumber = null;
	private int maxFrameNumber;

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
		if (!this.animating || (this.frameNumber != null
				&& this.frameNumber == frameNumber)) {
			return;
		}

		this.frameNumber = frameNumber;
		this.visualizer.setFrameNumber(frameNumber);
		this.currentRobotState = this.probNSolt.robotStates
				.get(this.frameNumber);

		if (this.frameNumber == 0) {
			this.probNSolt.board.state = this.probNSolt.board.initRobotState
					.clone();
		}

		if (this.currentRobotState.ee.equals(this.probNSolt.board.state.ee1)) {
			// System.out.println("Grapple 1");
			this.probNSolt.board.state.ee1Grappled = true;
			this.probNSolt.board.state.ee2Grappled = false;

			for (int i = 0; i < this.currentRobotState.angles.size(); i++) {
				Segment local = this.probNSolt.board.state.segments.get(i);
				local.angle = this.currentRobotState.angles.get(i);
				local.len = this.currentRobotState.lengths.get(i);
			}

			this.probNSolt.board.state.calcJoints();
		} else if (this.currentRobotState.ee
				.equals(this.probNSolt.board.state.ee2)) {
			// System.out.println("Grapple 2");
			this.probNSolt.board.state.ee1Grappled = false;
			this.probNSolt.board.state.ee2Grappled = true;

			for (int i = 0; i < this.currentRobotState.angles.size(); i++) {
				Segment local = this.probNSolt.board.state.segments.get(i);
				local.angle = this.currentRobotState.angles.get(i);
				local.len = this.currentRobotState.lengths.get(i);
			}

			this.probNSolt.board.state.calcJoints();
		} else {
			System.exit(-1);
		}

		repaint();
	}

	public int getFrameNumber() {
		return this.frameNumber;
	}

	public void playPauseAnimation() {
		if (!this.probNSolt.isSolutionLoaded() || this.animationTimer == null)
			return;
		if (this.animationTimer.isRunning()) {
			this.animationTimer.stop();
			this.visualizer.setPlaying(false);
		} else {
			if (this.frameNumber >= this.maxFrameNumber) {
				gotoFrame(0);
			}
			this.animationTimer.start();
			this.visualizer.setPlaying(true);
		}
	}

	public void stopAnimation() {
		if (this.animationTimer != null) {
			this.animationTimer.stop();
		}
		this.animating = false;
		this.visualizer.setPlaying(false);
		this.frameNumber = null;
	}

	public ProblemAndSolution getProblemAndSolution() {
		return this.probNSolt;
	}

	public void calculateTransform() {
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
						* GlbCfg.eeLabelOffset))
				* GlbCfg.displayPanelSize;
		double Y1 = (1 - (rs.ee1.Y
				+ (Math.sin(rs.ee1Segments.get(0).angle.radian + Math.PI)
						* GlbCfg.eeLabelOffset)))
				* GlbCfg.displayPanelSize;

		double X2 = (rs.ee2.X
				+ (Math.cos(rs.ee2Segments.get(0).angle.radian + Math.PI)
						* GlbCfg.eeLabelOffset))
				* GlbCfg.displayPanelSize;

		double Y2 = (1 - (rs.ee2.Y
				+ (Math.sin(rs.ee2Segments.get(0).angle.radian + Math.PI)
						* GlbCfg.eeLabelOffset)))
				* GlbCfg.displayPanelSize;

		FontMetrics fm = g2.getFontMetrics();

		X1 = X1 - fm.stringWidth("ee1") / 2;
		X2 = X2 - fm.stringWidth("ee2") / 2;

		Y1 = Y1 + fm.getAscent() / 2;
		Y2 = Y2 + fm.getAscent() / 2;

		Font font = new Font("SERIF", Font.BOLD, 15);
		g2.setFont(font);
		g2.drawString("EE1", (float) X1, (float) Y1);
		g2.drawString("EE2", (float) X2, (float) Y2);
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

		double X = gpl.X - GlbCfg.gpRadius;
		double Y = gpl.Y - GlbCfg.gpRadius;

		Ellipse2D.Double g = new Ellipse2D.Double(X, Y, 2 * GlbCfg.gpRadius,
				2 * GlbCfg.gpRadius);
		g2.fill(transform.createTransformedShape(g));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if (!this.probNSolt.isProblemLoaded()) {
			return;
		}

		this.setSize(GlbCfg.displayPanelSize, GlbCfg.displayPanelSize);
		this.calculateTransform();

		Graphics2D g2 = (Graphics2D) graphics;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.drawImage(this.bgImage, 0, 0, null);

		for (BoundingBox ob : Board.obstacles)
			this.paintObstacle(g2, ob, GlbCfg.obstacleColor);

		for (Coordinate gpl : this.probNSolt.board.grapples)
			this.paintGrapple(g2, gpl, GlbCfg.grapplesColor);

		if (this.animating) {
			if (!this.probNSolt.isSolutionLoaded()) {
				return;
			}
			this.paintRobot(g2, this.probNSolt.board.state,
					GlbCfg.robotStateColor);
		} else {
			this.paintRobot(g2, this.probNSolt.board.initRobotState,
					GlbCfg.robotStateColor);
			this.paintRobot(g2, this.probNSolt.board.goalRobotState,
					GlbCfg.targetStateColor);
		}
	}

}
