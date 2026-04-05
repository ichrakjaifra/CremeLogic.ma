package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Produit;
import ma.cremelogic.CremeLogic.ma.enums.CategorieProduit;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    Optional<Produit> findByCodeProduit(String codeProduit);
    List<Produit> findByNomContainingIgnoreCase(String nom);
    List<Produit> findByCategorie(CategorieProduit categorie);
    List<Produit> findByStatut(StatutProduit statut);
    List<Produit> findByStockDisponibleLessThan(Integer stockMinimum);
    List<Produit> findByPrixVenteBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT p FROM Produit p WHERE p.stockDisponible <= p.stockMinimum")
    List<Produit> findProduitsStockFaible();

    @Query("SELECT p FROM Produit p WHERE p.stockDisponible = 0")
    List<Produit> findProduitsEnRupture();

    @Query("SELECT p FROM Produit p ORDER BY p.dateCreation DESC")
    List<Produit> findRecentProducts(int limit);

    boolean existsByCodeProduit(String codeProduit);
}