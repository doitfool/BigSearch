package org.answercow.stream.intent.queryExtent.KBExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.answercow.stream.common.Query;
import org.answercow.stream.common.ACWord;
import org.answercow.stream.intent.queryExtent.HowNet.HowNet;
import org.answercow.stream.intent.queryExtent.MappingFileOperation.MappingFile;
import org.answercow.utils.RLog;
import org.apache.hadoop.hbase.util.Hash;

import com.hankcs.hanlp.collection.dartsclone.Pair;
import com.jcraft.jsch.jce.ARCFOUR;

/**
 * Created by AC on 2015/12/27.
 */
public class KBWordMapping {
	private HashMap<String, List<Pair<String, Double>>> relationExt2uriMap = null;
	private HashMap<String, List<Pair<String, Double>>> entityExt2uriMap = null;

	public KBWordMapping() throws IOException {
		MappingFile mf = new MappingFile();
		try {
			relationExt2uriMap = mf.loadKBRelationMappingFile(mf
					.getKBRelationMappingFile());
		} catch (Exception e) {
			RLog.error(e.getMessage());
		}
		try {
			entityExt2uriMap = mf.loadKBEntityMappingFile(mf
					.getKBEntityMappingFile());
		} catch (Exception e) {
			RLog.error(e.getMessage());
		}
	}

	public HashMap<String, List<Pair<String, Double>>> getRelationExt2uriMap() {
		return relationExt2uriMap;
	}

	public Query extend(Query query) {
		if (query.getTitleSegment() != null) {
			for (ACWord word : query.getTitleSegment()) {
				relationMapping(word);
				// entityMapping(word);
			}
		}
		if (query.getBodySegment() != null) {
			for (ACWord word : query.getBodySegment()) {
				relationMapping(word);
				// entityMapping(word);
			}
		}
		return query;
	}

	// 知识图谱关系映射
	public ACWord relationMapping(ACWord word) {
		if (relationExt2uriMap != null
				&& relationExt2uriMap.containsKey(word.getWord())) { // 知识图谱关系扩展字典中存在搜索词
			List<Pair<String, Double>> freebaseURI = relationExt2uriMap
					.get(word.getWord());
			word.setFreebaseURI(freebaseURI);
			word.setType(1);
		} else {
			RLog.debug("知识图谱关系扩展字典中没有收录" + word.getWord());
		}
		return word;
	}

	// 知识图谱实体映射
	public ACWord entityMapping(ACWord word) {
		if (entityExt2uriMap != null
				&& entityExt2uriMap.containsKey(word.getWord())) { // 知识图谱关系扩展字典中存在搜索词
			List<Pair<String, Double>> freebaseURI = entityExt2uriMap.get(word
					.getWord());
			word.setFreebaseURI(freebaseURI);
			word.setType(0);
		} else {
			RLog.debug("知识图谱实体映射字典中没有收录" + word.getWord());
		}
		return word;
	}

	public static void main(String[] args) throws IOException {
		KBWordMapping kbm = new KBWordMapping();
		Query query = new Query();

		List<ACWord> titleSegment = new ArrayList<ACWord>();
		ACWord word1 = new ACWord();
		word1.setWord("首都");
		ACWord word2 = new ACWord();
		word2.setWord("孩子");
		titleSegment.add(word1);
		titleSegment.add(word2);
		query.setTitleSegment(titleSegment);

		query = kbm.extend(query);
		for (ACWord word : query.getTitleSegment()) {
			System.out.print("word:" + word.getWord() + "\ttype:"
					+ word.getType() + "\tfreebaseURI:[");
			for (Pair<String, Double> pair : word.getFreebaseURI()) {
				System.out.print(pair.toString() + "\t");
			}
			// RLog.debug();
		}

	}

}
