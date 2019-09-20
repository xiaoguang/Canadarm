package solve;

import javax.swing.JButton;
import javax.swing.JFrame;

import comp.Board;
import comp.ProblemSpec;

public class Solver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("My First GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		JButton button1 = new JButton("Button 1");
		JButton button2 = new JButton("Button 2");
		frame.getContentPane().add(button1);
		frame.getContentPane().add(button2);
		frame.setVisible(true);

		try {
			Board bd = ProblemSpec.readInput(args[0]);
			// System.out.println(bd.toString());
			ProblemSpec.readOutput(args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
