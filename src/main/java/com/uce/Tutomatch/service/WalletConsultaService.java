package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.TransaccionToken;
import com.uce.Tutomatch.model.WalletToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletConsultaService {

    int obtenerSaldo(Long usuarioId);

    WalletToken obtenerWallet(Long usuarioId);

    Page<TransaccionToken> obtenerHistorial(Long usuarioId, Pageable pageable);
}
