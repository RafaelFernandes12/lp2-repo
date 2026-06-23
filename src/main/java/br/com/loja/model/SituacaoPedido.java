package br.com.loja.model;

import java.util.Arrays;
import java.util.List;

/**
 * Estados possíveis de um pedido e as transições válidas entre eles.
 * Cada estado declara para quais outros estados pode evoluir, formando a
 * máquina de estados que governa o ciclo de vida do pedido.
 */
public enum SituacaoPedido {
    ACEITO,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO;

    /** Retorna os estados para os quais este estado pode transitar. */
    public List<SituacaoPedido> proximosPermitidos() {
        switch (this) {
            case ACEITO:
                return Arrays.asList(PAGO, CANCELADO);
            case PAGO:
                return Arrays.asList(ENVIADO, CANCELADO);
            case ENVIADO:
                return Arrays.asList(ENTREGUE);
            case ENTREGUE:
            case CANCELADO:
            default:
                return List.of(); // estados finais
        }
    }

    public boolean podeIrPara(SituacaoPedido destino) {
        return proximosPermitidos().contains(destino);
    }
}
