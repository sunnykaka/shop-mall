package com.kariqu.searchengine.domain;

/**
 * 自动补齐结果
 * User: Asion
 * Date: 11-8-18
 * Time: 下午8:46
 */
public class SuggestResult {

    private String term;

    private long count;

    public SuggestResult(String term, long count) {
        this.term = term;
        this.count = count;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SuggestResult{" +
                "term='" + term + '\'' +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuggestResult that = (SuggestResult) o;

        if (count != that.count) return false;
        if (term != null ? !term.equals(that.term) : that.term != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = term != null ? term.hashCode() : 0;
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }
}
