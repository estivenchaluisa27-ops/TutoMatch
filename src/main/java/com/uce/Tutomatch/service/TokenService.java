package com.uce.Tutomatch.service;

import com.uce.Tutomatch.exception.SaldoInsuficienteException;
import com.uce.Tutomatch.model.TransaccionToken;
import com.uce.Tutomatch.model.TransaccionToken.TipoTransaccion;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.model.WalletToken;
import com.uce.Tutomatch.repository.TransaccionTokenRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.repository.WalletTokenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService implements WalletConsultaService, WalletOperacionService {

    private static final int TOKENS_BIENVENIDA = 5;

    private final WalletTokenRepository walletRepo;
    private final TransaccionTokenRepository transaccionRepo;
    private final UsuarioRepository usuarioRepo;

    public TokenService(WalletTokenRepository walletRepo,
                        TransaccionTokenRepository transaccionRepo,
                        UsuarioRepository usuarioRepo) {
        this.walletRepo = walletRepo;
        this.transaccionRepo = transaccionRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    @Transactional
    public void inicializarWallet(Long usuarioId) {
        if (walletRepo.findByUsuarioId(usuarioId).isPresent()) return;

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        WalletToken wallet = new WalletToken(usuario);
        wallet.setSaldo(TOKENS_BIENVENIDA);
        walletRepo.save(wallet);

        TransaccionToken tx = new TransaccionToken(
                usuario, TipoTransaccion.BIENVENIDA, TOKENS_BIENVENIDA,
                "Tokens de bienvenida", null);
        transaccionRepo.save(tx);
    }

    @Override
    @Transactional
    public int obtenerSaldo(Long usuarioId) {
        return walletRepo.findByUsuarioId(usuarioId)
                .map(WalletToken::getSaldo)
                .orElse(0);
    }

    private WalletToken obtenerOCrearWallet(Long usuarioId) {
        return walletRepo.findByUsuarioIdWithLock(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepo.findById(usuarioId)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
                    WalletToken nueva = new WalletToken(usuario);
                    return walletRepo.save(nueva);
                });
    }

    @Override
    @Transactional
    public void acreditar(Long usuarioId, int cantidad,
                          TipoTransaccion tipo, String descripcion, Long referenciaId) {
        WalletToken wallet = obtenerOCrearWallet(usuarioId);

        wallet.setSaldo(wallet.getSaldo() + cantidad);
        walletRepo.save(wallet);

        Usuario usuario = wallet.getUsuario();
        TransaccionToken tx = new TransaccionToken(
                usuario, tipo, cantidad, descripcion, referenciaId);
        transaccionRepo.save(tx);
    }

    @Override
    @Transactional
    public void debitar(Long usuarioId, int cantidad,
                        TipoTransaccion tipo, String descripcion, Long referenciaId) {
        WalletToken wallet = obtenerOCrearWallet(usuarioId);

        if (wallet.getSaldo() < cantidad) {
            throw new SaldoInsuficienteException(wallet.getSaldo(), cantidad);
        }

        wallet.setSaldo(wallet.getSaldo() - cantidad);
        walletRepo.save(wallet);

        Usuario usuario = wallet.getUsuario();
        TransaccionToken tx = new TransaccionToken(
                usuario, tipo, -cantidad, descripcion, referenciaId);
        transaccionRepo.save(tx);
    }

    @Override
    @Transactional
    public WalletToken obtenerWallet(Long usuarioId) {
        return obtenerOCrearWallet(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionToken> obtenerHistorial(Long usuarioId, Pageable pageable) {
        return transaccionRepo.findByUsuarioIdOrderByFechaDesc(usuarioId, pageable);
    }
}
