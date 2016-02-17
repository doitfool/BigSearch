package org.answercow.stream.intent.queryExtent.MappingFileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.answercow.stream.common.CowConfig;
import org.answercow.utils.RLog;

import com.hankcs.hanlp.collection.dartsclone.Pair;

/**
 * Created by AC on 2015/12/27. 对于元搜索扩展和知识图谱扩展，都有下位词词典作为映射文件，统一格式，统一载入方法
 * 已完成元搜索下位词扩展，
 */
public class MappingFile {
	private static String SEMappingFile = CowConfig.MODEL_FILE_PREFIX()
			+ "queryExtent/WordNetHypo_zh.txt"; // 元搜索下位词扩展文件
	private static String KBRelationExtendFile = CowConfig.MODEL_FILE_PREFIX()
			+ "queryExtent/FBRelationExtendFile.txt"; // 知识图谱关系扩展文件
	private static String KBEntityMappingFile = CowConfig.MODEL_FILE_PREFIX()
			+ "queryExtent/FBEntityMappingFile.txt"; // 知识图谱实体映射文件
	private static String TongyiciCilinFile = CowConfig.MODEL_FILE_PREFIX()
			+ "queryExtent/synonym.txt";	// 同义词词林同义词扩展文件
	
	// 可考虑更高级别抽象，直接loadMappingFile将所有映射词典载入内存

	// 同义词词林扩展文件载入
	public static List<ArrayList<String>> loadTongyiciCilinFile(String filename) throws IOException{
		List<ArrayList<String>> synonymList = new ArrayList<ArrayList<String>>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ( (line = br.readLine()) != null ){
			ArrayList<String> synonyms = new ArrayList<String>();
			for ( String synonym : line.split(" ") ){
				synonyms.add(synonym);
			}
			synonymList.add(synonyms);
		}
		return synonymList;
	}
	
