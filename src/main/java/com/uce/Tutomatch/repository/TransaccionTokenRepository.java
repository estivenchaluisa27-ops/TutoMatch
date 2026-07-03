package com.uce.Tutomatch.repository;

import com.uce.Tutomatch.model.TransaccionToken;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionTokenRepository extends JpaRepository<TransaccionToken, Long> {

    List<TransaccionToken> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Page<TransaccionToken> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pageable);
}
