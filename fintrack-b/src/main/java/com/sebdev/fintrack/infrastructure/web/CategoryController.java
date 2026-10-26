package com.sebdev.fintrack.infrastructure.web;

import com.sebdev.fintrack.application.usecases.DeactivateCategoryUseCase;
import com.sebdev.fintrack.application.usecases.ListCategoriesUseCase;
import com.sebdev.fintrack.application.usecases.SaveCategoryUseCase;
import com.sebdev.fintrack.domain.models.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final SaveCategoryUseCase saveCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final DeactivateCategoryUseCase deactivateCategoryUseCase;

    public CategoryController(
            SaveCategoryUseCase saveCategoryUseCase,
            ListCategoriesUseCase listCategoriesUseCase,
            DeactivateCategoryUseCase deactivateCategoryUseCase
    ) {
        this.saveCategoryUseCase = saveCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.deactivateCategoryUseCase = deactivateCategoryUseCase;
    }

    @PostMapping
    public CategoryResponse save(@Valid @RequestBody SaveCategoryRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID idUser = UUID.fromString(jwt.getSubject());
        Category category = saveCategoryUseCase.execute(request.name(), idUser);
        return CategoryResponse.from(category);
    }

    @GetMapping
    public List<CategoryResponse> list(@AuthenticationPrincipal Jwt jwt) {
        UUID idUser = UUID.fromString(jwt.getSubject());
        return listCategoriesUseCase.execute(idUser).stream()
                .map(CategoryResponse::from)
                .toList();
    }
    @PatchMapping("/{idCategory}/deactivate")
    public CategoryResponse deactivate(@PathVariable UUID idCategory, @AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(jwt.getSubject());
        return CategoryResponse.from(deactivateCategoryUseCase.execute(idCategory,idUser));
    }

    public record SaveCategoryRequest(
            @NotBlank @Size(max = 40) String name
    ) {
    }

    public record CategoryResponse(
            UUID id,
            String name,
            UUID idUser,
            Instant createdAt,
            boolean active
    ) {
        private static CategoryResponse from(Category category) {
            return new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getIdUser(),
                    category.getCreatedAt(),
                    category.isActive()
            );
        }
    }
}
