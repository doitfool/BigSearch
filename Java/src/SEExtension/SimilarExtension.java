package org.answercow.stream.intent.queryExtent.SEExtension;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.answercow.stream.common.DataFormat;
import org.answercow.stream.common.Query;
import org.answercow.stream.common.ACWord;
import org.answercow.stream.intent.queryExtent.HowNet.HowNet;
import org.answercow.stream.intent.queryExtent.MappingFileOperation.MappingFile;
import org.answercow.stream.intent.queryExtent.TongyiciCilin.TongyiciCilin;
import org.answercow.stream.intent.queryExtent.Word2Vec.W2VWebService;
import org.answercow.stream.intent.queryExtent.Word2Vec.Word2VEC;
import org.answercow.stream.intent.queryExtent.Word2Vec.domain.WordEntry;
import org.answercow.utils.RLog;

/**
 * Created by AC on 2015/12/27. 相似相关扩展模块：使用word2vec，hownet和同义词词林进行词扩展，用于元搜索
 * 
 * modified by AC on 2016/1/4 暂时只使用中文维基语料训练的word2vec模型进行相关词扩展
 */
public class SimilarExtension {
	private List<ArrayList<String>> synonymList = null;
	W2VWebService ws = null;
	
	public SimilarExtension(W2VWebService w2vWebService) throws Exception {
		setSynonymList(MappingFile.loadTongyiciCilinFile(MappingFile.getTongyiciCilinFile()));
		ws = w2vWebService;
	}
	
	public List<ArrayList<String>> getSynonymList() {
		return synonymList;
	}

	public void setSynonymList(List<ArrayList<String>> synonymList) {
		this.synonymList = synonymList;
	}
	
	public Query extend(Query query) throws NumberFormatException, RemoteException {
		TongyiciCilin tongyiciCilin = new TongyiciCilin();
		if (query.getTitleSegment() != null) {
			for (ACWord word : query.getTitleSegment()) {
				extend(word, tongyiciCilin);
			}
		}
		if (query.getBodySegment() != null) {
			for (ACWord word : query.getBodySegment()) {
				extend(word, tongyiciCilin);
			}
		}
		return query;
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
	public List<ACWord> extend(String word, TongyiciCilin tongyiciCilin) throws NumberFormatException, RemoteException {
		List<ACWord> similarWords = new ArrayList<ACWord>();
		for ( ArrayList<String> synonyms : getSynonymList() ){
			if ( synonyms.contains(word) ){
				for ( String synonym : synonyms ){
					if ( !word.equals(synonym) ){
						ACWord similarWord = new ACWord();
						similarWord.setWord(synonym);
						similarWord.setWeight(Double.parseDouble(ws.getProxy().word_similarity_zh(word, synonym)));	//	使用word2vec计算词汇相似度
						similarWords.add(similarWord);
					}
				}
			}
		}
		return similarWords;
	}
	
	public List<ACWord> extend(ACWord word, TongyiciCilin tongyiciCilin) throws NumberFormatException, RemoteException {
		List<ACWord> similarWords = new ArrayList<ACWord>();
		String lemma = word.getWord();
		String pos = word.getPos();
		for ( ArrayList<String> synonyms : getSynonymList() ){
			if ( synonyms.contains(lemma) ){
				for ( String synonym : synonyms ){
					if ( !lemma.equals(synonym) ){
						ACWord similarWord = new ACWord();
						similarWord.setWord(synonym);
						similarWord.setPos(pos);
						similarWord.setWeight(HowNet.simWord(lemma, synonym));	//	使用hownet计算词汇相似度
//						similarWord.setWeight(Double.parseDouble(ws.getProxy().word_similarity_zh(word.getWord(), synonym)));	//	使用word2vec计算词汇相似度
						similarWords.add(similarWord);
					}
				}
			}
		}
		return similarWords;
	}
	
	public static void main(String[] args) throws Exception {
		W2VWebService ws = new W2VWebService();
		SimilarExtension similarExtension = new SimilarExtension(ws);
		Scanner scanner = new Scanner(System.in);
		List<ACWord> extWords;

		// 同义词词林扩展测试
		TongyiciCilin tongyiciCilin = new TongyiciCilin();

		while (scanner.hasNext()) {
			String word = scanner.nextLine();
			extWords = similarExtension.extend(word, tongyiciCilin);
			RLog.debug(word + "扩展如下:");
			for (ACWord extWord : extWords) {
				RLog.debug(extWord.getWord() + "\t" + extWord.getWeight());
			}
		}
	}

	
}
