package gift.controller;

import gift.dto.OptionRequestDTO;
import gift.dto.OptionResponseDTO;
import gift.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class OptionController {
    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<OptionResponseDTO>> getOptionsById(
            @PathVariable Integer id) {
        List<OptionResponseDTO> options = optionService.getOptionsByProductId(id);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<OptionResponseDTO> createOption(
            @PathVariable Integer id,
            @Valid @RequestBody OptionRequestDTO optionRequestDTO) {
        OptionResponseDTO option = optionService.createOption(id, optionRequestDTO);
        return ResponseEntity.ok(option);
    }
}
