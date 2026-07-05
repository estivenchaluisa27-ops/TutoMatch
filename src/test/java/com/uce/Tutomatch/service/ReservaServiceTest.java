package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.*;
import com.uce.Tutomatch.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private DisponibilidadRepository disponibilidadRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private PerfilTutorRepository perfilTutorRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private NotificacionService notificacionService;

    private ReservaService reservaService;
    private Usuario solicitante;
    private Usuario tutorUsuario;
    private PerfilTutor perfilTutor;
    private Disponibilidad bloque;
    private Materia materia;
    private TutorMateria tutorMateria;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(reservaRepository, disponibilidadRepository,
                materiaRepository, perfilTutorRepository, usuarioRepository, notificacionService);

        solicitante = new Usuario("alumno@uce.edu.ec", "pass", "Alumno Test", true, false, false);
        solicitante.setId(1L);

        tutorUsuario = new Usuario("tutor@uce.edu.ec", "pass", "Tutor Test", false, true, false);
        tutorUsuario.setId(2L);

        perfilTutor = new PerfilTutor(tutorUsuario, 5, "Tutor experimentado");
        perfilTutor.setId(10L);
        perfilTutor.setVerificado(true);
        perfilTutor.setVisible(true);

        materia = new Materia("Matemáticas", "Ciencias", 3);
        materia.setId(100L);

        tutorMateria = new TutorMateria();
        tutorMateria.setPerfilTutor(perfilTutor);
        tutorMateria.setMateria(materia);
        perfilTutor.getMaterias().add(tutorMateria);

        bloque = new Disponibilidad(perfilTutor, 1, LocalTime.of(8, 0), LocalTime.of(10, 0));
        bloque.setId(1L);
    }

    @Test
    void crear_exito() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(reservaRepository.existsByDisponibilidadIdAndEstadoNot(1L, Reserva.EstadoReserva.CANCELADA))
                .thenReturn(false);
        when(materiaRepository.findById(100L)).thenReturn(Optional.of(materia));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        Reserva result = reservaService.crear(1L, 1L, 100L);

        assertNotNull(result);
        assertEquals(Reserva.EstadoReserva.PENDIENTE, result.getEstado());
        assertEquals(solicitante, result.getSolicitante());
        assertEquals(materia, result.getMateria());
        verify(disponibilidadRepository).save(bloque);
        assertEquals(Disponibilidad.EstadoDisponibilidad.RESERVADO, bloque.getEstado());
    }

    @Test
    void crear_bloqueNoDisponible_lanzaError() {
        bloque.setEstado(Disponibilidad.EstadoDisponibilidad.RESERVADO);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(bloque));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, 100L));
        assertTrue(ex.getMessage().contains("no est\u00e1 disponible"));
    }

    @Test
    void crear_tutorNoVerificado_lanzaError() {
        perfilTutor.setVerificado(false);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(reservaRepository.existsByDisponibilidadIdAndEstadoNot(1L, Reserva.EstadoReserva.CANCELADA))
                .thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, 100L));
        assertTrue(ex.getMessage().contains("no est\u00e1 disponible"));
    }

    @Test
    void crear_tutorNoVisible_lanzaError() {
        perfilTutor.setVisible(false);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(reservaRepository.existsByDisponibilidadIdAndEstadoNot(1L, Reserva.EstadoReserva.CANCELADA))
                .thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, 100L));
        assertTrue(ex.getMessage().contains("no est\u00e1 disponible"));
    }

    @Test
    void crear_reservaMismaPersona_lanzaError() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(reservaRepository.existsByDisponibilidadIdAndEstadoNot(1L, Reserva.EstadoReserva.CANCELADA))
                .thenReturn(false);
        when(materiaRepository.findById(100L)).thenReturn(Optional.of(materia));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(tutorUsuario));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(2L, 1L, 100L));
        assertTrue(ex.getMessage().contains("No puedes reservarte a ti mismo"));
    }

    @Test
    void crear_materiaNoPerteneceAlTutor_lanzaError() {
        TutorMateria otraMateria = new TutorMateria();
        Materia fisica = new Materia("F\u00edsica", "Ciencias", 3);
        fisica.setId(200L);
        otraMateria.setMateria(fisica);
        perfilTutor.getMaterias().clear();
        perfilTutor.getMaterias().add(otraMateria);

        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(reservaRepository.existsByDisponibilidadIdAndEstadoNot(1L, Reserva.EstadoReserva.CANCELADA))
                .thenReturn(false);
        when(materiaRepository.findById(100L)).thenReturn(Optional.of(materia));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, 100L));
        assertTrue(ex.getMessage().contains("no ofrece esa materia"));
    }

    @Test
    void confirmar_exito() {
        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        Reserva result = reservaService.confirmar(1L, 2L);

        assertEquals(Reserva.EstadoReserva.CONFIRMADA, result.getEstado());
    }

    @Test
    void confirmar_soloTutor_lanzaError() {
        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.confirmar(1L, 1L));
        assertTrue(ex.getMessage().contains("Solo el tutor"));
    }

    @Test
    void confirmar_soloPendiente_lanzaError() {
        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva.setId(1L);
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.confirmar(1L, 2L));
        assertTrue(ex.getMessage().contains("Solo se pueden confirmar reservas pendientes"));
    }

    @Test
    void cancelar_solicitanteCancelaPendiente_exito() {
        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(i -> i.getArgument(0));

        Reserva result = reservaService.cancelar(1L, 1L, false);

        assertEquals(Reserva.EstadoReserva.CANCELADA, result.getEstado());
        assertEquals(Disponibilidad.EstadoDisponibilidad.LIBRE, bloque.getEstado());
    }

    @Test
    void cancelar_tutorCancelaPendiente_exito() {
        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(i -> i.getArgument(0));

        Reserva result = reservaService.cancelar(1L, 2L, false);

        assertEquals(Reserva.EstadoReserva.CANCELADA, result.getEstado());
    }

    @Test
    void cancelar_estadoTerminal_lanzaError() {
        Reserva reserva = new Reserva(solicitante, bloque, materia);
        reserva.setId(1L);
        reserva.setEstado(Reserva.EstadoReserva.FINALIZADA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.cancelar(1L, 1L, false));
        assertTrue(ex.getMessage().contains("ya est\u00e1"));
    }

    @Test
    void obtenerComoSolicitante_conPageable_retornaPage() {
        reservaService.obtenerComoSolicitante(1L, org.springframework.data.domain.PageRequest.of(0, 10));
        verify(reservaRepository).findBySolicitanteIdOrderByFechaCreacionDesc(eq(1L), any());
    }

    @Test
    void obtenerComoTutor_conPageable_retornaPage() {
        perfilTutor.getUsuario().setId(2L);
        when(perfilTutorRepository.findByUsuarioId(2L))
                .thenReturn(Optional.of(perfilTutor));

        reservaService.obtenerComoTutor(2L, org.springframework.data.domain.PageRequest.of(0, 10));

        verify(reservaRepository)
                .findByDisponibilidadPerfilTutorIdOrderByFechaCreacionDesc(eq(10L), any());
    }
}
