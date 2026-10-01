package kr.ac.hansung.kjh.bookrental.dao.model;

import kr.ac.hansung.kjh.bookrental.domain.Book;

public record BookSearchRow(Book book, int totalCount, int availableCount) {
}
