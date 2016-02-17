package org.answercow.stream.intent.queryExtent.Word2Vec;

import java.util.ArrayList;
import java.util.List;

import org.answercow.stream.common.Query;
import org.answercow.stream.common.CowConfig;
import org.answercow.stream.common.ACWord;
import org.answercow.utils.RLog;

import tns.ApplicationProxy;

/**
 * @author AC
 * @date 2016年1月8日
 * @description
 */
public class W2VWebService {
	private ApplicationProxy proxy;
	private String endPoint;

	public W2VWebService() throws Exception {
		setEndPoint(CowConfig.W2V_WS_ENDPOINT());
		setProxy(new ApplicationProxy(getEndPoint()));
	}

	public Query extend(Query query) throws Exception {
		for (ACWord word : query.getTitleSegment()) {
			if (word.getNerType()>=0)
				continue;
			List<ACWord> synonyms = new ArrayList<ACWord>(); // 存储word的同义词
			String[] extendResults = getProxy().w2V_extend_zh(word.getWord());
			for (String extendResult : extendResults) {
				String[] extend = extendResult.split("\t"); // extend格式：extendWord\tweight
															// (大陆
															// 0.515557408333)
				String wordStr = extend[0];
				double weight = Double.parseDouble(extend[1]);
				ACWord synonym = new ACWord();
				synonym.setWord(wordStr);
				synonym.setPos(word.getPos());
				synonym.setNerType(word.getNerType());
				synonym.setWeight(weight);
				synonyms.add(synonym);
			}
			word.setSynonyms(synonyms);
		}
		for (ACWord word : query.getBodySegment()) {
			if (word.getNerType()>=0)
				continue;
			List<ACWord> synonyms = new ArrayList<ACWord>(); // 存储word的同义词
			String[] extendResults = getProxy().w2V_extend_zh(word.getWord());
			for (String extendResult : extendResults) {
				String[] extend = extendResult.split("\t"); // extend格式：extendWord\tweight
															// (大陆
															// 0.515557408333)
				String wordStr = extend[0];
				double weight = Double.parseDouble(extend[1]);
				ACWord synonym = new ACWord();
				synonym.setWord(wordStr);
				synonym.setPos(word.getPos());
				synonym.setNerType(word.getNerType());
				synonym.setWeight(weight);
				synonyms.add(synonym);
			}
			word.setSynonyms(synonyms);
		}
		return query;
	}

	public ApplicationProxy getProxy() {
		return proxy;
	}

	public void setProxy(ApplicationProxy proxy) {
		this.proxy = proxy;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public static void main(String[] args) throws Exception {
		W2VWebService ws = new W2VWebService();
		String[] results = ws.getProxy().w2V_extend_zh("中国");
		for (String result : results) {
			RLog.debug(result);
		}
	}

}
