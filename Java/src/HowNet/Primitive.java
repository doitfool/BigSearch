package org.answercow.stream.intent.queryExtent.HowNet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 义原
 */
public class Primitive {

	private int id; // 义原id
	private String primitive; // 义原表示
	private int parentId; // 父义原id

	public Primitive() {
	}

	public Primitive(int id, String primitive, int parentId) {
		this.id = id;
		this.parentId = parentId;
		this.primitive = primitive;
	}

	public String getPrimitive() {
		return primitive;
	}

	public int getId() {
		return id;
	}

	public int getParentId() {
		return parentId;
	}

	public boolean isTop() {
		return id == parentId;
	}

	/**
	 * 判断词word是否是义原
	 * 
	 * @param word
	 *            ：待判断是否是义原的词
	 * @param PRIMITIVESID_zh
	 *            ：所有义原的中文表示与ID的映射Map
	 * @param PRIMITIVESID_en
	 *            ：所有义原的英文表示与ID的映射Map
	 * @return： False表示不是义原，True表示是义原
	 */
	public static boolean isPrimitive(String word,
			Map<String, Integer> PRIMITIVESID_zh,
			Map<String, Integer> PRIMITIVESID_en) {
		return PRIMITIVESID_zh.containsKey(word)
				|| PRIMITIVESID_en.containsKey(word);
	}

	/**
	 * 获得一个义原的所有父义原，直到顶层位置。
	 * 
	 * @param primitive
	 * @return 如果查找的义原没有查找到，则返回一个空list
	 */
	public List<Integer> getParents(String primitive,
			Map<Integer, Primitive> ALLPRIMITIVES_zh,
			Map<String, Integer> PRIMITIVESID_zh,
			Map<String, Integer> PRIMITIVESID_en) {
		List<Integer> list = new ArrayList<Integer>();
		// get the id of this primitive
		Integer id = null;
		if (PRIMITIVESID_zh.containsKey(primitive)) {
			id = PRIMITIVESID_zh.get(primitive);
		} else if (PRIMITIVESID_en.containsKey(primitive)) {
			id = PRIMITIVESID_en.get(primitive);
		}
		if (id != null) {
			Primitive parent = ALLPRIMITIVES_zh.get(id);
			list.add(id);
			while (!parent.isTop()) {
				list.add(parent.getParentId());
				parent = ALLPRIMITIVES_zh.get(parent.getParentId());
			}
		}
		return list;
	}

	public static void main(String[] args) {

	}
}
