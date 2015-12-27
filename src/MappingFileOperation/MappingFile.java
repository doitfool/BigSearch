package MappingFileOperation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by AC on 2015/12/27.
 * 对于元搜索扩展和知识图谱扩展，都有下位词词典作为映射文件，统一格式，统一载入方法
 */
public class MappingFile {
    private String SEMappingFile = "C:\\Users\\AC\\Desktop\\test.txt";
    private String KBMappingFile = "";

    public HashMap<String, List<String>> loadMappingFile(String filename) throws IOException{
        HashMap<String, List<String>> hype2HyposMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ( (line = br.readLine()) != null ){
            String[] hypeHypos = line.split("\t");
//            System.out.println(hypeHypos[0]+"\t"+hypeHypos[1]);
            // String.split()根据"|"或"."来分割时，由于他们都是转义字符，必须加上"\\"
            List<String> hypos = new ArrayList<String>(Arrays.asList(hypeHypos[1].split("\\|")));  // 将String[]转化为List<String>
//            System.out.print(hypeHypos[0]+"\t");
//            for ( String hypo : hypos ){
//                System.out.print(hypo+"-");
//            }
//            System.out.println();
            hype2HyposMap.put(hypeHypos[0], hypos);
        }
        return hype2HyposMap;
    }

    public String getSEMappingFile(){
        return SEMappingFile;
    }

    public String getKBMappingFile(){
        return KBMappingFile;
    }

    public static void main(String[] args){
        MappingFile mf = new MappingFile();
        try {
            HashMap<String, List<String>> hypoMap = mf.loadMappingFile(mf.SEMappingFile);
            Iterator iter = hypoMap.entrySet().iterator();
            while (iter .hasNext()) {          //该种遍历方式效率高
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                System.out.println(key +":" +val );
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
