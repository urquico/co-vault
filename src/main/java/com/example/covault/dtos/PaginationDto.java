package com.example.covault.dtos;

import lombok.Data;

@Data
public class PaginationDto<T> {
    private int page;
    private int limit;
    private int totalItems;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private T items;
}
