package service.model;

import domain.Book;

public record BookDetail(Book book, int totalCount, int availableCount){
}
