package kr.ac.hansung.kjh.bookrental.service.model;

import kr.ac.hansung.kjh.bookrental.domain.Book;

public record BookDetail(Book book, int totalCount, int availableCount) {
}
