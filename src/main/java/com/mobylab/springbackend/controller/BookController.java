package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.entity.Book;
import com.mobylab.springbackend.service.BookServie;
import com.mobylab.springbackend.service.dto.BookDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController implements SecuredRestController {

    private final BookServie bookServie;

    public BookController(BookServie bookServie){
        this.bookServie = bookServie;
    }

    @GetMapping("/getByAuthor")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(String author){
        List<BookDto> bookDtoList = bookServie.getBooksByAuthor(author);
        return ResponseEntity.status(200).body(bookDtoList);
    }

    @PostMapping("/addBook")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Book> addBook(@RequestBody BookDto bookDto){
        Book book = bookServie.addBook(bookDto);
        return ResponseEntity.status(201).body(book);
    }
}