	// 元搜索下位词词典载入
	public static HashMap<String, List<String>> loadHypoMappingFile(String filename)
			throws IOException {
		HashMap<String, List<String>> hype2HyposMap = new HashMap<String, List<String>>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			String[] hypeHypos = line.split("\t");
			// RLog.debug(hypeHypos[0]+"\t"+hypeHypos[1]);
			// String.split()根据"|"或"."来分割时，由于他们都是转义字符，必须加上"\\"
			List<String> hypos = new ArrayList<String>(
					Arrays.asList(hypeHypos[1].split("\\|"))); // 将String[]转化为List<String>
			// System.out.print(hypeHypos[0]+"\t");
			// for ( String hypo : hypos ){
			// System.out.print(hypo+"-");
			// }
			// RLog.debug();
			hype2HyposMap.put(hypeHypos[0], hypos);
		}
		return hype2HyposMap;
	}

	// 知识图谱关系扩展词典载入
	public static HashMap<String, List<Pair<String, Double>>> loadKBRelationMappingFile(
			String filename) throws IOException {
		HashMap<String, List<Pair<String, Double>>> relationExt2uriMap = new HashMap<String, List<Pair<String, Double>>>(); // 关系的中英文表示到URI的映射
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			String[] splits = line.split("\t");
			String uri = splits[0];
			String labelEN = splits[1]; // 原关系英文表示权重为1.0
			String labelZH = splits[2]; // 原关系中文表示权重为1.0
			Boolean extIsNone = splits[3].equals("None\n"); // 用于判断是否扩展为“None”

			if (relationExt2uriMap.containsKey(labelEN)) {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				relationExt2uriMap.get(labelEN).add(value);
			} else {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				List<Pair<String, Double>> values = new ArrayList<Pair<String, Double>>();
				values.add(value);
				relationExt2uriMap.put(labelEN, values);
			}

			if (relationExt2uriMap.containsKey(labelZH)) {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				relationExt2uriMap.get(labelZH).add(value);
			} else {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				List<Pair<String, Double>> values = new ArrayList<Pair<String, Double>>();
				values.add(value);
				relationExt2uriMap.put(labelZH, values);
			}
			/*
			 * if ( !extIsNone ){ String[] extendRelations =
			 * splits[3].split("\\|"); for ( int i=0;
			 * i<extendRelations.length-1; i++ ){ String[] extRelationSplits =
			 * extendRelations[i].split(","); double score =
			 * Double.parseDouble(extRelationSplits[1]); if (score<0.7)
			 * continue; Pair<String, Double> value = new Pair<String,
			 * Double>(uri, Double.parseDouble(extRelationSplits[1])); if (
			 * relationExt2uriMap.containsKey(extRelationSplits[0]) ){
			 * relationExt2uriMap.get(extRelationSplits[0]).add(value); }else{
			 * List<Pair<String, Double>> values = new
			 * ArrayList<Pair<String,Double>>(); values.add(value);
			 * relationExt2uriMap.put(extRelationSplits[0], values); } } }
			 */
		}
		return relationExt2uriMap;
	}

	public static HashMap<String, List<Pair<String, Double>>> loadKBEntityMappingFile(
			String filename) throws IOException {
		HashMap<String, List<Pair<String, Double>>> entity2uriMap = new HashMap<String, List<Pair<String, Double>>>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			String[] splits = line.split("\t");
			String uri = splits[0];
			String labelEN = splits[1];
			String labelZH = splits[2];
			Boolean extIsNone = splits[3].equals("None\n"); // 用于判断是否扩展为“None”

			if (entity2uriMap.containsKey(labelEN)) {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				entity2uriMap.get(labelEN).add(value);
			} else {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				List<Pair<String, Double>> values = new ArrayList<Pair<String, Double>>();
				values.add(value);
				entity2uriMap.put(labelEN, values);
			}

			if (entity2uriMap.containsKey(labelZH)) {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				entity2uriMap.get(labelZH).add(value);
			} else {
				Pair<String, Double> value = new Pair<String, Double>(uri, 1.0);
				List<Pair<String, Double>> values = new ArrayList<Pair<String, Double>>();
				values.add(value);
				entity2uriMap.put(labelZH, values);
			}

			if (!extIsNone) {
				String[] extendRelations = splits[3].split("\\|");
				for (int i = 0; i < extendRelations.length - 1; i++) {
					String[] extRelationSplits = extendRelations[i].split(",");
					Pair<String, Double> value = new Pair<String, Double>(uri,
							Double.parseDouble(extRelationSplits[1]));
					if (entity2uriMap.containsKey(extRelationSplits[0])) {
						entity2uriMap.get(extRelationSplits[0]).add(value);
					} else {
						List<Pair<String, Double>> values = new ArrayList<Pair<String, Double>>();
						values.add(value);
						entity2uriMap.put(extRelationSplits[0], values);
					}
				}
			}
		}
		return entity2uriMap;
	}

	public static String getSEMappingFile() {
		return SEMappingFile;
	}

	public static String getKBRelationMappingFile() {
		return KBRelationExtendFile;
	}

	public static String getKBEntityMappingFile() {
		return KBEntityMappingFile;
	}

	public static void setKBEntityMappingFile(String kBEntityMappingFile) {
		KBEntityMappingFile = kBEntityMappingFile;
	}

	public static void main(String[] args) throws IOException {
		MappingFile mf = new MappingFile();

		// 元搜索下位词映射词典载入测试
		// HashMap<String, List<String>> seHypoMap =
		// mf.loadHypoMappingFile(mf.SEMappingFile);
		// Iterator iter = seHypoMap.entrySet().iterator();
		// while (iter.hasNext()) { //该种遍历方式效率高
		// Map.Entry entry = (Map.Entry) iter.next();
		// Object key = entry.getKey();
		// Object val = entry.getValue();
		// RLog.debug(key +":" +val );
		// }

		// 知识图谱关系扩展映射词典载入测试
		HashMap<String, List<Pair<String, Double>>> relationExt2uriMap = mf
				.loadKBRelationMappingFile(mf.KBRelationExtendFile);
		List<Pair<String, Double>> f = relationExt2uriMap.get("出生日期");
		RLog.debug(f);

	}

	public static String getTongyiciCilinFile() {
		return TongyiciCilinFile;
	}

	public static void setTongyiciCilinFile(String tongyiciCilinFile) {
		TongyiciCilinFile = tongyiciCilinFile;
	}

}
