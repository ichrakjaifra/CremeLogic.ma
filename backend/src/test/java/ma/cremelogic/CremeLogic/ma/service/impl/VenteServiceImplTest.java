package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.VenteResponse;
import ma.cremelogic.CremeLogic.ma.entity.Vente;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.repository.ProduitRepository;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.repository.VenteRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenteServiceImplTest {

    @Mock
    private VenteRepository venteRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private MouvementStockService mouvementStockService;

    @Mock
    private AlerteService alerteService;

    @Mock
    private HistoriqueService historiqueService;

    @InjectMocks
    private VenteServiceImpl venteService;

    private Vente venteTest;

    @BeforeEach
    void setUp() {
        venteTest = Vente.builder()
                .id(1L)
                .numeroVente("V-12345")
                .dateVente(LocalDateTime.now())
                .montantTotal(BigDecimal.valueOf(100))
                .montantPaye(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void testGetVente() {
        when(venteRepository.findById(1L)).thenReturn(Optional.of(venteTest));

        VenteResponse result = venteService.getVente(1L);

        assertNotNull(result);
        assertEquals("V-12345", result.getNumeroVente());
        verify(venteRepository, times(1)).findById(1L);
    }

    @Test
    void testGetVenteNotFound() {
        when(venteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> venteService.getVente(1L));
        verify(venteRepository, times(1)).findById(1L);
    }

    @Test
    void testAnnulerVente() {
        // Arrange
        when(venteRepository.findById(1L)).thenReturn(Optional.of(venteTest));
        when(venteRepository.save(any(Vente.class))).thenReturn(venteTest);

        // Act
        venteService.annulerVente(1L, "Erreur de saisie");

        // Assert
        verify(venteRepository, times(1)).save(any(Vente.class));
        verify(alerteService, times(1)).creerAlerteVenteAnnulee(any(Vente.class), anyString());
    }

    @Test
    void testGetChiffreAffaires() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(venteRepository.getChiffreAffairesPeriode(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(500.0);

        // Act
        BigDecimal result = venteService.getChiffreAffairesPeriode(now.minusDays(1), now);

        // Assert
        assertEquals(BigDecimal.valueOf(500.0), result);
        verify(venteRepository, times(1)).getChiffreAffairesPeriode(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
