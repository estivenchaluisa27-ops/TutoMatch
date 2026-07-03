package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.WalletToken;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletTokenRepository extends JpaRepository<WalletToken, Long> {

    Optional<WalletToken> findByUsuarioId(Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletToken w WHERE w.usuario.id = :usuarioId")
    Optional<WalletToken> findByUsuarioIdWithLock(@Param("usuarioId") Long usuarioId);
}
