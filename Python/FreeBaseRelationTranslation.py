# coding:utf8

from BaiduTranslationAPI import BaiduTranslation
import sys
reload(sys)
sys.setdefaultencoding('utf8')
'''
    功能说明：将多语种关系表示（label）统一为中文表示，并与RDF中的关系建立映射，写入文件
'''

# 存在问题：当label是中文事，自动翻译为英文了？？？
def freebase_relation_translation(relation_file, start, target_file):
    fw = open(target_file, 'a')
    bt = BaiduTranslation()
    i = start
    with open(relation_file, 'r') as fr:
        lines = fr.readlines()
        for line in lines[start:]:
            relation, label = [element.strip('\n') for element in line.split('\t')]
            trans_results = bt.translate_httplib(label, to='zh')
            target = ''
            print relation,
            for key in trans_results:
                print key, trans_results[key]
                target += trans_results[key]
            fw.write(relation+'\t'+label+'\t'+target+'\n')
            i += 1
            print 'Translate', i, 'relations'
    fw.close()


if __name__ == '__main__':
    relation_file = 'C:\Users\AC\Desktop\property\\freebase_property.txt'
    target_file = 'C:\Users\AC\Desktop\property\\freebase_target.txt'
    freebase_relation_translation(relation_file, 0, target_file)