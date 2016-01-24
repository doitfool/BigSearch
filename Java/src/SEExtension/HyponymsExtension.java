package org.answercow.stream.intent.queryExtent.SEExtension;

import org.answercow.stream.intent.queryExtent.MappingFileOperation.MappingFile;
import org.answercow.stream.intent.queryExtent.Word2Vec.Word2VEC;
import org.answercow.stream.common.Query;
import org.answercow.stream.common.ACWord;
import org.json.JSONObject;
import org.answercow.stream.intent.queryExtent.HowNet.HowNet;
import org.answercow.utils.RLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AC on 2015/12/27. 下位词扩展模块：基于nltk建立的下位词词典，对词语进行下位词扩展，用于元搜索
 */
public class HyponymsExtension {
	private HashMap<String, List<String>> hype2HyposMap = null;
	private HowNet WSInstance = null;
	 HowNet howNet = null;
	public HyponymsExtension() throws IOException {
		MappingFile mf = new MappingFile();
		hype2HyposMap = mf.loadHypoMappingFile(mf.getSEMappingFile());
		 WSInstance = new HowNet(); // 暂时使用hownet计算相似度
		  howNet = (HowNet) WSInstance; 
	}

	public HashMap<String, List<String>> getHype2HyposMap() {
		return hype2HyposMap;
	}

	public Query extend(Query query) {
		
		if (query.getTitleSegment() != null) {
			for (ACWord word : query.getTitleSegment()) {
				extend(word);
			}
		}
		if (query.getBodySegment() != null) {
			for (ACWord word : query.getBodySegment()) {
				extend(word);
			}
		}
		return query;
	}

	/**
	 * @description 下位词扩展
	 * @param ACWord
	 *            word
	 * @param WSInstance
	 *            : 词语相似度计算实例化类（HoeNet， Word2Vec或TongyiciCilin）
	 * @return List<Word>
	 */
	public List<ACWord> extend(ACWord word) {
		// 暂时使用hownet计算相似度
		List<ACWord> hyponymsWord = null;
		if (hype2HyposMap.containsKey(word.getWord())) { // 元搜索下位词字典中存在搜索词
			hyponymsWord = new ArrayList<ACWord>();
			List<String> hyponyms = hype2HyposMap.get(word.getWord());
			for (String hyponym : hyponyms) { // 将String类型下位词修改为Word类型
				String tagger = word.getPos(); // 扩展词词性默认与原词词性相同
				double weight = howNet.simWord(word.getWord(), hyponym); // 调用实例化相似度计算类的相似度计算方法，计算下位词与原词的相似度
				ACWord hyponymWord = new ACWord();
				hyponymWord.setWord(hyponym);
				hyponymWord.setPos(tagger);
				hyponymWord.setWeight(weight);
				hyponymsWord.add(hyponymWord);
			}
		} else {
//			RLog.debug("基于nltk构建的上下位词词典WordNetHypo_zh.txt中没有收录"
//					+ word.getWord());
		}
		return hyponymsWord;
	}

	public static void main(String[] args) throws IOException {
		HowNet howNet = new HowNet();
		ACWord word1 = new ACWord();
		word1.setWord("医生");
		word1.setPos("N");
		HyponymsExtension hypoExt = new HyponymsExtension();
		List<ACWord> hypoWords = hypoExt.extend(word1);
		RLog.debug("基于nltk建立的下位词词典扩展的下位词如下：");
		for (ACWord hypoWord : hypoWords) {
			RLog.debug(hypoWord.getWord() + "\t" + hypoWord.getWeight());
		}
	}
}
