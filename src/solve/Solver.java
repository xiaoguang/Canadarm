package solve;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFrame;

import comp.Coordinate;

public class Solver {

	static class OutputFormat {
		String x1;
		String x2;
		String x3;

		Coordinate ee1;

		OutputFormat(String[] input) {
			assertTrue(input.length == 3);
			x1 = input[0].trim();
			x2 = input[1].trim();
			x3 = input[2].trim();
		}

		@Override
		public String toString() {
			String str = "";
			str = str.concat(x1).concat(" ; ");
			str = str.concat(x2).concat(" ; ");
			str = str.concat(x3).concat(" ; ");
			return str;
		}
	}

	private static void readOutput(String fileName) throws Exception {
		List<OutputFormat> list = new ArrayList<OutputFormat>();
		Stream<String> stream = Files.lines(Paths.get(fileName));
		// br returns as stream and convert it into a List
		list = (List<OutputFormat>) stream.map(p -> {
			return new OutputFormat(p.split(";"));
		}).collect(Collectors.toList());
		list.forEach(System.out::println);
		if (stream != null)
			stream.close();
	}

	private static void readInput(String fileName) throws Exception {
		List<String> list = new ArrayList<String>();
		Stream<String> stream = Files.lines(Paths.get(fileName));
		// list = (List<String>) stream.collect(Collectors.toList());
		list = (List<String>) stream.filter(p -> !p.startsWith("#"))
				.collect(Collectors.toList());
		list.forEach(System.out::println);
		if (stream != null)
			stream.close();
	}

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

		// real things
		try {
			readInput(args[0]);
			// readOutput(args[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
