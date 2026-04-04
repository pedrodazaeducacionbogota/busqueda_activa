package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.EstudianteBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.ResponsableBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.SolicitudCupoBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.RegistroBusquedaActivaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusquedaActivaServiceTest {

    @InjectMocks
    private BusquedaActivaServiceImpl service;

    @Mock
    private RegistroBusquedaActivaRepository repository;

    private RegistroBusquedaActiva registroPadre;

    @Before
    public void setUp() {
        registroPadre = new RegistroBusquedaActiva();
        
        // Simular que cuando se llama al save(), retorna el mismo objeto ingresado
        when(repository.save(any(RegistroBusquedaActiva.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testInicializarRegistro_DevuelvePendienteYEtapaNull() {
        RegistroBusquedaActiva guardado = service.guardarRegistro(registroPadre);
        
        assertThat(guardado.getNumeroRegistro()).isNotNull();
        assertThat(guardado.getFechaRegistro()).isNotNull();
        assertThat(guardado.getEstado()).isEqualTo("Pendiente");
        assertThat(guardado.getUltimaEtapaRegistrada()).isNull();
    }

    @Test
    public void testAvanzarEtapa1_EstudianteSociodemografico() {
        registroPadre.setEstudiante(new EstudianteBusquedaActiva());
        RegistroBusquedaActiva guardado = service.guardarRegistro(registroPadre);
        
        assertThat(guardado.getUltimaEtapaRegistrada()).isEqualTo(1);
        assertThat(guardado.getEstado()).isEqualTo("Pendiente"); // Sigue pendiente, faltan etapas
    }

    @Test
    public void testAvanzarEtapa2_SolicitudCupo() {
        registroPadre.setEstudiante(new EstudianteBusquedaActiva());
        registroPadre.setSolicitudCupo(new SolicitudCupoBusquedaActiva());
        RegistroBusquedaActiva guardado = service.guardarRegistro(registroPadre);
        
        assertThat(guardado.getUltimaEtapaRegistrada()).isEqualTo(2);
        assertThat(guardado.getEstado()).isEqualTo("Pendiente"); // Sigue pendiente, falta el responsable
    }

    @Test
    public void testAvanzarEtapa3_Responsable_RegistroFinalizado() {
        registroPadre.setEstudiante(new EstudianteBusquedaActiva());
        registroPadre.setSolicitudCupo(new SolicitudCupoBusquedaActiva());
        registroPadre.setResponsable(new ResponsableBusquedaActiva());
        RegistroBusquedaActiva guardado = service.guardarRegistro(registroPadre);
        
        assertThat(guardado.getUltimaEtapaRegistrada()).isEqualTo(3);
        assertThat(guardado.getEstado()).isEqualTo("Finalizado"); // Al llegar a etapa 3 finaliza
    }

    @Test
    public void testFlujoCompleto_ConHermanosYGestante() {
        // [Caja Amarilla] - Flujo Femenino -> Gestante
        EstudianteBusquedaActiva estudiante = new EstudianteBusquedaActiva();
        estudiante.setSexo("Femenino");
        estudiante.setEsGestante(true);
        estudiante.setNumeroDocumento("100200300");
        registroPadre.setEstudiante(estudiante);

        // [Caja Verde] - Flujo con Hermanos -> Sí (Se llenan los datos del hermano)
        SolicitudCupoBusquedaActiva solicitud = new SolicitudCupoBusquedaActiva();
        solicitud.setTieneHermanos(true);
        solicitud.setTipoDocumentoHermano("CC");
        solicitud.setNumeroDocumentoHermano("998877");
        solicitud.setPrimerNombreHermano("Carlos");
        solicitud.setMismaInstitucion(false);
        registroPadre.setSolicitudCupo(solicitud);

        // [Caja Azul] - Responsable
        ResponsableBusquedaActiva responsable = new ResponsableBusquedaActiva();
        responsable.setPrimerNombre("Maria");
        responsable.setPrimerApellido("Perez");
        registroPadre.setResponsable(responsable);

        RegistroBusquedaActiva guardado = service.guardarRegistro(registroPadre);
        
        // Assertions del flujo de negocio
        assertThat(guardado.getEstudiante().getSexo()).isEqualTo("Femenino");
        assertThat(guardado.getEstudiante().getEsGestante()).isTrue();
        assertThat(guardado.getSolicitudCupo().getTieneHermanos()).isTrue();
        assertThat(guardado.getSolicitudCupo().getNumeroDocumentoHermano()).isEqualTo("998877");
        assertThat(guardado.getEstado()).isEqualTo("Finalizado");
    }

    @Test
    public void testFlujoCompleto_SinHermanosYMasculino() {
        // [Caja Amarilla] - Flujo Masculino -> NO Gestante
        EstudianteBusquedaActiva estudiante = new EstudianteBusquedaActiva();
        estudiante.setSexo("Masculino");
        estudiante.setEsGestante(false);
        registroPadre.setEstudiante(estudiante);

        // [Caja Verde] - Flujo Sin Hermanos (Campos hermanos nulos)
        SolicitudCupoBusquedaActiva solicitud = new SolicitudCupoBusquedaActiva();
        solicitud.setTieneHermanos(false);
        solicitud.setNumeroDocumentoHermano(null);
        registroPadre.setSolicitudCupo(solicitud);

        // [Caja Azul]
        registroPadre.setResponsable(new ResponsableBusquedaActiva());

        RegistroBusquedaActiva guardado = service.guardarRegistro(registroPadre);
        
        assertThat(guardado.getEstudiante().getSexo()).isEqualTo("Masculino");
        assertThat(guardado.getSolicitudCupo().getTieneHermanos()).isFalse();
        assertThat(guardado.getSolicitudCupo().getNumeroDocumentoHermano()).isNull();
        assertThat(guardado.getEstado()).isEqualTo("Finalizado");
    }
}
