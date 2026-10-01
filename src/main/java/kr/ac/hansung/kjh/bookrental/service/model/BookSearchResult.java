package kr.ac.hansung.kjh.bookrental.service.model;

import java.util.List;

public record BookSearchResult(Page page, List<BookDetail> books) {
}
