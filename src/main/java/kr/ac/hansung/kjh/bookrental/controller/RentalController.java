package kr.ac.hansung.kjh.bookrental.controller;

import kr.ac.hansung.kjh.bookrental.dto.request.RentalCreateRequest;
import kr.ac.hansung.kjh.bookrental.dto.response.RentalResponse;
import kr.ac.hansung.kjh.bookrental.service.BookRentalService;
import kr.ac.hansung.kjh.bookrental.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final BookRentalService bookRentalService;

    public RentalController(RentalService rentalService, BookRentalService bookRentalService) {
        this.rentalService = rentalService;
        this.bookRentalService = bookRentalService;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rentBook(@RequestBody RentalCreateRequest request, Principal principal) {
        String userEmail = principal.getName();

        bookRentalService.rentBook(userEmail, request.isbn());
    }

    @GetMapping("/me")
    public List<RentalResponse> getMyRentals(Principal principal) {
        String userEmail = principal.getName();

        return rentalService.retrieveUserRentalHistory(userEmail).stream().map(RentalResponse::from).toList();
    }
    
    @PatchMapping("{rentalId}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long rentalId, Principal principal) {
        String userEmail = principal.getName();

        bookRentalService.returnBook(rentalId, userEmail);
    }

}
