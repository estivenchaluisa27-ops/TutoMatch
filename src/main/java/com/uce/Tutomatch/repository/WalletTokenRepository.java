package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.WalletToken;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface WalletTokenRepository extends JpaRepository<WalletToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WalletToken> findByUsuarioId(Long usuarioId);
}
