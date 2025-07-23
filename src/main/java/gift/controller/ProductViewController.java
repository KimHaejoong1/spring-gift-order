package gift.controller;

import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductViewController {
    private final ProductService productService;
    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            Model model) {
        Page<ProductResponseDTO> products = productService.getPage(pageable);
        model.addAttribute("products", products);
        return "products";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductRequestDTO product,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(e ->
                errorMessage.append(e.getDefaultMessage())
                        .append("\n"));
            redirectAttributes.addFlashAttribute("createError", errorMessage.toString());
            redirectAttributes.addFlashAttribute("showCreateModal", true);
            redirectAttributes.addFlashAttribute("createFormData", product);
            return "redirect:/products";
        }

        try {
            productService.create(product);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("createError", e.getMessage());
            redirectAttributes.addFlashAttribute("showCreateModal", true);
            redirectAttributes.addFlashAttribute("createFormData", product);
            return "redirect:/products";
        }
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateProduct(
            @RequestParam("id") Integer id,
            @Valid @ModelAttribute ProductRequestDTO product,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(e ->
                    errorMessage.append(e.getDefaultMessage())
                            .append("\n"));
            redirectAttributes.addFlashAttribute("updateError", errorMessage.toString());
            redirectAttributes.addFlashAttribute("showUpdateModal", true);
            redirectAttributes.addFlashAttribute("updateFormData", product);
            redirectAttributes.addFlashAttribute("updateId", id);
            return "redirect:/products";
        }
        try {
            productService.update(id, product);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("updateError", e.getMessage());
            redirectAttributes.addFlashAttribute("showUpdateModal", true);
            redirectAttributes.addFlashAttribute("updateFormData", product);
            redirectAttributes.addFlashAttribute("updateId", id);
        }
        return "redirect:/products";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") Integer id) {
        productService.delete(id);
        return "redirect:/products";
    }
}
