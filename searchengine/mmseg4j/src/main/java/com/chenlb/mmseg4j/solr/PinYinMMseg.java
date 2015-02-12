package com.chenlb.mmseg4j.solr;

import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;
import com.kariqu.common.pinyin.PinYinUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

/**
 * 带有拼音的分词行为，装饰MMseg
 * User: Asion
 * Date: 12-6-30
 * Time: 下午2:09
 */
public class PinYinMMseg extends MMSeg {


    private TrieStore trieStore;


    public PinYinMMseg(Reader input, Seg seg, TrieStore trieStore) {
        super(input, seg);
        this.trieStore = trieStore;

    }

    /**
     * 把MMseg分出来的词转化为拼音然后放到trie树中
     * <p/>
     * 首先到缓存中读取trie,如果没找到，就到磁盘中找，如果还没找到就构建一棵新树
     * <p/>
     * todo 树被写入缓存和磁盘的时机是一个文档被分词完毕，如果每次dump很多商品，可能太频繁
     * <p/>
     * 高亮的时候也会调用分词器，要保证只是dump的时候才写入trie树，所以在分布式缓存中放入了一个标志位
     * 这个标志位可在启动dump的时候设置
     *
     * @return
     * @throws IOException
     */
    @Override
    public Word next() throws IOException {
        Word word = super.next();

        if (trieStore == null) {
            return word;
        }

        if (isWord(word)) {
            if (trieStore.isDump()) {
                Collection<String> composePinYin = PinYinUtil.getComposePinYin(word.getString());
                for (String pinyin : composePinYin) {
                    trieStore.getTrie().insert(pinyin, word.getString());
                }
            }

        } else {
            trieStore.saveTrie();
        }
        return word;
    }


    private boolean isWord(Word word) {
        return word != null && word.getType().equals("word");
    }


}
