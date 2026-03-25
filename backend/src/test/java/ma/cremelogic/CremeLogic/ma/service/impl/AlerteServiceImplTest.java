package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.AlerteResponse;
import ma.cremelogic.CremeLogic.ma.entity.Alerte;
import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
import ma.cremelogic.CremeLogic.ma.repository.AlerteRepository;
import ma.cremelogic.CremeLogic.ma.repository.CommandeAchatRepository;
import ma.cremelogic.CremeLogic.ma.repository.IngredientRepository;
import ma.cremelogic.CremeLogic.ma.repository.OrdreProductionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlerteServiceImplTest {

    @Mock
    private AlerteRepository alerteRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private CommandeAchatRepository commandeAchatRepository;

    @Mock
    private OrdreProductionRepository ordreProductionRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AlerteServiceImpl alerteService;

    private Alerte alerteTest;

    @BeforeEach
    void setUp() {
        alerteTest = Alerte.builder()
                .id(1L)
                .titre("Test Alerte")
                .description("Description test")
                .type(TypeAlerte.STOCK_FAIBLE)
                .priorite("HAUTE")
                .resolue(false)
                .dateCreation(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetAlertesNonResolues() {
        // Arrange
        when(alerteRepository.findAlertesNonResoluesTriees()).thenReturn(Arrays.asList(alerteTest));

        // Act
        List<AlerteResponse> result = alerteService.getAlertesNonResolues();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Alerte", result.get(0).getTitre());
        verify(alerteRepository, times(1)).findAlertesNonResoluesTriees();
    }

    @Test
    void testCountAlertesNonResolues() {
        // Arrange
        when(alerteRepository.countByResolueFalse()).thenReturn(5L);

        // Act
        Long count = alerteService.countAlertesNonResolues();

        // Assert
        assertEquals(5L, count);
        verify(alerteRepository, times(1)).countByResolueFalse();
    }

    @Test
    void testResoudreAlerte() {
        // Arrange
        when(alerteRepository.findById(1L)).thenReturn(Optional.of(alerteTest));
        when(alerteRepository.save(any(Alerte.class))).thenReturn(alerteTest);

        // Act
        AlerteResponse result = alerteService.resoudreAlerte(1L, "Problème réglé");

        // Assert
        assertNotNull(result);
        assertTrue(result.isResolue());
        verify(alerteRepository, times(1)).findById(1L);
        verify(alerteRepository, times(1)).save(alerteTest);
    }

    @Test
    void testSupprimerAlerte() {
        // Arrange
        doNothing().when(alerteRepository).deleteById(1L);

        // Act
        alerteService.supprimerAlerte(1L);

        // Assert
        verify(alerteRepository, times(1)).deleteById(1L);
    }
}
