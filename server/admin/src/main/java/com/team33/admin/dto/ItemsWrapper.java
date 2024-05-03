package com.team33.moduleapi.admin.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ItemsWrapper {

    private List<ItemWrapper> items = new ArrayList<>();
    private int numOfRows;
    private int totalCount;
    private int pageNo;

}
