package com.library.management.service;

import java.util.List;

import com.library.management.entity.Book;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(Integer id);
    Book getBookByIsbn(String isbn);
    Book createBook(Book book);
    Book updateBook(Integer id, Book bookDetails);
    void deleteBook(Integer id);
    List<Book> searchBooksByTitle(String title);
    List<Book> getBooksByCategory(Integer categoryId);
    
}