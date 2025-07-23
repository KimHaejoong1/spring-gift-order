package gift.controller;

import gift.annotation.LoginMember;
import gift.dto.*;
import gift.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping
    public ResponseEntity<WishlistResponseDTO> addWishlist(
            @Valid @RequestBody WishlistRequestDTO request,
            @LoginMember AuthenticatedMemberDTO member) {
        WishlistResponseDTO response = wishlistService.addWishlist(member.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponseDTO>> getWishlist(@LoginMember AuthenticatedMemberDTO member) {
        List<WishlistResponseDTO> wishlists = wishlistService.getAllWishlistByMemberId(member.id());
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<WishlistResponseDTO>> getWishlistsByPage(
            @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable,
            @LoginMember AuthenticatedMemberDTO member) {
        Page<WishlistResponseDTO> wishlists = wishlistService.getWishlistByMemberId(member.id(), pageable);
        return ResponseEntity.ok(wishlists);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WishlistResponseDTO> updateWishlist(
            @PathVariable Integer id,
            @Valid @RequestBody WishlistUpdateRequestDTO request,
            @LoginMember AuthenticatedMemberDTO member) {
        WishlistResponseDTO response = wishlistService.updateQuantity(id, request.quantity(), member.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(
            @PathVariable Integer id,
            @LoginMember AuthenticatedMemberDTO member) {
        wishlistService.deleteWishlist(id, member.id());
        return ResponseEntity.noContent().build();
    }
}
