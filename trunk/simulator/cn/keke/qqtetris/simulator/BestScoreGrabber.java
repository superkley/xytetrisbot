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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class BestScoreGrabber {
	static class Info {
		public int s;

		public int c;

		public Info(int s) {
			this.s = s;
			this.c = 1;
		}

		public Info add(int s) {
			this.s += s;
			this.c++;
			return this;
		}

		public Double getAvg() {
			return ((double) this.s) / this.c;
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(
		"F:\\k-tune.txt")); // max: 1528, min->1000
//		BufferedReader r = new BufferedReader(new FileReader(
//				"F:\\l-tune.txt")); // max: 1528, min->1000
//		BufferedReader r = new BufferedReader(new FileReader(
//				"F:\\t-tune.dat")); // max: 2894, min->1800
		String line;
		HashMap<String, Info> m = new HashMap<String, Info>();
		while ((line = r.readLine()) != null) {
			if (line.startsWith("Ended ")) {
				String a = line.substring(line.indexOf('[') + 1,
						line.indexOf(']'));
				int s = Integer
						.parseInt(line.substring(line.indexOf("-> ") + 3));
				Info o = m.get(a);
				if (o != null) {
					m.put(a, o.add(s));
				} else {
					m.put(a, new Info(s));
				}
			}
		}
		TreeMap<Double, String> t = new TreeMap<Double, String>();
		Set<String> k = m.keySet();
		for (String key : k) {
			Info info = m.get(key);
			t.put(info.getAvg(), info.c + ": " + key);
		}
		for (int i = Math.max(0, t.size() - 51); i < t.size(); i++) {
			Double max = t.lastKey();
			System.out.println(max + ": " + t.get(max));
			t.remove(max);
		}
	}
}
