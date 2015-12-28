package QueryExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AC on 2015/12/26.
 */
public class Word {
    private String word;    // 原词
    private String pos; // 原词词性
    private String freebaseURI = "NULL";    //  对应KB中的URI
    private int type = 0;   //  -1,0,1表示other, entity, relation
    private double weight = -1.0;   //-1.0表示初始值，正值表示相似词与原词的相似度
    private List<Word> hyponyms = new ArrayList<>();    // 下位词
    private List<Word> synonyms = new ArrayList<>();    // 同义词

    public Word(String word){
        this.word = word;
    }
    public Word(String word, String pos){
        this.word = word;
        this.pos = pos;
    }
    public Word(String word, String pos, double weight){
        this.word = word;
        this.pos = pos;
        this.weight = weight;
    }

    @Override
    public String toString() {
        String s = "word:"+word+"\npos:"+pos+"\nfreebaseURI:"+freebaseURI+"\nweight:"+weight+"\ntype:"+type+"\nhyponyms:";
        for ( Word word : hyponyms ){
            s += (word.getWord()+","+word.getWeight())+" | ";
        }
        s += "\nsynonyms:";
        for ( Word word : synonyms ){
            s += (word.getWord()+","+word.getWeight())+" | ";
        }
        s += "\n\n";
        return s;
    }

    public String getWord(){
        return word;
    }
    public String setWord(String word){
        this.word = word;
        return word;
    }

    public String getPos(){
        return pos;
    }
    public String setPos(String pos){
        this.pos = pos;
        return pos;
    }

    public String getFreebaseURI(){
        return freebaseURI;
    }
    public String setFreebaseURI(String freebaseURI){
        this.freebaseURI = freebaseURI;
        return freebaseURI;
    }

    public int getType(){
        return type;
    }
    public int setType(int type){
        this.type = type;
        return type;
    }

    public double getWeight(){
        return weight;
    }
    public double setWeight(double weight){
        this.weight = weight;
        return weight;
    }

    public List<Word> getHyponyms(){
        return hyponyms;
    }
    public List<Word> setHyponyms(List<Word> hyponyms){
        this.hyponyms = hyponyms;
        return hyponyms;
    }

    public List<Word> getSynonyms(){
        return synonyms;
    }
    public List<Word> setSynonyms(List<Word> synonyms){
        this.synonyms = synonyms;
        return synonyms;
    }

    public static void main(String[] args){
        String word = "出生地";
        Word word1 = new Word(word);
        word1.setPos("NN");
        word1.setFreebaseURI("http://rdf.freebase.com/ns/user.hashimj.default_domain.person.place_of_birth");
        System.out.print(word1);
    }
}
