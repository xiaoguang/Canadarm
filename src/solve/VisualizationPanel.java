package solve;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.Timer;

public class VisualizationPanel extends JComponent {

	private static final long serialVersionUID = -609530771419701598L;
	private Timer animationTimer;
	private Visualizer visualizer;
	private int samplingPeriod = 100;

	public VisualizationPanel(Visualizer v) {
		super();
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		this.visualizer = v;
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
			visualiser.setPlaying(true);
		}
	}

	public void setSamplingPeriod(int samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
		repaint();
	}

	public void gotoFrame(int frameNumber) {
		if (!animating || (this.frameNumber != null
				&& this.frameNumber == frameNumber)) {
			return;
		}
		this.frameNumber = frameNumber;
		visualiser.setFrameNumber(frameNumber);
		currentRobotConfig = problemSetup.getRobotPath().get(frameNumber);
		currentMovingBoxes = problemSetup.getMovingBoxPath().get(frameNumber);
		currentMovingObstacles = problemSetup.getMovingObstaclePath()
				.get(frameNumber);
		repaint();
	}

	public void setFramerate(int framerate) {
		this.framePeriod = 1000 / framerate;
		if (animationTimer != null) {
			animationTimer.setDelay(framePeriod);
		}
	}

	public void initAnimation() {
		if (!problemSetup.getSolutionLoaded()) {
			return;
		}
		if (animationTimer != null) {
			animationTimer.stop();
		}
		animating = true;
		gotoFrame(0);
		maxFrameNumber = problemSetup.getRobotPath().size() - 1;
		animationTimer = new Timer(framePeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int newFrameNumber = frameNumber + 1;
				if (newFrameNumber >= maxFrameNumber) {
					animationTimer.stop();
					visualiser.setPlaying(false);
				}
				if (newFrameNumber <= maxFrameNumber) {
					gotoFrame(newFrameNumber);
				}
			}
		});
		visualizer.setPlaying(false);
		visualizer.updateMaximum();
	}

	public void stopAnimation() {
		if (animationTimer != null) {
			animationTimer.stop();
		}
		animating = false;
		visualizer.setPlaying(false);
		frameNumber = null;
	}

	public void calculateTransform() {
		transform = AffineTransform.getScaleInstance(getWidth(), -getHeight());
		transform.concatenate(translation);
	}

	public boolean isDisplayingSolution() {
		return displayingSolution;
	}

}
