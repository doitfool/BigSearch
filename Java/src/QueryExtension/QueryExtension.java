package org.answercow.stream.intent.queryExtent.QueryExtension;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.rpc.ServiceException;

import org.answercow.stream.common.DataFormat;
import org.answercow.stream.common.Query;
import org.answercow.stream.common.ACWord;
import org.answercow.stream.intent.queryExtent.KBExtension.KBWordMapping;
import org.answercow.stream.intent.queryExtent.SEExtension.HyponymsExtension;
import org.answercow.stream.intent.queryExtent.Word2Vec.W2VWebService;
import org.answercow.stream.intent.queryExtent.Word2Vec.Word2VEC;
import org.answercow.utils.RLog;

import net.sf.json.JSONObject;
import tns.Application_PortType;
import tns.Application_Service;
import tns.Application_ServiceLocator;

/**
 * @author AC
 * @date 2016年1月8日
 * @description
 */

public class QueryExtension {
	KBWordMapping kbWordMapping = null;
	HyponymsExtension hypoExt = null;

	public QueryExtension() {

		try {
			kbWordMapping = new KBWordMapping();
			// 对titleSegment和bodySegment进行知识图谱映射
			hypoExt = new HyponymsExtension(); // 对titleSegment和bodySegment进行元搜索下位词扩展
		} catch (IOException e) {
			// TODO Auto-generated catch block
			RLog.error(e.getMessage());
		}
	}

	public Query queryExtend(Query query) throws Exception {

		List<ACWord> titleSegment = query.getTitleSegment();
		List<ACWord> bodySegment = query.getBodySegment();

		W2VWebService w2vWebService = new W2VWebService(); // 对titleSegment和bodySegment进行元搜索同义词扩展
		try {
			w2vWebService.extend(query);
		} catch (Exception e) {

		}

		try {
			if (hypoExt != null)
				hypoExt.extend(query);
		} catch (Exception e) {

		}
		if (kbWordMapping != null)
			kbWordMapping.extend(query);

		return query;

	}

	public static void main(String[] args) throws Exception {
		Query query = new Query();
		List<ACWord> titleSegment = new ArrayList<ACWord>();
		ACWord word1 = new ACWord();
		word1.setWord("中国");
		word1.setPos("N");
		ACWord word2 = new ACWord();
		word2.setWord("足球");
		word2.setPos("N");
		titleSegment.add(word1);
		titleSegment.add(word2);
		query.setTitleSegment(titleSegment);

		List<ACWord> bodySegment = new ArrayList<ACWord>();
		ACWord word3 = new ACWord();
		word3.setWord("中国");
		word3.setPos("N");
		ACWord word4 = new ACWord();
		word4.setWord("男子");
		word4.setPos("N");
		ACWord word5 = new ACWord();
		word5.setWord("足球");
		word5.setPos("N");
		ACWord word6 = new ACWord();
		word6.setWord("进入");
		word6.setPos("V");
		ACWord word7 = new ACWord();
		word7.setWord("世界杯");
		word7.setPos("N");
		bodySegment.add(word3);
		bodySegment.add(word4);
		bodySegment.add(word5);
		bodySegment.add(word6);
		bodySegment.add(word7);
		query.setTitleSegment(bodySegment);
		RLog.debug(query);

		QueryExtension queryExtension = new QueryExtension();
		queryExtension.queryExtend(query);

		RLog.debug(query);
	}

}
