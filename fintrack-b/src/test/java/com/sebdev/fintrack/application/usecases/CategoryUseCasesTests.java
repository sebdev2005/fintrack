package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.domain.models.Category;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryUseCasesTests {

    @Test
    void saveCreatesAndPersistsAnActiveCategory() {
        InMemoryCategoryRepository repository = new InMemoryCategoryRepository();
        SaveCategoryUseCase useCase = new SaveCategoryUseCase(repository);
        UUID userId = UUID.randomUUID();

        Category saved = useCase.execute("Food", userId);

        assertNotNull(saved.getId());
        assertEquals("Food", saved.getName());
        assertEquals(userId, saved.getIdUser());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.isActive());
        assertEquals(saved, repository.savedCategories.getFirst());
    }

    @Test
    void listReturnsCategoriesFromTheRepository() {
        InMemoryCategoryRepository repository = new InMemoryCategoryRepository();
        UUID userId = UUID.randomUUID();
        Category expected = Category.builder()
                .id(UUID.randomUUID())
                .name("Food")
                .idUser(userId)
                .active(true)
                .build();
        repository.savedCategories.add(expected);

        List<Category> actual = new ListCategoriesUseCase(repository).execute(userId);

        assertEquals(List.of(expected), actual);
        assertEquals(userId, repository.queriedUserId);
    }

    @Test
    void domainRejectsCategoryNamesLongerThanDatabaseLimit() {
        assertThrows(IllegalArgumentException.class, () -> Category.builder()
                .name("x".repeat(41))
                .build());
    }

    private static class InMemoryCategoryRepository implements CategoryRepositoryPort {
        private final List<Category> savedCategories = new ArrayList<>();
        private UUID queriedUserId;

        @Override
        public Category save(Category category) {
            savedCategories.add(category);
            return category;
        }

        @Override
        public List<Category> categories(UUID idUser) {
            queriedUserId = idUser;
            return List.copyOf(savedCategories);
        }
    }
}
