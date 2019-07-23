package com.planetwalk.ponion.rapi.model;

import java.util.List;

public class PagedData<T> {
    public int size;
    public int total;
    public boolean isFirstPage;
    public boolean isLastPage;
    public List<T> list;
}
