package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.IngredientResponse;
import ma.cremelogic.CremeLogic.ma.entity.Ingredient;
import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.repository.IngredientRepository;
import ma.cremelogic.CremeLogic.ma.repository.FournisseurRepository;
import ma.cremelogic.CremeLogic.ma.repository.MouvementStockRepository;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.repository.AlerteRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @Mock
    private AlerteService alerteService;

    @Mock
    private HistoriqueService historiqueService;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private AlerteRepository alerteRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private Ingredient ingredientTest;

    @BeforeEach
    void setUp() {
        ingredientTest = Ingredient.builder()
                .id(1L)
                .nom("Farine")
                .codeIngredient("ING-FAR-1234")
                .uniteMesure(UniteMesure.KILOGRAMME)
                .quantiteStock(BigDecimal.TEN)
                .quantiteMinimum(BigDecimal.ONE)
                .quantiteMaximum(BigDecimal.valueOf(100))
                .prixUnitaire(BigDecimal.valueOf(10))
                .build();
    }

    @Test
    void testGetIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredientTest));

        IngredientResponse result = ingredientService.getIngredient(1L);

        assertNotNull(result);
        assertEquals("Farine", result.getNom());
        verify(ingredientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetIngredientNotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getIngredient(1L));
        verify(ingredientRepository, times(1)).findById(1L);
    }

    @Test
    void testAjusterStockEntree() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredientTest));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredientTest);

        IngredientResponse result = ingredientService.ajusterStock(1L, BigDecimal.valueOf(5), "ENTREE", "Arrivage");

        assertNotNull(result);
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testDeleteIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredientTest));
        doNothing().when(ingredientRepository).delete(ingredientTest);

        ingredientService.deleteIngredient(1L);

        verify(ingredientRepository, times(1)).delete(ingredientTest);
        verify(alerteRepository, times(1)).deleteByIngredientId(1L);
    }
}
