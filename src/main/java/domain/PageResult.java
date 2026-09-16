package domain;

import java.util.*;

public class PageResult<T>{
    private Page pageInfo;
    private List<T> data;

    public PageResult(Page pageInfo, List<T> data){
        this.pageInfo = pageInfo;
        this.data = data;
    }

    public Page getPageInfo() {
        return pageInfo;
    }

    public List<T> getData(){
        return data;
    }
}