import { Component, OnInit, AfterViewInit, OnDestroy, NgZone, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeguimientoService } from '../../services/seguimiento.service';
import { BolsinDTO, ConsultaSeguimientoResponse } from '../../models/bolsin.model';
import * as L from 'leaflet';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seguimiento.component.html',
  styleUrl: './seguimiento.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeguimientoComponent implements OnInit, AfterViewInit, OnDestroy {

  nombreCMUsuarioLogueado: string = '';
  bolsines: BolsinDTO[] = [];
  bolsinSeleccionado: BolsinDTO | null = null;
  realizacionExitosaDelCU: string = '';

  cargando: boolean = false;
  error: string = '';
  mostrandoDialogoEmail: boolean = false;
  filtroPrecinto: string = '';
  filtroCMDestino: string = '';

  private mapa: L.Map | null = null;
  private markers: L.Marker[] = [];
  private resizeObserver: ResizeObserver | null = null;

  constructor(
    private seguimientoService: SeguimientoService,
    private zone: NgZone,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.opcionConsultarSeguimiento();
  }

  ngAfterViewInit(): void {
    // Mapa se inicializa en mostrarPosicionBolsin(), cuando el contenedor ya es visible
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    if (this.mapa) {
      this.mapa.remove();
    }
  }

  // Mensaje 1 del diagrama de secuencia
  opcionConsultarSeguimiento(): void {
    this.habilitarVentana();

    this.seguimientoService.iniciarConsulta().subscribe({
      next: (response: ConsultaSeguimientoResponse) => {
        this.cargando = false;
        if (response.mensaje && (!response.bolsines || response.bolsines.length === 0)) {
          this.error = response.mensaje;
          this.cdr.markForCheck();
          return;
        }
        this.mostrarCMUsuarioLogueado(response.nombreCMUsuarioLogueado ?? '');
        this.mostrarPosicionBolsin(response.bolsines ?? []);
        this.solicitarSeleccionBolsin();
      },
      error: () => {
        this.cargando = false;
        this.error = 'Error al conectar con el servidor. Verifique que el backend esté en ejecución.';
        this.cdr.markForCheck();
      }
    });
  }

  // Mensaje 2 del diagrama de secuencia
  habilitarVentana(): void {
    this.cargando = true;
    this.error = '';
    this.realizacionExitosaDelCU = '';
    this.bolsines = [];
  }

  // Mensaje 9 del diagrama de secuencia
  mostrarCMUsuarioLogueado(nombre: string): void {
    this.nombreCMUsuarioLogueado = nombre;
    this.cdr.markForCheck();
  }

  // Mensaje 26 del diagrama de secuencia
  mostrarPosicionBolsin(bolsines: BolsinDTO[]): void {
    this.bolsines = bolsines;
    this.cdr.markForCheck();
    setTimeout(() => {
      this.inicializarMapa();
      this.mapa?.invalidateSize();
      this.actualizarMarcadores();
    }, 150);
  }

  // Mensaje 27 del diagrama de secuencia
  solicitarSeleccionBolsin(): void {
    // La pantalla presenta la lista de bolsines y el mapa para que el EB seleccione
  }

  get bolsinesFiltrados(): BolsinDTO[] {
    return this.bolsines.filter(b => {
      const filtroPrecinto = this.filtroPrecinto.trim();
      const filtroCM = this.filtroCMDestino.trim().toLowerCase();
      const coincidePrecinto = filtroPrecinto === '' || b.numeroPrecinto.toString().includes(filtroPrecinto);
      const coincideCM = filtroCM === '' || b.cmDestinoNombre.toLowerCase().includes(filtroCM);
      return coincidePrecinto && coincideCM;
    });
  }

  // Mensaje 28 del diagrama de secuencia
  tomarSeleccionBolsin(bolsin: BolsinDTO): void {
    this.bolsinSeleccionado = bolsin;
    this.mostrandoDialogoEmail = false;
    this.realizacionExitosaDelCU = '';

    this.seguimientoService.seleccionarBolsin(bolsin.numeroBolsin).subscribe({
      next: () => {
        this.consultarEnvioEmail();
        if (bolsin.latitud && bolsin.longitud) {
          this.mapa?.setView([bolsin.latitud, bolsin.longitud], 10);
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Error al seleccionar el bolsín.';
        this.cdr.markForCheck();
      }
    });
  }

  // Mensaje 31 del diagrama de secuencia
  consultarEnvioEmail(): void {
    this.mostrandoDialogoEmail = true;
    this.cdr.markForCheck();
  }

  // Mensaje 33 del diagrama de secuencia
  tomarConfirmacionEmail(confirma: boolean): void {
    if (!this.bolsinSeleccionado) return;
    this.mostrandoDialogoEmail = false;

    this.seguimientoService.confirmarEmail(this.bolsinSeleccionado.numeroBolsin, confirma).subscribe({
      next: (response: ConsultaSeguimientoResponse) => {
        this.mostrarRealizacionExitosaDelCU(response.mensaje ?? (confirma ? 'Operación completada.' : 'Consulta finalizada.'));
      },
      error: () => {
        this.error = 'Error al confirmar el email.';
        this.cdr.markForCheck();
      }
    });
  }

  // Mensaje 43 del diagrama de secuencia
  mostrarRealizacionExitosaDelCU(mensaje: string): void {
    this.realizacionExitosaDelCU = mensaje;
    this.cdr.markForCheck();
  }

  actualizarFiltros(tipo: string, valor: string): void {
    if (tipo === 'precinto') this.filtroPrecinto = valor;
    if (tipo === 'cm') this.filtroCMDestino = valor;
  }

  private inicializarMapa(): void {
    if (this.mapa) return;
    const contenedor = document.getElementById('mapa');
    if (!contenedor) return;

    this.mapa = L.map('mapa').setView([-34.6037, -58.3816], 5);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.mapa);

    this.resizeObserver = new ResizeObserver(() => {
      this.zone.runOutsideAngular(() => this.mapa?.invalidateSize());
    });
    this.resizeObserver.observe(contenedor);
  }

  private actualizarMarcadores(): void {
    if (!this.mapa) return;

    this.markers.forEach(m => m.remove());
    this.markers = [];

    const bounds: [number, number][] = [];

    this.bolsines.forEach(bolsin => {
      if (bolsin.latitud && bolsin.longitud) {
        const marker = L.marker([bolsin.latitud, bolsin.longitud])
          .addTo(this.mapa!)
          .bindPopup(this.armarPopupBolsin(bolsin));

        marker.on('click', () => this.zone.run(() => this.tomarSeleccionBolsin(bolsin)));
        this.markers.push(marker);
        bounds.push([bolsin.latitud, bolsin.longitud]);
      }
    });

    if (bounds.length > 0) {
      this.mapa.fitBounds(bounds, { padding: [50, 50] });
    }
  }

  private armarPopupBolsin(bolsin: BolsinDTO): string {
    return `
      <div class="popup-bolsin">
        <strong>Bolsín #${bolsin.numeroBolsin}</strong><br>
        Precinto: ${bolsin.numeroPrecinto}<br>
        Destino: ${bolsin.cmDestinoNombre}<br>
        Actualizado: ${bolsin.fechaHoraActualizacion ?? 'N/D'}<br>
        <small>Lat: ${bolsin.latitud?.toFixed(4)}, Lng: ${bolsin.longitud?.toFixed(4)}</small>
      </div>
    `;
  }
}
