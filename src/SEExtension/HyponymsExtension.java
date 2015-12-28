package SEExtension;

import MappingFileOperation.MappingFile;
import QueryExtension.Word;
import org.json.JSONObject;
import HowNet.HowNet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by AC on 2015/12/27.
 * 下位词扩展模块：基于nltk建立的下位词词典，对词语进行下位词扩展，用于元搜索
 */
public class HyponymsExtension {
    private HashMap<String, List<String>> hype2HyposMap = null;
    public HyponymsExtension() throws IOException{
        MappingFile mf = new MappingFile();
        hype2HyposMap = mf.loadMappingFile(mf.getSEMappingFile());
    }

    public HashMap<String, List<String>> getHype2HyposMap(){
        return hype2HyposMap;
    }

    /*
        函数参数query格式：
            JSONObject{
              String word1: Word word1,
              String word2: Word word2,
              ...
              String wordn: Word wordn
            }
        函数返回值格式：
            JSONObject{
              String word1: List<Word> word1Extension;
              String word2: List<Word> word2Extension;
              ...
              String wordn: List<Word> wordnExtension;
            }
    */
    public JSONObject queryExtend(JSONObject data){
        HowNet howNet = new HowNet();
        for ( String word : data.keySet() ){
            Word queryWord = (Word)data.get(word);
            if ( hype2HyposMap.keySet().contains(word) ){       //  元搜索下位词字典中存在搜索词
                List<Word> hyponymsWord = new ArrayList<>();
                List<String> hyponyms = hype2HyposMap.get(word);  // 将String类型下位词修改为Word类型
                for ( String hyponym : hyponyms ){
//                    String tagger = nlp.posTag(hyponym);
                    String tagger = queryWord.getPos();     // 一般词性标注是对一句话或一段文本进行标注，不能只对一个词标注？？？ 扩展词词性先使用原词词性
                    double weight = howNet.simWord(word, hyponym);        // 调用实例化相似度计算类的相似度计算方法，计算下位词与原词的相似度
                    Word hyponymWord = new Word(hyponym, tagger, weight);
                    hyponymsWord.add(hyponymWord);
                }
                queryWord.setHyponyms(hyponymsWord);
            }
        }
        return data;
    }

    public static void main(String[] args){
        JSONObject query = new JSONObject();
        query.put("挂", new Word("挂", "NN"));
        query.put("独创性", new Word("独创性", "NN"));
        query.put("口头描述", new Word("口头描述", "NN"));
        for ( String key : query.keySet() ){
            System.out.print(query.get(key));
        }
        try {
            HyponymsExtension hypoExt = new HyponymsExtension();
            JSONObject ret = hypoExt.queryExtend(query);
            for ( String key : ret.keySet() ){
                System.out.print(ret.get(key));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
