package org.answercow.stream.intent.queryExtent.SEExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.answercow.stream.common.DataFormat;
import org.answercow.stream.common.ACWord;
import org.answercow.stream.intent.queryExtent.HowNet.HowNet;
import org.answercow.stream.intent.queryExtent.TongyiciCilin.TongyiciCilin;
import org.answercow.stream.intent.queryExtent.Word2Vec.Word2VEC;
import org.answercow.stream.intent.queryExtent.Word2Vec.domain.WordEntry;
import org.answercow.utils.RLog;

/**
 * Created by AC on 2015/12/27. 相似相关扩展模块：使用word2vec，hownet和同义词词林进行词扩展，用于元搜索
 * 
 * modified by AC on 2016/1/4 暂时只使用中文维基语料训练的word2vec模型进行相关词扩展
 */
public class SimilarExtension {

	public static void main(String[] args) throws IOException {
		SimilarExtension similarExtension = new SimilarExtension();
		Scanner scanner = new Scanner(System.in);
		List<ACWord> extWords;

		// word2vec词扩展测试
		Word2VEC w2v = new Word2VEC();

		while (scanner.hasNext()) {
			String word = scanner.nextLine();
			extWords = similarExtension.extend(word, w2v);
			RLog.debug(word + "扩展如下:");
			for (ACWord extWord : extWords) {
				RLog.debug(extWord.getWord() + "\t" + extWord.getWeight());
			}
		}
	}

	// 使用word2vec进行词扩展
	public List<ACWord> extend(String word, Word2VEC w2v) {
		List<ACWord> similarWords = DataFormat.w2vSimilarSet2WordList(w2v
				.distance(word));
		// similarWords是否有序？？？ 无序需要按照weight降序排序
		return similarWords;
	}

	public List<ACWord> extend(ACWord word, Word2VEC w2v) {
		List<ACWord> similarWords = DataFormat.w2vSimilarSet2WordList(w2v
				.distance(word.getWord()));
		// similarWords是否有序？？？ 无序需要按照weight降序排序
		return similarWords;
	}

	// 使用HowNet进行词扩展示例代码
	public List<ACWord> extend(ACWord word, HowNet howNet) {
		List<ACWord> similarWords = new ArrayList<ACWord>();
		return similarWords;
	}

	// 使用同义词词林进行词扩展示例代码
	public List<ACWord> extend(ACWord word, TongyiciCilin tongyiciCilin) {
		List<ACWord> similarWords = new ArrayList<ACWord>();
		return similarWords;
	}
}
