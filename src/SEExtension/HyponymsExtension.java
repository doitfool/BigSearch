package SEExtension;

import MappingFileOperation.MappingFile;
import QueryExtension.Word;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AC on 2015/12/27.
 * 下位词扩展模块：基于nltk建立的下位词词典，对词语进行下位词扩展，用于元搜索
 */
public class HyponymsExtension {
    private HashMap<String, List<String>> hype2HyposMap = new HashMap<>();
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
    public JSONObject queryExtend(JSONObject query){
        JSONObject ret = new JSONObject();
        return ret;
    }
}
