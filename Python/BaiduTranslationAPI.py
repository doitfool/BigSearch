# coding:utf8
__author__ = 'AC'
import urllib, urllib2, json, httplib
import time
import sys
reload(sys)
sys.setdefaultencoding('utf8')
'''
    语种表示：
    中文	zh	      英语 en            日语 jp
    韩语	kor       西班牙语 spa	    法语 fra
    泰语	th	      阿拉伯语 ara       俄罗斯语 ru
    葡萄牙语	pt    粤语 yue	        文言文 wyw
    白话文 zh	  自动检测 auto      德语 de
    意大利语	it    荷兰语 nl	        希腊语 el

    1. 所有参数及其值均为小写字母
    2. 所有上表中未列明的方向组合均会被重置为from=auto&to=auto
    3. 自动识别的规则
    当源语言为非中文时，目标语言自动设置为中文。当源语言为中文时，目标语言自动设置为英文。
    如下例：
    1）源语言识别为中文，则翻译方向为“中 -> 英”
    2）源语言识别为英文，则翻译方向为“英 -> 中”
    3）源语言识别为日文，则翻译方向为“日 -> 中”
'''

'''
    函数说明
    1.translateV3：调用百度翻译V3实现语言翻译，免费（使用百度APIStore apikey）
        网址：http://apistore.baidu.com/apiworks/servicedetail/118.html
        局限：支持英语德语等英文字符类语言翻译，不支持韩语日语俄语等翻译
        百度翻译V3返回json格式如下：
        {
            "errNum": 0,
            "errMsg": "success",
            "retData":
            {
                "from": "en",
                "to": "zh",
                "trans_result":
                [
                    {
                        "src": "I am chinese,and you?",
                        "dst": "我是中国人，你呢？"
                    }
                ]
            }
        }

    2.translate:
        调用百度翻译API实现语言翻译，超出限制收费（使用百度开发者中心的apikey）
        网址：http://api.fanyi.baidu.com/api/trans/product/index
        文档网站：http://apistore.baidu.com/astore/serviceinfo/1773.html
        支持多语种翻译，当源语言设置为auto时，识别需要时间
        百度翻译API返回json格式如下：
            翻译成功情况下：
            {
                "from": "en",
                "to": "zh",
                "trans_result":
                [
                    {
                        "src": "today",
                        "dst": "今天"
                    },
                    {
                        "src": "tomorrow",
                        "dst": "明天"
                    }
                ]
            }
             翻译出错情况下：
            {
                "error_code": "52001",
                "error_msg": "TIMEOUT",
                "from": "auto",
                "to": "auto",
                "query": "he's"
            }


'''
class BaiduTranslation():
    _target_ = 'zh'  # 设置翻译dst为中文
    def __init__(self, target=_target_):
        self._target_ = target
    def translateV3(self, query, fr='auto', to=_target_):  # 翻译结果类型dict{src:dst}
        api_key_V3 = 'cd644479f9031628e9d09019370dd658' # 百度APIStore apikey，
        url = 'http://apis.baidu.com/apistore/tranlateservice/translate?query='+urllib.quote(query)+'&from='+fr+'&to='+to
        # url = 'http://apis.baidu.com/apistore/tranlateservice/translate?query='+query+'&from='+fr+'&to='+to
        # print url
        req = urllib2.Request(url)
        req.add_header('apikey', api_key_V3)
        resp = urllib2.urlopen(req)
        content = resp.read()      # str类型
        content_json = json.loads(content)  # 转化为python内置数据结构,此处为dict类型
        trans_results = {}
        if content_json['errMsg'] == 'success':
            for trans_result in content_json['retData']['trans_result']:
                trans_results[trans_result['src']] = trans_result['dst']
        else:
            print '翻译失败'
        return trans_results

    def translate(self, query, fr='auto', to=_target_):
        api_key = '58DPn8FWdsWE4SZRdBHWo8u1'
        # url = 'http://openapi.baidu.com/public/2.0/bmt/translate?client_id='+api_key+'&q='+query+'&from='+fr+'&to='+to  # 基本都可翻译,但须去除\n等符号
        url = 'http://openapi.baidu.com/public/2.0/bmt/translate?client_id='+api_key+'&q='+urllib.quote(query)+'&from='+fr+'&to='+to  # 支持多语种
        req = urllib2.Request(url)
        resp = urllib2.urlopen(req)
        content = resp.read()      # str类型
        content_json = json.loads(content)  # 转化为python内置数据结构,此处为dict类型
        trans_results = {}
        # print content_json
        if content_json.has_key('trans_result'):
            for trans_result in content_json['trans_result']:
                trans_results[trans_result['src']] = trans_result['dst']
        else:
            print '翻译失败'
        return trans_results

    # 使用httplib包实现的http protocol client调用翻译API
    def translate_httplib(self, query, fr='auto', to=_target_):
        api_key = '58DPn8FWdsWE4SZRdBHWo8u1'
        url = 'openapi.baidu.com'       # 使用httplib加入http会报错
        headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
        conn = httplib.HTTPConnection(url, 80, timeout=60)
        conn.request('GET', '/public/2.0/bmt/translate?client_id='+api_key+'&q='+urllib.quote(query)+'&from='+fr+'&to='+to, headers=headers)
        resp = conn.getresponse()
        content = resp.read()      # str类型
        content_json = json.loads(content)  # 转化为python内置数据结构,此处为dict类型
        trans_results = {}
        for trans_result in content_json['trans_result']:
            trans_results[trans_result['src']] = trans_result['dst']
        return trans_results
    '''
    对于未处理的query，百度翻译不能给出合理的翻译结果（不含中文字符），则进行query处理后再次调用百度翻译
    '''
    def has_chinese(self, string):  # 判断字符串中是否包含中文字符
        for ch in string.decode('utf-8'):
            if u'\u4e00' <= ch <= u'\u9fff':
                return True
            return False

    def query_process(self, query):  # 对知识图谱中的关系字符串进行预处理
        result = ''
        for i in xrange(len(query)-1):
            if str.islower(query[i]) and str.isupper(query[i+1]):  # MedlinePlus
                result += query[i] + ' '
            elif str.isalpha(query[i]) and str.isdigit(query[i+1]):  #
                result += query[i] + ' '
            elif str.isdigit(query[i]) and str.isalpha(query[i+1]):  # 2010Density
                result += query[i] + ' '
            else:
                result += query[i]
        result += query[-1]
        return result

    def target(self, query, fr='auto', to=_target_):  # 得到最终翻译结果
        target = ''
        trans_results = self.translate(query, fr, to)
        for key in trans_results:
            target += trans_results[key]
        if not self.has_chinese(target):
            target = ''
            query_processed = self.query_process(query)
            trans_results = self.translate(query_processed, fr, to)
            for key in trans_results:
                target += trans_results[key]
        return target

    def save(self, relation_file, target_file):
        # 不对relation_file做预处理，直接存储翻译结果
        fw = open(target_file, 'a')
        i = 51501
        with open(relation_file, 'r') as fr:
            relations = fr.readlines()
            for relation in relations[i:]:
                query = relation[:-1]
                i += 1
                trans_results = bt.translate_httplib(query)
                target = ''
                for key in trans_results:
                    print key, trans_results[key]
                    target += trans_results[key]
                fw.write(query+','+target+'\n')
                print 'Line', i, 'saved.\n'
        fw.close()

        # 对relation_file预处理后，再存储翻译结果

if __name__ == '__main__':
    bt = BaiduTranslation()
    # query = "I'm a Chinese, and you?\nThe president of American."
    # query = 'Der gesunde menschenverstand' # 德语
    # query = '상식'  # 百度翻译可以翻译
    # query = '常識'  # 百度翻译，繁体需要翻译为粤语 'yue'
    # query = 'الحس' # 阿拉伯语
    # query = 'mõistust' # 爱沙尼亚语
    # query= 'здравый смысл' # 俄语
    # query = 'zeměpisná délka' # 捷克语 经度

    # trans_results = bt.translate(query)
    # for key in trans_results:
    #     print key, '--->', trans_results[key]
    # print bt.target(query)
    # print bt.target('facebookPage')


    relation_file = 'C:\Users\AC\Desktop\infobox_proprerty_label.txt'
    target_file = 'C:\Users\AC\Desktop\\target.txt'
    bt = BaiduTranslation()
    bt.save(relation_file, target_file)  # 下次运行从target文件最后一行开始