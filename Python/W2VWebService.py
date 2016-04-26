# coding: utf-8
import soaplib # soaplib-2.0.0b2
from soaplib.core.util.wsgi_wrapper import run_twisted #发布服务
from soaplib.core.server import wsgi
from soaplib.core.service import DefinitionBase  #所有服务类必须继承该类
from soaplib.core.service import soap  #声明注解
from soaplib.core.model.clazz import Array #声明要使用的类型
from soaplib.core.model.clazz import ClassModel  #若服务返回类，该返回类必须是该类的子类
from soaplib.core.model.primitive import Integer, String
import gensim
import logging
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

__author__ = 'Chao An'

#   本地测试 web service URI: http://172.26.30.90:7789/W2VWebService?wsdl


class W2VWebService(DefinitionBase):

    @soap(String, String, _returns=String)
    def word_similarity_zh(self, word1, word2):
        global model_zh
        similarity = model_zh.similarity(word1.decode('utf-8'), word2.decode('utf-8'))
        return str(similarity)

    @soap(String, String, _returns=String)
    def word_similarity_en(self, word1, word2):
        global model_en
        similarity = model_en.similarity(word1, word2)
        return str(similarity)

    @soap(String, _returns=Array(String))
    def w2v_extend_zh(self, word):
        global model_zh
        result = []
        similars = model_zh.most_similar(word.decode('utf-8'))
        for similar in similars:
            item = str(similar[0])+'\t'+str(similar[1])
            result.append(item)
        return result

    @soap(String, _returns=Array(String))
    def w2v_extend_en(self, word):
        global model_en
        result = []
        similars = model_en.most_similar(word.decode('utf-8'))
        for similar in similars:
            item = str(similar[0])+'\t'+str(similar[1])
            result.append(item)
        return result

    # 知识图谱实体映射
    @soap(String, _returns=Array(String))
    def kg_entity_extend(self, word):
        global entity_map
        if entity_map.has_key(word):
            return entity_map[word]
        else:
            return 'None'


def freebase_entity_map(kg_entity_file):
    fb_entity_map = {}
    with open(kg_entity_file, 'r') as fr:
        for line in fr:
            entity_uri = line[line.find('<'):(line.find('>')+1)]
            entity_en = line[(line.find('"')+1):line.rfind('"')]
            entity_zh = line[(line.rfind('\t')+1):-1]
            print entity_uri, entity_en, entity_zh
            try:
                if fb_entity_map.has_key(entity_en):
                    fb_entity_map[entity_en].append(entity_uri)
                else:
                    fb_entity_map[entity_en] = [entity_uri]
                if fb_entity_map.has_key(entity_zh):
                    fb_entity_map[entity_zh].append(entity_uri)
                else:
                    fb_entity_map[entity_zh] = [entity_uri]
            except KeyError:
                pass
    # for k, v in fb_entity_map.iteritems():
    #     print k.decode('gbk'), v
    return fb_entity_map


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    logging.getLogger().setLevel(logging.DEBUG)

    model_zh_file = 'C:\Users\AC\Desktop\models\queryExtent\wiki.zh.text.vector'
    model_en_file = 'C:\Users\AC\Desktop\models\queryExtent\GoogleNews-vectors-negative300.bin'
    kg_entity_file = 'C:\Users\AC\Desktop\models\queryExtent\FBEntityMappingFile.txt'
    server_ip = '172.26.30.90'
    if len(sys.argv) == 5:
        model_zh_file = sys.argv[1]     # 服务器中文word2vec模型路径
        model_en_file = sys.argv[2]     # 服务器英文word2vec模型路径
        server_ip = sys.argv[3]         # 服务器IP
        kg_entity_file = sys.argv[4]    # 服务器freebase实体映射文件路径

    model_zh = gensim.models.Word2Vec.load_word2vec_format(model_zh_file, binary=False)
    model_en = gensim.models.Word2Vec.load_word2vec_format(model_en_file, binary=True)
    entity_map = freebase_entity_map(kg_entity_file)
    try:
        from wsgiref.simple_server import make_server
        soap_application = soaplib.core.Application([W2VWebService], 'tns')
        wsgi_application = wsgi.Application(soap_application)
        server = make_server(server_ip, 7789, wsgi_application)
        print 'soap server starting......'
        server.serve_forever()
    except ImportError:
        print "Error: example server code requires Python >= 2.5"
    except KeyError:
        print "Error: input word not found in vocabulary"

