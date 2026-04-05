package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;
import ma.cremelogic.CremeLogic.ma.entity.Produit;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduit;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.repository.ProduitRepository;
import ma.cremelogic.CremeLogic.ma.repository.RecetteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private RecetteRepository recetteRepository;

    @Mock
    private HistoriqueService historiqueService;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private Produit produitTest;

    @BeforeEach
    void setUp() {
        produitTest = Produit.builder()
                .id(1L)
                .nom("Glace Vanille")
                .codeProduit("PROD-GLA-1234")
                .prixVente(BigDecimal.valueOf(25))
                .stockDisponible(10)
                .stockMinimum(5)
                .stockMaximum(100)
                .statut(StatutProduit.ACTIF)
                .build();
    }

    @Test
    void testGetProduit() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produitTest));

        ProduitResponse result = produitService.getProduit(1L);

        assertNotNull(result);
        assertEquals("Glace Vanille", result.getNom());
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProduitNotFound() {
        when(produitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> produitService.getProduit(1L));
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    void testAjusterStockEntree() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produitTest));
        when(produitRepository.save(any(Produit.class))).thenReturn(produitTest);

        ProduitResponse result = produitService.ajusterStock(1L, 5, "ENTREE");

        assertNotNull(result);
        assertEquals(15, result.getStockDisponible());
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    void testDeleteProduit() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produitTest));
        doNothing().when(produitRepository).delete(produitTest);

        produitService.deleteProduit(1L);

        verify(produitRepository, times(1)).delete(produitTest);
        verify(historiqueService, times(1)).enregistrerSuppression(anyString(), anyLong(), anyString());
    }
}
