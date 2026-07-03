package com.uce.Tutomatch.service;

import com.uce.Tutomatch.exception.SaldoInsuficienteException;
import com.uce.Tutomatch.model.TransaccionToken.TipoTransaccion;

public interface WalletOperacionService {

    void acreditar(Long usuarioId, int cantidad,
                   TipoTransaccion tipo, String descripcion, Long referenciaId);

    void debitar(Long usuarioId, int cantidad,
                 TipoTransaccion tipo, String descripcion, Long referenciaId)
            throws SaldoInsuficienteException;

    void inicializarWallet(Long usuarioId);
}
