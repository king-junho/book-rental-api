package kr.ac.hansung.kjh.bookrental.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.kjh.bookrental.dto.request.RentalCreateRequest;
import kr.ac.hansung.kjh.bookrental.dto.response.RentalResponse;
import kr.ac.hansung.kjh.bookrental.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rentBook(@Valid @RequestBody RentalCreateRequest request, Principal principal) {
        String userEmail = principal.getName();

        rentalService.rentBook(userEmail, request.isbn());
    }

    @GetMapping("/me")
    public List<RentalResponse> getMyRentals(Principal principal) {
        String userEmail = principal.getName();

        return rentalService.retrieveUserRentalHistory(userEmail);
    }

    @PatchMapping("{rentalId}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long rentalId, Principal principal) {
        String userEmail = principal.getName();

        rentalService.returnBook(rentalId, userEmail);
    }

}
