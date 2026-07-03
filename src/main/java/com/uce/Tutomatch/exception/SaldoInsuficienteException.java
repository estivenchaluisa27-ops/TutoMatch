package com.uce.Tutomatch.exception;

public class SaldoInsuficienteException extends BusinessException {

    public SaldoInsuficienteException(int saldoActual, int requerido) {
        super("Saldo insuficiente. Tienes " + saldoActual + " tokens, necesitas " + requerido + ".");
    }
}
