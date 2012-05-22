/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
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
