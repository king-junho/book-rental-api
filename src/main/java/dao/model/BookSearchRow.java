package dao.model;

import domain.Book;

public record BookSearchRow(Book book, int totalCount, int availableCount) {
}
