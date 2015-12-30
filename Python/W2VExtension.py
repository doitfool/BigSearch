# coding:utf8
import gensim
import sys
reload(sys)
sys.setdefaultencoding('utf8')

'''
    使用word2vec对freebase中的关系进行扩展
'''

# 使用训练得到的w2v模型获得词语word（unicode类型）的扩展词及相似度（权重）
def w2v_extension(model, word):
    try:
        words_extension = model.most_similar(word)
    except Exception:
        words_extension = None
    return words_extension

def save_to_file(origin_file, final_file, model):
    fw = open(final_file, 'a')
    with open(origin_file, 'r') as fr:
        lines = fr.readlines()
        for line in lines:
            word = line.split('\t')[-1].strip()
            words_extension = w2v_extension(model, word.decode('utf-8'))
            print word, words_extension
            fw.write(line.strip()+'\t')
            if words_extension:
                for word_extension in words_extension:
                    fw.write(word_extension[0]+','+str(word_extension[1])+'|')
            else:
                fw.write('None')
            fw.write('\n')
    fw.close()

if __name__ == '__main__':
    origin_file = 'freebase_target.txt'
    final_file = 'final_file.txt'
    vector_file = 'wiki.zh.text.vector'
    model = gensim.models.Word2Vec.load_word2vec_format(vector_file)
    save_to_file(origin_file, final_file, model)