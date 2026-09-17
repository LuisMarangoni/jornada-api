package br.com.luismarangoni.jornada_api.jornada;

import java.time.DayOfWeek;

public final class JornadaPrevista {

    private static final int MINUTOS_DIA_UTIL = 510;
    private static final int TOLERANCIA_MINUTOS = 10;

    private JornadaPrevista() {
    }

    public static JornadaPrevista padrao() {
        return new JornadaPrevista();
    }

    public int minutosPrevistos(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY ->
                    MINUTOS_DIA_UTIL;
            case SATURDAY, SUNDAY -> 0;
        };
    }

    public int deficitDescontavel(int minutosTrabalhados) {
        int deficit = MINUTOS_DIA_UTIL - minutosTrabalhados;
        return Math.max(0, deficit - TOLERANCIA_MINUTOS);
    }
}
