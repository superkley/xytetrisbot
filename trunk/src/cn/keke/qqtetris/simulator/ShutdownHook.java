package cn.keke.qqtetris.simulator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class ShutdownHook extends Thread {
	StrategyOptimizer optimizer;

	public ShutdownHook(StrategyOptimizer optimizer) {
		this.optimizer = optimizer;
	}

	@Override
	public void run() {
		saveState();
	}

	private void saveState() {
		try {
			FileWriter writer = new FileWriter(StrategyOptimizer.FILE_CSV);
			Collection<double[]> values = optimizer.history.values();
			for (double[] c : values) {
				String line = Arrays.toString(c);
				writer.write(line.substring(1, line.length() - 2));
				writer.write('\n');
			}
			for (double[] c : optimizer.chromosomes) {
				String line = Arrays.toString(c);
				writer.write(line.substring(1, line.length() - 2));
				writer.write('\n');
			}
			writer.close();
			System.out.println("Written current states to '"
					+ StrategyOptimizer.FILE_CSV + "' ...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
