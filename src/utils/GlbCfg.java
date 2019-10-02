package utils;

import java.awt.Color;

public final class GlbCfg {

	public static final int prime1 = 7;
	public static final int prime2 = 13;
	public static final int prime3 = 23;
	public static final int prime4 = 31;
	public static final int prime5 = 89;

	public static final double epsilon = 1e-6;
	public static final double gpRadius = 0.013;

	public static final int frameRateMin = 1;
	public static final int frameRateMax = 200;
	public static final int frameRateInit = 50;
	public static final int samplingPeriodInit = 100;

	public static final int displayPanelSize = 600;
	public static final int controlPanelSizeX = 600;
	public static final int controlPanelSizeY = 100;
	public static final int frameSizeX = 610;
	public static final int frameSizeY = 800;

	public static final int lineWidth = 3;
	public static final double eeLabelOffset = 0.025;

	public static final String loadProblem = "Load Problem";
	public static final String loadSolution = "Load Solution";
	public static final String exit = "Exit";
	public static final String file = "File";

	public static final Color robotStateColor = Color.BLUE;
	public static final Color targetStateColor = Color.GREEN;
	public static final Color grapplesColor = Color.RED;
	public static final Color obstacleColor = Color.WHITE;

	public static final double deltaRadian = 0.0349065850399;
	public static final double deltaLength = 0.005;
	public static final double rrtMaxRadianDistance = deltaRadian * 4;
	public static final double rrtMaxLengthDistance = deltaLength * 4;
	public static final double angleLowerBound = (-11 * Math.PI / 12) - epsilon;
	public static final double angleUpperBound = (11 * Math.PI / 12) + epsilon;
	public static final double angleSampleRange = angleUpperBound
			- angleLowerBound;

	public static final int maxNumberOfSamples = 100000;

}
