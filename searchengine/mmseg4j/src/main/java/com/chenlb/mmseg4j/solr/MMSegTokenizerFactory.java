package com.chenlb.mmseg4j.solr;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Logger;

import com.chenlb.mmseg4j.*;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.solr.analysis.BaseTokenizerFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

import com.chenlb.mmseg4j.analysis.MMSegTokenizer;

public class MMSegTokenizerFactory extends BaseTokenizerFactory implements ResourceLoaderAware {

    static final Logger log = Logger.getLogger(MMSegTokenizerFactory.class.getName());
    /* 线程内共享 */
    private ThreadLocal<MMSegTokenizer> tokenizerLocal = new ThreadLocal<MMSegTokenizer>();

    /* 词典 */
    private Dictionary dic = null;

    private TrieStore trieStore = null;

    public Tokenizer create(Reader input) {
        MMSegTokenizer tokenizer = tokenizerLocal.get();
        if (tokenizer == null) {
            tokenizer = newTokenizer(input);
        } else {
            try {
                tokenizer.reset(input);
            } catch (IOException e) {
                tokenizer = newTokenizer(input);
                log.info("MMSegTokenizer.reset i/o error by:" + e.getMessage());
            }
        }

        return tokenizer;
    }

    private Seg newSeg(Map<String, String> args) {
        Seg seg;
        log.info("create new Seg ...");
        //default max-word
        String mode = args.get("mode");
        if ("simple".equals(mode)) {
            log.info("use simple mode");
            seg = new SimpleSeg(dic);
        } else if ("complex".equals(mode)) {
            log.info("use complex mode");
            seg = new ComplexSeg(dic);
        } else {
            log.info("use max-word mode");
            seg = new MaxWordSeg(dic);
        }
        return seg;
    }



    private MMSegTokenizer newTokenizer(Reader input) {
        Seg seg = newSeg(getArgs());
        MMSegTokenizer tokenizer = new MMSegTokenizer(seg, input, new PinYinMMseg(input, seg, trieStore));
        tokenizerLocal.set(tokenizer);
        return tokenizer;
    }

    public void inform(ResourceLoader loader) {
        String dicPath = args.get("dicPath");

        dic = Utils.getDict(dicPath, loader);

        String trieKey = args.get(TrieStore.TRIE_KEY);
        String trieCacheAddress = args.get(TrieStore.TRIE_CACHE_ADDRESS);

        if (trieKey != null && trieCacheAddress != null) {
            trieStore = new TrieStore(dic, trieKey, trieCacheAddress);
            trieStore.loadTrieToCache();
        }

        log.info("dic load... in=" + dic.getDicPath().toURI());
    }


}
