package org.app.intelligentrobot.entity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CosineSimilarAlgorithm {
	public static double getSimilarity(String doc1, String doc2) {
		if (doc1 != null && doc1.trim().length() > 0 && doc2 != null
				&& doc2.trim().length() > 0) {

			Map<Integer, int[]> AlgorithmMap = new HashMap<Integer, int[]>();

			for (int i = 0; i < doc1.length(); i++) {
				char d1 = doc1.charAt(i);
				if (isHanZi(d1)) {
					int charIndex = getGB2312Id(d1);
					if (charIndex != -1) {
						int[] fq = AlgorithmMap.get(charIndex);
						if (fq != null && fq.length == 2) {
							fq[0]++;
						} else {
							fq = new int[2];
							fq[0] = 1;
							fq[1] = 0;
							AlgorithmMap.put(charIndex, fq);
						}
					}
				}
			}

			for (int i = 0; i < doc2.length(); i++) {
				char d2 = doc2.charAt(i);
				if (isHanZi(d2)) {
					int charIndex = getGB2312Id(d2);
					if (charIndex != -1) {
						int[] fq = AlgorithmMap.get(charIndex);
						if (fq != null && fq.length == 2) {
							fq[1]++;
						} else {
							fq = new int[2];
							fq[0] = 0;
							fq[1] = 1;
							AlgorithmMap.put(charIndex, fq);
						}
					}
				}
			}

			Iterator<Integer> iterator = AlgorithmMap.keySet().iterator();
			double sqdoc1 = 0;
			double sqdoc2 = 0;
			double denominator = 0;
			while (iterator.hasNext()) {
				int[] c = AlgorithmMap.get(iterator.next());
				denominator += c[0] * c[1];
				sqdoc1 += c[0] * c[0];
				sqdoc2 += c[1] * c[1];
			}

			return denominator / Math.sqrt(sqdoc1 * sqdoc2);
		} else {
			throw new NullPointerException(
					" the Document is null or have not cahrs!!");
		}
	}

	public static boolean isHanZi(char ch) {
		return (ch >= 0x4E00 && ch <= 0x9FA5);

	}

	public static short getGB2312Id(char ch) {
		try {
			byte[] buffer = Character.toString(ch).getBytes("GB2312");
			if (buffer.length != 2) {

				return -1;
			}
			int b0 = (int) (buffer[0] & 0x0FF) - 161;
			int b1 = (int) (buffer[1] & 0x0FF) - 161;
			return (short) (b0 * 94 + b1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int EditDistanceChange(String source, String target) {
		if (target.length() != 0 && source.length() == 0) {
			return EditDistanceChange(source, target.substring(1)) + 1;
		} else if (target.length() == 0 && source.length() != 0) {
			return EditDistanceChange(source.substring(1), target) + 1;
		} else if (target.length() != 0 && source.length() != 0) {
			// 当源字符第一个值和目标字符第一个值相同时
			if (source.charAt(0) == target.charAt(0)) {
				return EditDistanceChange(source.substring(1),
						target.substring(1));
			} else {
				int insert = EditDistanceChange(source.substring(1), target) + 1;
				int del = EditDistanceChange(source, target.substring(1)) + 1;
				int update = EditDistanceChange(source.substring(1),
						target.substring(1)) + 1;
				return Math.min(insert, del) > Math.min(del, update) ? Math
						.min(del, update) : Math.min(insert, del);
			}
		} else {
			return 0;
		}
	}

	public static int EditDistance(String source, String target) {
		char[] s = source.toCharArray();
		char[] t = target.toCharArray();
		int slen = source.length();
		int tlen = target.length();
		int d[][] = new int[slen + 1][tlen + 1];
		for (int i = 0; i <= slen; i++) {
			d[i][0] = i;
		}
		for (int i = 0; i <= tlen; i++) {
			d[0][i] = i;
		}
		for (int i = 1; i <= slen; i++) {
			for (int j = 1; j <= tlen; j++) {
				if (s[i - 1] == t[j - 1]) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					int insert = d[i][j - 1] + 1;
					int del = d[i - 1][j] + 1;
					int update = d[i - 1][j - 1] + 1;
					d[i][j] = Math.min(insert, del) > Math.min(del, update) ? Math
							.min(del, update) : Math.min(insert, del);
				}
			}
		}
		return d[slen][tlen];
	}

}